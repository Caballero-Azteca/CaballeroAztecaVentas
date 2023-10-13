package com.brainstormideas.caballeroaztecaventas.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.data.models.Pago;

import java.util.List;


public class PagosAdapter extends RecyclerView.Adapter<PagosAdapter.PagoViewHolder> {

    private List<Pago> pagos;

    public PagosAdapter(List<Pago> pagos) {
        this.pagos = pagos;
    }

    @NonNull
    @Override
    public PagoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pago, parent, false);
        return new PagoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PagoViewHolder holder, int position) {
        Pago pago = pagos.get(position);
        holder.idUnico.setText("ID: " + pago.getIdUnico());
        holder.factura.setText("Factura: " + pago.getFactura());
        holder.importe.setText("Importe: " + String.valueOf(pago.getImporte()));
        holder.fecha.setText("Fecha: " + pago.getFecha());
        holder.banco.setText("Banco: " + pago.getBanco());
        holder.tipoPago.setText("Tipo de Pago: " + pago.getTipoPago());
    }

    @Override
    public int getItemCount() {
        return pagos.size();
    }

    public static class PagoViewHolder extends RecyclerView.ViewHolder {
        TextView idUnico;
        TextView factura;
        TextView importe;
        TextView fecha;
        TextView banco;
        TextView tipoPago;

        public PagoViewHolder(@NonNull View itemView) {
            super(itemView);
            idUnico = itemView.findViewById(R.id.textIdUnico);
            factura = itemView.findViewById(R.id.textFactura);
            importe = itemView.findViewById(R.id.textImporte);
            fecha = itemView.findViewById(R.id.textFecha);
            banco = itemView.findViewById(R.id.textBanco);
            tipoPago = itemView.findViewById(R.id.textTipoPago);
        }
    }

    public void setPagos(List<Pago> nuevosPagos) {
        this.pagos = nuevosPagos;
        notifyDataSetChanged();
    }
}
