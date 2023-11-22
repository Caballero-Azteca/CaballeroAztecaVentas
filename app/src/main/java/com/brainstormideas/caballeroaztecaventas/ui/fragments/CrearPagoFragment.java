package com.brainstormideas.caballeroaztecaventas.ui.fragments;

import static com.brainstormideas.caballeroaztecaventas.data.models.TipoPago.CHEQUE;
import static com.brainstormideas.caballeroaztecaventas.data.models.TipoPago.EFECTIVO;
import static com.brainstormideas.caballeroaztecaventas.data.models.TipoPago.TRANSFERENCIA;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.data.models.Cliente;
import com.brainstormideas.caballeroaztecaventas.data.models.Cobro;
import com.brainstormideas.caballeroaztecaventas.data.models.Pago;
import com.brainstormideas.caballeroaztecaventas.data.models.PagoCheque;
import com.brainstormideas.caballeroaztecaventas.data.models.PagoEfectivo;
import com.brainstormideas.caballeroaztecaventas.data.models.PagoTransferencia;
import com.brainstormideas.caballeroaztecaventas.data.models.Pedido;
import com.brainstormideas.caballeroaztecaventas.data.models.TipoPago;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.FacturaSpinnerAdapter;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.PagosAdapter;
import com.brainstormideas.caballeroaztecaventas.ui.viewmodels.CobroViewModel;
import com.brainstormideas.caballeroaztecaventas.ui.viewmodels.PagoViewModel;

