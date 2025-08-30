// 4.21，ManhattanCalculation.java
// 曼哈顿岛估值计算程序
// 计算24美元在400年后在不同利率下的价值，并找出达到3万亿美元所需的利率

public class ManhattanCalculation {
    public static void main(String[] args) {
        double principal = 24.00; // 初始金额（美元）
        double manhattanValue = 3e12; // 曼哈顿岛当前估值（3万亿美元）
        
        // 显示列标题
        System.out.printf("%s%24s%n", "利率(%)", "400年后的价值(美元)");
        System.out.println("----------------------------------------");
        
        // 利率从5%到10%，每次增加1%
        for (double rate = 5.0; rate <= 10.0; rate++) {
            double yearlyRate = rate / 100.0; // 转换为小数形式
            // 计算400年后的价值
            double amount = principal * Math.pow(1.0 + yearlyRate, 400);
            // 显示利率和对应价值
            System.out.printf("%8.1f%24.2f%n", rate, amount);
        }
        
        // 寻找达到曼哈顿岛估值的利率
        System.out.println("\n寻找达到曼哈顿岛当前估值（3万亿美元）的利率：");
        double targetRate = findRate(principal, manhattanValue, 400);
        System.out.printf("当利率为%.4f%%时，24美元在400年后将达到3万亿美元%n", targetRate * 100);
    }
    
    // 二分法寻找使终值达到目标的利率
    private static double findRate(double principal, double target, int years) {
        double low = 0.0;
        double high = 0.2; // 20%的上限
        double mid = 0.0;
        double epsilon = 1e-10; // 精度
        
        while (high - low > epsilon) {
            mid = (low + high) / 2;
            double amount = principal * Math.pow(1.0 + mid, years);
            
            if (amount < target) {
                low = mid;
            } else {
                high = mid;
            }
        }
        
        return mid;
    }
}
