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
import user.Student;
import user.StudentService;
import user.User;
import user.UserService;

public class StudentFrame extends JFrame implements ActionListener {

    private JLabel titleLabel;
    private JButton closeBtn;
    private JButton btnProfile, btnAttendance, btnExcused, btnSchedule;
    private JPanel currentPanel;
    private JPanel contentContainer;
    private Student currentStudent;
    private User currentUser;
    private final StudentService studentService = new StudentService();
    private final UserService userService = new UserService();

    private StudentProfilePanel profilePanel;
    private StudentAttendancePanel attendancePanel;
    private StudentExcusePanel excusePanel;
    private StudentSchedulePanel schedulePanel;

    public StudentFrame(){
        ConfigureWindow();
        TitleBar();
        ContentContainer();
        setVisible(true);
    }

    public void setCurrentUser(User user){
        this.currentUser = user;
        if(currentUser != null){
            var studentOpt = studentService.getStudentById(currentUser.userId());
            if(studentOpt.isPresent()){
                this.currentStudent = studentOpt.get();
                titleLabel.setText("Orange - Student | " + currentStudent.firstName() + " " + currentStudent.lastName());
            }
        }

        profilePanel = new StudentProfilePanel(currentStudent);
        attendancePanel = new StudentAttendancePanel();
        excusePanel = new StudentExcusePanel();
        schedulePanel = new StudentSchedulePanel();

        if(currentStudent != null){
            attendancePanel.loadAttendanceForStudent(currentStudent.studentId());
            excusePanel.setCurrentStudent(currentStudent);
            excusePanel.setCurrentUser(currentUser);
            excusePanel.loadExcuseHistory(currentStudent.studentId());
        }

        showPanel(profilePanel);
    }

    public User getCurrentUser(){
        return currentUser;
    }

    public Student getCurrentStudent(){
        return currentStudent;
    }

    public StudentService getStudentService(){
        return studentService;
    }

    public UserService getUserService(){
        return userService;
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

        titleLabel = new JLabel("Orange - Student");
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
        int gap = 10;
        int totalWidth = (buttonWidth * 4) + (gap * 3);
        int startX = (getWidth() - totalWidth) / 2;
        int buttonY = 8;

        btnProfile = new JButton("Profile");
        btnProfile.setBounds(startX, buttonY, buttonWidth, buttonHeight);
        btnProfile.setBackground(Color.WHITE);
        btnProfile.setBorder(null);
        btnProfile.setFocusPainted(false);
        btnProfile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnProfile.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnProfile.addActionListener(this);
        titleBar.add(btnProfile);

        btnAttendance = new JButton("Attendance");
        btnAttendance.setBounds(startX + buttonWidth + gap, buttonY, buttonWidth, buttonHeight);
        btnAttendance.setBackground(Color.WHITE);
        btnAttendance.setBorder(null);
        btnAttendance.setFocusPainted(false);
        btnAttendance.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAttendance.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnAttendance.addActionListener(this);
        titleBar.add(btnAttendance);

        btnExcused = new JButton("Excuse Letter");
        btnExcused.setBounds(startX + (buttonWidth + gap) * 2, buttonY, buttonWidth, buttonHeight);
        btnExcused.setBackground(Color.WHITE);
        btnExcused.setBorder(null);
        btnExcused.setFocusPainted(false);
        btnExcused.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExcused.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnExcused.addActionListener(this);
        titleBar.add(btnExcused);

        btnSchedule = new JButton("Schedule");
        btnSchedule.setBounds(startX + (buttonWidth + gap) * 3, buttonY, buttonWidth, buttonHeight);
        btnSchedule.setBackground(Color.WHITE);
        btnSchedule.setBorder(null);
        btnSchedule.setFocusPainted(false);
        btnSchedule.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSchedule.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnSchedule.addActionListener(this);
        titleBar.add(btnSchedule);

        add(titleBar);
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
        if(e.getSource() == btnProfile){
            showPanel(profilePanel);
        }
        if(e.getSource() == btnAttendance){
            showPanel(attendancePanel);
        }
        if(e.getSource() == btnExcused){
            showPanel(excusePanel);
        }
        if(e.getSource() == btnSchedule){
            showPanel(schedulePanel);
        }
    }
}