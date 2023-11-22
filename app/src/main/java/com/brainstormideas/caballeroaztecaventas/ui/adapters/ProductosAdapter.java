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
import com.brainstormideas.caballeroaztecaventas.data.callbacks.ProductosDiffCallback;
import com.brainstormideas.caballeroaztecaventas.data.models.Producto;

import java.util.List;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder> implements View.OnClickListener {

    Context context;
    private List<Producto> productos;
    private OnItemClickListener listener;
    int rowIndex;

    public ProductosAdapter(Context context, List<Producto> productos) {
        this.context = context;
        this.productos = productos;
        rowIndex = -1;
    }

    @Override
    public void onClick(View view) {

    }


    public interface OnItemClickListener {
        void onItemClick(Producto producto);
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        int adapterPosition = holder.getAdapterPosition();
        Producto producto = productos.get(adapterPosition);
        holder.textCodigo.setText(producto.getCode());
        holder.textDescripcion.setText(producto.getNombre());
        holder.textMarca.setText(producto.getMarca());
        holder.bind(producto);
        holder.rowLinearlayout.setOnClickListener(v -> {
            rowIndex = position;
            ControllerRecyclerViewAdapter.productoSeleccionado = productos.get(position);
            ControllerRecyclerViewAdapter.posicionProducto = position;
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
        return productos.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {

        TextView textCodigo;
        TextView textDescripcion;
        TextView textMarca;
        LinearLayout rowLinearlayout;


        private final OnItemClickListener listener;

        public void bind(final Producto producto) {
            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onItemClick(producto);
                }
            });
        }

        public ProductoViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            textCodigo = itemView.findViewById(R.id.textCodigo);
            textDescripcion = itemView.findViewById(R.id.textDescripcion);
            textMarca = itemView.findViewById(R.id.textMarca);
            rowLinearlayout = itemView.findViewById(R.id.linearLayout_item);

            this.listener = listener;
        }
    }

    public void setProductos(List<Producto> nuevosProductos) {
        ProductosDiffCallback diffCallback = new ProductosDiffCallback(productos, nuevosProductos);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        productos = nuevosProductos;
        diffResult.dispatchUpdatesTo(this);
    }
}

