package com.example;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.google.gson.Gson;

public class ReceiveSocket {
    public class User {
        private String username;
        private String password;

        User(String usn, String psw) {
            this.username = usn;
            this.password = psw;
        }

        void setUsername(String usn) {
            this.username = usn;
        }

        void setPassword(String psw) {
            this.password = psw;
        }
        String getUserName()
        {
            return username;
        }
    }

    private int POP3;
    private Socket socket;
    public  String localhost;
    private User user;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String clientSent;
    private String serverResp;
    ReceiveSocket(String host, int pop3) {
        this.localhost = host;
        this.POP3 = pop3;
    }

    void setUser(User usr) {
        this.user = usr;
    }
    User getUser()
    {
        return user;
    }
    void sendCmd(String cmd) {
        try {
            writer.write(cmd + "\r\n");
            writer.flush();
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Error send command");
        }
    }

    int getPOP3() {
        return this.POP3;
    }
    void setPOP3(int pop3) {
        this.POP3 = pop3;
    }
    void setLocalhost(String host) {
        this.localhost = host;
    }
    BufferedReader getReader() {
        return this.reader;
    }


    int getEmail(ArrayList<HashMap<Integer, Email>> List, Email curEmail,int idx,Client client, ArrayList<String> pathsFolder ) {

        int idxFilter=0; // loc xem nen de mail o folder nao
        try {
            sendCmd("RETR " + idx); 
            reader.readLine();
            while(!(serverResp = reader.readLine()).contains("Subject")) {
                curEmail.setHeader(serverResp);
            }
            curEmail.setSubject(serverResp.substring(9));
            

            if (curEmail.getData().getBoundary().length() > 25) {
                String boundary = curEmail.getData().getBoundary();
                reader.readLine();
                reader.readLine();
                reader.readLine();
                reader.readLine();
                while(!(serverResp = reader.readLine()).equals("--" + boundary)) {
                    curEmail.setContent(serverResp + '\n');  
                }

                idxFilter=client.filter(curEmail); 
                
                curEmail.setIndex(List.get(idxFilter).size()+1);
                String filePath= pathsFolder.get(idxFilter)+"/"+curEmail.getIndex()+"/";;
                Files.createDirectories(Paths.get(filePath));
                while(true)
                {
                //  String filePath = "D:\\mail";
                    serverResp = reader.readLine();
                    String fileName = serverResp.substring(serverResp.indexOf("\"") + 1, serverResp.length() - 1);
                    System.out.println(fileName);
                    reader.readLine();
                    reader.readLine();
                    reader.readLine();
                    File file = new File(filePath, fileName);
                    file.createNewFile();
                    OutputStream fileWriter = new FileOutputStream(file);
                    String encoded = "";
                    while(!(serverResp = reader.readLine()).isEmpty()) {
                        encoded += serverResp;
                    }
                    byte[] decoded = Base64.getDecoder().decode(encoded);
                    fileWriter.write(decoded);
                    fileWriter.close();
                    curEmail.setData(file);

                    if((serverResp = reader.readLine()).equals("--"+ boundary+"--"))
                    {
                        reader.readLine();
                        break;
                    } 
                }
            } 
            else {
                reader.readLine();
                reader.readLine();
                while(!(serverResp = reader.readLine()).equals(".")) {
                    curEmail.setContent(serverResp + "\n");
                }
                idxFilter=client.filter(curEmail);
                curEmail.setIndex(List.get(idxFilter).size()+1);
                String filePath= pathsFolder.get(idxFilter)+"/"+curEmail.getIndex()+"/";;
                Files.createDirectories(Paths.get(filePath));
            }
            
        } catch (IOException e) {
            System.out.println("Error get email");
        }
        return idxFilter;
    }

    void printEmailList(HashMap<Integer, Email> List) {
        for (int i = 0; i < List.size(); i++) {
            Email email = List.get(i + 1);
            email.printEmailReview();
        }
    }
    void readEmail(int idx, HashMap<Integer, Email> List,String filePath) throws Exception {
        Email email = List.get(idx);
        email.printEmailDetail();
        email.setCheck(1);
        downloadEmail(email,filePath);
    } 
    Email readEmailFromFile(String filePath,int index) throws Exception {
        String path =filePath+"/"+index+"/";
        File emailFile = new File(path + "mail" + index + ".json");
        Scanner fileReader = new Scanner(emailFile);
        String json = fileReader.nextLine();
        Gson gson = new Gson();
        Email emailRead = gson.fromJson(json, Email.class);
        return emailRead;
    }
    void getEmailList(ArrayList<HashMap<Integer, Email>> List,Client client,ArrayList<String> pathsFolder) {
        try {

            sendCmd("USER "+this.user.username);
            reader.readLine();
            sendCmd("PASS "+this.user.password);
            reader.readLine();
            clientSent = "LIST";
            sendCmd(clientSent);
            reader.readLine();
            int n = 0;
            while (!(serverResp = reader.readLine()).equals(".")) {
                n++;
            }
            int size=0;
            for(HashMap<Integer, Email> x : List)
            {
                size+=x.size();
            }
            for (int i = size+1; i <= n; i++) {
                Email email = new Email();
                int idx=getEmail(List,email,i,client,pathsFolder);
              //  int idx =client.filter(email);
                downloadEmail(email,pathsFolder.get(idx));
                List.get(idx).put(List.get(idx).size()+1, email);
            }
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Error getList");
        }
       // return List;
    }
    

    void login(String usn, String psw) {
        this.user = new User(usn, psw);
    }
    void loginPOP3()
    {
        try {
            while (true) {
                clientSent = "USER " + user.username;
                sendCmd(clientSent);
                serverResp = reader.readLine();
                if (serverResp.contains("OK"))
                    break;
            }
            while (true) {
                clientSent = "PASS " + user.password;
                sendCmd(clientSent);
                serverResp = reader.readLine();
                if (serverResp.contains("OK"))
                    break;
            }
        } catch (Exception e) {
           // TODO: handle exception
            System.out.println("error");
        }
    }
    void downloadEmail(Email email,String path) throws IOException { //dua file vao CSDL
      //  email.setIndex(List.get(idx).size());
      //  String filePath = "D:\\mail";
        String filePath=path +"/"+email.getIndex()+"/";
        String fileName = "mail" + email.getIndex() + ".json";
        File file = new File(filePath, fileName);
        try {
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            Gson json = new Gson();
            String convertToJson = json.toJson(email);
            fileWriter.write(convertToJson);
            fileWriter.close();
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Error create new file");
        }
    }
    void connect(int port) {
        try {
            socket = new Socket(this.localhost, port);
            if (socket.isConnected()) {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                serverResp = reader.readLine();
            } else {
                System.out.println("Can't connect to POP3 server");
            }
            this.loginPOP3();
          //  this.login();
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Error connect");
        }
    }

    void close() throws Exception {
        writer.close();
        reader.close();
        socket.close();
    }

}
