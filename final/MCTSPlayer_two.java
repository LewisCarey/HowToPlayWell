import java.util.*;

import java.util.*;

public class MCTSPlayer_two implements Player {

   private ArrayList<Card> hand = new ArrayList<Card>();
   private int position;
   private Controller controller;
   private int[] options;

   private boolean verbose = false;

   public MCTSPlayer_two (int[] options) {
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
      info += "I am MCTS_two player " + position + " and my hand is " + hand.size() + "\n\n";

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
      
      // Find out what is legal to play - We use this as our hand
      //ArrayList<Card> legalHands = controller.getLegalHands(tempHand);
      
      // Create a score based system for these possible plays
      ArrayList<Integer> scores = new ArrayList<Integer>();
      for (int i = 0; i < tempHand.size(); i++) {
         scores.add(0);
      }

      long startTime = System.nanoTime();

      if (verbose) {
         System.out.println("Starting Hand:\n\n");
         for (Card hand : tempHand) {
            System.out.println(hand);
         }
      }
      
      long endTime = System.nanoTime();
      int count = 0;
      // While there is still time remaining, keep searching
      while (((endTime - startTime)/1000000000) < 1) { // Is this seconds?
         // Find the card
         Card tempCard = MCTS_two.search(state, tempHand, options);
         if (verbose) System.out.println("Performed MCTS: " + tempCard);
         count++;
         // Update scores
         for (int i = 0; i < tempHand.size(); i++) {
            if (tempHand.get(i).match(tempCard)) {
               scores.set(i, scores.get(i) + 1);
               //System.out.println("MATCH");
            }
         }
         endTime = System.nanoTime();
      }

      if (verbose) System.out.println("Searched " + count + " MCTS trees.");

      // Calculate what card was most successful
      int max = 0;
      for (int i = 0; i < tempHand.size(); i++) {
         if (scores.get(i) > scores.get(max)) max = i;
      }
      Card winningCard = tempHand.get(max);

      // Find the card to remove
      int index = 0;
      for (int i = 0; i < hand.size(); i++) {
         if (hand.get(i).match(winningCard)) index = i;
      }
      if (verbose) System.out.println("Playing: " + hand.get(index));
      hand.remove(index);
      return winningCard;
   }
   
}
