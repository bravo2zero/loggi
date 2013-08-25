package de.dmc.loggi.model;

/**
 * Prototyping class to configure CLI parameters without boilerplate code.
 * @author CptSpaetzle
 */
 public enum Parameter {
    TEMPLATE("t", "template", "path to template.json file", true, false),
    SOURCE("s", "source", "Source log file", true, false),
    HELP("h", "help", "Display help and usage info", false, false),
    MAX_RECORD_LENGTH("m","max-length","Maximum length for single record before separator alert goes off",true,false),
    PROCESSOR_THREADS("p","threads","Number of threads for column processors, default is (numberOfCPUs-1)",true,false);
    
    String shortName;
    String longName;
    String description;
    boolean hasArgument;
    boolean required;

    private Parameter(String shortName, String longName, String description, boolean hasArgument, boolean required) {
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;
        this.hasArgument = hasArgument;
        this.required = required;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHasArgument() {
        return hasArgument;
    }

    public void setHasArgument(boolean hasArgument) {
        this.hasArgument = hasArgument;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
    
}
