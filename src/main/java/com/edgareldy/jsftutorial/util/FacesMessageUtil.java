package com.edgareldy.jsftutorial.util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * Single entry point every managed bean uses to report success/failure to the
 * page, so the user-feedback convention can be changed in one place.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
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
