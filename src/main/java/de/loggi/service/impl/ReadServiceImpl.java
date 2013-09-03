package de.loggi.service.impl;

import de.loggi.model.Parameter;
import de.loggi.service.ConfigurationService;
import de.loggi.service.ReadService;
import de.loggi.service.WriteService;
import de.loggi.threads.NamingThreadFactory;
import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadServiceImpl implements ReadService {

    private static final long SLEEPTIME = 1000;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private ConfigurationService configuration;
    private WriteService writeService;
    private ThreadPoolExecutor executor;
    private int numberOfThreads = 0;
    private int maxRecordLength = 10000;

    // TODO add support for wildcards in source

    @Override
    public void process() {
        initialize();

        // count total number of lines in source file
        long sourceNumberOfLines = 0;
        try (BufferedReader reader = Files.newBufferedReader(configuration.getSource(), Charset.defaultCharset())) {
            String currentLine = null;
            while ((currentLine = reader.readLine()) != null) {
                sourceNumberOfLines++;
            }
            System.out.println("Source lines: "+ sourceNumberOfLines);
        } catch (IOException e) {
            logger.error("Error calculating number of lines in source file");
        }

        // init phase
        Pattern separator = Pattern.compile("(.*)(" + configuration.getTemplate().getRecordSeparator() + ")(.*)");

        // read file
        long numberOfRecords = 0;
        try (BufferedReader reader = Files.newBufferedReader(configuration.getSource(), Charset.defaultCharset())) {
            String currentLine = null;
            StringBuilder currentRecord = new StringBuilder();
            while ((currentLine = reader.readLine()) != null) {
                if (currentRecord.length() > maxRecordLength) {
                    throw new IOException("maxRecordLength overflow, check if your separator is correct!");
                }
                // TODO warning! - do not use groups in record separator! - fix me
                Matcher matcher = separator.matcher(currentLine);
                if (matcher.matches()) {
                    currentRecord.append(matcher.group(1));
                    if (currentRecord.length() > 0) {
                        executor.submit(new RecordTask(currentRecord.toString()));
                        numberOfRecords++;
                        currentRecord = new StringBuilder();
                    }
                    currentRecord.append(matcher.group(2) + matcher.group(3));
                } else {
                    currentRecord.append(currentLine);
                }
            }
            executor.submit(new RecordTask(currentRecord.toString()));
            numberOfRecords++;
        } catch (IOException ex) {
            logger.error("Error reading source [" + configuration.getSource().toString() + "]", ex);
        }
        System.out.println("Done reading, " + numberOfRecords + " records submitted.");

        //wait for the executor to finish, print out progress bar
        executor.shutdown();
        while (executor.getQueue().size() > 0) {
            try {
                String progressBar = progressBarString(executor.getCompletedTaskCount(), executor.getTaskCount());
                System.out.print(progressBar + " " + executor.getCompletedTaskCount() + "/" + executor.getTaskCount());
                Thread.sleep(SLEEPTIME);
            } catch (InterruptedException e) {
                logger.error("Interrupted ThreadPoolExecutor termination", e);
            }
        }
        String progressBar = progressBarString(executor.getCompletedTaskCount(), executor.getTaskCount());
        System.out.println(progressBar + " " + executor.getCompletedTaskCount() + "/" + executor.getTaskCount());
    }

    String progressBarString(long done, long total) {
        int scale = 20;
        StringBuilder builder = new StringBuilder("\r");
        int progress = Math.round(((float) done / (float) total) * scale);
        builder.append("[").append(repeat("=", progress - 1));
        builder.append(">").append(repeat(" ", scale - progress));
        builder.append("]");
        return builder.toString();
    }

    private void initialize() {
        // get params
        CommandLine commandLine = configuration.getCommandLine();
        if (commandLine.hasOption(Parameter.MAX_RECORD_LENGTH.getShortName())) {
            setMaxRecordLength(Integer.valueOf(commandLine.getOptionValue(Parameter.MAX_RECORD_LENGTH.getShortName())));
        }
        if (commandLine.hasOption(Parameter.PROCESSOR_THREADS.getShortName())) {
            setNumberOfThreads(Integer.valueOf(commandLine.getOptionValue(Parameter.PROCESSOR_THREADS.getShortName())));
        }

        // define number of processing threads
        if (numberOfThreads == 0) {
            // (numberOfProcessors - 1) by default
            int numberOfCPUs = Runtime.getRuntime().availableProcessors();
            numberOfThreads = numberOfCPUs > 1 ? numberOfCPUs - 1 : numberOfCPUs;
        }
        //init executors pool
        executor = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new NamingThreadFactory("ColumnProcessor"));
        logger.debug("Processing with {} threads", numberOfThreads);
    }

    private String repeat(String val, int count) {
        StringBuilder repeat = new StringBuilder();
        for (int i = 0; i < count; i++) {
            repeat.append(val);
        }
        return repeat.toString();
    }

    public void setConfiguration(ConfigurationService configuration) {
        this.configuration = configuration;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public void setWriteService(WriteService writeService) {
        this.writeService = writeService;
    }

    public void setMaxRecordLength(int maxRecordLength) {
        this.maxRecordLength = maxRecordLength;
    }

    class RecordTask implements Callable<Object> {
        private String record;

        public RecordTask(String record) {
            this.record = record;
        }

        @Override
        public Object call() throws Exception {
            writeService.processRecord(record);
            return null;
        }

    }


}
