/* File: MCTSNode.java - April 2014 */
//package raw_sim;

import java.util.*;

/**
 * This is the node class used in the MCTS. It contains key information
 * that the algorithm uses to run, such as times visited and how many
 * successful plays have been recorded from its leaves.
 *
 * @author Lewis Carey
 */

public class MCTSNode_one implements MCTSNode  {

   private int amountVisited;
   private int score;
   private Card play;
   public ArrayList<MCTSNode> children;
   private MCTSNode parent;
   private State state;
   private int length; // Records the length of the subtree

   public MCTSNode_one (Card play, MCTSNode parent) {
      amountVisited = 0;
      score = 0;
      this.play = play;
      this.parent = parent;
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

   // Needs to add to children, not overwrite
   public void assignChildren (ArrayList<MCTSNode> children) {
      if (this.children == null || this.children.size() == 0) {
         this.children = children;
      }
      else {
         for (MCTSNode i : children) {
            this.children.add(i);
         }
      }
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

   public double getAverageScore() {
      return (double) score / (double) amountVisited;
   }

   // Not used for the first implementation
   public int getWinner() {
      return -1;
   }

   // Not used for the first implementation
   public int getStartPlayer() {
      return -1;
   }

   public ArrayList<Card> getTrick () {
      return null;
   }

}