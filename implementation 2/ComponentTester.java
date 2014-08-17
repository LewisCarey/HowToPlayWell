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

      int tricks = 4;

      ArrayList<Card> hand = new ArrayList<Card>();

      ArrayList<Card> pool = GameApp.randomRemove(tricks);

      for(int i = 0; i < tricks; i++) {
        hand.add(new Card(pool.get(i).getSuit(), pool.get(i).getRank()));
      }
      

      

      ArrayList<ArrayList<Card>> opponentsHands = new ArrayList<ArrayList<Card>>();
      opponentsHands.add(new ArrayList<Card>());
      opponentsHands.add(new ArrayList<Card>());
      opponentsHands.add(new ArrayList<Card>());
      
      for (int i = 0; i < 3; i++) {
        for (int z = 0; z < tricks; z++) {
          opponentsHands.get(i).add(new Card(pool.get(tricks + (i * tricks) + z).getSuit(), pool.get(tricks + (i * tricks) + z).getRank()));
        }
      }

      State testState = new State(false, -1, null);
      ArrayList<Card> trick = new ArrayList<Card>();
      trick.add(new Card(2, 0));
      testState.setCurrentTrick(trick);

      System.out.println(DoubleDummy.PlayOut(hand, opponentsHands, testState));
      
   }
}
