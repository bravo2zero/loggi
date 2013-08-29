package de.loggi.model;

/**
 * @author CptSpaetzle
 */
public class Template {

    String dbMode = "memory";
    String recordSeparator = "";
    Column[] columns;

    public String getDbMode() {
        return dbMode;
    }

    public void setDbMode(String dbMode) {
        this.dbMode = dbMode;
    }

    public Column[] getColumns() {
        return columns;
    }

    public void setColumns(Column[] columns) {
        this.columns = columns;
    }

    public String getRecordSeparator() {
        return recordSeparator;
    }

    public void setRecordSeparator(String recordSeparator) {
        this.recordSeparator = recordSeparator;
    }


}
