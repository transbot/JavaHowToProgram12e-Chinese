// 图22.10: MergeSortTest.java
// 使用合并排序对数组进行排序
import java.util.Arrays;
import java.util.random.RandomGenerator; 

public class MergeSortTest {
   // 调用递归的sortArray方法开始合并排序
   public static void mergeSort(int[] data) {
      sortArray(data, 0, data.length - 1); // 对整个数组排序
   }                                  

   // 拆分数组，排序子数组并将子数组合并为有序数组
   private static void sortArray(int[] data, int low, int high) {
      // 测试基本情况：数组大小等于1     
      if ((high - low) >= 1) {             // 如果不是基本情况
         int middle1 = (low + high) / 2;   // 计算数组中间位置
         int middle2 = middle1 + 1;        // 计算下一个元素位置     

         // 输出拆分步骤
         System.out.printf("拆分:    %s%n", 
            subarrayString(data, low, high));
         System.out.printf("         %s%n", 
            subarrayString(data, low, middle1));
         System.out.printf("         %s%n%n",
            subarrayString(data, middle2, high));

         // 将数组拆分为两半；分别排序每一半（递归调用）
         sortArray(data, low, middle1);  // 数组的前半部分       
         sortArray(data, middle2, high); // 数组的后半部分     

         // 在拆分调用返回后合并两个已排序的数组
         merge (data, low, middle1, middle2, high);             
      }                                            
   }                               
   
   // 将两个已排序的子数组合并为一个有序子数组             
   private static void merge(int[] data, int left, int middle1, 
      int middle2, int right) {

      int leftIndex = left;     // 左子数组的索引              
      int rightIndex = middle2; // 右子数组的索引         
      int combinedIndex = left; // 临时工作数组的索引
      int[] combined = new int[data.length]; // 工作数组        
      
      // 在合并前输出两个子数组
      System.out.printf("合并:    %s%n", 
         subarrayString(data, left, middle1));
      System.out.printf("         %s%n", 
         subarrayString(data, middle2, right));

      // 合并数组直到到达任一数组的末尾         
      while (leftIndex <= middle1 && rightIndex <= right) {
         // 将两个当前元素中较小的放入结果中  
         // 并移动到数组中的下一个位置                   
         if (data[leftIndex] <= data[rightIndex]) {       
            combined[combinedIndex++] = data[leftIndex++]; 
         } 
         else {                                                 
            combined[combinedIndex++] = data[rightIndex++];
         } 
      } 
   
      // 如果左数组为空                                
      if (leftIndex == middle2) {                             
         // 复制右数组的剩余部分                        
         while (rightIndex <= right) {                        
            combined[combinedIndex++] = data[rightIndex++];
         } 
      } 
      else { // 右数组为空                             
         // 复制左数组的剩余部分                         
         while (leftIndex <= middle1) {                        
            combined[combinedIndex++] = data[leftIndex++]; 
         } 
      } 

      // 将值复制回原始数组
      for (int i = left; i <= right; ++i) { 
         data[i] = combined[i];          
      } 

      // 输出合并后的数组
      System.out.printf("         %s%n%n", 
         subarrayString(data, left, right));
   } 

   // 输出数组中特定值的方法
   private static String subarrayString(int[] data, int low, int high) {
      StringBuilder temporary = new StringBuilder();

      // 输出用于对齐的空格
      for (int i = 0; i < low; ++i) {
         temporary.append("   ");
      } 

      // 输出数组中剩余的元素
      for (int i = low; i <= high; ++i) {
         temporary.append(" " + data[i]);
      } 

      return temporary.toString();
   }

   public static void main(String[] args) {
      var generator = RandomGenerator.getDefault();

      // 创建包含10个随机整数的无序数组
      int[] data = generator.ints(10, 10, 91).toArray(); 

      System.out.printf("未排序数组: %s%n%n", Arrays.toString(data));
      mergeSort(data); // 对数组排序
      System.out.printf("已排序数组: %s%n", Arrays.toString(data));
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

