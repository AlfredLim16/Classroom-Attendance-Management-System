package frames;

import core.User;
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
import panels.AdminCoursesPanel;
import panels.AdminDashboardPanel;
import panels.AdminProfessorsPanel;
import panels.AdminReportsPanel;
import panels.AdminSectionsPanel;
import panels.AdminSettingsPanel;
import panels.AdminStudentsPanel;
import panels.AdminUsersPanel;

public class AdminFrame extends JFrame implements ActionListener {

    private JLabel titleLabel;
    private JButton closeBtn;
    private JButton btnDashboard, btnUsers, btnCourses, btnSections, btnProfessors, btnStudents, btnReports, btnSettings, btnLogout;
    private JPanel currentPanel;
    private JPanel contentContainer;
    private JPanel titleBar;

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
        titleBar = new JPanel();
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

        btnDashboard = new JButton("Dashboard");
        btnDashboard.setBackground(Color.WHITE);
        btnDashboard.setBorder(null);
        btnDashboard.setFocusPainted(false);
        btnDashboard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDashboard.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnDashboard.addActionListener(this);
        titleBar.add(btnDashboard);

        btnUsers = new JButton("Users");
        btnUsers.setBackground(Color.WHITE);
        btnUsers.setBorder(null);
        btnUsers.setFocusPainted(false);
        btnUsers.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUsers.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnUsers.addActionListener(this);
        titleBar.add(btnUsers);

        btnCourses = new JButton("Courses");
        btnCourses.setBackground(Color.WHITE);
        btnCourses.setBorder(null);
        btnCourses.setFocusPainted(false);
        btnCourses.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCourses.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCourses.addActionListener(this);
        titleBar.add(btnCourses);

        btnSections = new JButton("Sections");
        btnSections.setBackground(Color.WHITE);
        btnSections.setBorder(null);
        btnSections.setFocusPainted(false);
        btnSections.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSections.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnSections.addActionListener(this);
        titleBar.add(btnSections);

        btnProfessors = new JButton("Professors");
        btnProfessors.setBackground(Color.WHITE);
        btnProfessors.setBorder(null);
        btnProfessors.setFocusPainted(false);
        btnProfessors.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnProfessors.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnProfessors.addActionListener(this);
        titleBar.add(btnProfessors);

        btnStudents = new JButton("Students");
        btnStudents.setBackground(Color.WHITE);
        btnStudents.setBorder(null);
        btnStudents.setFocusPainted(false);
        btnStudents.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnStudents.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnStudents.addActionListener(this);
        titleBar.add(btnStudents);

        btnReports = new JButton("Reports");
        btnReports.setBackground(Color.WHITE);
        btnReports.setBorder(null);
        btnReports.setFocusPainted(false);
        btnReports.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReports.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnReports.addActionListener(this);
        titleBar.add(btnReports);

        btnSettings = new JButton("Settings");
        btnSettings.setBackground(Color.WHITE);
        btnSettings.setBorder(null);
        btnSettings.setFocusPainted(false);
        btnSettings.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSettings.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnSettings.addActionListener(this);
        titleBar.add(btnSettings);

        btnLogout = new JButton("Logout");
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setBorder(null);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnLogout.addActionListener(this);
        titleBar.add(btnLogout);

        repositionTitleBar(getWidth());
        add(titleBar);
    }

    private void repositionTitleBar(int w){
        if(closeBtn == null){
            return;
        }

        titleBar.setBounds(0, 0, w, 32);
        closeBtn.setBounds(w - 40, 8, 28, 24);

        if(btnDashboard == null){
            return;
        }

        int totalWidth = (80 * 8) + (90) + (6 * 8);
        int startX = (w - totalWidth) / 2;
        int y = 8;

        btnDashboard.setBounds(startX, y, 80, 24);
        btnUsers.setBounds(startX + 80 + 6, y, 80, 24);
        btnCourses.setBounds(startX + (80 + 6) * 2, y, 80, 24);
        btnSections.setBounds(startX + (80 + 6) * 3, y, 80, 24);
        btnProfessors.setBounds(startX + (80 + 6) * 4, y, 90, 24);
        btnStudents.setBounds(startX + (80 + 6) * 4 + 90 + 6, y, 80, 24);
        btnReports.setBounds(startX + (80 + 6) * 4 + 90 + 6 + 80 + 6, y, 80, 24);
        btnSettings.setBounds(startX + (80 + 6) * 4 + 90 + 6 + (80 + 6) * 2, y, 80, 24);
        btnLogout.setBounds(startX + (80 + 6) * 4 + 90 + 6 + (80 + 6) * 3, y, 80, 24);
    }

    @Override
    public void setBounds(int x, int y, int w, int h){
        super.setBounds(x, y, w, h);
        repositionTitleBar(w);
        if(contentContainer != null){
            contentContainer.setBounds(0, 32, w, h - 32);
        }
        if(currentPanel != null && contentContainer != null){
            currentPanel.setBounds(0, 0, contentContainer.getWidth(), contentContainer.getHeight());
        }
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
            if(currentUser != null){
                panel.setCurrentUser(currentUser);
            }
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
        if(e.getSource() == btnLogout){
            dispose();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        }
    }
}
