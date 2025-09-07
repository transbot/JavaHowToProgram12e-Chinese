// 练习7.27， UnitConverter.java
// 支持多种格式的单位转换
// 支持的格式包括:
// "1m to mm", "1 kilometer to meter", "convert 2kg to g", "how many mm in 1m"
// 支持的单位包括: 
// 长度: mm, cm, m, km, inch(es), foot/feet, yard(s), mile(s)
// 体积: ml, l, liter(s), gallon(s), quart(s), pint(s), cup(s)
// 质量: mg, g, kg, ton(s), ounce(s), pound(s), lb(s)
// 支持单位的单复数形式    

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnitConverter {
    
    // 定义单位类型
    private static final int LENGTH = 0;
    private static final int VOLUME = 1;
    private static final int MASS = 2;
    private static final int INVALID = -1;
    
    // 单位数据数组 [单位名称, 换算系数, 单位类型, 显示名称]
    private static final String[][] units = {
        // 长度单位
        {"mm", "0.001", "0", "mm"},
        {"cm", "0.01", "0", "cm"},
        {"m", "1.0", "0", "m"},
        {"km", "1000.0", "0", "km"},
        {"meter", "1.0", "0", "m"},
        {"meters", "1.0", "0", "m"},
        {"kilometer", "1000.0", "0", "km"},
        {"kilometers", "1000.0", "0", "km"},
        {"centimeter", "0.01", "0", "cm"},
        {"centimeters", "0.01", "0", "cm"},
        {"millimeter", "0.001", "0", "mm"},
        {"millimeters", "0.001", "0", "mm"},
        {"inch", "0.0254", "0", "inch"},
        {"inches", "0.0254", "0", "inches"},
        {"foot", "0.3048", "0", "foot"},
        {"feet", "0.3048", "0", "feet"},
        {"yard", "0.9144", "0", "yard"},
        {"yards", "0.9144", "0", "yards"},
        {"mile", "1609.344", "0", "mile"},
        {"miles", "1609.344", "0", "miles"},
        
        // 体积单位
        {"ml", "0.001", "1", "ml"},
        {"l", "1.0", "1", "l"},
        {"liter", "1.0", "1", "l"},
        {"liters", "1.0", "1", "l"},
        {"milliliter", "0.001", "1", "ml"},
        {"milliliters", "0.001", "1", "ml"},
        {"gallon", "3.78541", "1", "gallon"},
        {"gallons", "3.78541", "1", "gallons"},
        {"quart", "0.946353", "1", "quart"},
        {"quarts", "0.946353", "1", "quarts"},
        {"pint", "0.473176", "1", "pint"},
        {"pints", "0.473176", "1", "pints"},
        {"cup", "0.24", "1", "cup"},
        {"cups", "0.24", "1", "cups"},
        
        // 质量单位
        {"mg", "0.000001", "2", "mg"},
        {"g", "0.001", "2", "g"},
        {"kg", "1.0", "2", "kg"},
        {"ton", "1000.0", "2", "ton"},
        {"milligram", "0.000001", "2", "mg"},
        {"milligrams", "0.000001", "2", "mg"},
        {"gram", "0.001", "2", "g"},
        {"grams", "0.001", "2", "g"},
        {"kilogram", "1.0", "2", "kg"},
        {"kilograms", "1.0", "2", "kg"},
        {"ounce", "0.0283495", "2", "ounce"},
        {"ounces", "0.0283495", "2", "ounces"},
        {"pound", "0.453592", "2", "pound"},
        {"pounds", "0.453592", "2", "pounds"},
        {"lb", "0.453592", "2", "lb"}
    };

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("欢迎使用单位换算程序!");
        System.out.println("支持的提问方式（注意只能用英文提问）: '1m to mm', '1 kilometer to meter', 'convert 2kg to g', 'how many mm in 1m'");
        System.out.println("输入'exit'退出:");
        
        while (true) {
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("exit")) {
                break;
            }
            
            if (input.isEmpty()) {
                continue;
            }
            
            String result = processQuestion(input);
            System.out.println(result);
            System.out.println("\n输入另一个换算，或者输入'exit'退出:");
        }
        
        scanner.close();
        System.out.println("感谢使用单位换算程序!");
    }

    private static String processQuestion(String question) {
        // 多种匹配模式 - 改进版，允许数字和单位之间无空格
        Pattern pattern1 = Pattern.compile("(\\d+\\.?\\d*)\\s*([a-zA-Z]+)\\s*(?:to|in|into)\\s*([a-zA-Z]+)");
        Pattern pattern2 = Pattern.compile("(?i)convert\\s+(\\d+\\.?\\d*)\\s*([a-zA-Z]+)\\s+to\\s+([a-zA-Z]+)");
        Pattern pattern3 = Pattern.compile("(?i)how\\s+many\\s+([a-zA-Z]+)\\s+(?:are\\s+)?in\\s+(\\d+\\.?\\d*)\\s*([a-zA-Z]+)");
        Pattern pattern4 = Pattern.compile("(?i)how\\s+many\\s+([a-zA-Z]+)\\s+(?:are\\s+)?there\\s+in\\s+(\\d+\\.?\\d*)\\s*([a-zA-Z]+)");
        Pattern pattern5 = Pattern.compile("(?i)(\\d+\\.?\\d*)\\s*([a-zA-Z]+)\\s+to\\s+([a-zA-Z]+)");
        Pattern pattern6 = Pattern.compile("(?i)(\\d+\\.?\\d*)\\s*([a-zA-Z]+)\\s*[=]\\s*\\?\\s*([a-zA-Z]+)");
        Pattern pattern7 = Pattern.compile("(?i)(\\d+\\.?\\d*)\\s*([a-zA-Z]+)");
        
        double value;
        String fromUnit;
        String toUnit;
        Matcher matcher;
        
        if ((matcher = pattern3.matcher(question)).find()) {
            // Format: "how many mm in 1m" or "how many mm in 1km" (with or without space)
            toUnit = matcher.group(1).toLowerCase();
            value = Double.parseDouble(matcher.group(2));
            fromUnit = matcher.group(3).toLowerCase();
        }
        else if ((matcher = pattern4.matcher(question)).find()) {
            // Format: "how many feet are there in 5 kilometers"
            toUnit = matcher.group(1).toLowerCase();
            value = Double.parseDouble(matcher.group(2));
            fromUnit = matcher.group(3).toLowerCase();
        }
        else if ((matcher = pattern1.matcher(question)).find()) {
            // Format: "1m to mm" or "1km to m" (with or without space)
            value = Double.parseDouble(matcher.group(1));
            fromUnit = matcher.group(2).toLowerCase();
            toUnit = matcher.group(3).toLowerCase();
        } 
        else if ((matcher = pattern2.matcher(question)).find()) {
            // Format: "convert 1m to mm"
            value = Double.parseDouble(matcher.group(1));
            fromUnit = matcher.group(2).toLowerCase();
            toUnit = matcher.group(3).toLowerCase();
        }
        else if ((matcher = pattern5.matcher(question)).find()) {
            // Format: "5 kilometers to feet"
            value = Double.parseDouble(matcher.group(1));
            fromUnit = matcher.group(2).toLowerCase();
            toUnit = matcher.group(3).toLowerCase();
        }
        else if ((matcher = pattern6.matcher(question)).find()) {
            // Format: "1m = ? mm"
            value = Double.parseDouble(matcher.group(1));
            fromUnit = matcher.group(2).toLowerCase();
            toUnit = matcher.group(3).toLowerCase();
        }
        else if ((matcher = pattern7.matcher(question)).find()) {
            // Format: "10km" (简单格式，假设用户想要转换为基本单位)
            value = Double.parseDouble(matcher.group(1));
            fromUnit = matcher.group(2).toLowerCase();
            
            // 根据单位类型选择合适的目标单位
            int fromIndex = findUnitIndex(fromUnit);
            if (fromIndex == -1) {
                return "错误: 无法识别单位，请检查您的输入。";
            }
            
            int type = getUnitType(fromIndex);
            toUnit = getBaseUnit(type);
        }
        else {
            return "不能理解您的问题，请使用'1m to mm'或'how many inches in 1km'这样的格式";
        }
        
        // 检查单位有效性
        int fromIndex = findUnitIndex(fromUnit);
        int toIndex = findUnitIndex(toUnit);
        
        if (fromIndex == -1 || toIndex == -1) {
            return "错误: 无法识别的单位，请检查您的输入。";
        }
        
        // 检查单位类型是否匹配
        int fromType = getUnitType(fromIndex);
        int toType = getUnitType(toIndex);
        
        if (fromType != toType) {
            return "错误: 不同单位类型之间不能相互换算. " + 
                   fromUnit + "是" + getUnitTypeName(fromType) + 
                   "单位，而" + toUnit + "是" + getUnitTypeName(toType) + "单位。";
        }
        
        // 执行转换
        double result = convert(value, fromIndex, toIndex);
        
        // 格式化结果
        return String.format("%.2f %s = %.6f %s", value, getDisplayName(fromIndex), result, getDisplayName(toIndex));
    }

    private static int findUnitIndex(String unit) {
        for (int i = 0; i < units.length; i++) {
            if (units[i][0].equalsIgnoreCase(unit)) {
                return i;
            }
        }
        return -1;
    }

    private static int getUnitType(int unitIndex) {
        if (unitIndex == -1) return INVALID;
        return Integer.parseInt(units[unitIndex][2]);
    }

    private static double getConversionFactor(int unitIndex) {
        if (unitIndex == -1) return 0;
        return Double.parseDouble(units[unitIndex][1]);
    }

    private static String getDisplayName(int unitIndex) {
        if (unitIndex == -1) return "未知";
        return units[unitIndex][3];
    }

    private static String getBaseUnit(int type) {
        switch (type) {
            case LENGTH: return "m";
            case VOLUME: return "l";
            case MASS: return "kg";
            default: return "未知";
        }
    }

    private static double convert(double value, int fromIndex, int toIndex) {
        // 转换为基本单位，然后再转换为目标单位
        double valueInBase = value * getConversionFactor(fromIndex);
        return valueInBase / getConversionFactor(toIndex);
    }

    private static String getUnitTypeName(int type) {
        switch (type) {
            case LENGTH: return "长度";
            case VOLUME: return "体积";
            case MASS: return "质量";
            default: return "未知";
        }
    }
}