package de.dmc.loggi.service.impl;

import de.dmc.loggi.processors.ColumnProcessor;
import de.dmc.loggi.service.ReadService;
import de.dmc.loggi.service.ConfigurationService;
import de.dmc.loggi.service.WriteService;
import de.dmc.loggi.threads.NamingThreadFactory;
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
    private int numberOfThreads = 4;

    //TODO limit max record length (wrong separator?)
    // TODO put some progress indication in logs

    @Override
    public void process() {
        executor = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new NamingThreadFactory("ColumnProcessor"));

        // init phase
        Pattern separator = Pattern.compile("(.*)(" + configuration.getTemplate().getRecordSeparator()+")(.*)");


        // read file
        try (BufferedReader reader = Files.newBufferedReader(configuration.getSource(), Charset.defaultCharset())) {
            String currentLine = null;
            StringBuilder currentRecord = new StringBuilder();
            while ((currentLine = reader.readLine()) != null) {
                Matcher matcher = separator.matcher(currentLine);
                if(matcher.matches()){
                    currentRecord.append(matcher.group(1));
                    if(currentRecord.length() > 0){
                        executor.submit(new RecordTask(currentRecord.toString()));
                        currentRecord = new StringBuilder();
                    }
                    currentRecord.append(matcher.group(2) + matcher.group(3));
                }else{
                    currentRecord.append(currentLine);
                }
            }
            executor.submit(new RecordTask(currentRecord.toString()));
        } catch (IOException ex) {

            logger.error("Error reading source ["+configuration.getSource().toString()+"]", ex);
        }

        //wait for the executor to finish
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE,TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            logger.error("Interrupted executor termination",e);
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

    class RecordTask implements Callable<Object>{
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
