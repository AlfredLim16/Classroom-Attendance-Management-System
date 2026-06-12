package panels;

import core.Professor;
import core.User;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import junction.MissedQuizFlag;
import lookup.DecisionType;
import services.MissedQuizFlagService;
import services.ProfessorCourseService;

public class ProfessorMissedQuizPanel extends JPanel implements ActionListener {

    private JLabel lblTitle, lblSubTitle;
    private JSeparator separator;
    private JComboBox<String> cmbDecision;
    private JButton btnDecide, btnViewDetails;
    private JTable flagTable;
    private DefaultTableModel tableModel;
    private JScrollPane tableScroll;

    private Professor professor;
    private User currentUser;
    private final MissedQuizFlagService flagService = new MissedQuizFlagService();
    private final ProfessorCourseService courseService = new ProfessorCourseService();
    private final List<MissedQuizFlag> rowFlags = new ArrayList<>();

    public ProfessorMissedQuizPanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("Review Missed Quiz/Lab Flags");
        lblTitle.setBounds(40, 20, 350, 30);
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

        btnViewDetails = new JButton("View Details");
        btnViewDetails.setBounds(40, 100, 120, 36);
        btnViewDetails.setFont(new Font("Arial", Font.BOLD, 12));
        btnViewDetails.setForeground(Color.WHITE);
        btnViewDetails.setBackground(new Color(255, 140, 0));
        btnViewDetails.setBorder(null);
        btnViewDetails.setFocusPainted(false);
        btnViewDetails.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnViewDetails.addActionListener(this);
        add(btnViewDetails);

        cmbDecision = new JComboBox<>(new String[]{"Select Decision", "Allow Make-up", "Zero Score", "Excused Absence"});
        cmbDecision.setBounds(180, 100, 180, 36);
        cmbDecision.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbDecision.setBackground(Color.WHITE);
        cmbDecision.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbDecision.setFocusable(false);
        cmbDecision.setSelectedItem("Select Decision");

