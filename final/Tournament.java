
/* File: Tournament.java - August 2014 */
//package raw_sim;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

/**
 * This is the tournament between different Hearts players.
 * 
 * Each player will play against three random opponents. The deal is shared between
 * all tournaments each game. The average number of Hearts will be recorded and the 
 * results analysed.
 *
 * @author Lewis Carey
 */
public class Tournament {
   

   /**
    * The main function, sets up the player space and begins the play
    * loop.
    *
    */
   public static void main(String[]args) {
      try {

        // Specific files to store results
        File file_random = new File("output_random.txt");
        File file_basic = new File("output_basic.txt");
        File file_advanced = new File("output_advanced.txt");
        File file_mcts_one = new File("output_mcts_one.txt");
        File file_mcts_two = new File("output_mcts_two.txt");

        BufferedWriter writer_random = new BufferedWriter(new FileWriter(file_random));
        BufferedWriter writer_basic = new BufferedWriter(new FileWriter(file_basic));
        BufferedWriter writer_advanced = new BufferedWriter(new FileWriter(file_advanced));
        BufferedWriter writer_mcts_one = new BufferedWriter(new FileWriter(file_mcts_one));
        BufferedWriter writer_mcts_two = new BufferedWriter(new FileWriter(file_mcts_two));

        // Hold the buffered writers
        ArrayList<BufferedWriter> writers = new ArrayList<BufferedWriter>();
        writers.add(writer_random);
        writers.add(writer_basic);
        writers.add(writer_advanced);
        writers.add(writer_mcts_one);
        writers.add(writer_mcts_two);

        int[] options = new int[3];
        options[0] = 20; // thresehold for mcts select
        options[1] = 10; // Expand nodes
        options[2] = 150000; // Timer

        int[] options_one = new int[3];
        options_one[0] = 20; // Thresehold
        options_one[2] = 300000; // Timer
        
        // Holds the teams
        ArrayList<ArrayList<Player>> teams = new ArrayList<ArrayList<Player>>();

        // Make the players
        ArrayList<Player> team_random = new ArrayList<Player>();
        team_random.add(new RandomPlayer());
        team_random.add(new RandomPlayer());
        team_random.add(new RandomPlayer());
        team_random.add(new RandomPlayer());

        ArrayList<Player> team_basic = new ArrayList<Player>();
        team_basic.add(new BasicPlayer());
        team_basic.add(new RandomPlayer());
        team_basic.add(new RandomPlayer());
        team_basic.add(new RandomPlayer());

        ArrayList<Player> team_advanced = new ArrayList<Player>();
        team_advanced.add(new AdvancedPlayer());
        team_advanced.add(new RandomPlayer());
        team_advanced.add(new RandomPlayer());
        team_advanced.add(new RandomPlayer());

        ArrayList<Player> team_mcts_one = new ArrayList<Player>();
        team_mcts_one.add(new MCTSPlayer_one(options_one));
        team_mcts_one.add(new RandomPlayer());
        team_mcts_one.add(new RandomPlayer());
        team_mcts_one.add(new RandomPlayer());

        ArrayList<Player> team_mcts_two = new ArrayList<Player>();
        team_mcts_two.add(new MCTSPlayer_two(options));
        team_mcts_two.add(new RandomPlayer());
        team_mcts_two.add(new RandomPlayer());
        team_mcts_two.add(new RandomPlayer());

        // Enter teams into the holder
        teams.add(team_random);
        teams.add(team_basic);
        teams.add(team_advanced);
        teams.add(team_mcts_one);
        teams.add(team_mcts_two);

        // Play out the games, generating a new deal / state each time
        int countOfGames = 0;
        // Set up the options
        while (true) {

          State state = new State(true, -1, new ArrayList<Card>());
          ArrayList<ArrayList<Card>> deal = Controller.GenerateHands(state, null);

          // Repeat the playouts for each configuration of the teams
          for (int y = 0; y < 3; y++) {
            // ENABLE SWAPPING OF HANDS
            // Hold the hand
            ArrayList<Card> tempHand = deal.get(0);
            // Remove player from teams array
            deal.remove(0);
            // Add it on to the end again
            deal.add(tempHand);

            // Testing the playout
            for (int i = 0; i < teams.size(); i++) {

              state.resetCardsPlayed();
              Controller control = new Controller(state, teams.get(i), deal);

              State results = control.playGames(1, 0, 13);
              
              int[] scores = results.getScores();

              System.err.println("END OF GAME " + countOfGames + " for player " + i + " round " + y);
              for (int x = 0; x < 4; x++) {

                writers.get(i).write(scores[x] + ";");

              }
              writers.get(i).newLine();
              writers.get(i).flush();

            }

          }

          countOfGames++;
        }

        

      } catch (IOException e) {

      }
      

    }

    /*
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
  */
}
