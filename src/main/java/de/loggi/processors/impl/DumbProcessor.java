package de.loggi.processors.impl;

import de.loggi.exceptions.ConfigurationException;
import de.loggi.model.Column;
import de.loggi.processors.AbstractColumnProcessor;
import de.loggi.processors.AttributeDef;
import de.loggi.processors.MetaInfo;

/**
 * @author CptSpaetzle
 */
@MetaInfo(
        description = "Simplest case of processor. Just returning raw record value as java.lang.String.",
        attributes = {
                @AttributeDef(name = DumbProcessor.ATTR_MAXSIZE, description = "maximum column value size", defaultValue = "255")
        }
)
public class DumbProcessor extends AbstractColumnProcessor {

    public static final String ATTR_MAXSIZE = "maxSize";

    public DumbProcessor(Column column) throws ConfigurationException {
        super(column);
    }

    @Override
    public String getProcessedValue(String record) {
        int maxSize = Integer.valueOf(this.<String>getAttributeValue(ATTR_MAXSIZE));
        int actualMaxSize = maxSize > record.length() ? record.length() : maxSize;
        return record == null ? "" : record.substring(0, actualMaxSize);
    }
}
