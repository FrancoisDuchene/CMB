import fichier.FichierR;
import fichier.FichierW;

import javax.swing.SwingUtilities;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

/*
 This program is an database manager. This source file is main part of it
 Central Movie Base, CMB for short, version is currently : 0.3
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
    private static FichierW fW = null;
    private static FichierR fR = null;
    private static Connection conn = null;
    private static final boolean debug = true;
    private CMB(){}

    /*On n'essaie pas encore de faire une base donnée parfaite, on veut pour l'instant
    seulement pouvoir y accéder via ce programme.
    Pour ce qui est de la structure de la BD, on va utiliser un simple fichier texte.
    EDIT : en train de migrer vers SQLite
     */
    public static void main(String [] args)
    {
        System.out.println("Central Movie Database - " + dateActuelle());
        if(!createFolder()){
            System.err.println("Error when creating the file");
            System.exit(1);
        }
        final String filename = cheminBD() + inter() + "Films.db";
        SqliteManager app = new SqliteManager(filename);
        app.closeDB();
        /*
        SwingUtilities.invokeLater(CMB_gui::new);
        */
    }
    private static boolean createFolder() {
        File folder = new File(cheminBD());
        // On verifie que le dossier BaseDonnee existe
        if(!folder.exists() && !folder.isDirectory())
        {
            if(!folder.mkdirs())
                return false;
        }
        return true;
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
                else if (file2.getName().endsWith("avi")) {
                    final String nom = file2.getPath();
                    getfW().ecrireString(nom);
                }else if (file2.getName().endsWith("mp4")) {
                    final String nom = file2.getPath();
                    getfW().ecrireString(nom);
                }else if (file2.getName().endsWith("mkv")) {
                    final String nom = file2.getPath();
                    getfW().ecrireString(nom);
                }else if (file2.getName().endsWith("mov")) {
                    final String nom = file2.getPath();
                    getfW().ecrireString(nom);
                }
            }
            //On force l'écriture pour être sûr que c'est écrit dans le fichier
            getfW().forcerEcriture();

            //D'abord on obtient la liste des noms contenus dans la liste
            //TODO trouver une autre façon de procéder moins consomatrice en ressource
            String[] listeFichiers = donnerListeFichiers();
            //TODO corriger le tri par fusion pour gagner en temps
            //mergeSort(listeFichiers,0,taille-1);
            bubbleSort(listeFichiers);

            //TODO trouver un autre moyen que d'ouvrir un nouveau flux
            FichierW editeurTmp = new FichierW(cheminBD()+inter()+"Films.bd");
            editeurTmp.ouvrirFuxWriter(false);
            for (String listeFichier : listeFichiers) {
                editeurTmp.ecrireString(listeFichier);
            }
            editeurTmp.fermerFluxWriter();
        }
    }
    private static void bubbleSort(String[] listeFi)
    {
        final int n = listeFi.length;
        for (int i=1;i<=n;i++)
        {
            for(int j=0;j<(n-i);j++)
            {
                if(listeFi[j].compareToIgnoreCase(listeFi[j+1]) > 0) {
                    String tmp = listeFi[j+1];
                    listeFi[j+1] = listeFi[j];
                    listeFi[j] = tmp;
                }
            }
        }
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
        System.arraycopy(A, min, Aux, min, max + 1 - min);
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

    /**
     * Fonction de recherche naive, il recherche toutes les occurences du mot dans la liste.
     * Complexité temporelle en O(n) mais spatiale en O(n) également avec n le nombre d'entrées
     * @param motAChercher le mot à chercher
     * @return un liste chainée des occurences
     */
    public static List<String> searchWord(String motAChercher)
    {
        List<String> liste = new ArrayList<>();
        String tmp;
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
    public static int BinarySearch(String[] listFiles, String filename, int min, int max)
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
        //TODO moyen ameliorer performance en ne renvoyant pas l'entiereté de la liste de fichiers
    }
    /*
        Fonctions Usuelles
     */

    /**
     *
     * @return une liste des noms de fichiers avec chemin complet
     */
    private static String[] donnerListeFichiers()
    {
        List<String> liste = new ArrayList<>();
        final long taille = getfR().longueurFichier();
        final String sep = System.getProperty("line.separator"); //Pour détecter la fin du fichier
        // Ne surtout pas oublier d'en mettre un nouveau sinon
        // il pourrait commencer à lire à la fin du flux
        getfR().setNewBufferedReader();
        for(int i = 0; i < taille; i++) {
            final String str = getfR().lire();
            if(!str.equals(sep) && !str.equals("")) {
                liste.add(str);
            }
        }
        return liste.toArray(new String[liste.size()]);
    }

    /**
     *
     * @return une liste des noms de fichiers sans chemin
     */
    public static String[] donnerListeNomsF()
    {
        String []liste = donnerListeFichiers();
        for (int i=0;i<liste.length;i++) {
            final String mot = liste[i];
            //On regarde la dernière occurence de / pour ne prendre que le nom du film sans le chemin
            final int indice = mot.lastIndexOf(inter());
            if(indice != -1) {
                liste[i] = mot.substring(indice+1);
            }
        }
        return liste;
    }

    /**
     * On modifie la liste passée en paramètre en retirant toute extension
     * @param liste la liste de noms dont on veut filtrer l'extensions
     */
    public static void filtrerExtensions(String[] liste) {
        for(int i=0;i<liste.length;i++) {
            final String mot = liste[i];
            //On pourrait utiliser String.endswith pour voir avec quel extension on termine
            //On filtre les extensions
            final int indice = mot.lastIndexOf("."); //On suppose que tous les films ont une extension
            if(indice != -1) {
                liste[i] = mot.substring(0,indice);
            }
        }
    }
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
    private static String cheminBD()
    {
        String path = null;
        final String homePath = System.getProperty("user.home");
        final String OS = getOsName();
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

    private static String inter()
    {
        return File.separator;
    }

    private static String getOsName() {
        return System.getProperty("os.name");
    }

    public static FichierW getfW() {
        return fW;
    }

    private static void setfW(FichierW fW) {
        CMB.fW = fW;
    }

    public static FichierR getfR() {
        return fR;
    }

    private static void setfR(FichierR fR) {
        CMB.fR = fR;
    }

    public static boolean isDebug() {
        return debug;
    }
}