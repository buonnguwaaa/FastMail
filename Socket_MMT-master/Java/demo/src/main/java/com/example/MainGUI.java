package com.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.paint.Color;

public class MainGUI {

    @FXML
    private Button sendBtn;

    @FXML
    private TabPane tabList;

    @FXML
    private Tab inboxTab;

    @FXML
    private Tab projectTab;

    @FXML
    private Tab subjectTab;

    @FXML
    private Tab workTab;

    @FXML
    private Tab spamTab;

    @FXML
    private AnchorPane MailDetail;

    @FXML
    private Label senderLabel;

    @FXML
    private Label toLabel;

    @FXML
    private Label ccLabel;

    @FXML
    private Label subjectLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private SplitPane splitPane;

    @FXML
    private ScrollPane scrollPane;

    private ReceiveSocket receiveSocket;
    private ArrayList<HashMap<Integer, Email>> foldersFilter;

    private Email emailCheck;

    private ArrayList<String> pathsFolder;

    private ChangeListener<String> changeListViewItem;
    private ChangeListener<String> changeFileListen;
    private ChangeListener<Tab> changeTab;

    private ObservableList<String> listEmailItems;

    private double contentAreaHeight = 0;

    String convertAbsPathToURL(String path) {
        return path.replace('\\', '/');
    }

    ImageView createImageView(String imgURL, double layoutX, double layoutY) {
        Image image = new Image("file:///" + imgURL);
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitWidth(450);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        imageView.setLayoutX(layoutX);
        imageView.setLayoutY(layoutY);
        return imageView;
    }

    void addImageView(Email email) throws IOException {
        int n = 0;
        AnchorPane imgViewWrapper = new AnchorPane();
        Node lastChild = null;
        if (email.getData().getAtm().size() != 0) {
            for (File file : email.getData().getAtm()) {
                String[] imgFormat = {".jpeg", ".png", ".jpg"};
                for (String format : imgFormat) {
                    if (file.getName().contains(format)) {
                        n++;
                        if (n == 1) {
                            Label imgFlag = new Label("Attach Images: ");
                            imgFlag.setStyle("-fx-font-style: italic; -fx-font-weight: bold;");
                            imgViewWrapper.getChildren().add(imgFlag);
                        }
                        lastChild = imgViewWrapper.getChildren().get(imgViewWrapper.getChildren().size() - 1);
                        Label imgLabel = new Label(file.getName());
                        imgLabel.setLayoutX(15);
                        imgLabel.setLayoutY(lastChild.getLayoutY() + lastChild.getBoundsInLocal().getHeight() + 15);
                        imgLabel.setStyle("-fx-font-style: italic;");
                        imgViewWrapper.getChildren().add(imgLabel);
                        ImageView imageView = createImageView(convertAbsPathToURL(file.getPath()), 15, imgLabel.localToScene(0, imgLabel.getBoundsInLocal().getMaxY()).getY() + 30);
                        imgViewWrapper.getChildren().add(imageView);
                        // double newHeight = imgViewWrapper.getHeight() + imageView.getBoundsInParent().getMaxY() + 10;
                        // imgViewWrapper.setPrefHeight(newHeight);
                        break;
                    }
                }

            }
        }
        if (n != 0) {
            lastChild = MailDetail.getChildren().get(MailDetail.getChildren().size() - 1);
            imgViewWrapper.setId("myImgViewWrapper");
            imgViewWrapper.setLayoutX(15);
            imgViewWrapper.setLayoutY(MailDetail.getPrefHeight());
            MailDetail.getChildren().add(imgViewWrapper);
            double newHeight = MailDetail.getPrefHeight() + imgViewWrapper.getBoundsInParent().getHeight() + 30;
            MailDetail.setPrefHeight(newHeight);
        }
    }

    void addAttachmentList(Email email) {

        Node lastChild = MailDetail.getChildren().get(MailDetail.getChildren().size() - 1);
        if (email.getData().getAtm().size() == 0) {
            return;
        }
        ArrayList<String> arrayListAttachment = new ArrayList<String>();
        String fileName;
        for (int i = 0; i < email.getData().getAtm().size(); i++) {
            // statusEmail[i - 1] = email.getChecked();
            fileName = email.getData().getAtm(i).getName();
            arrayListAttachment.add(fileName);
        }
        ObservableList listAtmFile = FXCollections.observableArrayList(arrayListAttachment);
        ListView<String> listView = new ListView<String>(listAtmFile);
        listView.setId("myAttachmentListView");
        listView.setMinWidth(300);
        listView.setMaxHeight(100);
        listView.setLayoutX(15);
        listView.setLayoutY(MailDetail.getPrefHeight() + 30);
        listView.setCursor(Cursor.HAND);
        Label listAtmLabel = new Label("Download attachment files: ");
        listAtmLabel.setLayoutX(15);
        listAtmLabel.setLayoutY(MailDetail.getPrefHeight());
        listAtmLabel.setId("myAtmLabel");
        listAtmLabel.setStyle("-fx-font-style: italic; -fx-font-weight: bold;");
        MailDetail.getChildren().add(listAtmLabel);
        MailDetail.getChildren().add(listView);
        double newHeight = MailDetail.getPrefHeight() + listView.getMaxHeight() + 60;
        MailDetail.setPrefHeight(newHeight);
    }

