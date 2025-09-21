// 第12章练习（Simple语言编译器），练习6.20的修改版，Simpletron.java
// 模拟Simpletron虚拟机。
// 新增功能：从指定的.txt文件加载SML程序，并在程序结束后输出结果到output.txt。

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模拟Simpletron虚拟机。
 * 修改版：从指定的.txt文件加载SML程序，并在程序结束后输出结果到output.txt。
 * 支持行号和代码之间的问号和空格分隔符。
 */
public class Simpletron {

    // --- 寄存器 ---
    private int accumulator;
    private int instructionCounter;
    private int instructionRegister;
    private int operationCode;
    private int operand;

    // --- 内存 ---
    private final int MEMORY_SIZE = 100;
    private int[] memory;

    // --- SML 操作码常量 ---
    private static final int READ = 10;
    private static final int WRITE = 11;
    private static final int LOAD = 20;
    private static final int STORE = 21;
    private static final int ADD = 30;
    private static final int SUBTRACT = 31;
    private static final int DIVIDE = 32;
    private static final int MULTIPLY = 33;
    private static final int BRANCH = 40;
    private static final int BRANCHNEG = 41;
    private static final int BRANCHZERO = 42;
    private static final int HALT = 43;

    // --- 用于输出到文件 ---
    private PrintWriter fileWriter;
    private StringBuilder outputContent;

    /**
     * 构造函数
     */
    public Simpletron() {
        memory = new int[MEMORY_SIZE];
        // 初始化寄存器
        accumulator = 0;
        instructionCounter = 0;
        instructionRegister = 0;
        operationCode = 0;
        operand = 0;
        outputContent = new StringBuilder();
    }

    /**
     * 运行Simpletron模拟器的主方法。
     * @param filename 包含SML程序的.txt文件名
     */
    public void run(String filename) {
        displayWelcomeMessage();
        if (loadProgramFromFile(filename)) {
             executeProgram();
        }
        writeOutputToFile();
    }

    /**
     * 显示欢迎信息。
     */
    private void displayWelcomeMessage() {
        String message = "*** 欢迎来到Simpletron！***\n";
        System.out.print(message);
        outputContent.append(message);
    }

    /**
     * 从指定的.txt文件加载SML程序到内存中。
     * @param filename SML程序文件名
     * @return 如果加载成功返回 true, 否则返回 false
     */
    private boolean loadProgramFromFile(String filename) {
        String message = String.format("*** 正在从 %s 加载程序... ***\n", filename);
        System.out.print(message);
        outputContent.append(message);

        File file = new File(filename);
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("//")) {
                    continue; // 跳过空行和注释行
                }

                // 使用正则表达式匹配行号和指令
                // 支持两种格式：
                // 1. 数字 问号 指令 (例如: "00 ? +1099")
                // 2. 数字 空格 指令 (例如: "00 +1099")
                Pattern pattern = Pattern.compile("^(\\d+)\\s*[?]?\\s*([+-]?\\d+)\\s*(//.*)?$");
                Matcher matcher = pattern.matcher(line);
                
