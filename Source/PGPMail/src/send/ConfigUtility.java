package send;

import com.sun.mail.util.MailSSLSocketFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.Properties;

/**
 * A utility class that reads/saves SMTP settings from/to a properties file.
 * @author www.codejava.net
 *
 */
public class ConfigUtility {
	private File configFile = new File("smtp.properties"); 
	private Properties configProps;
	
	public Properties loadProperties() throws IOException {
		Properties defaultProps = new Properties();
		// sets default properties
		defaultProps.setProperty("mail.smtp.host", "smtp.gmail.com");
		defaultProps.setProperty("mail.smtp.port", "587");
		defaultProps.setProperty("mail.user", "****@gmail.com");
		defaultProps.setProperty("mail.password", "pass");
		defaultProps.setProperty("mail.smtp.starttls.enable", "true");
		defaultProps.setProperty("mail.smtp.auth", "true");
                defaultProps.setProperty("mail.smtp.ssl.trust", "smtp.gmail.com");
                defaultProps.setProperty("mail.smtp.ssl.trust", "smtp.mail.yahoo.com");
                
               
		
		configProps = new Properties(defaultProps);
		
		// loads properties from file
		if (configFile.exists()) {
			InputStream inputStream = new FileInputStream(configFile);
			configProps.load(inputStream);
			inputStream.close();
		}
		
		return configProps;
	}
	
	public void saveProperties(String host, String port, String user, String pass) throws IOException, GeneralSecurityException {
		configProps.setProperty("mail.smtp.host", host);
		configProps.setProperty("mail.smtp.port", port);
		configProps.setProperty("mail.user", user);
		configProps.setProperty("mail.password", pass);
		configProps.setProperty("mail.smtp.starttls.enable", "true");
		configProps.setProperty("mail.smtp.auth", "true");
                configProps.setProperty("mail.smtp.ssl.trust", host);
                
         
                
                

		
		OutputStream outputStream = new FileOutputStream(configFile);
		configProps.store(outputStream, "host setttings");
		outputStream.close();
	}	
        public String getUser()
        {
            return configProps.getProperty("mail.user");
        }
        public String getPassword()
        {
            return configProps.getProperty("mail.password");
        }
}