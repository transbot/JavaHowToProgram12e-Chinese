import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * TableEntry类，表示符号表中的条目
 */
class TableEntry {
    int symbol;    // 符号的整数值
    char type;     // 类型：'C'常量，'L'行号，'V'变量
    int location;  // 内存位置
    
    TableEntry(int symbol, char type, int location) {
        this.symbol = symbol;
        this.type = type;
        this.location = location;
    }
    
    @Override
    public String toString() {
        return String.format("符号: %d, 类型: %c, 位置: %d", symbol, type, location);
    }
}

/**
 * PostfixEvaluator类，用于评估后缀表达式
 */
class PostfixEvaluator {
    
    /**
     * 评估后缀表达式
     * @param postfixExpression 后缀表达式
     * @param symbolTable 符号表，用于查找变量和常量的值
     * @return 表达式的计算结果
     */
    public static int evaluate(String postfixExpression, Map<String, TableEntry> symbolTable) {
        String[] tokens = postfixExpression.split("\\s+");
        Stack<Integer> stack = new Stack<>();
        
        for (String token : tokens) {
            if (isOperator(token)) {
                // 处理操作符
                if (stack.size() < 2) {
                    throw new RuntimeException("操作符 '" + token + "' 处栈中元素不足");
                }
                
                int rightOperand = stack.pop();
                int leftOperand = stack.pop();
                
                int result = 0;
                char operator = token.charAt(0);
                switch (operator) {
                    case '+': result = leftOperand + rightOperand; break;
                    case '-': result = leftOperand - rightOperand; break;
                    case '*': result = leftOperand * rightOperand; break;
                    case '/': 
                        if (rightOperand == 0) {
                            throw new RuntimeException("除数不能为零");
                        }
                        result = leftOperand / rightOperand;
                        break;
                    default: throw new RuntimeException("未知操作符: " + operator);
                }
                
                stack.push(result);
            } else if (isNumeric(token)) {
                // 数字常量
                stack.push(Integer.parseInt(token));
            } else if (isVariable(token) && symbolTable.containsKey(token)) {
                // 变量，从符号表获取值
                TableEntry entry = symbolTable.get(token);
                stack.push(entry.symbol);
            } else {
                throw new RuntimeException("无效的token: " + token);
            }
        }
        
        if (stack.size() != 1) {
            throw new RuntimeException("表达式求值完成后栈中元素数量不正确");
        }
        
        return stack.pop();
    }
    
    /**
     * 检查token是否为操作符
     */
    private static boolean isOperator(String token) {
        return token.length() == 1 && "+-*/".contains(token);
    }
    
