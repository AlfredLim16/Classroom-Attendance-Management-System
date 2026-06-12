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
import panels.AdminEnrollmentsPanel;
import panels.AdminProfessorAssignmentsPanel;
import panels.AdminProfessorsPanel;
import panels.AdminProgramsPanel;
import panels.AdminReportsPanel;
import panels.AdminSectionsPanel;
import panels.AdminSemestersPanel;
import panels.AdminSettingsPanel;
import panels.AdminStudentsPanel;
import panels.AdminUsersPanel;

public class AdminFrame extends JFrame implements ActionListener {

    private JLabel titleLabel;
    private JButton closeBtn;
    private JButton btnDashboard, btnUsers, btnPrograms, btnCourses, btnSections,
                    btnProfessors, btnAssigning, btnEnrollments,
                    btnStudents, btnReports, btnSemesters, btnSettings;
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

    public void doLogout(){
        dispose();
        new LoginFrame().setVisible(true);
    }

    private void ConfigureWindow(){
        setUndecorated(true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width  = (int)(screenSize.width  * 0.9);
        int height = (int)(screenSize.height * 0.85);
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
        closeBtn.setBackground(Color.WHITE);
        closeBtn.setBorder(null);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(this);
        titleBar.add(closeBtn);

        String[] labels = {"Dashboard","Users","Programs","Courses","Sections",
                           "Professors","Assigning","Enrollments","Students",
                           "Reports","Semesters","Settings"};
        JButton[] btns = new JButton[labels.length];
        for(int i = 0; i < labels.length; i++){
            btns[i] = new JButton(labels[i]);
            btns[i].setBackground(Color.WHITE);
            btns[i].setBorder(null);
            btns[i].setFocusPainted(false);
            btns[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            btns[i].setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btns[i].addActionListener(this);
            titleBar.add(btns[i]);
        }
        btnDashboard  = btns[0];
        btnUsers      = btns[1];
        btnPrograms   = btns[2];
        btnCourses    = btns[3];
        btnSections   = btns[4];
        btnProfessors = btns[5];
        btnAssigning  = btns[6];
        btnEnrollments= btns[7];
        btnStudents   = btns[8];
        btnReports    = btns[9];
        btnSemesters  = btns[10];
        btnSettings   = btns[11];

        repositionTitleBar(getWidth());
        add(titleBar);
    }

    private void repositionTitleBar(int w){
        if(closeBtn == null) return;
        titleBar.setBounds(0, 0, w, 32);
        closeBtn.setBounds(w - 40, 8, 28, 24);
        if(btnDashboard == null) return;

        int g = 6;
        int[] widths = {78, 60, 82, 72, 74, 90, 78, 95, 72, 72, 84, 70};
        int total = 0;
        for(int ww : widths) total += ww;
        total += g * (widths.length - 1);

        int x = (w - total) / 2;
        int y = 8;
        JButton[] btns = {btnDashboard, btnUsers, btnPrograms, btnCourses, btnSections,
                          btnProfessors, btnAssigning, btnEnrollments, btnStudents,
                          btnReports, btnSemesters, btnSettings};
        for(int i = 0; i < btns.length; i++){
            btns[i].setBounds(x, y, widths[i], 24);
            x += widths[i] + g;
        }
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
        Object src = e.getSource();
        if(src == closeBtn){
            System.exit(0);
        } else if(src == btnDashboard){
            AdminDashboardPanel panel = new AdminDashboardPanel();
            if(currentUser != null) panel.setCurrentUser(currentUser);
            showPanel(panel);
        } else if(src == btnUsers){
            showPanel(new AdminUsersPanel());
        } else if(src == btnPrograms){
            showPanel(new AdminProgramsPanel());
        } else if(src == btnCourses){
            showPanel(new AdminCoursesPanel());
        } else if(src == btnSections){
            showPanel(new AdminSectionsPanel());
        } else if(src == btnProfessors){
            showPanel(new AdminProfessorsPanel());
        } else if(src == btnAssigning){
            showPanel(new AdminProfessorAssignmentsPanel());
        } else if(src == btnEnrollments){
            showPanel(new AdminEnrollmentsPanel());
        } else if(src == btnStudents){
            showPanel(new AdminStudentsPanel());
        } else if(src == btnReports){
            showPanel(new AdminReportsPanel());
        } else if(src == btnSemesters){
            showPanel(new AdminSemestersPanel());
        } else if(src == btnSettings){
            AdminSettingsPanel panel = new AdminSettingsPanel(this);
            showPanel(panel);
        }
    }
}
