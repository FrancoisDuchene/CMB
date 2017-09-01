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

import org.sqlite.SQLiteConfig;

import java.sql.*;

/**
 * Created by vinsifroid on 29/08/17.
 */
public class SqliteManager {
    private final String filename;
    private Connection conn;

    public SqliteManager(String filename) {
        this.filename = filename;
        conn = null;
        connect();
        createNewTable();
    }

    //INITIALISATION

    private void connect() {
        try {
            // Paramètres de la bdd
            final String url = "jdbc:sqlite:" + filename;
            //Pour faire en sorte qu'il tienne compte des clés étrangères
            /*SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);*/
            //On crée la connection à la bdd. Si elle n'existe pas elle est automatiquement crée
            conn = DriverManager.getConnection(url/*,config.toProperties()*/);
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("Le nom du driver est " + meta.getDriverName());
            System.out.println("La connection s'est effectuée correctement");
        } catch (SQLException e) {
            e.printStackTrace();
            closeDB();
        }
    }
    public void closeDB() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    private void createNewTable() {
        final String sql = "CREATE TABLE IF NOT EXISTS 'film' (\n"
                + "film_id integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n"
                + "name text NOT NULL,\n"
                + "path text NOT NULL,\n"
                + "year integer,\n"
                + "harddrive_id integer,\n"
                + "FOREIGN KEY('harddrive_id') REFERENCES 'harddisk'('harddisk') ON DELETE NO ACTION ON UPDATE CASCADE\n"
                + ");";
        final String sql2 = "CREATE TABLE IF NOT EXISTS 'genre' (\n"
                + "genre_id integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n"
                + "nom text NOT NULL\n"
                + ");";
        final String sql3 = "CREATE TABLE IF NOT EXISTS 'harddisk' (\n"
                + "harddisk_id integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n"
                + "hd_nom text NOT NULL\n"
                + ");";
        //table de transisition entre film et genre comme un film peut avoir plusieurs genres
        //et qu'un genre concerne plusieurs films
        final String sql4 = "CREATE TABLE IF NOT EXISTS 'filmXgenre' (\n"
                + "film_id integer,\n"
                + "genre_id integer,\n"
                + "FOREIGN KEY('film_id') REFERENCES 'film'('film_id') ON DELETE CASCADE,\n"
                + "FOREIGN KEY('genre_id') REFERENCES 'genre'('genre_id') ON DELETE CASCADE\n"
                + ");";
        try(Statement stmt = conn.createStatement()) {
            // on crée les nouvelles tables si elles n'existent pas
            stmt.execute(sql);
            stmt.execute(sql2);
            stmt.execute(sql3);
            stmt.execute(sql4);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //SELECT

    public void selectAllMovies() {
        final String sql = "SELECT film.film_id, film.name, film.path, film.year, genre.nom, harddisk.hd_nom FROM filmXgenre " +
                "INNER JOIN film ON filmXgenre.film_id = film.film_id " +
                "INNER JOIN genre ON filmXgenre.genre_id = genre.genre_id " +
                "INNER JOIN harddisk ON harddisk.harddisk_id = film.harddrive_id ";
        stmtRS(sql,new String[]{"film_id","name","path","year","nom","hd_nom"},new byte[]{1,2,2,1,2,2});
    }
    public void selectAllGenres() {
        final String sql = "SELECT genre_id, nom FROM genre";
        stmtRS(sql,new String[]{"genre_id","nom"},new byte[]{1,2});
    }
    public void selectAllGenresAndMovies() {
        final String sql = "SELECT genre.nom, film.name FROM filmXgenre " +
                "INNER JOIN genre ON filmXgenre.genre_id = genre.genre_id " +
                "INNER JOIN film ON filmXgenre.film_id = film.film_id ";
        stmtRS(sql,new String[]{"nom","name"},new byte[]{2,2});
    }
    public void selectAllHarddives() {
        final String sql = "SELECT harddisk_id, hd_nom FROM harddisk";
        stmtRS(sql,new String[]{"harddisk_id","hd_nom"},new byte[]{1,2});
    }
    public void selectAllHarddrivesAndMovies() {
        final String sql = "SELECT harddisk.hd_nom, film.name FROM film " +
                "INNER JOIN harddisk ON film.harddrive_id = harddisk.harddisk_id";
        stmtRS(sql,new String[]{"hd_nom","name"},new byte[]{2,2});
    }

    private void stmtRS(String sql, String []args, byte[] meth) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            // on parcours l'ensemble des résultats
            if(CMB.isDebug())
                printRes_Debug(rs, args, meth);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void searchMovie(String nom) {
        final String sql = "SELECT film.name, film.path, film.year, harddisk.hd_nom FROM film WHERE film.name = ? " +
                "INNER JOIN harddisk ON film.harddisk_id = harddisk.harddisk_id";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1,nom);
            ResultSet rs = pstmt.executeQuery();
            if(CMB.isDebug()) {
                printRes_Debug(rs,new String[]{"name","path","year","hd_nom"},new byte[]{2,2,1,2});
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    //INSERT - UPDATE

    /**
     * En UPDATE, on ne peut pas supprimer des genres de films, on ne peut qu'en ajouter
     * @param mode true pour INSERT et false pour UPDATE
     * @param id l'id de l'objet à UPDATE, si mode = true, l'argument est négligé
     * @param name
     * @param path
     * @param year
     * @param genre_id
     * @param harddrive_id
     */
    public void insertUpdateFilm(boolean mode,int id, String name, String path, int year, int[] genre_id, int harddrive_id) {
        final String sql, sql2 = "INSERT INTO filmXgenre(film_id, genre_id) VALUES((" +
                "SELECT film_id FROM film WHERE name = ?)," +
                "?)";
        if(mode) {
            sql = "INSERT INTO film(name,path,year,harddrive_id) VALUES(?,?,?,?)";
        }else{
            sql = "UPDATE film SET name = ? , path = ? , " +
                    "year = ? , genre_id = ? , harddrive_id = ? " +
                    "WHERE film_id = ?";
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            //On modifie la table film
            pstmt.setString(1, name);
            pstmt.setString(2, path);
            pstmt.setInt(3, year);
            pstmt.setInt(4, harddrive_id);
            if(!mode)
                pstmt.setInt(5,id);
            pstmt.executeUpdate();
            //On modifie la table filmXgenre

            PreparedStatement pstmt2 = conn.prepareStatement(sql2);
            pstmt2.setString(1,name);
            for(int i=0;i<genre_id.length;i++) {
                pstmt2.setInt(2,genre_id[i]);
                pstmt2.executeUpdate();
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    public void insertUpdateGenre(boolean mode, int id, String name) {
        final String sql;
        if(mode) {
            sql = "INSERT INTO genre(nom) VALUES(?)";
        }else{
            sql = "UPDATE genre SET nom = ? WHERE genre_id = ?";
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            if(!mode)
                pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    public void insertUpdateHarddisk(boolean mode, int id, String name) {
        final String sql;
        if(mode) {
            sql = "INSERT INTO harddisk(hd_nom) VALUES(?)";
        }else{
            sql = "UPDATE harddisk SET hd_nom = ? WHERE harddisk_id = ?";
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            if(!mode)
                pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    //DELETE

    public void deleteFilm(int id) {
        //TODO supprimer les liens correspondants dans la table filmXgenre
        final String sql = "DELETE FROM film WHERE film_id = ?";
        stmtDel(sql,id);
        System.out.println("Film n°" + id + " a bien été supprimé");
    }
    public void deleteHarddisk(int id) {
        //TODO supprimer les films qui faisaient partie de ce disque dur
        //TODO laisser l'utilisateur le choix de les supprimer ou non. Message d'information quand cela se fait
        final String sql = "DELETE FROM harddisk WHERE harddisk_id = ?";
        stmtDel(sql,id);
        System.out.println("Disque Dur n°" + id + " a bien été supprimé");
    }

    private void stmtDel(String sql, int id) {
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //FONCTIONS DEBUG SQL

    /**
     * Fonction de debug pour afficher les résultats d'une requête.
     * Pre: longueur de args = longueur meth
     * @param rs les résultats
     * @param args les arguments à rechercher via les méthodes appliqués à rs
     * @param meth les méthodes à appliquer à rs, ex rs.getInt, getString, ... Chaque méthode a un num d'identification
     *             1. getInt
     *             2. getString
     *             3. getDouble
     */
    public static void printRes_Debug(ResultSet rs, String []args, byte[] meth) {
        try {
            while(rs.next()) {
                for(int i=0;i<args.length;i++) {
                    switch (meth[i]) {
                        case 1:
                            System.out.print(rs.getInt(args[i]) + "\t");
                            break;
                        case 2:
                            System.out.print(rs.getString(args[i]) + "\t");
                            break;
                        case 3:
                            System.out.print(rs.getDouble(args[i]) + "\t");
                            break;
                    }
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
