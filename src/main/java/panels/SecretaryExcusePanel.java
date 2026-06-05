package panels;

import core.Section;
import core.Student;
import dao.SecretaryStudentDAO;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
import services.ExcuseLetterServiceAdapter;

public class SecretaryExcusePanel extends JPanel implements ActionListener {

    private JLabel lblTitle, lblSubTitle;
    private JSeparator separator;
    private JButton btnView, btnForwardToProfessor;
    private JTable excuseTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private Section section;
    private final ExcuseLetterServiceAdapter excuseService = new ExcuseLetterServiceAdapter();
    private final SecretaryStudentDAO secretaryDAO = new SecretaryStudentDAO();
    private final List<ExcuseLetter> rowExcuseLetters = new ArrayList<>();

    public SecretaryExcusePanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("Excuse Letters (View Only)");
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

        btnView = new JButton("View Details");
        btnView.setBounds(40, 100, 120, 36);
        btnView.setFont(new Font("Arial", Font.PLAIN, 14));
        btnView.setForeground(Color.WHITE);
        btnView.setBackground(new Color(255, 140, 0));
        btnView.setBorder(null);
        btnView.setFocusPainted(false);
        btnView.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnView.addActionListener(this);
        add(btnView);

        btnForwardToProfessor = new JButton("Forward to Professor");
        btnForwardToProfessor.setBounds(170, 100, 160, 36);
        btnForwardToProfessor.setFont(new Font("Arial", Font.PLAIN, 14));
        btnForwardToProfessor.setForeground(new Color(100, 100, 100));
        btnForwardToProfessor.setBackground(Color.WHITE);
        btnForwardToProfessor.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btnForwardToProfessor.setFocusPainted(false);
        btnForwardToProfessor.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnForwardToProfessor.addActionListener(this);
        add(btnForwardToProfessor);

        String[] columns = {"Date", "Student", "Course", "Reason", "Status", "Submitted"};
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

        scrollPane = new JScrollPane(excuseTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane);
    }

    public void setSection(Section section){
        this.section = section;
        loadExcuses();
    }

    private void loadExcuses(){
        if(section == null){
            return;
        }

        List<Student> students = secretaryDAO.findStudentsBySectionId(section.sectionId());
        Set<Integer> studentIds = students.stream().map(Student::studentId).collect(Collectors.toSet());

        List<ExcuseLetter> allExcuses = excuseService.getAllExcuseLetters();
        tableModel.setRowCount(0);
        rowExcuseLetters.clear();

        for(ExcuseLetter letter : allExcuses){
            if(studentIds.contains(letter.student().studentId())){
                rowExcuseLetters.add(letter);
                addExcuseRow(
                    letter.absentDate().toString(),
                    letter.student().firstName() + " " + letter.student().lastName(),
                    letter.course().courseCode(),
                    letter.reason(),
                    letter.status().toString(),
                    letter.submittedDate() != null ? letter.submittedDate().toString() : "-"
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

        int y = 72;
        int gap = 34;

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

    private void forwardToProfessor(){
        ExcuseLetter letter = getSelectedLetter();
        if(letter == null){
            JOptionPane.showMessageDialog(this, "Please select an excuse letter from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = letter.status().toString();
        if(status.equalsIgnoreCase("Approved") || status.equalsIgnoreCase("Rejected")){
            JOptionPane.showMessageDialog(this,
                "This excuse letter has already been reviewed.\nCurrent status: " + status,
                "Already Reviewed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            """
            Forward this excuse letter to the professor for review?
            Student: """ + letter.student().firstName() + " " + letter.student().lastName() + "\n"
            + "Course:  " + letter.course().courseCode() + "\n"
            + "Date:    " + letter.absentDate(),
            "Confirm Forward", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if(confirm == JOptionPane.YES_OPTION){
            JOptionPane.showMessageDialog(this,
                "Excuse letter forwarded successfully to the professor.",
                "Forwarded", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x, y, width, height);
        if(width > 0 && height > 0 && scrollPane != null){
            separator.setBounds(40, 80, width - 80, height - 160);
            scrollPane.setBounds(40, 150, width - 80, height - 180);
        }
    }

    public void addExcuseRow(String date, String student, String course, String reason, String status, String submitted){
        tableModel.addRow(new Object[]{date, student, course, reason, status, submitted});
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnView){
            ExcuseLetter letter = getSelectedLetter();
            if(letter == null){
                JOptionPane.showMessageDialog(this, "Please select an excuse letter from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showDetailDialog(letter);
        }
        if(e.getSource() == btnForwardToProfessor){
            forwardToProfessor();
        }
    }
}
