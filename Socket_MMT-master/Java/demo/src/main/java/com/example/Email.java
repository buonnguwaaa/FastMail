package com.example;

import java.io.*;
import java.util.*;
import javax.swing.JFileChooser;

public class Email {
    private int index;
    private String date;
    private String from;
    String[] to;
    String[] cc;
    String[] bcc;
    private Data data;
    private int checked;
    Email()
    {
        index = 1;
        date = "";
        from=new String();
        data=new Data();
        checked = 0;
    }
    Email (int index, String date, String from, String[] to, Data data) {
        this.index = index;
        this.date = date;
        this.from = from;
        this.to = to;
        this.data = data;
    }
    Email(int index, String date, String from, String[] to, String[] cc, String[] bcc, Data data)
    {
        this.from=from;
        this.to=to;
        this.cc=cc;
        this.bcc=bcc;
        this.data=data;
    }
    int getIndex() {
        return this.index;
    }
    String getDate() {
        return this.date;
    }
    String getFrom()
    {
        return from;
    }
    Data getData()
    {
        return data;
    }
    String[] getTo() {
        return this.to;
    }
    String[] getCc() {
        return this.cc;
    }
    String[] getBcc() {
        return this.bcc;
    }
    int getChecked() {
        return this.checked;
    }
    void addTo(String s)
    {
        to=s.split(" ");
    }
    void addCc(String s)
    {
        cc=s.split(" ");
    }
    void addBcc(String s)
    {
        bcc=s.split(" ");
    }
    void setIndex(int idx) {
        this.index = idx;
    }
    void setDate(String date) {
        this.date = date;
    }
    void setCheck(int idx) {
        this.checked = idx;
    }
    void setFrom(String s)
    {
        from=s;
    }
    void setData(String sbj, String cnt, File attachment) {
        this.data.setSubject(sbj);
        this.data.addContents(cnt);
        this.data.addAttachment(attachment);
    }
    void setData(File file) {
        this.data.addAttachment(file);
    }
    void setHeader(String s) {
        if (s.contains("Date")) {
            this.setDate(s.substring(6));
        } else if (s.contains("boundary")) {
            this.data.setBoundary(s.substring(s.indexOf('=') + 2, s.length() - 1));
        } else if (s.contains("From")) {
            this.setFrom(s.substring(6));
        } else if (s.contains("To")) {
            this.addTo(s.substring(4));
        } else if (s.contains("Cc")) {
            this.addCc(s.substring(4));
        } else {
            return;
        }
    }
    void setSubject(String s) {
        this.data.setSubject(s);
    }
    void setContent(String s) {
        this.data.addContents(s);
    }
    void setBody(String s) {
        this.setContent(s);
    }
    String sendRCPT()
    {
        String a=new String();
        for(String x : to)
        {
            if(x.equals("")) continue;
            a+="RCPT TO: <" +x+ ">\n";
        }
        for(String x: cc)
        {
            if(x.equals("")) continue;
            a+="RCPT TO: <" +x+ ">\n";
        }
        for(String x: bcc)
        {
            if(x.equals("")) continue;
            a+="RCPT TO: <" +x+ ">\n";
        }
        System.out.println(a);
        return a;
    }
    String sendTo()
    {
        String a=new String("To:");
        for(int i=0;i<to.length;i++ )
        {
            a+=" " +to[i];
            if(i!=to.length-1) a+=",";
        }
        a+="\n";
        return a;
    }
    String sendCc()
    {
        String a=new String("Cc:");
        if(cc[0].equals("")) return "";
        for(int i=0;i<cc.length;i++ )
        {
            a+=" " +cc[i];
            if(i!=cc.length-1) a+=",";
        }
        a+="\n";
        return a;
    }

