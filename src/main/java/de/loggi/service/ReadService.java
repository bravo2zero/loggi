package de.loggi.service;

import java.nio.file.Path;

/**
 * @author CptSpaetzle
 */
public interface ReadService {

    void process();
    void processFile(Path file);
}
