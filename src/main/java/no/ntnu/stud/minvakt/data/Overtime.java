package no.ntnu.stud.minvakt.data;

import java.sql.Date;

/**
 * Created by AnitaKristineAune on 13.01.2017.
 */
public class Overtime {

    private Date date;
    private int startTime;
    private int endTime;

    public Overtime(Date date, int startTime, int endTime){
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public Date getDate(){
        return date;
    }
    public int getStartTime(){
        return startTime;
    }
    public int getEndTime(){
        return endTime;
    }

    public String toString(){
        String res = "";
        res += "Date: " + date;
        res+= " Start time: " +startTime;
        res+= " End time: " + endTime;
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Overtime overtime = (Overtime) o;

        if (startTime != overtime.startTime) return false;
        if (endTime != overtime.endTime) return false;

        return date != null ? date.equals(overtime.date) : overtime.date == null;
    }
}
