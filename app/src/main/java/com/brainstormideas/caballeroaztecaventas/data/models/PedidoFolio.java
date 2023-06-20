package com.brainstormideas.caballeroaztecaventas.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.brainstormideas.caballeroaztecaventas.entidad.ItemProductoPedido;

import java.util.ArrayList;
import java.util.List;

public class PedidoFolio implements Parcelable {

    String folio;
    String tipo;

    Vendedor vendedor;
    String ruta;
    String tipoDocumento;

    String codigoCliente;
    String razon;
    String rfc;
    String domicilio;
    String ciudad;
    String estado;
    String telefono;
    String email;

    String total;
    String observaciones;

    String status;
    String numeroFactura;
    String pagoFactura;
    String repartidor;

    String fecha;

    ArrayList<ItemProductoPedido> listaDeProductos;

    String totalSinIVA;
    String totalConIVA;

    String historial;

    String uriExcel;
    String uriPdf;

    public PedidoFolio(String folio, String tipo, Vendedor vendedor, String ruta, String tipoDocumento,
                       String codigoCliente, String razon, String rfc, String domicilio,
                       String ciudad, String estado, String telefono, String email,
                       String total, String observaciones, ArrayList<ItemProductoPedido> listaDeProductos,
                       String status, String numeroFactura, String pagoFactura, String repartidor,
                       String fecha, String totalSinIVA, String totalConIVA, String historial,
                       String uriExcel, String uriPdf) {
        this.folio = folio;
        this.tipo = tipo;
        this.vendedor = vendedor;
        this.ruta = ruta;
        this.tipoDocumento = tipoDocumento;
        this.codigoCliente = codigoCliente;
        this.razon = razon;
        this.rfc = rfc;
        this.domicilio = domicilio;
        this.ciudad = ciudad;
        this.estado = estado;
        this.telefono = telefono;
        this.email = email;
        this.total = total;
        this.observaciones = observaciones;
        this.listaDeProductos = listaDeProductos;
        this.status = status;
        this.numeroFactura = numeroFactura;
        this.pagoFactura = pagoFactura;
        this.repartidor = repartidor;
        this.fecha = fecha;
        this.totalSinIVA = totalSinIVA;
        this.totalConIVA = totalConIVA;
        this.historial = historial;
        this.uriExcel = uriExcel;
        this.uriPdf = uriPdf;
    }

    public static final Parcelable.Creator<PedidoFolio> CREATOR = new Parcelable.Creator<PedidoFolio>() {
        @Override
        public PedidoFolio createFromParcel(Parcel in) {
            return new PedidoFolio(in);
        }

        @Override
        public PedidoFolio[] newArray(int size) {
            return new PedidoFolio[size];
        }
    };

    protected PedidoFolio(Parcel in) {
        folio = in.readString();
        tipo = in.readString();
        vendedor = in.readParcelable(Vendedor.class.getClassLoader());
        ruta = in.readString();
        tipoDocumento = in.readString();
        codigoCliente = in.readString();
        razon = in.readString();
        rfc = in.readString();
        domicilio = in.readString();
        ciudad = in.readString();
        estado = in.readString();
        telefono = in.readString();
        email = in.readString();
        total = in.readString();
        observaciones = in.readString();
        listaDeProductos = in.createTypedArrayList(ItemProductoPedido.CREATOR);
        status = in.readString();
        numeroFactura = in.readString();
        pagoFactura = in.readString();
        repartidor = in.readString();
        fecha = in.readString();
        totalSinIVA = in.readString();
        totalConIVA = in.readString();
        historial = in.readString();
        uriExcel = in.readString();
        uriPdf = in.readString();
    }

    public PedidoFolio() {

    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getRazon() {
        return razon;
    }

    public void setRazon(String razon) {
        this.razon = razon;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<ItemProductoPedido> getListaDeProductos() {
        return listaDeProductos;
    }

    public void setListaDeProductos(ArrayList<ItemProductoPedido> listaDeProductos) {
        this.listaDeProductos = listaDeProductos;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public String getRepartidor() {
        return repartidor;
    }

    public String getPagoFactura() {
        return pagoFactura;
    }

    public void setPagoFactura(String pagoFactura) {
        this.pagoFactura = pagoFactura;
    }

    public void setRepartidor(String repartidor) {
        this.repartidor = repartidor;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTotalSinIVA() {
        return totalSinIVA;
    }

    public void setTotalSinIVA(String totalSinIVA) {
        this.totalSinIVA = totalSinIVA;
    }

    public String getTotalConIVA() {
        return totalConIVA;
    }

    public void setTotalConIVA(String totalConIVA) {
        this.totalConIVA = totalConIVA;
    }

    public String getHistorial() {
        return historial;
    }

    public void setHistorial(String historial) {
        this.historial = historial;
    }

    public String getUriExcel() {
        return uriExcel;
    }

    public void setUriExcel(String uriExcel) {
        this.uriExcel = uriExcel;
    }

    public String getUriPdf() {
        return uriPdf;
    }

    public void setUriPdf(String uriPdf) {
        this.uriPdf = uriPdf;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(folio);
        parcel.writeString(tipo);
        parcel.writeParcelable(vendedor, i);
        parcel.writeString(ruta);
        parcel.writeString(tipoDocumento);
        parcel.writeString(codigoCliente);
        parcel.writeString(razon);
        parcel.writeString(rfc);
        parcel.writeString(domicilio);
        parcel.writeString(ciudad);
        parcel.writeString(estado);
        parcel.writeString(telefono);
        parcel.writeString(email);
        parcel.writeString(total);
        parcel.writeString(observaciones);
        parcel.writeTypedList(listaDeProductos);
        parcel.writeString(status);
        parcel.writeString(numeroFactura);
        parcel.writeString(pagoFactura);
        parcel.writeString(repartidor);
        parcel.writeString(fecha);
        parcel.writeString(totalSinIVA);
        parcel.writeString(totalConIVA);
        parcel.writeString(historial);
        parcel.writeString(uriExcel);
        parcel.writeString(uriPdf);
    }
}
