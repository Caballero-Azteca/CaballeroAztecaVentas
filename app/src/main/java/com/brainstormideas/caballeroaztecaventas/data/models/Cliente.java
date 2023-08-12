package com.brainstormideas.caballeroaztecaventas.data.models;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "clientes")
public class Cliente {

    @PrimaryKey
    @NonNull
    private Long id;
    @NonNull
    private String code;
    @Nullable
    private String razon;
    @Nullable
    private String rfc;
    @Nullable
    private String municipio;
    @Nullable
    private String estado;
    @Nullable
    private String calle;
    @Nullable
    private String colonia;
    @Nullable
    private String numeroExterior;
    @Nullable
    private String numeroInterior;
    @Nullable
    private String cp;
    @Nullable
    private String telefono;
    @Nullable
    private String email;
    @Nullable
    private String ruta;
    @Nullable
    private String agenteVenta;
    @Nullable
    private String agenteCobro;

    public Cliente(@NonNull Long id, @NonNull String code, @Nullable String razon, @Nullable String rfc, @Nullable String municipio,
                   @Nullable String estado, @Nullable String calle, @Nullable String colonia, @Nullable String numeroExterior, @Nullable String numeroInterior,
                   @Nullable String cp, @Nullable String telefono, @Nullable String email, @Nullable String ruta, @Nullable String agenteVenta,
                   @Nullable String agenteCobro) {

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

    @Nullable
    public String getRazon() {
        return razon;
    }

    public void setRazon(@Nullable String razon) {
        this.razon = razon;
    }

    @Nullable
    public String getRfc() {
        return rfc;
    }

    public void setRfc(@Nullable String rfc) {
        this.rfc = rfc;
    }

    @Nullable
    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(@Nullable String municipio) {
        this.municipio = municipio;
    }

    @Nullable
    public String getEstado() {
        return estado;
    }

    public void setEstado(@Nullable String estado) {
        this.estado = estado;
    }

    @Nullable
    public String getCalle() {
        return calle;
    }

    public void setCalle(@Nullable String calle) {
        this.calle = calle;
    }

    @Nullable
    public String getColonia() {
        return colonia;
    }

    public void setColonia(@Nullable String colonia) {
        this.colonia = colonia;
    }

    @Nullable
    public String getNumeroExterior() {
        return numeroExterior;
    }

    public void setNumeroExterior(@Nullable String numeroExterior) {
        this.numeroExterior = numeroExterior;
    }

    @Nullable
    public String getNumeroInterior() {
        return numeroInterior;
    }

    public void setNumeroInterior(@Nullable String numeroInterior) {
        this.numeroInterior = numeroInterior;
    }

    @Nullable
    public String getCp() {
        return cp;
    }

    public void setCp(@Nullable String cp) {
        this.cp = cp;
    }

    @Nullable
    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(@Nullable String telefono) {
        this.telefono = telefono;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    @Nullable
    public String getRuta() {
        return ruta;
    }

    public void setRuta(@Nullable String ruta) {
        this.ruta = ruta;
    }

    @Nullable
    public String getAgenteVenta() {
        return agenteVenta;
    }

    public void setAgenteVenta(@Nullable String agenteVenta) {
        this.agenteVenta = agenteVenta;
    }

    @Nullable
    public String getAgenteCobro() {
        return agenteCobro;
    }

    public void setAgenteCobro(@Nullable String agenteCobro) {
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
