package com.eschool.service;

import com.eschool.beans.User;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
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
             PdfPCell cellCountry = new PdfPCell(new Phrase(user.getCountry()));
             table.addCell(cellCountry);
             PdfPCell cellState = new PdfPCell(new Phrase(user.getState()));
             table.addCell(cellState);
             PdfPCell cellCity = new PdfPCell(new Phrase(user.getCity()));
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
}
