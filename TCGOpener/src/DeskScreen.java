import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class DeskScreen {
    private JLayeredPane desk1;
    private JPanel desk1Panel;
    private JLayeredPane hand;
    private JPanel cardHolder;
    private JButton newPack;
    private JButton revealNormal;
    private JButton revealRare;
    private JButton fromDesktoCollection;
    private Image deskImage;
    private String deskImagePath;
    private Dimension wideScreen = new Dimension(1920, 1080);
    private Dimension cardHolderDimension = new Dimension(1200, 1000);
    private Dimension packsize = new Dimension(300, 515);
    private int handStartX = (wideScreen.width - packsize.width) - 150;
    private int handStartY = (wideScreen.height - packsize.height) - 200;
    private GridBagConstraints gbConst = new GridBagConstraints();

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
}
