package de.loggi.service;

import org.apache.commons.cli.Options;

/**
 * @author CptSpaetzle
 */
public interface HelpService {

    void printUsage(Options options);
    void printHelp(Options options);
}
