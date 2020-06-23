package com.projectm.common;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;

public class ExcelUtils {

    private static final String EXCEL_XLS = ".xls";
    private static final String EXCEL_XLSX = ".xlsx";

    /**
     *判断excel的版本，并根据文件流数据获取workbook
     * @throws Exception
     *
     */
    public Workbook getWorkBook(InputStream is, String fileName) throws Exception{

        Workbook workbook = null;
        if(fileName.endsWith(EXCEL_XLS)){
            workbook = new HSSFWorkbook(is);
        }else if(fileName.endsWith(EXCEL_XLSX)){
            workbook = new XSSFWorkbook(is);
        }
        return workbook;
    }
}
