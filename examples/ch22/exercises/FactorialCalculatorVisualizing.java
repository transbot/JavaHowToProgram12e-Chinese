// 练习22.14: FactorialCalculatorVisualizing.java
// 递归方法factorial - 修改版，添加递归可视化
import java.math.BigInteger;

public class FactorialCalculatorVisualizing {
   // 递归方法factorial（假设其参数值>=0）- 添加可视化功能
   public static BigInteger factorial(int number) {
      return factorialHelper(number, 0);
   }
   
   // 辅助方法，添加递归深度参数
   private static BigInteger factorialHelper(int number, int depth) {
      // 创建缩进字符串
      String indent = "  ".repeat(depth);
      
      // 打印调用信息
      System.out.printf("%s调用 factorial(%d)%n", indent, number);
      
      if (number <= 1) { // 测试基本情况
         System.out.printf("%s基本情况: 返回 1%n", indent);
         return BigInteger.ONE; // 基本情况: 0! = 1，且1! = 1
      }                                                         
      else { // 递归步骤
         System.out.printf("%s计算 %d * factorial(%d)%n", indent, number, number - 1);
         BigInteger recursiveResult = factorialHelper(number - 1, depth + 1);
         BigInteger result = BigInteger.valueOf(number).multiply(recursiveResult);
         System.out.printf("%s返回 %d * %d = %d%n", indent, number, recursiveResult, result);
         return result;
      }
   }

   public static void main(String[] args) {
      // 计算0~5的阶乘（为了输出清晰，减少计算数量）
      System.out.println("计算0~5的阶乘:");
      for (int counter = 0; counter <= 5; ++counter) {
         System.out.printf("%n=== 计算 %d! ===%n", counter);
         System.out.printf("%d! = %d%n", counter, factorial(counter));
      } 

      // 单独计算10、20和30的阶乘
      System.out.printf("%n=== 计算 10! ===%n");
      System.out.printf("10! = %s%n", factorial(10));
      
      System.out.printf("%n=== 计算 20! ===%n");
      System.out.printf("20! = %s%n", factorial(20));
      
      System.out.printf("%n=== 计算 30! ===%n");
      System.out.printf("30! = %s%n", factorial(30));
   } 
}