package com.brainstormideas.caballeroaztecaventas.ui.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FacturaSpinnerAdapter extends ArrayAdapter<String> {

    private Set<String> facturasSet;

    public FacturaSpinnerAdapter(Context context, int resource) {
        super(context, resource);
        facturasSet = new HashSet<>();
    }

    public void actualizarFacturas(List<String> nuevasFacturas) {
        facturasSet.clear();
        facturasSet.addAll(nuevasFacturas);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return facturasSet.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        String[] array = facturasSet.toArray(new String[0]);
        return array[position];
    }
}


