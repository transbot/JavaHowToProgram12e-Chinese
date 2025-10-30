import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.Random;

public class SlotMachine extends Application {
    
    private static final int CANVAS_WIDTH = 600;
    private static final int CANVAS_HEIGHT = 300;
    private static final int NUM_WHEELS = 3;
    private static final int SYMBOLS_PER_WHEEL = 6;
    private static final double WHEEL_WIDTH = 150;
    private static final double WHEEL_HEIGHT = 200;
    private static final double SPACING = 50;
    
    // Unicode水果符号
    private final String[] SYMBOL_CHARS = {"🍒", "🍋", "🍊", "🍉", "🍇", "🔔"};
    private final String[] SYMBOL_NAMES = {"樱桃", "柠檬", "橙子", "西瓜", "葡萄", "铃铛"};
    
    private Canvas canvas;
    private GraphicsContext gc;
    private Button spinButton;
    private Label balanceLabel;
    private Label resultLabel;
    private Stage primaryStage; // 添加主舞台引用
    
    // 游戏状态
    private int balance = 100;
    private final int BET_AMOUNT = 10;
    private final int WIN_AMOUNT = 50;
    
    // 轮子状态
    private boolean[] isSpinning = new boolean[NUM_WHEELS];
    private int[] currentSymbols = new int[NUM_WHEELS];
    private int[] targetSymbols = new int[NUM_WHEELS];
    private double[] spinSpeeds = new double[NUM_WHEELS];
    private int wheelsStopped = 0;
    
    // 动画时间轴
    private Timeline spinTimeline;
    
    // 音效
    private AudioClip spinSound;
    private AudioClip stopSound;
    private AudioClip winSound;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage; // 保存主舞台引用
        
        // 加载音效
        loadSounds();
        
        // 创建Canvas
        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        
        // 创建UI控件
        createControls();
        
        // 创建布局
        VBox infoBox = new VBox(10, balanceLabel, resultLabel);
        infoBox.setStyle("-fx-alignment: center; -fx-padding: 10;");
        
        HBox buttonBox = new HBox(spinButton);
        buttonBox.setStyle("-fx-alignment: center; -fx-padding: 10;");
        
        BorderPane root = new BorderPane();
        root.setTop(infoBox);
        root.setCenter(canvas);
        root.setBottom(buttonBox);
        
        // 创建场景
        Scene scene = new Scene(root, CANVAS_WIDTH, CANVAS_HEIGHT + 120);
        
        // 设置舞台
        primaryStage.setTitle("老虎机游戏 - 积分: " + balance);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        // 初始绘制
        drawMachine();
        
