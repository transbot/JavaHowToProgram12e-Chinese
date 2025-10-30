// 疑心病
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class ScrollingMarquee extends Application {
    
    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 100;
    private static final int TEXT_SIZE = 48;
    private static final int TEXT_SPACING = 50; // 文本之间的间距
    
    private Canvas canvas;
    private GraphicsContext gc;
    private AnimationTimer timer;
    
    private String displayText = "因为自信，所以成功";
    private double scrollOffset = CANVAS_WIDTH;
    private double scrollSpeed = 2.0;
    private boolean isRunning = false;
    
    private TextField textInput;
    private Button startButton;
    private Button stopButton;
    private Label statusLabel;
    
    @Override
    public void start(Stage primaryStage) {
        // 创建主布局
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #2c3e50;");
        
        // 创建标题
        Label titleLabel = new Label("滚动跑马灯");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, 20, 0));
        
        // 创建画布
        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        canvas.setStyle("-fx-border-color: #1abc9c; -fx-border-width: 3; -fx-border-radius: 5;");
        
        // 创建控制面板
        VBox controlPanel = createControlPanel();
        
        // 创建状态栏
        statusLabel = new Label("准备就绪");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #bdc3c7;");
        HBox statusBar = new HBox(statusLabel);
        statusBar.setPadding(new Insets(10, 0, 0, 0));
        
        // 组装主布局
        VBox centerContainer = new VBox(20);
        centerContainer.setAlignment(Pos.CENTER);
        centerContainer.getChildren().addAll(canvas, controlPanel);
        
        root.setTop(titleBox);
        root.setCenter(centerContainer);
        root.setBottom(statusBar);
        
        // 创建动画计时器
        createAnimationTimer();
        
        // 初始绘制
        drawMarquee();
        
        // 创建场景
        Scene scene = new Scene(root, 900, 300);
        
        // 设置舞台
        primaryStage.setTitle("滚动跑马灯");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private VBox createControlPanel() {
        VBox controlPanel = new VBox(15);
        controlPanel.setPadding(new Insets(20));
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.setStyle("-fx-background-color: #34495e; -fx-background-radius: 10; -fx-border-color: #1abc9c; -fx-border-width: 2; -fx-border-radius: 8;");
        
        // 文本输入区域
        HBox inputArea = new HBox(10);
        inputArea.setAlignment(Pos.CENTER);
        
        Label inputLabel = new Label("显示文本:");
        inputLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #ecf0f1;");
        
        textInput = new TextField(displayText);
        textInput.setPrefWidth(300);
        textInput.setStyle("-fx-font-size: 14px;");
        
        inputArea.getChildren().addAll(inputLabel, textInput);
        
        // 控制按钮区域
        HBox buttonArea = new HBox(15);
        buttonArea.setAlignment(Pos.CENTER);
        
        startButton = createStyledButton("开始滚动", "#2ecc71");
        startButton.setOnAction(e -> startScrolling());
        
        stopButton = createStyledButton("停止滚动", "#e74c3c");
        stopButton.setOnAction(e -> stopScrolling());
        stopButton.setDisable(true);
        
        Button resetButton = createStyledButton("重置", "#3498db");
        resetButton.setOnAction(e -> resetMarquee());
        
        buttonArea.getChildren().addAll(startButton, stopButton, resetButton);
        
        controlPanel.getChildren().addAll(inputArea, buttonArea);
        
        return controlPanel;
    }
    
    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(String.format(
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20; " +
            "-fx-background-color: %s; -fx-text-fill: white; -fx-background-radius: 8;",
            color
        ));
        
        // 添加悬停效果
        button.setOnMouseEntered(e -> button.setStyle(String.format(
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20; " +
            "-fx-background-color: derive(%s, 20%%); -fx-text-fill: white; -fx-background-radius: 8;",
            color
        )));
        
        button.setOnMouseExited(e -> button.setStyle(String.format(
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20; " +
            "-fx-background-color: %s; -fx-text-fill: white; -fx-background-radius: 8;",
            color
        )));
        
        return button;
    }
    
    private void createAnimationTimer() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateMarquee();
            }
        };
    }
    
    private void startScrolling() {
        String newText = textInput.getText().trim();
        if (newText.isEmpty()) {
            statusLabel.setText("请输入要显示的文本");
            return;
        }
        
        displayText = newText;
        isRunning = true;
        startButton.setDisable(true);
        stopButton.setDisable(false);
        textInput.setDisable(true);
        
        timer.start();
        statusLabel.setText("跑马灯正在滚动显示: " + displayText);
    }
    
    private void stopScrolling() {
        isRunning = false;
        startButton.setDisable(false);
        stopButton.setDisable(true);
        textInput.setDisable(false);
        
        timer.stop();
        statusLabel.setText("跑马灯已停止");
    }
    
    private void resetMarquee() {
        stopScrolling();
        scrollOffset = CANVAS_WIDTH;
        drawMarquee();
        statusLabel.setText("跑马灯已重置");
    }
    
    private void updateMarquee() {
        scrollOffset -= scrollSpeed;
        
        // 计算文本宽度
        gc.setFont(Font.font("System", TEXT_SIZE));
        double textWidth = gc.getFont().getSize() * displayText.length();
        
        // 当文本完全滚出左侧时，从右侧重新进入
        if (scrollOffset < -textWidth) {
            scrollOffset = CANVAS_WIDTH;
        }
        
        drawMarquee();
    }
    
    private void drawMarquee() {
        // 清除画布
        gc.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        
        // 设置背景色
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        
        // 设置文本颜色和字体
        gc.setFill(Color.CYAN);
        gc.setFont(Font.font("System", TEXT_SIZE));
        
        // 计算垂直居中位置
        double textHeight = gc.getFont().getSize();
        double yOffset = (CANVAS_HEIGHT + textHeight) / 2 - 5; // 减5是为了更好的垂直居中
        
        // 绘制文本
        gc.fillText(displayText, scrollOffset, yOffset);
        
        // 计算文本宽度
        double textWidth = gc.getFont().getSize() * displayText.length();
        
        // 如果需要，绘制重复文本以实现无缝循环
        if (scrollOffset + textWidth > 0) {
            // 在右侧绘制重复文本
            gc.fillText(displayText, scrollOffset + textWidth + TEXT_SPACING, yOffset);
        }
        
        // 如果文本开始离开画布，在左侧也绘制重复文本以实现无缝循环
        if (scrollOffset < CANVAS_WIDTH && scrollOffset > -textWidth) {
            gc.fillText(displayText, scrollOffset - textWidth - TEXT_SPACING, yOffset);
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}