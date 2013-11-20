/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loggi.service;

import de.loggi.exceptions.ConfigurationException;
import de.loggi.model.Template;
import de.loggi.processors.ColumnProcessor;
import org.apache.commons.cli.CommandLine;

import java.nio.file.Path;
import java.util.List;

/**
 *
 * @author CptSpaetzle
 */
public interface ConfigurationService {
    /**
     * Initializes app configuration from template file
     * @param templateFile path to json template file to initialize configuration from
     */
    void initialize(String templateFile) throws ConfigurationException;
    
    Template getTemplate();

    CommandLine getCommandLine();

    WriteService getWriter() throws ConfigurationException;

    void setCommandLine(CommandLine commandLine);

    List<ColumnProcessor> getProcessors();
    
    String currentConfigToString() throws ConfigurationException;

    void setSource(String sourceFile) throws ConfigurationException;

    List<Path> getSources();
}
