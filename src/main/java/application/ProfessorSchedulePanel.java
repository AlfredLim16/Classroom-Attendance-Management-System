package application;

import course.Course;
import course.Section;
import event.SchoolEvent;
import event.SchoolEventService;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import session.ClassSession;
import session.ClassSessionManagementService;
import session.ClassSessionService;
import session.ContextType;
import user.Professor;
import user.ProfessorCourse;
import user.ProfessorCourseService;
import user.ProfessorSection;
import user.ProfessorSectionService;

public class ProfessorSchedulePanel extends JPanel implements ActionListener {

    private JLabel lblTitle, lblSubTitle;
    private JSeparator separator;
    private JButton btnCreateSession, btnCreateQuiz;
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private Professor professor;
    private final ClassSessionService sessionService = new ClassSessionService();
    private final ClassSessionManagementService managementService = new ClassSessionManagementService();
    private final ProfessorSectionService sectionService = new ProfessorSectionService();
    private final ProfessorCourseService courseService = new ProfessorCourseService();
    private final SchoolEventService eventService = new SchoolEventService();

    private final List<ClassSession> rowSessions = new java.util.ArrayList<>();

    public ProfessorSchedulePanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("Manage Class Schedule");
        lblTitle.setBounds(40, 20, 300, 30);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle);

        lblSubTitle = new JLabel("View and organize your upcoming classes, quizzes, and lab sessions in one place.");
        lblSubTitle.setBounds(40, 50, 800, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblSubTitle);

        separator = new JSeparator();
        separator.setForeground(Color.BLACK);
        add(separator);

        btnCreateSession = new JButton("+ Class Session");
        btnCreateSession.setBounds(40, 100, 140, 36);
        btnCreateSession.setFont(new Font("Arial", Font.PLAIN, 14));
        btnCreateSession.setForeground(Color.WHITE);
        btnCreateSession.setBackground(new Color(255, 140, 0));
        btnCreateSession.setBorder(null);
        btnCreateSession.setFocusPainted(false);
        btnCreateSession.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCreateSession.addActionListener(this);
        add(btnCreateSession);

        btnCreateQuiz = new JButton("+ Quiz/Lab");
        btnCreateQuiz.setBounds(190, 100, 120, 36);
        btnCreateQuiz.setFont(new Font("Arial", Font.PLAIN, 14));
        btnCreateQuiz.setForeground(new Color(100, 100, 100));
        btnCreateQuiz.setBackground(Color.WHITE);
        btnCreateQuiz.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btnCreateQuiz.setFocusPainted(false);
        btnCreateQuiz.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCreateQuiz.addActionListener(this);
        add(btnCreateQuiz);

        String[] columns = {"Date", "Course", "Type", "Start", "End", "Section", "Context"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        scheduleTable = new JTable(tableModel);
        scheduleTable.setRowHeight(28);
        scheduleTable.setFillsViewportHeight(true);
        scheduleTable.getTableHeader().setReorderingAllowed(false);
        scheduleTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        scheduleTable.getTableHeader().setBackground(new Color(255, 255, 255));
        scheduleTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        scheduleTable.getTableHeader().setPreferredSize(new Dimension(scheduleTable.getPreferredSize().width, 28));

        DefaultTableCellRenderer centerCollumn = new DefaultTableCellRenderer();
        centerCollumn.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < scheduleTable.getColumnCount(); i++){
            scheduleTable.getColumnModel().getColumn(i).setCellRenderer(centerCollumn);
        }

        scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane);
    }

    public void setProfessor(Professor professor){
        this.professor = professor;
        loadSchedule();
    }

    private void loadSchedule(){
        if(professor == null){
            return;
        }
        tableModel.setRowCount(0);
        rowSessions.clear();

        List<ClassSession> sessions = sessionService.getClassSessionsByProfessor(professor.professorId());
        for(ClassSession s : sessions){
            rowSessions.add(s);
            String context = s.contextType() == ContextType.SCHOOL_EVENT && s.event() != null
                ? "Event: " + s.event().eventName()
                : s.contextType().getContextName();
            addScheduleRow(
                s.sessionDate().toString(),
                s.course().courseCode() + " - " + s.course().courseName(),
                s.course().courseCode(),
                s.startTime().toString(),
                s.endTime().toString(),
                s.section().sectionCode(),
                context
            );
        }
    }

    private void showCreateSessionDialog(){
        if(professor == null){
            return;
        }

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Create Class Session");
        dialog.setModal(true);
        dialog.setSize(480, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setResizable(false);

        JLabel accent = new JLabel("");
        accent.setBounds(0, 0, 4, 520);
        accent.setBackground(new Color(255, 140, 0));
        accent.setOpaque(true);
        dialog.add(accent);

        JLabel lblHeader = new JLabel("Create New Class Session");
        lblHeader.setBounds(30, 20, 300, 30);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));
        lblHeader.setForeground(new Color(60, 60, 60));
        dialog.add(lblHeader);

        JSeparator sep = new JSeparator();
        sep.setBounds(30, 55, 420, 1);
        sep.setForeground(new Color(220, 220, 220));
        dialog.add(sep);

        // Course
        JLabel lblCourse = new JLabel("Course");
        lblCourse.setBounds(30, 70, 120, 25);
        lblCourse.setFont(new Font("Arial", Font.BOLD, 13));
        lblCourse.setForeground(new Color(100, 100, 100));
        dialog.add(lblCourse);

        JComboBox<String> cmbCourse = new JComboBox<>();
        cmbCourse.setBounds(150, 70, 280, 32);
        cmbCourse.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbCourse.setBackground(Color.WHITE);
        cmbCourse.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbCourse.setFocusable(false);
        Map<Integer, Integer> courseMap = new HashMap<>();
        List<ProfessorCourse> courses = courseService.getCoursesByProfessor(professor.professorId());
        int idx = 0;
        for(ProfessorCourse pc : courses){
            cmbCourse.addItem(pc.course().courseCode() + " - " + pc.course().courseName());
            courseMap.put(idx, pc.course().courseId());
            idx++;
        }
        dialog.add(cmbCourse);

        // Section
        JLabel lblSection = new JLabel("Section");
        lblSection.setBounds(30, 115, 120, 25);
        lblSection.setFont(new Font("Arial", Font.BOLD, 13));
        lblSection.setForeground(new Color(100, 100, 100));
        dialog.add(lblSection);

        JComboBox<String> cmbSection = new JComboBox<>();
        cmbSection.setBounds(150, 115, 280, 32);
        cmbSection.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbSection.setBackground(Color.WHITE);
        cmbSection.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbSection.setFocusable(false);
        Map<Integer, Integer> sectionMap = new HashMap<>();
        List<ProfessorSection> sections = sectionService.getSectionsByProfessor(professor.professorId());
        idx = 0;
        for(ProfessorSection ps : sections){
            cmbSection.addItem(ps.section().sectionCode());
            sectionMap.put(idx, ps.section().sectionId());
            idx++;
        }
        dialog.add(cmbSection);

        // Date
        JLabel lblDate = new JLabel("Date (YYYY-MM-DD)");
        lblDate.setBounds(30, 160, 150, 25);
        lblDate.setFont(new Font("Arial", Font.BOLD, 13));
        lblDate.setForeground(new Color(100, 100, 100));
        dialog.add(lblDate);

        JTextField txtDate = new JTextField(LocalDate.now().toString());
        txtDate.setBounds(150, 160, 280, 32);
        txtDate.setFont(new Font("Arial", Font.PLAIN, 13));
        txtDate.setForeground(new Color(60, 60, 60));
        txtDate.setBackground(Color.WHITE);
        txtDate.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        dialog.add(txtDate);

        // Start Time
        JLabel lblStart = new JLabel("Start Time (HH:MM)");
        lblStart.setBounds(30, 205, 150, 25);
        lblStart.setFont(new Font("Arial", Font.BOLD, 13));
        lblStart.setForeground(new Color(100, 100, 100));
        dialog.add(lblStart);

        JTextField txtStart = new JTextField("08:00");
        txtStart.setBounds(150, 205, 120, 32);
        txtStart.setFont(new Font("Arial", Font.PLAIN, 13));
        txtStart.setForeground(new Color(60, 60, 60));
        txtStart.setBackground(Color.WHITE);
        txtStart.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        dialog.add(txtStart);

        // End Time
        JLabel lblEnd = new JLabel("End Time (HH:MM)");
        lblEnd.setBounds(30, 250, 150, 25);
        lblEnd.setFont(new Font("Arial", Font.BOLD, 13));
        lblEnd.setForeground(new Color(100, 100, 100));
        dialog.add(lblEnd);

        JTextField txtEnd = new JTextField("10:00");
        txtEnd.setBounds(150, 250, 120, 32);
        txtEnd.setFont(new Font("Arial", Font.PLAIN, 13));
        txtEnd.setForeground(new Color(60, 60, 60));
        txtEnd.setBackground(Color.WHITE);
        txtEnd.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        dialog.add(txtEnd);

        // Context Type
        JLabel lblContext = new JLabel("Context");
        lblContext.setBounds(30, 295, 120, 25);
        lblContext.setFont(new Font("Arial", Font.BOLD, 13));
        lblContext.setForeground(new Color(100, 100, 100));
        dialog.add(lblContext);

        JComboBox<String> cmbContext = new JComboBox<>(new String[]{"Classroom", "School Event"});
        cmbContext.setBounds(150, 295, 280, 32);
        cmbContext.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbContext.setBackground(Color.WHITE);
        cmbContext.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbContext.setFocusable(false);
        dialog.add(cmbContext);

        // School Event (optional)
        JLabel lblEvent = new JLabel("Event (optional)");
        lblEvent.setBounds(30, 340, 120, 25);
        lblEvent.setFont(new Font("Arial", Font.BOLD, 13));
        lblEvent.setForeground(new Color(100, 100, 100));
        dialog.add(lblEvent);

        JComboBox<String> cmbEvent = new JComboBox<>();
        cmbEvent.addItem("None");
        cmbEvent.setBounds(150, 340, 280, 32);
        cmbEvent.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbEvent.setBackground(Color.WHITE);
        cmbEvent.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbEvent.setFocusable(false);
        Map<Integer, Integer> eventMap = new HashMap<>();
        List<SchoolEvent> events = eventService.getAllSchoolEvents();
        idx = 1;
        for(SchoolEvent ev : events){
            cmbEvent.addItem(ev.eventName() + " (" + ev.eventDate() + ")");
            eventMap.put(idx, ev.eventId());
            idx++;
        }
        dialog.add(cmbEvent);

        // Buttons
        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBounds(230, 420, 100, 36);
        btnCancel.setFont(new Font("Arial", Font.PLAIN, 14));
        btnCancel.setForeground(new Color(100, 100, 100));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.add(btnCancel);

        JButton btnSave = new JButton("Create");
        btnSave.setBounds(340, 420, 100, 36);
        btnSave.setFont(new Font("Arial", Font.PLAIN, 14));
        btnSave.setForeground(Color.WHITE);
        btnSave.setBackground(new Color(255, 140, 0));
        btnSave.setBorder(null);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> {
            try{
                if(cmbCourse.getSelectedIndex() < 0 || cmbSection.getSelectedIndex() < 0){
                    JOptionPane.showMessageDialog(dialog, "Please select course and section.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int courseId = courseMap.get(cmbCourse.getSelectedIndex());
                int sectionId = sectionMap.get(cmbSection.getSelectedIndex());
                LocalDate date = LocalDate.parse(txtDate.getText().trim());
                LocalTime start = LocalTime.parse(txtStart.getText().trim());
                LocalTime end = LocalTime.parse(txtEnd.getText().trim());

                Course selectedCourse = courses.get(cmbCourse.getSelectedIndex()).course();
                Section selectedSection = sections.get(cmbSection.getSelectedIndex()).section();

                ClassSession session;
                if(cmbContext.getSelectedIndex() == 1 && cmbEvent.getSelectedIndex() > 0){
                    int eventId = eventMap.get(cmbEvent.getSelectedIndex());
                    SchoolEvent event = eventService.getSchoolEventById(eventId).orElse(null);
                    session = managementService.createSchoolEventSession(
                        selectedCourse, selectedSection, professor, date, start, end, event);
                }else{
                    session = managementService.createClassroomSession(
                        selectedCourse, selectedSection, professor, date, start, end);
                }

                JOptionPane.showMessageDialog(dialog, "Class session created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadSchedule();

            }catch(Exception ex){
                JOptionPane.showMessageDialog(dialog, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(btnSave);

        dialog.setVisible(true);
    }

    private void showCreateQuizDialog(){
        JOptionPane.showMessageDialog(this,
            "Quiz/Lab scheduling feature.\n\nThis would open a dialog to schedule quizzes/labs for assigned courses.",
            "Create Quiz/Lab", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x, y, width, height);
        if(width > 0 && height > 0 && scrollPane != null){
            separator.setBounds(40, 80, width - 80, height - 160);
            scrollPane.setBounds(40, 150, width - 80, height - 180);
        }
    }

    public void addScheduleRow(String date, String course, String type, String start, String end, String section, String context){
        tableModel.addRow(new Object[]{date, course, type, start, end, section, context});
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnCreateSession){
            showCreateSessionDialog();
        }
        if(e.getSource() == btnCreateQuiz){
            showCreateQuizDialog();
        }
    }
}
