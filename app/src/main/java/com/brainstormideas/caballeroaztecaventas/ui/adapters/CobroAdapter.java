package com.brainstormideas.caballeroaztecaventas.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.data.models.Cobro;

import java.util.List;

public class CobroAdapter extends RecyclerView.Adapter<CobroAdapter.CobroViewHolder> {

    private List<Cobro> cobros;
    private OnItemClickListener listener;

    public CobroAdapter(List<Cobro> cobros) {
        this.cobros = cobros;
    }

    public interface OnItemClickListener {
        void onItemClick(Cobro cobro);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CobroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cobro, parent, false);
        return new CobroViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CobroViewHolder holder, int position) {
        Cobro cobro = cobros.get(position);
        holder.textFecha.setText(cobro.getFechaEmision());
        holder.textFactura.setText(cobro.getFactura());
        holder.textImporte.setText(""+cobro.getImportePorPagar());
        if(cobro.isVencidas()){
            holder.textVencida.setText("Vencida");
        } else {
            holder.textVencida.setText("No vencida");
        }
        holder.bind(cobro);
    }

    @Override
    public int getItemCount() {
        return cobros.size();
    }

    public static class CobroViewHolder extends RecyclerView.ViewHolder {
        TextView textFecha;
        TextView textFactura;
        TextView textImporte;
        TextView textVencida;
        private OnItemClickListener listener;

        public void bind(final Cobro cobro) {
            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onItemClick(cobro);
                }
            });
        }

        public CobroViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            textFecha = itemView.findViewById(R.id.textFecha);
            textFactura = itemView.findViewById(R.id.textFactura);
            textImporte = itemView.findViewById(R.id.textImporte);
            textVencida = itemView.findViewById(R.id.textVencida);
            this.listener = listener;
        }
    }

    public void setCobros(List<Cobro> nuevosCobros) {
        this.cobros = nuevosCobros;
        notifyDataSetChanged();
    }
}
