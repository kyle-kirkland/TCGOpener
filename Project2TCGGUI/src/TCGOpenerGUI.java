import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
//import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Color;
//import javax.swing.*;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

/**
 * - Possible way to run program in a browser: https://cheerpj.com/cheerpj-core/
 * - Things to look up: 
 *      - paint() method, what it does and how to use it
 *      - do I need to create a custom class that extends JPanel
 *      - how to better implement GridBagConstraints with GridBagLayout 
 *          - see if there is a better alternative to GridBagLayout (upon initial research looks like GridBagLayout is best bet)
 *          - use CardLayout for cycling through a freshly opened booster pack
 * - Show booster pack wrapping before opening
 * - Score could be points based off of rarity of card pulled
 * - Give option to reorganize opened cards so that the rare is on the bottom
 * - Maybe add a sleeve animation for super rares
 * - Layout opened cards beside each other
 * - Each booster pack has 11 cards
 *      - 1 rare
 *      - 3 uncommon 
 *      - 5 commons
 *      - 2 energy cards
 * - View hierarchy (priority = number of *):
 *      - Main JFrame ***
 *          - root JPanel (CardLayout) ***
 *              - startscreen JPanel (GridBagLayout) **
 *              - collection JPanel (GridBagLayout) **
 *              - desk JLayeredPane ***
 *                  - cardsUnpacking JPanel (GridBagLayout) ***
 *                  - cardsDisplayed JPanel (GridBagLayout) ***
 *                      - boosterCard CardPanel(or maybe CardLabel, idk yet) (CardLayout) ***
 *                      - boosterPack CardPanel (Default Layout)
 *                  - buttons JPanel (GridBagLayout) **
 *                  - cardDisplayArea JPanel (GridBagLayout) **
 *                  - wrapperDisplayArea JPanel (Default Layout) *
 *              - collection JPanel (GridBagLayout) **
 * 
 */

    public class TCGOpenerGUI extends JFrame implements ActionListener {
        private JPanel root;
        private JPanel welcomescreen;
        private JPanel collection;
        private JLayeredPane desk;
        private JPanel cardsDisplayed;
        private JPanel cardsUnpacking;
        private JLabel boosterPack;
        private CardLabel boosterCard;
        private GridBagConstraints gbConst;
        private JButton fromWelcometoDesk;
        private JButton fromWelcometoCollection;
        private JButton fromCollectiontoDesk;
        private JButton fromDesktoCollection;
        private JButton startOpening;
        private JButton newPack;
        private JButton currentCollection;
        private Dimension typicalRes = new Dimension(1024, 768);
        private Dimension packsize = new Dimension(300, 500);
        private Dimension wideScreen = new Dimension(1920, 1080);
        private Image deskImage;
        private Image packImage;
        private String frameTitle;
        private String deskImagePath;
        private String TEMPcardFrontPath; // Until the directories are created
        private String TEMPcardBackPath;  // Until the directories are created
        private String TEMPboosterpackPath; // Until the directories are created

        public TCGOpenerGUI() {

            createMainPanel();

            gbConst = new GridBagConstraints();
            boosterPack = new JLabel(TEMPboosterpackPath);
            boosterCard = new CardLabel(TEMPcardFrontPath, TEMPcardBackPath);

            startOpening = new JButton("Start Opening");
            newPack = new JButton("Open New Pack");
            currentCollection = new JButton("Show Current Collection");

        }

        private JPanel createWelcomescreenPanel() {
            welcomescreen = new JPanel(new GridBagLayout());
            welcomescreen.setPreferredSize(wideScreen);

            gbConst = new GridBagConstraints();

            fromWelcometoDesk = new JButton("To Desk");
            fromWelcometoCollection = new JButton("To Collection");

            fromWelcometoDesk.addActionListener(this);
            fromWelcometoCollection.addActionListener(this);

            gbConst.insets = new Insets(10, 10, 10, 10);
            gbConst.gridx = 0;
            gbConst.gridy = 0;
            gbConst.anchor = GridBagConstraints.CENTER;
            gbConst.fill = GridBagConstraints.NONE;
            gbConst.weightx = 1.0;
            gbConst.weighty = 1.0;

            welcomescreen.add(fromWelcometoDesk, gbConst);

            gbConst.gridy = 1;
            welcomescreen.add(fromWelcometoCollection, gbConst);

            return welcomescreen;
        }

        private JPanel createCollectionPanel() {
            collection = new JPanel(new GridBagLayout());
            collection.setPreferredSize(wideScreen);

            gbConst = new GridBagConstraints();

            fromCollectiontoDesk = new JButton("To Desk");
            fromCollectiontoDesk.addActionListener(this);

            gbConst.insets = new Insets(10, 10, 10, 10);
            gbConst.gridx = 0;
            gbConst.gridy = 0;
            gbConst.anchor = GridBagConstraints.CENTER;
            gbConst.fill = GridBagConstraints.NONE;
            gbConst.weightx = 1.0;
            gbConst.weighty = 1.0;

            collection.add(fromCollectiontoDesk, gbConst);

            return collection;
        }

        private JLayeredPane createDeskPane() {
            desk = new JLayeredPane();
            desk.setPreferredSize(wideScreen);

            TEMPboosterpackPath = "/card_packs/charizard_pack.png";
            deskImagePath = "/backgrounds/wood_desk.png";
            deskImage = new ImageIcon(getClass().getResource(deskImagePath)).getImage();
            packImage = new ImageIcon(getClass().getResource(TEMPboosterpackPath)).getImage();

            int cardsUnpackingWidth = (wideScreen.width - packsize.width)/ 2;
            int cardsUnpackingHeight = (wideScreen.height - packsize.height)/ 2;
            cardsUnpacking = new JPanel() {
                @Override
                public void paintComponent(Graphics graphic) {
                    super.paintComponent(graphic);
                    graphic.drawImage(packImage, 0, 0, getWidth(), getHeight(), this);
                }
            };
            cardsUnpacking.setPreferredSize(packsize);
            cardsUnpacking.setBackground(Color.GRAY);
            cardsUnpacking.setBounds(cardsUnpackingWidth, cardsUnpackingHeight, packsize.width, packsize.height);

            cardsDisplayed = new JPanel() {
                @Override
                public void paintComponent(Graphics graphic) {
                    super.paintComponent(graphic);
                    graphic.drawImage(deskImage, 0, 0,getWidth(), getHeight(), this);
                }
            };
            cardsDisplayed.setPreferredSize(wideScreen);
            cardsDisplayed.setBackground(Color.GRAY);
            cardsDisplayed.setBounds(0, 0, wideScreen.width, wideScreen.height);

            fromDesktoCollection = new JButton("To Collection");
            fromDesktoCollection.addActionListener(this);

            gbConst.insets = new Insets(10, 10, 10, 10);
            gbConst.gridx = 0;
            gbConst.gridy = 0;
            gbConst.anchor = GridBagConstraints.LINE_START;
            gbConst.fill = GridBagConstraints.NONE;
            gbConst.weightx = 1.0;
            gbConst.weighty = 1.0;

            cardsDisplayed.add(fromDesktoCollection, gbConst);

            JLabel test = new JLabel("test");
            cardsUnpacking.add(test);

            desk.add(cardsDisplayed, JLayeredPane.DEFAULT_LAYER);
            desk.add(cardsUnpacking, JLayeredPane.PALETTE_LAYER);

            return desk;
        }

        private void createMainPanel() {
            root = new JPanel(new CardLayout());
            root.setPreferredSize(wideScreen);

            welcomescreen = createWelcomescreenPanel();
            collection = createCollectionPanel();
            desk = createDeskPane();

            root.add(welcomescreen, "WelcomeScreen");
            root.add(collection, "Collection");
            root.add(desk, "Desk");
            
            CardLayout layout = (CardLayout) root.getLayout();
            layout.show(root, "WelcomeScreen");

            frameTitle = new String("TCG Opener");
            setTitle(frameTitle);
            setSize(wideScreen);
            setLocationRelativeTo(null);
            add(root);

            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == fromWelcometoDesk || event.getSource() == fromCollectiontoDesk) {
                CardLayout layout = (CardLayout) root.getLayout();
                layout.show(root, "Desk");
            } else if (event.getSource() == fromWelcometoCollection || event.getSource() == fromDesktoCollection) {
                CardLayout layout = (CardLayout) root.getLayout();
                layout.show(root, "Collection");
            }
        }

        public int[] buildPack() {
            int[] pack = new int[11];

            return pack;
        }

        public ArrayList<ImageIcon> assignImagestoPack() {
            ArrayList<ImageIcon> packImages = new ArrayList<ImageIcon>();

            return packImages;
        }
        /**
        * Resizes an image to the specified width and height.
        * 
        * <p>
        * This method is currently part of the TCGOpenerGUI class as resizing images 
        * is only used within the GUI to handle card resizing. If image resizing 
        * is needed in other parts of the application in the future, this may be 
        * consider refactoring this method into a utility class (e.g., ImageUtils) 
        * for better reusability and separation of concerns.
        * </p>
        * 
        * @param imagePath The file path of the image to be resized.
        * @param width The desired width of the resized image.
        * @param height The desired height of the resized image.
        * @return A new ImageIcon with the resized image.
        */
        public static ImageIcon resizeImage(String imgPath, int width, int height) {
            ImageIcon icon = new ImageIcon(imgPath);
            Image image = icon.getImage();
            Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

            return new ImageIcon(resizedImage);
        }
        public class CardLabel extends JLabel{

            private String cardBackImagePath;
            private String cardFrontImagePath;
            private final int sizeX = 130;
            private final int sizeY = 180;
            private final int bigSizeX = 200;
            private final int bigSizeY = 250;
            boolean isRevealed = false;
            boolean isBig = false;
            
            // Need to make sure cards are properly sorted in specific folders to be used here
            public CardLabel(String cardFrontImagePath, String cardBackImagePath) {
                
                setIcon(resizeImage(this.cardBackImagePath, sizeX, sizeY));


                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        if (!isRevealed) {
                            if (event.getClickCount() == 1) {
                                setIcon(resizeImage(cardFrontImagePath, sizeX, sizeY));
                                isRevealed = true;
                            }
                        }   
                        else {
                            if (event.isShiftDown() || event.getClickCount() == 2) {
                                swapCardSize();
                            }
                        }
                    }
                });
            }

            public void swapCardSize() {
                if (!isBig) {
                    setIcon(resizeImage(cardFrontImagePath, bigSizeX, bigSizeY));
                    isBig = true;
                } else {
                    setIcon(resizeImage(cardBackImagePath, sizeX, sizeY));
                    isBig = false;
                }
            }
        }
        public static void main(String[] args) {
            new TCGOpenerGUI();
        }
    }