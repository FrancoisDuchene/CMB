/*
 This program is an database manager. This source file is main part of it
 Central Movie dataBase, CMB for short, current version is : 0.4
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

package main;

import database.Movie;
import database.SqliteManager;
import graphic.CMB_gui;

import javax.swing.SwingUtilities;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static main.Analyse.filtreExtension;

/**
 * This class have the basics functions of the app
 * @author vinsifroid
 * @since v0.1
 */
public class CMB {
    private static final boolean debug = false;
    private static final String filterFileName = cheminBD() + inter() + "filter";
    private static SqliteManager app;
    private CMB(){}

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
        if(!DBExist) {
            DBInit();
        }
        SwingUtilities.invokeLater(CMB_gui::new);
    }
    private static boolean createFolder() {
        final String ch = cheminBD();
        File folder = new File(ch);
        // We verify that BaseDonnee folder exist
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
        return bdd.exists();
    }

    private static void DBInit() {
        app.insertUpdateGenre(true,0,"N/A");
        app.insertUpdateGenre(true,0,"Science-Fiction");
        app.insertUpdateGenre(true,0,"Action");
        app.insertUpdateGenre(true,0,"Fantastique");
        app.insertUpdateGenre(true,0,"Horreur");
        app.insertUpdateGenre(true,0,"Comedie");
        app.insertUpdateGenre(true,0,"Policier");
        app.insertUpdateGenre(true,0,"Thriller");
        app.insertUpdateGenre(true,0,"Drame");
        app.insertUpdateGenre(true,0,"Parodie");
        app.insertUpdateHarddisk(true,0,"N/A");
    }
    /*
        Functions to realize the movie list
     */
    public static void findFiles(File file1)
    {
        //TODO Add new filters to automatically add genre, years, ...
        //TODO Make an alert when the user try to add movies that already exists in the db and warn him
        File[] list = file1.listFiles();
        if(list!=null) {
            for(File file2 : list) {
                //TODO do performance test to see if stock nomSuf instead of do a call to each if is more efficient
                final String nomSuf = file2.getName();
                if (file2.isDirectory()) {
                    findFiles(file2);
                }
                else if (nomSuf.endsWith("avi")) {
                    final String path = file2.getAbsolutePath();
                    final String nom = filtreExtension(nomSuf);
                    //final String[] valeurs = analyseMovieFileName(nomSuf);
                    app.insertUpdateFilm(true,0,nom,path,"avi","","",0,null,1);
                }else if (nomSuf.endsWith("mp4")) {
                    final String path = file2.getPath();
                    final String nom = filtreExtension(nomSuf);
                    //final String[] valeurs = analyseMovieFileName(nomSuf);
                    app.insertUpdateFilm(true,0,nom,path,"mp4","","",0,null,1);
                }else if (nomSuf.endsWith("mkv")) {
                    final String path = file2.getPath();
                    final String nom = filtreExtension(nomSuf);
                    //final String[] valeurs = analyseMovieFileName(nomSuf);
                    app.insertUpdateFilm(true,0,nom,path,"mkv","","",0,null,1);
                }else if (nomSuf.endsWith("mov")) {
                    final String path = file2.getPath();
                    final String nom = filtreExtension(nomSuf);
                    //final String[] valeurs = analyseMovieFileName(nomSuf);
                    app.insertUpdateFilm(true,0,nom,path,"mov","","",0,null,1);
                }
            }
        }
    }
    /*
        Function to interact with the db
     */
    public static Movie[] searchMovie(String name) {
        Movie[] liste = app.searchMovie(name);
        return liste;
    }
    public static Movie[] getAllMovies() {
        final String[][] liste = app.selectAllMovies();
        Movie[] movies = new Movie[liste.length];
        int i=0;
        for(String[] film : liste) {
            int[] genres = app.getIdAllGenresOfMovie(film[0]);
            Movie mov = new Movie(Integer.parseInt(film[0]),film[1],film[2],film[3],
                    film[4], film[5], Integer.parseInt(film[6]),Integer.parseInt(film[7]),genres);
            movies[i] = mov;
            i++;
        }
        return movies;
    }
    public static String[][] getAllGenres() {
        final String[][] genres = app.selectAllGenres();
        return genres;
    }
    /*
        Other functions
     */

    /**
     *
     * @param path of folder where to search
     * @param fileName of the file to find
     * @return true if the file exist and false otherway
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
    public static String dateActuelle() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.now();
        return dateFormat.format(localDate);
    }
    private static String cheminBD() {
        final String homePath = System.getProperty("user.home");
        final String OS = getOsName();
        if(OS.contains("Windows")){
            return homePath + "\\BaseDonnee";
        }else if (OS.contains("Linux")){
            return homePath + "/BaseDonnee";
        }else{
            System.err.println("Error OS not supported");
            System.exit(-1);
        }
        return null;
    }


    private static String inter() {
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