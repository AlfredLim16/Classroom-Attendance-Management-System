package application;

import course.CourseService;
import course.SectionService;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import user.ProfessorService;
import user.SecretaryService;
import user.StudentService;
import user.User;
import user.UserService;

public class AdminDashboardPanel extends JPanel {

    private JSeparator separator;
    private JLabel lblTitle, lblSubTitle;
    private JPanel cardUsers, cardStudents, cardProfessors, cardSecretaries, cardCourses, cardSections;

    private User currentUser;
    private final UserService userService = new UserService();
    private final StudentService studentService = new StudentService();
    private final ProfessorService professorService = new ProfessorService();
    private final SecretaryService secretaryService = new SecretaryService();
    private final CourseService courseService = new CourseService();
    private final SectionService sectionService = new SectionService();

    public AdminDashboardPanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("Admin Dashboard");
        lblTitle.setBounds(40, 20, 300, 30);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle);

        lblSubTitle = new JLabel("System overview and management controls");
        lblSubTitle.setBounds(40, 50, 400, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblSubTitle);

        separator = new JSeparator();
        separator.setForeground(Color.BLACK);
        add(separator);

        cardUsers = DashboardCard("Total Users", "0", new Color(255, 140, 0));
        add(cardUsers);

        cardStudents = DashboardCard("Students", "0", new Color(40, 167, 69));
        add(cardStudents);

        cardProfessors = DashboardCard("Professors", "0", new Color(220, 53, 69));
        add(cardProfessors);

        cardSecretaries = DashboardCard("Secretaries", "0", new Color(255, 193, 7));
        add(cardSecretaries);

        cardCourses = DashboardCard("Courses", "0", new Color(23, 162, 184));
        add(cardCourses);

        cardSections = DashboardCard("Sections", "0", new Color(108, 117, 125));
        add(cardSections);
    }

    public void setCurrentUser(User user){
        this.currentUser = user;
        loadStats();
    }

    private void loadStats(){
        int totalUsers = userService.getAllUsers().size();
        int students = studentService.getAllStudents().size();
        int professors = professorService.getAllProfessors().size();
        int secretaries = secretaryService.getAllSecretaries().size();
        int courses = courseService.getAllCourses().size();
        int sections = sectionService.getAllSections().size();

        updateCardValue(0, String.valueOf(totalUsers));
        updateCardValue(1, String.valueOf(students));
        updateCardValue(2, String.valueOf(professors));
        updateCardValue(3, String.valueOf(secretaries));
        updateCardValue(4, String.valueOf(courses));
        updateCardValue(5, String.valueOf(sections));
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

        if(width > 0 && cardUsers != null && cardStudents != null && cardProfessors != null
                && cardSecretaries != null && cardCourses != null && cardSections != null){
            int leftMargin = 40;
            int rightMargin = 40;
            int gap = 20;
            int cardY = 100;
            int cardH = 100;

            int availableWidth = width - leftMargin - rightMargin;
            int totalGap = gap * 5;
            int cardW = (availableWidth - totalGap) / 6;

            cardUsers.setBounds(leftMargin, cardY, cardW, cardH);
            cardStudents.setBounds(leftMargin + cardW + gap, cardY, cardW, cardH);
            cardProfessors.setBounds(leftMargin + (cardW + gap) * 2, cardY, cardW, cardH);
            cardSecretaries.setBounds(leftMargin + (cardW + gap) * 3, cardY, cardW, cardH);
            cardCourses.setBounds(leftMargin + (cardW + gap) * 4, cardY, cardW, cardH);
            cardSections.setBounds(leftMargin + (cardW + gap) * 5, cardY, cardW, cardH);
        }
    }

    public void updateCardValue(int cardIndex, String value){
        JPanel targetCard = switch(cardIndex){
            case 0 -> cardUsers;
            case 1 -> cardStudents;
            case 2 -> cardProfessors;
            case 3 -> cardSecretaries;
            case 4 -> cardCourses;
            case 5 -> cardSections;
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