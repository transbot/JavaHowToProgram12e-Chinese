// 练习22.24，MazeTraversal.java
import java.util.Arrays;

public class MazeTraversal {
    private static final int SIZE = 12;
    private static char[][] maze = {
        {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
        {'#', '.', '.', '.', '#', '.', '.', '.', '.', '.', '.', '#'},
        {'.', '.', '#', '.', '#', '.', '#', '#', '#', '#', '.', '#'},
        {'#', '#', '#', '.', '#', '.', '.', '.', '.', '#', '.', '#'},
        {'#', '.', '.', '.', '.', '#', '#', '#', '.', '#', '.', '.'},
        {'#', '#', '#', '#', '.', '#', '.', '#', '.', '#', '.', '#'},
        {'#', '.', '.', '#', '.', '#', '.', '#', '.', '#', '.', '#'},
        {'#', '#', '.', '#', '.', '#', '.', '#', '.', '#', '.', '#'},
        {'#', '.', '.', '.', '.', '.', '.', '.', '.', '#', '.', '#'},
        {'#', '#', '#', '#', '#', '#', '.', '#', '#', '#', '.', '#'},
        {'#', '.', '.', '.', '.', '.', '.', '#', '.', '.', '.', '#'},
        {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'}
    };
    
    private static final int[][] DIRECTIONS = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}}; // 下、右、上、左

    public static void main(String[] args) {
        System.out.println("初始迷宫:");
        printMaze();
        
        // 从入口点(2,0)开始
        if (mazeTraversal(2, 0)) {
            System.out.println("成功找到出口!");
        } else {
            System.out.println("没有找到出口!");
        }
        
        System.out.println("最终路径:");
        printMaze();
    }
    
    /**
     * 递归回溯方法遍历迷宫
     * @param row 当前行
     * @param col 当前列
     * @return 如果找到出口返回true，否则返回false
     */
    public static boolean mazeTraversal(int row, int col) {
        // 检查是否到达出口（边界上的点，但不是入口）
        if (isExit(row, col) && !(row == 2 && col == 0)) {
            maze[row][col] = 'x'; // 标记出口
            printMaze();
            return true;
        }
        
        // 标记当前位置为已访问
        char original = maze[row][col];
        maze[row][col] = 'x';
        printMaze();
        
        // 尝试所有可能的方向
        for (int[] dir : DIRECTIONS) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            
            // 检查新位置是否有效且可通行
            if (isValidMove(newRow, newCol)) {
                // 递归调用
                if (mazeTraversal(newRow, newCol)) {
                    return true; // 找到出口
                }
            }
        }
        
        // 如果所有方向都失败，回溯并标记为死胡同
        maze[row][col] = '0';
        printMaze();
        return false;
    }
    
    /**
     * 检查位置是否是出口（在边界上）
     */
    private static boolean isExit(int row, int col) {
        return row == 0 || row == SIZE - 1 || col == 0 || col == SIZE - 1;
    }
    
    /**
     * 检查移动是否有效
     */
    private static boolean isValidMove(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE && 
               (maze[row][col] == '.' || maze[row][col] == '0');
    }
    
    /**
     * 打印迷宫当前状态
     */
    private static void printMaze() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.print(maze[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
        
        // 添加延迟以便观察
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}