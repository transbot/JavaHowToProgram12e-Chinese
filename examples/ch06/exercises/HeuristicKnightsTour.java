// 练习6.12c， HeuristicKnightsTour.java
// 编写一个程序，模拟骑士随机巡游
// 骑士依次从64个位置开始，尝试访问尽可能多的不同位置
// 每次移动时，随机选择8种可能的移动方式之一
// 如果移动后的位置已被访问过或超出棋盘范围，则选择另一种移动方式
// 当所有8种移动方式都不可行时，结束并显示结果

import java.util.Random;

public class HeuristicKnightsTour {
    // 棋盘大小
    private static final int BOARD_SIZE = 8;
    
    // 骑士的8种可能移动方式（水平和垂直分量）
    private static final int[] horizontal = {2, 1, -1, -2, -2, -1, 1, 2};
    private static final int[] vertical = {-1, -2, -2, -1, 1, 2, 2, 1};
    
    // 初始可访问性矩阵
    private static final int[][] initialAccessibility = {
        {2, 3, 4, 4, 4, 4, 3, 2},
        {3, 4, 6, 6, 6, 6, 4, 3},
        {4, 6, 8, 8, 8, 8, 6, 4},
        {4, 6, 8, 8, 8, 8, 6, 4},
        {4, 6, 8, 8, 8, 8, 6, 4},
        {4, 6, 8, 8, 8, 8, 6, 4},
        {3, 4, 6, 6, 6, 6, 4, 3},
        {2, 3, 4, 4, 4, 4, 3, 2}
    };
    
    // 随机数生成器（用于处理平局情况）
    private static Random random = new Random();
    
    public static void main(String[] args) {
        int completeTours = 0; // 完整巡游次数
        
        System.out.println("骑士巡游结果（64次，每次从不同位置开始）：");
        System.out.println("起始位置\t移动步数\t是否完成");
        System.out.println("----------------------------------------");
        
        // 从64个不同位置开始巡游
        for (int startRow = 0; startRow < BOARD_SIZE; startRow++) {
            for (int startCol = 0; startCol < BOARD_SIZE; startCol++) {
                int moves = runTour(startRow, startCol);
                boolean complete = (moves == 64);
                
                if (complete) {
                    completeTours++;
                }
                
                System.out.printf("(%d,%d)\t%d\t%s\n", 
                                 startRow, startCol, moves, 
                                 complete ? "是" : "否");
            }
        }
        
        System.out.println("----------------------------------------");
        System.out.println("完整巡游次数: " + completeTours + " / 64");
        System.out.println("完整巡游比例: " + (completeTours * 100.0 / 64) + "%");
    }
    
    // 运行一次巡游
    private static int runTour(int startRow, int startCol) {
        // 创建棋盘和可访问性矩阵的副本
        int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
        int[][] accessibility = new int[BOARD_SIZE][BOARD_SIZE];
        
        // 初始化棋盘和可访问性矩阵
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.arraycopy(initialAccessibility[i], 0, accessibility[i], 0, BOARD_SIZE);
        }
        
        // 设置骑士的起始位置
        int currentRow = startRow;
        int currentCol = startCol;
        board[currentRow][currentCol] = 1; // 标记起始位置为1
        
        // 更新可访问性矩阵（减少从起始位置能到达的方格的可访问性分数）
        updateAccessibility(accessibility, currentRow, currentCol, -1);
        
        // 尝试移动骑士
        int moveCount = 1; // 从1开始计数（起始位置已占用一步）
        
        // 继续移动直到无法移动或完成所有64步
        while (moveCount < 64) {
            // 找出所有可能的下一步移动
            int bestMove = -1;
            int minAccessibility = Integer.MAX_VALUE;
            
            // 找出可访问性分数最低的移动
            for (int moveNumber = 0; moveNumber < 8; moveNumber++) {
                int nextRow = currentRow + vertical[moveNumber];
                int nextCol = currentCol + horizontal[moveNumber];
                
                // 检查移动是否有效（在棋盘内且未访问过）
                if (isValidMove(board, nextRow, nextCol)) {
                    if (accessibility[nextRow][nextCol] < minAccessibility) {
                        minAccessibility = accessibility[nextRow][nextCol];
                        bestMove = moveNumber;
                    } else if (accessibility[nextRow][nextCol] == minAccessibility) {
                        // 如果出现平局，随机选择一个
                        if (random.nextBoolean()) {
                            bestMove = moveNumber;
                        }
                    }
                }
            }
            
            // 如果没有找到有效移动，结束循环
            if (bestMove == -1) {
                break;
            }
            
            // 执行移动
            currentRow += vertical[bestMove];
            currentCol += horizontal[bestMove];
            moveCount++;
            board[currentRow][currentCol] = moveCount;
            
            // 更新可访问性矩阵（减少从新位置能到达的方格的可访问性分数）
            updateAccessibility(accessibility, currentRow, currentCol, -1);
        }
        
        return moveCount;
    }
    
    // 更新可访问性矩阵
    private static void updateAccessibility(int[][] accessibility, int row, int col, int delta) {
        for (int moveNumber = 0; moveNumber < 8; moveNumber++) {
            int nextRow = row + vertical[moveNumber];
            int nextCol = col + horizontal[moveNumber];
            
            // 检查是否在棋盘范围内
            if (nextRow >= 0 && nextRow < BOARD_SIZE && 
                nextCol >= 0 && nextCol < BOARD_SIZE) {
                accessibility[nextRow][nextCol] += delta;
                
                // 确保可访问性分数不会低于0
                if (accessibility[nextRow][nextCol] < 0) {
                    accessibility[nextRow][nextCol] = 0;
                }
            }
        }
    }
    
    // 检查移动是否有效
    private static boolean isValidMove(int[][] board, int row, int column) {
        // 检查是否在棋盘范围内
        if (row < 0 || row >= BOARD_SIZE || column < 0 || column >= BOARD_SIZE) {
            return false;
        }
        
        // 检查该位置是否已被访问过
        return board[row][column] == 0;
    }
}