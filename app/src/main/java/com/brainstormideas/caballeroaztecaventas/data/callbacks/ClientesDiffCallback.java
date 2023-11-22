package com.brainstormideas.caballeroaztecaventas.data.callbacks;

import androidx.recyclerview.widget.DiffUtil;

import com.brainstormideas.caballeroaztecaventas.data.models.Cliente;

import java.util.List;

public class ClientesDiffCallback extends DiffUtil.Callback{

    private final List<Cliente> viejosClientes;
    private final List<Cliente> nuevosClientes;

    public ClientesDiffCallback(List<Cliente> viejosClientes, List<Cliente> nuevosClientes) {
        this.viejosClientes = viejosClientes;
        this.nuevosClientes = nuevosClientes;
    }

    @Override
    public int getOldListSize() {
        return viejosClientes.size();
    }

    @Override
    public int getNewListSize() {
        return nuevosClientes.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return viejosClientes.get(oldItemPosition).getId() == nuevosClientes.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return viejosClientes.get(oldItemPosition).equals(nuevosClientes.get(newItemPosition));
    }
}
