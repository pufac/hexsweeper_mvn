import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Random;

/**
 * Ez a fő game loop class
 */
public class Game implements Serializable {
    public JFrame gamewindow;
    public LinkedList<LinkedList<Tile>> grid = new LinkedList<LinkedList<Tile>>();
    public Settings settings;
    public Drawer drawer;
    public int totalghostcount = 0;

    private int placedbombs;
    private int tilepadding = 50;
    private int tilesize = 50;
    private int offset = 30;

    private Random random = new Random();

    private final int startx = 40, starty = 100;
    private final int sizex = 47, sizey = 54;//46,49   jó: 47,54

    public boolean firstclick = true;
    public boolean secondchance = true;

    public boolean gamewon = false;

    @Override
    public String toString() {
        return firstclick+","+secondchance;
    }


    /**
     * Inicializál egy játékot, a megadott beállításokkal
     * @param settings A beállítások
     */
    public void init(Settings settings)
    {
        gamewindow = new JFrame("MINESWEEPER");

        gamewindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gamewindow.setLayout(new BorderLayout());

        this.settings = settings;
        gamewindow.setBackground(Color.black);
        //max: 40, 18



        secondchance = settings.secondchance;

        InputMap im = gamewindow.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = gamewindow.getRootPane().getActionMap();
        im.put(KeyStroke.getKeyStroke("ESCAPE"),"escapePressed");
        am.put("escapePressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {

                    Object[] options = { "Folytatás", "Új játék","Menü","Mentés és kilépés"};
                    int n = JOptionPane.showOptionDialog(null, "Biztos, hogy ki akarsz lépni?","Kilépés",
                            JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    if(n == 1){
                        Object[] options1 = { "Igen", "Mégse"};
                        int n1 = JOptionPane.showOptionDialog(null, "Biztos szeretnél új játékot?","Új játék?",
                                JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE, null, options1, options1[0]);
                        if(n1 == 0) StartNewGame();
                    }
                    if(n == 2)
                    {
                        Object[] options1 = { "Igen", "Nincs mentés"};
                        int n1 = JOptionPane.showOptionDialog(null, "El szeretnéd menteni előtte?","Mentés?",
                                JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE, null, options1, options1[0]);
                        if(n1 == 0) Main.serializer.Serialize(false);

                        Main.mainmenu.setVisible(true);
                        gamewindow.dispose();
                    }
                    if(n == 3){
                        Main.serializer.Serialize(true);
                    }


            }
        });

        drawer = new Drawer(settings.x,settings.y,offset);

        drawer.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int[] a = GetClickedTileIndex(e.getPoint());
                if(SwingUtilities.isLeftMouseButton(e))
                {
                    Tile clickedTile = GetClickedTile(e.getPoint());
                    ClickedLeft(clickedTile,a);
                }
                if(SwingUtilities.isRightMouseButton(e))
                {

                    Tile clickedTile = GetClickedTile(e.getPoint());
                    ClickedRight(clickedTile,a);

                }

                CheckWinCondition(true);


