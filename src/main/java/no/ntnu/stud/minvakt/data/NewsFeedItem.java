package no.ntnu.stud.minvakt.data;

import no.ntnu.stud.minvakt.util.SanitizeUtil;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.logging.Level;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 * Created by evend on 1/20/2017.
 */
public class NewsFeedItem {
    private int feedId;
    private Timestamp dateTime;
    private String content;
    private int userIdTo;
    private int userIdInvolving;
    private int shiftId;
    private NewsFeedCategory category;

    //Default value -1, if
    private int startTimeTimebank;

    public enum NewsFeedCategory {
        SHIFT_CHANGE_EMPLOYEE, SHIFT_CHANGE_ADMIN, VALID_ABSENCE, TIMEBANK, NOTIFICATION;



        public int getValue() {
            return super.ordinal();
        }

        public static NewsFeedCategory valueOf(int newsFeedNr) {
            for (NewsFeedCategory type : NewsFeedCategory.values()) {
                if (type.ordinal() == newsFeedNr) {
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
    public NewsFeedItem(int feedId, Timestamp dateTime, String content, int userIdTo, int userIdInvolving, int shiftId,
                        NewsFeedCategory category) {
        this.feedId = feedId;
        this.dateTime = dateTime;
        this.content = content;
        this.userIdTo = userIdTo;
        this.userIdInvolving = userIdInvolving;
        this.shiftId = shiftId;
        this.category = category;
        this.startTimeTimebank = -1;
    }
    public NewsFeedItem(int feedId, Timestamp dateTime, String content, int userIdTo, int userIdInvolving, int shiftId,
                        NewsFeedCategory category, int startTimeTimebank) {
        this.feedId = feedId;
        this.dateTime = dateTime;
        this.content = content;
        this.userIdTo = userIdTo;
        this.userIdInvolving = userIdInvolving;
        this.shiftId = shiftId;
        this.category = category;
        this.startTimeTimebank = startTimeTimebank;
    }

    public NewsFeedItem(){}

    public int getStartTimeTimebank() {
        return startTimeTimebank;
    }

    public void setStartTimeTimebank(int startTimeTimebank) {
        this.startTimeTimebank = startTimeTimebank;
    }

    public NewsFeedCategory getCategory() {
        return category;
    }

    public void setCategory(NewsFeedCategory category) {
        this.category = category;
    }

    public int getFeedId() {
        return feedId;
    }

    public void setFeedId(int feedId) {
        this.feedId = feedId;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = SanitizeUtil.filterInput(content);
    }

    public int getUserIdTo() {
        return userIdTo;
    }

    public void setUserIdTo(int userIdTo) {
        this.userIdTo = userIdTo;
    }

    public int getUserIdInvolving() {
        return userIdInvolving;
    }

    public void setUserIdInvolving(int userIdInvolving) {
        this.userIdInvolving = userIdInvolving;
    }

    public int getShiftId() {
        return shiftId;
    }

    public void setShiftId(int shiftId) {
        this.shiftId = shiftId;
    }
}
