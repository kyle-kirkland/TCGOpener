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
    - Add card zoom feature : *** DONE (ended up using a JPanel then adding a CardLabel (zoomedCard) to it with the corresponding image from largeCards folder)
    - Break down overall program into smaller classes
        - Class for building each screen (Desk, Collection, Welcome)
        - Class for building each card (CardLabel)
        - Class for building each pack (BoosterPack)
        - Maybe a class just containing a bunch of the random helper methods? Is that a thing that's done?
    - Identify which private fields can be made local to certain methods or even restrict to the broken off classes
    - Add image for welcome screen (possibly take image used for desk and toss Pokemon logo over the buttons with a bunch of cards scattered    *** DONE
        around facing up and down, todo in GIMP)                                                                                                *** DONE
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

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import java.io.*;

public class TCGOpener extends JFrame {
    private WelcomeScreen welcomeScreen;
    private DeskScreen deskScreen;
    private CollectionScreen collectionScreen;
    private Dimension wideScreen = new Dimension(1920, 1080);

    public TCGOpener() {
        buildMainPanel();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void buildMainPanel() {
        JPanel root = new JPanel(new CardLayout());
        root.setPreferredSize(wideScreen);

        welcomeScreen = new WelcomeScreen();
        deskScreen = new DeskScreen();
        collectionScreen = new CollectionScreen();

        root.add("Welcome Screen", welcomeScreen);
        root.add("Desk Screen", deskScreen);
        root.add("Collection Screen", collectionScreen);

        setTitle("TCG Opener");
        setSize(wideScreen);
        add(root);
    }
        

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TCGOpener tcgOpener = new TCGOpener();
            tcgOpener.buildCollectionFromCSVs();
        });
    }
}