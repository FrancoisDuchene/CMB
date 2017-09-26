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

import graphic.GenreTableModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GenreTableModelTest {
    private GenreTableModel model;
    @Before
    public void setUp() throws Exception {
        String[][] data = new String[][] { {"0","Action"},
                {"1","Comedie"},
                {"2","Science-fiction"},
                {"3","Fantastique"}
        };
        model = new GenreTableModel(data);
    }

    @Test
    public void getRowCount() throws Exception {
        final int returnVal = model.getRowCount();
        Assert.assertEquals(4,returnVal);
    }

    @Test
    public void getColumnCount() throws Exception {
        final int returnVal = model.getColumnCount();
        Assert.assertEquals(2,returnVal);
    }

    @Test
    public void getColumnName() throws Exception {
        String returnVal = model.getColumnName(0);
        Assert.assertEquals("ID",returnVal);
        returnVal = model.getColumnName(1);
        Assert.assertEquals("Genre",returnVal);
    }

    @Test
    public void getValueAt() throws Exception {
        int returnVal = (int) model.getValueAt(0,0);
        Assert.assertEquals(0,returnVal);
        returnVal = (int) model.getValueAt(1,0);
        Assert.assertEquals(1,returnVal);
        String returnValString = (String) model.getValueAt(1,1);
        Assert.assertEquals("Comedie",returnValString);
        returnValString = (String) model.getValueAt(2,1);
        Assert.assertEquals("Science-fiction",returnValString);
    }

    @Test
    public void getColumnClass() throws Exception {
        Assert.assertEquals(Integer.class,model.getColumnClass(0));
        Assert.assertEquals(String.class,model.getColumnClass(1));
    }

    @Test
    public void addGenre() throws Exception {
    }

    @Test
    public void removeGenre() throws Exception {
    }

    @Test
    public void clearAll() throws Exception {
    }
}