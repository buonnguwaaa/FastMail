package com.example;

import org.apache.commons.lang3.RandomStringUtils;

import com.example.Client.General;

import java.util.*;
import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class SendEmail {

    Socket socket=null;
    Email email;
    OutputStreamWriter outputStreamWriter=null;
    DataOutputStream dataOutputStream=null;
    BufferedWriter bufferedWriter=null;
    SendEmail(General general) throws IOException
    {
        email=new Email();
        socket=new Socket(general.getMailServer(), general.getSMTP());
        general.setUsername(LoginGUI.getUsername());
        email.setFrom(general.getUsername());
        outputStreamWriter=new OutputStreamWriter(socket.getOutputStream());
        bufferedWriter=new BufferedWriter(outputStreamWriter);
        dataOutputStream=new DataOutputStream(socket.getOutputStream());

    }
    Email getEmail()
    {
        return email;
    }
    public void setEmail(Email email2) {
        email=email2;
    }
    void send() throws IOException, InterruptedException
    {
        dataOutputStream.writeBytes("MAIL FROM: <"+this.email.getFrom()+">\n");
        dataOutputStream.writeBytes("HELO\n");
        dataOutputStream.writeBytes(email.sendRCPT());
        dataOutputStream.writeBytes("DATA\n");
        //tạo ngày giờ
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm:ss");
        String dateToSend = currentDateTime.format(dtf);
        email.setDate(dateToSend);
        dataOutputStream.writeBytes("Date: " + dateToSend + '\n');

        //tao data
        Data data=email.getData();
        data.setBoundary(RandomStringUtils.randomAlphanumeric(25)); //tạo boundary nếu cần gửi file
        String underData="";
        underData+="From: "+email.getFrom()+"\n";
        underData+=email.sendTo();
        underData+=email.sendCc();

        underData+="Subject: "+ data.getSubject()+"\n";
      
        data.contents="Content-Type: text/plain; charset=UTF-8; format=flowed\n"+"Content-Transfer-Encoding: 7bit\n\n"+ data.contents;

        if(data.getArrFile().size()==0)
        {
            dataOutputStream.writeBytes(underData);
            dataOutputStream.writeBytes(data.getContents()+"\n.\nQUIT");
            return;
        }
        //
        dataOutputStream.writeBytes("Content-Type: multipart/mixed; boundary=\"------------"+data.getBoundary()+  "\"\n");
        dataOutputStream.writeBytes(underData+"\n");
        dataOutputStream.writeBytes("--------------"+data.getBoundary()+'\n');
        dataOutputStream.writeBytes(data.getContents()+ '\n');
        //

        for(File fileToSend : data.getArrFile())
        {   
            dataOutputStream.writeBytes("--------------"+data.getBoundary()+'\n');  
            String fileName=fileToSend.getName();

            String contentType=new String(fileType(fileName));
            bufferedWriter.write("Content-Type: " +contentType+ "; name=\"" +fileName +"\"");
            bufferedWriter.newLine();
            bufferedWriter.write("Content-Disposition: attachment"+"; filename=\"" +fileName +"\"");;
            bufferedWriter.newLine();
            bufferedWriter.write("Content-Transfer-Encoding: base64" + '\n');
            bufferedWriter.newLine();
            bufferedWriter.flush();

            FileInputStream fileInputStream=new FileInputStream(fileToSend.getAbsolutePath());
            byte fileContentsByte[] =new byte[(int)fileToSend.length()];
            fileInputStream.read(fileContentsByte);
            byte encodeByte[] = Base64.getEncoder().encode(fileContentsByte);
            int cnt=encodeByte.length;
            int tmp=0;
            int len=6500;
            while(cnt>0)
            {
                Thread.sleep(5);
                if(cnt<len)
                {
                    dataOutputStream.write(encodeByte,tmp,cnt);
                }
                else
                {
                    dataOutputStream.write(encodeByte,tmp,len);
                }
                
                tmp+=len;
                cnt-=len;
                dataOutputStream.writeBytes("\n"); 
            }
            dataOutputStream.writeBytes("\n"); 
        }
        dataOutputStream.writeBytes("--------------"+data.getBoundary()+"--\n");
        dataOutputStream.writeBytes(".\nQUIT");

        if(socket!=null)
        {
            socket.close();
        }
        if(outputStreamWriter!=null)
        {
            outputStreamWriter.close();
        }
        if(bufferedWriter!=null)
        {
            bufferedWriter.close();
        }
        if(dataOutputStream!=null)
        {
            dataOutputStream.close();
        }
    }


    static String fileType(String s)
    {
        int i=s.lastIndexOf(".");
        String contentType=s.substring(i+1);
        if(contentType.equalsIgnoreCase("png"))
        {
            contentType="image/png";
        }
        else if(contentType.equalsIgnoreCase("jpg")||contentType.equalsIgnoreCase("jpeg"))
        {
            contentType="image/jpeg";
        }
        else if(contentType.equalsIgnoreCase("pdf"))
        {
            contentType="application/pdf";
        }
        else if(contentType.equalsIgnoreCase("html"))
        {
            contentType="text/html; charset=UTF-8";
        }
        else if(contentType.equalsIgnoreCase("cpp")|contentType.equalsIgnoreCase("txt"))
        {
            contentType="text/plain; charset=UTF-8";
        }
        else if(contentType.equalsIgnoreCase("html"))
        {
            contentType="text/html; charset=UTF-8";
        }
        else if(contentType.equalsIgnoreCase("docx"))
        {
            contentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }
        else if(contentType.equalsIgnoreCase("zip"))
        {
            contentType="application/x-zip-compressed";
        }
        else if(contentType.equalsIgnoreCase("rar"))
        {
            contentType="application/x-compressed";
        }
        else
        {
            contentType="video/mp4";
        }
        return contentType;
    }
    
}