    /**
     * 检查token是否为数字
     */
    private static boolean isNumeric(String token) {
        try {
            Integer.parseInt(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 检查token是否为有效的变量名（单字母）
     */
    private static boolean isVariable(String token) {
        return token.length() == 1 && Character.isLetter(token.charAt(0));
    }
}

/**
 * Simple语言编译器
 * 将Simple语言程序编译成Simpletron机器语言程序
 */
public class SimpleCompiler {
    
    // SML指令数组
    private int[] sml;
    // 指令计数器
    private int instructionCounter;
    // 符号表
    private Map<String, TableEntry> symbolTable;
    // 标志数组，用于记录需要第二次扫描修复的指令
    private int[] flags;
    // 当前处理的行号
    private int currentLineNumber;
    // 生成的SML指令列表
    private List<String> generatedInstructions;
    // 下一个可用内存位置（从99开始向下分配）
    private int nextAvailableLocation;
    // 已使用的内存位置集合
    private Set<Integer> usedMemoryLocations;
    
    /**
     * 构造函数
     */
    public SimpleCompiler() {
        sml = new int[100];
        instructionCounter = 0;
        symbolTable = new HashMap<>();
        flags = new int[100];
        Arrays.fill(flags, -1);
        currentLineNumber = 0;
        generatedInstructions = new ArrayList<>();
        nextAvailableLocation = 99;  // 从99开始向下分配
        usedMemoryLocations = new HashSet<>();
    }
    
    /**
     * 执行第一次扫描
     * @param filename Simple程序文件名
     * @return 如果扫描成功返回true，否则返回false
     */
    public boolean firstPass(String filename) {
        try (Scanner fileScanner = new Scanner(new File(filename))) {
            System.out.println("开始第一次扫描...");
            
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty()) continue;
                
                // 解析行号和命令
                String[] tokens = line.split("\\s+", 3);
                if (tokens.length < 2) {
                    System.out.println("无效的行: " + line);
                    continue;
                }
                
                try {
                    currentLineNumber = Integer.parseInt(tokens[0]);
                    String command = tokens[1].toLowerCase();
                    String argument = (tokens.length > 2) ? tokens[2] : "";
                    
                    // 将行号添加到符号表
                    addToSymbolTable(String.valueOf(currentLineNumber), 'L', instructionCounter);
                    
                    // 处理不同的命令
                    switch (command) {
                        case "rem":
                            // 注释，不生成代码
                            break;
                        case "input":
                            handleInput(argument);
                            break;
                        case "print":
                            handlePrint(argument);
                            break;
                        case "let":
                            handleLet(argument);
                            break;
                        case "goto":
                            handleGoto(argument);
                            break;
                        case "if":
                            handleIf(argument);
                            break;
                        case "end":
                            handleEnd();
                            break;
                        default:
                            System.out.println("未知命令: " + command);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("无效的行号: " + tokens[0]);
                } catch (Exception e) {
                    System.out.println("处理行时发生错误: " + line);
                    e.printStackTrace();
                }
            }
            
            System.out.println("第一次扫描完成");
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("文件未找到: " + filename);
            return false;
        }
    }
    
    /**
     * 执行第二次扫描
     */
    public void secondPass() {
        System.out.println("开始第二次扫描...");
        
        for (int i = 0; i < flags.length; i++) {
            if (flags[i] != -1) {
                // 查找符号表中的行号
                String lineNumber = String.valueOf(flags[i]);
                if (symbolTable.containsKey(lineNumber)) {
                    TableEntry entry = symbolTable.get(lineNumber);
                    // 修复指令
                    int instruction = sml[i];
                    int opcode = instruction / 100;
                    int newInstruction = opcode * 100 + entry.location;
                    sml[i] = newInstruction;
                    
                    // 更新生成的指令列表
                    if (i < generatedInstructions.size()) {
                        String oldInstruction = generatedInstructions.get(i);
                        String newInstructionStr = String.format("%02d %+05d   // 修复的指令（跳转到行号%s）", 
                                i, newInstruction, lineNumber);
                        generatedInstructions.set(i, newInstructionStr);
                    }
                    
                    System.out.println("修复指令 " + i + ": 跳转到行号 " + lineNumber + " (位置 " + entry.location + ")");
                } else {
                    System.out.println("错误: 未找到行号 " + flags[i] + " 的符号表条目");
                }
            }
        }
        
        System.out.println("第二次扫描完成");
    }
    
    /**
     * 处理input命令
     * @param argument 参数（变量名）
     */
    private void handleInput(String argument) {
        // 获取或创建变量的内存位置
        int variableLocation = getOrCreateVariableLocation(argument);
        
        // 生成READ指令
        generateInstruction(10, variableLocation, "读取 " + argument);
    }
    
    /**
     * 处理print命令
     * @param argument 参数（变量名）
     */
    private void handlePrint(String argument) {
        // 获取或创建变量的内存位置
        int variableLocation = getOrCreateVariableLocation(argument);
        
        // 生成WRITE指令
        generateInstruction(11, variableLocation, "打印 " + argument);
    }
    
    /**
     * 处理let命令
     * @param argument 参数（赋值表达式）
     */
    private void handleLet(String argument) {
        // 解析赋值语句：变量 = 表达式
        String[] parts = argument.split("=", 2);
        if (parts.length != 2) {
            System.out.println("无效的赋值语句: " + argument);
            return;
        }
        
        String variable = parts[0].trim();
        String expression = parts[1].trim();
        
        // 获取或创建变量的内存位置
        int variableLocation = getOrCreateVariableLocation(variable);
        
        // 处理表达式
        processExpression(expression, variableLocation);
    }
    
    /**
     * 处理表达式
     * @param expression 表达式
     * @param targetLocation 目标位置（存储结果的位置）
     */
    private void processExpression(String expression, int targetLocation) {
        // 将中缀表达式转换为后缀表达式
        String postfixExpression = InfixToPostfixConverter.convertToPostfix(expression);
        System.out.println("中缀表达式: " + expression + " -> 后缀表达式: " + postfixExpression);
        
        // 从后缀表达式生成SML指令
        generateSMLFromPostfix(postfixExpression, targetLocation);
    }
    
    /**
     * 从后缀表达式生成SML指令
     * @param postfixExpression 后缀表达式
     * @param targetLocation 结果存储的内存位置
     */
    private void generateSMLFromPostfix(String postfixExpression, int targetLocation) {
        // 分割表达式为token
        String[] tokens = postfixExpression.split("\\s+");
        Stack<Integer> stack = new Stack<>(); // 存储内存位置
        
        for (String token : tokens) {
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
                int tempLocation = allocateMemory(true);
                generateInstruction(21, tempLocation, "存储结果到临时位置");
                
                // 将临时位置压入栈
                stack.push(tempLocation);
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
                        location = allocateMemory(false);
                        symbolTable.put(key, new TableEntry(value, 'C', location));
                        // 将常量值存储到内存中
                        sml[location] = value;
                        System.out.println("常量 " + value + " 分配到位置 " + location);
                    }
                } else if (isVariable(token)) {
                    // 变量
                    String varName = token;
                    
                    // 获取或创建变量的内存位置
                    location = getOrCreateVariableLocation(varName);
                } else {
                    throw new RuntimeException("无效的token: " + token);
                }
                
                stack.push(location);
            }
        }
        
