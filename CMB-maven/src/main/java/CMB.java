import fichier.FichierR;
import fichier.FichierW;

import javax.swing.SwingUtilities;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
    private static SqliteManager app;
    private static final boolean debug = false;
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
        final boolean DBExist = isDBExist();
        final String filename = cheminBD() + inter() + "Films.db";
        app = new SqliteManager(filename);
        //test_init();
        SwingUtilities.invokeLater(CMB_gui::new);
    }
    private static boolean createFolder() {
        final String ch = cheminBD();
        File folder = new File(ch);
        // On verifie que le dossier BaseDonnee existe
        if(!folder.exists() && !folder.isDirectory())
        {
            if(!folder.mkdirs())
                return false;
        }

        return true;
    }
    private static boolean isDBExist() {
        final String ch = cheminBD();
        File bdd = new File(ch + inter() + "Films.db");
        if(!bdd.exists()) {
            return false;
        }
        return true;
    }
    /*
        Fonctions pour réaliser la liste de films
     */
    public static void findFiles(File file1)
    {
        //TODO Ajouter de nouveaux filtres pour ajouter automatiquement les genres, années,...
        //TODO faire un système pour retenir des films qui existent déjà dans la db et avertir l'utilisateur
        File[] list = file1.listFiles();
        if(list!=null)
        {
            for(File file2 : list)
            {
                //TODO tester performance de stocker nomSuf au lieu de faire des appels à fonction dans chaque if
                final String nomSuf = file2.getName();
                if (file2.isDirectory())
                {
                    findFiles(file2);
                }
                else if (nomSuf.endsWith("avi")) {
                    final String path = file2.getAbsolutePath();
                    final String nom = filtreExtension(nomSuf);
                    app.insertUpdateFilm(true,0,nom,path,".mov",0,null,1);
                }else if (nomSuf.endsWith("mp4")) {
                    final String path = file2.getPath();
                    final String nom = filtreExtension(nomSuf);
                    app.insertUpdateFilm(true,0,nom,path,"mp4",0,null,1);
                }else if (nomSuf.endsWith("mkv")) {
                    final String path = file2.getPath();
                    final String nom = filtreExtension(nomSuf);
                    app.insertUpdateFilm(true,0,nom,path,"mkv",0,null,1);
                }else if (nomSuf.endsWith("mov")) {
                    final String path = file2.getPath();
                    final String nom = filtreExtension(nomSuf);
                    app.insertUpdateFilm(true,0,nom,path,"mov",0,null,1);
                }
            }
        }
    }
    /*
        Fonctions interaction avec la bdd
     */
    public static List<String[]> searchMovie(String name) {
        List<String[]> liste = app.searchMovie(name);
        return liste;
    }
    public static List<String[]> getAllMovies() {
        List<String[]> liste = app.selectAllMovies();
        return liste;
    }
    /*
        Fonctions Usuelles
     */

    private static String filtreExtension(String mot) {
        final int indice = mot.lastIndexOf(".");
        if(indice != -1)
            return mot.substring(0, indice);
        return mot;
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

    //Tests

    private static void test_init() {
        app.insertUpdateFilm(true,0,"Star wars","action/","avi",1977,new int[]{1,3},1);
        app.insertUpdateFilm(true,0,"Star trek","sciencefiction/","avi",1977,new int[]{1},1);
        app.insertUpdateFilm(true,0,"La soupe aux choux","/francais","avi",0,new int[]{2},1);
        app.insertUpdateHarddisk(true,0,"Emtec");
        app.insertUpdateGenre(true,0,"Science-fiction");
        app.insertUpdateGenre(true,0,"Comédie");
        app.insertUpdateGenre(true,0,"Fantastique");
        if(isDebug()) {
            test_select();
            app.searchMovie("Star wars");
        }
    }

    private static void test_select() {
        System.out.println();
        app.selectAllMovies();
        System.out.println();
        app.selectAllGenres();
        System.out.println();
        app.selectAllHarddives();
        System.out.println();
    }

    private static String inter()
    {
        return File.separator;
    }

    private static String getOsName() {
        return System.getProperty("os.name");
    }

    public static boolean isDebug() {
        return debug;
    }

    public static SqliteManager getApp() {
        return app;
    }

    public static void setApp(SqliteManager app) {
        CMB.app = app;
    }
}