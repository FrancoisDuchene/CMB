package main;

import fichier.FichierR;

import java.util.ArrayList;
import java.util.List;

class Analyse {
    static String filtreExtension(String mot) {
        final int indice = mot.lastIndexOf(".");
        if(indice != -1)
            return mot.substring(0, indice);
        return mot;
    }

    //TODO way to make performances better: do a contain method that will look at every character of the word once
    private static String[] analyseMovieFileName(String name) {
        final String[] filtres = new String[2];
        filtres[0] = "FRENCH"; filtres[1] = "VOSTFR";
        List<String> valeurs = new ArrayList<>();

        for(int i=0;i<filtres.length;i++) {
            if(name.contains(filtres[i])) {
                valeurs.add(filtres[i]);
            }
        }
        final String rep = hasYear(name);
        if(rep != null) {
            valeurs.add(rep);
        }
        return valeurs.toArray(new String[valeurs.size()]);
    }

    private static String hasYear(String name) {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<name.length();i++) {
            char c = name.charAt(i);
            if(Character.isDigit(c)) {
                sb.append(Character.digit(c,10));
            }
        }
        if(sb.length() == 4) {
            return sb.toString();
        }else {
            return null;
        }
    }
}
