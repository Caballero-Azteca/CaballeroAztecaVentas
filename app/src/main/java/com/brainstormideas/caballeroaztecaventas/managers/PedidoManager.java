package com.brainstormideas.caballeroaztecaventas.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.brainstormideas.caballeroaztecaventas.data.adapters.UriTypeAdapter;
import com.brainstormideas.caballeroaztecaventas.data.models.PedidoFolio;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PedidoManager {

    private static PedidoManager instance;
    private final Set<PedidoFolio> listaPedidos;
    private final SharedPreferences sharedPreferences;
    private final Object lock = new Object();

    private PedidoManager(Context context) {
        listaPedidos = new HashSet<>();
        sharedPreferences = context.getSharedPreferences("PedidoManagerPrefs", Context.MODE_PRIVATE);
        cargarPedidosGuardados();
    }

    public static synchronized PedidoManager getInstance(Context context) {
        if (instance == null) {
            instance = new PedidoManager(context);
        }
        return instance;
    }

    private synchronized void cargarPedidosGuardados() {
        synchronized (lock) {
            String pedidosJson = sharedPreferences.getString("pedidos", null);
            if (pedidosJson != null) {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Uri.class, new UriTypeAdapter())
                        .create();
                Type type = new TypeToken<Set<PedidoFolio>>() {
                }.getType();
                Set<PedidoFolio> pedidosGuardados = gson.fromJson(pedidosJson, type);
                if (pedidosGuardados != null) {
                    listaPedidos.addAll(pedidosGuardados);
                }
            }
        }
    }

    public synchronized Set<PedidoFolio> getListaPedidos() {
        synchronized (lock) {
            return new HashSet<>(listaPedidos);
        }
    }

    public void setListaPedidos(List<PedidoFolio> listaPedidos) {
        synchronized (lock) {
            if (listaPedidos != null) {
                this.listaPedidos.addAll(listaPedidos);
                guardarPedidos();
            }
        }
    }

    private void guardarPedidos() {
        synchronized (lock) {

            Set<String> folios = new HashSet<>();
            List<PedidoFolio> listaActualizada = new ArrayList<>();

            for (PedidoFolio pedido : listaPedidos) {
                if (!folios.contains(pedido.getFolio())) {
                    folios.add(pedido.getFolio());
                    listaActualizada.add(pedido);
                }
            }

            listaPedidos.clear();
            listaPedidos.addAll(listaActualizada);

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Uri.class, new UriTypeAdapter())
                    .create();
            String pedidosJson = gson.toJson(listaPedidos);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("pedidos", pedidosJson);
            editor.apply();
        }
    }

    public synchronized void agregarPedido(PedidoFolio pedido) {
        synchronized (lock) {
            listaPedidos.add(pedido);
            guardarPedidos();
        }
    }

    public synchronized void eliminarPedido(PedidoFolio pedido) {
        synchronized (lock) {
            listaPedidos.remove(pedido);
            guardarPedidos();
        }
    }

    public String getUltimoFolioPedidos() {
        synchronized (lock) {
            return sharedPreferences.getString("ultimoFolioPedidos", "N/A");
        }
    }

    public String getUltimoFolioCotizaciones() {
        synchronized (lock) {
            return sharedPreferences.getString("ultimoFolioCotizaciones", "N/A");
        }
    }

    public void guardarUltimoFolioPedidos(String ultimoFolio) {
        synchronized (lock) {
            String ultimoFolioGuardado = sharedPreferences.getString("ultimoFolioPedidos", "N/A");
            if (ultimoFolio != ultimoFolioGuardado) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("ultimoFolioPedidos", ultimoFolio);
                editor.apply();
            }
        }
    }

    public void guardarUltimoFolioCotizaciones(String ultimoFolio) {
        synchronized (lock) {
            String ultimoFolioGuardado = sharedPreferences.getString("ultimoFolioCotizaciones", "N/A");
            if (ultimoFolio != ultimoFolioGuardado) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("ultimoFolioCotizaciones", ultimoFolio);
                editor.apply();
            }
        }
    }
}
