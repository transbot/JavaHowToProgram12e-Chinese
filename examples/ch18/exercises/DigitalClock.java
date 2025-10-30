import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 数字时钟应用 - 带闹钟功能
 * 显示实时时间，并可设置闹钟提醒
 */
public class DigitalClockWithAlarm extends Application {
    private Label timeLabel;           // 时间显示标签
    private Label dateLabel;           // 日期显示标签
    private Label alarmStatusLabel;    // 闹钟状态标签
    private Label alarmAlertLabel;     // 闹钟提醒标签
    private DateTimeFormatter timeFormatter;  // 时间格式化器
    private DateTimeFormatter dateFormatter;  // 日期格式化器
    private Glow glowEffect;           // 发光效果
    
    // 闹钟相关变量
    private int alarmHour = -1;        // 闹钟小时
    private int alarmMinute = -1;      // 闹钟分钟
    private boolean alarmActive = false; // 闹钟是否激活
    private boolean alarmRinging = false; // 闹钟是否正在响铃
    private AudioClip alarmSound;      // 闹钟声音
    
    @Override
    public void start(Stage primaryStage) {
        // 初始化格式化器
        timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        dateFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 EEEE");
        
        // 创建主容器
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #0f2027, #203a43, #2c5364);");
        
        // 创建顶部控制栏
        HBox topBar = createTopBar(primaryStage);
        root.setTop(topBar);
        
        // 创建中央内容区域（时间显示）
        VBox centerContent = createCenterContent();
        root.setCenter(centerContent);
        
        // 创建底部闹钟设置区域
        HBox alarmPanel = createAlarmPanel();
        root.setBottom(alarmPanel);
        
        // 创建场景
        Scene scene = new Scene(root, 600, 400);
        scene.setFill(Color.TRANSPARENT);
        
        // 配置舞台
        primaryStage.setTitle("数字时钟 - 带闹钟功能");
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setAlwaysOnTop(true);
        
        // 添加拖拽功能
        makeDraggable(primaryStage, root);
        
        // 启动动画计时器
        startClock();
        
        primaryStage.show();
    }
    
    /**
     * 创建顶部控制栏
     */
    private HBox createTopBar(Stage primaryStage) {
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(10, 15, 10, 15));
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");
        
        // 最小化按钮
        Button minimizeButton = new Button("—");
        minimizeButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 16px; -fx-min-width: 30px; -fx-min-height: 30px;");
        minimizeButton.setTooltip(new Tooltip("最小化"));
        minimizeButton.setOnAction(e -> ((Stage) minimizeButton.getScene().getWindow()).setIconified(true));
        
        // 关闭按钮
        Button closeButton = new Button("✕");
        closeButton.setStyle("-fx-background-color: rgba(255,0,0,0.7); -fx-text-fill: white; -fx-font-size: 16px; -fx-min-width: 30px; -fx-min-height: 30px;");
        closeButton.setTooltip(new Tooltip("关闭"));
        closeButton.setOnAction(e -> {
            stopAlarmSound();
            primaryStage.close();
        });
        
        // 添加按钮到顶部栏
        topBar.getChildren().addAll(minimizeButton, closeButton);
        