    ChangeListener<String> handleDownloadAttachmentFile(ListView<String> listView) {

        return new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {

                int idx = listView.getSelectionModel().getSelectedIndex();
                if (idx >= 0) {
                    // String fileName = emailCheck.getData().getAtm(idx).getPath();
                    // String [] commands = {
                    // "cmd.exe" , "/c", "start" , "\"DummyTitle\"", "\"" + fileName + "\""
                    // };
                    // ProcessBuilder builder = new ProcessBuilder(commands);
                    // try {
                    // builder.start();
                    // } catch (IOException e1) {
                    // // TODO Auto-generated catch block
                    // e1.printStackTrace();
                    // }
                    int ktra = emailCheck.downloadAttachmentFile(idx);
                    if (ktra == 1)
                        showSuccess();

                } else {
                    System.out.println("Error!");
                }
            }
        };
    }

    void handleSelecteAttachmentFile() {
        ListView<String> attachmentListView = (ListView) MailDetail.lookup("#myAttachmentListView");
        if (attachmentListView == null) {
            return;
        }
        if (changeFileListen != null) {
            attachmentListView.getSelectionModel().selectedItemProperty().removeListener(changeFileListen);
        }
        ChangeListener<String> changeListener = handleDownloadAttachmentFile(attachmentListView);
        attachmentListView.getSelectionModel().selectedItemProperty().addListener(changeFileListen = changeListener);
    }

    void clearContent() {
        senderLabel.setText("");
        toLabel.setText("");
        ccLabel.setText("");
        dateLabel.setText("");
        subjectLabel.setText("");
        MailDetail.getChildren().remove(MailDetail.lookup("#myContentLabel"));
        if (MailDetail.lookup("#myAttachmentListView") != null) {
            double newHeight = MailDetail.getPrefHeight() - ((ListView) MailDetail.lookup("#myAttachmentListView")).getMaxHeight() - 60;
            MailDetail.getChildren().remove((ListView) MailDetail.lookup("#myAttachmentListView"));
            MailDetail.getChildren().remove((Label) MailDetail.lookup("#myAtmLabel"));
            MailDetail.setPrefHeight(newHeight);
        }
        if (MailDetail.lookup("#myImgViewWrapper") != null) {
            double newHeight = MailDetail.getPrefHeight() - MailDetail.lookup("#myImgViewWrapper").getBoundsInParent().getHeight() - 30;
            MailDetail.getChildren().remove((AnchorPane) MailDetail.lookup("#myImgViewWrapper"));
            MailDetail.setPrefHeight(newHeight);
        }
        if (MailDetail.lookup("#myContentArea") != null) {
            double newHeight = MailDetail.getPrefHeight() - contentAreaHeight - 30;
            MailDetail.getChildren().remove(MailDetail.lookup("#myContentArea"));
            MailDetail.setPrefHeight(newHeight);
        }
        // contentArea.getChildren().clear();
        // attachmentLabel.setVisible(false);
        // attachmentListView.setVisible(false);
    }

    void handleSeclectedTab() {

        if (changeTab != null) {
            tabList.getSelectionModel().selectedItemProperty().removeListener(changeTab);
        }

        tabList.getSelectionModel().selectedItemProperty().addListener(changeTab = new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> arg0, Tab arg1, Tab arg2) {
                clearContent();
                int index = tabList.getSelectionModel().getSelectedIndex();

                printEmailListAtFolder(index);
                handleSelectEmailInFolder();
                //handleSelecteAttachmentFile(index);

            }
        });
    }

    void handleSelectEmailInFolder() {
        int idFolder = tabList.getSelectionModel().getSelectedIndex();
        HashMap<Integer, Email> ListmailInFolder = foldersFilter.get(idFolder);
        AnchorPane anchorPaneListView = (AnchorPane) splitPane.getItems().get(0);
        ListView<String> mailListView = (ListView<String>) anchorPaneListView.getChildren().get(0);

        if (changeListViewItem != null) {
            mailListView.getSelectionModel().selectedItemProperty().removeListener(changeListViewItem);
        }
        mailListView.getSelectionModel().selectedItemProperty()
                .addListener(changeListViewItem = new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                        //attachmentListView.getItems().clear();
                      
                        int index = mailListView.getSelectionModel().getSelectedIndex();
                        if (index == -1 && changeListViewItem != null) {
                            mailListView.getSelectionModel().selectedItemProperty().removeListener(changeListViewItem);
                        }
                        if (index < 0) {
                            return;
                        }
                          clearContent();
                        scrollPane.setVvalue(0.0);
                        Email email = ListmailInFolder.get(index + 1);

                        if (index != -1) {

                            // mailListView.set(index,"a");
                            emailCheck = email;
                            // //update status
                            if (emailCheck != null) {

                                emailCheck.setCheck(1);
                                /////////// 
                                String emailReview=email.getDate().substring(5)+" ";
                                emailReview += (email.getChecked() == 1) ? "" : "(Chua doc)";
                                emailReview += "\nFrom: " + email.getFrom() + '\n' + "Subject: " + email.getData().getSubject();
                                ////////
                            //    String emailReview = email.getDate() + '\n';
                            //    emailReview += (email.getChecked() == 1) ? "(Da doc)" : "(Chua doc)";
                            //    emailReview += "\nFrom: " + email.getFrom() + '\n' + "Subject: "
                            //            + email.getData().getSubject();
                                ///////////
                                final String string = emailReview;



                                new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            sleep(1);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                listEmailItems.set(index, string);
                                            }
                                        });
                                    }
                                }.start();
                                try {
                                    receiveSocket.downloadEmail(emailCheck, pathsFolder.get(idFolder));
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }

                            // Platform.runLater(() -> printEmailListAtFolder(idFolder));;
                            if (email != null) {

                                String text = "";
                                senderLabel.setText("Sender: " + email.getFrom());
                                dateLabel.setText("Date: "+email.getDate());
                                for (String i : email.getTo()) {
                                    text += i + "  ";
                                }
                                toLabel.setText("To: " + text);
                                if (email.getCc() != null) {
                                    text="";
                                    for (String i : email.getCc()) {
                                        text += i + "  ";
                                    }
                                    ccLabel.setText("Cc: " + text);
                                    ccLabel.setVisible(true);
                                } else {
                                    ccLabel.setVisible(false);
                                }
                                subjectLabel.setText("Subject: " + email.getData().getSubject());
                                Label contentLabel = new Label("Contents: ");
                                contentLabel.setId("myContentLabel");
                                contentLabel.setLayoutX(15);
                                contentLabel.setLayoutY(180);
                                contentLabel.setStyle("-fx-font-style: italic");
                                MailDetail.getChildren().add(contentLabel);
                                if (email.getData().getContents().length() != 2) {
                                    TextArea textArea = new TextArea(email.getData().getContents());
                                    textArea.setWrapText(true);
                                    textArea.setId("myContentArea");
                                    textArea.setMaxWidth(470);
                                    textArea.setMinHeight(350);
                                    textArea.setLayoutX(15);
                                    textArea.setLayoutY(210);
                                    MailDetail.getChildren().add(textArea);
                                    contentAreaHeight = textArea.getMinHeight() + 30;
                                } else {
                                    contentLabel.setText("(No contents)");
                                    contentAreaHeight = 50;
                                }
                                    double newHeight = 180 + contentAreaHeight + 30;
                                    MailDetail.setPrefHeight(newHeight);
                                try {
                                    addImageView(email);
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                addAttachmentList(email);
                                handleSelecteAttachmentFile();
                            }
                        }
                    }
                });

    }

    void printEmailListAtFolder(int idFolder) {
        int temp = 0;
        AnchorPane anchorPaneListView = (AnchorPane) splitPane.getItems().get(0);
        HashMap<Integer, Email> ListmailInFolder = foldersFilter.get(idFolder);
        if (ListmailInFolder.size() != 0) {
            temp = 1;
        }
        ArrayList<String> arrayListEmail = new ArrayList<String>();
        Email email = new Email();
        String emailReview;
        for (int i = 1; i <= ListmailInFolder.size(); i++) {
            email = ListmailInFolder.get(i);
            ///////////// 
            emailReview=email.getDate().substring(5)+" ";
            emailReview += (email.getChecked() == 1) ? "" : "(Chua doc)";
            emailReview += "\nFrom: " + email.getFrom() + '\n' + "Subject: " + email.getData().getSubject();
            ////////////
         //   emailReview = email.getDate() + '\n';
         //   emailReview += (email.getChecked() == 1) ? "(Da doc)" : "(Chua doc)";
         //   emailReview += "\nFrom: " + email.getFrom() + '\n' + "Subject: " + email.getData().getSubject();
            ////////////
            arrayListEmail.add(emailReview);
        }
        listEmailItems = FXCollections.observableArrayList(arrayListEmail);
        ListView<String> listView = new ListView<String>(listEmailItems);
        listView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                ListCell<String> cells = new ListCell<String>() {       
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item);
                            setTextFill(Color.BLACK);
                            setCursor(Cursor.HAND);
                            if (!item.contains("(Chua doc)")) {
                                setBackground(new Background(new BackgroundFill(Color.web("#DEDEDE"), CornerRadii.EMPTY, null)));
                                // setTextFill(Color.web("#215557"));
                                if (isSelected()) {
                                    setTextFill(Color.BLACK);
                                }
                            } else {
                                setBackground(null);
                            }
                        }
                    }
                };
                return cells;
            }
        });
        listView.setId("myListView");
        if (temp != 0) {
            listView.setItems(listEmailItems);
        } else {
            listView.getItems().clear();
        }
        listView.setMinHeight(625.0);
        if (!anchorPaneListView.getChildren().isEmpty()) {
            anchorPaneListView.getChildren().clear();
        }
        anchorPaneListView.getChildren().add(listView);
        return;
    }

    ArrayList<HashMap<Integer, Email>> initData() {
        try {
            Client client = new Client().readConfigFile();
            ReceiveSocket receiveSocket = new ReceiveSocket(client.getGeneral().getMailServer(),
                    client.getGeneral().getPOP3());
            client.getGeneral().setUsername(LoginGUI.getUsername());
            client.getGeneral().setPassword(LoginGUI.getPassword());
            receiveSocket.login(client.getGeneral().getUsername(), client.getGeneral().getPassword());

            receiveSocket.connect(receiveSocket.getPOP3());
            this.receiveSocket = receiveSocket;
            String currentPath = new java.io.File(".").getCanonicalPath() + "/CSDL/"
                    + receiveSocket.getUser().getUserName() + "/"; // lấy đường dẫn tới folder hiện tại
            Files.createDirectories(Paths.get(currentPath)); // tạo folder có tên là username
            pathsFolder = client.setFolders(currentPath);
            client.createFolders(pathsFolder);
            // File directory = new File(currentPath);
            int countFile = 0;

            ArrayList<HashMap<Integer, Email>> ListArrayEmail = new ArrayList<>(5);
            // 0:spam 1:Project 2: important 3:work 4:inbox
            for (int i = 0; i <= 4; i++) {
                ListArrayEmail.add(new HashMap<>());
                String x = pathsFolder.get(i);
                File directory = new File(x);

                File listDir[] = directory.listFiles();
                countFile = listDir.length;
                // đọc mail đã đọc từ trước
                for (int j = 1; j <= countFile; j++) {
                    Email email = new Email();
                    try {
                        email = receiveSocket.readEmailFromFile(x, j);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    ListArrayEmail.get(i).put(j, email);
                    // receiveSocket.sendCmd("QUIT");
                }
            }

            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    // HashMap<Integer, Email> List = receiveSocket.getEmailList();

                    // lấy tiếp những mail chưa đọc
                    receiveSocket.getEmailList(ListArrayEmail, client, pathsFolder);
                }
            }, 0, client.getGeneral().getAutoload());

            return ListArrayEmail;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    void handleSendBtn() {
        sendBtn.setOnMouseClicked(e -> {
            Stage primaryStage = new Stage();
            Parent root;

            try {
                root = FXMLLoader.load(getClass().getResource("/fxml/sendScene.fxml"));
                Scene scene = new Scene(root);
                scene.setCursor(Cursor.DEFAULT);
                primaryStage.setTitle("Send Email");
                primaryStage.setScene(scene);
                primaryStage.initModality(Modality.APPLICATION_MODAL); // set always on top
                primaryStage.show();
            } catch (Exception err) {
                // TODO: handle exception
            }
        });
    }

    void showSuccess() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Success");
        // Header Text: null
        alert.setHeaderText(null);
        alert.setContentText("Email download successfully!");
        alert.showAndWait();
    }

    @FXML
    void initialize() {
        foldersFilter = initData();
        printEmailListAtFolder(0);

        handleSelectEmailInFolder();
        //handleSelecteAttachmentFile(0);
        handleSeclectedTab();
        handleSendBtn();
        // mailListView.getItems().clear();
    }
}