    int downloadAttachmentFile(int index) {
        File f= this.data.attachment.get(index);
      //  System.out.println(f);
        try {
            FileInputStream fileInputStream=new FileInputStream(f);
            byte fileContentsByte[] =new byte[(int)f.length()];
            fileInputStream.read(fileContentsByte);
            int size=fileContentsByte.length;
            
            // chọn folder để lưu
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
            jFileChooser.showSaveDialog(null);
            //System.out.println(jFileChooser.getSelectedFile());
            System.out.println(jFileChooser.getSelectedFile());
            if (jFileChooser.getSelectedFile() != null) {
                FileOutputStream fileOutputStream=new FileOutputStream(jFileChooser.getSelectedFile()+"\\"+f.getName());
                //ghi dữ liệu vào file
                int tmp=0;
                int len=6500;
                while(size>0)
                {
                    Thread.sleep(4);
                    if(size<len)
                    {
                        fileOutputStream.write(fileContentsByte,tmp,size);
                    }
                    else
                    {
                        fileOutputStream.write(fileContentsByte,tmp,len);
                    }
                    
                    tmp+=len;
                    size-=len;
                }
                return 1;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return 0;
    }



    void displayAttachmentFile(ArrayList<File> listFile) throws Exception {
        System.out.println("Email has " + listFile.size() + " attachment file: ");
        for (int i = 0; i < listFile.size(); i++) {
            System.out.println(i + 1 + ". " + listFile.get(i).getName());
        }
        System.out.print("Input your index of file you want to view (0 to break): ");
        Scanner userInput = new Scanner(System.in);
        int idx = userInput.nextInt();
        if (idx != 0) {
            String fileName = this.data.getAtm(idx - 1).getPath();
            String [] commands = {
                "cmd.exe" , "/c", "start" , "\"DummyTitle\"", "\"" + fileName + "\""
            };
            Process p = Runtime.getRuntime().exec(commands);
        } else return;
    }
    void printEmailReview() {
        System.out.print(this.index + ". ");
        if (this.checked == 1) {
            System.out.println("Da doc");
        } else System.out.println("Chua doc");
        System.out.println("From: <" + this.getFrom() + ">");
        System.out.println("Subject: <" + this.data.getSubject() + ">");
    }
    void printEmailDetail() throws Exception {
        System.out.println("From: " + this.from);
        System.out.print("To: ");
        for (String s : this.to) {
            System.out.print(s + " ");
        }
        System.out.print('\n');
        if (this.cc != null) {
            System.out.print("Cc: ");
            for (String s : this.cc) {
                System.out.print(s + " ");
            }
            System.out.print('\n');
        } 
        System.out.println("Subject: " + this.data.getSubject());
        System.out.println(this.data.getContents());
        if (this.data.attachment.size() != 0) {
            displayAttachmentFile(this.data.attachment);
        }  

        if (this.data.attachment.size() != 0) {
            System.out.println("File dinh kem:");
            int cnt=0;
            for (File f : this.data.attachment) {
                cnt++;
                System.out.println(cnt + "."+f.getName());
            }
            while(true)
            {
                System.out.printf("Chon file muon luu(0 de huy):");
                int lc=new Scanner(System.in).nextInt();
                if (lc==0) break;
                else if(lc>=1&&lc<=this.data.attachment.size())
                {
                    // cho dữ liệu vào mảng byte
                    File f= this.data.attachment.get(lc-1);
                    System.out.println(f);
                    FileInputStream fileInputStream=new FileInputStream(f);
                    byte fileContentsByte[] =new byte[(int)f.length()];
                    fileInputStream.read(fileContentsByte);
                    int size=fileContentsByte.length;
                    
                    // chọn folder để lưu
                    JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
                    jFileChooser.showSaveDialog(null);
                    //System.out.println(jFileChooser.getSelectedFile());
                    FileOutputStream fileOutputStream=new FileOutputStream(jFileChooser.getSelectedFile()+"\\"+f.getName());

                    //ghi dữ liệu vào file
                    int tmp=0;
                    int len=6500;
                    while(size>0)
                    {
                        Thread.sleep(1);
                        if(size<len)
                        {
                            fileOutputStream.write(fileContentsByte,tmp,size);
                        }
                        else
                        {
                            fileOutputStream.write(fileContentsByte,tmp,len);
                        }
                        
                        tmp+=len;
                        size-=len;
                    }
                    System.out.println("Luu thanh cong!");
                }
                else {
                    System.out.println("Khong ton tai !");
                }
            }
        }
    }
}