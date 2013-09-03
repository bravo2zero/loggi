package de.loggi.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CptSpaetzle
 */
public class Console {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public void debug(String msg, String... args) {
        logger.debug(msg, args);
    }

    public void info(String msg, String... args) {
        logger.info(msg, args);
    }

    public void error(String msg, Throwable thr) {
        logger.error(msg, thr);
    }
}
