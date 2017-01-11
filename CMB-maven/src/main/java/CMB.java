import fichier.FichierR;
import fichier.FichierW;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        //TODO lien vers un menu principal en console
        //TODO pouvoir faire une recherche dans la BD via cette interface
        try {
            findFiles(new File(""));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fW.fermerFluxWriter();
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
                fW = new FichierW(cheminBD()+inter()+"Films.bd");
                fW.ouvrirFuxWriter(false);
                System.out.println("Fichier cree");
            }else{
                System.exit(-1);
            }
        } else {
            fW = new FichierW(cheminBD()+inter()+"Films.bd");
            fW.ouvrirFuxWriter(false);
        }
    }
    /*
        Fonctions pour réaliser la liste de films
     */
    private static void findFiles(File file1)throws IOException
    {
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
                    fW.ecrireString(file2.getName());
                }else if (file2.getName().contains(".mp4")) {
                    fW.ecrireString(file2.getName());
                }else if (file2.getName().contains(".mkv")) {
                    fW.ecrireString(file2.getName());
                }else if (file2.getName().contains(".mov")) {
                    fW.ecrireString(file2.getName());
                }
            }
        }
    }

    /*
        Fonctions pour rechercher dans la liste
     */
    private static void searchWord()
    {

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
    private static String dateActuelle()
    {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
        LocalDate localDate = LocalDate.now();
        return dateFormat.format(localDate);
    }
    private static String cheminBD()
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
    private static String inter()
    {
        String inter = null;
        if(OS.contains("Windows")){
            inter = "\\";
        }else if (OS.contains("Linux")){
            inter = "/";
        }else{
            System.err.println("Error OS not supported");
            System.exit(-1);
        }
        return inter;
    }
    private static String getOsName()
    {
        if(OS == null) { OS = System.getProperty("os.name"); }
        return OS;
    }
}