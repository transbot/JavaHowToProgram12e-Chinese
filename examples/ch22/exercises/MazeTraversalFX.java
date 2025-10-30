// 练习22.24，MazeTraversalFX.java
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class MazeTraversalFX extends Application {
    private static final int SIZE = 12;
    private static final int CELL_SIZE = 40;
    
    private char[][] maze = {
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
    
    private Rectangle[][] cells = new Rectangle[SIZE][SIZE];
    private GridPane grid;
    private Timeline timeline;
    private List<int[]> finalPath = new ArrayList<>();
    private List<AnimationStep> animationSteps = new ArrayList<>();
    private int currentStep = 0;
    private boolean[][] visited;
    
    // 方向：下、右、上、左
    private static final int[][] DIRECTIONS = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
    
    // 动画步骤类，用于记录每一步的状态
    private static class AnimationStep {
        int row;
        int col;
        boolean isForward; // true表示前进，false表示回溯
        
        AnimationStep(int row, int col, boolean isForward) {
            this.row = row;
            this.col = col;
            this.isForward = isForward;
        }
    }
    
    @Override
    public void start(Stage primaryStage) {
        grid = new GridPane();
        initializeMaze();
        
        Scene scene = new Scene(grid);
        primaryStage.setTitle("迷宫遍历可视化 - 显示回溯过程");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // 开始迷宫遍历
        startMazeTraversal();
    }
    
    private void initializeMaze() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                
                if (maze[i][j] == '#') {
                    cell.setFill(Color.BLACK);
                } else {
                    cell.setFill(Color.WHITE);
                }
                
                cell.setStroke(Color.GRAY);
                cells[i][j] = cell;
                grid.add(cell, j, i);
            }
        }
    }
    
    private void startMazeTraversal() {
        visited = new boolean[SIZE][SIZE];
        
        // 使用递归回溯找到路径
        boolean found = mazeTraversal(2, 0, new ArrayList<>());
        
        if (found) {
            System.out.println("成功找到出口!");
        } else {
            System.out.println("没有找到出口!");
        }
        
        // 设置时间线动画来显示路径
        if (!animationSteps.isEmpty()) {
            timeline = new Timeline(new KeyFrame(Duration.millis(300), e -> showNextStep()));
            timeline.setCycleCount(animationSteps.size());
            timeline.play();
        }
    }
    
    private boolean mazeTraversal(int row, int col, List<int[]> currentPath) {
        // 检查边界和墙壁
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE || 
            maze[row][col] == '#' || visited[row][col]) {
            return false;
        }
        
        // 标记为已访问
        visited[row][col] = true;
        currentPath.add(new int[]{row, col});
        animationSteps.add(new AnimationStep(row, col, true));
        
        // 检查是否到达出口（边界上的点，但不是入口）
        if (isExit(row, col) && !(row == 2 && col == 0)) {
            // 保存最终路径
            finalPath.addAll(currentPath);
            return true;
        }
        
        // 尝试四个方向
        for (int[] dir : DIRECTIONS) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            
            if (mazeTraversal(newRow, newCol, new ArrayList<>(currentPath))) {
                return true;
            }
        }
        
        // 如果所有方向都失败，回溯
        animationSteps.add(new AnimationStep(row, col, false));
        return false;
    }
    
    private boolean isExit(int row, int col) {
        return (row == 0 || row == SIZE - 1 || col == 0 || col == SIZE - 1);
    }
    
    private void showNextStep() {
        if (currentStep < animationSteps.size()) {
            AnimationStep step = animationSteps.get(currentStep);
            
            // 根据步骤类型设置颜色
            if (step.isForward) {
                // 前进 - 蓝色
                cells[step.row][step.col].setFill(Color.BLUE);
            } else {
                // 回溯 - 红色
                cells[step.row][step.col].setFill(Color.RED);
            }
            
            currentStep++;
            
            // 如果到达最后一步，停止动画并只显示最终路径
            if (currentStep >= animationSteps.size()) {
                timeline.stop();
                showOnlyFinalPath();
            }
        }
    }
    
    private void showOnlyFinalPath() {
        // 重置所有路径单元格的颜色为白色
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (maze[i][j] != '#') {
                    cells[i][j].setFill(Color.WHITE);
                }
            }
        }
        
        // 只显示最终路径为绿色
        for (int[] pos : finalPath) {
            cells[pos[0]][pos[1]].setFill(Color.GREEN);
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}