/* File: Misc.java - March 2014 */
//package raw_sim;

import java.util.*;


public class Misc {

    private static boolean verbose = false;

    // Based on a hand and a trump suit
    public static ArrayList<Card> GetLegalPlays (ArrayList<Card> hand, int trumpSuit) {
        ArrayList<Card> legal = new ArrayList<Card>();
        for (Card i : hand) {
            if (i.getSuit() == trumpSuit) {
                legal.add(new Card(i.getSuit(), i.getRank()));
            }
        }

        if (legal.size() == 0) return hand;
        return legal;
    }

   // Returns how many non null elements are in an arraylist
    public static int RealSize (ArrayList<Card> list) {
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
           if (list.get(i) != null) count++;
        }
        return count;
    }

    // Returns a deep copy of the array passed
    public static ArrayList<Card> DeepCopyTrick (ArrayList<Card> trick) {
        // if (trick == null) return null;
        
        ArrayList<Card> copy = new ArrayList<Card>();

        for (Card i : trick) {
            if (i == null) {
                copy.add(null);
            } else {
                copy.add(new Card(i.getSuit(), i.getRank()));
            }
            
        }

        return copy;

    }

    // Returns a deep copy of the array passed
    public static ArrayList<ArrayList<Card>> DeepCopy (ArrayList<ArrayList<Card>> deck) {

    	ArrayList<ArrayList<Card>> copy = new ArrayList<ArrayList<Card>>();

    	int count = 0;
    	for (ArrayList<Card> i : deck) {
    		copy.add(new ArrayList<Card>());
    		for (Card x : i) {
    			copy.get(count).add(new Card(x.getSuit(), x.getRank()));
    		}
    		count++;
    	}

    	return copy;

    }

    // If the card is within the hand, it returns the array where it is.
    // Otherwise returns -1.
    public static int RemoveIndex (Card cardToRemove, ArrayList<Card> hand) {
    	int count = 0;
    	for (Card i : hand) {
    		if (i.match(cardToRemove)) return count;
    		count++;
    	}

    	return -1;
    }

    public static ArrayList<ArrayList<Card>> RemovePlayedCards (ArrayList<ArrayList<Card>> deal, MCTSNode node) {
        // Deep copy the deal
        ArrayList<ArrayList<Card>> hands = Misc.DeepCopy(deal);
        //System.out.println("HANDS " + hands.size());


        // WE NEVER REMOVE FROM ROOT NODE!
        //if (node.getParent() == null && (node.getTrick() == null || node.getTrick().size() == 0)) return hands;
        if (node.getParent() == null) return hands;
        // If the node contains null values, it is the root node and therefore will not be removed,
        // since the trick is repeated in the immeidate leaves

        // Set up the game state - remove all cards that have already been played
        MCTSNode currentNode = node;
        int count = 0;
        //while (currentNode != null && (currentNode.getTrick() != null && currentNode.getTrick().size() != 0)) {
        while (currentNode.getParent() != null) {
            if (verbose) System.out.println("Count: " + count + "Node: " + currentNode.getTrick());
            count++;
           // Remove a card from each hand per trick
           for (int i = 0; i < hands.size(); i++) {
                //System.out.println("Hand: " + hands.get(i));
              for (int x = 0; x < hands.get(i).size(); x++) {
                    //System.out.println("TRICK " + currentNode.getTrick() + " i VALUE: " + i);
                    //System.out.println("Comparing: " + hands.get(i).get(x) + " with " + currentNode.getTrick().get(i));
                 if (hands.get(i).get(x).match(currentNode.getTrick().get(i))) {
                    
                    hands.get(i).remove(x);
                    break;
                 }

              }

           }

           currentNode = currentNode.getParent();
           // CHECK TO SEE IF WE HAVE REACHED A ROOT NODE CASE WITH NO / HALF TRICK
            if (node.getParent() == null) return hands;
        }

        return hands;
    }

}
