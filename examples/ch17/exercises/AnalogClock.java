import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class AnalogClock extends Application {
    
    // 时钟尺寸
    private static final int CLOCK_SIZE = 400;
    private static final int CLOCK_RADIUS = 180;
    private static final int CENTER_X = CLOCK_SIZE / 2;
    private static final int CENTER_Y = CLOCK_SIZE / 2;
    
    // 指针长度
    private static final int HOUR_HAND_LENGTH = 70;
    private static final int MINUTE_HAND_LENGTH = 100;
    private static final int SECOND_HAND_LENGTH = 120;
    
    // 指针宽度
    private static final int HOUR_HAND_WIDTH = 6;
    private static final int MINUTE_HAND_WIDTH = 4;
    private static final int SECOND_HAND_WIDTH = 2;
    
    // 颜色
    private static final Color HOUR_HAND_COLOR = Color.DARKBLUE;
    private static final Color MINUTE_HAND_COLOR = Color.BLUE;
    private static final Color SECOND_HAND_COLOR = Color.RED;
    private static final Color CLOCK_FACE_COLOR = Color.WHITE;
    private static final Color CLOCK_BORDER_COLOR = Color.BLACK;
    private static final Color MARKER_COLOR = Color.BLACK;
    
    private Canvas canvas;
    private GraphicsContext gc;
    private AnimationTimer timer;
    
    // 用于计算帧间时间
    private long lastTime = 0;
    private double accumulatedTime = 0;
    
    @Override
    public void start(Stage primaryStage) {
        // 创建画布
        canvas = new Canvas(CLOCK_SIZE, CLOCK_SIZE);
        gc = canvas.getGraphicsContext2D();
        
        // 创建布局
        Pane root = new Pane(canvas);
        
        // 创建场景
        Scene scene = new Scene(root, CLOCK_SIZE, CLOCK_SIZE);
        
        // 设置舞台
        primaryStage.setTitle("模拟时钟");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        // 创建动画计时器
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // 计算时间增量（纳秒转换为秒）
                double deltaTime = (lastTime == 0) ? 0 : (now - lastTime) / 1_000_000_000.0;
                lastTime = now;
                
                // 累积时间
                accumulatedTime += deltaTime;
                
                // 绘制时钟
                drawClock(accumulatedTime);
            }
        };
        
        // 启动动画
        timer.start();
    }
    
    private void drawClock(double totalTime) {
        // 清除画布
        gc.clearRect(0, 0, CLOCK_SIZE, CLOCK_SIZE);
        
        // 绘制时钟表盘
        drawClockFace();
        
        // 绘制时钟刻度
        drawMarkers();
        
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        int hours = now.getHour();
        int minutes = now.getMinute();
        int seconds = now.getSecond();
        int nanos = now.getNano();
        
        // 计算秒针角度（考虑纳秒部分以实现平滑移动）
        double secondAngle = calculateSecondAngle(seconds, nanos, totalTime);
        
        // 计算分针角度（考虑秒数部分）
        double minuteAngle = calculateMinuteAngle(minutes, seconds, nanos);
        
        // 计算时针角度（考虑分钟部分）
        double hourAngle = calculateHourAngle(hours, minutes, seconds);
        
        // 绘制指针
        drawHand(hourAngle, HOUR_HAND_LENGTH, HOUR_HAND_WIDTH, HOUR_HAND_COLOR);
        drawHand(minuteAngle, MINUTE_HAND_LENGTH, MINUTE_HAND_WIDTH, MINUTE_HAND_COLOR);
        drawHand(secondAngle, SECOND_HAND_LENGTH, SECOND_HAND_WIDTH, SECOND_HAND_COLOR);
        
        // 绘制中心点
        drawCenter();
    }
    
    private void drawClockFace() {
        // 绘制外圆
        gc.setFill(CLOCK_FACE_COLOR);
        gc.setStroke(CLOCK_BORDER_COLOR);
        gc.setLineWidth(2);
        gc.fillOval(CENTER_X - CLOCK_RADIUS, CENTER_Y - CLOCK_RADIUS, 
                   CLOCK_RADIUS * 2, CLOCK_RADIUS * 2);
        gc.strokeOval(CENTER_X - CLOCK_RADIUS, CENTER_Y - CLOCK_RADIUS, 
                     CLOCK_RADIUS * 2, CLOCK_RADIUS * 2);
    }
    
    private void drawMarkers() {
        gc.setStroke(MARKER_COLOR);
        gc.setLineWidth(2);
        
        // 绘制小时刻度
        for (int i = 0; i < 12; i++) {
            double angle = Math.toRadians(i * 30);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            
            // 内点
            double innerX = CENTER_X + (CLOCK_RADIUS - 20) * sin;
            double innerY = CENTER_Y - (CLOCK_RADIUS - 20) * cos;
            
            // 外点
            double outerX = CENTER_X + CLOCK_RADIUS * sin;
            double outerY = CENTER_Y - CLOCK_RADIUS * cos;
            
            gc.strokeLine(innerX, innerY, outerX, outerY);
        }
        
        // 绘制分钟刻度（更细的线）
        gc.setLineWidth(1);
        for (int i = 0; i < 60; i++) {
            if (i % 5 != 0) { // 跳过小时刻度位置
                double angle = Math.toRadians(i * 6);
                double cos = Math.cos(angle);
                double sin = Math.sin(angle);
                
                // 内点
                double innerX = CENTER_X + (CLOCK_RADIUS - 10) * sin;
                double innerY = CENTER_Y - (CLOCK_RADIUS - 10) * cos;
                
                // 外点
                double outerX = CENTER_X + CLOCK_RADIUS * sin;
                double outerY = CENTER_Y - CLOCK_RADIUS * cos;
                
                gc.strokeLine(innerX, innerY, outerX, outerY);
            }
        }
        
        // 绘制数字
        gc.setFill(MARKER_COLOR);
        gc.setLineWidth(1);
        for (int i = 1; i <= 12; i++) {
            double angle = Math.toRadians((i * 30) - 90);
            double x = CENTER_X + (CLOCK_RADIUS - 40) * Math.cos(angle);
            double y = CENTER_Y + (CLOCK_RADIUS - 40) * Math.sin(angle);
            
            String number = String.valueOf(i);
            double textWidth = gc.getFont().getSize() * number.length() / 2;
            double textHeight = gc.getFont().getSize() / 2;
            
            gc.fillText(number, x - textWidth / 2, y + textHeight / 2);
        }
    }
    
    private void drawHand(double angle, double length, double width, Color color) {
        // 将角度转换为弧度（减去90度使0度在顶部）
        double radianAngle = Math.toRadians(angle - 90);
        
        // 计算指针终点
        double endX = CENTER_X + length * Math.cos(radianAngle);
        double endY = CENTER_Y + length * Math.sin(radianAngle);
        
        // 绘制指针
        gc.setStroke(color);
        gc.setLineWidth(width);
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        gc.strokeLine(CENTER_X, CENTER_Y, endX, endY);
    }
    
    private void drawCenter() {
        // 绘制中心点
        gc.setFill(SECOND_HAND_COLOR);
        gc.fillOval(CENTER_X - 5, CENTER_Y - 5, 10, 10);
        gc.setStroke(CLOCK_BORDER_COLOR);
        gc.setLineWidth(1);
        gc.strokeOval(CENTER_X - 5, CENTER_Y - 5, 10, 10);
    }
    
    private double calculateSecondAngle(int seconds, int nanos, double totalTime) {
        // 使用累积时间计算平滑的秒针角度
        // 这样即使帧率不稳定，秒针也能平滑移动
        double smoothSeconds = seconds + (nanos / 1_000_000_000.0);
        return smoothSeconds * 6; // 每秒6度
    }
    
    private double calculateMinuteAngle(int minutes, int seconds, int nanos) {
        // 分针角度，考虑秒数部分以实现平滑移动
        double smoothMinutes = minutes + (seconds / 60.0) + (nanos / 60_000_000_000.0);
        return smoothMinutes * 6; // 每分钟6度
    }
    
    private double calculateHourAngle(int hours, int minutes, int seconds) {
        // 时针角度，考虑分钟和秒数部分以实现平滑移动
        double smoothHours = (hours % 12) + (minutes / 60.0) + (seconds / 3600.0);
        return smoothHours * 30; // 每小时30度
    }
    
    @Override
    public void stop() {
        // 停止动画计时器
        if (timer != null) {
            timer.stop();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}