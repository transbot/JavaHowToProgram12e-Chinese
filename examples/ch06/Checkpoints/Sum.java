// 6.18节，自测题3: Sum.java
// 求整数命令行参数之和
public class Sum {
   public static void main(String[] args) {
      // 判断是否提供了命令行参数
      if (args.length == 0) {
         System.out.println(
            "请提供一些整数命令行参数，以空格分隔");
         return;
      }

      int sum = 0;

      // 计算命令行参数之和
      for (String arg : args) {
         sum += Integer.parseInt(arg);
      }

      System.out.printf("总和为: %d%n", sum); // 显示总和
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
