package panels;

import core.Course;
import core.Secretary;
import core.Section;
import core.Semester;
import core.Student;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import junction.StudentCourse;
import services.CourseService;
import services.SecretaryService;
import services.SectionService;
import services.SemesterService;
import services.StudentCourseService;
import services.StudentService;

public class AdminEnrollmentsPanel extends JPanel implements ActionListener {

    private static final String CARD_ENROLLMENT = "ENROLLMENT";
    private static final String CARD_SECRETARY  = "SECRETARY";

    private JPanel tabBar;
    private JButton btnTabEnroll, btnTabSecretary;
    private JPanel cardHost;

    private JPanel enrollCard;
    private JLabel lblEnrTitle, lblEnrSub;
    private JSeparator enrSep;
    private JComboBox<String> cmbEnrStudent;
    private JButton btnEnrLoad, btnEnrEnroll, btnEnrUnenroll;
    private JTable enrTable;
    private DefaultTableModel enrModel;
    private JScrollPane enrScroll;

    private JPanel secretaryCard;
    private JLabel lblSecTitle, lblSecSub;
    private JSeparator secSep;
    private JComboBox<String> cmbSecSection;
    private JButton btnSecLoad, btnSecAssign, btnSecRemove;
    private JTable secTable;
    private DefaultTableModel secModel;
    private JScrollPane secScroll;

    private final StudentService studentService         = new StudentService();
    private final CourseService courseService           = new CourseService();
    private final SemesterService semesterService       = new SemesterService();
    private final SectionService sectionService         = new SectionService();
    private final StudentCourseService enrollService    = new StudentCourseService();
    private final SecretaryService secretaryService     = new SecretaryService();

    private final Map<Integer, Integer> enrStudentCmbMap  = new HashMap<>();
    private final Map<Integer, Integer> secSectionCmbMap  = new HashMap<>();
    private final List<StudentCourse> enrRows             = new ArrayList<>();
    private final List<Secretary>     secRows             = new ArrayList<>();

    public AdminEnrollmentsPanel(){
        setLayout(null);
        setBackground(Color.WHITE);
        buildTabBar();
        buildCardHost();
        buildEnrollmentCard();
        buildSecretaryCard();
        switchTab(CARD_ENROLLMENT);
    }

    private void buildTabBar(){
        tabBar = new JPanel(null);
        tabBar.setBackground(Color.WHITE);
        add(tabBar);

        btnTabEnroll = tabButton("Student Enrollment");
        btnTabEnroll.addActionListener(e -> switchTab(CARD_ENROLLMENT));
        tabBar.add(btnTabEnroll);

        btnTabSecretary = tabButton("Secretary Assignment");
        btnTabSecretary.addActionListener(e -> switchTab(CARD_SECRETARY));
        tabBar.add(btnTabSecretary);
    }

