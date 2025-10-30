import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnakeGame extends Application {
    
    // 游戏常量
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final int CELL_SIZE = 20;
    private static final int GRID_WIDTH = WIDTH / CELL_SIZE;
    private static final int GRID_HEIGHT = HEIGHT / CELL_SIZE;
    private static final int INITIAL_SNAKE_LENGTH = 3;
    private static final int GAME_SPEED = 10;
    
    // 游戏状态
    private enum GameState {
        READY, RUNNING, GAME_OVER
    }
    
    // 方向枚举
    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
    
    // 游戏变量
    private GameState gameState = GameState.READY;
    private Direction currentDirection = Direction.RIGHT;
    private Direction nextDirection = Direction.RIGHT;
    private List<Point> snake = new ArrayList<>();
    private Point food;
    private int score = 0;
    private int frameCount = 0;
    
    // JavaFX组件
    private Canvas canvas;
    private GraphicsContext gc;
    private AnimationTimer gameLoop;
    
    // 点类
    private static class Point {
        int x, y;
        
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Point point = (Point) obj;
            return x == point.x && y == point.y;
        }
    }
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // 初始化UI
            Pane root = new Pane();
            canvas = new Canvas(WIDTH, HEIGHT);
            gc = canvas.getGraphicsContext2D();
            root.getChildren().add(canvas);
            
            Scene scene = new Scene(root, WIDTH, HEIGHT);
            primaryStage.setTitle("JavaFX 贪吃蛇游戏");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            
            // 先创建游戏循环，然后再初始化游戏
            createGameLoop();
            
            // 初始化游戏
            initializeGame();
            
            // 设置键盘事件处理
            scene.setOnKeyPressed(event -> {
                KeyCode keyCode = event.getCode();
                
                if (gameState == GameState.READY && keyCode == KeyCode.SPACE) {
                    startGame();
                } else if (gameState == GameState.GAME_OVER && keyCode == KeyCode.SPACE) {
                    restartGame();
                } else if (gameState == GameState.RUNNING) {
                    handleKeyPress(keyCode);
                }
            });
            
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("启动错误: " + e.getMessage());
        }
    }
    
    // 创建游戏循环
    private void createGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                frameCount++;
                if (frameCount >= GAME_SPEED) {
                    updateGame();
                    renderGame();
                    frameCount = 0;
                }
            }
        };
    }
    
    // 初始化游戏
    private void initializeGame() {
        gameState = GameState.READY;
        currentDirection = Direction.RIGHT;
        nextDirection = Direction.RIGHT;
        score = 0;
        frameCount = 0;
        
        // 初始化蛇
        snake.clear();
        int startX = GRID_WIDTH / 2;
        int startY = GRID_HEIGHT / 2;
        
        for (int i = 0; i < INITIAL_SNAKE_LENGTH; i++) {
            snake.add(new Point(startX - i, startY));
        }
        
        // 生成食物
        generateFood();
        
        // 启动游戏循环
        if (gameLoop != null) {
            gameLoop.start();
        }
        
        // 初始渲染
        renderGame();
    }
    
    // 开始游戏
    private void startGame() {
        gameState = GameState.RUNNING;
    }
    
    // 重新开始游戏
    private void restartGame() {
        // 停止当前游戏循环
        if (gameLoop != null) {
            gameLoop.stop();
        }
        
        // 重新初始化游戏
        initializeGame();
        startGame();
    }
    
    // 处理键盘输入
    private void handleKeyPress(KeyCode keyCode) {
        switch (keyCode) {
            case UP:
                if (currentDirection != Direction.DOWN) {
                    nextDirection = Direction.UP;
                }
                break;
            case DOWN:
                if (currentDirection != Direction.UP) {
                    nextDirection = Direction.DOWN;
                }
                break;
            case LEFT:
                if (currentDirection != Direction.RIGHT) {
                    nextDirection = Direction.LEFT;
                }
                break;
            case RIGHT:
                if (currentDirection != Direction.LEFT) {
                    nextDirection = Direction.RIGHT;
                }
                break;
        }
    }
    
    // 更新游戏状态
    private void updateGame() {
        if (gameState != GameState.RUNNING) {
            return;
        }
        
        // 更新方向
        currentDirection = nextDirection;
        
        // 计算新的头部位置
        Point head = snake.get(0);
        Point newHead = new Point(head.x, head.y);
        
        switch (currentDirection) {
            case UP:
                newHead.y--;
                break;
            case DOWN:
                newHead.y++;
                break;
            case LEFT:
                newHead.x--;
                break;
            case RIGHT:
                newHead.x++;
                break;
        }
        
        // 检查碰撞
        if (isCollision(newHead)) {
            gameState = GameState.GAME_OVER;
            return;
        }
        
        // 移动蛇
        snake.add(0, newHead);
        
        // 检查是否吃到食物
        if (newHead.equals(food)) {
            score += 10;
            generateFood();
        } else {
            // 如果没有吃到食物，移除尾部
            snake.remove(snake.size() - 1);
        }
    }
    
    // 检查碰撞
    private boolean isCollision(Point point) {
        // 检查墙壁碰撞
        if (point.x < 0 || point.x >= GRID_WIDTH || point.y < 0 || point.y >= GRID_HEIGHT) {
            return true;
        }
        
        // 检查自身碰撞
        for (int i = 0; i < snake.size(); i++) {
            if (point.equals(snake.get(i))) {
                return true;
            }
        }
        
        return false;
    }
    
    // 生成食物
    private void generateFood() {
        Random random = new Random();
        Point newFood;
        
        do {
            int x = random.nextInt(GRID_WIDTH);
            int y = random.nextInt(GRID_HEIGHT);
            newFood = new Point(x, y);
        } while (snake.contains(newFood));
        
        food = newFood;
    }
    
    // 渲染游戏
    private void renderGame() {
        // 清空画布
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        
        // 绘制网格线
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(0.5);
        
        for (int i = 0; i <= GRID_WIDTH; i++) {
            gc.strokeLine(i * CELL_SIZE, 0, i * CELL_SIZE, HEIGHT);
        }
        
        for (int i = 0; i <= GRID_HEIGHT; i++) {
            gc.strokeLine(0, i * CELL_SIZE, WIDTH, i * CELL_SIZE);
        }
        
        // 绘制食物
        gc.setFill(Color.RED);
        gc.fillRect(food.x * CELL_SIZE, food.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        
        // 绘制蛇
        for (int i = 0; i < snake.size(); i++) {
            Point segment = snake.get(i);
            
            // 蛇头用不同颜色
            if (i == 0) {
                gc.setFill(Color.GREEN);
            } else {
                gc.setFill(Color.LIMEGREEN);
            }
            
            gc.fillRect(segment.x * CELL_SIZE, segment.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            
            // 添加边框
            gc.setStroke(Color.DARKGREEN);
            gc.setLineWidth(1);
            gc.strokeRect(segment.x * CELL_SIZE, segment.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
        
        // 绘制分数
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(16));
        gc.fillText("分数: " + score, 28, 20);
        
        // 绘制游戏状态信息
        if (gameState == GameState.READY) {
            drawCenteredText("贪吃蛇游戏", 30);
            drawCenteredText("按 SPACE 键开始游戏", 0);
            drawCenteredText("使用方向键控制", -30);
        } else if (gameState == GameState.GAME_OVER) {
            drawCenteredText("游戏结束!", 30);
            drawCenteredText("最终得分: " + score, 0);
            drawCenteredText("按 SPACE 键重新开始", -30);
        }
    }
    
    // 绘制居中文本
    private void drawCenteredText(String text, double yOffset) {
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(24));
        gc.fillText(text, WIDTH / 2, HEIGHT / 2 + yOffset);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}