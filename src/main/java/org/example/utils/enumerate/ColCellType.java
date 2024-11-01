package org.example.utils.enumerate;


public enum ColCellType {
    _STRING("STRING"),
    _DATE("DATE"),
    _DOLLARS("DOLLARS"), _DOUBLE("DOUBLE"),
    _INTEGER("INTEGER"),
    _FORMULA("_FORMULA");

    ColCellType(String label) {
        this.label = label;
    }

    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
