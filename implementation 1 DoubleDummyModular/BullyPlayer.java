/* File: BullyPlayer.java - March 2014 */

import java.util.*;

/**
 * This is the BullyPlayer class.
 *
 * The play in this class will always be the highest card possible to play.
 * This player aims to win every trick it can, and will always play its
 * highest card which follows the rules.
 *
 * @author Lewis Carey
 */
public class BullyPlayer implements Player  {

   private ArrayList<Card> hand = new ArrayList<Card>();
   private int position;
   private Controller controller;

   public BullyPlayer () {
      
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
      info += "I am BULLY player " + position + " and my hand is " + hand.size() + "\n\n";

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
      
      // Play the HIGHEST CARD in the group
      Card tempCard = legalHands.get(0);
      for (Card i : legalHands) {
         if (i.getRank() > tempCard.getRank()) {
            tempCard = i;
         }
      }
      
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

   public int getPosition() {
      return position;
   }

   public ArrayList<Card> getHand() {
      return hand;
   }

   public Card doubleDummyPlay (ArrayList<Card> hand, ArrayList<ArrayList<Card>> opponentsHands, ArrayList<Card> currentTrick) {
      return null;
   }

   
}
