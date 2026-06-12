package panels;

import core.Semester;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
import services.SemesterService;

public class AdminSemestersPanel extends JPanel implements ActionListener {

    private JLabel lblTitle, lblSubTitle;
    private JSeparator separator;
    private JButton btnAdd, btnEdit, btnDelete;
    private JTable semesterTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private final SemesterService semesterService = new SemesterService();
    private final List<Semester> rowSemesters = new ArrayList<>();

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public AdminSemestersPanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("Semester Management");
        lblTitle.setBounds(40, 20, 300, 30);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle);

        lblSubTitle = new JLabel("Create and manage academic semesters required for courses and enrollments");
        lblSubTitle.setBounds(40, 50, 600, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblSubTitle);

        separator = new JSeparator();
        separator.setForeground(new Color(220, 220, 220));
        add(separator);

        btnAdd = new JButton("+ Add Semester");
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

        String[] columns = {"Semester Name", "School Year", "Start Date", "End Date"};
        tableModel = new DefaultTableModel(columns, 0){
            @Override
            public boolean isCellEditable(int row, int col){ return false; }
        };

        semesterTable = new JTable(tableModel);
        semesterTable.setRowHeight(28);
        semesterTable.setFillsViewportHeight(true);
        semesterTable.getTableHeader().setReorderingAllowed(false);
        semesterTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        semesterTable.getTableHeader().setBackground(Color.WHITE);
        semesterTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        semesterTable.getTableHeader().setPreferredSize(new Dimension(semesterTable.getPreferredSize().width, 28));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < semesterTable.getColumnCount(); i++){
            semesterTable.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        scrollPane = new JScrollPane(semesterTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane);

        loadSemesters();
    }

    private void loadSemesters(){
        tableModel.setRowCount(0);
        rowSemesters.clear();
        List<Semester> list = semesterService.getAllSemesters();
        for(Semester s : list){
            rowSemesters.add(s);
            tableModel.addRow(new Object[]{
                s.semesterName(),
                s.schoolYear(),
                s.startDate().format(DATE_FMT),
                s.endDate().format(DATE_FMT)
            });
        }
    }

    private Semester getSelected(){
        int row = semesterTable.getSelectedRow();
        if(row < 0 || row >= rowSemesters.size()) return null;
        return rowSemesters.get(row);
    }

    private void showDialog(Semester existing){
        boolean isEdit = existing != null;
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                isEdit ? "Edit Semester" : "Add Semester");
        dialog.setModal(true);
        dialog.setSize(440, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setResizable(false);

        // Orange accent strip
        JLabel accent = new JLabel("");
        accent.setBounds(0, 0, 4, 380);
        accent.setBackground(new Color(255, 140, 0));
        accent.setOpaque(true);
        dialog.add(accent);

        JLabel lblHeader = new JLabel(isEdit ? "Edit Semester" : "Add New Semester");
        lblHeader.setBounds(24, 20, 300, 30);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));
        lblHeader.setForeground(new Color(60, 60, 60));
        dialog.add(lblHeader);

        JSeparator sep = new JSeparator();
        sep.setBounds(24, 56, 392, 1);
        sep.setForeground(new Color(220, 220, 220));
        dialog.add(sep);

        int y = 74, gap = 52;

        // Semester Name
        JLabel lblName = new JLabel("Semester Name");
        lblName.setBounds(24, y, 140, 22);
        lblName.setFont(new Font("Arial", Font.BOLD, 13));
        lblName.setForeground(new Color(100, 100, 100));
        dialog.add(lblName);

        JTextField txtName = new JTextField(isEdit ? existing.semesterName() : "");
        txtName.setBounds(170, y, 246, 32);
        txtName.setFont(new Font("Arial", Font.PLAIN, 13));
        txtName.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        dialog.add(txtName);
        y += gap;

        // School Year
        JLabel lblYear = new JLabel("School Year");
        lblYear.setBounds(24, y, 140, 22);
        lblYear.setFont(new Font("Arial", Font.BOLD, 13));
        lblYear.setForeground(new Color(100, 100, 100));
        dialog.add(lblYear);

        JTextField txtYear = new JTextField(isEdit ? existing.schoolYear() : "");
        txtYear.setBounds(170, y, 246, 32);
        txtYear.setFont(new Font("Arial", Font.PLAIN, 13));
        txtYear.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        dialog.add(txtYear);
        y += gap;

        // Start Date
        JLabel lblStart = new JLabel("Start Date");
        lblStart.setBounds(24, y, 140, 22);
        lblStart.setFont(new Font("Arial", Font.BOLD, 13));
        lblStart.setForeground(new Color(100, 100, 100));
        dialog.add(lblStart);

        JLabel lblStartHint = new JLabel("yyyy-MM-dd");
        lblStartHint.setBounds(24, y + 22, 140, 16);
        lblStartHint.setFont(new Font("Arial", Font.PLAIN, 11));
        lblStartHint.setForeground(new Color(160, 160, 160));
        dialog.add(lblStartHint);

        JTextField txtStart = new JTextField(isEdit ? existing.startDate().format(DATE_FMT) : "");
        txtStart.setBounds(170, y, 246, 32);
        txtStart.setFont(new Font("Arial", Font.PLAIN, 13));
        txtStart.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        dialog.add(txtStart);
        y += gap;

        // End Date
        JLabel lblEnd = new JLabel("End Date");
        lblEnd.setBounds(24, y, 140, 22);
        lblEnd.setFont(new Font("Arial", Font.BOLD, 13));
        lblEnd.setForeground(new Color(100, 100, 100));
        dialog.add(lblEnd);

        JLabel lblEndHint = new JLabel("yyyy-MM-dd");
        lblEndHint.setBounds(24, y + 22, 140, 16);
        lblEndHint.setFont(new Font("Arial", Font.PLAIN, 11));
        lblEndHint.setForeground(new Color(160, 160, 160));
        dialog.add(lblEndHint);

        JTextField txtEnd = new JTextField(isEdit ? existing.endDate().format(DATE_FMT) : "");
        txtEnd.setBounds(170, y, 246, 32);
        txtEnd.setFont(new Font("Arial", Font.PLAIN, 13));
        txtEnd.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        dialog.add(txtEnd);
        y += gap + 4;

        // Buttons
        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBounds(220, y, 90, 34);
        btnCancel.setFont(new Font("Arial", Font.PLAIN, 13));
        btnCancel.setForeground(new Color(100, 100, 100));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(ev -> dialog.dispose());
        dialog.add(btnCancel);

        JButton btnSave = new JButton(isEdit ? "Update" : "Create");
        btnSave.setBounds(320, y, 96, 34);
        btnSave.setFont(new Font("Arial", Font.PLAIN, 13));
        btnSave.setForeground(Color.WHITE);
        btnSave.setBackground(new Color(255, 140, 0));
        btnSave.setBorder(null);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(ev -> {
            String name  = txtName.getText().trim();
            String year  = txtYear.getText().trim();
            String start = txtStart.getText().trim();
            String end   = txtEnd.getText().trim();

            if(name.isEmpty() || year.isEmpty() || start.isEmpty() || end.isEmpty()){
                JOptionPane.showMessageDialog(dialog, "All fields are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try{
                LocalDate startDate = LocalDate.parse(start, DATE_FMT);
                LocalDate endDate   = LocalDate.parse(end, DATE_FMT);
                if(startDate.isAfter(endDate)){
                    JOptionPane.showMessageDialog(dialog, "Start date must be before end date.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                boolean success;
                if(isEdit){
                    Semester updated = new Semester(existing.semesterId(), name, year, startDate, endDate);
                    success = semesterService.updateSemester(updated);
                } else {
                    success = semesterService.createSemester(name, year, startDate, endDate);
                }

                if(success){
                    JOptionPane.showMessageDialog(dialog,
                        "Semester " + (isEdit ? "updated" : "created") + " successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadSemesters();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to save semester.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }catch(DateTimeParseException ex){
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use yyyy-MM-dd.", "Validation", JOptionPane.WARNING_MESSAGE);
            }
        });
        dialog.add(btnSave);

        dialog.setVisible(true);
    }

    @Override
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x, y, width, height);
        if(width > 0 && height > 0 && scrollPane != null){
            separator.setBounds(40, 80, width - 80, 1);
            scrollPane.setBounds(40, 150, width - 80, height - 180);
        }
        if(width > 0 && btnDelete != null){
            int rightMargin = 40;
            int gap = 15;
            int delW = 100, editW = 100, addW = 140;
            int delX  = width - rightMargin - delW;
            btnDelete.setBounds(delX, 100, delW, 36);
            int editX = delX - gap - editW;
            btnEdit.setBounds(editX, 100, editW, 36);
            int addX  = editX - gap - addW;
            btnAdd.setBounds(addX, 100, addW, 36);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnAdd){
            showDialog(null);
        }
        if(e.getSource() == btnEdit){
            Semester sel = getSelected();
            if(sel == null){
                JOptionPane.showMessageDialog(this, "Please select a semester from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showDialog(sel);
        }
        if(e.getSource() == btnDelete){
            Semester sel = getSelected();
            if(sel == null){
                JOptionPane.showMessageDialog(this, "Please select a semester from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                "Delete semester: " + sel.semesterName() + " " + sel.schoolYear() + "?\n\n"
                + "This will fail if any courses are linked to this semester.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if(confirm == JOptionPane.YES_OPTION){
                boolean success = semesterService.deleteSemester(sel.semesterId());
                if(success){
                    JOptionPane.showMessageDialog(this, "Semester deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    loadSemesters();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to delete. Semester may be referenced by courses or enrollments.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
