// 第12章练习（Simple语言编译器），练习12.23的修改版，  PostfixEvaluator.java
// 新增功能：支持多位整数操作数和单字母变量名操作数。
// 新增功能：实现"钩子"以生成SML指令，而非直接对表达式求值。

// 编写一个程序，提示用户输入一个后缀表达式，
// 验证表达式的有效性，然后计算该表达式的值。
// 该程序应显示计算过程，提示用户输入新的表达式，直到用户选择退出。

import java.util.Scanner;
import java.util.Stack;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PostfixEvaluator {    
    // SML指令数组
    private int[] sml;
    // 指令计数器
    private int instructionCounter;
    // 数据计数器（从99开始向下分配）
    private int dataCounter;
    // 符号表
    private Map<String, TableEntry> symbolTable;
    // 临时变量计数器
    private int tempCounter;
    // 生成的SML指令列表
    private List<String> generatedInstructions;
    
    /**
     * 构造函数
     */
    public PostfixEvaluator() {
        sml = new int[100];
        instructionCounter = 0;
        dataCounter = 99;
        symbolTable = new HashMap<>();
        tempCounter = 0;
        generatedInstructions = new ArrayList<>();
    }
    
    /**
     * TableEntry类，表示符号表中的条目
     */
    private static class TableEntry {
        int symbol;    // 符号的整数值
        char type;     // 类型：'C'常量，'L'行号，'V'变量
        int location;  // 内存位置
        
        TableEntry(int symbol, char type, int location) {
            this.symbol = symbol;
            this.type = type;
            this.location = location;
        }
    }
    
    /**
     * 生成SML指令从后缀表达式
     * @param postfixExpression 后缀表达式（空格分隔）
     * @return 表达式结果的内存位置
     */
    public int generateSMLFromPostfix(String postfixExpression) {
        // 分割表达式为token
        String[] tokens = postfixExpression.split("\\s+");
        Stack<Integer> stack = new Stack<>(); // 存储内存位置
        
        System.out.println("生成SML指令过程:");
        System.out.println("初始栈: " + stackToString(stack));
        System.out.println("表达式: " + postfixExpression);
        
        for (String token : tokens) {
            System.out.println("\n当前token: '" + token + "'");
            
            if (isOperator(token)) {
                // 处理操作符
                if (stack.size() < 2) {
                    throw new RuntimeException("操作符 '" + token + "' 处栈中元素不足");
                }
                
                int rightOperandLocation = stack.pop();
                int leftOperandLocation = stack.pop();
                
                // 生成加载左操作数的指令
                generateInstruction(20, leftOperandLocation, "加载左操作数");
                
                // 生成运算指令
                char operator = token.charAt(0);
                int opcode = 0;
                switch (operator) {
                    case '+': opcode = 30; break;
                    case '-': opcode = 31; break;
                    case '*': opcode = 33; break;
                    case '/': opcode = 32; break;
                    default: throw new RuntimeException("未知操作符: " + operator);
                }
                
                generateInstruction(opcode, rightOperandLocation, "执行运算: " + operator);
                
                // 分配临时内存位置存储结果
                int tempLocation = allocateTempLocation();
                generateInstruction(21, tempLocation, "存储结果到临时位置");
                
                // 将临时位置压入栈
                stack.push(tempLocation);
                System.out.println("临时位置 " + tempLocation + " 压入栈");
                System.out.println("栈状态: " + stackToString(stack));
            } else {
                // 处理操作数（数字或变量）
                int location;
                
                if (isNumeric(token)) {
                    // 数字常量
                    int value = Integer.parseInt(token);
                    String key = "C" + value; // 常量键
                    
                    if (symbolTable.containsKey(key)) {
                        location = symbolTable.get(key).location;
                    } else {
                        location = dataCounter--;
                        symbolTable.put(key, new TableEntry(value, 'C', location));
                        System.out.println("常量 " + value + " 分配到位置 " + location);
                    }
                } else if (isVariable(token)) {
                    // 变量
                    String varName = token;
                    
                    if (symbolTable.containsKey(varName)) {
                        location = symbolTable.get(varName).location;
                    } else {
                        location = dataCounter--;
                        symbolTable.put(varName, new TableEntry(varName.charAt(0), 'V', location));
                        System.out.println("变量 " + varName + " 分配到位置 " + location);
                    }
                } else {
                    throw new RuntimeException("无效的token: " + token);
                }
                
                stack.push(location);
                System.out.println("操作数位置 " + location + " 压入栈");
                System.out.println("栈状态: " + stackToString(stack));
            }
        }
        
        if (stack.size() != 1) {
            throw new RuntimeException("表达式求值完成后栈中元素数量不正确");
        }
        
        return stack.pop();
    }
    
    /**
     * 生成SML指令
     * @param opcode 操作码
     * @param operand 操作数
     * @param description 指令描述
     */
    private void generateInstruction(int opcode, int operand, String description) {
        if (instructionCounter >= sml.length) {
            throw new RuntimeException("SML内存已满");
        }
        
        int instruction = opcode * 100 + operand;
        sml[instructionCounter] = instruction;
        
        String instructionStr = String.format("%02d: +%04d   // %s", 
            instructionCounter, instruction, description);
        generatedInstructions.add(instructionStr);
        
        System.out.println("生成指令: " + instructionStr);
        instructionCounter++;
    }
    
    /**
     * 分配临时内存位置
     * @return 临时内存位置
     */
    private int allocateTempLocation() {
        if (dataCounter < 0) {
            throw new RuntimeException("数据内存已满");
        }
        
        int tempLocation = dataCounter--;
        System.out.println("分配临时位置: " + tempLocation);
        return tempLocation;
    }
    
    /**
     * 检查token是否为操作符
     * @param token 要检查的token
     * @return 如果是操作符返回true，否则返回false
     */
    private boolean isOperator(String token) {
        return token.length() == 1 && "+-*/".contains(token);
    }
    
    /**
     * 检查token是否为数字
     * @param token 要检查的token
     * @return 如果是数字返回true，否则返回false
     */
    private boolean isNumeric(String token) {
        try {
            Integer.parseInt(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 检查token是否为有效的变量名（单字母）
     * @param token 要检查的token
     * @return 如果是有效的变量名返回true，否则返回false
     */
    private boolean isVariable(String token) {
        return token.length() == 1 && Character.isLetter(token.charAt(0));
    }
    
    /**
     * 将栈转换为字符串表示
     * @param stack 要转换的栈
     * @return 栈的字符串表示
     */
    private String stackToString(Stack<Integer> stack) {
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
    
    /**
     * 获取生成的SML指令
     * @return SML指令列表
     */
    public List<String> getGeneratedInstructions() {
        return generatedInstructions;
    }
    
    /**
     * 获取符号表
     * @return 符号表
     */
    public Map<String, TableEntry> getSymbolTable() {
        return symbolTable;
    }
    
    /**
     * 获取SML指令数组
     * @return SML指令数组
     */
    public int[] getSml() {
        return sml;
    }
    
    /**
     * 主方法 - 测试程序
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.print("请输入后缀表达式（支持多位整数和单字母变量，空格分隔，输入'quit'退出）: ");
            String postfixExpression = scanner.nextLine().trim();
            
            if (postfixExpression.equalsIgnoreCase("quit")) {
                System.out.println("程序已退出。");
                break;
            }
            
            try {
                PostfixEvaluator evaluator = new PostfixEvaluator();
                int resultLocation = evaluator.generateSMLFromPostfix(postfixExpression);
                
                System.out.println("\n*** 生成完成 ***");
                System.out.println("表达式结果存储在位置: " + resultLocation);
                
                System.out.println("\n生成的SML指令:");
                for (String instruction : evaluator.getGeneratedInstructions()) {
                    System.out.println(instruction);
                }
                
                System.out.println("\n符号表:");
                for (Map.Entry<String, TableEntry> entry : evaluator.getSymbolTable().entrySet()) {
                    TableEntry te = entry.getValue();
                    System.out.printf("符号: %s, 类型: %c, 位置: %d\n", 
                        entry.getKey(), te.type, te.location);
                }
            } catch (Exception e) {
                System.out.println("处理过程中发生错误: " + e.getMessage());
            }
            
            System.out.println(); // 空行分隔
        }
        
        scanner.close();
    }
}