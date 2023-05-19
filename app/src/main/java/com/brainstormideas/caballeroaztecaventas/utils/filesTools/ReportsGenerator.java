package com.brainstormideas.caballeroaztecaventas.utils.filesTools;

import android.content.Context;

import androidx.annotation.NonNull;

import com.brainstormideas.caballeroaztecaventas.entidad.ItemProductoPedido;
import com.brainstormideas.caballeroaztecaventas.data.models.Cliente;
import com.brainstormideas.caballeroaztecaventas.data.models.Pedido;
import com.brainstormideas.caballeroaztecaventas.data.models.Vendedor;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ReportsGenerator {

    private Context context;
    private Cliente cliente;
    private Vendedor vendedor;
    private ArrayList<ItemProductoPedido> listDeProductos;
    private String fecha;
    private HSSFWorkbook workbook;
    private HSSFSheet sheet;
    File file;
    String precioTitulo = "PRECIO";
    public String[] titulos;

    InputStream img;

    public ReportsGenerator(Context context, Cliente cliente, Vendedor vendedor, ArrayList<ItemProductoPedido> listDeProductos, String fecha, InputStream img) {
        this.context = context;
        this.cliente = cliente;
        this.vendedor = vendedor;
        this.listDeProductos = listDeProductos;
        this.fecha = fecha;
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet("Pedido");
        this.img = img;
        if(Pedido.getDocumento().equals("Factura")){
            precioTitulo = "PRECIO (SIN IVA)";
        }
        titulos = new String[]{"CANTIDAD", "CODIGO", "MARCA", "DESCRIPCION DEL ARTICULO", precioTitulo};
    }

    public ReportsGenerator() {

    }

    public void generarReporte() throws IOException {

        //ENCABEZADO

        Row filaTitulo = sheet.createRow(0);
        Cell celdaTitulo = filaTitulo.createCell(0);
        celdaTitulo.setCellValue("DISTRIBUIDORA FERRETERA CABALLERO AZTECA, S.A. DE C.V.");

        //FECHA Y HORA
        Row filaFecha = sheet.createRow(2);
        Cell celdaTituloHora = filaFecha.createCell(0);
        celdaTituloHora.setCellValue("FECHA Y HORA: ");

        Cell celdaHora = filaFecha.createCell(2);
        celdaHora.setCellValue(fecha);


        //TITULO DE INFORMACION GENERAL

        Row filaTituloInfoGeneral = sheet.createRow(4);
        Cell celdaTituloInfoGeneral = filaTituloInfoGeneral.createCell(0);
        celdaTituloInfoGeneral.setCellValue("INFORMACIÓN DEL GENERAL:");

        //FOLIO
        Row filaFolio = sheet.createRow(5);
        Cell celdaTituloFolio = filaFolio.createCell(0);
        celdaTituloFolio.setCellValue("FOLIO: ");

        Cell celdaFolio = filaFolio.createCell(1);
        celdaFolio.setCellValue(Pedido.getFolio());

        //NOMBRE DE VENDEDOR

        Row filaVendedor = sheet.createRow(6);
        Cell celdaTituloVendedor = filaVendedor.createCell(0);
        celdaTituloVendedor.setCellValue("VENDEDOR: ");

        Cell celdaVendedor = filaVendedor.createCell(1);
        celdaVendedor.setCellValue(Pedido.getVendedor().getNombre());

        //RUTA DE PEDIDO

        Row filaRuta = sheet.createRow(7);
        Cell celdaTituloRuta = filaRuta.createCell(0);
        celdaTituloRuta.setCellValue("RUTA: ");

        Cell celdaRuta = filaRuta.createCell(1);
        celdaRuta.setCellValue(Pedido.getCliente().getRuta());

        //DOCUMENTO DE PEDIDO

        Row filaDocumento = sheet.createRow(8);
        Cell celdaTituloDocumento = filaDocumento.createCell(0);
        celdaTituloDocumento.setCellValue("FACTURA O REMISION: ");

        Cell celdaDocumento = filaDocumento.createCell(1);
        celdaDocumento.setCellValue(Pedido.getDocumento().toUpperCase());


        //TITULO DE INFORMACION DEL CLIENTE

        Row filaInfoCliente = sheet.createRow(10);
        Cell celdaInfoCliente = filaInfoCliente.createCell(0);
        celdaInfoCliente.setCellValue("INFORMACIÓN DEL CLIENTE:");


        //CODIGO Y RAZON
        Row filaInfo1 = sheet.createRow(11);

        Cell celdaTituloCodigo = filaInfo1.createCell(0);
        celdaTituloCodigo.setCellValue("CODIGO: ");
        Cell celdaCodigo = filaInfo1.createCell(1);
        celdaCodigo.setCellValue(Pedido.getCliente().getId());

        Cell celdaTituloRazon = filaInfo1.createCell(2);
        celdaTituloRazon.setCellValue("RAZON: ");
        Cell celdaRazon = filaInfo1.createCell(3);
        celdaRazon.setCellValue(Pedido.getCliente().getRazon());

        //RFC y DOMICILIO
        Row filaInfo2 = sheet.createRow(12);

        Cell celdaTituloRFC = filaInfo2.createCell(0);
        celdaTituloRFC.setCellValue("RFC: ");
        Cell celdaRFC = filaInfo2.createCell(1);
        celdaRFC.setCellValue(Pedido.getCliente().getRfc());

        Cell celdaTituloDomicilio = filaInfo2.createCell(2);
        celdaTituloDomicilio.setCellValue("DOMICILIO: ");
        Cell celdaDomicilio = filaInfo2.createCell(3);
        celdaDomicilio.setCellValue(Pedido.getCliente().getCalle() + " " +
                "#" + Pedido.getCliente().getNumeroExterior() + "INT: " + Pedido.getCliente().getNumeroInterior() +
                " COLONIA " + Pedido.getCliente().getColonia() + " C.P. " + Pedido.getCliente().getCp());

        //CIUDAD Y ESTADO
        Row filaInfo3 = sheet.createRow(13);

        Cell celdaTituloCiudad = filaInfo3.createCell(0);
        celdaTituloCiudad.setCellValue("CIUDAD: ");
        Cell celdaCiudad = filaInfo3.createCell(1);
        celdaCiudad.setCellValue(Pedido.getCliente().getMunicipio());

        Cell celdaTituloEstado = filaInfo3.createCell(2);
        celdaTituloEstado.setCellValue("ESTADO: ");
        Cell celdaEstado = filaInfo3.createCell(3);
        celdaEstado.setCellValue(Pedido.getCliente().getEstado());

        //TELEFONO Y EMAIL
        Row filaInfo4 = sheet.createRow(14);

        Cell celdaTituloTelefono = filaInfo4.createCell(0);
        celdaTituloTelefono.setCellValue("TELEFONO: ");
        Cell celdaTelefono = filaInfo4.createCell(1);
        celdaTelefono.setCellValue(Pedido.getCliente().getTelefono());

        Cell celdaTituloEmail = filaInfo4.createCell(2);
        celdaTituloEmail.setCellValue("EMAIL: ");
        Cell celdaEmail = filaInfo4.createCell(3);
        celdaEmail.setCellValue(Pedido.getCliente().getEmail());


        Row filaTitulos = sheet.createRow(16);
        for (int i = 0; i < titulos.length; i++) {

            Cell celda = filaTitulos.createCell(i);
            celda.setCellValue(titulos[i]);
        }

        for (int i = 0; i < listDeProductos.size(); ++i) {

            Row dataRow = sheet.createRow(i+17);
            String cantidad = listDeProductos.get(i).getCantidad();
            String codigo = listDeProductos.get(i).getId();
            String marca = listDeProductos.get(i).getMarca();
            String descripcion = listDeProductos.get(i).getNombre();
            String precio = listDeProductos.get(i).getPrecio();

            dataRow.createCell(0).setCellValue(cantidad);
            dataRow.createCell(1).setCellValue(codigo);
            dataRow.createCell(2).setCellValue(marca);
            dataRow.createCell(3).setCellValue(descripcion);
            dataRow.createCell(4).setCellValue(precio);

        }

        Row filaTotal = sheet.createRow(listDeProductos.size()+18);
        Cell celdaTotal = filaTotal.createCell(0);
        celdaTotal.setCellValue("TOTAL:" + Pedido.getTotal());

        Row filaObservaciones = sheet.createRow(listDeProductos.size()+19);
        Cell celdaObservaciones = filaObservaciones.createCell(0);
        if (!Pedido.getObservaciones().equals("")) {
            celdaObservaciones.setCellValue("Observaciones:" + Pedido.getObservaciones().toUpperCase());
        } else {
            celdaObservaciones.setCellValue("SIN OBSERVACIONES");
        }

    }

    public void guardar() {
        file = new File(context.getExternalFilesDir(null),  Pedido.getFolio() +  "xls.xls");
        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            try {
                assert outputStream != null;
                outputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public File obtenerReporte() {
        return file;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public ArrayList<ItemProductoPedido> getListDeProductos() {
        return listDeProductos;
    }

    public void setListDeProductos(ArrayList<ItemProductoPedido> listDeProductos) {
        this.listDeProductos = listDeProductos;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @NonNull
    @Override
    public String toString() {
        return "ReportsGenerator{" +
                "cliente=" + cliente +
                ", vendedor=" + vendedor +
                ", listDeProductos=" + listDeProductos +
                ", fecha=" + fecha +
                '}';
    }
}
