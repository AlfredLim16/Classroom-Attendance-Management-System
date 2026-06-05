package panels;

import core.Course;
import core.Student;
import core.User;
import junction.ExcuseLetter;
import lookup.ExcuseStatus;
import services.CourseService;
import services.ExcuseLetterServiceAdapter;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class StudentExcusePanel extends JPanel implements ActionListener {

   private JLabel lblTitle, lblCourse, lblDate, lblReason, lblDocument;
    private JTextField txtCourse, txtDate;
    private JTextArea txtReason;
    private JButton btnSubmit, btnBrowse;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JScrollPane tableScroll, reasonScroll;
    private final ExcuseLetterServiceAdapter excuseLetterService = new ExcuseLetterServiceAdapter();
    private final CourseService courseService = new CourseService();
    private Student currentStudent;
    private User currentUser;
    private String selectedFilePath;

    public StudentExcusePanel(){
        setLayout(null);
        setBackground(Color.WHITE);
        setBounds(0, 32, 1200, 700);

        lblTitle = new JLabel("Excuse Letters");
        lblTitle.setBounds(40, 20, 300, 30);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle);

        lblCourse = new JLabel("Course");
        lblCourse.setBounds(40, 70, 200, 20);
        lblCourse.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCourse.setForeground(new Color(80, 80, 80));
        add(lblCourse);

        txtCourse = new JTextField();
        txtCourse.setBounds(40, 92, 300, 36);
        txtCourse.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCourse.setForeground(new Color(60, 60, 60));
        txtCourse.setBackground(Color.WHITE);
        txtCourse.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        add(txtCourse);

        lblDate = new JLabel("Absent Date (YYYY-MM-DD)");
        lblDate.setBounds(360, 70, 200, 20);
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDate.setForeground(new Color(80, 80, 80));
        add(lblDate);

        txtDate = new JTextField();
        txtDate.setBounds(360, 92, 200, 36);
        txtDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDate.setForeground(new Color(60, 60, 60));
        txtDate.setBackground(Color.WHITE);
        txtDate.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        add(txtDate);

        lblReason = new JLabel("Reason");
        lblReason.setBounds(40, 145, 200, 20);
        lblReason.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblReason.setForeground(new Color(80, 80, 80));
        add(lblReason);

        txtReason = new JTextArea();
        txtReason.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtReason.setLineWrap(true);
        txtReason.setWrapStyleWord(true);
        txtReason.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        reasonScroll = new JScrollPane(txtReason);
        reasonScroll.setBounds(40, 167, 520, 100);
        reasonScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        add(reasonScroll);

        lblDocument = new JLabel("Supporting Document (Optional)");
        lblDocument.setBounds(40, 280, 250, 20);
        lblDocument.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDocument.setForeground(new Color(80, 80, 80));
        add(lblDocument);

        btnBrowse = new JButton("Browse File...");
        btnBrowse.setBounds(40, 302, 140, 36);
        btnBrowse.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnBrowse.setForeground(new Color(100, 100, 100));
        btnBrowse.setBackground(Color.WHITE);
        btnBrowse.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btnBrowse.setFocusPainted(false);
        btnBrowse.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBrowse.addActionListener(this);
        add(btnBrowse);

        btnSubmit = new JButton("Submit Excuse Letter");
        btnSubmit.setBounds(40, 360, 200, 40);
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setBackground(new Color(255, 140, 0));
        btnSubmit.setBorder(null);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubmit.addActionListener(this);
        add(btnSubmit);

        JLabel lblHistory = new JLabel("Submission History");
        lblHistory.setBounds(40, 420, 300, 30);
        lblHistory.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHistory.setForeground(new Color(60, 60, 60));
        add(lblHistory);

        String[] columns = {"Date", "Course", "Reason", "Status", "Reviewed By"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        historyTable = new JTable(tableModel);
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        historyTable.setRowHeight(32);
        historyTable.setShowGrid(false);
        historyTable.setIntercellSpacing(new java.awt.Dimension(0, 0));
        historyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        historyTable.getTableHeader().setBackground(new Color(250, 250, 250));
        historyTable.getTableHeader().setForeground(new Color(60, 60, 60));
        historyTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        tableScroll = new JScrollPane(historyTable);
        tableScroll.setBounds(40, 460, 800, 200);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        tableScroll.getViewport().setBackground(Color.WHITE);
        add(tableScroll);
    }

    public void loadExcuseHistory(int studentId) {
        tableModel.setRowCount(0);
        List<ExcuseLetter> excuses = excuseLetterService.getExcuseLettersByStudent(studentId);
        for (ExcuseLetter e : excuses) {
            String date = e.absentDate().toString();
            String course = e.course().courseCode();
            String reason = e.reason().length() > 30 ? e.reason().substring(0, 30) + "..." : e.reason();
            String status = e.status().getExcuseStatusName();
            String reviewedBy = e.reviewedBy() != null ? e.reviewedBy().userName() : "Pending";
            addHistoryRow(date, course, reason, status, reviewedBy);
        }
    }

    public void addHistoryRow(String date, String course, String reason, String status, String reviewedBy){
        tableModel.addRow(new Object[]{date, course, reason, status, reviewedBy});
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnBrowse){
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFilePath = fileChooser.getSelectedFile().getAbsolutePath();
                lblDocument.setText("Selected: " + fileChooser.getSelectedFile().getName());
            }
        }
        if(e.getSource() == btnSubmit){
            submitExcuseLetter();
        }
    }

    private void submitExcuseLetter() {
        if (currentStudent == null || currentUser == null) {
            JOptionPane.showMessageDialog(this, "Student not loaded properly", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String courseCode = txtCourse.getText().trim();
        String dateStr = txtDate.getText().trim();
        String reason = txtReason.getText().trim();

        if (courseCode.isEmpty() || dateStr.isEmpty() || reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate absentDate = LocalDate.parse(dateStr);
            List<Course> courses = courseService.getAllCourses();
            Course selectedCourse = null;
            for (Course c : courses) {
                if (c.courseCode().equalsIgnoreCase(courseCode)) {
                    selectedCourse = c;
                    break;
                }
            }

            if (selectedCourse == null) {
                JOptionPane.showMessageDialog(this, "Course not found: " + courseCode, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ExcuseLetter letter = excuseLetterService.submitExcuseLetter(
                currentStudent,
                selectedCourse,
                absentDate,
                reason,
                selectedFilePath,
                ExcuseStatus.PENDING,
                LocalDateTime.now()
            );

            if (letter != null) {
                JOptionPane.showMessageDialog(this, "Excuse letter submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                txtCourse.setText("");
                txtDate.setText("");
                txtReason.setText("");
                selectedFilePath = null;
                lblDocument.setText("Supporting Document (Optional)");
                loadExcuseHistory(currentStudent.studentId());
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JTextField getCourseField(){
        return txtCourse;
    }
    public JTextField getDateField(){
        return txtDate;
    }
    public JTextArea getReasonArea(){
        return txtReason;
    }

    public void setCurrentStudent(Student student){
        this.currentStudent = student;
    }

    public void setCurrentUser(User user){
        this.currentUser = user;
    }
}
