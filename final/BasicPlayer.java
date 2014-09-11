/* File: AdvancedPlayer.java - March 2014 */

import java.util.*;

/**
 * This is the AdvancedPlayer class.
 *
 * The play in this class will attempt to be as advanced as possible. A rule based approach is used.
 *    -> If last to play in trick
 *       -> Play highest card of the suit ELSE
 *       -> Play Queen of Spades ELSE
 *       -> Play highest card of suit with fewset remaining cards
 *    -> If leading the trick
 *       -> Open with the lowest card...?
 *    -> If 2nd or 3rd in trick
 *       -> Play heighest card of suit that will not win trick ELSE
 *       -> Play Queen of Spades ELSE
 *       -> Play highest card of suit with fewset remaining cards
 *
 * @author Lewis Carey
 */
public class BasicPlayer implements Player  {

   private ArrayList<Card> hand = new ArrayList<Card>();
   private int position;
   private Controller controller;

   public BasicPlayer () {
      
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
      info += "I am an BASIC PLAYER player " + position + " and my hand is " + hand.size() + "\n\n";

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

   // Consider getting rid of one suit
   public Card getPlay(State state) {
      //return hand.get(0);
      
      // Have to deep copy the hand for this to work
      ArrayList<Card> tempHand = new ArrayList<Card>();
      for (Card i : hand) {
         tempHand.add(i);
      }
      
      // Find out what is legal to play
      ArrayList<Card> legalHands = controller.getLegalHands(tempHand);
      
      Card tempCard = legalHands.get(0);
      // Find out the player we are in the trick
      int player = 0;

      if (state.getCurrentTrick() != null) player = Misc.RealSize(state.getCurrentTrick());

      if (player == 0) {
         // If starting player, open with lowest card
         for (Card i : legalHands) {
            if (i.getRank() < tempCard.getRank()) {
               tempCard = i;
            }
         }  
      } else {
         // Last player

         // Check to see if we still have the suit
         boolean playingToSuit = false;
         //System.out.println(state.getCurrentTrick());

         /*
         // Gets the trump suit
         int trumpSuit = -1;
         for (Card i : state.getCurrentTrick()) {
            if (i != null) {
               trumpSuit = i.getSuit();
               break;
            }
         }
         */
         
         int trumpSuit;
         if (Misc.RealSize(state.getCurrentTrick()) == 1) {
            trumpSuit = state.getCurrentTrick().get(3).getSuit();
         } else if (Misc.RealSize(state.getCurrentTrick()) == 2) {
            trumpSuit = state.getCurrentTrick().get(2).getSuit();
         } else {
            trumpSuit = state.getCurrentTrick().get(1).getSuit();
         }
         


         for (Card i : legalHands) {
            if (i.getSuit() == trumpSuit) {
               playingToSuit = true;
               break;
            }
         }

         if (playingToSuit) {
            // Play lowest card possible
            // Play the LOWEST CARD in the group
            tempCard = legalHands.get(0);
            for (Card i : legalHands) {
               if (i.getRank() < tempCard.getRank()) {
                  tempCard = i;
               }
            }

         } else {
            // If we are not playing to suit, play random
            Random rand = new Random();
            tempCard = legalHands.get(rand.nextInt(legalHands.size()));

         }
      }

      // Tempcard is now the card we are going to play

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
