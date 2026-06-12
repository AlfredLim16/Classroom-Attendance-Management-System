package panels;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import junction.Attendance;
import junction.StudentCourse;
import services.AttendanceService;
import services.AttendanceQueryService;
import services.StudentCourseService;

public class StudentAttendancePanel extends JPanel implements ActionListener {

    private static final String CARD_SUMMARY = "SUMMARY";
    private static final String CARD_DETAIL  = "DETAIL";

    private JPanel tabBar;
    private JButton btnTabSummary, btnTabDetail;
    private JPanel cardHost;

    private JPanel summaryCard;
    private JLabel lblSumTitle, lblSumSub;
    private JSeparator sumSep;
    private JTable summaryTable;
    private DefaultTableModel summaryModel;
    private JScrollPane summaryScroll;

    private JPanel detailCard;
    private JLabel lblDetTitle, lblDetSub;
    private JSeparator detSep;
    private JTable detailTable;
    private DefaultTableModel detailModel;
    private JScrollPane detailScroll;

    private final AttendanceService attendanceService     = new AttendanceService();
    private final AttendanceQueryService attendanceQuery  = new AttendanceQueryService();
    private final StudentCourseService courseService      = new StudentCourseService();

    public StudentAttendancePanel(){
        setLayout(null);
        setBackground(Color.WHITE);
        buildTabBar();
        buildCardHost();
        buildSummaryCard();
        buildDetailCard();
        switchTab(CARD_SUMMARY);
    }

    private void buildTabBar(){
        tabBar = new JPanel(null);
        tabBar.setBackground(Color.WHITE);
        add(tabBar);

        btnTabSummary = tabButton("Summary by Course");
        btnTabSummary.addActionListener(e -> switchTab(CARD_SUMMARY));
        tabBar.add(btnTabSummary);

        btnTabDetail = tabButton("Full Record");
        btnTabDetail.addActionListener(e -> switchTab(CARD_DETAIL));
        tabBar.add(btnTabDetail);
    }

