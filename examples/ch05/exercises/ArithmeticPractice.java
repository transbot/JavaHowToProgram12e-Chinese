// 练习5.26~5.30，ArithmeticPractice.java
// 开发一个算术练习程序，帮助小学生练习加、减、乘、除四则运算。
// 1. 允许用户选择难度级别（1-3），决定数字的最大位数。
// 2. 允许用户选择题目类型（1-5），分别为加法、减法、乘法、除法和混合类型。
// 3. 生成10道题目，用户答对后显示随机的积极反馈，答错后允许重试并显示随机的鼓励性反馈。
// 4. 练习结束后显示正确率，并根据正确率给出相应的建议。
import java.util.Random;
import java.util.Scanner;

public class ArithmeticPractice {
    private static final Random random = new Random();
    private static Scanner scanner = new Scanner(System.in);
    
    // 正确答案的回应
    private static final String[] CORRECT_RESPONSES = {
        "非常好！",
        "太棒了！",
        "做得好！",
        "继续保持！"
    };
    
    // 错误答案的回应
    private static final String[] INCORRECT_RESPONSES = {
        "不对，请重试。",
        "错了，再试一次。",
        "不要放弃！",
        "不对，继续尝试。"
    };
    
    public static void main(String[] args) {
        System.out.println("=== 欢迎使用算术练习程序 ===");
        
        // 循环允许新学生使用程序
        while (true) {
            // 获取难度级别（最大位数）
            int difficulty = getDifficultyLevel();
            
            // 获取题目类型
            int problemType = getProblemType();
            
            // 开始练习
            runPracticeSession(difficulty, problemType);
            
            // 询问是否继续
            System.out.print("是否要开始新的练习？(1-是，0-否): ");
            int continueChoice = scanner.nextInt();
            if (continueChoice != 1) {
                System.out.println("谢谢使用！再见！");
                break;
            }
        }
        
        scanner.close();
    }
    
    // 获取用户选择的难度级别（最大位数）
    private static int getDifficultyLevel() {
        int difficulty;
        do {
            System.out.print("请选择难度级别（数字最大位数）: ");
            System.out.print("1-最大一位数(1-9)，2-最大两位数(1-99)，3-最大三位数(1-999): ");
            difficulty = scanner.nextInt();
            if (difficulty < 1 || difficulty > 3) {
                System.out.println("难度级别必须在1到3之间，请重新输入。");
            }
        } while (difficulty < 1 || difficulty > 3);
        return difficulty;
    }
    
    // 获取用户选择的题目类型
    private static int getProblemType() {
        int type;
        do {
            System.out.println("\n请选择题目类型:");
            System.out.println("1 - 加法");
            System.out.println("2 - 减法");
            System.out.println("3 - 乘法");
            System.out.println("4 - 除法");
            System.out.println("5 - 混合类型");
            System.out.print("请选择 (1-5): ");
            type = scanner.nextInt();
            if (type < 1 || type > 5) {
                System.out.println("请输入1到5之间的数字。");
            }
        } while (type < 1 || type > 5);
        return type;
    }
    
    // 运行一次练习 session
    private static void runPracticeSession(int difficulty, int problemType) {
        int correctCount = 0;
        int totalProblems = 10;
        
        System.out.println("\n--- 开始练习 ---");
        System.out.println("当前难度：数字最大为" + (int)Math.pow(10, difficulty) + "以内（可包含更小位数）");
        
        for (int i = 0; i < totalProblems; i++) {
            // 根据设置生成题目并获取结果
            boolean isCorrect = generateAndCheckProblem(difficulty, problemType);
            if (isCorrect) {
                correctCount++;
            }
        }
        
        // 显示练习结果
        displayResults(correctCount, totalProblems);
    }
    
    // 生成题目并检查答案
    private static boolean generateAndCheckProblem(int difficulty, int problemType) {
        // 根据难度确定数字范围（1到maxNumber，包含更小位数）
        int maxNumber = (int) Math.pow(10, difficulty) - 1; // 1级:9, 2级:99, 3级:999
        
        // 生成两个随机数（1到maxNumber，可包含一位数、两位数、三位数等）
        int num1 = random.nextInt(maxNumber) + 1;
        int num2 = random.nextInt(maxNumber) + 1;
        
        // 确定当前题目类型（如果是混合模式，则随机选择一种类型）
        int currentType = problemType;
        if (problemType == 5) {
            currentType = random.nextInt(4) + 1; // 1-4之间的随机数
        }
        
        // 根据题目类型计算正确答案并显示问题
        int correctAnswer = 0;
        String operator = "";
        
        switch (currentType) {
            case 1: // 加法
                correctAnswer = num1 + num2;
                operator = " + ";
                break;
            case 2: // 减法 (确保结果非负)
                // 确保num1 >= num2，使结果非负
                if (num1 < num2) {
                    int temp = num1;
                    num1 = num2;
                    num2 = temp;
                }
                correctAnswer = num1 - num2;
                operator = " - ";
                break;
            case 3: // 乘法
                correctAnswer = num1 * num2;
                operator = " * ";
                break;
            case 4: // 除法 (确保整除，数字范围1到maxNumber)
                // 确保除数不为0且能整除
                // 重新生成除数num2（1到maxNumber）
                num2 = random.nextInt(maxNumber) + 1;
                // 计算最大可能的商，确保被除数不超过maxNumber
                int maxQuotient = maxNumber / num2;
                // 确保商至少为1
                correctAnswer = maxQuotient > 0 ? random.nextInt(maxQuotient) + 1 : 1;
                // 被除数 = 除数 * 商（保证整除）
                num1 = num2 * correctAnswer;
                operator = " / ";
                break;
        }
        
        // 显示问题并获取用户答案
        int userAnswer;
        while (true) {
            System.out.print("问题: " + num1 + operator + num2 + " = ? ");
            try {
                userAnswer = scanner.nextInt();
                break;
            } catch (java.util.InputMismatchException e) {
                System.out.println("输入错误，请输入一个整数。");
                scanner.next(); // 清除错误输入
            }
        }
        
        // 检查答案并给出反馈
        if (userAnswer == correctAnswer) {
            // 随机选择一个正确回应
            String response = CORRECT_RESPONSES[random.nextInt(CORRECT_RESPONSES.length)];
            System.out.println(response);
            return true;
        } else {
            // 错误时允许重试，直到答对
            while (userAnswer != correctAnswer) {
                // 随机选择一个错误回应
                String response = INCORRECT_RESPONSES[random.nextInt(INCORRECT_RESPONSES.length)];
                System.out.println(response);
                
                System.out.print("再试一次: " + num1 + operator + num2 + " = ? ");
                userAnswer = scanner.nextInt();
            }
            
            // 答对后的反馈
            String response = CORRECT_RESPONSES[random.nextInt(CORRECT_RESPONSES.length)];
            System.out.println(response);
            return false; // 记录为错误（即使最终答对）
        }
    }
    
    // 显示练习结果
    private static void displayResults(int correctCount, int totalProblems) {
        double percentage = (double) correctCount / totalProblems * 100;
        
        System.out.println("\n--- 练习结果 ---");
        System.out.println("总题目数: " + totalProblems);
        System.out.println("做对: " + correctCount);
        System.out.println("正确率: " + percentage + "%");
        
        if (percentage < 75) {
            System.out.println("请向你的老师寻求额外帮助。");
        } else {
            System.out.println("恭喜，你已准备好进入下一级别！");
        }
        System.out.println("----------------\n");
    }
}
