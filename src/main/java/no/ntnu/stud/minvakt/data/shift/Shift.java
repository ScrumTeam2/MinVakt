package no.ntnu.stud.minvakt.data.shift;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Data structure for the shift entity
 */
public class Shift {
    private int id;
    private int staffNumb;
    private Date date;
    private ShiftType type;
    private ArrayList<ShiftUser> shiftUsers;
    private int deptId;
    private boolean approved;


    public enum ShiftType {
        DAY, EVENING, NIGHT;

        public int getValue() {
            return super.ordinal();
        }

        public static ShiftType valueOf(int shiftTypeNr) {
            for (ShiftType type : ShiftType.values()) {
                if (type.ordinal() == shiftTypeNr) {
                    return type;
                }
            }
            return null;
        }
        @Override
        public String toString() {
            String constName = super.toString();
            return constName.substring(0, 1) + constName.substring(1).toLowerCase();
        }

        public int getStartHour() {
            return (super.ordinal() + 1) * 8 - 1;
        }
    }
    public Shift(){

    }

    public Shift(int id, int staffNumb, Date date, ShiftType type, int deptId, ArrayList<ShiftUser> shiftUsers, boolean approved) {
        this.id = id;
        this.date = date;
        this.staffNumb = staffNumb;
        this.type = type;
        this.shiftUsers = shiftUsers;
        this.deptId = deptId;
        this.approved = approved;
    }

    public Shift(int id, int staffNumb, Date date, int type, int deptId, ArrayList<ShiftUser> shiftUsers, boolean approved) {
        this(id, staffNumb, date, ShiftType.valueOf(type), deptId, shiftUsers, approved);
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public ShiftType getType(){
        return type;
    }


    public int getDeptId() {
        return deptId;
    }

    public int getStaffNumb(){
        return staffNumb;
    }

    public ArrayList<ShiftUser> getShiftUsers(){
        return shiftUsers;
    }

    public void setStaffNumb(int staffNumb){
        this.staffNumb = staffNumb;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setType(ShiftType type) {
        this.type = type;
    }

    public void setShiftUsers(ArrayList<ShiftUser> shiftUsers){
        this.shiftUsers = shiftUsers;
    }


    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public LocalDateTime getStartTime() {
        return date.toLocalDate().atStartOfDay().plusHours(type.getStartHour());
    }

    @Override
    public String toString() {
        return "Shift{" +
                "id=" + id +
                ", staffNumb=" + staffNumb +
                ", date=" + date +
                ", type=" + type +
                ", shiftUsers=" + shiftUsers +
                ", deptId=" + deptId +
                ", approved=" + approved +
                '}';
    }
}
