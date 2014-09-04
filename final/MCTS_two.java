/* File: MCTS_two.java - August 2014 */
//package raw_sim;

import java.util.*;

/**
 * Second implementation of MCTS. This version is intended to have less time
 * for each tree, since multiple trees will be made.
 *
 * Each node contains trick info (as opposed to just a play), and therefore
 * some of the component methods change accordingly.
 *
 * @author Lewis Carey
 */

public class MCTS_two {

   private static boolean verbose = false;
   private static boolean visualise = false;

   // Given a State and a Player, search the state space and return the
   // best card available to be played.
   /**
    * OPTIONS:
    *    [0] Select method thresehold criteria
    *    [1] Max nodes expanded
    *    [2] Timer variable
    */
   public static Card search (State state, ArrayList<Card> hand, int[] options) {      
      // THE MCTS ALGORITHM
      MCTSNode currentNode, rootNode, prevNode = null;

      // CREATES THE BASE DEAL - REMOVING ALL CARDS ALREADY PLAYED AT PLAY CALL
      
      // Creates the deal based on remaining cards - NOTE: should only do this for opponents,
      // and our hand will remain the same.
      ArrayList<ArrayList<Card>> deal;
      if (state.getCurrentTrick() != null && state.getCurrentTrick().size() != 0) {
         deal = Controller.GenerateHandsTrick(state, hand);
      } else {
         deal = Controller.GenerateHands(state, hand);
      }

      /*
      for (ArrayList<Card> player : deal) {
         System.out.println("Deal Player Size: " + player.size());
      }
      */
      

      // Check to see if there is a trick in play
      ArrayList<Card> currentTrick = Misc.DeepCopyTrick(state.getCurrentTrick());
      //System.out.println(currentTrick);
      if (currentTrick != null && currentTrick.size() != 0) {
         // Calculate start player based on the trick - it will be earliest card played
         int rootNodeStartPlayer = 0;
         for (int i = 0; i < 4; i++) {
            if (currentTrick.get(i) != null) {
               rootNodeStartPlayer = i;
               break;
            }
         }
         // Creates a half full root node
         rootNode = new MCTSNode_two(null, null, currentTrick, rootNodeStartPlayer);
      } else { 
         // Creates an empty root node, ready for expansion
         // If this is the case, we must be starting the trick, so we are the start player
         rootNode = new MCTSNode_two(null, null, null, 0);
      }

      // CONSTRUCTS AND SEARCHES THE TREE
      // while (has time)
      int timer = 0;
      while (timer < options[2]) {
         int depth = 0;
         // current node <-- root node
         currentNode = rootNode;
         // Assign prevNode to currentNode, used in the case of examining the root
         prevNode = currentNode;
         // while (current node is within the State aka has children)
         while (currentNode.getNumberChildren() != 0) {
            // prev node <-- current node
            prevNode = currentNode;
            // current node <-- Select(current_node)
            currentNode = currentNode.getChildren().get(Select(currentNode, options[0]));
            depth++;
         }
         
         if (verbose) System.out.println("Expanding at depth: " + depth);
         if (verbose) System.out.println("Cards played: " + state.getCardsPlayed().size());

         // prev node <-- expand(prev node)
         // Assign children will have to append to the children array, not replace it
         
         currentNode.assignChildren(Expand(currentNode, state, deal, options[1]));
         

         /*
         System.out.println("PREV " + prevNode);
         System.out.println("CURR " + currentNode);
         System.out.println("Children = " + Expand(currentNode, state, hand));
         */
         // R <-- play_simulated_game(prev node)
         
         // Must take into account the current trick from the state!
         if (verbose) System.out.println("\n\nExploring a node with count: " + timer + " \n\n");
         if (verbose) System.out.println("Current node represents : " + currentNode.getPlay());
         if (verbose) {
            for (ArrayList<Card> i : deal) {
               System.out.println(i);
            }
         }
         
         

         // Avoid simulating play / backprop at root node
         if (timer != 0) {

            int doesWin = SimulatePlay(currentNode, state, deal);
            
            // BACK PROPAGATE METHOD
            // while (current node is within the State aka is not root node)
            while (currentNode != null) {
               // current node.backprop(R)
               currentNode.setScore(doesWin);
               // current node.visit count ++
               currentNode.setVisit(1);
               // current node <-- current node.parent
               currentNode = currentNode.getParent();
            }

         }
         
         
         timer++;
      }
      
      /*
      // Iterate through the tree (only first layer at the moment)
      for (MCTSNode i : rootNode.getChildren()) {
         System.out.println(i);
      }
      
      System.out.println(rootNode.getChildren().size());
      System.out.println(rootNode);*/

      // Return the best move specified in tree
      // To do this, simply look at the LOWEST score on the first depth
      MCTSNode max = rootNode.getChildren().get(0);
      for (MCTSNode i : rootNode.getChildren()) {
         if (i.getAverageScore() < max.getAverageScore()) {
            max = i;
         }
      }
      // Max is now the node with the best score. Lets check if any are the same, and if so, change if visit count higher
      for (MCTSNode i : rootNode.getChildren()) {
         if (i.getAverageScore() == max.getAverageScore()) {
            if (i.getVisitCount() > max.getVisitCount()) {
               max = i;
            }
         }
      }
      
      return max.getPlay();

   }

