package github.com.icezerocat.core.utils;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Description: POI excel 工具类
 * CreateDate:  2020/8/21 9:24
 *
 * @author zero
 * @version 1.0
 */
public class POIExcelUtil {

    /**
     * 克隆sheet
     *
     * @param excelFile     excel文件
     * @param srcSheetName  原sheet名字
     * @param destSheetName 目标sheet名字
     */
    public static void cloneSheet(File excelFile, String srcSheetName, String destSheetName) {
        Workbook sheets = readExcelFromFile(excelFile);
        int index = sheets.getSheetIndex(srcSheetName);
        cloneSheet(excelFile, index, destSheetName);
    }

    /**
     * 克隆sheet
     *
     * @param excelFile     excel文件
     * @param index         需要克隆sheet的下标
     * @param destSheetName 目标sheet名字
     */
    public static void cloneSheet(File excelFile, Integer index, String destSheetName) {
        Workbook sheets = readExcelFromFile(excelFile);
        //克隆一个新的sheet
        Sheet newSheet = sheets.cloneSheet(index);
        int sheetIndex = sheets.getSheetIndex(newSheet);
        sheets.setSheetName(sheetIndex, destSheetName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(excelFile);
            fileOutputStream.flush();
            sheets.write(fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取excel转化为poi可操纵工作簿类型
     *
     * @param file excel文件
     * @return 工作簿
     */
    public static Workbook readExcelFromFile(File file) {
        if (file == null) {
            return null;
        }
        try {
            return new XSSFWorkbook(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException("文件解析失败");
        }
    }
}
