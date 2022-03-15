package com.maestronic.gtfs.bean;

import com.maestronic.gtfs.service.ImportTransactionService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PreDestroy;

public class TerminateBean {

    @Autowired
    private ImportTransactionService importTransactionService;

    @PreDestroy
    public void onDestroy() throws Exception {
        importTransactionService.killRunProcByStatus();
    }
}
