package no.ntnu.stud.minvakt.data.shift;

import java.sql.Date;

/**
 * Created by evend on 1/26/2017.
 */
public class CalendarData {
    private Date date;
    private boolean available;
    private boolean ownShift;

    public CalendarData(Date date, boolean available, boolean ownShift) {
        this.date = date;
        this.available = available;
        this.ownShift = ownShift;
    }
    public CalendarData(){}

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isOwnShift() {
        return ownShift;
    }

    public void setOwnShift(boolean ownShift) {
        this.ownShift = ownShift;
    }
}
