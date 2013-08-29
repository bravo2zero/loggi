package de.loggi.service.impl;

import de.loggi.processors.AbstractColumnProcessor;
import de.loggi.processors.MetaInfo;
import de.loggi.service.HelpService;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author CptSpaetzle
 */
public class HelpServiceImpl implements HelpService {
    public static final String NEWLINE = System.getProperty("line.separator");
    public static final String PROCESSORS_PACKAGE = "de.loggi.processors.impl";

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private String author;
    private String projectVersion;
    private String projectName;
    private String projectUrl;
    private String description;

    @Override
    public void printUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar loggi.jar", options, true);
    }

    @Override
    public void printHelp(Options options) {
        String header = String.format("" + NEWLINE +
                "%1$s - %2$s" + NEWLINE +
                "%3$s" + NEWLINE +
                "Author: %4$s" + NEWLINE +
                "%5$s" +  NEWLINE,
                projectName, projectVersion, description, author, projectUrl);
        System.out.println(header);
        printUsage(options);

        printHeader("Available Column Processors:");
        for (Class annotatedProcessor : getPackageClasses(PROCESSORS_PACKAGE)) {
            try {
                System.out.println(AbstractColumnProcessor.getProcessorInfo(annotatedProcessor));
            } catch (Exception e) {
                logger.error("Exception compiling column processor usage info", e);
            }
        }

        // TODO printHeader("Supported Data Types:");

    }

    private void printHeader(String headerText){
        System.out.println(NEWLINE + headerText + NEWLINE);
    }

    private Set<Class<?>> getPackageClasses(String packageName) {
        Reflections reflections = new Reflections(packageName);
        return reflections.getTypesAnnotatedWith(MetaInfo.class);
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
