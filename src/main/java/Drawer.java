import javax.swing.*;
import java.awt.*;

/**
 * Pálya kirajzoló class
 */
public class Drawer extends JPanel {

    int numx, numy;

    Game game;
    Color border = new Color(36,36,36);
    Color bg = new Color(0, 26, 92);
    Color tile = new Color(232, 232, 232);
    Color flagged = Color.orange;
    Color ghost = Color.pink;

    boolean drawghosts = false;

    int topoffset = 40;

    /**
     * Rajzoló class konstruktora
     * @param numx Pálya szélessége hexagon számban (X)
     * @param numy Pálya magassága hexagon számban (Y)
     * @param offset
     */
    public Drawer(int numx, int numy, int offset) {
        this.numx = numx;
        this.numy = numy;
        game = Main.game;
        this.setBackground(bg);
    }

    /**
     * Meghívja a JPanel paintComponentjét, és a DrawHexagons(Graphics)-ot
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Drawhexagons(g);
    }

    /**
     * Ez a függvény kirajzolja a drawer JPanel-jére a hexagonokat. Ez be is színezi be pirosra (ha megnyomott bomba), sárgára (ha flagged), fehérre (ha már felfedezett).
     * A tile-okon lévő számokért is ez felelős. Miután kész van, meghívja a repaint() függvényt
     * @param g Graphics
     */
    public void Drawhexagons(Graphics g){
        game.gamewindow.setBackground(bg);
        Graphics2D g2d = (Graphics2D) g;
        for (int i = 0; i < game.grid.size(); i++) {
            for (int j = 0; j < game.grid.get(0).size(); j++) {
                Polygon p = new Polygon();
                for (int k = 0; k < 6; k++) {
                    p.addPoint((int)game.grid.get(i).get(j).points[k].x,(int)game.grid.get(i).get(j).points[k].y);
                }
                g2d.setStroke(new BasicStroke(1));
                //g2d.setColor(Color.white);
                if(game.grid.get(i).get(j).bomb && false) // DRAW BOMBS?
                {
                    //BOMB COLOR
                    g2d.setColor(Color.red);
                    g2d.fillPolygon(p);
                    //g2d.setColor(Color.white);
                }
                else{

                    if(game.grid.get(i).get(j).ghost)
                    {
                        if(drawghosts)
                        {
                            g2d.setColor(ghost);
                            g2d.setStroke(new BasicStroke(4));
                            g2d.drawPolygon(p);
                        }else{
                            g2d.setColor(bg);
                        }
                        //g2d.setColor(ghost);
                        //g2d.fillPolygon(p);

                        continue;
                    }

                    //NORMAL COLOR
                    if(game.grid.get(i).get(j).hidden)
                    {
                        if(game.grid.get(i).get(j).flagged)
                        {
                            g2d.setColor(flagged);
                        }else g2d.setColor(Color.gray);

                    }else{
                        g2d.setColor(tile);
                    }
                    g2d.fillPolygon(p);
                }

                if(game.grid.get(i).get(j).clickedbomb)
                {
                    g2d.setColor(Color.red);
                    g2d.fillPolygon(p);
                }

                g2d.setStroke(new BasicStroke(4));

                //BORDER COLOR
                g2d.setColor(border);
                g2d.drawPolygon(p);
                g2d.setFont(new Font("Arial", Font.BOLD, 25));
                if(!game.grid.get(i).get(j).hidden && !game.grid.get(i).get(j).clickedbomb)
                {
                    g2d.setColor(DetermineStringColour(game.grid.get(i).get(j).bombcount));
                    g2d.drawString(game.grid.get(i).get(j).bombcount+"",game.grid.get(i).get(j).points[0].x-32,game.grid.get(i).get(j).points[0].y+5);
                }
            }
        }
        //game.gamewindow.setBackground(Color.red);
        this.repaint();
        game.gamewindow.repaint();
    }

    /**
     * Megkap egy számot, és vissza ad egy színt, hogy milyen színnel legyen ez a szám kiírva a felfedett tile-ra
     * @param num A szám (0-6)
     * @return A szín (Color)
     */
    private Color DetermineStringColour(int num){
        switch (num){
            case 0:
                return tile;

            case 1:
                return Color.gray;

            case 2:
                return Color.orange;

            case 3:
                return Color.blue;

            case 4:
                return Color.magenta;

            case 5:
                return Color.red;

        }
        return Color.black;
    }

}
