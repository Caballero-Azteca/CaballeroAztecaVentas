package com.brainstormideas.caballeroaztecaventas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class PdfViewer extends AppCompatActivity {

    private PDFView pdfView;
    private File file;

    private Button atras_btn;

    boolean candadoModificar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        pdfView = findViewById(R.id.pdfview);

        getSupportActionBar().setTitle("PDF Generado");
        candadoModificar = getIntent().getExtras().getBoolean("candadoModificar", true);

        atras_btn = findViewById(R.id.atras_btn);
        atras_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            file = new File(bundle.getString("path", ""));
        }
        pdfView.fromFile(file).enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAntialiasing(true)
                .load();
    }

    public void volver() {
        Intent i = new Intent(getApplicationContext(), Menu_final.class);
        i.putExtra("tipoCliente", "cliente");
        i.putExtra("candadoModificar", candadoModificar);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}