package com.brainstormideas.caballeroaztecaventas.data.callbacks;

import androidx.recyclerview.widget.DiffUtil;

import com.brainstormideas.caballeroaztecaventas.data.models.Producto;

import java.util.List;

public class ProductosDiffCallback extends DiffUtil.Callback {

    private final List<Producto> viejosProductos;
    private final List<Producto> nuevosProductos;

    public ProductosDiffCallback(List<Producto> viejosProductos, List<Producto> nuevosProductos) {
        this.viejosProductos = viejosProductos;
        this.nuevosProductos = nuevosProductos;
    }

    @Override
    public int getOldListSize() {
        return viejosProductos.size();
    }

    @Override
    public int getNewListSize() {
        return nuevosProductos.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return viejosProductos.get(oldItemPosition).getId() == nuevosProductos.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return viejosProductos.get(oldItemPosition).equals(nuevosProductos.get(newItemPosition));
    }
}
