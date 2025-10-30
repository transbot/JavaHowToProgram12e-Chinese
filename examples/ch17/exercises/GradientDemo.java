import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GradientDemo extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 创建主布局
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #f0f0f0;");
        
        // 标题
        Text title = new Text("JavaFX渐变效果演示");
        title.setFont(Font.font(24));
        title.setStyle("-fx-fill: #333333;");
        
        // 创建线性渐变示例区域
        VBox linearGradients = createLinearGradientsSection();
        
        // 创建径向渐变示例区域
        VBox radialGradients = createRadialGradientsSection();
        
        // 创建复杂渐变示例区域
        VBox complexGradients = createComplexGradientsSection();
        
        // 添加到主布局
        mainLayout.getChildren().addAll(title, linearGradients, radialGradients, complexGradients);
        
        // 创建场景
        Scene scene = new Scene(mainLayout, 800, 1000);
        
        // 设置舞台
        primaryStage.setTitle("JavaFX渐变效果演示");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private VBox createLinearGradientsSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(15));
        section.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1;");
        
        Text sectionTitle = new Text("线性渐变示例");
        sectionTitle.setFont(Font.font(18));
        sectionTitle.setStyle("-fx-fill: #2c3e50;");
        
        // 创建网格布局来放置示例
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        
        // 示例1: 从左到右的线性渐变
        Rectangle rect1 = new Rectangle(150, 100);
        rect1.setStyle("-fx-fill: linear-gradient(to right, #ff7e5f, #feb47b);");
        
        VBox desc1 = createDescription("从左到右", "to right, #ff7e5f, #feb47b");
        VBox example1 = createExampleBox(rect1, desc1);
        
        // 示例2: 从右上到左下的线性渐变
        Rectangle rect2 = new Rectangle(150, 100);
        rect2.setStyle("-fx-fill: linear-gradient(to bottom left, #667eea, #764ba2);");
        
        VBox desc2 = createDescription("从右上到左下", "to bottom left, #667eea, #764ba2");
        VBox example2 = createExampleBox(rect2, desc2);
        
        // 示例3: 垂直线性渐变
        Rectangle rect3 = new Rectangle(150, 100);
        rect3.setStyle("-fx-fill: linear-gradient(to bottom, #11998e, #38ef7d);");
        
        VBox desc3 = createDescription("从上到下", "to bottom, #11998e, #38ef7d");
        VBox example3 = createExampleBox(rect3, desc3);
        
        // 示例4: 对角线渐变
        Rectangle rect4 = new Rectangle(150, 100);
        rect4.setStyle("-fx-fill: linear-gradient(45deg, #ff9a9e, #fad0c4);");
        
        VBox desc4 = createDescription("45度角", "45deg, #ff9a9e, #fad0c4");
        VBox example4 = createExampleBox(rect4, desc4);
        
        // 示例5: 多颜色停止点
        Rectangle rect5 = new Rectangle(150, 100);
        rect5.setStyle("-fx-fill: linear-gradient(to right, red 0%, yellow 50%, green 100%);");
        
        VBox desc5 = createDescription("多颜色停止点", "to right, red 0%, yellow 50%, green 100%");
        VBox example5 = createExampleBox(rect5, desc5);
        
        // 示例6: 重复线性渐变
        Rectangle rect6 = new Rectangle(150, 100);
        rect6.setStyle("-fx-fill: repeating-linear-gradient(45deg, #a1c4fd, #a1c4fd 10px, #c2e9fb 10px, #c2e9fb 20px);");
        
        VBox desc6 = createDescription("重复线性渐变", "repeating-linear-gradient(45deg, #a1c4fd, #a1c4fd 10px, #c2e9fb 10px, #c2e9fb 20px)");
        VBox example6 = createExampleBox(rect6, desc6);
        
        // 添加到网格
        grid.add(example1, 0, 0);
        grid.add(example2, 1, 0);
        grid.add(example3, 2, 0);
        grid.add(example4, 0, 1);
        grid.add(example5, 1, 1);
        grid.add(example6, 2, 1);
        
        section.getChildren().addAll(sectionTitle, grid);
        return section;
    }
    
    private VBox createRadialGradientsSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(15));
        section.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1;");
        
        Text sectionTitle = new Text("径向渐变示例");
        sectionTitle.setFont(Font.font(18));
        sectionTitle.setStyle("-fx-fill: #2c3e50;");
        
        // 创建网格布局
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        
        // 示例1: 基本径向渐变
        Circle circle1 = new Circle(50);
        circle1.setStyle("-fx-fill: radial-gradient(center 50% 50%, radius 50%, #ffecd2, #fcb69f);");
        
        VBox desc1 = createDescription("中心径向渐变", "radial-gradient(center 50% 50%, radius 50%, #ffecd2, #fcb69f)");
        VBox example1 = createExampleBox(circle1, desc1);
        
        // 示例2: 焦点径向渐变
        Circle circle2 = new Circle(50);
        circle2.setStyle("-fx-fill: radial-gradient(focus-angle 45deg, focus-distance 20%, center 50% 50%, radius 50%, #a8edea, #fed6e3);");
        
        VBox desc2 = createDescription("带焦点的径向渐变", "radial-gradient(focus-angle 45deg, focus-distance 20%, center 50% 50%, radius 50%, #a8edea, #fed6e3)");
        VBox example2 = createExampleBox(circle2, desc2);
        
        // 示例3: 椭圆形径向渐变
        Ellipse ellipse3 = new Ellipse(75, 50);
        ellipse3.setStyle("-fx-fill: radial-gradient(center 50% 50%, radius 75%, #ff9a9e, #fecfef);");
        
        VBox desc3 = createDescription("椭圆形径向渐变", "radial-gradient(center 50% 50%, radius 75%, #ff9a9e, #fecfef)");
        VBox example3 = createExampleBox(ellipse3, desc3);
        
        // 示例4: 重复径向渐变
        Circle circle4 = new Circle(50);
        circle4.setStyle("-fx-fill: repeating-radial-gradient(center 50% 50%, radius 20%, #84fab0, #84fab0 5px, #8fd3f4 5px, #8fd3f4 10px);");
        
        VBox desc4 = createDescription("重复径向渐变", "repeating-radial-gradient(center 50% 50%, radius 20%, #84fab0, #84fab0 5px, #8fd3f4 5px, #8fd3f4 10px)");
        VBox example4 = createExampleBox(circle4, desc4);
        
        // 示例5: 多颜色径向渐变
        Circle circle5 = new Circle(50);
        circle5.setStyle("-fx-fill: radial-gradient(center 50% 50%, radius 60%, red 0%, yellow 30%, green 60%, blue 100%);");
        
        VBox desc5 = createDescription("多颜色径向渐变", "radial-gradient(center 50% 50%, radius 60%, red 0%, yellow 30%, green 60%, blue 100%)");
        VBox example5 = createExampleBox(circle5, desc5);
        
        // 示例6: 偏移中心径向渐变
        Circle circle6 = new Circle(50);
        circle6.setStyle("-fx-fill: radial-gradient(center 30% 30%, radius 50%, #667eea, #764ba2);");
        
        VBox desc6 = createDescription("偏移中心", "radial-gradient(center 30% 30%, radius 50%, #667eea, #764ba2)");
        VBox example6 = createExampleBox(circle6, desc6);
        
        // 添加到网格
        grid.add(example1, 0, 0);
        grid.add(example2, 1, 0);
        grid.add(example3, 2, 0);
        grid.add(example4, 0, 1);
        grid.add(example5, 1, 1);
        grid.add(example6, 2, 1);
        
        section.getChildren().addAll(sectionTitle, grid);
        return section;
    }
    
    private VBox createComplexGradientsSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(15));
        section.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1;");
        
        Text sectionTitle = new Text("复杂形状和渐变组合");
        sectionTitle.setFont(Font.font(18));
        sectionTitle.setStyle("-fx-fill: #2c3e50;");
        
        // 创建网格布局
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        
        // 示例1: 多边形线性渐变
        Polygon polygon1 = new Polygon();
        polygon1.getPoints().addAll(
            0.0, 50.0,
            50.0, 0.0,
            100.0, 50.0,
            75.0, 100.0,
            25.0, 100.0
        );
        polygon1.setStyle("-fx-fill: linear-gradient(to bottom right, #ff6b6b, #c56cf0);");
        
        VBox desc1 = createDescription("多边形线性渐变", "linear-gradient(to bottom right, #ff6b6b, #c56cf0)");
        VBox example1 = createExampleBox(polygon1, desc1);
        
        // 示例2: 圆形径向渐变
        Circle circle2 = new Circle(50);
        circle2.setStyle("-fx-fill: radial-gradient(center 30% 30%, radius 80%, #ffafbd, #ffc3a0);");
        
        VBox desc2 = createDescription("圆形径向渐变", "radial-gradient(center 30% 30%, radius 80%, #ffafbd, #ffc3a0)");
        VBox example2 = createExampleBox(circle2, desc2);
        
        // 示例3: 复杂路径渐变
        Path path3 = new Path();
        path3.getElements().addAll(
            new MoveTo(25, 0),
            new LineTo(75, 0),
            new LineTo(100, 50),
            new LineTo(75, 100),
            new LineTo(25, 100),
            new LineTo(0, 50),
            new ClosePath()
        );
        path3.setStyle("-fx-fill: linear-gradient(135deg, #667eea 0%, #764ba2 100%);");
        
        VBox desc3 = createDescription("复杂路径渐变", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)");
        VBox example3 = createExampleBox(path3, desc3);
        
        // 示例4: 椭圆径向渐变
        Ellipse ellipse4 = new Ellipse(75, 50);
        ellipse4.setStyle("-fx-fill: radial-gradient(focus-angle 0deg, focus-distance 0%, center 50% 50%, radius 100%, #f093fb, #f5576c);");
        
        VBox desc4 = createDescription("椭圆径向渐变", "radial-gradient(focus-angle 0deg, focus-distance 0%, center 50% 50%, radius 100%, #f093fb, #f5576c)");
        VBox example4 = createExampleBox(ellipse4, desc4);
        
        // 示例5: 梯形线性渐变
        Polygon polygon5 = new Polygon();
        polygon5.getPoints().addAll(
            25.0, 0.0,
            75.0, 0.0,
            100.0, 100.0,
            0.0, 100.0
        );
        polygon5.setStyle("-fx-fill: linear-gradient(to bottom, #4facfe, #00f2fe);");
        
        VBox desc5 = createDescription("梯形线性渐变", "linear-gradient(to bottom, #4facfe, #00f2fe)");
        VBox example5 = createExampleBox(polygon5, desc5);
        
        // 示例6: 星形径向渐变
        Polygon star6 = createStar(50, 50, 5, 20, 40);
        star6.setStyle("-fx-fill: radial-gradient(center 50% 50%, radius 60%, #ffecd2, #fcb69f);");
        
        VBox desc6 = createDescription("星形径向渐变", "radial-gradient(center 50% 50%, radius 60%, #ffecd2, #fcb69f)");
        VBox example6 = createExampleBox(star6, desc6);
        
        // 添加到网格
        grid.add(example1, 0, 0);
        grid.add(example2, 1, 0);
        grid.add(example3, 2, 0);
        grid.add(example4, 0, 1);
        grid.add(example5, 1, 1);
        grid.add(example6, 2, 1);
        
        section.getChildren().addAll(sectionTitle, grid);
        return section;
    }
    
    private VBox createExampleBox(Shape shape, VBox description) {
        VBox exampleBox = new VBox(10);
        exampleBox.setAlignment(Pos.CENTER);
        exampleBox.setPadding(new Insets(10));
        exampleBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1;");
        
        // 创建形状容器
        Pane shapeContainer = new Pane(shape);
        shapeContainer.setPrefSize(150, 100);
        shapeContainer.setStyle("-fx-background-color: transparent;");
        
        exampleBox.getChildren().addAll(shapeContainer, description);
        return exampleBox;
    }
    
    private VBox createDescription(String title, String gradientCode) {
        VBox description = new VBox(5);
        description.setAlignment(Pos.CENTER);
        
        Text titleText = new Text(title);
        titleText.setFont(Font.font(14));
        titleText.setStyle("-fx-fill: #495057;");
        
        Text codeText = new Text(gradientCode);
        codeText.setFont(Font.font(10));
        codeText.setStyle("-fx-fill: #6c757d;");
        codeText.setWrappingWidth(140);
        
        description.getChildren().addAll(titleText, codeText);
        return description;
    }
    
    // 创建星形多边形
    private Polygon createStar(double centerX, double centerY, int points, double innerRadius, double outerRadius) {
        Polygon star = new Polygon();
        
        for (int i = 0; i < points * 2; i++) {
            double radius = (i % 2 == 0) ? outerRadius : innerRadius;
            double angle = Math.PI / points * i;
            
            double x = centerX + radius * Math.sin(angle);
            double y = centerY - radius * Math.cos(angle);
            
            star.getPoints().addAll(x, y);
        }
        
        return star;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}