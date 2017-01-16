import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * Created by vinsifroid on 11/01/17.
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

        // Barre de menu
        menuBar = new JMenuBar();
        Fichier = new JMenu("Fichier");
        Fichier.setMnemonic(KeyEvent.VK_A);
        aPropos = new JMenu("A propos");
        aPropos.setMnemonic(KeyEvent.VK_B);

        quit = new JMenuItem("Quitter");
        quit.addActionListener(event -> {
            CMB.getfW().fermerFluxWriter();
            CMB.getfR().fermerFluxReader();
            System.exit(0);
        });
        majBD = new JMenuItem("Mise à jour");
        majBD.setMnemonic(KeyEvent.VK_C);
        majBD.addActionListener(event -> majBD());
        rechMot = new JMenuItem("Rechercher");
        rechMot.setMnemonic(KeyEvent.VK_D);
        rechMot.addActionListener(actionEvent -> {
            String rep = JOptionPane.showInputDialog(null, "Introduisez un mot à chercher", "Requête",
                    JOptionPane.QUESTION_MESSAGE);
            java.util.List<String> listMots = CMB.searchWord(rep);
            // On cree un JTextArea qui contiendra la liste des noms de fichiers
            JTextArea area = new JTextArea();
            JScrollPane spane = new JScrollPane(area);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            for(int i=0;i<listMots.size(); i++)
            {
                // Quand on rencontre un mot de la liste, on l'ajoute a l'area
                area.append(listMots.get(i) + "\n");

            }
            area.setVisible(true);
            gl.setHorizontalGroup(gl.createParallelGroup().addComponent(spane));
            gl.setVerticalGroup(gl.createSequentialGroup().addComponent(spane));
            this.getContentPane().removeAll();
            this.getContentPane().add(princ);
            this.getContentPane().revalidate();
            this.getContentPane().repaint();
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
        this.setVisible(true);
    }

    private void majBD()
    {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        fc.showOpenDialog(this.getComponent(0));
        final  File selFile = fc.getSelectedFile();

        CMB.findFiles(selFile);
        CMB.getfW().forcerEcriture();
    }
}
