package de.dmc.loggi.service;

import de.dmc.loggi.processors.ColumnProcessor;

import java.util.List;

/**
 * @author CptSpaetzle
 */
//TODO should we be able to create column processors which return other columns dependent values
public interface WriteService {
    void processRecord(String record);
    void initialize() throws Exception;
    void finalizeAndShutdown();
}
