package com.brainstormideas.caballeroaztecaventas.utils;

public class Tools {

    public static boolean isNumeric(String cadena) {
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
