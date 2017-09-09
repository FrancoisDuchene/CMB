/*
 This program is an database manager. This source file is the GUI part of it
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

public class Movie {
    private int id;
    private String name;
    private String path;
    private String extension;
    private int year;
    private int harddrive_id;
    private int[] genres_id;

    public Movie(int id, String name, String path, String extension, int year, int harddrive_id, int[] genre_id) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.extension = extension;
        this.year = year;
        this.harddrive_id = harddrive_id;
        this.genres_id = genre_id;
    }
    public Movie(int id, String name, String path, String extension, int year, int harddrive_id) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.extension = extension;
        this.year = year;
        this.harddrive_id = harddrive_id;
        genres_id = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getHarddrive_id() {
        return harddrive_id;
    }

    public void setHarddrive_id(int harddrive_id) {
        this.harddrive_id = harddrive_id;
    }

    public int[] getGenres_id() {
        return genres_id;
    }

    public void setGenres_id(int[] genres_id) {
        this.genres_id = genres_id;
    }
}
