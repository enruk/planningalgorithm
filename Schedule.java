package planningalgorithm;

import java.awt.Color;

import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class Schedule extends ApplicationFrame {

    public Schedule(String titel, int AnzMa,List<Machine> Res) {
        super(titel);
        int bla = AnzMa + 1;
        final CategoryDataset dataset = createDataset();
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 350));
        setContentPane(chartPanel);
        }

        private CategoryDataset createDataset() {
        double[][] data = new double[][]{
        {210, 300, 320, 265, 299, 200},
        {200, 304, 201, 201, 340, 300},
        };
        return DatasetUtilities.createCategoryDataset(
        "Team ", "Match", data);
        }

        private JFreeChart createChart(final CategoryDataset dataset) {

        final JFreeChart chart = ChartFactory.createStackedBarChart(
        "Stacked Bar Chart ", "", "Score",
        dataset, PlotOrientation.VERTICAL, true, true, false);

        chart.setBackgroundPaint(new Color(249, 231, 236));

        CategoryPlot plot = chart.getCategoryPlot();
        plot.getRenderer().setSeriesPaint(0, new Color(128, 0, 0));
        plot.getRenderer().setSeriesPaint(1, new Color(0, 0, 255));

        return chart;
    }



    //public void output() {

        //final Schedule demo = new Schedule("Stacked Bar Chart");
        //demo.pack();
        //RefineryUtilities.centerFrameOnScreen(demo);
        //demo.setVisible(true);
    //}
}