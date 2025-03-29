package com.example.genessystem.utils;

import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class PatientPDFGenerator {
    public static void generatePDF(Map<String, Object> patientData, File file) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        // Define fonts
        Font titleFont = new Font(Font.HELVETICA, 22, Font.BOLD, new Color(70, 70, 70));
        Font subtitleFont = new Font(Font.HELVETICA, 14, Font.BOLD, new Color(70, 70, 70));
        Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(70, 70, 70));
        Font textFont = new Font(Font.HELVETICA, 10);
        Font boldTextFont = new Font(Font.HELVETICA, 10, Font.BOLD);

        // Header with logo and title
        Image logo = Image.getInstance(PatientPDFGenerator.class.getResource("/images/mainLogo.png"));
        logo.scaleAbsolute(80f, 80f); // Increased logo size

        // Create header table
        PdfPTable headerTable = new PdfPTable(3);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1, 3, 1.5f});
        headerTable.setSpacingAfter(20);

        // Logo cell
        PdfPCell logoCell = new PdfPCell(logo, true);
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerTable.addCell(logoCell);

        // Title cell
        Paragraph title = new Paragraph();
        title.add(new Chunk("SGS\n", titleFont));
        title.add(new Chunk("Smart Genetic System\n", new Font(Font.HELVETICA, 12, Font.ITALIC)));
        title.setAlignment(Element.ALIGN_CENTER);

        PdfPCell titleCell = new PdfPCell(title);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerTable.addCell(titleCell);

        // Date/time cell
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

        Paragraph dateTime = new Paragraph();
        dateTime.add(new Chunk("Date: ", boldTextFont));
        dateTime.add(new Chunk(date + "\n", textFont));
        dateTime.add(new Chunk("Time: ", boldTextFont));
        dateTime.add(new Chunk(time, textFont));
        dateTime.setAlignment(Element.ALIGN_RIGHT);

        PdfPCell dateCell = new PdfPCell(dateTime);
        dateCell.setBorder(Rectangle.NO_BORDER);
        dateCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerTable.addCell(dateCell);

        document.add(headerTable);

        // Add separator line
        LineSeparator line = new LineSeparator();
        line.setLineWidth(1f);
        document.add(line);
        document.add(Chunk.NEWLINE);

        // Patient Info Section
        Paragraph patientHeader = new Paragraph("Patient Information", subtitleFont);
        patientHeader.setSpacingAfter(10);
        document.add(patientHeader);

        // Create patient info table (2 columns)
        PdfPTable patientTable = new PdfPTable(2);
        patientTable.setWidthPercentage(100);
        patientTable.setWidths(new float[]{1, 3});
        patientTable.setSpacingAfter(15);

        addTableRow(patientTable, "National ID:", patientData.get("nationalId"), boldTextFont, textFont);
        addTableRow(patientTable, "Medical Record:", patientData.get("medicalRecord"), boldTextFont, textFont);
        addTableRow(patientTable, "Name:", patientData.get("firstName") + " " + patientData.get("lastName"), boldTextFont, textFont);
        addTableRow(patientTable, "Age:", patientData.get("age") + " yrs", boldTextFont, textFont);
        addTableRow(patientTable, "Gender:", patientData.get("gender"), boldTextFont, textFont);
        addTableRow(patientTable, "Phone:", patientData.get("phone"), boldTextFont, textFont);
        addTableRow(patientTable, "Email:", patientData.get("email"), boldTextFont, textFont);

        document.add(patientTable);

        // Diagnostics Section
        Paragraph diagHeader = new Paragraph("Diagnostics", subtitleFont);
        diagHeader.setSpacingAfter(10);
        document.add(diagHeader);

        PdfPTable diagTable = new PdfPTable(2);
        diagTable.setWidthPercentage(100);
        diagTable.setWidths(new float[]{1, 3});
        diagTable.setSpacingAfter(15);

        addTableRow(diagTable, "Disease:", patientData.get("disease"), boldTextFont, textFont);
        addTableRow(diagTable, "Condition:", patientData.get("condition"), boldTextFont, textFont);
        addTableRow(diagTable, "1st Relative:", patientData.get("firstRelative"), boldTextFont, textFont);
        addTableRow(diagTable, "2nd Relative:", patientData.get("secondRelative"), boldTextFont, textFont);

        document.add(diagTable);

        // Analysis Section
        Paragraph analysisHeader = new Paragraph("Analysis", subtitleFont);
        analysisHeader.setSpacingAfter(10);
        document.add(analysisHeader);

        PdfPTable analysisTable = new PdfPTable(2);
        analysisTable.setWidthPercentage(100);
        analysisTable.setWidths(new float[]{1, 3});
        analysisTable.setSpacingAfter(15);

        addTableRow(analysisTable, "Weight:", patientData.get("diseaseWeight"), boldTextFont, textFont);
        addTableRow(analysisTable, "Risk Score:", patientData.get("totalRiskScore"), boldTextFont, textFont);
        addTableRow(analysisTable, "Risk Level:", patientData.get("riskLevel"), boldTextFont, textFont);

        document.add(analysisTable);

        // Summary Section
        Paragraph summaryHeader = new Paragraph("Summary", subtitleFont);
        summaryHeader.setSpacingAfter(10);
        document.add(summaryHeader);

        Paragraph summaryContent = new Paragraph(String.valueOf(patientData.get("wikiSummary")), textFont);
        summaryContent.setSpacingAfter(15);
        document.add(summaryContent);

        // Recommendations Section
        Paragraph recHeader = new Paragraph("Recommendations", subtitleFont);
        recHeader.setSpacingAfter(10);
        document.add(recHeader);

        Paragraph recContent = new Paragraph(String.valueOf(patientData.get("recommendations")), textFont);
        document.add(recContent);

        document.close();
    }

    private static void addTableRow(PdfPTable table, String label, Object value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPaddingBottom(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(String.valueOf(value), valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPaddingBottom(5);
        table.addCell(valueCell);
    }
}
