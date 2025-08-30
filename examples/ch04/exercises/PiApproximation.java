// 练习4.14，PiApproximation.java
// 使用无穷级数计算圆周率π的值
public class PiApproximation {
   public static void main(String[] args) {
      double pi = 0.0;
      int terms = 200000;
      int targetCount = 0;
      boolean targetFound = false;
      
      System.out.println("项数\t\tπ的近似值");
      System.out.println("----------------------------");
      
      for (int i = 1; i <= terms; i++) {
         double term = 4.0 / (2 * i - 1);
         if (i % 2 == 0) {
            pi -= term;
         } else {
            pi += term;
         }
         
         // 输出每10000项的近似值
         if (i % 10000 == 0) {
            System.out.printf("%d\t\t%.10f%n", i, pi);
         }
         
         // 更精确地检查是否达到3.14159精度
         if (!targetFound && pi >= 3.14159 && pi < 3.14160) {
            targetCount = i;
            targetFound = true;
            System.out.println("\n首次达到3.14159精度在项数: " + targetCount + ", 值: " + pi);
         }
      }
      
      System.out.println("\n计算" + terms + "项后的最终近似值: " + pi);
      
      // 检查最终精度
      if (pi >= 3.14159 && pi < 3.14160) {
         System.out.println("最终结果达到3.14159精度");
      } else {
         System.out.println("最终结果未达到3.14159精度");
      }
   }
}