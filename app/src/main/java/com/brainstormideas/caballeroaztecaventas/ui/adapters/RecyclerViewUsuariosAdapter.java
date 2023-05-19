package com.brainstormideas.caballeroaztecaventas.ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.brainstormideas.caballeroaztecaventas.ui.Lista_usuarios;
import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.entidad.ItemUsuario;
import com.brainstormideas.caballeroaztecaventas.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RecyclerViewUsuariosAdapter extends RecyclerView.Adapter {

    Context context;
    final ArrayList<ItemUsuario> listItems;
    DatabaseReference dbUsuariosReferencia;
    String nombre;
    String usuario;
    FirebaseUser user;
    FirebaseAuth mAuth;
    SessionManager sessionManager;
    String nombreDeUsuario;

    View viewInflated;
    EditText nombre_txt;
    EditText usuario_txt;

    public RecyclerViewUsuariosAdapter(Context context, ArrayList<ItemUsuario> listItems) {
        this.context = context;
        this.listItems = listItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.item_lista_usuarios, parent, false);
        return new HolderUsuario(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderUsuario, final int position) {

        final ItemUsuario itemUsuario = listItems.get(position);
        final HolderUsuario HolderUsuario = (HolderUsuario) holderUsuario;
        HolderUsuario.nombre.setText(itemUsuario.getNombre());
        HolderUsuario.id.setText(itemUsuario.getUsuario());
        sessionManager = new SessionManager(context);
        nombreDeUsuario = sessionManager.getUsuario();
        mAuth = FirebaseAuth.getInstance();

        HolderUsuario.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbUsuariosReferencia = FirebaseDatabase.getInstance().getReference().child("Usuario");
                if (nombreDeUsuario.equals("admin")) {

                    viewInflated = LayoutInflater.from(context).inflate(R.layout.editar_item, null);

                    nombre_txt = viewInflated.findViewById(R.id.editar_nombre_txt);
                    usuario_txt = viewInflated.findViewById(R.id.editar_usuario_txt);

                    Query query = dbUsuariosReferencia.orderByChild("nombre").equalTo(itemUsuario.getNombre());
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                nombre = Objects.requireNonNull(ds.child("nombre").getValue()).toString();
                                usuario = Objects.requireNonNull(ds.child("usuario").getValue()).toString();
                                nombre_txt.setText(nombre);
                                usuario_txt.setText(usuario);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Editar usuario.");
                    builder.setMessage("Edite la informacion del usuario.");

                    builder.setView(viewInflated);

                    builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String nombreEditado = nombre_txt.getText().toString().trim();
                            String usuarioEditado = usuario_txt.getText().toString().trim();

                            listItems.get(position).setNombre(nombreEditado);
                            listItems.get(position).setUsuario(usuarioEditado);

                            mAuth.signInWithEmailAndPassword(itemUsuario.getEmail(), itemUsuario.getPass());
                            user = mAuth.getCurrentUser();
                            assert user != null;


                            Map<String, Object> vendedor = new HashMap<>();
                            vendedor.put("nombre", nombreEditado);
                            vendedor.put("usuario", usuarioEditado);
                            dbUsuariosReferencia.child(user.getUid()).updateChildren(vendedor);

                            Intent i = new Intent(context, Lista_usuarios.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            context.startActivity(i);

                            Toast.makeText(context, "Usuario editado con exito.", Toast.LENGTH_LONG).show();

                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    Toast.makeText(context, "Usted no es administrador del sistema.", Toast.LENGTH_LONG).show();
                }

            }
        });

        HolderUsuario.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth.signInWithEmailAndPassword(itemUsuario.getEmail(), itemUsuario.getPass());
                user = mAuth.getCurrentUser();

                dbUsuariosReferencia = FirebaseDatabase.getInstance().getReference().child("Usuario");
                if (nombreDeUsuario.equals("admin")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Alerta de seguridad.");
                    builder.setMessage("¿Seguro que desea eliminar al usuario " + itemUsuario.getNombre() + "?");
                    builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Query query = dbUsuariosReferencia.orderByChild("nombre").equalTo(itemUsuario.getNombre());
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        ds.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            assert user != null;
                            user.delete();

                            Intent i = new Intent(context, Lista_usuarios.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            context.startActivity(i);

                            Toast.makeText(context, "Usuario eliminado con éxito.", Toast.LENGTH_LONG).show();

                        }

                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {

                    Toast.makeText(context, "Usted no es administrador del sistema.", Toast.LENGTH_LONG).show();
                }

            }

        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public static class HolderUsuario extends RecyclerView.ViewHolder {

        TextView nombre;
        TextView id;
        ImageButton btnEditar;
        ImageButton btnEliminar;

        public HolderUsuario(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.nombre_usuario_item);
            id = itemView.findViewById(R.id.id_usuario_item);
            btnEditar = itemView.findViewById(R.id.edit_list_item_btn);
            btnEliminar = itemView.findViewById(R.id.delete_list_item_btn);
        }
    }

}
