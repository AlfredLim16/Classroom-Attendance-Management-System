package panels;

import core.Student;
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
import junction.ClassSession;
import services.ClassSessionService;

public class StudentSchedulePanel extends JPanel {

    private JLabel lblTitle, lblSubTitle;
    private JSeparator separator;
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private final ClassSessionService sessionService = new ClassSessionService();

    public StudentSchedulePanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("My Schedule");
        lblTitle.setBounds(40, 20, 300, 30);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle);

        lblSubTitle = new JLabel("Upcoming and past class sessions for your section.");
        lblSubTitle.setBounds(40, 50, 800, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblSubTitle);

        separator = new JSeparator();
        separator.setForeground(new Color(220, 220, 220));
        add(separator);

        String[] columns = {"Date", "Course", "Context", "Start Time", "End Time", "Professor"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column){ return false; }
        };

        scheduleTable = new JTable(tableModel);
        scheduleTable.setRowHeight(28);
        scheduleTable.setFillsViewportHeight(true);
        scheduleTable.getTableHeader().setReorderingAllowed(false);
        scheduleTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        scheduleTable.getTableHeader().setBackground(Color.WHITE);
        scheduleTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        scheduleTable.getTableHeader().setPreferredSize(new Dimension(scheduleTable.getPreferredSize().width, 28));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < scheduleTable.getColumnCount(); i++){
            scheduleTable.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane);
    }

    public void loadScheduleForStudent(Student student){
        tableModel.setRowCount(0);
        if(student == null) return;

        List<ClassSession> sessions = sessionService.getClassSessionsBySection(student.section().sectionId());
        for(ClassSession s : sessions){
            String context = s.contextType().getContextName();
            if(s.event() != null){
                context = "Event: " + s.event().eventName();
            }
            tableModel.addRow(new Object[]{
                s.sessionDate().toString(),
                s.course().courseCode() + " - " + s.course().courseName(),
                context,
                s.startTime().toString(),
                s.endTime().toString(),
                s.professor().getFullName()
            });
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x, y, width, height);
        if(width > 0 && height > 0 && scrollPane != null){
            separator.setBounds(40, 80, width - 80, 1);
            scrollPane.setBounds(40, 150, width - 80, height - 180);
        }
    }
}
