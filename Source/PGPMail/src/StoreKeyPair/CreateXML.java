/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package StoreKeyPair;

import java.io.File;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import crypt.Crypt;
import static crypt.Crypt.SaveKeyPair;
import static crypt.Crypt.generateKeyPair;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author lelinh
 */
public class CreateXML {
    
    public static void Create(String username, String email, String password) throws ParserConfigurationException, TransformerConfigurationException, TransformerException, NoSuchAlgorithmException, NoSuchProviderException, IOException, SAXException
    {
        File f = new File("./src/path/data/data.xml");
        Document doc;
        Element rootElement;
        if( !f.exists())
        {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            rootElement = doc.createElement("root");
            doc.appendChild(rootElement);
        }
        else
        {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
            rootElement = doc.getDocumentElement();
        }
        
        Element emailElement = doc.createElement("email");
        Attr addr = doc.createAttribute("address");
        addr.setValue(email);
        Attr user = doc.createAttribute("username");
        user.setValue(username);
        Attr pass = doc.createAttribute("password");
        pass.setValue(password);
        Attr url = doc.createAttribute("url");
        String path = "./src/path/keyStore" ;
        url.setValue(path);
        
        
        emailElement.setAttributeNode(addr);
        emailElement.setAttributeNode(user);
        emailElement.setAttributeNode(pass);
        emailElement.setAttributeNode(url);
        rootElement.appendChild(emailElement);
        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
         DOMSource source = new DOMSource(doc);
         
         StreamResult result = new StreamResult(f);
         transformer.transform(source, result);
         // Output to console for testing
         StreamResult consoleResult =
         new StreamResult(System.out);
         transformer.transform(source, consoleResult);
         
         
         // generate key
          KeyPair keypair;
          keypair = generateKeyPair( "RSA", 1024);
          SaveKeyPair(email, path, keypair);
          
          
        
        
        
        
    }
    public static String getValueAttr(String Attr, String value, String need ) throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory factory =DocumentBuilderFactory.newInstance();
        DocumentBuilder builder =factory.newDocumentBuilder();
        Document doc = builder.parse(new File("./src/path/data/data.xml"));
        Element root = doc.getDocumentElement();
        
        // Xử lý các node con dạng Element
        String _value1 = null;
        String _value2 = null;
        NodeList list = root.getChildNodes();
        for(int i=0;i<list.getLength();++i)
        {
            Node node = list.item(i);
            if(node instanceof Element)
            {
                
                Element element = (Element) node;
                _value1 = element.getAttribute(Attr);
                
                if( _value1.equals(value) )
                {
                    _value2 = element.getAttribute(need);
                    return _value2;
                }
            }
        }
        
        return null;
        
    }
    
}
