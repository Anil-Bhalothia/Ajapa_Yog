package com.eschool.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.eschool.beans.User;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelService {

    public void generateExcelFile(List<User> users, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employee Data");

        // Create header row
        Row headerRow = sheet.createRow(0);

        headerRow.createCell(0).setCellValue("Sr. No");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Email");
        headerRow.createCell(3).setCellValue("Mobile Number");
        headerRow.createCell(4).setCellValue("Country");
        headerRow.createCell(5).setCellValue("State");
        headerRow.createCell(6).setCellValue("City");
        headerRow.createCell(7).setCellValue("DOB");
        headerRow.createCell(8).setCellValue("Status");    

        // Populate data
        int rowNum = 1;
        for (User user : users) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum-1);
            row.createCell(1).setCellValue(user.getName());
            row.createCell(2).setCellValue(user.getEmail());
            row.createCell(3).setCellValue(user.getMobileNumber());
            row.createCell(4).setCellValue(user.getCountry());
            row.createCell(5).setCellValue(user.getState());
            row.createCell(6).setCellValue(user.getCity());
            row.createCell(7).setCellValue(user.getDob());
            row.createCell(8).setCellValue(user.getStatus()); 
        }

        // Write the workbook to a file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }

        // Close the workbook to release resources
        workbook.close();
    }
}