import java.awt.*;
import java.io.Serializable;

public class Tile implements Serializable {
    public Point[] points = new Point[6];
    private float radius = 30;

    public boolean bomb;

    public int bombcount;
    public boolean hidden = true;
    public boolean clickedbomb = false;
    public boolean flagged = false;

    public boolean ghost = false;

    private Game game = Main.game;

    public Tile(int x, int y){
        generatePointsFromCenter(x,y);
    }


    private void generatePointsFromCenter(int x, int y){
        for (int i = 0; i < 6; i++) {
            Point p = new Point((int) (radius * Math.cos(Math.PI/3*i) + x),(int) (radius * Math.sin(Math.PI/3*i)) + y);

            points[i] = p;
        }
    }

    @Override
    public String toString()
    {
        return "tile,"+bomb+","+bombcount+","+hidden+","+clickedbomb+","+flagged;
    }

}