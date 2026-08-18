package com.edgareldy.jsftutorial.exception;

import com.edgareldy.jsftutorial.util.FacesMessageUtil;

import javax.faces.FacesException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Registers {@link CustomExceptionHandler} as the JSF exception handler for
 * this application (see faces-config.xml), so unhandled exceptions never
 * reach the browser as a raw stack trace.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
public class CustomExceptionHandlerFactory extends ExceptionHandlerFactory {

    public CustomExceptionHandlerFactory(ExceptionHandlerFactory parent) {
        super(parent);
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return new CustomExceptionHandler(getWrapped().getExceptionHandler());
    }

    /**
     * Logs every unhandled exception queued during the current request and
     * reports a generic error message via {@link FacesMessageUtil} instead of
     * letting it surface as a raw stack trace.
     * <p>
     * Created by Edgar Muhamyangabo on 8/17/26
     * Author : Edgar Muhamyangabo
     * Date : 8/17/26
     * Project : jsf_tutorial
     */
    private static class CustomExceptionHandler extends ExceptionHandlerWrapper {

        private static final Logger LOGGER = Logger.getLogger(CustomExceptionHandler.class.getName());

        private final ExceptionHandler wrapped;

        CustomExceptionHandler(ExceptionHandler wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public ExceptionHandler getWrapped() {
            return wrapped;
        }

        @Override
        public void handle() throws FacesException {
            Iterator<ExceptionQueuedEvent> events = getUnhandledExceptionQueuedEvents().iterator();
            while (events.hasNext()) {
                ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) events.next().getSource();
                Throwable exception = context.getException();
                LOGGER.log(Level.SEVERE, "Unhandled exception during JSF request processing", exception);
                FacesMessageUtil.addError("An unexpected error occurred. Please try again.");
                events.remove();
            }
            getWrapped().handle();
        }
    }
}
