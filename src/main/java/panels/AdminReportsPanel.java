package panels;

import services.CourseService;
import services.SectionService;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
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
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import services.AttendanceReportService;
import services.ClassSessionService;
import services.ProfessorService;
import services.StudentService;

public class AdminReportsPanel extends JPanel implements ActionListener {

    private JLabel lblTitle, lblSubTitle;
    private JSeparator separator;
    private JComboBox<String> cmbReportType, cmbFilter;
    private JTextField txtDateFrom, txtDateTo;
    private JButton btnGenerate, btnExport;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private final AttendanceReportService attendanceReportService = new AttendanceReportService();
    private final ClassSessionService classSessionService = new ClassSessionService();
    private final StudentService studentService = new StudentService();
    private final ProfessorService professorService = new ProfessorService();
    private final CourseService courseService = new CourseService();
    private final SectionService sectionService = new SectionService();

    public AdminReportsPanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("System Reports");
        lblTitle.setBounds(40, 20, 300, 30);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle);

        lblSubTitle = new JLabel("Generate and export attendance, performance, and system reports");
        lblSubTitle.setBounds(40, 50, 600, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblSubTitle);

        separator = new JSeparator();
        separator.setForeground(Color.BLACK);
        add(separator);

        cmbReportType = new JComboBox<>(new String[]{"Select Report", "Attendance Summary", "Student Performance", "Professor Activity", "Course Analytics", "System Overview"});
        cmbReportType.setBounds(40, 100, 180, 36);
        cmbReportType.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbReportType.setBackground(Color.WHITE);
        cmbReportType.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbReportType.setFocusable(false);
        cmbReportType.setRenderer(new BasicComboBoxRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                javax.swing.JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus){
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(new EmptyBorder(0, 10, 0, 0));
                return label;
            }
        });
        add(cmbReportType);

        cmbFilter = new JComboBox<>(new String[]{"All", "By Program", "By Section", "By Course", "By Professor"});
        cmbFilter.setBounds(230, 100, 150, 36);
        cmbFilter.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbFilter.setBackground(Color.WHITE);
        cmbFilter.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbFilter.setFocusable(false);
        cmbFilter.setRenderer(new BasicComboBoxRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                javax.swing.JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus){
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(new EmptyBorder(0, 10, 0, 0));
                return label;
            }
        });
        add(cmbFilter);

        txtDateFrom = new JTextField("2025-08-01");
        txtDateFrom.setBounds(400, 100, 120, 36);
        txtDateFrom.setFont(new Font("Arial", Font.PLAIN, 13));
        txtDateFrom.setForeground(new Color(60, 60, 60));
        txtDateFrom.setBackground(Color.WHITE);
        txtDateFrom.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        add(txtDateFrom);

        txtDateTo = new JTextField("2026-05-30");
        txtDateTo.setBounds(530, 100, 120, 36);
        txtDateTo.setFont(new Font("Arial", Font.PLAIN, 13));
        txtDateTo.setForeground(new Color(60, 60, 60));
        txtDateTo.setBackground(Color.WHITE);
        txtDateTo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        add(txtDateTo);

        btnGenerate = new JButton("Generate");
        btnGenerate.setBounds(670, 100, 120, 36);
        btnGenerate.setFont(new Font("Arial", Font.PLAIN, 14));
        btnGenerate.setForeground(Color.WHITE);
        btnGenerate.setBackground(new Color(255, 140, 0));
        btnGenerate.setBorder(null);
        btnGenerate.setFocusPainted(false);
        btnGenerate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGenerate.addActionListener(this);
        add(btnGenerate);

        btnExport = new JButton("Export");
        btnExport.setBounds(800, 100, 100, 36);
        btnExport.setFont(new Font("Arial", Font.PLAIN, 14));
        btnExport.setForeground(new Color(100, 100, 100));
        btnExport.setBackground(Color.WHITE);
        btnExport.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btnExport.setFocusPainted(false);
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExport.addActionListener(this);
        add(btnExport);

        String[] columns = {"Metric", "Value", "Period", "Trend"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        reportTable = new JTable(tableModel);
        reportTable.setRowHeight(28);
        reportTable.setFillsViewportHeight(true);
        reportTable.getTableHeader().setReorderingAllowed(false);
        reportTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        reportTable.getTableHeader().setBackground(new Color(255, 255, 255));
        reportTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        reportTable.getTableHeader().setPreferredSize(new Dimension(reportTable.getPreferredSize().width, 28));

        DefaultTableCellRenderer centerCollumn = new DefaultTableCellRenderer();
        centerCollumn.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < reportTable.getColumnCount(); i++){
            reportTable.getColumnModel().getColumn(i).setCellRenderer(centerCollumn);
        }

        scrollPane = new JScrollPane(reportTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane);
    }

    @Override
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x, y, width, height);
        if(width > 0 && height > 0 && scrollPane != null){
            separator.setBounds(40, 80, width - 80, height - 160);
            scrollPane.setBounds(40, 150, width - 80, height - 180);
        }

        if(width > 0 && btnExport != null && btnGenerate != null){
            int rightMargin = 40;
            int gap = 15;
            int btnH = 36;
            int expW = 100;
            int genW = 120;

            int expX = width - rightMargin - expW;
            btnExport.setBounds(expX, 100, expW, btnH);

            int genX = expX - gap - genW;
            btnGenerate.setBounds(genX, 100, genW, btnH);
        }
    }

    public void addReportRow(String metric, String value, String period, String trend){
        tableModel.addRow(new Object[]{metric, value, period, trend});
    }

    private void generateAttendanceSummary(){
        tableModel.setRowCount(0);
        var sessions = classSessionService.getAllClassSessions();
        int totalSessions = sessions.size();
        int totalAttendances = 0;
        int totalPresent = 0, totalLate = 0, totalAbsent = 0, totalExcused = 0;

        for(var session : sessions){
            Map<String, Integer> stats = attendanceReportService.getDailyAttendanceStats(session.sessionId());
            totalPresent += stats.getOrDefault("Present", 0);
            totalLate += stats.getOrDefault("Late", 0);
            totalAbsent += stats.getOrDefault("Absent", 0);
            totalExcused += stats.getOrDefault("Excused", 0);
            totalAttendances += stats.values().stream().mapToInt(Integer::intValue).sum();
        }

        double attendanceRate = totalAttendances > 0 ? (double)(totalPresent + totalExcused) / totalAttendances * 100 : 0;

        addReportRow("Total Class Sessions", String.valueOf(totalSessions), "All Time", "—");
        addReportRow("Total Attendance Records", String.valueOf(totalAttendances), "All Time", "—");
        addReportRow("Present", String.valueOf(totalPresent), "All Time", "—");
        addReportRow("Late", String.valueOf(totalLate), "All Time", "—");
        addReportRow("Absent", String.valueOf(totalAbsent), "All Time", "—");
        addReportRow("Excused", String.valueOf(totalExcused), "All Time", "—");
        addReportRow("Attendance Rate", String.format("%.1f%%", attendanceRate), "All Time", totalAttendances > 0 ? "+" + String.format("%.1f", attendanceRate - 85) + "%" : "N/A");
    }

    private void generateStudentPerformance(){
        tableModel.setRowCount(0);
        var students = studentService.getAllStudents();
        int totalStudents = students.size();
        int atRiskCount = 0;
        int goodStanding = totalStudents;

        addReportRow("Total Students", String.valueOf(totalStudents), "Current Semester", "—");
        addReportRow("Good Standing", String.valueOf(goodStanding), "Current Semester", "—");
        addReportRow("At Risk", String.valueOf(atRiskCount), "Current Semester", "—");
        addReportRow("Average Attendance", "87.3%", "Current Semester", "+3.2%");
    }

    private void generateProfessorActivity(){
        tableModel.setRowCount(0);
        var professors = professorService.getAllProfessors();
        var sessions = classSessionService.getAllClassSessions();

        for(var prof : professors){
            long profSessions = sessions.stream()
                .filter(s -> s.professor().professorId() == prof.professorId())
                .count();
            addReportRow(prof.getFullName(), String.valueOf(profSessions) + " sessions", "All Time", profSessions > 0 ? "Active" : "Inactive");
        }
    }

    private void generateCourseAnalytics(){
        tableModel.setRowCount(0);
        var courses = courseService.getAllCourses();
        var sessions = classSessionService.getAllClassSessions();

        for(var course : courses){
            long courseSessions = sessions.stream()
                .filter(s -> s.course().courseId() == course.courseId())
                .count();
            addReportRow(course.courseCode() + " - " + course.courseName(), String.valueOf(courseSessions) + " sessions", "All Time", courseSessions > 0 ? "Active" : "No sessions");
        }
    }

    private void generateSystemOverview(){
        tableModel.setRowCount(0);
        int totalUsers = new services.UserService().getAllUsers().size();
        int totalStudents = studentService.getAllStudents().size();
        int totalProfessors = professorService.getAllProfessors().size();
        int totalCourses = courseService.getAllCourses().size();
        int totalSections = sectionService.getAllSections().size();
        int totalSessions = classSessionService.getAllClassSessions().size();

        addReportRow("Total Users", String.valueOf(totalUsers), "System", "—");
        addReportRow("Total Students", String.valueOf(totalStudents), "System", "—");
        addReportRow("Total Professors", String.valueOf(totalProfessors), "System", "—");
        addReportRow("Total Courses", String.valueOf(totalCourses), "System", "—");
        addReportRow("Total Sections", String.valueOf(totalSections), "System", "—");
        addReportRow("Total Class Sessions", String.valueOf(totalSessions), "System", "—");
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnGenerate){
            String reportType = cmbReportType.getSelectedItem().toString();
            if(reportType.equals("Select Report")){
                JOptionPane.showMessageDialog(this, "Please select a report type.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            tableModel.setRowCount(0);
            switch(reportType){
                case "Attendance Summary" -> generateAttendanceSummary();
                case "Student Performance" -> generateStudentPerformance();
                case "Professor Activity" -> generateProfessorActivity();
                case "Course Analytics" -> generateCourseAnalytics();
                case "System Overview" -> generateSystemOverview();
            }
            JOptionPane.showMessageDialog(this, "Report generated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
        if(e.getSource() == btnExport){
            if(reportTable.getRowCount() == 0){
                JOptionPane.showMessageDialog(this, "Please generate a report first.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            try{
                StringBuilder csv = new StringBuilder();
                csv.append("Metric,Value,Period,Trend\n");
                for(int i = 0; i < reportTable.getRowCount(); i++){
                    for(int j = 0; j < reportTable.getColumnCount(); j++){
                        csv.append(reportTable.getValueAt(i, j));
                        if(j < reportTable.getColumnCount() - 1) csv.append(",");
                    }
                    csv.append("\n");
                }
                java.nio.file.Files.writeString(
                    java.nio.file.Paths.get("report_export.csv"),
                    csv.toString()
                );
                JOptionPane.showMessageDialog(this, "Report exported to report_export.csv", "Export Success", JOptionPane.INFORMATION_MESSAGE);
            }catch(Exception ex){
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}