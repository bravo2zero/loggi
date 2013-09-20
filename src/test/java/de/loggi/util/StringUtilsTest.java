package de.loggi.util;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

/**
 * @author CptSpaetzle
 */
public class StringUtilsTest {

    @Test
    public void testRepeatString() throws Exception {
        assertEquals("*****", StringUtils.repeatString("*", 5));
        assertNotEquals("***", StringUtils.repeatString("#", 5));
    }

    @Test
    public void testCreateProgressBar() throws Exception {
        assertEquals("\r[===>                ] 20/100", StringUtils.createProgressBar(20, 100));
        assertEquals("\r[=========>          ] 50/100", StringUtils.createProgressBar(50, 100));
    }

}
