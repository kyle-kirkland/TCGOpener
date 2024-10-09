import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
//import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;
import java.awt.BorderLayout;
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
        private JPanel hand;
        private JPanel cardHolder;
        private JLabel initialPack;
        private CardLabel boosterCard;
        private PackLabel boosterPack;
        private GridBagConstraints gbConst;
        private JButton fromWelcometoDesk;
        private JButton fromWelcometoCollection;
        private JButton fromCollectiontoDesk;
        private JButton fromDesktoCollection;
        private JButton revealNormal;
        private JButton revealRare;
        private JButton startOpening;
        private JButton newPack;
        private JButton currentCollection;
        private Dimension packsize = new Dimension(300, 500);
        private Dimension cardHolderDimension = new Dimension(1200, 900);
        private Dimension wideScreen = new Dimension(1920, 1080);
        private Image deskImage;
        private String frameTitle;
        private String deskImagePath;
        private String TEMPcardFrontPath; // Until the directories are created
        private String TEMPcardBackPath;  // Until the directories are created
        private String TEMPboosterpackPath; // Until the directories are created
        private boolean revealLast;

        public TCGOpenerGUI() {

            createMainPanel();

            gbConst = new GridBagConstraints();
            startOpening = new JButton("Start Opening");
            currentCollection = new JButton("Show Current Collection");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setVisible(true);

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
            // Add menu to select the reveal method, use Event.getActionCommand() 
            // method in actionPerformed for menus
            desk = new JLayeredPane();
            desk.setPreferredSize(wideScreen);

            deskImagePath = "/backgrounds/Peter_Desk.png";
            TEMPboosterpackPath = "/card_packs/venusaur_pack.jpg";
            deskImage = new ImageIcon(getClass().getResource(deskImagePath)).getImage();
            //packImage = new ImageIcon(getClass().getResource(TEMPboosterpackPath)).getImage();

            int handWidth = (wideScreen.width - packsize.width) - 150;
            int handHeight = (wideScreen.height - packsize.height) - 200;

            initialPack = new JLabel();
            hand = new JPanel();
            hand.setLayout(new BorderLayout());
            hand.setPreferredSize(packsize);
            hand.setBackground(null);
            hand.setOpaque(false);
            hand.setBounds(handWidth, handHeight, packsize.width, packsize.height);
            unpackCards(hand);

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

            cardHolder = new JPanel();
            gbConst.gridwidth = 4;
            gbConst.gridheight = 3;
            cardHolder.setLayout(new GridLayout(3, 4, 5, 5));
            cardHolder.setPreferredSize(cardHolderDimension);
            cardHolder.setBackground(null);
            cardHolder.setOpaque(false);
            cardHolder.setBounds(50, 50, cardHolderDimension.width, cardHolderDimension.height);

            newPack = new JButton("Open New Pack");
            newPack.addActionListener(this);
            revealNormal = new JButton("Reveal Normal Order");
            revealNormal.addActionListener(this);
            revealRare = new JButton("Reveal Rare Last");
            revealRare.addActionListener(this);

            fromDesktoCollection = new JButton("To Collection");
            fromDesktoCollection.addActionListener(this);

            gbConst.gridx = 0;
            gbConst.gridy = 0;
            cardsDisplayed.add(revealNormal, gbConst);

            gbConst.gridx = 1;
            cardsDisplayed.add(revealRare, gbConst);

            gbConst.gridx = 2;
            cardsDisplayed.add(newPack, gbConst);

            gbConst.insets = new Insets(10, 10, 10, 10);
            gbConst.gridx = 0;
            gbConst.gridy = 0;
            gbConst.anchor = GridBagConstraints.LINE_START;
            gbConst.fill = GridBagConstraints.NONE;
            gbConst.weightx = 1.0;
            gbConst.weighty = 1.0;

            cardsDisplayed.add(fromDesktoCollection, gbConst);

            desk.add(cardsDisplayed, JLayeredPane.DEFAULT_LAYER);
            desk.add(hand, JLayeredPane.PALETTE_LAYER);
            desk.add(cardHolder, JLayeredPane.MODAL_LAYER);

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

        }

        @Override
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == fromWelcometoDesk || event.getSource() == fromCollectiontoDesk) {
                CardLayout layout = (CardLayout) root.getLayout();
                layout.show(root, "Desk");
            }
            else if (event.getSource() == fromWelcometoCollection || event.getSource() == fromDesktoCollection) {
                CardLayout layout = (CardLayout) root.getLayout();
                layout.show(root, "Collection");
            }
            else if (event.getSource() == revealNormal) {
                revealLast = false;
            }
            else if (event.getSource() == revealRare) {
                revealLast = true;
            }
            else if (event.getSource() == newPack) {
                unpackCards(hand);
            }
        }

        public void unpackCards(JPanel hand) {
            ArrayList<JLabel> currentPack = assignImagestoPack(buildPack(), revealLast);
            hand.getLayout();
            for (int i = currentPack.size(); i > 0; i--) {
                hand.add(currentPack.get(i - 1), BorderLayout.CENTER);
            }
        }

        public int randCommon() {
            Random rng = new Random();
            int common = rng.nextInt(32) + 1;
            return common;
        }
        public int randUncommon() {
            Random rng = new Random();
            int uncommon = rng.nextInt(32) + 1;
            return uncommon;
        }
        public int randRare() {
            Random rng = new Random();
            int rare = rng.nextInt(16) + 1;
            return rare;
        }
        public int randEnergy() {
            Random rng = new Random();
            int energy = rng.nextInt(6) + 1;
            return energy;
        }
        public int[] noDupes(int[] cardList) {
            int[] newCardList = new int[cardList.length];
            for (int i = 0; i < cardList.length; i++) {
                newCardList[i] = cardList[i];
            }

            for (int i = 0; i < newCardList.length; i++) {
                for (int j = 0; j < newCardList.length; j++) {
                    if (i != j && newCardList[i] == newCardList[j]) {
                        while (newCardList[i] == newCardList[j]) {                            
                            if (i < 5) {
                                newCardList[i] = randCommon();
                            }
                            else if (i >= 5 || i < 7) {
                                newCardList[i] = randEnergy();
                            }
                            else if (i >= 8) {
                                newCardList[i] = randRare();
                            }
                            else if (i >= 9 || i < 12) {
                                newCardList[i] = randUncommon();
                            }
                        }
                    }
                }
            }
            return newCardList;
        }
        public int[] buildPack() {
            /**      
            *      - 5 commons
            *      - 2 energy cards 
            *      - 1 rare
            *      - 3 uncommon
            */

            int[] pack = new int[12];
            if (!revealLast) {
                pack[0] = 0;
                pack[1] = randCommon();
                pack[2] = randCommon();
                pack[3] = randCommon();
                pack[4] = randCommon();
                pack[5] = randCommon();
                pack[6] = randEnergy();
                pack[7] = randEnergy();
                pack[8] = randRare();
                pack[9] = randUncommon();
                pack[10] = randUncommon();
                pack[11] = randUncommon();
            }
            else {
                pack[0] = 0;
                pack[1] = randCommon();
                pack[2] = randCommon();
                pack[3] = randCommon();
                pack[4] = randCommon();
                pack[5] = randCommon();
                pack[6] = randEnergy();
                pack[7] = randEnergy();
                pack[8] = randUncommon();
                pack[9] = randUncommon();
                pack[10] = randUncommon();
                pack[11] = randRare();
            }

            int[] newPack = new int[12];
            newPack = noDupes(pack);

            return newPack;
        }
        // Might change this to ArrayList of JLabels and add the ImageIcons within method
        public ArrayList<JLabel> assignImagestoPack(int[] pack, boolean revealMethod) {
            ArrayList<JLabel> packImages = new ArrayList<JLabel>();
            String commonPath = "/cardImages/largeCards/common/common(";
            String energyPath = "/cardImages/largeCards/energy/energy(";
            String rarePath = "/cardImages/largeCards/rare/rare(";
            String holoPath = "/cardImages/largeCards/holo/holo(";
            String uncommonPath = "/cardImages/largeCards/uncommon/uncommon(";
            boolean rareLast = revealMethod;
            boosterPack = new PackLabel();

            // Normal card reveal method
            if (!rareLast) {
                for (int i = 0; i < pack.length; i++) {
                    if (i < 5) {
                        String currentCommon = commonPath + pack[i] + ").jpg";
                        boosterCard = new CardLabel(currentCommon);
                        packImages.add(boosterCard);
                    }
                    else if (i >= 5 && i < 7) {
                        String currentEnergy = energyPath + pack[i] + ").jpg";
                        boosterCard = new CardLabel(currentEnergy);
                        packImages.add(boosterCard);
                    }
                    else if (i == 7) {
                        Random rng = new Random();
                        if (rng.nextInt(4) == 3) {
                            String currentHolo = holoPath + pack[i] + ").jpg";
                            boosterCard = new CardLabel(currentHolo);
                            packImages.add(boosterCard);
                        }
                        else {
                            String currentRare = rarePath + pack[i] + ").jpg";
                            boosterCard = new CardLabel(currentRare); 
                            packImages.add(boosterCard);
                        }
                    }
                    else if (i >= 9 && i <= 10) {
                        String currentUncommon = uncommonPath + pack[i] + ").jpg";
                        boosterCard = new CardLabel(currentUncommon);
                        packImages.add(boosterCard);
                    }
                }
                
            }
            // Reveal rare card last method
            if (rareLast) {
                for (int j = 0; j < pack.length; j++) {
                    if (j < 5) {
                        String currentCommon = commonPath + pack[j] + ").jpg";
                        boosterCard = new CardLabel(currentCommon);
                        packImages.add(boosterCard);
                    }
                    else if (j >= 5 && j < 7) {
                        String currentEnergy = energyPath + pack[j] + ").jpg";
                        boosterCard = new CardLabel(currentEnergy);
                        packImages.add(boosterCard);
                    }
                    else if (j >= 7 && j < 10) {
                        String currentUncommon = uncommonPath + pack[j] + ").jpg";
                        boosterCard = new CardLabel(currentUncommon);
                        packImages.add(boosterCard);
                    }
                    else if ( j == 10) {
                        Random rng = new Random();
                        if (rng.nextInt(4) == 3) {
                            String currentHolo = holoPath + pack[j] + ").jpg";
                            boosterCard = new CardLabel(currentHolo);
                            packImages.add(boosterCard);
                        }
                        else {
                            String currentRare = rarePath + pack[j] + ").jpg";
                            boosterCard = new CardLabel(currentRare);
                            packImages.add(boosterCard);   
                        }
                    }
                }
            }

            packImages.add(0, boosterPack);
            return packImages;
        }
        // Rewrite to change size of card, using an additional JPanel or resizing the JLabel? Idk yet
        public static ImageIcon resizeImage(String imgPath, int width, int height) {
            ImageIcon icon = new ImageIcon(imgPath);
            Image image = icon.getImage();
            Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

            return new ImageIcon(resizedImage);
        }

        public class PackLabel extends JLabel{
            ImageIcon randomPackImage = randomPack();
            public String type = "pack";
            public boolean isBig = false;

            public PackLabel() {

                setIcon(randomPackImage);
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        if (event.isShiftDown()) {
                            swapCardSize();
                        }
                        else {
                            removePack();
                        }
                    }
                });
            }
            public void removePack() {
                hand.remove(this);
            }
            private ImageIcon randomPack() {
                String venusaurPackPath = "/card_packs/venusaur_pack.jpg";
                String charizardPackPath = "/card_packs/charizard_pack.jpg";
                String blastoisePackPath = "/card_packs/blastoise_pack.jpg";
                ImageIcon randomPackImage = new ImageIcon();
                ImageIcon venusaurPack = new ImageIcon(getClass().getResource(venusaurPackPath));
                ImageIcon charizardPack = new ImageIcon(getClass().getResource(charizardPackPath));
                ImageIcon blastoisePack = new ImageIcon(getClass().getResource(blastoisePackPath));
                Random rng2 = new Random();
                int num = rng2.nextInt(2);
                if (num == 0) {
                    randomPackImage = venusaurPack;
                }
                else if (num == 1) {
                    randomPackImage = charizardPack;
                }
                else if (num == 2) {
                    randomPackImage = blastoisePack;
                }
                return randomPackImage;
            }
            // Need to rewrite this method, no longer using Images need to resize ImageIcons
            public void swapCardSize() {
                //if (!isBig) {
                //    setIcon(resizeImage(cardFrontImagePath, bigSizeX, bigSizeY));
                //    isBig = true;
                //} else {
                //    setIcon(resizeImage(cardFrontImagePath, sizeX, sizeY));
                //    isBig = false;
                //}
            }
        }

        public class CardLabel extends JLabel{

            //private String cardBackImagePath; Not sure if needed
            private String cardFrontImagePath;
            private final int sizeX = 130;
            private final int sizeY = 180;
            private final int bigSizeX = 200;
            private final int bigSizeY = 250;
            public boolean isBig = false;
            
            // Need to make sure cards are properly sorted in specific folders to be used here
            public CardLabel(String cardFrontImagePath) {
                // String cardBackImagePath = "/cardBacks/cardBack.png"; Not sure if needed
                // setIcon(resizeImage(this.cardBackImagePath, sizeX, sizeY)); Not sure if needed
                setIcon(new ImageIcon(getClass().getResource(cardFrontImagePath))); // *****WHERE I LEFT OFF saying the location is null, need to specify location 


                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        if (event.isShiftDown()) {
                            swapCardSize();
                        }
                        else {
                            fromHandtoDesk();
                        }
                    }
                });
            }
            public void fromHandtoDesk() {
                // moving from hand to cardHolder
                hand.remove(this);
            }

            public void swapCardSize() {
                if (!isBig) {
                    setIcon(resizeImage(cardFrontImagePath, bigSizeX, bigSizeY));
                    isBig = true;
                } else {
                    setIcon(resizeImage(cardFrontImagePath, sizeX, sizeY));
                    isBig = false;
                }
            }
        }
        public static void main(String[] args) {
            new TCGOpenerGUI();
        }
    }