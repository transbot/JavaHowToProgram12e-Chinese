// 练习22.23，StarFractal.java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class StarFractal extends Application {
    // 常量定义
    private static final int MIN_LEVEL = 0;   // 最小分形层级
    private static final int MAX_LEVEL = 10;  // 最大分形层级
    private static final int CANVAS_SIZE = 600; // 画布大小
    
    // GUI组件
    private Canvas canvas;
    private Label levelLabel;
    private Button increaseButton;
    private Button decreaseButton;
    
    // 其他实例变量
    private int level = MIN_LEVEL;           // 初始分形层级
    private GraphicsContext gc;              // 用于在画布上绘制的图形上下文
    
    // 颜色数组，用于多色星形
    private Color[] colors = {
        Color.RED, Color.BLUE, Color.GREEN, Color.PURPLE, Color.ORANGE
    };

    @Override
    public void start(Stage primaryStage) {
        // 创建画布
        canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
        gc = canvas.getGraphicsContext2D();
        
        // 创建控制面板
        levelLabel = new Label("Level: " + level);
        increaseButton = new Button("+");
        decreaseButton = new Button("-");
        
        // 设置按钮事件处理
        increaseButton.setOnAction(e -> increaseLevel());
        decreaseButton.setOnAction(e -> decreaseLevel());
        
        // 创建控制面板布局
        HBox controlPanel = new HBox(10);
        controlPanel.getChildren().addAll(decreaseButton, levelLabel, increaseButton);
        
        // 创建主布局
        BorderPane root = new BorderPane();
        root.setCenter(canvas);
        root.setBottom(controlPanel);
        
        // 创建场景并显示
        Scene scene = new Scene(root);
        primaryStage.setTitle("多色星形分形");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // 初始绘制
        drawFractal();
    }
    
    // 增加层级
    private void increaseLevel() {
        if (level < MAX_LEVEL) {
            level++;
            levelLabel.setText("Level: " + level);
            drawFractal();
        }
    }
    
    // 降低层级
    private void decreaseLevel() {
        if (level > MIN_LEVEL) {
            level--;
            levelLabel.setText("Level: " + level);
            drawFractal();
        }
    }
    
    // 绘制分形星形
    private void drawFractal() {
        // 清空画布
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // 设置背景色
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // 计算中心点
        int centerX = CANVAS_SIZE / 2;
        int centerY = CANVAS_SIZE / 2;
        
        // 星形半径
        int radius = 200;
        
        // 绘制5条臂，每条臂应用Lo羽毛分形
        for (int i = 0; i < 5; i++) {
            // 计算臂的角度
            double angle = 2 * Math.PI * i / 5;
            
            // 计算臂的端点坐标
            int endX = centerX + (int)(radius * Math.cos(angle));
            int endY = centerY + (int)(radius * Math.sin(angle));
            
            // 为每条臂设置不同颜色
            gc.setStroke(colors[i]);
            
            // 绘制该臂的分形
            drawFractalArm(level, centerX, centerY, endX, endY);
        }
    }
    
    // 递归绘制分形臂
    private void drawFractalArm(int level, int xA, int yA, int xB, int yB) {
        // 基本情况：绘制连接两个给定点的直线
        if (level == 0) {
            gc.strokeLine(xA, yA, xB, yB);
        } else {
            // 递归步骤：计算新点，绘制下一层级
            // 计算(xA, yA)和(xB, yB)之间的中点
            int xC = (xA + xB) / 2;
            int yC = (yA + yB) / 2;

            // 计算第四个点(xD, yD)，该点与(xA, yA)
            // 和(xC, yC)形成一个直角在(xD, yD)的
            // 等腰直角三角形
            int xD = xA + (xC - xA) / 2 - (yC - yA) / 2;
            int yD = yA + (yC - yA) / 2 + (xC - xA) / 2;
            
            // 递归绘制分形
            drawFractalArm(level - 1, xD, yD, xA, yA); // 从D点到A点
            drawFractalArm(level - 1, xD, yD, xC, yC); // 从D点到C点
            drawFractalArm(level - 1, xD, yD, xB, yB); // 从D点到B点
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}