// 练习12.22，InfixToPostfixConverter.java
// 编写一个程序，提示用户输入一个中缀表达式，其中只允许包含1位整数、+、-、*、/、(和)。
// 将该表达式转换为后缀表达式并显示转换过程。
// 该程序应使用栈来存储操作符和左右括号。
    
import java.util.Scanner;
import java.util.Stack;

public class InfixToPostfixConverter {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.print("请输入中缀表达式（仅支持1位整数和二元操作符，输入'quit'退出）: ");
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
            
            // 检查是否包含多位整数
            if (containsMultiDigitNumbers(infixExpression)) {
                System.out.println("错误: 表达式包含多位整数，仅支持1位整数");
                System.out.println("请重新输入只包含1位整数的表达式");
                continue;
            }
            
            // 检查是否包含非二元操作符
            if (containsNonBinaryOperators(infixExpression)) {
                System.out.println("错误: 表达式包含非二元操作符，仅支持二元操作符(+,-,*,/)");
                System.out.println("请重新输入只包含二元操作符的表达式");
                continue;
            }
            
            System.out.println("中缀表达式: " + infixExpression);
            
            try {
                String postfixExpression = convertToPostfix(infixExpression);
                System.out.println("后缀表达式: " + postfixExpression);
            } catch (Exception e) {
                System.out.println("转换过程中发生错误: " + e.getMessage());
            }
            
            System.out.println(); // 空行分隔
        }
        
        scanner.close();
    }
    
    // 检查表达式是否包含非二元操作符
    private static boolean containsNonBinaryOperators(String expression) {
        // 移除所有空格
        String cleanExpr = expression.replaceAll("\\s+", "");
        
        // 检查每个字符，如果是不支持的操作符，则返回true
        for (int i = 0; i < cleanExpr.length(); i++) {
            char c = cleanExpr.charAt(i);
            
            // 如果是操作符但不是支持的二元操作符
            if (!Character.isDigit(c) && c != '(' && c != ')' && !isBinaryOperator(c)) {
                return true;
            }
        }
        
        return false;
    }
    
    // 检查字符是否为支持的二元操作符
    private static boolean isBinaryOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }
    
    // 检查表达式是否包含多位整数
    private static boolean containsMultiDigitNumbers(String expression) {
        // 移除所有空格
        String cleanExpr = expression.replaceAll("\\s+", "");
        
        for (int i = 0; i < cleanExpr.length(); i++) {
            char currentChar = cleanExpr.charAt(i);
            
            // 如果当前字符是数字
            if (Character.isDigit(currentChar)) {
                // 检查下一个字符是否也是数字
                if (i < cleanExpr.length() - 1 && Character.isDigit(cleanExpr.charAt(i + 1))) {
                    return true;
                }
            }
        }
        
        return false;
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
        
        // 检查操作数-操作符比例是否正确（二元操作符需要两个操作数）
        if (!hasCorrectOperandOperatorRatio(cleanExpr)) {
            System.out.println("错误: 操作数和操作符数量不匹配");
            return false;
        }
        
        return true;
    }
    
    // 检查操作数和操作符比例是否正确
    private static boolean hasCorrectOperandOperatorRatio(String expression) {
        int operandCount = 0;
        int operatorCount = 0;
        
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            
            if (Character.isDigit(c)) {
                operandCount++;
            } else if (isBinaryOperator(c)) {
                operatorCount++;
            }
        }
        
        // 对于有效的二元操作符表达式，操作数数量应该比操作符数量多1
        return operandCount == operatorCount + 1;
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
    
    // 检查是否包含合法字符（数字、二元操作符、括号）
    private static boolean containsValidCharacters(String expression) {
        for (char c : expression.toCharArray()) {
            if (!Character.isDigit(c) && !isBinaryOperator(c) && c != '(' && c != ')') {
                return false;
            }
        }
        return true;
    }
    
    // 检查操作符使用是否正确
    private static boolean areOperatorsUsedCorrectly(String expression) {
        // 检查表达式是否以操作符开始或结束
        if (isBinaryOperator(expression.charAt(0))) {
            return false;
        }
        
        if (isBinaryOperator(expression.charAt(expression.length() - 1))) {
            return false;
        }
        
        // 检查连续操作符
        for (int i = 0; i < expression.length() - 1; i++) {
            char current = expression.charAt(i);
            char next = expression.charAt(i + 1);
            
            if (isBinaryOperator(current) && isBinaryOperator(next)) {
                return false;
            }
            
            // 检查操作符后是否跟着右括号
            if (isBinaryOperator(current) && next == ')') {
                return false;
            }
            
            // 检查左括号后是否跟着操作符
            if (current == '(' && isBinaryOperator(next)) {
                return false;
            }
        }
        
        return true;
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
        for (int i = 0; i < infix.length(); i++) {
            char currentChar = infix.charAt(i);
            System.out.println("\n当前字符: '" + currentChar + "'");
            
            if (Character.isDigit(currentChar)) {
                // 如果是数字，直接添加到后缀表达式
                postfix.append(currentChar).append(" ");
                System.out.println("数字添加到后缀表达式: " + postfix.toString());
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
}