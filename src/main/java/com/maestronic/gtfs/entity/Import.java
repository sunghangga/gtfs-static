package com.maestronic.gtfs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = Import.TABLE_NAME)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Import {

    public static final String TABLE_NAME= "import";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "task_name")
    private String taskName;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    @OneToMany(
            mappedBy = "importId",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    private List<ImportDetail> importDetail = new ArrayList<>();

    public Import(String taskName, String fileName, String fileType, String status, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime releaseDate) {
        this.taskName = taskName;
        this.fileName = fileName;
        this.fileType = fileType;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.releaseDate = releaseDate;
    }
}