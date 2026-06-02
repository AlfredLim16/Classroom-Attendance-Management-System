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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import user.Professor;
import user.ProfessorService;
import user.User;

public class ProfessorFrame extends JFrame implements ActionListener {

    private JLabel titleLabel;
    private JButton closeBtn;
    private JButton btnDashboard, btnSections, btnAttendance, btnExcuses, btnMissedQuiz, btnSchedule, btnPolicy;
    private JPanel currentPanel;
    private JPanel contentContainer;

    private User currentUser;
    private Professor professor;
    private final ProfessorService professorService = new ProfessorService();

    public ProfessorFrame(){
        ConfigureWindow();
        TitleBar();
        ContentContainer();
        showPanel(new ProfessorDashboardPanel());
        setVisible(true);
    }

    public void setCurrentUser(User user){
        this.currentUser = user;
        professorService.getProfessorByUserId(user.userId()).ifPresentOrElse(
            prof -> {
                this.professor = prof;
                if(currentPanel instanceof ProfessorDashboardPanel panel){
                    panel.setProfessor(professor);
                }
            },
            () -> {
                JOptionPane.showMessageDialog(this,
                    "No professor record found for user: " + user.userName(),
                    "Professor Lookup Failed", JOptionPane.ERROR_MESSAGE);
            }
        );
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

        titleLabel = new JLabel("Orange - Professor");
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

        int buttonWidth = 90;
        int buttonHeight = 24;
        int gap = 8;
        int totalWidth = (buttonWidth * 7) + (gap * 6);
        int startX = (getWidth() - totalWidth) / 2;
        int buttonY = 8;

        btnDashboard = new JButton("Dashboard");
        btnDashboard.setBounds(startX, buttonY, buttonWidth, buttonHeight);
        btnDashboard.setBackground(Color.WHITE);
        btnDashboard.setBorder(null);
        btnDashboard.setFocusPainted(false);
        btnDashboard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDashboard.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnDashboard.addActionListener(this);
        titleBar.add(btnDashboard);

        btnSections = new JButton("Sections");
        btnSections.setBounds(startX + buttonWidth + gap, buttonY, buttonWidth, buttonHeight);
        btnSections.setBackground(Color.WHITE);
        btnSections.setBorder(null);
        btnSections.setFocusPainted(false);
        btnSections.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSections.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnSections.addActionListener(this);
        titleBar.add(btnSections);

        btnAttendance = new JButton("Attendance");
        btnAttendance.setBounds(startX + (buttonWidth + gap) * 2, buttonY, buttonWidth, buttonHeight);
        btnAttendance.setBackground(Color.WHITE);
        btnAttendance.setBorder(null);
        btnAttendance.setFocusPainted(false);
        btnAttendance.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAttendance.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnAttendance.addActionListener(this);
        titleBar.add(btnAttendance);

        btnExcuses = new JButton("Excuses");
        btnExcuses.setBounds(startX + (buttonWidth + gap) * 3, buttonY, buttonWidth, buttonHeight);
        btnExcuses.setBackground(Color.WHITE);
        btnExcuses.setBorder(null);
        btnExcuses.setFocusPainted(false);
        btnExcuses.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExcuses.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnExcuses.addActionListener(this);
        titleBar.add(btnExcuses);

        btnMissedQuiz = new JButton("Missed Quiz");
        btnMissedQuiz.setBounds(startX + (buttonWidth + gap) * 4, buttonY, buttonWidth, buttonHeight);
        btnMissedQuiz.setBackground(Color.WHITE);
        btnMissedQuiz.setBorder(null);
        btnMissedQuiz.setFocusPainted(false);
        btnMissedQuiz.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMissedQuiz.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnMissedQuiz.addActionListener(this);
        titleBar.add(btnMissedQuiz);

        btnSchedule = new JButton("Schedule");
        btnSchedule.setBounds(startX + (buttonWidth + gap) * 5, buttonY, buttonWidth, buttonHeight);
        btnSchedule.setBackground(Color.WHITE);
        btnSchedule.setBorder(null);
        btnSchedule.setFocusPainted(false);
        btnSchedule.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSchedule.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnSchedule.addActionListener(this);
        titleBar.add(btnSchedule);

        btnPolicy = new JButton("Policies");
        btnPolicy.setBounds(startX + (buttonWidth + gap) * 6, buttonY, buttonWidth, buttonHeight);
        btnPolicy.setBackground(Color.WHITE);
        btnPolicy.setBorder(null);
        btnPolicy.setFocusPainted(false);
        btnPolicy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPolicy.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnPolicy.addActionListener(this);
        titleBar.add(btnPolicy);

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
        if(e.getSource() == btnDashboard){
            ProfessorDashboardPanel panel = new ProfessorDashboardPanel();
            if(professor != null) panel.setProfessor(professor);
            showPanel(panel);
        }
        if(e.getSource() == btnSections){
            ProfessorSectionsPanel panel = new ProfessorSectionsPanel();
            if(professor != null) panel.setProfessor(professor);
            showPanel(panel);
        }
        if(e.getSource() == btnAttendance){
            ProfessorAttendancePanel panel = new ProfessorAttendancePanel();
            if(professor != null) panel.setProfessor(professor);
            if(currentUser != null) panel.setCurrentUser(currentUser);
            showPanel(panel);
        }
        if(e.getSource() == btnExcuses){
            ProfessorExcusePanel panel = new ProfessorExcusePanel();
            if(professor != null) panel.setProfessor(professor);
            if(currentUser != null) panel.setCurrentUser(currentUser);
            showPanel(panel);
        }
        if(e.getSource() == btnMissedQuiz){
            ProfessorMissedQuizPanel panel = new ProfessorMissedQuizPanel();
            if(professor != null) panel.setProfessor(professor);
            if(currentUser != null) panel.setCurrentUser(currentUser);
            showPanel(panel);
        }
        if(e.getSource() == btnSchedule){
            ProfessorSchedulePanel panel = new ProfessorSchedulePanel();
            if(professor != null) panel.setProfessor(professor);
            showPanel(panel);
        }
        if(e.getSource() == btnPolicy){
            ProfessorPolicyPanel panel = new ProfessorPolicyPanel();
            if(professor != null) panel.setProfessor(professor);
            showPanel(panel);
        }
    }
}