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

   ArrayList<Card> currentTrick;
   private boolean heartsBroken;
   private int[] scores = new int[4];
   int startPlayer;
   public Player[] players = new Player[4];
   private boolean trickInPlay = false;
   private int currentSuit;
   private State state;

   private static boolean verbose = true;

   /**
    * The main function, sets up the player space / hearts environment.
    *
    */
   public Controller (State state, ArrayList<Player> playerTypes) {
      this.state = state;

      // Sets up the scores for each player
      for (int x = 0; x < scores.length; x++) {
         scores[x] = 0;
      }

      // Default value for the current suit
      currentSuit = -1;
      
      // Determines whether or not hearts is broken
      if (state != null) {
         heartsBroken = state.isHeartsBroken();
      } else {
         heartsBroken = false;
      }
      

      // Generates and assigns hands to the players based on what has already been played
      ArrayList<ArrayList> hands = GenerateHands(state);

      // Creates the players
      players[0] = playerTypes.get(0);
      players[1] = playerTypes.get(1);
      players[2] = playerTypes.get(2);
      players[3] = playerTypes.get(3);
      players[0].setUp(0, hands.get(0), this);
      players[1].setUp(1, hands.get(1), this);
      players[2].setUp(2, hands.get(2), this);
      players[3].setUp(3, hands.get(3), this);

      if (verbose) {
         System.out.println("Playing with these hands");
         for (int i = 0; i < 4; i++) {
            System.out.println(players[i]);
         }
         System.out.println();
      }

      // Assigns a start player
      if (state == null || state.getStartPlayer() == -1) {
         Random rand = new Random();
         startPlayer = rand.nextInt(4);
      }
      else {
         startPlayer = state.getStartPlayer();
      }

      // Initiates the trick holding array
     currentTrick = new ArrayList<Card>(4);
   }

   public static ArrayList<ArrayList> GenerateHands(State state) {
      ArrayList<Card> deck = new ArrayList<Card>();

      if (state != null) {
         deck = state.getRemainingCards();
      } else {
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
      int handSize = deck.size() / 4;
      for (int i = 0; i < 4; i++) {
         ArrayList<Card> temp = new ArrayList<Card>();
         for (int z = 0; z < handSize; z++) {
            temp.add(deck.get(i * handSize + z));
         }
         splitDeck.add(temp);
      }

      return splitDeck;
   }
   
   private static int GetWinningCardIndex(ArrayList<Card> trick, int startPlayer) {
      // Gets the winning card index
      Card candidate = trick.get(startPlayer);
      int trumpSuit = trick.get(startPlayer).getSuit();

      // Compares cards to see which one wins
      for (Card i : trick) {
         if (i.getSuit() == trumpSuit && i.getRank() > candidate.getRank()) {
            candidate = i;
         }
      }
      //System.out.println("WINNER = " + candidate);
      return trick.indexOf(candidate);
   }

   // Returns an array of cards which a player could play
   public ArrayList<Card> getLegalHands (ArrayList<Card> currentHand) {
      ArrayList<Card> legalPlays = new ArrayList<Card>();

      // If current hand is of size 1, return the current hand
      if (currentHand.size() == 1) return currentHand;

      // If a suit is already in play
      if (trickInPlay) {
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

      // If the size of the hearts caught equals the hand, it means we can only play hearts, we are forced to break hearts early
      if (heartsCaught.size() == currentHand.size()) return currentHand;

      //System.out.println("HEARTS CAUGHT = " + heartsCaught);
      
      for (Card i : heartsCaught) {
         currentHand.remove(i);
      }

      //System.out.println("LEGAL PLAYS = " + currentHand);
      
      return currentHand;
   }

   /* Plays a certain number of games from beginning to end, and records
    * the scores in the State class it returns.
    */
   public State playGames (int gameNumber, int reporting, int handSize) {
      int[] scores = new int[4];

      for (int i = 0; i < gameNumber; i++) {
         // Plays the first games
         State tempState = play(handSize, reporting);

         // Assign scores
         for (int x = 0; x < 4; x++) {
            scores[x] += tempState.getScores()[x];
         }

         // Generates new hands
         ArrayList<ArrayList> hands = GenerateHands(state);
         // Assigns new hands to the players
         players[0].setUp(0, hands.get(0), this);
         players[1].setUp(1, hands.get(1), this);
         players[2].setUp(2, hands.get(2), this);
         players[3].setUp(3, hands.get(3), this);

         //System.out.println("END OF GAME " + i + "\n\n\n\n");
      }

      return new State(scores, new int[13]);
   }

   /* Initiates the playing of a round. Returns a State class, which is the state of the game
    * world following the play of the given number of tricks.
    */
   public State play (int tricks, int reporting) {
      // Variables for recording game info
      int[] scores = new int[4];
      int[] trickRecord = new int[13];

      // Plays out the tricks specified
      for (int i = 0; i < tricks; i++) {
         state.setCurrentTrick(new ArrayList<Card>(4));
         trickInPlay = false;
         for (int x = 0; x < 4; x++) {
            currentTrick.add(null);
         }
         
         if(reporting != 0) System.out.println("STARTING TRICK NUMBER " + i + " --------------");
         //System.out.println(players[0] + "\n" +players[1] + "\n" +players[2] + "\n" +players[3]);
         // Each player plays a card
         currentTrick.set(startPlayer, players[startPlayer].getPlay(state));
         trickInPlay = true;
         currentSuit = currentTrick.get(startPlayer).getSuit();
         state.setCurrentTrick(currentTrick);
         currentTrick.set(players[(startPlayer+1)%4].getPosition(), players[(startPlayer+1)%4].getPlay(state));
         state.setCurrentTrick(currentTrick);
         currentTrick.set(players[(startPlayer+2)%4].getPosition(), players[(startPlayer+2)%4].getPlay(state));
         state.setCurrentTrick(currentTrick);
         currentTrick.set(players[(startPlayer+3)%4].getPosition(), players[(startPlayer+3)%4].getPlay(state));
         state.setCurrentTrick(currentTrick);

         // If a heart is in play that is not the first card
         if (!heartsBroken) {
            for (Card z : currentTrick) {
               if (z.getSuit() == 0) {
                  heartsBroken = true;
                  if (reporting != 0) System.out.println("HEARTS BROKEN");
               }
            }
         }
         
         // Calculate who wins the trick
         int oldStartPlayer = startPlayer;
         startPlayer = GetWinningCardIndex(currentTrick, oldStartPlayer);

         if (reporting != 0) System.out.println("Winner = " + startPlayer);

         // Record results of the trick
         // Calculate how many hearts were won
         int scoreAddition = 0;
         for (int x = 0; x < 4; x++) {
            if (currentTrick.get(x).getSuit() == 0) scoreAddition++;
            if (currentTrick.get(x).getSuit() == 1 && currentTrick.get(x).getRank() == 10) scoreAddition += 13; // Queen of Spades
         }
         if (reporting != 0) System.out.println("adding a score of " + scoreAddition);

         scores[startPlayer] += scoreAddition;
         trickRecord[i] = startPlayer;

         if (reporting != 0) System.out.println(currentTrick + "\n");
         currentTrick.clear();

         
      }

      // Creates a new State representing the game state as it is after the tricks were played out
      State currentState = new State(scores, trickRecord);

      return currentState;
   }

   public String toString() {
      for (Player i : players) {
         System.out.println(i);
      }
      return "";
   }
}
