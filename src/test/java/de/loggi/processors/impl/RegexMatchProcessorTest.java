package de.loggi.processors.impl;

import de.loggi.exceptions.ConfigurationException;
import de.loggi.model.Attribute;
import de.loggi.processors.AbstractColumnProcessorTest;
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

    @Test
    public void testGetColumnValueGroup() throws Exception {
        RegexMatchProcessor processor = new RegexMatchProcessor(
                createColumn("column1",
                        createAttribute("regex", "(abc)(\\d{3})"),
                        createAttribute("group","2")));
        String record = "ab123 abc123 abc234 abc12";
        String result = processor.getColumnValue(record);
        assertEquals(result, "123");
    }

    @Test
    public void testNewLineSearches() throws Exception{
        RegexMatchProcessor processor = new RegexMatchProcessor(
                createColumn("column_userid",
                createAttribute("regex","(?m)^UserID:\\s(.+?)$"),
                createAttribute("group","1")));
        String record = "[2014-07-23 00:00:07.201 +0200CEST] ERROR rp-app2.boreus.de ES2 appserver0 [Tredex-RunnersPoint_DE-Site] [-] org.apache.catalina.core.ContainerBase.[Engine].[rp-app2].[/].[jsp] [] [Storefront] [Lv0GO8tue-YxO5igkpzVubtkbn6kqS2ah9tGuGn8] [Oh4qblPO3r_TgnAK-1-01] \"Oh4qblPO3r_TgnAK-1-01\" Servlet.service() for servlet jsp threw exception java.lang.NullPointerException: null\n" +
                "\n" +
                "System Information\n" +
                "------------------\n" +
                "RequestID: Oh4qblPO3r_TgnAK-1-01\n" +
                "StartDate: Tue Jul 22 23:59:27 CEST 2014\n" +
                "SessionType: STOREFRONT\n" +
                "SessionID: Lv0GO8tue-YxO5igkpzVubtkbn6kqS2ah9tGuGn8\n" +
                "UserID: 43oKcILPjdUAAAFHEBMHrGAW\n" +
                "ServerName: www.runnerspoint.com\n" +
                "ServerPort: 80\n" +
                "\n" +
                "Request Information\n" +
                "-------------------\n" +
                "URI: /servlet/Beehive/WFS/Tredex-RunnersPoint_DE-Site/de_DE/-/EUR/ViewParametricSearch-SimpleOfferSearch\n" +
                "Method: GET\n" +
                "PathInfo: /WFS/Tredex-RunnersPoint_DE-Site/de_DE/-/EUR/ViewParametricSearch-SimpleOfferSearch\n" +
                "QueryString: SearchTerm=nike+free&SortingAttribute=Attr_Scoring-desc\n" +
                "Remote Address: 78.48.52.250\n" +
                "\n" +
                "Request Parameters\n" +
                "------------------\n" +
                "SortingAttribute=Attr_Scoring-desc\n" +
                "SearchTerm=nike free\n";
        String result = processor.getColumnValue(record);
        assertEquals(result,"43oKcILPjdUAAAFHEBMHrGAW");
    }

}
