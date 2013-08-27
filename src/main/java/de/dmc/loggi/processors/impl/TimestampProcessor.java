package de.dmc.loggi.processors.impl;

import de.dmc.loggi.exceptions.ConfigurationException;
import de.dmc.loggi.model.Column;
import de.dmc.loggi.processors.AbstractColumnProcessor;
import de.dmc.loggi.processors.MetaInfo;
import de.dmc.loggi.service.impl.H2WriteServiceImpl;

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
