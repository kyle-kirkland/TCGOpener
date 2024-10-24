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

/**
*** TBD ***         - Possible way to run program in a browser: https://cheerpj.com/cheerpj-core/
*** Added ***       - Use CardLayout for cycling through a freshly opened booster pack
*** Added *** - Add options to display card collection by rarity
*** TBD ***         - Show booster pack wrapping before opening (if added, won't be until much later, not messing with animations at the moment)
*** TBD ***         - Score could be points based off of rarity of card pulled (energy = 0, common = 1, uncommon = 3, rare = 5, holo = 10)
*** Added ***       - Give option to reorganize opened cards so that the rare is on the bottom *** Added ***
*** TBD ***         - Maybe add a sleeve animation for super rares (if added, won't be until much later, not messing with animations at the moment)
*** Added ***       - Layout opened cards beside each other
*** Added ***       - Add functionality to save current collection of cards to external files (currently CSV files, but may shift to JSON files)
*** Added ***       - Each booster pack has 11 cards
                        - 1 rare
                        - 3 uncommon 
                        - 5 commons
                        - 2 energy cards
                    - Hierarchy (priority = number of #):
*** Done ***        - Main JFrame 
*** Done ***            - root JPanel (CardLayout, root panel for whole GUI) 
*** In Progress ***         - startscreen JPanel (GridBagLayout) ###
*** Done ***       - collection JLayeredPane (root pane for collection) ###
*** Done ***                    - desk2 JPanel (BorderLayout, holds image of desk and collectionButtons panel)
*** Done ***                    - displayArea JPanel (FlowLayout, holds current "page" of cards from collection binder)
*** Done ***                    - collectionButtons JPanel (FlowLayout)
*** Done ***                        - fromCollectiontoDesk JButton (moves to Desk screen)
*** Done ***                        - openBinder JButton (pulls cards in collection until displayArea is full)
*** Done ***                         - nextPage JButton (will remove current cards in displayArea and pull in the next 12 cards)
*** Done ***                         - previousPage JButton (will remove current cards in displayArea and pull in the previous 12 cards)
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

*** Things to do to wrap up project ***
    - Add card zoom feature (probably using JLabel with images from largeCards folder)
    - Break down overall program into smaller classes
        - Class for building each screen (Desk, Collection, Welcome)
        - Class for building each card (CardLabel)
        - Class for building each pack (BoosterPack)
        - Maybe a class just containing a bunch of the random helper methods? Is that a thing that's done?
    - Identify which private fields can be made local to certain methods or even restrict to the broken off classes
    - Add image for welcome screen (possibly take image used for desk and toss Pokemon logo over the buttons with a bunch of cards scattered 
        around facing up and down, todo in GIMP)
    - Add JavaDoc comments to all methods
    - Clean up random commented out code
    - Make sure to replace all of exceptions.printStackTrace() with proper logging
    - Write up user manual for how to use program
    - Final functionality should be as follows:
        - Open program to welcome screen
        - Basic usage instructions display on welcome screen as pop-up
        - More specific instructions for each screen (Desk, Collection) as pop-up
        - Choose to open packs or view collection
        - Choose to reveal rare card last or reveal cards in normal order
        - Open new pack to reveal 11 cards (5 common, 3 uncommon, 2 energy, 1 rare) stacked on top of each other
        - Add each card to add to collection when clicked, and at the same time shrink the card and move it to the cardHolder
        - Can continue to open new packs until the program is closed
        - Click on collection to view all cards that have been collected
        - Either sort by rarity or view all cards at once, sorted alphebetically by name
        - Can cycle through pages in a collection (12 cards per page) both forward and backward (next/previous buttons)
        - Can return to desk screen from collection screen
        - All cards collected are saved to CSV files in the data folder and then read back in when the program is opened
        - Can zoom into cards to see more detail 
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
    private JPanel cardZoomPanel;
    private CardLabel boosterCard;
    private CardLabel boosterPack;
    private CardLabel zoomedCard;
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
    private ArrayList<CardLabel> currCollection = overallCollection;
    private Dimension packsize = new Dimension(300, 515);
    private Dimension cardHolderDimension = new Dimension(1200, 1000);
    private Dimension wideScreen = new Dimension(1920, 1080);
    private Dimension smallScreen = new Dimension(1280, 720);
    private Image deskImage;
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
    private int[] startIndex = {0, 0, 0, 0, 0, 0};      // int[0] = overall, int [1] = common, int[2] = energy,
    private int[] endIndex = {12, 12, 12, 12, 12, 12};  // int[3] = uncommon, int[4] = rare, int[5] = holo
    private int[] currIndex = {0, 0, 0, 0, 0, 0};
    private int[] startCollectionSizes = setCollectionSizes();
    private int[] currCollectionSizes;
    private int chosenCurrIndex = 0;
    private int chosenStartIndex = 0;
    private int chosenEndIndex = 12;   

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

            JComboBox<String> chooseCollection = new JComboBox<>(new String[]{"Overall Collection", "Common", "Energy", "Uncommon", "Rare", "Holo"});
            chooseCollection.setSelectedIndex(0);
            chooseCollection.addActionListener(event -> {                       // *** LAMBDA ***
                int chosenCollection = chooseCollection.getSelectedIndex();
                switch (chosenCollection) {
                    case 0:
                        currCollection = overallCollection;
                        chosenCurrIndex = currIndex[0];
                        chosenStartIndex = startIndex[0];
                        chosenEndIndex = endIndex[0];
                        break;
                    case 1:
                        currCollection = commonCollection;
                        chosenCurrIndex = currIndex[1];
                        chosenStartIndex = startIndex[1];
                        chosenEndIndex = endIndex[1];
                        break;
                    case 2:
                        currCollection = energyCollection;
                        chosenCurrIndex = currIndex[2];
                        chosenStartIndex = startIndex[2];
                        chosenEndIndex = endIndex[2];
                        break;
                    case 3:
                        currCollection = uncommonCollection;
                        chosenCurrIndex = currIndex[3];
                        chosenStartIndex = startIndex[3];
                        chosenEndIndex = endIndex[3];
                        break;
                    case 4:
                        currCollection = rareCollection;
                        chosenCurrIndex = currIndex[4];
                        chosenStartIndex = startIndex[4];
                        chosenEndIndex = endIndex[4];
                        break;
                    case 5:
                        currCollection = holoCollection;
                        chosenCurrIndex = currIndex[5];
                        chosenStartIndex = startIndex[5];
                        chosenEndIndex = endIndex[5];
                        break;
                }
            });

            fromCollectiontoDesk = new JButton("To Desk");
            fromCollectiontoDesk.addActionListener(event -> switchPanel("Desk"));           // *** LAMBDA ***
            openBinder = new JButton("Open Binder");
            openBinder.addActionListener(event -> {
                nextPage.setVisible(true);
                previousPage.setVisible(true);
                collectionButtons.revalidate();
                collectionButtons.repaint();
                updateDisplayArea(currCollection);
            });
            nextPage = new JButton("Next Page");
            nextPage.addActionListener(event -> nextButton(currCollection));     // *** LAMBDA ***
            nextPage.setVisible(false);
            previousPage = new JButton("Previous Page");
            previousPage.addActionListener(event -> previousButton(currCollection));     // *** LAMBDA ***
            previousPage.setVisible(false);

            cardZoomPanel = new JPanel();
            int cardZoomXpos = (int)(((wideScreen.getWidth()) / 2) - 300);
            int cardZoomYpos = (int)(((wideScreen.getHeight()) / 2) - 412);
            Dimension cardZoomSize = new Dimension(600, 825);
            cardZoomPanel.setBounds(cardZoomXpos, cardZoomYpos, cardZoomSize.width, cardZoomSize.height);
            cardZoomPanel.setVisible(false);

            collectionButtons.add(fromCollectiontoDesk);
            collectionButtons.add(openBinder);
            collectionButtons.add(chooseCollection);
            collectionButtons.add(previousPage);
            collectionButtons.add(nextPage);

            desk2.add(collectionButtons, BorderLayout.NORTH);

            collection.add(desk2, JLayeredPane.DEFAULT_LAYER);
            collection.add(displayArea, JLayeredPane.PALETTE_LAYER);
            collection.add(cardZoomPanel, JLayeredPane.POPUP_LAYER);
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
            newPack.addActionListener(event -> openNewPack(hand));                    // *** LAMBDA w/ SwingWorker IMPLEMENTATION ***
            revealNormal = new JButton("Reveal Normal Order");
            revealNormal.addActionListener(event -> revealCards(false));    // *** LAMBDA ***
            revealRare = new JButton("Reveal Rare Last");
            revealRare.addActionListener(event -> revealCards(true));       // *** LAMBDA ***
            fromDesktoCollection = new JButton("To Collection");
            fromDesktoCollection.addActionListener(event -> {                         // *** LAMBDA ***
                switchPanel("Collection");
                sortCollections();
            });
            

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
                ImageIcon resizedImage = new ImageIcon();
                resizedImage = resizeCardImage(new ImageIcon(getClass().getResource(cardFrontImagePath)));
                setIcon(resizedImage);
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        if (event.isShiftDown()) {
                            if (isBig == false) {
                                String largeCardPath = cardFrontImagePath.replaceAll("smallCards", "largeCards");
                                isBig = true;
                                swapCardSize(largeCardPath);
                            }
                            else {
                                isBig = false;
                                swapCardSize(cardFrontImagePath);
                            }
                        }
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

            public void swapCardSize(String path) {
                ImageIcon smallCardIcon = new ImageIcon(getClass().getResource(path));
                if (zoomedCard != null) {
                    cardZoomPanel.remove(zoomedCard);
                }
                zoomedCard = new CardLabel(smallCardIcon);
                if (this.getisBig()) {
                    setisBig(false);
                    zoomedCard.setisBig(true);
                    zoomedCard.setFocusable(true);
                    zoomedCard.requestFocusInWindow();
                    zoomedCard.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent event) {
                            zoomedCard.setisBig(false);
                            cardZoomPanel.remove(zoomedCard);
                            cardZoomPanel.setVisible(false);
                            collection.revalidate();
                            collection.repaint();
                        }
                    });
                    cardZoomPanel.add(zoomedCard, BorderLayout.CENTER);
                    cardZoomPanel.setVisible(true);
                    collection.revalidate();
                    collection.repaint();
                }
            }

            public void shrinkCard() {
                String smallCardImagePath = cardFrontImagePath.replaceAll("largeCards", "smallCards");
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

            public void setisBig(boolean isBig) {
                this.isBig = isBig;
            }

            public boolean getisBig() {
                return isBig;
            }

            public String getImagePath() {
                return cardFrontImagePath;
            }
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

        public void addCardstoDisplayArea(CardLabel currCard) {
            displayArea.add(currCard);
            displayArea.revalidate();
            displayArea.repaint();
        }

        public void nextButton(ArrayList<CardLabel> currCollection) {
            chosenStartIndex += 12;
            chosenEndIndex += 12;

            if (chosenEndIndex > currCollection.size()) {
                chosenEndIndex = currCollection.size();
            }

            if (chosenStartIndex >= currCollection.size()) {
                chosenStartIndex = currCollection.size() - (currCollection.size() % 12);
            }

            updateDisplayArea(currCollection);
        }

        public void previousButton(ArrayList<CardLabel> currCollection) {
            chosenStartIndex -= 12;
            chosenEndIndex -= 12;

            if (chosenStartIndex < 0) {    
                chosenEndIndex = 0;
                chosenEndIndex = Math.min(12, currCollection.size());
            }
            else {
                chosenEndIndex = chosenStartIndex + 12;
            }

            chosenCurrIndex = chosenStartIndex;

            updateDisplayArea(currCollection);
        }

        public void updateDisplayArea(ArrayList<CardLabel> currCollection) {
            displayArea.removeAll();
            for (int i = chosenStartIndex; i < chosenEndIndex; i++) {
                if (i >= currCollection.size()) {
                    break;
                }
                CardLabel card = currCollection.get(i);
                displayArea.add(card);
            }

            if (currCollection.size() <= 12) {
                nextPage.setVisible(false);
                previousPage.setVisible(false);
            }
            else {
                if (chosenEndIndex >= currCollection.size()) {
                    nextPage.setVisible(false);
                }
                else {
                    nextPage.setVisible(true);
                }
                
                if (chosenStartIndex <= 0) {
                    previousPage.setVisible(false);
                }
                else {
                    previousPage.setVisible(true);
                }
            }

            displayArea.revalidate();
            displayArea.repaint();
        }

        public void sortCollections() {
            SwingWorker<Void, Void> sortWorker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    currCollectionSizes = setCollectionSizes();
                    if (startCollectionSizes[0] != currCollectionSizes[0]) {
                        overallCollection.sort((card1, card2) -> card1.getName().compareToIgnoreCase(card2.getName()));
                        currCollectionSizes[0] = overallCollection.size();
                    }
                    if (startCollectionSizes[1] != currCollectionSizes[1]) {
                        commonCollection.sort((card1, card2) -> card1.getName().compareToIgnoreCase(card2.getName()));
                        currCollectionSizes[0] = commonCollection.size();
                    }
                    if (startCollectionSizes[2] != currCollectionSizes[2]) {
                        energyCollection.sort((card1, card2) -> card1.getName().compareToIgnoreCase(card2.getName()));
                        currCollectionSizes[0] = energyCollection.size();
                    }
                    if (startCollectionSizes[3] != currCollectionSizes[3]) {
                        uncommonCollection.sort((card1, card2) -> card1.getName().compareToIgnoreCase(card2.getName()));
                        currCollectionSizes[0] = uncommonCollection.size();
                    }
                    if (startCollectionSizes[4] != currCollectionSizes[4]) {
                        rareCollection.sort((card1, card2) -> card1.getName().compareToIgnoreCase(card2.getName()));
                        currCollectionSizes[0] = rareCollection.size();
                    }
                    if (startCollectionSizes[5] != currCollectionSizes[5]) {
                        holoCollection.sort((card1, card2) -> card1.getName().compareToIgnoreCase(card2.getName()));
                        currCollectionSizes[0] = holoCollection.size();
                    }
                    return null;
                }
                @Override
                protected void done() {
                    try {
                        get();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            sortWorker.execute();
        }

        public int[] setCollectionSizes() {
            int[] tempCollectionSizes = new int[6];
            tempCollectionSizes[0] = overallCollection.size();
            tempCollectionSizes[1] = commonCollection.size();
            tempCollectionSizes[2] = energyCollection.size();
            tempCollectionSizes[3] = uncommonCollection.size();
            tempCollectionSizes[4] = rareCollection.size();
            tempCollectionSizes[5] = holoCollection.size();

            return tempCollectionSizes;
        }

        public void saveCollectionToCSVs() {
            saveCardsToCSV(overallCollection, "TCGOpener/src/data/overallCollection.csv");
            saveCardsToCSV(commonCollection, "TCGOpener/src/data/commonCollection.csv");
            saveCardsToCSV(energyCollection, "TCGOpener/src/data/energyCollection.csv");
            saveCardsToCSV(uncommonCollection, "TCGOpener/src/data/uncommonCollection.csv");
            saveCardsToCSV(rareCollection, "TCGOpener/src/data/rareCollection.csv");
            saveCardsToCSV(holoCollection, "TCGOpener/src/data/holoCollection.csv");
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
                "TCGOpener/src/data/overallCollection.csv",
                "TCGOpener/src/data/commonCollection.csv",
                "TCGOpener/src/data/uncommonCollection.csv",
                "TCGOpener/src/data/energyCollection.csv",
                "TCGOpener/src/data/rareCollection.csv",
                "TCGOpener/src/data/holoCollection.csv"
            };
        
            for (String filePath : csvFiles) {
                try (FileWriter writer = new FileWriter(filePath, false)) {
                    // Create a FileWriter without the append flag to overwrite the file content
                    writer.write(""); 
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
            SwingUtilities.invokeLater(() -> {
                TCGOpenerGUI newTCG = new TCGOpenerGUI();
                newTCG.buildCollectionFromCSVs();
            });
        }
    }