   public static void Iterate (ArrayList<MCTSNode> childNodes, int depth) {

      for (MCTSNode i : childNodes) {
         System.out.println(i + "    Parent: " + i.getParent() +  "     at depth: " + depth);
         if (i.getChildren() != null) {
            MCTS_two.Iterate(i.getChildren(), depth + 1);
         }
      }
      System.out.println("------------------------------------------------------------\n");

   }

   /* Select method to account for scoring based on avergae hearts collected per visit.
      The idea here is that the score associated with a node is how many hearts it has collected in total.
      If we divide this by number of visits we get average hearts collected.
      We increase the chance of picking a node with low average hearts, and decrease it otherwise.
   */
   public static int Select(MCTSNode node, int option) {

      return Select.UCT(node, option);

      /* Old select method 
      int numberOfChildren = node.getNumberChildren();
      Random rand = new Random();
      ArrayList<Integer> indexSelection = new ArrayList<Integer>();

      // Adds nodes based on equation 13 - (average score / 2)
      // If still 13, add no extra nodes
      for (int i = 0; i < numberOfChildren; i++) {
         // Adds at least 1 representation of node to indexSelection array
         indexSelection.add(i);

         MCTSNode temp = node.getChildren().get(i);

         // If node has been visited.
         if (temp.getVisitCount() > 0) {
            // Calculate average score / 2
            int averageHeartScore = temp.getScore() / temp.getVisitCount();
            int representation = 13 - temp.getScore();

            for (int z = 0; z < representation; z++) {
               indexSelection.add(i);
            }
         }
       
      }

      // Selects a node at random from the array
      int successor = indexSelection.get(rand.nextInt(indexSelection.size()));

      return successor;
      */
   }

