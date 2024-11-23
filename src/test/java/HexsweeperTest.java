import org.junit.jupiter.api.*;

import javax.swing.*;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tesztelő class
 * Lefuttat összesen 12 tesztet, 3 classen keresztül
 */
public class HexsweeperTest {

    Game game;
    Settings s;

    /**
     * Lefut minden teszt előtt egy pálya inicializálás. Van benne egy feltétel, hogyha a lefuttatandó teszt nevébe szerepel az hogy "Custom", akkor
     * nem futtatja ezt le. Ezáltal lehet akármelyik tesztnek saját inicializálást csinálni.
     * @param testInfo Ebbe van benne, hogy melyik teszt fut
     */
    @BeforeEach
    public void init(TestInfo testInfo) {
        if(testInfo.toString().contains("Custom"))
        {
            return;
        }
        s = new Settings();
        s.x = 10;
        s.y = 10;
        s.bombs = 30;
        s.enableghosts = false;
        game = new Game();
        Main.game = game;
        game.init(s);
    }

    /**
     * Teszteli hogy megfelelő mennyiségű bombát generál-e a játék
     */
    @Test // game.DistributeBombs
    public void TestBombCount(){

        int bombcount = 0;
        game.DistributeBombs();
        for (int i = 0; i < s.x; i++) {
            for (int j = 0; j < s.y; j++) {
                if(game.grid.get(i).get(j).bomb) bombcount++;
            }
        }
        assertEquals(s.bombs,bombcount);
    }

    /**
     * Teszteli, hogy jól reagál-e a játék, ha egy tile-ra balklikkelek (felfedi-e). Bárhova klikkelhetne a teszt, az első klikk sosem lesz bomba (firstclick = false)
     */
    @Test //game.ClickedLeft
    public void TestLeftClickedTile(){
        int[] a = {0,0};
        Tile firsttile = game.grid.get(0).get(0);
        game.ClickedLeft(firsttile,a);
        assertFalse(firsttile.hidden);
    }

    /**
     * Teszteli, hogy jól reagál-e a játék, ha egy tile-ra jobbklikkelek (rak-e zászlót)
     */
    @Test //game.ClickedRight
    public void TestRightClickedTile(){
        int[] a = {0,0};
        Tile firsttile = game.grid.get(0).get(0);
        game.ClickedRight(firsttile,a);
        assertTrue(firsttile.flagged);
    }

    /**
     * Teszteli, hogy létrejön-e a gamewindow inicializáláskor
     */
    @Test //game.init
    public void TestGameWindow(){
        try {
            JFrame gamewindow = game.gamewindow;
        }
        catch (Exception e) {
            fail();
        }
    }

    /**
     * Teszteli, hogy nyer-e a játékos ha nincsen több felfedezetlen tile
     */
    @Test //game.CheckWinCondition
    public void TestCustomWinCondition(){
        s = new Settings();
        s.x = 10;
        s.y = 10;
        s.bombs = 0;
        s.enableghosts = false;
        s.secondchance = false;
        game = new Game();
        game.firstclick = true;
        Main.game = game;
        game.init(s);

        game.ClickedLeft(game.grid.get(0).get(0), new int[]{0,0});
        game.CheckWinCondition(false);
        assertTrue(game.gamewon);
    }

    /**
     * Teszteli, hogy veszít-e a játékos ha bombára kattint (secondchance ilyenkor ki van kapcsolva)
     */
    @Test //game.DoesPlayerDie
    public void TestCustomDeath(){
        s = new Settings();
        s.x = 10;
        s.y = 10;
        s.bombs = 99;
        s.enableghosts = false;
        s.secondchance = false;
        game = new Game();
        Main.game = game;
        game.init(s);
        game.ClickedLeft(game.grid.get(0).get(0), new int[]{0,0});
        assertTrue(game.DoesPlayerDie(false,new int[]{1,1}));
        //game.ClickedLeft(game.grid.get(1).get(1), new int[]{1,1});

    }

    /**
     * Teszteli, hogy jól számozza-e meg a tile-okat a játék, ha van körülöttük bomba
     */
    @Test //game.MarkTilesForBombs
    public void TestCustomMarkBombs(){
        s = new Settings();
        s.x = 10;
        s.y = 10;
        s.bombs = 0;
        s.enableghosts = false;
        s.secondchance = false;
        game = new Game();
        Main.game = game;
        game.init(s);

        game.grid.get(0).get(0).bomb = true;
        game.grid.get(0).get(1).bomb = true;
        game.grid.get(1).get(0).bomb = true;

        game.MarkTilesForBombs();

        assertEquals(3,game.grid.get(1).get(1).bombcount);

    }

    /**
     * Teszteli a rekurzív feldefezés függvényt, azaz, hogyha klikkelek egy üres tile-ra, akkor a környezőket felfedi-e
     */
    @Test //game.RecursiveDiscovery
    public void TestCustomRecursiveDiscovery(){
        s = new Settings();
        s.x = 10;
        s.y = 10;
        s.bombs = 0;
        s.enableghosts = false;
        s.secondchance = false;
        game = new Game();
        Main.game = game;
        game.init(s);

        game.ClickedLeft(game.grid.get(0).get(0), new int[]{0,0});

        assertFalse(game.grid.get(5).get(5).hidden);

    }

    /**
     * Teszteli, hogy a GetClickedTile függvény megfelelő tile-t ad-e vissza
     */
    @Test //game.GetClickedTile
    public void TestCorrectTileReturn(){
        Point p = new Point(50,80);

        Tile hex = game.GetClickedTile(p);

        assertEquals(game.grid.get(0).get(0),hex);

    }

    /**
     * Teszteli, hogy sikeresen kezdd-e új játékot
     */
    @Test //game.StartNewGame
    public void TestStartNewGame(){
        game.StartNewGame();

        boolean same = (game == Main.game);
        assertFalse(same);

    }

    /**
     * Teszteli, hogy 6 pontja lett-e egy generált tile-nak
     */
    @Test //tile.generatePointsFromCenter
    public void TestPointsOfHexagon(){
        Tile tile = game.grid.get(0).get(0);

        int a = 0;
        for (int i = 0; i < tile.points.length; i++) {
            a++;
        }
        assertEquals(6,a);

    }

    /**
     * Teszteli, hogy tényleg nem lehet-e rossz beállításokkal elinditani a játékot
     */
    @Test //tile.generatePointsFromCenter
    public void TestCustomSettingsErrors(){
        int fails = 0;

        Settings s = new Settings();
        Main.settings = s;
        s.x = 10;
        s.y = 10;
        s.bombs = 101;
        if(!Main.checkifcorrectsettings(false)){
            fails++;
        }
        s.bombs = 100;
        if(!Main.checkifcorrectsettings(false)){
            fails++;
        }
        s.x = 0;
        if(!Main.checkifcorrectsettings(false)){
            fails++;
        }
        s.diff="Choose";
        if(!Main.checkifcorrectsettings(false)){
            fails++;
        }
        assertEquals(4,fails);

    }
}
