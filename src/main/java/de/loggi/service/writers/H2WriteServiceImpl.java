package de.loggi.service.writers;

import de.loggi.exceptions.ConfigurationException;
import de.loggi.exceptions.ProcessingException;
import de.loggi.processors.AttributeDef;
import de.loggi.processors.ColumnProcessor;
import de.loggi.processors.MetaInfo;
import org.h2.jdbc.JdbcSQLException;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author CptSpaetzle
 */
@MetaInfo(
        description = "H2 Database writer. Brings up embedded H2 server and writes output to 'record' table.",
        attributes = {
                @AttributeDef(name = H2WriteServiceImpl.ATTR_MODE, description = "H2 embedded mode: [memory|file]", defaultValue = "memory"),
                @AttributeDef(name = H2WriteServiceImpl.ATTR_PORT, description = "Port to be used by H2 Server", defaultValue = "8082"),
                @AttributeDef(name = H2WriteServiceImpl.ATTR_USERNAME, description = "Login username", defaultValue = "user"),
                @AttributeDef(name = H2WriteServiceImpl.ATTR_PASSWORD, description = "Login password", defaultValue = "password"),
                @AttributeDef(name = H2WriteServiceImpl.ATTR_LOCALE, description = "Locale to be used when parsing datetime field values", defaultValue = "en_US"),
                @AttributeDef(name = H2WriteServiceImpl.ATTR_DEBUG, description = "Print out SQL Statements for debug purposes (false|true)", defaultValue = "false")
        }
)
public class H2WriteServiceImpl extends AbstractWriteServiceImpl {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String ATTR_MODE = "mode";
    public static final String ATTR_PORT = "port";
    public static final String ATTR_USERNAME = "user";
    public static final String ATTR_PASSWORD = "password";
    public static final String ATTR_DEBUG = "debug";
    public static final String ATTR_LOCALE = "locale";

    public static final String TABLE_NAME = "records";
    public static final String H2_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String H2_MEMORY_SERVER_URI = "jdbc:h2:mem:loggi;DB_CLOSE_DELAY=-1";
    public static final String DEBUG_TRACE_OPTION = ";TRACE_LEVEL_SYSTEM_OUT=3";

    public static final Pattern REGEX_PRECISION = Pattern.compile("\\((\\d+?)\\)");

    private String serverUri;
    private JdbcConnectionPool cp;
    private Server server;

    @Override
    public void processRecord(String record) throws ProcessingException {

        String[] fields = new String[configuration.getProcessors().size()];
        String[] values = new String[configuration.getProcessors().size()];
        for (int i = 0; i < configuration.getProcessors().size(); i++) {
            ColumnProcessor processor = configuration.getProcessors().get(i);
            fields[i] = processor.getColumn().getName();
            String rawValue = processor.getColumnValue(record);
            String formattedValue = null;
            try {
                formattedValue = getFormattedValue(rawValue, processor.getColumn().getDataType(), processor.getColumn().getDataFormat());
            } catch (Exception e) {
                throw new ProcessingException("Exception formatting field value. Column:" + fields[i] + " raw value:[" + rawValue + "], format:[" + processor.getColumn().getDataFormat() + "]. Record:[" + record + "]", e);

            }

            values[i] = formattedValue == null ? "'null'" : formattedValue;
        }

        try {
            Connection conn = cp.getConnection();
            StringBuilder sqlStatement = new StringBuilder();
            sqlStatement
                    .append("insert into ").append(TABLE_NAME).append("(")
                    .append(StringUtils.arrayToCommaDelimitedString(fields))
                    .append(") values (")
                    .append(StringUtils.arrayToCommaDelimitedString(values))
                    .append(")");
            logger.debug("H2> {}", sqlStatement.toString());
            conn.prepareStatement(sqlStatement.toString()).executeUpdate();
            conn.close();
        } catch (SQLException e) {
            logger.error("Exception processing record", e);
        }
    }

