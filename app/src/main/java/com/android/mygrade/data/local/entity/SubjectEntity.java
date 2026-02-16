package com.android.mygrade.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.android.mygrade.domain.model.Subject;

@Entity(tableName = "subjects")
public class SubjectEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String sheetUrl;
    public String column;
    public int row;
    public String sheetName;
    public String currentValue;
    public int maxValue;

    public SubjectEntity() {
    }
    public SubjectEntity(int id, String name, String sheetUrl, String column, int row, String sheetName, String currentValue, int maxValue) {
        this.id = id;
        this.name = name;
        this.sheetUrl = sheetUrl;
        this.column = column;
        this.row = row;
        this.sheetName = sheetName;
        this.currentValue = currentValue;
        this.maxValue = maxValue;
    }

    public Subject toDomainModel() {
        return new Subject(id, name, sheetUrl, column, row, sheetName, currentValue, maxValue);
    }

    public static SubjectEntity fromDomainModel(Subject subject) {
        return new SubjectEntity(subject.getId(),
                subject.getName(),
                subject.getSheetUrl(),
                subject.getColumn(),
                subject.getRow(),
                subject.getSheetName(),
                subject.getCurrentValue(),
                subject.getMaxValue());
    }
}
