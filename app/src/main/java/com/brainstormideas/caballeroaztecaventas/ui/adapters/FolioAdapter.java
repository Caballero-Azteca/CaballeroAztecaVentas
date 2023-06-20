package com.brainstormideas.caballeroaztecaventas.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brainstormideas.caballeroaztecaventas.R;

import java.util.List;

public class FolioAdapter extends RecyclerView.Adapter<FolioAdapter.FolioViewHolder> {

    private final List<String> folios;

    public FolioAdapter(List<String> folios) {
        this.folios = folios;
    }

    @NonNull
    @Override
    public FolioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cola_folios, parent, false);
        return new FolioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolioViewHolder holder, int position) {
        String folio = folios.get(position);
        holder.textFolio.setText(folio);
    }

    @Override
    public int getItemCount() {
        return folios.size();
    }

    public static class FolioViewHolder extends RecyclerView.ViewHolder {
        TextView textFolio;

        public FolioViewHolder(@NonNull View itemView) {
            super(itemView);
            textFolio = itemView.findViewById(R.id.folio_txt);
        }
    }
}