        // 创建动画时间轴
        createSpinTimeline();
    }
    
    private void createControls() {
        // 创建旋转按钮
        spinButton = new Button("旋转 (下注 " + BET_AMOUNT + " 积分)");
        spinButton.setFont(Font.font(16));
        spinButton.setOnAction(e -> startSpinning());
        
        // 创建标签
        balanceLabel = new Label("当前积分: " + balance);
        balanceLabel.setFont(Font.font(18));
        
        resultLabel = new Label("点击旋转开始游戏!");
        resultLabel.setFont(Font.font(14));
    }
    
    private void loadSounds() {
        try {
            // 加载MP3音效文件
            spinSound = new AudioClip(new File("sounds/spin.mp3").toURI().toString());
            stopSound = new AudioClip(new File("sounds/stop.mp3").toURI().toString());
            winSound = new AudioClip(new File("sounds/win.mp3").toURI().toString());
            
            // 设置旋转音效循环播放
            spinSound.setCycleCount(AudioClip.INDEFINITE);
        } catch (Exception e) {
            System.err.println("无法加载音效文件: " + e.getMessage());
            System.out.println("请确保在sounds目录下有spin.mp3, stop.mp3和win.mp3文件");
        }
    }
    
    private void createSpinTimeline() {
        spinTimeline = new Timeline(
            new KeyFrame(Duration.millis(50), e -> updateSpinning())
        );
        spinTimeline.setCycleCount(Animation.INDEFINITE);
    }
    
    private void startSpinning() {
        // 检查积分是否足够
        if (balance < BET_AMOUNT) {
            resultLabel.setText("积分不足! 无法继续游戏。");
            return;
        }
        
        // 扣除下注金额
        balance -= BET_AMOUNT;
        updateBalanceDisplay();
        
        // 重置状态
        for (int i = 0; i < NUM_WHEELS; i++) {
            isSpinning[i] = true;
            spinSpeeds[i] = 15 + Math.random() * 10;
            targetSymbols[i] = new Random().nextInt(SYMBOLS_PER_WHEEL);
        }
        wheelsStopped = 0;
        spinButton.setDisable(true);
        resultLabel.setText("旋转中...");
        
        // 播放旋转音效（循环）
        if (spinSound != null) {
            spinSound.play();
        }
        
        // 启动动画
        spinTimeline.play();
    }
    
    private void updateSpinning() {
        boolean allStopped = true;
        
        for (int i = 0; i < NUM_WHEELS; i++) {
            if (isSpinning[i]) {
                // 更新当前符号（模拟旋转）
                currentSymbols[i] = (currentSymbols[i] + 1) % SYMBOLS_PER_WHEEL;
                
                // 逐渐减速
                spinSpeeds[i] *= 0.98;
                
                // 当速度足够慢且接近目标符号时停止
                if (spinSpeeds[i] < 0.5 && currentSymbols[i] == targetSymbols[i]) {
                    isSpinning[i] = false;
                    wheelsStopped++;
                    
                    // 播放停止音效
                    if (stopSound != null) {
                        stopSound.play();
                    }
                    
                    // 如果所有轮子都停止了
                    if (wheelsStopped == NUM_WHEELS) {
                        checkWin();
                        spinButton.setDisable(false);
                        
                        // 停止旋转音效
                        if (spinSound != null) {
                            spinSound.stop();
                        }
                    }
                }
            }
            
            if (isSpinning[i]) {
                allStopped = false;
            }
        }
        
        // 重绘
        drawMachine();
        
        // 如果所有轮子都停止了，停止动画
        if (allStopped) {
            spinTimeline.stop();
        }
    }
    
    private void checkWin() {
        boolean win = true;
        for (int i = 1; i < NUM_WHEELS; i++) {
            if (currentSymbols[i] != currentSymbols[0]) {
                win = false;
                break;
            }
        }
        
        if (win) {
            // 增加获胜金额
            balance += WIN_AMOUNT;
            updateBalanceDisplay();
            
            // 播放胜利音效
            if (winSound != null) {
                winSound.play();
            }
            
            resultLabel.setText("恭喜获胜! 获得 " + WIN_AMOUNT + " 积分!");
        } else {
            resultLabel.setText("未获胜，再试一次!");
        }
    }
    
    private void updateBalanceDisplay() {
        balanceLabel.setText("当前积分: " + balance);
        if (primaryStage != null) {
            primaryStage.setTitle("老虎机游戏 - 积分: " + balance);
        }
    }
    
    private void drawMachine() {
        // 清除画布
        gc.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        
        // 绘制背景
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        
        // 绘制每个轮子
        for (int i = 0; i < NUM_WHEELS; i++) {
            double x = SPACING + i * (WHEEL_WIDTH + SPACING);
            double y = (CANVAS_HEIGHT - WHEEL_HEIGHT) / 2;
            
            // 绘制轮子背景
            gc.setFill(Color.GRAY);
            gc.fillRoundRect(x, y, WHEEL_WIDTH, WHEEL_HEIGHT, 20, 20);
            gc.setStroke(Color.BLACK);
            gc.strokeRoundRect(x, y, WHEEL_WIDTH, WHEEL_HEIGHT, 20, 20);
            
            // 绘制当前符号
            drawSymbol(i, x, y);
            
            // 如果轮子正在旋转，添加旋转效果
            if (isSpinning[i]) {
                gc.setFill(Color.rgb(255, 255, 255, 0.3));
                gc.fillRoundRect(x, y, WHEEL_WIDTH, WHEEL_HEIGHT, 20, 20);
            }
        }
    }
    
    private void drawSymbol(int wheelIndex, double x, double y) {
        int symbolIndex = currentSymbols[wheelIndex];
        String symbolChar = SYMBOL_CHARS[symbolIndex];
        
        // 设置字体大小
        gc.setFont(Font.font(80));
        
        // 根据符号类型设置颜色
        switch(symbolIndex) {
            case 0: gc.setFill(Color.RED); break;     // 樱桃
            case 1: gc.setFill(Color.YELLOW); break;  // 柠檬
            case 2: gc.setFill(Color.ORANGE); break;  // 橙子
            case 3: gc.setFill(Color.GREEN); break;   // 西瓜
            case 4: gc.setFill(Color.PURPLE); break;  // 葡萄
            case 5: gc.setFill(Color.GOLD); break;    // 铃铛
        }
        
        // 计算文本居中位置
        double textWidth = 80;
        double textHeight = 80;
        double textX = x + (WHEEL_WIDTH - textWidth) / 2;
        double textY = y + (WHEEL_HEIGHT + textHeight) / 2 - 10;
        
        // 绘制符号
        gc.fillText(symbolChar, textX, textY);
        
        // 绘制符号名称
        gc.setFont(Font.font(14));
        gc.setFill(Color.WHITE);
        String symbolName = SYMBOL_NAMES[symbolIndex];
        double nameWidth = symbolName.length() * 8;
        double nameX = x + (WHEEL_WIDTH - nameWidth) / 2;
        double nameY = y + WHEEL_HEIGHT - 10;
        gc.fillText(symbolName, nameX, nameY);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}