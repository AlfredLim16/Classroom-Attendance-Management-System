package panels;

import core.Student;
import java.awt.Color;
import java.awt.Font;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import services.StudentService;

public class StudentProfilePanel extends JPanel {

    private JLabel lblTitle, lblStudentNumber, lblName, lblProgram, lblYearLevel, lblSection;
    private JTextField txtStudentNumber, txtName, txtProgram, txtYearLevel, txtSection;
    private final StudentService studentService = new StudentService();

    public StudentProfilePanel(Student student){
        setLayout(null);
        setBackground(Color.WHITE);
        setBounds(0, 32, 1200, 700);

        lblTitle = new JLabel("My Profile");
        lblTitle.setBounds(40, 20, 300, 30);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle);

        lblStudentNumber = new JLabel("Student Number");
        lblStudentNumber.setBounds(40, 70, 200, 20);
        lblStudentNumber.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblStudentNumber.setForeground(new Color(80, 80, 80));
        add(lblStudentNumber);

        txtStudentNumber = new JTextField(student != null ? student.studentNumber() : "");
        txtStudentNumber.setBounds(40, 92, 300, 36);
        txtStudentNumber.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtStudentNumber.setForeground(new Color(60, 60, 60));
        txtStudentNumber.setBackground(Color.WHITE);
        txtStudentNumber.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        txtStudentNumber.setEditable(false);
        add(txtStudentNumber);

        lblName = new JLabel("Full Name");
        lblName.setBounds(40, 145, 200, 20);
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblName.setForeground(new Color(80, 80, 80));
        add(lblName);

        txtName = new JTextField(student != null ? student.getFullName() : "");
        txtName.setBounds(40, 167, 300, 36);
        txtName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtName.setForeground(new Color(60, 60, 60));
        txtName.setBackground(Color.WHITE);
        txtName.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        txtName.setEditable(false);
        add(txtName);

        lblProgram = new JLabel("Program");
        lblProgram.setBounds(40, 220, 200, 20);
        lblProgram.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblProgram.setForeground(new Color(80, 80, 80));
        add(lblProgram);

        txtProgram = new JTextField(student != null && student.program() != null ? student.program().programName() : "");
        txtProgram.setBounds(40, 242, 300, 36);
        txtProgram.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtProgram.setForeground(new Color(60, 60, 60));
        txtProgram.setBackground(Color.WHITE);
        txtProgram.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        txtProgram.setEditable(false);
        add(txtProgram);

        lblYearLevel = new JLabel("Year Level");
        lblYearLevel.setBounds(40, 295, 200, 20);
        lblYearLevel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblYearLevel.setForeground(new Color(80, 80, 80));
        add(lblYearLevel);

        txtYearLevel = new JTextField(student != null && student.yearLevel() != null ? student.yearLevel().getYearLevelName() : "");
        txtYearLevel.setBounds(40, 317, 300, 36);
        txtYearLevel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtYearLevel.setForeground(new Color(60, 60, 60));
        txtYearLevel.setBackground(Color.WHITE);
        txtYearLevel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        txtYearLevel.setEditable(false);
        add(txtYearLevel);

        lblSection = new JLabel("Section");
        lblSection.setBounds(40, 370, 200, 20);
        lblSection.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSection.setForeground(new Color(80, 80, 80));
        add(lblSection);

        txtSection = new JTextField(student != null && student.section() != null ? student.section().sectionCode() : "");
        txtSection.setBounds(40, 392, 300, 36);
        txtSection.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSection.setForeground(new Color(60, 60, 60));
        txtSection.setBackground(Color.WHITE);
        txtSection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        txtSection.setEditable(false);
        add(txtSection);
    }

    public void loadStudentData(Student student) {
        txtStudentNumber.setText(student.studentNumber());
        txtName.setText(student.getFullName());
        txtProgram.setText(student.program() != null ? student.program().programName() : "");
        txtYearLevel.setText(student.yearLevel() != null ? student.yearLevel().getYearLevelName() : "");
        txtSection.setText(student.section() != null ? student.section().sectionCode() : "");
    }

    public void loadStudentById(int studentId) {
        Optional<Student> opt = new StudentService().getStudentById(studentId);
        opt.ifPresent(this::loadStudentData);
    }
}
