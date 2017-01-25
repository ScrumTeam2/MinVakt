package no.ntnu.stud.minvakt.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.sql.Date;

/**
 * Created by AnitaKristineAune on 13.01.2017.
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

    public Overtime(){}

   public Overtime(int userId, int shiftId, int startTime, int minutes, boolean approved) {
       this.userId = userId;
       this.shiftId = shiftId;
       this.startTime = startTime;
       this.minutes = minutes;
       this.approved = approved;
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

    public boolean getApproved(){
        return approved;
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

    public void setApproved(boolean approved) {
        this.approved = approved;
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
