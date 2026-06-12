package panels;

import core.Course;
import core.Professor;
import core.SchoolEvent;
import core.Section;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import junction.ClassSession;
import junction.ProfessorCourse;
import junction.ProfessorSection;
import junction.QuizLabSchedule;
import lookup.ContextType;
import lookup.QuizType;
import services.ClassSessionManagementService;
import services.ClassSessionService;
import services.ProfessorCourseService;
import services.ProfessorSectionService;
import services.QuizLabScheduleService;
import services.SchoolEventService;

public class ProfessorSchedulePanel extends JPanel implements ActionListener {

    private static final String CARD_SESSIONS = "SESSIONS";
    private static final String CARD_QUIZ     = "QUIZ";

    private JPanel tabBar;
    private JButton btnTabSessions, btnTabQuiz;
    private JPanel cardHost;

    private JPanel sessionsCard;
    private JLabel lblSesTitle, lblSesSub;
    private JSeparator sesSep;
    private JButton btnCreateSession, btnDeleteSession;
    private JTable sessionsTable;
    private DefaultTableModel sessionsModel;
    private JScrollPane sessionsScroll;
    private final List<ClassSession> sessionRows = new ArrayList<>();

    private JPanel quizCard;
    private JLabel lblQuizTitle, lblQuizSub;
    private JSeparator quizSep;
    private JButton btnCreateQuiz, btnDeleteQuiz;
    private JTable quizTable;
    private DefaultTableModel quizModel;
    private JScrollPane quizScroll;
    private final List<QuizLabSchedule> quizRows = new ArrayList<>();

    private Professor professor;
    private final ClassSessionService sessionService         = new ClassSessionService();
    private final ClassSessionManagementService manageSvc   = new ClassSessionManagementService();
    private final ProfessorSectionService sectionService    = new ProfessorSectionService();
    private final ProfessorCourseService courseService      = new ProfessorCourseService();
    private final SchoolEventService eventService           = new SchoolEventService();
    private final QuizLabScheduleService quizLabService     = new QuizLabScheduleService();

    public ProfessorSchedulePanel(){
        setLayout(null);
        setBackground(Color.WHITE);
        buildTabBar();
        buildCardHost();
        buildSessionsCard();
        buildQuizCard();
        switchTab(CARD_SESSIONS);
    }

