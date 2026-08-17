package com.edgareldy.jsftutorial.util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public final class FacesMessageUtil {

    private FacesMessageUtil() {
    }

    public static void addInfo(String summary) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null));
    }

    public static void addError(String summary) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null));
    }
}
