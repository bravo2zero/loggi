package de.loggi.service;

/**
 * @author CptSpaetzle
 */
//TODO should we be able to create column processors which return other columns dependent values
public interface WriteService {
    void processRecord(String record);
    void initialize() throws Exception;
    void finalizeAndShutdown();
}
