package com.example;
import java.io.IOException;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    // public static void sendMail() {
    //     Application.launch();
    // }

    public static void execute() {       
        Application.launch();
    }
    
    @Override
    public void start(Stage primaryStage) {

        Parent root;
        try {
            
            root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(root);
            scene.setCursor(Cursor.DEFAULT);

            primaryStage.setTitle("Fast Mail");
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(e -> {
                Platform.exit();
                System.exit(0);
            });
            primaryStage.show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        
    }
}
