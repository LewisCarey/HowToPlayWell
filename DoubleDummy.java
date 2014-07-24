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

   // Returns the number of hearts collected by the player during the play out
   // Currently assumes that the player starts the trick
   public static int PlayOut(ArrayList<Card> hand, ArrayList<ArrayList<Card>> opponentsHands) {

      // Stores points
      int[] playerScores = new int[4];

      // Puts the players together
      ArrayList<ArrayList<Card>> players = new ArrayList<ArrayList<Card>>();
      players.add(hand);
      players.add(opponentsHands.get(0));
      players.add(opponentsHands.get(1));
      players.add(opponentsHands.get(2));

      int startPlayer = 0;
      // While there are still cards to be played
      while (players.get(0).size() > 0) {
         ArrayList<Card> currentTrick = new ArrayList<Card>();
         

         // Play out the trick
         for (int i = 0; i < 4; i++) {
            
            int currentPlayer = (startPlayer + i) % 4;

            //System.out.println(players.get(currentPlayer));

            Card cardToPlay = playCard(players.get(currentPlayer), currentTrick);
            currentTrick.add(cardToPlay);
            // Remove card played from players hand
            int cardIndex = 0;
            for (int z = 0; z < players.get(currentPlayer).size(); z++) {
               if (players.get(currentPlayer).get(z).match(cardToPlay)) cardIndex = z;
            }
            players.get(currentPlayer).remove(cardIndex);
         }

         // Calculate the winner of the trick and how many points they receive
         int previousStartPlayer = startPlayer;
         startPlayer = getWinner(currentTrick, previousStartPlayer);
         playerScores[startPlayer] += getPoints(currentTrick);

      }

      // Return player score
      return playerScores[0];
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

   private static int getWinner (ArrayList<Card> trick, int startPlayer) {
      // Get trump suit
      int trumpSuit = trick.get(startPlayer).getSuit();

      // See what trump suit wins
      int winningIndex = startPlayer;
      for (int i = 0; i < trick.size(); i++) {
         // If suits match
         if (trick.get(winningIndex).getSuit() == trick.get(i).getSuit()) {
            // If rank is greater than, change it
            if (trick.get(winningIndex).getRank() < trick.get(i).getRank()) winningIndex = i;
         }
      }

      return winningIndex;
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
   
}
