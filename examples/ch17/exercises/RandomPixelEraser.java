import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Random;

public class RandomPixelEraser extends Application {

    private Canvas canvas;
    private GraphicsContext gc;
    private PixelWriter pixelWriter;
    private Image originalImage;
    private Color[][] originalPixels;
    private boolean[][] erasedPixels;
    
    private Label statusLabel;
    private Button startButton;
    private Button pauseButton;
    private Button resetButton;
    private Button uploadButton;
    private ComboBox<String> eraseModeComboBox;
    private Slider speedSlider;
    private ProgressBar progressBar;
    
    private AnimationTimer eraserTimer;
    private Random random;
    private int erasedCount = 0;
    private int totalPixels = 0;
    private double eraseThreshold = 0.8;
    private boolean isPaused = false;
    
    // 擦除模式
    private enum EraseMode {
        PIXEL, LINE, CIRCLE, RECTANGLE
    }
    
    private EraseMode currentMode = EraseMode.PIXEL;
    
    // 默认图像路径
    private String defaultImagePath = "../images/purpleflowers.png";
    
    @Override
    public void start(Stage primaryStage) {
        // 创建主布局
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #2c3e50;");
        
        // 创建顶部控制面板
        HBox topControlPanel = createTopControlPanel(primaryStage);
        
        // 创建画布区域
        VBox canvasContainer = createCanvasContainer();
        
        // 创建底部状态面板
        HBox bottomStatusPanel = createBottomStatusPanel();
        
        // 组装主布局
        root.setTop(topControlPanel);
        root.setCenter(canvasContainer);
        root.setBottom(bottomStatusPanel);
        
        // 初始化随机数生成器
        random = new Random();
        
        // 创建擦除计时器
        createEraserTimer();
        
        // 加载默认图像
        loadDefaultImage();
        
        // 创建场景
        Scene scene = new Scene(root, 1000, 800);
        
        // 设置舞台
        primaryStage.setTitle("随机像素擦除器 - 支持随时重置");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private HBox createTopControlPanel(Stage primaryStage) {
        HBox topPanel = new HBox(15);
        topPanel.setPadding(new Insets(15));
        topPanel.setAlignment(Pos.CENTER);
        topPanel.setStyle("-fx-background-color: #34495e; -fx-background-radius: 10; -fx-border-color: #1abc9c; -fx-border-width: 2; -fx-border-radius: 8;");
        
        // 创建标题
        Label titleLabel = new Label("随机像素擦除器");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");
        
        // 将标题和控件分开
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        
        // 创建文件操作按钮 - 上传按钮始终可用
        uploadButton = createStyledButton("上传新图像", "#3498db");
        uploadButton.setOnAction(e -> uploadImage(primaryStage));
        
        startButton = createStyledButton("开始擦除", "#2ecc71");
        startButton.setOnAction(e -> startErasing());
        startButton.setDisable(true);
        
        pauseButton = createStyledButton("暂停", "#e74c3c");
        pauseButton.setOnAction(e -> togglePause());
        pauseButton.setDisable(true);
        
        resetButton = createStyledButton("重置当前图像", "#e67e22");
        resetButton.setOnAction(e -> resetCurrentImage());
        resetButton.setDisable(true);
        
        topPanel.getChildren().addAll(titleLabel, uploadButton, startButton, pauseButton, resetButton);
        
        return topPanel;
    }
    
    private VBox createCanvasContainer() {
        VBox canvasContainer = new VBox(10);
        canvasContainer.setPadding(new Insets(10));
        canvasContainer.setAlignment(Pos.CENTER);
        canvasContainer.setStyle("-fx-background-color: #34495e; -fx-background-radius: 10;");
        
        // 创建画布
        canvas = new Canvas(800, 500);
        gc = canvas.getGraphicsContext2D();
        pixelWriter = gc.getPixelWriter();
        
        // 创建画布边框
        canvas.setStyle("-fx-border-color: #1abc9c; -fx-border-width: 3; -fx-border-radius: 5;");
        
        // 创建模式控制面板
        HBox modeControlPanel = createModeControlPanel();
        
        canvasContainer.getChildren().addAll(canvas, modeControlPanel);
        
        return canvasContainer;
    }
    
    private HBox createModeControlPanel() {
        HBox modePanel = new HBox(20);
        modePanel.setPadding(new Insets(15));
        modePanel.setAlignment(Pos.CENTER);
        modePanel.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 10;");
        
        // 擦除模式选择
        VBox modeSelection = new VBox(5);
        modeSelection.setAlignment(Pos.CENTER);
        
        Label modeLabel = new Label("擦除模式");
        modeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");
        
        eraseModeComboBox = new ComboBox<>();
        eraseModeComboBox.getItems().addAll("单个像素", "随机线条", "随机圆形", "随机矩形");
        eraseModeComboBox.setValue("单个像素");
        eraseModeComboBox.setStyle("-fx-font-size: 14px; -fx-pref-width: 150;");
        eraseModeComboBox.setOnAction(e -> {
            switch (eraseModeComboBox.getValue()) {
                case "单个像素": currentMode = EraseMode.PIXEL; break;
                case "随机线条": currentMode = EraseMode.LINE; break;
                case "随机圆形": currentMode = EraseMode.CIRCLE; break;
                case "随机矩形": currentMode = EraseMode.RECTANGLE; break;
            }
            updateStatus("已切换到 " + eraseModeComboBox.getValue() + " 模式");
        });
        
        modeSelection.getChildren().addAll(modeLabel, eraseModeComboBox);
        
        // 速度控制
        VBox speedControl = new VBox(5);
        speedControl.setAlignment(Pos.CENTER);
        
        Label speedLabel = new Label("擦除速度");
        speedLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");
        
        speedSlider = new Slider(1, 10, 5);
        speedSlider.setPrefWidth(200);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(1);
        speedSlider.setSnapToTicks(true);
        speedSlider.setStyle("-fx-control-inner-background: #3498db;");
        
        HBox speedLabels = new HBox();
        speedLabels.setAlignment(Pos.CENTER);
        Label slowLabel = new Label("慢");
        slowLabel.setStyle("-fx-text-fill: #bdc3c7;");
        Label fastLabel = new Label("快");
        fastLabel.setStyle("-fx-text-fill: #bdc3c7;");
        HBox.setHgrow(slowLabel, Priority.ALWAYS);
        HBox.setHgrow(fastLabel, Priority.ALWAYS);
        fastLabel.setAlignment(Pos.CENTER_RIGHT);
        speedLabels.getChildren().addAll(slowLabel, fastLabel);
        
        speedControl.getChildren().addAll(speedLabel, speedSlider, speedLabels);
        
        modePanel.getChildren().addAll(modeSelection, speedControl);
        
        return modePanel;
    }
    
    private HBox createBottomStatusPanel() {
        HBox bottomPanel = new HBox(15);
        bottomPanel.setPadding(new Insets(15));
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setStyle("-fx-background-color: #34495e; -fx-background-radius: 10; -fx-border-color: #1abc9c; -fx-border-width: 2; -fx-border-radius: 8;");
        
        // 状态标签
        statusLabel = new Label("请上传或加载图像开始");
        statusLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");
        
        // 进度条
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);
        progressBar.setStyle("-fx-accent: #1abc9c;");
        
        bottomPanel.getChildren().addAll(statusLabel, progressBar);
        
        return bottomPanel;
    }
    
    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(String.format(
            "-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12 24; " +
            "-fx-background-color: %s; -fx-text-fill: white; -fx-background-radius: 8;",
            color
        ));
        