import org.apache.poi.xssf.util.NumericRanges;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CrearPagoFragment extends DialogFragment {

    private Spinner spinnerFactura;
    private EditText editTextImporte;
    private EditText editTextBanco;
    private EditText editTextNumeroCheque;
    private EditText editTextNumeroTranferencia;
    private Spinner spinnerTipoPago;
    private Button buttonAgregar;
    private Button buttonSubir;

    private CobroViewModel cobroViewModel;
    private PagoViewModel pagoViewModel;

    private Pago pago;
    private FacturaSpinnerAdapter facturaAdapter;
    private ArrayList<String> facturas = new ArrayList<>();

    String tipoPagoSeleccionado;
    String facturaSeleccionada;
    private  Cliente cliente;
    private ArrayList<Pago> pagos = new ArrayList<>();
    private String idUnico;

    private PagosAdapter pagosAdapter;
    ArrayList<Cobro> cobrosClienteEspecifico = new ArrayList<>();
    private HashMap<String, Double> importesCobros = new HashMap<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crear_pago, container, false);

        Bundle args = getArguments();
        if (args != null) {
            cliente = (Cliente) args.getSerializable("cliente");
        }

        spinnerFactura = view.findViewById(R.id.spinnerFactura);
        editTextImporte = view.findViewById(R.id.editTextImporte);
        editTextBanco = view.findViewById(R.id.editTextBanco);
        editTextNumeroCheque = view.findViewById(R.id.editTextNumeroCheque);
        editTextNumeroTranferencia = view.findViewById(R.id.editTextNumeroTranferencia);

        spinnerTipoPago = view.findViewById(R.id.spinnerTipoPago);
        spinnerFactura = view.findViewById(R.id.spinnerFactura);
        buttonAgregar = view.findViewById(R.id.buttonAgregar);
        buttonSubir = view.findViewById(R.id.buttonSubir);

        cobroViewModel = new CobroViewModel(getContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, TipoPago.getTiposDePago());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoPago.setAdapter(adapter);

        pagoViewModel = new PagoViewModel(getContext());

        cargarFacturas();
        obtenerIdUnico();

        facturaAdapter = new FacturaSpinnerAdapter(requireContext(), android.R.layout.simple_spinner_item);
        facturaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFactura.setAdapter(facturaAdapter);

        spinnerTipoPago.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                tipoPagoSeleccionado = (String) parentView.getItemAtPosition(position);

                editTextNumeroCheque.setVisibility(View.GONE);
                editTextNumeroTranferencia.setVisibility(View.GONE);

                if (tipoPagoSeleccionado.equals(CHEQUE)) {
                    editTextNumeroCheque.setVisibility(View.VISIBLE);
                    editTextBanco.setVisibility(View.VISIBLE);
                } else if (tipoPagoSeleccionado.equals(TRANSFERENCIA)) {
                    editTextNumeroTranferencia.setVisibility(View.VISIBLE);
                    editTextBanco.setVisibility(View.VISIBLE);
                } else {
                    editTextNumeroCheque.setVisibility(View.GONE);
                    editTextNumeroTranferencia.setVisibility(View.GONE);
                    editTextBanco.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });


        spinnerFactura.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                facturaSeleccionada = (String) adapterView.getItemAtPosition(i);
                Double montoAPagar = importesCobros.get(facturaSeleccionada);

                if (montoAPagar != null) {
                    editTextImporte.setText(String.valueOf(montoAPagar));
                } else {
                    editTextImporte.setText("0");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.pagos_agregados_rv);
        pagosAdapter = new PagosAdapter(pagos);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(pagosAdapter);

        buttonAgregar.setOnClickListener(view1 -> {
            insertarPago();
        });

        buttonSubir.setOnClickListener(view12 -> {
            subirPagos();
        });


        return view;
    }

    private void subirPagos(){
        if(!pagos.isEmpty()){
            try {
                for (Pago pago : pagos) {
                    pagoViewModel.insertPago(pago);
                }
                pagar();
                limpiarCampos();
                Toast.makeText(requireContext(), "Pagos agregados", Toast.LENGTH_SHORT).show();
                this.dismiss();
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error al agregar los pagos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(requireContext(), "Agrega pagos primero", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertarPago() {

        if(rellenarPago() != null){
            pagos.add(rellenarPago());
            pagosAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getContext(), "No es posible agregar este pago", Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarCampos() {
        editTextImporte.setText("");
        editTextBanco.setText("");
        editTextNumeroCheque.setText("");
        editTextNumeroTranferencia.setText("");
    }

    private Pago rellenarPago() {

        String factura = facturaSeleccionada;
        Double montoAPagar = importesCobros.get(facturaSeleccionada);
        String banco = editTextBanco.getText().toString();
        String importeStr = editTextImporte.getText().toString().trim();
        String numeroCheque = editTextNumeroCheque.getText().toString();
        String numeroTransferencia = editTextNumeroTranferencia.getText().toString();

        LocalDate fechaActual = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String fechaFormateada = fechaActual.format(formatter);

        if (importeStr.isEmpty()) {
            Toast.makeText(requireContext(), "El importe no puede estar vacío", Toast.LENGTH_SHORT).show();
            return null;
        }

        if((tipoPagoSeleccionado.equals(CHEQUE) || tipoPagoSeleccionado.equals(TRANSFERENCIA)) && banco.isEmpty()){
            Toast.makeText(getContext(), "Debe agregar el nombre del banco", Toast.LENGTH_SHORT).show();
            return null;
        }

        double importe;
        try {
            NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
            Number number = numberFormat.parse(importeStr);
            importe = number.doubleValue();
        } catch (ParseException | NumberFormatException e) {
            Toast.makeText(requireContext(), "Importe inválido", Toast.LENGTH_SHORT).show();
            return null;
        }

        if (importe>montoAPagar) {
            Toast.makeText(requireContext(), "Importe es mayor al necesario", Toast.LENGTH_SHORT).show();
            return null;
        }

        switch (tipoPagoSeleccionado) {
            case EFECTIVO:
                PagoEfectivo pagoEfectivo = new PagoEfectivo();
                pagoEfectivo.setFactura(factura);
                pagoEfectivo.setImporte(importe);
                pagoEfectivo.setFecha(fechaFormateada);
                pagoEfectivo.setTipoPago(TipoPago.EFECTIVO);
                pagoEfectivo.setIdUnico(idUnico);
                return pagoEfectivo;
            case CHEQUE:
                PagoCheque pagoCheque = new PagoCheque();
                pagoCheque.setFactura(factura);
                pagoCheque.setImporte(importe);
                pagoCheque.setFecha(fechaFormateada);
                pagoCheque.setBanco(banco);
                pagoCheque.setNumeroCheque(numeroCheque);
                pagoCheque.setTipoPago(TipoPago.CHEQUE);
                pagoCheque.setIdUnico(idUnico);
                return pagoCheque;
            case TRANSFERENCIA:
                PagoTransferencia pagoTransferencia = new PagoTransferencia();
                pagoTransferencia.setFactura(factura);
                pagoTransferencia.setImporte(importe);
                pagoTransferencia.setFecha(fechaFormateada);
                pagoTransferencia.setBanco(banco);
                pagoTransferencia.setNumeroTransferencia(numeroTransferencia);
                pagoTransferencia.setTipoPago(TipoPago.TRANSFERENCIA);
                pagoTransferencia.setIdUnico(idUnico);
                return pagoTransferencia;
            default:
                return null;
        }
    }

    private void cargarFacturas() {
        cobroViewModel.getCobros().observe(this, cobrosNuevos -> {
            ArrayList<String> cobrosClienteEspecificoFactura = new ArrayList<>();
            importesCobros.clear();
            for (Cobro cobro : cobrosNuevos) {
                if (cobro.getCodigoCliente() != null && cobro.getCodigoCliente().equals(cliente.getCode())) {
                    cobrosClienteEspecifico.add(cobro);
                    cobrosClienteEspecificoFactura.add(cobro.getFactura());
                    importesCobros.put(cobro.getFactura(), cobro.getImportePorPagar());
                }
            }
            facturas.clear();
            facturaAdapter.clear();
            facturas.addAll(cobrosClienteEspecificoFactura);
            facturaAdapter.actualizarFacturas(facturas);
        });
    }

    private void obtenerIdUnico() {
        UUID nuevoUuid = UUID.randomUUID();
        String idUnicoGenera = String.valueOf(nuevoUuid).substring(19);
        idUnico = idUnicoGenera;
    }

    private void pagar(){

        for (Cobro cobro : cobrosClienteEspecifico) {

            String facturaCobro = cobro.getFactura();
            for (Pago pago : pagos) {
                String facturaPago = pago.getFactura();

                if (facturaCobro.equals(facturaPago)) {
                    actualizarFactura(cobro, pago.getImporte());
                }
            }
        }
    }

    public void actualizarFactura(Cobro cobro, double nuevoImporte) {
        // Obtén el importe actual del cobro
        double importeCobro = cobro.getImporteFactura();

        // Verifica si la resta resulta en un importe no negativo
        if (importeCobro >= nuevoImporte) {
            // Calcula el nuevo importe del cobro restando el nuevoImporte
            double nuevoImporteCobro = importeCobro - nuevoImporte;

            // Calcula el nuevo importe por pagar
            double importePorPagar = nuevoImporteCobro - cobro.getAbono();

            // Calcula el nuevo saldo
            double saldo = cobro.getImporteFactura() - importePorPagar;

            double abono = cobro.getAbono() + nuevoImporte;

            // Validar que las cantidades no sean negativas
            if (nuevoImporteCobro >= 0 && importePorPagar >= 0 && saldo >= 0) {

                cobro.setImporteFactura(nuevoImporteCobro);
                cobro.setImportePorPagar(importePorPagar);
                cobro.setSaldo(saldo);
                cobro.setAbono(abono);

                // Actualiza el cobro utilizando el ViewModel
                cobroViewModel.updateCobro(cobro);
            } else {
                Toast.makeText(getContext(), "Error: Los importes no pueden ser negativos", Toast.LENGTH_SHORT).show();
                throw new RuntimeException("El nuevo importe no puede ser negativo");
            }
        } else {
            Toast.makeText(getContext(), "El pago excede el monto del importe", Toast.LENGTH_SHORT).show();
            throw new RuntimeException("El nuevo importe no puede ser negativo");
        }
    }
}