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
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import user.Role;
import user.User;
import user.UserService;

public class LoginFrame extends JFrame implements ActionListener {

    private JLabel titleLabel, userLabel, passLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheck;
    private JButton closeBtn, loginBtn, registerBtn;
    private final UserService userService = new UserService();

    public LoginFrame(){
        ConfigureWindow();
        TitleBar();
        Components();
    }

    private void ConfigureWindow(){
        setUndecorated(true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.5);
        int height = (int) (screenSize.height * 0.70);
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210), 1));
        setVisible(true);
    }

    private void TitleBar(){
        JPanel titleBar = new JPanel();
        titleBar.setBounds(0, 0, getWidth(), 32);
        titleBar.setBackground(Color.WHITE);
        titleBar.setLayout(null);

        ImageIcon logoRaw = new ImageIcon(getClass().getResource("/icons/favicon.png"));
        Image logoScaled = logoRaw.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
        JLabel icon = new JLabel(new ImageIcon(logoScaled));
        icon.setBounds(12, 10, 20, 20);
        titleBar.add(icon);

        titleLabel = new JLabel("Orange - Login");
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

        add(titleBar);
    }

    private void Components(){
        userLabel = new JLabel("Username");
        userLabel.setBounds(250, 150, 300, 20);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLabel.setForeground(new Color(80, 80, 80));
        add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(250, 175, 300, 40);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setForeground(new Color(60, 60, 60));
        usernameField.setBackground(Color.WHITE);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        usernameField.setCaretColor(new Color(255, 140, 0));
        add(usernameField);

        passLabel = new JLabel("Password");
        passLabel.setBounds(250, 235, 300, 20);
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passLabel.setForeground(new Color(80, 80, 80));
        add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(250, 260, 300, 40);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setForeground(new Color(60, 60, 60));
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        passwordField.setCaretColor(new Color(255, 140, 0));
        add(passwordField);

        showPasswordCheck = new JCheckBox("Show Password");
        showPasswordCheck.setBounds(250, 305, 300, 20);
        showPasswordCheck.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        showPasswordCheck.setForeground(new Color(100, 100, 100));
        showPasswordCheck.setBackground(Color.WHITE);
        showPasswordCheck.setFocusPainted(false);
        showPasswordCheck.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showPasswordCheck.addActionListener(this);
        add(showPasswordCheck);

        loginBtn = new JButton("Log In");
        loginBtn.setBounds(250, 350, 300, 40);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBackground(new Color(255, 140, 0));
        loginBtn.setBorder(null);
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(this);
        add(loginBtn);

        registerBtn = new JButton("Create Account");
        registerBtn.setBounds(250, 402, 300, 40);
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        registerBtn.setForeground(new Color(100, 100, 100));
        registerBtn.setBackground(Color.WHITE);
        registerBtn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        registerBtn.setFocusPainted(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.addActionListener(this);
        add(registerBtn);

        revalidate();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == closeBtn){
            System.exit(0);
            dispose();
        }

        if(e.getSource() == showPasswordCheck){
            if(showPasswordCheck.isSelected()){
                passwordField.setEchoChar((char) 0);
            }else{
                passwordField.setEchoChar('\u2022');
            }
        }

        if(e.getSource() == loginBtn){
            handleLogin();
        }
    }

    private void handleLogin(){
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if(username.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        var userOpt = userService.login(username, password);

        if(userOpt.isPresent()){
            User user = userOpt.get();
            Role role = user.role();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run(){
                    switch(role){
                        case STUDENT -> {
                            StudentFrame studentFrame = new StudentFrame();
                            studentFrame.setCurrentUser(user);

                        }
                        case PROFESSOR -> {
                            ProfessorFrame professorFrame = new ProfessorFrame();
                            professorFrame.setCurrentUser(user);
                        }
                        case ADMIN -> {
                            dispose();
                            AdminFrame adminFrame = new AdminFrame();
                            adminFrame.setCurrentUser(user);
                        }
                        case SECRETARY -> {
                            SecretaryFrame secretaryFrame = new SecretaryFrame();
                            secretaryFrame.setCurrentUser(user);
                        }
                        default ->
                            JOptionPane.showMessageDialog(LoginFrame.this, "Unknown role: " + role, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            dispose();
        }else{
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }

}
