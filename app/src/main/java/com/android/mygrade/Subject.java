package com.android.mygrade;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "subjects")
public class Subject {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String sheetUrl;
    public String column;
    public int row;
    public String sheetName;
    public String currentValue;
    public int maxValue;

    public Subject(String name, String sheetUrl, String column, int row, String sheetName, int maxValue) {
        this.name = name;
        this.sheetUrl = sheetUrl;
        this.column = column;
        this.row = row;
        this.sheetName = sheetName;
        this.maxValue = maxValue;
        this.currentValue = "...";
    }
}