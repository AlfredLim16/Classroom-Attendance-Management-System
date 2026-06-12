package panels;

import core.Semester;
import frames.AdminFrame;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import services.SemesterService;

public class AdminSettingsPanel extends JPanel implements ActionListener {

    private JLabel lblTitle, lblSubTitle;
    private JSeparator separator;
    private JLabel lblSemester, lblStartDate, lblEndDate;
    private JComboBox<String> cmbSemester;
    private JTextField txtStartDate, txtEndDate;
    private JButton btnSave, btnLogout;

    private final SemesterService semesterService = new SemesterService();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final AdminFrame frame;

    public AdminSettingsPanel(AdminFrame frame){
        this.frame = frame;
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("System Settings");
        lblTitle.setBounds(40, 20, 300, 30);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle);

        lblSubTitle = new JLabel("Update semester date ranges");
        lblSubTitle.setBounds(40, 50, 500, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblSubTitle);

        separator = new JSeparator();
        separator.setForeground(new Color(220, 220, 220));
        add(separator);

        lblSemester = new JLabel("Semester");
        lblSemester.setBounds(40, 100, 120, 25);
        lblSemester.setFont(new Font("Arial", Font.BOLD, 13));
        lblSemester.setForeground(new Color(100, 100, 100));
        add(lblSemester);

        List<Semester> semesters = semesterService.getAllSemesters();

        cmbSemester = new JComboBox<>();
        for(Semester s : semesters){
            cmbSemester.addItem(s.semesterName() + " " + s.schoolYear());
        }
        cmbSemester.setBounds(40, 128, 250, 36);
        cmbSemester.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbSemester.setBackground(Color.WHITE);
        cmbSemester.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbSemester.setFocusable(false);
        add(cmbSemester);

        if(!semesters.isEmpty()){
            Semester first = semesters.get(0);
            txtStartDate = new JTextField(first.startDate().format(DATE_FORMAT));
            txtEndDate   = new JTextField(first.endDate().format(DATE_FORMAT));
        } else {
            txtStartDate = new JTextField("2025-08-15");
            txtEndDate   = new JTextField("2025-12-20");
        }

        lblStartDate = new JLabel("Start Date");
        lblStartDate.setBounds(320, 100, 100, 25);
        lblStartDate.setFont(new Font("Arial", Font.BOLD, 13));
        lblStartDate.setForeground(new Color(100, 100, 100));
        add(lblStartDate);

        txtStartDate.setBounds(320, 128, 150, 36);
        txtStartDate.setFont(new Font("Arial", Font.PLAIN, 14));
        txtStartDate.setForeground(new Color(60, 60, 60));
        txtStartDate.setBackground(Color.WHITE);
        txtStartDate.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        add(txtStartDate);

        lblEndDate = new JLabel("End Date");
        lblEndDate.setBounds(490, 100, 100, 25);
        lblEndDate.setFont(new Font("Arial", Font.BOLD, 13));
        lblEndDate.setForeground(new Color(100, 100, 100));
        add(lblEndDate);

        txtEndDate.setBounds(490, 128, 150, 36);
        txtEndDate.setFont(new Font("Arial", Font.PLAIN, 14));
        txtEndDate.setForeground(new Color(60, 60, 60));
        txtEndDate.setBackground(Color.WHITE);
        txtEndDate.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        add(txtEndDate);

        cmbSemester.addActionListener(ev -> {
            int idx = cmbSemester.getSelectedIndex();
            if(idx >= 0 && idx < semesters.size()){
                Semester sel = semesters.get(idx);
                txtStartDate.setText(sel.startDate().format(DATE_FORMAT));
                txtEndDate.setText(sel.endDate().format(DATE_FORMAT));
            }
        });

        btnSave = new JButton("Save Settings");
        btnSave.setBounds(40, 200, 140, 36);
        btnSave.setFont(new Font("Arial", Font.PLAIN, 14));
        btnSave.setForeground(Color.WHITE);
        btnSave.setBackground(new Color(255, 140, 0));
        btnSave.setBorder(null);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(this);
        add(btnSave);

        btnLogout = new JButton("Logout");
        btnLogout.setBounds(40, 260, 140, 36);
        btnLogout.setFont(new Font("Arial", Font.PLAIN, 14));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setBackground(new Color(220, 53, 69));
        btnLogout.setBorder(null);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(this);
        add(btnLogout);
    }

    @Override
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x, y, width, height);
        if(width > 0 && height > 0 && separator != null){
            separator.setBounds(40, 80, width - 80, 1);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnSave){
            try{
                LocalDate startDate = LocalDate.parse(txtStartDate.getText().trim(), DATE_FORMAT);
                LocalDate endDate   = LocalDate.parse(txtEndDate.getText().trim(), DATE_FORMAT);

                if(startDate.isAfter(endDate)){
                    JOptionPane.showMessageDialog(this, "Start date must be before end date.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                List<Semester> semesters = semesterService.getAllSemesters();
                int semIdx = cmbSemester.getSelectedIndex();
                if(semIdx < 0 || semIdx >= semesters.size()){
                    JOptionPane.showMessageDialog(this, "Please select a semester.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Semester old     = semesters.get(semIdx);
                Semester updated = new Semester(old.semesterId(), old.semesterName(), old.schoolYear(), startDate, endDate);
                boolean success  = semesterService.updateSemester(updated);

                if(success){
                    JOptionPane.showMessageDialog(this,
                        "Saved: " + cmbSemester.getSelectedItem() + "\nStart: " + startDate + "  End: " + endDate,
                        "Settings Saved", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save semester settings.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch(DateTimeParseException ex){
                JOptionPane.showMessageDialog(this, "Invalid date format. Use yyyy-MM-dd.", "Validation", JOptionPane.WARNING_MESSAGE);
            }
        } else if(e.getSource() == btnLogout){
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION){
                frame.doLogout();
            }
        }
    }
}
