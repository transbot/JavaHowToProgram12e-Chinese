// 图B.5: FieldWidthTest.java
// 在字段中右对齐整数

public class FieldWidthTest {
   public static void main(String[] args) {
      System.out.printf("%4d%n", 1);
      System.out.printf("%4d%n", 12);
      System.out.printf("%4d%n", 123);
      System.out.printf("%4d%n", 1234);
      System.out.printf("%4d%n%n", 12345); // 太大

      System.out.printf("%4d%n", -1);
      System.out.printf("%4d%n", -12);
      System.out.printf("%4d%n", -123);
      System.out.printf("%4d%n", -1234);  // 太大
      System.out.printf("%4d%n", -12345); // 太大
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
