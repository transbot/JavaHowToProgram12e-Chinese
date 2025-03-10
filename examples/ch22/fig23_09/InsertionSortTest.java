// Fig. 22.9: InsertionSortTest.java
// Sorting an array with insertion sort.
import java.util.Arrays;
import java.util.random.RandomGenerator; 

public class InsertionSortTest {
   // sort array using insertion sort
   public static void insertionSort(int[] data) {
      // loop over data.length - 1 elements           
      for (int next = 1; next < data.length; ++next) {
         int insert = data[next]; // value to insert      
         int moveIndex = next; // location to place element

         // search for place to put current element         
         while (moveIndex > 0 && data[moveIndex - 1] > insert) {
            // shift element right one slot                   
            data[moveIndex] = data[moveIndex - 1];              
            moveIndex--;                                       
         }

         data[moveIndex] = insert; // place inserted element    
         printPass(data, next, moveIndex); // output pass of algorithm
      }                                             
   }

   // print a pass of the algorithm
   public static void printPass(int[] data, int pass, int index) {
      System.out.printf("after pass %2d: ", pass);

      // output elements till swapped item
      for (int i = 0; i < index; ++i) {
         System.out.printf("%d  ", data[i]);
      }                                             

      System.out.printf("%d* ", data[index]); // indicate swap

      // finish outputting array
      for (int i = index + 1; i < data.length; ++i) {
         System.out.printf("%d  ", data[i]);
      }                                             

      System.out.printf("%n               "); // for alignment

      // indicate amount of array that's sorted
      for (int i = 0; i <= pass; ++i) {
         System.out.print("--  ");
      }                                             
      System.out.println();
   } 

   public static void main(String[] args) {
      var generator = RandomGenerator.getDefault();

      // create unordered array of 10 random ints
      int[] data = generator.ints(10, 10, 91).toArray(); 

      System.out.printf("Unsorted array: %s%n%n", Arrays.toString(data));
      insertionSort(data); // sort array
      System.out.printf("%nSorted array: %s%n", Arrays.toString(data));
   } 
} 


/**************************************************************************
 * (C) Copyright 1992-2025 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/

