// 练习12.23， PostfixEvaluator.java
// 编写一个程序，提示用户输入一个后缀表达式，
// 验证表达式的有效性，然后计算该表达式的值。
// 该程序应显示计算过程，提示用户输入新的表达式，直到用户选择退出。
// 表达式只包含1位整数操作数和+、-、*、/等二元操作符。
// 例如，表达式2 3 * 5 4 * + 9 -应转换为(2 * 3) + (5 * 4) - 9。
// 使用栈来存储操作数，并在遇到操作符时弹出栈顶的两个操作数进行计算，
// 然后将结果压入栈中。

import java.util.Scanner;
import java.util.Stack;

public class PostfixEvaluator {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.print("请输入后缀表达式（支持空格分隔或无空格分隔，输入'quit'退出）: ");
            String postfixExpression = scanner.nextLine().trim();
            
            if (postfixExpression.equalsIgnoreCase("quit")) {
                System.out.println("程序已退出。");
                break;
            }
            
            // 移除所有空格，统一处理
            String cleanExpression = postfixExpression.replaceAll("\\s+", "");
            
            // 验证表达式有效性
            if (!isValidPostfixExpression(cleanExpression)) {
                System.out.println("后缀表达式无效，请重新输入。");
                continue;
            }
            
            try {
                int result = evaluatePostfixExpression(cleanExpression);
                System.out.println("计算结果: " + result);
            } catch (Exception e) {
                System.out.println("计算过程中发生错误: " + e.getMessage());
            }
            
            System.out.println(); // 空行分隔
        }
        
        scanner.close();
    }
    
    // 验证后缀表达式的有效性
    public static boolean isValidPostfixExpression(String expression) {
        if (expression == null || expression.isEmpty()) {
            System.out.println("错误: 表达式为空");
            return false;
        }
        
        // 检查是否包含非法字符
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (!Character.isDigit(c) && !isOperator(c)) {
                System.out.println("错误: 包含非法字符 '" + c + "'");
                return false;
            }
        }
        
        // 检查操作数和操作符的数量关系
        int operandCount = 0;
        int operatorCount = 0;
        
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (Character.isDigit(c)) {
                operandCount++;
            } else if (isOperator(c)) {
                operatorCount++;
            }
        }
        
        // 对于有效的后缀表达式，操作数数量应该比操作符数量多1
        if (operandCount != operatorCount + 1) {
            System.out.println("错误: 操作数和操作符数量不匹配");
            System.out.println("操作数: " + operandCount + ", 操作符: " + operatorCount);
            return false;
        }
        
        return true;
    }
    
    // 计算后缀表达式
    public static int evaluatePostfixExpression(String expression) {
        Stack<Integer> stack = new Stack<>();
        
        System.out.println("计算过程:");
        System.out.println("初始栈: " + stackToString(stack));
        System.out.println("表达式: " + expression);
        
        // 遍历表达式中的每个字符
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            
            System.out.println("\n当前字符: '" + c + "'");
            
            if (Character.isDigit(c)) {
                // 如果是数字，转换为整数并压入栈
                int value = c - '0'; // 将字符转换为对应的整数值
                stack.push(value);
                System.out.println("数字 " + value + " 压入栈");
                System.out.println("栈状态: " + stackToString(stack));
            } else if (isOperator(c)) {
                // 如果是操作符，弹出栈顶两个元素进行计算
                if (stack.size() < 2) {
                    throw new RuntimeException("操作符 '" + c + "' 处栈中元素不足");
                }
                
                int x = stack.pop();
                int y = stack.pop();
                int result = calculate(y, x, c); // 注意顺序: y 操作符 x
                
                System.out.println("弹出 " + x + " 和 " + y);
                System.out.println("计算: " + y + " " + c + " " + x + " = " + result);
                
                stack.push(result);
                System.out.println("结果 " + result + " 压入栈");
                System.out.println("栈状态: " + stackToString(stack));
            }
        }
        
        // 最终栈中应只有一个元素，即计算结果
        if (stack.size() != 1) {
            throw new RuntimeException("计算完成后栈中元素数量不正确");
        }
        
        return stack.pop();
    }
    
    // 执行计算操作
    public static int calculate(int operand1, int operand2, char operator) {
        switch (operator) {
            case '+':
                return operand1 + operand2;
            case '-':
                return operand1 - operand2;
            case '*':
                return operand1 * operand2;
            case '/':
                if (operand2 == 0) {
                    throw new RuntimeException("除以零错误");
                }
                return operand1 / operand2;
            default:
                throw new RuntimeException("未知操作符: " + operator);
        }
    }
    
    // 检查字符是否为操作符
    public static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }
    
    // 将整数栈转换为字符串表示
    private static String stackToString(Stack<Integer> stack) {
        if (stack.isEmpty()) {
            return "[]";
        }
        
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < stack.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(stack.get(i));
        }
        sb.append("]");
        return sb.toString();
    }
}