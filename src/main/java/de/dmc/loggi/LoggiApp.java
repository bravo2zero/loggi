package de.dmc.loggi;

import de.dmc.loggi.model.Parameter;
import de.dmc.loggi.service.ConfigurationService;

import de.dmc.loggi.service.HelpService;
import de.dmc.loggi.service.WriteService;
import de.dmc.loggi.service.impl.ReadServiceImpl;
import org.apache.commons.cli.*;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

/**
 * Main application starter
 */
public class LoggiApp {

    static final Logger logger = LoggerFactory.getLogger(LoggiApp.class);
    CommandLine commandLine;
    Options options;
    ApplicationContext context;
    private HelpService help;

    public static void main(String[] args) {
        LoggiApp app = new LoggiApp();
        app.initializeContext();

        try {
            app.initializeOptions(args);
            app.start();
        } catch (ParseException e) {
            logger.error("Error initializing options", e);
            app.printUsage();
        }
    }

    private void printUsage() {
        help.printUsage(options);
    }

    /*
     * All the magic goes here
     */
    private void start() {
        try {
            if(commandLine.hasOption(Parameter.HELP.getShortName())){
                help.printHelp(options);
                System.exit(0);
            }

            if(!commandLine.hasOption(Parameter.SOURCE.getShortName())){
                help.printUsage(options);
                System.exit(0);
            }

            String templateFileName = commandLine.hasOption(Parameter.TEMPLATE.getShortName()) ? commandLine.getOptionValue(Parameter.TEMPLATE.getShortName()) : "template.json";

            WriteService writeService = (WriteService) context.getBean("writeService");
            ConfigurationService configurationService = (ConfigurationService) context.getBean("configurationService");
            configurationService.initialize(templateFileName);
            configurationService.setSource(commandLine.getOptionValue(Parameter.SOURCE.getShortName()));
            writeService.initialize();
            // TODO implement cli option to test/validate template file
            logger.debug("Using configuration template: \n {}", configurationService.currentConfigToString());

            ReadServiceImpl readService = (ReadServiceImpl) context.getBean("readService");
            if(commandLine.hasOption(Parameter.MAX_RECORD_LENGTH.getShortName())){
                readService.setMaxRecordLength(Integer.valueOf(commandLine.getOptionValue(Parameter.MAX_RECORD_LENGTH.getShortName())));
            }
            if(commandLine.hasOption(Parameter.PROCESSOR_THREADS.getShortName())){
                readService.setNumberOfThreads(Integer.valueOf(commandLine.getOptionValue(Parameter.PROCESSOR_THREADS.getShortName())));
            }
            readService.process();

            System.out.println("Press <CTRL>+<C> to stop...");
            writeService.finalizeAndShutdown();

        } catch (Exception ex) {
            logger.error("Exception initializing Configuration Service", ex);
        }

    }

    /**
     * Initialize logger and Spring context
     */
    public void initializeContext() {
        DOMConfigurator.configure(LoggiApp.class.getClassLoader().getResource("log4j.xml"));
        context = new GenericXmlApplicationContext("classpath:sp-loggi.xml");
        help = (HelpService) context.getBean("helpService");

    }

    public void initializeOptions(String[] args) throws ParseException {
        CommandLineParser parser = new GnuParser();

        this.options = new Options();

        for (Parameter param : Parameter.values()) {
            OptionBuilder option = OptionBuilder
                    .withLongOpt(param.getLongName())
                    .withDescription(param.getDescription())
                    .isRequired(param.isRequired());
            if (param.isHasArgument()) {
                option.hasArg();
                option.withArgName(param.getLongName() + "Arg");
            }
            options.addOption(option.create(param.getShortName()));
        }

        this.commandLine = parser.parse(options, args);
    }




}
