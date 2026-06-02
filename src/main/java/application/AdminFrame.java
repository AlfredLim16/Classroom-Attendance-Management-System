package application;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import user.User;

public class AdminFrame extends JFrame implements ActionListener {

    private JLabel titleLabel;
    private JButton closeBtn;
    private JButton btnDashboard, btnUsers, btnCourses, btnSections, btnProfessors, btnStudents, btnReports, btnSettings;
    private JPanel currentPanel;
    private JPanel contentContainer;

    private User currentUser;

    public AdminFrame(){
        ConfigureWindow();
        TitleBar();
        ContentContainer();
        showPanel(new AdminDashboardPanel());
        setVisible(true);
    }

    public void setCurrentUser(User user){
        this.currentUser = user;
        if(currentPanel instanceof AdminDashboardPanel panel){
            panel.setCurrentUser(user);
        }
    }

    private void ConfigureWindow(){
        setUndecorated(true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.85);
        int height = (int) (screenSize.height * 0.85);
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210), 1));
    }

    private void TitleBar(){
        JPanel titleBar = new JPanel();
        titleBar.setBounds(0, 0, getWidth(), 32);
        titleBar.setBackground(Color.WHITE);
        titleBar.setLayout(null);

        ImageIcon rawIcon = new ImageIcon(getClass().getResource("/icons/favicon.png"));
        Image scaled = rawIcon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
        JLabel icon = new JLabel(new ImageIcon(scaled));
        icon.setBounds(12, 10, 20, 20);
        titleBar.add(icon);

        titleLabel = new JLabel("Orange - Admin");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(60, 60, 60));
        titleLabel.setBounds(40, 0, 200, 40);
        titleBar.add(titleLabel);

        ImageIcon closeRaw = new ImageIcon(getClass().getResource("/icons/close.png"));
        Image closeScaled = closeRaw.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);

        closeBtn = new JButton(new ImageIcon(closeScaled));
        closeBtn.setBounds(getWidth() - 40, 8, 28, 24);
        closeBtn.setBackground(Color.WHITE);
        closeBtn.setBorder(null);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(this);
        titleBar.add(closeBtn);

        int buttonWidth = 80;
        int buttonHeight = 24;
        int gap = 6;
        int totalWidth = (buttonWidth * 8) + (gap * 7);
        int startX = (getWidth() - totalWidth) / 2;
        int buttonY = 8;

        btnDashboard = navButton("Dashboard", startX, buttonY, buttonWidth, buttonHeight);
        btnUsers = navButton("Users", startX + buttonWidth + gap, buttonY, buttonWidth, buttonHeight);
        btnCourses = navButton("Courses", startX + (buttonWidth + gap) * 2, buttonY, buttonWidth, buttonHeight);
        btnSections = navButton("Sections", startX + (buttonWidth + gap) * 3, buttonY, buttonWidth, buttonHeight);
        btnProfessors = navButton("Professors", startX + (buttonWidth + gap) * 4, buttonY, buttonWidth + 10, buttonHeight);
        btnStudents = navButton("Students", startX + (buttonWidth + gap) * 5 + 10, buttonY, buttonWidth, buttonHeight);
        btnReports = navButton("Reports", startX + (buttonWidth + gap) * 6 + 10, buttonY, buttonWidth, buttonHeight);
        btnSettings = navButton("Settings", startX + (buttonWidth + gap) * 7 + 10, buttonY, buttonWidth, buttonHeight);

        titleBar.add(btnDashboard);
        titleBar.add(btnUsers);
        titleBar.add(btnCourses);
        titleBar.add(btnSections);
        titleBar.add(btnProfessors);
        titleBar.add(btnStudents);
        titleBar.add(btnReports);
        titleBar.add(btnSettings);

        add(titleBar);
    }

    private JButton navButton(String text, int x, int y, int w, int h){
        JButton btn = new JButton(text);
        btn.setBounds(x, y, w, h);
        btn.setBackground(Color.WHITE);
        btn.setBorder(null);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.addActionListener(this);
        return btn;
    }

    private void ContentContainer(){
        contentContainer = new JPanel();
        contentContainer.setLayout(null);
        contentContainer.setBackground(Color.WHITE);
        contentContainer.setBounds(0, 32, getWidth(), getHeight() - 32);
        add(contentContainer);
    }

    private void showPanel(JPanel panel){
        if(currentPanel != null){
            contentContainer.remove(currentPanel);
        }
        currentPanel = panel;
        panel.setBounds(0, 0, contentContainer.getWidth(), contentContainer.getHeight());
        contentContainer.add(currentPanel);
        contentContainer.revalidate();
        contentContainer.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == closeBtn){
            System.exit(0);
        }
        if(e.getSource() == btnDashboard){
            AdminDashboardPanel panel = new AdminDashboardPanel();
            if(currentUser != null) panel.setCurrentUser(currentUser);
            showPanel(panel);
        }
        if(e.getSource() == btnUsers){
            showPanel(new AdminUsersPanel());
        }
        if(e.getSource() == btnCourses){
            showPanel(new AdminCoursesPanel());
        }
        if(e.getSource() == btnSections){
            showPanel(new AdminSectionsPanel());
        }
        if(e.getSource() == btnProfessors){
            showPanel(new AdminProfessorsPanel());
        }
        if(e.getSource() == btnStudents){
            showPanel(new AdminStudentsPanel());
        }
        if(e.getSource() == btnReports){
            showPanel(new AdminReportsPanel());
        }
        if(e.getSource() == btnSettings){
            showPanel(new AdminSettingsPanel());
        }
    }
}