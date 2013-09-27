package de.loggi.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author CptSpaetzle
 */
public class FileUtils {
    static Pattern pathWithoutGlob = Pattern.compile("^(.*)\\*");

    public static Path getAbsolutePathFromPath(String path, Path currentPath) {
        Matcher m = pathWithoutGlob.matcher(path);
        if(m.find()){
            String leftPath = m.group(1);
            Path resolved = currentPath.resolve(Paths.get(leftPath));
            return resolved;
        }else{
            return currentPath;
        }
    }
}
