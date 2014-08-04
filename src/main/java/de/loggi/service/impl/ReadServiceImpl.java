package de.loggi.service.impl;

import de.loggi.exceptions.ProcessingException;
import de.loggi.model.Parameter;
import de.loggi.service.ConfigurationService;
import de.loggi.service.ReadService;
import de.loggi.service.WriteService;
import de.loggi.threads.NamingThreadFactory;
import de.loggi.util.StringUtils;
import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadServiceImpl implements ReadService {

    private static final long SLEEPTIME = 1000;
    public static final String NEWLINE = System.getProperty("line.separator");
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private ConfigurationService configuration;
    private WriteService writeService;
    private ThreadPoolExecutor executor;
    private int numberOfThreads = 0;
    private int maxRecordLength = 50000;
    private double memoryUsageThreshold = 80d;

    @Override
    public void process() {
        initialize();

        for (Path sourceFile : configuration.getSources()) {
            processFile(sourceFile);
            // prevent OOM, wait for the queue size to drop
            // TODO should improve implementation to limit record submits by some constant number instead of by file (otherwise some huge file could still cause oom problems)
            while (getUsedMemoryPercent() > memoryUsageThreshold && executor.getQueue().size() > 0) {
                try {
                    System.out.println("Memory usage threshold reached.. waiting for the queue to go down. queueSize:" + executor.getQueue().size() + " memUsage:" + new DecimalFormat("#.00").format(getUsedMemoryPercent()));
                    Thread.sleep(SLEEPTIME);
                } catch (InterruptedException e) {
                    logger.error("Exception waiting for the queue to free up", e);
                }
            }
        }
        //wait for the executor to finish, print out progress bar
        executor.shutdown();
        while (executor.getQueue().size() > 0) {
            try {
                System.out.print(StringUtils.createProgressBar(executor.getCompletedTaskCount(), executor.getTaskCount()));
                Thread.sleep(SLEEPTIME);
            } catch (InterruptedException e) {
                logger.error("Interrupted ThreadPoolExecutor termination", e);
            }
        }
        System.out.println(StringUtils.createProgressBar(executor.getCompletedTaskCount(), executor.getTaskCount()));
    }

    @Override
    public void processFile(Path file) {
        // count total number of lines in source file
        long sourceNumberOfLines = 0;
        try (BufferedReader reader = Files.newBufferedReader(file, Charset.defaultCharset())) {
            String currentLine = null;
            while ((currentLine = reader.readLine()) != null) {
                sourceNumberOfLines++;
            }
            System.out.println(file.getFileName() + ": " + sourceNumberOfLines + " line(s)");
        } catch (IOException e) {
            logger.error("Error calculating number of lines in source file");
        }

        // init phase
        Pattern separator = Pattern.compile("(.*)(" + configuration.getTemplate().getRecordSeparator() + ")(.*)");

        // read file
        long numberOfRecords = 0;
        try (BufferedReader reader = Files.newBufferedReader(file, Charset.defaultCharset())) {
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
                    currentRecord.append(matcher.group(2) + matcher.group(3)).append(NEWLINE);
                } else {
                    currentRecord.append(currentLine).append(NEWLINE);
                }
            }
            executor.submit(new RecordTask(currentRecord.toString()));
            numberOfRecords++;
        } catch (IOException ex) {
            logger.error("Error reading source [" + configuration.getSources().toString() + "]", ex);
        }
        System.out.println(file.toAbsolutePath() + ": done. submited:" + numberOfRecords + " queueSize:" + executor.getQueue().size() + " memoryUsage:" + new DecimalFormat("#.00").format(getUsedMemoryPercent()));
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

    /**
     * Returns amount of Heap used in percents
     *
     * @return
     */
    public double getUsedMemoryPercent() {
        Runtime runtime = Runtime.getRuntime();
        double maxMemoryMb = runtime.maxMemory() / (1024 * 1024);
        double usedMemoryMb = runtime.totalMemory() / (1024 * 1024);
        return (usedMemoryMb / maxMemoryMb) * 100;
    }

    class RecordTask implements Callable<Object> {
        private String record;

        public RecordTask(String record) {
            this.record = record;
        }

        @Override
        public Object call() throws Exception {
            try {
                writeService.processRecord(record);
            } catch (ProcessingException ex) {
                logger.error("Exception processing record", ex);
            }
            return null;
        }

    }


}
