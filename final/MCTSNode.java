/* File: MCTSNode_two.java - August 2014 */
//package raw_sim;

import java.util.*;


public interface MCTSNode {

   public int getScore ();

   public int getVisitCount ();

   public Card getPlay ();

   public int getNumberChildren ();

   public MCTSNode getParent();

   public ArrayList<MCTSNode> getChildren();

   public void setScore (int amount);

   public void setVisit (int amount);

   // NEEDS TO ADD TO THE CHILDREN NOT REPLACE
   public void assignChildren (ArrayList<MCTSNode> children);

   public String toString ();

   public int getLength ();

   public void setLength (int length);

   public ArrayList<Card> getTrick ();

   public int getStartPlayer();

   public int getWinner ();

   public double getAverageScore();

}