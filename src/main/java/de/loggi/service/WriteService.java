package de.loggi.service;

import de.loggi.exceptions.ConfigurationException;
import de.loggi.exceptions.ProcessingException;

/**
 * @author CptSpaetzle
 */
public interface WriteService {
    void setConfigurationService (ConfigurationService configurationService);
    void processRecord(String record) throws ProcessingException;
    void initialize() throws ConfigurationException;
    public <V> V getAttributeValue(String key) throws ConfigurationException;
    void finalizeAndShutdown();
    String getSuccessHint();
}
