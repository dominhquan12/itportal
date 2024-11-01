package org.example.utils;

import org.apache.naming.factory.BeanFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class CommonUtils {

    public static String pathFolder;
    private static BeanFactory beanFactory;

    public CommonUtils(@Value("${urlUpload}") String pathFolder) {
        CommonUtils.pathFolder = pathFolder;
    }

    public static boolean isNumber(String value) {
        try {
            int number = Integer.parseInt(value);
            if (number > 0) {
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String uploadFileToFolder(Workbook workbook) {
        String path = System.getProperty("user.dir") + pathFolder;
        String fileName = "file_error_" + System.currentTimeMillis() + ".xlsx"; // Tạo đối tượng File để đại diện cho tệp đích
        File targetFile = new File(path, fileName);
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Ghi Workbook vào tệp đích
        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            workbook.write(outputStream);
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isNumber2(String value) {
        try {
            int number = Integer.parseInt(value);
            if (number >= 0) {
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
