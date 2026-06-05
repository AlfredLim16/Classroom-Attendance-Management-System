package panels;

import core.Section;
import core.Student;
import core.User;
import junction.Attendance;
import junction.ClassSession;
import lookup.AttendanceStatus;
import dao.SecretaryStudentDAO;
import services.AttendanceQueryService;
import services.AttendanceRecordingService;
import services.ClassSessionService;
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
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class SecretaryAttendancePanel extends JPanel implements ActionListener {

    private JLabel lblTitle, lblSubTitle;
    private JSeparator separator;
    private JComboBox<String> cmbSession;
    private JButton btnLoad, btnMarkPresent, btnMarkLate, btnMarkAbsent;
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private Section section;
    private User currentUser;
    private final ClassSessionService sessionService = new ClassSessionService();
    private final AttendanceQueryService attendanceService = new AttendanceQueryService();
    private final AttendanceRecordingService recordingService = new AttendanceRecordingService();
    private final SecretaryStudentDAO secretaryDAO = new SecretaryStudentDAO();

    private final Map<Integer, Integer> comboIndexToSessionId = new HashMap<>();
    private final List<Integer> rowStudentIds = new ArrayList<>();

    public SecretaryAttendancePanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("Record Attendance");
        lblTitle.setBounds(40, 20, 300, 30);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle);

        lblSubTitle = new JLabel("Description");
        lblSubTitle.setBounds(40, 50, 400, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblSubTitle);

        separator = new JSeparator();
        separator.setForeground(Color.BLACK);
        add(separator);

        cmbSession = new JComboBox<>();
        cmbSession.setBounds(40, 100, 300, 36);
        cmbSession.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbSession.setBackground(Color.WHITE);
        cmbSession.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbSession.setFocusable(false);
        cmbSession.addItem("Select Session");
        cmbSession.setSelectedItem("Select Session");

        cmbSession.setRenderer(new BasicComboBoxRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                javax.swing.JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus){

                JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                label.setBorder(new EmptyBorder(0, 10, 0, 0));
                return label;
            }
        });
        add(cmbSession);

        btnLoad = new JButton("Load Students");
        btnLoad.setBounds(360, 100, 140, 36);
        btnLoad.setFont(new Font("Arial", Font.PLAIN, 14));
        btnLoad.setForeground(Color.WHITE);
        btnLoad.setBackground(new Color(255, 140, 0));
        btnLoad.setBorder(null);
        btnLoad.setFocusPainted(false);
        btnLoad.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLoad.addActionListener(this);
        add(btnLoad);

        btnMarkPresent = new JButton("Mark Present");
        btnMarkPresent.setFont(new Font("Arial", Font.PLAIN, 14));
        btnMarkPresent.setForeground(Color.WHITE);
        btnMarkPresent.setBackground(new Color(40, 167, 69));
        btnMarkPresent.setBorder(null);
        btnMarkPresent.setFocusPainted(false);
        btnMarkPresent.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMarkPresent.addActionListener(this);
        add(btnMarkPresent);

        btnMarkLate = new JButton("Mark Late");
        btnMarkLate.setFont(new Font("Arial", Font.PLAIN, 14));
        btnMarkLate.setForeground(Color.WHITE);
        btnMarkLate.setBackground(new Color(255, 193, 7));
        btnMarkLate.setBorder(null);
        btnMarkLate.setFocusPainted(false);
        btnMarkLate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMarkLate.addActionListener(this);
        add(btnMarkLate);

        btnMarkAbsent = new JButton("Mark Absent");
        btnMarkAbsent.setFont(new Font("Arial", Font.PLAIN, 14));
        btnMarkAbsent.setForeground(Color.WHITE);
        btnMarkAbsent.setBackground(new Color(220, 53, 69));
        btnMarkAbsent.setBorder(null);
        btnMarkAbsent.setFocusPainted(false);
        btnMarkAbsent.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMarkAbsent.addActionListener(this);
        add(btnMarkAbsent);

        String[] columns = {"Student No.", "Name", "Status", "Time Recorded"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        attendanceTable = new JTable(tableModel);
        attendanceTable.setRowHeight(28);
        attendanceTable.setFillsViewportHeight(true);
        attendanceTable.getTableHeader().setReorderingAllowed(false);
        attendanceTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        attendanceTable.getTableHeader().setBackground(new Color(255, 255, 255));
        attendanceTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        attendanceTable.getTableHeader().setPreferredSize(new Dimension(attendanceTable.getPreferredSize().width, 28));

        DefaultTableCellRenderer centerCollumn = new DefaultTableCellRenderer();
        centerCollumn.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < attendanceTable.getColumnCount(); i++){
            attendanceTable.getColumnModel().getColumn(i).setCellRenderer(centerCollumn);
        }

        scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane);
    }

    public void setSection(Section section){
        this.section = section;
        loadSessions();
    }

    public void setCurrentUser(User user){
        this.currentUser = user;
    }

    private void loadSessions(){
        cmbSession.removeAllItems();
        cmbSession.addItem("Select Session");
        comboIndexToSessionId.clear();
        if(section == null){
            return;
        }

        List<ClassSession> sessions = sessionService.getClassSessionsBySection(section.sectionId());
        int index = 1;
        for(ClassSession s : sessions){
            String display = s.course().courseCode() + " - " + s.sessionDate() + " (" + s.startTime() + ")";
            cmbSession.addItem(display);
            comboIndexToSessionId.put(index, s.sessionId());
            index++;
        }
    }

    private void loadStudentsForSelectedSession(){
        if(section == null || cmbSession.getSelectedIndex() <= 0){
            return;
        }

        int sessionId = comboIndexToSessionId.get(cmbSession.getSelectedIndex());
        List<Student> students = secretaryDAO.findStudentsBySectionId(section.sectionId());
        List<Attendance> existing = attendanceService.getAttendancesBySession(sessionId);
        Map<Integer, Attendance> attendanceMap = new HashMap<>();
        for(Attendance a : existing){
            attendanceMap.put(a.student().studentId(), a);
        }

        tableModel.setRowCount(0);
        rowStudentIds.clear();

        for(Student student : students){
            rowStudentIds.add(student.studentId());
            Attendance att = attendanceMap.get(student.studentId());
            String status = att != null ? att.status().getStatusName() : "Not Recorded";
            String time = "-";
            addStudentRow(student.studentNumber(), student.firstName() + " " + student.lastName(), status, time);
        }
    }

    private void markSelected(AttendanceStatus status){
        if(section == null || cmbSession.getSelectedIndex() <= 0 || currentUser == null){
            JOptionPane.showMessageDialog(this, "Session or user not available.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int sessionId = comboIndexToSessionId.get(cmbSession.getSelectedIndex());
        int[] selectedRows = attendanceTable.getSelectedRows();
        if(selectedRows.length == 0){
            JOptionPane.showMessageDialog(this, "Please select at least one student.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Integer> newStudentIds = new ArrayList<>();
        for(int row : selectedRows){
            int studentId = rowStudentIds.get(row);
            String currentStatus = (String) tableModel.getValueAt(row, 2);
            if("Not Recorded".equals(currentStatus)){
                newStudentIds.add(studentId);
            }else{
                Optional<Attendance> existing = attendanceService.getAttendanceBySessionAndStudent(sessionId, studentId);
                if(existing.isPresent()){
                    Attendance old = existing.get();
                    Attendance updated = Attendance.builder()
                        .attendanceId(old.attendanceId())
                        .session(old.session())
                        .student(old.student())
                        .status(status)
                        .recordedBy(currentUser)
                        .build();
                    attendanceService.updateAttendance(updated);
                }
            }
        }

        if(!newStudentIds.isEmpty()){
            recordingService.bulkRecordAttendance(sessionId, newStudentIds, status, currentUser);
        }

        loadStudentsForSelectedSession();
        JOptionPane.showMessageDialog(this, "Attendance updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x, y, width, height);
        if(width > 0 && height > 0 && scrollPane != null){
            separator.setBounds(40, 80, width - 80, height - 160);
            scrollPane.setBounds(40, 150, width - 80, height - 180);
        }

        if(width > 0 && btnMarkAbsent != null && btnMarkLate != null && btnMarkPresent != null){
            int rightMargin = 40;
            int gap = 20;

            int absentW = 120;
            int lateW = 120;
            int presentW = 140;

            int absentX = width - rightMargin - absentW;
            btnMarkAbsent.setBounds(absentX, 100, absentW, 36);

            int lateX = absentX - gap - lateW;
            btnMarkLate.setBounds(lateX, 100, lateW, 36);

            int presentX = lateX - gap - presentW;
            btnMarkPresent.setBounds(presentX, 100, presentW, 36);
        }
    }

    public void addStudentRow(String studentNo, String name, String status, String time){
        tableModel.addRow(new Object[]{studentNo, name, status, time});
    }

    public JTable getAttendanceTable(){
        return attendanceTable;
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnLoad){
            loadStudentsForSelectedSession();
        }
        if(e.getSource() == btnMarkPresent){
            markSelected(AttendanceStatus.PRESENT);
        }
        if(e.getSource() == btnMarkLate){
            markSelected(AttendanceStatus.LATE);
        }
        if(e.getSource() == btnMarkAbsent){
            markSelected(AttendanceStatus.ABSENT);
        }
    }
}
