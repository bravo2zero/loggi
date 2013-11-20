package de.loggi.service.writers;

import de.loggi.exceptions.ConfigurationException;
import de.loggi.processors.AbstractColumnProcessor;
import de.loggi.processors.AttributeDef;
import de.loggi.processors.ColumnProcessor;
import de.loggi.processors.MetaInfo;
import de.loggi.service.ConfigurationService;

/**
 * @author CptSpaetzle
 */
@MetaInfo(
        description = "Simple console writer, sends output to stdout with some basic configurations for separators",
        attributes = {
                @AttributeDef(name = ConsoleWriteServiceImpl.ATTR_COLUMN_SEPARATOR, description = "Specifies the column separator", defaultValue = "\t")
        }
)
public class ConsoleWriteServiceImpl extends AbstractWriteServiceImpl {

    public static final String ATTR_COLUMN_SEPARATOR = "columnSeparator";

    @Override
    public void processRecord(String record) {
        StringBuilder builder = new StringBuilder();
        for (ColumnProcessor processor : configuration.getProcessors()) {
            builder.append(processor.getColumnValue(record)).append(getAttributeValue(ATTR_COLUMN_SEPARATOR));
        }
        System.out.println(builder.toString());
    }

    @Override
    public void initialize() throws ConfigurationException {
        // init attributes map
        super.initialize();

        // print header
        StringBuilder builder = new StringBuilder();
        for (ColumnProcessor processor : configuration.getProcessors()) {
            if (builder.length() > 0) {
                builder.append(getAttributeValue(ATTR_COLUMN_SEPARATOR));
            }
            builder.append(processor.getColumn().getName());
        }
        System.out.println(builder.toString());
    }

    @Override
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configuration = configurationService;
    }

    public void setConfiguration(ConfigurationService configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getSuccessHint() {
        return "All done.";
    }
}
