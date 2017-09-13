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

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GenreTableModel extends AbstractTableModel{

    private final List<Integer> ids = new ArrayList<>();
    private final List<String> data = new ArrayList<>();
    private final String[] header = {"ID","Genre"};

    public GenreTableModel(int[] ids, String[] data) {
        super();
        Integer[] idsInt = Arrays.stream(ids).boxed().toArray(Integer[]::new);
        Collections.addAll(this.ids, idsInt);
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
                return ids.get(rowIndex);
            case 1:
                return data.get(rowIndex);
            default:
                return null;
        }
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        switch(columnIndex) {
            case 0:
                return Integer.class;
            default:
                return String.class;
        }
    }

    public void addGenre(String genre) {
        data.add(genre);
        this.fireTableRowsInserted(data.size() -1, data.size() -1);
    }

    public void removeGenre(int rowIndex) {
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
