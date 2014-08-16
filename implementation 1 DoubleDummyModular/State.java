/* File: State.java - March 2014 */

import java.util.*;

/**
 * This is the State class for the hearts game simulator used in 2014
 * COSC490 project.
 * This class records an instance / snapshot of a game state. It can be
 * given to the Controller class as a starting point for playing out
 * hearts. It mainly records environment variables detailing the state of
 * play - for example, what cards have been played, who has what points,
 * and so on.
 *
 * @author Lewis Carey
 */

public class State {

   private boolean heartsBroken;
   private int startPlayer, currentPlayer;
   private ArrayList<Card> cardsPlayed, currentTrick;
   private int[] scores;
   private int[] trickRecord;
   private ArrayList<Player> playerType = new ArrayList<Player>();
   
   // State PRIOR to a trick being played
   public State (boolean heartsBroken, int startPlayer, ArrayList<Card> cardsPlayed){
      this.heartsBroken = heartsBroken;
      this.startPlayer = startPlayer;
      this.cardsPlayed = cardsPlayed;
   }

   // State during a trick being played
   public State (boolean heartsBroken, int startPlayer, ArrayList<Card> cardsPlayed, int currentPlayer, ArrayList<Card> currentTrick) {
      this(heartsBroken, startPlayer, cardsPlayed);
      this.currentTrick = currentTrick;
      this.currentPlayer = currentPlayer;
   }

   // State for recording meta information about the game
   public State (int[] scores, int[] trickRecord) {
      this.scores = scores;
      this.trickRecord = trickRecord;
   }

   // Set up the player types currently playing the game
   public void assignPlayerTypes (ArrayList<Player> players) {
      // Should not need to deep copy, since just using static methods?
      playerType = players;
   }

   public ArrayList<Player> getPlayerTypes () {
      return playerType;
   }

   public boolean isHeartsBroken () {
      return heartsBroken;
   }

   public int getStartPlayer () {
      return startPlayer;
   }

   public int[] getScores () {
      return scores;
   }

   /* Using the array of cardsPlayed, we look at what cards are left to be played with. */
   public ArrayList<Card> getRemainingCards () {
      ArrayList<Card> deck = new ArrayList<Card>();
      
      // Generate an ArrayList of 52 cards
      int suit = 0, rank = 0;
      while (suit < 4) {
         while (rank < 13) {
            Card tempCard = new Card(suit, rank);
            deck.add(tempCard);
            rank++;
         }
         suit++;
         rank = 0;
      }

      // Remove the cards which have already been played
      for (Card i : cardsPlayed) {
         int x = 0;
         while (x < deck.size()) {
            if (i.match(deck.get(x))) {
               deck.remove(x);
               break;
            }
            x++;
         }
      }

      return deck;
   }

   /* Using the array of cardsPlayed, we look at what cards are left to be played with. */
   public ArrayList<Card> getRemainingCards (ArrayList<Card> subtract) {
      ArrayList<Card> deck = getRemainingCards();
      
      // Remove the cards which have already been played
      for (Card i : subtract) {
         int x = 0;
         while (x < deck.size()) {
            if (i.match(deck.get(x))) deck.remove(x);
            x++;
         }
      }

      return deck;
   }

   public void setCurrentTrick (ArrayList<Card> trick) {
      this.currentTrick = trick;
   }

   public ArrayList<Card> getCurrentTrick () {
      return currentTrick;
   }

   // Looking at the cards played, the current trick, and a set of cards, returns a list of Cards which may legally
   // be played next from the set.
   public ArrayList<Card> getLegalPlays (ArrayList<Card> candidates, int trumpSuit) {
      ArrayList<Card> deck = candidates;

      ArrayList<Card> deleteList = new ArrayList<Card>();

      // If we have a current trick in play
      if (currentTrick != null && Misc.RealSize(currentTrick) != 0) {
         // Find the trump suit
         //int trumpSuit = currentTrick.get(0).getSuit();
         // If there is a card of that suit among the candidates, delete all other cards not of that suit
         boolean suitPresent = false;
         for (Card i : candidates) {
            if (i.getSuit() == trumpSuit) {
               suitPresent = true;
               break;
            }
         }
         // Depending on whether or not suit was present, mark cards of other suit for deletion
         if (suitPresent) {
            for (Card i : candidates) {
               if (i.getSuit() != trumpSuit) deleteList.add(i);
            }
         }
      }

      // Remove all cards on the delete list
      for (Card i : deleteList) {
         deck.remove(i);
      }

      return deck;


   }



}
