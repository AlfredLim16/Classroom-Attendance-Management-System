package frames;

import core.Student;
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
import panels.StudentAttendancePanel;
import panels.StudentExcusePanel;
import panels.StudentProfilePanel;
import panels.StudentSchedulePanel;
import services.StudentService;

public class StudentFrame extends JFrame implements ActionListener {

    private JLabel titleLabel;
    private JButton closeBtn;
    private JButton btnProfile, btnAttendance, btnExcused, btnSchedule, btnLogout;
    private JPanel currentPanel;
    private JPanel contentContainer;
    private JPanel titleBar;

    private Student currentStudent;
    private User currentUser;
    private final StudentService studentService = new StudentService();

    public StudentFrame(){
        ConfigureWindow();
        TitleBar();
        ContentContainer();
        setVisible(true);
    }

    public void setCurrentUser(User user){
        this.currentUser = user;
        if(currentUser != null){
            studentService.getStudentByUserId(currentUser.userId()).ifPresent(s -> {
                this.currentStudent = s;
                titleLabel.setText("Orange - Student | " + s.firstName() + " " + s.lastName());
            });
        }
        showPanel(buildProfilePanel());
    }

    private StudentProfilePanel buildProfilePanel(){
        return new StudentProfilePanel(currentStudent);
    }

    private StudentAttendancePanel buildAttendancePanel(){
        StudentAttendancePanel panel = new StudentAttendancePanel();
        if(currentStudent != null){
            panel.loadAttendanceForStudent(currentStudent.studentId());
        }
        return panel;
    }

    private StudentExcusePanel buildExcusePanel(){
        StudentExcusePanel panel = new StudentExcusePanel();
        if(currentStudent != null){
            panel.setCurrentStudent(currentStudent);
            panel.setCurrentUser(currentUser);
            panel.loadExcuseHistory(currentStudent.studentId());
        }
        return panel;
    }

    private StudentSchedulePanel buildSchedulePanel(){
        StudentSchedulePanel panel = new StudentSchedulePanel();
        if(currentStudent != null){
            panel.loadScheduleForStudent(currentStudent);
        }
        return panel;
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

        titleLabel = new JLabel("Orange - Student");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(60, 60, 60));
        titleLabel.setBounds(40, 0, 300, 40);
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

        btnProfile = navButton("Profile");
        btnAttendance = navButton("Attendance");
        btnExcused = navButton("Excuse Letter");
        btnSchedule = navButton("Schedule");
        btnLogout = navButton("Logout");

        titleBar.add(btnProfile);
        titleBar.add(btnAttendance);
        titleBar.add(btnExcused);
        titleBar.add(btnSchedule);
        titleBar.add(btnLogout);

        repositionTitleBar(getWidth());
        add(titleBar);
    }

    private JButton navButton(String text){
        JButton b = new JButton(text);
        b.setBackground(Color.WHITE);
        b.setBorder(null);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        b.addActionListener(this);
        return b;
    }

    private void repositionTitleBar(int w){
        if(closeBtn == null){
            return;
        }
        titleBar.setBounds(0, 0, w, 32);
        closeBtn.setBounds(w - 40, 8, 28, 24);
        if(btnProfile == null){
            return;
        }

        int[] widths = {70, 90, 110, 80, 70};
        int gap = 10;
        int total = 0;
        for(int ww : widths){
            total += ww;
        }
        total += gap * (widths.length - 1);

        int x = (w - total) / 2;
        int y = 8;
        JButton[] btns = {btnProfile, btnAttendance, btnExcused, btnSchedule, btnLogout};
        for(int i = 0; i < btns.length; i++){
            btns[i].setBounds(x, y, widths[i], 24);
            x += widths[i] + gap;
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
        if(e.getSource() == closeBtn){
            System.exit(0);
        }else if(e.getSource() == btnProfile){
            showPanel(buildProfilePanel());
        }else if(e.getSource() == btnAttendance){
            showPanel(buildAttendancePanel());
        }else if(e.getSource() == btnExcused){
            showPanel(buildExcusePanel());
        }else if(e.getSource() == btnSchedule){
            showPanel(buildSchedulePanel());
        }else if(e.getSource() == btnLogout){
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
