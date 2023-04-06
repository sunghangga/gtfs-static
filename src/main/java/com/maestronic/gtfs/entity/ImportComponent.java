package com.maestronic.gtfs.entity;

import java.util.List;
import java.util.Map;

public class ImportComponent {

    private int saveCount;
    private List<String> entityList;
    private List<Object> dataList;

    public ImportComponent() {
    }

    public ImportComponent(int saveCount, List<String> entityList, List<Object> dataList) {
        this.saveCount = saveCount;
        this.entityList = entityList;
        this.dataList = dataList;
    }

    public int getSaveCount() {
        return saveCount;
    }

    public void setSaveCount(int saveCount) {
        this.saveCount = saveCount;
    }

    public List<String> getEntityList() {
        return entityList;
    }

    public List<Object> getDataList() {
        return dataList;
    }
}
