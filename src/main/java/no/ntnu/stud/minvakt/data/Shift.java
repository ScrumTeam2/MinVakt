package no.ntnu.stud.minvakt.data;

import java.sql.Date;

/**
 * Created by evend on 1/10/2017.
 */
public class Shift {
    private int id;
    private Date date;
    private ShiftType type;
    private boolean responsibility;
    private boolean validAbsence;
    private int userId;
    private int deptId;


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

    }

    public Shift(int id, Date date, int type, boolean responsibility,
                 boolean validAbsence, int userId, int deptId) {
        this.id = id;
        this.date = date;
        this.type = ShiftType.valueOf(type);
        this.responsibility = responsibility;
        this.validAbsence = validAbsence;
        this.userId = userId;
        this.deptId = deptId;
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

    public boolean isResponsible() {
        return responsibility;
    }

    public boolean isValidAbsence() {
        return validAbsence;
    }

    public int getUserId() {
        return userId;
    }

    public int getDeptId() {
        return deptId;
    }
    public void setId(int id){
        this.id = id;
    }





}
