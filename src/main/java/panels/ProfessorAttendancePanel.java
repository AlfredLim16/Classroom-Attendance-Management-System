package panels;

import core.Professor;
import core.Student;
import core.User;
import junction.Attendance;
import junction.ClassSession;
import junction.ProfessorSection;
import dao.SecretaryStudentDAO;
import lookup.AttendanceStatus;
import services.AttendancePermissionStore;
import services.AttendanceQueryService;
import services.AttendanceRecordingService;
import services.ClassSessionService;
import services.ProfessorSectionService;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ProfessorAttendancePanel extends JPanel implements ActionListener {

    // ── tab bar ─────────────────────────────────────────────────────────────
    private JPanel tabBar;
    private JButton btnTabRecord, btnTabView;

    // ── card layout host ──────────────────────────────────────────────────
    private JPanel cardHost;
    private static final String CARD_RECORD = "RECORD";
    private static final String CARD_VIEW   = "VIEW";

    // ── RECORD card ──────────────────────────────────────────────────────
    private JPanel recordCard;
    private JLabel  lblRecTitle, lblRecSub;
    private JSeparator recSep;
    private JComboBox<String> cmbRecSection, cmbRecSession;
    private JButton btnRecLoad, btnRecPresent, btnRecLate, btnRecAbsent;
    private JButton btnToggleSecretary;
    private JTable  recTable;
    private DefaultTableModel recTableModel;
    private JScrollPane recScroll;

    private final Map<Integer, Integer> recSectionIndexToId  = new HashMap<>();
    private final Map<Integer, Integer> recSessionIndexToId  = new HashMap<>();
    private final List<Integer>         recRowStudentIds     = new ArrayList<>();

    // ── VIEW card ────────────────────────────────────────────────────────
    private JPanel viewCard;
    private JLabel  lblViewTitle, lblViewSub;
    private JSeparator viewSep;
    private JComboBox<String> cmbViewSection, cmbViewSession;
    private JButton btnViewLoad, btnViewExport;
    private JTable  viewTable;
    private DefaultTableModel viewTableModel;
    private JScrollPane viewScroll;

    private final Map<Integer, Integer> viewSectionIndexToId = new HashMap<>();
    private final Map<Integer, Integer> viewSessionIndexToId = new HashMap<>();

    // ── shared state ─────────────────────────────────────────────────────
    private Professor professor;
    private User      currentUser;

    private final ProfessorSectionService   sectionService    = new ProfessorSectionService();
    private final ClassSessionService       sessionService    = new ClassSessionService();
    private final AttendanceQueryService    attendanceQuery   = new AttendanceQueryService();
    private final AttendanceRecordingService attendanceRecord = new AttendanceRecordingService();
    private final SecretaryStudentDAO       secretaryDAO      = new SecretaryStudentDAO();
    private final AttendancePermissionStore permStore         = AttendancePermissionStore.getInstance();

    // tracks the professorSectionId row needed for DB flag persistence
    private final java.util.Map<Integer, Integer> recSectionIdToProfessorId = new java.util.HashMap<>();

    // ─────────────────────────────────────────────────────────────────────
    public ProfessorAttendancePanel() {
        setLayout(null);
        setBackground(Color.WHITE);

        buildTabBar();
        buildCardHost();
        buildRecordCard();
        buildViewCard();

        // default to Record tab
        switchTab(CARD_RECORD);
    }

    // ── tab bar ──────────────────────────────────────────────────────────
    private void buildTabBar() {
        tabBar = new JPanel(null);
        tabBar.setBackground(Color.WHITE);
        tabBar.setBounds(0, 0, 800, 44);

        btnTabRecord = tabButton("Record Attendance");
        btnTabRecord.addActionListener(e -> switchTab(CARD_RECORD));
        tabBar.add(btnTabRecord);

        btnTabView = tabButton("View Attendance");
        btnTabView.addActionListener(e -> switchTab(CARD_VIEW));
        tabBar.add(btnTabView);

        add(tabBar);
    }

    private JButton tabButton(String text) {
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

    private void switchTab(String card) {
        ((CardLayout) cardHost.getLayout()).show(cardHost, card);

        Color active   = new Color(255, 140, 0);
        Color inactive = new Color(200, 200, 200);

        if (CARD_RECORD.equals(card)) {
            btnTabRecord.setForeground(active);
            btnTabRecord.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, active));
            btnTabView.setForeground(new Color(100, 100, 100));
            btnTabView.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, inactive));
        } else {
            btnTabView.setForeground(active);
            btnTabView.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, active));
            btnTabRecord.setForeground(new Color(100, 100, 100));
            btnTabRecord.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, inactive));
        }
    }

    // ── card host ─────────────────────────────────────────────────────────
    private void buildCardHost() {
        cardHost = new JPanel(new CardLayout());
        cardHost.setBackground(Color.WHITE);
        add(cardHost);
    }

    // ── RECORD card ───────────────────────────────────────────────────────
    private void buildRecordCard() {
        recordCard = new JPanel(null);
        recordCard.setBackground(Color.WHITE);

        lblRecTitle = new JLabel("Record Attendance");
        lblRecTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblRecTitle.setForeground(new Color(60, 60, 60));
        recordCard.add(lblRecTitle);

        lblRecSub = new JLabel("Select a section and session, then mark student attendance.");
        lblRecSub.setFont(new Font("Arial", Font.PLAIN, 14));
        recordCard.add(lblRecSub);

        recSep = new JSeparator();
        recSep.setForeground(new Color(220, 220, 220));
        recordCard.add(recSep);

        // section combo
        cmbRecSection = buildCombo();
        cmbRecSection.addItem("Select Section");
        cmbRecSection.addActionListener(this);
        recordCard.add(cmbRecSection);

        // session combo
        cmbRecSession = buildCombo();
        cmbRecSession.addItem("Select Session");
        recordCard.add(cmbRecSession);

        // Load button
        btnRecLoad = actionButton("Load Students", new Color(255, 140, 0));
        btnRecLoad.addActionListener(this);
        recordCard.add(btnRecLoad);

        // Mark buttons
        btnRecPresent = actionButton("Mark Present", new Color(40, 167, 69));
        btnRecPresent.addActionListener(this);
        recordCard.add(btnRecPresent);

        btnRecLate = actionButton("Mark Late", new Color(255, 193, 7));
        btnRecLate.addActionListener(this);
        recordCard.add(btnRecLate);

        btnRecAbsent = actionButton("Mark Absent", new Color(220, 53, 69));
        btnRecAbsent.addActionListener(this);
        recordCard.add(btnRecAbsent);

        // Secretary toggle button
        btnToggleSecretary = new JButton("Allow Secretary to Take Attendance: OFF");
        btnToggleSecretary.setFont(new Font("Arial", Font.PLAIN, 13));
        btnToggleSecretary.setForeground(Color.WHITE);
        btnToggleSecretary.setBackground(new Color(108, 117, 125));
        btnToggleSecretary.setBorder(null);
        btnToggleSecretary.setFocusPainted(false);
        btnToggleSecretary.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToggleSecretary.addActionListener(this);
        recordCard.add(btnToggleSecretary);

        // Table
        String[] cols = {"Student No.", "Name", "Status", "Time Recorded"};
        recTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        recTable = buildTable(recTableModel);
        recScroll = buildScroll(recTable);
        recordCard.add(recScroll);

        cardHost.add(recordCard, CARD_RECORD);
    }

    // ── VIEW card ─────────────────────────────────────────────────────────
    private void buildViewCard() {
        viewCard = new JPanel(null);
        viewCard.setBackground(Color.WHITE);

        lblViewTitle = new JLabel("View Attendance");
        lblViewTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblViewTitle.setForeground(new Color(60, 60, 60));
        viewCard.add(lblViewTitle);

        lblViewSub = new JLabel("Select a section and session to view recorded attendance.");
        lblViewSub.setFont(new Font("Arial", Font.PLAIN, 14));
        viewCard.add(lblViewSub);

        viewSep = new JSeparator();
        viewSep.setForeground(new Color(220, 220, 220));
        viewCard.add(viewSep);

        cmbViewSection = buildCombo();
        cmbViewSection.addItem("Select Section");
        cmbViewSection.addActionListener(this);
        viewCard.add(cmbViewSection);

        cmbViewSession = buildCombo();
        cmbViewSession.addItem("Select Session");
        viewCard.add(cmbViewSession);

        btnViewLoad = actionButton("Load", new Color(255, 140, 0));
        btnViewLoad.addActionListener(this);
        viewCard.add(btnViewLoad);

        btnViewExport = new JButton("Export");
        btnViewExport.setFont(new Font("Arial", Font.BOLD, 14));
        btnViewExport.setForeground(new Color(100, 100, 100));
        btnViewExport.setBackground(Color.WHITE);
        btnViewExport.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btnViewExport.setFocusPainted(false);
        btnViewExport.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnViewExport.addActionListener(this);
        viewCard.add(btnViewExport);

        String[] cols = {"Student No.", "Name", "Status", "Time", "Recorded By"};
        viewTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        viewTable  = buildTable(viewTableModel);
        viewScroll = buildScroll(viewTable);
        viewCard.add(viewScroll);

        cardHost.add(viewCard, CARD_VIEW);
    }

    // ── helpers ───────────────────────────────────────────────────────────
    private JComboBox<String> buildCombo() {
        JComboBox<String> c = new JComboBox<>();
        c.setFont(new Font("Arial", Font.PLAIN, 14));
        c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        c.setFocusable(false);
        c.setRenderer(new BasicComboBoxRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                lbl.setBorder(new EmptyBorder(0, 10, 0, 0));
                return lbl;
            }
        });
        return c;
    }

    private JButton actionButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.PLAIN, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setBorder(null);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JTable buildTable(DefaultTableModel model) {
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
        for (int i = 0; i < t.getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        return t;
    }

    private JScrollPane buildScroll(JTable t) {
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    // ── public setters ────────────────────────────────────────────────────
    public void setProfessor(Professor professor) {
        this.professor = professor;
        loadRecordSections();
        loadViewSections();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    // ── Record card logic ─────────────────────────────────────────────────
    private void loadRecordSections() {
        cmbRecSection.removeAllItems();
        cmbRecSection.addItem("Select Section");
        recSectionIndexToId.clear();
        if (professor == null) return;
        List<ProfessorSection> sections = sectionService.getSectionsByProfessor(professor.professorId());
        int idx = 1;
        for (ProfessorSection ps : sections) {
            cmbRecSection.addItem(ps.section().sectionCode());
            recSectionIndexToId.put(idx++, ps.section().sectionId());
        }
    }

    private void loadRecordSessionsForSection() {
        cmbRecSession.removeAllItems();
        cmbRecSession.addItem("Select Session");
        recSessionIndexToId.clear();
        if (professor == null || cmbRecSection.getSelectedIndex() <= 0) return;
        int sectionId = recSectionIndexToId.get(cmbRecSection.getSelectedIndex());
        List<ClassSession> sessions = sessionService.getClassSessionsBySection(sectionId);
        int idx = 1;
        for (ClassSession s : sessions) {
            String display = s.course().courseCode() + " - " + s.sessionDate() + " (" + s.startTime() + ")";
            cmbRecSession.addItem(display);
            recSessionIndexToId.put(idx++, s.sessionId());
        }
        // Load the persisted flag from DB into the in-memory store, then refresh button
        boolean secretaryAllowed = sectionService.getRecordingFlag(professor.professorId(), sectionId);
        if (secretaryAllowed) {
            permStore.grantSecretary(sectionId);
        } else {
            permStore.revokeSecretary(sectionId);
        }
        refreshToggleButton(sectionId);
    }

    private void refreshToggleButton(int sectionId) {
        boolean allowed = permStore.isSecretaryAllowed(sectionId);
        updateToggleAppearance(allowed);
    }

    private void updateToggleAppearance(boolean allowed) {
        if (allowed) {
            btnToggleSecretary.setText("Allow Secretary to Take Attendance: ON");
            btnToggleSecretary.setBackground(new Color(40, 167, 69));
        } else {
            btnToggleSecretary.setText("Allow Secretary to Take Attendance: OFF");
            btnToggleSecretary.setBackground(new Color(108, 117, 125));
        }
    }

    private void loadStudentsForRecordSession() {
        if (professor == null || cmbRecSection.getSelectedIndex() <= 0 || cmbRecSession.getSelectedIndex() <= 0) return;

        int sectionId = recSectionIndexToId.get(cmbRecSection.getSelectedIndex());
        int sessionId = recSessionIndexToId.get(cmbRecSession.getSelectedIndex());

        List<Student> students = secretaryDAO.findStudentsBySectionId(sectionId);
        List<Attendance> existing = attendanceQuery.getAttendancesBySession(sessionId);
        Map<Integer, Attendance> attMap = new HashMap<>();
        for (Attendance a : existing) attMap.put(a.student().studentId(), a);

        recTableModel.setRowCount(0);
        recRowStudentIds.clear();

        for (Student s : students) {
            recRowStudentIds.add(s.studentId());
            Attendance att = attMap.get(s.studentId());
            String status = att != null ? att.status().getStatusName() : "Not Recorded";
            recTableModel.addRow(new Object[]{s.studentNumber(), s.firstName() + " " + s.lastName(), status, "-"});
        }
    }

    private void markRecordSelected(AttendanceStatus status) {
        if (professor == null || currentUser == null
                || cmbRecSection.getSelectedIndex() <= 0
                || cmbRecSession.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Please select a section and session first.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int sessionId = recSessionIndexToId.get(cmbRecSession.getSelectedIndex());
        int[] selectedRows = recTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select at least one student.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Integer> newIds = new ArrayList<>();
        for (int row : selectedRows) {
            int studentId = recRowStudentIds.get(row);
            String currentStatus = (String) recTableModel.getValueAt(row, 2);
            if ("Not Recorded".equals(currentStatus)) {
                newIds.add(studentId);
            } else {
                Optional<Attendance> existing = attendanceQuery.getAttendanceBySessionAndStudent(sessionId, studentId);
                if (existing.isPresent()) {
                    Attendance old = existing.get();
                    Attendance updated = Attendance.builder()
                            .attendanceId(old.attendanceId())
                            .session(old.session())
                            .student(old.student())
                            .status(status)
                            .recordedBy(currentUser)
                            .build();
                    boolean ok = attendanceQuery.updateAttendance(updated);
                    if (!ok) {
                        JOptionPane.showMessageDialog(this,
                            "Could not update attendance for " + old.student().getFullName() + ".\nThis student may have exceeded the absence limit.",
                            "Policy Violation", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        }

        if (!newIds.isEmpty()) {
            List<String> dropped = attendanceRecord.bulkRecordAttendance(sessionId, newIds, status, currentUser);
            if (!dropped.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "The following students have exceeded the absence limit:\n" + String.join("\n", dropped),
                    "Policy Violation", JOptionPane.WARNING_MESSAGE);
            }
        }

        loadStudentsForRecordSession();
        JOptionPane.showMessageDialog(this, "Attendance updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleSecretaryToggle() {
        if (professor == null || cmbRecSection.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Please select a section first.", "No Section Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int sectionId = recSectionIndexToId.get(cmbRecSection.getSelectedIndex());
        boolean newState = permStore.toggle(sectionId);

        // Persist to DB: isProfessorRecording is the inverse of secretaryAllowed
        boolean isProfessorRecording = !newState;
        boolean saved = sectionService.updateRecordingFlag(professor.professorId(), sectionId, isProfessorRecording);
        if (!saved) {
            // Revert the in-memory toggle if DB write failed
            permStore.toggle(sectionId);
            JOptionPane.showMessageDialog(this, "Failed to save permission change. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        updateToggleAppearance(newState);
        String sectionCode = (String) cmbRecSection.getSelectedItem();
        String msg = newState
                ? "Secretary is now allowed to take attendance for " + sectionCode + "."
                : "Secretary can no longer take attendance for " + sectionCode + ". Professor will handle it.";
        JOptionPane.showMessageDialog(this, msg, "Permission Updated", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── View card logic ───────────────────────────────────────────────────
    private void loadViewSections() {
        cmbViewSection.removeAllItems();
        cmbViewSection.addItem("Select Section");
        viewSectionIndexToId.clear();
        if (professor == null) return;
        List<ProfessorSection> sections = sectionService.getSectionsByProfessor(professor.professorId());
        int idx = 1;
        for (ProfessorSection ps : sections) {
            cmbViewSection.addItem(ps.section().sectionCode());
            viewSectionIndexToId.put(idx++, ps.section().sectionId());
        }
    }

    private void loadViewSessionsForSection() {
        cmbViewSession.removeAllItems();
        cmbViewSession.addItem("Select Session");
        viewSessionIndexToId.clear();
        if (professor == null || cmbViewSection.getSelectedIndex() <= 0) return;
        int sectionId = viewSectionIndexToId.get(cmbViewSection.getSelectedIndex());
        List<ClassSession> sessions = sessionService.getClassSessionsBySection(sectionId);
        int idx = 1;
        for (ClassSession s : sessions) {
            String display = s.course().courseCode() + " - " + s.sessionDate() + " (" + s.startTime() + ")";
            cmbViewSession.addItem(display);
            viewSessionIndexToId.put(idx++, s.sessionId());
        }
    }

    private void loadViewAttendance() {
        if (cmbViewSession.getSelectedIndex() <= 0) return;
        int sessionId = viewSessionIndexToId.get(cmbViewSession.getSelectedIndex());
        List<Attendance> list = attendanceQuery.getAttendancesBySession(sessionId);
        viewTableModel.setRowCount(0);
        for (Attendance a : list) {
            String recordedBy = a.recordedBy() != null ? a.recordedBy().userName() : "-";
            viewTableModel.addRow(new Object[]{
                a.student().studentNumber(),
                a.student().firstName() + " " + a.student().lastName(),
                a.status().getStatusName(),
                "-",
                recordedBy
            });
        }
    }

    private void exportAttendanceToCSV() {
        if (viewTable.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No attendance data to export. Please load a session first.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String defaultFileName = "attendance_export.csv";
        if (cmbViewSection.getSelectedIndex() > 0 && cmbViewSession.getSelectedIndex() > 0) {
            String sec  = cmbViewSection.getSelectedItem().toString().replaceAll("[^a-zA-Z0-9_-]", "_");
            String sess = cmbViewSession.getSelectedItem().toString().replaceAll("[^a-zA-Z0-9_-]", "_").replace("__", "_");
            defaultFileName = "attendance_" + sec + "_" + sess + ".csv";
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Attendance to CSV");
        chooser.setSelectedFile(new File(defaultFileName));
        chooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".csv");
        }

        try {
            StringBuilder csv = new StringBuilder();
            csv.append("Student No.,Name,Status,Time,Recorded By\n");
            for (int i = 0; i < viewTable.getRowCount(); i++) {
                for (int j = 0; j < viewTable.getColumnCount(); j++) {
                    String cell = viewTable.getValueAt(i, j) != null ? viewTable.getValueAt(i, j).toString() : "";
                    if (cell.contains(",") || cell.contains("\"") || cell.contains("\n")) {
                        cell = "\"" + cell.replace("\"", "\"\"") + "\"";
                    }
                    csv.append(cell);
                    if (j < viewTable.getColumnCount() - 1) csv.append(",");
                }
                csv.append("\n");
            }
            java.nio.file.Files.writeString(file.toPath(), csv.toString());
            JOptionPane.showMessageDialog(this, "Exported successfully to:\n" + file.getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // ── layout ────────────────────────────────────────────────────────────
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        if (width <= 0 || height <= 0) return;

        int tabH = 44;
        tabBar.setBounds(0, 0, width, tabH);
        btnTabRecord.setBounds(40, 10, 200, 28);
        btnTabView.setBounds(260, 10, 180, 28);

        cardHost.setBounds(0, tabH, width, height - tabH);

        // record card layout
        if (recordCard != null) {
            int cardW = width;
            int cardH = height - tabH;

            lblRecTitle.setBounds(40, 20, 400, 30);
            lblRecSub.setBounds(40, 50, 700, 30);
            recSep.setBounds(40, 82, cardW - 80, 1);

            // combos + load
            cmbRecSection.setBounds(40, 100, 200, 36);
            cmbRecSession.setBounds(260, 100, 260, 36);
            btnRecLoad.setBounds(540, 100, 130, 36);

            // toggle button — left side of row 2
            btnToggleSecretary.setBounds(40, 148, 320, 32);

            // mark buttons — right side of row 2
            int rightMargin = 40;
            int gap = 12;
            int absentW = 130;
            int lateW   = 110;
            int presentW = 140;
            int absentX  = cardW - rightMargin - absentW;
            btnRecAbsent.setBounds(absentX, 148, absentW, 32);
            int lateX = absentX - gap - lateW;
            btnRecLate.setBounds(lateX, 148, lateW, 32);
            int presentX = lateX - gap - presentW;
            btnRecPresent.setBounds(presentX, 148, presentW, 32);

            recScroll.setBounds(40, 192, cardW - 80, cardH - 220);
        }

        // view card layout
        if (viewCard != null) {
            int cardW = width;
            int cardH = height - tabH;

            lblViewTitle.setBounds(40, 20, 400, 30);
            lblViewSub.setBounds(40, 50, 700, 30);
            viewSep.setBounds(40, 82, cardW - 80, 1);

            cmbViewSection.setBounds(40, 100, 200, 36);
            cmbViewSession.setBounds(260, 100, 260, 36);

            int rightMargin = 40;
            int gap = 12;
            int exportW = 120;
            int loadW   = 120;
            int exportX = cardW - rightMargin - exportW;
            btnViewExport.setBounds(exportX, 100, exportW, 36);
            int loadX = exportX - gap - loadW;
            btnViewLoad.setBounds(loadX, 100, loadW, 36);

            viewScroll.setBounds(40, 150, cardW - 80, cardH - 180);
        }
    }

    // ── action handling ───────────────────────────────────────────────────
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        // Record tab
        if (src == cmbRecSection)       loadRecordSessionsForSection();
        else if (src == btnRecLoad)     loadStudentsForRecordSession();
        else if (src == btnRecPresent)  markRecordSelected(AttendanceStatus.PRESENT);
        else if (src == btnRecLate)     markRecordSelected(AttendanceStatus.LATE);
        else if (src == btnRecAbsent)   markRecordSelected(AttendanceStatus.ABSENT);
        else if (src == btnToggleSecretary) handleSecretaryToggle();

        // View tab
        else if (src == cmbViewSection) loadViewSessionsForSection();
        else if (src == btnViewLoad)    loadViewAttendance();
        else if (src == btnViewExport)  exportAttendanceToCSV();
    }
}
