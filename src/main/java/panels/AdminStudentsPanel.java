package panels;

import core.Program;
import core.Section;
import core.Student;
import core.User;
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
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import lookup.Role;
import lookup.YearLevel;
import services.ProgramService;
import services.SectionService;
import services.StudentService;
import services.UserService;

public class AdminStudentsPanel extends JPanel implements ActionListener {

    private JLabel lblTitle, lblSubTitle;
    private JSeparator separator;
    private JTextField txtSearch;
    private JComboBox<String> cmbFilter;
    private JButton btnSearch, btnAdd, btnEdit, btnDelete;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private final StudentService studentService = new StudentService();
    private final UserService userService = new UserService();
    private final ProgramService programService = new ProgramService();
    private final SectionService sectionService = new SectionService();
    private final List<Student> rowStudents = new java.util.ArrayList<>();

    public AdminStudentsPanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("Student Management");
        lblTitle.setBounds(40, 20, 300, 30);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle);

        lblSubTitle = new JLabel("Manage student records and enrollments");
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

        cmbFilter = new JComboBox<>(new String[]{"All Fields", "Student No.", "Name", "Program", "Year", "Section", "Username"});
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

        btnAdd = new JButton("+ Add Student");
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

        String[] columns = {"Student No.", "Name", "Program", "Year", "Section", "Username"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(28);
        studentTable.setFillsViewportHeight(true);
        studentTable.getTableHeader().setReorderingAllowed(false);
        studentTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        studentTable.getTableHeader().setBackground(new Color(255, 255, 255));
        studentTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        studentTable.getTableHeader().setPreferredSize(new Dimension(studentTable.getPreferredSize().width, 28));

        DefaultTableCellRenderer centerCollumn = new DefaultTableCellRenderer();
        centerCollumn.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < studentTable.getColumnCount(); i++){
            studentTable.getColumnModel().getColumn(i).setCellRenderer(centerCollumn);
        }

        scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane);

        loadStudents();
    }

    private void loadStudents(){
        tableModel.setRowCount(0);
        rowStudents.clear();
        List<Student> students = studentService.getAllStudents();
        for(Student s : students){
            rowStudents.add(s);
            addStudentRow(
                s.studentNumber(),
                s.firstName() + " " + s.lastName(),
                s.program().programName(),
                s.yearLevel().getYearLevelName(),
                s.section().sectionCode(),
                s.user().userName()
            );
        }
    }

    private Student getSelectedStudent(){
        int row = studentTable.getSelectedRow();
        if(row < 0 || row >= rowStudents.size()){
            return null;
        }
        return rowStudents.get(row);
    }

    private void showStudentDialog(Student student, boolean isEdit){
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), isEdit ? "Edit Student" : "Add Student");
        dialog.setModal(true);
        dialog.setSize(480, 560);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setResizable(false);

        JLabel accent = new JLabel("");
        accent.setBounds(0, 0, 4, 560);
        accent.setBackground(new Color(255, 140, 0));
        accent.setOpaque(true);
        dialog.add(accent);

        JLabel lblHeader = new JLabel(isEdit ? "Edit Student" : "Add New Student");
        lblHeader.setBounds(30, 20, 300, 30);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));
        lblHeader.setForeground(new Color(60, 60, 60));
        dialog.add(lblHeader);

        JSeparator sep = new JSeparator();
        sep.setBounds(30, 55, 420, 1);
        sep.setForeground(new Color(220, 220, 220));
        dialog.add(sep);

        int y = 72, gap = 46;

        JLabel lblUser = new JLabel("Username");
        lblUser.setBounds(30, y, 120, 25);
        lblUser.setFont(new Font("Arial", Font.BOLD, 13));
        lblUser.setForeground(new Color(100, 100, 100));
        dialog.add(lblUser);

        JTextField txtUser = new JTextField(isEdit ? student.user().userName() : "");
        txtUser.setBounds(150, y, 280, 32);
        txtUser.setFont(new Font("Arial", Font.PLAIN, 13));
        txtUser.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        dialog.add(txtUser);
        y += gap;

        if(!isEdit){
            JLabel lblPass = new JLabel("Password");
            lblPass.setBounds(30, y, 120, 25);
            lblPass.setFont(new Font("Arial", Font.BOLD, 13));
            lblPass.setForeground(new Color(100, 100, 100));
            dialog.add(lblPass);

            JPasswordField txtPass = new JPasswordField();
            txtPass.setBounds(150, y, 280, 32);
            txtPass.setFont(new Font("Arial", Font.PLAIN, 13));
            txtPass.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
            dialog.add(txtPass);
            y += gap;
        }

        JLabel lblStudNo = new JLabel("Student No.");
        lblStudNo.setBounds(30, y, 120, 25);
        lblStudNo.setFont(new Font("Arial", Font.BOLD, 13));
        lblStudNo.setForeground(new Color(100, 100, 100));
        dialog.add(lblStudNo);

        JTextField txtStudNo = new JTextField(isEdit ? student.studentNumber() : "");
        txtStudNo.setBounds(150, y, 280, 32);
        txtStudNo.setFont(new Font("Arial", Font.PLAIN, 13));
        txtStudNo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        dialog.add(txtStudNo);
        y += gap;

        JLabel lblFname = new JLabel("First Name");
        lblFname.setBounds(30, y, 120, 25);
        lblFname.setFont(new Font("Arial", Font.BOLD, 13));
        lblFname.setForeground(new Color(100, 100, 100));
        dialog.add(lblFname);

        JTextField txtFname = new JTextField(isEdit ? student.firstName() : "");
        txtFname.setBounds(150, y, 280, 32);
        txtFname.setFont(new Font("Arial", Font.PLAIN, 13));
        txtFname.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        dialog.add(txtFname);
        y += gap;

        JLabel lblMname = new JLabel("Middle Name");
        lblMname.setBounds(30, y, 120, 25);
        lblMname.setFont(new Font("Arial", Font.BOLD, 13));
        lblMname.setForeground(new Color(100, 100, 100));
        dialog.add(lblMname);

        JTextField txtMname = new JTextField(isEdit ? (student.middleName() != null ? student.middleName() : "") : "");
        txtMname.setBounds(150, y, 280, 32);
        txtMname.setFont(new Font("Arial", Font.PLAIN, 13));
        txtMname.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1),  BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        dialog.add(txtMname);
        y += gap;

        JLabel lblLname = new JLabel("Last Name");
        lblLname.setBounds(30, y, 120, 25);
        lblLname.setFont(new Font("Arial", Font.BOLD, 13));
        lblLname.setForeground(new Color(100, 100, 100));
        dialog.add(lblLname);

        JTextField txtLname = new JTextField(isEdit ? student.lastName() : "");
        txtLname.setBounds(150, y, 280, 32);
        txtLname.setFont(new Font("Arial", Font.PLAIN, 13));
        txtLname.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        dialog.add(txtLname);
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
            cmbProgram.setSelectedItem(student.program().programName());
        }
        dialog.add(cmbProgram);
        y += gap;

        JLabel lblYear = new JLabel("Year Level");
        lblYear.setBounds(30, y, 120, 25);
        lblYear.setFont(new Font("Arial", Font.BOLD, 13));
        lblYear.setForeground(new Color(100, 100, 100));
        dialog.add(lblYear);

        JComboBox<String> cmbYear = new JComboBox<>(new String[]{"1st Year", "2nd Year", "3rd Year", "4th Year"});
        cmbYear.setBounds(150, y, 130, 32);
        cmbYear.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbYear.setBackground(Color.WHITE);
        cmbYear.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbYear.setFocusable(false);
        if(isEdit){
            cmbYear.setSelectedItem(student.yearLevel().getYearLevelName());
        }
        dialog.add(cmbYear);

        JLabel lblSection = new JLabel("Section");
        lblSection.setBounds(300, y, 60, 25);
        lblSection.setFont(new Font("Arial", Font.BOLD, 13));
        lblSection.setForeground(new Color(100, 100, 100));
        dialog.add(lblSection);

        JComboBox<String> cmbSection = new JComboBox<>();
        List<Section> sections = sectionService.getAllSections();
        for(Section sec : sections){
            cmbSection.addItem(sec.sectionCode());
        }
        cmbSection.setBounds(360, y, 70, 32);
        cmbSection.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbSection.setBackground(Color.WHITE);
        cmbSection.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbSection.setFocusable(false);
        if(isEdit){
            cmbSection.setSelectedItem(student.section().sectionCode());
        }
        dialog.add(cmbSection);
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
            String username = txtUser.getText().trim();
            String studNo = txtStudNo.getText().trim();
            String fname = txtFname.getText().trim();
            String lname = txtLname.getText().trim();
            String mname = txtMname.getText().trim();
            if(mname.isEmpty()) mname = null;
            String programName = cmbProgram.getSelectedItem().toString();
            String yearName = cmbYear.getSelectedItem().toString();
            String sectionCode = cmbSection.getSelectedItem().toString();

            if(username.isEmpty() || studNo.isEmpty() || fname.isEmpty() || lname.isEmpty()){
                JOptionPane.showMessageDialog(dialog, "Username, student number, first name, and last name are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Program program = programService.getAllPrograms().stream()
                .filter(p -> p.programName().equals(programName)).findFirst().orElse(null);
            YearLevel yearLevel = switch (yearName) {
                case "1st Year" -> YearLevel.FIRST_YEAR;
                case "2nd Year" -> YearLevel.SECOND_YEAR;
                case "3rd Year" -> YearLevel.THIRD_YEAR;
                case "4th Year" -> YearLevel.FOURTH_YEAR;
                default -> YearLevel.FIRST_YEAR;
            };
            Section section = sectionService.getAllSections().stream()
                .filter(s -> s.sectionCode().equals(sectionCode)).findFirst().orElse(null);

            if(program == null || section == null){
                JOptionPane.showMessageDialog(dialog, "Invalid program or section selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success;
            if(isEdit){
                User updatedUser = new User(student.user().userId(), username, student.user().userPassword(), Role.STUDENT);
                userService.updateUser(updatedUser);
                Student updated = Student.builder()
                    .studentId(student.studentId())
                    .user(updatedUser)
                    .studentNumber(studNo)
                    .firstName(fname)
                    .middleName(mname)
                    .lastName(lname)
                    .program(program)
                    .yearLevel(yearLevel)
                    .section(section)
                    .build();
                success = studentService.updateStudent(updated);
            } else {
                java.awt.Component[] comps = dialog.getContentPane().getComponents();
                String password = "";
                for(java.awt.Component c : comps){
                    if(c instanceof JPasswordField pf){
                        password = new String(pf.getPassword()).trim();
                        break;
                    }
                }
                if(password.isEmpty()){
                    JOptionPane.showMessageDialog(dialog, "Password is required for new students.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                User newUser = userService.createUser(username, password, Role.STUDENT);
                if(newUser == null || newUser.userId() == 0){
                    JOptionPane.showMessageDialog(dialog, "Failed to create user. Username may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Student created = studentService.createStudent(newUser, studNo, fname, mname, lname, program, yearLevel, section);
                success = created != null && created.studentId() > 0;
            }

            if(success){
                JOptionPane.showMessageDialog(dialog,
                    "Student " + (isEdit ? "updated" : "created") + " successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadStudents();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to save student. Student number may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
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

    public void addStudentRow(String studentNo, String name, String program, String year, String section, String username){
        tableModel.addRow(new Object[]{studentNo, name, program, year, section, username});
    }

    private void performSearch(){
        String query = txtSearch.getText().trim().toLowerCase();
        String filter = cmbFilter.getSelectedItem().toString();
        if(query.isEmpty()){
            loadStudents();
            return;
        }
        tableModel.setRowCount(0);
        for(Student s : rowStudents){
            boolean match = false;
            String fullName = (s.firstName() + " " + s.lastName()).toLowerCase();
            switch(filter){
                case "Student No." -> match = s.studentNumber().toLowerCase().contains(query);
                case "Name" -> match = fullName.contains(query);
                case "Program" -> match = s.program().programName().toLowerCase().contains(query);
                case "Year" -> match = s.yearLevel().getYearLevelName().toLowerCase().contains(query);
                case "Section" -> match = s.section().sectionCode().toLowerCase().contains(query);
                case "Username" -> match = s.user().userName().toLowerCase().contains(query);
                default -> match = s.studentNumber().toLowerCase().contains(query)
                    || fullName.contains(query)
                    || s.program().programName().toLowerCase().contains(query)
                    || s.yearLevel().getYearLevelName().toLowerCase().contains(query)
                    || s.section().sectionCode().toLowerCase().contains(query)
                    || s.user().userName().toLowerCase().contains(query);
            }
            if(match){
                addStudentRow(s.studentNumber(), s.firstName() + " " + s.lastName(),
                    s.program().programName(), s.yearLevel().getYearLevelName(),
                    s.section().sectionCode(), s.user().userName());
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnSearch){
            performSearch();
        }
        if(e.getSource() == btnAdd){
            showStudentDialog(null, false);
        }
        if(e.getSource() == btnEdit){
            Student selected = getSelectedStudent();
            if(selected == null){
                JOptionPane.showMessageDialog(this, "Please select a student from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showStudentDialog(selected, true);
        }
        if(e.getSource() == btnDelete){
            Student selected = getSelectedStudent();
            if(selected == null){
                JOptionPane.showMessageDialog(this, "Please select a student from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Delete student: " + selected.firstName() + " " + selected.lastName() + "?\n\nThis action cannot be undone.", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if(confirm == JOptionPane.YES_OPTION){
                boolean success = studentService.deleteStudent(selected.studentId());
                if(success){
                    userService.deleteUser(selected.user().userId());
                    JOptionPane.showMessageDialog(this, "Student deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    loadStudents();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete student. Student may be referenced by other records.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}