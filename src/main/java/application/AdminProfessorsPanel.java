package application;

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
import user.Professor;
import user.ProfessorService;
import user.ProfessorType;
import user.Role;
import user.User;
import user.UserService;

public class AdminProfessorsPanel extends JPanel implements ActionListener {

    private JLabel lblTitle, lblSubTitle;
    private JSeparator separator;
    private JTextField txtSearch;
    private JComboBox<String> cmbFilter;
    private JButton btnSearch, btnAdd, btnEdit, btnDelete;
    private JTable professorTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private final ProfessorService professorService = new ProfessorService();
    private final UserService userService = new UserService();
    private final List<Professor> rowProfessors = new java.util.ArrayList<>();

    public AdminProfessorsPanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("Professor Management");
        lblTitle.setBounds(40, 20, 300, 30);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle);

        lblSubTitle = new JLabel("Manage professors and their assignments");
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
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        add(txtSearch);

        cmbFilter = new JComboBox<>(new String[]{"All Fields", "ID", "Name", "Type", "Username"});
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

        btnAdd = new JButton("+ Add Professor");
        btnAdd.setBounds(420, 100, 130, 36);
        btnAdd.setFont(new Font("Arial", Font.PLAIN, 14));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setBorder(null);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(this);
        add(btnAdd);

        btnEdit = new JButton("Edit");
        btnEdit.setBounds(560, 100, 100, 36);
        btnEdit.setFont(new Font("Arial", Font.PLAIN, 14));
        btnEdit.setForeground(new Color(100, 100, 100));
        btnEdit.setBackground(Color.WHITE);
        btnEdit.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btnEdit.setFocusPainted(false);
        btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEdit.addActionListener(this);
        add(btnEdit);

        btnDelete = new JButton("Delete");
        btnDelete.setBounds(670, 100, 100, 36);
        btnDelete.setFont(new Font("Arial", Font.PLAIN, 14));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setBorder(null);
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.addActionListener(this);
        add(btnDelete);

        String[] columns = {"ID", "Name", "Type", "Username"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        professorTable = new JTable(tableModel);
        professorTable.setRowHeight(28);
        professorTable.setFillsViewportHeight(true);
        professorTable.getTableHeader().setReorderingAllowed(false);
        professorTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        professorTable.getTableHeader().setBackground(new Color(255, 255, 255));
        professorTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        professorTable.getTableHeader().setPreferredSize(new Dimension(professorTable.getPreferredSize().width, 28));

        DefaultTableCellRenderer centerCollumn = new DefaultTableCellRenderer();
        centerCollumn.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < professorTable.getColumnCount(); i++){
            professorTable.getColumnModel().getColumn(i).setCellRenderer(centerCollumn);
        }

        scrollPane = new JScrollPane(professorTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane);

        loadProfessors();
    }

    private void loadProfessors(){
        tableModel.setRowCount(0);
        rowProfessors.clear();
        List<Professor> professors = professorService.getAllProfessors();
        for(Professor p : professors){
            rowProfessors.add(p);
            addProfessorRow(
                String.valueOf(p.professorId()),
                p.getFullName(),
                p.professorType().getProfessorTypeName(),
                p.user().userName()
            );
        }
    }

    private Professor getSelectedProfessor(){
        int row = professorTable.getSelectedRow();
        if(row < 0 || row >= rowProfessors.size()){
            return null;
        }
        return rowProfessors.get(row);
    }

    private void showProfessorDialog(Professor professor, boolean isEdit){
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), isEdit ? "Edit Professor" : "Add Professor");
        dialog.setModal(true);
        dialog.setSize(460, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setResizable(false);

        JLabel accent = new JLabel("");
        accent.setBounds(0, 0, 4, 480);
        accent.setBackground(new Color(255, 140, 0));
        accent.setOpaque(true);
        dialog.add(accent);

        JLabel lblHeader = new JLabel(isEdit ? "Edit Professor" : "Add New Professor");
        lblHeader.setBounds(30, 20, 300, 30);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));
        lblHeader.setForeground(new Color(60, 60, 60));
        dialog.add(lblHeader);

        JSeparator sep = new JSeparator();
        sep.setBounds(30, 55, 400, 1);
        sep.setForeground(new Color(220, 220, 220));
        dialog.add(sep);

        int y = 72, gap = 46;

        JLabel lblUser = new JLabel("Username");
        lblUser.setBounds(30, y, 120, 25);
        lblUser.setFont(new Font("Arial", Font.BOLD, 13));
        lblUser.setForeground(new Color(100, 100, 100));
        dialog.add(lblUser);

        JTextField txtUser = new JTextField(isEdit ? professor.user().userName() : "");
        txtUser.setBounds(150, y, 260, 32);
        txtUser.setFont(new Font("Arial", Font.PLAIN, 13));
        txtUser.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        dialog.add(txtUser);
        y += gap;

        if(!isEdit){
            JLabel lblPass = new JLabel("Password");
            lblPass.setBounds(30, y, 120, 25);
            lblPass.setFont(new Font("Arial", Font.BOLD, 13));
            lblPass.setForeground(new Color(100, 100, 100));
            dialog.add(lblPass);

            JPasswordField txtPass = new JPasswordField();
            txtPass.setBounds(150, y, 260, 32);
            txtPass.setFont(new Font("Arial", Font.PLAIN, 13));
            txtPass.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));
            dialog.add(txtPass);
            y += gap;
        }

        JLabel lblFname = new JLabel("First Name");
        lblFname.setBounds(30, y, 120, 25);
        lblFname.setFont(new Font("Arial", Font.BOLD, 13));
        lblFname.setForeground(new Color(100, 100, 100));
        dialog.add(lblFname);

        JTextField txtFname = new JTextField(isEdit ? professor.firstName() : "");
        txtFname.setBounds(150, y, 260, 32);
        txtFname.setFont(new Font("Arial", Font.PLAIN, 13));
        txtFname.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        dialog.add(txtFname);
        y += gap;

        JLabel lblMname = new JLabel("Middle Name");
        lblMname.setBounds(30, y, 120, 25);
        lblMname.setFont(new Font("Arial", Font.BOLD, 13));
        lblMname.setForeground(new Color(100, 100, 100));
        dialog.add(lblMname);

        JTextField txtMname = new JTextField(isEdit ? (professor.middleName() != null ? professor.middleName() : "") : "");
        txtMname.setBounds(150, y, 260, 32);
        txtMname.setFont(new Font("Arial", Font.PLAIN, 13));
        txtMname.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        dialog.add(txtMname);
        y += gap;

        JLabel lblLname = new JLabel("Last Name");
        lblLname.setBounds(30, y, 120, 25);
        lblLname.setFont(new Font("Arial", Font.BOLD, 13));
        lblLname.setForeground(new Color(100, 100, 100));
        dialog.add(lblLname);

        JTextField txtLname = new JTextField(isEdit ? professor.lastName() : "");
        txtLname.setBounds(150, y, 260, 32);
        txtLname.setFont(new Font("Arial", Font.PLAIN, 13));
        txtLname.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        dialog.add(txtLname);
        y += gap;

        JLabel lblType = new JLabel("Type");
        lblType.setBounds(30, y, 120, 25);
        lblType.setFont(new Font("Arial", Font.BOLD, 13));
        lblType.setForeground(new Color(100, 100, 100));
        dialog.add(lblType);

        JComboBox<String> cmbType = new JComboBox<>(new String[]{"Faculty", "Full-time", "Part-time"});
        cmbType.setBounds(150, y, 260, 32);
        cmbType.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbType.setBackground(Color.WHITE);
        cmbType.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbType.setFocusable(false);
        if(isEdit){
            cmbType.setSelectedItem(professor.professorType().getProfessorTypeName());
        }
        dialog.add(cmbType);
        y += gap + 10;

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBounds(220, y, 100, 36);
        btnCancel.setFont(new Font("Arial", Font.PLAIN, 14));
        btnCancel.setForeground(new Color(100, 100, 100));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(ev -> dialog.dispose());
        dialog.add(btnCancel);

        JButton btnSaveDialog = new JButton(isEdit ? "Update" : "Create");
        btnSaveDialog.setBounds(330, y, 100, 36);
        btnSaveDialog.setFont(new Font("Arial", Font.PLAIN, 14));
        btnSaveDialog.setForeground(Color.WHITE);
        btnSaveDialog.setBackground(new Color(255, 140, 0));
        btnSaveDialog.setBorder(null);
        btnSaveDialog.setFocusPainted(false);
        btnSaveDialog.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSaveDialog.addActionListener(ev -> {
            String username = txtUser.getText().trim();
            String fname = txtFname.getText().trim();
            String lname = txtLname.getText().trim();
            String mname = txtMname.getText().trim();
            if(mname.isEmpty()) mname = null;
            String typeName = cmbType.getSelectedItem().toString();

            if(username.isEmpty() || fname.isEmpty() || lname.isEmpty()){
                JOptionPane.showMessageDialog(dialog, "Username, first name, and last name are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ProfessorType pType = switch(typeName){
                case "Faculty" -> ProfessorType.FACULTY;
                case "Full-time" -> ProfessorType.FULL_TIME;
                case "Part-time" -> ProfessorType.PART_TIME;
                default -> ProfessorType.FACULTY;
            };

            boolean success;
            if(isEdit){
                User updatedUser = new User(professor.user().userId(), username, professor.user().userPassword(), Role.PROFESSOR);
                userService.updateUser(updatedUser);
                Professor updated = new Professor(professor.professorId(), updatedUser, fname, mname, lname, pType);
                success = professorService.updateProfessor(updated);
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
                    JOptionPane.showMessageDialog(dialog, "Password is required for new professors.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                User newUser = userService.createUser(username, password, Role.PROFESSOR);
                if(newUser == null || newUser.userId() == 0){
                    JOptionPane.showMessageDialog(dialog, "Failed to create user. Username may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Professor created = professorService.createProfessor(newUser, fname, mname, lname, pType);
                success = created != null && created.professorId() > 0;
            }

            if(success){
                JOptionPane.showMessageDialog(dialog,
                    "Professor " + (isEdit ? "updated" : "created") + " successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadProfessors();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to save professor.", "Error", JOptionPane.ERROR_MESSAGE);
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
            int addW = 130;

            int delX = width - rightMargin - delW;
            btnDelete.setBounds(delX, 100, delW, btnH);

            int editX = delX - gap - editW;
            btnEdit.setBounds(editX, 100, editW, btnH);

            int addX = editX - gap - addW;
            btnAdd.setBounds(addX, 100, addW, btnH);
        }
    }

    public void addProfessorRow(String id, String name, String type, String username){
        tableModel.addRow(new Object[]{id, name, type, username});
    }

    private void performSearch(){
        String query = txtSearch.getText().trim().toLowerCase();
        String filter = cmbFilter.getSelectedItem().toString();
        if(query.isEmpty()){
            loadProfessors();
            return;
        }
        tableModel.setRowCount(0);
        for(Professor p : rowProfessors){
            boolean match = false;
            String fullName = p.getFullName().toLowerCase();
            switch(filter){
                case "ID" -> match = String.valueOf(p.professorId()).contains(query);
                case "Name" -> match = fullName.contains(query);
                case "Type" -> match = p.professorType().getProfessorTypeName().toLowerCase().contains(query);
                case "Username" -> match = p.user().userName().toLowerCase().contains(query);
                default -> match = String.valueOf(p.professorId()).contains(query)
                    || fullName.contains(query)
                    || p.professorType().getProfessorTypeName().toLowerCase().contains(query)
                    || p.user().userName().toLowerCase().contains(query);
            }
            if(match){
                addProfessorRow(String.valueOf(p.professorId()), p.getFullName(),
                    p.professorType().getProfessorTypeName(), p.user().userName());
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnSearch){
            performSearch();
        }
        if(e.getSource() == btnAdd){
            showProfessorDialog(null, false);
        }
        if(e.getSource() == btnEdit){
            Professor selected = getSelectedProfessor();
            if(selected == null){
                JOptionPane.showMessageDialog(this, "Please select a professor from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showProfessorDialog(selected, true);
        }
        if(e.getSource() == btnDelete){
            Professor selected = getSelectedProfessor();
            if(selected == null){
                JOptionPane.showMessageDialog(this, "Please select a professor from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                "Delete professor: " + selected.getFullName() + "?\n\nThis action cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if(confirm == JOptionPane.YES_OPTION){
                boolean success = professorService.deleteProfessor(selected.professorId());
                if(success){
                    userService.deleteUser(selected.user().userId());
                    JOptionPane.showMessageDialog(this, "Professor deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    loadProfessors();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete professor. Professor may be referenced by other records.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}