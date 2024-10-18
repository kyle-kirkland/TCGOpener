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
                            Using: JFrame, JButton, JPanel, JLabel, JLayeredPane, ImageIcon, SwingUtilities, SwingWorker
                        */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.*;

/**
*** TBD ***         - Possible way to run program in a browser: https://cheerpj.com/cheerpj-core/
*** TBD ***         - Do I need to create a custom class that extends JPanel? (Maybe, might happen when refactoring code later)
*** Added ***       - Use CardLayout for cycling through a freshly opened booster pack
*** In Progress *** - Add options to display card collection by rarity
*** TBD ***         - Show booster pack wrapping before opening (if added, won't be until much later, not messing with animations at the moment)
*** TBD ***         - Score could be points based off of rarity of card pulled (energy = 0, common = 1, uncommon = 3, rare = 5, holo = 10)
*** Added ***       - Give option to reorganize opened cards so that the rare is on the bottom *** Added ***
*** TBD ***         - Maybe add a sleeve animation for super rares (if added, won't be until much later, not messing with animations at the moment)
*** Added ***       - Layout opened cards beside each other
*** In Progress *** - Add functionality to save current collection of cards to external files (currently CSV files, but may shift to JSON files)
*** Added ***       - Each booster pack has 11 cards
                        - 1 rare
                        - 3 uncommon 
                        - 5 commons
                        - 2 energy cards
                    - Hierarchy (priority = number of #):
*** Done ***        - Main JFrame 
*** Done ***            - root JPanel (CardLayout, root panel for whole GUI) 
*** In Progress ***         - startscreen JPanel (GridBagLayout) ###
*** Current Focus ***       - collection JLayeredPane (root pane for collection) ###
*** Done ***                    - desk2 JPanel (BorderLayout, holds image of desk and collectionButtons panel)
*** Done ***                    - displayArea JPanel (FlowLayout, holds current "page" of cards from collection binder)
*** Done ***                    - collectionButtons JPanel (FlowLayout)
*** Done ***                        - fromCollectiontoDesk JButton (moves to Desk screen)
*** Done ***                        - openBinder JButton (pulls cards in collection until displayArea is full)
*** TBD ***                         - nextPage JButton (will remove current cards in displayArea and pull in the next several cards)
*** TBD ***                         - previousPage JButton (will remove current cards in displayArea and pull in the previous several cards)
*** Done ***                - desk1 JLayeredPane (root panel for desk1, pack opening screen) 
*** Done ***                    - hand JPanel (CardLayout) 
*** Done ***                        - boosterPack CardLabel (type = "pack") 
*** Done ***                        - boosterCard CardLabel (type = "card") 
*** Done ***                    - desk1Panel JPanel (GridBagLayout, holds image of desk) 
*** Done ***                        - newPack JButton (builds and presents a new booster pack to be opened) 
*** Done ***                        - revealNormal JButton (sets rareLast = false, changes reveal order of cards in booster pack to how the cards are packed) 
*** Done ***                        - revealRare JButton (sets rareLast = true, changes reveal order of cards in booster pack to reveal the rare card last) 
*** Done ***                        - fromDesktoCollection JButton (moves to Collection screen) 
*** Done ***                    - cardHolder JPanel (FlowLayout, cards appear here after being clicked until entire booster pack (11 cards) are displayed) 
*** TBD ***                     - wrapperDisplayArea JPanel (Default Layout) # (if added, won't be until much later, not messing with animations at the moment)  
*/

    public class TCGOpenerGUI extends JFrame implements ActionListener {
        private JPanel root;
        private JPanel welcomescreen;
        private JLayeredPane collection;
        private JLayeredPane desk1;
        private JPanel desk2;
        private JLayeredPane hand;
        private JPanel desk1Panel;
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
        private ArrayList<CardLabel> overallCollection = new ArrayList<CardLabel>();
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

            Dimension pageMaxSize = new Dimension((wideScreen.width - 550), (wideScreen.height - 100));
            displayArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 75, 12));
            displayArea.setBounds(40, 50, pageMaxSize.width, pageMaxSize.height);
            displayArea.setOpaque(false);
            displayArea.setVisible(true);

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

            desk2.add(collectionButtons, BorderLayout.NORTH);

            collection.add(desk2, JLayeredPane.DEFAULT_LAYER);
            collection.add(displayArea, JLayeredPane.PALETTE_LAYER);
            collection.revalidate();
            collection.repaint();
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
            deskImage = new ImageIcon(getClass().getResource(deskImagePath)).getImage();

            desk1Panel = new JPanel() {
                @Override
                public void paintComponent(Graphics graphic) {
                    super.paintComponent(graphic);
                    graphic.drawImage(deskImage, 0, 0, getWidth(), getHeight(), this);
                }
            };
            desk1Panel.setPreferredSize(wideScreen);
            desk1Panel.setBackground(Color.GRAY);
            desk1Panel.setBounds(0, 0, wideScreen.width, wideScreen.height); 

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
            desk1Panel.add(fromDesktoCollection, gbConst);

            gbConst.gridx = 1;
            desk1Panel.add(revealNormal, gbConst);

            gbConst.gridx = 2;
            desk1Panel.add(revealRare, gbConst);

            gbConst.gridx = 3;
            desk1Panel.add(newPack, gbConst);

            desk1.add(hand, JLayeredPane.PALETTE_LAYER);
            desk1.add(cardHolder, JLayeredPane.PALETTE_LAYER);
            desk1.add(desk1Panel, JLayeredPane.DEFAULT_LAYER);
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
                            if (hand.getComponentCount() == 0) {
                                saveCollectionToCSVs();
                            }
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
                overallCollection.add(this);
                switch (this.rarity) {
                    case "Common":
                        commonCollection.add(this);
                        break;
                    case "Uncommon":
                        uncommonCollection.add(this);
                        break;
                    case "Energy":
                        energyCollection.add(this);
                        break;
                    case "Rare":
                        rareCollection.add(this);
                        break;
                    case "Holo":
                        holoCollection.add(this);
                        break;
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
            buildCollectionFromCSVs();

            if (displayArea.getComponentCount() == 1) {
                if (currIndex == 0 && previousIndex == 0 && nextIndex == 0) {
                    nextIndex += 18;
                };
            }
            for (CardLabel i : overallCollection) {
                displayArea.add(i);
                displayArea.revalidate();
                displayArea.repaint();
                currentIndex++;
            }
        }

        public void saveCollectionToCSVs() {
            saveCardsToCSV(overallCollection, "Project2TCGGUI/src/data/overallCollection.csv");
            saveCardsToCSV(commonCollection, "Project2TCGGUI/src/data/commonCollection.csv");
            saveCardsToCSV(energyCollection, "Project2TCGGUI/src/data/energyCollection.csv");
            saveCardsToCSV(uncommonCollection, "Project2TCGGUI/src/data/uncommonCollection.csv");
            saveCardsToCSV(rareCollection, "Project2TCGGUI/src/data/rareCollection.csv");
            saveCardsToCSV(holoCollection, "Project2TCGGUI/src/data/holoCollection.csv");
        }

        private void saveCardsToCSV(ArrayList<CardLabel> cardList, String fileName) {
            try (FileWriter writer = new FileWriter(fileName)) {
                for (CardLabel card : cardList) {
                    if (cardList.isEmpty()) {
                        System.out.println("No cards to save from current card list.");
                        break;
                    }
                    String info = card.getName() + "," + card.getImagePath() + "," + card.getRarity() + "\n";
                    writer.write(info);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void buildCollectionFromCSVs() {
            readCardsFromCSV(overallCollection, "/data/overallCollection.csv");
            readCardsFromCSV(commonCollection, "/data/commonCollection.csv");
            readCardsFromCSV(energyCollection, "/data/energyCollection.csv");
            readCardsFromCSV(uncommonCollection, "/data/uncommonCollection.csv");
            readCardsFromCSV(rareCollection, "/data/rareCollection.csv");
            readCardsFromCSV(holoCollection, "/data/holoCollection.csv");
        }

        public void readCardsFromCSV(ArrayList<CardLabel> listOfCards, String csvPath) {
            try {InputStream inStream = getClass().getResourceAsStream(csvPath);
                if (inStream == null) {
                    throw new FileNotFoundException("Collection CSV file not found.");
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
                
                String card;
                while ((card = reader.readLine())!= null) {
                    String[] fields = card.split(",");

                    String cardName = fields[0];
                    String imagePath = fields[1];
                    String rarity = fields[2];

                    CardLabel currCard = new CardLabel(imagePath);
                    currCard.shrinkCard();
                    currCard.setName(cardName);
                    currCard.setRarity(rarity);

                    listOfCards.add(currCard);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        //*************************************     SHOULDN'T NEED WHEN PROJECT IS DONE, MAYBE
        public void clearAllCsvFiles() {
            String[] csvFiles = {
                "Project2TCGGUI/src/data/overallCollection.csv",
                "Project2TCGGUI/src/data/commonCollection.csv",
                "Project2TCGGUI/src/data/uncommonCollection.csv",
                "Project2TCGGUI/src/data/energyCollection.csv",
                "Project2TCGGUI/src/data/rareCollection.csv",
                "Project2TCGGUI/src/data/holoCollection.csv"
            };
        
            for (String filePath : csvFiles) {
                try (FileWriter writer = new FileWriter(filePath, false)) {
                    // Create a FileWriter without the append flag to overwrite the file content
                    writer.write(""); // Overwrites file with nothing, effectively clearing it
                } catch (IOException e) {
                    System.err.println("Error clearing file: " + filePath);
                    e.printStackTrace();
                }
            }
        }

        //*************************************

        public void openNewPack(JLayeredPane inputPane) { // *** FIRST USE OF SwingWorker DEFINITLEY HAD AI HELP HERE ***
            SwingWorker<ArrayList<CardLabel>, Void> bgThread = new SwingWorker<>() {
                @Override
                protected ArrayList<CardLabel> doInBackground() throws Exception {
                    
                    // **********************************   NEED TO REMOVE WHEN PROJECT IS COMPLETE   **********************************
                    clearAllCsvFiles();
                    // **********************************   NEED TO REMOVE WHEN PROJECT IS COMPLETE   **********************************
                    return packCards();
                }

                @Override
                protected void done() { // *** TELLS THE PROGRAM THAT THE BACKGROUND THREAD HAS FINISHED ***
                    try {
                        ArrayList<CardLabel> currentPack = get(); // *** RETRIEVES THE ArrayList<CardLabel> FROM BACKGROUND THREAD ***
                        cardHolder.removeAll();
                        cardHolder.revalidate();
                        cardHolder.repaint();
                        hand.removeAll();
                        hand.revalidate();
                        hand.repaint();
                        currentPack.get(0).setBounds(0, 0, packsize.width, packsize.height); // *** EXECUTION RESUMES ON EDT TO UPDATE GUI ***
                        currentPack.get(0).setVisible(true);
                        inputPane.add(currentPack.get(0));
                        for (int i = 1; i < (currentPack.size()); i++) {
                            currentPack.get(i).setBounds(0, 51, packsize.width, 413);
                            currentPack.get(i).setVisible(true);
                            inputPane.add(currentPack.get(i));
                        }
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
            int num = rng2.nextInt(3);
            switch (num) {
                case 0:
                    ImageIcon newVenusaurPack = resizePackImage(venusaurPack);
                    randomPackImage = newVenusaurPack;
                    break;
                case 1:
                    ImageIcon newCharizardPack = resizePackImage(charizardPack);
                    randomPackImage = newCharizardPack;
                    break;
                case 2:
                    ImageIcon newBlastoisePack = resizePackImage(blastoisePack);
                    randomPackImage = newBlastoisePack;
                    break;
            }
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
            boosterPack.revalidate();
            boosterPack.repaint();

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
                        boosterCard.setRarity("Energy");
                        packImages.add(boosterCard);
                    }
                    else if (i == 8) {
                        Random rng = new Random();
                        if (rng.nextInt(4) == 3) {
                            String currentHolo = holoPath + allCardNames[4][pack[i]] + "holo(" + pack[i] + ").jpg";
                            boosterCard = new CardLabel(currentHolo);
                            boosterCard.setName(fixCardName(allCardNames[4][pack[i]]));
                            boosterCard.setRarity("Holo");
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
                        boosterCard.setRarity("Energy");
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
                            boosterCard.setRarity("Holo");
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

        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> new TCGOpenerGUI());
        }
    }
