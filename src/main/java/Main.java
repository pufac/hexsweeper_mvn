import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main {
    public static JFrame mainmenu;
    public static Settings settings = new Settings();
    public static Game game;
    public static Serializer serializer;


    public static void main(String[] args)
    {
        init();
        setupMainmenu();
    }

    public static void init()
    {
        mainmenu = new JFrame("HexSweeper");
        mainmenu.setSize(400,400);
        mainmenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainmenu.setLayout(null);
        mainmenu.setBackground(Color.darkGray);
        mainmenu.setVisible(true);
        serializer = new Serializer();
        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException e) {
            // handle exception
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public static int tryCalculateBombs(JTextField x, JTextField y, String diff){
        if(!x.getText().isEmpty() && !y.getText().isEmpty()){
            int mult = Integer.parseInt(x.getText()) * Integer.parseInt(y.getText());
            switch(diff){
                case "Easy":
                    return (int)Math.round(mult*0.1);

                case "Normal":
                    return (int)Math.round(mult*0.15);

                case "Hard":
                    return (int)Math.round(mult*0.2);

                case "Very hard":
                    return (int)Math.round(mult*0.35);

            }
        }
        return -1;
    }

    public static void setupMainmenu(){

        //ghost checkbox
        JCheckBox ghostcheckbox = new JCheckBox("Enable islands");
        ghostcheckbox.setSelected(true);
        ghostcheckbox.setBounds(20,210,400,30);
        ghostcheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settings.enableghosts = ghostcheckbox.isSelected();
            }
        });
        settings.enableghosts = true;

        //second chance checkbox
        JCheckBox secondchancecheckbox = new JCheckBox("Enable second chance");
        secondchancecheckbox.setSelected(true);
        secondchancecheckbox.setBounds(20,240,400,30);
        secondchancecheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settings.secondchance = secondchancecheckbox.isSelected();
            }
        });
        settings.secondchance = true;

        //main title
        JLabel title = new JLabel("HEXSWEEPER");
        title.setBounds(80,20,400,25);
        title.setFont(new Font("SansSerif", Font.BOLD,30));


        //test button

        JButton startgame = new JButton("Start Game");
        startgame.setBounds(30,60,100,50);
        startgame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newgame(settings);
            }
        });


        JButton loadsave = new JButton("Load game");
        loadsave.setBounds(140,60,100,50);
        loadsave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                serializer.Deserialize();
            }
        });

        //width label
        JLabel widthlabel = new JLabel("Map Width:");
        widthlabel.setBounds(30,95,100,50);

        //width input
        JTextField sizeX = new JTextField("");
        sizeX.setBounds(30,130,30,30);
        sizeX.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                char c = e.getKeyChar();
                if(Character.isISOControl(c)) return;
                if(!Character.isDigit(c)){ e.consume(); return; }
                if(sizeX.getText().isEmpty()) return;
                settings.x = Integer.parseInt(sizeX.getText());

            }
        });
        sizeX.setText("40");
        settings.x = 40;

        //height label
        JLabel heightLabel = new JLabel("Map Height:");
        heightLabel.setBounds(200,95,100,50);

        //height input
        JTextField sizeY = new JTextField("");
        sizeY.setBounds(200,130,30,30);
        sizeY.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                char c = e.getKeyChar();
                if(Character.isISOControl(c)) return;
                if(!Character.isDigit(c)){ e.consume(); return; }
                if(sizeY.getText().isEmpty()) return;
                settings.y = Integer.parseInt(sizeY.getText());

            }
        });
        sizeY.setText("17");
        settings.y = 17;

        //bombs
        JLabel bomblabel = new JLabel("Bombs:");
        bomblabel.setBounds(30,145,100,50);

        JTextField bombs = new JTextField("");
        bombs.setBounds(30,180,30,30);


        //autobombs
        JButton autobombs = new JButton("Dynamic");
        autobombs.setBounds(70,185,100,20);

        //difficultyComboBox
        String[] diffs = {"Choose","Easy", "Normal", "Hard", "Very hard","Custom"};
        JComboBox<String> difficultyCB = new JComboBox<>(diffs);
        difficultyCB.setSelectedItem("Normal");
        settings.diff = "Choose";
        difficultyCB.setBounds(200,185,100,25);
        difficultyCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String diff = (String) difficultyCB.getSelectedItem();
                settings.diff = diff;
                settings.bombs = tryCalculateBombs(sizeX,sizeY,diff);
                if(!diff.equals("Custom"))
                {
                    bombs.setText((String.valueOf(settings.bombs)));
                }

            }
        });
        //bombs.setText("10");
        settings.diff = "Normal";
        settings.bombs = tryCalculateBombs(sizeX,sizeY,"Normal");
        bombs.setText(settings.bombs+"");

        bombs.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {

                char c = e.getKeyChar();
                if(Character.isISOControl(c)){
                    difficultyCB.setSelectedItem("Custom");
                    return;
                }
                if(!Character.isDigit(c)){
                    e.consume();
                }else{
                    difficultyCB.setSelectedItem("Custom");
                    settings.bombs = Integer.parseInt(bombs.getText());
                }
            }
        });

        //add stuff
        mainmenu.add(title);
        mainmenu.add(sizeX);
        mainmenu.add(sizeY);
        mainmenu.add(startgame);
        mainmenu.add(widthlabel);
        mainmenu.add(heightLabel);
        mainmenu.add(bomblabel);
        mainmenu.add(bombs);
        mainmenu.add(loadsave);
        //mainmenu.add(autobombs);
        mainmenu.add(difficultyCB);
        mainmenu.add(ghostcheckbox);
        mainmenu.add(secondchancecheckbox);

        mainmenu.repaint();
    }

    public static void newgame(Settings settings){
        if(checkifcorrectsettings(true))
        {
            game = new Game();
            game.init(settings);
            mainmenu.setVisible(false);
        }

    }

    public static boolean checkifcorrectsettings(boolean popup){
        if(settings.bombs > settings.x * settings.y || (settings.bombs == 0 ||settings.bombs == -1)){
            if(popup) JOptionPane.showMessageDialog(null,"There are more bombs than tiles, or zero bombs\n" + settings.bombs, "Game can't be started", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(settings.bombs == settings.x * settings.y)
        {
            if(popup) JOptionPane.showMessageDialog(null,"ne\n", "Game can't be started", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(settings.x == 0 || settings.y == 0){
            if(popup) JOptionPane.showMessageDialog(null,"Not all fields are completed", "Game can't be started", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(settings.diff.equals("Choose")){
            if(popup) JOptionPane.showMessageDialog(null,"Choose a difficulty", "Game can't be started", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

}
