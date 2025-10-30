// 练习22.19，EightQueens.java
public class EightQueens {
    private static final int BOARD_SIZE = 8;
    private static int[] queens = new int[BOARD_SIZE]; // queens[col] = 行号
    private static int solutionCount = 0;

    public static void main(String[] args) {
        System.out.println("八皇后问题解决方案");
        System.out.println("==================");
        
        // 从第0列开始放置皇后
        placeQueen(0);
        
        System.out.println("\n总共找到" + solutionCount + "种解决方案");
    }

    /**
     * 递归方法：在第col列放置皇后
     * @param col 当前要放置皇后的列号
     */
    public static void placeQueen(int col) {
        // 基本情况：所有列都已放置皇后
        if (col >= BOARD_SIZE) {
            printSolution();
            return;
        }

        // 递归步骤：尝试在当前列的每一行放置皇后
        for (int row = 0; row < BOARD_SIZE; row++) {
            // 检查当前位置是否安全
            if (isSafe(col, row)) {
                // 放置皇后
                queens[col] = row;
                
                // 递归地在下一列放置皇后
                placeQueen(col + 1);
                
                // 回溯：移除皇后（实际上不需要显式移除，因为queens[col]会被覆盖）
            }
        }
    }

    /**
     * 检查在(col, row)位置放置皇后是否安全
     * @param col 列号
     * @param row 行号
     * @return 如果安全返回true，否则返回false
     */
    private static boolean isSafe(int col, int row) {
        // 检查所有已放置皇后的列
        for (int i = 0; i < col; i++) {
            int otherRow = queens[i];
            
            // 检查是否在同一行
            if (otherRow == row) {
                return false;
            }
            
            // 检查是否在同一对角线（左上到右下）
            if (otherRow - i == row - col) {
                return false;
            }
            
            // 检查是否在同一反对角线（右上到左下）
            if (otherRow + i == row + col) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * 打印当前解决方案
     */
    private static void printSolution() {
        solutionCount++;
        System.out.println("\n解决方案" + solutionCount + ":");
        
        // 打印棋盘
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (queens[col] == row) {
                    System.out.print("Q ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }
}