import javax.swing.GroupLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.*;
import java.io.File;

/*
 This program is an database manager. This source file is the GUI part of it
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

/**
 * Cette classe définit la fenêtre principale de l'application
 * @author vinsifroid
 * @since v0.1
 */
final class CMB_gui extends JFrame{
    // Barre de menu
    private JMenuBar menuBar;
    private JMenu Fichier;
    private JMenu aPropos;

    private JMenuItem majBD;
    private JMenuItem rechMot;
    private JMenuItem quit;
    private JMenuItem APropo;

    CMB_gui()
    {
        // Caractéristiques de base
        super();
        this.setTitle("CMB");
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
        // On cree un JTextArea qui contiendra la liste des noms de fichiers
        JTextArea area = new JTextArea();
        JScrollPane spane = new JScrollPane(area);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setVisible(true);
        gl.setHorizontalGroup(gl.createParallelGroup().addComponent(spane));
        gl.setVerticalGroup(gl.createSequentialGroup().addComponent(spane));
        // Barre de menu
        menuBar = new JMenuBar();
        Fichier = new JMenu("Fichier");
        aPropos = new JMenu("A propos");

        quit = new JMenuItem("Quitter");
        quit.addActionListener(event -> {
            CMB.getApp().closeDB();
            System.exit(0);
        });
        majBD = new JMenuItem("Mise à jour");
        majBD.addActionListener(event -> majBD());
        rechMot = new JMenuItem("Rechercher");
        rechMot.addActionListener(actionEvent -> {
            String rep = JOptionPane.showInputDialog(null, "Introduisez un mot à chercher", "Requête",
                    JOptionPane.QUESTION_MESSAGE);
            if(rep != null) {
                java.util.List<String[]> listRes = CMB.searchMovie(rep);
                printToArea(area,listRes,new String[]{"nom","chemin","année","disque dur"});
                //Make sure the new text is visible, even if there
                //was a selection in the text area.
                area.setCaretPosition(area.getDocument().getLength());
            }
        });
        APropo = new JMenuItem("A propos");
        //TODO enregistrer certaines propriétés (version, date de build) dans un fichier texte géré par des properties
        APropo.addActionListener(event -> JOptionPane.showMessageDialog(null, "Version 0.3 : Build du " + CMB.dateActuelle() + "\nDéveloppeur : vinsifroid",
                "A propos", JOptionPane.INFORMATION_MESSAGE));

        //Quand on lance l'app, elle nous affiche directement tous les films
        final java.util.List<String[]> films = CMB.getAllMovies();
        printToArea(area,films,new String[]{"ID","Nom","Chemin","année","genre","Disque Dur"});
        // On ajoute les coomposants
        Fichier.add(majBD);
        Fichier.add(rechMot);
        Fichier.add(quit);
        aPropos.add(APropo);
        menuBar.add(Fichier);
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
        System.out.println("Fait le " + CMB.dateActuelle() + " en " + (endTime - startTime) + " ms");
    }

    private void printToArea(JTextArea area, java.util.List<String[]> films, String[] nomsChamps) {
        final String esp = "\t\t";
        area.setText("");
        for(String champ : nomsChamps) {
            area.append(champ + esp);
        }
        area.append("\n");
        for(String [] listeFilms : films) {
            for (String attribut : listeFilms) {
                // Quand on rencontre un mot de la liste, on l'ajoute a l'area
                area.append(attribut + esp);
            }
            area.append("\n");
        }
    }

    // Permet d'avoir les dimensions de l'écran (largeur + hauteur)
    private Dimension screenSizeDimension()
    {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }
}
