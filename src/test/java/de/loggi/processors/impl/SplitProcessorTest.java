package de.loggi.processors.impl;

import de.loggi.processors.AbstractColumnProcessorTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author CptSpaetzle
 */
public class SplitProcessorTest extends AbstractColumnProcessorTest{

    @Test
    public void testInitializeProcessorDefaultAttributes() throws Exception {
        SplitProcessor processor = new SplitProcessor(createColumn("column1"));
        assertNotNull(processor.getAttributeValue(SplitProcessor.ATTR_SEPARATOR));
        assertNotNull(processor.getAttributeValue(SplitProcessor.ATTR_COLUMN));
    }

    @Test
    public void testGetColumnValue() throws Exception {
        SplitProcessor processor = new SplitProcessor(
                createColumn("column1",
                        createAttribute("separator", "\\|"),
                        createAttribute("column","2")
                ));
        String record = "a|b|c|d";
        String result = processor.getColumnValue(record);
        assertEquals(result, "c");
    }
}
