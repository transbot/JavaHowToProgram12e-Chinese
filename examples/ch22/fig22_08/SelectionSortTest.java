// 图22.8: SelectionSortTest.java
// 使用选择排序对数组进行排序
import java.util.Arrays;
import java.util.random.RandomGenerator; 

public class SelectionSortTest {
   // 使用选择排序对数组进行排序
   public static void selectionSort(int[] data) {
      // 遍历data.length - 1个元素
      for (int i = 0; i < data.length - 1; ++i) {
         int smallest = i; // 剩余数组的第一个索引

         // 循环查找最小元素的索引
         for (int index = i + 1; index < data.length; index++) {
            if (data[index] < data[smallest]) {
               smallest = index;                                        
             }                                                                 
         }                               
                                                                        
         swap(data, i, smallest); // 将最小元素交换到位置
         printPass(data, i + 1, smallest); // 输出算法的一轮
      }                                                                 
   } 

   // 交换两个元素值的辅助方法
   private static void swap(int[] data, int first, int second) {
      int temporary = data[first]; // 将第一个值存储到临时变量
      data[first] = data[second];  // 用第二个值替换第一个值
      data[second] = temporary;    // 将临时值放入第二个位置
   } 

   // 打印算法的一趟
   private static void printPass(int[] data, int pass, int index) {
      System.out.printf("经过第%d轮后: ", pass);

      // 输出到选定项之前的元素
      for (int i = 0; i < index; ++i) {
         System.out.printf("%d  ", data[i]);
      }

      System.out.printf("%d* ", data[index]); // 指示交换

      // 完成输出数组的剩余部分
      for (int i = index + 1; i < data.length; i++) {
         System.out.printf("%d  ", data[i]);
      }

      System.out.printf("%n             "); // 用于对齐

      // 指示已排序的数组部分
      for (int j = 0; j < pass; ++j) {
         System.out.print("--  ");
      }
      System.out.println(); 
   }

   public static void main(String[] args) {
      var generator = RandomGenerator.getDefault();

      // 创建包含10个随机整数的无序数组
      int[] data = generator.ints(10, 10, 91).toArray(); 

      System.out.printf("未排序数组: %s%n%n", Arrays.toString(data));
      selectionSort(data); // 排序数组
      System.out.printf("%n已排序数组: %s%n", Arrays.toString(data));
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

