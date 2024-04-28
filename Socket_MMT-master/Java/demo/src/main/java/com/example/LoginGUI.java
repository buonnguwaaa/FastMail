package com.example;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class LoginGUI  {
    
    @FXML
    private HBox mainLoginScene;

    @FXML
    private Button loginBtn;

    @FXML
    private PasswordField password;

    @FXML
    private TextField username;

    @FXML
    private Label error;


    static String usernameString="";
    static String passString="";

    static String getUsername()
    {
        return usernameString;
    }
    static String getPassword()
    {
        return passString;
    }
    int handleLoginForm() {
        return 0;
    }
    @FXML
    void login(ActionEvent event) {
        usernameString=username.getText();
        passString=password.getText();
        System.out.println("tK:"+ usernameString);
       // System.out.println("mk:" +passString);
        if(usernameString.equals("")||passString.equals(""))
        {
            error.setVisible(true);
            error.setText("   Please type both username and password!");
            return;
        }
        int index=usernameString.indexOf("@", 0);
        if(index==-1)
        {
            error.setVisible(true);
            error.setText("Username has wrong format. Please type again!        -Ex: abc@example.com");
            return;
        }
        int index2=usernameString.indexOf(".", index);
        if(index2==-1)
        {
            error.setVisible(true);
            error.setText("Username has wrong format. Please type again!        -Ex: abc@example.com");
            return;
        }
        Stage loginStage = (Stage) mainLoginScene.getScene().getWindow();
        loginStage.close();
        Stage primaryStage = new Stage();
        Parent root;
            
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/mainScreen.fxml"));
            Scene scene = new Scene(root);
            scene.setCursor(Cursor.DEFAULT);
            primaryStage.setTitle("Fast Email");
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setOnCloseRequest(e -> {
                Platform.exit();
                System.exit(0);
            });
        } catch (Exception err) {
            // TODO: handle exception
        }
    }

}
