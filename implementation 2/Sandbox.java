public class Sandbox {

  public static void main (String[]args) {
    int count = 0;

    long startTime = System.nanoTime();
      
    long endTime = System.nanoTime();
    // While there is still time remaining, keep searching
    while (((endTime - startTime)/1000000000) < 10) { // Is this seconds?
        if (count % 100000 == 0) {
          System.out.println((endTime - startTime) / 1000000000);
        }
       endTime = System.nanoTime();
    }

  }

}