        if (stack.size() != 1) {
            throw new RuntimeException("表达式求值完成后栈中元素数量不正确");
        }
        
        // 将结果存储到目标位置
        int resultLocation = stack.pop();
        if (resultLocation != targetLocation) {
            generateInstruction(20, resultLocation, "加载结果");
            generateInstruction(21, targetLocation, "存储到目标位置");
        }
    }
    
    /**
     * 处理goto命令
     * @param argument 参数（目标行号）
     */
    private void handleGoto(String argument) {
        try {
            int targetLine = Integer.parseInt(argument);
            
            // 检查目标行号是否在符号表中
            if (symbolTable.containsKey(argument)) {
                TableEntry targetEntry = symbolTable.get(argument);
                generateInstruction(40, targetEntry.location, "跳转到行号 " + argument);
            } else {
                // 前向引用，标记需要第二次扫描修复
                generateInstruction(40, 0, "跳转到行号 " + argument + " (待修复)");
                flags[instructionCounter - 1] = targetLine;
            }
        } catch (NumberFormatException e) {
            System.out.println("无效的行号: " + argument);
        }
    }
    
    /**
     * 处理if命令
     * @param argument 参数（条件表达式和目标行号）
     */
    private void handleIf(String argument) {
        // 解析if语句：条件 goto 行号
        String[] parts = argument.split("goto", 2);
        if (parts.length != 2) {
            System.out.println("无效的if语句: " + argument);
            return;
        }
        
        String condition = parts[0].trim();
        String targetLineStr = parts[1].trim();
        
        try {
            int targetLine = Integer.parseInt(targetLineStr);
            
            // 解析条件表达式
            String[] conditionParts = condition.split("(==|!=|<=|>=|<|>)");
            if (conditionParts.length != 2) {
                System.out.println("无效的条件表达式: " + condition);
                return;
            }
            
            String leftOperand = conditionParts[0].trim();
            String rightOperand = conditionParts[1].trim();
            
            // 提取比较操作符
            Pattern pattern = Pattern.compile("(==|!=|<=|>=|<|>)");
            Matcher matcher = pattern.matcher(condition);
            String operator = "";
            if (matcher.find()) {
                operator = matcher.group();
            }
            
            // 获取左操作数的位置
            int leftLocation = getOperandLocation(leftOperand);
            
            // 获取右操作数的位置
            int rightLocation = getOperandLocation(rightOperand);
            
            // 生成比较指令
            generateInstruction(20, leftLocation, "加载 " + leftOperand);
            generateInstruction(31, rightLocation, "减去 " + rightOperand);
            
            // 根据操作符生成条件分支指令
            int branchInstruction = 0;
            switch (operator) {
                case "==":
                    branchInstruction = 42; // BRANCHZERO
                    break;
                case "!=":
                    // 不等于需要两条指令：如果不为零则跳转
                    generateInstruction(42, instructionCounter + 2, "如果为零跳过下一条指令");
                    branchInstruction = 40; // BRANCH
                    break;
                case "<":
                    branchInstruction = 41; // BRANCHNEG
                    break;
                case "<=":
                    // 小于等于需要两条指令：如果为负或为零则跳转
                    generateInstruction(41, instructionCounter + 2, "如果为负跳过下一条指令");
                    branchInstruction = 42; // BRANCHZERO
                    break;
                case ">":
                    // 大于需要两条指令：如果不为负且不为零则跳转
                    generateInstruction(41, instructionCounter + 3, "如果为负跳过跳转");
                    generateInstruction(42, instructionCounter + 2, "如果为零跳过跳转");
                    branchInstruction = 40; // BRANCH
                    break;
                case ">=":
                    // 大于等于需要一条指令：如果不为负则跳转
                    generateInstruction(41, instructionCounter + 2, "如果为负跳过下一条指令");
                    branchInstruction = 40; // BRANCH
                    break;
                default:
                    System.out.println("未知的操作符: " + operator);
                    return;
            }
            
            // 检查目标行号是否在符号表中
            if (symbolTable.containsKey(targetLineStr)) {
                TableEntry targetEntry = symbolTable.get(targetLineStr);
                generateInstruction(branchInstruction, targetEntry.location, "条件跳转到行号 " + targetLineStr);
            } else {
                // 前向引用，标记需要第二次扫描修复
                generateInstruction(branchInstruction, 0, "条件跳转到行号 " + targetLineStr + " (待修复)");
                flags[instructionCounter - 1] = targetLine;
            }
        } catch (NumberFormatException e) {
            System.out.println("无效的行号: " + targetLineStr);
        }
    }
    
    /**
     * 处理end命令
     */
    private void handleEnd() {
        generateInstruction(43, 0, "终止程序");
    }
    
    /**
     * 获取操作数的内存位置
     * @param operand 操作数（变量名或常量）
     * @return 内存位置，如果未找到返回-1
     */
    private int getOperandLocation(String operand) {
        // 检查是否是数字常量
        try {
            int value = Integer.parseInt(operand);
            String key = "C" + value;
            
            if (!symbolTable.containsKey(key)) {
                int location = allocateMemory(false);
                addToSymbolTable(key, 'C', location);
                // 将常量值存储到内存中
                sml[location] = value;
                System.out.println("常量 " + value + " 分配到位置 " + location);
            }
            return symbolTable.get(key).location;
        } catch (NumberFormatException e) {
            // 不是数字，应该是变量
            if (symbolTable.containsKey(operand)) {
                return symbolTable.get(operand).location;
            } else {
                // 变量未定义，创建并初始化为0
                return getOrCreateVariableLocation(operand);
            }
        }
    }
    
    /**
     * 获取或创建变量的内存位置
     * @param variableName 变量名
     * @return 变量的内存位置
     */
    private int getOrCreateVariableLocation(String variableName) {
        if (symbolTable.containsKey(variableName)) {
            return symbolTable.get(variableName).location;
        } else {
            // 首次遇到变量，分配内存位置并初始化为0
            int location = allocateMemory(false);
            addToSymbolTable(variableName, 'V', location);
            
            // 在SML内存中初始化为0
            sml[location] = 0;
            System.out.println("变量 " + variableName + " 初始化为0，分配到位置 " + location);
            
            return location;
        }
    }
    
    /**
     * 分配内存位置
     * @param isTemporary 是否为临时变量
     * @return 分配的内存位置
     */
    private int allocateMemory(boolean isTemporary) {
        // 从最高可用位置开始向下查找可用位置
        for (int i = nextAvailableLocation; i >= 0; i--) {
            if (!usedMemoryLocations.contains(i)) {
                usedMemoryLocations.add(i);
                nextAvailableLocation = i - 1; // 更新下一个可用位置
                
                if (isTemporary) {
                    System.out.println("分配临时位置: " + i);
                } else {
                    System.out.println("分配内存位置: " + i);
                }
                
                return i;
            }
        }
        
        throw new RuntimeException("内存已满");
    }
    
    /**
     * 添加条目到符号表
     * @param symbol 符号
     * @param type 类型
     * @param location 内存位置
     */
    private void addToSymbolTable(String symbol, char type, int location) {
        int symbolValue;
        if (type == 'C') {
            // 常量，提取数值
            symbolValue = Integer.parseInt(symbol.substring(1));
        } else if (type == 'L') {
            // 行号
            symbolValue = Integer.parseInt(symbol);
        } else {
            // 变量
            symbolValue = symbol.charAt(0);
        }
        
        symbolTable.put(symbol, new TableEntry(symbolValue, type, location));
        System.out.println("添加到符号表: " + symbol + " -> " + type + " at " + location);
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
        
        String instructionStr = String.format("%02d %+05d   // %s", 
                instructionCounter, instruction, description);
        generatedInstructions.add(instructionStr);
        
        System.out.println("生成指令: " + instructionStr);
        instructionCounter++;
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
     * 检查token是否为有效的变量名（以字母开头，后面可以跟字母或数字）
     * @param token 要检查的token
     * @return 如果是有效的变量名返回true，否则返回false
     */
    private boolean isVariable(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        // 变量名必须以字母开头
        if (!Character.isLetter(token.charAt(0))) {
            return false;
        }
        // 变量名其余部分可以是字母或数字
        for (int i = 1; i < token.length(); i++) {
            if (!Character.isLetterOrDigit(token.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 将SML程序写入文件
     * @param filename 输出文件名
     */
    public void writeSMLFile(String filename) {
        try (PrintWriter writer = new PrintWriter(filename)) {
            // 写入所有使用过的内存位置，包括指令和常量
            int maxUsedLocation = -1;
            for (int loc : usedMemoryLocations) {
                if (loc > maxUsedLocation) {
                    maxUsedLocation = loc;
                }
            }
            
            // 确保至少写入到指令计数器的位置
            maxUsedLocation = Math.max(maxUsedLocation, instructionCounter - 1);
            
            for (int i = 0; i <= maxUsedLocation; i++) {
                writer.printf("%02d %+05d%n", i, sml[i]);
            }                        
            System.out.println("SML程序已写入文件: " + filename);
        } catch (FileNotFoundException e) {
            System.out.println("无法创建输出文件: " + filename);
        }
    }
    
    /**
     * 显示生成的SML指令
     */
    public void displaySMLInstructions() {
        System.out.println("\n生成的SML指令:");
        for (String instruction : generatedInstructions) {
            System.out.println(instruction);
        }
    }
    
    /**
     * 显示符号表
     */
    public void displaySymbolTable() {
        System.out.println("\n符号表:");
        for (Map.Entry<String, TableEntry> entry : symbolTable.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
    
    /**
     * 程序主入口
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("使用方法: java SimpleCompiler <Simple程序文件>");
            return;
        }
        
        String inputFilename = args[0];
        String outputFilename = inputFilename.replaceFirst("\\.[^.]+$", "") + ".simpletron";
        
        SimpleCompiler compiler = new SimpleCompiler();
        
        // 第一次扫描
        if (compiler.firstPass(inputFilename)) {
            // 第二次扫描
            compiler.secondPass();
            
            // 显示结果
            compiler.displaySymbolTable();
            compiler.displaySMLInstructions();
            
            // 写入SML文件
            compiler.writeSMLFile(outputFilename);
            
            System.out.println("编译完成，输出文件: " + outputFilename);
        }
    }
}

/**
 * 中缀转后缀转换器（修改版）
 * 支持多位整数操作数和多字母变量名操作数
 */
class InfixToPostfixConverter {
    
    /**
     * 将中缀表达式转换为后缀表达式
     * @param infix 中缀表达式
     * @return 后缀表达式
     */
    public static String convertToPostfix(String infix) {
        // 移除所有空格
        infix = infix.replaceAll("\\s+", "");
        
        // 创建栈和结果字符串
        Stack<Character> stack = new Stack<>();
        StringBuilder postfix = new StringBuilder();
        
        // 在表达式末尾添加右括号，并在栈中压入左括号
        infix += ")";
        stack.push('(');
        
        // 遍历表达式中的每个字符
        int i = 0;
        while (i < infix.length()) {
            char currentChar = infix.charAt(i);
            
            if (Character.isDigit(currentChar)) {
                // 如果是数字，读取完整的数字
                int start = i;
                while (i < infix.length() && Character.isDigit(infix.charAt(i))) {
                    i++;
                }
                String number = infix.substring(start, i);
                postfix.append(number).append(" ");
                continue; // 跳过i++，因为我们已经移动了i
            } else if (Character.isLetter(currentChar)) {
                // 如果是字母，读取完整的变量名（多字母支持）
                int start = i;
                while (i < infix.length() && (Character.isLetterOrDigit(infix.charAt(i)) || infix.charAt(i) == '_')) {
                    i++;
                }
                String variable = infix.substring(start, i);
                postfix.append(variable).append(" ");
                continue; // 跳过i++，因为我们已经移动了i
            } else if (currentChar == '(') {
                // 如果是左括号，压入栈
                stack.push(currentChar);
            } else if (isBinaryOperator(currentChar)) {
                // 如果是操作符，处理栈中优先级较高或相等的操作符
                while (!stack.isEmpty() && isBinaryOperator(stack.peek()) && 
                       precedence(stack.peek(), currentChar)) {
                    char poppedOperator = stack.pop();
                    postfix.append(poppedOperator).append(" ");
                }
                stack.push(currentChar);
            } else if (currentChar == ')') {
                // 如果是右括号，弹出栈中元素直到遇到左括号
                while (!stack.isEmpty() && stack.peek() != '(') {
                    char poppedOperator = stack.pop();
                    postfix.append(poppedOperator).append(" ");
                }
                // 弹出左括号
                if (!stack.isEmpty() && stack.peek() == '(') {
                    stack.pop();
                }
            }
            
            i++; // 移动到下一个字符
        }
        
        // 弹出栈中剩余的所有操作符
        while (!stack.isEmpty() && stack.peek() != '(') {
            char poppedOperator = stack.pop();
            postfix.append(poppedOperator).append(" ");
        }
        
        return postfix.toString().trim();
    }
    
    /**
     * 检查字符是否为支持的二元操作符
     */
    private static boolean isBinaryOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }
    
    /**
     * 比较两个操作符的优先级
     */
    private static boolean precedence(char operator1, char operator2) {
        int prec1 = getPrecedence(operator1);
        int prec2 = getPrecedence(operator2);
        return prec1 >= prec2;
    }
    
    /**
     * 获取操作符的优先级值
     */
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