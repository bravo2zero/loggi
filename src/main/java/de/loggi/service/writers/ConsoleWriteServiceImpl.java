package de.loggi.service.writers;

import de.loggi.processors.AbstractColumnProcessor;
import de.loggi.processors.ColumnProcessor;
import de.loggi.service.ConfigurationService;
import de.loggi.service.WriteService;

/**
 * @author CptSpaetzle
 */
public class ConsoleWriteServiceImpl implements WriteService {


    private ConfigurationService configuration;

    @Override
    public void processRecord(String record) {
        StringBuilder builder = new StringBuilder();
        for (ColumnProcessor processor : configuration.getProcessors()) {
            builder.append(processor.getColumnValue(record)).append("\t");
        }
        System.out.println(builder.toString());
    }

    @Override
    public void initialize(){
        StringBuilder builder = new StringBuilder();
        for (ColumnProcessor processor : configuration.getProcessors()) {
            if (builder.length() > 0) {
                builder.append(AbstractColumnProcessor.TAB);
            }
            builder.append(processor.getColumn().getName());
        }
        System.out.println(builder.toString());
    }

    @Override
    public void finalizeAndShutdown() {
        // nothing
    }

    public void setConfiguration(ConfigurationService configuration) {
        this.configuration = configuration;
    }
}
