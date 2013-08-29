package de.loggi.processors.impl;

import de.loggi.exceptions.ConfigurationException;
import de.loggi.model.Column;
import de.loggi.processors.AbstractColumnProcessor;
import de.loggi.processors.AttributeDef;
import de.loggi.processors.MetaInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author CptSpaetzle
 */
@MetaInfo(
        description = "Column value is matched by regular expression",
        attributes = {
                @AttributeDef(name = RegexMatchProcessor.ATTR_REGEX, description = "regular expression to match against record string. Returns empty string if no match found, first match in other case"),
                @AttributeDef(name = RegexMatchProcessor.ATTR_GROUP, description = "if available, returns regular expression specific matched group value", defaultValue = "0")
        }
)
public class RegexMatchProcessor extends AbstractColumnProcessor {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String ATTR_REGEX = "regex";
    public static final String ATTR_GROUP = "group";

    public RegexMatchProcessor(Column column) throws ConfigurationException {
        super(column);
    }

    @Override
    public String getProcessedValue(String record) {

        Pattern pattern = Pattern.compile(this.<String>getAttributeValue(ATTR_REGEX));
        Matcher matcher = pattern.matcher(record);

        int groupNumber = 0; // return whole match by default
        if (Integer.valueOf(this.<String>getAttributeValue(ATTR_GROUP)) <= matcher.groupCount()) {
            groupNumber = Integer.valueOf(this.<String>getAttributeValue(ATTR_GROUP));
        } else {
            logger.warn("No Group {} found within regex, column:{}", this.<String>getAttributeValue(ATTR_GROUP), getColumn().getName());
        }

        if (matcher.find()) {
            return matcher.group(groupNumber);
        }
        return column.getDefaultValue();
    }
}
