package com.sanjot.inventory.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.sanjot.inventory.entity.Transaction;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StockExportUtil {

    public static void exportToExcel(List<Transaction> list, HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=issued_inventory_report.xlsx");

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Issued Inventory");

        Row header = sheet.createRow(0);
        String[] columns = {"Item Name", "Quantity", "Unit Price",  "Department", "Transaction Date"};

        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        int rowNum = 1;

        // Filter only issued transactions (REMOVE)
        List<Transaction> issuedList = list.stream()
                .filter(t -> "REMOVE".equalsIgnoreCase(t.getTransactionType().name()))
                .collect(Collectors.toList());

        for (Transaction t : issuedList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(t.getInventoryItem().getName());
            row.createCell(1).setCellValue(t.getQuantity());
            row.createCell(2).setCellValue(t.getInventoryItem().getUnitPrice().doubleValue());
//            row.createCell(3).setCellValue(t.getInventoryItem().getCategory().getName());
            row.createCell(3).setCellValue(
                    t.getUser() != null && t.getUser().getDepartment() != null ?
                            t.getUser().getDepartment().getName() : "N/A"
            );
//            row.createCell().setCellValue(t.getInventoryItem().getSupplier());
//            row.createCell(6).setCellValue(t.getInventoryItem().getStatus().toString());
            row.createCell(4).setCellValue(t.getTransactionDate().toString());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    public static void exportToPdf(List<Transaction> list, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=issued_inventory_report.pdf");

        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph("Issued Inventory Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{2, 1, 2, 2,  3});

            Stream.of("Item Name", "Quantity", "Unit Price",  "Department",  "Transaction Date")
                    .forEach(header -> {
                        PdfPCell cell = new PdfPCell(new Phrase(header));
                        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        table.addCell(cell);
                    });

            // Filter only issued transactions (REMOVE)
            List<Transaction> issuedList = list.stream()
                    .filter(t -> "REMOVE".equalsIgnoreCase(t.getTransactionType().name()))
                    .collect(Collectors.toList());

            for (Transaction t : issuedList) {
                table.addCell(t.getInventoryItem().getName());
                table.addCell(String.valueOf(t.getQuantity()));
                table.addCell(t.getInventoryItem().getUnitPrice().toString());
//                table.addCell(t.getInventoryItem().getCategory().getName());
                table.addCell(
                        t.getUser() != null && t.getUser().getDepartment() != null ?
                                t.getUser().getDepartment().getName() : "N/A"
                );
//                table.addCell(t.getInventoryItem().getSupplier());
//                table.addCell(t.getInventoryItem().getStatus().toString());
                table.addCell(t.getTransactionDate().toString());
            }

            document.add(table);
            document.close();
        } catch (DocumentException e) {
            throw new IOException(e.getMessage());
        }
    }
}
