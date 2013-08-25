package de.dmc.loggi.processors.impl;

import de.dmc.loggi.exceptions.ConfigurationException;
import de.dmc.loggi.model.Column;
import de.dmc.loggi.processors.AbstractColumnProcessor;
import de.dmc.loggi.processors.AttributeDef;
import de.dmc.loggi.processors.ColumnProcessor;
import de.dmc.loggi.processors.MetaInfo;

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
    public String getColumnValue(String record) {
        int maxSize = Integer.valueOf(this.<String>getAttributeValue(ATTR_MAXSIZE));
        int actualMaxSize = maxSize > record.length() ? record.length() : maxSize;
        return record == null ? "" : record.substring(0, actualMaxSize);
    }
}
