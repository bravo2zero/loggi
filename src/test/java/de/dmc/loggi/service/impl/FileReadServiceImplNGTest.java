package de.dmc.loggi.service.impl;

import org.testng.annotations.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author CptSpaetzle
 */
public class FileReadServiceImplNGTest {

    Path testSourcePath = FileSystems.getDefault().getPath("test-source.log");

    @Test
    public void testReadComplete() throws Exception {
        cleanTestSourceFile();
        prepareTestSourceFile("Come get some");
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