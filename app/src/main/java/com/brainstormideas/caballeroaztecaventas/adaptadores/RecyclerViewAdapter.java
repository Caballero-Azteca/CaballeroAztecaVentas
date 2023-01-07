package com.brainstormideas.caballeroaztecaventas.adaptadores;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.entidad.Item;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    Context context;
    ArrayList<Item> listItems;
    private View.OnClickListener listener;
    int row_index;

    public RecyclerViewAdapter(Context context, ArrayList<Item> listItems) {
        this.context = context;
        this.listItems = listItems;
        row_index = -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.item_lista_clientes, parent, false);
        contentView.setOnClickListener(this);
        return new Holder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        Item item = listItems.get(position);
        final Holder Holder = (Holder) holder;
        Holder.ivId.setText(item.getId());
        Holder.tvTitulo.setText(item.getTitulo());
        Holder.tvCodigo.setText(item.getDato1());
        Holder.row_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index = position;
                ControllerRecyclerViewAdapter.itemSeleccionado = listItems.get(position);
                ControllerRecyclerViewAdapter.posicion = position;
                notifyDataSetChanged();
            }
        });

        if (row_index == position) {
            Drawable drawable = context.getResources().getDrawable(R.drawable.custom_border_selected);
            Holder.row_linearlayout.setBackground(drawable);
        } else {
            Drawable drawable = context.getResources().getDrawable(R.drawable.custom_border);
            Holder.row_linearlayout.setBackground(drawable);
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView ivId;
        TextView tvTitulo;
        TextView tvCodigo;
        LinearLayout row_linearlayout;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ivId = itemView.findViewById(R.id.codigo_etx);
            tvTitulo = itemView.findViewById(R.id.nombre_etx);
            tvCodigo = itemView.findViewById(R.id.rfc_etx);
            row_linearlayout = (LinearLayout) itemView.findViewById(R.id.linearLayout_item);
        }
    }

    public void setFilter(ArrayList<Item> listItems) {
        this.listItems = new ArrayList<>();
        this.listItems.addAll(listItems);
        notifyDataSetChanged();
    }

}