   // Expands the node, creating child nodes based on following tricks.
   // Returns a list of child nodes
   public static ArrayList<MCTSNode> Expand(MCTSNode node, State state, ArrayList<ArrayList<Card>> deal, int numChildren) {
      ArrayList<MCTSNode> candidateChildren = new ArrayList<MCTSNode>();
     
      // Root node case - TRICK IS IN PLAY
      if (node.getParent() == null && node.getTrick() != null) {
         // We are in the root node and have to deal with a current trick
         // All tricks must begin with what is currently in the trick already - assume this information in the node
         // Assumes trick will have null entries if no card has been played
         ArrayList<Card> trick = new ArrayList<Card>();
         
         // No cards to remove from deal (due to root node) but we still must deep copy
         ArrayList<ArrayList<Card>> revisedDeal = Misc.DeepCopy(deal);
         // Remove cards already played in the trick from the specific players -- DONT NEED TO DO THIS, BUT ACTS AS A CHECK!
         for (int i = 0; i < 4; i++) {
            // If we find a card in the trick
            if (node.getTrick().get(i) != null) {
               // Remove from hand that played it
               int index = Misc.RemoveIndex(node.getTrick().get(i), revisedDeal.get(i));
               revisedDeal.get(i).remove(index);
            }
         }

         // Make new tricks - WE CAN ONLY PLAY WHAT IS LEGAL, AND SO CAN THE OPPONENT
         Random rand = new Random();
         // Find the trump suit of the trick
         int trumpSuit = -1;
         for (int i = 0; i < node.getTrick().size(); i++) {
            if (node.getTrick().get(i) != null) {
               trumpSuit = node.getTrick().get(i).getSuit();
               break;
            }
         }

         for (int i = 0; i < numChildren; i++) {
            ArrayList<Card> newTrick = Misc.DeepCopyTrick(node.getTrick());
            for (int x = 0; x < 4; x++) {
               if (newTrick.get(x) == null) {
                  ArrayList<Card> legalPlays = Misc.GetLegalPlays(revisedDeal.get(x), trumpSuit);
                  int selected = rand.nextInt(legalPlays.size());
                  newTrick.set(x, new Card(legalPlays.get(selected).getSuit(), legalPlays.get(selected).getRank()));


                  //int selected = rand.nextInt(revisedDeal.get(x).size());
                  //newTrick.set(x, new Card(revisedDeal.get(x).get(selected).getSuit(), revisedDeal.get(x).get(selected).getRank()));
               }
            }
            // Creates the node with the startPlayer and parent based on the parent node
            MCTSNode temp = new MCTSNode_two(newTrick.get(0), node, newTrick, node.getStartPlayer());
            // Add to candidate children
            candidateChildren.add(temp);
         }
      }  
      // EASY CASE - NO TRICK IN PLAY
      else {
         // Remove already played cards from deal
         //System.out.println("TRACE: " + node.getTrick());
         //System.out.println(deal.get(0));
         //System.out.println(deal.get(1));
         ArrayList<ArrayList<Card>> revisedDeal = Misc.RemovePlayedCards(deal, node);
         // If we have no plays left to expand, return empty
         if (revisedDeal.get(0).size() == 0) return new ArrayList<MCTSNode>();
         //System.out.println(revisedDeal.get(0));
         // Expanding possible nodes based on all the remaining tricks available
         // Will expand until numChildren reached
         for (int i = 0; i < numChildren; i++) {
            // Generate a trick
            ArrayList<Card> trick = new ArrayList<Card>();
            Random rand = new Random();
            for (ArrayList<Card> player : revisedDeal) {
               int selected = rand.nextInt(player.size());
               trick.add(new Card(player.get(selected).getSuit(), player.get(selected).getRank()));
            }
            // Creates the node with the startPlayer and parent based on the parent node
            MCTSNode temp = new MCTSNode_two(trick.get(0), node, trick, node.getWinner());
            // Add to candidate children
            candidateChildren.add(temp);

         }

      }

      return candidateChildren;
   }

   /* Plays through the game world, beginning with current node selected.
    * 
    * Basic procedure is as follows...
    *    -> Calculate winner of the trick passed.
    *    -> Set up the game state
    *          -> Recursively remove cards already played
    *          -> Set up remaining hands / cards
    *    -> Pass this info to DoubleDummy play
    *    -> Return winner
    *
    * We assume players are set up like... { Player, O1, O2, O3}
    *
    */
   public static int SimulatePlay(MCTSNode node, State state, ArrayList<ArrayList<Card>> deal) {
      // Deep copy the deal
      ArrayList<ArrayList<Card>> hands = Misc.DeepCopy(deal);
      
      // Calculate winner of the trick passed
      int winner = node.getWinner();
      int[] scores = {0, 0, 0, 0};

      /*
      for (ArrayList<Card> player : deal) {
         System.out.println("Deal Player Size: " + player.size());
      }
      */

      // Set up the game state - remove all cards that have already been played
      MCTSNode currentNode = node;
      //while (currentNode != null && (currentNode.getTrick() != null && currentNode.getTrick().size() != 0)) {
      while (currentNode.getParent() != null) {
         // Remove a card from each hand per trick
         for (int i = 0; i < hands.size(); i++) {
            for (int x = 0; x < hands.get(i).size(); x++) {
               if (hands.get(i).get(x).match(currentNode.getTrick().get(i))) {
                  if (hands.get(i).get(x).getSuit() == 0) {
                     scores[currentNode.getWinner()] += 1;
                  }
                  if (hands.get(i).get(x).match(new Card(1, 10))) {
                     scores[currentNode.getWinner()] += 13;
                  }
                  hands.get(i).remove(x);
                  break;
               }
            }
         }

         currentNode = currentNode.getParent();
      }

      /*
      for (ArrayList<Card> player : hands) {
         System.out.println("Hands Player Size Post Removal: " + player.size());
      }
      */

      // We now have a current game state, current scores, and the winner of the last trick
      // Time to pass all this information to the doubledummy player and play it out
      return DoubleDummy.PlayOut(hands, scores, winner);


      /*
      for (ArrayList<Card> i : hands) {
         System.out.println(i);
      }
      for (int i : scores) {
         System.out.println(i);
      }
      */
   }
}










