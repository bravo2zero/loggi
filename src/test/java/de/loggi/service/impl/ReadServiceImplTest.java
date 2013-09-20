package de.loggi.service.impl;

import org.testng.annotations.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertNotNull;

/**
 * @author CptSpaetzle
 */
public class ReadServiceImplTest {

    @Test(enabled = false)
    public void testDateFormat() throws Exception {
        String date = "2013-08-29 00:04:40.376 +0200MESZ";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Zz");
        Date dateObj = df.parse(date);
        assertNotNull(dateObj);
    }

    @Test
    public void testDateFormat2() throws Exception {
        String dateString = "19/Sep/2013:02:38:38 +0200";
        SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z");
        Date dateObj = df.parse(dateString);
        assertNotNull(dateObj);

    }

    @Test(enabled = false)
    public void testRegex() {
        Pattern pattern = Pattern.compile("\\((\\d+?)\\)");
        Matcher matcher = pattern.matcher("varchar2(255)");
        if (matcher.find()) {
            assertNotNull(matcher.group(1));
        }
    }


}