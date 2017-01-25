import fichier.FichierR;
import fichier.FichierW;

import javax.swing.SwingUtilities;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/*
 This program is an database manager. This source file is main part of it
 Copyright (C) 2017  Vinsifroid ~ François Duchêne

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * Cette classe a les fonctions basiques de l'application
 * @author vinsifroid
 * @since v0.1
 */
public class CMB {
    private static String OS = null;
    private static FichierW fW = null;
    private static FichierR fR = null;
    private CMB(){}

    /*On n'essaie pas encore de faire une base donnée parfaite, on veut pour l'instant
    seulement pouvoir y accéder via ce programme.
    Pour ce qui est de la structure de la BD, on va utiliser un simple fichier texte.
     */
    public static void main(String [] args)
    {
        OS = getOsName();
        System.out.println("Central Movie Database - " + dateActuelle());
        createFichier();
        SwingUtilities.invokeLater(() -> new CMB_gui());
    }

    /**
     * Cette fonction cree le fichier "Films.bd"
     * @return true si le fichier est bien cree, false si le dossier n'existait pas, si il y a eu un problème ou si le fichier existait déjà
     */
    private static void createFichier()
    {
        // On regarde si le fichier existe

        File folder = new File(cheminBD());
        // On verifie que le dossier BaseDonnee existe
        if(!folder.exists() && !folder.isDirectory())
        {
            folder.mkdirs();
        }
        // On verifie que le fichier existe
        if (!fichierExiste(cheminBD(), "Films.bd")) {
            // Sinon on le crée
            if(FichierW.creerNouveauFichier(cheminBD()+inter()+"Films.bd")) {
                // On doit s'arranger pour que dans tous les cas, on ouvre le flux
                setfW(new FichierW(cheminBD()+inter()+"Films.bd"));
                getfW().ouvrirFuxWriter(true);
                System.out.println("Fichier cree");
                setfR(new FichierR(cheminBD()+inter()+"Films.bd"));
                getfR().ouvrirFluxReader();
            }else{
                System.exit(-1);
            }
        } else {
            setfW(new FichierW(cheminBD()+inter()+"Films.bd"));
            getfW().ouvrirFuxWriter(true);
            setfR(new FichierR(cheminBD()+inter()+"Films.bd"));
            getfR().ouvrirFluxReader();
        }
    }
    /*
        Fonctions pour réaliser la liste de films
     */
    public static void findFiles(File file1)
    {
        //TODO lister les fichiers dans l'ordre alphabétique pour pouvoir faire une recherche dichotomique par la suite
        //TODO ne pas réécrire les mêmes fichiers 2 fois
        File[] list = file1.listFiles();
        if(list!=null)
        {
            for(File file2 : list)
            {
                if (file2.isDirectory())
                {
                    findFiles(file2);
                }
                else if (file2.getName().contains(".avi")) {
                    getfW().ecrireString(file2.getPath()+file2.getName());
                }else if (file2.getName().contains(".mp4")) {
                    getfW().ecrireString(file2.getPath()+file2.getName());
                }else if (file2.getName().contains(".mkv")) {
                    getfW().ecrireString(file2.getPath()+file2.getName());
                }else if (file2.getName().contains(".mov")) {
                    getfW().ecrireString(file2.getPath()+file2.getName());
                }
            }
        }
    }
    /*
        Une bonne méthode de tri serait de trier dès qu'on ajoute un nouvel élément ;
        On pré-suppose que la liste est déjà trié dans l'ordre alphabétique (seuls les noms de fichiers sont triés
        et non pas le chemin complet + nom. On pourrait aller commencer par cibler l'endroit précis où on doit insérer
        le nom comme pour la recherche dichotomique.
     */
    private static void insertName(String filepath, String filename)
    {

    }

    /**
     *
     * @param listFichiers une liste de n fichiers
     * @param min l'indice minimal. Initialement min=0
     * @param max l'indice maximal. Initialement max = n-1
     * @return un tableau String[] trié
     */
    private static String[] mergeSort(String[] listFichiers, int min, int max)
    {
        //Cas de base
        if(min >= max)
            return listFichiers;
        final int mid = (min+max)/2;            // Indice du milieu
        mergeSort(listFichiers, min, mid);      // Tri première moitié
        mergeSort(listFichiers, mid+1, max);    // Tri seconde moitié
        merge(listFichiers, min, mid, max);     // Fusion
        return listFichiers;
    }

