package panels;

import core.Course;
import core.Professor;
import core.Section;
import core.Semester;
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
import junction.ProfessorCourse;
import junction.ProfessorSection;
import services.CourseService;
import services.ProfessorCourseService;
import services.ProfessorSectionService;
import services.ProfessorService;
import services.SectionService;
import services.SemesterService;

public class AdminProfessorAssignmentsPanel extends JPanel implements ActionListener {

    private static final String CARD_SECTIONS = "SECTIONS";
    private static final String CARD_COURSES = "COURSES";

    private JPanel tabBar;
    private JButton btnTabSections, btnTabCourses;
    private JPanel cardHost;

    private JPanel sectionsCard;
    private JLabel lblSecTitle, lblSecSub;
    private JSeparator secSep;
    private JComboBox<String> cmbSecProfessor;
    private JButton btnSecLoad, btnSecAssign, btnSecUnassign;
    private JTable secTable;
    private DefaultTableModel secModel;
    private JScrollPane secScroll;

    private JPanel coursesCard;
    private JLabel lblCrsTitle, lblCrsSub;
    private JSeparator crsSep;
    private JComboBox<String> cmbCrsProfessor;
    private JButton btnCrsLoad, btnCrsAssign, btnCrsUnassign;
    private JTable crsTable;
    private DefaultTableModel crsModel;
    private JScrollPane crsScroll;

    private final ProfessorService professorService = new ProfessorService();
    private final ProfessorSectionService sectionAssignSvc = new ProfessorSectionService();
    private final ProfessorCourseService courseAssignSvc = new ProfessorCourseService();
    private final SectionService sectionService = new SectionService();
    private final CourseService courseService = new CourseService();
    private final SemesterService semesterService = new SemesterService();

    private final Map<Integer, Integer> secCmbIndexToId = new HashMap<>();
    private final Map<Integer, Integer> crsCmbIndexToId = new HashMap<>();
    private final List<ProfessorSection> secRows = new ArrayList<>();
    private final List<ProfessorCourse> crsRows = new ArrayList<>();

    public AdminProfessorAssignmentsPanel(){
        setLayout(null);
        setBackground(Color.WHITE);
        buildTabBar();
        buildCardHost();
        buildSectionsCard();
        buildCoursesCard();
        switchTab(CARD_SECTIONS);
    }

