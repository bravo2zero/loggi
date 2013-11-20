package de.loggi.service.writers;

import de.loggi.exceptions.ConfigurationException;
import de.loggi.model.Attribute;
import de.loggi.processors.AttributeDef;
import de.loggi.processors.ColumnProcessor;
import de.loggi.processors.MetaInfo;
import de.loggi.service.ConfigurationService;
import de.loggi.service.WriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CptSpaetzle
 */
public abstract class AbstractWriteServiceImpl implements WriteService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String NEWLINE = System.getProperty("line.separator");
    public static final String TAB = "\t";

    protected ConfigurationService configuration;
    protected Map<String, Object> attributeMap = new HashMap<>();

    @Override
    public void finalizeAndShutdown() {
    }

    @Override
    public <V> V getAttributeValue(String key) {
        return (V) attributeMap.get(key);
    }

    @Override
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configuration = configurationService;
    }

    @Override
    public void initialize() throws ConfigurationException {
        // set attributes
        Map<String, String> config = new HashMap<>();
        for (Attribute attribute : configuration.getTemplate().getWriter().getAttributes()) {
            config.put(attribute.getName(), attribute.getValue());
        }
        //add defaults, check required
        if (this.getClass().isAnnotationPresent(MetaInfo.class)) {
            MetaInfo metaInfo = this.getClass().getAnnotation(MetaInfo.class);
            for (AttributeDef attributeDef : metaInfo.attributes()) {
                String value = config.get(attributeDef.name()) == null ? attributeDef.defaultValue() : config.get(attributeDef.name());
                if (value == null || value.isEmpty()) {
                    throw new ConfigurationException("Missing required writer attribute '" + attributeDef.name() + "'");
                }
                attributeMap.put(attributeDef.name(), value);
            }
        } else {
            throw new ConfigurationException("Missing MetaInfo for WriteService class: " + this.getClass().getName());
        }
    }

    public static String getProcessorInfo(Class<? extends WriteService> writerClass) {
        StringBuilder builder = new StringBuilder();
        if (writerClass.isAnnotationPresent(MetaInfo.class)) {
            MetaInfo metaInfo = writerClass.getAnnotation(MetaInfo.class);
            builder.append(writerClass.getName()).append(" - ").append(metaInfo.description()).append(NEWLINE);

            for (AttributeDef attributeDef : metaInfo.attributes()) {
                builder
                        .append(TAB).append(attributeDef.name()).append(TAB + TAB).append(attributeDef.description())
                        .append(attributeDef.defaultValue().isEmpty() ? "" : " (default: " + attributeDef.defaultValue() + ")")
                        .append(NEWLINE);
            }
        }
        return builder.toString();
    }
}
