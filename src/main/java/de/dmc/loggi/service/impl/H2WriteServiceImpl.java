package de.dmc.loggi.service.impl;

import de.dmc.loggi.processors.ColumnProcessor;
import de.dmc.loggi.service.ConfigurationService;
import de.dmc.loggi.service.WriteService;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author CptSpaetzle
 */
public class H2WriteServiceImpl implements WriteService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String TABLE_NAME = "records";
    public static final String H2_DATETIME = "yyyy-MM-dd hh:mm:ss";

    private ConfigurationService configuration;
    private JdbcConnectionPool cp;
    private Server server;

    @Override
    public void processRecord(String record) {

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
                logger.error("Exception formatting field value. Column:" + fields[i] + " raw value:[" + rawValue + "], format:[]" + processor.getColumn().getDataFormat(), e);
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
            conn.commit();
            conn.close();
        } catch (SQLException e) {
            logger.error("Exception processing record", e);
        }
    }

    private String getFormattedValue(String columnValue, String dataType, String dataFormat) throws Exception {
        if (dataType.toLowerCase().startsWith("int")) {
            return columnValue;
        }
        if (dataType.toLowerCase().startsWith("datetime")) {
            SimpleDateFormat dt = new SimpleDateFormat(dataFormat);
            Date date = dt.parse(columnValue);
            SimpleDateFormat h2dt = new SimpleDateFormat(H2_DATETIME);
            return "'" + h2dt.format(date) + "'";
        }
        return "'" + columnValue + "'";
    }

    @Override
    public void initialize() throws Exception {
        // TODO configure either memory or hdd storage from configuration setting
        // TODO change later port, usr,pwd (from config)
        Class.forName("org.h2.Driver").newInstance();
        cp = JdbcConnectionPool.create("jdbc:h2:mem:loggi", "user", "password");
        // create & start embedded H2 Server
        server = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
        Connection conn = cp.getConnection();

        // create table
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
        conn.commit();
        conn.close();
    }


    @Override
    public void finalizeAndShutdown() {
        cp.dispose();
        server.shutdown();
    }

    public void setConfiguration(ConfigurationService configuration) {
        this.configuration = configuration;
    }

}
