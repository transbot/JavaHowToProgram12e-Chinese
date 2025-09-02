// 练习5.32，RollDieTimed.java
import java.util.random.RandomGenerator;
import java.time.Duration;
import java.time.Instant;

public class RollDieTimed {
   public static void main(String[] args) {
      // 用randomNumbers对象生成随机数
      RandomGenerator randomNumbers = RandomGenerator.getDefault(); 

      // 模拟6千万次掷骰
      System.out.println("模拟6千万次掷骰:");
      simulateDiceRolls(60_000_000, randomNumbers);
      
      System.out.println(); // 空行分隔
      
      // 模拟6亿次掷骰
      System.out.println("模拟6亿次掷骰:");
      simulateDiceRolls(600_000_000, randomNumbers);
   }
   
   // 模拟指定次数的掷骰并计时
   private static void simulateDiceRolls(int numberOfRolls, RandomGenerator randomNumbers) {
      int frequency1 = 0; // 掷出1点的次数
      int frequency2 = 0; // 掷出2点的次数
      int frequency3 = 0; // 掷出3点的次数
      int frequency4 = 0; // 掷出4点的次数
      int frequency5 = 0; // 掷出5点的次数
      int frequency6 = 0; // 掷出6点的次数
   
      // 记录开始时间
      Instant start = Instant.now();
      
      // 统计指定次数的掷骰结果
      for (int roll = 1; roll <= numberOfRolls; ++roll) {
         int face = randomNumbers.nextInt(1, 7); // 生成1~6的随机整数
   
         // 根据点数来决定递增哪个计数器
         switch (face) {
            case 1 -> ++frequency1; // 递增点数1的计数器
            case 2 -> ++frequency2; // 递增点数2的计数器
            case 3 -> ++frequency3; // 递增点数3的计数器
            case 4 -> ++frequency4; // 递增点数4的计数器
            case 5 -> ++frequency5; // 递增点数5的计数器
            case 6 -> ++frequency6; // 递增点数6的计数器
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
      System.out.println("点数\t次数"); // 列标题
      System.out.printf("1\t%d%n2\t%d%n3\t%d%n4\t%d%n5\t%d%n6\t%d%n",
         frequency1, frequency2, frequency3, frequency4,
         frequency5, frequency6);
      
      // 输出耗时
      System.out.printf("总耗时: %d 毫秒%n", totalMillis);
   }
}