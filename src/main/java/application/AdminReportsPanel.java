package application;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class AdminReportsPanel extends JPanel implements ActionListener {

    private JLabel lblTitle, lblSubTitle;
    private JSeparator separator;
    private JComboBox<String> cmbReportType, cmbFilter;
    private JTextField txtDateFrom, txtDateTo;
    private JButton btnGenerate, btnExport;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    public AdminReportsPanel(){
        setLayout(null);
        setBackground(Color.WHITE);

        lblTitle = new JLabel("System Reports");
        lblTitle.setBounds(40, 20, 300, 30);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle);

        lblSubTitle = new JLabel("Generate and export attendance, performance, and system reports");
        lblSubTitle.setBounds(40, 50, 600, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblSubTitle);

        separator = new JSeparator();
        separator.setForeground(Color.BLACK);
        add(separator);

        cmbReportType = new JComboBox<>(new String[]{"Select Report", "Attendance Summary", "Student Performance", "Professor Activity", "Course Analytics", "System Logs"});
        cmbReportType.setBounds(40, 100, 180, 36);
        cmbReportType.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbReportType.setBackground(Color.WHITE);
        cmbReportType.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbReportType.setFocusable(false);
        cmbReportType.setRenderer(new BasicComboBoxRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                javax.swing.JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus){
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(new EmptyBorder(0, 10, 0, 0));
                return label;
            }
        });
        add(cmbReportType);

        cmbFilter = new JComboBox<>(new String[]{"All", "By Program", "By Section", "By Course", "By Professor"});
        cmbFilter.setBounds(230, 100, 150, 36);
        cmbFilter.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbFilter.setBackground(Color.WHITE);
        cmbFilter.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cmbFilter.setFocusable(false);
        cmbFilter.setRenderer(new BasicComboBoxRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                javax.swing.JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus){
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(new EmptyBorder(0, 10, 0, 0));
                return label;
            }
        });
        add(cmbFilter);

        txtDateFrom = new JTextField("2025-08-01");
        txtDateFrom.setBounds(400, 100, 120, 36);
        txtDateFrom.setFont(new Font("Arial", Font.PLAIN, 13));
        txtDateFrom.setForeground(new Color(60, 60, 60));
        txtDateFrom.setBackground(Color.WHITE);
        txtDateFrom.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        add(txtDateFrom);

        txtDateTo = new JTextField("2026-05-30");
        txtDateTo.setBounds(530, 100, 120, 36);
        txtDateTo.setFont(new Font("Arial", Font.PLAIN, 13));
        txtDateTo.setForeground(new Color(60, 60, 60));
        txtDateTo.setBackground(Color.WHITE);
        txtDateTo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        add(txtDateTo);

        btnGenerate = new JButton("Generate");
        btnGenerate.setBounds(670, 100, 120, 36);
        btnGenerate.setFont(new Font("Arial", Font.PLAIN, 14));
        btnGenerate.setForeground(Color.WHITE);
        btnGenerate.setBackground(new Color(255, 140, 0));
        btnGenerate.setBorder(null);
        btnGenerate.setFocusPainted(false);
        btnGenerate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGenerate.addActionListener(this);
        add(btnGenerate);

        btnExport = new JButton("Export");
        btnExport.setBounds(800, 100, 100, 36);
        btnExport.setFont(new Font("Arial", Font.PLAIN, 14));
        btnExport.setForeground(new Color(100, 100, 100));
        btnExport.setBackground(Color.WHITE);
        btnExport.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btnExport.setFocusPainted(false);
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExport.addActionListener(this);
        add(btnExport);

        String[] columns = {"Metric", "Value", "Period", "Trend"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        reportTable = new JTable(tableModel);
        reportTable.setRowHeight(28);
        reportTable.setFillsViewportHeight(true);
        reportTable.getTableHeader().setReorderingAllowed(false);
        reportTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        reportTable.getTableHeader().setBackground(new Color(255, 255, 255));
        reportTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        reportTable.getTableHeader().setPreferredSize(new Dimension(reportTable.getPreferredSize().width, 28));

        DefaultTableCellRenderer centerCollumn = new DefaultTableCellRenderer();
        centerCollumn.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < reportTable.getColumnCount(); i++){
            reportTable.getColumnModel().getColumn(i).setCellRenderer(centerCollumn);
        }

        scrollPane = new JScrollPane(reportTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane);
    }

    @Override
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x, y, width, height);
        if(width > 0 && height > 0 && scrollPane != null){
            separator.setBounds(40, 80, width - 80, height - 160);
            scrollPane.setBounds(40, 150, width - 80, height - 180);
        }

        if(width > 0 && btnExport != null && btnGenerate != null){
            int rightMargin = 40;
            int gap = 15;
            int btnH = 36;
            int expW = 100;
            int genW = 120;

            int expX = width - rightMargin - expW;
            btnExport.setBounds(expX, 100, expW, btnH);

            int genX = expX - gap - genW;
            btnGenerate.setBounds(genX, 100, genW, btnH);
        }
    }

    public void addReportRow(String metric, String value, String period, String trend){
        tableModel.addRow(new Object[]{metric, value, period, trend});
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnGenerate){
            tableModel.setRowCount(0);
            addReportRow("Total Attendance Records", "1,245", "Aug 2025 - May 2026", "+12%");
            addReportRow("Average Attendance Rate", "87.3%", "Aug 2025 - May 2026", "+3.2%");
            addReportRow("Excuse Letters Approved", "45", "Aug 2025 - May 2026", "-5%");
            addReportRow("Missed Quiz Flags", "28", "Aug 2025 - May 2026", "+8%");
            addReportRow("Active Courses", "30", "Current Semester", "0%");
            addReportRow("Total Sections", "24", "Current Semester", "+2");
            JOptionPane.showMessageDialog(this, "Report generated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
        if(e.getSource() == btnExport){
            JOptionPane.showMessageDialog(this, "Export to CSV/Excel coming soon.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
