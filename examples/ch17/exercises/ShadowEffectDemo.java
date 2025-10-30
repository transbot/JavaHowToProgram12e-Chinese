import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ShadowEffectDemo extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 创建标签
        Label label = new Label("欢迎使用JavaFX");
        
        // 设置标签样式
        label.setStyle(
            "-fx-font-size: 48px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: linear-gradient(to bottom, #2D8BFF, #003C99);" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75), 10, 0.5, 3, 3);"
        );
        
        // 创建布局
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #E6F3FF, #B8DAFF);");
        root.getChildren().add(label);
        
        // 创建场景
        Scene scene = new Scene(root, 400, 200);
        
        // 设置舞台
        primaryStage.setTitle("JavaFX 阴影效果演示");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}