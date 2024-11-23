import javax.swing.*;
import java.io.*;

/**
 * Serializáló class
 */
public class Serializer {
    /**
     * Játék állás adatokat kiírja egy fájlba
     */
    public void Serialize(boolean quit)
    {
        boolean success = false;
        String saveError = "";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("hex.save"))) {
            oos.writeObject(Main.game);
            oos.writeObject(Main.settings);
            for (int i = 0; i < Main.settings.x; i++) {
                for (int j = 0; j < Main.settings.y; j++) {
                    oos.writeObject(Main.game.grid.get(i).get(j));
                }
            }


            System.out.println("Game state serialized");
            success = true;
        } catch (IOException e) {
            System.err.println("Serialization failed: " + e.getMessage());
            saveError = e.getMessage();
        }
        if(success)
        {
            if(quit)
            {
                Object[] options = {"Kilépés"};
                int n = JOptionPane.showOptionDialog(null, "Mentés sikeres!","Siker!",
                        JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                System.exit(0);
            }else{
                Object[] options = {"Ok"};
                int n = JOptionPane.showOptionDialog(null, "Mentés sikeres!","Siker!",
                        JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            }

        }else{
            Object[] options = {"Folytatás","Kilépés mindenképp"};
            int n = JOptionPane.showOptionDialog(null, "Nem sikerült menteni! :(\n"+saveError,"Nem sikerült!",
                    JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.ERROR_MESSAGE, null, options, options[0]);
            if(n == 1){
                Object[] options1 = {"Igen","Mégse"};
                int n1 = JOptionPane.showOptionDialog(null, "Biztos ki akarsz lépni?","Biztos?",
                        JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE, null, options1, options1[0]);
                if(n1 == 0 && quit) System.exit(0);
            }
        }
    }

    /**
     * Fájlból visszaolvassa egy játék állását (és automatikusan betölti azt)
     */
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
