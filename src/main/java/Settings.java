import java.io.Serializable;

/**
 * A beállítások class, ami tárolja a jelenlegi játék beállításait
 */
public class Settings implements Serializable {
    public int x;
    public int y;
    public boolean specialtype;
    public int bombs;
    public String diff;
    public int tilesize;

    public boolean enableghosts;
    public boolean secondchance;


    @Override
    public String toString() {
        return x+","+y+","+bombs+","+diff;
    }
}
