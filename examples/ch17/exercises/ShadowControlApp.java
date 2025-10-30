import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ShadowControlAppEnhanced extends Application {
    
    // 阴影效果
    private DropShadow dropShadow;
    
    // 显示阴影参数的标签
    private Label radiusLabel;
    private Label offsetXLabel;
    private Label offsetYLabel;
    private Label spreadLabel;
    private Label colorLabel;
    
    // 颜色选择器
    private ColorPicker colorPicker;
    
    @Override
    public void start(Stage primaryStage) {
        // 创建阴影效果
        dropShadow = new DropShadow();
        dropShadow.setColor(Color.BLACK);
        
        // 创建矩形
        Rectangle rectangle = createRectangle();
        
        // 创建控制面板
        VBox controlPanel = createControlPanel();
        
        // 创建信息面板
        VBox infoPanel = createInfoPanel();
        
        // 创建主布局
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #e6f7ff, #b3e0ff);");
        
        // 将组件添加到布局
        root.setCenter(rectangle);
        root.setRight(controlPanel);
        root.setBottom(infoPanel);
        
        // 创建场景
        Scene scene = new Scene(root, 1000, 700);
        
        // 设置舞台
        primaryStage.setTitle("JavaFX 阴影控制应用程序 - 增强版");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private Rectangle createRectangle() {
        Rectangle rectangle = new Rectangle(350, 250);
        rectangle.setFill(Color.rgb(100, 149, 237)); // 矢车菊蓝
        rectangle.setStroke(Color.DARKBLUE);
        rectangle.setStrokeWidth(2);
        
        // 应用阴影效果
        rectangle.setEffect(dropShadow);
        
        return rectangle;
    }
    
    private VBox createControlPanel() {
        VBox controlPanel = new VBox(20);
        controlPanel.setPadding(new Insets(20));
        controlPanel.setPrefWidth(350);
        controlPanel.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1;");
        
        // 标题
        Label title = new Label("阴影控制面板");
        title.setFont(Font.font(18));
        title.setStyle("-fx-text-fill: #2c3e50;");
        
        // 创建阴影半径滑块
        VBox radiusControl = createSliderControl(
            "阴影半径", 
            0, 100, 
            dropShadow.getRadius(), 
            "控制阴影的模糊程度"
        );
        
        // 创建X偏移滑块
        VBox offsetXControl = createSliderControl(
            "X轴偏移", 
            -50, 50, 
            dropShadow.getOffsetX(), 
            "控制阴影在X轴方向的偏移"
        );
        
        // 创建Y偏移滑块
        VBox offsetYControl = createSliderControl(
            "Y轴偏移", 
            -50, 50, 
            dropShadow.getOffsetY(), 
            "控制阴影在Y轴方向的偏移"
        );
        
        // 创建扩散度滑块
        VBox spreadControl = createSliderControl(
            "阴影扩散", 
            0, 1, 
            dropShadow.getSpread(), 
            "控制阴影的扩散程度 (0.0-1.0)"
        );
        
        // 颜色选择控制
        VBox colorControl = createColorControl();
        
        // 预设颜色控制
        VBox presetControl = createPresetColorControl();
        
        controlPanel.getChildren().addAll(
            title, radiusControl, offsetXControl, offsetYControl, 
            spreadControl, colorControl, presetControl
        );
        
        return controlPanel;
    }
    
    private VBox createSliderControl(String label, double min, double max, double initial, String description) {
        VBox control = new VBox(8);
        
        // 创建标签
        Label controlLabel = new Label(label);
        controlLabel.setFont(Font.font(14));
        controlLabel.setStyle("-fx-text-fill: #34495e;");
        
        // 创建滑块
        Slider slider = new Slider(min, max, initial);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit((max - min) / 5);
        slider.setBlockIncrement((max - min) / 20);
        
        // 创建值标签
        Label valueLabel = new Label(String.format("%.1f", initial));
        valueLabel.setFont(Font.font(12));
        valueLabel.setStyle("-fx-text-fill: #7f8c8d;");
        
        // 创建描述标签
        Label descLabel = new Label(description);
        descLabel.setFont(Font.font(10));
        descLabel.setStyle("-fx-text-fill: #95a5a6;");
        
        // 添加监听器
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double value = newValue.doubleValue();
                valueLabel.setText(String.format("%.1f", value));
                
                // 根据标签更新对应的阴影属性
                switch (label) {
                    case "阴影半径":
                        dropShadow.setRadius(value);
                        break;
                    case "X轴偏移":
                        dropShadow.setOffsetX(value);
                        break;
                    case "Y轴偏移":
                        dropShadow.setOffsetY(value);
                        break;
                    case "阴影扩散":
                        dropShadow.setSpread(value);
                        break;
                }
                
                // 更新信息面板
                updateInfoPanel();
            }
        });
        
        control.getChildren().addAll(controlLabel, slider, valueLabel, descLabel);
        return control;
    }
    
    private VBox createColorControl() {
        VBox colorControl = new VBox(15);
        
        Label colorLabel = new Label("阴影颜色控制");
        colorLabel.setFont(Font.font(16));
        colorLabel.setStyle("-fx-text-fill: #34495e;");
        
        // 创建颜色选择器
        HBox colorPickerBox = new HBox(10);
        colorPickerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label pickerLabel = new Label("颜色选择器:");
        pickerLabel.setFont(Font.font(14));
        
        colorPicker = new ColorPicker(dropShadow.getColor());
        colorPicker.setPrefWidth(150);
        
        // 颜色选择器监听器
        colorPicker.valueProperty().addListener(new ChangeListener<Color>() {
            @Override
            public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
                dropShadow.setColor(newValue);
                updateInfoPanel();
            }
        });
        
        colorPickerBox.getChildren().addAll(pickerLabel, colorPicker);
        
        // 创建颜色选择滑块
        VBox colorSliders = new VBox(10);
        colorSliders.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-border-color: #dee2e6; -fx-border-width: 1;");
        
        Label slidersLabel = new Label("RGB和透明度调节");
        slidersLabel.setFont(Font.font(14));
        slidersLabel.setStyle("-fx-text-fill: #495057;");
        
        HBox rgbSliders = new HBox(10);
        
        // 红色滑块
        VBox redControl = createColorSlider("红", 0, 255, 
            (int)(dropShadow.getColor().getRed() * 255));
        
        // 绿色滑块
        VBox greenControl = createColorSlider("绿", 0, 255, 
            (int)(dropShadow.getColor().getGreen() * 255));
        
        // 蓝色滑块
        VBox blueControl = createColorSlider("蓝", 0, 255, 
            (int)(dropShadow.getColor().getBlue() * 255));
        
        // 透明度滑块
        VBox alphaControl = createColorSlider("透明度", 0, 100, 
            (int)(dropShadow.getColor().getOpacity() * 100));
        
        rgbSliders.getChildren().addAll(redControl, greenControl, blueControl, alphaControl);
        colorSliders.getChildren().addAll(slidersLabel, rgbSliders);
        
        colorControl.getChildren().addAll(colorLabel, colorPickerBox, colorSliders);
        return colorControl;
    }
    
    private VBox createColorSlider(String colorName, int min, int max, int initial) {
        VBox control = new VBox(5);
        control.setAlignment(Pos.CENTER);
        
        Label label = new Label(colorName);
        label.setFont(Font.font(10));
        label.setStyle("-fx-text-fill: #7f8c8d;");
        
        Slider slider = new Slider(min, max, initial);
        slider.setOrientation(Orientation.VERTICAL);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setPrefHeight(120);
        
        Label valueLabel = new Label(String.valueOf(initial));
        valueLabel.setFont(Font.font(10));
        valueLabel.setStyle("-fx-text-fill: #7f8c8d;");
        
        // 添加监听器
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                int value = newValue.intValue();
                valueLabel.setText(String.valueOf(value));
                
                // 更新阴影颜色
                Color currentColor = dropShadow.getColor();
                double red = currentColor.getRed();
                double green = currentColor.getGreen();
                double blue = currentColor.getBlue();
                double alpha = currentColor.getOpacity();
                
                switch (colorName) {
                    case "红":
                        red = value / 255.0;
                        break;
                    case "绿":
                        green = value / 255.0;
                        break;
                    case "蓝":
                        blue = value / 255.0;
                        break;
                    case "透明度":
                        alpha = value / 100.0;
                        break;
                }
                
                Color newColor = Color.color(red, green, blue, alpha);
                dropShadow.setColor(newColor);
                
                // 更新颜色选择器
                colorPicker.setValue(newColor);
                
                updateInfoPanel();
            }
        });
        
        control.getChildren().addAll(slider, valueLabel, label);
        return control;
    }
    
    private VBox createPresetColorControl() {
        VBox presetControl = new VBox(10);
        presetControl.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-border-color: #dee2e6; -fx-border-width: 1;");
        
        Label presetLabel = new Label("预设阴影颜色");
        presetLabel.setFont(Font.font(14));
        presetLabel.setStyle("-fx-text-fill: #495057;");
        
        // 创建颜色按钮网格
        GridPane colorGrid = new GridPane();
        colorGrid.setHgap(5);
        colorGrid.setVgap(5);
        
        // 定义预设颜色
        Color[] presetColors = {
            Color.BLACK,           // 黑色
            Color.GRAY,            // 灰色
            Color.DARKRED,         // 深红色
            Color.RED,             // 红色
            Color.ORANGE,          // 橙色
            Color.GOLD,            // 金色
            Color.DARKGREEN,       // 深绿色
            Color.GREEN,           // 绿色
            Color.TEAL,            // 青蓝色
            Color.DARKBLUE,        // 深蓝色
            Color.BLUE,            // 蓝色
            Color.PURPLE,          // 紫色
            Color.color(0.5, 0, 0.5, 0.8), // 半透明紫色
            Color.color(0, 0, 0, 0.5)      // 半透明黑色
        };
        
        String[] colorNames = {
            "黑色", "灰色", "深红", "红色", "橙色", "金色",
            "深绿", "绿色", "青蓝", "深蓝", "蓝色", "紫色",
            "半透紫", "半透黑"
        };
        
        // 创建颜色按钮
        for (int i = 0; i < presetColors.length; i++) {
            Button colorButton = new Button(colorNames[i]);
            colorButton.setPrefWidth(80);
            colorButton.setStyle(String.format(
                "-fx-background-color: #%02X%02X%02X; -fx-text-fill: %s;",
                (int)(presetColors[i].getRed() * 255),
                (int)(presetColors[i].getGreen() * 255),
                (int)(presetColors[i].getBlue() * 255),
                presetColors[i].getBrightness() > 0.5 ? "black" : "white"
            ));
            
            final Color color = presetColors[i];
            colorButton.setOnAction(e -> {
                dropShadow.setColor(color);
                colorPicker.setValue(color);
                updateInfoPanel();
            });
            
            colorGrid.add(colorButton, i % 4, i / 4);
        }
        
        presetControl.getChildren().addAll(presetLabel, colorGrid);
        return presetControl;
    }
    
    private VBox createInfoPanel() {
        VBox infoPanel = new VBox(10);
        infoPanel.setPadding(new Insets(15));
        infoPanel.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1 0 0 0;");
        
        Label title = new Label("阴影参数信息");
        title.setFont(Font.font(16));
        title.setStyle("-fx-text-fill: #2c3e50;");
        
        // 创建参数标签
        radiusLabel = new Label();
        offsetXLabel = new Label();
        offsetYLabel = new Label();
        spreadLabel = new Label();
        colorLabel = new Label();
        
        // 设置标签样式
        String labelStyle = "-fx-text-fill: #34495e; -fx-font-family: 'Monospaced';";
        radiusLabel.setStyle(labelStyle);
        offsetXLabel.setStyle(labelStyle);
        offsetYLabel.setStyle(labelStyle);
        spreadLabel.setStyle(labelStyle);
        colorLabel.setStyle(labelStyle);
        
        // 初始更新
        updateInfoPanel();
        
        infoPanel.getChildren().addAll(
            title, radiusLabel, offsetXLabel, offsetYLabel, spreadLabel, colorLabel
        );
        
        return infoPanel;
    }
    
    private void updateInfoPanel() {
        Color shadowColor = dropShadow.getColor();
        
        radiusLabel.setText(String.format("阴影半径: %.1f", dropShadow.getRadius()));
        offsetXLabel.setText(String.format("X轴偏移: %.1f", dropShadow.getOffsetX()));
        offsetYLabel.setText(String.format("Y轴偏移: %.1f", dropShadow.getOffsetY()));
        spreadLabel.setText(String.format("阴影扩散: %.2f", dropShadow.getSpread()));
        colorLabel.setText(String.format(
            "阴影颜色: RGB(%.0f,%.0f,%.0f) 透明度: %.0f%% | HEX: #%02X%02X%02X", 
            shadowColor.getRed() * 255,
            shadowColor.getGreen() * 255,
            shadowColor.getBlue() * 255,
            shadowColor.getOpacity() * 100,
            (int)(shadowColor.getRed() * 255),
            (int)(shadowColor.getGreen() * 255),
            (int)(shadowColor.getBlue() * 255)
        ));
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}