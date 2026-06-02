package application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import user.Professor;
import user.ProfessorSection;
import user.ProfessorSectionService;
import user.SecretaryDAO;

public class ProfessorSectionsPanel extends JPanel {

    private JLabel lblTitle, lblSubTitle;
    private JSeparator separator;
    private JTable sectionTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private Professor professor;
    private final ProfessorSectionService sectionService = new ProfessorSectionService();
    private final SecretaryDAO secretaryDAO = new SecretaryDAO();

    public ProfessorSectionsPanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("My Sections");
        lblTitle.setBounds(40, 20, 300, 30);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle);

        lblSubTitle = new JLabel("Description");
        lblSubTitle.setBounds(40, 50, 800, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblSubTitle);

        separator = new JSeparator();
        separator.setForeground(Color.BLACK);
        add(separator);

        String[] columns = {"Section", "Course", "Program", "Year Level", "Semester", "Students"};
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
    }

    public void setProfessor(Professor professor){
        this.professor = professor;
        loadSections();
    }

    private void loadSections(){
        if(professor == null) return;
        tableModel.setRowCount(0);

        List<ProfessorSection> sections = sectionService.getSectionsByProfessor(professor.professorId());
        for(ProfessorSection ps : sections){
            int studentCount = secretaryDAO.findStudentsBySectionId(ps.section().sectionId()).size();
            addSectionRow(
                ps.section().sectionCode(),
                ps.section().program().programName(),
                ps.section().program().programName(),
                ps.section().yearLevel().getYearLevelName(),
                ps.semester().semesterName() + " " + ps.semester().schoolYear(),
                String.valueOf(studentCount)
            );
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x, y, width, height);
        if(width > 0 && height > 0 && scrollPane != null){
            separator.setBounds(40, 80, width - 80, height - 160);
            scrollPane.setBounds(40, 150, width - 80, height - 180);
        }
    }

    public void addSectionRow(String section, String course, String program, String yearLevel, String semester, String students){
        tableModel.addRow(new Object[]{section, course, program, yearLevel, semester, students});
    }
}