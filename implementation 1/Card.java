/* File: Card.java - March 2014 */
//package raw_sim;

import java.util.*;

/**
 * This is the Card class which represents a single card. This class has
 * methods to determine suit and rank of a card.
 *
 * Basics taken from http://www.dreamincode.net/forums/topic/110380-deck-of-cards-using-various-methods/
 *
 * @author Lewis Carey
 */
public class Card {

   private int rank, suit;

   private static String[] suits = {"hearts", "spades", "diamonds", "clubs"};
   private static String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king", "ace"};

   Card (int suit, int rank) {
      if (suit >= 4 || rank >= 13) {
         System.out.println("Incorrect card spec");
         return;
      }
      this.suit = suit;
      this.rank = rank;
   }

   public String toString() {
      return ranks[rank] + " of " + suits[suit];
   }

   public int getRank() {
      return rank;
   }

   public int getSuit() {
      return suit;
   }

   public String getSuitString() {
      return suits[suit];
   }

   public boolean match (Card candidate) {
      if (this.suit == candidate.getSuit() && this.rank == candidate.getRank()) return true;
      return false;
   }
   
}
