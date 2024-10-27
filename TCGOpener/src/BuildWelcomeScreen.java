import javax.swing.*;
import java.awt.*;

public class BuildWelcomeScreen {
    private JPanel welcomescreen;
    private GridBagConstraints gbConst;
    private JButton fromWelcometoDesk;
    private JButton fromWelcometoCollection;
    private Dimension wideScreen = new Dimension(1920, 1080);

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
}
