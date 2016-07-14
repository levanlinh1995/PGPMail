package send;

// import crypt
import crypt.Crypt;
import static crypt.Crypt.LoadKeyPair;
import static crypt.Crypt.SaveKeyPair;
import static crypt.Crypt.generateKeyPair;
import static crypt.Crypt.convertKeyToString;
import static crypt.Crypt.encrypt;
import static crypt.Crypt.encryptFile;
import static crypt.Crypt.decryptFile;
import static crypt.Crypt.decrypt;
import static crypt.Crypt.generateSessionKey;
import static crypt.Crypt.convertStringToKey;
import static crypt.Crypt.generateSessionKey;
import StoreKeyPair.CreateXML;
import static crypt.Crypt.signMsg;



import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.Properties;
import javax.crypto.SecretKey;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * A utility class that sends an e-mail message with attachments.
 * @author www.codejava.net
 *
 */
public class EmailUtility {
       
        public static Multipart encryptMessage(String toAddress, String message,File[] attachFiles) throws NoSuchAlgorithmException, NoSuchProviderException, IOException, InvalidKeySpecException, Exception
        {
            
            
                
               
                
                String path = "./src/path/keyStore";
                
                String address = CreateXML.getValueAttr("address", toAddress, "address");
              
                KeyPair LoadKeyPair = LoadKeyPair(address,path, "RSA");
                Key pubkey = LoadKeyPair.getPublic();
                // phat sinh session key
                SecretKey  sekey = generateSessionKey("AES", 128);

                String keyses = convertKeyToString(sekey);
                System.out.println("keyString:" + keyses);///////////////////////

                // ma hoa  ses key
                String sesString = encrypt(keyses, "RSA", pubkey);  
                
               

                //ma hoa data
                String EncMessageString = encrypt(message, "AES", sekey); 
              
                
                
                
		// creates message part
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(EncMessageString);
                //messageBodyPart.setDescription("encrypt");
                
                MimeBodyPart cryptPart = new MimeBodyPart();
		cryptPart.setContent("=== ENCRYPT AES + RSA ===","text/plain" );
                
                
                MimeBodyPart sessionBodyPart = new MimeBodyPart();
		sessionBodyPart.setText(sesString);
                //sessionBodyPart.setDescription("encrypt");
                
                
                // create multipart
                Multipart multipart = new MimeMultipart();

                // add bodypart message
                multipart.addBodyPart(cryptPart,0);
                multipart.addBodyPart(sessionBodyPart,1);
		multipart.addBodyPart(messageBodyPart,2);
                

		// adds attachments
		if ( attachFiles != null && attachFiles.length > 0) {
			for (File aFile : attachFiles) {
				MimeBodyPart attachPart = new MimeBodyPart();
                                // ma hoa file
                                String nameFile = aFile.getName();
                                String path1 = "./src/path/AttSend/";
                                File OutFile = new File(path1 + nameFile);
                                encryptFile(aFile, OutFile, "RSA",  pubkey);
                                //////////////

				try {
					attachPart.attachFile(OutFile);
				} catch (IOException ex) {
					throw ex;
				}

				multipart.addBodyPart(attachPart);
			}
		}
            
             return multipart;
        
        }
        public static Multipart encryptAES( String message,File[] attachFiles) throws NoSuchAlgorithmException, NoSuchProviderException, IOException, InvalidKeySpecException, Exception
        {
            
            
                
               
                
                
                // phat sinh AES key
                SecretKey  aesKey = generateSessionKey("AES", 128);

                String stringAESKey = convertKeyToString(aesKey);
         

                //ma hoa data
                String EncMessageString = encrypt(message, "AES", aesKey); 
   
		// creates message part
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(EncMessageString);
                //messageBodyPart.setDescription("encrypt");
                
                MimeBodyPart cryptPart = new MimeBodyPart();
		cryptPart.setContent("=== ENCRYPT AES ===","text/plain" );
                
                
                MimeBodyPart aesKeyBodyPart = new MimeBodyPart();
		aesKeyBodyPart.setText(stringAESKey);
                //sessionBodyPart.setDescription("encrypt");
                
                
                // create multipart
                Multipart multipart = new MimeMultipart();

                // add bodypart message
                multipart.addBodyPart(cryptPart,0);
                multipart.addBodyPart(aesKeyBodyPart,1);
		multipart.addBodyPart(messageBodyPart,2);
                

		// adds attachments
		if ( attachFiles != null && attachFiles.length > 0) {
			for (File aFile : attachFiles) {
				MimeBodyPart attachPart = new MimeBodyPart();
                                // ma hoa file
                                String nameFile = aFile.getName();
                                String path1 = "./src/path/AttSend/";
                                File OutFile = new File(path1 + nameFile);
                                encryptFile(aFile, OutFile, "AES",  aesKey);
                                //////////////

				try {
					attachPart.attachFile(OutFile);
				} catch (IOException ex) {
					throw ex;
				}

				multipart.addBodyPart(attachPart);
			}
		}
            
             return multipart;
        
        }
        public static Multipart message(String message,File[] attachFiles) throws NoSuchAlgorithmException, NoSuchProviderException, IOException, InvalidKeySpecException, Exception
        {
            
         
		// creates message part
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(message);
                

                // create multipart
                Multipart multipart = new MimeMultipart();

                // add bodypart message
		multipart.addBodyPart(messageBodyPart);
                

		// adds attachments
		if ( attachFiles != null && attachFiles.length > 0) {
			for (File aFile : attachFiles) {
				MimeBodyPart attachPart = new MimeBodyPart();

				try {
					attachPart.attachFile(aFile);
				} catch (IOException ex) {
					throw ex;
				}

				multipart.addBodyPart(attachPart);
			}
		}

             return multipart;
        
        }
         public static Multipart Sign(String fromAddress, String message,File[] attachFiles) throws NoSuchAlgorithmException, NoSuchProviderException, IOException, InvalidKeySpecException, Exception
        {
                String path = "./src/path/keyStore";
                
                String address = CreateXML.getValueAttr("address", fromAddress, "address");
              
                KeyPair LoadKeyPair = LoadKeyPair(address,path, "RSA");
                
 
                //sign data
                String signString = signMsg(LoadKeyPair, message);
                
                
              

		// creates message part
		MimeBodyPart signBodyPart = new MimeBodyPart();
		signBodyPart.setText(signString);
                
                
                MimeBodyPart msgBodyPart = new MimeBodyPart();
		msgBodyPart.setText(message);
                
                MimeBodyPart signPart = new MimeBodyPart();
		signPart.setText("=== SIGN ===");
                

                
                // create multipart
                Multipart multipart = new MimeMultipart();

                // add bodypart message
                multipart.addBodyPart(signPart,0);
		multipart.addBodyPart(signBodyPart,1);
                multipart.addBodyPart(msgBodyPart,2);

		// adds attachments
		if ( attachFiles != null && attachFiles.length > 0) {
			for (File aFile : attachFiles) {
				MimeBodyPart attachPart = new MimeBodyPart();
                                

				try {
					attachPart.attachFile(aFile);
				} catch (IOException ex) {
					throw ex;
				}

				multipart.addBodyPart(attachPart);
			}
		}
             
             return multipart;
        
        }
         //3
          public static Multipart signAfterEncAndRsaAes(String toAddress, String fromAddress, String message,File[] attachFiles) throws NoSuchAlgorithmException, NoSuchProviderException, IOException, InvalidKeySpecException, Exception
        {
            
            
                
               
                
                String path = "./src/path/keyStore";
                
                String address = CreateXML.getValueAttr("address", toAddress, "address");
              
                KeyPair LoadKeyPair = LoadKeyPair(address,path, "RSA");
                Key pubkey = LoadKeyPair.getPublic();
                // phat sinh session key
                SecretKey  sekey = generateSessionKey("AES", 128);

                String keyses = convertKeyToString(sekey);
                System.out.println("keyString:" + keyses);///////////////////////

                // ma hoa  ses key
                String sesString = encrypt(keyses, "RSA", pubkey);  
                
               

                //ma hoa data
                String EncMessageString = encrypt(message, "AES", sekey); 
                
                
                // thuc hien ki msg
                String address1 = CreateXML.getValueAttr("address", fromAddress, "address");
                KeyPair LoadKeyPair1 = LoadKeyPair(address1,path, "RSA");
                String signString = signMsg(LoadKeyPair1, EncMessageString);
              
                
                
                
		// creates message part
		MimeBodyPart signBodyPart = new MimeBodyPart();
		signBodyPart.setText(signString);
                
                MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(EncMessageString);
                
                //messageBodyPart.setDescription("encrypt");
                MimeBodyPart cryptPart = new MimeBodyPart();
		cryptPart.setContent("=== SIGN AFTER ENCRY  +  AES + RSA ===","text/plain" );
                
                
                MimeBodyPart sessionBodyPart = new MimeBodyPart();
		sessionBodyPart.setText(sesString);
                //sessionBodyPart.setDescription("encrypt");
                
                
                // create multipart
                Multipart multipart = new MimeMultipart();

                // add bodypart message
                multipart.addBodyPart(cryptPart,0);
                multipart.addBodyPart(sessionBodyPart,1);
		multipart.addBodyPart(messageBodyPart,2);
                multipart.addBodyPart(signBodyPart,3);
                

		// adds attachments
		if ( attachFiles != null && attachFiles.length > 0) {
			for (File aFile : attachFiles) {
				MimeBodyPart attachPart = new MimeBodyPart();
                                // ma hoa file
                                String nameFile = aFile.getName();
                                String path1 = "./src/path/AttSend/";
                                File OutFile = new File(path1 + nameFile);
                                encryptFile(aFile, OutFile, "RSA",  pubkey);
                                //////////////

				try {
					attachPart.attachFile(OutFile);
				} catch (IOException ex) {
					throw ex;
				}

				multipart.addBodyPart(attachPart);
			}
		}
            
             return multipart;
        
        }
          //5
         public static Multipart signBeforeEncAndRsaAes(String toAddress, String fromAddress, String message,File[] attachFiles) throws NoSuchAlgorithmException, NoSuchProviderException, IOException, InvalidKeySpecException, Exception
        {
            
            
                
               
                
                String path = "./src/path/keyStore";
                
                String address = CreateXML.getValueAttr("address", toAddress, "address");
              
                KeyPair LoadKeyPair = LoadKeyPair(address,path, "RSA");
                Key pubkey = LoadKeyPair.getPublic();
                // phat sinh session key
                SecretKey  sekey = generateSessionKey("AES", 128);

                String keyses = convertKeyToString(sekey);
                System.out.println("keyString:" + keyses);///////////////////////

                // ma hoa  ses key
                String sesString = encrypt(keyses, "RSA", pubkey);  
                
               // thuc hien ki msg
                String address1 = CreateXML.getValueAttr("address", fromAddress, "address");
                KeyPair LoadKeyPair1 = LoadKeyPair(address1,path, "RSA");
                String signString = signMsg(LoadKeyPair1, message);

                //ma hoa data
                String EncMessageString = encrypt(message, "AES", sekey); 
                
                
                
              
                
                
                
		// creates message part
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(EncMessageString);
                
                MimeBodyPart signBodyPart = new MimeBodyPart();
		signBodyPart.setText(signString);
                
                //messageBodyPart.setDescription("encrypt");
                
                MimeBodyPart cryptPart = new MimeBodyPart();
		cryptPart.setContent("=== SIGN BEFORE ENCRY  +  AES + RSA ===","text/plain" );
                
                
                MimeBodyPart sessionBodyPart = new MimeBodyPart();
		sessionBodyPart.setText(sesString);
                //sessionBodyPart.setDescription("encrypt");
                
                
                // create multipart
                Multipart multipart = new MimeMultipart();

                // add bodypart message
                multipart.addBodyPart(cryptPart,0);
                multipart.addBodyPart(sessionBodyPart,1);
                multipart.addBodyPart(messageBodyPart,2);
                multipart.addBodyPart(signBodyPart,3);
		
                

		// adds attachments
		if ( attachFiles != null && attachFiles.length > 0) {
			for (File aFile : attachFiles) {
				MimeBodyPart attachPart = new MimeBodyPart();
                                // ma hoa file
                                String nameFile = aFile.getName();
                                String path1 = "./src/path/AttSend/";
                                File OutFile = new File(path1 + nameFile);
                                encryptFile(aFile, OutFile, "RSA",  pubkey);
                                //////////////

				try {
					attachPart.attachFile(OutFile);
				} catch (IOException ex) {
					throw ex;
				}

				multipart.addBodyPart(attachPart);
			}
		}
            
             return multipart;
        
        }
         //6
        public static Multipart signAfterEncAndAes(String fromAddress, String message,File[] attachFiles) throws NoSuchAlgorithmException, NoSuchProviderException, IOException, InvalidKeySpecException, Exception
        {
            
            
                
               
                String path = "./src/path/keyStore";
                
                // phat sinh AES key
                SecretKey  aesKey = generateSessionKey("AES", 128);

                String stringAESKey = convertKeyToString(aesKey);
         

                //ma hoa data
                String EncMessageString = encrypt(message, "AES", aesKey); 
                
                
                // thuc hien ki msg
                String address1 = CreateXML.getValueAttr("address", fromAddress, "address");
                KeyPair LoadKeyPair1 = LoadKeyPair(address1,path, "RSA");
                String signString = signMsg(LoadKeyPair1, EncMessageString);
                
   
		// creates message part
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(EncMessageString);
                
                MimeBodyPart signBodyPart = new MimeBodyPart();
		signBodyPart.setText(signString);
                
                //messageBodyPart.setDescription("encrypt");
                
                MimeBodyPart cryptPart = new MimeBodyPart();
		cryptPart.setContent("=== SIGN AFTER ENCRY  +  AES ===","text/plain" );
                
                
                MimeBodyPart aesKeyBodyPart = new MimeBodyPart();
		aesKeyBodyPart.setText(stringAESKey);
                //sessionBodyPart.setDescription("encrypt");
                
                
                // create multipart
                Multipart multipart = new MimeMultipart();

                // add bodypart message
                multipart.addBodyPart(cryptPart,0);
                multipart.addBodyPart(aesKeyBodyPart,1);
		multipart.addBodyPart(messageBodyPart,2);
                multipart.addBodyPart(signBodyPart,3);
                

		// adds attachments
		if ( attachFiles != null && attachFiles.length > 0) {
			for (File aFile : attachFiles) {
				MimeBodyPart attachPart = new MimeBodyPart();
                                // ma hoa file
                                String nameFile = aFile.getName();
                                String path1 = "./src/path/AttSend/";
                                File OutFile = new File(path1 + nameFile);
                                encryptFile(aFile, OutFile, "AES",  aesKey);
                                //////////////

				try {
					attachPart.attachFile(OutFile);
				} catch (IOException ex) {
					throw ex;
				}

				multipart.addBodyPart(attachPart);
			}
		}
            
             return multipart;
        
        }
        //7
        public static Multipart signBeforeEncAndAes(String fromAddress, String message,File[] attachFiles) throws NoSuchAlgorithmException, NoSuchProviderException, IOException, InvalidKeySpecException, Exception
        {
            
            
                
               
                String path = "./src/path/keyStore";
                
                // phat sinh AES key
                SecretKey  aesKey = generateSessionKey("AES", 128);

                String stringAESKey = convertKeyToString(aesKey);
                
                // thuc hien ki msg
                String address1 = CreateXML.getValueAttr("address", fromAddress, "address");
                KeyPair LoadKeyPair1 = LoadKeyPair(address1,path, "RSA");
                String signString = signMsg(LoadKeyPair1, message);
         

                //ma hoa data
                String EncMessageString = encrypt(message, "AES", aesKey); 
                
                
                
                
   
		// creates message part
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(EncMessageString);
                
                MimeBodyPart signBodyPart = new MimeBodyPart();
		signBodyPart.setText(signString);
                
                //messageBodyPart.setDescription("encrypt");
                
                MimeBodyPart cryptPart = new MimeBodyPart();
		cryptPart.setContent("=== SIGN BEFORE ENCRY  +  AES ===","text/plain" );
                
                
                MimeBodyPart aesKeyBodyPart = new MimeBodyPart();
		aesKeyBodyPart.setText(stringAESKey);
                //sessionBodyPart.setDescription("encrypt");
                
                
                // create multipart
                Multipart multipart = new MimeMultipart();

                // add bodypart message
                multipart.addBodyPart(cryptPart,0);
                multipart.addBodyPart(aesKeyBodyPart,1);
		multipart.addBodyPart(messageBodyPart,2);
                multipart.addBodyPart(signBodyPart,3);
                

		// adds attachments
		if ( attachFiles != null && attachFiles.length > 0) {
			for (File aFile : attachFiles) {
				MimeBodyPart attachPart = new MimeBodyPart();
                                // ma hoa file
                                String nameFile = aFile.getName();
                                String path1 = "./src/path/AttSend/";
                                File OutFile = new File(path1 + nameFile);
                                encryptFile(aFile, OutFile, "AES",  aesKey);
                                //////////////

				try {
					attachPart.attachFile(OutFile);
				} catch (IOException ex) {
					throw ex;
				}

				multipart.addBodyPart(attachPart);
			}
		}
            
             return multipart;
        
        }
	public static void sendEmail(Properties smtpProperties, String toAddress,
			String subject, String message, File[] attachFiles, int select)
			throws AddressException, MessagingException, IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, Exception {

		final String userName = smtpProperties.getProperty("mail.user");
		final String password = smtpProperties.getProperty("mail.password");
		
		// creates a new session with an authenticator
		Authenticator auth = new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		};
		Session session = Session.getInstance(smtpProperties, auth);

