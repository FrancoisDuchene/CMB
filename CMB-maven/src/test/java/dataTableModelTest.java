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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class dataTableModelTest {
    private dataTableModel model;
    @Before
    public void setUp() throws Exception {
        Movie[] movies = new Movie[]{new Movie(1,"La grande Vadrouille","/Videos/French","avi", "language", "subtitles", 1966,13,new int[]{3,5}),
                new Movie(2,"Star Wars","/Videos/action","mov", "language", "subtitles", 1977,1,null),
                new Movie(3,"Avatar","/Videos/action","mp4", "language", "subtitles", 2007,1,new int[]{1,4,6})};
        model = new dataTableModel(movies);
    }

    @Test
    public void getRowCount() throws Exception {
        final int returnVal = model.getRowCount();
        Assert.assertEquals(3,returnVal);
    }

    @Test
    public void getColumnCount() throws Exception {
        final int returnVal = model.getColumnCount();
        Assert.assertEquals(9,returnVal);
    }

    @Test
    public void getColumnName() throws Exception {
        final String returnVal = model.getColumnName(2);
        Assert.assertEquals("Chemin",returnVal);
    }

    @Test
    public void getValueAt() throws Exception {
        int k = (int) model.getValueAt(0,0);
        Assert.assertEquals(1,k);
        //test genres_id
        String genres = (String) model.getValueAt(1,8);
        Assert.assertEquals(null,genres);
        genres = (String) model.getValueAt(2,8);
        Assert.assertEquals("1 4 6 ",genres);
    }

    @Test
    public void getColumnClass() throws Exception {
        Assert.assertEquals(Integer.class,model.getColumnClass(1));
        Assert.assertEquals(String.class,model.getColumnClass(2));
    }

    @Test
    public void addMovie() throws Exception {
        final int original = model.getRowCount();
        model.addMovie(new Movie(4,"Interstellar","/Videos/","mp4", "language", "subtitles", 2014,2,new int[]{4}));
        Assert.assertEquals(original+1,model.getRowCount());
    }

    @Test
    public void removeMovie() throws Exception {
        final int original = model.getRowCount();
        model.removeMovie(2);
        Assert.assertEquals("Count should be less than original",original-1,model.getRowCount());
    }

    @Test
    public void clearAll() throws Exception {
        model.clearAll();
        Assert.assertEquals(0,model.getRowCount());
        model.clearAll();
        Assert.assertEquals(0,model.getRowCount());
    }

}