package Client.Interface;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import Client.Data.MaintenanceData;
import Client.Transmit.TransferFile;
import Utility.JFilePicker;

/**
 * A Swing application that uploads files to a HTTP server.
 * 
 * @author www.codejava.net
 *
 */
public class SwingFileUploadHTTP extends JFrame implements PropertyChangeListener
{
	private JLabel labelURL = new JLabel("원격지 URL : ");
	private JTextField fieldURL = new JTextField(30);
	private JFilePicker filePicker = new JFilePicker("파일 선택 : ", "Browse");
	private JButton buttonUpload = new JButton("업로드");
	private JLabel labelProgress = new JLabel("Progress:");
	private JProgressBar progressBar = new JProgressBar(0, 100);
	ClientFrame clientFrame;
	
	public SwingFileUploadHTTP(ClientFrame clientFrame)
	{
		super("파일 업로드");
		this.clientFrame = clientFrame;
		// set up layout
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 5, 5);
		// set up components
		filePicker.setMode(JFilePicker.MODE_OPEN);
		buttonUpload.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				buttonUploadActionPerformed(event);
			}
		});
		progressBar.setPreferredSize(new Dimension(200, 30));
		progressBar.setStringPainted(true);
		// add components to the frame
		constraints.gridx = 0;
		constraints.gridy = 0;
		add(labelURL, constraints);
		constraints.gridx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		fieldURL.setText("http://" + MaintenanceData.serverIP + MaintenanceData.uploadFolderAddress);
		fieldURL.setEditable(false);
		add(fieldURL, constraints);
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 0.0;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.NONE;
		add(filePicker, constraints);
		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.CENTER;
		add(buttonUpload, constraints);
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.WEST;
		add(labelProgress, constraints);
		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		add(progressBar, constraints);
		pack();
		setVisible(true);
		setLocationRelativeTo(null); // center on screen
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	/**
	 * handle click event of the Upload button
	 */
	private void buttonUploadActionPerformed(ActionEvent event)
	{
		String uploadURL = fieldURL.getText();
		String filePath = filePicker.getSelectedFilePath();
		// validate input first
		if (uploadURL.equals(""))
		{
			JOptionPane.showMessageDialog(this, "업로드 될 원격지 주소를 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
			fieldURL.requestFocus();
			return;
		}
		if (filePath.equals(""))
		{
			JOptionPane.showMessageDialog(this, "업로드 할 파일을 지정하세요.", "오류", JOptionPane.ERROR_MESSAGE);
			return;
		}
		try
		{
			progressBar.setValue(0);
			TransferFile tf = new TransferFile(MaintenanceData.serverIP, MaintenanceData.fileServerPORT, filePath, progressBar);
			tf.addPropertyChangeListener(this);
			tf.execute();
			// File uploadFile = new File(filePath);
			// UploadTask task = new UploadTask(uploadURL, uploadFile);
			// task.addPropertyChangeListener(this);
			// task.execute();
		} catch (Exception ex)
		{
			JOptionPane.showMessageDialog(this, "Error executing upload task: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		//dispose();
	}

	/**
	 * Update the progress bar's state whenever the progress of upload changes.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if ("progress" == evt.getPropertyName())
		{
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		}
	}
	/**
	 * Launch the application
	 * 
	 * public static void main(String[] args) { try { // set look and feel to
	 * system dependent
	 * UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
	 * catch (Exception ex) { ex.printStackTrace(); }
	 * 
	 * SwingUtilities.invokeLater(new Runnable() {
	 * 
	 * @Override public void run() { new SwingFileUploadHTTP().setVisible(true);
	 *           } }); }
	 */
}
