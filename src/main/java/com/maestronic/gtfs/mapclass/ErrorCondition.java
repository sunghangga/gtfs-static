package com.maestronic.gtfs.mapclass;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ErrorCondition")
public class ErrorCondition implements Serializable {

    @XmlElement(name = "OtherError")
    private ExceptionError otherError;
    @XmlElement(name = "MissingRequestParameterError")
    private ExceptionError missingRequestParameterError;
    @XmlElement(name = "IllegalArgumentError")
    private ExceptionError illegalArgumentError;
    @XmlElement(name = "NoInfoForTopicError")
    private ExceptionError noInfoForTopicError;

    public ErrorCondition() {
    }

    public void setOtherError(ExceptionError otherError) {
        this.otherError = otherError;
    }

    public void setMissingRequestParameterError(ExceptionError missingRequestParameterError) {
        this.missingRequestParameterError = missingRequestParameterError;
    }

    public void setIllegalArgumentError(ExceptionError illegalArgumentError) {
        this.illegalArgumentError = illegalArgumentError;
    }

    public void setNoInfoForTopicError(ExceptionError noInfoForTopicError) {
        this.noInfoForTopicError = noInfoForTopicError;
    }
}
