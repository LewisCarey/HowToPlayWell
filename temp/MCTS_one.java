/* File: MCTS.java - April 2014 */
//package raw_sim;

import java.util.*;

/* ASSUMPTIONS:
 * - We do not know the hands which have been played by which
 * player, therefore possible plays by any opponent is any
 * remaining card not in our hand that still has not been played.
 *
 */

/**
 * This is the class which performs a MCTS on a given State.
 * A node consists of a given State and a card to be played.
 * This search space is then examined via MCTS to find a recommended
 * play. The general process of this algorithm is as follows:
 *
 * 1) Selection of node to search
 * 2) Expansion of the selected node
 * 3) Simulation of game play (Double Dummy)
 * 4) Backpropagation through the network
 *
 * Note: this algorithm will return a card which is the recommended
 * course of action a player takes for a likely chance at winning the
 * game (which may not necessarily be the trick).
 *
 * @author Lewis Carey
 */

public class MCTS {

   private static boolean verbose = false;
   private static boolean visualise = false;

   // Given a State and a Player, search the state space and return the
   // best card available to be played.
   public static Card search (State state, ArrayList<Card> hand, int[] options) {


      int explorationDepth = options[2];

      // Decrease the time spent exploring relative to the trick we are in
      int trickNumber = 13 - hand.size();
      explorationDepth = explorationDepth - (10000 * trickNumber);

      //System.out.println(hand);
      //if (state.getCurrentTrick() == null) return null;

      // Tracking depth
      //ArrayList<Integer> explorationDepthCount = new ArrayList<Integer>();
      
      // THE MCTS ALGORITHM
      MCTSNode currentNode, rootNode, prevNode = null;

      // Interprets the state and sets the initial variables
      rootNode = new MCTSNode(null, null);

      // CONSTRUCTS AND SEARCHES THE TREE
      // while (has time)
      int timer = 0;
      int maxDepth = 0;
      while (timer < explorationDepth) {
         int depth = 0;
         
         // current node <-- root node
         currentNode = rootNode;
         // Assign prevNode to currentNode, used in the case of examining the root
         prevNode = currentNode;

         /* ------------------------------------------------------------------------------------------------------
         // Choosing when to stop selecting
         // Can do this way: aka when we reach a leaf node...
         ------------------------------------------------------------------------------------------------------ */
         /*
         // while (current node is within the State aka has children)
         while (currentNode.getNumberChildren() != 0) {
            // prev node <-- current node
            prevNode = currentNode;
            // current node <-- Select(current_node)
            currentNode = currentNode.getChildren().get(Select(currentNode, options[0]));
            depth++;
         }
         
         System.out.println("Exploring at depth: " + depth);

         // prev node <-- expand(prev node)
         currentNode.assignChildren(Expand(currentNode, state, hand));
         */
         /* ------------------------------------------------------------------------------------------------------
         // Choosing when to stop selecting
         // Can re-explore non-leaf nodes. The higher the depth, the further down we wish to explore
         ------------------------------------------------------------------------------------------------------ */
         
         // First case - root must be expanded
         // Stops either based on a probability, or when there are no more children
         // while (current node is within the State aka has children)
         Random rand = new Random();
         while (timer != 0 && currentNode.getNumberChildren() != 0) {
            // prev node <-- current node
            prevNode = currentNode;
            // current node <-- Select(current_node)
            currentNode = currentNode.getChildren().get(Select(currentNode, options[0]));
            depth++;
            //System.out.println("Exploring");
            // Break if node has been explored less than the thresehold
            // This means every node explored at least X times, and simulated not just continuing down the branch
            if (currentNode.getVisitCount() < options[0]) break;

            // Probability of stopping
            double breakCase = rand.nextDouble();
            //System.out.println(breakCase + " " + (double)depth/(double)maxDepth);
            if (breakCase <= (double)depth/(double)maxDepth) break;
         }
         //System.out.println("broke");
         
         //System.out.println("Expanding at depth: " + depth);
         // Only expands if we have not already expanded, otherwise it just plays out a game
         // prev node <-- expand(prev node)
         if (currentNode.getNumberChildren() == 0) {
            currentNode.assignChildren(Expand(currentNode, state, hand));
            // If we were at max depth, we are increasing it
            //System.out.println("Expanding!" + depth + " " + maxDepth);
            if (depth == maxDepth) maxDepth++;

         }

         //System.out.println("Exploring at depth: " + depth + " with Max Depth: " + maxDepth);
         // Tracking depth
         /*
         if (explorationDepthCount.size() < maxDepth) {
            explorationDepthCount.add(1);
         } else {
            explorationDepthCount.set(depth, explorationDepthCount.get(depth) + 1);
         }

   */



         
         // Must take into account the current trick from the state!
         if (verbose) System.out.println("Exploring a node with count: " + timer + " \n\n");
         if (verbose) System.out.println("Current node represents : " + currentNode.getPlay());
         int doesWin = SimulatePlay(currentNode, state, hand);
         
         // while (current node is within the State aka is not root node)
         while (currentNode != null) {
            // current node.backprop(R)
            currentNode.setScore(doesWin);
            // current node.visit count ++
            currentNode.setVisit(1);
            // current node <-- current node.parent
            currentNode = currentNode.getParent();
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
      //System.out.println("CHILD NODES OF ROOT");
      for (MCTSNode i : rootNode.getChildren()) {
         //System.out.println(i.getPlay());
         if (i.getAverageScore() < max.getAverageScore()) {
            max = i;
         }
      }
      //System.out.println();
      // Max is now the node with the best score. Lets check if any are the same, and if so, change if visit count higher
      for (MCTSNode i : rootNode.getChildren()) {
         if (i.getAverageScore() == max.getAverageScore()) {
            if (i.getVisitCount() > max.getVisitCount()) {
               max = i;
            }
         }
      }
      //System.out.println("The best move is :  " + max.getPlay());

      // Tracking depth
      //System.out.println(explorationDepthCount);
      
      return max.getPlay();

   }

   /* Select method to account for scoring based on avergae hearts collected per visit.
      The idea here is that the score associated with a node is how many hearts it has collected in total.
      If we divide this by number of visits we get average hearts collected.
      We increase the chance of picking a node with low average hearts, and decrease it otherwise.
   */
   public static int Select(MCTSNode node, int option) {

      return Select.UCT(node, option);

      /*
      // Old select method 
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

   // Expands the node, creating child nodes based on the cards able to be played.
   // Legal hands to be played are based on the State in the node.
   // This method will be modular - with different expansion methods being substituted and recorded.
   public static ArrayList<MCTSNode> Expand(MCTSNode node, State state, ArrayList<Card> hand) {
      // Copies the array to avoid side effects
      ArrayList<Card> currentHand = new ArrayList<Card>();
      for (Card i : hand) {
         currentHand.add(new Card(i.getSuit(), i.getRank()));
      }


      // Removes the cards that have been played already
      MCTSNode parent = node;
      //System.out.println(node);
      while (parent != null && parent.getPlay() != null) {
         // Determine if the card is within the hand
         for (int i = 0; i < currentHand.size(); i++) {
            //System.out.println(hand.get(i));
            // If cards match
            if (currentHand.get(i).getRank() == parent.getPlay().getRank() && currentHand.get(i).getSuit() == parent.getPlay().getSuit()) {
               currentHand.remove(i);
            }
         }
         parent = parent.getParent();
      }

      // Gets the set of legal plays we can use
      //ArrayList<Card> legalPlays = state.getLegalPlays(currentHand, -1);
      // If we are the root node, take into account legal plays
      ArrayList<Card> legalPlays;
      if (node.getParent() == null) {
         legalPlays = state.getLegalPlays(currentHand, -1);
      } else {
         legalPlays = currentHand;
      }

      //System.out.println("State current trick: " + state.getCurrentTrick());
      //System.out.println("Legal Plays: " + legalPlays);
      
      //System.out.println(legalPlays);

      //System.out.println("HAND PLAYS SIZE " + hand.size());
      // Create nodes based on legal plays
      ArrayList<MCTSNode> tempChildren = new ArrayList<MCTSNode>();
      for (Card i : legalPlays) {
         tempChildren.add(new MCTSNode(i, node));
      }

      return tempChildren;
   }

   // Plays through the game world, beginning with current node selected.
   // This method will be modular - with different play methods being substituted and recorded.

   // Method 0: we choose a selection of random hands for the other players, and just play through
   // face up.
   public static int SimulatePlay(MCTSNode node, State state, ArrayList<Card> hand) {
      
      // Assign random hands to the players
      ArrayList<Card> deck = state.getRemainingCards(hand);
      //System.out.println("THIS IS THE DECK SIZE : " + deck.size());
      //System.out.println("THIS IS THE HAND : " + hand);

      ArrayList<Card> playerHand = new ArrayList<Card>();
      for (Card i : hand) {
         playerHand.add(new Card(i.getSuit(), i.getRank()));
      }
      ArrayList<ArrayList<Card>> opponentsHands = new ArrayList<ArrayList<Card>>();

      // Remove the cards we have already played from our hands by recursively exploring the tree
      ArrayList<Card> cardsPlayedByTree = new ArrayList<Card>();
      while (node.getParent() != null) {
         cardsPlayedByTree.add(node.getPlay());
         node = node.getParent();
      }
      
      for (Card i : cardsPlayedByTree) {
         for (int z = 0; z < playerHand.size(); z++) {
            if (i.match(hand.get(z))) playerHand.remove(z);
         }
      }
      
      // Shuffle the deck
      // For each index in the deck starting at the last and decrementing
      Random rand = new Random();
      for(int i = deck.size() - 1; i > 0; i--) {
         // Pick a card from the remaining deck
         int randomNum = rand.nextInt(i + 1);
         // Swap the card at the end for the random number card
         Card tempCard = deck.get(i);
         deck.set(i, deck.get(randomNum));
         deck.set(randomNum, tempCard);
      }
      //System.out.println("THIS IS A SHUFFLED DECK: " + deck);
      opponentsHands.add(new ArrayList<Card>());
      opponentsHands.add(new ArrayList<Card>());
      opponentsHands.add(new ArrayList<Card>());

      /*
      // Assign the hands to the players
      for (int i = 0; i < playerHand.size(); i++) {
         opponentsHands.get(0).add(deck.get(i));
         opponentsHands.get(1).add(deck.get(hand.size() + i));
         opponentsHands.get(2).add(deck.get((hand.size() * 2) + i));
      }
      */
      // DEEP COPY
      //System.out.println("THIS IS THE DECK SIZE : " + deck.size());

      // If we are at the last node, we have to play this card. So we may as well see what tends to happen if we play this hand
      if (playerHand.size() == 0) playerHand.add(new Card(hand.get(0).getSuit(), hand.get(0).getRank()));

      for (int i = 0; i < playerHand.size(); i++) {
         opponentsHands.get(0).add(new Card(deck.get(i).getSuit(), deck.get(i).getRank()));
         opponentsHands.get(1).add(new Card(deck.get(hand.size() + i).getSuit(), deck.get(hand.size() + i).getRank()));
         opponentsHands.get(2).add(new Card(deck.get((hand.size() * 2) + i).getSuit(), deck.get((hand.size() * 2) + i).getRank()));
      }
     // System.out.println("THIS IS A HAND: " + opponentsHands.get(0));

      //System.out.println("THIS IS A HAND: " + opponentsHands.get(0));
      //System.exit(0);

      // Trying to add mid trick functionality
      // Remove currently played cards in the trick
      // Needs fixing - need to remove from the correct hands
      /*
      if (state.getCurrentTrick() != null) {
         for (int i = 0; i < state.getCurrentTrick().size(); i++) {
            for (int z = 0; z < 3; z++) {
               for (int x = 0; x < opponentsHands.get(z).size(); x++) {
                  if (opponentsHands.get(z).get(x).match(state.getCurrentTrick().get(i))) opponentsHands.get(z).remove(x);
               }
            }
         }
      }
      */
      
      
      //System.out.println(hand + "\n\n");

      // Play out the round double dummy, assuming all opponents want to win
      /*
      System.out.println(hand + "\n\n");
      System.out.println(opponentsHands.get(0) + "\n\n");
      System.out.println(opponentsHands.get(1) + "\n\n");
      System.out.println(opponentsHands.get(2) + "\n\n");
      */
      ArrayList<Card> copyOfHand = new ArrayList<Card>();
      for (Card i : playerHand) {
         copyOfHand.add(new Card(i.getSuit(), i.getRank()));
      }
      //int win = DoubleDummy.PlayOut(copyOfHand, opponentsHands);
      
      int win;
      //System.out.println("CURRENT TRICK SIZE = " + state.getCurrentTrick().size());
      
      //if (state.getCurrentTrick() != null && state.getCurrentTrick().size() > 0) {
      // Avoid considering state when we are of depth greater than first children
      if (state.getCurrentTrick() != null && state.getCurrentTrick().size() > 0 && (node.getParent() != null && node.getParent().getPlay() == null)) {
         win = DoubleDummy.PlayOut(copyOfHand, opponentsHands, state);
      } else {
         win = DoubleDummy.PlayOut(copyOfHand, opponentsHands);
      }
      
      
      
      return win;
   }

}










