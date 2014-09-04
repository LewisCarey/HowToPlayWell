
/* File: StatsApp.java - March 2014 */
//package raw_sim;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/**
 * This is the app class for the hearts project. It contains the main method
 * and calls upon the other classes to create a game state and then begins
 * playing the game.
 *
 * @author Lewis Carey
 */
public class StatsApp {
   

   /**
    * The main function, sets up the player space and begins the play
    * loop.
    *
    */
   public static void main(String[]args) {

      // Options array - 0 = Select coefficient
      int[] options = new int[3];
      options[0] = 20;
      options[2] = 100000;
      

      // MAIN GAME CODE HERE

      int numberOfGames = 1500;
      ArrayList<String> printResults = new ArrayList<String>();
      for (int i = 0; i < 10; i++) {
        printResults.add("");
      }
      
      
      for (int x = 0; x < numberOfGames; x++) {
        ArrayList<Card> cardsPlayed = randomRemove(0);
        State testState = new State(false, -1, cardsPlayed);
        // Generates the hands that are shared for each round
        ArrayList<ArrayList> hands = Controller.GenerateHands(testState);

        ArrayList<Player> playerType = new ArrayList<Player>();
        playerType.add(new MCTSPlayer(options));
        playerType.add(new RandomPlayer());
        playerType.add(new RandomPlayer());
        playerType.add(new RandomPlayer());
        
        
        

        Controller control = new Controller(testState, playerType, hands);
      
        
        State results = control.playGames(1, 0, 13);
        
        int[] scores = results.getScores();
        for (int i = 0; i < 4; i++) {
          System.out.print(scores[i] + ";");
        }
        System.out.println();
        System.err.println("END OF GAME " + x);
        
      }
      
     }

   // Returns a random assortment of cards played
  public static ArrayList<Card> randomRemove (int tricks) {
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

    // Return a portion of this randomised deck
    ArrayList<Card> temp = new ArrayList<Card>();
    for (int i = 0; i < 4*tricks; i++) {
      temp.add(deck.get(i));
    }
    return temp;
  }
}
