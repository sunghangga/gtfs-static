package com.maestronic.gtfs.bean;

import com.maestronic.gtfs.importer.ImportTransaction;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PreDestroy;

public class TerminateBean {

    @Autowired
    private ImportTransaction importTransaction;

    @PreDestroy
    public void onDestroy() throws Exception {
        importTransaction.killRunProcByStatus();
    }
}
