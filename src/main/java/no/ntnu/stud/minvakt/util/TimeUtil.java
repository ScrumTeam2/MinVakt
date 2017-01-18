package no.ntnu.stud.minvakt.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by Audun on 17.01.2017.
 */
public class TimeUtil {
    public static LocalDate convertJavaDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
