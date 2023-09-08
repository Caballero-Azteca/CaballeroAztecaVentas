package com.brainstormideas.caballeroaztecaventas.ui.adapters;

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

    private final List<Cobro> cobros;

    public CobroAdapter(List<Cobro> cobros) {
        this.cobros = cobros;
    }

    @NonNull
    @Override
    public CobroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cobro, parent, false);
        return new CobroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CobroViewHolder holder, int position) {
        Cobro cobro = cobros.get(position);

        holder.textFecha.setText("Fecha: " + cobro.getFechaEmision());
        holder.textFactura.setText("Factura: " + cobro.getFactura());
        holder.textImporte.setText("Importe: " + cobro.getImportePorPagar());
        holder.textNombreCliente.setText("Cliente: " + cobro.getNombreCliente());
    }

    @Override
    public int getItemCount() {
        return cobros.size();
    }

    public static class CobroViewHolder extends RecyclerView.ViewHolder {
        TextView textFecha;
        TextView textFactura;
        TextView textImporte;
        TextView textNombreCliente;

        public CobroViewHolder(@NonNull View itemView) {
            super(itemView);
            textFecha = itemView.findViewById(R.id.textFecha);
            textFactura = itemView.findViewById(R.id.textFactura);
            textImporte = itemView.findViewById(R.id.textImporte);
            textNombreCliente = itemView.findViewById(R.id.textNombreCliente);
        }
    }
}
