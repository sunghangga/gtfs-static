package com.maestronic.gtfs.util;

public class GlobalHelper implements GlobalVariable {

    public static String directionName(int directionId) {
        return directionId == 0 ? OUTBOUND : INBOUND;
    }
}
