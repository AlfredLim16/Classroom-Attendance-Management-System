package panels;

import core.Professor;
import core.User;
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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import junction.ExcuseLetter;
import lookup.ExcuseStatus;
import services.ExcuseLetterService;
import services.ProfessorCourseService;

public class ProfessorExcusePanel extends JPanel implements ActionListener {

    private JLabel lblTitle, lblSubTitle;
    private JSeparator separator;
    private JButton btnApprove, btnReject, btnViewDetails;
    private JTable excuseTable;
    private DefaultTableModel tableModel;
    private JScrollPane tableScroll;

    private Professor professor;
    private User currentUser;
    private final ExcuseLetterService excuseService = new ExcuseLetterService();
    private final ProfessorCourseService courseService = new ProfessorCourseService();
    private final List<ExcuseLetter> rowExcuseLetters = new ArrayList<>();

    public ProfessorExcusePanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("Review Excuse Letters");
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

        btnApprove = new JButton("Approve");
        btnApprove.setBounds(170, 100, 100, 36);
        btnApprove.setFont(new Font("Arial", Font.BOLD, 12));
        btnApprove.setForeground(Color.WHITE);
        btnApprove.setBackground(new Color(40, 167, 69));
        btnApprove.setBorder(null);
        btnApprove.setFocusPainted(false);
        btnApprove.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnApprove.addActionListener(this);
        add(btnApprove);

        btnReject = new JButton("Reject");
        btnReject.setBounds(280, 100, 100, 36);
        btnReject.setFont(new Font("Arial", Font.BOLD, 12));
        btnReject.setForeground(Color.WHITE);
        btnReject.setBackground(new Color(220, 53, 69));
        btnReject.setBorder(null);
        btnReject.setFocusPainted(false);
        btnReject.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReject.addActionListener(this);
        add(btnReject);

        String[] columns = {"Date", "Student", "Course", "Absent Date", "Reason", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        excuseTable = new JTable(tableModel);
        excuseTable.setRowHeight(28);
        excuseTable.setFillsViewportHeight(true);
        excuseTable.getTableHeader().setReorderingAllowed(false);
        excuseTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        excuseTable.getTableHeader().setBackground(new Color(255, 255, 255));
        excuseTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        excuseTable.getTableHeader().setPreferredSize(new Dimension(excuseTable.getPreferredSize().width, 28));

        DefaultTableCellRenderer centerCollumn = new DefaultTableCellRenderer();
        centerCollumn.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < excuseTable.getColumnCount(); i++){
            excuseTable.getColumnModel().getColumn(i).setCellRenderer(centerCollumn);
        }

        tableScroll = new JScrollPane(excuseTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        tableScroll.getViewport().setBackground(Color.WHITE);
        add(tableScroll);

    }

    public void setProfessor(Professor professor) throws SQLException{
        this.professor = professor;
        loadExcuses();
    }

    public void setCurrentUser(User user){
        this.currentUser = user;
    }

    private void loadExcuses() throws SQLException{
        if(professor == null){
            return;
        }
        tableModel.setRowCount(0);
        rowExcuseLetters.clear();

        var myCourses = courseService.getCoursesByProfessor(professor.professorId());
        var myCourseIds = myCourses.stream().map(pc -> pc.course().courseId()).toList();

        List<ExcuseLetter> all = excuseService.getAll();
        for(ExcuseLetter letter : all){
            if(myCourseIds.contains(letter.course().courseId())){
                rowExcuseLetters.add(letter);
                addExcuseRow(
                    letter.submittedDate() != null ? letter.submittedDate().toLocalDate().toString() : "-",
                    letter.student().firstName() + " " + letter.student().lastName(),
                    letter.course().courseCode(),
                    letter.absentDate().toString(),
                    letter.reason(),
                    letter.status().toString()
                );
            }
        }
    }

    private ExcuseLetter getSelectedLetter(){
        int row = excuseTable.getSelectedRow();
        if(row < 0 || row >= rowExcuseLetters.size()){
            return null;
        }
        return rowExcuseLetters.get(row);
    }

    private void showDetailDialog(ExcuseLetter letter){
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Excuse Letter Details");
        dialog.setModal(true);
        dialog.setSize(520, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setResizable(false);

        JLabel accent = new JLabel("");
        accent.setBounds(0, 0, 4, 550);
        accent.setBackground(new Color(255, 140, 0));
        accent.setOpaque(true);
        dialog.add(accent);

        JLabel lblHeader = new JLabel("Excuse Letter Details");
        lblHeader.setBounds(30, 20, 300, 30);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));
        lblHeader.setForeground(new Color(60, 60, 60));
        dialog.add(lblHeader);

        JSeparator sep = new JSeparator();
        sep.setBounds(30, 55, 460, 1);
        sep.setForeground(new Color(220, 220, 220));
        dialog.add(sep);

