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
import java.util.List;

public class PedidoManager {

    private static PedidoManager instance;
    private final List<PedidoFolio> listaPedidos;
    private final SharedPreferences sharedPreferences;
    private final Object lock = new Object();

    private PedidoManager(Context context) {
        listaPedidos = new ArrayList<>();
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

            System.out.println("CARGANDO PEDIDOS.... DESDE PEDIDOS MANAGER");

            String pedidosJson = sharedPreferences.getString("pedidos", null);
            if (pedidosJson != null) {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Uri.class, new UriTypeAdapter())
                        .create();
                Type type = new TypeToken<List<PedidoFolio>>() {
                }.getType();
                List<PedidoFolio> pedidosGuardados = gson.fromJson(pedidosJson, type);
                if (pedidosGuardados != null) {
                    listaPedidos.addAll(pedidosGuardados);
                }
            }
            System.out.println("PEDIDOS GUARDADOS: " + listaPedidos.size());
        }
    }


    public synchronized List<PedidoFolio> getListaPedidos() {
        synchronized (lock) {
            return new ArrayList<>(listaPedidos);
        }
    }


    public void setListaPedidos(List<PedidoFolio> listaPedidos) {
        synchronized (lock) {
            if (listaPedidos != null) {
                for (PedidoFolio pedido : listaPedidos) {
                    if (!this.listaPedidos.contains(pedido)) {
                        this.listaPedidos.add(pedido);
                    }
                }
                guardarPedidos();
            }
        }
    }

    private void guardarPedidos() {

        synchronized (lock) {
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

    public int getUltimoFolioPedidos() {
        synchronized (lock) {
            return sharedPreferences.getInt("ultimoFolioPedidos", 0);
        }
    }

    public int getUltimoFolioCotizaciones() {
        synchronized (lock) {
            return sharedPreferences.getInt("ultimoFolioCotizaciones", 0);
        }
    }

    public void guardarUltimoFolioPedidos(int ultimoFolio) {
        synchronized (lock) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("ultimoFolioPedidos", ultimoFolio);
            editor.apply();
        }
    }

    public void guardarUltimoFolioCotizaciones(int ultimoFolio) {
        synchronized (lock) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("ultimoFolioCotizaciones", ultimoFolio);
            editor.apply();
        }
    }
}
