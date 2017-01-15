import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Created by vinsifroid on 11/01/17.
 */
public class CMB_gui extends JFrame{
    // Barre de menu
    private JMenuBar menuBar;
    private JMenu Quitter;
    private JMenu aPropos;
    private JMenu majBD;
    private JMenu rechMot;

    // Layouts
    private CardLayout cl;
    private JPanel card;

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

        // Barre de menu
        Quitter.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent event)
        {System.exit(0);}});

        aPropos.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent event)
        {
            JOptionPane.showMessageDialog(null, "Version 0.1 : Build du " + CMB.dateActuelle() + "\nDéveloppeur : vinsifroid",
                    "A propos", JOptionPane.INFORMATION_MESSAGE);
        }});

        majBD.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                majBD();
            }
        });

        rechMot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        });

        // Panneaux
        // Pile
        card = new JPanel();
        cl = new CardLayout(30,10);
        card.setLayout(cl);
        // Panneau principal
        JPanel princ = new JPanel();
        GridLayout gd = new GridLayout();
        gd.setColumns(3);
        gd.setRows(10);
        gd.setHgap(50);
        gd.setVgap(30);
        princ.setLayout(gd);

        // On ajoute les coomposants
        menuBar.add(Quitter);
        menuBar.add(aPropos);
        menuBar.add(majBD);
        this.setVisible(true);
    }

    private void majBD()
    {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        fc.showOpenDialog(this.getComponent(1));
        final  File selFile = fc.getSelectedFile();

        try {
            CMB.findFiles(selFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
