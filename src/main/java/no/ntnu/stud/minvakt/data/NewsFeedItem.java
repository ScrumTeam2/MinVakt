package no.ntnu.stud.minvakt.data;

import java.sql.Date;
import java.sql.Timestamp;

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

    public NewsFeedItem(int feedId, Timestamp dateTime, String content, int userIdTo, int userIdInvolving, int shiftId) {
        this.feedId = feedId;
        this.dateTime = dateTime;
        this.content = content;
        this.userIdTo = userIdTo;
        this.userIdInvolving = userIdInvolving;
        this.shiftId = shiftId;
    }

    public int getFeedItemId() {
        return feedId;
    }

    public void setFeedItemId(int feedId) {
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
        this.content = content;
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