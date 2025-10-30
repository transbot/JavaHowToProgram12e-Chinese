// 图22.9: InsertionSortTest.java
// 使用插入排序对数组进行排序
import java.util.Arrays;
import java.util.random.RandomGenerator; 

public class InsertionSortTest {
   // 使用插入排序算法对数组进行排序
   public static void insertionSort(int[] data) {
      // 循环遍历data.length - 1个元素           
      for (int next = 1; next < data.length; ++next) {
         int insert = data[next]; // 要插入的值      
         int moveIndex = next;    // 放置元素的位置

         // 查找放置当前元素的位置         
         while (moveIndex > 0 && data[moveIndex - 1] > insert) {
            // 将元素右移一位                   
            data[moveIndex] = data[moveIndex - 1];              
            moveIndex--;                                       
         }

         data[moveIndex] = insert;         // 放置插入的元素    
         printPass(data, next, moveIndex); // 输出算法的一轮
      }                                             
   }

   // 输出算法的一轮排序过程
   public static void printPass(int[] data, int pass, int index) {
      System.out.printf("第%d轮排序后: ", pass);

      // 输出交换元素前的数组元素
      for (int i = 0; i < index; ++i) {
         System.out.printf("%d  ", data[i]);
      }                                             

      System.out.printf("%d* ", data[index]); // 标记交换位置

      // 输出剩余数组元素
      for (int i = index + 1; i < data.length; ++i) {
         System.out.printf("%d  ", data[i]);
      }                                             

      System.out.printf("%n             "); // 用于对齐

      // 标识已排序的数组部分
      for (int i = 0; i <= pass; ++i) {
         System.out.print("--  ");
      }                                             
      System.out.println();
   } 

   public static void main(String[] args) {
      var generator = RandomGenerator.getDefault();

      // 创建包含10个随机整数的无序数组
      int[] data = generator.ints(10, 10, 91).toArray(); 

      System.out.printf("未排序数组: %s%n%n", Arrays.toString(data));
      insertionSort(data); // 对数组排序
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

