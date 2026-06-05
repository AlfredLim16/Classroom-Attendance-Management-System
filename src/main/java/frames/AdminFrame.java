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

    private static final int TITLEBAR_H = 32;
    private static final int NAV_BTN_W = 80;
    private static final int NAV_BTN_H = 24;
    private static final int NAV_BTN_GAP = 6;

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
        titleBar.setBounds(0, 0, getWidth(), TITLEBAR_H);
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
        titleLabel.setBounds(40, 0, 200, TITLEBAR_H + 8);
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

        btnDashboard = navButton("Dashboard");
        btnUsers = navButton("Users");
        btnCourses = navButton("Courses");
        btnSections = navButton("Sections");
        btnProfessors = navButton("Professors");
        btnStudents = navButton("Students");
        btnReports = navButton("Reports");
        btnSettings = navButton("Settings");
        btnLogout = navButton("Logout");

        titleBar.add(btnDashboard);
        titleBar.add(btnUsers);
        titleBar.add(btnCourses);
        titleBar.add(btnSections);
        titleBar.add(btnProfessors);
        titleBar.add(btnStudents);
        titleBar.add(btnReports);
        titleBar.add(btnSettings);
        titleBar.add(btnLogout);

        repositionTitleBar(getWidth());
        add(titleBar);
    }

    private void repositionTitleBar(int w){
        if(closeBtn == null){
            return;
        }

        closeBtn.setBounds(w - 40, 8, 28, 24);

        if(titleBar != null){
            titleBar.setBounds(0, 0, w, TITLEBAR_H);
        }

        if(btnDashboard == null){
            return;
        }

        int profW = NAV_BTN_W + 10;
        int totalW = NAV_BTN_W * 8 + profW + NAV_BTN_GAP * 8;
        int startX = (w - totalW) / 2;
        int y = 8;

        int x = startX;
        btnDashboard.setBounds(x, y, NAV_BTN_W, NAV_BTN_H);
        x += NAV_BTN_W + NAV_BTN_GAP;
        btnUsers.setBounds(x, y, NAV_BTN_W, NAV_BTN_H);
        x += NAV_BTN_W + NAV_BTN_GAP;
        btnCourses.setBounds(x, y, NAV_BTN_W, NAV_BTN_H);
        x += NAV_BTN_W + NAV_BTN_GAP;
        btnSections.setBounds(x, y, NAV_BTN_W, NAV_BTN_H);
        x += NAV_BTN_W + NAV_BTN_GAP;
        btnProfessors.setBounds(x, y, profW, NAV_BTN_H);
        x += profW + NAV_BTN_GAP;
        btnStudents.setBounds(x, y, NAV_BTN_W, NAV_BTN_H);
        x += NAV_BTN_W + NAV_BTN_GAP;
        btnReports.setBounds(x, y, NAV_BTN_W, NAV_BTN_H);
        x += NAV_BTN_W + NAV_BTN_GAP;
        btnSettings.setBounds(x, y, NAV_BTN_W, NAV_BTN_H);
        x += NAV_BTN_W + NAV_BTN_GAP;
        btnLogout.setBounds(x, y, NAV_BTN_W, NAV_BTN_H);
    }

    @Override
    public void setBounds(int x, int y, int w, int h){
        super.setBounds(x, y, w, h);
        repositionTitleBar(w);
        if(contentContainer != null){
            contentContainer.setBounds(0, TITLEBAR_H, w, h - TITLEBAR_H);
        }
        if(currentPanel != null && contentContainer != null){
            currentPanel.setBounds(0, 0, contentContainer.getWidth(), contentContainer.getHeight());
        }
    }

    private JButton navButton(String text){
        JButton btn = new JButton(text);
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