        return topBar;
    }
    
    /**
     * 创建中央内容区域（时间显示）
     */
    private VBox createCenterContent() {
        VBox centerContent = new VBox(15);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setPadding(new Insets(20));
        
        // 创建时间标签
        timeLabel = new Label();
        timeLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 80));
        timeLabel.setTextFill(Color.WHITE);
        
        // 创建日期标签
        dateLabel = new Label();
        dateLabel.setFont(Font.font("微软雅黑", FontWeight.NORMAL, 24));
        dateLabel.setTextFill(Color.LIGHTGRAY);
        
        // 创建闹钟提醒标签（初始隐藏）
        alarmAlertLabel = new Label("⏰ 闹钟提醒!");
        alarmAlertLabel.setFont(Font.font("微软雅黑", FontWeight.BOLD, 28));
        alarmAlertLabel.setTextFill(Color.rgb(255, 100, 100));
        alarmAlertLabel.setVisible(false);
        
        // 添加特效
        applyEffects();
        
        // 添加到容器
        centerContent.getChildren().addAll(timeLabel, dateLabel, alarmAlertLabel);
        
        return centerContent;
    }
    
    /**
     * 创建底部闹钟设置面板
     */
    private HBox createAlarmPanel() {
        HBox alarmPanel = new HBox(15);
        alarmPanel.setAlignment(Pos.CENTER);
        alarmPanel.setPadding(new Insets(15));
        alarmPanel.setStyle("-fx-background-color: rgba(0, 0, 0, 0.2);");
        
        // 闹钟图标和标签
        Label alarmIcon = new Label("⏰ 闹钟");
        alarmIcon.setFont(Font.font("微软雅黑", FontWeight.BOLD, 16));
        alarmIcon.setTextFill(Color.LIGHTGRAY);
        
        // 小时选择
        Label hourLabel = new Label("时:");
        hourLabel.setTextFill(Color.LIGHTGRAY);
        
        Spinner<Integer> hourSpinner = new Spinner<>(0, 23, 12);
        hourSpinner.setStyle("-fx-background-color: white; -fx-max-width: 70px;");
        hourSpinner.setEditable(true);
        
        // 分钟选择
        Label minuteLabel = new Label("分:");
        minuteLabel.setTextFill(Color.LIGHTGRAY);
        
        Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0);
        minuteSpinner.setStyle("-fx-background-color: white; -fx-max-width: 70px;");
        minuteSpinner.setEditable(true);
        
        // 设置闹钟按钮
        Button setAlarmButton = new Button("设置闹钟");
        setAlarmButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        setAlarmButton.setOnAction(e -> {
            alarmHour = hourSpinner.getValue();
            alarmMinute = minuteSpinner.getValue();
            alarmActive = true;
            alarmRinging = false;
            updateAlarmStatus();
        });
        
        // 清除闹钟按钮
        Button clearAlarmButton = new Button("清除闹钟");
        clearAlarmButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        clearAlarmButton.setOnAction(e -> {
            alarmActive = false;
            alarmRinging = false;
            alarmHour = -1;
            alarmMinute = -1;
            stopAlarmSound();
            alarmAlertLabel.setVisible(false);
            updateAlarmStatus();
        });
        
        // 停止响铃按钮（初始隐藏）
        Button stopAlarmButton = new Button("停止响铃");
        stopAlarmButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;");
        stopAlarmButton.setVisible(false);
        stopAlarmButton.setOnAction(e -> {
            alarmRinging = false;
            stopAlarmSound();
            alarmAlertLabel.setVisible(false);
            stopAlarmButton.setVisible(false);
        });
        
        // 闹钟状态显示
        alarmStatusLabel = new Label("未设置闹钟");
        alarmStatusLabel.setFont(Font.font("微软雅黑", FontWeight.NORMAL, 14));
        alarmStatusLabel.setTextFill(Color.LIGHTGRAY);
        
        // 添加所有组件到闹钟面板
        alarmPanel.getChildren().addAll(
            alarmIcon, hourLabel, hourSpinner, minuteLabel, minuteSpinner,
            setAlarmButton, clearAlarmButton, stopAlarmButton, alarmStatusLabel
        );
        
        return alarmPanel;
    }
    
    /**
     * 更新闹钟状态显示
     */
    private void updateAlarmStatus() {
        if (alarmRinging) {
            alarmStatusLabel.setText("闹钟响铃中!");
            alarmStatusLabel.setTextFill(Color.rgb(255, 100, 100));
            
            // 显示停止响铃按钮
            for (var node : ((HBox) alarmStatusLabel.getParent()).getChildren()) {
                if (node instanceof Button && ((Button) node).getText().equals("停止响铃")) {
                    node.setVisible(true);
                    break;
                }
            }
        } else if (alarmActive) {
            alarmStatusLabel.setText("闹钟已设置: " + String.format("%02d:%02d", alarmHour, alarmMinute));
            alarmStatusLabel.setTextFill(Color.LIGHTGREEN);
            
            // 隐藏停止响铃按钮
            for (var node : ((HBox) alarmStatusLabel.getParent()).getChildren()) {
                if (node instanceof Button && ((Button) node).getText().equals("停止响铃")) {
                    node.setVisible(false);
                    break;
                }
            }
        } else {
            alarmStatusLabel.setText("未设置闹钟");
            alarmStatusLabel.setTextFill(Color.LIGHTGRAY);
            
            // 隐藏停止响铃按钮
            for (var node : ((HBox) alarmStatusLabel.getParent()).getChildren()) {
                if (node instanceof Button && ((Button) node).getText().equals("停止响铃")) {
                    node.setVisible(false);
                    break;
                }
            }
        }
    }
    
    /**
     * 应用视觉效果
     */
    private void applyEffects() {
        // 创建发光效果并保存引用
        glowEffect = new Glow();
        glowEffect.setLevel(0.3);
        
        // 添加外阴影效果
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.7));
        dropShadow.setRadius(20);
        dropShadow.setOffsetX(5);
        dropShadow.setOffsetY(5);
        dropShadow.setInput(glowEffect);
        
        timeLabel.setEffect(dropShadow);
        
        // 为日期标签添加简单阴影
        DropShadow dateShadow = new DropShadow();
        dateShadow.setColor(Color.BLACK);
        dateShadow.setRadius(5);
        dateLabel.setEffect(dateShadow);
    }
    
    /**
     * 启动时钟动画
     */
    private void startClock() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateClock();
                checkAlarm();
            }
        };
        timer.start();
    }
    
    /**
     * 更新时钟显示
     */
    private void updateClock() {
        LocalDateTime now = LocalDateTime.now();
        
        // 更新时间显示
        String timeText = now.format(timeFormatter);
        timeLabel.setText(timeText);
        
        // 更新日期显示
        String dateText = now.format(dateFormatter);
        dateLabel.setText(dateText);
        
        // 根据秒数添加脉动效果
        int second = now.getSecond();
        double pulse = 0.7 + 0.3 * Math.sin(second * Math.PI / 30);
        glowEffect.setLevel(0.2 + pulse * 0.3);
        
        // 如果闹钟正在响铃，时间标签红色闪烁
        if (alarmRinging && second % 2 == 0) {
            timeLabel.setTextFill(Color.rgb(255, 100, 100)); // 红色闪烁
            alarmAlertLabel.setVisible(true);
        } else if (alarmRinging) {
            timeLabel.setTextFill(Color.WHITE);
            alarmAlertLabel.setVisible(true);
        } else if (alarmActive && second % 2 == 0) {
            timeLabel.setTextFill(Color.rgb(255, 215, 0)); // 黄色闪烁表示已设置闹钟
        } else {
            timeLabel.setTextFill(Color.WHITE);
            alarmAlertLabel.setVisible(false);
        }
    }
    
    /**
     * 使窗口可拖拽
     */
    private void makeDraggable(Stage stage, BorderPane root) {
        final double[] xOffset = new double[1];
        final double[] yOffset = new double[1];
        
        // 允许通过顶部栏拖拽
        root.getTop().setOnMousePressed(event -> {
            xOffset[0] = event.getSceneX();
            yOffset[0] = event.getSceneY();
        });
        
        root.getTop().setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset[0]);
            stage.setY(event.getScreenY() - yOffset[0]);
        });
        
        // 允许通过中央内容区域拖拽
        root.getCenter().setOnMousePressed(event -> {
            xOffset[0] = event.getSceneX();
            yOffset[0] = event.getSceneY();
        });
        
        root.getCenter().setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset[0]);
            stage.setY(event.getScreenY() - yOffset[0]);
        });
    }
    
    /**
     * 检查闹钟时间
     */
    private void checkAlarm() {
        if (!alarmActive || alarmRinging) return;
        
        LocalDateTime now = LocalDateTime.now();
        int currentHour = now.getHour();
        int currentMinute = now.getMinute();
        int currentSecond = now.getSecond();
        
        // 检查是否到达闹钟时间（只在整分钟开始时触发一次）
        if (currentHour == alarmHour && currentMinute == alarmMinute && currentSecond == 0) {
            triggerAlarm();
        }
    }
    
    /**
     * 触发闹钟
     */
    private void triggerAlarm() {
        alarmRinging = true;
        
        // 播放闹钟声音
        playAlarmSound();
        
        // 更新状态显示
        updateAlarmStatus();
    }
    
    /**
     * 播放闹钟声音
     */
    private void playAlarmSound() {
        try {
            // 尝试从资源文件加载闹钟声音
            // 注意：需要在resources文件夹中添加alarm.wav文件
            alarmSound = new AudioClip(getClass().getResource("/alarm.wav").toString());
            alarmSound.setCycleCount(AudioClip.INDEFINITE); // 循环播放
            alarmSound.setVolume(0.7); // 设置音量
            alarmSound.play();
        } catch (Exception e) {
            System.out.println("无法播放闹钟声音: " + e.getMessage());
            // 如果无法播放声音，至少显示视觉警报
        }
    }
    
    /**
     * 停止闹钟声音
     */
    private void stopAlarmSound() {
        if (alarmSound != null) {
            alarmSound.stop();
        }
    }
    
    @Override
    public void stop() {
        // 清理资源
        stopAlarmSound();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}