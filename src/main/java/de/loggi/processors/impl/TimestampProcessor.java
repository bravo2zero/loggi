package de.loggi.processors.impl;

import de.loggi.exceptions.ConfigurationException;
import de.loggi.model.Column;
import de.loggi.processors.AbstractColumnProcessor;
import de.loggi.processors.MetaInfo;
import de.loggi.service.writers.H2WriteServiceImpl;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author CptSpaetzle
 */
@MetaInfo(
        description = "Returns a string representing H2 datetime format from Unix timestamp",
        attributes = {}
)
public class TimestampProcessor extends AbstractColumnProcessor {

    public TimestampProcessor(Column column) throws ConfigurationException {
        super(column);
    }

    @Override
    public String getProcessedValue(String record) {
        SimpleDateFormat sdf = new SimpleDateFormat(H2WriteServiceImpl.H2_DATETIME);
        return sdf.format(new Date(Long.valueOf(record)));
    }
}
