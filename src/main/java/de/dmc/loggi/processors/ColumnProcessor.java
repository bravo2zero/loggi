package de.dmc.loggi.processors;

import de.dmc.loggi.exceptions.ConfigurationException;
import de.dmc.loggi.model.Column;

/**
 * @author CptSpaetzle
 */
public interface ColumnProcessor<T>
{
    public static final String ATTR_REGEX = "regex";
    public static final String ATTR_MAXSIZE = "maxSize";

    T getColumnValue(String record);
    public <V> V getAttributeValue(String key) throws ConfigurationException;
    public Column getColumn();
}
