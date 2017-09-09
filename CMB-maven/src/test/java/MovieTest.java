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

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class MovieTest {
    private Movie mov;
    @Before
    public void setUp() throws Exception {
        mov = new Movie(1,"La grande Vadrouille","/Videos/French","avi", "language", "subtitles", 1966,13,new int[]{3,5});
    }

    @Test
    public void getId() throws Exception {
        final int returnVal = mov.getId();
        Assert.assertEquals(1,returnVal);
    }

    @Test
    public void setId() throws Exception {
        mov.setId(255);
        final int returnVal = mov.getId();
        Assert.assertEquals(255,returnVal);
    }

    @Test
    public void getName() throws Exception {
        final String returnVal = mov.getName();
        Assert.assertEquals("La grande Vadrouille",returnVal);
    }

    @Test
    public void setName() throws Exception {
        mov.setName("La folie des grandeurs");
        final String returnVal = mov.getName();
        Assert.assertEquals("La folie des grandeurs",returnVal);
    }

    @Test
    public void getPath() throws Exception {
        final String returnVal = mov.getPath();
        Assert.assertEquals("/Videos/French",returnVal);
    }

    @Test
    public void setPath() throws Exception {
        mov.setPath("/Videos");
        final String returnVal = mov.getPath();
        Assert.assertEquals("/Videos",returnVal);
    }

    @Test
    public void getExtension() throws Exception {
        final String returnVal = mov.getExtension();
        Assert.assertEquals("avi",returnVal);
    }

    @Test
    public void setExtension() throws Exception {
        mov.setExtension("mp4");
        final String returnVal = mov.getExtension();
        Assert.assertEquals("mp4",returnVal);
    }

    @Test
    public void getYear() throws Exception {
        final int returnVal = mov.getYear();
        Assert.assertEquals(1966,returnVal);
    }

    @Test
    public void setYear() throws Exception {
        mov.setYear(1971);
        final int returnVal = mov.getYear();
        Assert.assertEquals(1971,returnVal);
    }

    @Test
    public void getHarddrive_id() throws Exception {
        final int returnVal = mov.getHarddrive_id();
        Assert.assertEquals(13,returnVal);
    }

    @Test
    public void setHarddrive_id() throws Exception {
        mov.setHarddrive_id(6);
        final int returnVal = mov.getHarddrive_id();
        Assert.assertEquals(6,returnVal);
    }

    @Test
    public void getGenres_id() throws Exception {
        final int[] returnVal = mov.getGenres_id();
        Assert.assertArrayEquals(new int[]{3,5},returnVal);
    }

    @Test
    public void setGenres_id() throws Exception {
        mov.setGenres_id(new int[]{8,10,7});
        final int[] returnVal = mov.getGenres_id();
        Assert.assertArrayEquals(new int[]{8,10,7},returnVal);
    }

}