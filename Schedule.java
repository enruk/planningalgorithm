package planningalgorithm;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.awt.Font;
import java.util.List;
import java.awt.color.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.CategoryDataset;
//import org.jfree.chart.plot.CategoryPlot;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.chart.renderer.category.CategoryItemRenderer;
//import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.general.DatasetUtilities;
//import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.ui.ApplicationFrame;
//import org.jfree.ui.RefineryUtilities;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;


public class Schedule extends ApplicationFrame {

    public Schedule(String titel, int AnzMa, List<Machine> Res) {
        super(titel);
      
        final CategoryDataset dataset = createDataset(AnzMa, Res);
        final JFreeChart chart = createChart(dataset,Res);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 350));
        setContentPane(chartPanel);
    }
    
    public int[] AddToArray (int[] Arr, int what, int n){
        int[] NewArr = new int[Arr.length+n];
        for (int i=0;i<Arr.length;i++){
            NewArr[i] = Arr[i];
        }
        for (int j=Arr.length+1;j<Arr.length+n;j++){
            NewArr[j] = what;
        }

        return NewArr;
    }


    private CategoryDataset createDataset(int AnzMaschinen, List<Machine> Ressourcen) {

        //Get the maximum amount of operations of all machines
        int MaxOp = 0;
        for (int i=0;i<AnzMaschinen;i++){
            int temp = Ressourcen.get(i).PlannedOperations.length;
            if (temp>MaxOp){
                MaxOp = temp;
            }
        }

        // Fill sequences of all other machines with zeros
        for (int i=0;i<AnzMaschinen;i++){
            int AnzOps = Ressourcen.get(i).PlannedOperations.length;
            if (AnzOps<MaxOp){
                int DiffOps = MaxOp - AnzOps;
                Ressourcen.get(i).Startzeiten = AddToArray(Ressourcen.get(i).Startzeiten, 0, DiffOps);
                Ressourcen.get(i).Endzeiten = AddToArray(Ressourcen.get(i).Endzeiten, 0, DiffOps);
                Ressourcen.get(i).PlannedOperations = AddToArray(Ressourcen.get(i).PlannedOperations,0,DiffOps);
            }
        }

        //Filling Array Ganttplan which is needed to create the XYChart.Series
        for (int i=0;i<AnzMaschinen;i++){
            Ressourcen.get(i).Ganntplan = new int[2*MaxOp];
            Ressourcen.get(i).Ganntplan[0] = Ressourcen.get(i).Startzeiten[0]; //Startzeit der erste Op = Belegungszeit, Index 0, Comment: Nicht ungebingt
            for (int j=1;j<MaxOp;j++){
                Ressourcen.get(i).Ganntplan[2*j-1] = Ressourcen.get(i).Endzeiten[j-1] - Ressourcen.get(i).Startzeiten[j-1];  //Prozess, Index: 1,3,5,
                if (Ressourcen.get(i).Endzeiten[j-1]<Ressourcen.get(i).Startzeiten[j]){
                    Ressourcen.get(i).Ganntplan[2*j] = Ressourcen.get(i).Startzeiten[j] - Ressourcen.get(i).Endzeiten[j-1];// Pause, Index: 2,4,5,6
                }
            }
            Ressourcen.get(i).Ganntplan[2*MaxOp-1] = Ressourcen.get(i).Endzeiten[MaxOp-1] - Ressourcen.get(i).Startzeiten[MaxOp-1];
        }



        double[][] data = new double[Ressourcen.get(0).Ganntplan.length][AnzMaschinen];
        for (int i=0;i<Ressourcen.get(0).Ganntplan.length;i++){
            for (int j=0;j<AnzMaschinen;j++){
                data[i][j] = Ressourcen.get(j).Ganntplan[i];
            }
        }
        return DatasetUtilities.createCategoryDataset("Operations", "Machine ", data);
    }
      
    private JFreeChart createChart(final CategoryDataset dataset, List<Machine> Res) {
      
        final JFreeChart chart = ChartFactory.createStackedBarChart("Production Schedule", "", "Time",dataset, PlotOrientation.HORIZONTAL, false, true, false);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(java.awt.Color.WHITE);
        int ColorChangeCounter = 0;
        for (int row=0; row<dataset.getRowCount(); row++) {
            
            if (row%2 == 0 || row == 0) {
                plot.getRenderer().setSeriesPaint(row, java.awt.Color.white);
            }
            else { 
                if (ColorChangeCounter%2 == 0 || ColorChangeCounter == 0) {
                    plot.getRenderer().setSeriesPaint(row, java.awt.Color.LIGHT_GRAY);
                }
                else{
                    plot.getRenderer().setSeriesPaint(row, java.awt.Color.GRAY);
                }
                ColorChangeCounter++;
            }
        }
    
        

        // BAR LABELS
        // calculate the column total for each column
        // column = Machine
        // row = Operations on Machines
        for (int col=0; col<dataset.getColumnCount(); col++) {
            
            double AddedValues = 0;
            int PlannedOpsCounter=0;
            for (int row=0; row<dataset.getRowCount(); row++) {
                if (dataset.getValue(row, col) != null) {
                    Number value = dataset.getValue(row, col);
                    double valuedouble = value.doubleValue();
                    double OperationsName;
                    if (row%2 == 0 || row == 0) {
                        //Do nothing
                    }
                    else { 
                        int valueint = value.intValue();
                        if ( valueint != 0){
                            OperationsName = Res.get(col).PlannedOperations[PlannedOpsCounter];
                            PlannedOpsCounter++;

                            //  display as decimal integer
                            NumberFormat nf = DecimalFormat.getIntegerInstance();
                            // Create the annotation
                            CategoryTextAnnotation cta = new CategoryTextAnnotation(nf.format(Math.round(OperationsName)),dataset.getColumnKey(col), AddedValues + valuedouble*0.5);
                            Font font = new Font("Courier", Font.BOLD,12);
                            cta.setFont(font);
                            // Add to the plot
                            plot.addAnnotation(cta);
                        }
                    }

                    AddedValues += valuedouble;

                }           
            }
        }

        

        //plot.setRenderer(renderer);
        return chart;
    }


    //private IntervalCategoryDataset getCategoryDataset(int AnzMa, List<Machine> Res){
    
        

    //    return dataset;
    //}


        //JFreeChart chart = ChartFactory.createGanttChart("Schedule", "Machines", "Time", collection, false, false, false);
        //CategoryPlot plot = chart.getCategoryPlot();

        //CategoryItemRenderer renderer = plot.getRenderer();

        



    //public void output() {

        //final Schedule demo = new Schedule("Stacked Bar Chart");
        //demo.pack();
        //RefineryUtilities.centerFrameOnScreen(demo);
        //demo.setVisible(true);
    //}
}