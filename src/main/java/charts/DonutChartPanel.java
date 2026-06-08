package charts;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JPanel;

public class DonutChartPanel extends JPanel {

    private Map<String, Integer> data   = new LinkedHashMap<>();
    private Map<String, Color>   colors = new LinkedHashMap<>();
    private String title = "";
    private String centerLabel = "";

    private static final float HOLE_RATIO = 0.52f;

    public DonutChartPanel(){
        setBackground(Color.WHITE);
        setBorder(javax.swing.BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
    }

    public void setData(Map<String, Integer> data, Map<String, Color> colors){
        this.data   = new LinkedHashMap<>(data);
        this.colors = new LinkedHashMap<>(colors);
        repaint();
    }

    public void setTitle(String title){
        this.title = title;
        repaint();
    }

    public void setCenterLabel(String label){
        this.centerLabel = label;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int W = getWidth();
        int H = getHeight();

        int titleH = 0;
        if(!title.isEmpty()){
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.setColor(new Color(60, 60, 60));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(title, (W - fm.stringWidth(title)) / 2, 22);
            titleH = 28;
        }

        if(data == null || data.isEmpty()){
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(new Color(180, 180, 180));
            g2.drawString("No data", W / 2 - 20, H / 2);
            return;
        }

        int total = data.values().stream().mapToInt(Integer::intValue).sum();
        if(total == 0){
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(new Color(180, 180, 180));
            g2.drawString("No data", W / 2 - 20, H / 2);
            return;
        }

        int legendW  = 110;
        int chartAreaW = W - legendW;
        int chartAreaH = H - titleH - 10;

        int diameter = Math.min(chartAreaW, chartAreaH) - 20;
        if(diameter < 10) return;

        int cx = (chartAreaW - diameter) / 2 + diameter / 2;
        int cy = titleH + 10 + (chartAreaH - diameter) / 2 + diameter / 2;

        int x0 = cx - diameter / 2;
        int y0 = cy - diameter / 2;

        double startAngle = 90.0; // start at top
        for(Map.Entry<String, Integer> entry : data.entrySet()){
            double sweep = (double) entry.getValue() / total * 360.0;
            Color c = colors.getOrDefault(entry.getKey(), Color.GRAY);

            g2.setColor(new Color(0, 0, 0, 12));
            Arc2D.Double shadow = new Arc2D.Double(x0 + 2, y0 + 2, diameter, diameter, startAngle, -sweep, Arc2D.PIE);
            g2.fill(shadow);

            g2.setColor(c);
            Arc2D.Double arc = new Arc2D.Double(x0, y0, diameter, diameter, startAngle, -sweep, Arc2D.PIE);
            g2.fill(arc);

            startAngle -= sweep;
        }

        int holeD = (int)(diameter * HOLE_RATIO);
        int hx    = cx - holeD / 2;
        int hy    = cy - holeD / 2;
        g2.setColor(Color.WHITE);
        g2.fill(new Ellipse2D.Double(hx, hy, holeD, holeD));

        if(!centerLabel.isEmpty()){
            g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
            g2.setColor(new Color(60, 60, 60));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(centerLabel,
                cx - fm.stringWidth(centerLabel) / 2,
                cy + fm.getAscent() / 2 - 2);
        }

        int legendX  = chartAreaW + 6;
        int legendY  = titleH + 20;
        int swatchSz = 11;
        int lineH    = 20;

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        for(Map.Entry<String, Integer> entry : data.entrySet()){
            Color c   = colors.getOrDefault(entry.getKey(), Color.GRAY);
            int pct   = total > 0 ? (int) Math.round((double) entry.getValue() / total * 100) : 0;
            String lbl = entry.getKey() + " " + pct + "%";

            g2.setColor(c);
            g2.fillRoundRect(legendX, legendY + 1, swatchSz, swatchSz, 3, 3);

            g2.setColor(new Color(80, 80, 80));
            g2.drawString(lbl, legendX + swatchSz + 5, legendY + swatchSz);

            legendY += lineH;
        }
    }
}
