package com.brainstormideas.caballeroaztecaventas.data.models;

import com.brainstormideas.caballeroaztecaventas.entidad.ItemProductoPedido;

import java.util.ArrayList;
import java.util.List;

public class PedidoFolio {

    String folio;
    String tipo;

    String vendedor;
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

    List<ItemProductoPedido> listaDeProductos;

    String totalSinIVA;
    String totalConIVA;
    
    String historial;

    public PedidoFolio(String folio, String tipo, String vendedor, String ruta, String tipoDocumento,
                       String codigoCliente, String razon, String rfc, String domicilio,
                       String ciudad, String estado, String telefono, String email,
                       String total, String observaciones, List<ItemProductoPedido> listaDeProductos,
                       String status, String numeroFactura, String pagoFactura, String repartidor, String fecha,
                       String totalSinIVA, String totalConIVA, String historial) {
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
    }

    public PedidoFolio(){

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

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
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

    public void setListaDeProductos(List<ItemProductoPedido> listaDeProductos) {
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
}
