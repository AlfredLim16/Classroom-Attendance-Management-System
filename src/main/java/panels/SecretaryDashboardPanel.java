package panels;

import core.Section;
import core.Student;
import junction.ClassSession;
import dao.SecretaryStudentDAO;
import services.AttendanceReportService;
import services.ClassSessionService;
import java.awt.Color;
import java.awt.Font;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

public class SecretaryDashboardPanel extends JPanel {

    private JSeparator separator;
    private JLabel lblTitle, lblSubTitle, lblStats;
    private JPanel cardTotal, cardPresent, cardAbsent, cardExcused;

    private Section section;
    private final AttendanceReportService reportService = new AttendanceReportService();
    private final ClassSessionService sessionService = new ClassSessionService();
    private final SecretaryStudentDAO secretaryDAO = new SecretaryStudentDAO();

    public SecretaryDashboardPanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("Secretary Dashboard");
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

        cardTotal = DashboardCard("Total Students", "0", new Color(255, 140, 0));
        add(cardTotal);

        cardPresent = DashboardCard("Present Today", "0", new Color(40, 167, 69));
        add(cardPresent);

        cardAbsent = DashboardCard("Absent Today", "0", new Color(220, 53, 69));
        add(cardAbsent);

        cardExcused = DashboardCard("Excused", "0", new Color(255, 193, 7));
        add(cardExcused);

        lblStats = new JLabel("Quick Actions");
        lblStats.setBounds(40, 220, 300, 30);
        lblStats.setFont(new Font("Arial", Font.PLAIN, 18));
        lblStats.setForeground(new Color(60, 60, 60));
        add(lblStats);
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

        int present = 0, absent = 0, excused = 0;
        for(ClassSession session : todaysSessions){
            Map<String, Integer> stats = reportService.getDailyAttendanceStats(session.sessionId());
            present += stats.getOrDefault("PRESENT", 0);
            absent += stats.getOrDefault("ABSENT", 0);
            excused += stats.getOrDefault("EXCUSED", 0);
        }

        updateCardValue(1, String.valueOf(present));
        updateCardValue(2, String.valueOf(absent));
        updateCardValue(3, String.valueOf(excused));
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

        JLabel lblTitle = new JLabel(title);
        lblTitle.setBounds(20, 15, 180, 20);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 13));
        lblTitle.setForeground(new Color(100, 100, 100));
        card.add(lblTitle);

        JLabel lblValue = new JLabel(value);
        lblValue.setBounds(20, 45, 180, 35);
        lblValue.setFont(new Font("Arial", Font.BOLD, 28));
        lblValue.setForeground(new Color(60, 60, 60));
        card.add(lblValue);

        return card;
    }

    @Override
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x, y, width, height);
        if(width > 0 && height > 0){
            separator.setBounds(40, 80, width - 80, height - 160);
        }

        if(width > 0 && cardTotal != null && cardPresent != null && cardAbsent != null && cardExcused != null){
            int leftMargin = 40;
            int rightMargin = 40;
            int gap = 20;
            int cardY = 100;
            int cardH = 100;

            int availableWidth = width - leftMargin - rightMargin;
            int totalGap = gap * 3;
            int cardW = (availableWidth - totalGap) / 4;

            cardTotal.setBounds(leftMargin, cardY, cardW, cardH);
            cardPresent.setBounds(leftMargin + cardW + gap, cardY, cardW, cardH);
            cardAbsent.setBounds(leftMargin + (cardW + gap) * 2, cardY, cardW, cardH);
            cardExcused.setBounds(leftMargin + (cardW + gap) * 3, cardY, cardW, cardH);
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
