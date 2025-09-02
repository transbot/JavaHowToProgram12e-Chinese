// 练习5.33，RollDieSecureTimed.java
import java.util.random.RandomGenerator;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

public class RollDieSecureTimed {
   public static void main(String[] args) {
      // 测试RandomGenerator
      System.out.println("使用RandomGenerator模拟6千万次掷骰:");
      simulateWithRandomGenerator(60_000_000);
      
      System.out.println(); // 空行分隔
      
      // 测试SecureRandom
      System.out.println("使用SecureRandom模拟6千万次掷骰:");
      simulateWithSecureRandom(60_000_000);
   }
   
   // 使用RandomGenerator模拟掷骰并计时
   private static void simulateWithRandomGenerator(int numberOfRolls) {
      RandomGenerator randomNumbers = RandomGenerator.getDefault();
      
      int frequency1 = 0;
      int frequency2 = 0;
      int frequency3 = 0;
      int frequency4 = 0;
      int frequency5 = 0;
      int frequency6 = 0;
   
      // 记录开始时间
      Instant start = Instant.now();
      
      // 统计指定次数的掷骰结果
      for (int roll = 1; roll <= numberOfRolls; ++roll) {
         int face = randomNumbers.nextInt(1, 7); // 生成1~6的随机整数
   
         // 根据点数来决定递增哪个计数器
         switch (face) {
            case 1 -> ++frequency1;
            case 2 -> ++frequency2;
            case 3 -> ++frequency3;
            case 4 -> ++frequency4;
            case 5 -> ++frequency5;
            case 6 -> ++frequency6;
         } 
      }
      
      // 记录结束时间
      Instant end = Instant.now();
      
      // 计算时间差
      Duration duration = Duration.between(start, end);
      
      // 获取总纳秒数并转换为毫秒
      long totalNanos = duration.getNano() + duration.getSeconds() * 1_000_000_000L;
      long totalMillis = totalNanos / 1_000_000; // 1毫秒 = 1,000,000纳秒

      // 输出结果
      System.out.println("点数\t次数");
      System.out.printf("1\t%d%n2\t%d%n3\t%d%n4\t%d%n5\t%d%n6\t%d%n",
         frequency1, frequency2, frequency3, frequency4,
         frequency5, frequency6);
      
      // 输出耗时
      System.out.printf("总耗时: %d 毫秒%n", totalMillis);
   }
   
   // 使用SecureRandom模拟掷骰并计时
   private static void simulateWithSecureRandom(int numberOfRolls) {
      SecureRandom secureRandom = new SecureRandom();
      
      int frequency1 = 0;
      int frequency2 = 0;
      int frequency3 = 0;
      int frequency4 = 0;
      int frequency5 = 0;
      int frequency6 = 0;
   
      // 记录开始时间
      Instant start = Instant.now();
      
      // 统计指定次数的掷骰结果
      for (int roll = 1; roll <= numberOfRolls; ++roll) {
         int face = secureRandom.nextInt(6) + 1; // 生成1~6的随机整数
   
         // 根据点数来决定递增哪个计数器
         switch (face) {
            case 1 -> ++frequency1;
            case 2 -> ++frequency2;
            case 3 -> ++frequency3;
            case 4 -> ++frequency4;
            case 5 -> ++frequency5;
            case 6 -> ++frequency6;
         } 
      }
      
      // 记录结束时间
      Instant end = Instant.now();
      
      // 计算时间差
      Duration duration = Duration.between(start, end);
      
      // 获取总纳秒数并转换为毫秒
      long totalNanos = duration.getNano() + duration.getSeconds() * 1_000_000_000L;
      long totalMillis = totalNanos / 1_000_000; // 1毫秒 = 1,000,000纳秒

      // 输出结果
      System.out.println("点数\t次数");
      System.out.printf("1\t%d%n2\t%d%n3\t%d%n4\t%d%n5\t%d%n6\t%d%n",
         frequency1, frequency2, frequency3, frequency4,
         frequency5, frequency6);
      
      // 输出耗时
      System.out.printf("总耗时: %d 毫秒%n", totalMillis);
   }
}