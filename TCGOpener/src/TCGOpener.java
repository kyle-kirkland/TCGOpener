import java.awt.*;      /**
                            Using:  GridBagConstraints, GridBagLayout, FlowLayout, BorderLayout, CardLayout
                                    Graphics, Image, Insets, Dimension, Color
                        */
import java.awt.event.*;/**
                            Using: ActionEvent, ActionListener, KeyEvent, KeyListener, MouseAdapter, MouseEvent
                        */
import java.util.*;     /**
                            Using: ArrayList, Random, Scanner     
                        */
import javax.swing.*;   /**
                            Using: JFrame, JButton, JComboBox, JPanel, JLabel, JLayeredPane, ImageIcon, SwingUtilities, SwingWorker
                        */
import java.io.*;       /**
                            Using: BufferedReader, FileNotFoundException, FileWriter, IOException, InputStream, InputStreamReader
                        */

public class TCGOpener extends JFrame {
    private WelcomeScreen welcomeScreen;
    private DeskScreen deskScreen;
    private CollectionScreen collectionScreen;
    private Dimension wideScreen = new Dimension(1920, 1080);

    public TCGOpener() {
        buildMainPanel();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void buildMainPanel() {
        JPanel root = new JPanel(new CardLayout());
        root.setPreferredSize(wideScreen);

        welcomeScreen = new WelcomeScreen();
        deskScreen = new DeskScreen();
        collectionScreen = new CollectionScreen();

        root.add("Welcome Screen", welcomeScreen);
        root.add("Desk Screen", deskScreen);
        root.add("Collection Screen", collectionScreen);

        setTitle("TCG Opener");
        setSize(wideScreen);
        add(root);
    }
        

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TCGOpener tcgOpener = new TCGOpener();
            tcgOpener.buildCollectionFromCSVs();
        });
    }
}