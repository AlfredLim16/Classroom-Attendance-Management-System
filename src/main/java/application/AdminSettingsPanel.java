package application;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

public class AdminSettingsPanel extends JPanel implements ActionListener {

    private JLabel lblTitle, lblSubTitle;
    private JSeparator separator;
    private JLabel lblSemester, lblStartDate, lblEndDate, lblLateThreshold, lblAutoDrop, lblEmailNotif;
    private JComboBox<String> cmbSemester;
    private JTextField txtStartDate, txtEndDate, txtLateThreshold;
    private JCheckBox chkAutoDrop, chkEmailNotif;
    private JButton btnSave, btnReset;

    public AdminSettingsPanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("System Settings");
        lblTitle.setBounds(40, 20, 300, 30);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle);

        lblSubTitle = new JLabel("Configure global attendance and academic settings");
        lblSubTitle.setBounds(40, 50, 500, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblSubTitle);

        separator = new JSeparator();
        separator.setForeground(Color.BLACK);
        add(separator);

        // Current Semester
        lblSemester = new JLabel("Current Semester");
        lblSemester.setBounds(40, 100, 150, 25);
        lblSemester.setFont(new Font("Arial", Font.BOLD, 13));
        lblSemester.setForeground(new Color(100, 100, 100));
        add(lblSemester);

        cmbSemester = new JComboBox<>(new String[]{"1st Semester 2025-2026", "2nd Semester 2025-2026"});
        cmbSemester.setBounds(40, 128, 250, 36);
        cmbSemester.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbSemester.setBackground(Color.WHITE);
        cmbSemester.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbSemester.setFocusable(false);
        add(cmbSemester);

        // Start Date
        lblStartDate = new JLabel("Start Date");
        lblStartDate.setBounds(320, 100, 120, 25);
        lblStartDate.setFont(new Font("Arial", Font.BOLD, 13));
        lblStartDate.setForeground(new Color(100, 100, 100));
        add(lblStartDate);

        txtStartDate = new JTextField("2025-08-15");
        txtStartDate.setBounds(320, 128, 150, 36);
        txtStartDate.setFont(new Font("Arial", Font.PLAIN, 14));
        txtStartDate.setForeground(new Color(60, 60, 60));
        txtStartDate.setBackground(Color.WHITE);
        txtStartDate.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        add(txtStartDate);

        // End Date
        lblEndDate = new JLabel("End Date");
        lblEndDate.setBounds(490, 100, 120, 25);
        lblEndDate.setFont(new Font("Arial", Font.BOLD, 13));
        lblEndDate.setForeground(new Color(100, 100, 100));
        add(lblEndDate);

        txtEndDate = new JTextField("2025-12-20");
        txtEndDate.setBounds(490, 128, 150, 36);
        txtEndDate.setFont(new Font("Arial", Font.PLAIN, 14));
        txtEndDate.setForeground(new Color(60, 60, 60));
        txtEndDate.setBackground(Color.WHITE);
        txtEndDate.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        add(txtEndDate);

        // Default Late Threshold
        lblLateThreshold = new JLabel("Default Late Threshold (mins)");
        lblLateThreshold.setBounds(40, 190, 220, 25);
        lblLateThreshold.setFont(new Font("Arial", Font.BOLD, 13));
        lblLateThreshold.setForeground(new Color(100, 100, 100));
        add(lblLateThreshold);

        txtLateThreshold = new JTextField("15");
        txtLateThreshold.setBounds(40, 218, 120, 36);
        txtLateThreshold.setFont(new Font("Arial", Font.PLAIN, 14));
        txtLateThreshold.setForeground(new Color(60, 60, 60));
        txtLateThreshold.setBackground(Color.WHITE);
        txtLateThreshold.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        add(txtLateThreshold);

        // Auto-drop checkbox
        lblAutoDrop = new JLabel("Auto-drop on Excessive Absences");
        lblAutoDrop.setBounds(40, 280, 250, 25);
        lblAutoDrop.setFont(new Font("Arial", Font.BOLD, 13));
        lblAutoDrop.setForeground(new Color(100, 100, 100));
        add(lblAutoDrop);

        chkAutoDrop = new JCheckBox("Enable automatic student drop notification");
        chkAutoDrop.setBounds(40, 308, 350, 25);
        chkAutoDrop.setFont(new Font("Arial", Font.PLAIN, 13));
        chkAutoDrop.setForeground(new Color(60, 60, 60));
        chkAutoDrop.setBackground(Color.WHITE);
        chkAutoDrop.setFocusPainted(false);
        chkAutoDrop.setSelected(true);
        add(chkAutoDrop);

        // Email notifications
        lblEmailNotif = new JLabel("Email Notifications");
        lblEmailNotif.setBounds(40, 350, 200, 25);
        lblEmailNotif.setFont(new Font("Arial", Font.BOLD, 13));
        lblEmailNotif.setForeground(new Color(100, 100, 100));
        add(lblEmailNotif);

        chkEmailNotif = new JCheckBox("Send email alerts for missed quizzes and absences");
        chkEmailNotif.setBounds(40, 378, 400, 25);
        chkEmailNotif.setFont(new Font("Arial", Font.PLAIN, 13));
        chkEmailNotif.setForeground(new Color(60, 60, 60));
        chkEmailNotif.setBackground(Color.WHITE);
        chkEmailNotif.setFocusPainted(false);
        chkEmailNotif.setSelected(false);
        add(chkEmailNotif);

        // Save button
        btnSave = new JButton("Save Settings");
        btnSave.setBounds(40, 440, 140, 36);
        btnSave.setFont(new Font("Arial", Font.PLAIN, 14));
        btnSave.setForeground(Color.WHITE);
        btnSave.setBackground(new Color(255, 140, 0));
        btnSave.setBorder(null);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(this);
        add(btnSave);

        // Reset button
        btnReset = new JButton("Reset to Defaults");
        btnReset.setBounds(200, 440, 150, 36);
        btnReset.setFont(new Font("Arial", Font.PLAIN, 14));
        btnReset.setForeground(new Color(100, 100, 100));
        btnReset.setBackground(Color.WHITE);
        btnReset.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btnReset.setFocusPainted(false);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(this);
        add(btnReset);
    }

    @Override
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x, y, width, height);
        if(width > 0 && height > 0){
            separator.setBounds(40, 80, width - 80, height - 160);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnSave){
            JOptionPane.showMessageDialog(this,
                "Settings saved successfully:\n" +
                "Semester: " + cmbSemester.getSelectedItem() + "\n" +
                "Late Threshold: " + txtLateThreshold.getText() + " mins\n" +
                "Auto-drop: " + (chkAutoDrop.isSelected() ? "Enabled" : "Disabled") + "\n" +
                "Email Notifications: " + (chkEmailNotif.isSelected() ? "Enabled" : "Disabled"),
                "Settings Saved", JOptionPane.INFORMATION_MESSAGE);
        }
        if(e.getSource() == btnReset){
            cmbSemester.setSelectedIndex(0);
            txtStartDate.setText("2025-08-15");
            txtEndDate.setText("2025-12-20");
            txtLateThreshold.setText("15");
            chkAutoDrop.setSelected(true);
            chkEmailNotif.setSelected(false);
            JOptionPane.showMessageDialog(this, "Settings reset to defaults.", "Reset", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}