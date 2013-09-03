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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author CptSpaetzle
 */
public class ReadServiceImplTest {

    Path testSourcePath = FileSystems.getDefault().getPath("test-source.log");

    @Test(enabled = false)
    public void testReadComplete() throws Exception {
        cleanTestSourceFile();
        prepareTestSourceFile("Come get some");
    }

    @Test(enabled = false)
    public void testDateFormat() throws Exception {
        String date = "2013-08-29 00:04:40.376 +0200MESZ";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Zz");
        Date dateObj = df.parse(date);
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

    @Test
    public void testProgressBarString(){
        ReadServiceImpl service = new ReadServiceImpl();
        assertEquals("\r[===>                ]",service.progressBarString(20,100));
        assertEquals("\r[=========>          ]",service.progressBarString(50,100));
    }

    private void prepareTestSourceFile(String content) throws IOException {
        try (BufferedWriter testSource = Files.newBufferedWriter(testSourcePath, Charset.defaultCharset())) {
            testSource.append(content);
            testSource.flush();
        }
    }


    private void cleanTestSourceFile() throws IOException {
        if (testSourcePath.toFile().exists()) {
            Files.delete(testSourcePath);
        }
    }
}