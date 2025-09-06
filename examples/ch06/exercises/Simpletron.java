import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模拟Simpletron虚拟机。
 * 修改版：从指定的.txt文件加载SML程序。
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
    }

    /**
     * 显示欢迎信息。
     */
    private void displayWelcomeMessage() {
        System.out.println("*** 欢迎来到Simpletron！***");
    }

    /**
     * 从指定的.txt文件加载SML程序到内存中。
     * @param filename SML程序文件名
     * @return 如果加载成功返回 true, 否则返回 false
     */
    private boolean loadProgramFromFile(String filename) {
        System.out.printf("*** 正在从 %s 加载程序... ***\n", filename);

        File file = new File(filename);
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();

                // 寻找 '?' 和 '//'
                int qMarkIndex = line.indexOf('?');
                
                // 如果行无效或没有 '?'，则跳过
                if (qMarkIndex == -1) {
                    continue;
                }

                // 提取地址
                String addressStr = line.substring(0, qMarkIndex).trim();
                int address = Integer.parseInt(addressStr);

                // 提取指令字符串
                String instructionStr;
                int commentIndex = line.indexOf("//");
                if (commentIndex != -1) {
                    instructionStr = line.substring(qMarkIndex + 1, commentIndex).trim();
                } else {
                    instructionStr = line.substring(qMarkIndex + 1).trim();
                }

                // 解析指令
                int instruction = Integer.parseInt(instructionStr);
                
                // 检查哨兵值
                if (instruction == -99999) {
                    break;
                }
                
                // 验证地址和指令范围
                if (address < 0 || address >= MEMORY_SIZE) {
                     System.out.printf("错误: 文件中的地址 %d 超出范围。\n", address);
                     return false;
                }
                if (instruction < -9999 || instruction > 9999) {
                    System.out.printf("错误: 地址 %02d 处的指令 %d 超出范围。\n", address, instruction);
                    return false;
                }

                memory[address] = instruction;
            }

            System.out.println("\n*** 程序加载完成 ***");
            System.out.println("*** 程序开始执行 ***\n");
            return true;

        } catch (FileNotFoundException e) {
            System.err.println("错误: 文件未找到 - " + filename);
            return false;
        } catch (NumberFormatException e) {
            System.err.println("错误: 文件格式错误，无法解析地址或指令。");
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
                    System.out.flush(); // 新增：强制刷新输出流，确保提示立即可见
                    memory[operand] = scanner.nextInt();
                    break;
                case WRITE:
                    System.out.printf("输出: %+05d\n", memory[operand]);
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
                    System.out.println("\n*** Simpletron执行终止 ***");
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
        System.out.println("*** Simpletron执行异常终止 ***");
        dump();
    }

    /**
     * 显示所有寄存器和内存的内容。
     */
    public void dump() {
        System.out.println("\n寄存器:");
        System.out.printf("累加器:             %+05d\n", accumulator);
        System.out.printf("指令计数器:         %02d\n", instructionCounter);
        System.out.printf("指令寄存器:         %+05d\n", instructionRegister);
        System.out.printf("操作码:             %02d\n", operationCode);
        System.out.printf("操作数:             %02d\n", operand);

        System.out.println("\n内存:");
        System.out.print("   ");
        for (int i = 0; i < 10; i++) System.out.printf("    %1d ", i);
        System.out.println();

        for (int i = 0; i < MEMORY_SIZE; i++) {
            if (i % 10 == 0) System.out.printf("%2d ", i);
            System.out.printf("%+05d ", memory[i]);
            if ((i + 1) % 10 == 0) System.out.println();
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


