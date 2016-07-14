package Interface;
import send.ConfigUtility;
import send.SettingsDialog;
import send.EmailUtility;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import send.JFilePicker.JFilePicker;



public class SwingEmailSender extends JFrame {
	private ConfigUtility configUtil = new ConfigUtility();
	
	private JLabel labelTo = new JLabel("To: ");
	private JLabel labelSubject = new JLabel("Subject: ");
	
	private JTextField fieldTo = new JTextField(30);
	private JTextField fieldSubject = new JTextField(30);
	
	private JButton buttonSend = new JButton("SEND");
	
	private JFilePicker filePicker = new JFilePicker("Attached", "Attach File...");
	
	private JTextArea textAreaMessage = new JTextArea(10, 30);
	
	private GridBagConstraints constraints = new GridBagConstraints();
        
        int selected = 0;
	public SwingEmailSender() {
            
        }
	public SwingEmailSender(int select) {
		super("Swing E-mail Sender Program");
                // 1: encrypt
                // 2: sign
                // 3: encrypt and sign
                // 4: decrypt
                // 5: verify
                // 6: decrypt and verify
                
                selected = select;
                
		
		// set up layout
		setLayout(new GridBagLayout());
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 5, 5);
	
		setupForm();
		
		pack();
		setLocationRelativeTo(null);	// center on screen
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);		
	}

	void setupMenu() {
            SettingsDialog dialog = new SettingsDialog( configUtil);
				dialog.setVisible(true);
	}
	
	private void setupForm() {
		constraints.gridx = 0;
		constraints.gridy = 0;
		add(labelTo, constraints);
		
		constraints.gridx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		add(fieldTo, constraints);
		
		constraints.gridx = 0;
		constraints.gridy = 1;
		add(labelSubject, constraints);
		
		constraints.gridx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		add(fieldSubject, constraints);
		
		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.gridheight = 2;
		constraints.fill = GridBagConstraints.BOTH;
		buttonSend.setFont(new Font("Arial", Font.BOLD, 16));
		add(buttonSend, constraints);
		
		buttonSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				buttonSendActionPerformed(event);
			}
		});
		
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridheight = 1;
		constraints.gridwidth = 3;
		filePicker.setMode(JFilePicker.MODE_OPEN);
		add(filePicker, constraints);
		
		constraints.gridy = 3;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		
		add(new JScrollPane(textAreaMessage), constraints);		
	}
	
	private void buttonSendActionPerformed(ActionEvent event) {
		if (!validateFields()) {
			return;
		}
		
		String toAddress = fieldTo.getText();
		String subject = fieldSubject.getText();
		String message = textAreaMessage.getText();
		
		File[] attachFiles = null;
		
		if (!filePicker.getSelectedFilePath().equals("")) {
			File selectedFile = new File(filePicker.getSelectedFilePath());
			attachFiles = new File[] {selectedFile};
		}
		
		try {
			Properties smtpProperties = configUtil.loadProperties();
			EmailUtility.sendEmail(smtpProperties, toAddress, subject, message, attachFiles, selected);
			
			JOptionPane.showMessageDialog(this, 
					"The e-mail has been sent successfully!");
			
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, 
					"Error while sending the e-mail: " + ex.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private boolean validateFields() {
		if (fieldTo.getText().equals("")) {
			JOptionPane.showMessageDialog(this, 
					"Please enter To address!",
					"Error", JOptionPane.ERROR_MESSAGE);
			fieldTo.requestFocus();
			return false;
		}
		
		if (fieldSubject.getText().equals("")) {
			JOptionPane.showMessageDialog(this, 
					"Please enter subject!",
					"Error", JOptionPane.ERROR_MESSAGE);
			fieldSubject.requestFocus();
			return false;
		}
		
		if (textAreaMessage.getText().equals("")) {
			JOptionPane.showMessageDialog(this, 
					"Please enter message!",
					"Error", JOptionPane.ERROR_MESSAGE);
			textAreaMessage.requestFocus();
			return false;
		}
		
		return true;
	}
	/*
	public static void main(String[] args) {
		// set look and feel to system dependent
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new SwingEmailSender().setVisible(true);
			}
		});
	}
        */
}