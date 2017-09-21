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
import main.CMB;

import javax.swing.GroupLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;

/**
 * Cette classe définit la fenêtre principale de l'application
 * @author vinsifroid
 * @since v0.1
 */
final public class CMB_gui extends JFrame{

    public CMB_gui()
    {
        // Caractéristiques de base
        super();
        this.setTitle("Central Movie dataBase");
        this.setSize(screenSizeDimension());
        this.setLocationRelativeTo(null);
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Caractéristiques

        // Panneaux
        // Panneau principal
        JPanel princ = new JPanel();
        GroupLayout gl = new GroupLayout(princ);
        princ.setLayout(gl);
        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);
        //On crée la table principale qui contiendra les données
        DataTableModel model = new DataTableModel(CMB.getAllMovies());
        JTable table = new JTable(model);
        table.setDefaultRenderer(Integer.class, new IntegerCellRenderer());
        //The scrollPane for table
        //TODO vérifier si c'est bien afficher sur le contentPane
        JScrollPane spane = new JScrollPane(table);
        gl.setHorizontalGroup(gl.createParallelGroup().addComponent(spane));
        gl.setVerticalGroup(gl.createSequentialGroup().addComponent(spane));
        // Barre de menu
        JMenuBar menuBar = new JMenuBar();
        JMenu fichier = new JMenu("Fichier");
        JMenu view = new JMenu("Affichage");
        JMenu aPropos = new JMenu("A propos");

        //Items du menu
        //FICHIER
        JMenuItem quit = new JMenuItem("Quitter");
        quit.addActionListener(event -> {
            CMB.getApp().closeDB();
            System.exit(0);
        });
        JMenuItem majBD = new JMenuItem("Mise à jour");
        majBD.addActionListener(event -> majBD());
        JMenuItem rechMot = new JMenuItem("Rechercher");
        rechMot.addActionListener(actionEvent -> {
            String rep = JOptionPane.showInputDialog(null, "Introduisez un mot à chercher", "Requête",
                    JOptionPane.QUESTION_MESSAGE);
            if(rep != null) {
                Movie[] movies = CMB.searchMovie(rep);
                if(movies != null) {
                    if(movies[0] != null) {
                        model.clearAll();
                        addAllMoviesToModel(model,movies);
                    }
                }else {
                    JOptionPane.showMessageDialog(null,"Aucun résultat trouvé !","Résultat",JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        //VIEW
        JMenuItem movieView = new JMenuItem("Vue films");
        movieView.addActionListener(event -> {
            //TODO
        });
        JMenuItem genreView = new JMenuItem("Vue genres");
        genreView.addActionListener(actionEvent -> {
            //TODO
        });
        //A PROPOS
        JMenuItem about = new JMenuItem("A propos");
        //TODO enregistrer certaines propriétés (version, date de build) dans un fichier texte géré par des properties
        about.addActionListener(event -> JOptionPane.showMessageDialog(null, "Version 0.4.2 : Build du " + CMB.dateActuelle() + "\nDéveloppeur : vinsifroid",
                "A propos", JOptionPane.INFORMATION_MESSAGE));

        //Quand on lance l'app, elle nous affiche directement tous les films
        final Movie[] films = CMB.getAllMovies();
        addAllMoviesToModel(model,films);
        // On ajoute les composants
        fichier.add(majBD);
        fichier.add(rechMot);
        fichier.add(quit);
        view.add(movieView);
        view.add(genreView);
        aPropos.add(about);
        menuBar.add(fichier);
        menuBar.add(view);
        menuBar.add(aPropos);
        this.setJMenuBar(menuBar);
        this.getContentPane().add(princ);
        this.setVisible(true);
    }

    private void majBD()
    {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        fc.showOpenDialog(this.getComponent(0));
        final File selFile = fc.getSelectedFile();
        final long startTime = System.currentTimeMillis();
        System.out.println("Mise à jour Base de donnee - Initialisation...");
        if(selFile != null) {
            CMB.findFiles(selFile);
        }
        final long endTime = System.currentTimeMillis();
        System.out.println(/*"Fait le " + CMB.dateActuelle() + " en " + */(endTime - startTime)/* + " ms"*/);
    }

    private void addAllMoviesToModel(DataTableModel model, Movie[] movies) {
        //TODO Do a perf test to see if this is more efficient than do a 'addAll' method in the model
        for(Movie mov : movies) {
            model.addMovie(mov);
        }
    }

    // Permet d'avoir les dimensions de l'écran (largeur + hauteur)
    private Dimension screenSizeDimension()
    {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }
}
