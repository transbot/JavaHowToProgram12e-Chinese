import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageMarquee extends Application {
    
    private static final double CANVAS_WIDTH = 800;
    private static final double CANVAS_HEIGHT = 300;
    private static final double SCROLL_SPEED = 1.0; // 像素/帧
    private static final double IMAGE_HEIGHT = 200;
    private static final double SPACING = 20; // 图像之间的间距
    
    private List<Image> images = new ArrayList<>();
    private List<Double> imageWidths = new ArrayList<>(); // 存储每个图像的缩放后宽度
    private double currentX = 0;
    private double totalWidth = 0;
    private boolean isPaused = false;
    
    @Override
    public void start(Stage primaryStage) {
        // 加载图像
        loadImages();
        
        // 计算所有图像的总宽度（包括间距）
        calculateTotalWidth();
        
        // 创建Canvas
        Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // 添加鼠标事件监听器
        canvas.setOnMouseEntered(this::handleMouseEnter);
        canvas.setOnMouseExited(this::handleMouseExit);
        
        // 创建主布局
        Pane root = new Pane(canvas);
        Scene scene = new Scene(root, CANVAS_WIDTH, CANVAS_HEIGHT);
        
        primaryStage.setTitle("图像跑马灯显示牌 - 鼠标悬停暂停");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        // 创建动画定时器
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // 清除画布
                gc.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
                
                // 绘制背景
                gc.setFill(javafx.scene.paint.Color.LIGHTGRAY);
                gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
                
                // 绘制状态提示
                if (isPaused) {
                    gc.setFill(javafx.scene.paint.Color.RED);
                    gc.fillText("已暂停 - 鼠标移出继续", 10, 20);
                }
                
                // 绘制所有可见图像
                drawImages(gc);
                
                // 如果未暂停，更新位置
                if (!isPaused) {
                    currentX -= SCROLL_SPEED;
                    
                    // 当第一组图像完全滚出屏幕时，重置位置以实现无缝循环
                    if (currentX <= -totalWidth) {
                        currentX += totalWidth;
                    }
                }
            }
        };
        
        timer.start();
    }
    
    private void handleMouseEnter(MouseEvent event) {
        isPaused = true;
    }
    
    private void handleMouseExit(MouseEvent event) {
        isPaused = false;
    }
    
    private void loadImages() {
        // 获取上级目录中的images文件夹
        File currentDir = new File(System.getProperty("user.dir"));
        File parentDir = currentDir.getParentFile();
        File imagesDir = new File(parentDir, "images");
        
        // 如果上级目录的images文件夹不存在，尝试当前目录的images文件夹
        if (!imagesDir.exists() || !imagesDir.isDirectory()) {
            imagesDir = new File(currentDir, "images");
        }
        
        System.out.println("尝试从目录加载图像: " + imagesDir.getAbsolutePath());
        
        if (imagesDir.exists() && imagesDir.isDirectory()) {
            File[] imageFiles = imagesDir.listFiles((dir, name) -> {
                String lower = name.toLowerCase();
                return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || 
                       lower.endsWith(".png") || lower.endsWith(".gif") ||
                       lower.endsWith(".bmp");
            });
            
            if (imageFiles != null && imageFiles.length > 0) {
                for (File file : imageFiles) {
                    try {
                        Image image = new Image(file.toURI().toString());
                        images.add(image);
                        
                        // 计算并存储缩放后的宽度
                        double aspectRatio = image.getWidth() / image.getHeight();
                        double scaledWidth = IMAGE_HEIGHT * aspectRatio;
                        imageWidths.add(scaledWidth);
                        
                        System.out.println("加载图像: " + file.getName() + 
                                         " (原始尺寸: " + image.getWidth() + "x" + image.getHeight() + 
                                         ", 显示尺寸: " + scaledWidth + "x" + IMAGE_HEIGHT + ")");
                    } catch (Exception e) {
                        System.err.println("无法加载图像: " + file.getName() + " - " + e.getMessage());
                    }
                }
            } else {
                System.err.println("在目录中未找到图像文件: " + imagesDir.getAbsolutePath());
                createSampleMessage();
            }
        } else {
            System.err.println("图像目录不存在: " + imagesDir.getAbsolutePath());
            createSampleMessage();
        }
        
        // 如果没有加载到任何图像，创建示例消息
        if (images.isEmpty()) {
            createSampleMessage();
        }
    }
    
    private void createSampleMessage() {
        // 创建一个简单的文本图像作为示例
        System.out.println("创建示例消息图像");
        // 在实际使用中，您可以创建一个包含文本的Canvas并转换为图像
        // 这里我们只是输出消息，实际运行时需要用户提供图像文件
    }
    
    private void calculateTotalWidth() {
        totalWidth = 0;
        for (Double width : imageWidths) {
            totalWidth += width + SPACING;
        }
        // 减去最后一个图像的额外间距
        if (!imageWidths.isEmpty()) {
            totalWidth -= SPACING;
        }
        
        System.out.println("图像总宽度: " + totalWidth);
    }
    
    private void drawImages(GraphicsContext gc) {
        double x = currentX;
        int imageIndex = 0;
        
        // 绘制足够多的图像副本来填满屏幕并实现无缝循环
        while (x < CANVAS_WIDTH) {
            for (int i = 0; i < images.size(); i++) {
                Image image = images.get(i);
                double scaledWidth = imageWidths.get(i);
                double scaledHeight = IMAGE_HEIGHT;
                
                // 计算垂直居中位置
                double y = (CANVAS_HEIGHT - scaledHeight) / 2;
                
                // 如果图像在画布可见区域内，则绘制
                if (x + scaledWidth > 0 && x < CANVAS_WIDTH) {
                    gc.drawImage(image, x, y, scaledWidth, scaledHeight);
                    
                    // 可选：绘制图像边框
                    gc.setStroke(javafx.scene.paint.Color.BLACK);
                    gc.strokeRect(x, y, scaledWidth, scaledHeight);
                }
                
                x += scaledWidth + SPACING;
                
                // 如果已经超出画布右侧足够远，可以提前结束
                if (x > CANVAS_WIDTH + totalWidth) {
                    return;
                }
            }
            
            // 在每组图像之间添加额外间距（可选）
            x += SPACING;
            imageIndex++;
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}