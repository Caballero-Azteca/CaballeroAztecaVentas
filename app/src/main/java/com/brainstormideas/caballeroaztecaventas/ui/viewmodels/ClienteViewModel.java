package com.brainstormideas.caballeroaztecaventas.ui.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.brainstormideas.caballeroaztecaventas.data.models.Cliente;
import com.brainstormideas.caballeroaztecaventas.data.repository.ClienteRepository;

import java.util.List;

public class ClienteViewModel {

    private final ClienteRepository clienteRepository;
    private final LiveData<List<Cliente>> clientes;

    public ClienteViewModel(Context context) {
        clienteRepository = new ClienteRepository(context);
        clientes = clienteRepository.getAllClientes();
    }

    public LiveData<List<Cliente>> getClientes() {
        return clientes;
    }

    public LiveData<Cliente> getCliente(String clienteId) {
        return clienteRepository.getCliente(clienteId);
    }

    public void insertProducto(Cliente cliente) {
        clienteRepository.insertCliente(cliente);
    }
}
