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
   public static Card search (State state, ArrayList<Card> hand) {

      
      
      // THE MCTS ALGORITHM
      MCTSNode currentNode, rootNode, prevNode = null;

      // Interprets the state and sets the initial variables
      rootNode = new MCTSNode(null, null);

      // CONSTRUCTS AND SEARCHES THE TREE
      // while (has time)
      int timer = 0;
      while (timer < 100) {
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
            currentNode = currentNode.getChildren().get(Select(currentNode));
            depth++;
         }
         
         //System.out.println("Expanding at depth: " + depth);

         // prev node <-- expand(prev node)
         currentNode.assignChildren(Expand(currentNode, state, hand));
         /*
         System.out.println("PREV " + prevNode);
         System.out.println("CURR " + currentNode);
         System.out.println("Children = " + Expand(currentNode, state, hand));
         */
         // R <-- play_simulated_game(prev node)
         
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
      // To do this, simply look at the highest score on the first depth
      MCTSNode max = rootNode.getChildren().get(0);
      for (MCTSNode i : rootNode.getChildren()) {
         if (i.getScore() > max.getScore()) {
            max = i;
         }
      }
      //System.out.println("The best move is :  " + max.getPlay());

      // Find midway point for root node
      ArrayList<MCTSNode> children = rootNode.getChildren();
      // Find the total length
      int totalLength = 0;
      for (MCTSNode i : children) {
         totalLength += i.getLength();
      }
      // Find the midway point
      int tempLength = 0;
      int leftNodeLength = 0;
      for (MCTSNode i : children) {
         tempLength += i.getLength();
         if (tempLength > totalLength / 2) break;
      }

      
      // VISUALISE METHOD
      if (visualise) {
         //System.out.println("Total Length = " + CalculateLengths(rootNode));

         String treeStructure = "{";
         treeStructure += CalculatePositions(rootNode, tempLength, -1, 1);
         treeStructure += "}";
         GameApp.Visualise(treeStructure, rootNode.getLength());
      }
     
      
      
      // Iterates down the tree
      //MCTS.Iterate(rootNode.getChildren(), 0);
      
      return max.getPlay();

   }

   // A depth first search through the tree printing out the length of the subtree
   public static int CalculateLengths (MCTSNode childNode) {

      // If no more children, we are at a leaf node, return 1
      if (childNode.children == null || childNode.getChildren().size() == 0) {
         childNode.setLength(1);
         //System.out.println("1");
         return 1;
      }

      int length = 0;
      for (MCTSNode i : childNode.getChildren()) {
         length += CalculateLengths(i) + 1;
      }
      childNode.setLength(length);
      //System.out.println(length);
      return length;

   }

   // A depth first search through the tree printing out the length of the subtree
   // Each node returns a string that is a representation of the node on the graph
   public static String CalculatePositions (MCTSNode childNode, int leftBarrier, int parentPos, int depth) {
      String nodeString = "";
      if (parentPos == -1) {
         // If root node
         // Add labels to the return string
         nodeString += "ROOT,";
         nodeString += childNode.getScore() + ",";
      } else {
         // Add labels to the return string
         nodeString += childNode.getPlay().getSuitString().charAt(0);
         nodeString += childNode.getPlay().getRank() + ",";
         nodeString += childNode.getScore() + ",";
      }
      
      // Place the node you are currently on
      // Do you have child nodes?
      if (childNode.children != null) {
         // Yes?
            // Split into two groups, left and right
            ArrayList<MCTSNode> children = childNode.getChildren();
            /* FIX THIS MORE ADVANCE SPLITTING BASED ON LENGTH
            // Find the total length
            int totalLength = 0;
            for (MCTSNode i : children) {
               totalLength += i.getLength();
            }
            // Find the midway point
            int tempLength = 0;
            int leftNodeLength = 0;
            for (MCTSNode i : children) {
               tempLength += i.getLength();
               if (tempLength > totalLength / 2) break;
            }
            // Place yourself at sum(left node lengths) + leftBarrier + 1
            int myPos = tempLength + leftBarrier + 1;
            nodeString += myPos + ",";
            // Add width and parent info
            nodeString += depth + ",";
            nodeString += parentPos + ";";

            int prevLengths = 0;
            boolean left = true;
            for (MCTSNode i : children) {
               // Call Calculate on all left nodes, sending leftBarrier + sum(prevChildNode.length)
               if (left = true) {
                  nodeString += CalculatePositions(i, leftBarrier + prevLengths, myPos, depth + 1);
                  prevLengths += i.getLength();
                  if (prevLengths >= tempLength) {
                     left = false;
                     prevLengths = 0;
                  }
               }
               // Call calculate on all right nodes, sending your position as leftBarrier + prevChildNode.length
               else {
                  nodeString += CalculatePositions(i, myPos + prevLengths, myPos, depth + 1);
                  prevLengths += i.getLength();
               }
            }
            */
            // SPLITTING BASED ON NUMBER OF NODES
            // Find the total length
            int midway = children.size() / 2;
            
            // Find the midway point (length)
            int tempLength = 0;
            int count = 0;
            for (MCTSNode i : children) {
               tempLength += i.getLength();
               if (count > midway) break;
               count++;
            }
            // Place yourself at sum(left node lengths) + leftBarrier + 1
            int myPos = tempLength + leftBarrier + 1;
            nodeString += myPos + ",";
            // Add width and parent info
            nodeString += depth + ",";
            nodeString += parentPos + ";";

            count = 0;
            boolean resetFlag = true;
            int prevLengths = 0;
            for (MCTSNode i : children) {
               // Call Calculate on all left nodes, sending leftBarrier + sum(prevChildNode.length)
               if (count <= midway) {
                  nodeString += CalculatePositions(i, leftBarrier + prevLengths, myPos, depth + 1);
                  prevLengths += i.getLength();
               }
               // Call calculate on all right nodes, sending your position as leftBarrier + prevChildNode.length
               else {
                  if (resetFlag) {
                     prevLengths = 0;
                     resetFlag = false;
                  }
                  nodeString += CalculatePositions(i, myPos + prevLengths, myPos, depth + 1);
                  prevLengths += i.getLength();
               }
               count++;
            }

            
      } else {
         // No?
            // Place yourself at 1 + leftBarrier
         nodeString += (1 + leftBarrier) + ",";
         // Add width and parent info
         nodeString += depth + ",";
         nodeString += parentPos + ";"; 
      }
      System.out.println(nodeString);
      return nodeString;
   }

   public static void Iterate (ArrayList<MCTSNode> childNodes, int depth) {

      for (MCTSNode i : childNodes) {
         System.out.println(i + "    Parent: " + i.getParent() +  "     at depth: " + depth);
         if (i.children != null) {
            MCTS.Iterate(i.getChildren(), depth + 1);
         }
      }
      System.out.println("------------------------------------------------------------\n");

   }

   /* Select method to account for scoring based on avergae hearts collected per visit.
      The idea here is that the score associated with a node is how many hearts it has collected in total.
      If we divide this by number of visits we get average hearts collected.
      We increase the chance of picking a node with low average hearts, and decrease it otherwise.
   */
   public static int Select(MCTSNode node) {
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
      ArrayList<Card> legalPlays = state.getLegalPlays(currentHand, -1);
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
   // System.out.println("THIS IS THE PLAYERS HAND : " + playerHand);

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
      
      if (state.getCurrentTrick() != null && state.getCurrentTrick().size() > 0) {
         win = DoubleDummy.PlayOut(copyOfHand, opponentsHands, state);
      } else {
         win = DoubleDummy.PlayOut(copyOfHand, opponentsHands);
      }
      
      
      
      return win;
   }

}










