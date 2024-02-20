package com.eschool.service;

import com.eschool.beans.EventRegistration;
import com.eschool.beans.User;
import com.eschool.repo.UserRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class PDFService {
	
    public byte[] generatePdfFile(List<User> users) throws IOException {
    	 Document document = new Document(PageSize.A4.rotate());
    	 document.setMargins(-20, -20, 10, 10);
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
         PdfWriter.getInstance(document, baos);
         document.open();
         String headers[]= {"Sr. No","Name","Email","Mobile Number","Country","State","City","DOB","Status"};
         PdfPTable table = new PdfPTable(headers.length);
         // Add headers
         for (String header : headers) {
             PdfPCell cell = new PdfPCell(new Phrase(header));
             table.addCell(cell);
         }
         // Add data
         int i=1;
         for (User user : users) {             
        	 PdfPCell cellSrNo = new PdfPCell(new Phrase(""+i));
             table.addCell(cellSrNo);
             PdfPCell cellName = new PdfPCell(new Phrase(user.getName()));
             table.addCell(cellName);
             PdfPCell cellEmail = new PdfPCell(new Phrase(user.getEmail()));
             table.addCell(cellEmail);
             PdfPCell cellMobileNo = new PdfPCell(new Phrase(user.getMobileNumber()));
             table.addCell(cellMobileNo);
             PdfPCell cellCountry = new PdfPCell(new Phrase(user.getCountry().split(":")[1]));
             table.addCell(cellCountry);
             PdfPCell cellState = new PdfPCell(new Phrase(user.getState().split(":")[1]));
             table.addCell(cellState);
             PdfPCell cellCity = new PdfPCell(new Phrase(user.getCity().split(":")[1]));
             table.addCell(cellCity);
             PdfPCell cellDOB = new PdfPCell(new Phrase(user.getDob()));
             table.addCell(cellDOB);
             PdfPCell cellStatus = new PdfPCell(new Phrase(user.getStatus()));
             table.addCell(cellStatus);             
             i++;
         }
         document.add(table);
         document.close();
        }
        catch(Exception ex)
        {
        	System.out.println(ex.getMessage());
        }
        return baos.toByteArray();
    }
    
    public byte[] generatePDFFileForEventRegistrations(List<EventRegistration> eventRegistrations,UserRepository uRepo) throws IOException {
   	 Document document = new Document(PageSize.A4.rotate());
   	 document.setMargins(-20, -20, 10, 10);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
       try {
        PdfWriter.getInstance(document, baos);
        document.open();
        String headers[]= {"Sr. No","Name","Arrivale Mode","Train No.","Arrivale Date","Arrivale Time","Departure Mode","Train No.","Departure Date","Departure Time","Mobile No","Email","Family Id","City","State","Country","Attending Shivir"};
        PdfPTable table = new PdfPTable(headers.length);
        // Add headers
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            table.addCell(cell);
        }
        // Add data
        int i=1;
        for (EventRegistration eventRegistration:eventRegistrations) {             
       	    User user=uRepo.findById(eventRegistration.getUserId());
        	PdfPCell cellSrNo = new PdfPCell(new Phrase(""+i));
            table.addCell(cellSrNo);
            PdfPCell cellName = new PdfPCell(new Phrase(eventRegistration.getUserName()));
            table.addCell(cellName);
            PdfPCell cellArrivalMode = new PdfPCell(new Phrase(eventRegistration.getArrivalModeOfTransport()));
            table.addCell(cellArrivalMode);
            PdfPCell cellATrainNumber = new PdfPCell(new Phrase(eventRegistration.getArrivalTrainNumber()));
            table.addCell(cellATrainNumber);
            PdfPCell cellArrivalDate = new PdfPCell(new Phrase(eventRegistration.getArrivalDate()));
            table.addCell(cellArrivalDate);
            PdfPCell cellArrivalTime = new PdfPCell(new Phrase(eventRegistration.getArrivalTime()));
            table.addCell(cellArrivalTime);
            PdfPCell cellDepartureMode = new PdfPCell(new Phrase(eventRegistration.getDepartureModeOfTransport()));
            table.addCell(cellDepartureMode);
            PdfPCell cellDTrainNumber = new PdfPCell(new Phrase(eventRegistration.getDepartureTrainNumber()));
            table.addCell(cellDTrainNumber);
            PdfPCell cellDepartureDate = new PdfPCell(new Phrase(eventRegistration.getDepartureDate()));
            table.addCell(cellDepartureDate);
            PdfPCell cellDepartureTime = new PdfPCell(new Phrase(eventRegistration.getDepartureTime()));
            table.addCell(cellDepartureTime);            
            PdfPCell cellMobileNo = new PdfPCell(new Phrase(user.getMobileNumber()));
            table.addCell(cellMobileNo);
            PdfPCell cellEmail = new PdfPCell(new Phrase(user.getEmail()));
            table.addCell(cellEmail);
            PdfPCell cellFamilyId = new PdfPCell(new Phrase(eventRegistration.getFamilyId()));
            table.addCell(cellFamilyId);
            PdfPCell cellCity = new PdfPCell(new Phrase(user.getCity().split(":")[1]));
            table.addCell(cellCity);
            PdfPCell cellState = new PdfPCell(new Phrase(user.getState().split(":")[1]));
            table.addCell(cellState);
            PdfPCell cellCountry = new PdfPCell(new Phrase(user.getCountry().split(":")[1]));
            table.addCell(cellCountry);
            PdfPCell cellAttendingShivir = new PdfPCell(new Phrase(""+eventRegistration.getAttendingShivir()));
            table.addCell(cellAttendingShivir);     
 
            i++;
        }
        document.add(table);
        document.close();
       }
       catch(Exception ex)
       {
       	System.out.println(ex.getMessage());
       }
       return baos.toByteArray();
   }

    
}
