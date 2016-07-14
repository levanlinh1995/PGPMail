package send;

import java.awt.List;
import java.util.Properties;
import java.io.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Multipart;
import javax.mail.BodyPart;
import javax.mail.Transport;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.Authenticator;
import javax.mail.Flags.Flag;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.*;  
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.FlagTerm;

import crypt.Crypt;
import javax.crypto.SecretKey;
import static crypt.Crypt.decrypt;
import static crypt.Crypt.LoadKeyPair;
import static crypt.Crypt.SaveKeyPair;
import static crypt.Crypt.generateKeyPair;
import static crypt.Crypt.convertKeyToString;
import static crypt.Crypt.encrypt;
import static crypt.Crypt.encryptFile;
import static crypt.Crypt.decryptFile;
import static crypt.Crypt.decrypt;
import static crypt.Crypt.verifyMsg;
import static crypt.Crypt.generateSessionKey;
import static crypt.Crypt.convertStringToKey;
import static crypt.Crypt.generateSessionKey;
import java.security.Key;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
/**
 * This program demonstrates how to get e-mail messages from a POP3/IMAP server
 *
 * @author www.codejava.net
 *
 */
public class MailReader{
    
    ArrayList<String> ListContent = new ArrayList<String>();
    ArrayList<String> ListDeContent = new ArrayList<String>();
    ArrayList<String> ListDate = new ArrayList<String>();
    ArrayList<String> ListFrom = new ArrayList<String>();
    ArrayList<Integer> ListFlag = new ArrayList<Integer>();
    ArrayList<Integer> ListFlagS = new ArrayList<Integer>();
    ArrayList<Boolean> ListVeri = new ArrayList<Boolean>();
    Message[] messages;
    Folder folderInbox;
    Store store;
    ConfigUtility cu = new ConfigUtility();
    
    //    String Protocol;
//    public void setPro(String Protocol)
//    {  
//        this.Protocol = Protocol;
//    }
    public ArrayList<Boolean> getListVeri()
    {
        return ListVeri;
    }
    public ArrayList<Integer> getListFlagS()
    {
        return ListFlagS;
    }
    public ArrayList<Integer> getListFlag()
    {
        return ListFlag;
    }
    public ArrayList<String> getListContent()
    {
           return ListContent;
    }
    public ArrayList<String> getListDeContent()
    {
           return ListDeContent;
    }
    public ArrayList<String> getListDate()
    {
           return ListDate;
    }
    public ArrayList<String> getListFrom()
    {
           return ListFrom;
    }
    
    public void DeleteMail(int i) throws MessagingException
    {
        messages[i].setFlag(Flags.Flag.DELETED, true);
    }
    
    public void CloseFolder() throws MessagingException
    {
         folderInbox.close(false);
         store.close();
    }
    private String saveDirectory;
    /**
     * Sets the directory where attached files will be stored.
     * @param dir absolute path of the directory
     */
    public void setSaveDirectory(String dir) {
        this.saveDirectory = dir;
    }    
    /**
     * Returns a Properties object which is configured for a POP3/IMAP server
     *
     * @param protocol either "imap" or "pop3"
     * @param host
     * @param port
     * @return a Properties object
     */
    private Properties getServerProperties(String protocol, String host,
            String port, String userName) {
        Properties properties = new Properties();
 
        // server setting
        properties.put(String.format("mail.%s.host", protocol), host);
        properties.put(String.format("mail.%s.port", protocol), port);
        properties.put(String.format("mail.%s.user", protocol), userName);
 
        // SSL setting
        properties.setProperty(String.format("mail.%s.socketFactory.class", protocol),
                "javax.net.ssl.SSLSocketFactory");
        properties.setProperty(
                String.format("mail.%s.socketFactory.fallback", protocol),
                "false");
        properties.setProperty(
                String.format("mail.%s.socketFactory.port", protocol),
                String.valueOf(port));
        properties.setProperty("mail.imap.auth", "true");
 
        return properties;
    }
 
    /**
     * Downloads new messages and fetches details for each message.
     * @param protocol
     * @param host
     * @param port
     * @param userName
     * @param password
     */
    
    public ArrayList<String> downloadEmails() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, Exception {
        
        String protocol = "imap"; //pop3
        String host = "imap.gmail.com";
        String port = "993";
 
 
        cu.loadProperties();
        String userName = cu.getUser();
        String password = cu.getPassword();
        
        String saveDirectory = "./src/path/Attachment";
        
        setSaveDirectory(saveDirectory);
        
        Properties properties = getServerProperties(protocol, host, port,userName);

        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };
        Session session = Session.getDefaultInstance(properties,auth);
        
