package de.dmc.loggi.processors.impl;

import de.dmc.loggi.exceptions.ConfigurationException;
import de.dmc.loggi.model.Attribute;
import de.dmc.loggi.processors.AbstractColumnProcessorTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author CptSpaetzle
 */
public class RegexMatchProcessorTest extends AbstractColumnProcessorTest {


    @Test(expectedExceptions = ConfigurationException.class)
    public void testInitializeProcessorMissingAttribute() throws Exception {
        Attribute mockAttr = createAttribute("someRandomAttr", "someValue");
        RegexMatchProcessor processor = new RegexMatchProcessor(createColumn("column1", mockAttr));
    }

    @Test
    public void testInitializeProcessor() throws Exception {
        String regex = "abc";
        RegexMatchProcessor processor = new RegexMatchProcessor(createColumn("column1", createAttribute("regex", regex)));
        assertEquals(processor.getAttributeValue("regex"), regex);
    }

    @Test
    public void testGetColumnValue() throws Exception {
        RegexMatchProcessor processor = new RegexMatchProcessor(createColumn("column1", createAttribute("regex", "abc\\d{3}")));
        String record = "ab123 abc123 abc234 abc12";
        String result = processor.getColumnValue(record);
        assertEquals(result, "abc123");
    }
}
