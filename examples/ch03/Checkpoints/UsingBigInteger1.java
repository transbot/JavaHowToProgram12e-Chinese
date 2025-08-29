// 练习3.22: UsingBigInteger1.java
// 使用Java API BigInteger类的对象来
// 创建和操作超大整数
import java.math.BigInteger;

public class UsingBigInteger1 {
   public static void main(String[] args) {
      // 显示一个long能存储的最大值
      System.out.printf("long的最大值: %d%n%n", Long.MAX_VALUE);

      // 可以从字符串或整数来创建BigInteger类型的对象
      BigInteger value1 = 
         new BigInteger("100000000000000000000000000000"); // 30位整数
      BigInteger value2 = BigInteger.valueOf(9_223_372_036_854_775_807L);
      BigInteger value3 = BigInteger.valueOf(17); 

      System.out.println("初始值");
      System.out.printf("BigInteger value1: %s%n", value1);
      System.out.printf("BigInteger value2: %s%n", value2);
      System.out.printf("BigInteger value3: %s%n", value3);

      System.out.println("\n大整数算术运算");
      System.out.printf("     value1.add(value2): %s%n", 
         value1.add(value2));
      System.out.printf("value1.subtract(value2): %s%n", 
         value1.subtract(value2));
      System.out.printf("value1.multiply(value2): %s%n", 
         value1.multiply(value2));
      System.out.printf("value1.multiply(value3): %s%n", 
         value1.multiply(value3));
      
      // 添加除法和求余运算
      System.out.printf("  value1.divide(value2): %s%n", 
         value1.divide(value2));
      System.out.printf("     value1.mod(value2): %s%n", 
         value1.mod(value2));

      System.out.println("\n使用BigInteger的pow和sqrt方法");
      // 计算value1的平方
      System.out.printf("value1.pow(2): %s%n", 
         value1.pow(2));
      
      // 创建value4（1后面36个零）
      BigInteger value4 = new BigInteger("1000000000000000000000000000000000000");
      System.out.printf("BigInteger value4: %s%n", value4);
      
      // 计算value4的平方根
      System.out.printf("value4.sqrt(): %s%n", 
         value4.sqrt());
   }
}