                if (matcher.find()) {
                    // 提取地址
                    String addressStr = matcher.group(1);
                    int address = Integer.parseInt(addressStr);

                    // 提取指令字符串
                    String instructionStr = matcher.group(2);
                    int instruction = Integer.parseInt(instructionStr);
                    
                    // 检查哨兵值
                    if (instruction == -99999) {
                        break;
                    }
                    
                    // 验证地址和指令范围
                    if (address < 0 || address >= MEMORY_SIZE) {
                         message = String.format("错误: 文件中的地址 %d 超出范围。\n", address);
                         System.out.print(message);
                         outputContent.append(message);
                         return false;
                    }
                    if (instruction < -9999 || instruction > 9999) {
                        message = String.format("错误: 地址 %02d 处的指令 %d 超出范围。\n", address, instruction);
                        System.out.print(message);
                        outputContent.append(message);
                        return false;
                    }

                    memory[address] = instruction;
                } else {
                    message = String.format("警告: 无法解析行: %s\n", line);
                    System.out.print(message);
                    outputContent.append(message);
                }
            }

            message = "\n*** 程序加载完成 ***\n";
            System.out.print(message);
            outputContent.append(message);
            message = "*** 程序开始执行 ***\n\n";
            System.out.print(message);
            outputContent.append(message);
            return true;

        } catch (FileNotFoundException e) {
            message = "错误: 文件未找到 - " + filename + "\n";
            System.err.print(message);
            outputContent.append(message);
            return false;
        } catch (NumberFormatException e) {
            message = "错误: 文件格式错误，无法解析地址或指令。\n";
            System.err.print(message);
            outputContent.append(message);
            return false;
        }
    }


    /**
     * 执行已加载到内存中的SML程序。
     */
    private void executeProgram() {
        Scanner scanner = new Scanner(System.in);
        boolean halted = false;

        while (!halted && instructionCounter < MEMORY_SIZE) {
            instructionRegister = memory[instructionCounter];
            operationCode = instructionRegister / 100;
            operand = instructionRegister % 100;

            boolean shouldIncrementCounter = true;
            switch (operationCode) {
                case READ:
                    System.out.print("请输入一个整数: ");
                    System.out.flush();
                    outputContent.append("请输入一个整数: ");
                    memory[operand] = scanner.nextInt();
                    break;
                case WRITE:
                    String outputMessage = String.format("输出: %+05d\n", memory[operand]);
                    System.out.print(outputMessage);
                    outputContent.append(outputMessage);
                    break;
                case LOAD:
                    accumulator = memory[operand];
                    break;
                case STORE:
                    memory[operand] = accumulator;
                    break;
                case ADD:
                    accumulator += memory[operand];
                    if (isOverflow()) { handleFatalError("*** 累加器溢出 ***"); return; }
                    break;
                case SUBTRACT:
                    accumulator -= memory[operand];
                    if (isOverflow()) { handleFatalError("*** 累加器溢出 ***"); return; }
                    break;
                case DIVIDE:
                    if (memory[operand] == 0) { handleFatalError("*** 尝试除以零 ***"); return; }
                    accumulator /= memory[operand];
                    break;
                case MULTIPLY:
                    accumulator *= memory[operand];
                    if (isOverflow()) { handleFatalError("*** 累加器溢出 ***"); return; }
                    break;
                case BRANCH:
                    instructionCounter = operand;
                    shouldIncrementCounter = false;
                    break;
                case BRANCHNEG:
                    if (accumulator < 0) {
                        instructionCounter = operand;
                        shouldIncrementCounter = false;
                    }
                    break;
                case BRANCHZERO:
                    if (accumulator == 0) {
                        instructionCounter = operand;
                        shouldIncrementCounter = false;
                    }
                    break;
                case HALT:
                    halted = true;
                    outputMessage = "\n*** Simpletron执行终止 ***\n";
                    System.out.print(outputMessage);
                    outputContent.append(outputMessage);
                    break;
                default:
                     handleFatalError(String.format("*** 无效操作码: %02d ***", operationCode));
                     return;
            }
            
            if (shouldIncrementCounter && !halted) {
                instructionCounter++;
            }
        }
        
        if (halted) {
             dump();
        }
    }
    
    private boolean isOverflow() {
        return accumulator < -9999 || accumulator > 9999;
    }
    
    private void handleFatalError(String message) {
        System.out.println("\n" + message);
        outputContent.append("\n").append(message).append("\n");
        message = "*** Simpletron执行异常终止 ***\n";
        System.out.print(message);
        outputContent.append(message);
        dump();
    }

    /**
     * 显示所有寄存器和内存的内容。
     */
    public void dump() {
        StringBuilder dumpOutput = new StringBuilder();
        dumpOutput.append("\n寄存器:\n");
        dumpOutput.append(String.format("累加器:             %+05d\n", accumulator));
        dumpOutput.append(String.format("指令计数器:         %02d\n", instructionCounter));
        dumpOutput.append(String.format("指令寄存器:         %+05d\n", instructionRegister));
        dumpOutput.append(String.format("操作码:             %02d\n", operationCode));
        dumpOutput.append(String.format("操作数:             %02d\n", operand));

        dumpOutput.append("\n内存:\n");
        dumpOutput.append("   ");
        for (int i = 0; i < 10; i++) dumpOutput.append(String.format("    %1d ", i));
        dumpOutput.append("\n");

        for (int i = 0; i < MEMORY_SIZE; i++) {
            if (i % 10 == 0) dumpOutput.append(String.format("%2d ", i));
            dumpOutput.append(String.format("%+05d ", memory[i]));
            if ((i + 1) % 10 == 0) dumpOutput.append("\n");
        }
        
        System.out.print(dumpOutput.toString());
        outputContent.append(dumpOutput.toString());
    }

    /**
     * 将输出内容写入文件。
     */
    private void writeOutputToFile() {
        try {
            fileWriter = new PrintWriter("output.txt");
            fileWriter.print(outputContent.toString());
            fileWriter.close();
        } catch (FileNotFoundException e) {
            System.err.println("无法创建输出文件: output.txt");
        }
    }

    /**
     * 程序主入口。
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("使用方法: java Simpletron <文件名.txt>");
            return;
        }

        Simpletron simpletron = new Simpletron();
        simpletron.run(args[0]);
    }
}