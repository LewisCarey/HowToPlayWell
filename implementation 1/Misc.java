/* File: Misc.java - March 2014 */
//package raw_sim;

import java.util.*;


public class Misc {

   // Returns how many non null elements are in an arraylist
   public static int RealSize (ArrayList<Card> list) {
      int count = 0;
      for (int i = 0; i < list.size(); i++) {
         if (list.get(i) != null) count++;
      }
      return count;
   }

}
