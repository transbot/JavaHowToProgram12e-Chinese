// 3.7节，自测题2: Calculate.java
// 计算整数1～10之和
public class Calculate {
   public static void main(String[] args) {
      int sum = 0;
      int x = 1;

      while (x <= 10) { // 当x小于或等于10时
         sum = sum + x; // 将x加到sum上
         x = x + 1;     // x递增1
      } 

      System.out.printf("1～10之和是: %d%n", sum);
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
