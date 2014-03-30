/* File: Player.java - March 2014 */
//package raw_sim;

/* MAKE AN INTERFACE FOR THIS */
import java.util.*;

/**
 * This is the Player class. It contains an array of cards equal to its current
 * hand, as well as methods for playing a specific card.
 *
 * @author Lewis Carey
 */
public class Player  {

   private ArrayList<Card> hand = new ArrayList<Card>();
   private int position;
   private Controller controller;

   Player (int position, ArrayList<Card> hand, Controller controller) {
      this.position = position;
      this.controller = controller;
      // Sets up the ArrayList of Cards
      for (Card i : hand) {
         this.hand.add(i);
      }
   }

   public String toString() {
      String info = "";
      info += "I am player " + position + " and my hand is " + hand.size() + "\n\n";

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

   public Card getPlay() {
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
      hand.remove(tempCard);
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
   
}