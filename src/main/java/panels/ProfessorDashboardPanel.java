package panels;

import core.Professor;
import java.awt.Color;
import java.awt.Font;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
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
import services.ProfessorSectionService;

public class ProfessorDashboardPanel extends JPanel {

    private JSeparator separator;
    private JLabel lblTitle, lblSubTitle;
    private JPanel cardSections, cardSessions, cardPending, cardFlags;

    private Professor professor;
    private final ProfessorSectionService sectionService = new ProfessorSectionService();
    private final ClassSessionService sessionService = new ClassSessionService();
    private final AttendanceReportService reportService = new AttendanceReportService();
    private final ExcuseLetterService excuseService = new ExcuseLetterService();
    private final MissedQuizFlagService flagService = new MissedQuizFlagService();

    public ProfessorDashboardPanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("Professor Dashboard");
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

        cardSections = DashboardCard("My Sections", "0", new Color(255, 140, 0));
        add(cardSections);

        cardSessions = DashboardCard("Sessions Today", "0", new Color(40, 167, 69));
        add(cardSessions);

        cardPending = DashboardCard("Pending Excuses", "0", new Color(220, 53, 69));
        add(cardPending);

        cardFlags = DashboardCard("Missed Quiz Flags", "0", new Color(255, 193, 7));
        add(cardFlags);
    }

    public void setProfessor(Professor professor) throws SQLException{
        this.professor = professor;
        loadStats();
    }

    private void loadStats() throws SQLException{
        if(professor == null) return;

        int sectionCount = sectionService.getSectionsByProfessor(professor.professorId()).size();
        updateCardValue(0, String.valueOf(sectionCount));

        LocalDate today = LocalDate.now();
        List<ClassSession> allSessions = sessionService.getClassSessionsByProfessor(professor.professorId());
        long sessionsToday = allSessions.stream().filter(s -> s.sessionDate().equals(today)).count();
        updateCardValue(1, String.valueOf(sessionsToday));

        long pendingExcuses = excuseService.getAll().stream()
            .filter(e -> e.status() == ExcuseStatus.PENDING)
            .filter(e -> e.course().courseId() == professor.professorId())
            .count();
        updateCardValue(2, String.valueOf(pendingExcuses));

        long pendingFlags = flagService.getAllMissedQuizFlags().stream()
            .filter(f -> f.missedQuizStatus() == MissedQuizStatus.PENDING)
            .filter(f -> f.quiz().course().courseId() == professor.professorId())
            .count();
        updateCardValue(3, String.valueOf(pendingFlags));
    }

    private JPanel DashboardCard(String title, String value, Color accentColor){
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        JLabel cardAccent = new JLabel("");
        cardAccent.setBounds(0, 0, 4, 100);
        cardAccent.setBackground(accentColor);
        cardAccent.setOpaque(true);
        card.add(cardAccent);

        JLabel cardTitle = new JLabel(title);
        cardTitle.setBounds(20, 15, 180, 20);
        cardTitle.setFont(new Font("Arial", Font.PLAIN, 13));
        cardTitle.setForeground(new Color(100, 100, 100));
        card.add(cardTitle);

        JLabel cardValue = new JLabel(value);
        cardValue.setBounds(20, 45, 180, 35);
        cardValue.setFont(new Font("Arial", Font.BOLD, 28));
        cardValue.setForeground(new Color(60, 60, 60));
        card.add(cardValue);

        return card;
    }

    @Override
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x, y, width, height);
        if(width > 0 && height > 0){
            separator.setBounds(40, 80, width - 80, height - 160);
        }

        if(width > 0 && cardSections != null && cardSessions != null && cardPending != null && cardFlags != null){
            int leftMargin = 40;
            int rightMargin = 40;
            int gap = 20;
            int cardY = 100;
            int cardH = 100;

            int availableWidth = width - leftMargin - rightMargin;
            int totalGap = gap * 3;
            int cardW = (availableWidth - totalGap) / 4;

            cardSections.setBounds(leftMargin, cardY, cardW, cardH);
            cardSessions.setBounds(leftMargin + cardW + gap, cardY, cardW, cardH);
            cardPending.setBounds(leftMargin + (cardW + gap) * 2, cardY, cardW, cardH);
            cardFlags.setBounds(leftMargin + (cardW + gap) * 3, cardY, cardW, cardH);
        }
    }

    public void updateCardValue(int cardIndex, String value){
        JPanel targetCard = switch(cardIndex){
            case 0 -> cardSections;
            case 1 -> cardSessions;
            case 2 -> cardPending;
            case 3 -> cardFlags;
            default -> null;
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