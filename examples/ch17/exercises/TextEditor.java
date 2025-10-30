import javafx.application.Application;
import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.util.Optional;

public class TextEditor extends Application {

    private TextArea editor;
    private Stage primaryStage;
    private File currentFile;
    private boolean documentChanged = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("文本编辑器");

        // 创建文本编辑器
        editor = new TextArea();
        
        // 创建菜单栏
        MenuBar menuBar = createMenuBar();
        
        // 创建状态栏
        Label statusLabel = new Label("就绪");
        HBox statusBar = new HBox(statusLabel);
        statusBar.setPadding(new Insets(5));
        
        // 监听文档变化
        editor.textProperty().addListener((obs, oldText, newText) -> {
            documentChanged = true;
            statusLabel.setText("已修改");
        });

        // 布局
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(editor);
        root.setBottom(statusBar);
        
        // 设置场景
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // 设置关闭确认
        primaryStage.setOnCloseRequest(event -> {
            if (documentChanged) {
                if (!confirmSave()) {
                    event.consume();
                }
            }
        });
    }

    private MenuBar createMenuBar() {
        // 文件菜单
        Menu fileMenu = new Menu("文件");
        
        MenuItem newItem = new MenuItem("新建");
        newItem.setOnAction(e -> newDocument());
        
        MenuItem openItem = new MenuItem("打开");
        openItem.setOnAction(e -> openDocument());
        
        MenuItem saveItem = new MenuItem("保存");
        saveItem.setOnAction(e -> saveDocument(false));
        
        MenuItem saveAsItem = new MenuItem("另存为");
        saveAsItem.setOnAction(e -> saveDocument(true));
        
        MenuItem exitItem = new MenuItem("退出");
        exitItem.setOnAction(e -> {
            if (documentChanged) {
                if (confirmSave()) {
                    primaryStage.close();
                }
            } else {
                primaryStage.close();
            }
        });
        
        fileMenu.getItems().addAll(newItem, openItem, saveItem, saveAsItem, new SeparatorMenuItem(), exitItem);
        
        // 编辑菜单
        Menu editMenu = new Menu("编辑");
        
        MenuItem cutItem = new MenuItem("剪切");
        cutItem.setOnAction(e -> editor.cut());
        
        MenuItem copyItem = new MenuItem("复制");
        copyItem.setOnAction(e -> editor.copy());
        
        MenuItem pasteItem = new MenuItem("粘贴");
        pasteItem.setOnAction(e -> editor.paste());
        
        MenuItem clearItem = new MenuItem("清空");
        clearItem.setOnAction(e -> editor.clear());
        
        editMenu.getItems().addAll(cutItem, copyItem, pasteItem, new SeparatorMenuItem(), clearItem);
        
        // 帮助菜单
        Menu helpMenu = new Menu("帮助");
        
        MenuItem aboutItem = new MenuItem("关于");
        aboutItem.setOnAction(e -> showAboutDialog());
        
        helpMenu.getItems().add(aboutItem);
        
        // 创建菜单栏并添加菜单
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);
        
        return menuBar;
    }

    // 文件操作方法
    private void newDocument() {
        if (documentChanged) {
            if (!confirmSave()) {
                return;
            }
        }
        
        editor.clear();
        currentFile = null;
        documentChanged = false;
        primaryStage.setTitle("文本编辑器 - 新文档");
    }

    private void openDocument() {
        if (documentChanged) {
            if (!confirmSave()) {
                return;
            }
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("打开文档");
        fileChooser.getExtensionFilters().addAll(
            new ExtensionFilter("文本文档", "*.txt"),
            new ExtensionFilter("所有文件", "*.*")
        );
        
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                // 打开文本文件
                String content = Files.readString(file.toPath());
                editor.setText(content);
                currentFile = file;
                documentChanged = false;
                primaryStage.setTitle("文本编辑器 - " + file.getName());
            } catch (Exception e) {
                showErrorDialog("打开文件错误", "无法打开文件: " + e.getMessage());
            }
        }
    }

    private boolean saveDocument(boolean saveAs) {
        if (currentFile == null || saveAs) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("保存文档");
            fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("文本文档", "*.txt"),
                new ExtensionFilter("所有文件", "*.*")
            );
            
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                currentFile = file;
            } else {
                return false;
            }
        }
        
        try {
            // 保存文本
            Files.writeString(currentFile.toPath(), editor.getText());
            documentChanged = false;
            primaryStage.setTitle("文本编辑器 - " + currentFile.getName());
            return true;
        } catch (Exception e) {
            showErrorDialog("保存文件错误", "无法保存文件: " + e.getMessage());
            return false;
        }
    }

    private boolean confirmSave() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("保存确认");
        alert.setHeaderText("文档已修改");
        alert.setContentText("是否保存更改?");
        
        ButtonType saveButton = new ButtonType("保存");
        ButtonType dontSaveButton = new ButtonType("不保存");
        ButtonType cancelButton = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(saveButton, dontSaveButton, cancelButton);
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == saveButton) {
                return saveDocument(false);
            } else return result.get() == dontSaveButton;
        }
        return false;
    }

    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("关于");
        alert.setHeaderText("文本编辑器");
        
        // 创建可点击的链接
        Hyperlink link = new Hyperlink("https://bookzhou.com");
        link.setOnAction(e -> {
            HostServices hostServices = getHostServices();
            hostServices.showDocument(link.getText());
        });
        
        // 创建内容布局
        VBox content = new VBox(5);
        content.getChildren().addAll(
            new Text("版本 1.0"),
            new Text("一个简单的文本编辑器示例。详情请访问:"),
            link
        );
        
        // 设置对话框内容
        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}