    private JButton tabButton(String text){
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.PLAIN, 14));
        b.setBackground(Color.WHITE);
        b.setForeground(new Color(100, 100, 100));
        b.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        return b;
    }

    private void switchTab(String card){
        ((CardLayout) cardHost.getLayout()).show(cardHost, card);
        Color active   = new Color(255, 140, 0);
        Color inactive = new Color(200, 200, 200);
        if(CARD_ENROLLMENT.equals(card)){
            btnTabEnroll.setForeground(active);
            btnTabEnroll.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, active));
            btnTabSecretary.setForeground(new Color(100, 100, 100));
            btnTabSecretary.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, inactive));
        } else {
            btnTabSecretary.setForeground(active);
            btnTabSecretary.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, active));
            btnTabEnroll.setForeground(new Color(100, 100, 100));
            btnTabEnroll.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, inactive));
        }
    }

    private void buildCardHost(){
        cardHost = new JPanel(new CardLayout());
        cardHost.setBackground(Color.WHITE);
        add(cardHost);
    }

    private void buildEnrollmentCard(){
        enrollCard = new JPanel(null);
        enrollCard.setBackground(Color.WHITE);

        lblEnrTitle = new JLabel("Student Course Enrollment");
        lblEnrTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblEnrTitle.setForeground(new Color(60, 60, 60));
        enrollCard.add(lblEnrTitle);

        lblEnrSub = new JLabel("Enroll or remove a student from courses for a semester.");
        lblEnrSub.setFont(new Font("Arial", Font.PLAIN, 14));
        enrollCard.add(lblEnrSub);

        enrSep = new JSeparator();
        enrSep.setForeground(new Color(220, 220, 220));
        enrollCard.add(enrSep);

        cmbEnrStudent = buildCombo();
        cmbEnrStudent.addItem("Select Student");
        loadStudentsIntoCombo();
        enrollCard.add(cmbEnrStudent);

        btnEnrLoad = actionButton("Load", new Color(255, 140, 0));
        btnEnrLoad.addActionListener(this);
        enrollCard.add(btnEnrLoad);

        btnEnrEnroll = actionButton("Enroll in Course", new Color(40, 167, 69));
        btnEnrEnroll.addActionListener(this);
        enrollCard.add(btnEnrEnroll);

        btnEnrUnenroll = actionButton("Remove", new Color(220, 53, 69));
        btnEnrUnenroll.addActionListener(this);
        enrollCard.add(btnEnrUnenroll);

        String[] cols = {"Course Code", "Course Name", "Program", "Semester"};
        enrModel = new DefaultTableModel(cols, 0){
            @Override public boolean isCellEditable(int r, int c){ return false; }
        };
        enrTable = buildTable(enrModel);
        enrScroll = buildScroll(enrTable);
        enrollCard.add(enrScroll);

        cardHost.add(enrollCard, CARD_ENROLLMENT);
    }

    private void buildSecretaryCard(){
        secretaryCard = new JPanel(null);
        secretaryCard.setBackground(Color.WHITE);

        lblSecTitle = new JLabel("Secretary (Class Officer) Assignment");
        lblSecTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblSecTitle.setForeground(new Color(60, 60, 60));
        secretaryCard.add(lblSecTitle);

        lblSecSub = new JLabel("Assign or remove a student as class officer for a section.");
        lblSecSub.setFont(new Font("Arial", Font.PLAIN, 14));
        secretaryCard.add(lblSecSub);

        secSep = new JSeparator();
        secSep.setForeground(new Color(220, 220, 220));
        secretaryCard.add(secSep);

        cmbSecSection = buildCombo();
        cmbSecSection.addItem("Select Section");
        loadSectionsIntoCombo();
        secretaryCard.add(cmbSecSection);

        btnSecLoad = actionButton("Load", new Color(255, 140, 0));
        btnSecLoad.addActionListener(this);
        secretaryCard.add(btnSecLoad);

        btnSecAssign = actionButton("Assign Secretary", new Color(40, 167, 69));
        btnSecAssign.addActionListener(this);
        secretaryCard.add(btnSecAssign);

        btnSecRemove = actionButton("Remove", new Color(220, 53, 69));
        btnSecRemove.addActionListener(this);
        secretaryCard.add(btnSecRemove);

        String[] cols = {"Student No.", "Name", "Section"};
        secModel = new DefaultTableModel(cols, 0){
            @Override public boolean isCellEditable(int r, int c){ return false; }
        };
        secTable = buildTable(secModel);
        secScroll = buildScroll(secTable);
        secretaryCard.add(secScroll);

        cardHost.add(secretaryCard, CARD_SECRETARY);
    }

    private void loadStudentsIntoCombo(){
        List<Student> students = studentService.getAllStudents();
        int idx = 1;
        for(Student s : students){
            cmbEnrStudent.addItem(s.studentNumber() + " - " + s.firstName() + " " + s.lastName());
            enrStudentCmbMap.put(idx++, s.studentId());
        }
    }

    private void loadSectionsIntoCombo(){
        List<Section> sections = sectionService.getAllSections();
        int idx = 1;
        for(Section s : sections){
            cmbSecSection.addItem(s.sectionCode() + " - " + s.program().programName());
            secSectionCmbMap.put(idx++, s.sectionId());
        }
    }

    private void loadEnrollments(){
        enrRows.clear();
        enrModel.setRowCount(0);
        if(cmbEnrStudent.getSelectedIndex() <= 0) return;
        int studentId = enrStudentCmbMap.get(cmbEnrStudent.getSelectedIndex());
        List<StudentCourse> list = enrollService.getCoursesByStudent(studentId);
        for(StudentCourse sc : list){
            enrRows.add(sc);
            enrModel.addRow(new Object[]{
                sc.course().courseCode(),
                sc.course().courseName(),
                sc.course().program().programName(),
                sc.semester().semesterName() + " " + sc.semester().schoolYear()
            });
        }
    }

    private void loadSecretaries(){
        secRows.clear();
        secModel.setRowCount(0);
        if(cmbSecSection.getSelectedIndex() <= 0) return;
        int sectionId = secSectionCmbMap.get(cmbSecSection.getSelectedIndex());
        List<Secretary> list = secretaryService.getSecretariesBySection(sectionId);
        for(Secretary s : list){
            secRows.add(s);
            secModel.addRow(new Object[]{
                s.student().studentNumber(),
                s.student().firstName() + " " + s.student().lastName(),
                s.section().sectionCode()
            });
        }
    }

    private void showEnrollDialog(){
        if(cmbEnrStudent.getSelectedIndex() <= 0){
            JOptionPane.showMessageDialog(this, "Please select a student first.", "No Student", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int studentId = enrStudentCmbMap.get(cmbEnrStudent.getSelectedIndex());
        Student student = studentService.getAllStudents().stream()
            .filter(s -> s.studentId() == studentId).findFirst().orElse(null);
        if(student == null) return;

        List<Course> courses = courseService.getAllCourses();
        List<Semester> semesters = semesterService.getAllSemesters();
        if(courses.isEmpty() || semesters.isEmpty()){
            JOptionPane.showMessageDialog(this, "No courses or semesters available.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Enroll Student in Course");
        dialog.setModal(true);
        dialog.setSize(480, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setResizable(false);

        JLabel accent = new JLabel("");
        accent.setBounds(0, 0, 4, 280);
        accent.setBackground(new Color(255, 140, 0));
        accent.setOpaque(true);
        dialog.add(accent);

        JLabel lblHeader = new JLabel("Enroll: " + student.firstName() + " " + student.lastName());
        lblHeader.setBounds(24, 20, 430, 25);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 15));
        lblHeader.setForeground(new Color(60, 60, 60));
        dialog.add(lblHeader);

        JSeparator sep = new JSeparator();
        sep.setBounds(24, 50, 432, 1);
        sep.setForeground(new Color(220, 220, 220));
        dialog.add(sep);

        JLabel lblCourse = new JLabel("Course");
        lblCourse.setBounds(24, 68, 100, 25);
        lblCourse.setFont(new Font("Arial", Font.BOLD, 13));
        lblCourse.setForeground(new Color(100, 100, 100));
        dialog.add(lblCourse);

        JComboBox<String> cmbCourse = new JComboBox<>();
        Map<Integer, Course> courseMap = new HashMap<>();
        int i = 0;
        for(Course c : courses){
            cmbCourse.addItem(c.courseCode() + " - " + c.courseName());
            courseMap.put(i++, c);
        }
        cmbCourse.setBounds(130, 68, 320, 32);
        cmbCourse.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbCourse.setBackground(Color.WHITE);
        cmbCourse.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbCourse.setFocusable(false);
        dialog.add(cmbCourse);

        JLabel lblSemester = new JLabel("Semester");
        lblSemester.setBounds(24, 114, 100, 25);
        lblSemester.setFont(new Font("Arial", Font.BOLD, 13));
        lblSemester.setForeground(new Color(100, 100, 100));
        dialog.add(lblSemester);

        JComboBox<String> cmbSemester = new JComboBox<>();
        Map<Integer, Semester> semesterMap = new HashMap<>();
        int j = 0;
        for(Semester s : semesters){
            cmbSemester.addItem(s.semesterName() + " " + s.schoolYear());
            semesterMap.put(j++, s);
        }
        cmbSemester.setBounds(130, 114, 320, 32);
        cmbSemester.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbSemester.setBackground(Color.WHITE);
        cmbSemester.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbSemester.setFocusable(false);
        dialog.add(cmbSemester);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBounds(260, 200, 90, 34);
        btnCancel.setFont(new Font("Arial", Font.PLAIN, 13));
        btnCancel.setForeground(new Color(100, 100, 100));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(ev -> dialog.dispose());
        dialog.add(btnCancel);

        JButton btnSave = new JButton("Enroll");
        btnSave.setBounds(360, 200, 90, 34);
        btnSave.setFont(new Font("Arial", Font.PLAIN, 13));
        btnSave.setForeground(Color.WHITE);
        btnSave.setBackground(new Color(255, 140, 0));
        btnSave.setBorder(null);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(ev -> {
            Course course = courseMap.get(cmbCourse.getSelectedIndex());
            Semester semester = semesterMap.get(cmbSemester.getSelectedIndex());
            if(course == null || semester == null) return;
            boolean ok = enrollService.enrollStudent(student, course, semester);
            if(ok){
                JOptionPane.showMessageDialog(dialog, "Student enrolled successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadEnrollments();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to enroll. Student may already be enrolled in this course for the same semester.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(btnSave);

        dialog.setVisible(true);
    }

    private void unenrollSelected(){
        int row = enrTable.getSelectedRow();
        if(row < 0 || row >= enrRows.size()){
            JOptionPane.showMessageDialog(this, "Please select an enrollment to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        StudentCourse sc = enrRows.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Remove " + sc.student().getFullName() + " from " + sc.course().courseCode() + "?",
            "Confirm Remove", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if(confirm == JOptionPane.YES_OPTION){
            boolean ok = enrollService.unenrollStudent(sc.studentCourseId());
            if(ok){
                loadEnrollments();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to remove enrollment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAssignSecretaryDialog(){
        if(cmbSecSection.getSelectedIndex() <= 0){
            JOptionPane.showMessageDialog(this, "Please select a section first.", "No Section", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int sectionId = secSectionCmbMap.get(cmbSecSection.getSelectedIndex());
        Section section = sectionService.getAllSections().stream()
            .filter(s -> s.sectionId() == sectionId).findFirst().orElse(null);
        if(section == null) return;

        List<Student> students = studentService.getAllStudents().stream()
            .filter(s -> s.section().sectionId() == sectionId)
            .toList();

        if(students.isEmpty()){
            JOptionPane.showMessageDialog(this, "No students found in this section.", "No Students", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Assign Secretary");
        dialog.setModal(true);
        dialog.setSize(440, 220);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setResizable(false);

        JLabel accent = new JLabel("");
        accent.setBounds(0, 0, 4, 220);
        accent.setBackground(new Color(255, 140, 0));
        accent.setOpaque(true);
        dialog.add(accent);

        JLabel lblHeader = new JLabel("Assign Secretary for " + section.sectionCode());
        lblHeader.setBounds(24, 20, 390, 25);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 15));
        lblHeader.setForeground(new Color(60, 60, 60));
        dialog.add(lblHeader);

        JSeparator sep = new JSeparator();
        sep.setBounds(24, 50, 392, 1);
        sep.setForeground(new Color(220, 220, 220));
        dialog.add(sep);

        JLabel lblStudent = new JLabel("Student");
        lblStudent.setBounds(24, 68, 100, 25);
        lblStudent.setFont(new Font("Arial", Font.BOLD, 13));
        lblStudent.setForeground(new Color(100, 100, 100));
        dialog.add(lblStudent);

        JComboBox<String> cmbStudent = new JComboBox<>();
        Map<Integer, Student> studentMap = new HashMap<>();
        int i = 0;
        for(Student s : students){
            cmbStudent.addItem(s.studentNumber() + " - " + s.firstName() + " " + s.lastName());
            studentMap.put(i++, s);
        }
        cmbStudent.setBounds(130, 68, 280, 32);
        cmbStudent.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbStudent.setBackground(Color.WHITE);
        cmbStudent.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbStudent.setFocusable(false);
        dialog.add(cmbStudent);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBounds(220, 145, 90, 34);
        btnCancel.setFont(new Font("Arial", Font.PLAIN, 13));
        btnCancel.setForeground(new Color(100, 100, 100));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(ev -> dialog.dispose());
        dialog.add(btnCancel);

        JButton btnSave = new JButton("Assign");
        btnSave.setBounds(320, 145, 90, 34);
        btnSave.setFont(new Font("Arial", Font.PLAIN, 13));
        btnSave.setForeground(Color.WHITE);
        btnSave.setBackground(new Color(255, 140, 0));
        btnSave.setBorder(null);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(ev -> {
            Student student = studentMap.get(cmbStudent.getSelectedIndex());
            if(student == null) return;
            boolean ok = secretaryService.assignSecretary(student, section);
            if(ok){
                JOptionPane.showMessageDialog(dialog, "Secretary assigned successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadSecretaries();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to assign. This student may already be a secretary.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(btnSave);

        dialog.setVisible(true);
    }

    private void removeSecretary(){
        int row = secTable.getSelectedRow();
        if(row < 0 || row >= secRows.size()){
            JOptionPane.showMessageDialog(this, "Please select a secretary to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Secretary s = secRows.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Remove " + s.student().getFullName() + " as secretary of " + s.section().sectionCode() + "?",
            "Confirm Remove", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if(confirm == JOptionPane.YES_OPTION){
            boolean ok = secretaryService.removeSecretary(s.secretaryId());
            if(ok){
                loadSecretaries();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to remove secretary.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JComboBox<String> buildCombo(){
        JComboBox<String> c = new JComboBox<>();
        c.setFont(new Font("Arial", Font.PLAIN, 14));
        c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        c.setFocusable(false);
        c.setRenderer(new BasicComboBoxRenderer(){
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus){
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setBorder(new EmptyBorder(0, 10, 0, 0));
                return lbl;
            }
        });
        return c;
    }

    private JButton actionButton(String text, Color bg){
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.PLAIN, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setBorder(null);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JTable buildTable(DefaultTableModel model){
        JTable t = new JTable(model);
        t.setRowHeight(28);
        t.setFillsViewportHeight(true);
        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        t.getTableHeader().setBackground(Color.WHITE);
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        t.getTableHeader().setPreferredSize(new Dimension(t.getPreferredSize().width, 28));
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < t.getColumnCount(); i++){
            t.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        return t;
    }

    private JScrollPane buildScroll(JTable t){
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    @Override
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x, y, width, height);
        if(width <= 0 || height <= 0) return;

        int tabH = 44;
        tabBar.setBounds(0, 0, width, tabH);
        btnTabEnroll.setBounds(40, 10, 200, 28);
        btnTabSecretary.setBounds(260, 10, 200, 28);
        cardHost.setBounds(0, tabH, width, height - tabH);

        int cardH = height - tabH;
        int rightMargin = 40;
        int gap = 12;

        if(enrollCard != null){
            lblEnrTitle.setBounds(40, 20, 400, 30);
            lblEnrSub.setBounds(40, 50, 600, 30);
            enrSep.setBounds(40, 82, width - 80, 1);
            cmbEnrStudent.setBounds(40, 100, 300, 36);
            btnEnrLoad.setBounds(350, 100, 90, 36);

            int removeW = 100;
            int enrollW = 150;
            int removeX = width - rightMargin - removeW;
            btnEnrUnenroll.setBounds(removeX, 100, removeW, 36);
            btnEnrEnroll.setBounds(removeX - gap - enrollW, 100, enrollW, 36);

            enrScroll.setBounds(40, 150, width - 80, cardH - 180);
        }

        if(secretaryCard != null){
            lblSecTitle.setBounds(40, 20, 450, 30);
            lblSecSub.setBounds(40, 50, 600, 30);
            secSep.setBounds(40, 82, width - 80, 1);
            cmbSecSection.setBounds(40, 100, 280, 36);
            btnSecLoad.setBounds(330, 100, 90, 36);

            int removeW = 100;
            int assignW = 160;
            int removeX = width - rightMargin - removeW;
            btnSecRemove.setBounds(removeX, 100, removeW, 36);
            btnSecAssign.setBounds(removeX - gap - assignW, 100, assignW, 36);

            secScroll.setBounds(40, 150, width - 80, cardH - 180);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnEnrLoad)       loadEnrollments();
        else if(e.getSource() == btnEnrEnroll) showEnrollDialog();
        else if(e.getSource() == btnEnrUnenroll) unenrollSelected();
        else if(e.getSource() == btnSecLoad)   loadSecretaries();
        else if(e.getSource() == btnSecAssign) showAssignSecretaryDialog();
        else if(e.getSource() == btnSecRemove) removeSecretary();
    }
}
