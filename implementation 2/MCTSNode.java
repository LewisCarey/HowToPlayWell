/* File: MCTSNode.java - August 2014 */
//package raw_sim;

import java.util.*;

/**
 * This is the node class used in the MCTS implementation 2.
 *
 * The node not only records a play, but also records the trick.
 * The players are stored in order in the trick
 *       0) MCTS Player
 *       1) Opponent 1
 *       2) Opponent 2
 *       3) Opoonent 3
 *
 * @author Lewis Carey
 */

public class MCTSNode {

   private int amountVisited;
   private int score;
   private Card play;
   private ArrayList<Card> trick;
   public ArrayList<MCTSNode> children;
   private MCTSNode parent;
   private State state;
   private int startPlayer;
   private int length; // Records the length of the subtree

   public MCTSNode (Card play, MCTSNode parent, ArrayList<Card> trick, int startPlayer) {
      amountVisited = 0;
      score = 0;
      this.play = play;
      this.parent = parent;
      this.trick = trick;
      this.startPlayer = startPlayer;
   }

   public int getScore () {
      return score;
   }

   public int getVisitCount () {
      return amountVisited;
   }

   public Card getPlay () {
      return play;
   }

   public int getNumberChildren () {
      if (children == null) return 0;
      return children.size();
   }

   public MCTSNode getParent() {
      return parent;
   }

   public ArrayList<MCTSNode> getChildren() {
      return children;
   }

   public void setScore (int amount) {
      score += amount;
   }

   public void setVisit (int amount) {
      amountVisited += amount;
   }

   // NEEDS TO ADD TO THE CHILDREN NOT REPLACE
   public void assignChildren (ArrayList<MCTSNode> children) {
      this.children = children;
   }

   public String toString () {
      return play != null ? play.toString() + " " + this.getScore() : "No play node" + " " + this.getScore();
   }

   public int getLength () {
      return length;
   }

   public void setLength (int length) {
      this.length = length;
   }

   public ArrayList<Card> getTrick () {
      return trick;
   }

   public int getStartPlayer() {
      return startPlayer;
   }

   public int getWinner () {
      // If we are calling on the empty root node, we are the first in the trick and therefore winner is us (we are the next start player)
      if (trick == null) return 0;

      int trumpSuit = trick.get(startPlayer).getSuit();

      // Find the winning card
      int max = startPlayer;
      for (int i = 0; i < trick.size(); i++) {

         if (trick.get(i).getSuit() == trumpSuit) {
            if (trick.get(i).getRank() > trick.get(max).getRank()) max = i;
         }
      }

      return max;
   }

   public double getAverageScore() {
      return (double) score / (double) amountVisited;
   }

}