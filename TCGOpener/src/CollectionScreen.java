import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class CollectionScreen {
    private JLayeredPane collection;
    private JPanel desk2;
    private JPanel displayArea;
    private JPanel collectionButtons;
    private JButton fromCollectiontoDesk;
    private JButton openBinder;
    private JButton nextPage;
    private JButton previousPage;
    private JPanel cardZoomPanel;
    private Image deskImage;
    private CardLabel zoomedCard;
    private ArrayList<CardLabel> overallCollection = new ArrayList<CardLabel>();
    private ArrayList<CardLabel> commonCollection = new ArrayList<CardLabel>();
    private ArrayList<CardLabel> energyCollection = new ArrayList<CardLabel>();
    private ArrayList<CardLabel> uncommonCollection = new ArrayList<CardLabel>();
    private ArrayList<CardLabel> rareCollection = new ArrayList<CardLabel>();
    private ArrayList<CardLabel> holoCollection = new ArrayList<CardLabel>();
    private ArrayList<CardLabel> currCollection = overallCollection;
    private Dimension wideScreen = new Dimension(1920, 1080);
    private int[] startIndex = {0, 0, 0, 0, 0, 0};      // int[0] = overall, int [1] = common, int[2] = energy,
    private int[] endIndex = {12, 12, 12, 12, 12, 12};  // int[3] = uncommon, int[4] = rare, int[5] = holo
    private int[] currIndex = {0, 0, 0, 0, 0, 0};
    private int chosenCurrIndex = 0;
    private int chosenStartIndex = 0;
    private int chosenEndIndex = 12;


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
}
