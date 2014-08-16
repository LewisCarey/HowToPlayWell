import java.util.*;

public interface Player {

   public String toString();

   // Plays a card to begin the trick
   public Card getLead();
   // Plays a card in the trick
   public Card getPlay(State state);
   // Determines whether or not they have the 2 of clubs
   public boolean getStart();
   // Finds position of player
   public int getPosition();

   // Sets up the player state
   public void setUp (int position, ArrayList<Card> hand, Controller controller);

   // Returns the players current hand
   public ArrayList<Card> getHand();
   
}
