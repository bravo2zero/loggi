package de.loggi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CptSpaetzle
 */
public class Transformer {
    private String type;
    private List<Attribute> attributes;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
