// 练习7.28，SpellingChecker.java
// 拼写检查器
// 从文件words.txt加载单词列表
// 检查用户输入的单词是否拼写正确
// 如果拼写错误，提供可能的正确拼写建议

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class SpellingChecker {
    private Set<String> wordSet; // 使用Set来存储单词，提高查找效率
    private List<String> wordList; // 保留原始单词列表

    public static void main(String[] args) {
        SpellingChecker checker = new SpellingChecker();
        checker.loadWordList("words.txt");
        checker.run();
    }

    // 从文件加载单词列表
    public void loadWordList(String filename) {
        wordList = new ArrayList<>();
        wordSet = new HashSet<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim();
                if (!word.isEmpty()) {
                    wordList.add(word);
                    wordSet.add(word); // 保留原始大小写
                    
                    // 同时添加小写版本到wordSet，以便处理大小写变化的情况
                    String lowerCaseVersion = word.toLowerCase();
                    if (!wordSet.contains(lowerCaseVersion)) {
                        wordSet.add(lowerCaseVersion);
                    }
                    
                    // 添加首字母大写版本到wordSet
                    if (word.length() > 1 && Character.isLowerCase(word.charAt(0))) {
                        String capitalizedVersion = Character.toUpperCase(word.charAt(0)) + word.substring(1);
                        if (!wordSet.contains(capitalizedVersion)) {
                            wordSet.add(capitalizedVersion);
                        }
                    }
                }
            }
            System.out.println("已加载 " + wordList.size() + " 个单词到字典中。");
        } catch (IOException e) {
            System.err.println("读取文件时出错: " + e.getMessage());
            System.exit(1);
        }
    }

    // 运行拼写检查器
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("欢迎使用拼写检查器！");
        System.out.println("请输入一个单词进行检查（输入'exit'退出程序）:");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            if (input.isEmpty()) {
                continue;
            }

            checkSpelling(input);
        }

        scanner.close();
        System.out.println("感谢使用拼写检查器！");
    }

    // 检查单词拼写
    private void checkSpelling(String word) {
        // 检查单词是否在字典中（精确匹配，包括大小写）
        if (wordSet.contains(word)) {
            System.out.println("单词拼对了！");
            return;
        }
        
        // 检查是否只是大小写不同
        String lowerCaseVersion = word.toLowerCase();
        if (wordSet.contains(lowerCaseVersion)) {
            System.out.println("单词拼对了！");
            return;
        }
        
        // 检查是否只是首字母大小写不同
        if (word.length() > 0) {
            String capitalizedVersion = Character.toUpperCase(word.charAt(0)) + 
                                      (word.length() > 1 ? word.substring(1).toLowerCase() : "");
            if (wordSet.contains(capitalizedVersion)) {
                System.out.println("单词拼对了！");
                return;
            }
        }

        System.out.println("单词拼写错误！");
        List<String> suggestions = generateSuggestions(word);
        
        if (suggestions.isEmpty()) {
            System.out.println("无法找到任何可能的正确拼写。");
        } else {
            System.out.println("你的意思是下列单词之一吗？");
            for (String suggestion : suggestions) {
                System.out.println("  - " + suggestion);
            }
        }
    }

    // 生成可能的正确拼写建议
    private List<String> generateSuggestions(String word) {
        Set<String> suggestions = new HashSet<>();
        
        // 1. 首先检查是否有大小写不同的版本
        for (String dictWord : wordList) {
            if (dictWord.equalsIgnoreCase(word) && !dictWord.equals(word)) {
                suggestions.add(dictWord);
            }
        }
        
        // 2. 交换相邻字母
        for (int i = 0; i < word.length() - 1; i++) {
            char[] chars = word.toCharArray();
            char temp = chars[i];
            chars[i] = chars[i + 1];
            chars[i + 1] = temp;
            String swapped = new String(chars);
            if (wordSet.contains(swapped)) {
                suggestions.add(swapped);
            }
        }
        
        // 3. 删除重复字母
        for (int i = 0; i < word.length() - 1; i++) {
            if (word.charAt(i) == word.charAt(i + 1)) {
                String withoutDuplicate = word.substring(0, i) + word.substring(i + 1);
                if (wordSet.contains(withoutDuplicate)) {
                    suggestions.add(withoutDuplicate);
                }
            }
        }
        
        // 4. 删除一个字母（尝试删除每个位置的字母）
        for (int i = 0; i < word.length(); i++) {
            String withoutChar = word.substring(0, i) + word.substring(i + 1);
            if (wordSet.contains(withoutChar)) {
                suggestions.add(withoutChar);
            }
        }
        
        // 5. 添加一个字母（尝试在每个位置添加字母）
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-&.";
        for (int i = 0; i <= word.length(); i++) {
            for (char c : alphabet.toCharArray()) {
                String withAdded = word.substring(0, i) + c + word.substring(i);
                if (wordSet.contains(withAdded)) {
                    suggestions.add(withAdded);
                }
            }
        }
        
        // 6. 替换一个字母（尝试替换每个位置的字母）
        for (int i = 0; i < word.length(); i++) {
            for (char c : alphabet.toCharArray()) {
                String replaced = word.substring(0, i) + c + word.substring(i + 1);
                if (wordSet.contains(replaced)) {
                    suggestions.add(replaced);
                }
            }
        }
        
        return new ArrayList<>(suggestions);
    }
}