    private void buildTabBar(){
        tabBar = new JPanel(null);
        tabBar.setBackground(Color.WHITE);
        add(tabBar);

        btnTabSessions = tabButton("Class Sessions");
        btnTabSessions.addActionListener(e -> switchTab(CARD_SESSIONS));
        tabBar.add(btnTabSessions);

        btnTabQuiz = tabButton("Quiz / Lab Schedules");
        btnTabQuiz.addActionListener(e -> switchTab(CARD_QUIZ));
        tabBar.add(btnTabQuiz);
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
        if(CARD_SESSIONS.equals(card)){
            btnTabSessions.setForeground(active);
            btnTabSessions.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, active));
            btnTabQuiz.setForeground(new Color(100, 100, 100));
            btnTabQuiz.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, inactive));
        } else {
            btnTabQuiz.setForeground(active);
            btnTabQuiz.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, active));
            btnTabSessions.setForeground(new Color(100, 100, 100));
            btnTabSessions.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, inactive));
        }
    }

    private void buildCardHost(){
        cardHost = new JPanel(new CardLayout());
        cardHost.setBackground(Color.WHITE);
        add(cardHost);
    }

    private void buildSessionsCard(){
        sessionsCard = new JPanel(null);
        sessionsCard.setBackground(Color.WHITE);

        lblSesTitle = new JLabel("Class Sessions");
        lblSesTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblSesTitle.setForeground(new Color(60, 60, 60));
        sessionsCard.add(lblSesTitle);

        lblSesSub = new JLabel("Create and manage your classroom and school event sessions.");
        lblSesSub.setFont(new Font("Arial", Font.PLAIN, 14));
        sessionsCard.add(lblSesSub);

        sesSep = new JSeparator();
        sesSep.setForeground(new Color(220, 220, 220));
        sessionsCard.add(sesSep);

        btnCreateSession = new JButton("+ Class Session");
        btnCreateSession.setFont(new Font("Arial", Font.PLAIN, 14));
        btnCreateSession.setForeground(Color.WHITE);
        btnCreateSession.setBackground(new Color(255, 140, 0));
        btnCreateSession.setBorder(null);
        btnCreateSession.setFocusPainted(false);
        btnCreateSession.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCreateSession.addActionListener(this);
        sessionsCard.add(btnCreateSession);

        btnDeleteSession = new JButton("Delete");
        btnDeleteSession.setFont(new Font("Arial", Font.PLAIN, 14));
        btnDeleteSession.setForeground(Color.WHITE);
        btnDeleteSession.setBackground(new Color(220, 53, 69));
        btnDeleteSession.setBorder(null);
        btnDeleteSession.setFocusPainted(false);
        btnDeleteSession.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDeleteSession.addActionListener(this);
        sessionsCard.add(btnDeleteSession);

        String[] cols = {"Date", "Course", "Context", "Start", "End", "Section"};
        sessionsModel = new DefaultTableModel(cols, 0){
            @Override public boolean isCellEditable(int r, int c){ return false; }
        };
        sessionsTable = buildTable(sessionsModel);
        sessionsScroll = buildScroll(sessionsTable);
        sessionsCard.add(sessionsScroll);

        cardHost.add(sessionsCard, CARD_SESSIONS);
    }

    private void buildQuizCard(){
        quizCard = new JPanel(null);
        quizCard.setBackground(Color.WHITE);

        lblQuizTitle = new JLabel("Quiz / Lab Schedules");
        lblQuizTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblQuizTitle.setForeground(new Color(60, 60, 60));
        quizCard.add(lblQuizTitle);

        lblQuizSub = new JLabel("Schedule quizzes, labs, and exams for your courses.");
        lblQuizSub.setFont(new Font("Arial", Font.PLAIN, 14));
        quizCard.add(lblQuizSub);

        quizSep = new JSeparator();
        quizSep.setForeground(new Color(220, 220, 220));
        quizCard.add(quizSep);

        btnCreateQuiz = new JButton("+ Quiz / Lab");
        btnCreateQuiz.setFont(new Font("Arial", Font.PLAIN, 14));
        btnCreateQuiz.setForeground(Color.WHITE);
        btnCreateQuiz.setBackground(new Color(255, 140, 0));
        btnCreateQuiz.setBorder(null);
        btnCreateQuiz.setFocusPainted(false);
        btnCreateQuiz.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCreateQuiz.addActionListener(this);
        quizCard.add(btnCreateQuiz);

        btnDeleteQuiz = new JButton("Delete");
        btnDeleteQuiz.setFont(new Font("Arial", Font.PLAIN, 14));
        btnDeleteQuiz.setForeground(Color.WHITE);
        btnDeleteQuiz.setBackground(new Color(220, 53, 69));
        btnDeleteQuiz.setBorder(null);
        btnDeleteQuiz.setFocusPainted(false);
        btnDeleteQuiz.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDeleteQuiz.addActionListener(this);
        quizCard.add(btnDeleteQuiz);

        String[] cols = {"Date", "Course", "Type"};
        quizModel = new DefaultTableModel(cols, 0){
            @Override public boolean isCellEditable(int r, int c){ return false; }
        };
        quizTable = buildTable(quizModel);
        quizScroll = buildScroll(quizTable);
        quizCard.add(quizScroll);

        cardHost.add(quizCard, CARD_QUIZ);
    }

    public void setProfessor(Professor professor){
        this.professor = professor;
        loadSessions();
        loadQuizSchedules();
    }

    private void loadSessions(){
        sessionsModel.setRowCount(0);
        sessionRows.clear();
        if(professor == null) return;
        List<ClassSession> sessions = sessionService.getClassSessionsByProfessor(professor.professorId());
        for(ClassSession s : sessions){
            sessionRows.add(s);
            String context = s.contextType() == ContextType.SCHOOL_EVENT && s.event() != null
                ? "Event: " + s.event().eventName()
                : s.contextType().getContextName();
            sessionsModel.addRow(new Object[]{
                s.sessionDate().toString(),
                s.course().courseCode() + " - " + s.course().courseName(),
                context,
                s.startTime().toString(),
                s.endTime().toString(),
                s.section().sectionCode()
            });
        }
    }

    private void loadQuizSchedules(){
        quizModel.setRowCount(0);
        quizRows.clear();
        if(professor == null) return;
        List<ProfessorCourse> courses = courseService.getCoursesByProfessor(professor.professorId());
        for(ProfessorCourse pc : courses){
            List<QuizLabSchedule> schedules = quizLabService.getSchedulesByCourse(pc.course().courseId());
            for(QuizLabSchedule q : schedules){
                quizRows.add(q);
                quizModel.addRow(new Object[]{
                    q.quizDate().toString(),
                    q.course().courseCode() + " - " + q.course().courseName(),
                    q.quizType().getQuizTypeName()
                });
            }
        }
    }

    private void showCreateSessionDialog(){
        if(professor == null) return;

        List<ProfessorCourse> courses  = courseService.getCoursesByProfessor(professor.professorId());
        List<ProfessorSection> sections = sectionService.getSectionsByProfessor(professor.professorId());
        List<SchoolEvent> events       = eventService.getAllSchoolEvents();

        if(courses.isEmpty() || sections.isEmpty()){
            JOptionPane.showMessageDialog(this, "You need at least one assigned course and section to create a session.", "No Assignments", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Create Class Session");
        dialog.setModal(true);
        dialog.setSize(480, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setResizable(false);

        JLabel accent = new JLabel("");
        accent.setBounds(0, 0, 4, 480);
        accent.setBackground(new Color(255, 140, 0));
        accent.setOpaque(true);
        dialog.add(accent);

        JLabel lblHeader = new JLabel("Create New Class Session");
        lblHeader.setBounds(30, 20, 300, 28);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));
        lblHeader.setForeground(new Color(60, 60, 60));
        dialog.add(lblHeader);

        JSeparator sep = new JSeparator();
        sep.setBounds(30, 54, 420, 1);
        sep.setForeground(new Color(220, 220, 220));
        dialog.add(sep);

        int y = 70, gap = 46;

        JLabel lblCourse = fieldLabel("Course", 30, y); dialog.add(lblCourse);
        JComboBox<String> cmbCourse = buildDialogCombo(160, y, 280, dialog);
        Map<Integer, Course> courseMap = new HashMap<>();
        for(int i = 0; i < courses.size(); i++){
            cmbCourse.addItem(courses.get(i).course().courseCode() + " - " + courses.get(i).course().courseName());
            courseMap.put(i, courses.get(i).course());
        }
        y += gap;

        JLabel lblSection = fieldLabel("Section", 30, y); dialog.add(lblSection);
        JComboBox<String> cmbSection = buildDialogCombo(160, y, 280, dialog);
        Map<Integer, Section> sectionMap = new HashMap<>();
        for(int i = 0; i < sections.size(); i++){
            cmbSection.addItem(sections.get(i).section().sectionCode());
            sectionMap.put(i, sections.get(i).section());
        }
        y += gap;

        JLabel lblDate = fieldLabel("Date (YYYY-MM-DD)", 30, y); dialog.add(lblDate);
        JTextField txtDate = fieldText(160, y, 280, LocalDate.now().toString(), dialog);
        y += gap;

        JLabel lblStart = fieldLabel("Start Time (HH:MM)", 30, y); dialog.add(lblStart);
        JTextField txtStart = fieldText(160, y, 130, "08:00", dialog);
        JLabel lblEnd = fieldLabel("End", 300, y); dialog.add(lblEnd);
        JTextField txtEnd = fieldText(320, y, 120, "10:00", dialog);
        y += gap;

        JLabel lblCtx = fieldLabel("Context", 30, y); dialog.add(lblCtx);
        JComboBox<String> cmbContext = buildDialogCombo(160, y, 280, dialog);
        cmbContext.addItem("Classroom");
        cmbContext.addItem("School Event");
        y += gap;

        JLabel lblEvent = fieldLabel("Event (optional)", 30, y); dialog.add(lblEvent);
        JComboBox<String> cmbEvent = buildDialogCombo(160, y, 280, dialog);
        cmbEvent.addItem("None");
        Map<Integer, SchoolEvent> eventMap = new HashMap<>();
        for(int i = 0; i < events.size(); i++){
            cmbEvent.addItem(events.get(i).eventName() + " (" + events.get(i).eventDate() + ")");
            eventMap.put(i + 1, events.get(i));
        }
        cmbEvent.setEnabled(false);
        cmbContext.addActionListener(ev -> cmbEvent.setEnabled(cmbContext.getSelectedIndex() == 1));
        y += gap;

        JButton btnCancel = cancelButton(270, y, dialog); dialog.add(btnCancel);
        JButton btnSave = new JButton("Create");
        btnSave.setBounds(370, y, 90, 34);
        styleOrangeButton(btnSave);
        btnSave.addActionListener(ev -> {
            try{
                Course course   = courseMap.get(cmbCourse.getSelectedIndex());
                Section section = sectionMap.get(cmbSection.getSelectedIndex());
                if(course == null || section == null){
                    JOptionPane.showMessageDialog(dialog, "Please select course and section.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                LocalDate date  = LocalDate.parse(txtDate.getText().trim());
                LocalTime start = LocalTime.parse(txtStart.getText().trim());
                LocalTime end   = LocalTime.parse(txtEnd.getText().trim());

                ClassSession session;
                if(cmbContext.getSelectedIndex() == 1 && cmbEvent.getSelectedIndex() > 0){
                    SchoolEvent event = eventMap.get(cmbEvent.getSelectedIndex());
                    session = manageSvc.createSchoolEventSession(course, section, professor, date, start, end, event);
                } else {
                    session = manageSvc.createClassroomSession(course, section, professor, date, start, end);
                }

                if(session != null){
                    JOptionPane.showMessageDialog(dialog, "Session created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadSessions();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to create session. It may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch(Exception ex){
                JOptionPane.showMessageDialog(dialog, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(btnSave);
        dialog.setVisible(true);
    }

    private void deleteSelectedSession(){
        int row = sessionsTable.getSelectedRow();
        if(row < 0 || row >= sessionRows.size()){
            JOptionPane.showMessageDialog(this, "Please select a session to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ClassSession s = sessionRows.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete session: " + s.course().courseCode() + " on " + s.sessionDate() + "?\n\nThis will also delete all attendance records for this session.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if(confirm == JOptionPane.YES_OPTION){
            boolean ok = manageSvc.deleteSession(s.sessionId());
            if(ok){
                loadSessions();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete session.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showCreateQuizDialog(){
        if(professor == null) return;

        List<ProfessorCourse> courses = courseService.getCoursesByProfessor(professor.professorId());
        if(courses.isEmpty()){
            JOptionPane.showMessageDialog(this, "You need at least one assigned course to schedule a quiz.", "No Courses", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Schedule Quiz / Lab");
        dialog.setModal(true);
        dialog.setSize(440, 290);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setResizable(false);

        JLabel accent = new JLabel("");
        accent.setBounds(0, 0, 4, 290);
        accent.setBackground(new Color(255, 140, 0));
        accent.setOpaque(true);
        dialog.add(accent);

        JLabel lblHeader = new JLabel("Schedule Quiz / Lab");
        lblHeader.setBounds(30, 20, 300, 28);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));
        lblHeader.setForeground(new Color(60, 60, 60));
        dialog.add(lblHeader);

        JSeparator sep = new JSeparator();
        sep.setBounds(30, 54, 380, 1);
        sep.setForeground(new Color(220, 220, 220));
        dialog.add(sep);

        int y = 70, gap = 46;

        JLabel lblCourse = fieldLabel("Course", 30, y); dialog.add(lblCourse);
        JComboBox<String> cmbCourse = buildDialogCombo(150, y, 260, dialog);
        Map<Integer, Course> courseMap = new HashMap<>();
        for(int i = 0; i < courses.size(); i++){
            cmbCourse.addItem(courses.get(i).course().courseCode() + " - " + courses.get(i).course().courseName());
            courseMap.put(i, courses.get(i).course());
        }
        y += gap;

        JLabel lblDate = fieldLabel("Date (YYYY-MM-DD)", 30, y); dialog.add(lblDate);
        JTextField txtDate = fieldText(150, y, 260, LocalDate.now().toString(), dialog);
        y += gap;

        JLabel lblType = fieldLabel("Type", 30, y); dialog.add(lblType);
        JComboBox<String> cmbType = buildDialogCombo(150, y, 260, dialog);
        cmbType.addItem("Lab");
        cmbType.addItem("Quiz");
        cmbType.addItem("Exam");
        y += gap;

        JButton btnCancel = cancelButton(230, y, dialog); dialog.add(btnCancel);
        JButton btnSave = new JButton("Schedule");
        btnSave.setBounds(330, y, 90, 34);
        styleOrangeButton(btnSave);
        btnSave.addActionListener(ev -> {
            try{
                Course course = courseMap.get(cmbCourse.getSelectedIndex());
                if(course == null){
                    JOptionPane.showMessageDialog(dialog, "Please select a course.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                LocalDate date = LocalDate.parse(txtDate.getText().trim());
                QuizType type = switch(cmbType.getSelectedIndex()){
                    case 1  -> QuizType.QUIZ;
                    case 2  -> QuizType.EXAM;
                    default -> QuizType.LAB;
                };
                boolean ok = quizLabService.createSchedule(course, date, type);
                if(ok){
                    JOptionPane.showMessageDialog(dialog, "Schedule created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadQuizSchedules();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to create schedule. It may already exist for this course and date.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch(Exception ex){
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(btnSave);
        dialog.setVisible(true);
    }

    private void deleteSelectedQuiz(){
        int row = quizTable.getSelectedRow();
        if(row < 0 || row >= quizRows.size()){
            JOptionPane.showMessageDialog(this, "Please select a schedule to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        QuizLabSchedule q = quizRows.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete " + q.quizType().getQuizTypeName() + " schedule for " + q.course().courseCode() + " on " + q.quizDate() + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if(confirm == JOptionPane.YES_OPTION){
            boolean ok = quizLabService.deleteSchedule(q.quizId());
            if(ok){
                loadQuizSchedules();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete schedule.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JLabel fieldLabel(String text, int x, int y){
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, 130, 25);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(new Color(100, 100, 100));
        return lbl;
    }

    private JTextField fieldText(int x, int y, int w, String defaultVal, JDialog dialog){
        JTextField tf = new JTextField(defaultVal);
        tf.setBounds(x, y, w, 32);
        tf.setFont(new Font("Arial", Font.PLAIN, 13));
        tf.setForeground(new Color(60, 60, 60));
        tf.setBackground(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        dialog.add(tf);
        return tf;
    }

    private JComboBox<String> buildDialogCombo(int x, int y, int w, JDialog dialog){
        JComboBox<String> c = new JComboBox<>();
        c.setBounds(x, y, w, 32);
        c.setFont(new Font("Arial", Font.PLAIN, 13));
        c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        c.setFocusable(false);
        c.setRenderer(new BasicComboBoxRenderer(){
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus){
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setBorder(new EmptyBorder(0, 8, 0, 0));
                return lbl;
            }
        });
        dialog.add(c);
        return c;
    }

    private JButton cancelButton(int x, int y, JDialog dialog){
        JButton b = new JButton("Cancel");
        b.setBounds(x, y, 90, 34);
        b.setFont(new Font("Arial", Font.PLAIN, 13));
        b.setForeground(new Color(100, 100, 100));
        b.setBackground(Color.WHITE);
        b.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addActionListener(ev -> dialog.dispose());
        return b;
    }

    private void styleOrangeButton(JButton b){
        b.setFont(new Font("Arial", Font.PLAIN, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(255, 140, 0));
        b.setBorder(null);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        btnTabSessions.setBounds(40, 10, 160, 28);
        btnTabQuiz.setBounds(220, 10, 200, 28);
        cardHost.setBounds(0, tabH, width, height - tabH);

        int cardH = height - tabH;
        int right = 40, gap = 12;

        if(sessionsCard != null){
            lblSesTitle.setBounds(40, 20, 400, 30);
            lblSesSub.setBounds(40, 50, 700, 30);
            sesSep.setBounds(40, 82, width - 80, 1);

            int delW = 100, addW = 140;
            int delX = width - right - delW;
            btnDeleteSession.setBounds(delX, 100, delW, 36);
            btnCreateSession.setBounds(delX - gap - addW, 100, addW, 36);

            sessionsScroll.setBounds(40, 150, width - 80, cardH - 180);
        }

        if(quizCard != null){
            lblQuizTitle.setBounds(40, 20, 400, 30);
            lblQuizSub.setBounds(40, 50, 700, 30);
            quizSep.setBounds(40, 82, width - 80, 1);

            int delW = 100, addW = 140;
            int delX = width - right - delW;
            btnDeleteQuiz.setBounds(delX, 100, delW, 36);
            btnCreateQuiz.setBounds(delX - gap - addW, 100, addW, 36);

            quizScroll.setBounds(40, 150, width - 80, cardH - 180);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnCreateSession)     showCreateSessionDialog();
        else if(e.getSource() == btnDeleteSession) deleteSelectedSession();
        else if(e.getSource() == btnCreateQuiz)   showCreateQuizDialog();
        else if(e.getSource() == btnDeleteQuiz)   deleteSelectedQuiz();
    }
}
