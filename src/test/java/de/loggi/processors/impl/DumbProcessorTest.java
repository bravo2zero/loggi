package de.loggi.processors.impl;

import de.loggi.model.Attribute;
import de.loggi.model.Column;
import de.loggi.processors.AbstractColumnProcessorTest;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author CptSpaetzle
 */
public class DumbProcessorTest extends AbstractColumnProcessorTest {

    @Test
    public void testInitializeProcessor() throws Exception {
        String defaultAttrName = "maxSize";
        Attribute mockAttr = createAttribute("someRandomAttr", "someValue");
        Column mockColumn = createColumn("column1", mockAttr);
        DumbProcessor processor = new DumbProcessor(mockColumn);
        assertEquals(processor.getColumn(), mockColumn);
        assertNull(processor.getAttributeValue(mockAttr.getName()));
        assertNotNull(processor.getAttributeValue(defaultAttrName));
    }

    @Test
    public void testGetColumnValue() throws Exception {
        Column column = createColumn("columnName");
        DumbProcessor processor = new DumbProcessor(column);
        String recordValue = "value";
        String result = processor.getColumnValue(recordValue);
        assertEquals(result, recordValue);
    }

    @Test
    public void testGetColumnValueMaxSize() throws Exception {
        Column column = createColumn("columnName", createAttribute("maxSize", "3"));
        DumbProcessor processor = new DumbProcessor(column);
        String recordValue = "value";
        String result = processor.getColumnValue(recordValue);
        assertEquals(result, "val");
    }


}
