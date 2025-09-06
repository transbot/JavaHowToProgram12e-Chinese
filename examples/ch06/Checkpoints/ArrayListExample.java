// 6.20节，自测题3：ArrayListExample.java
// 包含整数元素的ArrayList
import java.util.ArrayList;

public class ArrayListExample {
   public static void main(String[] args) {
      var list = new ArrayList<Integer>();

      // 向ArrayList添加整数1~5
      for (int i = 1; i <= 5; ++i) {
         list.add(i); // 自动装箱int
      }

      System.out.printf("list中包含: %s%n", list); 

      // 计算列表元素总和
      int total = 0;

      for (int value : list) { // 自动拆箱Integer对象
         total += value;
      }

      System.out.printf("list中所有元素之和为: %d%n", total);
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
