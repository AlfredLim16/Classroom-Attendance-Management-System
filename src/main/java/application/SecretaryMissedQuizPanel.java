package application;

import assessment.MissedQuizFlag;
import assessment.MissedQuizFlagService;
import course.Section;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import user.SecretaryDAO;
import user.Student;

public class SecretaryMissedQuizPanel extends JPanel {

    private JLabel lblTitle, lblSubTitle;
    private JSeparator separator;
    private JTable flagTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private Section section;
    private final MissedQuizFlagService flagService = new MissedQuizFlagService();
    private final SecretaryDAO secretaryDAO = new SecretaryDAO();

    public SecretaryMissedQuizPanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("Missed Quiz Flags");
        lblTitle.setBounds(40, 20, 300, 30);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle);

        lblSubTitle = new JLabel("Description");
        lblSubTitle.setBounds(40, 50, 400, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblSubTitle);

        separator = new JSeparator();
        separator.setForeground(Color.BLACK);
        add(separator);

        String[] columns = {"Date", "Student", "Quiz/Lab", "Status", "Decision", "Decided By"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        flagTable = new JTable(tableModel);
        flagTable.setRowHeight(28);
        flagTable.setFillsViewportHeight(true);
        flagTable.getTableHeader().setReorderingAllowed(false);
        flagTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        flagTable.getTableHeader().setBackground(new Color(255, 255, 255));
        flagTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        flagTable.getTableHeader().setPreferredSize(new Dimension(flagTable.getPreferredSize().width, 28));

        DefaultTableCellRenderer centerCollumn = new DefaultTableCellRenderer();
        centerCollumn.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < flagTable.getColumnCount(); i++){
            flagTable.getColumnModel().getColumn(i).setCellRenderer(centerCollumn);
        }

        scrollPane = new JScrollPane(flagTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane);
    }

    public void setSection(Section section){
        this.section = section;
        loadFlags();
    }

    private void loadFlags(){
        if(section == null) return;

        List<Student> students = secretaryDAO.findStudentsBySectionId(section.sectionId());
        Set<Integer> studentIds = students.stream().map(Student::studentId).collect(Collectors.toSet());

        List<MissedQuizFlag> allFlags = flagService.getAllMissedQuizFlags();
        tableModel.setRowCount(0);

        for(MissedQuizFlag flag : allFlags){
            if(studentIds.contains(flag.student().studentId())){
                addFlagRow(
                    flag.quiz().quizDate().toString(),
                    flag.student().firstName() + " " + flag.student().lastName(),
                    flag.quiz().course().courseCode(),
                    flag.status().toString(),
                    flag.decisionType() != null ? flag.decisionType().toString() : "Pending",
                    flag.decidedBy() != null ? flag.decidedBy().getFullName() : "-"
                );
            }
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        if (width > 0 && height > 0 && scrollPane != null) {
            separator.setBounds(40, 80, width - 80, height - 160);
            scrollPane.setBounds(40, 100, width - 80, height - 140);
        }
    }

    public void addFlagRow(String date, String student, String quiz, String status, String decision, String decidedBy){
        tableModel.addRow(new Object[]{date, student, quiz, status, decision, decidedBy});
    }
}