import java.util.*;

import java.util.*;

public class MCTSPlayer implements Player {

   private ArrayList<Card> hand = new ArrayList<Card>();
   private int position;
   private Controller controller;
   private int[] options;

   public MCTSPlayer (int[] options) {
      this.options = options;
   }

   // Sets up the player state
   public void setUp (int position, ArrayList<Card> hand, Controller controller) {
      this.position = position;
      this.controller = controller;
      // Sets up the ArrayList of Cards
      for (Card i : hand) {
         this.hand.add(i);
      }
   }

   public String toString() {
      String info = "";
      info += "I am MCTS player " + position + " and my hand is " + hand.size() + "\n\n";

      int i = 0;
      while (i < hand.size()) {
         info += hand.get(i).toString() + "\n";
         i++;
      }

      return info;
   }

   // Plays a card to begin the trick
   public Card getLead() {
      // Start with first card in the hand
      Card tempCard = hand.get(0);
      hand.remove(0);
      return tempCard;
   }

   // Determines whether or not they have the 2 of clubs
   public boolean getStart() {
      // Finds out if we have the 2 of clubs and therefore can start play
      for (Card i : hand) {
         if (i.getRank() == 0 && i.getSuit() == 3) {
            return true;
         }
      }

      return false;
   }

   // Finds position of player
   public int getPosition() {
      return position;
   }

   // Returns the players current hand
   public ArrayList<Card> getHand() {
      return hand;
   }

   // Plays a card in the trick
   // Implements a MCTS to find the best play
   public Card getPlay(State state) {
      // Have to deep copy the hand for this to work
      ArrayList<Card> tempHand = new ArrayList<Card>();
      for (Card i : hand) {
         tempHand.add(i);
      }
      
      // Find out what is legal to play
      ArrayList<Card> legalHands = controller.getLegalHands(tempHand);
      
      // Play a card based on a MCTS with this hand
      Card tempCard = MCTS.search(state, legalHands, options);

      // Find the card to remove
      int index = 0;
      for (int i = 0; i < hand.size(); i++) {
         if (hand.get(i).match(tempCard)) index = i;
      }

      hand.remove(index);
      return tempCard;
   }
   
}
