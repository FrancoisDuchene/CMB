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

package graphic;

import database.Movie;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataTableModel extends AbstractTableModel{
    private final List<Movie> data = new ArrayList<Movie>();
    private final String[] header = {"ID", "Nom", "Chemin","Extension","Langue","Sous-titres","Année","ID harddrive","ID genre(s)"};

    public DataTableModel(Movie[] data) {
        super();
        Collections.addAll(this.data, data);
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return header.length;
    }

    @Override
    public String getColumnName(int columnIndex){
        return header[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return data.get(rowIndex).getId();
            case 1:
                return data.get(rowIndex).getName();
            case 2:
                return data.get(rowIndex).getPath();
            case 3:
                return data.get(rowIndex).getExtension();
            case 4:
                return data.get(rowIndex).getLanguage();
            case 5:
                return data.get(rowIndex).getSubtitles();
            case 6:
                return data.get(rowIndex).getYear();
            case 7:
                return data.get(rowIndex).getHarddrive_id();
            case 8:
                if(data.get(rowIndex).getGenres_id() != null) {
                    String genres = "";
                    final int genreLength = data.get(rowIndex).getGenres_id().length;
                    final int[] genres_id = data.get(rowIndex).getGenres_id();
                    for(int k=0;k<genreLength;k++) {
                        genres += Integer.toString(genres_id[k]) + " ";
                    }
                    return genres;
                }
                return null;
            default: //Should never happen
                return null;
        }
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        switch(columnIndex) {
            case 1:
                return Integer.class;
            case 6:
                return Integer.class;
            case 7:
                return Integer.class;
            default:
                return String.class;
        }
    }

    public void addMovie(Movie mov) {
        data.add(mov);
        this.fireTableRowsInserted(data.size() -1, data.size() -1);
    }

    public void removeMovie(int rowIndex) {
        data.remove(rowIndex);
        this.fireTableRowsDeleted(rowIndex,rowIndex);
    }

    public void clearAll() {
        data.clear();
        if(data.size() > 0) {
            this.fireTableRowsDeleted(0,data.size()-1);
        }
    }
}

