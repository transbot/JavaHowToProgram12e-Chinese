// 练习6.9，DiceRolling.java
// 编写一个程序，模拟投掷两枚骰子3600万次
// 计算并显示每个点数总和出现的次数和概率

import java.util.Random;

public class DiceRolling {
    public static void main(String[] args) {
        // 初始化随机数生成器
        Random random = new Random();
        
        // 创建数组来存储每个点数总和出现的次数（索引2-12）
        int[] sums = new int[13]; // 索引0和1不使用
        
        // 模拟投掷3600万次
        int totalRolls = 36000000;
        for (int i = 0; i < totalRolls; i++) {
            // 掷第一枚骰子（1-6）
            int die1 = random.nextInt(6) + 1;
            // 掷第二枚骰子（1-6）
            int die2 = random.nextInt(6) + 1;
            // 计算总和
            int sum = die1 + die2;
            // 增加对应总和的计数
            sums[sum]++;
        }
        
        // 打印表头（16个空格域内左对齐）
        System.out.println("================================================");
        System.out.printf("%s%16s%16s\n", "点数总和", "出现次数", "出现概率");
        System.out.println("================================================");
        
        // 计算并打印每个点数总和的结果（16个空格域内左对齐）
        for (int i = 2; i <= 12; i++) {
            // 计算实际概率
            double actualProbability = (double) sums[i] / totalRolls * 100;
            
            // 格式化输出，每个字段占16个字符宽度并左对齐
            System.out.printf("%d%22d%22.4f%%\n", 
                             i, sums[i], actualProbability);
        }
        
        // 额外信息：显示最常出现和最不常出现的点数
        int maxSum = 2;
        int minSum = 2;
        for (int i = 3; i <= 12; i++) {
            if (sums[i] > sums[maxSum]) maxSum = i;
            if (sums[i] < sums[minSum]) minSum = i;
        }
        
        System.out.println("================================================");
        System.out.printf("%-16s%-16s\n", "最常出现的点数:", maxSum + " (" + sums[maxSum] + " 次)");
        System.out.printf("%-16s%-16s\n", "最不常出现的点数:", minSum + " (" + sums[minSum] + " 次)");
        System.out.printf("%-16s%-16d\n", "总投掷次数:", totalRolls);
    }
}