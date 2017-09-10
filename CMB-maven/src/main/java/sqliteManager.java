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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            System.out.println("Le nom du driver est " + meta.getDriverName() + " v" + meta.getDatabaseMajorVersion() +
                    "." + meta.getDatabaseMinorVersion());
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
                + "extension text NOT NULL,\n"
                + "year integer,\n"
                + "harddrive_id integer,\n"
                + "FOREIGN KEY('harddrive_id') REFERENCES 'harddisk'('harddisk') ON DELETE NO ACTION ON UPDATE NO ACTION\n"
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
                + "FOREIGN KEY('film_id') REFERENCES 'film'('film_id') ON DELETE NO ACTION,\n"
                + "FOREIGN KEY('genre_id') REFERENCES 'genre'('genre_id') ON DELETE NO ACTION\n"
                + ");";
        try(Statement stmt = conn.createStatement()) {
            // on crée les nouvelles tables si elles n'existent pas
            stmt.execute(sql);
            stmt.execute(sql2);
            stmt.execute(sql3);
            stmt.execute(sql4);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //SELECT

    public String[][] selectAllMovies() {
        final String sql = "SELECT film.film_id, film.name, film.path, film.extension, " +
                "film.year, genre.nom, harddisk.hd_nom FROM filmXgenre " +
                "INNER JOIN film ON filmXgenre.film_id = film.film_id " +
                "INNER JOIN genre ON filmXgenre.genre_id = genre.genre_id " +
                "INNER JOIN harddisk ON harddisk.harddisk_id = film.harddrive_id ";
        return stmtRS(sql,new String[]{"film_id","name","path","extension",
                        "year","hd_nom","nom"},new byte[]{1,2,2,2,1,2,2});
    }
    public String[][] selectAllGenres() {
        final String sql = "SELECT genre_id, nom FROM genre";
        return stmtRS(sql,new String[]{"genre_id","nom"},new byte[]{1,2});
    }
    public String[][] selectAllGenresAndMovies() {
        final String sql = "SELECT genre.nom, film.name FROM filmXgenre " +
                "INNER JOIN genre ON filmXgenre.genre_id = genre.genre_id " +
                "INNER JOIN film ON filmXgenre.film_id = film.film_id ";
        return stmtRS(sql,new String[]{"nom","name"},new byte[]{2,2});
    }
    public String[][] selectAllHarddives() {
        final String sql = "SELECT harddisk_id, hd_nom FROM harddisk";
        return stmtRS(sql,new String[]{"harddisk_id","hd_nom"},new byte[]{1,2});
    }
    public String[][] selectAllHarddrivesAndMovies() {
        final String sql = "SELECT harddisk.hd_nom, film.name FROM film " +
                "INNER JOIN harddisk ON film.harddrive_id = harddisk.harddisk_id";
        return stmtRS(sql,new String[]{"hd_nom","name"},new byte[]{2,2});
    }

    private String[][] stmtRS(String sql, String []args, byte[] meth) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            // on parcours l'ensemble des résultats
            if(CMB.isDebug()){
                printRes_Debug(rs, args, meth);
                return null;
            }
            List<String> film = new ArrayList<>();
            List<List<String>> liste = new ArrayList<>();
            while(rs.next()) {
                for(int i=0;i<args.length;i++) {
                    switch (meth[i]) {
                        case 1:
                            film.add(Integer.toString(rs.getInt(args[i])));
                            break;
                        case 2:
                            film.add(rs.getString(args[i]));
                            break;
                        case 3:
                            film.add(Double.toString(rs.getDouble(args[i])));
                            break;
                    }
                }
                liste.add(film);
                film.clear();
            }
            stmt.close();
            String[][] array = new String[liste.size()][];
            int i=0;
            for(List<String> nestedList : liste) {
                array[i++] = nestedList.toArray(new String[nestedList.size()]);
            }
            return array;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //TODO faire tests performances pour voir si utiliser des List<String[]> est plus avantageux qu'un String[]
    /**
     *
     * @param nom
     * @return la liste des attributs du film ou NULL si il y a eu une exception
     */
    public List<String[]> searchMovie(String nom) {
        final String sql = "SELECT name, path, extension, year, harddrive_id FROM film WHERE name = ? ";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1,nom);
            ResultSet rs = pstmt.executeQuery();
            if(CMB.isDebug()) {
                printRes_Debug(rs,new String[]{"name","path","extension","year","harddrive_id"},new byte[]{2,2,2,1,1});
            }
            List<String[]> liste = new ArrayList<>();
            while(rs.next()) {
                final String[] mov =  new String[]{rs.getString("name"),rs.getString("path"),rs.getString("extension"),
                        Integer.toString(rs.getInt("year")),Integer.toString(rs.getInt("harddrive_id"))};
                liste.add(mov);
            }
            pstmt.close();
            return liste;
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param nom du film à rechercher
     * @return l'indice du film recherché ou -1 si un problème est survenu
     */
    private int getIdOfMovie(String nom) {
        final String sql = "SELECT film_id FROM film WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1,nom);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            final int id = rs.getInt(1);
            pstmt.close();
            return id;
        } catch(SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     *
     * @param nom du film auquel on cherche les genres
     * @return tableau contenant les genres
     */
    public String [] searchAllGenresOfMovie(String nom) {
        final int id = getIdOfMovie(nom);
        final String sql = "SELECT genre.nom FROM filmXgenre" +
                " INNER JOIN genre ON filmXgenre.genre_id = genre.genre_id AND filmXgenre.film_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1,id);
            ResultSet rs = pstmt.executeQuery();
            if(CMB.isDebug()) {
                printRes_Debug(rs, new String[]{"nom"}, new byte[]{2});
                return null;
            }
            List<String> liste = new ArrayList<>();
            while(rs.next()) {
                liste.add(rs.getString(1));
            }
            pstmt.close();
            return liste.toArray(new String[liste.size()]);
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int[] getIdAllGenresOfMovie(String nom) {
        final int id = getIdOfMovie(nom);
        final String sql = "SELECT genre_id FROM filmXgenre WHERE film_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1,id);
            ResultSet rs = pstmt.executeQuery();
            List<Integer> liste = new ArrayList<>();
            while(rs.next()) {
                liste.add(rs.getInt(1));
            }
            pstmt.close();
            //On ne peut pas caster directement faire int[]
            final Integer[] ls = liste.toArray(new Integer[liste.size()]);
            return Arrays.stream(ls).mapToInt(Integer::intValue).toArray();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
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
    public boolean insertUpdateFilm(boolean mode,int id, String name, String path, String extension,
                                 int year, int[] genre_id, int harddrive_id) {
        final String sql, sql2 = "INSERT INTO filmXgenre(film_id, genre_id) VALUES((" +
                "SELECT film_id FROM film WHERE name = ?)," +
                "?)";
        if(mode) {
            sql = "INSERT INTO film(name,path,extension,year,harddrive_id) VALUES(?,?,?,?,?)";
        }else{
            sql = "UPDATE film SET name = ? , path = ? , extension = ? ," +
                    "year = ? , genre_id = ? , harddrive_id = ? " +
                    "WHERE film_id = ?";
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            //On modifie la table film
            pstmt.setString(1, name);
            pstmt.setString(2, path);
            pstmt.setString(3,extension);
            pstmt.setInt(4, year);
            pstmt.setInt(5, harddrive_id);
            if(!mode)
                pstmt.setInt(6,id);
            pstmt.executeUpdate();
            //On modifie la table filmXgenre
            if(genre_id != null) {
                PreparedStatement pstmt2 = conn.prepareStatement(sql2);
                pstmt2.setString(1,name);
                for(int i=0;i<genre_id.length;i++) {
                    pstmt2.setInt(2,genre_id[i]);
                    pstmt2.executeUpdate();
                }
                pstmt2.close();
            }
            pstmt.close();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean insertUpdateGenre(boolean mode, int id, String name) {
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
            pstmt.close();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean insertUpdateHarddisk(boolean mode, int id, String name) {
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
            pstmt.close();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //DELETE

    public boolean deleteFilm(int id) {
        final String sql = "DELETE FROM film WHERE film_id = ?";
        final boolean ok = stmtDel(sql,id);
        final String sql2 = "DELETE FROM filmXgenre WHERE film_id = ?";
        final boolean ok2 = stmtDel(sql2,id);
        if(ok && ok2)
            System.out.println("Film n°" + id + " a bien été supprimé");
        return ok && ok2;
    }

    /**
     *
     * @param id
     * @param fichiers si on supprime les fichiers qui se trouvent sur le disque dur
     * @return true si tout s'est bien déroulé, false sinon
     */
    public boolean deleteHarddisk(int id, boolean fichiers) {
        //TODO message d'info pour supprimer fichiers ou non
        final String sql = "DELETE FROM harddisk WHERE harddisk_id = ?";
        final boolean ok = stmtDel(sql,id);
        if(fichiers) {
            final String sql2 = "DELETE FROM film WHERE harddisk_id = ?";
            final boolean ok2 = stmtDel(sql2,id);
            if(ok && ok2)
                System.out.println("Disque Dur n°" + id +
                        " a bien été supprimé ainsi que tous les films en faisant partie");
            return ok && ok2;
        }
        if(ok)
            System.out.println("Disque Dur n°" + id + " a bien été supprimé");
        return ok;
    }

    private boolean stmtDel(String sql, int id) {
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            pstmt.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
    private static void printRes_Debug(ResultSet rs, String[] args, byte[] meth) {
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