    private void buildTabBar(){
        tabBar = new JPanel(null);
        tabBar.setBackground(Color.WHITE);
        add(tabBar);

        btnTabSections = tabButton("Section Assignments");
        btnTabSections.addActionListener(e -> switchTab(CARD_SECTIONS));
        tabBar.add(btnTabSections);

        btnTabCourses = tabButton("Course Assignments");
        btnTabCourses.addActionListener(e -> switchTab(CARD_COURSES));
        tabBar.add(btnTabCourses);
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
        Color active = new Color(255, 140, 0);
        Color inactive = new Color(200, 200, 200);
        if(CARD_SECTIONS.equals(card)){
            btnTabSections.setForeground(active);
            btnTabSections.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, active));
            btnTabCourses.setForeground(new Color(100, 100, 100));
            btnTabCourses.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, inactive));
        }else{
            btnTabCourses.setForeground(active);
            btnTabCourses.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, active));
            btnTabSections.setForeground(new Color(100, 100, 100));
            btnTabSections.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, inactive));
        }
    }

    private void buildCardHost(){
        cardHost = new JPanel(new CardLayout());
        cardHost.setBackground(Color.WHITE);
        add(cardHost);
    }

    private void buildSectionsCard(){
        sectionsCard = new JPanel(null);
        sectionsCard.setBackground(Color.WHITE);

        lblSecTitle = new JLabel("Section Assignments");
        lblSecTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblSecTitle.setForeground(new Color(60, 60, 60));
        sectionsCard.add(lblSecTitle);

        lblSecSub = new JLabel("Assign or remove sections handled by a professor.");
        lblSecSub.setFont(new Font("Arial", Font.PLAIN, 14));
        sectionsCard.add(lblSecSub);

        secSep = new JSeparator();
        secSep.setForeground(new Color(220, 220, 220));
        sectionsCard.add(secSep);

        cmbSecProfessor = buildCombo();
        cmbSecProfessor.addItem("Select Professor");
        loadProfessorsIntoCombo(cmbSecProfessor, secCmbIndexToId);
        sectionsCard.add(cmbSecProfessor);

        btnSecLoad = actionButton("Load", new Color(255, 140, 0));
        btnSecLoad.addActionListener(this);
        sectionsCard.add(btnSecLoad);

        btnSecAssign = actionButton("Assign Section", new Color(40, 167, 69));
        btnSecAssign.addActionListener(this);
        sectionsCard.add(btnSecAssign);

        btnSecUnassign = actionButton("Unassign", new Color(220, 53, 69));
        btnSecUnassign.addActionListener(this);
        sectionsCard.add(btnSecUnassign);

        String[] cols = {"Section Code", "Program", "Year Level", "Semester"};
        secModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c){
                return false;
            }
        };
        secTable = buildTable(secModel);
        secScroll = buildScroll(secTable);
        sectionsCard.add(secScroll);

        cardHost.add(sectionsCard, CARD_SECTIONS);
    }

    private void buildCoursesCard(){
        coursesCard = new JPanel(null);
        coursesCard.setBackground(Color.WHITE);

        lblCrsTitle = new JLabel("Course Assignments");
        lblCrsTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblCrsTitle.setForeground(new Color(60, 60, 60));
        coursesCard.add(lblCrsTitle);

        lblCrsSub = new JLabel("Assign or remove courses handled by a professor.");
        lblCrsSub.setFont(new Font("Arial", Font.PLAIN, 14));
        coursesCard.add(lblCrsSub);

        crsSep = new JSeparator();
        crsSep.setForeground(new Color(220, 220, 220));
        coursesCard.add(crsSep);

        cmbCrsProfessor = buildCombo();
        cmbCrsProfessor.addItem("Select Professor");
        loadProfessorsIntoCombo(cmbCrsProfessor, crsCmbIndexToId);
        coursesCard.add(cmbCrsProfessor);

        btnCrsLoad = actionButton("Load", new Color(255, 140, 0));
        btnCrsLoad.addActionListener(this);
        coursesCard.add(btnCrsLoad);

        btnCrsAssign = actionButton("Assign Course", new Color(40, 167, 69));
        btnCrsAssign.addActionListener(this);
        coursesCard.add(btnCrsAssign);

        btnCrsUnassign = actionButton("Unassign", new Color(220, 53, 69));
        btnCrsUnassign.addActionListener(this);
        coursesCard.add(btnCrsUnassign);

        String[] cols = {"Course Code", "Course Name", "Program", "Semester"};
        crsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c){
                return false;
            }
        };
        crsTable = buildTable(crsModel);
        crsScroll = buildScroll(crsTable);
        coursesCard.add(crsScroll);

        cardHost.add(coursesCard, CARD_COURSES);
    }

    private void loadProfessorsIntoCombo(JComboBox<String> cmb, Map<Integer, Integer> indexMap){
        List<Professor> professors = professorService.getAllProfessors();
        int idx = 1;
        for(Professor p : professors){
            cmb.addItem(p.getFullName() + " (" + p.user().userName() + ")");
            indexMap.put(idx++, p.professorId());
        }
    }

    private void loadSectionAssignments(){
        secRows.clear();
        secModel.setRowCount(0);
        if(cmbSecProfessor.getSelectedIndex() <= 0){
            return;
        }
        int professorId = secCmbIndexToId.get(cmbSecProfessor.getSelectedIndex());
        List<ProfessorSection> assignments = sectionAssignSvc.getSectionsByProfessor(professorId);
        for(ProfessorSection ps : assignments){
            secRows.add(ps);
            secModel.addRow(new Object[]{
                ps.section().sectionCode(),
                ps.section().program().programName(),
                ps.section().yearLevel().getYearLevelName(),
                ps.semester().semesterName()
            });
        }
    }

    private void loadCourseAssignments(){
        crsRows.clear();
        crsModel.setRowCount(0);
        if(cmbCrsProfessor.getSelectedIndex() <= 0){
            return;
        }
        int professorId = crsCmbIndexToId.get(cmbCrsProfessor.getSelectedIndex());
        List<ProfessorCourse> assignments = courseAssignSvc.getCoursesByProfessor(professorId);
        for(ProfessorCourse pc : assignments){
            crsRows.add(pc);
            crsModel.addRow(new Object[]{
                pc.course().courseCode(),
                pc.course().courseName(),
                pc.course().program().programName(),
                pc.semester().semesterName()
            });
        }
    }

    private void showAssignSectionDialog(){
        if(cmbSecProfessor.getSelectedIndex() <= 0){
            JOptionPane.showMessageDialog(this, "Please select a professor first.", "No Professor", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int professorId = secCmbIndexToId.get(cmbSecProfessor.getSelectedIndex());
        Professor professor = professorService.getAllProfessors().stream()
            .filter(p -> p.professorId() == professorId).findFirst().orElse(null);
        if(professor == null){
            return;
        }

        List<Section> sections = sectionService.getAllSections();
        List<Semester> semesters = semesterService.getAllSemesters();
        if(sections.isEmpty() || semesters.isEmpty()){
            JOptionPane.showMessageDialog(this, "No sections or semesters available.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Assign Section");
        dialog.setModal(true);
        dialog.setSize(420, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setResizable(false);

        JLabel accent = new JLabel("");
        accent.setBounds(0, 0, 4, 280);
        accent.setBackground(new Color(255, 140, 0));
        accent.setOpaque(true);
        dialog.add(accent);

        JLabel lblHeader = new JLabel("Assign Section to " + professor.getFullName());
        lblHeader.setBounds(30, 20, 360, 25);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 15));
        lblHeader.setForeground(new Color(60, 60, 60));
        dialog.add(lblHeader);

        JSeparator sep = new JSeparator();
        sep.setBounds(30, 50, 360, 1);
        sep.setForeground(new Color(220, 220, 220));
        dialog.add(sep);

        JLabel lblSection = new JLabel("Section");
        lblSection.setBounds(30, 68, 100, 25);
        lblSection.setFont(new Font("Arial", Font.BOLD, 13));
        lblSection.setForeground(new Color(100, 100, 100));
        dialog.add(lblSection);

        JComboBox<String> cmbSection = new JComboBox<>();
        Map<Integer, Section> sectionMap = new HashMap<>();
        int i = 0;
        for(Section s : sections){
            cmbSection.addItem(s.sectionCode() + " - " + s.program().programName());
            sectionMap.put(i++, s);
        }
        cmbSection.setBounds(140, 68, 240, 32);
        cmbSection.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbSection.setBackground(Color.WHITE);
        cmbSection.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbSection.setFocusable(false);
        dialog.add(cmbSection);

        JLabel lblSemester = new JLabel("Semester");
        lblSemester.setBounds(30, 114, 100, 25);
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
        cmbSemester.setBounds(140, 114, 240, 32);
        cmbSemester.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbSemester.setBackground(Color.WHITE);
        cmbSemester.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbSemester.setFocusable(false);
        dialog.add(cmbSemester);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBounds(200, 195, 90, 34);
        btnCancel.setFont(new Font("Arial", Font.PLAIN, 13));
        btnCancel.setForeground(new Color(100, 100, 100));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(ev -> dialog.dispose());
        dialog.add(btnCancel);

        JButton btnSave = new JButton("Assign");
        btnSave.setBounds(300, 195, 90, 34);
        btnSave.setFont(new Font("Arial", Font.PLAIN, 13));
        btnSave.setForeground(Color.WHITE);
        btnSave.setBackground(new Color(255, 140, 0));
        btnSave.setBorder(null);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(ev -> {
            Section section = sectionMap.get(cmbSection.getSelectedIndex());
            Semester semester = semesterMap.get(cmbSemester.getSelectedIndex());
            if(section == null || semester == null){
                return;
            }
            boolean ok = sectionAssignSvc.assignSection(professor, section, semester);
            if(ok){
                JOptionPane.showMessageDialog(dialog, "Section assigned successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadSectionAssignments();
            }else{
                JOptionPane.showMessageDialog(dialog, "Failed to assign. This professor may already be assigned to that section in the same semester.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(btnSave);

        dialog.setVisible(true);
    }

    private void showAssignCourseDialog(){
        if(cmbCrsProfessor.getSelectedIndex() <= 0){
            JOptionPane.showMessageDialog(this, "Please select a professor first.", "No Professor", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int professorId = crsCmbIndexToId.get(cmbCrsProfessor.getSelectedIndex());
        Professor professor = professorService.getAllProfessors().stream()
            .filter(p -> p.professorId() == professorId).findFirst().orElse(null);
        if(professor == null){
            return;
        }

        List<Course> courses = courseService.getAllCourses();
        List<Semester> semesters = semesterService.getAllSemesters();
        if(courses.isEmpty() || semesters.isEmpty()){
            JOptionPane.showMessageDialog(this, "No courses or semesters available.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Assign Course");
        dialog.setModal(true);
        dialog.setSize(460, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setResizable(false);

        JLabel accent = new JLabel("");
        accent.setBounds(0, 0, 4, 280);
        accent.setBackground(new Color(255, 140, 0));
        accent.setOpaque(true);
        dialog.add(accent);

        JLabel lblHeader = new JLabel("Assign Course to " + professor.getFullName());
        lblHeader.setBounds(30, 20, 400, 25);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 15));
        lblHeader.setForeground(new Color(60, 60, 60));
        dialog.add(lblHeader);

        JSeparator sep = new JSeparator();
        sep.setBounds(30, 50, 400, 1);
        sep.setForeground(new Color(220, 220, 220));
        dialog.add(sep);

        JLabel lblCourse = new JLabel("Course");
        lblCourse.setBounds(30, 68, 100, 25);
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
        cmbCourse.setBounds(140, 68, 290, 32);
        cmbCourse.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbCourse.setBackground(Color.WHITE);
        cmbCourse.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbCourse.setFocusable(false);
        dialog.add(cmbCourse);

        JLabel lblSemester = new JLabel("Semester");
        lblSemester.setBounds(30, 114, 100, 25);
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
        cmbSemester.setBounds(140, 114, 290, 32);
        cmbSemester.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbSemester.setBackground(Color.WHITE);
        cmbSemester.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbSemester.setFocusable(false);
        dialog.add(cmbSemester);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBounds(240, 195, 90, 34);
        btnCancel.setFont(new Font("Arial", Font.PLAIN, 13));
        btnCancel.setForeground(new Color(100, 100, 100));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(ev -> dialog.dispose());
        dialog.add(btnCancel);

        JButton btnSave = new JButton("Assign");
        btnSave.setBounds(340, 195, 90, 34);
        btnSave.setFont(new Font("Arial", Font.PLAIN, 13));
        btnSave.setForeground(Color.WHITE);
        btnSave.setBackground(new Color(255, 140, 0));
        btnSave.setBorder(null);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(ev -> {
            Course course = courseMap.get(cmbCourse.getSelectedIndex());
            Semester semester = semesterMap.get(cmbSemester.getSelectedIndex());
            if(course == null || semester == null){
                return;
            }
            boolean ok = courseAssignSvc.assignCourse(professor, course, semester);
            if(ok){
                JOptionPane.showMessageDialog(dialog, "Course assigned successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadCourseAssignments();
            }else{
                JOptionPane.showMessageDialog(dialog, "Failed to assign. This professor may already be assigned to that course in the same semester.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(btnSave);

        dialog.setVisible(true);
    }

    private void unassignSection(){
        int row = secTable.getSelectedRow();
        if(row < 0 || row >= secRows.size()){
            JOptionPane.showMessageDialog(this, "Please select a section assignment to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ProfessorSection ps = secRows.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Remove " + ps.professor().getFullName() + " from section " + ps.section().sectionCode() + "?",
            "Confirm Unassign", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if(confirm == JOptionPane.YES_OPTION){
            boolean ok = sectionAssignSvc.unassignSection(ps.professorSectionId());
            if(ok){
                loadSectionAssignments();
            }else{
                JOptionPane.showMessageDialog(this, "Failed to remove assignment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void unassignCourse(){
        int row = crsTable.getSelectedRow();
        if(row < 0 || row >= crsRows.size()){
            JOptionPane.showMessageDialog(this, "Please select a course assignment to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ProfessorCourse pc = crsRows.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Remove " + pc.professor().getFullName() + " from course " + pc.course().courseCode() + "?",
            "Confirm Unassign", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if(confirm == JOptionPane.YES_OPTION){
            boolean ok = courseAssignSvc.unassignCourse(pc.professorCourseId());
            if(ok){
                loadCourseAssignments();
            }else{
                JOptionPane.showMessageDialog(this, "Failed to remove assignment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JComboBox<String> buildCombo(){
        JComboBox<String> c = new JComboBox<>();
        c.setFont(new Font("Arial", Font.PLAIN, 14));
        c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        c.setFocusable(false);
        c.setRenderer(new BasicComboBoxRenderer() {
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
        if(width <= 0 || height <= 0){
            return;
        }

        int tabH = 44;
        tabBar.setBounds(0, 0, width, tabH);
        btnTabSections.setBounds(40, 10, 200, 28);
        btnTabCourses.setBounds(260, 10, 200, 28);
        cardHost.setBounds(0, tabH, width, height - tabH);

        int cardH = height - tabH;

        if(sectionsCard != null){
            lblSecTitle.setBounds(40, 20, 400, 30);
            lblSecSub.setBounds(40, 50, 600, 30);
            secSep.setBounds(40, 82, width - 80, 1);

            cmbSecProfessor.setBounds(40, 100, 280, 36);
            btnSecLoad.setBounds(330, 100, 90, 36);

            int rightMargin = 40;
            int gap = 12;
            int unassignW = 110;
            int assignW = 140;
            int unassignX = width - rightMargin - unassignW;
            btnSecUnassign.setBounds(unassignX, 100, unassignW, 36);
            btnSecAssign.setBounds(unassignX - gap - assignW, 100, assignW, 36);

            secScroll.setBounds(40, 150, width - 80, cardH - 180);
        }

        if(coursesCard != null){
            lblCrsTitle.setBounds(40, 20, 400, 30);
            lblCrsSub.setBounds(40, 50, 600, 30);
            crsSep.setBounds(40, 82, width - 80, 1);

            cmbCrsProfessor.setBounds(40, 100, 280, 36);
            btnCrsLoad.setBounds(330, 100, 90, 36);

            int rightMargin = 40;
            int gap = 12;
            int unassignW = 110;
            int assignW = 140;
            int unassignX = width - rightMargin - unassignW;
            btnCrsUnassign.setBounds(unassignX, 100, unassignW, 36);
            btnCrsAssign.setBounds(unassignX - gap - assignW, 100, assignW, 36);

            crsScroll.setBounds(40, 150, width - 80, cardH - 180);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnSecLoad){
            loadSectionAssignments();
        }else if(e.getSource() == btnSecAssign){
            showAssignSectionDialog();
        }else if(e.getSource() == btnSecUnassign){
            unassignSection();
        }else if(e.getSource() == btnCrsLoad){
            loadCourseAssignments();
        }else if(e.getSource() == btnCrsAssign){
            showAssignCourseDialog();
        }else if(e.getSource() == btnCrsUnassign){
            unassignCourse();
        }
    }
}
