package charts;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JPanel;

public class BarChartPanel extends JPanel {

    private Map<String, Integer> data = new LinkedHashMap<>();
    private Map<String, Color> colors = new LinkedHashMap<>();
    private String title = "";
    private String yAxisLabel = "";

    private static final int PADDING_LEFT   = 55;
    private static final int PADDING_RIGHT  = 20;
    private static final int PADDING_TOP    = 40;
    private static final int PADDING_BOTTOM = 50;
    private static final int BAR_GAP_RATIO  = 3;

    public BarChartPanel(){
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

    public void setYAxisLabel(String label){
        this.yAxisLabel = label;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if(data == null || data.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int W = getWidth();
        int H = getHeight();

        if(!title.isEmpty()){
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.setColor(new Color(60, 60, 60));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(title, (W - fm.stringWidth(title)) / 2, 22);
        }

        int chartX = PADDING_LEFT;
        int chartY = PADDING_TOP;
        int chartW = W - PADDING_LEFT - PADDING_RIGHT;
        int chartH = H - PADDING_TOP - PADDING_BOTTOM;

        int maxVal = data.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        if(maxVal == 0) maxVal = 1;

        int gridLines = 4;
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.setColor(new Color(200, 200, 200));
        FontMetrics fmSmall = g2.getFontMetrics();
        for(int i = 0; i <= gridLines; i++){
            int y = chartY + chartH - (int)((double) i / gridLines * chartH);
            g2.setColor(new Color(230, 230, 230));
            g2.drawLine(chartX, y, chartX + chartW, y);
            // Y axis labels
            int labelVal = (int) Math.round((double) maxVal * i / gridLines);
            String label = String.valueOf(labelVal);
            g2.setColor(new Color(140, 140, 140));
            g2.drawString(label, chartX - fmSmall.stringWidth(label) - 5, y + fmSmall.getAscent() / 2);
        }

        g2.setColor(new Color(200, 200, 200));
        g2.drawLine(chartX, chartY, chartX, chartY + chartH);
        g2.drawLine(chartX, chartY + chartH, chartX + chartW, chartY + chartH);

        int n = data.size();
        int slotW = chartW / n;
        int barW  = slotW * 2 / BAR_GAP_RATIO;
        int barOffset = (slotW - barW) / 2;

        int idx = 0;
        for(Map.Entry<String, Integer> entry : data.entrySet()){
            String key = entry.getKey();
            int val    = entry.getValue();

            Color barColor = colors.getOrDefault(key, new Color(255, 140, 0));

            int barH = (int)((double) val / maxVal * chartH);
            int bx   = chartX + idx * slotW + barOffset;
            int by   = chartY + chartH - barH;

            g2.setColor(new Color(0, 0, 0, 15));
            g2.fillRoundRect(bx + 2, by + 2, barW, barH, 6, 6);

            g2.setColor(barColor);
            g2.fillRoundRect(bx, by, barW, barH, 6, 6);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2.setColor(new Color(60, 60, 60));
            FontMetrics fmVal = g2.getFontMetrics();
            String valStr = String.valueOf(val);
            int valX = bx + (barW - fmVal.stringWidth(valStr)) / 2;
            g2.drawString(valStr, valX, by - 4);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(new Color(100, 100, 100));
            FontMetrics fmLbl = g2.getFontMetrics();
            int lblX = chartX + idx * slotW + (slotW - fmLbl.stringWidth(key)) / 2;
            g2.drawString(key, lblX, chartY + chartH + 18);

            idx++;
        }

        if(!yAxisLabel.isEmpty()){
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setColor(new Color(140, 140, 140));
            Graphics2D g2r = (Graphics2D) g2.create();
            g2r.rotate(-Math.PI / 2);
            g2r.drawString(yAxisLabel, -chartY - chartH / 2 - g2r.getFontMetrics().stringWidth(yAxisLabel) / 2, 12);
            g2r.dispose();
        }
    }
}