        cmbDecision.setRenderer(new BasicComboBoxRenderer() {
            @Override
            public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus){

                JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                label.setBorder(new EmptyBorder(0, 10, 0, 0));
                return label;
            }
        });
        add(cmbDecision);

        btnDecide = new JButton("Submit Decision");
        btnDecide.setBounds(380, 100, 140, 36);
        btnDecide.setFont(new Font("Arial", Font.BOLD, 12));
        btnDecide.setForeground(Color.WHITE);
        btnDecide.setBackground(new Color(40, 167, 69));
        btnDecide.setBorder(null);
        btnDecide.setFocusPainted(false);
        btnDecide.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDecide.addActionListener(this);
        add(btnDecide);

        String[] columns = {"Date", "Student", "Quiz/Lab", "Status", "Current Decision"};
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

        tableScroll = new JScrollPane(flagTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        tableScroll.getViewport().setBackground(Color.WHITE);
        add(tableScroll);

    }

    public void setProfessor(Professor professor){
        this.professor = professor;
        loadFlags();
    }

    public void setCurrentUser(User user){
        this.currentUser = user;
    }

    private void loadFlags(){
        if(professor == null){
            return;
        }
        tableModel.setRowCount(0);
        rowFlags.clear();

        var myCourses = courseService.getCoursesByProfessor(professor.professorId());
        var myCourseIds = myCourses.stream().map(pc -> pc.course().courseId()).toList();

        List<MissedQuizFlag> all = flagService.getAllMissedQuizFlags();
        for(MissedQuizFlag flag : all){
            if(myCourseIds.contains(flag.quiz().course().courseId())){
                rowFlags.add(flag);
                addFlagRow(
                    flag.quiz().quizDate().toString(),
                    flag.student().firstName() + " " + flag.student().lastName(),
                    flag.quiz().course().courseCode(),
                    flag.missedQuizStatus().getMissedQuizStatusName(),
                    flag.decisionType() != null ? flag.decisionType().getDecisionTypeName() : "Pending"
                );
            }
        }
    }

    private MissedQuizFlag getSelectedFlag(){
        int row = flagTable.getSelectedRow();
        if(row < 0 || row >= rowFlags.size()){
            return null;
        }
        return rowFlags.get(row);
    }

    private void showDetailDialog(MissedQuizFlag flag){
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Missed Quiz Flag Details");
        dialog.setModal(true);
        dialog.setSize(520, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setResizable(false);

        JLabel accent = new JLabel("");
        accent.setBounds(0, 0, 4, 420);
        accent.setBackground(new Color(255, 140, 0));
        accent.setOpaque(true);
        dialog.add(accent);

        JLabel lblHeader = new JLabel("Missed Quiz/Lab Flag Details");
        lblHeader.setBounds(30, 20, 350, 30);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));
        lblHeader.setForeground(new Color(60, 60, 60));
        dialog.add(lblHeader);

        JSeparator sep = new JSeparator();
        sep.setBounds(30, 55, 460, 1);
        sep.setForeground(new Color(220, 220, 220));
        dialog.add(sep);

        int y = 72, gap = 34;
        addDetailRow(dialog, "Student", flag.student().firstName() + " " + flag.student().lastName(), y);
        y += gap;
        addDetailRow(dialog, "Course", flag.quiz().course().courseCode() + " - " + flag.quiz().course().courseName(), y);
        y += gap;
        addDetailRow(dialog, "Quiz Date", flag.quiz().quizDate().toString(), y);
        y += gap;
        addDetailRow(dialog, "Quiz Type", flag.quiz().quizType().toString(), y);
        y += gap;
        addDetailRow(dialog, "Status", flag.missedQuizStatus().getMissedQuizStatusName(), y);
        y += gap;
        addDetailRow(dialog, "Decision", flag.decisionType() != null ? flag.decisionType().getDecisionTypeName() : "Pending", y);
        y += gap;
        addDetailRow(dialog, "Decided By", flag.decidedByProfessor() != null ? flag.decidedByProfessor().getFullName() : "-", y);
        y += gap;

        JLabel lblRemarks = new JLabel("Remarks");
        lblRemarks.setBounds(30, y, 120, 25);
        lblRemarks.setFont(new Font("Arial", Font.BOLD, 13));
        lblRemarks.setForeground(new Color(100, 100, 100));
        dialog.add(lblRemarks);

        JTextArea txtRemarks = new JTextArea(flag.remarks() != null ? flag.remarks() : "-");
        txtRemarks.setBounds(150, y, 340, 60);
        txtRemarks.setFont(new Font("Arial", Font.PLAIN, 13));
        txtRemarks.setForeground(new Color(60, 60, 60));
        txtRemarks.setBackground(new Color(250, 250, 250));
        txtRemarks.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        txtRemarks.setLineWrap(true);
        txtRemarks.setWrapStyleWord(true);
        txtRemarks.setEditable(false);
        dialog.add(txtRemarks);

        JButton btnClose = new JButton("Close");
        btnClose.setBounds(370, 390, 120, 36);
        btnClose.setFont(new Font("Arial", Font.PLAIN, 14));
        btnClose.setForeground(Color.WHITE);
        btnClose.setBackground(new Color(255, 140, 0));
        btnClose.setBorder(null);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dialog.dispose());
        dialog.add(btnClose);

        dialog.setVisible(true);
    }

    private void addDetailRow(JDialog dialog, String label, String value, int y){
        JLabel lbl = new JLabel(label);
        lbl.setBounds(30, y, 120, 25);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(new Color(100, 100, 100));
        dialog.add(lbl);

        JLabel val = new JLabel(value);
        val.setBounds(150, y, 340, 25);
        val.setFont(new Font("Arial", Font.PLAIN, 13));
        val.setForeground(new Color(60, 60, 60));
        dialog.add(val);
    }

    private void submitDecision(){
        if(currentUser == null || professor == null){
            return;
        }
        MissedQuizFlag flag = getSelectedFlag();
        if(flag == null){
            JOptionPane.showMessageDialog(this, "Please select a flag from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int decisionIndex = cmbDecision.getSelectedIndex();
        if(decisionIndex <= 0){
            JOptionPane.showMessageDialog(this, "Please select a decision.", "No Decision", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DecisionType decisionType = switch(decisionIndex){
            case 1 -> DecisionType.MAKEUP;
            case 2 -> DecisionType.ZERO;
            case 3 -> DecisionType.EXCUSED_ABSENCE;
            default -> null;
        };

        if(decisionType == null){
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Submit decision: " + cmbDecision.getSelectedItem() + "\n\n"
            + "Student: " + flag.student().firstName() + " " + flag.student().lastName() + "\n"
            + "Quiz:    " + flag.quiz().course().courseCode(),
            "Confirm Decision", JOptionPane.YES_NO_OPTION);

        if(confirm == JOptionPane.YES_OPTION){
            boolean success = flagService.processMissedQuizFlag(
                flag.flagId(), decisionType, flag.remarks(), professor);
            if(success){
                JOptionPane.showMessageDialog(this, "Decision submitted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadFlags();
            }else{
                JOptionPane.showMessageDialog(this, "Failed to submit decision.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x, y, width, height);
        if(width > 0 && height > 0 && tableScroll != null){
            separator.setBounds(40, 80, width - 80, height - 160);
            tableScroll.setBounds(40, 150, width - 80, height - 180);
        }
    }

    public void addFlagRow(String date, String student, String quiz, String status, String currentDecision){
        tableModel.addRow(new Object[]{date, student, quiz, status, currentDecision});
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnViewDetails){
            MissedQuizFlag flag = getSelectedFlag();
            if(flag == null){
                JOptionPane.showMessageDialog(this, "Please select a flag from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showDetailDialog(flag);
        }
        if(e.getSource() == btnDecide){
            submitDecision();
        }
    }
}
