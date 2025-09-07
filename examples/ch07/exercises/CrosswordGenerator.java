import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class CrosswordGenerator {
    private static final int GRID_SIZE = 15;
    private char[][] grid;
    private List<String> words;
    private List<String> usedWords;
    private List<WordPosition> wordPositions;

    // 内部类，用于存储单词的位置信息
    private static class WordPosition {
        String word;
        int row;
        int col;
        boolean isHorizontal;
        
        WordPosition(String word, int row, int col, boolean isHorizontal) {
            this.word = word;
            this.row = row;
            this.col = col;
            this.isHorizontal = isHorizontal;
        }
    }

    public static void main(String[] args) {
        CrosswordGenerator generator = new CrosswordGenerator();
        generator.loadWords("words.txt");
        generator.initializeGrid();
        generator.generateCrossword();
        generator.printGrid();
        generator.printWordList();
    }

    // 从文件加载单词
    private void loadWords(String filename) {
        words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim();
                if (word.length() > 2 && word.length() <= GRID_SIZE) {
                    words.add(word.toUpperCase());
                }
            }
            System.out.println("已加载 " + words.size() + " 个可用单词。");
        } catch (IOException e) {
            System.err.println("读取文件时出错: " + e.getMessage());
            System.exit(1);
        }
    }

    // 初始化网格
    private void initializeGrid() {
        grid = new char[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = ' ';
            }
        }
        usedWords = new ArrayList<>();
        wordPositions = new ArrayList<>();
    }

    // 生成纵横字谜
    private void generateCrossword() {
        if (words.isEmpty()) {
            System.out.println("没有可用的单词来生成字谜。");
            return;
        }
        
        // 放置第一个单词在网格中央
        String firstWord = words.get(new Random().nextInt(words.size()));
        int startCol = (GRID_SIZE - firstWord.length()) / 2;
        int startRow = GRID_SIZE / 2;
        
        placeWord(firstWord, startRow, startCol, true);
        usedWords.add(firstWord);
        words.remove(firstWord);
        
        // 尝试放置其他单词
        int attempts = 0;
        while (!words.isEmpty() && attempts < 1000) {
            String word = words.get(new Random().nextInt(words.size()));
            if (tryPlaceWord(word)) {
                usedWords.add(word);
                words.remove(word);
                attempts = 0;
            } else {
                attempts++;
            }
        }
        
        System.out.println("生成了包含 " + usedWords.size() + " 个单词的纵横字谜。");
    }

    // 尝试放置一个单词
    private boolean tryPlaceWord(String word) {
        // 尝试与已放置的单词交叉
        for (WordPosition wp : wordPositions) {
            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                
                // 查找匹配的字母
                for (int j = 0; j < wp.word.length(); j++) {
                    if (wp.word.charAt(j) == c) {
                        // 计算新单词的位置
                        int newRow, newCol;
                        boolean newIsHorizontal = !wp.isHorizontal;
                        
                        if (newIsHorizontal) {
                            newRow = wp.row + (wp.isHorizontal ? 0 : j);
                            newCol = wp.col + (wp.isHorizontal ? j : 0) - i;
                        } else {
                            newRow = wp.row + (wp.isHorizontal ? j : 0) - i;
                            newCol = wp.col + (wp.isHorizontal ? 0 : j);
                        }
                        
                        // 检查是否可以放置
                        if (canPlaceWord(word, newRow, newCol, newIsHorizontal)) {
                            placeWord(word, newRow, newCol, newIsHorizontal);
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }

    // 检查是否可以放置单词
    private boolean canPlaceWord(String word, int row, int col, boolean isHorizontal) {
        // 检查边界
        if (isHorizontal) {
            if (col < 0 || col + word.length() > GRID_SIZE || row < 0 || row >= GRID_SIZE) {
                return false;
            }
        } else {
            if (row < 0 || row + word.length() > GRID_SIZE || col < 0 || col >= GRID_SIZE) {
                return false;
            }
        }
        
        // 检查与现有字母的冲突
        for (int i = 0; i < word.length(); i++) {
            int r = isHorizontal ? row : row + i;
            int c = isHorizontal ? col + i : col;
            
            char existingChar = grid[r][c];
            if (existingChar != ' ' && existingChar != word.charAt(i)) {
                return false;
            }
        }
        
        return true;
    }

    // 放置单词到网格中
    private void placeWord(String word, int row, int col, boolean isHorizontal) {
        for (int i = 0; i < word.length(); i++) {
            int r = isHorizontal ? row : row + i;
            int c = isHorizontal ? col + i : col;
            grid[r][c] = word.charAt(i);
        }
        wordPositions.add(new WordPosition(word, row, col, isHorizontal));
    }

    // 打印网格
    private void printGrid() {
        System.out.println("\n纵横字谜网格:");
        System.out.print("   ");
        for (int i = 0; i < GRID_SIZE; i++) {
            System.out.print(String.format("%2d ", i + 1));
        }
        System.out.println();
        
        for (int i = 0; i < GRID_SIZE; i++) {
            System.out.print(String.format("%2d ", i + 1));
            for (int j = 0; j < GRID_SIZE; j++) {
                System.out.print(" " + (grid[i][j] == ' ' ? "." : grid[i][j]) + " ");
            }
            System.out.println();
        }
    }

    // 打印单词列表
    private void printWordList() {
        System.out.println("\n横向单词:");
        for (WordPosition wp : wordPositions) {
            if (wp.isHorizontal) {
                System.out.println("- " + wp.word + " (" + (wp.row + 1) + ", " + (wp.col + 1) + ")");
            }
        }
        
        System.out.println("\n纵向单词:");
        for (WordPosition wp : wordPositions) {
            if (!wp.isHorizontal) {
                System.out.println("- " + wp.word + " (" + (wp.row + 1) + ", " + (wp.col + 1) + ")");
            }
        }
        
        System.out.println("\n提示:");
        System.out.println("1. 数字表示单词的起始位置（行,列）");
        System.out.println("2. '.' 表示空白格子");
        System.out.println("3. 字母表示已填充的格子");
    }
}