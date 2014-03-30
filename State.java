/* File: State.java - March 2014 */

import java.util.*;

/**
 * This is the State class for the hearts game simulator used in 2014
 * COSC490 project.
 * This class records an instance / snapshot of a game state. It can be
 * given to the Controller class as a starting point for playing out
 * hearts. It mainly records environment variables detailing the state of
 * play - for example, what cards have been played, who has what points,
 * and so on.
 *
 * @author Lewis Carey
 */

public class State {

   private boolean heartsBroken;
   private int startPlayer;
   
   public State (boolean heartsBroken, int startPlayer){
      this.heartsBroken = heartsBroken;
      this.startPlayer = startPlayer;
   }

   public boolean isHeartsBroken () {
      return heartsBroken;
   }

   public int getStartPlayer () {
      return startPlayer;
   }

}
