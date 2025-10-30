// 图22.6: LinearSearchTest.java
// 对数组进行顺序查找
import java.util.Arrays;
import java.util.Scanner;
import java.util.random.RandomGenerator; 

public class LinearSearchTest {
   // 在数据上执行线性查找                   
   public static int linearSearch(int data[], int searchKey) {
      // 顺序遍历数组
      for (int index = 0; index < data.length; ++index) {     
         if (data[index] == searchKey) {                      
            return index; // 返回整数的索引
         }                                                    
      }                                                       
   
      return -1; // 未找到整数
   }

   public static void main(String[] args) {
      var input = new Scanner(System.in);
      var generator = RandomGenerator.getDefault(); 

      int[] data = generator.ints(10, 10, 91).toArray();  // 创建数组
      System.out.printf("%s%n%n", Arrays.toString(data)); // 显示数组

      // 从用户获取输入
      System.out.print("请输入一个整数值 (-1退出): ");
      int searchInt = input.nextInt(); 

      // 重复输入整数；-1 终止程序
      while (searchInt != -1) {
         int position = linearSearch(data, searchInt);    // 执行查找

         if (position == -1) { // 未找到
            System.out.printf("%d未找到%n%n", searchInt); 
         }
         else { // 找到
            System.out.printf("%d在位置%d找到%n%n", 
               searchInt, position);
         }

         // 从用户获取输入
         System.out.print("请输入一个整数值 (-1退出): ");
         searchInt = input.nextInt();
      } 
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