    /**
     *  Cette fonction trie le tableau A
     * @param A un tableau de la liste de fichiers avec [min...mid] et [mid+1...max]
     * @param min l'indice minimal
     * @param mid l'indice milieu
     * @param max l'indice maximal
     */
    private static void merge(String[] A, int min, int mid, int max)
    {
        String[] Aux = new String[A.length];
        for(int k=min;k<=max;k++)
        {
            Aux[k] = A[k];  // Copie dans un tableau auxiliaire
        }
        final int i=min;    // Debut de la première partie
        final int j=mid+1;  // Debut de la seconde partie
        for(int k=min;k<=max;k++)
        {
            if(i>mid){
                A[k] = Aux[j+1];
            }else if(j > max){
                A[k] = Aux[i+1];
            }else if(Aux[i].compareToIgnoreCase(Aux[j]) <= 0){ // plus petit dans la première partie
                A[k] = Aux[i+1];
            }else{                 // plus petit que la seconde partie
                A[k] = Aux[j+1];
            }
        }
    }

    /*
        Fonctions pour rechercher dans la liste de mots
     */
    public static List<String> searchWord(String motAChercher)
    {
        List<String> liste = new ArrayList<>();
        String tmp = null;
        final long taille = getfR().longueurFichier();
        // Ne surtout pas oublier d'en mettre un nouveau sinon
        // il pourrait commencer à lire à la fin du flux
        getfR().setNewBufferedReader();
        for(int i = 0; i < taille; i++)
        {
            tmp = getfR().lire();
            if(tmp.contains(motAChercher))
            {
                liste.add(tmp);
            }
        }
        return liste;
    }
    /**
     *
     * @param listFiles liste de n filenames (pas de chemin donc) triés par ordre croissant
     * @param filename la clé à chercher
     * @param min l'indice minimal. Initialement 0
     * @param max l'indice maximal. Initialement n-1
     * @return l'indice de la clé dans le tableau, -1 si absente
     */
    private static int BinarySearch(String[] listFiles,String filename,int min, int max)
    {
        if (min > max)
            return -1;
        final int middle = (min+max)/2;
        if(filename.equals(listFiles[middle]))
            return middle;
        if(filename.compareToIgnoreCase(listFiles[middle]) < 0){
            // On cherche dans la première moitiée
            return BinarySearch(listFiles, filename, min, middle-1);
        }else{
            // On cherche dans la seconde moitiée
            return BinarySearch(listFiles, filename, middle+1, max);
        }
    }
    /*
        Fonctions Usuelles
     */
    /**
     *
     * @param path le chemin du dossier dans lequel chercher
     * @param fileName le fichier a trouver
     * @return true si le fichier existe et false si il n'existe pas dans le dossier specifie
     */
    private static boolean fichierExiste(String path, String fileName)
    {
        File folder = new File(path);
        File[] files = folder.listFiles();
        if(files != null) {
            for (File fl : files) {
                if (fl.isFile() && fl.getName().equals(fileName)) {
                    return true;
                }
            }
        }
        return false;
    }
    public static String dateActuelle()
    {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.now();
        return dateFormat.format(localDate);
    }
    public static String cheminBD()
    {
        String path = null;
        final String homePath = System.getProperty("user.home");
        if(OS.contains("Windows")){
            path = homePath + "\\BaseDonnee";
        }else if (OS.contains("Linux")){
            path = homePath + "/BaseDonnee";
        }else{
            System.err.println("Error OS not supported");
            System.exit(-1);
        }
        return path;
    }
    public static String inter()
    {
        return File.separator;
    }
    private static String getOsName()
    {
        if(OS == null) { OS = System.getProperty("os.name"); }
        return OS;
    }

    public static FichierW getfW() {
        return fW;
    }

    public static void setfW(FichierW fW) {
        CMB.fW = fW;
    }

    public static FichierR getfR() {
        return fR;
    }

    public static void setfR(FichierR fR) {
        CMB.fR = fR;
    }
}