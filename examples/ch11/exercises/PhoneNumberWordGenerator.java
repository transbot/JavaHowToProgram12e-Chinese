// 练习11.11，PhoneNumberWordGenerator.java
// 编写一个程序，提示用户输入一个7位数电话号码（允许输入短划线），
// 然后生成该电话号码对应的所有可能的单词组合。
    
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PhoneNumberWordGenerator {
    // 使用数组存储数字到字母的映射
    private static final String[] DIGIT_LETTERS = new String[10];
    
    static {
        // 初始化数字到字母的映射
        DIGIT_LETTERS[2] = "ABC";
        DIGIT_LETTERS[3] = "DEF";
        DIGIT_LETTERS[4] = "GHI";
        DIGIT_LETTERS[5] = "JKL";
        DIGIT_LETTERS[6] = "MNO";
        DIGIT_LETTERS[7] = "PQRS";
        DIGIT_LETTERS[8] = "TUV";
        DIGIT_LETTERS[9] = "WXYZ";
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;
        
        do {
            // 显示菜单
            System.out.println("\n=== 电话号码单词生成器 ===");
            System.out.println("1. 根据电话号码查找匹配单词");
            System.out.println("2. 根据单词生成电话号码");
            System.out.println("3. 退出程序");
            System.out.print("请选择操作 (1-3): ");
            
            // 获取用户选择
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // 消耗换行符
            } catch (InputMismatchException e) {
                System.out.println("错误：请输入有效的数字选项");
                scanner.nextLine(); // 清除无效输入
                choice = 0;
                continue;
            }
            
            // 根据选择执行相应操作
            switch (choice) {
                case 1:
                    findWordsFromPhoneNumber(scanner);
                    break;
                case 2:
                    findPhoneNumberFromWord(scanner);
                    break;
                case 3:
                    System.out.println("感谢使用，再见！");
                    break;
                default:
                    System.out.println("错误：请输入有效的选项 (1-3)");
            }
        } while (choice != 3);
        
        scanner.close();
    }
    
    // 选项1：根据电话号码查找匹配单词
    private static void findWordsFromPhoneNumber(Scanner scanner) {
        // 获取用户输入的电话号码
        System.out.print("请输入7位数电话号码（可包含短划线）: ");
        String phoneInput = scanner.nextLine().replaceAll("-", "");
        
        // 验证输入
        if (phoneInput.length() != 7 || !isValidPhoneNumber(phoneInput)) {
            System.out.println("错误：请输入有效的7位数字电话号码（数字2-9）");
            return;
        }
        
        // 读取单词文件
        String[] words;
        try {
            words = readWordsFromFile("words.txt");
        } catch (IOException e) {
            System.out.println("错误：无法读取words.txt文件");
            return;
        }
        
        // 查找匹配的单词
        List<String> matchedWords = findMatchingWords(phoneInput, words);
        
        // 写入结果文件
        String fileName = "MatchedWords" + phoneInput + ".txt";
        Path documentsPath = Path.of(System.getProperty("user.home"), "Documents");
        Path outputPath = documentsPath.resolve(fileName);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath.toFile()))) {
            for (String word : matchedWords) {
                writer.println(word.toUpperCase()); // 确保输出为大写
            }
            
            System.out.println("已找到 " + matchedWords.size() + " 个匹配的单词");
            System.out.println("结果已保存至: " + outputPath);
            
            // 显示部分匹配结果（最多显示10个）
            if (matchedWords.size() > 0) {
                System.out.println("部分匹配结果:");
                int displayCount = Math.min(matchedWords.size(), 10);
                for (int i = 0; i < displayCount; i++) {
                    System.out.println(matchedWords.get(i).toUpperCase());
                }
                if (matchedWords.size() > 10) {
                    System.out.println("... 还有 " + (matchedWords.size() - 10) + " 个单词");
                }
            }
        } catch (IOException e) {
            System.out.println("错误：无法创建输出文件");
        }
    }
    
    // 选项2：根据单词生成电话号码
    private static void findPhoneNumberFromWord(Scanner scanner) {
        System.out.print("请输入7字母单词: ");
        String word = scanner.nextLine().trim();
        
        // 验证输入
        if (word.length() != 7) {
            System.out.println("错误：请输入7个字母的单词");
            return;
        }
        
        // 检查是否只包含字母
        if (!word.matches("[a-zA-Z]+")) {
            System.out.println("错误：单词只能包含字母");
            return;
        }
        
        // 转换为大写
        word = word.toUpperCase();
        
        // 生成电话号码
        StringBuilder phoneNumber = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            char letter = word.charAt(i);
            int digit = findDigitForLetter(letter);
            
            if (digit == -1) {
                System.out.println("错误：字母 '" + letter + "' 没有对应的数字");
                return;
            }
            
            phoneNumber.append(digit);
            
            // 在适当位置添加短划线
            if (i == 2) {
                phoneNumber.append("-");
            }
        }
        
        System.out.println("单词 \"" + word + "\" 对应的电话号码是: " + phoneNumber.toString());
    }
    
    // 根据字母查找对应的数字
    private static int findDigitForLetter(char letter) {
        for (int digit = 2; digit <= 9; digit++) {
            if (DIGIT_LETTERS[digit].indexOf(letter) != -1) {
                return digit;
            }
        }
        return -1; // 未找到
    }
    
    // 验证电话号码是否有效（只包含2-9的数字）
    private static boolean isValidPhoneNumber(String phoneNumber) {
        for (int i = 0; i < phoneNumber.length(); i++) {
            char c = phoneNumber.charAt(i);
            if (c < '2' || c > '9') {
                return false;
            }
        }
        return true;
    }
    
    // 从文件读取单词
    private static String[] readWordsFromFile(String filename) throws IOException {
        Path path = Paths.get(filename);
        if (!Files.exists(path)) {
            throw new FileNotFoundException();
        }
        
        List<String> wordList = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() == 7) {
                    wordList.add(line);
                }
            }
        }
        
        return wordList.toArray(new String[0]);
    }
    
    // 查找匹配的单词
    private static List<String> findMatchingWords(String phoneNumber, String[] words) {
        List<String> result = new ArrayList<>();
        
        for (String word : words) {
            boolean matches = true;
            
            for (int i = 0; i < 7; i++) {
                // 获取当前数字
                int digit = Character.getNumericValue(phoneNumber.charAt(i));
                // 获取当前字母（转换为大写进行比较）
                char letter = Character.toUpperCase(word.charAt(i));
                
                // 检查字母是否在数字对应的字母列表中
                String possibleLetters = DIGIT_LETTERS[digit];
                boolean found = false;
                
                for (int j = 0; j < possibleLetters.length(); j++) {
                    if (possibleLetters.charAt(j) == letter) {
                        found = true;
                        break;
                    }
                }
                
                if (!found) {
                    matches = false;
                    break;
                }
            }
            
            if (matches) {
                result.add(word.toUpperCase()); // 存储为大写形式
            }
        }
        
        return result;
    }
}