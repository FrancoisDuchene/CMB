package fichier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/*
 This program is an database manager. This source file is the file writing part
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
 * Une classe qui gère les flux d'écriture d'un fichier
 * @author vinsifroid
 * @since v0.1
 */
@SuppressWarnings("serial")
public class FichierW
{
	private final String filename;
	private PrintWriter PW;
	private boolean ouvert;

	public FichierW(String filename)
	{
		File f = new File(filename);
		if(!f.exists() && !f.isFile())
		{
			try {
				throw new FileNotFoundException(filename + " - wrong filename or path");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		this.filename = filename;		
		PW = null;
		ouvert = false;
	}

    public static boolean creerNouveauFichier(String path)
    {
        File bd = new File(path);
        try {
			return bd.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

	/**
	 *
	 * @param append si c'est true, le flux se mettra à la fin du fichier. Sinon le fichier est écrasé par le nouveau flux.
	 */
	public void ouvrirFuxWriter(boolean append)
	{
		if(ouvert)
		{
			System.err.println("Error - stream already open");
		}
		else{
			try{
				PW = new PrintWriter(new FileWriter(filename, append));
			}catch(IOException e){
				e.printStackTrace();
				System.exit(-1);
			}
			ouvert = true;
		}
	}

	public void fermerFluxWriter()
	{
		if(ouvert)
		{
			PW.close();
			ouvert = false;
		}else{
			System.err.println("No Stream are open");
		}
	}

	/**
	 * @param tmp is the integer to write in the next line of the file
	 */
	public void ecrireInt(int tmp)
	{
		String chaine = String.valueOf(tmp);
		PW.println(chaine);	
	}
	/**
	 * @param tmp is the double to write in the next line of the file
	 */
	public void ecrireDouble(double tmp)
	{		
		String chaine = String.valueOf(tmp);
		PW.println(chaine);
	}
	/**
	 * @param tmp is the String to write in the next line of the file. tmp != null
	 */
	public void ecrireString(String tmp)
	{
		assert(tmp != null) : "Il s'agit d'un String vide !";
		PW.println(tmp);
	}
	
	/**
	 * Create a new PrintWriter with the name of the file stocked in the object Fichier
	 */
	public void setNewPrintWriter() {
		try {
			PW = new PrintWriter(new FileWriter(filename));
		}
		catch (IOException e) {			
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Force the System to write with a flush() statement
	 */
	public void forcerEcriture()
	{
		PW.flush();
	}

	/**
	 * Write a new empty line. Only in 'W' or 'E' mode !
	 */
	public void introduireEspace() {		
		PW.println("");	
	}
}
