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
import java.io.File;

/*
 This program is an database manager. This source file is the GUI part of it
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
public class CMB_gui extends JFrame{
    // Barre de menu
    private JMenuBar menuBar;
    private JMenu Fichier;
    private JMenu aPropos;

    private JMenuItem majBD;
    private JMenuItem rechMot;
    private JMenuItem quit;
    private JMenuItem APropo;

    public CMB_gui()
    {
        // Caractéristiques de base
        super();
        this.setTitle("CMB");
        this.setSize(1200,750);
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
            CMB.getfW().fermerFluxWriter();
            CMB.getfR().fermerFluxReader();
            System.exit(0);
        });
        majBD = new JMenuItem("Mise à jour");
        majBD.addActionListener(event -> majBD());
        rechMot = new JMenuItem("Rechercher");
        rechMot.addActionListener(actionEvent -> {
            String rep = JOptionPane.showInputDialog(null, "Introduisez un mot à chercher", "Requête",
                    JOptionPane.QUESTION_MESSAGE);
            if(rep != null) {
                java.util.List<String> listMots = CMB.searchWord(rep);
                area.setText("");
                for (String listMot : listMots) {
                    // Quand on rencontre un mot de la liste, on l'ajoute a l'area
                    area.append(listMot + "\n");
                }
                //Make sure the new text is visible, even if there
                //was a selection in the text area.
                area.setCaretPosition(area.getDocument().getLength());
            }
        });
        APropo = new JMenuItem("A propos");
        APropo.addActionListener(event -> JOptionPane.showMessageDialog(null, "Version 0.1 : Build du " + CMB.dateActuelle() + "\nDéveloppeur : vinsifroid",
                "A propos", JOptionPane.INFORMATION_MESSAGE));

        // On ajoute les coomposants
        Fichier.add(majBD);
        Fichier.add(rechMot);
        Fichier.add(quit);
        aPropos.add(APropo);
        menuBar.add(Fichier);
        menuBar.add(aPropos);
        this.setJMenuBar(menuBar);
        this.getContentPane().add(princ);
        pack();
        this.setVisible(true);
    }

    private void majBD()
    {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        fc.showOpenDialog(this.getComponent(0));
        final  File selFile = fc.getSelectedFile();
        if(selFile != null) {
            CMB.findFiles(selFile);
            CMB.getfW().forcerEcriture();
        }
    }
}
