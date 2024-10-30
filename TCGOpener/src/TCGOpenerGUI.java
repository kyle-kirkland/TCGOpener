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
 * Main class for the Trading Card Game Opening Simulator.
 * Handles the GUI, user interactions, and card collection management.
 * @author Kyle Kirkland
 */
public class TCGOpenerGUI extends JFrame {
    
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
    private int[] startIndex = {0, 0, 0, 0, 0, 0};      // int[0] = overall, int [1] = common, int[2] = energy,
    private int[] endIndex = {12, 12, 12, 12, 12, 12};  // int[3] = uncommon, int[4] = rare, int[5] = holo
    private int[] currIndex = {0, 0, 0, 0, 0, 0};
    private int[] startCollectionSizes = setCollectionSizes();
    private int[] currCollectionSizes;
    private int chosenCurrIndex = 0;
    private int chosenStartIndex = 0;
    private int chosenEndIndex = 12;   
    /**
     * Constructor for TCGOpenerGUI class.
     * Initializes the main panel and sets up the frame.
     */
    public TCGOpenerGUI() {

        createMainPanel();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

    }
    /**
     * Creates the welcome screen panel.
     * 
     * @return JPanel representing the welcome screen.
     */
    private JPanel createWelcomescreenPanel() {
        String wecomeImagePath = "/backgrounds/wood_desk_Welcome_Screen.png";
        Image welcomeImage = new ImageIcon(getClass().getResource(wecomeImagePath)).getImage();
        welcomescreen = new JPanel(new GridBagLayout()) { 
            @Override
            public void paintComponent(Graphics graphic) {
                super.paintComponent(graphic);
                graphic.drawImage(welcomeImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        welcomescreen.setPreferredSize(wideScreen); 

        gbConst = new GridBagConstraints();

        fromWelcometoDesk = new JButton("Start Opening Packs");
        fromWelcometoDesk.addActionListener(event -> switchPanel("Desk"));

        fromWelcometoCollection = new JButton("Show Current Collection");            
        fromWelcometoCollection.addActionListener(event -> {
            sortCollections();
            switchPanel("Collection");
        });

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
    /**
     * Creates the collection panel.
     * 
     * @return JLayeredPane representing the collection panel.
     */
    private JLayeredPane createCollectionPanel() {
        collection = new JLayeredPane();
        collection.setBounds(0, 0, wideScreen.width, wideScreen.height);
        collection.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (zoomedCard != null && zoomedCard != event.getSource()) {
                    swapCardSize(zoomedCard);
                }
        }});

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
        chooseCollection.addActionListener(event -> {
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
        fromCollectiontoDesk.addActionListener(event -> switchPanel("Desk"));
        openBinder = new JButton("Open Binder");
        openBinder.addActionListener(event -> {
            nextPage.setVisible(true);
            previousPage.setVisible(true);
            collectionButtons.revalidate();
            collectionButtons.repaint();
            updateDisplayArea(currCollection);
        });
        nextPage = new JButton("Next Page");
        nextPage.addActionListener(event -> nextButton(currCollection));
        nextPage.setVisible(false);
        previousPage = new JButton("Previous Page");
        previousPage.addActionListener(event -> previousButton(currCollection));
        previousPage.setVisible(false);

        cardZoomPanel = new JPanel();
        int cardZoomXpos = (int)(((wideScreen.getWidth()) / 2) - 300);
        int cardZoomYpos = (int)(((wideScreen.getHeight()) / 2) - 412);
        Dimension cardZoomSize = new Dimension(600, 825);
        cardZoomPanel.setBounds(cardZoomXpos, cardZoomYpos, cardZoomSize.width, cardZoomSize.height);
        cardZoomPanel.setOpaque(false);
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
        newPack.addActionListener(event -> openNewPack(hand));
        revealNormal = new JButton("Reveal Normal Order");
        revealNormal.addActionListener(event -> revealCards(false));
        revealRare = new JButton("Reveal Rare Last");
        revealRare.addActionListener(event -> revealCards(true));
        fromDesktoCollection = new JButton("To Collection");
        fromDesktoCollection.addActionListener(event -> {
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

    public class CardLabel extends JLabel{
        private String name; 
        private String type;
        private String rarity;
        private String cardImagePath;
        private boolean isBig = false;
            
        public CardLabel(String cardImagePath) {
            this.type = "card";
            this.cardImagePath = cardImagePath;
            ImageIcon resizedImage = new ImageIcon();
            resizedImage = resizeCardImage(new ImageIcon(getClass().getResource(cardImagePath)));
            setIcon(resizedImage);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    if (event.isShiftDown() && CardLabel.this.getParent() == displayArea && !CardLabel.this.getisBig()) {
                        swapCardSize(CardLabel.this);       
                    }
                    else if (CardLabel.this.getParent() == hand) {
                        fromHandtoDesk();
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
                    fromHandtoDesk();
                }
            });
        }
        public void fromHandtoDesk() {
            if (this.type.equalsIgnoreCase("pack")) {
                hand.remove(this);
                hand.revalidate();
                hand.repaint();
            }

            else if (this.type.equalsIgnoreCase("card") && hand.isAncestorOf(this)) {
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
                    protected void done() {
                        try {
                            get();
                            addToCardHolder();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                };
                bgThread.execute();
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
            String smallCardImagePath = cardImagePath.replaceAll("largeCards", "smallCards");
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
            return cardImagePath;
        }
    }

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

    public void swapCardSize(CardLabel card) {
        if (!card.getisBig()) {
            String largeCardPath = card.getImagePath().replaceAll("smallCards", "largeCards");
            zoomedCard = card;                
            card.setisBig(true);
            zoomedCard.setIcon(new ImageIcon(getClass().getResource(largeCardPath)));
            displayArea.remove(card);
            cardZoomPanel.add(zoomedCard);
            cardZoomPanel.setVisible(true);
            cardZoomPanel.revalidate();
            cardZoomPanel.repaint();
        }
        else {
            String smallCardPath = card.getImagePath().replaceAll("largeCards", "smallCards");
            card.setIcon(new ImageIcon(getClass().getResource(smallCardPath)));
            card.setisBig(false);
            cardZoomPanel.remove(zoomedCard);
            zoomedCard = null;
            cardZoomPanel.setVisible(false);
            updateDisplayArea(currCollection);
            cardZoomPanel.revalidate();
            cardZoomPanel.repaint();
        }
    }

    public static void displayUserGuide() {
        String userGuideText = "<html>" +
            "<h1><b>Trading Card Opening Sim User Guide</b></h1>" +
            "<h2><b>Overview</b></h2>" +
            "<p>Welcome to the Trading Card Opening Sim! This program allows you to open trading card booster packs, adding them to your <br>collection. You can view your collection, sort cards by rarity, and even zoom into your cards for a closer look. This guide will walk <br>you through all the features and functionalities that the program offers.</p>" +
            "<h2><b>Getting Started</b></h2>" +
            "<ul><b>Start Opening Packs:</b>Click this button to go to your desk where you can start opening booster packs.</ul>" +
            "<ul><b>Show Current Collection:</b>Click this button to view your current collection of cards.</ul>" +

            "<h2><b>Booster Pack Opening</b></h2>" +
            "<p>While on this screen, you can open booster packs to reveal the cards inside.</p>" +
            "<ul><b>Open New Pack:</b> Press this button to open a new booster pack. Each booster pack consists of 11 cards: 5 common, 3 uncommon, <br>2 energy cards, and 1 rare or rare-holographic card.</ul>" +
            "<ul><b>Reveal Normal Order:</b> Click this button if you wish to reveal the cards in the order they are packed .</ul>" +
            "<ul><b>Reveal Rare Last:</b> Click this button to reveal the rare card last.</ul>" +
            "<ul><b>To Collection:</b> Once you have finished opening your packs, click this button to switch over to your collection to view the cards <br>you have collected.</ul>" +

            "<h2><b>Your Collection</b></h2>" +
            "<ul><b>Choose Collection:</b> Use the Dropdown Menu to choose which collection to view. You can choose from the following options:</ul>" +
            "<li><b>Overall Collection:</b> Displays all cards all of the cards you have opened.</li>" +
            "<li><b>Common, Energy, Uncommon, Rare, Holo:</b> Displays cards based off of their specific rarities.</li>" +
            "<ul><b>Open Binder:</b> Click this button to view your collection based off of your dropdown selection.</ul>" +
            "<ul><b>Next Page/Previous Page:</b> Use these buttons to navigate through your collection. Each page displays up to 12 cards.</ul>" +
            "<ul><b>To Desk:</b> Use this button to continue opening booster packs.</ul>" +

            "<h3><b>Zoom Feature</b></h3>" +
            "<p>While viewing your collection, each card can be zoomed in to by holding the Shift key and clicking on the card. The card will appear in <br>front of you and much larger to give you a closer look at its details. To return it to the display, click <br>outside the zoomed card.</p>" +
            "<h3><b>Saving and Loading Your Collection</b></h3>" +
            "<p>Your collection is automatically saved after each pack is opened. When you restart the program, your previous collection will be loaded <br>which allows you to continue adding to it.</p>" +
            "<p><b>User Guide Shortcut:</b> Press Ctrl + G at any time to view this guide again.</p>";      
        JOptionPane.showMessageDialog(null, userGuideText, "User Guide", JOptionPane.INFORMATION_MESSAGE);
    }

    public void openNewPack(JLayeredPane inputPane) {
        SwingWorker<ArrayList<CardLabel>, Void> bgThread = new SwingWorker<>() {
            @Override
            protected ArrayList<CardLabel> doInBackground() throws Exception {
                return packCards();
            }

            @Override
            protected void done() {
                try {
                    ArrayList<CardLabel> currentPack = get();
                    cardHolder.removeAll();
                    cardHolder.revalidate();
                    cardHolder.repaint();
                    hand.removeAll();
                    hand.revalidate();
                    hand.repaint();
                    currentPack.get(0).setBounds(0, 0, packsize.width, packsize.height);
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
        bgThread.execute();
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
            displayUserGuide();
        });
    }
}