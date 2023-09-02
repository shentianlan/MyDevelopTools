package cn.shentianlan.mydeveloptools;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MyDevelopToolsApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("MyDevelopTools.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 500);
        primaryStage.setIconified(false);
        primaryStage.getIcons().add(new Image("./icon/MyDevelopTools.jpg"));
        primaryStage.setScene(scene);
        primaryStage.setTitle("开发辅助工具");
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}