package com.maestronic.gtfs.importer;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = Import.TABLE_NAME)
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

    @Column(name = "detail")
    private String detail;

    @Column(name = "last_state")
    private String lastState;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    public Import() {
    }

    public Import(String taskName, String fileName, String fileType, String detail, String lastState, String status, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime releaseDate) {
        this.taskName = taskName;
        this.fileName = fileName;
        this.fileType = fileType;
        this.detail = detail;
        this.lastState = lastState;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.releaseDate = releaseDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setLastState(String lastState) {
        this.lastState = lastState;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getLastState() {
        return lastState;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}