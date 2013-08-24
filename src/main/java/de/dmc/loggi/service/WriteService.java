package de.dmc.loggi.service;

import de.dmc.loggi.processors.ColumnProcessor;

import java.util.List;

/**
 * @author CptSpaetzle
 */
public interface WriteService {
    void processRecord(String record);
    void initialize() throws Exception;
    void finalizeAndShutdown();
}
