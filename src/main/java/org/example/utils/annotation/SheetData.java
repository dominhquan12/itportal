package org.example.utils.annotation;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class SheetData<T> {
    private String sheetName;
    private List<T> data;
    private Class<T> clazz;
    private int ignoreRowHeader;

    public SheetData(int ignoreRowHeader, String sheetName, List<T> data, Class<T> clazz) {
        this.sheetName = sheetName;
        this.data = data;
        this.clazz = clazz;
        this.ignoreRowHeader = ignoreRowHeader;
    }
}