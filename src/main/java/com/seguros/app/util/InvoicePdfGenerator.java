package com.seguros.app.util;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.seguros.app.model.Payment;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;

public class InvoicePdfGenerator {

    // Generates the invoice PDF for a specific payment
    public static void generate(Payment payment,
                                OutputStream outputStream) throws Exception {

        // Creates the PDF document in A4 format
        Document document = new Document(PageSize.A4, 36, 36, 50, 36);

        // Connects the PDF document to the HTTP response stream
        PdfWriter.getInstance(document, outputStream);

        // Opens the document for writing
        document.open();
        
        // Agrega el logo de la empresa al PDF
        Image logo = Image.getInstance(
                InvoicePdfGenerator.class
                        .getResource("/static/images/logo.jpeg")
        );

// Tamaño del logo
logo.scaleToFit(140, 140);

// Centra el logo
logo.setAlignment(Element.ALIGN_CENTER);

// Agrega espacio debajo del logo
logo.setSpacingAfter(15);

// Inserta el logo en el documento
document.add(logo);

        // Title font configuration
        Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);

        // Main title
        Paragraph title = new Paragraph("Trust Insurance", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Subtitle
        Paragraph subtitle = new Paragraph("Payment Invoice");
        subtitle.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitle);

        // Blank line
        document.add(new Paragraph(" "));

        // Invoice information table (2 columns)
        PdfPTable info = new PdfPTable(2);
        info.setWidthPercentage(100);

        // Basic invoice data
        info.addCell(createCell("Invoice Number"));
        info.addCell("INV-" + payment.getId());

        info.addCell(createCell("Payment Date"));
        info.addCell(
                payment.getPaymentDate()
                        .format(DateTimeFormatter.ISO_DATE)
        );

        info.addCell(createCell("Client"));
        info.addCell(
                payment.getPolicy()
                        .getClient()
                        .getName()
        );

        info.addCell(createCell("Insurance"));
        info.addCell(
                payment.getPolicy()
                        .getInsurance()
                        .getName()
        );

        info.addCell(createCell("Policy Number"));
        info.addCell(payment.getPolicy().getPolicyNumber());

        info.addCell(createCell("Policy Status"));
        info.addCell(payment.getPolicy().getState());

        document.add(info);

        // Blank line
        document.add(new Paragraph(" "));

        // Payment detail table
        PdfPTable detail = new PdfPTable(2);
        detail.setWidthPercentage(100);

        detail.addCell(createCell("Description"));
        detail.addCell(createCell("Amount"));

        detail.addCell("Insurance Payment");
        detail.addCell(String.format("%,.2f", payment.getAmount()));

        document.add(detail);

        // Blank line
        document.add(new Paragraph(" "));

        // Total table aligned to the right
        PdfPTable totals = new PdfPTable(2);
        totals.setWidthPercentage(40);
        totals.setHorizontalAlignment(Element.ALIGN_RIGHT);

        totals.addCell(createCell("Total"));
        totals.addCell(String.format("%,.2f", payment.getAmount()));

        document.add(totals);

        // Footer message
        document.add(new Paragraph(" "));
        Paragraph footer = new Paragraph(
                "Thank you for choosing Seguros App."
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        // Closes the document
        document.close();
    }

    // Creates a formatted header cell
    private static PdfPCell createCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(6);
        return cell;
    }
}