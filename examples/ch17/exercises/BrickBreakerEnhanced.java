import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BrickBreakerEnhanced extends Application {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int BRICK_ROWS = 5;
    private static final int BRICK_COLS = 10;
    private static final int BRICK_WIDTH = 75;
    private static final int BRICK_HEIGHT = 20;
    private static final int BRICK_PADDING = 5;
    private static final int BALL_RADIUS = 10;
    private static final int INITIAL_PADDLE_WIDTH = 100;
    private static final int PADDLE_HEIGHT = 15;
    private static final int INITIAL_BALL_SPEED = 2;
    private static final int INITIAL_LIVES = 3;
    
    private Pane gamePane;
    private Circle ball;
    private Rectangle paddle;
    private List<Brick> bricks;
    private Timeline gameLoop;
    private int dx = INITIAL_BALL_SPEED;
    private int dy = INITIAL_BALL_SPEED;
    private int lives = INITIAL_LIVES;
    private int score = 0;
    private int level = 1;
    private int lastUpgradeScore = 0; // 记录上一次升级时的分数
    private Label livesLabel;
    private Label scoreLabel;
    private Label levelLabel;
    private boolean isBallLaunched = false;
    private double initialPaddleWidth = INITIAL_PADDLE_WIDTH;
    private boolean isContinuousMode = true;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        
        // 创建游戏面板
        gamePane = new Pane();
        gamePane.setPrefSize(WIDTH, HEIGHT);
        gamePane.setStyle("-fx-background-color: #001100;");
        
        // 创建标签
        livesLabel = new Label("生命: " + lives);
        scoreLabel = new Label("分数: " + score);
        levelLabel = new Label("等级: " + level);
        
        // 设置标签样式 - 增强可见性
        for (Label label : new Label[] {livesLabel, scoreLabel, levelLabel}) {
            label.setTextFill(Color.YELLOW); // 使用黄色文本提高可见度
            label.setFont(Font.font("Arial", 24)); // 使用更大的字体和具体字体
            label.setVisible(true); // 明确设置为可见
        }
        
        // 使用HBox水平排列标签，使它们更紧凑且居中显示
        HBox labelsBox = new HBox(20);
        labelsBox.setAlignment(Pos.CENTER); // 居中对齐
        labelsBox.setPadding(new Insets(10)); // 添加内边距
        labelsBox.getChildren().addAll(livesLabel, scoreLabel, levelLabel);
        labelsBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);"); // 添加半透明背景
        
        // 将游戏面板和标签添加到根节点
        root.getChildren().addAll(labelsBox, gamePane);
        
        // 初始化游戏元素
        initializeGame();
        
        // 创建场景并显示
        Scene scene = new Scene(root);
        primaryStage.setTitle("增强型打砖块游戏");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // 添加鼠标事件监听器
        scene.setOnMouseMoved(event -> {
            double mouseX = event.getX();
            // 确保挡板不会移出游戏面板
            double paddleX = Math.max(0, Math.min(WIDTH - paddle.getWidth(), mouseX - paddle.getWidth() / 2));
            paddle.setX(paddleX);
            
            // 如果球还未发射，让球随挡板移动
            if (!isBallLaunched) {
                ball.setCenterX(paddleX + paddle.getWidth() / 2);
                ball.setCenterY(paddle.getY() - BALL_RADIUS - 1);
            }
        });
        
        // 单击鼠标发射球
        scene.setOnMouseClicked(event -> {
            if (!isBallLaunched) {
                isBallLaunched = true;
                dy = -INITIAL_BALL_SPEED;
                startGameLoop();
            }
        });
    }
    
    private void initializeGame() {
        // 清空游戏面板
        gamePane.getChildren().clear();
        
        // 创建球
        ball = new Circle(WIDTH / 2, HEIGHT - 50, BALL_RADIUS, Color.WHITE);
        
        // 创建挡板
        paddle = new Rectangle(
                (WIDTH - initialPaddleWidth) / 2,
                HEIGHT - 40,
                initialPaddleWidth,
                PADDLE_HEIGHT
        );
        paddle.setFill(Color.BLUE);
        
        // 创建砖块
        bricks = new ArrayList<>();
        createBricks();
        
        // 将游戏元素添加到面板
        gamePane.getChildren().addAll(ball, paddle);
        for (Brick brick : bricks) {
            gamePane.getChildren().add(brick.getRectangle());
        }
        
        // 重置球的状态
        resetBallPosition();
        
        // 更新标签
        updateLabels();
    }
    
    private void createBricks() {
        bricks.clear();
        int startX = (WIDTH - (BRICK_COLS * (BRICK_WIDTH + BRICK_PADDING) - BRICK_PADDING)) / 2;
        int startY = 50;
        
        for (int row = 0; row < BRICK_ROWS; row++) {
            for (int col = 0; col < BRICK_COLS; col++) {
                int x = startX + col * (BRICK_WIDTH + BRICK_PADDING);
                int y = startY + row * (BRICK_HEIGHT + BRICK_PADDING);
                
                // 根据行数确定砖块颜色和分数
                Color color;
                int brickScore;
                switch (row) {
                    case 0: color = Color.RED; brickScore = 100; break;
                    case 1: color = Color.ORANGE; brickScore = 70; break;
                    case 2: color = Color.YELLOW; brickScore = 50; break;
                    case 3: color = Color.GREEN; brickScore = 30; break;
                    case 4: color = Color.CYAN; brickScore = 20; break;
                    default: color = Color.WHITE; brickScore = 10;
                }
                
                Brick brick = new Brick(x, y, BRICK_WIDTH, BRICK_HEIGHT, color, brickScore);
                bricks.add(brick);
            }
        }
    }
    
    private void startGameLoop() {
        gameLoop = new Timeline(new KeyFrame(Duration.millis(10), event -> {
            // 移动球
            ball.setCenterX(ball.getCenterX() + dx);
            ball.setCenterY(ball.getCenterY() + dy);
            
            // 检测边界碰撞
            checkBoundaryCollision();
            
            // 检测挡板碰撞
            checkPaddleCollision();
            
            // 检测砖块碰撞
            checkBrickCollision();
            
            // 检测游戏结束条件
            checkGameOver();
        }));
        
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
    }
    
    private void checkBoundaryCollision() {
        Bounds bounds = gamePane.getBoundsInLocal();
        
        // 防止球直上直下（dx为0）导致的死循环
        if (Math.abs(dx) < 1) {
            dx = (dx >= 0) ? INITIAL_BALL_SPEED : -INITIAL_BALL_SPEED;
        }
        
        // 左右边界碰撞
        if (ball.getCenterX() - BALL_RADIUS <= bounds.getMinX() || 
            ball.getCenterX() + BALL_RADIUS >= bounds.getMaxX()) {
            dx *= -1;
        }
        
        // 上边界碰撞
        if (ball.getCenterY() - BALL_RADIUS <= bounds.getMinY()) {
            dy *= -1;
        }
        
        // 下边界碰撞（失去生命）
        if (ball.getCenterY() + BALL_RADIUS >= bounds.getMaxY()) {
            lives--;
            updateLabels();
            
            if (lives <= 0) {
                gameOver(false);
            } else {
                resetBallPosition();
            }
        }
    }
    
    private void checkPaddleCollision() {
        // 使用Shape.intersect检测球与挡板的碰撞
        if (ball.getBoundsInParent().intersects(paddle.getBoundsInParent()) && dy > 0) {
            dy = -Math.abs(dy); // 确保球向上反弹
            
            // 根据撞击点调整球的水平速度
            double paddleCenter = paddle.getX() + paddle.getWidth() / 2;
            double ballCenter = ball.getCenterX();
            double distanceFromCenter = ballCenter - paddleCenter;
            
            // 将挡板分为三个区域
            double thirdOfPaddle = paddle.getWidth() / 3;
            
            if (distanceFromCenter < -thirdOfPaddle) {
                // 左区域 - 球向左反弹更快
                dx = -5;
            } else if (distanceFromCenter > thirdOfPaddle) {
                // 右区域 - 球向右反弹更快
                dx = 5;
            } else {
                // 中心区域 - 保持原方向，但速度适中
                dx = (dx > 0) ? 2 : -2;
            }
        }
    }
    
    private void checkBrickCollision() {
        boolean[] rowCompleted = new boolean[BRICK_ROWS];
        for (int i = 0; i < BRICK_ROWS; i++) {
            rowCompleted[i] = true;
        }
        
        for (Brick brick : bricks) {
            if (brick.isVisible() && ball.getBoundsInParent().intersects(brick.getRectangle().getBoundsInParent())) {
                // 移除砖块
                brick.setVisible(false);
                gamePane.getChildren().remove(brick.getRectangle());
                
                // 添加分数
                score += brick.getScore();
                updateLabels();
                
                // 立即检查升级条件
                checkGameOver();
                
                // 球反弹
                dy *= -1;
                
                break; // 一次只处理一个砖块碰撞
            }
        }
        
        // 检查是否有完整的行被消除
        if (isContinuousMode) {
            checkAndHandleCompletedRows();
        }
    }
    
    private void checkAndHandleCompletedRows() {
        for (int row = 0; row < BRICK_ROWS; row++) {
            boolean isRowComplete = true;
            int bricksInRow = 0;
            
            // 检查当前行是否所有砖块都被消除
            for (Brick brick : bricks) {
                if (brick.getRow() == row) {
                    bricksInRow++;
                    if (brick.isVisible()) {
                        isRowComplete = false;
                        break;
                    }
                }
            }
            
            // 如果当前行是完整的
            if (isRowComplete && bricksInRow > 0) {
                // 将所有上面的砖块下移一行
                moveBricksDown(row);
                
                // 在顶部创建新的一行砖块
                createNewTopRow();
                
                // 添加额外奖励分数
                score += 500;
                updateLabels();
                
                // 立即检查升级条件
                checkGameOver();
                
                break; // 一次只处理一行
            }
        }
    }
    
    private void moveBricksDown(int startingRow) {
        for (Brick brick : bricks) {
            if (brick.getRow() < startingRow && brick.isVisible()) {
                int newY = (int) (brick.getRectangle().getY() + BRICK_HEIGHT + BRICK_PADDING);
                brick.getRectangle().setY(newY);
                brick.setRow(brick.getRow() + 1);
            }
        }
    }
    
    private void createNewTopRow() {
        int startX = (WIDTH - (BRICK_COLS * (BRICK_WIDTH + BRICK_PADDING) - BRICK_PADDING)) / 2;
        int startY = 50;
        
        for (int col = 0; col < BRICK_COLS; col++) {
            int x = startX + col * (BRICK_WIDTH + BRICK_PADDING);
            int y = startY;
            
            // 随机颜色和分数
            Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN};
            int colorIndex = new Random().nextInt(colors.length);
            Color color = colors[colorIndex];
            int brickScore = 10 + colorIndex * 18; // 根据颜色给分
            
            Brick brick = new Brick(x, y, BRICK_WIDTH, BRICK_HEIGHT, color, brickScore, 0);
            bricks.add(brick);
            gamePane.getChildren().add(brick.getRectangle());
        }
    }
    
    private void checkGameOver() {
        // 检查是否所有可见砖块都被消除
        boolean allBricksDestroyed = true;
        for (Brick brick : bricks) {
            if (brick.isVisible()) {
                allBricksDestroyed = false;
                break;
            }
        }
        
        // 升级条件1: 所有砖块都被消除
        // 升级条件2: 达到特定分数（每5000分升级一次）
        if (allBricksDestroyed || (score >= lastUpgradeScore + 5000)) {
            // 升级
            level++;
            lastUpgradeScore = score;
            
            // 增加球的速度
            double speedMultiplier = 1.1; // 增加10%
            dx = (int) (Math.signum(dx) * Math.abs(dx) * speedMultiplier);
            dy = (int) (Math.signum(dy) * Math.abs(dy) * speedMultiplier);
            
            // 减小挡板尺寸
            initialPaddleWidth *= 0.95; // 减小5%
            
            // 显示升级信息
            Label levelUpLabel = new Label("恭喜升级到第" + level + "关！");
            levelUpLabel.setTextFill(Color.YELLOW);
            levelUpLabel.setFont(Font.font("Arial", 30));
            levelUpLabel.setLayoutX(WIDTH / 2 - 150);
            levelUpLabel.setLayoutY(HEIGHT / 2 - 50);
            gamePane.getChildren().add(levelUpLabel);
            
            // 短暂暂停后重新初始化游戏
            gameLoop.stop();
            
            // 2秒后重新开始游戏
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                Platform.runLater(() -> {
                    gamePane.getChildren().remove(levelUpLabel);
                    initializeGame();
                });
            }).start();
        }
    }
    
    private void resetBallPosition() {
        isBallLaunched = false;
        ball.setCenterX(paddle.getX() + paddle.getWidth() / 2);
        ball.setCenterY(paddle.getY() - BALL_RADIUS - 1);
        dx = INITIAL_BALL_SPEED;
        dy = INITIAL_BALL_SPEED;
        
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }
    
    private void updateLabels() {
        livesLabel.setText("生命: " + lives);
        scoreLabel.setText("分数: " + score);
        levelLabel.setText("等级: " + level);
    }
    
    private void gameOver(boolean isWin) {
        gameLoop.stop();
        Label gameOverLabel = new Label(isWin ? "恭喜你赢了！" : "游戏结束！");
        gameOverLabel.setTextFill(Color.RED);
        gameOverLabel.setFont(Font.font(40));
        gameOverLabel.setLayoutX(WIDTH / 2 - 150);
        gameOverLabel.setLayoutY(HEIGHT / 2 - 50);
        
        Label finalScoreLabel = new Label("最终分数: " + score);
        finalScoreLabel.setTextFill(Color.WHITE);
        finalScoreLabel.setFont(Font.font(30));
        finalScoreLabel.setLayoutX(WIDTH / 2 - 100);
        finalScoreLabel.setLayoutY(HEIGHT / 2 + 50);
        
        gamePane.getChildren().addAll(gameOverLabel, finalScoreLabel);
    }
    
    // 砖块类
    private class Brick {
        private Rectangle rectangle;
        private int score;
        private boolean visible;
        private int row;
        
        public Brick(int x, int y, int width, int height, Color color, int score) {
            this(x, y, width, height, color, score, -1);
        }
        
        public Brick(int x, int y, int width, int height, Color color, int score, int row) {
            this.rectangle = new Rectangle(x, y, width, height);
            this.rectangle.setFill(color);
            this.rectangle.setStroke(Color.BLACK);
            this.rectangle.setStrokeWidth(1);
            this.score = score;
            this.visible = true;
            this.row = row;
            
            // 如果没有指定行号，根据y坐标计算
            if (row == -1) {
                int startY = 50;
                this.row = (y - startY) / (BRICK_HEIGHT + BRICK_PADDING);
            }
        }
        
        public Rectangle getRectangle() {
            return rectangle;
        }
        
        public int getScore() {
            return score;
        }
        
        public boolean isVisible() {
            return visible;
        }
        
        public void setVisible(boolean visible) {
            this.visible = visible;
        }
        
        public int getRow() {
            return row;
        }
        
        public void setRow(int row) {
            this.row = row;
        }
    }
}