package com.maestronic.gtfs.validation;

import com.maestronic.gtfs.enumerate.SortTypeEnum;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class PageValidation {

    @NotNull
    @Min(1)
    private int pageNo;
    @NotNull
    @Min(1)
    private int pageSize;
    @NotNull
    private SortTypeEnum sortType;
}
