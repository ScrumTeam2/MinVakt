package no.ntnu.stud.minvakt.util;

import org.junit.Test;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Created by evend on 1/24/2017.
 */
public class FormattingUtilTest {
    @Test
    public void testDateFormat(){
        String expected = "tirsdag 24. januar";
        Date date = Date.valueOf("2017-01-24");
        assertEquals(expected,FormattingUtil.formatDate(date));
    }
}
