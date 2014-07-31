/* File: RandomPlayer.java - March 2014 */

import java.util.*;

/**
 * This is the RandomPlayer class.
 *
 * The play in this class is determined entirely by randomisation.
 * As long as a group of cards satisfy to rules of the play, then a random
 * card will be picked.
 *
 * @author Lewis Carey
 */
public class RandomPlayer implements Player  {

   private ArrayList<Card> hand = new ArrayList<Card>();
   private int position;
   private Controller controller;

   public RandomPlayer () {
      
   }

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
      info += "I am RANDOM player " + position + " and my hand is " + hand.size() + "\n\n";

      int i = 0;
      while (i < hand.size()) {
         info += hand.get(i).toString() + "\n";
         i++;
      }

      return info;
   }

   public Card getLead() {
      // Find out what is legal to play
      // Play a card randomly from this subset
      Card tempCard = hand.get(0);
      hand.remove(0);
      return tempCard;
   }

   public Card getPlay(State state) {
      // Have to deep copy the hand for this to work
      ArrayList<Card> tempHand = new ArrayList<Card>();
      for (Card i : hand) {
         tempHand.add(i);
      }
      
      // Find out what is legal to play
      ArrayList<Card> legalHands = controller.getLegalHands(tempHand);
      
      // Play a card randomly from this subset
      Random rand = new Random();
      int randomNum = rand.nextInt(legalHands.size());
      
      Card tempCard = legalHands.get(randomNum);

      // Find the card to remove
      int index = 0;
      for (int i = 0; i < hand.size(); i++) {
         if (hand.get(i).match(tempCard)) index = i;
      }

      hand.remove(index);
      return tempCard;
   }

   public boolean getStart() {
      // Finds out if we have the 2 of clubs and therefore can start play
      for (Card i : hand) {
         if (i.getRank() == 0 && i.getSuit() == 3) {
            return true;
         }
      }

      return false;
   }

   public int getPosition() {
      return position;
   }

   public ArrayList<Card> getHand() {
      return hand;
   }
   
}
