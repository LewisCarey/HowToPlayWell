/* File: GameApp.java - March 2014 */
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
public class GameApp {
   

   /**
    * The main function, sets up the player space and begins the play
    * loop.
    *
    */
   public static void main(String[]args) {
      

      ArrayList<Card> cardsPlayed = new ArrayList<Card>();
      
      for (int i = 0; i < 10; i++) {
        for (int s = 0; s < 4; s++) {
          cardsPlayed.add(new Card(s, i));
        }
      }



      ArrayList<Player> playerType = new ArrayList<Player>();
      playerType.add(new MCTSPlayer());
      playerType.add(new RandomPlayer());
      playerType.add(new RandomPlayer());
      playerType.add(new RandomPlayer());
      
      State testState = new State(false, -1, cardsPlayed);
      
      Controller control = new Controller(testState, playerType);
    
      
      State results = control.playGames(1000, 0, 3);
      //State results = control.play(3, 1);
      
      int[] scores = results.getScores();
      for (int i : scores) {
        System.out.println(i);
      }
      
      
      
     //System.out.println(MCTS.search(testState, control.players[0].getHand()));
      //Visualise("");

     // Testing visualisation method
     //Visualise("{c6,0,1,0,140;}");

      
      /*
      // Testing the Select method
      MCTSNode root = new MCTSNode(null, null);
      ArrayList<MCTSNode> children = new ArrayList<MCTSNode>();
      children.add(new MCTSNode(new Card(0, 0), root));
      children.add(new MCTSNode(new Card(0, 1), root));
      children.add(new MCTSNode(new Card(0, 2), root));
      children.add(new MCTSNode(new Card(0, 3), root));

      children.get(0).setScore(1);
      children.get(0).setVisit(1);

      children.get(1).setScore(1);
      children.get(1).setVisit(1);

      int[] scores = {0,0,0,0};

      root.assignChildren(children);
      for (int i = 0; i < 10000; i++) {
        scores[MCTS.Select(root).getPlay().getRank()] += 1;
      }

      for (int i = 0; i < 4; i++) {
        System.out.println(scores[i]);
      }
      */

      /*
      // Testing the Expand method
      ArrayList<Card> tempHand = new ArrayList<Card>();
      tempHand.add(new Card(0,0));
      tempHand.add(new Card(1,0));

      // Creates a MCTS tree with a root node and 4 possible plays - 1, 2, 3, 4 of Hearts
      MCTSNode root = new MCTSNode(null, null);

      ArrayList<MCTSNode> children = new ArrayList<MCTSNode>();
      children.add(new MCTSNode(new Card(0, 0), root));
      root.assignChildren(children);

      ArrayList<MCTSNode> children2 = new ArrayList<MCTSNode>();
      children2.add(new MCTSNode(new Card(1, 0), root.getChildren().get(0)));
      root.getChildren().get(0).assignChildren(children2);

      ArrayList<MCTSNode> expansion = MCTS.Expand(root.getChildren().get(0).getChildren().get(0), testState, tempHand);

      for (MCTSNode i : expansion) {
        System.out.println(i);
      }
      */

      /*
      // Testing the SimulatePlay method
      System.out.println(control.players[0].getHand() + "\n\n");

      MCTSNode root = new MCTSNode(null, null);

      ArrayList<MCTSNode> children = new ArrayList<MCTSNode>();
      children.add(new MCTSNode(control.players[0].getHand().get(0), root));
      root.assignChildren(children);

      ArrayList<MCTSNode> children2 = new ArrayList<MCTSNode>();
      children2.add(new MCTSNode(control.players[0].getHand().get(1), root.getChildren().get(0)));
      root.getChildren().get(0).assignChildren(children2);

      System.out.println(MCTS.SimulatePlay(root.getChildren().get(0).getChildren().get(0), testState, control.players[0].getHand()));
      */
      
      
      /*
      int max = 0;
      // Testing the DoubleDummy method
      for (int i = 0; i < 5000; i++) {
        ArrayList<ArrayList> hands = Controller.GenerateHands(null);
        ArrayList<Card> player = hands.get(0);
        ArrayList<ArrayList<Card>> opponents = new ArrayList<ArrayList<Card>>();
        opponents.add(hands.get(1));
        opponents.add(hands.get(2));
        opponents.add(hands.get(3));


        int result = DoubleDummy.PlayOut(player, opponents);
        if (result > max) max = result;
      }

      System.out.println(max);
      */


      /*
      
      ArrayList<Player> playerType = new ArrayList<Player>();
      playerType.add(new RandomPlayer());
      playerType.add(new CowardPlayer());
      playerType.add(new BullyPlayer());
      playerType.add(new RandomPlayer());
      
      Controller control = new Controller(null, playerType);

      System.out.println(control);

      control.play(13);
      */
      
   }

   public static void Visualise (String treeStructure, int totalNodes) {
      JFrame frame = new JFrame ("Tree GUI");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      //TreePanel panel = new TreePanel("{A,0,6,7,-1;}{B,0,2,5,6;C,0,7,1,6;}{D,0,1,1,2;E,0,4,3,2;}{F,0,3,1,4;G,0,5,1,4;}");
      //{0,A,0,6,7,-1;}{1,B,0,2,5,6;2,C,0,7,1,6;}{3,D,0,1,1,2;4,E,0,4,3,2;}{5,F,0,3,1,4;6,G,0,5,1,4;}
      TreePanel panel = new TreePanel(treeStructure, totalNodes);
      panel.setLayout(null);
      

      JScrollPane scrollFrame = new JScrollPane(panel);
      scrollFrame.setPreferredSize(new Dimension(panel.getWidth(), panel.getHeight()));

      frame.getContentPane().add(scrollFrame);
      //frame.getContentPane().add(panel);

      //frame.setLayout(null);
      frame.pack();
      frame.setVisible(true);
   }
}
