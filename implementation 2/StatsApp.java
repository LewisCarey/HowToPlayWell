
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
      int[] options = new int[1];
      
      

      
     
      // Set up the State and the Deal specific to the MCTS
      // State - Hearts broken, start player, cards played (none)
      State state = new State(true, -1, new ArrayList<Card>());
      // Specific Deal:
      ArrayList<ArrayList<Card>> deal = Controller.GenerateHands(state, null);
    
      // Deep copying here, do we need to?
      Card play = new Card(deal.get(0).get(0).getSuit(), deal.get(0).get(0).getRank());
      ArrayList<Card> trick = new ArrayList<Card>();
      trick.add(new Card(deal.get(0).get(0).getSuit(), deal.get(0).get(0).getRank()));
      trick.add(null);
      trick.add(null);
      trick.add(new Card(deal.get(3).get(0).getSuit(), deal.get(3).get(0).getRank()));

      
      Card play2 = new Card(deal.get(0).get(1).getSuit(), deal.get(0).get(1).getRank());
      ArrayList<Card> trick2 = new ArrayList<Card>();
      trick2.add(new Card(deal.get(0).get(1).getSuit(), deal.get(0).get(2).getRank()));
      trick2.add(new Card(deal.get(1).get(1).getSuit(), deal.get(1).get(2).getRank()));
      trick2.add(new Card(deal.get(2).get(1).getSuit(), deal.get(2).get(2).getRank()));
      trick2.add(new Card(deal.get(3).get(1).getSuit(), deal.get(3).get(2).getRank()));

      
      System.out.println("Trick:");
      for (Card i : trick) {
        System.out.println(i);
      }
      /*
      System.out.println("Trick2:");
      for (Card i : trick2) {
        System.out.println(i);
      }
      */

      MCTSNode node = new MCTSNode(null, null, null, 0);
      //MCTSNode node = new MCTSNode(play, null, trick, 0);
      //MCTSNode node2 = new MCTSNode(play2, node, trick2, node.getWinner());

      // TESTS THE EXPAND METHOD FOR ROOT NODES
      ArrayList<MCTSNode> children = MCTS.Expand(node, state, deal, 10);
      for (MCTSNode i : children) {
        System.out.println(i.getTrick());
      }

      /*
      // TESTS THE EXPAND METHOD FOR CHILD NODES
      ArrayList<MCTSNode> children = MCTS.Expand(node2, state, deal, 10);
      for (MCTSNode i : children) {
        System.out.println(i.getTrick());
      }
      */
      /*
      // TESTS THE SIMULATE PLAY METHOD
      System.out.println(MCTS.SimulatePlay(node, state, deal));
      */
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
