package no.ntnu.stud.minvakt.util.rest;

import no.ntnu.stud.minvakt.util.SanitizeUtil;

/**
 * Created by Audun on 11.01.2017.
 */
public class ErrorInfo {
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = SanitizeUtil.filterInput(error);
    }

    public ErrorInfo(String error) {
        this.error = SanitizeUtil.filterInput(error);
    }

    public ErrorInfo() {

    }
}