    private JButton tabButton(String text){
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.PLAIN, 14));
        b.setBackground(Color.WHITE);
        b.setForeground(new Color(100, 100, 100));
        b.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        return b;
    }

    private void switchTab(String card){
        ((CardLayout) cardHost.getLayout()).show(cardHost, card);
        Color active   = new Color(255, 140, 0);
        Color inactive = new Color(200, 200, 200);
        if(CARD_SUMMARY.equals(card)){
            btnTabSummary.setForeground(active);
            btnTabSummary.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, active));
            btnTabDetail.setForeground(new Color(100, 100, 100));
            btnTabDetail.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, inactive));
        } else {
            btnTabDetail.setForeground(active);
            btnTabDetail.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, active));
            btnTabSummary.setForeground(new Color(100, 100, 100));
            btnTabSummary.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, inactive));
        }
    }

    private void buildCardHost(){
        cardHost = new JPanel(new CardLayout());
        cardHost.setBackground(Color.WHITE);
        add(cardHost);
    }

    private void buildSummaryCard(){
        summaryCard = new JPanel(null);
        summaryCard.setBackground(Color.WHITE);

        lblSumTitle = new JLabel("Attendance Summary");
        lblSumTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblSumTitle.setForeground(new Color(60, 60, 60));
        summaryCard.add(lblSumTitle);

        lblSumSub = new JLabel("Your attendance count per subject this semester.");
        lblSumSub.setFont(new Font("Arial", Font.PLAIN, 14));
        summaryCard.add(lblSumSub);

        sumSep = new JSeparator();
        sumSep.setForeground(new Color(220, 220, 220));
        summaryCard.add(sumSep);

        String[] cols = {"Course Code", "Course Name", "Present", "Late", "Absent", "Excused", "Total Sessions"};
        summaryModel = new DefaultTableModel(cols, 0){
            @Override public boolean isCellEditable(int r, int c){ return false; }
        };
        summaryTable = buildTable(summaryModel);
        summaryScroll = buildScroll(summaryTable);
        summaryCard.add(summaryScroll);

        cardHost.add(summaryCard, CARD_SUMMARY);
    }

    private void buildDetailCard(){
        detailCard = new JPanel(null);
        detailCard.setBackground(Color.WHITE);

        lblDetTitle = new JLabel("Full Attendance Record");
        lblDetTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblDetTitle.setForeground(new Color(60, 60, 60));
        detailCard.add(lblDetTitle);

        lblDetSub = new JLabel("All recorded attendance entries across all your courses.");
        lblDetSub.setFont(new Font("Arial", Font.PLAIN, 14));
        detailCard.add(lblDetSub);

        detSep = new JSeparator();
        detSep.setForeground(new Color(220, 220, 220));
        detailCard.add(detSep);

        String[] cols = {"Date", "Course", "Status", "Recorded By"};
        detailModel = new DefaultTableModel(cols, 0){
            @Override public boolean isCellEditable(int r, int c){ return false; }
        };
        detailTable = buildTable(detailModel);
        detailScroll = buildScroll(detailTable);
        detailCard.add(detailScroll);

        cardHost.add(detailCard, CARD_DETAIL);
    }

    public void loadAttendanceForStudent(int studentId){
        summaryModel.setRowCount(0);
        detailModel.setRowCount(0);

        List<StudentCourse> enrollments = courseService.getCoursesByStudent(studentId);

        for(StudentCourse sc : enrollments){
            try{
                int[] summary = attendanceService.getAttendanceSummary(studentId, sc.course().courseId());
                int present  = summary[0];
                int late     = summary[1];
                int absent   = summary[2];
                int excused  = summary[3];
                int total    = present + late + absent + excused;
                summaryModel.addRow(new Object[]{
                    sc.course().courseCode(),
                    sc.course().courseName(),
                    present, late, absent, excused, total
                });
            }catch(SQLException e){
                System.err.println("[StudentAttendancePanel] loadSummary courseId=" + sc.course().courseId() + ": " + e.getMessage());
            }
        }

        List<Attendance> all = attendanceQuery.getAttendancesByStudent(studentId);
        for(Attendance a : all){
            detailModel.addRow(new Object[]{
                a.session().sessionDate().toString(),
                a.session().course().courseCode() + " - " + a.session().course().courseName(),
                a.status().getStatusName(),
                a.recordedBy() != null ? a.recordedBy().userName() : "-"
            });
        }
    }

    private JTable buildTable(DefaultTableModel model){
        JTable t = new JTable(model);
        t.setRowHeight(28);
        t.setFillsViewportHeight(true);
        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        t.getTableHeader().setBackground(Color.WHITE);
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        t.getTableHeader().setPreferredSize(new Dimension(t.getPreferredSize().width, 28));
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < t.getColumnCount(); i++){
            t.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        return t;
    }

    private JScrollPane buildScroll(JTable t){
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    @Override
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x, y, width, height);
        if(width <= 0 || height <= 0) return;

        int tabH = 44;
        tabBar.setBounds(0, 0, width, tabH);
        btnTabSummary.setBounds(40, 10, 180, 28);
        btnTabDetail.setBounds(240, 10, 140, 28);
        cardHost.setBounds(0, tabH, width, height - tabH);

        int cardH = height - tabH;

        if(summaryCard != null){
            lblSumTitle.setBounds(40, 20, 400, 30);
            lblSumSub.setBounds(40, 50, 700, 30);
            sumSep.setBounds(40, 82, width - 80, 1);
            summaryScroll.setBounds(40, 100, width - 80, cardH - 130);
        }

        if(detailCard != null){
            lblDetTitle.setBounds(40, 20, 400, 30);
            lblDetSub.setBounds(40, 50, 700, 30);
            detSep.setBounds(40, 82, width - 80, 1);
            detailScroll.setBounds(40, 100, width - 80, cardH - 130);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){}

    public DefaultTableModel getTableModel(){
        return detailModel;
    }
}
