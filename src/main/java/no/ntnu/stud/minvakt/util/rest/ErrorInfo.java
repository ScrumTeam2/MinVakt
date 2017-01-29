package no.ntnu.stud.minvakt.util.rest;

import no.ntnu.stud.minvakt.util.SanitizeUtil;

/**
 * Plain old Java object used for sending error objects to the client
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
