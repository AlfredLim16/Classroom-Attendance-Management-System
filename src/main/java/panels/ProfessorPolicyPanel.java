package panels;

import core.Course;
import junction.AttendancePolicy;
import junction.ProfessorCourse;
import services.AttendancePolicyService;
import services.ProfessorCourseService;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import core.Professor;

public class ProfessorPolicyPanel extends JPanel implements ActionListener {

    private JLabel lblTitle, lblSubTitle;
    private JSeparator separator;
    private JLabel lblCourse, lblLate, lblLates, lblAbsents;
    private JComboBox<String> cmbCourse;
    private JTextField txtLate, txtLates, txtAbsents;
    private JButton btnSave, btnToggle;
    private JTable policyTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private Professor professor;
    private final AttendancePolicyService policyService = new AttendancePolicyService();
    private final ProfessorCourseService courseService = new ProfessorCourseService();

    private final List<Course> rowCourses = new java.util.ArrayList<>();

    public ProfessorPolicyPanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        // Title
        lblTitle = new JLabel("Attendance Policies");
        lblTitle.setBounds(40, 20, 300, 30);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle);

        // Description - same as other panels
        lblSubTitle = new JLabel("Manage late thresholds and absence rules for your courses");
        lblSubTitle.setBounds(40, 50, 800, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblSubTitle);

        // Separator - same as other panels
        separator = new JSeparator();
        separator.setForeground(Color.BLACK);
        add(separator);

        // Course Label
        lblCourse = new JLabel("Course");
        lblCourse.setBounds(40, 100, 100, 20);
        lblCourse.setFont(new Font("Arial", Font.PLAIN, 13));
        lblCourse.setForeground(new Color(80, 80, 80));
        add(lblCourse);

        // Course Combo
        cmbCourse = new JComboBox<>();
        cmbCourse.setBounds(40, 122, 250, 36);
        cmbCourse.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbCourse.setBackground(Color.WHITE);
        cmbCourse.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbCourse.setFocusable(false);
        add(cmbCourse);

        // Late Threshold Label
        lblLate = new JLabel("Late Threshold (mins)");
        lblLate.setBounds(310, 100, 150, 20);
        lblLate.setFont(new Font("Arial", Font.PLAIN, 13));
        lblLate.setForeground(new Color(80, 80, 80));
        add(lblLate);

        // Late Threshold Field
        txtLate = new JTextField();
        txtLate.setBounds(310, 122, 120, 36);
        txtLate.setFont(new Font("Arial", Font.PLAIN, 14));
        txtLate.setForeground(new Color(60, 60, 60));
        txtLate.setBackground(Color.WHITE);
        txtLate.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        add(txtLate);

        // Lates = Absent Label
        lblLates = new JLabel("Lates = 1 Absent");
        lblLates.setBounds(450, 100, 120, 20);
        lblLates.setFont(new Font("Arial", Font.PLAIN, 13));
        lblLates.setForeground(new Color(80, 80, 80));
        add(lblLates);

        // Lates Field
        txtLates = new JTextField();
        txtLates.setBounds(450, 122, 100, 36);
        txtLates.setFont(new Font("Arial", Font.PLAIN, 14));
        txtLates.setForeground(new Color(60, 60, 60));
        txtLates.setBackground(Color.WHITE);
        txtLates.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        add(txtLates);

        // Absents = Dropped Label
        lblAbsents = new JLabel("Absents = Dropped");
        lblAbsents.setBounds(570, 100, 130, 20);
        lblAbsents.setFont(new Font("Arial", Font.PLAIN, 13));
        lblAbsents.setForeground(new Color(80, 80, 80));
        add(lblAbsents);

        // Absents Field
        txtAbsents = new JTextField();
        txtAbsents.setBounds(570, 122, 100, 36);
        txtAbsents.setFont(new Font("Arial", Font.PLAIN, 14));
        txtAbsents.setForeground(new Color(60, 60, 60));
        txtAbsents.setBackground(Color.WHITE);
        txtAbsents.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        add(txtAbsents);

        // Save Button - orange
        btnSave = new JButton("Save Policy");
        btnSave.setBounds(690, 122, 120, 36);
        btnSave.setFont(new Font("Arial", Font.PLAIN, 14));
        btnSave.setForeground(Color.WHITE);
        btnSave.setBackground(new Color(255, 140, 0));
        btnSave.setBorder(null);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(this);
        add(btnSave);

        // Toggle Button - white outline
        btnToggle = new JButton("Toggle Active");
        btnToggle.setBounds(820, 122, 140, 36);
        btnToggle.setFont(new Font("Arial", Font.PLAIN, 14));
        btnToggle.setForeground(new Color(100, 100, 100));
        btnToggle.setBackground(Color.WHITE);
        btnToggle.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btnToggle.setFocusPainted(false);
        btnToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToggle.addActionListener(this);
        add(btnToggle);

        // Table columns
        String[] columns = {"Course", "Late Threshold", "Lates = Absent", "Absents = Dropped", "Active"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        policyTable = new JTable(tableModel);
        policyTable.setRowHeight(28);
        policyTable.setFillsViewportHeight(true);
        policyTable.setIntercellSpacing(new Dimension(0, 0));
        policyTable.getTableHeader().setReorderingAllowed(false);
        policyTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        policyTable.getTableHeader().setBackground(new Color(255, 255, 255));
        policyTable.getTableHeader().setForeground(new Color(60, 60, 60));
        policyTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        policyTable.getTableHeader().setPreferredSize(new Dimension(policyTable.getPreferredSize().width, 28));

        // Center all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < policyTable.getColumnCount(); i++){
            policyTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // ScrollPane - same as other panels
        scrollPane = new JScrollPane(policyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane);
    }

    public void setProfessor(Professor professor){
        this.professor = professor;
        loadCourses();
        loadPolicies();
    }

    private void loadCourses(){
        cmbCourse.removeAllItems();
        rowCourses.clear();
        if(professor == null){
            return;
        }

        List<ProfessorCourse> courses = courseService.getCoursesByProfessor(professor.professorId());
        for(ProfessorCourse pc : courses){
            cmbCourse.addItem(pc.course().courseCode() + " - " + pc.course().courseName());
            rowCourses.add(pc.course());
        }

        cmbCourse.addActionListener(e -> loadPolicyForSelectedCourse());
    }

    private void loadPolicyForSelectedCourse(){
        if(cmbCourse.getSelectedIndex() < 0 || cmbCourse.getSelectedIndex() >= rowCourses.size()){
            txtLate.setText("");
            txtLates.setText("");
            txtAbsents.setText("");
            return;
        }

        int courseId = rowCourses.get(cmbCourse.getSelectedIndex()).courseId();
        Optional<AttendancePolicy> policyOpt = policyService.getAttendancePolicyByCourse(courseId);
        if(policyOpt.isPresent()){
            AttendancePolicy p = policyOpt.get();
            txtLate.setText(String.valueOf(p.lateThresholdMinutes()));
            txtLates.setText(String.valueOf(p.latesEqualToAbsent()));
            txtAbsents.setText(String.valueOf(p.absentsEqualToDropped()));
        }else{
            txtLate.setText("15");
            txtLates.setText("3");
            txtAbsents.setText("5");
        }
    }

    private void loadPolicies(){
        tableModel.setRowCount(0);
        if(professor == null){
            return;
        }

        List<ProfessorCourse> courses = courseService.getCoursesByProfessor(professor.professorId());
        for(ProfessorCourse pc : courses){
            Optional<AttendancePolicy> policyOpt = policyService.getAttendancePolicyByCourse(pc.course().courseId());
            if(policyOpt.isPresent()){
                AttendancePolicy p = policyOpt.get();
                addPolicyRow(
                    pc.course().courseCode(),
                    String.valueOf(p.lateThresholdMinutes()),
                    String.valueOf(p.latesEqualToAbsent()),
                    String.valueOf(p.absentsEqualToDropped()),
                    p.isActive() ? "Yes" : "No"
                );
            }else{
                addPolicyRow(pc.course().courseCode(), "-", "-", "-", "No Policy");
            }
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x, y, width, height);
        if(width > 0 && height > 0){
            separator.setBounds(40, 80, width - 80, height - 160);

            if(scrollPane != null){
                scrollPane.setBounds(40, 180, width - 80, height - 210);
            }

            if(btnSave != null && btnToggle != null){
                int rightMargin = 50;
                int btnW = 120;
                int btnH = 36;
                int gap = 15;

                int toggleX = width - rightMargin - btnW - 10;
                btnToggle.setBounds(toggleX, 122, btnW + 20, btnH);

                int saveX = toggleX - gap - btnW;
                btnSave.setBounds(saveX, 122, btnW, btnH);

                int fieldShift = Math.max(0, (saveX - 690));
                if(fieldShift < 0){
                    lblAbsents.setBounds(570 + fieldShift, 100, 130, 20);
                    txtAbsents.setBounds(570 + fieldShift, 122, 100, 36);
                    lblLates.setBounds(450 + fieldShift, 100, 120, 20);
                    txtLates.setBounds(450 + fieldShift, 122, 100, 36);
                    lblLate.setBounds(310 + fieldShift, 100, 150, 20);
                    txtLate.setBounds(310 + fieldShift, 122, 120, 36);
                    lblCourse.setBounds(40 + fieldShift, 100, 100, 20);
                    cmbCourse.setBounds(40 + fieldShift, 122, 250, 36);
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnSave){
            savePolicy();
        }
        if(e.getSource() == btnToggle){
            togglePolicy();
        }
    }

    private void savePolicy(){
        if(professor == null || cmbCourse.getSelectedIndex() < 0){
            return;
        }

        try{
            int courseId = rowCourses.get(cmbCourse.getSelectedIndex()).courseId();
            int late = Integer.parseInt(txtLate.getText().trim());
            int lates = Integer.parseInt(txtLates.getText().trim());
            int absents = Integer.parseInt(txtAbsents.getText().trim());

            Optional<AttendancePolicy> existing = policyService.getAttendancePolicyByCourse(courseId);
            Course course = rowCourses.get(cmbCourse.getSelectedIndex());

            if(existing.isPresent()){
                AttendancePolicy old = existing.get();
                AttendancePolicy updated = AttendancePolicy.builder()
                    .policyId(old.policyId())
                    .course(course)
                    .lateThresholdMinutes(late)
                    .latesEqualToAbsent(lates)
                    .absentsEqualToDropped(absents)
                    .isActive(old.isActive())
                    .build();
                policyService.updateAttendancePolicy(updated);
            }else{
                policyService.createAttendancePolicy(course, late, lates, absents, true);
            }

            JOptionPane.showMessageDialog(this, "Policy saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadPolicies();

        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(this, "Please enter valid numbers.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void togglePolicy(){
        if(professor == null || cmbCourse.getSelectedIndex() < 0){
            return;
        }

        int courseId = rowCourses.get(cmbCourse.getSelectedIndex()).courseId();
        Optional<AttendancePolicy> policyOpt = policyService.getAttendancePolicyByCourse(courseId);
        if(policyOpt.isEmpty()){
            JOptionPane.showMessageDialog(this, "No policy exists for this course. Create one first.", "No Policy", JOptionPane.WARNING_MESSAGE);
            return;
        }

        AttendancePolicy old = policyOpt.get();
        AttendancePolicy updated = AttendancePolicy.builder()
            .policyId(old.policyId())
            .course(old.course())
            .lateThresholdMinutes(old.lateThresholdMinutes())
            .latesEqualToAbsent(old.latesEqualToAbsent())
            .absentsEqualToDropped(old.absentsEqualToDropped())
            .isActive(!old.isActive())
            .build();

        policyService.updateAttendancePolicy(updated);
        JOptionPane.showMessageDialog(this, "Policy toggled to " + (updated.isActive() ? "Active" : "Inactive") + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
        loadPolicies();
    }

    public void addPolicyRow(String course, String lateThreshold, String latesEqual, String absentsEqual, String active){
        tableModel.addRow(new Object[]{course, lateThreshold, latesEqual, absentsEqual, active});
    }
}