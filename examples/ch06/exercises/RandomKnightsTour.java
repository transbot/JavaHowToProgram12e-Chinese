// 练习6.12b，RandomKnightsTour.java
// 编写一个程序，模拟骑士在国际象棋棋盘上的随机巡游
// 骑士从(3,4)位置开始，尝试访问尽可能多的不同位置
// 每次移动时，随机选择8种可能的移动方式之一
// 如果移动后的位置已被访问过或超出棋盘范围，则选择另一种移动方式
// 当所有8种移动方式都不可行时，结束并显示结果

import java.util.Random;

public class RandomKnightsTour {
    // 棋盘大小
    private static final int BOARD_SIZE = 8;
    
    // 骑士的8种可能移动方式（水平和垂直分量）
    private static final int[] horizontal = {2, 1, -1, -2, -2, -1, 1, 2};
    private static final int[] vertical = {-1, -2, -2, -1, 1, 2, 2, 1};
    
    // 棋盘
    private static int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
    
    // 随机数生成器
    private static Random random = new Random();
    
    public static void main(String[] args) {
        // 初始化棋盘（所有位置设为0，表示未访问）
        initializeBoard();
        
        // 设置骑士的起始位置（行3，列4）
        int currentRow = 3;
        int currentColumn = 4;
        board[currentRow][currentColumn] = 1; // 标记起始位置为1
        
        // 尝试移动骑士
        int moveCount = 1; // 从1开始计数（起始位置已占用一步）
        boolean canMove = true;
        
        // 继续移动直到无法移动
        while (canMove) {
            canMove = false; // 假设无法移动
            
            // 创建一个随机顺序的移动方向数组
            int[] randomMoves = generateRandomMoveOrder();
            
            // 按随机顺序尝试所有8种可能的移动方式
            for (int i = 0; i < 8; i++) {
                int moveNumber = randomMoves[i];
                
                // 使用题目要求的语句计算下一步位置
                int nextRow = currentRow + vertical[moveNumber];
                int nextColumn = currentColumn + horizontal[moveNumber];
                
                // 检查移动是否有效（在棋盘内且未访问过）
                if (isValidMove(nextRow, nextColumn)) {
                    // 使用题目要求的语句更新当前位置
                    currentRow += vertical[moveNumber];
                    currentColumn += horizontal[moveNumber];
                    
                    moveCount++;
                    board[currentRow][currentColumn] = moveCount;
                    canMove = true; // 可以继续移动
                    break; // 找到一个有效移动后就跳出循环
                }
            }
        }
        
        // 打印结果
        printBoard();
        System.out.println("\n骑士总共移动了 " + moveCount + " 步");
        System.out.println("起始位置: (3, 4)");
    }
    
    // 初始化棋盘
    private static void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = 0;
            }
        }
    }
    
    // 生成随机顺序的移动方向数组
    private static int[] generateRandomMoveOrder() {
        int[] moves = {0, 1, 2, 3, 4, 5, 6, 7};
        
        // Fisher-Yates 洗牌算法
        for (int i = moves.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            // 交换
            int temp = moves[index];
            moves[index] = moves[i];
            moves[i] = temp;
        }
        
        return moves;
    }
    
    // 检查移动是否有效
    private static boolean isValidMove(int row, int column) {
        // 检查是否在棋盘范围内
        if (row < 0 || row >= BOARD_SIZE || column < 0 || column >= BOARD_SIZE) {
            return false;
        }
        
        // 检查该位置是否已被访问过
        return board[row][column] == 0;
    }
    
    // 打印棋盘
    private static void printBoard() {
        System.out.println("\n棋盘状态（数字表示访问顺序）：");
        System.out.println("   0  1  2  3  4  5  6  7");
        System.out.println("  -------------------------");
        
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.out.print(i + "|");
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 0) {
                    System.out.print(" . ");
                } else {
                    System.out.printf("%2d ", board[i][j]);
                }
            }
            System.out.println("|");
        }
        System.out.println("  -------------------------");
    }
}