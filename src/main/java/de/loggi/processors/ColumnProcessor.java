package de.loggi.processors;

import de.loggi.exceptions.ConfigurationException;
import de.loggi.model.Column;

/**
 * @author CptSpaetzle
 */
public interface ColumnProcessor
{
    String getColumnValue(String record);
    public <V> V getAttributeValue(String key) throws ConfigurationException;
    public Column getColumn();
}
