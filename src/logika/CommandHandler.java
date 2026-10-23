package logika;

import java.io.Serializable;

public class CommandHandler implements Serializable {
    private static final long serialVersionUID = 1L;

   private final Game game;

   public CommandHandler(Game game){
       this.game = game;
   }

   public void handleCommand (String input){
       if (input.isEmpty()) return;

       String cleanInput = removeDiacritics
   }
}