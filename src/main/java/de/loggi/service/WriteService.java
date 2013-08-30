package de.loggi.service;

import de.loggi.exceptions.ProcessingException;

/**
 * @author CptSpaetzle
 */
//TODO should we be able to create column processors which return other columns dependent values
public interface WriteService {
    void processRecord(String record) throws ProcessingException;
    void initialize() throws Exception;
    void finalizeAndShutdown();
}
