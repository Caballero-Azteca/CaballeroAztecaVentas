package com.brainstormideas.caballeroaztecaventas.data.adapters;

import android.net.Uri;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class UriTypeAdapter extends TypeAdapter<Uri> {

    @Override
    public void write(JsonWriter out, Uri uri) throws IOException {
        if (uri == null) {
            out.nullValue();
        } else {
            out.value(uri.toString());
        }
    }

    @Override
    public Uri read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        } else {
            String uriString = in.nextString();
            return Uri.parse(uriString);
        }
    }
}