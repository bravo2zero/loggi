package de.dmc.loggi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CptSpaetzle
 */
public class Column {
    String name;
    String defaultValue;
    String dataType = "varchar(255)";
    String dataFormat = "";
    String processorName;
    List<Attribute> attributes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getProcessorName() {
        return processorName;
    }

    public void setProcessorName(String processorName) {
        this.processorName = processorName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    public List<Attribute> getAttributes() {
        if(attributes == null)
        {
            attributes = new ArrayList<>();
        }
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

}
