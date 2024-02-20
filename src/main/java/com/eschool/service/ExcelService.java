package com.eschool.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eschool.beans.EventRegistration;
import com.eschool.beans.User;
import com.eschool.repo.UserRepository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelService {
	
	
	//Generate excel file for all users
    public void generateExcelFile(List<User> users, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("All Users");
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
            row.createCell(4).setCellValue(user.getCountry().split(":")[1]);
            row.createCell(5).setCellValue(user.getState().split(":")[1]);
            row.createCell(6).setCellValue(user.getCity().split(":")[1]);
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
  //Generate excel file for all users
    public void generateExcelFileForEventRegistrations(List<EventRegistration> eventRegistrations, String filePath,UserRepository uRepo) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Event Registrations");
        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Sr. No");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Arrival Mode");
        headerRow.createCell(3).setCellValue("Train No.");
        headerRow.createCell(4).setCellValue("Arrival Date");
        headerRow.createCell(5).setCellValue("Arrival Time");
        headerRow.createCell(6).setCellValue("Departure Mode");
        headerRow.createCell(7).setCellValue("Train No.");
        headerRow.createCell(8).setCellValue("Departure Date");
        headerRow.createCell(9).setCellValue("Departure Time");        
        headerRow.createCell(10).setCellValue("Mobile No.");
        headerRow.createCell(11).setCellValue("Email");
        headerRow.createCell(12).setCellValue("Family Id");
        headerRow.createCell(13).setCellValue("City");
        headerRow.createCell(14).setCellValue("State");
        headerRow.createCell(15).setCellValue("Country");
        headerRow.createCell(16).setCellValue("Attending Shivir");
        // Populate data
        int rowNum = 1;
        for (EventRegistration eventRegistration : eventRegistrations) {
            User user=uRepo.findById(eventRegistration.getUserId());
        	Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum-1);
            row.createCell(1).setCellValue(eventRegistration.getUserName());
            row.createCell(2).setCellValue(eventRegistration.getArrivalModeOfTransport());
            row.createCell(3).setCellValue(eventRegistration.getArrivalTrainNumber());
            row.createCell(4).setCellValue(eventRegistration.getArrivalDate());
            row.createCell(5).setCellValue(eventRegistration.getArrivalTime());
            row.createCell(6).setCellValue(eventRegistration.getDepartureModeOfTransport());
            row.createCell(7).setCellValue(eventRegistration.getDepartureTrainNumber());
            row.createCell(8).setCellValue(eventRegistration.getDepartureDate());
            row.createCell(9).setCellValue(eventRegistration.getDepartureTime());
            row.createCell(10).setCellValue(user.getMobileNumber());
            row.createCell(11).setCellValue(user.getEmail());
            row.createCell(12).setCellValue(eventRegistration.getFamilyId());
            row.createCell(13).setCellValue(eventRegistration.getFromCity().split(":")[1]);
            row.createCell(14).setCellValue(eventRegistration.getFromState().split(":")[1]);
            row.createCell(15).setCellValue(eventRegistration.getFromCountry().split(":")[1]);           
            row.createCell(16).setCellValue(eventRegistration.getAttendingShivir());             
        }
        // Write the workbook to a file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        // Close the workbook to release resources
        workbook.close();
    }
    
}