package panels;

import core.Course;
import core.Program;
import core.Semester;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
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
import lookup.YearLevel;
import services.CourseService;
import services.ProgramService;
import services.SemesterService;

public class AdminCoursesPanel extends JPanel implements ActionListener {

    private JLabel lblTitle, lblSubTitle;
    private JSeparator separator;
    private JTextField txtSearch;
    private JComboBox<String> cmbFilter;
    private JButton btnSearch, btnAdd, btnEdit, btnDelete;
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private final CourseService courseService = new CourseService();
    private final ProgramService programService = new ProgramService();
    private final SemesterService semesterService = new SemesterService();
    private final List<Course> rowCourses = new java.util.ArrayList<>();

    public AdminCoursesPanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("Course Management");
        lblTitle.setBounds(40, 20, 300, 30);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle);

        lblSubTitle = new JLabel("Manage courses, programs, and curricula");
        lblSubTitle.setBounds(40, 50, 400, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblSubTitle);

        separator = new JSeparator();
        separator.setForeground(Color.BLACK);
        add(separator);

        txtSearch = new JTextField();
        txtSearch.setBounds(40, 100, 200, 36);
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 14));
        txtSearch.setForeground(new Color(60, 60, 60));
        txtSearch.setBackground(Color.WHITE);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1), BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        add(txtSearch);

        cmbFilter = new JComboBox<>(new String[]{"All Fields", "Course Code", "Course Name", "Program", "Units", "Year Level", "Semester"});
        cmbFilter.setBounds(250, 100, 140, 36);
        cmbFilter.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbFilter.setBackground(Color.WHITE);
        cmbFilter.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbFilter.setFocusable(false);
        add(cmbFilter);

        btnSearch = new JButton("Search");
        btnSearch.setBounds(400, 100, 100, 36);
        btnSearch.setFont(new Font("Arial", Font.PLAIN, 14));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setBackground(new Color(255, 140, 0));
        btnSearch.setBorder(null);
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.addActionListener(this);
        add(btnSearch);

        btnAdd = new JButton("+ Add Course");
        btnAdd.setBounds(420, 100, 120, 36);
        btnAdd.setFont(new Font("Arial", Font.PLAIN, 14));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setBorder(null);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(this);
        add(btnAdd);

        btnEdit = new JButton("Edit");
        btnEdit.setBounds(550, 100, 100, 36);
        btnEdit.setFont(new Font("Arial", Font.PLAIN, 14));
        btnEdit.setForeground(new Color(100, 100, 100));
        btnEdit.setBackground(Color.WHITE);
        btnEdit.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btnEdit.setFocusPainted(false);
        btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEdit.addActionListener(this);
        add(btnEdit);

        btnDelete = new JButton("Delete");
        btnDelete.setBounds(660, 100, 100, 36);
        btnDelete.setFont(new Font("Arial", Font.PLAIN, 14));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setBorder(null);
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.addActionListener(this);
        add(btnDelete);

        String[] columns = {"Code", "Course Name", "Program", "Units", "Year Level", "Semester"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        courseTable = new JTable(tableModel);
        courseTable.setRowHeight(28);
        courseTable.setFillsViewportHeight(true);
        courseTable.getTableHeader().setReorderingAllowed(false);
        courseTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        courseTable.getTableHeader().setBackground(new Color(255, 255, 255));
        courseTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        courseTable.getTableHeader().setPreferredSize(new Dimension(courseTable.getPreferredSize().width, 28));

        DefaultTableCellRenderer centerCollumn = new DefaultTableCellRenderer();
        centerCollumn.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < courseTable.getColumnCount(); i++){
            courseTable.getColumnModel().getColumn(i).setCellRenderer(centerCollumn);
        }

        scrollPane = new JScrollPane(courseTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane);

        loadCourses();
    }

    private void loadCourses(){
        tableModel.setRowCount(0);
        rowCourses.clear();
        List<Course> courses = courseService.getAllCourses();
        for(Course c : courses){
            rowCourses.add(c);
            addCourseRow(
                c.courseCode(),
                c.courseName(),
                c.program().programName(),
                String.valueOf(c.units()),
                c.yearLevel().getYearLevelName(),
                c.semester().semesterName()
            );
        }
    }

    private Course getSelectedCourse(){
        int row = courseTable.getSelectedRow();
        if(row < 0 || row >= rowCourses.size()){
            return null;
        }
        return rowCourses.get(row);
    }

    private void showCourseDialog(Course course, boolean isEdit){
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), isEdit ? "Edit Course" : "Add Course");
        dialog.setModal(true);
        dialog.setSize(480, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setResizable(false);

        JLabel accent = new JLabel("");
        accent.setBounds(0, 0, 4, 500);
        accent.setBackground(new Color(255, 140, 0));
        accent.setOpaque(true);
        dialog.add(accent);

        JLabel lblHeader = new JLabel(isEdit ? "Edit Course" : "Add New Course");
        lblHeader.setBounds(30, 20, 300, 30);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));
        lblHeader.setForeground(new Color(60, 60, 60));
        dialog.add(lblHeader);

        JSeparator sep = new JSeparator();
        sep.setBounds(30, 55, 420, 1);
        sep.setForeground(new Color(220, 220, 220));
        dialog.add(sep);

        int y = 72, gap = 46;

        JLabel lblCode = new JLabel("Course Code");
        lblCode.setBounds(30, y, 120, 25);
        lblCode.setFont(new Font("Arial", Font.BOLD, 13));
        lblCode.setForeground(new Color(100, 100, 100));
        dialog.add(lblCode);

        JTextField txtCode = new JTextField(isEdit ? course.courseCode() : "");
        txtCode.setBounds(150, y, 280, 32);
        txtCode.setFont(new Font("Arial", Font.PLAIN, 13));
        txtCode.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        dialog.add(txtCode);
        y += gap;

        JLabel lblName = new JLabel("Course Name");
        lblName.setBounds(30, y, 120, 25);
        lblName.setFont(new Font("Arial", Font.BOLD, 13));
        lblName.setForeground(new Color(100, 100, 100));
        dialog.add(lblName);

        JTextField txtName = new JTextField(isEdit ? course.courseName() : "");
        txtName.setBounds(150, y, 280, 32);
        txtName.setFont(new Font("Arial", Font.PLAIN, 13));
        txtName.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        dialog.add(txtName);
        y += gap;

        JLabel lblProgram = new JLabel("Program");
        lblProgram.setBounds(30, y, 120, 25);
        lblProgram.setFont(new Font("Arial", Font.BOLD, 13));
        lblProgram.setForeground(new Color(100, 100, 100));
        dialog.add(lblProgram);

        JComboBox<String> cmbProgram = new JComboBox<>();
        List<Program> programs = programService.getAllPrograms();
        for(Program p : programs){
            cmbProgram.addItem(p.programName());
        }
        cmbProgram.setBounds(150, y, 280, 32);
        cmbProgram.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbProgram.setBackground(Color.WHITE);
        cmbProgram.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbProgram.setFocusable(false);
        if(isEdit){
            cmbProgram.setSelectedItem(course.program().programName());
        }
        dialog.add(cmbProgram);
        y += gap;

        JLabel lblUnits = new JLabel("Units");
        lblUnits.setBounds(30, y, 120, 25);
        lblUnits.setFont(new Font("Arial", Font.BOLD, 13));
        lblUnits.setForeground(new Color(100, 100, 100));
        dialog.add(lblUnits);

        JTextField txtUnits = new JTextField(isEdit ? String.valueOf(course.units()) : "3");
        txtUnits.setBounds(150, y, 80, 32);
        txtUnits.setFont(new Font("Arial", Font.PLAIN, 13));
        txtUnits.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        dialog.add(txtUnits);

        JLabel lblYear = new JLabel("Year Level");
        lblYear.setBounds(250, y, 80, 25);
        lblYear.setFont(new Font("Arial", Font.BOLD, 13));
        lblYear.setForeground(new Color(100, 100, 100));
        dialog.add(lblYear);

        JComboBox<String> cmbYear = new JComboBox<>(new String[]{"1st Year", "2nd Year", "3rd Year", "4th Year"});
        cmbYear.setBounds(330, y, 100, 32);
        cmbYear.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbYear.setBackground(Color.WHITE);
        cmbYear.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbYear.setFocusable(false);
        if(isEdit){
            cmbYear.setSelectedItem(course.yearLevel().getYearLevelName());
        }
        dialog.add(cmbYear);
        y += gap;

        JLabel lblSemester = new JLabel("Semester");
        lblSemester.setBounds(30, y, 120, 25);
        lblSemester.setFont(new Font("Arial", Font.BOLD, 13));
        lblSemester.setForeground(new Color(100, 100, 100));
        dialog.add(lblSemester);

        JComboBox<String> cmbSemester = new JComboBox<>();
        List<Semester> semesters = semesterService.getAllSemesters();
        for(Semester s : semesters){
            cmbSemester.addItem(s.semesterName());
        }
        cmbSemester.setBounds(150, y, 280, 32);
        cmbSemester.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbSemester.setBackground(Color.WHITE);
        cmbSemester.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbSemester.setFocusable(false);
        if(isEdit){
            cmbSemester.setSelectedItem(course.semester().semesterName());
        }
        dialog.add(cmbSemester);
        y += gap + 10;

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBounds(230, y, 100, 36);
        btnCancel.setFont(new Font("Arial", Font.PLAIN, 14));
        btnCancel.setForeground(new Color(100, 100, 100));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(ev -> dialog.dispose());
        dialog.add(btnCancel);

        JButton btnSaveDialog = new JButton(isEdit ? "Update" : "Create");
        btnSaveDialog.setBounds(340, y, 100, 36);
        btnSaveDialog.setFont(new Font("Arial", Font.PLAIN, 14));
        btnSaveDialog.setForeground(Color.WHITE);
        btnSaveDialog.setBackground(new Color(255, 140, 0));
        btnSaveDialog.setBorder(null);
        btnSaveDialog.setFocusPainted(false);
        btnSaveDialog.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSaveDialog.addActionListener(ev -> {
            String code = txtCode.getText().trim();
            String name = txtName.getText().trim();
            if(code.isEmpty() || name.isEmpty()){
                JOptionPane.showMessageDialog(dialog, "Course code and name are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try{
                int units = Integer.parseInt(txtUnits.getText().trim());
                String programName = cmbProgram.getSelectedItem().toString();
                String yearName = cmbYear.getSelectedItem().toString();
                String semName = cmbSemester.getSelectedItem().toString();

                Program program = programService.getAllPrograms().stream()
                    .filter(p -> p.programName().equals(programName)).findFirst().orElse(null);
                YearLevel yearLevel = switch (yearName) {
                    case "1st Year" -> YearLevel.FIRST_YEAR;
                    case "2nd Year" -> YearLevel.SECOND_YEAR;
                    case "3rd Year" -> YearLevel.THIRD_YEAR;
                    case "4th Year" -> YearLevel.FOURTH_YEAR;
                    default -> YearLevel.FIRST_YEAR;
                };
                Semester semester = semesterService.getAllSemesters().stream().filter(s -> s.semesterName().equals(semName)).findFirst().orElse(null);
                if(program == null || semester == null){
                    JOptionPane.showMessageDialog(dialog, "Invalid program or semester selected.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success;
                if(isEdit){
                    Course updated = Course.builder()
                        .courseId(course.courseId())
                        .program(program)
                        .courseCode(code)
                        .courseName(name)
                        .units((byte) units)
                        .semester(semester)
                        .yearLevel(yearLevel)
                        .build();
                    success = courseService.updateCourse(updated);
                } else {
                    Course created = courseService.createCourse(program, code, name, (byte) units, semester, yearLevel);
                    success = created != null && created.courseId() > 0;
                }

                if(success){
                    JOptionPane.showMessageDialog(dialog,"Course " + (isEdit ? "updated" : "created") + " successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadCourses();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to save course. Course code may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(dialog, "Units must be a valid number.", "Validation", JOptionPane.WARNING_MESSAGE);
            }
        });
        dialog.add(btnSaveDialog);

        dialog.setVisible(true);
    }

    @Override
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x, y, width, height);
        if(width > 0 && height > 0 && scrollPane != null){
            separator.setBounds(40, 80, width - 80, height - 160);
            scrollPane.setBounds(40, 150, width - 80, height - 180);
        }

        if(width > 0 && btnDelete != null && btnEdit != null && btnAdd != null){
            int rightMargin = 40;
            int gap = 15;
            int btnH = 36;
            int delW = 100;
            int editW = 100;
            int addW = 120;

            int delX = width - rightMargin - delW;
            btnDelete.setBounds(delX, 100, delW, btnH);

            int editX = delX - gap - editW;
            btnEdit.setBounds(editX, 100, editW, btnH);

            int addX = editX - gap - addW;
            btnAdd.setBounds(addX, 100, addW, btnH);
        }
    }

    public void addCourseRow(String code, String name, String program, String units, String year, String semester){
        tableModel.addRow(new Object[]{code, name, program, units, year, semester});
    }

    private void performSearch(){
        String query = txtSearch.getText().trim().toLowerCase();
        String filter = cmbFilter.getSelectedItem().toString();
        if(query.isEmpty()){
            loadCourses();
            return;
        }
        tableModel.setRowCount(0);
        for(Course c : rowCourses){
            boolean match = false;
            switch(filter){
                case "Course Code" -> match = c.courseCode().toLowerCase().contains(query);
                case "Course Name" -> match = c.courseName().toLowerCase().contains(query);
                case "Program" -> match = c.program().programName().toLowerCase().contains(query);
                case "Units" -> match = String.valueOf(c.units()).contains(query);
                case "Year Level" -> match = c.yearLevel().getYearLevelName().toLowerCase().contains(query);
                case "Semester" -> match = c.semester().semesterName().toLowerCase().contains(query);
                default -> match = c.courseCode().toLowerCase().contains(query) || c.courseName().toLowerCase().contains(query) || c.program().programName().toLowerCase().contains(query) || String.valueOf(c.units()).contains(query) || c.yearLevel().getYearLevelName().toLowerCase().contains(query) || c.semester().semesterName().toLowerCase().contains(query);
            }
            if(match){
                addCourseRow(c.courseCode(), c.courseName(), c.program().programName(), String.valueOf(c.units()), c.yearLevel().getYearLevelName(), c.semester().semesterName());
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnSearch){
            performSearch();
        }
        if(e.getSource() == btnAdd){
            showCourseDialog(null, false);
        }
        if(e.getSource() == btnEdit){
            Course selected = getSelectedCourse();
            if(selected == null){
                JOptionPane.showMessageDialog(this, "Please select a course from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showCourseDialog(selected, true);
        }
        if(e.getSource() == btnDelete){
            Course selected = getSelectedCourse();
            if(selected == null){
                JOptionPane.showMessageDialog(this, "Please select a course from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                "Delete course: " + selected.courseCode() + " - " + selected.courseName() + "?\n\nThis action cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if(confirm == JOptionPane.YES_OPTION){
                boolean success = courseService.deleteCourse(selected.courseId());
                if(success){
                    JOptionPane.showMessageDialog(this, "Course deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    loadCourses();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete course. Course may be referenced by other records.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}