import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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