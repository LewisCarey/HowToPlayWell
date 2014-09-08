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
public class DoubleDummy_one  {

   private static boolean verbose = false;

   // Returns the number of hearts collected by the player during the play out
   // Currently assumes that the player starts the trick
   public static int PlayOut(ArrayList<Card> hand, ArrayList<ArrayList<Card>> opponentsHands) {

      // Stores points
      int[] playerScores = new int[4];

      // Puts the players together
      ArrayList<ArrayList<Card>> players = new ArrayList<ArrayList<Card>>();
      // Deep copy the hands
      for (int i = 0; i < 4; i++) {
         players.add(new ArrayList<Card>());
      }
      for (Card i : hand) {
         players.get(0).add(new Card(i.getSuit(), i.getRank()));
      }
      for (int x = 1; x < 4; x++) {
         for (Card i : opponentsHands.get(x-1)) {
            players.get(x).add(new Card (i.getSuit(), i.getRank()));
         }
      }
      /*
      players.add(hand);
      players.add(opponentsHands.get(0));
      players.add(opponentsHands.get(1));
      players.add(opponentsHands.get(2));
      */
      // Randomise the starting trick
      Random rand = new Random();
      int startPlayer = rand.nextInt(4);
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
         //System.out.println(startPlayer);
         playerScores[startPlayer] += getPoints(currentTrick);

      }

      // Return player score
      return playerScores[0];
   }

   // Play out the round FROM HALFWAY THROUGH A TRICK
   // Since a state is passed, we assume that we should start the playout halfway through the trick.
   public static int PlayOut(ArrayList<Card> preHand, ArrayList<ArrayList<Card>> preOpponentsHands, State state) {
      //System.out.println("IM BEING PASSED A HAND : " + preOpponentsHands.get(0));

      ArrayList<Card> hand = new ArrayList<Card>();
      ArrayList<ArrayList<Card>> opponentsHands = new ArrayList<ArrayList<Card>>();
      for (Card i : preHand) {
         hand.add(new Card(i.getSuit(), i.getRank()));
      }
      opponentsHands.add(new ArrayList<Card>());
      opponentsHands.add(new ArrayList<Card>());
      opponentsHands.add(new ArrayList<Card>());
      for (int x = 0; x < 3; x++) {
         for (Card i : preOpponentsHands.get(x)) {
            opponentsHands.get(x).add(new Card(i.getSuit(), i.getRank()));
         }
      }
      // Stores points
      int[] playerScores = new int[4];

      // Puts the players together
      ArrayList<ArrayList<Card>> players = new ArrayList<ArrayList<Card>>();

      if (state.getCurrentTrick() == null) {
         return PlayOut(hand, opponentsHands);
      }
      
      // If there is a state with a current trick being played
      if (state.getCurrentTrick().size() != 4) {
         // Remove the currently played cards from the opponents hands

         // For each card already played
         for (Card z : state.getCurrentTrick()) {
            // Scan through opponents and remove if found
            for (int i = 0; i < 3; i++) {
               for (int x = 0; x < opponentsHands.get(i).size(); x++) {
                  if (opponentsHands.get(i).get(x).match(z)) opponentsHands.get(i).remove(x);
               }
            }
         }  
      }

      // Redeal the opponents hands based on how many cards were played
      ArrayList<Card> opponentsHandsPool = new ArrayList<Card>();
      for (int i = 0; i < 3; i++) {
         for (Card x : opponentsHands.get(i)) {
            opponentsHandsPool.add(new Card(x.getSuit(), x.getRank()));
         }
      }
      int dealCount = 0;
      int deckIndex = 0;
      while (dealCount < Misc.RealSize(state.getCurrentTrick())) {
         opponentsHands.get(dealCount).clear();
         for (int i = 0; i < hand.size() - 1; i++) {
            opponentsHands.get(dealCount).add(new Card(opponentsHandsPool.get(deckIndex).getSuit(), opponentsHandsPool.get(deckIndex).getRank()));
            deckIndex++;
         }
         dealCount++;
      }
      // For the remaining hands just deal normal number, since no cards have been played
      while (dealCount < 3) {
         opponentsHands.get(dealCount).clear();
         for (int i = 0; i < hand.size(); i++) {
            opponentsHands.get(dealCount).add(new Card(opponentsHandsPool.get(deckIndex).getSuit(), opponentsHandsPool.get(deckIndex).getRank()));
            deckIndex++;
         }
         dealCount++;
      }

      players.add(hand);
      players.add(opponentsHands.get(0));
      players.add(opponentsHands.get(1));
      players.add(opponentsHands.get(2));
      if (verbose) {
         System.out.println("PLAYING OUT WITH THESE HANDS");
         System.out.println(players.get(0));
         System.out.println(players.get(1));
         System.out.println(players.get(2));
         System.out.println(players.get(3));
         System.out.println("------------------------------");
      }
      
      // Opponents hands should now correctly reflect what has been played

      int startPlayer = Misc.RealSize(state.getCurrentTrick()) + 1;

      // Deep copy the states current trick
      ArrayList<Card> currentTrick = new ArrayList<Card>();
      for (Card i : state.getCurrentTrick()) {
         if(i != null) currentTrick.add(new Card(i.getSuit(), i.getRank()));
      }
      if (verbose) System.out.println("Starting with = " + currentTrick);

      boolean resetTrick = false;
      // While there are still cards to be played
      while (players.get(0).size() > 0) {         
         if (resetTrick) {
            currentTrick = new ArrayList<Card>();
         }
         int beginningCardsInTrick = currentTrick.size();
         // Play out the trick
         for (int i = 0; currentTrick.size() < 4; i++) {

            int currentPlayer = (startPlayer + i) % 4;

            if (verbose) {
               System.out.println(players.get(currentPlayer));
               System.out.println("Current Players Hand = " + players.get(currentPlayer));
               System.out.println("Current Trick = " + currentTrick);
               System.out.println("--------------");
            }
            // If an opponent has already played, it should not play again
            if (beginningCardsInTrick > 0 && currentPlayer != 0 && currentPlayer <= beginningCardsInTrick) {

            } else {
               Card cardToPlay = playCard(players.get(currentPlayer), currentTrick);
               currentTrick.add(cardToPlay);
               // Remove card played from players hand
               int cardIndex = 0;
               for (int z = 0; z < players.get(currentPlayer).size(); z++) {
                  if (players.get(currentPlayer).get(z).match(cardToPlay)) cardIndex = z;
               }
               players.get(currentPlayer).remove(cardIndex);
            }
            
         }

         // Calculate the winner of the trick and how many points they receive
         int previousStartPlayer = startPlayer - beginningCardsInTrick;
         if (verbose) {
            System.out.println("END TRICK = " + currentTrick);
            System.out.println("Player who started trick = " + previousStartPlayer);
            System.out.println("** NEW TRICK **");
         }
         startPlayer = getWinner(currentTrick, previousStartPlayer);
         //System.out.println(startPlayer);
         playerScores[startPlayer] += getPoints(currentTrick);
         resetTrick = true;
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
         if (trumpSuit == trick.get(i).getSuit()) {
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
