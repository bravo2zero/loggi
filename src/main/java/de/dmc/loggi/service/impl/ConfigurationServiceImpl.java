package de.dmc.loggi.service.impl;

import de.dmc.loggi.exceptions.ConfigurationException;
import de.dmc.loggi.model.Column;
import de.dmc.loggi.model.Template;
import de.dmc.loggi.processors.ColumnProcessor;
import de.dmc.loggi.service.ConfigurationService;
import org.apache.commons.cli.CommandLine;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationServiceImpl implements ConfigurationService {

    static final Logger logger = LoggerFactory.getLogger(ConfigurationService.class);
    private ObjectMapper mapper = new ObjectMapper();
    private Template template;
    private List<ColumnProcessor> processors;
    private Path sourceFile;
    private CommandLine commandLine;


    @Override
    public void initialize(String templateLocation) throws ConfigurationException {
        if (templateLocation == null || templateLocation.isEmpty()) {
            throw new ConfigurationException("Template file path is required");
        }
        Path templatePath = FileSystems.getDefault().getPath(templateLocation);

        if (!templatePath.toFile().exists()) {
            throw new ConfigurationException("Cannot find template file: " + templatePath.toString());
        }
        try {
            template = mapper.readValue(templatePath.toFile(), Template.class);
            processors = new ArrayList<>();

            for (Column column : template.getColumns()) {
                initializeColumn(column);
            }

        } catch (Throwable ex) {
            logger.error("Exception reading configuration from template", ex);
            throw new ConfigurationException("Exception reading configuration from template [" + templatePath.toString() + "]", ex);
        }
    }

    private void initializeColumn(Column column) {
        try {
            Class processorClass = Class.forName(column.getProcessorName());
            ColumnProcessor processor = (ColumnProcessor) processorClass.getConstructor(Column.class).newInstance(column);
            processors.add(processor);

        } catch (Exception e) {
            logger.error("Exception instantiating column, name:" + column.getName() + " processor:" + column.getProcessorName()+". Skipped.", e);
        }
    }

    @Override
    public List<ColumnProcessor> getProcessors() {
        return processors;
    }

    @Override
    public Template getTemplate() {
        return template;
    }

    @Override
    public CommandLine getCommandLine() {
        return commandLine;
    }

    @Override
    public void setCommandLine(CommandLine commandLine) {
        this.commandLine = commandLine;
    }

    @Override
    public Path getSource() {
        return sourceFile;
    }

    @Override
    public void setSource(String source) throws ConfigurationException {
        if (source == null || source.isEmpty()) {
            throw new ConfigurationException("Source file path is null or empty");
        }
        sourceFile = FileSystems.getDefault().getPath(source);
        if (!sourceFile.toFile().exists()) {
            throw new ConfigurationException("Source file not found: " + source);
        }
    }

    @Override
    public String currentConfigToString() throws ConfigurationException {
        if (template == null) {
            return "null";
        }
        try {
            return mapper.writeValueAsString(template);
        } catch (Exception ex) {
            logger.error("Exception writing configuration template to string", ex);
            return "null";
        }
    }
}
