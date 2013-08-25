package de.dmc.loggi.processors;

import de.dmc.loggi.exceptions.ConfigurationException;
import de.dmc.loggi.model.Attribute;
import de.dmc.loggi.model.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CptSpaetzle
 */
public abstract class AbstractColumnProcessor implements ColumnProcessor {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String NEWLINE = System.getProperty("line.separator");
    public static final String TAB = "\t";
    protected Column column;


    protected Map<String, Object> attributeMap = new HashMap<>();

    public AbstractColumnProcessor(Column column) throws ConfigurationException {
        initializeProcessor(column);
    }

    protected void initializeProcessor(Column column)throws ConfigurationException{
        if (column == null) {
            throw new ConfigurationException("Cannot create ColumnProcessor with null column");
        }

        this.column = column;
        // get values from config template
        Map<String, String> config = new HashMap<>();
        for (Attribute attribute : column.getAttributes()) {
            config.put(attribute.getName(), attribute.getValue());
        }

        //add defaults, check required
        if (this.getClass().isAnnotationPresent(MetaInfo.class)) {
            MetaInfo metaInfo = this.getClass().getAnnotation(MetaInfo.class);
            for (AttributeDef attributeDef : metaInfo.attributes()) {
                String value = config.get(attributeDef.name()) == null ? attributeDef.defaultValue() : config.get(attributeDef.name());
                if (value == null || value.isEmpty()) {
                    throw new ConfigurationException("Missing required attribute '" + attributeDef.name() + "' for column '" + column.getName() + "'");
                }
                attributeMap.put(attributeDef.name(), value);
            }
        } else {
            throw new ConfigurationException("Missing MetaInfo for ColumnProcessor class: " + this.getClass().getName());
        }
    }

    @Override
    abstract public String getColumnValue(String record);

    @Override
    public <V> V getAttributeValue(String key) {
        return (V) attributeMap.get(key);
    }

    public static String getProcessorInfo(Class<? extends ColumnProcessor> processorClass) {
        StringBuilder builder = new StringBuilder();
        if (processorClass.isAnnotationPresent(MetaInfo.class)) {
            MetaInfo metaInfo = processorClass.getAnnotation(MetaInfo.class);
            builder.append(processorClass.getName()).append(" - ").append(metaInfo.description()).append(NEWLINE);

            for (AttributeDef attributeDef : metaInfo.attributes()) {
                builder
                        .append(TAB).append(attributeDef.name()).append(TAB + TAB).append(attributeDef.description())
                        .append(attributeDef.defaultValue().isEmpty() ? "" : " (default: " + attributeDef.defaultValue() + ")")
                        .append(NEWLINE);
            }
        }
        return builder.toString();
    }

    @Override
    public Column getColumn() {
        return column;
    }
}
