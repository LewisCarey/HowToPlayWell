/* File: ComponentTester.java - March 2014 */
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
public class ComponentTester {
   

   /**
    * The main function, sets up the player space and begins the play
    * loop.
    *
    */
   public static void main(String[]args) {

      ArrayList<Card> hand = new ArrayList<Card>();
      hand.add(new Card(0, 0));
      hand.add(new Card(0, 1));
      hand.add(new Card(0, 2));
      hand.add(new Card(0, 3));

      ArrayList<ArrayList<Card>> opponentsHands = new ArrayList<ArrayList<Card>>();
      opponentsHands.add(new ArrayList<Card>());
      opponentsHands.add(new ArrayList<Card>());
      opponentsHands.add(new ArrayList<Card>());
      opponentsHands.get(0).add(new Card(1, 0));
      opponentsHands.get(0).add(new Card(1, 1));
      opponentsHands.get(0).add(new Card(1, 2));
      opponentsHands.get(0).add(new Card(1, 3));
      opponentsHands.get(1).add(new Card(1, 5));
      opponentsHands.get(1).add(new Card(2, 1));
      opponentsHands.get(1).add(new Card(2, 2));
      opponentsHands.get(1).add(new Card(2, 3));
      opponentsHands.get(2).add(new Card(3, 0));
      opponentsHands.get(2).add(new Card(3, 1));
      opponentsHands.get(2).add(new Card(3, 2));
      opponentsHands.get(2).add(new Card(4, 3));

      State testState = new State(false, -1, null);

      System.out.println(DoubleDummy.PlayOut(hand, opponentsHands, testState));
      
   }
}
