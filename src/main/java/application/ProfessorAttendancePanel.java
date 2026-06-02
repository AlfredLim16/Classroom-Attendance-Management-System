package application;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
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
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import session.Attendance;
import session.AttendanceService;
import session.ClassSession;
import session.ClassSessionService;
import user.Professor;
import user.ProfessorSection;
import user.ProfessorSectionService;
import user.SecretaryDAO;
import user.User;

public class ProfessorAttendancePanel extends JPanel implements ActionListener {

    private JLabel lblTitle, lblSubTitle;
    private JComboBox<String> cmbSection, cmbSession;
    private JButton btnLoad, btnExport;
    private JTable attendanceTable;
    private JSeparator separator;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private Professor professor;
    private User currentUser;
    private final ProfessorSectionService sectionService = new ProfessorSectionService();
    private final ClassSessionService sessionService = new ClassSessionService();
    private final AttendanceService attendanceService = new AttendanceService();
    private final SecretaryDAO secretaryDAO = new SecretaryDAO();

    private final Map<Integer, Integer> sectionComboIndexToId = new HashMap<>();
    private final Map<Integer, Integer> sessionComboIndexToId = new HashMap<>();

    public ProfessorAttendancePanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("View Attendance");
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

        cmbSection = new JComboBox<>();
        cmbSection.setBounds(40, 100, 200, 36);
        cmbSection.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbSection.setBackground(Color.WHITE);
        cmbSection.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbSection.setFocusable(false);
        cmbSection.addItem("Select Section");
        cmbSection.setSelectedItem("Select Section");

        cmbSection.setRenderer(new BasicComboBoxRenderer() {
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
        cmbSection.addActionListener(this);
        add(cmbSection);

        cmbSession = new JComboBox<>();
        cmbSession.setBounds(260, 100, 250, 36);
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

        btnLoad = new JButton("Load");
        btnLoad.setBounds(530, 100, 100, 36);
        btnLoad.setFont(new Font("Arial", Font.PLAIN, 14));
        btnLoad.setForeground(Color.WHITE);
        btnLoad.setBackground(new Color(255, 140, 0));
        btnLoad.setBorder(null);
        btnLoad.setFocusPainted(false);
        btnLoad.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLoad.addActionListener(this);
        add(btnLoad);

        btnExport = new JButton("Export");
        btnExport.setBounds(640, 100, 100, 36);
        btnExport.setFont(new Font("Arial", Font.BOLD, 14));
        btnExport.setForeground(new Color(100, 100, 100));
        btnExport.setBackground(Color.WHITE);
        btnExport.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btnExport.setFocusPainted(false);
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExport.addActionListener(this);
        add(btnExport);

        String[] columns = {"Student No.", "Name", "Status", "Time", "Recorded By"};
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

    public void setProfessor(Professor professor){
        this.professor = professor;
        loadSections();
    }

    public void setCurrentUser(User user){
        this.currentUser = user;
    }

    private void loadSections(){
        cmbSection.removeAllItems();
        cmbSection.addItem("Select Section");
        sectionComboIndexToId.clear();
        if(professor == null) return;

        List<ProfessorSection> sections = sectionService.getSectionsByProfessor(professor.professorId());
        int index = 1;
        for(ProfessorSection ps : sections){
            cmbSection.addItem(ps.section().sectionCode());
            sectionComboIndexToId.put(index, ps.section().sectionId());
            index++;
        }
    }

    private void loadSessionsForSection(){
        cmbSession.removeAllItems();
        cmbSession.addItem("Select Session");
        sessionComboIndexToId.clear();
        if(professor == null || cmbSection.getSelectedIndex() <= 0) return;

        int sectionId = sectionComboIndexToId.get(cmbSection.getSelectedIndex());
        List<ClassSession> sessions = sessionService.getClassSessionsBySection(sectionId);
        int index = 1;
        for(ClassSession s : sessions){
            String display = s.course().courseCode() + " - " + s.sessionDate() + " (" + s.startTime() + ")";
            cmbSession.addItem(display);
            sessionComboIndexToId.put(index, s.sessionId());
            index++;
        }
    }

    private void loadAttendance(){
        if(cmbSession.getSelectedIndex() <= 0) return;
        int sessionId = sessionComboIndexToId.get(cmbSession.getSelectedIndex());
        List<Attendance> attendances = attendanceService.getAttendancesBySession(sessionId);

        tableModel.setRowCount(0);
        for(Attendance a : attendances){
            String recordedBy = a.recordedBy() != null ? a.recordedBy().userName() : "-";
            addAttendanceRow(
                a.student().studentNumber(),
                a.student().firstName() + " " + a.student().lastName(),
                a.status().getStatusName(),
                "-",
                recordedBy
            );
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x, y, width, height);
        if(width > 0 && height > 0 && scrollPane != null){
            separator.setBounds(40, 80, width - 80, height - 160);
            scrollPane.setBounds(40, 150, width - 80, height - 180);
        }

        if(width > 0 && btnLoad != null && btnExport != null){
            int rightMargin = 40;
            int gap = 20;
            int absentW = 120;
            int lateW = 120;
            int absentX = width - rightMargin - absentW;
            btnLoad.setBounds(absentX, 100, absentW, 36);
            int lateX = absentX - gap - lateW;
            btnExport.setBounds(lateX, 100, lateW, 36);
        }
    }

    public void addAttendanceRow(String studentNo, String name, String status, String time, String recordedBy){
        tableModel.addRow(new Object[]{studentNo, name, status, time, recordedBy});
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == cmbSection){
            loadSessionsForSection();
        }
        if(e.getSource() == btnLoad){
            loadAttendance();
        }
        if(e.getSource() == btnExport){
            JOptionPane.showMessageDialog(this, "Export feature coming soon.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}