        int y = 72, gap = 34;
        addDetailRow(dialog, "Student", letter.student().firstName() + " " + letter.student().lastName(), y);
        y += gap;
        addDetailRow(dialog, "Course", letter.course().courseCode() + " - " + letter.course().courseName(), y);
        y += gap;
        addDetailRow(dialog, "Absent Date", letter.absentDate().toString(), y);
        y += gap;
        addDetailRow(dialog, "Status", letter.status().toString(), y);
        y += gap;
        addDetailRow(dialog, "Submitted", letter.submittedDate() != null ? letter.submittedDate().toString() : "-", y);
        y += gap;
        addDetailRow(dialog, "Reviewed By", letter.reviewedBy() != null ? letter.reviewedBy().userName() : "-", y);
        y += gap;
        addDetailRow(dialog, "Reviewed Date", letter.reviewedDate() != null ? letter.reviewedDate().toString() : "-", y);
        y += gap;

        JLabel lblReason = new JLabel("Reason");
        lblReason.setBounds(30, y, 120, 25);
        lblReason.setFont(new Font("Arial", Font.BOLD, 13));
        lblReason.setForeground(new Color(100, 100, 100));
        dialog.add(lblReason);

        JTextArea txtReason = new JTextArea(letter.reason());
        txtReason.setBounds(150, y, 340, 70);
        txtReason.setFont(new Font("Arial", Font.PLAIN, 13));
        txtReason.setForeground(new Color(60, 60, 60));
        txtReason.setBackground(new Color(250, 250, 250));
        txtReason.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        txtReason.setLineWrap(true);
        txtReason.setWrapStyleWord(true);
        txtReason.setEditable(false);
        dialog.add(txtReason);

        y += 85;

        JLabel lblDoc = new JLabel("Document");
        lblDoc.setBounds(30, y, 120, 25);
        lblDoc.setFont(new Font("Arial", Font.BOLD, 13));
        lblDoc.setForeground(new Color(100, 100, 100));
        dialog.add(lblDoc);

        String docPath = letter.supportingDocumentPath() != null ? letter.supportingDocumentPath() : "-";
        JTextArea txtDoc = new JTextArea(docPath);
        txtDoc.setBounds(150, y, 340, 40);
        txtDoc.setFont(new Font("Arial", Font.PLAIN, 13));
        txtDoc.setForeground(new Color(60, 60, 60));
        txtDoc.setBackground(new Color(250, 250, 250));
        txtDoc.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        txtDoc.setLineWrap(true);
        txtDoc.setWrapStyleWord(true);
        txtDoc.setEditable(false);
        dialog.add(txtDoc);

        JButton btnClose = new JButton("Close");
        btnClose.setBounds(370, 450, 120, 36);
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

    private void reviewSelected(ExcuseStatus newStatus){
        if(currentUser == null){
            return;
        }
        ExcuseLetter letter = getSelectedLetter();
        if(letter == null){
            JOptionPane.showMessageDialog(this, "Please select an excuse letter.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String currentStatus = letter.status().getExcuseStatusName();
        if(currentStatus.equalsIgnoreCase("Approved") || currentStatus.equalsIgnoreCase("Rejected")){
            JOptionPane.showMessageDialog(this, "This excuse letter has already been " + currentStatus.toLowerCase() + ".", "Already Reviewed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String action = newStatus == ExcuseStatus.APPROVED ? "approve" : "reject";
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to " + action + " this excuse letter?\n\n" + "Student: " + letter.student().firstName() + " " + letter.student().lastName() + "\n" + "Course:  " + letter.course().courseCode() + "\n" + "Date:    " + letter.absentDate(), "Confirm " + action.substring(0, 1).toUpperCase() + action.substring(1),
            JOptionPane.YES_NO_OPTION);

        if(confirm == JOptionPane.YES_OPTION){
            try{
                excuseService.reviewExcuseLetter(letter.excuseId(), newStatus, currentUser);
                JOptionPane.showMessageDialog(this, "Excuse letter " + action + "d successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadExcuses();
            }catch(Exception e){
                JOptionPane.showMessageDialog(this, "Failed to update excuse letter: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    public void addExcuseRow(String date, String student, String course, String absentDate, String reason, String status){
        tableModel.addRow(new Object[]{date, student, course, absentDate, reason, status});
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnViewDetails){
            ExcuseLetter letter = getSelectedLetter();
            if(letter == null){
                JOptionPane.showMessageDialog(this, "Please select an excuse letter.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showDetailDialog(letter);
        }
        if(e.getSource() == btnApprove){
            reviewSelected(ExcuseStatus.APPROVED);
        }
        if(e.getSource() == btnReject){
            reviewSelected(ExcuseStatus.REJECTED);
        }
    }
}
