// 4.22，ManhattanCalculationBigDecimal.java
// 曼哈顿岛估值计算程序（使用BigDecimal高精度计算）
// 计算24美元在400年后在不同利率下的价值，并找出达到3万亿美元所需的利率

// 使用BigDecimal进行高精度金融计算

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class ManhattanCalculationBigDecimal {
    public static void main(String[] args) {
        // 设置计算精度
        MathContext mc = new MathContext(100, RoundingMode.HALF_UP);
        
        BigDecimal principal = new BigDecimal("24.00"); // 初始投资
        BigDecimal manhattanValue = new BigDecimal("3000000000000"); // 3万亿美元
        int years = 400;
        
        System.out.println("曼哈顿岛投资分析（使用BigDecimal高精度计算）");
        System.out.println("初始投资: $" + principal);
        System.out.println("投资年限: " + years + "年");
        System.out.println("目标价值: $" + manhattanValue.toPlainString());
        System.out.println("========================================================");
        
        // 计算不同利率下的终值
        System.out.printf("%-8s%-40s%-20s%n", "利率(%)", "400年后的价值(美元)", "简写形式");
        System.out.println("----------------------------------------------------------------");
        
        for (int rate = 5; rate <= 10; rate++) {
            BigDecimal yearlyRate = new BigDecimal(rate).divide(new BigDecimal("100"), mc);
            BigDecimal amount = calculateCompoundInterest(principal, yearlyRate, years, mc);
            
            System.out.printf("%-8d%-40s%-20s%n", 
                            rate, 
                            formatCurrency(amount), 
                            getAbbreviatedForm(amount));
        }
        
        // 寻找目标利率
        System.out.println("\n寻找达到曼哈顿岛估值的利率：");
        BigDecimal targetRate = findTargetRate(principal, manhattanValue, years, mc);
        BigDecimal finalAmount = calculateCompoundInterest(principal, targetRate, years, mc);
        
        System.out.println("所需利率: " + targetRate.multiply(new BigDecimal("100")).setScale(8, RoundingMode.HALF_UP) + "%");
        System.out.println("最终金额: " + formatCurrency(finalAmount));
        System.out.println("简写形式: " + getAbbreviatedForm(finalAmount));
    }
    
    // 计算复利
    private static BigDecimal calculateCompoundInterest(BigDecimal principal, BigDecimal rate, 
                                                       int years, MathContext mc) {
        // 公式: amount = principal * (1 + rate)^years
        BigDecimal onePlusRate = BigDecimal.ONE.add(rate);
        BigDecimal growthFactor = onePlusRate.pow(years, mc);
        return principal.multiply(growthFactor, mc);
    }
    
    // 格式化货币输出
    private static String formatCurrency(BigDecimal amount) {
        try {
            return String.format("$%,.2f", amount);
        } catch (Exception e) {
            // 对于极大数值，使用科学计数法
            return "$" + amount.setScale(2, RoundingMode.HALF_UP).toEngineeringString();
        }
    }
    
    // 获取简写形式
    private static String getAbbreviatedForm(BigDecimal amount) {
        BigDecimal trillion = new BigDecimal("1E12");
        BigDecimal billion = new BigDecimal("1E9");
        BigDecimal million = new BigDecimal("1E6");
        BigDecimal thousand = new BigDecimal("1E3");
        
        if (amount.compareTo(trillion) >= 0) {
            return "约" + amount.divide(trillion, 2, RoundingMode.HALF_UP) + "万亿";
        } else if (amount.compareTo(billion) >= 0) {
            return "约" + amount.divide(billion, 2, RoundingMode.HALF_UP) + "十亿";
        } else if (amount.compareTo(million) >= 0) {
            return "约" + amount.divide(million, 2, RoundingMode.HALF_UP) + "百万";
        } else if (amount.compareTo(thousand) >= 0) {
            return "约" + amount.divide(thousand, 2, RoundingMode.HALF_UP) + "千";
        } else {
            return "约" + amount.setScale(2, RoundingMode.HALF_UP);
        }
    }
    
    // 使用二分法寻找目标利率
    private static BigDecimal findTargetRate(BigDecimal principal, BigDecimal target, 
                                           int years, MathContext mc) {
        BigDecimal low = BigDecimal.ZERO;
        BigDecimal high = new BigDecimal("0.20"); // 20%
        BigDecimal epsilon = new BigDecimal("1E-15");
        BigDecimal two = new BigDecimal("2");
        
        BigDecimal mid = BigDecimal.ZERO;
        int maxIterations = 100;
        
        for (int i = 0; i < maxIterations; i++) {
            mid = low.add(high).divide(two, mc);
            
            BigDecimal amount = calculateCompoundInterest(principal, mid, years, mc);
            BigDecimal difference = amount.subtract(target).abs();
            
            if (difference.compareTo(epsilon) < 0) {
                break; // 达到所需精度
            }
            
            if (amount.compareTo(target) < 0) {
                low = mid;
            } else {
                high = mid;
            }
        }
        
        return mid;
    }
}