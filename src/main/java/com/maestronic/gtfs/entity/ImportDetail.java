package com.maestronic.gtfs.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = ImportDetail.TABLE_NAME)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ImportDetail {

    public static final String TABLE_NAME= "import_detail";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "last_state")
    private String lastState;

    @Column(name = "detail")
    private String detail;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "import_id", referencedColumnName="id")
    private Import importId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ImportDetail(String lastState, String detail, Import importId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.lastState = lastState;
        this.detail = detail;
        this.importId = importId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}