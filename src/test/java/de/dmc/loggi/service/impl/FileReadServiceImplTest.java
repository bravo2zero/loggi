package de.dmc.loggi.service.impl;

import org.testng.annotations.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author CptSpaetzle
 */
public class FileReadServiceImplTest {

    Path testSourcePath = FileSystems.getDefault().getPath("test-source.log");

    @Test(enabled = false)
    public void testReadComplete() throws Exception {
        cleanTestSourceFile();
        prepareTestSourceFile("Come get some");
    }


    @Test
    public void testRegex(){
        Pattern pattern = Pattern.compile("(.*)(abc)(.*)");
        Matcher matcher = pattern.matcher("123abc456");
        if(matcher.matches()){
            System.out.println(matcher.group());
        }
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