/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crypt;
import java.io.*;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyFactory;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;

import com.sun.jersey.core.util.Base64;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Crypt {
    
    
    public static void SaveKeyPair(String addrMail,String path, KeyPair keyPair ) throws IOException {
                
                PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();
                
		// Store Public Key.
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
				publicKey.getEncoded());
                
		FileOutputStream fos = new FileOutputStream(path +"/" + addrMail + "_public.public");
		fos.write(x509EncodedKeySpec.getEncoded());
		fos.close();
 
		// Store Private Key.
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
				privateKey.getEncoded());
		fos = new FileOutputStream(path +"/" + addrMail + "_private.private");
		fos.write(pkcs8EncodedKeySpec.getEncoded());
		fos.close();
	}
    public static KeyPair LoadKeyPair(String addrMail, String path, String algorithm)
			throws IOException, NoSuchAlgorithmException,
			InvalidKeySpecException {
		// Read Public Key.
		File filePublicKey = new File(path +"/" + addrMail + "_public.public");
		FileInputStream fis = new FileInputStream(path +"/" + addrMail + "_public.public");
		byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
		fis.read(encodedPublicKey);
		fis.close();
 
		// Read Private Key.
		File filePrivateKey = new File(path +"/" + addrMail + "_private.private");
		fis = new FileInputStream(path +"/" + addrMail + "_private.private");
		byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
		fis.read(encodedPrivateKey);
		fis.close();
 
		// Generate KeyPair.
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				encodedPublicKey);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
 
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
				encodedPrivateKey);
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
 
		return new KeyPair(publicKey, privateKey);
	}
    
     public static KeyPair generateKeyPair(String keyAlgorithm, int numBits) throws NoSuchAlgorithmException, NoSuchProviderException {

             
               // Get the public/private key pair
              
               KeyPairGenerator keyGen = KeyPairGenerator.getInstance(keyAlgorithm);
               keyGen.initialize(numBits);
               KeyPair keyPair = keyGen.genKeyPair();
               
               return keyPair;
               
     }
     
      public static SecretKey   generateSessionKey(String ALGO, int nums) throws Exception {
      
                KeyGenerator keygen = KeyGenerator.getInstance(ALGO);
                keygen.init(nums);
                SecretKey   key = keygen.generateKey();
                return key;
    }
     public static String encrypt(String data, String ALGO, Key key) throws Exception {
         
         
                Cipher c = Cipher.getInstance(ALGO);
                c.init(Cipher.ENCRYPT_MODE, key);
                byte[] encVal = c.doFinal(data.getBytes());
                String encryptedValue = new BASE64Encoder().encode(encVal);
                return encryptedValue;
                
    }
     public static void encryptFile(File inputFile,File outputFile,String ALGO, Key key) throws Exception{
            
          
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.ENCRYPT_MODE, key);
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
             
            byte[] outputBytes = c.doFinal(inputBytes);
            
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
             
            inputStream.close();
            outputStream.close();
    }
     public static void decryptFile(File inputFile,File outputFile,String ALGO, Key key) throws Exception{
            
          
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.DECRYPT_MODE, key);
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
             
            byte[] outputBytes = c.doFinal(inputBytes);
             
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
             
            inputStream.close();
            outputStream.close();
    }
    
     public static String decrypt(String encryptedData, String ALGO, Key key) throws Exception {
               
                Cipher c = Cipher.getInstance(ALGO);
                c.init(Cipher.DECRYPT_MODE, key);
                byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
                byte[] decValue = c.doFinal(decordedValue);
                String decryptedValue = new String(decValue);
                return decryptedValue;
    }
     public static String signMsg(KeyPair keypair, String data) throws Exception {
                
                 /* Initializing the object with a private key */
                PrivateKey priv = keypair.getPrivate();
                Signature sig = Signature.getInstance("SHA1withRSA");  // SHA1withRSA
                
                sig.initSign(priv);

                /* Update and sign the data */
                
                
                sig.update(data.getBytes());
                byte[] _sign = sig.sign();
                int len = _sign.length;
               
                System.out.println(data);
                System.out.println(len);
                
                //System.out.println(_sign);
                String s = _sign.toString();
                System.out.println(s.length());
                
                return s;
     }
    
     public static String convertKeyToString(SecretKey  key ) throws NoSuchAlgorithmException
     {
         
          BASE64Encoder encoder = new BASE64Encoder();
          byte[] array = key.getEncoded();
          String keyString;
          keyString = encoder.encode(array);
          return keyString;
         
        
         
         
  
     }
     public static SecretKey convertStringToKey(String keyString, String ALGO ) throws IOException
     {
         BASE64Decoder decoder = new BASE64Decoder();
         byte[] encodedKey = decoder.decodeBuffer(keyString);
         
          SecretKey  key = new SecretKeySpec(encodedKey,0,encodedKey.length, ALGO);
          return key;
         
     }
      public static boolean verifyMsg(KeyPair keypair, String data, String _sign) throws Exception {
                /* Initializing the object with the public key */
                 PublicKey pub = keypair.getPublic();
                Signature sig = Signature.getInstance("SHA1withRSA"); 
               
                sig.initVerify(pub);

                /* Update and verify the data */
                byte [] sign = new byte[128];
                int j = 0;
                for(j = 0;j < _sign.length(); j++)
                {
                    sign[j] = (byte) _sign.charAt(j);
                }
                System.out.println(data);
               
                sig.update(data.getBytes());
                boolean verifies = sig.verify(sign);
                return verifies;
       
    }
     
     /*
     public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, Exception {
         
        
                // duong dan luu key pair
                String path = "./src/path";
                String data = "le van linh";
                // phát sinh session key
                SecretKey  sekey = generateSessionKey("AES", 128);
    
                // phat sinh khoa
                KeyPair keypair;
                keypair = generateKeyPair( "RSA", 1024);
                SaveKeyPair(path, keypair);
                KeyPair LoadKeyPair = LoadKeyPair(path, "RSA"); // hàm load keypair
                Key prikey = LoadKeyPair.getPrivate();
                Key pubkey = LoadKeyPair.getPublic();

                //ma hoa data
                String Encdata = encrypt(data, "AES", sekey); 
                
                
                
                String keyses = convertKeyToString(sekey);
                
                // ma hoa va giai ma ses key
                String Encses = encrypt(keyses, "RSA", pubkey);     
                String Decses = decrypt(Encses, "RSA", prikey);
                
                SecretKey  key =  convertStringToKey(Decses, "AES");
                
                 // giai ma data
                String Decdata = decrypt(Encdata, "AES", key);
                
                System.out.println(Decdata);
                

               
     }
     */
}
