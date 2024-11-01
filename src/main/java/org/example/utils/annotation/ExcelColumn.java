package org.example.utils.annotation;


import org.apache.poi.ss.usermodel.CellStyle;
import org.example.utils.ExcelUtils;
import org.example.utils.enumerate.ColCellType;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelColumn {
    int col() default 0;

    ColCellType type() default ColCellType._STRING;

    String title() default "";

    int width() default 2000;

    short alignHorizontal() default CellStyle.ALIGN_LEFT;

    short alignVertical() default CellStyle.VERTICAL_CENTER;

    String format() default "";

    String style() default ExcelUtils.LEFT;
}