package de.loggi.processors;

import de.loggi.exceptions.ConfigurationException;
import de.loggi.model.Attribute;
import de.loggi.model.Column;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;

/**
 * @author CptSpaetzle
 */

public class AbstractColumnProcessorTest {

    @Test(expectedExceptions = ConfigurationException.class)
    public void testInitializeProcessorNull() throws Exception{
        AbstractColumnProcessor abstractColumnProcessor = new AbstractColumnProcessor(null) {
            @Override
            public String getColumnValue(String record) {
                return null;
            }

            @Override
            public String getProcessedValue(String record) {
                return null;
            }
        };
    }

    protected Column createColumn(String name, Attribute... attributes){
        Column column = new Column();
        column.setName(name);
        column.setAttributes(Arrays.asList(attributes));
        return column;
    }

    protected Attribute createAttribute(String name, String value){
        Attribute attribute = new Attribute();
        attribute.setName(name);
        attribute.setValue(value);
        return attribute;
    }
}
