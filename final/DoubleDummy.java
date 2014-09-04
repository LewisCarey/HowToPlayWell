/* File: DoubleDummy.java - April 2014 */

import java.util.*;

/**
 * This class exists for the sole purpose to play a double dummy Hearts simulation out
 * and determine whether the player will win or lose.
 *
 * Assumes every player wants to win.
 *
 * @author Lewis Carey
 */
public class DoubleDummy  {

   private static boolean verbose = false;

   // DoubleDummy method - returns the score the first player (us) received.
   // remainingDeck has been deep copyed
   public static int PlayOut (ArrayList<ArrayList<Card>> remainingDeck, int[] scores, int startPlayer) {
      /*
      for (ArrayList<Card> player : remainingDeck) {
         System.out.println("Remaining Players Size: " + player.size());
      }
      */
      // Play out the hands until no cards remain
      while (remainingDeck.get(0).size() > 0) {

         ArrayList<Card> currentTrick = new ArrayList<Card>();

         // Playout the trick
         for (int i = 0; i < 4; i++) {
            // Add card to current trick
            currentTrick.add(playCard(remainingDeck.get((startPlayer + i) % 4), currentTrick));
            // Remove card from the hand
            int index = Misc.RemoveIndex(currentTrick.get(i), remainingDeck.get((startPlayer +i) % 4));
            remainingDeck.get((startPlayer +i) % 4).remove(index);
         }
         
         // Calculate the score of the trick
         int trickScore = getPoints(currentTrick);

         // Calculate the winner of the trick (relative to order played)
         int winner = (getWinner(currentTrick, startPlayer) + startPlayer) % 4;

         // Add the score to the winner
         scores[winner] += trickScore;

         if (verbose) System.out.println("Trick: " + currentTrick + "\nWinner: " + winner + "\nStart Player: " + startPlayer);

         // Set the startPlayer to be the new winner
         startPlayer = winner;



      }

      return scores[0];
   }

   // Plays the highest card it can provided there are no hearts currently being played. If there are, it
   // will play the lowest card.
   private static Card playCard (ArrayList<Card> hand, ArrayList<Card> currentTrick) {
      // Finds the trump suit
      int trumpSuit = -1;
      if (currentTrick.size() > 1) {
         trumpSuit = currentTrick.get(0).getSuit();
      }

      // If we have a trump suit, they are the legal hand, otherwise everything is legal
      ArrayList<Card> candidatePlays = new ArrayList<Card>();
      if (trumpSuit != -1) {
         for (Card i : hand) {
            if (i.getSuit() == trumpSuit) candidatePlays.add(i);
         }

         if (candidatePlays.size() == 0) candidatePlays = hand;
      } else {
         candidatePlays = hand;
      }

      // Candidate plays now contains all the cards we could play

      // See if there are any hearts present in the trick
      boolean heartsPresent = false;
      for (Card i : currentTrick) {
         if (i.getSuit() == 0) heartsPresent = true;
      }
      //System.out.println("candidatePlays. Hand size: " + hand.size());
      // If heart is present, play lowest card, else play highest
      Card play = candidatePlays.get(0);
      if (heartsPresent) {
         for (Card i : candidatePlays) {
            if (play.getRank() > i.getRank()) play = i;
         }
      } else {
         for (Card i : candidatePlays) {
            if (play.getRank() < i.getRank()) play = i;
         }
      }
      
      return play;
   }

   // Calculates score of the trick
   private static int getPoints (ArrayList<Card> trick) {
      int score = 0;
      for (Card i : trick) {
         // A heart
         if (i.getSuit() == 0) score++;
         // Black bitch
         if (i.getSuit() == 1 && i.getRank() == 10) score += 13;
      }
      return score;
   }

   // Gets the winner of the trick BASED ON ORDER OF PLAY
   // Does not return the index, returns X player to play
   private static int getWinner (ArrayList<Card> trick, int startPlayer) {
      int trumpSuit = trick.get(startPlayer).getSuit();

      int max = startPlayer;
      for (int i = 0; i < 4; i++) {
         if (trick.get((i + startPlayer) % 4).getSuit() == trumpSuit) {
            if (trick.get((i + startPlayer) % 4).getRank() > trick.get(max % 4).getRank()) max = startPlayer + i;
         }
      }

      return max - startPlayer;
   }
   
}
