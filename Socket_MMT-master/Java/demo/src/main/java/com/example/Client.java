package com.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.nio.file.*;


import com.google.gson.Gson;

public class Client {
    public class General {
        private String Username;
        private String Password;
        private String MailServer;
        private int SMTP;
        private int POP3;
        private int Autoload;

        General(String usn, String psw, String host, int smtp, int pop3, int load) {
            this.Username = usn;
            this.Password = psw;
            this.MailServer = host;
            this.SMTP = smtp;
            this.POP3 = pop3;
            this.Autoload = load;
        }
        void setUsername(String s)
        {
            this.Username=s;
        }
        void setPassword(String s)
        {
            this.Password=s;
        }
        String getUsername() {
            return this.Username;
        }

        String getPassword() {
            return this.Password;
        }

        String getMailServer() {
            return this.MailServer;
        }

        int getSMTP() {
            return this.SMTP;
        }

        int getPOP3() {
            return this.POP3;
        }

        int getAutoload() {
            return this.Autoload;
        }

    }
    
    public class Filter {
        private ArrayList<String> From;
        private String fromToFolder;
        private ArrayList<String> Subject;
        private String subjectToFolder;
        private ArrayList<String> Content;
        private String contentToFolder;
        private ArrayList<String> Spam;
        private String spamToFolder;
        Filter(ArrayList<String> from, String fromFolfer, ArrayList<String> subject, String subjectFolder, ArrayList<String> content, String contentFolder, ArrayList<String> spam, String spamFolder) {
            this.From = from;
            this.fromToFolder = fromFolfer;
            this.Subject = subject;
            this.subjectToFolder = subjectFolder;
            this.Content = content;
            this.contentToFolder = contentFolder;
            this.Spam = spam;
            this.spamToFolder = spamFolder;
        }

        ArrayList<String> getFrom() {
            return this.From;
        }

        String getFromFolder() {
            return this.fromToFolder;
        }

        ArrayList<String> getSubject() {
            return this.Subject;
        }

        String getSubjectFolder() {
            return this.subjectToFolder;
        }

        ArrayList<String> getContent() {
            return this.Content;
        }

        String getContentFolder() {
            return this.contentToFolder;
        }

        ArrayList<String> getSpam() {
            return this.Spam;
        }

        String getSpamFolder() {
            return this.spamToFolder;
        }
    }

    private General general;
    private Filter filter;

    void setGeneral(General general) {
        this.general = general;
    }

    void setFilter(Filter filter) {
        this.filter = filter;
    }

    General getGeneral() {
        return this.general;
    }

    Filter getFilter() {
        return this.filter;
    }

    void writeConfigFile() throws Exception {
        Gson gson = new Gson();
        String currentPath = new java.io.File(".").getCanonicalPath();
        File configFile = new File(currentPath, "config.json");
        FileWriter fileWriter = new FileWriter(configFile);
        fileWriter.write(gson.toJson(this));
        fileWriter.close();
    }

    Client readConfigFile() throws Exception {
        Gson gson = new Gson();
        String currentPath = new java.io.File(".").getCanonicalPath();
        File configFile = new File(currentPath, "config.json");
        Scanner fileReader = new Scanner(configFile);
        String json = fileReader.nextLine();
        Client client = gson.fromJson(json, Client.class);
        return client;
    }

    public ArrayList<String> setFolders(String path){
        ArrayList<String> paths=new ArrayList<String>();
        String newPath;
        newPath=path+"Inbox";
        paths.add(newPath);
        newPath=path+this.getFilter().fromToFolder;
        paths.add(newPath);
        newPath=path+this.getFilter().subjectToFolder;
        paths.add(newPath);
        newPath=path+this.getFilter().contentToFolder;
        paths.add(newPath);
        newPath=path+this.getFilter().spamToFolder;
        paths.add(newPath);
        return paths;
    }
    void createFolders(ArrayList <String> paths) throws IOException
    {
        for(String newPath : paths)
        {
            Files.createDirectories(Paths.get(newPath)); 
        }
    }

    public int filter(Email email) {
        String from =email.getFrom();
        String contents=email.getData().getContents();
        String subject=email.getData().getSubject();
        if(checkSpam(contents,subject))
        {
            return 4;
        }
        if(checkFrom(from))
        {
            return 1;
        }
        if(checkSubject(subject))
        {
            return 2;
        }
        if(checkContents(contents))
        {
            return 3;
        }
        return 0;
    }

    private boolean checkContents(String contents) {
        for(String x : this.filter.Content)
        {
            if(contents.contains(x))
                return true;
        }
        return false;
    }

    private boolean checkSubject(String subject) {
        for(String x : this.filter.Subject)
        {
            if(subject.contains(x))
                return true;
        }
        return false;
    }

    private boolean checkFrom(String from) {
        for(String x : this.filter.From)
        {
            if(from.contains(x))
                return true;
        }
        return false;
    }

    private boolean checkSpam(String contents, String subject) {
        for(String x : this.filter.Spam)
        {
            if(subject.contains(x)||contents.contains(x))
                return true;
        }
        return false;
    }
}
