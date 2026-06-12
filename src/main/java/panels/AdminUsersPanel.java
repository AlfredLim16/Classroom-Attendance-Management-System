package panels;

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
import services.UserService;

public class AdminUsersPanel extends JPanel implements ActionListener {

    private JLabel lblTitle, lblSubTitle;
    private JSeparator separator;
    private JTextField txtSearch;
    private JComboBox<String> cmbFilter;
    private JButton btnSearch, btnAdd, btnEdit, btnDelete;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private final UserService userService = new UserService();
    private final List<User> rowUsers = new java.util.ArrayList<>();

    public AdminUsersPanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("User Management");
        lblTitle.setBounds(40, 20, 300, 30);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle);

        lblSubTitle = new JLabel("Manage system users and their roles");
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
        txtSearch.setCaretColor(new Color(255, 140, 0));
        add(txtSearch);

        cmbFilter = new JComboBox<>(new String[]{"All Fields", "Username", "Role"});
        cmbFilter.setBounds(250, 100, 120, 36);
        cmbFilter.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbFilter.setBackground(Color.WHITE);
        cmbFilter.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbFilter.setFocusable(false);
        add(cmbFilter);

        btnSearch = new JButton("Search");
        btnSearch.setBounds(380, 100, 100, 36);
        btnSearch.setFont(new Font("Arial", Font.PLAIN, 14));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setBackground(new Color(255, 140, 0));
        btnSearch.setBorder(null);
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.addActionListener(this);
        add(btnSearch);

        btnAdd = new JButton("+ Add User");
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

        String[] columns = {"ID", "Username", "Role"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.setRowHeight(28);
        userTable.setFillsViewportHeight(true);
        userTable.getTableHeader().setReorderingAllowed(false);
        userTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        userTable.getTableHeader().setBackground(new Color(255, 255, 255));
        userTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        userTable.getTableHeader().setPreferredSize(new Dimension(userTable.getPreferredSize().width, 28));

        DefaultTableCellRenderer centerCollumn = new DefaultTableCellRenderer();
        centerCollumn.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < userTable.getColumnCount(); i++){
            userTable.getColumnModel().getColumn(i).setCellRenderer(centerCollumn);
        }

        scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane);

        loadUsers();
    }

    private void loadUsers(){
        tableModel.setRowCount(0);
        rowUsers.clear();
        List<User> users = userService.getAllUsers();
        for(User u : users){
            rowUsers.add(u);
            addUserRow(String.valueOf(u.userId()), u.userName(), u.role().toString());
        }
    }

    private User getSelectedUser(){
        int row = userTable.getSelectedRow();
        if(row < 0 || row >= rowUsers.size()){
            return null;
        }
        return rowUsers.get(row);
    }

    private void showUserDialog(User user, boolean isEdit){
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), isEdit ? "Edit User" : "Add User");
        dialog.setModal(true);
        dialog.setSize(420, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setResizable(false);

        JLabel accent = new JLabel("");
        accent.setBounds(0, 0, 4, 350);
        accent.setBackground(new Color(255, 140, 0));
        accent.setOpaque(true);
        dialog.add(accent);

        JLabel lblHeader = new JLabel(isEdit ? "Edit User" : "Add New User");
        lblHeader.setBounds(30, 20, 300, 30);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));
        lblHeader.setForeground(new Color(60, 60, 60));
        dialog.add(lblHeader);

        JSeparator sep = new JSeparator();
        sep.setBounds(30, 55, 360, 1);
        sep.setForeground(new Color(220, 220, 220));
        dialog.add(sep);

        JLabel lblUser = new JLabel("Username");
        lblUser.setBounds(30, 72, 120, 25);
        lblUser.setFont(new Font("Arial", Font.BOLD, 13));
        lblUser.setForeground(new Color(100, 100, 100));
        dialog.add(lblUser);

        JTextField txtUser = new JTextField(isEdit ? user.userName() : "");
        txtUser.setBounds(150, 72, 230, 32);
        txtUser.setFont(new Font("Arial", Font.PLAIN, 13));
        txtUser.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        dialog.add(txtUser);

        JLabel lblPass = new JLabel("Password");
        lblPass.setBounds(30, 118, 120, 25);
        lblPass.setFont(new Font("Arial", Font.BOLD, 13));
        lblPass.setForeground(new Color(100, 100, 100));
        dialog.add(lblPass);

        JPasswordField txtPass = new JPasswordField();
        txtPass.setBounds(150, 118, 230, 32);
        txtPass.setFont(new Font("Arial", Font.PLAIN, 13));
        txtPass.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        if(isEdit){
            txtPass.setText("********");
        }
        dialog.add(txtPass);

        JLabel lblRole = new JLabel("Role");
        lblRole.setBounds(30, 164, 120, 25);
        lblRole.setFont(new Font("Arial", Font.BOLD, 13));
        lblRole.setForeground(new Color(100, 100, 100));
        dialog.add(lblRole);

        JComboBox<String> cmbRole = new JComboBox<>(new String[]{"Admin", "Professor", "Student", "Secretary"});
        cmbRole.setBounds(150, 164, 230, 32);
        cmbRole.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbRole.setBackground(Color.WHITE);
        cmbRole.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbRole.setFocusable(false);
        if(isEdit){
            cmbRole.setSelectedItem(user.role().toString());
        }
        dialog.add(cmbRole);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBounds(180, 255, 100, 36);
        btnCancel.setFont(new Font("Arial", Font.PLAIN, 14));
        btnCancel.setForeground(new Color(100, 100, 100));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(ev -> dialog.dispose());
        dialog.add(btnCancel);

        JButton btnSaveDialog = new JButton(isEdit ? "Update" : "Create");
        btnSaveDialog.setBounds(290, 255, 100, 36);
        btnSaveDialog.setFont(new Font("Arial", Font.PLAIN, 14));
        btnSaveDialog.setForeground(Color.WHITE);
        btnSaveDialog.setBackground(new Color(255, 140, 0));
        btnSaveDialog.setBorder(null);
        btnSaveDialog.setFocusPainted(false);
        btnSaveDialog.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSaveDialog.addActionListener(ev -> {
            String username = txtUser.getText().trim();
            String password = new String(txtPass.getPassword()).trim();
            if(username.isEmpty()){
                JOptionPane.showMessageDialog(dialog, "Username is required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(!isEdit && password.isEmpty()){
                JOptionPane.showMessageDialog(dialog, "Password is required for new users.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Role role = Role.valueOf(cmbRole.getSelectedItem().toString().toUpperCase());
            boolean success;
            if(isEdit){
                if(password.equals("********") || password.isEmpty()){
                    User updated = new User(user.userId(), username, user.userPassword(), role);
                    success = userService.updateUser(updated);
                }else{
                    User updated = new User(user.userId(), username, password, role);
                    success = userService.updateUser(updated);
                }
            }else{
                User created = userService.createUser(username, password, role);
                success = created != null && created.userId() > 0;
            }
            if(success){
                JOptionPane.showMessageDialog(dialog, (isEdit ? "User updated" : "User created") + " successfully!\n\nUsername: " + username + "\nRole: " + role, "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadUsers();
            }else{
                JOptionPane.showMessageDialog(dialog, "Failed to save user. Username may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
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

    public void addUserRow(String id, String username, String role){
        tableModel.addRow(new Object[]{id, username, role});
    }

    private void performSearch(){
        String query = txtSearch.getText().trim().toLowerCase();
        String filter = cmbFilter.getSelectedItem().toString();
        if(query.isEmpty()){
            loadUsers();
            return;
        }
        tableModel.setRowCount(0);
        for(User u : rowUsers){
            boolean match = false;
            switch(filter){
                case "Username" ->
                    match = u.userName().toLowerCase().contains(query);
                case "Role" ->
                    match = u.role().toString().toLowerCase().contains(query);
                default ->
                    match = u.userName().toLowerCase().contains(query) || u.role().toString().toLowerCase().contains(query) || String.valueOf(u.userId()).contains(query);
            }
            if(match){
                addUserRow(String.valueOf(u.userId()), u.userName(), u.role().toString());
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnSearch){
            performSearch();
        }
        if(e.getSource() == btnAdd){
            showUserDialog(null, false);
        }
        if(e.getSource() == btnEdit){
            User selected = getSelectedUser();
            if(selected == null){
                JOptionPane.showMessageDialog(this, "Please select a user from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showUserDialog(selected, true);
        }
        if(e.getSource() == btnDelete){
            User selected = getSelectedUser();
            if(selected == null){
                JOptionPane.showMessageDialog(this, "Please select a user from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete user:\n\n" + "Username: " + selected.userName() + "\n" + "Role: " + selected.role() + "\n\nThis action cannot be undone.", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if(confirm == JOptionPane.YES_OPTION){
                boolean success = userService.deleteUser(selected.userId());
                if(success){
                    JOptionPane.showMessageDialog(this, "User '" + selected.userName() + "' deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    loadUsers();
                }else{
                    JOptionPane.showMessageDialog(this, "Failed to delete user. User may be referenced by other records.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
