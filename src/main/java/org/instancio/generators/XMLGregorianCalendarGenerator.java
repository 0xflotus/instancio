package org.instancio.generators;

import org.instancio.Generator;
import org.instancio.internal.random.RandomProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class XMLGregorianCalendarGenerator extends AbstractRandomGenerator<XMLGregorianCalendar> {
    private static final Logger LOG = LoggerFactory.getLogger(XMLGregorianCalendarGenerator.class);

    private final Generator<LocalDateTime> localDateTimeGenerator = new LocalDateTimeGenerator(random());

    public XMLGregorianCalendarGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public XMLGregorianCalendar generate() {
        LocalDateTime localDateTime = localDateTimeGenerator.generate();
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        } catch (DatatypeConfigurationException ex) {
            LOG.debug("Error generating XMLGregorianCalendar; returning a null", ex);
            return null;
        }
    }
}