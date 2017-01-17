import fichier.FichierR;
import fichier.FichierW;

import javax.swing.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinsifroid on 3/11/16.
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
        Fonctions pour rechercher dans la liste
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