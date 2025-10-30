// 图22.7: BinarySearchTest.java
// 使用二叉查找在数组中查找数据项（输出中的*标记了中间元素）
import java.util.Arrays;
import java.util.Scanner;
import java.util.random.RandomGenerator; 

public class BinarySearchTest {
   // 在数据上执行二叉查找      
   public static int binarySearch(int[] data, int key) {
      int low = 0; // 查找区域的低端                
      int high = data.length - 1; // 查找区域的高端
      int middle = (low + high + 1) / 2; // 中间元素      
      int location = -1; // 返回值；-1表示未找到 

      do { // 循环查找元素
         // 打印数组中剩余的元素
         System.out.print(remainingElements(data, low, high));

         // 输出空格用于对齐
         for (int i = 0; i < middle; ++i) {
            System.out.print("   ");
         }
         System.out.println(" * "); // 指示当前中间位置

         // 如果在中间位置找到元素                    
         if (key == data[middle]) {                                  
            location = middle; // 位置就是当前中间位置     
         }
         else if (key < data[middle]) { // 中间元素太大
            high = middle - 1; // 排除较高的一半          
         }
         else { // 中间元素太小                         
            low = middle + 1; // 排除较低的一半            
         }

         middle = (low + high + 1) / 2; // 重新计算中间位置
      } while ((low <= high) && (location == -1));               

      return location; // 返回查找键的位置
   }                         

   // 输出数组中特定值的方法
   private static String remainingElements(
      int[] data, int low, int high) {
      StringBuilder temporary = new StringBuilder();

      // 添加空格用于对齐
      for (int i = 0; i < low; ++i) {
         temporary.append("   ");
      }                         

      // 添加数组中剩余的元素 
      for (int i = low; i <= high; ++i) {
         temporary.append(data[i] + " ");
      }                         

      return String.format("%s%n", temporary);
   } 

   public static void main(String[] args) {
      var input = new Scanner(System.in);
      var generator = RandomGenerator.getDefault();

      // 创建包含15个随机整数的有序数组
      int[] data = generator.ints(15, 10, 91).sorted().toArray();
      System.out.printf("%s%n%n", Arrays.toString(data)); // 显示数组

      // 从用户获取输入
      System.out.print("请输入一个整数值 (-1退出): ");
      int searchInt = input.nextInt(); 

      // 重复输入整数；-1 终止程序
      while (searchInt != -1) {
         // 执行查找
         int location = binarySearch(data, searchInt);

         if (location == -1) { // 未找到
            System.out.printf("%d未找到%n%n", searchInt); 
         }                          
         else { // 找到
            System.out.printf("%d在位置%d找到%n%n", 
               searchInt, location);
         }                         
 
         // 从用户获取输入
         System.out.print("请输入一个整数值 (-1 退出): ");
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

