package panels;

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
import junction.Attendance;
import services.AttendanceQueryService;

public class StudentAttendancePanel extends JPanel {

    private JLabel lblTitle, lblSubTitle;
    private JSeparator separator;
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private final AttendanceQueryService attendanceService = new AttendanceQueryService();

    public StudentAttendancePanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("My Attendance");
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

        String[] columns = {"Date", "Course", "Status", "Recorded By"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        attendanceTable = new JTable(tableModel);
        attendanceTable.setRowHeight(28);
        attendanceTable.setFillsViewportHeight(true);
        attendanceTable.getTableHeader().setReorderingAllowed(false);
        attendanceTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        attendanceTable.getTableHeader().setBackground(new Color(255, 255, 255));
        attendanceTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        attendanceTable.getTableHeader().setPreferredSize(new Dimension(attendanceTable.getPreferredSize().width, 28));

        DefaultTableCellRenderer centerCollumn = new DefaultTableCellRenderer();
        centerCollumn.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < attendanceTable.getColumnCount(); i++){
            attendanceTable.getColumnModel().getColumn(i).setCellRenderer(centerCollumn);
        }

        scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setBounds(40, 150, 800, 400);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane);
    }

    @Override
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x, y, width, height);
        if(width > 0 && height > 0 && scrollPane != null){
            separator.setBounds(40, 80, width - 80, height - 160);
            scrollPane.setBounds(40, 150, width - 80, height - 180);
        }
    }

    public void loadAttendanceForStudent(int studentId){
        tableModel.setRowCount(0);
        List<Attendance> attendances = attendanceService.getAttendancesByStudent(studentId);
        for(Attendance a : attendances){
            String date = a.session().sessionDate().toString();
            String course = a.session().course().courseCode() + " - " + a.session().course().courseName();
            String status = a.status().getStatusName();
            String recordedBy = a.recordedBy().userName();
            addAttendanceRow(date, course, status, recordedBy);
        }
    }

    public void addAttendanceRow(String date, String course, String status, String recordedBy){
        tableModel.addRow(new Object[]{date, course, status, recordedBy});
    }

    public DefaultTableModel getTableModel(){
        return tableModel;
    }
}
