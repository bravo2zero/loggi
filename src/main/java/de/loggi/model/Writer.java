package de.loggi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CptSpaetzle
 */
public class Writer {
    String writerClass;
    List<Attribute> attributes;

    public String getWriterClass() {
        return writerClass;
    }

    public void setWriterClass(String writerClass) {
        this.writerClass = writerClass;
    }

    public List<Attribute> getAttributes() {
        if(attributes == null){
            attributes = new ArrayList<>();
        }
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }
}
