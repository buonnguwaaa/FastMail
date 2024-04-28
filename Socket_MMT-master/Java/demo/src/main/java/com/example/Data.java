package com.example;
import java.io.*;
import java.util.*;

public class Data {
    String subject;
    String contents;
    String boundary;
    ArrayList<File> attachment;
    Data()
    {
        subject=new String();
        contents=new String();
        boundary=new String();
        attachment=new ArrayList<File>();
    }
    Data(String sub,String cont,ArrayList<File> f)
    {
        this.subject=sub;
        this.contents =cont;
        this.attachment=f;
    }

    void setSubject(String s)
    {
        subject=s;
    }
    String getSubject() {
        return subject;
    }
    void addContents(String msgToSend) 
    {
        contents+=msgToSend;
    }
    String getContents() 
    {
        return contents;
    }
    void addAttachment(File atm) {
        this.attachment.add(atm);
    }
    void setBoundary(String randomAlphanumeric) 
    {
        boundary=randomAlphanumeric;
    }
    String getBoundary() 
    {
       return boundary;
    }
    File getAtm(int idx) {
        return this.attachment.get(idx);
    }
    ArrayList<File> getAtm() {
        return this.attachment;
    }
    ArrayList<File> getArrFile()
    {
        return this.attachment;
    }
}
