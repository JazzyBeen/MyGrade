package com.android.mygrade.domain.model;

public class Subject {
    private int id;
    private String name;
    private String sheetUrl;
    private String column;
    private int row;
    private String sheetName;
    private String currentValue;
    private int maxValue;

    public Subject(String name, String sheetUrl, String column, int row, String sheetName, int maxValue) {
        this.name = name;
        this.sheetUrl = sheetUrl;
        this.column = column;
        this.row = row;
        this.sheetName = sheetName;
        this.maxValue = maxValue;
        this.currentValue = "...";
    }

    public Subject(int id, String name, String sheetUrl, String column, int row, String sheetName, String currentValue, int maxValue) {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSheetUrl() {
        return sheetUrl;
    }

    public String getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public String getSheetName() {
        return sheetName;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSheetUrl(String sheetUrl) {
        this.sheetUrl = sheetUrl;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getProgressPercent() {
        try {
            double progress = Double.parseDouble(this.currentValue.replaceAll(",", ".")) / this.maxValue * 100;
            return (int) Math.round(progress);
        }
        catch (NumberFormatException e){
            return 0;
        }

    }
}