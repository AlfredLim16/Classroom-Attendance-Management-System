package panels;

import core.Program;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
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
import services.ProgramService;

public class AdminProgramsPanel extends JPanel implements ActionListener {

    private JLabel lblTitle, lblSubTitle;
    private JSeparator separator;
    private JTextField txtSearch;
    private JButton btnSearch, btnAdd, btnEdit, btnDelete;
    private JTable programTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private final ProgramService programService = new ProgramService();
    private final List<Program> rowPrograms = new ArrayList<>();

    public AdminProgramsPanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("Program Management");
        lblTitle.setBounds(40, 20, 300, 30);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle);

        lblSubTitle = new JLabel("Manage academic programs (e.g. BSIT, BSCS, BSED)");
        lblSubTitle.setBounds(40, 50, 500, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblSubTitle);

        separator = new JSeparator();
        separator.setForeground(new Color(220, 220, 220));
        add(separator);

        txtSearch = new JTextField();
        txtSearch.setBounds(40, 100, 260, 36);
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 14));
        txtSearch.setForeground(new Color(60, 60, 60));
        txtSearch.setBackground(Color.WHITE);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        add(txtSearch);

        btnSearch = new JButton("Search");
        btnSearch.setBounds(310, 100, 100, 36);
        btnSearch.setFont(new Font("Arial", Font.PLAIN, 14));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setBackground(new Color(255, 140, 0));
        btnSearch.setBorder(null);
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.addActionListener(this);
        add(btnSearch);

        btnAdd = new JButton("+ Add Program");
        btnAdd.setFont(new Font("Arial", Font.PLAIN, 14));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setBorder(null);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(this);
        add(btnAdd);

        btnEdit = new JButton("Edit");
        btnEdit.setFont(new Font("Arial", Font.PLAIN, 14));
        btnEdit.setForeground(new Color(100, 100, 100));
        btnEdit.setBackground(Color.WHITE);
        btnEdit.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btnEdit.setFocusPainted(false);
        btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEdit.addActionListener(this);
        add(btnEdit);

        btnDelete = new JButton("Delete");
        btnDelete.setFont(new Font("Arial", Font.PLAIN, 14));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setBorder(null);
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.addActionListener(this);
        add(btnDelete);

        String[] columns = {"ID", "Program Name"};
        tableModel = new DefaultTableModel(columns, 0){
            @Override public boolean isCellEditable(int r, int c){ return false; }
        };

        programTable = new JTable(tableModel);
        programTable.setRowHeight(28);
        programTable.setFillsViewportHeight(true);
        programTable.getTableHeader().setReorderingAllowed(false);
        programTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        programTable.getTableHeader().setBackground(Color.WHITE);
        programTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        programTable.getTableHeader().setPreferredSize(new Dimension(programTable.getPreferredSize().width, 28));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < programTable.getColumnCount(); i++){
            programTable.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        scrollPane = new JScrollPane(programTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane);

        loadPrograms();
    }

    private void loadPrograms(){
        tableModel.setRowCount(0);
        rowPrograms.clear();
        List<Program> programs = programService.getAllPrograms();
        for(Program p : programs){
            rowPrograms.add(p);
            tableModel.addRow(new Object[]{p.programId(), p.programName()});
        }
    }

    private Program getSelected(){
        int row = programTable.getSelectedRow();
        if(row < 0 || row >= rowPrograms.size()) return null;
        return rowPrograms.get(row);
    }

    private void showDialog(Program existing){
        boolean isEdit = existing != null;
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
            isEdit ? "Edit Program" : "Add Program");
        dialog.setModal(true);
        dialog.setSize(400, 220);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setResizable(false);

        JLabel accent = new JLabel("");
        accent.setBounds(0, 0, 4, 220);
        accent.setBackground(new Color(255, 140, 0));
        accent.setOpaque(true);
        dialog.add(accent);

        JLabel lblHeader = new JLabel(isEdit ? "Edit Program" : "Add New Program");
        lblHeader.setBounds(24, 20, 300, 28);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));
        lblHeader.setForeground(new Color(60, 60, 60));
        dialog.add(lblHeader);

        JSeparator sep = new JSeparator();
        sep.setBounds(24, 54, 352, 1);
        sep.setForeground(new Color(220, 220, 220));
        dialog.add(sep);

        JLabel lblName = new JLabel("Program Name");
        lblName.setBounds(24, 70, 130, 25);
        lblName.setFont(new Font("Arial", Font.BOLD, 13));
        lblName.setForeground(new Color(100, 100, 100));
        dialog.add(lblName);

        JTextField txtName = new JTextField(isEdit ? existing.programName() : "");
        txtName.setBounds(160, 70, 216, 32);
        txtName.setFont(new Font("Arial", Font.PLAIN, 13));
        txtName.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        dialog.add(txtName);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBounds(174, 140, 90, 34);
        btnCancel.setFont(new Font("Arial", Font.PLAIN, 13));
        btnCancel.setForeground(new Color(100, 100, 100));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(ev -> dialog.dispose());
        dialog.add(btnCancel);

        JButton btnSave = new JButton(isEdit ? "Update" : "Create");
        btnSave.setBounds(274, 140, 96, 34);
        btnSave.setFont(new Font("Arial", Font.PLAIN, 13));
        btnSave.setForeground(Color.WHITE);
        btnSave.setBackground(new Color(255, 140, 0));
        btnSave.setBorder(null);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(ev -> {
            String name = txtName.getText().trim();
            if(name.isEmpty()){
                JOptionPane.showMessageDialog(dialog, "Program name is required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean success;
            if(isEdit){
                Program updated = new Program(existing.programId(), name);
                success = programService.updateProgram(updated);
            } else {
                Program created = programService.createProgram(name);
                success = created != null;
            }
            if(success){
                JOptionPane.showMessageDialog(dialog,
                    "Program " + (isEdit ? "updated" : "created") + " successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadPrograms();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Failed to save. Program name may already exist.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(btnSave);

        dialog.setVisible(true);
    }

    private void performSearch(){
        String query = txtSearch.getText().trim().toLowerCase();
        if(query.isEmpty()){
            loadPrograms();
            return;
        }
        tableModel.setRowCount(0);
        for(Program p : rowPrograms){
            if(p.programName().toLowerCase().contains(query) || String.valueOf(p.programId()).contains(query)){
                tableModel.addRow(new Object[]{p.programId(), p.programName()});
            }
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x, y, width, height);
        if(width > 0 && height > 0 && scrollPane != null){
            separator.setBounds(40, 80, width - 80, 1);
            scrollPane.setBounds(40, 150, width - 80, height - 180);
        }
        if(width > 0 && btnDelete != null){
            int right = 40, gap = 15, h = 36;
            int delW = 100, editW = 100, addW = 130;
            int delX  = width - right - delW;
            btnDelete.setBounds(delX, 100, delW, h);
            int editX = delX - gap - editW;
            btnEdit.setBounds(editX, 100, editW, h);
            int addX  = editX - gap - addW;
            btnAdd.setBounds(addX, 100, addW, h);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnSearch){
            performSearch();
        } else if(e.getSource() == btnAdd){
            showDialog(null);
        } else if(e.getSource() == btnEdit){
            Program sel = getSelected();
            if(sel == null){
                JOptionPane.showMessageDialog(this, "Please select a program from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showDialog(sel);
        } else if(e.getSource() == btnDelete){
            Program sel = getSelected();
            if(sel == null){
                JOptionPane.showMessageDialog(this, "Please select a program from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                "Delete program: " + sel.programName() + "?\n\nThis will fail if sections or courses are linked to it.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if(confirm == JOptionPane.YES_OPTION){
                boolean success = programService.deleteProgram(sel.programId());
                if(success){
                    JOptionPane.showMessageDialog(this, "Program deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    loadPrograms();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to delete. Program may be linked to sections or courses.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
