package com.brainstormideas.caballeroaztecaventas.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "vendedores")
public class Vendedor implements Parcelable {

    @PrimaryKey
    @NonNull
    private int id;
    private String nombre;
    private String usuario;
    private String password;
    private String telefono;
    private String email;

    public Vendedor(int id, String nombre, String usuario, String password, String telefono, String email) {
        this.id = id;
        this.nombre = nombre;
        this.usuario = usuario;
        this.password = password;
        this.telefono = telefono;
        this.email = email;
    }

    protected Vendedor(Parcel in) {
        id = in.readInt();
        nombre = in.readString();
        usuario = in.readString();
        password = in.readString();
        telefono = in.readString();
        email = in.readString();
    }

    public Vendedor() {

    }

    public static final Creator<Vendedor> CREATOR = new Creator<Vendedor>() {
        @Override
        public Vendedor createFromParcel(Parcel in) {
            return new Vendedor(in);
        }

        @Override
        public Vendedor[] newArray(int size) {
            return new Vendedor[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nombre);
        dest.writeString(usuario);
        dest.writeString(password);
        dest.writeString(telefono);
        dest.writeString(email);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "Vendedor{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", userName='" + usuario + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
