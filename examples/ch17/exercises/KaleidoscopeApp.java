import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.Random;

public class KaleidoscopeApp extends Application {
    private Random random = new Random();
    private Canvas canvas;
    private GraphicsContext gc;
    private int width = 800;
    private int height = 600;

    @Override
    public void start(Stage primaryStage) {
        // 创建画布
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();

        // 初始绘制万花筒图案
        drawKaleidoscopePattern();

        // 创建按钮 - 使用更醒目的设计
        Button newPatternButton = new Button("生成新图案");
        newPatternButton.setOnAction(e -> {
            drawKaleidoscopePattern();
            // 添加点击反馈
            newPatternButton.setText("图案已更新！");
            newPatternButton.setStyle(
                "-fx-font-size: 18px; " +
                "-fx-text-fill: white; " +
                "-fx-background-color: #4CAF50; " +
                "-fx-background-radius: 10px; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 4px; " +
                "-fx-border-radius: 10px; " +
                "-fx-effect: dropshadow(gaussian, rgba(255,255,255,1), 15, 0, 0, 0);"
            );
            
            // 200毫秒后恢复原样式
            new Thread(() -> {
                try {
                    Thread.sleep(200);
                    javafx.application.Platform.runLater(() -> {
                        newPatternButton.setText("生成新图案");
                        newPatternButton.setStyle(
                            "-fx-font-size: 18px; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-color: #FF5722; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-border-color: white; " +
                            "-fx-border-width: 4px; " +
                            "-fx-border-radius: 10px; " +
                            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,1), 15, 0, 0, 0);"
                        );
                    });
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        });
        
        // 设置按钮样式
        newPatternButton.setPadding(new Insets(20, 40, 20, 40));
        newPatternButton.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-text-fill: white; " +
            "-fx-background-color: #FF5722; " +
            "-fx-background-radius: 10px; " +
            "-fx-border-color: white; " +
            "-fx-border-width: 4px; " +
            "-fx-border-radius: 10px; " +
            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,1), 15, 0, 0, 0);"
        );
        
        // 添加悬停效果
        newPatternButton.setOnMouseEntered(e -> {
            newPatternButton.setStyle(
                "-fx-font-size: 18px; " +
                "-fx-text-fill: white; " +
                "-fx-background-color: #E64A19; " +
                "-fx-background-radius: 10px; " +
                "-fx-border-color: yellow; " +
                "-fx-border-width: 4px; " +
                "-fx-border-radius: 10px; " +
                "-fx-effect: dropshadow(gaussian, rgba(255,255,0,1), 25, 0, 0, 0);"
            );
        });
        
        newPatternButton.setOnMouseExited(e -> {
            newPatternButton.setStyle(
                "-fx-font-size: 18px; " +
                "-fx-text-fill: white; " +
                "-fx-background-color: #FF5722; " +
                "-fx-background-radius: 10px; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 4px; " +
                "-fx-border-radius: 10px; " +
                "-fx-effect: dropshadow(gaussian, rgba(255,255,255,1), 15, 0, 0, 0);"
            );
        });

        // 创建布局 - 将按钮移到顶部
        BorderPane root = new BorderPane();
        root.setCenter(canvas);
        root.setTop(newPatternButton);
        BorderPane.setAlignment(newPatternButton, javafx.geometry.Pos.CENTER);
        BorderPane.setMargin(newPatternButton, new Insets(20));

        // 创建场景
        Scene scene = new Scene(root, width, height);

        // 设置舞台
        primaryStage.setTitle("JavaFX万花筒");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void drawKaleidoscopePattern() {
        // 清空画布
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);

        // 定义万花筒的对称性
        int symmetry = 6; // 六边形对称性
        double centerX = width / 2;
        double centerY = height / 2;

        // 生成随机颜色的数量
        int colorCount = 3 + random.nextInt(5);
        Color[] colors = new Color[colorCount];
        for (int i = 0; i < colorCount; i++) {
            colors[i] = Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());
        }

        // 生成随机线段数量
        int segmentCount = 100 + random.nextInt(200);

        // 绘制每个随机线段及其对称线段
        for (int i = 0; i < segmentCount; i++) {
            // 选择随机颜色
            Color color = colors[random.nextInt(colorCount)];
            gc.setStroke(color);
            gc.setLineWidth(1 + random.nextDouble() * 3);

            // 生成随机起点和终点（在画布的四分之一区域内）
            double x1 = centerX + (random.nextDouble() - 0.5) * centerX;
            double y1 = centerY + (random.nextDouble() - 0.5) * centerY;
            double x2 = x1 + (random.nextDouble() - 0.5) * 50;
            double y2 = y1 + (random.nextDouble() - 0.5) * 50;

            // 绘制所有对称线段
            for (int j = 0; j < symmetry; j++) {
                double angle = 2 * Math.PI * j / symmetry;
                
                // 计算对称点
                double sx1 = centerX + (x1 - centerX) * Math.cos(angle) - (y1 - centerY) * Math.sin(angle);
                double sy1 = centerY + (x1 - centerX) * Math.sin(angle) + (y1 - centerY) * Math.cos(angle);
                double sx2 = centerX + (x2 - centerX) * Math.cos(angle) - (y2 - centerY) * Math.sin(angle);
                double sy2 = centerY + (x2 - centerX) * Math.sin(angle) + (y2 - centerY) * Math.cos(angle);
                
                // 绘制线段
                gc.strokeLine(sx1, sy1, sx2, sy2);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}