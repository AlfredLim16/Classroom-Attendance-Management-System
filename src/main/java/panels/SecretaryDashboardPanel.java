package panels;

import charts.BarChartPanel;
import charts.DonutChartPanel;
import core.Section;
import core.Student;
import dao.SecretaryStudentDAO;
import java.awt.Color;
import java.awt.Font;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import junction.ClassSession;
import services.AttendanceReportService;
import services.ClassSessionService;

public class SecretaryDashboardPanel extends JPanel {

    private JSeparator separator;
    private JLabel lblTitle, lblSubTitle;
    private JPanel cardTotal, cardPresent, cardAbsent, cardExcused;

    private DonutChartPanel donutChart;
    private BarChartPanel barChart;
    private int cntPresent, cntAbsent, cntExcused;

    private Section section;
    private final AttendanceReportService reportService = new AttendanceReportService();
    private final ClassSessionService sessionService = new ClassSessionService();
    private final SecretaryStudentDAO secretaryDAO = new SecretaryStudentDAO();

    public SecretaryDashboardPanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("Secretary Dashboard");
        lblTitle.setBounds(40, 20, 300, 30);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle);

        lblSubTitle = new JLabel("Today's attendance overview for your section");
        lblSubTitle.setBounds(40, 50, 500, 20);
        lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubTitle.setForeground(new Color(140, 140, 140));
        add(lblSubTitle);

        separator = new JSeparator();
        separator.setForeground(new Color(230, 230, 230));
        add(separator);

        cardTotal = DashboardCard("Total Students", "0", new Color(255, 140, 0));
        cardPresent = DashboardCard("Present Today", "0", new Color(40, 167, 69));
        cardAbsent = DashboardCard("Absent Today", "0", new Color(220, 53, 69));
        cardExcused = DashboardCard("Excused", "0", new Color(255, 193, 7));
        add(cardTotal);
        add(cardPresent);
        add(cardAbsent);
        add(cardExcused);

        donutChart = new DonutChartPanel();
        donutChart.setTitle("Attendance Breakdown (Today)");
        add(donutChart);

        barChart = new BarChartPanel();
        barChart.setTitle("Attendance Count (Today)");
        add(barChart);
    }

    public void setSection(Section section){
        this.section = section;
        loadStats();
    }

    private void loadStats(){
        if(section == null){
            return;
        }

        List<Student> students = secretaryDAO.findStudentsBySectionId(section.sectionId());
        updateCardValue(0, String.valueOf(students.size()));

        LocalDate today = LocalDate.now();
        List<ClassSession> todaysSessions = sessionService.getClassSessionsBySection(section.sectionId())
            .stream()
            .filter(s -> s.sessionDate().equals(today))
            .toList();

        cntPresent = 0;
        cntAbsent = 0;
        cntExcused = 0;
        for(ClassSession session : todaysSessions){
            Map<String, Integer> stats = reportService.getDailyAttendanceStats(session.sessionId());
            cntPresent += stats.getOrDefault("PRESENT", 0);
            cntAbsent += stats.getOrDefault("ABSENT", 0);
            cntExcused += stats.getOrDefault("EXCUSED", 0);
        }

        updateCardValue(1, String.valueOf(cntPresent));
        updateCardValue(2, String.valueOf(cntAbsent));
        updateCardValue(3, String.valueOf(cntExcused));

        refreshCharts();
    }

    private void refreshCharts(){
        Map<String, Integer> donutData = new LinkedHashMap<>();
        Map<String, Color> donutColors = new LinkedHashMap<>();
        donutData.put("Present", cntPresent);
        donutColors.put("Present", new Color(40, 167, 69));
        donutData.put("Absent", cntAbsent);
        donutColors.put("Absent", new Color(220, 53, 69));
        donutData.put("Excused", cntExcused);
        donutColors.put("Excused", new Color(255, 193, 7));
        int total = cntPresent + cntAbsent + cntExcused;
        donutChart.setData(donutData, donutColors);
        donutChart.setCenterLabel(String.valueOf(total));

        Map<String, Integer> barData = new LinkedHashMap<>();
        Map<String, Color> barColors = new LinkedHashMap<>();
        barData.put("Present", cntPresent);
        barColors.put("Present", new Color(40, 167, 69));
        barData.put("Absent", cntAbsent);
        barColors.put("Absent", new Color(220, 53, 69));
        barData.put("Excused", cntExcused);
        barColors.put("Excused", new Color(255, 193, 7));
        barChart.setData(barData, barColors);
    }

    private JPanel DashboardCard(String title, String value, Color accentColor){
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        JLabel lblAccent = new JLabel("");
        lblAccent.setBounds(0, 0, 4, 100);
        lblAccent.setBackground(accentColor);
        lblAccent.setOpaque(true);
        card.add(lblAccent);

        JLabel lTitle = new JLabel(title);
        lTitle.setBounds(20, 15, 180, 20);
        lTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lTitle.setForeground(new Color(100, 100, 100));
        card.add(lTitle);

        JLabel lValue = new JLabel(value);
        lValue.setBounds(20, 45, 180, 35);
        lValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lValue.setForeground(new Color(60, 60, 60));
        card.add(lValue);

        return card;
    }

    @Override
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x, y, width, height);

        if(width > 0 && separator != null){
            separator.setBounds(40, 78, width - 80, 1);
        }

        if(width > 0 && cardTotal != null){
            int leftM = 40;
            int gap = 16;
            int cardY = 90;
            int cardH = 100;
            int availW = width - leftM * 2;
            int cardW = (availW - gap * 3) / 4;

            cardTotal.setBounds(leftM, cardY, cardW, cardH);
            cardPresent.setBounds(leftM + (cardW + gap), cardY, cardW, cardH);
            cardAbsent.setBounds(leftM + (cardW + gap) * 2, cardY, cardW, cardH);
            cardExcused.setBounds(leftM + (cardW + gap) * 3, cardY, cardW, cardH);
        }

        if(width > 0 && donutChart != null && barChart != null){
            int chartY = 210;
            int chartH = height - chartY - 20;
            if(chartH < 80){
                chartH = 80;
            }
            int leftM = 40;
            int gap = 20;
            int halfW = (width - leftM * 2 - gap) / 2;

            donutChart.setBounds(leftM, chartY, halfW, chartH);
            barChart.setBounds(leftM + halfW + gap, chartY, halfW, chartH);
        }
    }

    public void updateCardValue(int cardIndex, String value){
        JPanel targetCard = switch(cardIndex){
            case 0 ->
                cardTotal;
            case 1 ->
                cardPresent;
            case 2 ->
                cardAbsent;
            case 3 ->
                cardExcused;
            default ->
                null;
        };
        if(targetCard != null){
            for(java.awt.Component c : targetCard.getComponents()){
                if(c instanceof JLabel label && label.getFont().getSize() == 28){
                    label.setText(value);
                    break;
                }
            }
        }
    }
}
