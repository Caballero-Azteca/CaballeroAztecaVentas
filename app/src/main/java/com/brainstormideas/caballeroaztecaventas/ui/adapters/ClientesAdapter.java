package com.brainstormideas.caballeroaztecaventas.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.data.callbacks.ClientesDiffCallback;
import com.brainstormideas.caballeroaztecaventas.data.models.Cliente;

import java.util.List;

public class ClientesAdapter extends RecyclerView.Adapter<ClientesAdapter.ClienteViewHolder> implements View.OnClickListener {

    Context context;
    private List<Cliente> clientes;
    private OnItemClickListener listener;
    int rowIndex;

    public ClientesAdapter(Context context, List<Cliente> clientes) {
        this.context = context;
        this.clientes = clientes;
        rowIndex = -1;
    }

    @Override
    public void onClick(View view) {
    }

    public interface OnItemClickListener {
        void onItemClick(Cliente cliente);
    }

    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_clientes, parent, false);
        return new ClienteViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteViewHolder holder, int position) {
        Cliente cliente = clientes.get(position);
        holder.tvNombre.setText(cliente.getRazon());
        holder.tvCodigo.setText(cliente.getCode());
        holder.tvRfc.setText(cliente.getRfc());
        holder.bind(cliente);
        holder.rowLinearlayout.setOnClickListener(v -> {
            rowIndex = position;
            ControllerRecyclerViewAdapter.clienteSeleccionado = clientes.get(position);
            ControllerRecyclerViewAdapter.posicionCliente = position;
            notifyDataSetChanged();
        });

        if (rowIndex == position) {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.custom_border_selected);
            holder.rowLinearlayout.setBackground(drawable);
        } else {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.custom_border);
            holder.rowLinearlayout.setBackground(drawable);
        }
    }

    @Override
    public int getItemCount() {
        return clientes.size();
    }

    public static class ClienteViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvCodigo;
        TextView tvRfc;
        LinearLayout rowLinearlayout;

        private final ClientesAdapter.OnItemClickListener listener;

        public void bind(final Cliente cliente) {
            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onItemClick(cliente);
                }
            });
        }

        public ClienteViewHolder(@NonNull View itemView, ClientesAdapter.OnItemClickListener listener) {
            super(itemView);
            tvCodigo = itemView.findViewById(R.id.codigo_etx);
            tvNombre = itemView.findViewById(R.id.nombre_etx);
            tvRfc = itemView.findViewById(R.id.rfc_etx);
            rowLinearlayout = itemView.findViewById(R.id.linearLayout_item);

            this.listener = listener;
        }
    }

    public void setClientes(List<Cliente> nuevosClientes) {
        ClientesDiffCallback diffCallback = new ClientesDiffCallback(clientes, nuevosClientes);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        clientes = nuevosClientes;
        diffResult.dispatchUpdatesTo(this);
    }
}
