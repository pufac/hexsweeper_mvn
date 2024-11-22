import javax.swing.*;
import java.io.*;

public class Serializer {
    public void Serialize()
    {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("hex.save"))) {
            oos.writeObject(Main.game);
            oos.writeObject(Main.settings);
            for (int i = 0; i < Main.settings.x; i++) {
                for (int j = 0; j < Main.settings.y; j++) {
                    oos.writeObject(Main.game.grid.get(i).get(j));
                }
            }

            System.out.println("Game state serialized");
        } catch (IOException e) {
            System.err.println("Serialization failed: " + e.getMessage());
        }
    }
    public void Deserialize(){
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("hex.save"))) {
            Game temp = (Game) ois.readObject();
            Main.settings = (Settings) ois.readObject();
            Main.newgame(Main.settings);

            Main.game.firstclick = temp.firstclick;
            Main.game.secondchance = temp.secondchance;



            for (int i = 0; i < Main.settings.x; i++) {
                for (int j = 0; j < Main.settings.y; j++) {
                    Tile tile = (Tile) ois.readObject();
                    Main.game.grid.get(i).set(j,tile);
                }
            }
            Main.game.drawer.repaint();
            System.out.println("Save deserialized");
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,"Nincsen mentett játék állás! A \"hex.save\" nevű filet rakd a .jar mellé egy mappán belül!","Nincs mentés file",JOptionPane.ERROR_MESSAGE);
            System.err.println("Deserialization failed: " + e.getMessage());
        }
    }
}
