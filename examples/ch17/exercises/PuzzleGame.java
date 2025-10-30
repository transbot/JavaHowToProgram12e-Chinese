import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PuzzleGame extends Application {
    
    private static final int SIZE = 4; // 4x4 棋盘
    private static final int TILE_SIZE = 80; // 方块大小
    
    private Button[][] tiles = new Button[SIZE][SIZE]; // 棋盘上的方块
    private int emptyRow = SIZE - 1; // 空格的行位置
    private int emptyCol = SIZE - 1; // 空格的列位置
    
    private GridPane gameBoard; // 游戏棋盘
    private Label statusLabel; // 状态标签
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        
        // 创建标题
        Label titleLabel = new Label("15 拼图游戏");
        titleLabel.setFont(new Font(20));
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(10));
        root.setTop(titleBox);
        
        // 创建游戏棋盘
        gameBoard = new GridPane();
        gameBoard.setHgap(5);
        gameBoard.setVgap(5);
        gameBoard.setAlignment(Pos.CENTER);
        initializeBoard();
        root.setCenter(gameBoard);
        
        // 创建状态栏和重置按钮
        statusLabel = new Label("游戏目标：将所有方块按顺序排列");
        statusLabel.setFont(new Font(14));
        Button resetButton = new Button("重新开始");
        resetButton.setOnAction(e -> resetGame());
        
        // 创建目标布局展示区
        Label targetLabel = new Label("目标布局：");
        targetLabel.setFont(new Font(12));
        GridPane targetGrid = createTargetLayout();
        
        VBox statusBox = new VBox(5);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.getChildren().addAll(statusLabel, new HBox(5, targetLabel, targetGrid));
        
        HBox bottomBox = new HBox(20, statusBox, resetButton);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));
        root.setBottom(bottomBox);
        
        // 设置场景
        Scene scene = new Scene(root);
        primaryStage.setTitle("15 拼图");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    // 创建目标布局展示
    private GridPane createTargetLayout() {
        GridPane targetGrid = new GridPane();
        targetGrid.setHgap(2);
        targetGrid.setVgap(2);
        
        // 创建目标布局的迷你方块
        int cellSize = 30; // 迷你方块大小
        
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (row == SIZE - 1 && col == SIZE - 1) {
                    // 最后一个格子留空
                    continue;
                }
                
                // 计算目标值
                int value = row * SIZE + col + 1;
                
                // 创建迷你方块
                Label miniTile = new Label(String.valueOf(value));
                miniTile.setPrefSize(cellSize, cellSize);
                miniTile.setAlignment(Pos.CENTER);
                miniTile.setFont(new Font(10));
                miniTile.setStyle("-fx-border-color: gray; -fx-background-color: lightgray;");
                
                targetGrid.add(miniTile, col, row);
            }
        }
        
        return targetGrid;
    }
    
    // 初始化棋盘
    private void initializeBoard() {
        // 创建数字列表 1-15
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= SIZE * SIZE - 1; i++) {
            numbers.add(i);
        }
        
        // 随机打乱数字
        Collections.shuffle(numbers);
        
        // 创建棋盘上的方块
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (row == SIZE - 1 && col == SIZE - 1) {
                    // 空格
                    tiles[row][col] = null;
                    emptyRow = row;
                    emptyCol = col;
                } else {
                    // 数字方块
                    int index = row * SIZE + col;
                    Button tile = createTile(numbers.get(index));
                    
                    // 使用GridPane的坐标属性来获取当前位置
                    tile.setOnAction(e -> {
                        // 查找按钮在tiles数组中的当前位置
                        for (int i = 0; i < SIZE; i++) {
                            for (int j = 0; j < SIZE; j++) {
                                if (tiles[i][j] == tile) {
                                    moveTile(i, j);
                                    return;
                                }
                            }
                        }
                    });
                    
                    tiles[row][col] = tile;
                    gameBoard.add(tile, col, row);
                }
            }
        }
    }
    
    // 创建一个数字方块
    private Button createTile(int number) {
        Button tile = new Button(String.valueOf(number));
        tile.setPrefSize(TILE_SIZE, TILE_SIZE);
        tile.setFont(new Font(24));
        return tile;
    }
    
    // 移动方块
    private void moveTile(int row, int col) {
        // 检查点击的方块是否与空格相邻
        if (isAdjacent(row, col, emptyRow, emptyCol)) {
            // 更新方块在GridPane中的位置
            GridPane.setConstraints(tiles[row][col], emptyCol, emptyRow);
            
            // 更新棋盘状态
            tiles[emptyRow][emptyCol] = tiles[row][col];
            tiles[row][col] = null;
            
            // 更新空格位置
            int tempRow = emptyRow;
            int tempCol = emptyCol;
            emptyRow = row;
            emptyCol = col;
            
            // 检查是否获胜
            if (checkWin()) {
                showWinMessage();
            }
        }
    }
    
    // 检查两个位置是否相邻
    private boolean isAdjacent(int row1, int col1, int row2, int col2) {
        return (Math.abs(row1 - row2) + Math.abs(col1 - col2)) == 1;
    }
    
    // 检查是否获胜
    private boolean checkWin() {
        // 检查空格是否在右下角
        if (emptyRow != SIZE - 1 || emptyCol != SIZE - 1) {
            return false;
        }
        
        // 检查其他方块是否按顺序排列
        int expectedValue = 1;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                // 跳过空格
                if (row == SIZE - 1 && col == SIZE - 1) {
                    continue;
                }
                
                Button tile = tiles[row][col];
                int value = Integer.parseInt(tile.getText());
                if (value != expectedValue) {
                    return false;
                }
                expectedValue++;
            }
        }
        
        return true;
    }
    
    // 显示获胜消息
    private void showWinMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("恭喜");
        alert.setHeaderText("你赢了！");
        alert.setContentText("你成功完成了15拼图游戏！");
        alert.showAndWait();
    }
    
    // 重置游戏
    private void resetGame() {
        gameBoard.getChildren().clear();
        initializeBoard();
        statusLabel.setText("游戏已重置");
    }
}