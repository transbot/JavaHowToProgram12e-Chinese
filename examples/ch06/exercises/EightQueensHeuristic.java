import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 使用“最小消除数”启发式结合回溯法解决八皇后问题。
 */
public class EightQueensHeuristic {

    final int BOARD_SIZE = 8;
    int[][] board;

    // 一个内部类，用于存储一个可能的移动及其启发式评价值（消除数）
    class Move implements Comparable<Move> {
        int row;
        int col;
        int eliminationNumber;

        Move(int row, int col, int eliminationNumber) {
            this.row = row;
            this.col = col;
            this.eliminationNumber = eliminationNumber;
        }

        // 实现Comparable接口，以便按消除数从小到大排序
        @Override
        public int compareTo(Move other) {
            return Integer.compare(this.eliminationNumber, other.eliminationNumber);
        }
    }

    public EightQueensHeuristic() {
        // 初始化棋盘
        board = new int[BOARD_SIZE][BOARD_SIZE];
    }

    /**
     * 打印最终的解决方案
     */
    private void printSolution() {
        System.out.println("找到一个由启发式方法引导的解：");
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 1) {
                    System.out.print("Q ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }

    /**
     * 检查在当前棋盘状态下，(row, col)位置是否安全（未被攻击）
     */
    private boolean isSafe(int row, int col) {
        // 检查行和列
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[row][i] == 1 || board[i][col] == 1) {
                return false;
            }
        }
        // 检查对角线
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (Math.abs(row - i) == Math.abs(col - j)) {
                    if (board[i][j] == 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 计算如果在(row, col)放置一个皇后，会“消除”多少个其他当前安全的空格。
     */
    private int calculateEliminations(int row, int col) {
        int eliminatedCount = 0;
        // 临时在棋盘上放置皇后以进行计算
        board[row][col] = 1;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                // 如果是空位并且不安全了（被新皇后攻击），则计数
                if (board[i][j] == 0 && !isSafe(i, j)) {
                    eliminatedCount++;
                }
            }
        }
        
        // 计算完毕后，将皇后移走，恢复棋盘状态
        board[row][col] = 0;
        return eliminatedCount;
    }


    /**
     * 解决问题的主递归（回溯）函数
     * @param queensPlaced 当前已经放置的皇后数量
     * @return 如果找到解则返回true
     */
    private boolean solveUtil(int queensPlaced) {
        // 基本情况：8个皇后都已成功放置
        if (queensPlaced == BOARD_SIZE) {
            return true;
        }

        List<Move> potentialMoves = new ArrayList<>();

        // 1. 找出所有当前安全的空位，并计算它们的消除数
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (board[r][c] == 0 && isSafe(r, c)) {
                    int elims = calculateEliminations(r, c);
                    potentialMoves.add(new Move(r, c, elims));
                }
            }
        }

        // 2. 根据消除数对所有可能的移动进行排序（从小到大）
        Collections.sort(potentialMoves);

        // 3. 遍历排序后的移动列表，进行尝试和回溯
        for (Move move : potentialMoves) {
            // [尝试] 在“最佳”位置放置皇后
            board[move.row][move.col] = 1;

            // [递归]
            if (solveUtil(queensPlaced + 1)) {
                return true;
            }

            // [回溯]
            board[move.row][move.col] = 0;
        }
        
        return false;
    }

    /**
     * 启动求解过程的公共方法
     */
    public void solve() {
        if (!solveUtil(0)) {
            System.out.println("解决方案不存在");
            return;
        }
        printSolution();
    }

    public static void main(String[] args) {
        EightQueensHeuristic solver = new EightQueensHeuristic();
        solver.solve();
    }
}