                drawer.repaint();
            }
        });
        drawer.setBounds(0,0,1200,700);
        //drawer.setBackground(Color.white);

        GenerateHexagons(settings.x,settings.y);

        gamewindow.add(drawer);
        drawer.repaint();
        gamewindow.pack();
        gamewindow.setVisible(true);

        gamewindow.setSize(1200,700);

        gamewindow.setBackground(Color.blue);
        gamewindow.repaint();


        //DistributeBombs();
        //MarkTilesForBombs();
        drawer.repaint();
        //gamewindow.setSize(settings.x * 48, settings.y * 56);
        Point lastpointY = grid.get(0).get(grid.get(0).size()-1).points[2]; //legalsó hexagon legalsó pontja
        Point lastpointX = grid.get(grid.size()-1).get(0).points[0]; //legjobboldali hexagon legjobboldali pontja
        gamewindow.setSize(lastpointX.x + 25, lastpointY.y + 45);
    }

    /**
     * Megnézi, hogy nyert-e a játékos (megnézi hány tile lehet maximum felfedhető, és hogy hány van felfedve)
     * @param popup Felugorjon-e a "Nyertél!" ablak
     */
    public void CheckWinCondition(boolean popup){
        int normaltiles = settings.x * settings.y - settings.bombs - totalghostcount;
        int current = 0;
        for (int i = 0; i < settings.x; i++) {
            for (int j = 0; j < settings.y; j++) {
                if(!grid.get(i).get(j).hidden && !grid.get(i).get(j).ghost) current++;
            }
        }

        //WIN CONDITION
        if(normaltiles == current){
            if(popup)
            {
                drawer.repaint();
                System.out.println("you win");
                Object[] options = {"Új játék", "Menü", "Kilépés"};
                int n = JOptionPane.showOptionDialog(null, "Nyertél! Van kedved még játszani?","Nyertél!",
                        JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                if(n == JOptionPane.YES_OPTION)
                {
                    StartNewGame();
                }
                if(n == JOptionPane.NO_OPTION)
                {
                    Main.mainmenu.setVisible(true);
                    gamewindow.dispose();
                }
                if(n == JOptionPane.CANCEL_OPTION)
                {
                    System.exit(0);
                }
            }
            gamewon = true;
        }
    }

    /**
     * Nyit egy új játékot, ugyan azzal a beállításokkal, mint az előző játék volt
     */
    public void StartNewGame()
    {
        Game newgame = new Game();
        Point prev = gamewindow.getLocation();
        Main.game.gamewindow.dispose();
        Main.game = newgame;
        newgame.init(settings);
        newgame.gamewindow.setLocation(prev);

        drawer.repaint();
        gamewindow.repaint();
    }

    /**
     * Ha egy nullás tile-ra klikkelünk, akkor ez a rekurzív függvény lefut, és megnézi, hogy a környező tile-ok nullák-e.
     * Ha igen, akkor azokra is meghívja ezt. (Tile grid indexekkel működik)
     * @param x X. tile
     * @param y Y. tile
     */
    public void RecursiveDiscovery(int x, int y){

        if(grid.get(x).get(y).bombcount != 0 || !grid.get(x).get(y).hidden)
        {
            grid.get(x).get(y).hidden = false;
            return;
        }
        if(grid.get(x).get(y).ghost) return;
        grid.get(x).get(y).hidden = false;
        if(grid.get(x).get(y).bomb) return;

        if(x % 2 == 1)
        {
            if(x - 1 >= 0) RecursiveDiscovery(x-1,y);
            if(x + 1 < grid.size()) RecursiveDiscovery(x+1,y);

            if(y - 1 >= 0) RecursiveDiscovery(x,y-1);
            if(y - 1 >= 0 && x + 1 < grid.size()) RecursiveDiscovery(x+1,y-1);
            if(y - 1 >= 0 && x - 1 >= 0) RecursiveDiscovery(x-1,y-1);

            if(y + 1 < grid.get(0).size()) RecursiveDiscovery(x,y+1);
        }else{
            if(x - 1 >= 0) RecursiveDiscovery(x-1,y);
            if(x + 1 < grid.size()) RecursiveDiscovery(x+1,y);

            if(y - 1 >= 0) RecursiveDiscovery(x,y-1);

            if(y + 1 < grid.get(0).size()) RecursiveDiscovery(x,y+1);
            if(y + 1 < grid.get(0).size() && x + 1 < grid.size()) RecursiveDiscovery(x+1,y+1);
            if(y + 1 < grid.get(0).size() && x - 1 >= 0) RecursiveDiscovery(x-1,y+1);
        }
    }

    /**
     * Ez jobbklikkel egy tile-ra
     * @param tile Megadott tile
     * @param a Tile indexe a gridben
     */
    public void ClickedRight(Tile tile, int[] a)
    {
        //Tile clickedTile = GetClickedTile(e.getPoint());
        if(tile == null || tile.ghost) return;
        Tile tile1 = grid.get(a[0]).get(a[1]);
        //FLAG
        if(tile1.hidden)
        {
            if(tile1.flagged)
            {
                tile1.flagged = false;
            }else{
                tile1.flagged = true;
            }
        }
    }


    /**
     * Ez balklikkel egy tile-ra
     * @param tile Megadott tile
     * @param a Tile indexe a gridben
     */
    public void ClickedLeft(Tile tile, int[] a){
        //System.out.println("left: " + GetClickedTile(e.getPoint()));
        //Tile tile = GetClickedTile(e.getPoint());
        if(tile == null || tile.flagged || tile.ghost) return;

        if(tile.bomb)
        {
            if(DoesPlayerDie(true,a)){
                return;
            }
        }

        if(firstclick)
        {
            firstclick = false;
            boolean correct = false;
            do
            {
                DistributeBombs();
                if(!grid.get(a[0]).get(a[1]).bomb)
                {
                    correct = true;
                }
            }
            while(!correct);
            MarkTilesForBombs();
        }

        if(tile.bombcount == 0 && a[0] != -1) RecursiveDiscovery(a[0],a[1]);
        tile.hidden = false;
    }

    /**
     * Elosztja a bombákat a griden
     */
    public void DistributeBombs(){
        placedbombs = 0;

        for (int i = 0; i < settings.x; i++) {
            for (int j = 0; j < settings.y; j++) {
                if(grid.get(i).get(j).bomb)
                {
                    grid.get(i).get(j).bomb = false;
                }
            }

        }

        while(settings.bombs > placedbombs){
            int randX = random.nextInt(settings.x);
            int randY = random.nextInt(settings.y);

            if(!grid.get(randX).get(randY).bomb && !grid.get(randX).get(randY).ghost)
            {
                grid.get(randX).get(randY).bomb = true;
                placedbombs++;
            }
        }
        drawer.repaint();
    }

    /**
     * Megnézi, hogy veszt-e a player a jelenlegi kattintásnál
     * @param popup Felugorjon-e a "Vesztettél!" popup
     * @param a Kattintott tile indexe
     * @return Vesztett-e
     */
    public boolean DoesPlayerDie(boolean popup, int[] a){
        grid.get(a[0]).get(a[1]).clickedbomb = true;
        if(secondchance)
        {
            secondchance = false;
            Object[] options = {"Új játék","Menü","Utolsó esély"};
            grid.get(a[0]).get(a[1]).clickedbomb = true;
            drawer.repaint();
            int n = JOptionPane.showOptionDialog(null, "Meghaltál!","Játék vége..?",
                    JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if(n == JOptionPane.YES_OPTION)
            {
                StartNewGame();
            }
            if(n == JOptionPane.NO_OPTION)
            {
                Main.mainmenu.setVisible(true);
                gamewindow.dispose();
            }
            return false;
        }

        if(popup)
        {

            drawer.repaint();
            System.out.println("meghalsz csá");


            Object[] options2 = {"Új játék","Menü"};
            //JOptionPane.showMessageDialog(null,"Meghaltál.","Meghaltál",JOptionPane.ERROR_MESSAGE);
            int r = JOptionPane.showOptionDialog(null,"Új játék?","Játék vége.",JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE,null,options2,options2[0]);
            if(r == JOptionPane.YES_OPTION)
            {
                //start new game
                StartNewGame();
            }
            else{
                //exit
                Main.mainmenu.setVisible(true);
                gamewindow.dispose();
            }
        }
        return true;
    }


    /**
     * Minden tile-ra ráírja, hogy hány bomba van körülötte
     */
    public void MarkTilesForBombs()
    {
        int bombs = 0;

        for (int i = 0; i < grid.size(); i++) {
            for (int j = 0; j < grid.get(0).size(); j++) {
                bombs = 0;
                if(i % 2 == 1)
                {
                    if(i - 1 >= 0 && grid.get(i-1).get(j).bomb) bombs++;
                    if(i + 1 < grid.size() && grid.get(i+1).get(j).bomb) bombs++;

                    if(j - 1 >= 0 && grid.get(i).get(j-1).bomb) bombs++;
                    if(j - 1 >= 0 && i + 1 < grid.size() && grid.get(i+1).get(j-1).bomb) bombs++;
                    if(j - 1 >= 0 && i - 1 >= 0 && grid.get(i-1).get(j-1).bomb) bombs++;

                    if(j + 1 < grid.get(0).size() && grid.get(i).get(j+1).bomb) bombs++;
                }else{
                    if(i - 1 >= 0 && grid.get(i-1).get(j).bomb) bombs++;
                    if(i + 1 < grid.size() && grid.get(i+1).get(j).bomb) bombs++;

                    if(j - 1 >= 0 && grid.get(i).get(j-1).bomb) bombs++;

                    if(j + 1 < grid.get(0).size() && grid.get(i).get(j+1).bomb) bombs++;
                    if(j + 1 < grid.get(0).size() && i + 1 < grid.size() && grid.get(i+1).get(j+1).bomb) bombs++;
                    if(j + 1 < grid.get(0).size() && i - 1 >= 0 && grid.get(i-1).get(j+1).bomb) bombs++;
                }


                grid.get(i).get(j).bombcount = bombs;
            }
        }
        drawer.repaint();
    }

    /**
     * Feltölti a gridet hexagonokkal
     * @param numx Hány legyen egy sorban
     * @param numy Hány legyen egy oszlopban
     */
    public void GenerateHexagons(int numx, int numy)
    {
        int ghostcount = 0;
        int alltiles = settings.x * settings.y;
        int notbombtiles = alltiles - settings.bombs;
        int maxghostcount = (int) (notbombtiles * 0.4);

        for (int i = 0; i < numx; i++) {
            grid.add(new LinkedList<>());
            for (int j = 0; j < numy; j++) {

                Tile tile;
                if(i % 2 == 1)
                {
                    tile = new Tile(i*sizex+startx,j*sizey+starty-28);
                }
                else{
                    tile = new Tile(i*sizex+startx,j*sizey+starty);
                }
                if(random.nextInt(100) <  20 && settings.enableghosts && ghostcount < maxghostcount) //20% esély ghostra
                {
                    ghostcount++;
                    tile.ghost = true;
                }
                grid.get(i).add(tile);
            }
        }
        totalghostcount = ghostcount;
    }


    /**
     * Visszaadja a klikkelt Tile-t
     * @param p A klikkelt JFrame koordináta
     * @return A klikkelt Tile
     */
    public Tile GetClickedTile(Point p)
    {
        System.out.println("click: " + p);
        for (int i = 0; i < grid.size(); i++) {
            for (int j = 0; j < grid.get(0).size(); j++) {
                int[] x = new int[6];
                int[] y = new int[6];
                for (int k = 0; k < 6; k++) {
                    x[k] = (int) grid.get(i).get(j).points[k].x;
                    y[k] = (int) grid.get(i).get(j).points[k].y;
                }
                Polygon poly = new Polygon(x, y, 6);
                if(poly.contains(p))
                {
                    return grid.get(i).get(j);
                }
            }
        }
        return null;
    }

    /**
     * Visszaadja a klikkelt Tile indexét
     * @param p A klikkelt JFrame koordináta
     * @return A klikkelt tile indexjei
     */
    private int[] GetClickedTileIndex(Point p)
    {
        for (int i = 0; i < grid.size(); i++) {
            for (int j = 0; j < grid.get(0).size(); j++) {
                int[] x = new int[6];
                int[] y = new int[6];
                for (int k = 0; k < 6; k++) {
                    x[k] = (int) grid.get(i).get(j).points[k].x;
                    y[k] = (int) grid.get(i).get(j).points[k].y;
                }
                Polygon poly = new Polygon(x, y, 6);
                if(poly.contains(p))
                {
                    return new int[] {i, j};
                }
            }
        }
        return new int[] {-1, -1};
    }



}
