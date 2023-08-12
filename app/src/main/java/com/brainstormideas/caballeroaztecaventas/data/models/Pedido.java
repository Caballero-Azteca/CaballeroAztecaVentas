package com.brainstormideas.caballeroaztecaventas.data.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.brainstormideas.caballeroaztecaventas.entidad.ItemProductoPedido;

import java.util.ArrayList;
import java.util.Date;

public class Pedido {

    private static int id;
    private static Cliente cliente;
    private static Vendedor vendedor;
    private static ArrayList<ItemProductoPedido> listaDeProductos = new ArrayList<>();
    private static Date fecha;
    private static String folio;
    private static String documento;
    private static String observaciones;
    private static String tipo;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    public static final String correoPrincipal = "luisfong@caballeroazteca.com.mx";

    public static boolean preciosConIVA = true;

    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "Pedido";
    private static final String ID = "id";

    private static String total = "";

    public Pedido(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public Pedido() {
    }

    public void borrarPedido() {
        editor.clear();
        editor.commit();
    }

    public void crearPedido(Date fecha, String folio, String documento) {

        Pedido.fecha = fecha;
        Pedido.folio = folio;
        Pedido.documento = documento;
        editor.putInt(ID, id);
        editor.commit();
    }

    public static String getTipo() {
        return tipo;
    }

    public static void setTipo(String tipo) {
        Pedido.tipo = tipo;
    }

    public static String getFolio() {
        return folio;
    }

    public static void setFolio(String folio) {
        Pedido.folio = folio;
    }

    public static String getDocumento() {
        return documento;
    }

    public static void setDocumento(String documento) {
        Pedido.documento = documento;
    }

    public static String getObservaciones() {
        return observaciones;
    }

    public static void setObservaciones(String observaciones) {
        Pedido.observaciones = observaciones;
    }

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        Pedido.id = id;
    }

    public static Cliente getCliente() {
        return cliente;
    }

    public static void setCliente(Cliente cliente) {
        Pedido.cliente = cliente;
    }

    public static Vendedor getVendedor() {
        return vendedor;
    }

    public static void setVendedor(Vendedor vendedor) {
        Pedido.vendedor = vendedor;
    }

    public static ArrayList<ItemProductoPedido> getListaDeProductos() {
        return listaDeProductos;
    }

    public static void setListaDeProductos(ArrayList<ItemProductoPedido> listaDeProductos) {
        Pedido.listaDeProductos = listaDeProductos;
    }

    public static Date getFecha() {
        return fecha;
    }

    public static void setFecha(Date fecha) {
        Pedido.fecha = fecha;
    }

    public static String getTotal() {
        return total;
    }

    public static void setTotal(String total) {
        Pedido.total = total;
    }

}
