package com.brainstormideas.caballeroaztecaventas.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.data.models.Cliente;

import java.util.List;

public class ClienteConCobroAdapter extends RecyclerView.Adapter<ClienteConCobroAdapter.ClienteConCobroViewHolder> {

    private List<Cliente> clientesConCobros;
    private OnItemClickListener listener;

    public ClienteConCobroAdapter(List<Cliente> clientesConCobros) {
        this.clientesConCobros = clientesConCobros;
    }

    public interface OnItemClickListener {
        void onItemClick(Cliente cliente);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClienteConCobroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cliente_con_cobro, parent, false);
        return new ClienteConCobroViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteConCobroViewHolder holder, int position) {
        Cliente cliente = clientesConCobros.get(position);
        holder.textCodigo.setText(cliente.getCode());
        holder.textCliente.setText(cliente.getRazon());
        holder.bind(cliente);
    }

    @Override
    public int getItemCount() {
        return clientesConCobros.size();
    }

    public static class ClienteConCobroViewHolder extends RecyclerView.ViewHolder {
        TextView textCodigo;
        TextView textCliente;
        private OnItemClickListener listener;

        public void bind(final Cliente cliente) {
            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onItemClick(cliente);
                }
            });
        }

        public ClienteConCobroViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            textCodigo = itemView.findViewById(R.id.textCodigo);
            textCliente = itemView.findViewById(R.id.textCliente);
            this.listener = listener;
        }
    }

    public void setClientesConCobros(List<Cliente> nuevosClientesConCobros) {
        this.clientesConCobros = nuevosClientesConCobros;
        notifyDataSetChanged();
    }
}