package panels;

import core.Course;
import core.Student;
import core.User;
import junction.ExcuseLetter;
import junction.StudentCourse;
import lookup.ExcuseStatus;
import services.ExcuseLetterServiceAdapter;
import services.StudentCourseService;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class StudentExcusePanel extends JPanel implements ActionListener {

    private JLabel lblTitle, lblCourse, lblDate, lblReason, lblDocument, lblHistory;
    private JComboBox<String> cmbCourse;
    private JTextField txtDate;
    private JTextArea txtReason;
    private JButton btnSubmit, btnBrowse;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JScrollPane tableScroll, reasonScroll;

    private final ExcuseLetterServiceAdapter excuseLetterService = new ExcuseLetterServiceAdapter();
    private final StudentCourseService studentCourseService      = new StudentCourseService();

    private Student currentStudent;
    private User currentUser;
    private String selectedFilePath;
    private final List<Course> enrolledCourses = new ArrayList<>();

    public StudentExcusePanel(){
        setLayout(null);
        setBackground(Color.WHITE);

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

        cmbCourse = new JComboBox<>();
        cmbCourse.setBounds(40, 92, 310, 36);
        cmbCourse.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbCourse.setBackground(Color.WHITE);
        cmbCourse.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbCourse.setFocusable(false);
        cmbCourse.setRenderer(new BasicComboBoxRenderer(){
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus){
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setBorder(new EmptyBorder(0, 10, 0, 0));
                return lbl;
            }
        });
        add(cmbCourse);

        lblDate = new JLabel("Absent Date (YYYY-MM-DD)");
        lblDate.setBounds(370, 70, 200, 20);
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDate.setForeground(new Color(80, 80, 80));
        add(lblDate);

        txtDate = new JTextField(LocalDate.now().toString());
        txtDate.setBounds(370, 92, 200, 36);
        txtDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDate.setForeground(new Color(60, 60, 60));
        txtDate.setBackground(Color.WHITE);
        txtDate.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
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
        reasonScroll.setBounds(40, 167, 530, 100);
        reasonScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        add(reasonScroll);

        lblDocument = new JLabel("Supporting Document (Optional)");
        lblDocument.setBounds(40, 280, 300, 20);
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

        lblHistory = new JLabel("Submission History");
        lblHistory.setBounds(40, 420, 300, 30);
        lblHistory.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHistory.setForeground(new Color(60, 60, 60));
        add(lblHistory);

        String[] columns = {"Absent Date", "Course", "Reason", "Status", "Reviewed By"};
        tableModel = new DefaultTableModel(columns, 0){
            @Override public boolean isCellEditable(int row, int column){ return false; }
        };

        historyTable = new JTable(tableModel);
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        historyTable.setRowHeight(32);
        historyTable.setShowGrid(false);
        historyTable.setIntercellSpacing(new Dimension(0, 0));
        historyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        historyTable.getTableHeader().setBackground(new Color(250, 250, 250));
        historyTable.getTableHeader().setForeground(new Color(60, 60, 60));
        historyTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < historyTable.getColumnCount(); i++){
            historyTable.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        tableScroll = new JScrollPane(historyTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        tableScroll.getViewport().setBackground(Color.WHITE);
        add(tableScroll);
    }

    public void setCurrentStudent(Student student){
        this.currentStudent = student;
        loadEnrolledCourses();
    }

    public void setCurrentUser(User user){
        this.currentUser = user;
    }

    private void loadEnrolledCourses(){
        cmbCourse.removeAllItems();
        enrolledCourses.clear();
        if(currentStudent == null) return;
        cmbCourse.addItem("Select Course");
        List<StudentCourse> enrollments = studentCourseService.getCoursesByStudent(currentStudent.studentId());
        for(StudentCourse sc : enrollments){
            cmbCourse.addItem(sc.course().courseCode() + " - " + sc.course().courseName());
            enrolledCourses.add(sc.course());
        }
    }

    public void loadExcuseHistory(int studentId){
        tableModel.setRowCount(0);
        List<ExcuseLetter> excuses = excuseLetterService.getExcuseLettersByStudent(studentId);
        for(ExcuseLetter e : excuses){
            String reason = e.reason().length() > 40 ? e.reason().substring(0, 40) + "..." : e.reason();
            tableModel.addRow(new Object[]{
                e.absentDate().toString(),
                e.course().courseCode(),
                reason,
                e.status().getExcuseStatusName(),
                e.reviewedBy() != null ? e.reviewedBy().userName() : "Pending"
            });
        }
    }

    private void submitExcuseLetter(){
        if(currentStudent == null || currentUser == null){
            JOptionPane.showMessageDialog(this, "Student not loaded properly.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(cmbCourse.getSelectedIndex() <= 0){
            JOptionPane.showMessageDialog(this, "Please select a course.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dateStr = txtDate.getText().trim();
        String reason  = txtReason.getText().trim();

        if(dateStr.isEmpty() || reason.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try{
            LocalDate absentDate = LocalDate.parse(dateStr);
            Course selectedCourse = enrolledCourses.get(cmbCourse.getSelectedIndex() - 1);

            ExcuseLetter letter = excuseLetterService.submitExcuseLetter(
                currentStudent, selectedCourse, absentDate, reason,
                selectedFilePath, ExcuseStatus.PENDING, LocalDateTime.now());

            if(letter != null){
                JOptionPane.showMessageDialog(this, "Excuse letter submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                cmbCourse.setSelectedIndex(0);
                txtDate.setText(LocalDate.now().toString());
                txtReason.setText("");
                selectedFilePath = null;
                lblDocument.setText("Supporting Document (Optional)");
                loadExcuseHistory(currentStudent.studentId());
            }
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x, y, width, height);
        if(width > 0 && height > 0 && tableScroll != null){
            tableScroll.setBounds(40, 460, width - 80, height - 480);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnBrowse){
            JFileChooser chooser = new JFileChooser();
            if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                selectedFilePath = chooser.getSelectedFile().getAbsolutePath();
                lblDocument.setText("Selected: " + chooser.getSelectedFile().getName());
            }
        } else if(e.getSource() == btnSubmit){
            submitExcuseLetter();
        }
    }
}
