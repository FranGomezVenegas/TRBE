/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.thirdparties.sap;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
/**
 *
 * @author User
 */
public class ExcelExporter {

    public static void exportToExcel(String[][] data1, String[][] data2, String filename) {
        try {
            Workbook workbook = new XSSFWorkbook();
            
            // Export data1 to first worksheet
            Sheet sheet1 = workbook.createSheet("Data1");
            for (int i = 0; i < data1.length; i++) {
                Row row = sheet1.createRow(i);
                for (int j = 0; j < data1[i].length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(data1[i][j]);
                }
            }
            
            // Export data2 to second worksheet
            Sheet sheet2 = workbook.createSheet("Data2");
            for (int i = 0; i < data2.length; i++) {
                Row row = sheet2.createRow(i);
                for (int j = 0; j < data2[i].length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(data2[i][j]);
                }
            }
            
            // Save workbook to file
            FileOutputStream fileOut = new FileOutputStream(filename);
            workbook.write(fileOut);
            fileOut.close();
            
            System.out.println("Data exported to " + filename + " successfully.");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}