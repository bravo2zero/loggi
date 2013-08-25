/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dmc.loggi.service;

import de.dmc.loggi.exceptions.ConfigurationException;
import de.dmc.loggi.model.Template;
import de.dmc.loggi.processors.ColumnProcessor;
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

    void setCommandLine(CommandLine commandLine);

    List<ColumnProcessor> getProcessors();
    
    String currentConfigToString() throws ConfigurationException;

    void setSource(String sourceFile) throws ConfigurationException;

    Path getSource();
}
