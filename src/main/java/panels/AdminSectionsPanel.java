package panels;

import core.Program;
import core.Section;
import lookup.YearLevel;
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
import dao.SecretaryStudentDAO;
import services.ProgramService;
import services.SectionService;

public class AdminSectionsPanel extends JPanel implements ActionListener {

    private JLabel lblTitle, lblSubTitle;
    private JSeparator separator;
    private JTextField txtSearch;
    private JComboBox<String> cmbFilter;
    private JButton btnSearch, btnAdd, btnEdit, btnDelete;
    private JTable sectionTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private final SectionService sectionService = new SectionService();
    private final ProgramService programService = new ProgramService();
    private final SecretaryStudentDAO secretaryDAO = new SecretaryStudentDAO();
    private final List<Section> rowSections = new java.util.ArrayList<>();

    public AdminSectionsPanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("Section Management");
        lblTitle.setBounds(40, 20, 300, 30);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle);

        lblSubTitle = new JLabel("Manage class sections and student assignments");
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

        cmbFilter = new JComboBox<>(new String[]{"All Fields", "Section Code", "Program", "Year Level"});
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

        btnAdd = new JButton("+ Add Section");
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

        String[] columns = {"Section Code", "Program", "Year Level", "Students"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        sectionTable = new JTable(tableModel);
        sectionTable.setRowHeight(28);
        sectionTable.setFillsViewportHeight(true);
        sectionTable.getTableHeader().setReorderingAllowed(false);
        sectionTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        sectionTable.getTableHeader().setBackground(new Color(255, 255, 255));
        sectionTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        sectionTable.getTableHeader().setPreferredSize(new Dimension(sectionTable.getPreferredSize().width, 28));

        DefaultTableCellRenderer centerCollumn = new DefaultTableCellRenderer();
        centerCollumn.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < sectionTable.getColumnCount(); i++){
            sectionTable.getColumnModel().getColumn(i).setCellRenderer(centerCollumn);
        }

        scrollPane = new JScrollPane(sectionTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane);

        loadSections();
    }

    private void loadSections(){
        tableModel.setRowCount(0);
        rowSections.clear();
        List<Section> sections = sectionService.getAllSections();
        for(Section s : sections){
            rowSections.add(s);
            int count = secretaryDAO.findStudentsBySectionId(s.sectionId()).size();
            addSectionRow(
                s.sectionCode(),
                s.program().programName(),
                s.yearLevel().getYearLevelName(),
                String.valueOf(count)
            );
        }
    }

    private Section getSelectedSection(){
        int row = sectionTable.getSelectedRow();
        if(row < 0 || row >= rowSections.size()){
            return null;
        }
        return rowSections.get(row);
    }

    private void showSectionDialog(Section section, boolean isEdit){
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), isEdit ? "Edit Section" : "Add Section");
        dialog.setModal(true);
        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setResizable(false);

        JLabel accent = new JLabel("");
        accent.setBounds(0, 0, 4, 380);
        accent.setBackground(new Color(255, 140, 0));
        accent.setOpaque(true);
        dialog.add(accent);

        JLabel lblHeader = new JLabel(isEdit ? "Edit Section" : "Add New Section");
        lblHeader.setBounds(30, 20, 300, 30);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));
        lblHeader.setForeground(new Color(60, 60, 60));
        dialog.add(lblHeader);

        JSeparator sep = new JSeparator();
        sep.setBounds(30, 55, 360, 1);
        sep.setForeground(new Color(220, 220, 220));
        dialog.add(sep);

        int y = 72, gap = 46;

        JLabel lblCode = new JLabel("Section Code");
        lblCode.setBounds(30, y, 120, 25);
        lblCode.setFont(new Font("Arial", Font.BOLD, 13));
        lblCode.setForeground(new Color(100, 100, 100));
        dialog.add(lblCode);

        JTextField txtCode = new JTextField(isEdit ? section.sectionCode() : "");
        txtCode.setBounds(150, y, 230, 32);
        txtCode.setFont(new Font("Arial", Font.PLAIN, 13));
        txtCode.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        dialog.add(txtCode);
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
        cmbProgram.setBounds(150, y, 230, 32);
        cmbProgram.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbProgram.setBackground(Color.WHITE);
        cmbProgram.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbProgram.setFocusable(false);
        if(isEdit){
            cmbProgram.setSelectedItem(section.program().programName());
        }
        dialog.add(cmbProgram);
        y += gap;

        JLabel lblYear = new JLabel("Year Level");
        lblYear.setBounds(30, y, 120, 25);
        lblYear.setFont(new Font("Arial", Font.BOLD, 13));
        lblYear.setForeground(new Color(100, 100, 100));
        dialog.add(lblYear);

        JComboBox<String> cmbYear = new JComboBox<>(new String[]{"1st Year", "2nd Year", "3rd Year", "4th Year"});
        cmbYear.setBounds(150, y, 230, 32);
        cmbYear.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbYear.setBackground(Color.WHITE);
        cmbYear.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbYear.setFocusable(false);
        if(isEdit){
            cmbYear.setSelectedItem(section.yearLevel().getYearLevelName());
        }
        dialog.add(cmbYear);
        y += gap + 20;

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBounds(180, y, 100, 36);
        btnCancel.setFont(new Font("Arial", Font.PLAIN, 14));
        btnCancel.setForeground(new Color(100, 100, 100));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(ev -> dialog.dispose());
        dialog.add(btnCancel);

        JButton btnSaveDialog = new JButton(isEdit ? "Update" : "Create");
        btnSaveDialog.setBounds(290, y, 100, 36);
        btnSaveDialog.setFont(new Font("Arial", Font.PLAIN, 14));
        btnSaveDialog.setForeground(Color.WHITE);
        btnSaveDialog.setBackground(new Color(255, 140, 0));
        btnSaveDialog.setBorder(null);
        btnSaveDialog.setFocusPainted(false);
        btnSaveDialog.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSaveDialog.addActionListener(ev -> {
            String code = txtCode.getText().trim();
            if(code.isEmpty()){
                JOptionPane.showMessageDialog(dialog, "Section code is required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String programName = cmbProgram.getSelectedItem().toString();
            String yearName = cmbYear.getSelectedItem().toString();

            Program program = programService.getAllPrograms().stream()
                .filter(p -> p.programName().equals(programName)).findFirst().orElse(null);
            YearLevel yearLevel = switch (yearName) {
                case "1st Year" -> YearLevel.FIRST_YEAR;
                case "2nd Year" -> YearLevel.SECOND_YEAR;
                case "3rd Year" -> YearLevel.THIRD_YEAR;
                case "4th Year" -> YearLevel.FOURTH_YEAR;
                default -> YearLevel.FIRST_YEAR;
            };

            if(program == null){
                JOptionPane.showMessageDialog(dialog, "Invalid program selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success;
            if(isEdit){
                Section updated = new Section(section.sectionId(), program, yearLevel, code);
                success = sectionService.updateSection(updated);
            } else {
                Section created = sectionService.createSection(program, yearLevel, code);
                success = created != null && created.sectionId() > 0;
            }

            if(success){
                JOptionPane.showMessageDialog(dialog,
                    "Section " + (isEdit ? "updated" : "created") + " successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadSections();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to save section. Section code may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
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

    public void addSectionRow(String code, String program, String yearLevel, String students){
        tableModel.addRow(new Object[]{code, program, yearLevel, students});
    }

    private void performSearch(){
        String query = txtSearch.getText().trim().toLowerCase();
        String filter = cmbFilter.getSelectedItem().toString();
        if(query.isEmpty()){
            loadSections();
            return;
        }
        tableModel.setRowCount(0);
        for(Section s : rowSections){
            boolean match = false;
            switch(filter){
                case "Section Code" -> match = s.sectionCode().toLowerCase().contains(query);
                case "Program" -> match = s.program().programName().toLowerCase().contains(query);
                case "Year Level" -> match = s.yearLevel().getYearLevelName().toLowerCase().contains(query);
                default -> match = s.sectionCode().toLowerCase().contains(query)
                    || s.program().programName().toLowerCase().contains(query)
                    || s.yearLevel().getYearLevelName().toLowerCase().contains(query);
            }
            if(match){
                int count = secretaryDAO.findStudentsBySectionId(s.sectionId()).size();
                addSectionRow(s.sectionCode(), s.program().programName(), s.yearLevel().getYearLevelName(), String.valueOf(count));
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnSearch){
            performSearch();
        }
        if(e.getSource() == btnAdd){
            showSectionDialog(null, false);
        }
        if(e.getSource() == btnEdit){
            Section selected = getSelectedSection();
            if(selected == null){
                JOptionPane.showMessageDialog(this, "Please select a section from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showSectionDialog(selected, true);
        }
        if(e.getSource() == btnDelete){
            Section selected = getSelectedSection();
            if(selected == null){
                JOptionPane.showMessageDialog(this, "Please select a section from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                "Delete section: " + selected.sectionCode() + "?\n\nThis action cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if(confirm == JOptionPane.YES_OPTION){
                boolean success = sectionService.deleteSection(selected.sectionId());
                if(success){
                    JOptionPane.showMessageDialog(this, "Section deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    loadSections();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete section. Section may be referenced by other records.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}