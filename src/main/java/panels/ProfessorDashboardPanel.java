package panels;

import charts.BarChartPanel;
import charts.DonutChartPanel;
import core.Professor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import junction.ClassSession;
import lookup.ExcuseStatus;
import lookup.MissedQuizStatus;
import services.AttendanceReportService;
import services.ClassSessionService;
import services.ExcuseLetterService;
import services.MissedQuizFlagService;
import services.ProfessorCourseService;
import services.ProfessorSectionService;

public class ProfessorDashboardPanel extends JPanel {

    private JSeparator separator;
    private JLabel lblTitle, lblSubTitle;
    private JPanel cardSections, cardSessions, cardPending, cardFlags;

    private DonutChartPanel donutChart;
    private BarChartPanel barChart;

    private int cntPresent, cntLate, cntAbsent, cntExcused;
    private int cntPendingExcuses, cntPendingFlags;

    private Professor professor;
    private final ProfessorSectionService sectionService = new ProfessorSectionService();
    private final ClassSessionService sessionService = new ClassSessionService();
    private final AttendanceReportService reportService = new AttendanceReportService();
    private final ExcuseLetterService excuseService = new ExcuseLetterService();
    private final MissedQuizFlagService flagService = new MissedQuizFlagService();
    private final ProfessorCourseService courseService = new ProfessorCourseService();

    public ProfessorDashboardPanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("Professor Dashboard");
        lblTitle.setBounds(40, 20, 300, 30);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle);

        lblSubTitle = new JLabel("Today's sessions and pending action items");
        lblSubTitle.setBounds(40, 50, 500, 20);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSubTitle.setForeground(new Color(140, 140, 140));
        add(lblSubTitle);

        separator = new JSeparator();
        separator.setForeground(new Color(230, 230, 230));
        add(separator);

        cardSections = DashboardCard("My Sections", "0", new Color(255, 140, 0));
        cardSessions = DashboardCard("Sessions Today", "0", new Color(40, 167, 69));
        cardPending = DashboardCard("Pending Excuses", "0", new Color(220, 53, 69));
        cardFlags = DashboardCard("Missed Quiz Flags", "0", new Color(255, 193, 7));
        add(cardSections);
        add(cardSessions);
        add(cardPending);
        add(cardFlags);

        donutChart = new DonutChartPanel();
        donutChart.setTitle("Attendance Breakdown (Today)");
        add(donutChart);

        barChart = new BarChartPanel();
        barChart.setTitle("Pending Action Items");
        add(barChart);
    }

    public void setProfessor(Professor professor) throws SQLException{
        this.professor = professor;
        loadStats();
    }

    private void loadStats() throws SQLException{
        if(professor == null){
            return;
        }

        int sectionCount = sectionService.getSectionsByProfessor(professor.professorId()).size();
        updateCardValue(0, String.valueOf(sectionCount));

        LocalDate today = LocalDate.now();
        List<ClassSession> allSessions = sessionService.getClassSessionsByProfessor(professor.professorId());
        List<ClassSession> todaySessions = allSessions.stream().filter(s -> s.sessionDate().equals(today)).toList();
        updateCardValue(1, String.valueOf(todaySessions.size()));

        cntPresent = 0;
        cntLate = 0;
        cntAbsent = 0;
        cntExcused = 0;
        for(ClassSession session : todaySessions){
            Map<String, Integer> stats = reportService.getDailyAttendanceStats(session.sessionId());
            cntPresent += stats.getOrDefault("PRESENT", 0);
            cntLate += stats.getOrDefault("LATE", 0);
            cntAbsent += stats.getOrDefault("ABSENT", 0);
            cntExcused += stats.getOrDefault("EXCUSED", 0);
        }

        var professorCourseIds = courseService.getCoursesByProfessor(professor.professorId())
            .stream().map(pc -> pc.course().courseId()).toList();

        long pendingExcuses = excuseService.getAll().stream()
            .filter(e -> e.status() == ExcuseStatus.PENDING)
            .filter(e -> professorCourseIds.contains(e.course().courseId()))
            .count();
        cntPendingExcuses = (int) pendingExcuses;
        updateCardValue(2, String.valueOf(cntPendingExcuses));

        long pendingFlags = flagService.getAllMissedQuizFlags().stream().filter(f -> f.missedQuizStatus() == MissedQuizStatus.PENDING).filter(f -> professorCourseIds.contains(f.quiz().course().courseId())).count();
        cntPendingFlags = (int) pendingFlags;
        updateCardValue(3, String.valueOf(cntPendingFlags));

        refreshCharts();
    }

    private void refreshCharts(){
        Map<String, Integer> donutData = new LinkedHashMap<>();
        Map<String, Color> donutColors = new LinkedHashMap<>();
        donutData.put("Present", cntPresent);
        donutColors.put("Present", new Color(40, 167, 69));
        donutData.put("Late", cntLate);
        donutColors.put("Late", new Color(23, 162, 184));
        donutData.put("Absent", cntAbsent);
        donutColors.put("Absent", new Color(220, 53, 69));
        donutData.put("Excused", cntExcused);
        donutColors.put("Excused", new Color(255, 193, 7));
        int total = cntPresent + cntLate + cntAbsent + cntExcused;
        donutChart.setData(donutData, donutColors);
        donutChart.setCenterLabel(String.valueOf(total));

        Map<String, Integer> barData = new LinkedHashMap<>();
        Map<String, Color> barColors = new LinkedHashMap<>();
        barData.put("Excuses", cntPendingExcuses);
        barColors.put("Excuses", new Color(220, 53, 69));
        barData.put("Flags", cntPendingFlags);
        barColors.put("Flags", new Color(255, 193, 7));
        barChart.setData(barData, barColors);
    }

    private JPanel DashboardCard(String title, String value, Color accentColor){
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1), BorderFactory.createEmptyBorder(0, 0, 0, 0)));

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

        if(width > 0 && cardSections != null){
            int leftM = 40;
            int gap = 16;
            int cardY = 90;
            int cardH = 100;
            int availW = width - leftM * 2;
            int cardW = (availW - gap * 3) / 4;

            cardSections.setBounds(leftM, cardY, cardW, cardH);
            cardSessions.setBounds(leftM + (cardW + gap), cardY, cardW, cardH);
            cardPending.setBounds(leftM + (cardW + gap) * 2, cardY, cardW, cardH);
            cardFlags.setBounds(leftM + (cardW + gap) * 3, cardY, cardW, cardH);
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
                cardSections;
            case 1 ->
                cardSessions;
            case 2 ->
                cardPending;
            case 3 ->
                cardFlags;
            default ->
                null;
        };
        if(targetCard != null){
            for(Component c : targetCard.getComponents()){
                if(c instanceof JLabel label && label.getFont().getSize() == 28){
                    label.setText(value);
                    break;
                }
            }
        }
    }
}
