package fichier;

import java.io.*;

/**
 * Une classe qui gère les flux de lecture d'un fichier
 * @author vinsifroid
 * @since v1.0-eta
 */
@SuppressWarnings("serial")
public class FichierR
{
	/**
	 * Le nom du fichier ainsi que son chemin (les 2 en même temps)
	 */
	private final String filename;
	private BufferedReader BR;
	/**
	 * true quand un flux est ouvert et false quand un flux est ferme
	 */
	private boolean ouvert;

	public FichierR(String filename)
	{
		File f = new File(filename);
		if(!f.exists() && !f.isFile())
		{
			try {
				throw new FileNotFoundException(filename + " - wrong filename or path");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		this.filename = filename;
		BR = null;
		ouvert = false;
	}

	// Gestionnaires de flux

	public void ouvrirFluxReader()
	{
		if(ouvert)
		{
			System.err.println("Error - stream already open");
		}
		else{
			try{
				BR = new BufferedReader(new FileReader(filename));
			}catch(FileNotFoundException e){
				e.printStackTrace();
				System.exit(-1);
			}
			ouvert = true;
		}
	}
	public void fermerFluxReader()
	{
		if(ouvert)
		{
			try{
				BR.close();
			}catch(IOException e){
				e.printStackTrace();
				System.exit(-1);
			}
			ouvert = false;
		}else{
			System.err.println("No Stream are open");
		}
	}

	/**
	 * @return the current String read from the file
	 */
	public String lire() {
		File f = new File(filename);
		try {
			if(f.canRead() && BR.ready())
			{
				return BR.readLine();
			}
		}
		catch(IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
			return "Error IOException";
		}
		return "Error don't have the right to read the file";
	}
	/**
	 * @param n the number of line to skip
	 * @return the current String read from the file
	 */
	public String lire(int n) {
		File f = new File(filename);
		try {
			if(f.canRead() && BR.ready())
			{
				String chaine = "";
				for(int i =0; i<n;i++) {
					chaine = BR.readLine();
				}
				return chaine;
			}
		}
		catch(IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
			return "Error IOException";
		}
		return "Error don't have the right to read the file";
	}

	/**
	 * Create a new BufferedReader with the name of the file stocked in the object FichierR
	 */
	public void setNewBufferedReader() {
		try {
			BR = new BufferedReader(new FileReader(new File(filename)));
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Check in the file if the word is inside
	 * @param mot1 the String to check
	 * @return true if the word exist in the file and false if not
	 */
	public boolean equalsMots(String mot1) {
		setNewBufferedReader();
		String mot2;
		File f = new File(filename);
		for(int i = 0; i < f.length();i++) {
			mot2 = lire(i);
			if(mot1.equals(mot2)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Go to the end of File
	 */
	public void toEnd() {
		String str1;
		do {
			str1 = lire();
		}while(str1 != null);
	}

	/**
	 * @return Long
	 *  The function return the length of the file currently used 
	 */
	public long longueurFichier() {
        InputStream is = null;
        try{
			is = new BufferedInputStream(new FileInputStream(filename));
			byte[] c = new byte[1024];
			long count = 0;
			int readChars = 0;
			boolean endsWithoutNewLine = false;
			while ((readChars = is.read(c)) != -1) {
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n')
						++count;
				}
				endsWithoutNewLine = (c[readChars - 1] != '\n');
			}
			if(endsWithoutNewLine) {
				++count;
			}
			return count;
		}catch (IOException e) {
            e.printStackTrace();
		}
		finally {
            try{
                is.close();
            }catch(IOException e2){
                e2.printStackTrace();
                System.exit(-1);
            }
		}
		return 0;
	}
}
