package de.loggi.service;

import de.loggi.service.impl.H2WriteServiceImpl;
import org.apache.commons.cli.Options;

/**
 * @author CptSpaetzle
 */
public interface HelpService {

    void printUsage(Options options);
    void printHelp(Options options);
    void printH2PromptHint(String port);
}