		// creates a new e-mail message
		Message msg = new MimeMessage(session);

		msg.setFrom(new InternetAddress(userName));
		InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
		msg.setRecipients(Message.RecipientType.TO, toAddresses);
		msg.setSubject(subject);
		msg.setSentDate(new Date());
                
                Multipart multipart = null;
                if( select == 0)
                {
                    //no encrypt and sign
                    multipart = message(message,attachFiles);
                }
                else if( select == 1)
                {
                    
                    
                    multipart = encryptMessage(toAddress,message,attachFiles);
                }
                else if( select == 2)
                {
                    
                    multipart = Sign( userName , message ,attachFiles);
                }
                
                else if( select == 4)
                {
                    
                    multipart = encryptAES( message, attachFiles);
                }
                
                
                else if( select == 3)
                {
                    
                    multipart = signAfterEncAndRsaAes(toAddress, userName,  message, attachFiles);
                }
                else if( select == 5)
                {
                    
                    multipart = signBeforeEncAndRsaAes(toAddress, userName,  message, attachFiles);
                }
                else if( select == 6)
                {
                    
                    multipart = signAfterEncAndAes(userName,  message, attachFiles);
                }
                else if( select == 7)
                {
                    
                    multipart = signBeforeEncAndAes(userName, message, attachFiles);
                }
                
                

		msg.setContent(multipart);

		// sends the e-mail
		Transport.send(msg);

	}
}