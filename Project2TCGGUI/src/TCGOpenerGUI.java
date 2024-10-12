import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
//import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;
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
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

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
        private JLayeredPane hand;
        private JPanel cardsDisplayed;
        private JPanel cardHolder;
        private JPanel collectionButtons;
        private JPanel binder;
        private JPanel leftPage;
        private JPanel rightPage;
        private CardLabel boosterCard;
        private CardLabel boosterPack;
        private GridBagConstraints gbConst;
        private JButton fromWelcometoDesk;
        private JButton fromWelcometoCollection;
        private JButton fromCollectiontoDesk;
        private JButton fromDesktoCollection;
        private JButton revealNormal;
        private JButton revealRare;
        private JButton newPack;
        private JButton currentCollection;
        private JButton nextPage;
        private JButton previousPage;
        private ArrayList<CardLabel> cardCollection = new ArrayList<CardLabel>();
        private Dimension packsize = new Dimension(300, 515);
        private Dimension cardHolderDimension = new Dimension(1200, 1000);
        private Dimension wideScreen = new Dimension(1920, 1080);
        private Dimension smallScreen = new Dimension(1280, 720);
        private Image binderImage;
        private Image deskImage;
        private SwingUtilities SU;
        private String frameTitle;
        private String deskImagePath;
        private boolean rareLast = false;
        private int handStartX = (wideScreen.width - packsize.width) - 150;
        private int handStartY = (wideScreen.height - packsize.height) - 200;
        private int sshandStartX = (smallScreen.width - packsize.width) - 150;
        private int sshandStartY = (smallScreen.height - packsize.height) - 200;
        private int collectionNextIndex = 0;
        private int collectionPreviousIndex = 0;

        public TCGOpenerGUI() {

            createMainPanel();
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setVisible(true);

        }

        private JPanel createWelcomescreenPanel() {
            welcomescreen = new JPanel(new GridBagLayout());
            welcomescreen.setPreferredSize(wideScreen); 

            gbConst = new GridBagConstraints();

            fromWelcometoDesk = new JButton("Start Opening Packs");
            fromWelcometoDesk.addActionListener(event -> switchPanel("Desk")); // *** LAMBDA ***

            fromWelcometoCollection = new JButton("Show Current Collection");            
            fromWelcometoCollection.addActionListener(event -> switchPanel("Collection"));  // *** LAMBDA ***

            gbConst.insets = new Insets(5, 5, 5, 5);
            gbConst.gridx = 0;
            gbConst.gridy = 0;
            gbConst.anchor = GridBagConstraints.CENTER;
            gbConst.fill = GridBagConstraints.NONE;
            welcomescreen.add(fromWelcometoDesk, gbConst);

            gbConst.gridy = 1;
            welcomescreen.add(fromWelcometoCollection, gbConst);

            return welcomescreen;
        }

        private JPanel createCollectionPanel() {
            collection = new JPanel(new GridBagLayout());
            collection.setBounds(0, 0, wideScreen.width, wideScreen.height);

            collectionButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
            collectionButtons.setSize(wideScreen.width, 40);
            collectionButtons.setOpaque(false);

            String binderImagePath = "";
            binderImage = new ImageIcon(getClass().getResource(binderImagePath)).getImage();
            binder = new JPanel() { // *** MIGHT NEED TO CHANGE TO JLayeredPane FOR CARDS TO APPEAR OVER BINDER PAGES ***
                @Override
                public void paintComponent(Graphics graphic) {
                    super.paintComponent(graphic);
                    graphic.drawImage(binderImage, 0, 40, getWidth(), getHeight(), this);
                }
            };
            binder.setLayout(new GridBagLayout());
            int newheight = ((wideScreen.height) - 40);
            binder.setSize(wideScreen.width, newheight);
            binder.setOpaque(true);
            binder.setBackground(Color.BLUE);
            binder.setVisible(true);

            int newwidth = ((wideScreen.width) / 2);
            leftPage = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 25));
            leftPage.setSize(newwidth, newheight);
            leftPage.setOpaque(true);
            leftPage.setBackground(Color.GREEN);
            leftPage.setVisible(true);

            rightPage = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 25));
            rightPage.setSize(newwidth, newheight);
            rightPage.setOpaque(true);
            rightPage.setBackground(Color.ORANGE);
            rightPage.setVisible(true);

            GridBagConstraints gbConst1 = new GridBagConstraints();

            gbConst1.gridx = 0;
            gbConst1.gridy = 0;
            gbConst1.weightx = 0.8;
            gbConst1.weighty = 0.8;
            gbConst1.anchor = GridBagConstraints.CENTER;
            gbConst1.fill = GridBagConstraints.BOTH;
            gbConst1.insets = new Insets(40, 40, 40, 40);
            binder.add(leftPage, gbConst1);

            gbConst1.gridx = 1;
            binder.add(rightPage, gbConst1);

            GridBagConstraints gbConst2 = new GridBagConstraints();

            fromCollectiontoDesk = new JButton("To Desk");
            fromCollectiontoDesk.addActionListener(event -> switchPanel("Desk")); // *** LAMBDA ***
            nextPage = new JButton("Next Page");
            nextPage.addActionListener(this);
            previousPage = new JButton("Previous Page");
            previousPage.addActionListener(this);

            collectionButtons.add(fromCollectiontoDesk);
            collectionButtons.add(nextPage);
            collectionButtons.add(previousPage);

            gbConst2.gridx = 0;
            gbConst2.gridy = 0;
            gbConst2.anchor = GridBagConstraints.NORTH;
            collection.add(collectionButtons, gbConst2);

            gbConst2.gridy = 1;
            gbConst2.weightx = 1;
            gbConst2.weighty = 1;
            gbConst2.fill = GridBagConstraints.BOTH;
            collection.add(binder, gbConst2);
            revalidate();
            repaint();

            return collection;
        }

        private JLayeredPane createDeskPane() {
            // Add menu to select the reveal method, use Event.getActionCommand() 
            // method in actionPerformed for menus
            desk = new JLayeredPane();
            desk.setPreferredSize(wideScreen); 

            deskImagePath = "/backgrounds/wood_desk.png";
            String TEMPboosterpackPath = "/card_packs/venusaur_pack.jpg";
            deskImage = new ImageIcon(getClass().getResource(deskImagePath)).getImage();

            cardsDisplayed = new JPanel() {
                @Override
                public void paintComponent(Graphics graphic) {
                    super.paintComponent(graphic);
                    graphic.drawImage(deskImage, 0, 0, getWidth(), getHeight(), this);
                }
            };
            cardsDisplayed.setPreferredSize(wideScreen);
            cardsDisplayed.setBackground(Color.GRAY);
            cardsDisplayed.setBounds(0, 0, wideScreen.width, wideScreen.height); 

            hand = new JLayeredPane();
            hand.setPreferredSize(packsize);
            hand.setOpaque(false);
            hand.setVisible(true);
            hand.setBounds(handStartX, handStartY, packsize.width, packsize.height); 

            cardHolder = new JPanel();
            cardHolder.setLayout(new FlowLayout(FlowLayout.LEFT, 40, 15));
            cardHolder.setBackground(null);
            cardHolder.setOpaque(false);
            cardHolder.setBounds(25, 25, cardHolderDimension.width, cardHolderDimension.height);

            newPack = new JButton("Open New Pack");
            newPack.addActionListener(event -> openNewPack(hand)); // *** LAMBDA w/ SwingWorker IMPLEMENTATION ***
            revealNormal = new JButton("Reveal Normal Order");
            revealNormal.addActionListener(event -> revealCards(false)); // *** LAMBDA ***
            revealRare = new JButton("Reveal Rare Last");
            revealRare.addActionListener(event -> revealCards(true)); // *** LAMBDA ***
            fromDesktoCollection = new JButton("To Collection");
            fromDesktoCollection.addActionListener(event -> switchPanel("Collection")); // *** LAMBDA ***

            gbConst.insets = new Insets(10, 10, 10, 10);
            gbConst.gridx = 0;
            gbConst.gridy = 0;
            gbConst.anchor = GridBagConstraints.LINE_START;
            gbConst.fill = GridBagConstraints.NONE;
            cardsDisplayed.add(fromDesktoCollection, gbConst);

            gbConst.gridx = 1;
            cardsDisplayed.add(revealNormal, gbConst);

            gbConst.gridx = 2;
            cardsDisplayed.add(revealRare, gbConst);

            gbConst.gridx = 3;
            cardsDisplayed.add(newPack, gbConst);

            desk.add(hand, JLayeredPane.PALETTE_LAYER);
            desk.add(cardHolder, JLayeredPane.PALETTE_LAYER);
            desk.add(cardsDisplayed, JLayeredPane.DEFAULT_LAYER);
            desk.revalidate();
            desk.repaint();

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
            add(root);
        }
        /** CardLabel class that is used to create each card and pack */
        public class CardLabel extends JLabel{
            private String name; // need to name all cards in folders, just add between ")" and ".jpg" or whatever file ext
            private String type;
            private String cardFrontImagePath;
            private final int sizeX = 130;
            private final int sizeY = 180;
            private final int bigSizeX = 200;
            private final int bigSizeY = 250;
            public boolean isBig = false;
            
            // Need to make sure cards are properly sorted in specific folders to be used here
            public CardLabel(String cardFrontImagePath) {
                this.type = "card";
                this.cardFrontImagePath = cardFrontImagePath;
                ImageIcon resizedImage = new ImageIcon();
                resizedImage = resizeCardImage(new ImageIcon(getClass().getResource(cardFrontImagePath)));
                setIcon(resizedImage);
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        if (event.isShiftDown()) {
                            //swapCardSize();
                        }
                        else {
                        //fromHandtoDesk();
                        }
                    }
                });
            }

            public CardLabel(ImageIcon packImage) {
                this.type = "pack";
                setIcon(packImage);
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        if (event.isShiftDown()) {
                            //swapCardSize();
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
                hand.revalidate();
                hand.repaint();
                if (this.type.equalsIgnoreCase("card")) {
                    String smallCardImagePath = cardFrontImagePath.replaceAll("largeCards", "smallCards");
                    ImageIcon smallCardIcon = new ImageIcon(getClass().getResource(smallCardImagePath));
                    this.setIcon(smallCardIcon);
                    cardHolder.add(this);
                    cardCollection.add(this);
                }
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getType() {
                return type;
            }

            public String getImagePath() {
                return cardFrontImagePath;
            }
            // Will be method used to zoom into cards
            //public void swapCardSize() {
            //    if (!isBig) {
            //        setIcon(resizeImage(cardFrontImagePath, bigSizeX, bigSizeY));
            //        isBig = true;
            //    } else {
            //        setIcon(resizeImage(cardFrontImagePath, sizeX, sizeY));
            //        isBig = false;
            //    }
            //}
        }

        /** ActionListener for buttons that can't use a lambda expression */
        @Override
        public void actionPerformed(ActionEvent event) {
            // This may remain empty, we'll see
        }

        /** Methods for lambda expressions */
        private void switchPanel(String panelName) {
            CardLayout layout = (CardLayout) root.getLayout();
            layout.show(root, panelName);
        }

        public void revealCards(boolean lastOrNot) {
            rareLast = lastOrNot;
        }

        public void openNewPack(JLayeredPane inputPane) { // *** FIRST USE OF SwingWorker DEFINITLEY HAD AI HELP HERE ***
            SwingWorker<ArrayList<CardLabel>, Void> bgThread = new SwingWorker<>() {
                @Override
                protected ArrayList<CardLabel> doInBackground() throws Exception {
                    return packCards();
                }

                @Override
                protected void done() { // *** TELLS THE PROGRAM THAT THE BACKGROUND THREAD HAS FINISHED ***
                    try {
                        ArrayList<CardLabel> currentPack = get(); // *** RETRIEVES THE ArrayList<CardLabel> FROM BACKGROUND THREAD ***

                        currentPack.get(0).setBounds(0, 0, packsize.width, packsize.height); // *** EXECUTION RESUMES ON EDT TO UPDATE GUI ***
                        currentPack.get(0).setVisible(true);
                        inputPane.add(currentPack.get(0));
                        for (int i = 1; i < (currentPack.size()); i++) {
                            currentPack.get(i).setBounds(0, 51, packsize.width, 413);
                            currentPack.get(i).setVisible(true);
                            inputPane.add(currentPack.get(i));
                        }
                        cardHolder.removeAll();
                        cardHolder.revalidate();
                        cardHolder.repaint();
                        hand.revalidate();
                        hand.repaint();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            };
        }

        public void addToCollection() {
            for (int i = 0; i < cardCollection.size(); i++) {
                if (leftPage.getComponentCount() == 1) {
                    collectionPreviousIndex = i;
                }
                if (leftPage.getComponentCount() <= 9) {
                    cardCollection.get(i).setVisible(true);
                    leftPage.add(cardCollection.get(i));
                }
                else if (rightPage.getComponentCount() <= 9) {
                    cardCollection.get(i).setVisible(true);
                    rightPage.add(cardCollection.get(i));
                }
                else {
                    collectionNextIndex = i;
                    break;
                }
            }
            revalidate();
            repaint();
        }

        public ArrayList<CardLabel> packCards() {
            ArrayList<CardLabel> currentPack = new ArrayList<CardLabel>(assignImagestoPack(buildPack()));
            return currentPack;
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
            if (!rareLast) {
                for (int i = 1; i < newCardList.length; i++) {
                    for (int j = 1; j < newCardList.length; j++) {
                        if (i != j && newCardList[i] == newCardList[j]) {
                            while (newCardList[i] == newCardList[j]) {                            
                                if (i >= 1 && i < 6) {
                                    newCardList[i] = randCommon();
                                }
                                else if (i >= 6 && i < 8) {
                                    newCardList[i] = randEnergy();
                                }
                                else if (i == 8) {
                                    newCardList[i] = randRare();
                                }
                                else if (i >= 9 && i < 12) {
                                    newCardList[i] = randUncommon();
                                }
                            }
                        }
                    }
                }
            }
            else if (rareLast) {
                for (int i = 1; i < newCardList.length; i++) {
                    for (int j = 1; j < newCardList.length; j++) {
                        if (i != j && newCardList[i] == newCardList[j]) {
                            while (newCardList[i] == newCardList[j]) {                            
                                if (i >= 1 && i < 6) {
                                    newCardList[i] = randCommon();
                                }
                                else if (i >= 6 && i < 8) {
                                    newCardList[i] = randEnergy();
                                }
                                else if (i <= 8 && i < 11) {
                                    newCardList[i] = randUncommon();
                                }
                                else if (i == 11) {
                                    newCardList[i] = randRare();
                                }
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
            if (!rareLast) {
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
            else if (rareLast) {
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
        public ArrayList<CardLabel> assignImagestoPack(int[] pack) {
            ArrayList <CardLabel> packImages = new ArrayList<CardLabel>();
            String commonPath = "/cardImages/largeCards/common/common(";
            String energyPath = "/cardImages/largeCards/energy/energy(";
            String rarePath = "/cardImages/largeCards/rare/rare(";
            String holoPath = "/cardImages/largeCards/holo/holo(";
            String uncommonPath = "/cardImages/largeCards/uncommon/uncommon(";
            boosterPack = new CardLabel(randomPack());

            // Normal card reveal method
            if (!rareLast) {
                for (int i = 1; i < pack.length; i++) {
                    if (i >= 1 && i < 6) {
                        String currentCommon = commonPath + pack[i] + ").jpg";
                        boosterCard = new CardLabel(currentCommon);
                        packImages.add(boosterCard);
                    }
                    else if (i >= 6 && i < 8) {
                        String currentEnergy = energyPath + pack[i] + ").jpg";
                        boosterCard = new CardLabel(currentEnergy);
                        packImages.add(boosterCard);
                    }
                    else if (i == 8) {
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
                    else if (i >= 9 && i <= 11) {
                        String currentUncommon = uncommonPath + pack[i] + ").jpg";
                        boosterCard = new CardLabel(currentUncommon);
                        packImages.add(boosterCard);
                    }
                }
                
            }
            // Reveal rare card last method
            else if (rareLast) {
                for (int j = 1; j < pack.length; j++) {
                    if (j >= 1 && j < 6) {
                        String currentCommon = commonPath + pack[j] + ").jpg";
                        boosterCard = new CardLabel(currentCommon);
                        packImages.add(boosterCard);
                    }
                    else if (j >= 6 && j < 8) {
                        String currentEnergy = energyPath + pack[j] + ").jpg";
                        boosterCard = new CardLabel(currentEnergy);
                        packImages.add(boosterCard);
                    }
                    else if (j >= 8 && j < 11) {
                        String currentUncommon = uncommonPath + pack[j] + ").jpg";
                        boosterCard = new CardLabel(currentUncommon);
                        packImages.add(boosterCard);
                    }
                    else if ( j == 11) {
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
                ImageIcon newVenusaurPack = resizePackImage(venusaurPack);
                randomPackImage = newVenusaurPack;
            }
            else if (num == 1) {
                ImageIcon newCharizardPack = resizePackImage(charizardPack);
                randomPackImage = newCharizardPack;
            }
            else if (num == 2) {
                ImageIcon newBlastoisePack = resizePackImage(blastoisePack);
                randomPackImage = newBlastoisePack;
            };
            return randomPackImage;
        }
        // Rewrite to change size of card, using an additional JPanel or resizing the JLabel? Idk yet
        public ImageIcon resizePackImage(ImageIcon currentIcon) {
            ImageIcon newIcon;
            Image resizedImage = currentIcon.getImage().getScaledInstance(packsize.width, packsize.height, Image.SCALE_SMOOTH);
            newIcon = new ImageIcon(resizedImage);

            return newIcon;
        }

        public ImageIcon resizeCardImage(ImageIcon currentIcon) {
            ImageIcon newIcon;
            Dimension cardsize = new Dimension(300, 413);
            Image resizedImage = currentIcon.getImage().getScaledInstance(cardsize.width, cardsize.height, Image.SCALE_SMOOTH);
            newIcon = new ImageIcon(resizedImage);

            return newIcon;
        }
        public static void main(String[] args) {
            SwingUtilities.invokeLater(() ->new TCGOpenerGUI());
        }
    }
