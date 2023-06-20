package com.brainstormideas.caballeroaztecaventas.data.models;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "clientes")
public class Cliente {

    @PrimaryKey
    @NonNull
    private Long id;
    @NonNull
    private String code;
    private String razon;
    private String rfc;
    private String municipio;
    private String estado;
    private String calle;
    private String colonia;
    private String numeroExterior;
    private String numeroInterior;
    private String cp;
    private String telefono;
    private String email;
    private String ruta;
    private String agenteVenta;
    private String agenteCobro;

    public Cliente(@NonNull Long id, @NonNull String code, String razon, String rfc, String municipio,
                   String estado, String calle, String colonia, String numeroExterior, String numeroInterior,
                   String cp, String telefono, String email, String ruta, String agenteVenta,
                   String agenteCobro) {

        this.id = id;
        this.code = code;
        this.razon = razon;
        this.rfc = rfc;
        this.municipio = municipio;
        this.estado = estado;
        this.calle = calle;
        this.colonia = colonia;
        this.numeroExterior = numeroExterior;
        this.numeroInterior = numeroInterior;
        this.cp = cp;
        this.telefono = telefono;
        this.email = email;
        this.ruta = ruta;
        this.agenteVenta = agenteVenta;
        this.agenteCobro = agenteCobro;
    }

    public Cliente() {

        id = null;
        code = null;
    }

    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    @NonNull
    public String getCode() {
        return code;
    }

    public void setCode(@NonNull String code) {
        this.code = code;
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

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public String getNumeroExterior() {
        return numeroExterior;
    }

    public void setNumeroExterior(String numeroExterior) {
        this.numeroExterior = numeroExterior;
    }

    public String getNumeroInterior() {
        return numeroInterior;
    }

    public void setNumeroInterior(String numeroInterior) {
        this.numeroInterior = numeroInterior;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
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

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getAgenteVenta() {
        return agenteVenta;
    }

    public void setAgenteVenta(String agenteVenta) {
        this.agenteVenta = agenteVenta;
    }

    public String getAgenteCobro() {
        return agenteCobro;
    }

    public void setAgenteCobro(String agenteCobro) {
        this.agenteCobro = agenteCobro;
    }

    @NonNull
    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", razon='" + razon + '\'' +
                ", rfc='" + rfc + '\'' +
                ", municipio='" + municipio + '\'' +
                ", estado='" + estado + '\'' +
                ", calle='" + calle + '\'' +
                ", colonia='" + colonia + '\'' +
                ", numeroExterior='" + numeroExterior + '\'' +
                ", numeroInterior='" + numeroInterior + '\'' +
                ", cp='" + cp + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", ruta='" + ruta + '\'' +
                ", agenteVenta='" + agenteVenta + '\'' +
                ", agenteCobro='" + agenteCobro + '\'' +
                '}';
    }
}
