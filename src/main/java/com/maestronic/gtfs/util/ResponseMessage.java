package com.maestronic.gtfs.util;

import com.maestronic.gtfs.dto.siri.ErrorCondition;
import com.maestronic.gtfs.dto.siri.ExceptionError;

import java.util.HashMap;
import java.util.Map;

public class ResponseMessage {

    public static Map<String, Object> exceptionErrorJson(int status, Object message) {
        return new HashMap<String, Object>() {{
            put("status", status);
            put("message", message);
        }};
    }

    public static Map<String, Object> retrieveDataJson(int status, String message, Object response) {
        return new HashMap<String, Object>() {{
            put("status", status);
            put("message", message);
            put("data", response);
        }};
    }

    public static ErrorCondition missingRequestParameterError(int status, String message) {
        ExceptionError missingRequestParameterError = new ExceptionError(message, status);
        ErrorCondition errorCondition = new ErrorCondition();
        errorCondition.setMissingRequestParameterError(missingRequestParameterError);
        return errorCondition;
    }

    public static ErrorCondition illegalArgumentException(int status, String message) {
        ExceptionError illegalArgumentError = new ExceptionError(message, status);
        ErrorCondition errorCondition = new ErrorCondition();
        errorCondition.setIllegalArgumentError(illegalArgumentError);
        return errorCondition;
    }

    public static ErrorCondition noInfoForTopicError(int status, String message) {
        ExceptionError noInfoForTopicError = new ExceptionError(message, status);
        ErrorCondition errorCondition = new ErrorCondition();
        errorCondition.setNoInfoForTopicError(noInfoForTopicError);
        return errorCondition;
    }

    public static ErrorCondition otherError(int status, String message) {
        ExceptionError otherError = new ExceptionError(message, status);
        ErrorCondition errorCondition = new ErrorCondition();
        errorCondition.setOtherError(otherError);
        return errorCondition;
    }
}
