// 第12章练习（Simple语言编译器），练习12.22的修改版， InfixToPostfixConverter.java
// 新增功能：支持多位整数操作数和单字母变量名操作数

// 重要：暂不支持正负号等一元操作符
// 该程序提示用户输入一个中缀表达式，
// 验证表达式的有效性，然后将该表达式转换为后缀表达式。
// 该程序应显示转换过程，提示用户输入新的表达式，直到用户选择退出。
// 表达式包含多位整数操作数、单字母变量名操作数和+、-、*、/等二元操作符。
// 例如，表达式(12+34)*(a-5)应转换为12 34 + a 5 - *。
// 使用栈来存储操作符，并在遇到右括号时弹出栈顶的操作符，
// 以及在遇到优先级较低或相等的操作符时弹出栈顶的操作符。 
  
import java.util.Scanner;
import java.util.Stack;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class InfixToPostfixConverter {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.print("请输入中缀表达式（支持多位整数和单字母变量，输入'quit'退出）: ");
            String infixExpression = scanner.nextLine().trim();
            
            if (infixExpression.equalsIgnoreCase("quit")) {
                System.out.println("程序已退出。");
                break;
            }
            
            // 验证表达式有效性
            if (!isValidInfixExpression(infixExpression)) {
                System.out.println("表达式无效，请重新输入。");
                continue;
            }
            
            System.out.println("中缀表达式: " + infixExpression);
            
            try {
                String postfixExpression = convertToPostfix(infixExpression);
                System.out.println("后缀表达式: " + postfixExpression);
                
                // 计算并显示结果（如果表达式只包含数字）
                if (isNumericExpression(infixExpression)) {
                    int result = evaluatePostfix(postfixExpression);
                    System.out.println("计算结果: " + result);
                }
            } catch (Exception e) {
                System.out.println("转换过程中发生错误: " + e.getMessage());
                e.printStackTrace();
            }
            
            System.out.println(); // 空行分隔
        }
        
        scanner.close();
    }
    
    // 检查表达式是否只包含数字（不包含变量）
    private static boolean isNumericExpression(String expression) {
        String cleanExpr = expression.replaceAll("\\s+", "");
        for (int i = 0; i < cleanExpr.length(); i++) {
            char c = cleanExpr.charAt(i);
            if (Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }
    
    // 验证中缀表达式的有效性
    public static boolean isValidInfixExpression(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            System.out.println("错误: 表达式为空");
            return false;
        }
        
        // 移除所有空格
        String cleanExpr = expression.replaceAll("\\s+", "");
        
        // 检查括号是否匹配
        if (!areParenthesesBalanced(cleanExpr)) {
            System.out.println("错误: 括号不匹配");
            return false;
        }
        
        // 检查是否包含非法字符
        if (!containsValidCharacters(cleanExpr)) {
            System.out.println("错误: 包含非法字符");
            return false;
        }
        
        // 检查操作符使用是否正确
        if (!areOperatorsUsedCorrectly(cleanExpr)) {
            System.out.println("错误: 操作符使用不正确");
            return false;
        }
        
        return true;
    }
    
    // 检查括号是否匹配
    private static boolean areParenthesesBalanced(String expression) {
        Stack<Character> stack = new Stack<>();
        
        for (char c : expression.toCharArray()) {
            if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                if (stack.isEmpty() || stack.pop() != '(') {
                    return false;
                }
            }
        }
        
        return stack.isEmpty();
    }
    
    // 检查是否包含合法字符（数字、字母、二元操作符、括号）
    private static boolean containsValidCharacters(String expression) {
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (!Character.isDigit(c) && !Character.isLetter(c) && 
                !isBinaryOperator(c) && c != '(' && c != ')') {
                return false;
            }
        }
        return true;
    }
    
    // 检查操作符使用是否正确
    private static boolean areOperatorsUsedCorrectly(String expression) {
        // 检查表达式是否以操作符开始或结束（负号除外）
        if (isBinaryOperator(expression.charAt(0)) && expression.charAt(0) != '-') {
            return false;
        }
        
        if (isBinaryOperator(expression.charAt(expression.length() - 1))) {
            return false;
        }
        
        // 检查连续操作符（负号除外）
        for (int i = 0; i < expression.length() - 1; i++) {
            char current = expression.charAt(i);
            char next = expression.charAt(i + 1);
            
            if (isBinaryOperator(current) && isBinaryOperator(next) && next != '-') {
                return false;
            }
            
            // 检查操作符后是否跟着右括号
            if (isBinaryOperator(current) && next == ')') {
                return false;
            }
            
            // 检查左括号后是否跟着操作符（负号除外）
            if (current == '(' && isBinaryOperator(next) && next != '-') {
                return false;
            }
        }
        
        return true;
    }
    
    // 检查字符是否为支持的二元操作符
    private static boolean isBinaryOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }
    
    // 将中缀表达式转换为后缀表达式
    public static String convertToPostfix(String infix) {
        // 移除所有空格
        infix = infix.replaceAll("\\s+", "");
        
        // 创建栈和结果字符串
        Stack<Character> stack = new Stack<>();
        StringBuilder postfix = new StringBuilder();
        
        // 在表达式末尾添加右括号，并在栈中压入左括号
        infix += ")";
        stack.push('(');
        
        System.out.println("转换过程:");
        System.out.println("初始栈: " + stackToString(stack));
        System.out.println("初始后缀表达式: " + postfix.toString());
        
        // 遍历表达式中的每个字符
        int i = 0;
        while (i < infix.length()) {
            char currentChar = infix.charAt(i);
            System.out.println("\n当前字符: '" + currentChar + "'");
            
            if (Character.isDigit(currentChar)) {
                // 如果是数字，读取完整的数字
                int start = i;
                while (i < infix.length() && Character.isDigit(infix.charAt(i))) {
                    i++;
                }
                String number = infix.substring(start, i);
                postfix.append(number).append(" ");
                System.out.println("数字 '" + number + "' 添加到后缀表达式: " + postfix.toString());
                System.out.println("栈状态: " + stackToString(stack));
                continue; // 跳过i++，因为我们已经移动了i
            } else if (Character.isLetter(currentChar)) {
                // 如果是字母，读取完整的变量名（单字母）
                postfix.append(currentChar).append(" ");
                System.out.println("变量 '" + currentChar + "' 添加到后缀表达式: " + postfix.toString());
                System.out.println("栈状态: " + stackToString(stack));
            } else if (currentChar == '(') {
                // 如果是左括号，压入栈
                stack.push(currentChar);
                System.out.println("左括号压入栈");
                System.out.println("栈状态: " + stackToString(stack));
            } else if (isBinaryOperator(currentChar)) {
                // 如果是操作符，处理栈中优先级较高或相等的操作符
                while (!stack.isEmpty() && isBinaryOperator(stack.peek()) && 
                       precedence(stack.peek(), currentChar)) {
                    char poppedOperator = stack.pop();
                    postfix.append(poppedOperator).append(" ");
                    System.out.println("操作符 '" + poppedOperator + "' 弹出并添加到后缀表达式");
                    System.out.println("后缀表达式: " + postfix.toString());
                    System.out.println("栈状态: " + stackToString(stack));
                }
                stack.push(currentChar);
                System.out.println("操作符 '" + currentChar + "' 压入栈");
                System.out.println("栈状态: " + stackToString(stack));
            } else if (currentChar == ')') {
                // 如果是右括号，弹出栈中元素直到遇到左括号
                while (!stack.isEmpty() && stack.peek() != '(') {
                    char poppedOperator = stack.pop();
                    postfix.append(poppedOperator).append(" ");
                    System.out.println("操作符 '" + poppedOperator + "' 弹出并添加到后缀表达式");
                    System.out.println("后缀表达式: " + postfix.toString());
                    System.out.println("栈状态: " + stackToString(stack));
                }
                // 弹出左括号
                if (!stack.isEmpty() && stack.peek() == '(') {
                    char poppedParenthesis = stack.pop();
                    System.out.println("左括号 '" + poppedParenthesis + "' 弹出");
                    System.out.println("栈状态: " + stackToString(stack));
                }
            }
            
            i++; // 移动到下一个字符
        }
        
        // 弹出栈中剩余的所有操作符
        while (!stack.isEmpty() && stack.peek() != '(') {
            char poppedOperator = stack.pop();
            postfix.append(poppedOperator).append(" ");
            System.out.println("剩余操作符 '" + poppedOperator + "' 弹出并添加到后缀表达式");
            System.out.println("后缀表达式: " + postfix.toString());
            System.out.println("栈状态: " + stackToString(stack));
        }
        
        return postfix.toString().trim();
    }
    
    // 将栈转换为字符串表示
    private static String stackToString(Stack<Character> stack) {
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
    
    // 比较两个操作符的优先级
    public static boolean precedence(char operator1, char operator2) {
        int prec1 = getPrecedence(operator1);
        int prec2 = getPrecedence(operator2);
        return prec1 >= prec2;
    }
    
    // 获取操作符的优先级值
    private static int getPrecedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return 0;
        }
    }
    
    // 计算后缀表达式的值（仅适用于数字表达式）
    public static int evaluatePostfix(String postfix) {
        Stack<Integer> stack = new Stack<>();
        String[] tokens = postfix.split("\\s+");
        
        for (String token : tokens) {
            if (isNumeric(token)) {
                stack.push(Integer.parseInt(token));
            } else if (token.length() == 1 && isBinaryOperator(token.charAt(0))) {
                int operand2 = stack.pop();
                int operand1 = stack.pop();
                int result = applyOperator(token.charAt(0), operand1, operand2);
                stack.push(result);
            }
        }
        
        return stack.pop();
    }
    
    // 检查字符串是否为数字
    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    // 应用操作符到操作数
    private static int applyOperator(char operator, int operand1, int operand2) {
        switch (operator) {
            case '+': return operand1 + operand2;
            case '-': return operand1 - operand2;
            case '*': return operand1 * operand2;
            case '/': 
                if (operand2 == 0) {
                    throw new ArithmeticException("除以零错误");
                }
                return operand1 / operand2;
            default: return 0;
        }
    }
}