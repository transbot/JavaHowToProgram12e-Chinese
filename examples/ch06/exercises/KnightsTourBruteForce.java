// 练习6.13，KnightsTourBruteForce.java
// 编写一个程序，模拟骑士在国际象棋棋盘上的随机巡游
// 程序分两部分：
// 第一部分：运行1000次巡游，统计每次巡游的长度（   
// 即骑士访问的不同位置的数量），并显示频率表和1000次中最好的结果
// 第二部分：持续运行直到找到完整巡游（访问所有64个位置）
// 显示尝试次数、耗时和最终棋盘

import java.util.Random;

public class KnightsTourBruteForce {
    private static final int BOARD_SIZE = 8;
    private static final int[] horizontal = {2, 1, -1, -2, -2, -1, 1, 2};
    private static final int[] vertical = {-1, -2, -2, -1, 1, 2, 2, 1};
    private static Random random = new Random();

    public static void main(String[] args) {
        // 第一部分：运行1000次巡游，统计长度
        int[] frequency = new int[65]; // 索引从1到64，0不使用
        for (int i = 0; i < 1000; i++) {
            int moves = runRandomTour();
            if (moves >= 1 && moves <= 64) {
                frequency[moves]++;
            }
        }

        // 显示频率表
        System.out.println("巡游步数统计（1000次尝试）:");
        System.out.println("步数\t发生次数");
        for (int i = 1; i <= 64; i++) {
            if (frequency[i] > 0) {
                System.out.println(i + "\t" + frequency[i]);
            }
        }

        // 找出1000次中最好的结果（最大步数）
        int bestResult = 0;
        for (int i = 64; i >= 1; i--) {
            if (frequency[i] > 0) {
                bestResult = i;
                break;
            }
        }
        System.out.println("1000次尝试中最好的结果: " + bestResult + " 步");

        // 第二部分：持续运行直到找到完整巡游
        int attempts = 0;
        long startTime = System.currentTimeMillis();
        int moves = 0;
        int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
        while (moves != 64) {
            attempts++;
            // 重置棋盘
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    board[i][j] = 0;
                }
            }
            moves = runRandomTour(board);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("\n找到完整巡游！");
        System.out.println("尝试次数: " + attempts);
        System.out.println("耗时: " + duration + " 毫秒");
        System.out.println("最终棋盘:");
        printBoard(board);
    }

    // 运行一次随机巡游，使用内部棋盘，只返回步数（用于1000次统计）
    private static int runRandomTour() {
        int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
        return runRandomTour(board);
    }

    // 运行一次随机巡游，使用提供的棋盘，返回步数
    private static int runRandomTour(int[][] board) {
        // 随机选择起始位置
        int currentRow = random.nextInt(BOARD_SIZE);
        int currentCol = random.nextInt(BOARD_SIZE);
        board[currentRow][currentCol] = 1;
        int movesCount = 1;

        boolean canMove = true;
        while (canMove) {
            canMove = false;
            // 生成随机顺序的移动方向
            int[] moveOrder = generateRandomMoveOrder();
            for (int i = 0; i < 8; i++) {
                int moveIndex = moveOrder[i];
                int nextRow = currentRow + vertical[moveIndex];
                int nextCol = currentCol + horizontal[moveIndex];
                if (isValidMove(board, nextRow, nextCol)) {
                    currentRow = nextRow;
                    currentCol = nextCol;
                    movesCount++;
                    board[currentRow][currentCol] = movesCount;
                    canMove = true;
                    break;
                }
            }
        }
        return movesCount;
    }

    // 生成随机移动顺序
    private static int[] generateRandomMoveOrder() {
        int[] order = {0, 1, 2, 3, 4, 5, 6, 7};
        for (int i = 7; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = order[index];
            order[index] = order[i];
            order[i] = temp;
        }
        return order;
    }

    // 检查移动是否有效
    private static boolean isValidMove(int[][] board, int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE && board[row][col] == 0;
    }

    // 打印棋盘
    private static void printBoard(int[][] board) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.printf("%3d", board[i][j]);
            }
            System.out.println();
        }
    }
}