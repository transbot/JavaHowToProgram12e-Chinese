import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class BouncingBallsWithShadowMultiThread extends Application {
    private static final double PANE_WIDTH = 600;
    private static final double PANE_HEIGHT = 400;
    private static final double BALL_RADIUS = 15;
    private static final double INITIAL_SHADOW_WIDTH = 25;
    private static final double INITIAL_SHADOW_HEIGHT = 8;
    private static final double SHADOW_OFFSET_Y = 20;
    private static final double SIZE_CHANGE_FACTOR = 0.7;
    
    private Pane pane;
    private List<BallWithShadow> balls = new ArrayList<>();
    private Random random = new Random();
    private ExecutorService executor;
    private AtomicBoolean running = new AtomicBoolean(true);
    private final int THREAD_POOL_SIZE = 4; // 固定线程池大小
    
    @Override
    public void start(Stage primaryStage) {
        pane = new Pane();
        pane.setPrefSize(PANE_WIDTH, PANE_HEIGHT);
        
        // 创建固定大小的线程池
        executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        
        pane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                createBallWithShadow(e.getX(), e.getY());
            }
        });
        
        Scene scene = new Scene(pane);
        primaryStage.setTitle("Bouncing Balls with Shadow and 3D Effect - MultiThread");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // 启动主动画循环
        startAnimationLoop();
        
        // 关闭窗口时停止所有线程
        primaryStage.setOnCloseRequest(e -> {
            running.set(false);
            if (executor != null) {
                executor.shutdownNow();
            }
        });
    }
    
    private void createBallWithShadow(double x, double y) {
        Color ballColor = Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());
        
        Circle ball = new Circle(BALL_RADIUS, ballColor);
        ball.setCenterX(x);
        ball.setCenterY(y);
        
        Ellipse shadow = new Ellipse(INITIAL_SHADOW_WIDTH, INITIAL_SHADOW_HEIGHT);
        shadow.setFill(Color.BLACK);
        shadow.setOpacity(0.3);
        shadow.setCenterX(x);
        shadow.setCenterY(y + SHADOW_OFFSET_Y);
        
        pane.getChildren().addAll(shadow, ball);
        
        double angle = random.nextDouble() * 2 * Math.PI;
        double speed = 2 + random.nextDouble() * 3;
        double dx = Math.cos(angle) * speed;
        double dy = Math.sin(angle) * speed;
        
        BallWithShadow ballWithShadow = new BallWithShadow(ball, shadow, dx, dy, BALL_RADIUS);
        synchronized (balls) {
            balls.add(ballWithShadow);
        }
    }
    
    private void startAnimationLoop() {
        // 创建多个动画线程
        for (int i = 0; i < THREAD_POOL_SIZE; i++) {
            executor.execute(this::animationWorker);
        }
    }
    
    private void animationWorker() {
        final long FRAME_DELAY = 16; // ~60 FPS
        
        while (running.get()) {
            // 获取当前球的列表快照
            List<BallWithShadow> currentBalls;
            synchronized (balls) {
                currentBalls = new ArrayList<>(balls);
            }
            
            // 如果没有球，休眠一段时间再检查
            if (currentBalls.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
                continue;
            }
            
            // 将球分配给不同的线程处理
            int threadIndex = Integer.parseInt(Thread.currentThread().getName().split("-")[3]) % THREAD_POOL_SIZE;
            
            for (int i = 0; i < currentBalls.size(); i++) {
                if (!running.get()) break;
                
                // 将球均匀分配给各个线程
                if (i % THREAD_POOL_SIZE == threadIndex) {
                    BallWithShadow ballWithShadow = currentBalls.get(i);
                    updateBallPositionAndSize(ballWithShadow);
                    updateShadow(ballWithShadow);
                }
            }
            
            try {
                Thread.sleep(FRAME_DELAY);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    
    private void updateBallPositionAndSize(BallWithShadow ballWithShadow) {
        Circle ball = ballWithShadow.ball;
        double dx = ballWithShadow.dx;
        double dy = ballWithShadow.dy;
        double currentRadius = ballWithShadow.currentRadius;
        double targetRadius = ballWithShadow.targetRadius;
        
        double newX = ball.getCenterX() + dx;
        double newY = ball.getCenterY() + dy;
        
        boolean hitEdge = false;
        
        // Check for horizontal collision
        if (newX <= currentRadius || newX >= PANE_WIDTH - currentRadius) {
            dx = -dx;
            ballWithShadow.dx = dx;
            newX = ball.getCenterX() + dx;
            hitEdge = true;
        }
        
        // Check for vertical collision
        if (newY <= currentRadius || newY >= PANE_HEIGHT - currentRadius) {
            dy = -dy;
            ballWithShadow.dy = dy;
            newY = ball.getCenterY() + dy;
            hitEdge = true;
        }
        
        // 当碰到边缘时改变球的大小
        if (hitEdge) {
            // 设置目标半径为当前半径的70%（压缩效果）
            ballWithShadow.targetRadius = currentRadius * SIZE_CHANGE_FACTOR;
            ballWithShadow.sizeChangeCounter = 10; // 设置变化计数器
        }
        
        // 处理尺寸变化动画
        if (ballWithShadow.sizeChangeCounter > 0) {
            ballWithShadow.sizeChangeCounter--;
            
            if (ballWithShadow.targetRadius < currentRadius) {
                // 缩小
                ballWithShadow.currentRadius = Math.max(ballWithShadow.targetRadius, 
                    currentRadius - (ballWithShadow.originalRadius - ballWithShadow.targetRadius) / 10);
            } else {
                // 恢复原大小
                ballWithShadow.currentRadius = Math.min(ballWithShadow.targetRadius, 
                    currentRadius + (ballWithShadow.originalRadius - currentRadius) / 10);
            }
            
            final double newRadius = ballWithShadow.currentRadius;
            Platform.runLater(() -> ball.setRadius(newRadius));
            
            // 如果计数器用完且当前已达到目标大小，则设置下一个目标（恢复原大小）
            if (ballWithShadow.sizeChangeCounter == 0 && 
                Math.abs(ballWithShadow.currentRadius - ballWithShadow.targetRadius) < 0.1) {
                if (ballWithShadow.targetRadius < ballWithShadow.originalRadius) {
                    ballWithShadow.targetRadius = ballWithShadow.originalRadius;
                    ballWithShadow.sizeChangeCounter = 10;
                }
            }
        }
        
        final double finalNewX = newX;
        final double finalNewY = newY;
        Platform.runLater(() -> {
            ball.setCenterX(finalNewX);
            ball.setCenterY(finalNewY);
        });
    }
    
    private void updateShadow(BallWithShadow ballWithShadow) {
        Circle ball = ballWithShadow.ball;
        Ellipse shadow = ballWithShadow.shadow;
        
        // 在UI线程中更新阴影
        Platform.runLater(() -> {
            // Update shadow position (follows the ball with vertical offset)
            shadow.setCenterX(ball.getCenterX());
            shadow.setCenterY(ball.getCenterY() + SHADOW_OFFSET_Y);
            
            // Update shadow size based on ball's size and vertical position
            double normalizedY = ball.getCenterY() / PANE_HEIGHT;
            double sizeScale = ballWithShadow.currentRadius / ballWithShadow.originalRadius;
            double positionScale = 0.7 + normalizedY * 0.6;
            
            shadow.setRadiusX(INITIAL_SHADOW_WIDTH * sizeScale * positionScale);
            shadow.setRadiusY(INITIAL_SHADOW_HEIGHT * sizeScale * positionScale);
            
            // Update shadow opacity based on ball's vertical position
            double opacity = 0.2 + normalizedY * 0.3;
            shadow.setOpacity(opacity);
        });
    }
    
    private class BallWithShadow {
        Circle ball;
        Ellipse shadow;
        double dx;
        double dy;
        double originalRadius;
        double currentRadius;
        double targetRadius;
        int sizeChangeCounter = 0;
        
        BallWithShadow(Circle ball, Ellipse shadow, double dx, double dy, double radius) {
            this.ball = ball;
            this.shadow = shadow;
            this.dx = dx;
            this.dy = dy;
            this.originalRadius = radius;
            this.currentRadius = radius;
            this.targetRadius = radius;
        }
    }
    
    @Override
    public void stop() {
        running.set(false);
        if (executor != null) {
            executor.shutdownNow();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}