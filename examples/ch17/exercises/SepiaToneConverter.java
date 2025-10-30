import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class SepiaToneConverter extends Application {

    private ImageView originalImageView;
    private ImageView sepiaImageView;
    private SepiaTone sepiaTone;
    private Label statusLabel;
    
    // 默认图像路径
    private String defaultImagePath = "../images/purpleflowers.png";
    
    @Override
    public void start(Stage primaryStage) {
        // 创建棕褐色调效果
        sepiaTone = new SepiaTone();
        sepiaTone.setLevel(0.8); // 设置默认棕褐色调强度
        
        // 创建主布局
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        // 创建标题
        Label titleLabel = new Label("棕褐色调图像转换器");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, 20, 0));
        
        // 创建图像显示区域
        HBox imageContainer = createImageContainer();
        
        // 创建控制面板
        HBox controlPanel = createControlPanel(primaryStage);
        
        // 创建状态栏
        statusLabel = new Label("准备就绪");
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
        HBox statusBar = new HBox(statusLabel);
        statusBar.setPadding(new Insets(10, 0, 0, 0));
        
        // 组装主布局
        VBox centerContainer = new VBox(20);
        centerContainer.setAlignment(Pos.CENTER);
        centerContainer.getChildren().addAll(imageContainer, controlPanel);
        
        root.setTop(titleBox);
        root.setCenter(centerContainer);
        root.setBottom(statusBar);
        
        // 加载默认图像
        loadDefaultImage();
        
        // 创建场景
        Scene scene = new Scene(root, 1000, 700);
        
        // 设置舞台
        primaryStage.setTitle("棕褐色调图像转换器");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private HBox createImageContainer() {
        HBox imageContainer = new HBox(30);
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.setPadding(new Insets(20));
        imageContainer.setStyle("-fx-background-color: white; -fx-border-color: #dddddd; -fx-border-width: 1;");
        
        // 创建原始图像视图
        VBox originalBox = new VBox(10);
        originalBox.setAlignment(Pos.CENTER);
        Label originalLabel = new Label("原始图像");
        originalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        originalImageView = new ImageView();
        originalImageView.setFitWidth(400);
        originalImageView.setFitHeight(300);
        originalImageView.setPreserveRatio(true);
        originalImageView.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1;");
        
        originalBox.getChildren().addAll(originalLabel, originalImageView);
        
        // 创建棕褐色调图像视图
        VBox sepiaBox = new VBox(10);
        sepiaBox.setAlignment(Pos.CENTER);
        Label sepiaLabel = new Label("棕褐色调效果");
        sepiaLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        sepiaImageView = new ImageView();
        sepiaImageView.setFitWidth(400);
        sepiaImageView.setFitHeight(300);
        sepiaImageView.setPreserveRatio(true);
        sepiaImageView.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1;");
        
        sepiaBox.getChildren().addAll(sepiaLabel, sepiaImageView);
        
        imageContainer.getChildren().addAll(originalBox, sepiaBox);
        
        return imageContainer;
    }
    
    private HBox createControlPanel(Stage primaryStage) {
        HBox controlPanel = new HBox(20);
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.setPadding(new Insets(20));
        controlPanel.setStyle("-fx-background-color: white; -fx-border-color: #dddddd; -fx-border-width: 1;");
        
        // 创建上传按钮
        Button uploadButton = new Button("上传图像");
        uploadButton.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        uploadButton.setOnAction(e -> uploadImage(primaryStage));
        
        // 创建重置按钮
        Button resetButton = new Button("重置效果");
        resetButton.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-color: #2196F3; -fx-text-fill: white;");
        resetButton.setOnAction(e -> resetEffect());
        
        // 创建强度控制按钮
        Button increaseButton = new Button("增强效果");
        increaseButton.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-color: #FF9800; -fx-text-fill: white;");
        increaseButton.setOnAction(e -> increaseEffect());
        
        Button decreaseButton = new Button("减弱效果");
        decreaseButton.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-color: #FF5722; -fx-text-fill: white;");
        decreaseButton.setOnAction(e -> decreaseEffect());
        
        controlPanel.getChildren().addAll(uploadButton, resetButton, increaseButton, decreaseButton);
        
        return controlPanel;
    }
    
    private void loadDefaultImage() {
        try {
            File defaultImageFile = new File(defaultImagePath);
            
            // 如果默认图像不存在，尝试从类路径加载
            if (!defaultImageFile.exists()) {
                statusLabel.setText("默认图像不存在，请上传图像");
                return;
            }
            
            Image image = new Image(defaultImageFile.toURI().toString());
            setImage(image);
            statusLabel.setText("已加载默认图像: " + defaultImagePath);
        } catch (Exception e) {
            statusLabel.setText("加载默认图像失败: " + e.getMessage());
        }
    }
    
    private void uploadImage(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择图像文件");
        
        // 设置文件过滤器
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
            "图像文件", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"
        );
        fileChooser.getExtensionFilters().add(extFilter);
        
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                Image image = new Image(file.toURI().toString());
                setImage(image);
                statusLabel.setText("已加载图像: " + file.getName());
            } catch (Exception e) {
                statusLabel.setText("加载图像失败: " + e.getMessage());
            }
        }
    }
    
    private void setImage(Image image) {
        // 设置原始图像
        originalImageView.setImage(image);
        
        // 设置棕褐色调图像
        sepiaImageView.setImage(image);
        sepiaImageView.setEffect(sepiaTone);
    }
    
    private void resetEffect() {
        sepiaTone.setLevel(0.8);
        statusLabel.setText("已重置棕褐色调效果");
    }
    
    private void increaseEffect() {
        double currentLevel = sepiaTone.getLevel();
        if (currentLevel < 1.0) {
            sepiaTone.setLevel(Math.min(1.0, currentLevel + 0.1));
            statusLabel.setText(String.format("增强棕褐色调效果: %.1f", sepiaTone.getLevel()));
        } else {
            statusLabel.setText("棕褐色调效果已达到最大值");
        }
    }
    
    private void decreaseEffect() {
        double currentLevel = sepiaTone.getLevel();
        if (currentLevel > 0.0) {
            sepiaTone.setLevel(Math.max(0.0, currentLevel - 0.1));
            statusLabel.setText(String.format("减弱棕褐色调效果: %.1f", sepiaTone.getLevel()));
        } else {
            statusLabel.setText("棕褐色调效果已达到最小值");
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}