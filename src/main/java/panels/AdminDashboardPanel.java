package panels;

import charts.BarChartPanel;
import charts.DonutChartPanel;
import core.User;
import java.awt.Color;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import services.CourseService;
import services.ProfessorService;
import services.SecretaryService;
import services.SectionService;
import services.StudentService;
import services.UserService;

public class AdminDashboardPanel extends JPanel {

    private JSeparator separator;
    private JLabel lblTitle, lblSubTitle;
    private JPanel cardUsers, cardStudents, cardProfessors, cardSecretaries, cardCourses, cardSections;

    private BarChartPanel barChart;
    private DonutChartPanel donutChart;
    private JLabel lblBarTitle, lblDonutTitle;
    private int cntStudents, cntProfessors, cntSecretaries, cntCourses, cntSections;

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
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle);

        lblSubTitle = new JLabel("System overview and management controls");
        lblSubTitle.setBounds(40, 50, 400, 20);
        lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubTitle.setForeground(new Color(140, 140, 140));
        add(lblSubTitle);

        separator = new JSeparator();
        separator.setForeground(new Color(230, 230, 230));
        add(separator);

        cardUsers = DashboardCard("Total Users", "0", new Color(255, 140, 0));
        cardStudents = DashboardCard("Students", "0", new Color(40, 167, 69));
        cardProfessors = DashboardCard("Professors", "0", new Color(220, 53, 69));
        cardSecretaries = DashboardCard("Secretaries", "0", new Color(255, 193, 7));
        cardCourses = DashboardCard("Courses", "0", new Color(23, 162, 184));
        cardSections = DashboardCard("Sections", "0", new Color(108, 117, 125));
        add(cardUsers);
        add(cardStudents);
        add(cardProfessors);
        add(cardSecretaries);
        add(cardCourses);
        add(cardSections);

        barChart = new BarChartPanel();
        barChart.setTitle("User Distribution");
        add(barChart);

        donutChart = new DonutChartPanel();
        donutChart.setTitle("Role Breakdown");
        add(donutChart);

        loadStats();
    }

    public void setCurrentUser(User user){
        this.currentUser = user;
        loadStats();
    }

    private void loadStats(){
        int totalUsers = userService.getAllUsers().size();
        cntStudents = studentService.getAllStudents().size();
        cntProfessors = professorService.getAllProfessors().size();
        cntSecretaries = secretaryService.getAllSecretaries().size();
        cntCourses = courseService.getAllCourses().size();
        cntSections = sectionService.getAllSections().size();

        updateCardValue(0, String.valueOf(totalUsers));
        updateCardValue(1, String.valueOf(cntStudents));
        updateCardValue(2, String.valueOf(cntProfessors));
        updateCardValue(3, String.valueOf(cntSecretaries));
        updateCardValue(4, String.valueOf(cntCourses));
        updateCardValue(5, String.valueOf(cntSections));

        refreshCharts();
    }

    private void refreshCharts(){
        Map<String, Integer> barData = new LinkedHashMap<>();
        Map<String, Color> barColors = new LinkedHashMap<>();
        barData.put("Students", cntStudents);
        barColors.put("Students", new Color(40, 167, 69));
        barData.put("Professors", cntProfessors);
        barColors.put("Professors", new Color(220, 53, 69));
        barData.put("Secretaries", cntSecretaries);
        barColors.put("Secretaries", new Color(255, 193, 7));
        barData.put("Courses", cntCourses);
        barColors.put("Courses", new Color(23, 162, 184));
        barData.put("Sections", cntSections);
        barColors.put("Sections", new Color(108, 117, 125));
        barChart.setData(barData, barColors);

        Map<String, Integer> donutData = new LinkedHashMap<>();
        Map<String, Color> donutColors = new LinkedHashMap<>();
        donutData.put("Students", cntStudents);
        donutColors.put("Students", new Color(40, 167, 69));
        donutData.put("Professors", cntProfessors);
        donutColors.put("Professors", new Color(220, 53, 69));
        donutData.put("Secretaries", cntSecretaries);
        donutColors.put("Secretaries", new Color(255, 193, 7));
        int roleTotal = cntStudents + cntProfessors + cntSecretaries;
        donutChart.setData(donutData, donutColors);
        donutChart.setCenterLabel(String.valueOf(roleTotal));
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

        if(width > 0 && height > 0 && separator != null){
            separator.setBounds(40, 78, width - 80, 1);
        }

        if(width > 0 && cardUsers != null){
            int leftM = 40;
            int rightM = 40;
            int gap = 16;
            int cardY = 90;
            int cardH = 100;

            int availW = width - leftM - rightM;
            int totalGap = gap * 5;
            int cardW = (availW - totalGap) / 6;

            cardUsers.setBounds(leftM, cardY, cardW, cardH);
            cardStudents.setBounds(leftM + (cardW + gap), cardY, cardW, cardH);
            cardProfessors.setBounds(leftM + (cardW + gap) * 2, cardY, cardW, cardH);
            cardSecretaries.setBounds(leftM + (cardW + gap) * 3, cardY, cardW, cardH);
            cardCourses.setBounds(leftM + (cardW + gap) * 4, cardY, cardW, cardH);
            cardSections.setBounds(leftM + (cardW + gap) * 5, cardY, cardW, cardH);
        }

        if(width > 0 && barChart != null && donutChart != null){
            int chartY = 210;
            int chartH = height - chartY - 20;
            if(chartH < 80){
                chartH = 80;
            }
            int leftM = 40;
            int gap = 20;
            int halfW = (width - leftM * 2 - gap) / 2;

            barChart.setBounds(leftM, chartY, halfW, chartH);
            donutChart.setBounds(leftM + halfW + gap, chartY, halfW, chartH);
        }
    }

    public void updateCardValue(int cardIndex, String value){
        JPanel targetCard = switch(cardIndex){
            case 0 ->
                cardUsers;
            case 1 ->
                cardStudents;
            case 2 ->
                cardProfessors;
            case 3 ->
                cardSecretaries;
            case 4 ->
                cardCourses;
            case 5 ->
                cardSections;
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
