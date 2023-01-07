package com.brainstormideas.caballeroaztecaventas.tools;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.brainstormideas.caballeroaztecaventas.PdfViewer;
import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.models.Pedido;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.brainstormideas.caballeroaztecaventas.models.Pedido.getFolio;

public class TemplatePDF extends PdfPageEventHelper {

    private final Context context;
    private File pdfFile;
    private Document document;
    private PdfWriter pdfWriter;
    private Paragraph paragraph;
    private final Font ftitle = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
    private final Font fsubTitle = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    private final Font fText = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
    private final Font fHighText = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
    private Image imagen;
    private BarcodeQRCode barcodeQRCode;

    public TemplatePDF(Context context) throws IOException, BadElementException {
        this.context = context;
    }

    public void openDocument() {

        createFile();
        try {

            document = new Document(PageSize.A4);
            barcodeQRCode = new BarcodeQRCode(getFolio(),  100, 100, null);
            Image qrcodeImage = barcodeQRCode.getImage();
            qrcodeImage.setAbsolutePosition(410,702);
            qrcodeImage.scalePercent(100);
            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
            document.add(qrcodeImage);
            document.add(imagen);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void createFile() {
        File folder = new File(context.getExternalFilesDir(null), getFolio() +"pdf.pdf");

        if (!folder.exists()) {
            folder.mkdirs();
        }
        pdfFile = new File(folder, getFolio() +"pdf.pdf");

    }

    public void closeDocument() {
        document.close();
    }

    public void addData(String title, String subject, String author) {
        document.addTitle(title);
        document.addSubject(subject);
        document.addAuthor(author);
    }

    public void addTitles(String title, String subtitle, String date) throws DocumentException, IOException {

        paragraph = new Paragraph();

        PdfPTable pdfTable = new PdfPTable(2);
        pdfTable.setWidthPercentage(100);
        pdfTable.setSpacingBefore(2);
        pdfTable.setSpacingAfter(2);

        float[] medidaCeldas = {0.60f, 2.00f};
        pdfTable.setWidths(medidaCeldas);

        Drawable d = context.getResources().getDrawable(R.drawable.logo);
        BitmapDrawable bitDw = ((BitmapDrawable) d);
        Bitmap bmp = bitDw.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        imagen = Image.getInstance(stream.toByteArray());
        imagen.scaleToFit(150, 50);

        PdfPCell pdfPCellLogo;
        pdfPCellLogo = new PdfPCell();
        pdfPCellLogo.addElement(imagen);
        pdfPCellLogo.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfPCellLogo.setBorder(Rectangle.NO_BORDER);
        pdfTable.addCell(pdfPCellLogo);

        PdfPCell pdfPCellTitulo;
        pdfPCellTitulo = new PdfPCell(new Phrase(title +"\n" + subtitle+"\n" + "Fecha y hora: " + date, ftitle));
        pdfPCellTitulo.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
        pdfPCellTitulo.setBorder(Rectangle.NO_BORDER);
        pdfTable.addCell(pdfPCellTitulo);

        paragraph.add(pdfTable);
        document.add(paragraph);

    }

    public void addChildParagraph(Paragraph childParagraph) {
        childParagraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.add(childParagraph);
    }

    public void addParagraph(String text) throws DocumentException {
        paragraph = new Paragraph(text, fText);
        paragraph.setSpacingAfter(3);
        paragraph.setSpacingBefore(3);
        document.add(paragraph);
    }

    public void generalInfoTable() throws DocumentException {

        paragraph = new Paragraph();
        paragraph.setFont(fText);
        PdfPTable pdfTable = new PdfPTable(2);
        pdfTable.setWidthPercentage(100);
        pdfTable.setSpacingBefore(4);

        //////////// TITULO ///////////////////////////////////////////////////////

        PdfPCell pdfPCell;
        pdfPCell = new PdfPCell(new Phrase("FOLIO:", fHighText));
        pdfPCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfTable.addCell(pdfPCell);

        PdfPCell pdfPCell1;
        pdfPCell1 = new PdfPCell(new Phrase(getFolio(), fText));
        pdfPCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfTable.addCell(pdfPCell1);

        ////////////////////////////////////////////////////////////////////////////

        PdfPCell pdfPCell2;
        pdfPCell2 = new PdfPCell(new Phrase("VENDEDOR:", fHighText));
        pdfPCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfTable.addCell(pdfPCell2);

        PdfPCell pdfPCell3;
        pdfPCell3 = new PdfPCell(new Phrase(Pedido.getVendedor().getNombre(), fText));
        pdfPCell3.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfTable.addCell(pdfPCell3);

        //////////// TITULO ///////////////////////////////////////////////////////

        PdfPCell pdfPCell4;
        pdfPCell4 = new PdfPCell(new Phrase("RUTA:", fHighText));
        pdfPCell4.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfTable.addCell(pdfPCell4);

        PdfPCell pdfPCell5;
        pdfPCell5 = new PdfPCell(new Phrase(Pedido.getCliente().getRuta(), fText));
        pdfPCell5.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfTable.addCell(pdfPCell5);

        //////////// TITULO ///////////////////////////////////////////////////////

        PdfPCell pdfPCell6;
        pdfPCell6 = new PdfPCell(new Phrase("FACTURA O REGISTRO:", fHighText));
        pdfPCell6.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfTable.addCell(pdfPCell6);

        PdfPCell pdfPCell7;
        pdfPCell7 = new PdfPCell(new Phrase(Pedido.getDocumento().toUpperCase(), fText));
        pdfPCell7.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfTable.addCell(pdfPCell7);


        paragraph.add(pdfTable);
        document.add(paragraph);
    }

    public void clientInfoTable() throws DocumentException {

        paragraph = new Paragraph();
        paragraph.setFont(fText);
        PdfPTable pdfTable = new PdfPTable(4);
        pdfTable.setWidthPercentage(100);
        pdfTable.setSpacingBefore(3);
        pdfTable.setSpacingAfter(5);

        float[] medidaCeldas = {0.55f, 0.70f, 0.55f, 1.90f};
        pdfTable.setWidths(medidaCeldas);
        //////////// TITULO ///////////////////////////////////////////////////////

        PdfPCell pdfPCell;
        pdfPCell = new PdfPCell(new Phrase("CODIGO:", fHighText));
        pdfPCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfPCell.setFixedHeight(20);
        pdfTable.addCell(pdfPCell);

        PdfPCell pdfPCell1;
        pdfPCell1 = new PdfPCell(new Phrase(Pedido.getCliente().getId(), fText));
        pdfPCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfTable.addCell(pdfPCell1);

        ////////////////////////////////////////////////////////////////////////////

        PdfPCell pdfPCell2;
        pdfPCell2 = new PdfPCell(new Phrase("RAZON:", fHighText));
        pdfPCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfTable.addCell(pdfPCell2);

        PdfPCell pdfPCell3;
        pdfPCell3 = new PdfPCell(new Phrase(Pedido.getCliente().getRazon(), fText));
        pdfPCell3.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfPCell3.setFixedHeight(20);
        pdfTable.addCell(pdfPCell3);

        //////////// TITULO ///////////////////////////////////////////////////////

        PdfPCell pdfPCell4;
        pdfPCell4 = new PdfPCell(new Phrase("RFC:", fHighText));
        pdfPCell4.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfPCell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
        pdfTable.addCell(pdfPCell4);

        PdfPCell pdfPCell5;
        pdfPCell5 = new PdfPCell(new Phrase(Pedido.getCliente().getRfc(), fText));
        pdfPCell5.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfPCell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
        pdfPCell5.setFixedHeight(20);
        pdfTable.addCell(pdfPCell5);

        //////////// TITULO ///////////////////////////////////////////////////////

        PdfPCell pdfPCell6;
        pdfPCell6 = new PdfPCell(new Phrase("DOMICILIO:", fHighText));
        pdfPCell6.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfPCell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
        pdfTable.addCell(pdfPCell6);

        PdfPCell pdfPCell7;
        pdfPCell7 = new PdfPCell(new Phrase(Pedido.getCliente().getCalle() + " " +
                "#" + Pedido.getCliente().getNumeroExterior() + " INT: " + Pedido.getCliente().getNumeroInterior() +
                " COLONIA " + Pedido.getCliente().getColonia() + " C.P. " + Pedido.getCliente().getCp(), fText));
        pdfPCell7.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfPCell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
        pdfPCell7.setFixedHeight(30);
        pdfTable.addCell(pdfPCell7);

        //////////// TITULO ///////////////////////////////////////////////////////

        PdfPCell pdfPCell8;
        pdfPCell8 = new PdfPCell(new Phrase("CIUDAD", fHighText));
        pdfPCell8.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfTable.addCell(pdfPCell8);

        PdfPCell pdfPCell9;
        pdfPCell9 = new PdfPCell(new Phrase(Pedido.getCliente().getMunicipio(), fText));
        pdfPCell9.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfPCell9.setFixedHeight(20);
        pdfTable.addCell(pdfPCell9);

        //////////// TITULO ///////////////////////////////////////////////////////

        PdfPCell pdfPCell10;
        pdfPCell10 = new PdfPCell(new Phrase("ESTADO:", fHighText));
        pdfPCell10.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfTable.addCell(pdfPCell10);

        PdfPCell pdfPCell11;
        pdfPCell11 = new PdfPCell(new Phrase(Pedido.getCliente().getEstado(), fText));
        pdfPCell11.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfPCell11.setFixedHeight(20);
        pdfTable.addCell(pdfPCell11);

        //////////// TITULO ///////////////////////////////////////////////////////

        PdfPCell pdfPCell12;
        pdfPCell12 = new PdfPCell(new Phrase("TELEFONO:", fHighText));
        pdfPCell12.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfTable.addCell(pdfPCell12);

        PdfPCell pdfPCell13;
        pdfPCell13 = new PdfPCell(new Phrase(Pedido.getCliente().getTelefono(), fText));
        pdfPCell13.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfPCell13.setFixedHeight(20);
        pdfTable.addCell(pdfPCell13);

        //////////// TITULO ///////////////////////////////////////////////////////

        PdfPCell pdfPCell14;
        pdfPCell14 = new PdfPCell(new Phrase("EMAIL:", fHighText));
        pdfPCell14.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfTable.addCell(pdfPCell14);

        PdfPCell pdfPCell15;
        pdfPCell15 = new PdfPCell(new Phrase(Pedido.getCliente().getEmail(), fText));
        pdfPCell15.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfPCell15.setFixedHeight(20);
        pdfTable.addCell(pdfPCell15);

        paragraph.add(pdfTable);
        document.add(paragraph);
    }

    public void createTable(String[] header, ArrayList<String[]> productos) throws DocumentException {

        paragraph = new Paragraph();
        paragraph.setFont(fText);
        PdfPTable pdfTable = new PdfPTable(header.length);
        pdfTable.setWidthPercentage(100);
        pdfTable.setSpacingBefore(3);
        PdfPCell pdfPCell;
        float[] medidaCeldas = {0.45f, 0.45f, 0.55f, 2.35f, 0.80f};
        pdfTable.setWidths(medidaCeldas);

        int indexC = 0;
        while (indexC < header.length) {
            pdfPCell = new PdfPCell(new Phrase(header[indexC++], fText));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setBackgroundColor(BaseColor.GRAY);
            pdfPCell.setFixedHeight(20);
            pdfTable.addCell(pdfPCell);
        }

        for (int indexR = 0; indexR < productos.size(); indexR++) {
            String[] row = productos.get(indexR);
            for (int indexCell = 0; indexCell < header.length; indexCell++) {
                pdfPCell = new PdfPCell(new Phrase(row[indexCell], fText));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setFixedHeight(20);
                pdfTable.addCell(pdfPCell);
            }
        }

        paragraph.add(pdfTable);
        document.add(paragraph);
    }

    public void abrirVistaDePDF(boolean modificar) {
        Intent i = new Intent(context, PdfViewer.class);
        i.putExtra("path", pdfFile.getAbsolutePath());
        i.putExtra("candadoModificar", modificar);
        i.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public File obtenerReporte() {
        return pdfFile;
    }
}
