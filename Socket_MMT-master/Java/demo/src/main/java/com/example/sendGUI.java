package com.example;

import java.io.File;
import javax.swing.JFileChooser;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class sendGUI {

    @FXML
    private AnchorPane main;

    @FXML
    private TextField to;

    @FXML
    private TextField bcc;

    @FXML
    private TextField cc;

    @FXML
    private TextField subject;

    @FXML
    private TextArea content;

    @FXML
    private Label file1;

    @FXML
    private Label file2;

    @FXML
    private Label file3;

    @FXML
    private Button button1;

    @FXML
    private Button button2;

    @FXML
    private Button button3;

    @FXML
    private Button buttonSend;

    Email email=new Email();
    Data data= email.getData();
    
    @FXML
    void SendEmail(ActionEvent event) throws Exception {
        if(to.getText().equals("")&&cc.getText().equals("")&&bcc.getText().equals(""))
        {
            Alert();
            return;
        }
        if(subject.getText().equals(""))
        {
            Alert();
            return;
        }
        buttonSend.setDisable(true);
        Client client = new Client().readConfigFile();
        SendEmail sendEmail=new SendEmail(client.getGeneral());
        sendEmail.setEmail(email);
        email.setFrom(client.getGeneral().getUsername());
        email.addTo(to.getText());
        email.addCc(cc.getText());
        email.addBcc(bcc.getText());   
        data.setSubject(subject.getText());
        data.addContents(content.getText());
        //System.out.println(data.getContents());
        sendEmail.send();
        showSuccess();
        Stage primaryStage = (Stage) main.getScene().getWindow();
        primaryStage.close();
    }

    @FXML
    void attach1(ActionEvent event) {
        JFileChooser jFileChooser=new JFileChooser();
        File fileToSend;
        if(jFileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION)
        {
            fileToSend=jFileChooser.getSelectedFile();
            
            if(fileToSend.length()>1024*1024*3)
            {
                showAlert();
                return;
            }
            data.addAttachment(fileToSend);
            button1.setText("Attached");
            file1.setText(fileToSend.getName());
            button1.setDisable(true);
            button2.setVisible(true);
        }
    }

    @FXML
    void attach2(ActionEvent event) {
        JFileChooser jFileChooser=new JFileChooser();
        File fileToSend;
        if(jFileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION)
        {
            fileToSend=jFileChooser.getSelectedFile();
            
            if(fileToSend.length()>1024*1024*3)
            {
                showAlert();
                return;
            }
            data.addAttachment(fileToSend);
            button2.setText("Attached");
            file2.setText(fileToSend.getName());
            button2.setDisable(true);
            button3.setVisible(true);
        } 
    }

    @FXML
    void attach3(ActionEvent event) {
        JFileChooser jFileChooser=new JFileChooser();
        File fileToSend;
        if(jFileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION)
        {
            fileToSend=jFileChooser.getSelectedFile();
            
            if(fileToSend.length()>1024*1024*3)
            {
                showAlert();
                return;
            }
            data.addAttachment(fileToSend);
            button3.setText("Attached");
            file3.setText(fileToSend.getName());
            button3.setDisable(true);
        } 
    }

    void showSuccess()
    {
        Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Success");
		// Header Text: null
		alert.setHeaderText(null);
		alert.setContentText("Email sent successfully!");
		alert.showAndWait();
    }
    void showAlert()
    {
        Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Error");
		// Header Text: null
		alert.setHeaderText(null);
		alert.setContentText("Attachment's size is large than 3MB!\n Please choose again.");
		alert.showAndWait();
    }
    void Alert()
    {
        Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Error");
		// Header Text: null
		alert.setHeaderText(null);
		alert.setContentText("Please type both receiver and subject!");
		alert.showAndWait();
    }
}


