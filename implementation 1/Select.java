// Storing the different select methods

import java.util.*;
import java.lang.Math;

public class Select {

   // UCT Implementation
   // Returns the index in the nodes children array that points to the selected node
   public static int UCT (MCTSNode node, int option) {
      // Check to see if all the children have been explored - if not, randomly select from the children
      // that have not been explored.
      int visitThresehold = 20;
      ArrayList<MCTSNode> children = node.getChildren();
      for (MCTSNode i : children) {
         if (i.getVisitCount() <= visitThresehold) return RandomSelect(node, visitThresehold);
      }

      // For each child of the current node, see which one performs the best in the UCT equation
      ArrayList<Double> nodeUCTScores = new ArrayList<Double>();
      // Coefficient - may need adjusting
      double c = option;
      for (int i = 0; i < children.size(); i++) {
         MCTSNode k = children.get(i);
         double averageHearts = k.getScore() / k.getVisitCount();
         int kVisitCount = k.getVisitCount();
         if (kVisitCount == 0) kVisitCount++;

         // The actual UCT equation
         double uctScore = averageHearts + ( c * Math.sqrt(Math.log(node.getVisitCount())/kVisitCount));


         nodeUCTScores.add(uctScore);
      }

      // Find the node that performed the best in the UCT implementation
      int min = 0;
      for (int i = 0; i < nodeUCTScores.size(); i++) {
         if (nodeUCTScores.get(i) < nodeUCTScores.get(min)) min = i;
      }

      return min;
   }

   // Randomly selects from the unexplored children
   private static int RandomSelect (MCTSNode node, int visitThresehold) {
      ArrayList<Integer> indexSelection = new ArrayList<Integer>();
      for (int i = 0; i < node.getChildren().size(); i++) {
         if (node.getChildren().get(i).getVisitCount() <= visitThresehold) {
            indexSelection.add(i);
         }
      }

      // Randomly select from the indexSelection array
      Random rand = new Random();
      return rand.nextInt(indexSelection.size());
   }

}