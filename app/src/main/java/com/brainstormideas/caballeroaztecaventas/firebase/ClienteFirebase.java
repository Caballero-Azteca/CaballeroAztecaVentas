package com.brainstormideas.caballeroaztecaventas.firebase;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.brainstormideas.caballeroaztecaventas.data.models.Cliente;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ClienteFirebase {

    private static DatabaseReference databaseReference;

    public ClienteFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cliente");
    }

    // CREATE
    public void agregarCliente(Cliente cliente, OnCompleteListener listener) {
        new AgregarClienteAsyncTask(listener).execute(cliente);
    }

    // READ
    public void obtenerClientes(OnClientesObtenidosListener listener) {
        new ObtenerClientesAsyncTask((ObtenerClientesAsyncTask.OnClientesObtenidosListener) listener).execute();
    }

    // UPDATE
    public void actualizarCliente(String idCliente, Cliente cliente, OnCompleteListener listener) {
        new ActualizarClienteAsyncTask(listener).execute(idCliente, cliente);
    }

    // DELETE
    public void eliminarCliente(String idCliente, OnCompleteListener listener) {
        new EliminarClienteAsyncTask(listener).execute(idCliente);
    }

    // Interfaz para manejar la obtención de clientes
    public interface OnClientesObtenidosListener {
        void onClienteObtenido(Cliente cliente);
    }

    // Interfaz para manejar la finalización de operaciones
    public interface OnCompleteListener {
        void onTaskComplete();
    }

    // AsyncTask para agregar un cliente
    private static class AgregarClienteAsyncTask extends AsyncTask<Cliente, Void, Void> {
        private OnCompleteListener listener;

        AgregarClienteAsyncTask(OnCompleteListener listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Cliente... clientes) {
            String idCliente = databaseReference.push().getKey();
            assert idCliente != null;
            clientes[0].setId(Long.valueOf(idCliente));
            databaseReference.child(idCliente).setValue(clientes[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (listener != null) {
                listener.onTaskComplete();
            }
        }
    }

    // AsyncTask para obtener clientes
    public static class ObtenerClientesAsyncTask extends AsyncTask<Void, Void, List<Cliente>> {

        private final OnClientesObtenidosListener listener;

        ObtenerClientesAsyncTask(OnClientesObtenidosListener listener) {
            this.listener = listener;
        }

        @Override
        protected List<Cliente> doInBackground(Void... voids) {
            final List<Cliente> listaClientes = new ArrayList<>();

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listaClientes.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Cliente cliente = snapshot.getValue(Cliente.class);
                        if (cliente != null) {
                            listaClientes.add(cliente);
                        }
                    }

                    // Notificar al oyente una vez que se ha completado la lectura de la base de datos
                    if (listener != null) {
                        listener.onClientesObtenidos(listaClientes);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar error
                }
            });

            return listaClientes;
        }

        public interface OnClientesObtenidosListener {
            void onClientesObtenidos(List<Cliente> listaClientes);
        }
    }

    // AsyncTask para actualizar un cliente
    private static class ActualizarClienteAsyncTask extends AsyncTask<Object, Void, Void> {
        private final OnCompleteListener listener;

        ActualizarClienteAsyncTask(OnCompleteListener listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Object... params) {
            String idCliente = (String) params[0];
            Cliente cliente = (Cliente) params[1];
            databaseReference.child(idCliente).setValue(cliente);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (listener != null) {
                listener.onTaskComplete();
            }
        }
    }

    // AsyncTask para eliminar un cliente
    private static class EliminarClienteAsyncTask extends AsyncTask<String, Void, Void> {
        private final OnCompleteListener listener;

        EliminarClienteAsyncTask(OnCompleteListener listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(String... strings) {
            String idCliente = strings[0];
            databaseReference.child(idCliente).removeValue();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (listener != null) {
                listener.onTaskComplete();
            }
        }
    }
}

