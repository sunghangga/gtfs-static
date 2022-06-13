package com.maestronic.gtfs.dto.siri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class OnwardCalls implements Serializable {

    @XmlElement(name = "OnwardCall", required = true)
    private List<OnwardCall> onwardCalls;

    public List<OnwardCall> getOnwardCalls() {
        if (onwardCalls == null) {
            onwardCalls = new ArrayList<OnwardCall>();
        }
        return this.onwardCalls;
    }
}