    private String getFormattedValue(String columnValue, String dataType, String dataFormat) throws Exception {
        // trim value down to varchar limit
        // TODO move precision fetch to init state so we should make it only once
        if (dataType.toLowerCase().startsWith("varchar")) {
            Matcher matcher = REGEX_PRECISION.matcher(dataType);
            if (matcher.find()) {
                int precision = Integer.valueOf(matcher.group(1));
                int maxSize = precision > columnValue.length() ? columnValue.length() : precision;
                String trimmedValue = columnValue.substring(0, maxSize);
                //using two single quotes to create a quote
                return "'" + trimmedValue.replace("'", "''") + "'";
            }
        }

        // "support" for int values
        if (dataType.toLowerCase().startsWith("int")) {
            return columnValue;
        }
        // adjust date to H2 internal format
        if (dataType.toLowerCase().startsWith("datetime") && !dataFormat.isEmpty()) {
            SimpleDateFormat dt = new SimpleDateFormat(dataFormat, new Locale(this.<String>getAttributeValue(ATTR_LOCALE)));
            Date date = dt.parse(columnValue);
            SimpleDateFormat h2dt = new SimpleDateFormat(H2_DATETIME);
            return "'" + h2dt.format(date) + "'";
        }
        return "'" + columnValue + "'";
    }

    @Override
    public void initialize() throws ConfigurationException {
        // init attributes map
        super.initialize();

        try {
            // init driver
            Class.forName("org.h2.Driver").newInstance();
            // create server
            cp = JdbcConnectionPool.create(getServerUri(), this.<String>getAttributeValue(ATTR_USERNAME), this.<String>getAttributeValue(ATTR_PASSWORD));
            // create & start embedded H2 Server
            server = Server.createWebServer("-web", "-webAllowOthers", "-webPort", this.<String>getAttributeValue(ATTR_PORT)).start();
            Connection conn = cp.getConnection();

            // recreate table
            try {
                StringBuilder sqlDropTable = new StringBuilder();
                sqlDropTable.append("drop table ").append(TABLE_NAME);
                conn.prepareStatement(sqlDropTable.toString()).executeUpdate();
            } catch (JdbcSQLException exc) {
                logger.debug("Exception dropping the records table", exc);// ignore that, no table yet
            }
            StringBuilder sqlCreateTable = new StringBuilder();
            sqlCreateTable.append("create table ").append(TABLE_NAME).append("(");
            for (int i = 0; i < configuration.getProcessors().size(); i++) {
                ColumnProcessor processor = configuration.getProcessors().get(i);
                if (i > 0) {
                    sqlCreateTable.append(", ");
                }
                // it's not safe to assign datatype like this :(
                sqlCreateTable.append(processor.getColumn().getName())
                        .append(" ").append(processor.getColumn().getDataType());
            }
            sqlCreateTable.append(")");
            logger.debug("H2> {}", sqlCreateTable.toString());
            conn.prepareStatement(sqlCreateTable.toString()).executeUpdate();
            conn.close();

        } catch (Exception e) {
            throw new ConfigurationException("Exception initializing H2 Server", e);
        }
    }

    private String getServerUri() throws ConfigurationException {
        if (serverUri == null) {
            configServerUri();
        }
        return serverUri;
    }

    public void configServerUri() throws ConfigurationException {
        switch (this.<String>getAttributeValue(ATTR_MODE)) {
            case "file":
                this.serverUri = "jdbc:h2:" + TABLE_NAME;
                break;
            case "memory":
                this.serverUri = H2_MEMORY_SERVER_URI;
                break;
            default:
                throw new ConfigurationException("Unknown 'mode' attribute value: '" + this.<String>getAttributeValue(ATTR_MODE) + "' ");
        }

        if ("true".equalsIgnoreCase(this.<String>getAttributeValue(ATTR_DEBUG))) {
            serverUri = serverUri + DEBUG_TRACE_OPTION;
        }
    }

    @Override
    public void finalizeAndShutdown() {
        cp.dispose();
        server.shutdown();
    }

    @Override
    public String getSuccessHint() {
        StringBuilder builder = new StringBuilder();
        try {
            builder
                    .append("Open http://")
                    .append(Inet4Address.getLocalHost().getHostAddress())
                    .append(":")
                    .append(getAttributeValue(ATTR_PORT))
                    .append(" with connection string '").append(getServerUri()).append("' or press <Ctrl+C> to exit...");
        } catch (Exception e) {
            logger.error("Exception printing H2 command prompt hint", e);
        }
        return builder.toString();
    }
}
