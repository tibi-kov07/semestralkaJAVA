package logika;

import java.io.*;

public class GameSaveManager {
    private static final String SAVE_FILE = "savegame.dat";

    public static void saveGame(Game game) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(game);
            System.out.println("Uloží aktuálny stav hry.");
        } catch (IOException e) {
            System.out.println("Chyba pri ukladaní hry: " + e.getMessage());
        }
    }

    public static Game loadGame() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            Game loadedGame = (Game) ois.readObject();
            System.out.println("Načíta posledný uložený stav.");
            return loadedGame;
        } catch (FileNotFoundException e) {
            System.out.println("Žiadna uložená hra sa nenašla.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Chyba pri načítavaní uloženej hry: " + e.getMessage());
        }
        return null;
    }
}