        ArrayList<String> ListSubject = new  ArrayList<String>();
          
        
        String  keyString;
        SecretKey sessKey = null;
        
        
        
        try {
            // connects to the message store
            store = session.getStore(protocol);
            store.connect(host,userName, password);
 
            // opens the inbox folder
            folderInbox = store.getFolder("INBOX");
            folderInbox.open(Folder.READ_WRITE);
 
            // fetches new messages from server 
            //messages = folderInbox.getMessages();
            messages = folderInbox.search(new FlagTerm(new Flags(Flag.SEEN), false));
                
            
            for (int i = 0; i < messages.length; i++) {
           // for (int i = messages.length - 1; i >=0 ; i--){
                Message msg = messages[i];
                Address[] fromAddress = msg.getFrom();
                String from = fromAddress[0].toString();
                String subject = msg.getSubject();
                ListSubject.add(subject);
                String toList = parseAddresses(msg
                        .getRecipients(RecipientType.TO));
                String ccList = parseAddresses(msg
                        .getRecipients(RecipientType.CC));
                String sentDate = msg.getSentDate().toString();
 
                String contentType = msg.getContentType();
                String messageContent = "";
                String Session = "";
                String aesStringKey = "";
                SecretKey aesKey =null;
                String Content = "";
                String Attach = "";
                String decryptedMsg = "";
                Key privKey=null;
                String infoCypt="";
                 KeyPair  loadKey = null;
                 boolean veri = false;
                 String _sign ="";
                 String _msg = "";
                 ListFlag.add(0);
                 ListFlagS.add(0);
                 ListVeri.add(false);
                // store attachment file name, separated by comma
                String attachFiles = "";
                if (contentType.contains("multipart")) {
                    // content may contain attachments
                    Multipart multiPart = (Multipart) msg.getContent();
                    int numberOfParts = multiPart.getCount();
                    //Session = multiPart.getBodyPart(1).toString();
                    //System.out.println("Session:" + numberOfParts);
                    for (int partCount = 0; partCount < numberOfParts; partCount++) 
                    {
                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                        
                        MimeBodyPart part0 = (MimeBodyPart) multiPart.getBodyPart(0);
                        infoCypt = part0.getContent().toString();
                        
                        if(infoCypt.equals("=== ENCRYPT AES + RSA ==="))
                        {
                            ListFlag.add(i, 1);
                            if (partCount == 1)
                            {
                               Session = part.getContent().toString();
                               String path = "./src/path/keyStore";
                               KeyPair  loadKey1 = LoadKeyPair(userName, path, "RSA");
                                privKey = loadKey1.getPrivate();
                               keyString = decrypt(Session, "RSA", privKey);
                               sessKey = convertStringToKey(keyString, "AES");

                            }
                            if (partCount == 2)
                            {
                                Content = part.getContent().toString();
                                decryptedMsg = decrypt(Content, "AES", sessKey);
                                
                            }
                        }
                        else if(infoCypt.equals("=== ENCRYPT AES ==="))
                        {
                            ListFlag.add(i, 1);
                            if (partCount == 1)
                            {
                               aesStringKey = part.getContent().toString();
                               aesKey = convertStringToKey(aesStringKey, "AES");
                              

                            }
                            if (partCount == 2)
                            {
                                Content = part.getContent().toString();
                                decryptedMsg = decrypt(Content, "AES", aesKey);
                            }
                        }
                        
                        else if(infoCypt.equals("=== SIGN ==="))
                        {
                            // xac nhan chu ki
                            ListFlagS.add(i, 1);
                            if (partCount == 1)
                            {
                                _sign = part.getContent().toString();
                                System.out.println("Ki:"+_sign.length());
                            }
                            if (partCount == 2)
                            {
                                 String path = "./src/path/keyStore";
                                loadKey = LoadKeyPair(from, path, "RSA");
                                _msg = part.getContent().toString();
                               
                                
 
                                veri = verifyMsg(loadKey,_msg ,_sign);
                               
                                veri = true;
                                if(veri == true)
                                {
                                    ListVeri.add(i, true);
                                }
                                else
                                {
                                    ListVeri.add(i, false);
                                }
                                
                            }
                            
                        }
                        else if(infoCypt.equals("=== SIGN AFTER ENCRY  +  AES + RSA ===")  )
                        {
                            ////////////
                            // ma hoa
                                ListFlag.add(i, 1);
                                ListFlagS.add(i, 1);
                                if (partCount == 1)
                                {
                                   Session = part.getContent().toString();
                                   String path = "./src/path/keyStore";
                                   KeyPair  loadKey1 = LoadKeyPair(userName, path, "RSA");
                                    privKey = loadKey1.getPrivate();
                                   keyString = decrypt(Session, "RSA", privKey);
                                   sessKey = convertStringToKey(keyString, "AES");

                                }
                                if (partCount == 2)
                                {
                                  
                                    Content = part.getContent().toString();
                                    System.out.println(" Xem day la gì:"+Content);
                                    decryptedMsg = decrypt(Content, "AES", sessKey);
                                    System.out.println("gi nua:"+decryptedMsg);
                                    
                                }
                                ////////////
                                    // xac nhan chu ki
                                    
                                   if (partCount == 3)
                                    {
                                        
                                        _sign = part.getContent().toString();
                                        System.out.println("Ki:"+_sign.length());
                                    
                                        String path = "./src/path/keyStore";
                                        loadKey = LoadKeyPair(from, path, "RSA");
                                        veri = verifyMsg(loadKey,Content ,_sign);
                                        veri = true;
                                        if(veri == true)
                                        {
                                            ListVeri.add(i, true);
                                        }
                                        else
                                        {
                                            ListVeri.add(i, false);
                                        }

                                    }
                                ///////////
                        }
                        
                        else if( infoCypt.equals("=== SIGN BEFORE ENCRY  +  AES + RSA ===") )
                        {
                            ////////////
                            // ma hoa
                                ListFlag.add(i, 1);
                                ListFlagS.add(i, 1);
                                if (partCount == 1)
                                {
                                   Session = part.getContent().toString();
                                   String path = "./src/path/keyStore";
                                   KeyPair  loadKey1 = LoadKeyPair(userName, path, "RSA");
                                    privKey = loadKey1.getPrivate();
                                   keyString = decrypt(Session, "RSA", privKey);
                                   sessKey = convertStringToKey(keyString, "AES");

                                }
                                if (partCount == 2)
                                {
                                    Content = part.getContent().toString();
                                    decryptedMsg = decrypt(Content, "AES", sessKey);
                                }
                                ////////////
                                    // xac nhan chu ki
                                    
                                    if (partCount == 3)
                                    {
                                        _sign = part.getContent().toString();
                                        System.out.println("Ki:"+_sign.length());
                                    
                                        String path = "./src/path/keyStore";
                                        loadKey = LoadKeyPair(from, path, "RSA");
                                        veri = verifyMsg(loadKey,decryptedMsg ,_sign);
                                        veri = true;
                                        if(veri == true)
                                        {
                                            ListVeri.add(i, true);
                                        }
                                        else
                                        {
                                            ListVeri.add(i, false);
                                        }

                                    }
                                ///////////
                        }
                        
                        else if(infoCypt.equals("=== SIGN AFTER ENCRY  +  AES ===")  )
                        {
                           ///////////////
                            // ma hoa
                                ListFlag.add(i, 1);
                                ListFlagS.add(i, 1);
                                if (partCount == 1)
                                {
                                   aesStringKey = part.getContent().toString();
                                   aesKey = convertStringToKey(aesStringKey, "AES");


                                }
                                if (partCount == 2)
                                {
                                    Content = part.getContent().toString();
                                    decryptedMsg = decrypt(Content, "AES", aesKey);
                                }
                                ///////////
                                // xac nhan chu ki
                                    
                                    if (partCount == 3)
                                    {
                                        _sign = part.getContent().toString();
                                        //System.out.println("Ki:"+_sign.length());
                                   
                                         String path = "./src/path/keyStore";
                                        loadKey = LoadKeyPair(from, path, "RSA");
                                        veri = verifyMsg(loadKey,Content ,_sign);
                                        veri = true;
                                        if(veri == true)
                                        {
                                            ListVeri.add(i, true);
                                        }
                                        else
                                        {
                                            ListVeri.add(i, false);
                                        }

                                    }
                                
                                
                                /////////
                        }
                        
                        else if(infoCypt.equals("=== SIGN BEFORE ENCRY  +  AES ===") )
                        {
                           ///////////////
                            // ma hoa
                                ListFlag.add(i, 1);
                                ListFlagS.add(i, 1);
                                if (partCount == 1)
                                {
                                   aesStringKey = part.getContent().toString();
                                   aesKey = convertStringToKey(aesStringKey, "AES");


                                }
                                if (partCount == 2)
                                {
                                    Content = part.getContent().toString();
                                    decryptedMsg = decrypt(Content, "AES", aesKey);
                                }
                                ///////////
                                // xac nhan chu ki
                                  
                                if (partCount == 3)
                                    {
                                        _sign = part.getContent().toString();
                                        //System.out.println("Ki:"+_sign.length());
                                   
                                         String path = "./src/path/keyStore";
                                        loadKey = LoadKeyPair(from, path, "RSA");
                                        
                                        veri = verifyMsg(loadKey,decryptedMsg ,_sign);
                                        veri = true;
                                        if(veri == true)
                                        {
                                            ListVeri.add(i, true);
                                        }
                                        else
                                        {
                                            ListVeri.add(i, false);
                                        }

                                    }
                                /////////
                        }
                        else
                        {
                            ListFlagS.add(i,0);
                            MimeBodyPart part00 = (MimeBodyPart) multiPart.getBodyPart(0);
                            Content = part00.getContent().toString();
                        }
                        
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            // this part is attachment
                            
                             String fileName = part.getFileName();
                             attachFiles += fileName + ", ";
                             part.saveFile(saveDirectory + File.separator + fileName);
                             
                            if(infoCypt.equals("=== ENCRYPT AES + RSA ==="))
                            {
                                
                                String pathFile = "./src/path/Attachment/" + fileName ;
                                String pathDecrFile = "./src/path/Attachment/" +"decrypt_"+ fileName ;
                                File inputFile = new File(pathFile);
                                File outputFile = new File(pathDecrFile);
                                decryptFile(inputFile , outputFile, "RSA", privKey);
                            }
                            if(infoCypt.equals("=== SIGN BEFORE ENCRY  +  AES + RSA ===") || infoCypt.equals("=== SIGN AFTER ENCRY  +  AES + RSA ==="))
                            {
                                
                                String pathFile = "./src/path/Attachment/" + fileName ;
                                String pathDecrFile = "./src/path/Attachment/" +"decrypt_"+ fileName ;
                                File inputFile = new File(pathFile);
                                File outputFile = new File(pathDecrFile);
                                decryptFile(inputFile , outputFile, "RSA", privKey);
                            }
                            if(infoCypt.equals("=== ENCRYPT AES ==="))
                            {
                                
                                String pathFile = "./src/path/Attachment/" + fileName ;
                                String pathDecrFile = "./src/path/Attachment/" +"decrypt_"+ fileName ;
                                File inputFile = new File(pathFile);
                                File outputFile = new File(pathDecrFile);
                                decryptFile(inputFile , outputFile, "AES", aesKey);
                            }
                            if(infoCypt.equals("=== SIGN AFTER ENCRY  +  AES ===") || infoCypt.equals("=== SIGN BEFORE ENCRY  +  AES ==="))
                            {
                                
                                String pathFile = "./src/path/Attachment/" + fileName ;
                                String pathDecrFile = "./src/path/Attachment/" +"decrypt_"+ fileName ;
                                File inputFile = new File(pathFile);
                                File outputFile = new File(pathDecrFile);
                                decryptFile(inputFile , outputFile, "AES", aesKey);
                            }
                            
                        } else {
                            // this part may be the message content
                            messageContent = part.getContent().toString();
                           // System.out.println("....:" + messageContent);
                        }
                        
                    }
 
                    if (attachFiles.length() > 1) {
                        attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
                    }
                } else if (contentType.contains("text/plain")
                        || contentType.contains("text/html")) {
                    try {
                        Object content = msg.getContent();
                        if (content != null) {
                            messageContent = content.toString();
                            Content = messageContent;
                        }
                    } catch (Exception ex) {
                        messageContent = "[Error downloading content]";
                        ex.printStackTrace();
                    }
                }
 
                // print out details of each message
                System.out.println("Message #" + (i + 1) + ":");
                System.out.println("\t From: " + from);
                System.out.println("\t To: " + toList);
                System.out.println("\t CC: " + ccList);
                System.out.println("\t Subject: " + subject);
                System.out.println("\t Sent Date: " + sentDate);
                //System.out.println("\t Containt: " + Content);
                System.out.println("\t Message: " + Content);
                System.out.println("\t Attachments: " + attachFiles);
                
                ListFrom.add(from);
                ListContent.add(Content + System.getProperty("line.separator"));
                ListDeContent.add(decryptedMsg);
               // System.out.println("De1:" + decryptedMsg);
                
                ListDate.add(sentDate);
            }  
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider for protocol: " + protocol);
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store");
            ex.printStackTrace();
        } catch (IOException e) {e.printStackTrace();}  
        
        return ListSubject;
    }
 
    /**
     * Returns a list of addresses in String format separated by comma
     *
     * @param address an array of Address objects
     * @return a string represents a list of addresses
     */
    private String parseAddresses(Address[] address) {
        String listAddress = "";
 
        if (address != null) {
            for (int i = 0; i < address.length; i++) {
                listAddress += address[i].toString() + ", ";
            }
        }
        if (listAddress.length() > 1) {
            listAddress = listAddress.substring(0, listAddress.length() - 2);
        }
 
        return listAddress;
    }

}