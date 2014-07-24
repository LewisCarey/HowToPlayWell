import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TreePanel extends JPanel {
   private JLabel label;
   private JLabel label2;

   private int windowX = 10000;
   private int windowY = 10000;

   private int nodeCircleWidth = 50;
   private int nodeCircleHeight = 50;

   private int totalNodes;

   private String tree;

   // Constructor to set up GUI
   public TreePanel (String tree, int totalNodes){
      //label = new JLabel("Test");

      //add(label);
      this.totalNodes = totalNodes;
      this.tree = tree;
      setPreferredSize(new Dimension(windowX, windowY));

   }

   public void paintComponent (Graphics page) {
      super.paintComponent(page);

      /*
      {0,A,0,5,7,-1;}
      {1,B,0,1,5,0;2,C,0,0,1,0;}
      {3,D,0,0,1,1;4,E,0,1,3,1;}
      {5,F,0,0,1,4;6,G,0,0,1,4;}
      */
      
      // Pseudo code for algorithm while processing tree string
      // Divide the space into X length (X = root node width)
      float containerLength = (windowX -50) / totalNodes;
      int depth = 1;
      char c;
      int i = 0;
      // While there is still a depth to explore
      while (i < tree.length()) {
         c = tree.charAt(i);
         // Begin a new depth
         if (c == '{') {

            // While there are nodes to process in the depth
            while (c == '{' || c == ';') {
               i++;
               // If last node, break
               if (tree.charAt(i) == '}') break;
               
               // Make an array of values, splitting at the ;
               int endOfNode = 0;
               while (tree.charAt(i + endOfNode) != ';') {
                  endOfNode++;
               }

               // End of node now points to the semi colon, make a substring of node
               //System.out.println("END OF NODE : " + i);
               String node = tree.substring(i, i + endOfNode);
               String[] nodeSplit = node.split(",");

               // We now have an array representation for the values of the node
               //System.out.println("XPos: " + Integer.parseInt(nodeSplit[3]) *  (int) containerLength);
               drawNode(page, Integer.parseInt(nodeSplit[2]) *  (int) containerLength,  Integer.parseInt(nodeSplit[3]) * 100, nodeSplit[0], nodeSplit[1], Integer.parseInt(nodeSplit[4]) *  (int) containerLength, (Integer.parseInt(nodeSplit[3])-1) * 100);
               i += endOfNode;
               c = tree.charAt(i);
               
            }

         }
         depth++;
         i ++;
      }
      
      // for each node we need to draw (breadth first)
         // Split child nodes in half
         // Place node at Y (where Y = width of rightmost child nodes + 1)




      //drawNode(page, windowX/2, 25, "KH", "25", -1, -1);
      //drawNode(page, windowX/2/2 - nodeCircleWidth/2, 100, "AS", "25", windowX/2, nodeCircleHeight/2);
   }

   // Draws the node, attaches appropriate labels, and draws a line back to the parent node
   private void drawNode (Graphics page, int x, int y, String play, String score, int parentX, int parentY) {
      // Draw the node
      page.fillOval(x - nodeCircleWidth/2, y - nodeCircleHeight/2, nodeCircleWidth, nodeCircleHeight);

      // Draw the labels
      JLabel playLabel = new JLabel(play);
      playLabel.setLocation((x - nodeCircleWidth/2) + 15 , (y - nodeCircleHeight/2) + 5);
      playLabel.setForeground(Color.WHITE);
      playLabel.setSize(25,25);
      add(playLabel);

      JLabel scoreLabel = new JLabel(score);
      scoreLabel.setLocation((x - nodeCircleWidth/2) + 10 , (y - nodeCircleHeight/2) + 20);
      scoreLabel.setForeground(Color.WHITE);
      scoreLabel.setSize(40,25);
      add(scoreLabel);

      // Draw the line
      if (parentX > -1) page.drawLine(x, y, parentX, parentY);
   }

   public int getWidth() {
      return windowX;
   }

   public int getHeight() {
      return windowY;
   }
}