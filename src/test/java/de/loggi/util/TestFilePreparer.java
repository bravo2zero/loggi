package de.loggi.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CptSpaetzle
 */
public class TestFilePreparer {
    List<Path> files;

    public TestFilePreparer() {
        this.files = new ArrayList<>();
    }

    public void prepareTestSourceFile(String content, String fileName) throws IOException {
        Path testFilePath = FileSystems.getDefault().getPath(fileName);
        files.add(testFilePath);
        try (BufferedWriter testSource = Files.newBufferedWriter(testFilePath, Charset.defaultCharset())) {
            testSource.append(content);
            testSource.flush();
        }
    }

    public List<Path> getFiles() {
        return files;
    }

    public void cleanTestFiles() throws IOException {
        for (Path file : files) {
            Files.delete(file);
        }
    }
}
