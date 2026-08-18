package com.edgareldy.jsftutorial.exception;

/**
 * Thrown by a service when an operation violates a business rule rather than
 * a technical constraint (e.g. deleting a category that still has products).
 * Caught by the bean that triggered the operation and reported via
 * {@code FacesMessageUtil.addError}, never left to reach
 * {@code CustomExceptionHandlerFactory}'s generic error path.
 * <p>
 * Created by Edgar Muhamyangabo on 8/18/26
 * Author : Edgar Muhamyangabo
 * Date : 8/18/26
 * Project : jsf_tutorial
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
