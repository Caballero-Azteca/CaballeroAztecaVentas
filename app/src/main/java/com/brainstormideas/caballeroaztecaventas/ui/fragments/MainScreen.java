package com.brainstormideas.caballeroaztecaventas.ui.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.data.models.Pedido;
import com.brainstormideas.caballeroaztecaventas.data.models.PedidoFolio;
import com.brainstormideas.caballeroaztecaventas.managers.PedidoManager;
import com.brainstormideas.caballeroaztecaventas.services.MailboxService;
import com.brainstormideas.caballeroaztecaventas.ui.Agregar_cliente;
import com.brainstormideas.caballeroaztecaventas.ui.Lista_clientes;
import com.brainstormideas.caballeroaztecaventas.ui.Lista_usuarios;
import com.brainstormideas.caballeroaztecaventas.ui.Login;
import com.brainstormideas.caballeroaztecaventas.ui.MailManager;
import com.brainstormideas.caballeroaztecaventas.ui.QrScanner;
import com.brainstormideas.caballeroaztecaventas.ui.Verificador_precio;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.FolioAdapter;
import com.brainstormideas.caballeroaztecaventas.utils.InternetManager;
import com.brainstormideas.caballeroaztecaventas.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainScreen extends Fragment {

    private Button pedido_btn;
    private Button cotizacion_btn;
    private Button bandeja_btn;
    private Button cobranza_btn;

    private Button verificador_precio;
    private Button exit_button;

    private FloatingActionButton mostrar_usuarios;
    private SessionManager session;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private TextView usuarioActual_txt;
    private TextView cnx_state;
    private InternetManager internetManager;

    private List<String> folios;
    private RecyclerView recyclerViewBandeja;
    private FolioAdapter adapter;

    private RelativeLayout bandeja_container;
    private Button volver_menu_btn;
    private FloatingActionButton refresh_float_btn;

    private PedidoManager pedidoManager;
    private static String PEDIDO = "pedido";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_screen, container, false);
        FirebaseApp.initializeApp(requireContext());

        Pedido.setObservaciones("");
        Pedido.setFolio("");
        Pedido.setListaDeProductos(new ArrayList<>());
        Pedido.setPreciosConIVA(true);
        Pedido.setTipo(PEDIDO);

        pedidoManager = PedidoManager.getInstance(getContext());

        folios = new ArrayList<>();

        // Configurar el RecyclerView
        recyclerViewBandeja = view.findViewById(R.id.bandeja_folios_rv);
        recyclerViewBandeja.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FolioAdapter(folios);
        recyclerViewBandeja.setAdapter(adapter);

        bandeja_container = view.findViewById(R.id.bandeja_container);
        volver_menu_btn = view.findViewById(R.id.volver_menu_btn);

        bandeja_container.setVisibility(View.GONE);

        session = new SessionManager(requireContext());
        session.checkLogin();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (!comprobarSesion()) {
            irALogin();
        }
        usuarioActual_txt = view.findViewById(R.id.rfc_etx);
        cnx_state = view.findViewById(R.id.cnx_state);
        cnx_state.setText("MODO: Online. 5.4");

        String usuarioActualTexto = "Usuario: " + session.getName();
        usuarioActual_txt.setText(usuarioActualTexto);

        internetManager = new InternetManager(requireContext());
        if (!internetManager.isInternetAvaible()) {
            internetNoDisponibleAviso();
        }

        mostrar_usuarios = view.findViewById(R.id.mostrar_usuario_btn);

        pedido_btn = view.findViewById(R.id.pedido_btn);
        cotizacion_btn = view.findViewById(R.id.cotizacion_btn);
        bandeja_btn = view.findViewById(R.id.bandeja_button);
        cobranza_btn = view.findViewById(R.id.cobranza_botton);
        refresh_float_btn = view.findViewById(R.id.refresh_float_btn);

        verificador_precio = view.findViewById(R.id.verificador_precio_button);
        exit_button = view.findViewById(R.id.exit_button);

        pedido_btn.setOnClickListener(v -> pedido(PEDIDO));

        cotizacion_btn.setOnClickListener(v -> pedido("cotizacion"));

        verificador_precio.setOnClickListener(view1 -> irVerificadorDePrecio());

        bandeja_btn.setOnClickListener(view12 -> irABandejaDeFolios());

        cobranza_btn.setOnClickListener(v -> irACobranza());

        refresh_float_btn.setOnClickListener(view13 -> {
            Intent intent = new Intent(getContext(), MailboxService.class);
            getContext().startService(intent);

            List<PedidoFolio> listaActualizadaPedidos = PedidoManager.getInstance(getContext()).getListaPedidos();
            if (listaActualizadaPedidos != null) {
                List<String> foliosString = new ArrayList<>();
                for (PedidoFolio pedido : listaActualizadaPedidos) {
                    foliosString.add(pedido.getFolio());
                }
                folios.clear();
                folios.addAll(foliosString);
                adapter.notifyDataSetChanged();
            } else {
                folios.clear();
                adapter.notifyDataSetChanged();
                getContext().stopService(intent);
            }
        });

        volver_menu_btn.setOnClickListener(view14 -> bandeja_container.setVisibility(View.GONE));

        mostrar_usuarios.setOnClickListener(v -> irAMostrarUsuarios());
        exit_button.setOnClickListener(view15 -> preguntarSiSalir());

        setHasOptionsMenu(true);

        return view;
    }

    private boolean comprobarSesion() {
        return session.getUsuario().equals("admin") || session.getEmail().equals(user.getEmail());
    }

    private void pedido(String tipoPedido) {

        String title = tipoPedido.equals(PEDIDO) ? "Pedido" : "Cotizacion";
        final String[] tiposCliente = new String[]{"Cliente existente", "Cliente express"};
        final int[] checkedItem = {-1};

        if(!pedidoManager.getListaPedidos().isEmpty() && internetManager.isInternetAvaible()){
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Buzon lleno");
            builder.setMessage("Ya posee conexion a internet y el buzon tiene aun pedidos en cola.");
            builder.setNeutralButton("Entendido", (dialogInterface, i) -> {});
            builder.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle(title);
            builder.setSingleChoiceItems(tiposCliente, checkedItem[0], (dialog, which) -> {
                checkedItem[0] = which;
                Pedido.setTipo(tipoPedido);
                switch (checkedItem[0]) {
                    case 0:
                        irMenuClienteExistente();
                        break;
                    case 1:
                        irMenuClienteNuevo();
                        break;
                    default:
                        irMenuClienteExistente();
                        break;
                }
            });
            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
            AlertDialog customAlertDialog = builder.create();
            customAlertDialog.show();
        }
    }

    private void preguntarSiSalir() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Salir");
        builder.setMessage("Desea salir?");
        builder.setPositiveButton("SI", (dialogInterface, i) -> {
            session.logoutUser();
            signOut();
        });
        builder.setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.show();
    }

    private void irVerificadorDePrecio() {
        Intent i = new Intent(requireContext(), Verificador_precio.class);
        startActivity(i);
    }

    private void irMenuClienteExistente() {
        Intent i = new Intent(requireContext(), Lista_clientes.class);
        startActivity(i);

    }

    private void irMenuClienteNuevo() {
        Intent i = new Intent(requireContext(), Lista_clientes.class);
        startActivity(i);
    }

    private void irAMostrarUsuarios() {
        Intent i = new Intent(requireContext(), Lista_usuarios.class);
        startActivity(i);
    }

    private void irABandejaDeFolios() {

        List<PedidoFolio> pedidoFolios = PedidoManager.getInstance(getContext()).getListaPedidos();

        Set<String> foliosSet = new HashSet<>(); // Utilizamos un conjunto para evitar duplicados

        for (PedidoFolio pedido : pedidoFolios) {
            foliosSet.add(pedido.getFolio());
        }

        folios.clear();
        folios.addAll(foliosSet);
        adapter.notifyDataSetChanged();

        bandeja_container.setVisibility(View.VISIBLE);
    }

    private void irACobranza(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        CobranzaScreen cobranzaFragment = new CobranzaScreen();
        transaction.replace(R.id.container, cobranzaFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void signOut() {
        mAuth.signOut();
        session.logoutUser();
    }

    private void irALogin() {
        Intent i = new Intent(requireContext(), Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void irAScanner() {
        Intent i = new Intent(requireContext(), QrScanner.class);
        startActivity(i);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.qrScanner:
                irAScanner();
                return true;
            case R.id.mailAdmin:
                irAAdministradorCorreos();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void irAAdministradorCorreos() {
        Intent i = new Intent(requireContext(), MailManager.class);
        startActivity(i);
    }

    public void internetNoDisponibleAviso() {
        cnx_state.setText("MODO: Offline. 5.4");
    }

}