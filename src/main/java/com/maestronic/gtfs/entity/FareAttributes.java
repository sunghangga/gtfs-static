package com.maestronic.gtfs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@Table(name = FareAttributes.TABLE_NAME)
public class FareAttributes {

    public static final String TABLE_NAME= "fare_attributes";

    @Id
    @Column(name = "fare_id")
    private String fareId;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "currency_type")
    private String currencyType;

    @Column(name = "payment_method")
    private int paymentMethod;

    @Column(name = "transfers")
    private Integer transfers;

    @Column(name = "agency_id")
    private String agencyId;

    @Column(name = "transfer_duration")
    private Integer transferDuration;

    public FareAttributes(String fareId, BigDecimal price, String currencyType, int paymentMethod, Integer transfers, String agencyId, Integer transferDuration) {
        this.fareId = fareId;
        this.price = price;
        this.currencyType = currencyType;
        this.paymentMethod = paymentMethod;
        this.transfers = transfers;
        this.agencyId = agencyId;
        this.transferDuration = transferDuration;
    }
}
