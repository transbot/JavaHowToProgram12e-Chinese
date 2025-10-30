// 练习22.18，PalindromeChecker.java
public class PalindromeChecker {    
    // 递归方法检查回文
    public static boolean testPalindrome(char[] chars) {
        return testPalindromeHelper(chars, 0, chars.length - 1);
    }
    
    // 递归辅助方法，使用两个指针从两端向中间比较
    private static boolean testPalindromeHelper(char[] chars, int start, int end) {
        // 基本情况：当start >= end时，说明已经比较完所有字符
        if (start >= end) {
            return true;
        }
        
        // 跳过非字母字符和标点符号（包括全角和半角）
        if (!isValidCharacter(chars[start])) {
            return testPalindromeHelper(chars, start + 1, end);
        }
        
        // 跳过非字母字符和标点符号（包括全角和半角）
        if (!isValidCharacter(chars[end])) {
            return testPalindromeHelper(chars, start, end - 1);
        }
        
        // 比较字符（不区分大小写）
        if (Character.toLowerCase(chars[start]) != Character.toLowerCase(chars[end])) {
            return false;
        }
        
        // 递归比较内部子串
        return testPalindromeHelper(chars, start + 1, end - 1);
    }
    
    // 检查字符是否为有效字符（字母或需要保留的字符）
    private static boolean isValidCharacter(char c) {
        // 检查是否为字母（包括中文字符）
        if (Character.isLetter(c)) {
            return true;
        }
        
        // 检查是否为数字（如果需要保留数字）
        if (Character.isDigit(c)) {
            return true;
        }
        
        // 如果需要保留其他特定字符，可以在这里添加
        
        return false;
    }
    
    public static void main(String[] args) {
        // 测试用例
        String[] testStrings = {
            "radar",
            "菜油炒油菜",
            "able was i ere i saw elba",
            "a man a plan a canal panama",
            "hello",
            "racecar",
            "A man, a plan, a canal: Panama",
            "上海自来水来自海上", // 中文回文
            "，。，上海自来水来自海上，。，" // 包含全角标点的中文回文
        };
        
        for (String str : testStrings) {
            char[] chars = str.toCharArray();
            boolean isPalindrome = testPalindrome(chars);
            System.out.printf("\"%s\"是回文: %b%n", str, isPalindrome);
        }
    }
}