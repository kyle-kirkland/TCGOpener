import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
//import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.*;
import java.io.*;
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
        private JLayeredPane collection;
        private JLayeredPane desk1;
        private JPanel desk2;
        private JLayeredPane hand;
        private JPanel cardsDisplayed;
        private JPanel cardHolder;
        private JPanel collectionButtons;
        private JPanel displayArea;
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
        private JButton openBinder;
        private JButton nextPage;
        private JButton previousPage;
        private ArrayList<CardLabel> cardCollection = new ArrayList<CardLabel>();
        private ArrayList<CardLabel> commonCollection = new ArrayList<CardLabel>();
        private ArrayList<CardLabel> energyCollection = new ArrayList<CardLabel>();
        private ArrayList<CardLabel> uncommonCollection = new ArrayList<CardLabel>();
        private ArrayList<CardLabel> rareCollection = new ArrayList<CardLabel>();
        private ArrayList<CardLabel> holoCollection = new ArrayList<CardLabel>();
        private Dimension packsize = new Dimension(300, 515);
        private Dimension cardHolderDimension = new Dimension(1200, 1000);
        private Dimension wideScreen = new Dimension(1920, 1080);
        private Dimension smallScreen = new Dimension(1280, 720);
        private Image deskImage;
        private SwingUtilities SU;
        private Pattern pattern;
        private Matcher matcher;
        private File file;
        private String[] commonCardNames;
        private String[] energyCardNames;
        private String[] uncommonCardNames;
        private String[] rareCardNames;
        private String[] holoCardNames;
        private String frameTitle;
        private String deskImagePath;
        private boolean rareLast = false;
        private int handStartX = (wideScreen.width - packsize.width) - 150;
        private int handStartY = (wideScreen.height - packsize.height) - 200;
        private int sshandStartX = (smallScreen.width - packsize.width) - 150;
        private int sshandStartY = (smallScreen.height - packsize.height) - 200;
        private int nextIndex = 0;
        private int previousIndex = 0;
        private int currIndex = 0;

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

        private JLayeredPane createCollectionPanel() {
            collection = new JLayeredPane();
            collection.setBounds(0, 0, wideScreen.width, wideScreen.height);

            String deskImagePath = "/backgrounds/wood_desk.png";
            deskImage = new ImageIcon(getClass().getResource(deskImagePath)).getImage();
            desk2 = new JPanel() { 
                @Override
                public void paintComponent(Graphics graphic) {
                    super.paintComponent(graphic);
                    graphic.drawImage(deskImage, 0, 0, getWidth(), getHeight(), this);
                }
            };
            desk2.setLayout(new BorderLayout());
            desk2.setBounds(0, 0, wideScreen.width, wideScreen.height);
            desk2.setOpaque(true);
            desk2.setVisible(true);

            Dimension pageMaxSize = new Dimension((wideScreen.width - 50), (wideScreen.height - 50));
            displayArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 60, 10));
            displayArea.setBounds(25, 25, pageMaxSize.width, pageMaxSize.height);
            displayArea.setBackground(Color.BLUE);
            displayArea.setOpaque(true);
            displayArea.setVisible(true);

            GridBagConstraints gbConst1 = new GridBagConstraints();

            collectionButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
            collectionButtons.setBounds((wideScreen.width / 2), 0, 0, 0);
            collectionButtons.setOpaque(false);
            collectionButtons.setVisible(true);

            fromCollectiontoDesk = new JButton("To Desk");
            fromCollectiontoDesk.addActionListener(event -> switchPanel("Desk")); // *** LAMBDA ***
            openBinder = new JButton("Open Binder");
            openBinder.addActionListener(event -> showCardsInBinder());
            nextPage = new JButton("Next Page");
            nextPage.addActionListener(this);
            previousPage = new JButton("Previous Page");
            previousPage.addActionListener(this);

            collectionButtons.add(fromCollectiontoDesk);
            collectionButtons.add(openBinder);
            collectionButtons.add(nextPage);
            collectionButtons.add(previousPage);

            GridBagConstraints gbConst2 = new GridBagConstraints();
            desk2.add(collectionButtons, BorderLayout.NORTH);

            collection.add(displayArea, JLayeredPane.PALETTE_LAYER);
            collection.add(desk2, JLayeredPane.DEFAULT_LAYER);
            revalidate();
            repaint();

            return collection;
        }

        private JLayeredPane createDeskPane() {
            // Add menu to select the reveal method, use Event.getActionCommand() 
            // method in actionPerformed for menus
            desk1 = new JLayeredPane();
            desk1.setPreferredSize(wideScreen); 

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

            desk1.add(hand, JLayeredPane.PALETTE_LAYER);
            desk1.add(cardHolder, JLayeredPane.PALETTE_LAYER);
            desk1.add(cardsDisplayed, JLayeredPane.DEFAULT_LAYER);
            desk1.revalidate();
            desk1.repaint();

            return desk1;
        }

        private void createMainPanel() {
            root = new JPanel(new CardLayout());
            root.setPreferredSize(wideScreen); 

            welcomescreen = createWelcomescreenPanel();
            collection = createCollectionPanel();
            desk1 = createDeskPane();

            root.add(welcomescreen, "WelcomeScreen");
            root.add(collection, "Collection");
            root.add(desk1, "Desk");
            
            CardLayout layout = (CardLayout) root.getLayout();
            layout.show(root, "WelcomeScreen");

            frameTitle = new String("TCG Opener");
            setTitle(frameTitle);
            setSize(wideScreen); 
            add(root);
        }
        /** CardLabel class that is used to create each card and pack */
        public class CardLabel extends JLabel{
            private String name; 
            private String type;
            private String rarity;
            private String cardFrontImagePath;
            private boolean isBig = false;
            
            // Need to make sure cards are properly sorted in specific folders to be used here
            public CardLabel(String cardFrontImagePath) {
                this.type = "card";
                this.cardFrontImagePath = cardFrontImagePath;
                System.out.println(cardFrontImagePath); // *** DEBUG ***
                ImageIcon resizedImage = new ImageIcon();
                resizedImage = resizeCardImage(new ImageIcon(getClass().getResource(cardFrontImagePath)));
                setIcon(resizedImage);
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        //if (event.isShiftDown()) {
                        //    swapCardSize();
                        //}
                        fromHandtoDesk();
                    }
                });
            }

            public CardLabel(ImageIcon packImage) {
                this.type = "pack";
                setIcon(packImage);
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        //if (event.isShiftDown()) {
                        //    swapCardSize();
                        //}
                        fromHandtoDesk();
                    }
                });
            }
            public void fromHandtoDesk() {
                // moving from hand to cardHolder
                if (this.type.equalsIgnoreCase("pack")) {
                    hand.remove(this);
                    hand.revalidate();
                    hand.repaint();
                }

                else if (this.type.equalsIgnoreCase("card") && hand.isAncestorOf(this)) { // *** SECOND USE OF SwingWorker HAD AI HELP HERE BUT I UNDERSTAND A LITTLE BETTER NOW***
                    hand.remove(this);
                    hand.revalidate();
                    hand.repaint();
                    SwingWorker<Void, Void> bgThread = new SwingWorker<>() { 
                        @Override
                        protected Void doInBackground() throws Exception {
                            shrinkCard();
                            addToCollection();
                            return null;
                        }
                        @Override
                        protected void done() { // *** TELLS THE PROGRAM THAT THE BACKGROUND THREAD HAS FINISHED ***
                            try {
                                get(); // *** RETRIEVES ANYTHING DONE IN THE BACKGROUND, INCLUDING ERRORS TO BE PRINTED ***
                                addToCardHolder(); // *** EXECUTION RESUMES ON EDT TO UPDATE GUI ***
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    };
                    bgThread.execute(); // *** STARTS THE BACKGROUND THREAD, SUPER IMPORTANT DON'T FORGET ***
                }
            }

            public void addToCardHolder() {
                cardHolder.add(this);
                cardHolder.revalidate();
                cardHolder.repaint();
            }
            public void addToCollection() {
                cardCollection.add(this);
                switch (this.rarity) {
                    case "common":
                        commonCollection.add(this);
                    case "uncommon":
                        uncommonCollection.add(this);
                    case "energy":
                        energyCollection.add(this);
                    case "rare":
                        rareCollection.add(this);
                    case "holo":
                        holoCollection.add(this);
                }
            }
            public void shrinkCard() {
                String smallCardImagePath = cardFrontImagePath.replaceAll("largeCards", "smallCards");
                System.out.println(smallCardImagePath); // *** DEBUG ***
                ImageIcon smallCardIcon = new ImageIcon(getClass().getResource(smallCardImagePath));
                this.setIcon(smallCardIcon);
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getType() {
                return type;
            }

            public void setName(String name) {
                this.name = name;
            }
            
            public String getName() {
                return this.name;
            }

            public void setRarity(String rarity) {
                this.rarity = rarity;
            }

            public String getRarity() {
                return rarity;
            }

            public String getImagePath() { // *** MIGHT NOT NEED ***
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

        public void navigateBinder() {
            
        }
        public void addCardstoDisplayArea(CardLabel currCard) {
            displayArea.add(currCard);
            displayArea.revalidate();
            displayArea.repaint();
        }

        public void showCardsInBinder() {
            // nextIndex = 0;
            // previousIndex = 0;
            int currentIndex = 0;

            if (displayArea.getComponentCount() == 1) {
                if (currIndex == 0 && previousIndex == 0 && nextIndex == 0) {
                    nextIndex += 18;
                };
            }
            for (CardLabel i : cardCollection) {
                cardHolder.add(i);
                cardHolder.revalidate();
                cardHolder.repaint();
                currentIndex++;
            }
        }

        public void writeToCSVs() {
            for (CardLabel i : cardCollection) {
                
            }
            for (CardLabel i : commonCollection) {
                
            }
            for (CardLabel i : energyCollection) {
                
            }
            for (CardLabel i : uncommonCollection) {
                
            }
            for (CardLabel i : rareCollection) {
                
            }
            for (CardLabel i : holoCollection) {
                
            }
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

        public String[][] setAllCardNames() {
            String[][] allCardNames = new String[5][];
            String[] commonNames = {"'FILLER'", "'Abra'", "'Bulbasaur'", "'Caterpie'", "'Charmander'", "'Diglett'", "'Doduo'", "'Drowzee'", "'Gastly'", "'Koffing'", "'Machop'", "'Magnemite'", 
            "'Metapod'", "'Nidoran_M'", "'Onix'", "'Pidgey'", "'Pikachu'", "'Poliwag'", "'Ponyta'", "'Rattata'", "'Sandshrew'", "'Squirtle'", "'Starmie'", "'Staryu'", "'Tangela'", 
            "'Voltorb'", "'Vulpix'", "'Weedle'", "'Trainer_Bill'", "'Trainer_Energy_Removal'", "'Trainer_Gust_of_Wind'", "'Trainer_Potion'", "'Trainer_Switch'"};
            String[] energyNames = {"'FILLER'", "'Energy_Fighting'", "'Energy_Fire'", "'Energy_Grass'", "'Energy_Lightning'", "'Energy_Psychic'", "'Energy_Water'"};
            String[] uncommonNames = {"'FILLER'", "'Arcanine'", "'Charmeleon'", "'Dewgong'", "'Dratini'", "'Farfetchd'", "'Growlithe'", "'Haunter'", "'Ivysaur'", "'Jynx'", "'Kadabra'", "'Kakuna'", 
            "'Machoke'", "'Majikarp'", "'Magmar'", "'Nidorino'", "'Poliwhirl'", "'Porygon'", "'Raticate'", "'Seel'", "'Wartortle'", "'Trainer_Defender'", "'Trainer_Energy_Retrieval'", 
            "'Trainer_Full_Heal'", "'Trainer_Maintenance'", "'Trainer_PlusPower'", "'Trainer_Pokemon_Center'", "'Trainer_Pokemon_Flute'", "'Trainer_Pokedex'", "'Trainer_Professor_Oak'", 
            "'Trainer_Revive'", "'Trainer_Super_Potion'", "'Energy_Double_Colorless'"};
            String[] rareNames = {"'FILLER'", "'Beedrill'", "'Dragonair'", "'Dugtrio'", "'Electabuzz'", "'Electrode'", "'Pidgeotto'", "'Trainer_Clefairy_Doll'", "'Trainer_Computer_Search'", 
            "'Trainer_Devolution_Spray'", "'Trainer_Imposter_Professor_Oak'", "'Trainer_Item_Finder'", "'Trainer_Lass'", "'Trainer_Pokemon_Breeder'", "'Trainer_Pokemon_Trader'", 
            "'Trainer_Scoop_Up'", "'Trainer_Super_Energy_Removal'"};
            String[] holoNames = {"'FILLER'", "'Alakazam'", "'Blastoise'", "'Chansey'", "'Charizard'", "'Clefairy'", "'Gyarados'", "'Hitmonchan'", "'Machamp'", "'Magneton'", "'Mewtwo'", "'Nidoking'", 
            "'Ninetales'", "'Poliwrath'", "'Raichu'", "'Venusaur'", "'Zapdos'", };

            allCardNames[0] = commonNames;
            allCardNames[1] = energyNames;
            allCardNames[2] = uncommonNames;
            allCardNames[3] = rareNames;
            allCardNames[4] = holoNames;

            return allCardNames;
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

        public String fixCardName(String cardName) {
            String name = "";
            name = cardName.replaceAll("_", " ");
            name = name.replaceAll("'", "");
            return name;
        }

        public ArrayList<CardLabel> assignImagestoPack(int[] pack) {
            ArrayList <CardLabel> packImages = new ArrayList<CardLabel>();
            String[][] allCardNames = setAllCardNames();
            String commonPath = "/cardImages/largeCards/common/";       // allCardNames[0] = commonNames;
            String energyPath = "/cardImages/largeCards/energy/";       // allCardNames[1] = energyNames;
            String rarePath = "/cardImages/largeCards/rare/";           // allCardNames[3] = rareNames;
            String holoPath = "/cardImages/largeCards/holo/";           // allCardNames[4] = holoNames;
            String uncommonPath = "/cardImages/largeCards/uncommon/";   // allCardNames[2] = uncommonNames;
            boosterPack = new CardLabel(randomPack());

            // Normal card reveal method
            if (!rareLast) {
                for (int i = 1; i < pack.length; i++) {
                    if (i >= 1 && i < 6) {
                        String currentCommon = commonPath + allCardNames[0][pack[i]] + "common(" + pack[i] + ").jpg";
                        boosterCard = new CardLabel(currentCommon);
                        boosterCard.setName(fixCardName(allCardNames[0][pack[i]]));
                        boosterCard.setRarity("Common");
                        packImages.add(boosterCard);
                    }
                    else if (i >= 6 && i < 8) {
                        String currentEnergy = energyPath + allCardNames[1][pack[i]] + "energy(" + pack[i] + ").jpg";
                        boosterCard = new CardLabel(currentEnergy);
                        boosterCard.setName(fixCardName(allCardNames[1][pack[i]]));
                        boosterCard.setRarity("Common");
                        packImages.add(boosterCard);
                    }
                    else if (i == 8) {
                        Random rng = new Random();
                        if (rng.nextInt(4) == 3) {
                            String currentHolo = holoPath + allCardNames[4][pack[i]] + "holo(" + pack[i] + ").jpg";
                            boosterCard = new CardLabel(currentHolo);
                            boosterCard.setName(fixCardName(allCardNames[4][pack[i]]));
                            boosterCard.setRarity("Holo Rare");
                            packImages.add(boosterCard);
                        }
                        else {
                            String currentRare = rarePath + allCardNames[3][pack[i]] + "rare(" + pack[i] + ").jpg";
                            boosterCard = new CardLabel(currentRare);
                            boosterCard.setName(fixCardName(allCardNames[3][pack[i]]));
                            boosterCard.setRarity("Rare");
                            packImages.add(boosterCard);
                        }
                    }
                    else if (i >= 9 && i <= 11) {
                        String currentUncommon = uncommonPath + allCardNames[2][pack[i]] + "uncommon(" + pack[i] + ").jpg";
                        boosterCard = new CardLabel(currentUncommon);
                        boosterCard.setName(fixCardName(allCardNames[2][pack[i]]));
                        boosterCard.setRarity("Uncommon");
                        packImages.add(boosterCard);
                    }
                }
                
            }
            // Reveal rare card last method
            else if (rareLast) {
                for (int j = 1; j < pack.length; j++) {
                    if (j >= 1 && j < 6) {
                        String currentCommon = commonPath + allCardNames[0][pack[j]] + "common(" + pack[j] + ").jpg";
                        boosterCard = new CardLabel(currentCommon);
                        boosterCard.setName(fixCardName(allCardNames[0][pack[j]]));
                        boosterCard.setRarity("Common");
                        packImages.add(boosterCard);
                    }
                    else if (j >= 6 && j < 8) {
                        String currentEnergy = energyPath + allCardNames[1][pack[j]] + "energy(" + pack[j] + ").jpg";
                        boosterCard = new CardLabel(currentEnergy);
                        boosterCard.setName(fixCardName(allCardNames[1][pack[j]]));
                        boosterCard.setRarity("Common");
                        packImages.add(boosterCard);
                    }
                    else if (j >= 8 && j < 11) {
                        String currentUncommon = uncommonPath + allCardNames[2][pack[j]] + "uncommon(" + pack[j] + ").jpg";
                        boosterCard = new CardLabel(currentUncommon);
                        boosterCard.setName(fixCardName(allCardNames[2][pack[j]]));
                        boosterCard.setRarity("Uncommon");
                        packImages.add(boosterCard);
                    }
                    else if ( j == 11) {
                        Random rng = new Random();
                        if (rng.nextInt(4) == 3) {
                            String currentHolo = holoPath + allCardNames[4][pack[j]] + "holo(" + pack[j] + ").jpg";
                            boosterCard = new CardLabel(currentHolo);
                            boosterCard.setName(fixCardName(allCardNames[4][pack[j]]));
                            boosterCard.setRarity("Holo Rare");
                            packImages.add(boosterCard);
                        }
                        else {
                            String currentRare = rarePath + allCardNames[3][pack[j]] + "rare(" +  pack[j] + ").jpg";
                            boosterCard = new CardLabel(currentRare);
                            boosterCard.setName(fixCardName(allCardNames[3][pack[j]]));
                            boosterCard.setRarity("Rare");
                            packImages.add(boosterCard);   
                        }
                    }
                }
            }
            
            packImages.add(0, boosterPack);
            return packImages;
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
            bgThread.execute(); // *** STARTS THE BACKGROUND THREAD, SUPER IMPORTANT DON'T FORGET ***
        }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> new TCGOpenerGUI());
        }
    }