        // 添加悬停效果
        button.setOnMouseEntered(e -> button.setStyle(String.format(
            "-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12 24; " +
            "-fx-background-color: derive(%s, 20%%); -fx-text-fill: white; -fx-background-radius: 8;",
            color
        )));
        
        button.setOnMouseExited(e -> button.setStyle(String.format(
            "-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12 24; " +
            "-fx-background-color: %s; -fx-text-fill: white; -fx-background-radius: 8;",
            color
        )));
        
        return button;
    }
    
    private void createEraserTimer() {
        eraserTimer = new AnimationTimer() {
            private long lastUpdate = 0;
            
            @Override
            public void handle(long now) {
                if (isPaused) return;
                
                // 根据速度滑块调整更新间隔
                long interval = (long) (20_000_000 / speedSlider.getValue());
                
                if (now - lastUpdate >= interval) {
                    performErase();
                    lastUpdate = now;
                }
            }
        };
    }
    
    private void performErase() {
        if (erasedCount >= totalPixels * eraseThreshold) {
            clearRemainingPixels();
            eraserTimer.stop();
            updateStatus("擦除完成！已擦除 " + erasedCount + " 个像素");
            startButton.setDisable(false);
            pauseButton.setDisable(true);
            uploadButton.setDisable(false);
            return;
        }
        
        switch (currentMode) {
            case PIXEL:
                eraseRandomPixels((int) (50 * speedSlider.getValue() / 5));
                break;
            case LINE:
                eraseRandomLine();
                break;
            case CIRCLE:
                eraseRandomCircle();
                break;
            case RECTANGLE:
                eraseRandomRectangle();
                break;
        }
        
        // 更新进度条
        double progress = (double) erasedCount / totalPixels;
        progressBar.setProgress(progress);
        
        // 定期更新状态
        if (erasedCount % 1000 == 0) {
            updateStatus(String.format("擦除进度: %.1f%% (%d/%d) - 模式: %s", 
                progress * 100, erasedCount, totalPixels, eraseModeComboBox.getValue()));
        }
    }
    
    private void eraseRandomPixels(int count) {
        int width = (int) canvas.getWidth();
        int height = (int) canvas.getHeight();
        
        for (int i = 0; i < count; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            
            if (!erasedPixels[x][y]) {
                pixelWriter.setColor(x, y, Color.TRANSPARENT);
                erasedPixels[x][y] = true;
                erasedCount++;
            }
        }
    }
    
    private void eraseRandomLine() {
        int width = (int) canvas.getWidth();
        int height = (int) canvas.getHeight();
        
        // 随机选择起点和终点
        int x1 = random.nextInt(width);
        int y1 = random.nextInt(height);
        int x2 = random.nextInt(width);
        int y2 = random.nextInt(height);
        
        // 使用Bresenham算法绘制线条
        drawLine(x1, y1, x2, y2);
    }
    
    private void drawLine(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;
        int err = dx - dy;
        
        while (true) {
            if (x1 >= 0 && x1 < canvas.getWidth() && y1 >= 0 && y1 < canvas.getHeight()) {
                if (!erasedPixels[x1][y1]) {
                    pixelWriter.setColor(x1, y1, Color.TRANSPARENT);
                    erasedPixels[x1][y1] = true;
                    erasedCount++;
                }
            }
            
            if (x1 == x2 && y1 == y2) break;
            
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }
    
    private void eraseRandomCircle() {
        int width = (int) canvas.getWidth();
        int height = (int) canvas.getHeight();
        
        // 随机选择圆心和半径
        int centerX = random.nextInt(width);
        int centerY = random.nextInt(height);
        int radius = random.nextInt(20) + 5; // 半径5-25像素
        
        // 绘制圆形
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
                    if (distance <= radius && !erasedPixels[x][y]) {
                        pixelWriter.setColor(x, y, Color.TRANSPARENT);
                        erasedPixels[x][y] = true;
                        erasedCount++;
                    }
                }
            }
        }
    }
    
    private void eraseRandomRectangle() {
        int width = (int) canvas.getWidth();
        int height = (int) canvas.getHeight();
        
        // 随机选择矩形位置和大小
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int rectWidth = random.nextInt(30) + 10; // 宽度10-40像素
        int rectHeight = random.nextInt(30) + 10; // 高度10-40像素
        
        // 绘制矩形
        for (int i = x; i < x + rectWidth && i < width; i++) {
            for (int j = y; j < y + rectHeight && j < height; j++) {
                if (i >= 0 && j >= 0 && !erasedPixels[i][j]) {
                    pixelWriter.setColor(i, j, Color.TRANSPARENT);
                    erasedPixels[i][j] = true;
                    erasedCount++;
                }
            }
        }
    }
    
    private void loadDefaultImage() {
        try {
            File defaultImageFile = new File(defaultImagePath);
            
            if (!defaultImageFile.exists()) {
                updateStatus("默认图像不存在，请上传图像");
                return;
            }
            
            Image image = new Image(defaultImageFile.toURI().toString());
            setImage(image);
            updateStatus("已加载默认图像: " + defaultImagePath);
        } catch (Exception e) {
            updateStatus("加载默认图像失败: " + e.getMessage());
        }
    }
    
    private void uploadImage(Stage primaryStage) {
        // 无论何时都可以上传新图像
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择图像文件");
        
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
            "图像文件", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"
        );
        fileChooser.getExtensionFilters().add(extFilter);
        
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                // 停止当前擦除过程
                if (eraserTimer != null) {
                    eraserTimer.stop();
                }
                
                // 重置UI状态
                resetUIState();
                
                // 加载新图像
                Image image = new Image(file.toURI().toString());
                setImage(image);
                updateStatus("已加载新图像: " + file.getName());
            } catch (Exception e) {
                updateStatus("加载图像失败: " + e.getMessage());
            }
        }
    }
    
    private void setImage(Image image) {
        // 重置所有状态
        resetUIState();
        
        originalImage = image;
        
        // 调整画布大小以匹配图像，但限制最大尺寸
        double scale = Math.min(800 / image.getWidth(), 500 / image.getHeight());
        double displayWidth = image.getWidth() * scale;
        double displayHeight = image.getHeight() * scale;
        
        canvas.setWidth(displayWidth);
        canvas.setHeight(displayHeight);
        
        // 清除画布并绘制新图像
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(originalImage, 0, 0, displayWidth, displayHeight);
        
        // 存储新图像的像素数据
        storeOriginalPixels();
        
        // 更新按钮状态
        startButton.setDisable(false);
        resetButton.setDisable(false);
        pauseButton.setDisable(true);
        
        // 重置计数器
        erasedCount = 0;
        progressBar.setProgress(0);
        
        updateStatus("已加载图像，准备开始擦除");
    }
    
    private void storeOriginalPixels() {
        int width = (int) canvas.getWidth();
        int height = (int) canvas.getHeight();
        
        originalPixels = new Color[width][height];
        erasedPixels = new boolean[width][height];
        totalPixels = width * height;
        
        // 从原始图像读取像素数据
        PixelReader pixelReader = originalImage.getPixelReader();
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // 计算原始图像中的对应位置
                int origX = (int)(x * (originalImage.getWidth() / width));
                int origY = (int)(y * (originalImage.getHeight() / height));
                
                // 确保不超出原始图像边界
                if (origX < originalImage.getWidth() && origY < originalImage.getHeight()) {
                    originalPixels[x][y] = pixelReader.getColor(origX, origY);
                } else {
                    originalPixels[x][y] = Color.TRANSPARENT;
                }
                
                erasedPixels[x][y] = false;
            }
        }
    }
    
    private void startErasing() {
        if (originalImage == null) {
            updateStatus("请先加载图像");
            return;
        }
        
        startButton.setDisable(true);
        pauseButton.setDisable(false);
        
        eraserTimer.start();
        updateStatus("开始随机擦除... 模式: " + eraseModeComboBox.getValue());
    }
    
    private void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            pauseButton.setText("继续");
            updateStatus("擦除已暂停");
        } else {
            pauseButton.setText("暂停");
            updateStatus("继续擦除...");
        }
    }
    
    private void resetCurrentImage() {
        // 重置当前图像到初始状态
        if (eraserTimer != null) {
            eraserTimer.stop();
        }
        
        isPaused = false;
        pauseButton.setText("暂停");
        pauseButton.setDisable(true);
        
        if (originalImage != null) {
            // 重新绘制原始图像
            double scale = Math.min(800 / originalImage.getWidth(), 500 / originalImage.getHeight());
            double displayWidth = originalImage.getWidth() * scale;
            double displayHeight = originalImage.getHeight() * scale;
            
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.drawImage(originalImage, 0, 0, displayWidth, displayHeight);
            
            // 重置擦除状态
            for (int x = 0; x < erasedPixels.length; x++) {
                for (int y = 0; y < erasedPixels[0].length; y++) {
                    erasedPixels[x][y] = false;
                }
            }
            
            erasedCount = 0;
            startButton.setDisable(false);
            progressBar.setProgress(0);
            updateStatus("当前图像已重置");
        }
    }
    
    private void resetUIState() {
        // 停止计时器
        if (eraserTimer != null) {
            eraserTimer.stop();
        }
        
        // 重置状态变量
        isPaused = false;
        erasedCount = 0;
        
        // 重置按钮状态
        startButton.setDisable(true);
        pauseButton.setDisable(true);
        pauseButton.setText("暂停");
        resetButton.setDisable(true);
        
        // 重置进度条
        progressBar.setProgress(0);
        
        // 重置模式选择
        eraseModeComboBox.setValue("单个像素");
        currentMode = EraseMode.PIXEL;
        
        // 重置速度滑块
        speedSlider.setValue(5);
        
        // 清除画布
        if (gc != null) {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }
        
        // 上传按钮始终可用
        uploadButton.setDisable(false);
    }
    
    private void clearRemainingPixels() {
        int width = (int) canvas.getWidth();
        int height = (int) canvas.getHeight();
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!erasedPixels[x][y]) {
                    pixelWriter.setColor(x, y, Color.TRANSPARENT);
                    erasedPixels[x][y] = true;
                    erasedCount++;
                }
            }
        }
        progressBar.setProgress(1.0);
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}