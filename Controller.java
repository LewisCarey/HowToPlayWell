/* File: Controller.java - March 2014 */
//package raw_sim;

import java.util.*;

/**
 * This is the contoller class for the raw hearts simulator for my COSC490
 * project. The program simulates an environment of the card game hearts
 * and passes this information to the player modules. This program itself
 * does not do any of the algorithms, it merely sets up the play space.
 *
 * @author Lewis Carey
 */
public class Controller {

   //private Player[] players;
   ArrayList<Card> currentTrick;
   private boolean heartsBroken;
   private int[] scores = new int[4];
   int startPlayer;
   Player[] players = new Player[4];

   /**
    * The main function, sets up the player space / hearts environment.
    *
    */
   public Controller (State state) {
      // Sets up the scores for each player
      for (int x = 0; x < scores.length; x++) {
         scores[x] = 0;
      }

      // Determines whether or not hearts is broken
      if (state != null) {
         heartsBroken = state.isHeartsBroken();
      } else {
         heartsBroken = false;
      }
      

      // Generates and assigns hands to the players based on what has already been played
      ArrayList<ArrayList> hands = GenerateHands(state);

      // Creates the players
      Player player0 = new Player(0, hands.get(0), this);
      Player player1 = new Player(1, hands.get(1), this);
      Player player2 = new Player(2, hands.get(2), this);
      Player player3 = new Player(3, hands.get(3), this);
      players[0] = player0;
      players[1] = player1;
      players[2] = player2;
      players[3] = player3;

      // Finds the player with the two of clubs, and records that for start player,
      // unless otherwise specified in the state.
      startPlayer = 0;
      if (state == null || state.getStartPlayer() == -1) {
         for (int i = 0; i < 4; i++) {
            if (players[i].getStart() == true) {
               startPlayer = i;
            }
         }
      } else {
         startPlayer = state.getStartPlayer();
      }

      // Initiates the trick holding array
     currentTrick = new ArrayList<Card>();
   }

   private static ArrayList<ArrayList> GenerateHands(State state) {
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

      // Randomises the array
      Random rand = new Random();
      int randomNum;
      Card tempCard;

      // For each index in the deck starting at the last and decrementing
      for(int i = deck.size() - 1; i > 0; i--) {
         // Pick a card from the remainding deck
         randomNum = rand.nextInt(i + 1);
         // Swap the card at the end for the random number card
         tempCard = deck.get(i);
         deck.set(i, deck.get(randomNum));
         deck.set(randomNum, tempCard);
      }
      
      // Assign the deck to the players
      ArrayList<ArrayList> splitDeck = new ArrayList<ArrayList>();
      for (int i = 0; i < 4; i++) {
         ArrayList<Card> temp = new ArrayList<Card>();
         for (int z = 0; z < 13; z++) {
            temp.add(deck.get(i * 13 + z));
         }
         splitDeck.add(temp);
      }

      return splitDeck;
   }
   
   private static int GetWinningCardIndex(ArrayList<Card> trick) {
      // Gets the winning card index. Assumes Card at position 0 was the card started with
      Card candidate = trick.get(0);
      int trumpSuit = candidate.getSuit();

      // Compares cards to see which one wins
      for (Card i : trick) {
         if (i.getSuit() == trumpSuit && i.getRank() > candidate.getRank()) {
            candidate = i;
         }
      }
      System.out.println("WINNER = " + candidate);
      return trick.indexOf(candidate);
   }

   // Returns an array of cards which a player could play
   public ArrayList<Card> getLegalHands (ArrayList<Card> currentHand) {
      int currentSuit;
      ArrayList<Card> legalPlays = new ArrayList<Card>();

      // If a suit is already in play
      if (currentTrick.size() > 0) {
         currentSuit = currentTrick.get(0).getSuit();
         // Scan through the list of cards in the current hand, adding any of the suit to a different hand
         for (Card i : currentHand) {
            if (i.getSuit() == currentSuit) {
               legalPlays.add(i);
            }
         }

         // Return the legal plays if not empty, else return entire hand
         if (legalPlays.size() > 0) {
            return legalPlays;
         } else {
            return currentHand;
         }
      }

      // If there are no hands in play

      // If hearts broken
      if (heartsBroken) {
         return currentHand;
      }

      // Otherwise return the current hand except for all the hearts
      ArrayList<Card> heartsCaught = new ArrayList<Card>();
      for (Card i : currentHand) {
         if (i.getSuit() == 0) {
            heartsCaught.add(i);
         }
      }

      //System.out.println("HEARTS CAUGHT = " + heartsCaught);
      
      for (Card i : heartsCaught) {
         currentHand.remove(i);
      }

      //System.out.println("LEGAL PLAYS = " + currentHand);
      
      return currentHand;
   }

   /* Initiates the playing of a round. Returns a State class, which is the state of the game
    * world following the play of the given number of tricks.
    */
   public State play (int tricks) {
      // Plays out the tricks specified
      for (int i = 0; i < tricks; i++) {
         System.out.println("STARTING TRICK NUMBER " + i + " --------------");
         //System.out.println(players[0] + "\n" +players[1] + "\n" +players[2] + "\n" +players[3]);
         // Each player plays a card
         currentTrick.add(players[startPlayer].getPlay());
         currentTrick.add(players[(startPlayer+1)%4].getPlay());
         currentTrick.add(players[(startPlayer+2)%4].getPlay());
         currentTrick.add(players[(startPlayer+3)%4].getPlay());

         // If a heart is in play that is not the first card
         if (!heartsBroken) {
            for (Card z : currentTrick) {
               if (z.getSuit() == 0) {
                  heartsBroken = true;
                  System.out.println("HEARTS BROKEN");
               }
            }
         }
         
         // Calculate who wins the trick
         startPlayer = GetWinningCardIndex(currentTrick);
         System.out.println(currentTrick);
         currentTrick.clear();
      }
      return null;
   }
}
