// 练习4.19，BreakTest1.java
// 图4.7修改版: 移除break语句
// 不使用break语句，通过修改循环条件实现相同功能
public class BreakTest1 {
   public static void main(String[] args) {
      int count; 
      
      // 循环继续条件改为：count小于等于10且未达到需要提前退出的条件（count不等于5）
      // 当count等于5时，循环条件不成立，自然退出循环，替代break功能
      for (count = 1; count <= 10 && count != 5; ++count) { 
         System.out.printf("%d ", count);
      } 

      System.out.printf("%n当count = %d时，循环终止%n", count);
   }
} 