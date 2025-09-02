// 练习5.15，PerfectNumberCalculator.java
// 编写一个程序，找出1~1000范围内的所有完全数，并显示其真因子。
// 另外，测试远大于1000的完全数（如8128、33550336），以挑战算力。
import java.util.ArrayList;
import java.util.List;

public class PerfectNumberCalculator {
    public static void main(String[] args) {
        // 1. 输出 1~1000 范围内的所有完全数及其真因子
        System.out.println("=== 1~1000 范围内的完全数及其真因子 ===");
        for (int num = 1; num <= 1000; num++) {
            if (isPerfect(num)) {
                // 获取当前完全数的真因子列表
                List<Integer> properDivisors = getProperDivisors(num);
                // 格式化输出完全数和真因子
                System.out.printf("完全数：%d，真因子：%s%n", 
                    num, properDivisors.toString().replaceAll("[\\[\\]]", ""));
            }
        }

        // 2. 测试远大于 1000 的完全数（挑战算力）
        System.out.println("\n=== 测试远大于 1000 的完全数 ===");
        // 已知的大完全数（前几个完全数，后续完全数需更强算力）
        int[] largePerfectNumbers = {8128, 33550336}; 
        for (int largeNum : largePerfectNumbers) {
            long startTime = System.currentTimeMillis(); // 记录开始时间
            boolean isPerfect = isPerfect(largeNum);
            long endTime = System.currentTimeMillis(); // 记录结束时间
            long duration = endTime - startTime; // 计算耗时

            if (isPerfect) {
                List<Integer> divisors = getProperDivisors(largeNum);
                // 输出大完全数、真因子（仅显示前10个和最后1个，避免输出过长）
                String divisorStr;
                if (divisors.size() > 10) {
                    divisorStr = divisors.subList(0, 10).toString().replaceAll("[\\[\\]]", "") 
                        + " ... " + divisors.get(divisors.size() - 1);
                } else {
                    divisorStr = divisors.toString().replaceAll("[\\[\\]]", "");
                }
                System.out.printf("完全数：%d，真因子（部分）：%s，计算耗时：%d ms%n", 
                    largeNum, divisorStr, duration);
            } else {
                System.out.printf("数字 %d 不是完全数，计算耗时：%d ms%n", largeNum, duration);
            }
        }
    }

    /**
     * 判断一个整数是否为完全数
     * @param number 待判断的整数（需大于1，因1没有真因子）
     * @return true：是完全数；false：不是完全数
     */
    public static boolean isPerfect(int number) {
        // 1 没有真因子，直接返回 false
        if (number <= 1) {
            return false;
        }

        // 计算所有真因子的和（调用工具方法获取真因子列表，再求和）
        List<Integer> properDivisors = getProperDivisors(number);
        int sumOfDivisors = properDivisors.stream()
                                         .mapToInt(Integer::intValue)
                                         .sum();

        // 若真因子之和等于原数，则为完全数
        return sumOfDivisors == number;
    }

    /**
     * 获取一个整数的所有真因子（包括1，不包括自身）
     * @param number 待获取真因子的整数
     * @return 真因子的列表（按从小到大排序）
     */
    public static List<Integer> getProperDivisors(int number) {
        List<Integer> divisors = new ArrayList<>();
        // 真因子最大不超过 number/2（因 number/2 之后的数除了自身都无法整除 number）
        for (int i = 1; i <= number / 2; i++) {
            if (number % i == 0) { // 若 i 能整除 number，则 i 是真因子
                divisors.add(i);
            }
        }
        return divisors;
    }
}