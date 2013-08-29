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

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private ConfigurationService configuration;
    private WriteService writeService;
    private ThreadPoolExecutor executor;
    private int numberOfThreads = 0;
    private int maxRecordLength = 10000;

    // TODO put some progress indication in logs

    @Override
    public void process() {
        initialize();
        if (numberOfThreads == 0) {
            // (numberOfProcessors - 1) by default
            int numberOfCPUs = Runtime.getRuntime().availableProcessors();
            numberOfThreads = numberOfCPUs > 1 ? numberOfCPUs - 1 : numberOfCPUs;
        }
        logger.debug("Processing with {} threads", numberOfThreads);
        executor = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new NamingThreadFactory("ColumnProcessor"));

        // init phase
        Pattern separator = Pattern.compile("(.*)(" + configuration.getTemplate().getRecordSeparator() + ")(.*)");

        // read file
        try (BufferedReader reader = Files.newBufferedReader(configuration.getSource(), Charset.defaultCharset())) {
            String currentLine = null;
            StringBuilder currentRecord = new StringBuilder();
            while ((currentLine = reader.readLine()) != null) {
                if (currentRecord.length() > maxRecordLength) {
                    throw new IOException("maxRecordLength overflow, check if your separator is correct!");
                }
                // TODO warning! - do not use groups in record separator! - fix
                Matcher matcher = separator.matcher(currentLine);
                if (matcher.matches()) {
                    currentRecord.append(matcher.group(1));
                    if (currentRecord.length() > 0) {
                        executor.submit(new RecordTask(currentRecord.toString()));
                        currentRecord = new StringBuilder();
                    }
                    currentRecord.append(matcher.group(2) + matcher.group(3));
                } else {
                    currentRecord.append(currentLine);
                }
            }
            executor.submit(new RecordTask(currentRecord.toString()));
        } catch (IOException ex) {

            logger.error("Error reading source [" + configuration.getSource().toString() + "]", ex);
        }

        //wait for the executor to finish
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            logger.error("Interrupted ThreadPoolExecutor termination", e);
        }
    }

    private void initialize() {
        CommandLine commandLine = configuration.getCommandLine();
        if(commandLine.hasOption(Parameter.MAX_RECORD_LENGTH.getShortName())){
            setMaxRecordLength(Integer.valueOf(commandLine.getOptionValue(Parameter.MAX_RECORD_LENGTH.getShortName())));
        }
        if(commandLine.hasOption(Parameter.PROCESSOR_THREADS.getShortName())){
            setNumberOfThreads(Integer.valueOf(commandLine.getOptionValue(Parameter.PROCESSOR_THREADS.getShortName())));
        }
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
