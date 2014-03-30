/* File: GameApp.java - March 2014 */
//package raw_sim;

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
      Controller control = new Controller(null);

      control.play(13);
   }
}