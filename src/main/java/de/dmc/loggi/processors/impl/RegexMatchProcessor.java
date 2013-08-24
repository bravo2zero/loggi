package de.dmc.loggi.processors.impl;

import de.dmc.loggi.exceptions.ConfigurationException;
import de.dmc.loggi.model.Column;
import de.dmc.loggi.processors.AbstractColumnProcessor;
import de.dmc.loggi.processors.AttributeDef;
import de.dmc.loggi.processors.MetaInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author CptSpaetzle
 */
@MetaInfo(
        description = "Column value is matched by regular expression",
        attributes = {
        @AttributeDef(name = "regex", description = "regular expression to match against record string. Returns empty string if no match found, first match in other case"),
}
)
public class RegexMatchProcessor extends AbstractColumnProcessor<String> {

    public RegexMatchProcessor(Column column) throws ConfigurationException {
        super(column);
    }

    @Override
    public String getColumnValue(String record) {

        Pattern pattern = Pattern.compile(this.<String>getAttributeValue(ATTR_REGEX));
        Matcher matcher = pattern.matcher(record);
        if(matcher.find()){
            return matcher.group();
        }
        return column.getDefaultValue();
    }
}
