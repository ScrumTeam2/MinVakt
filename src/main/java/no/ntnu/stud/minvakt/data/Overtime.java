package no.ntnu.stud.minvakt.data;

import no.ntnu.stud.minvakt.data.shift.Shift;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.sql.Date;

/**
 * Represents the overtime entry. Has information about one overtime entry
 */
@XmlRootElement
public class Overtime {
    @XmlElement
    private int userId;
    @XmlElement
    private int shiftId;
    @XmlElement
    private int startTime;
    @XmlElement
    private int minutes;
    @XmlElement
    private boolean approved;
    @XmlElement
    private java.sql.Date date;
    @XmlElement
    private Shift.ShiftType type;

    public Overtime(){}

    public Overtime(int userId, int shiftId, int startTime, int minutes, boolean approved, java.sql.Date date, int type) {
        this.userId = userId;
        this.shiftId = shiftId;
        this.startTime = startTime;
        this.minutes = minutes;
        this.approved = approved;
        this.date = date;
        this.type = Shift.ShiftType.valueOf(type);
    }

    public Overtime(int userId, int shiftId, int startTime, int minutes, boolean approved) {
        this.userId = userId;
        this.shiftId = shiftId;
        this.startTime = startTime;
        this.minutes = minutes;
        this.approved = approved;
    }
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isApproved() {
        return approved;
    }

    public int getUserId(){
       return userId;
    }

    public int getShiftId(){
       return shiftId;
    }

    public int getStartTime(){
        return startTime;
    }

    public int getMinutes(){
        return minutes;
    }

    public void setUserId(int userId){
        this.userId = userId;
    }

    public void setShiftId(int shiftId) {
        this.shiftId = shiftId;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }


    @Override
    public String toString() {
        return "Overtime{" +
                "userId=" + userId +
                ", shiftId=" + shiftId +
                ", startTime=" + startTime +
                ", minutes=" + minutes +
                ", approved=" + approved +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Overtime overtime = (Overtime) o;

        if (userId != overtime.userId) return false;
        if (shiftId != overtime.shiftId) return false;
        if (startTime != overtime.startTime) return false;
        if (minutes != overtime.minutes) return false;
        return approved == overtime.approved;
    }
}
