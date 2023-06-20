package com.brainstormideas.caballeroaztecaventas.ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.data.models.Pedido;
import com.brainstormideas.caballeroaztecaventas.entidad.ItemProductoPedido;
import com.brainstormideas.caballeroaztecaventas.utils.Tools;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecyclerViewProductosPedidoAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<ItemProductoPedido> listItems;
    private ImageButton btnEliminar;
    DatabaseReference dbProductosReferencia;

    public RecyclerViewProductosPedidoAdapter(Context context, ArrayList<ItemProductoPedido> listItems) {
        this.context = context;
        this.listItems = listItems;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.item_productos_pedidos, parent, false);
        return new Holder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemProductoPedido item = listItems.get(position);
        final Holder Holder = (Holder) holder;
        Holder.tvCantidad.setText(item.getCantidad());
        Holder.tvCodigo.setText(item.getId());
        Holder.tvProducto.setText(item.getNombre());
        Holder.tvPrecio.setText(item.getPrecio());
        Holder.tvTipo.setText(item.getTipo());

        Holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbProductosReferencia = FirebaseDatabase.getInstance().getReference().child("Usuario");
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Alerta de seguridad.");
                builder.setMessage("¿Seguro que desea eliminar el producto listado " + item.getNombre() + " ?");
                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (Tools.isNumeric(item.getId())) {
                            int id = Integer.parseInt(item.getId());
                            Query query = dbProductosReferencia.orderByChild("id").equalTo(id);
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        ds.getRef().removeValue();
                                        Pedido.getListaDeProductos().remove(position - 1);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {
                            Query query = dbProductosReferencia.orderByChild("id").equalTo(item.getId());
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        ds.getRef().removeValue();
                                        Pedido.getListaDeProductos().remove(position);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        listItems.remove(position);
                        notifyItemRemoved(position);
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        if (listItems != null) {
            return listItems.size();
        }
        return 0;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView tvCantidad;
        TextView tvCodigo;
        TextView tvProducto;
        TextView tvPrecio;
        TextView tvTipo;
        ImageButton btnEliminar;
        LinearLayout row_linearlayout;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tvCantidad = itemView.findViewById(R.id.cantidad_tv);
            tvCodigo = itemView.findViewById(R.id.codigo_tv);
            tvProducto = itemView.findViewById(R.id.descripcion_tv);
            tvPrecio = itemView.findViewById(R.id.precio_tv);
            tvTipo = itemView.findViewById(R.id.tipo_tv);
            btnEliminar = itemView.findViewById(R.id.eliminar_ibtn);
            row_linearlayout = itemView.findViewById(R.id.linearLayout_item);
        }
    }

    public void setFilter(ArrayList<ItemProductoPedido> listItems) {
        this.listItems = new ArrayList<>();
        this.listItems.addAll(listItems);
        notifyDataSetChanged();
    }
}
