package com.maestronic.gtfs.mapclass;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.math.BigInteger;

@XmlAccessorType(XmlAccessType.FIELD)
public class ExceptionError implements Serializable {

    @XmlElement(name = "ErrorText")
    private String errorText;
    @XmlAttribute(name = "number")
    private int number;

    public ExceptionError(String errorText, int number) {
        this.errorText = errorText;
        this.number = number;
    }
}
