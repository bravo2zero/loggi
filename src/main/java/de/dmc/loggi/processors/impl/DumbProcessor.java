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
                @AttributeDef(name = "maxSize", description = "maximum column value size", defaultValue = "255")
        }
)
public class DumbProcessor extends AbstractColumnProcessor<String> {

    public DumbProcessor(Column column) throws ConfigurationException {
        super(column);
    }

    @Override
    public String getColumnValue(String record) {
        return record == null ? "" : record.substring(0,Integer.valueOf(this.<String>getAttributeValue(ColumnProcessor.ATTR_MAXSIZE)));
    }
}
