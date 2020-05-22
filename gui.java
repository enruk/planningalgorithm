package planningalgorithm;

import java.util.List;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class gui extends Application {

    public static int[] AddToArray(int[] Arr, int what, int n) {
        int[] NewArr = new int[Arr.length + n];
        for (int i = 0; i < Arr.length; i++) {
            NewArr[i] = Arr[i];
        }
        for (int j = Arr.length + 1; j < Arr.length + n; j++) {
            NewArr[j] = what;
        }

        return NewArr;
    }

    public static void main(String[] args) {

        // Launch der Startgui, um grundlagende Daten einzutragen
        int p = 100;
        int AnzMa = 3;

        Population P = new Population(p, AnzMa);
        P.GenetischerAlgorithmus();
        List<Machine> Ressourcen = PrepSchedule(P.Individuen.get(0).Machines,AnzMa);

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart barChart = new BarChart(xAxis, yAxis);
        barChart.setData(getChartData());
        barChart.setTitle("A");
        primaryStage.setTitle("BarChart example");

        StackPane root = new StackPane();
        root.getChildren().add(barChart);
        primaryStage.setScene(new Scene(root, 400, 250));
        primaryStage.show();
    }

    public static List<Machine> PrepSchedule(List<Machine> Res, int AnzMa) {
        // Get the maximum amount of operations of all machines
        int MaxOp = 0;
        for (int i = 0; i < AnzMa; i++) {
            int temp = Res.get(i).PlannedOperations.length;
            if (temp > MaxOp) {
                MaxOp = temp;
            }
        }

        // Ablaufppläne der anderen Maschinen mit Nullen auffüllen
        for (int i = 0; i < AnzMa; i++) {
            int AnzOps = Res.get(i).PlannedOperations.length;
            if (AnzOps < MaxOp) {
                int DiffOps = MaxOp - AnzOps;
                Res.get(i).Startzeiten = AddToArray(Res.get(i).Startzeiten, 0, DiffOps);
                Res.get(i).Endzeiten = AddToArray(Res.get(i).Endzeiten, 0, DiffOps);
            }
        }

        //Filling Array Ganttplan which is needed to create the XYChart.Series
        for (int i=0;i<AnzMa;i++){
            Res.get(i).Ganntplan = new int[2*MaxOp+1];
            for (int j=1;j<=MaxOp;j++){
                if(j==1){
                    Res.get(i).Ganntplan[0] = Res.get(i).Startzeiten[0];
                    Res.get(i).Ganntplan[1] = Res.get(i).Endzeiten[0] - Res.get(i).Startzeiten[0];
                }
                else{
                    if (Res.get(i).Endzeiten[j-2]<Res.get(i).Startzeiten[j-1]){
                        Res.get(i).Ganntplan[2*j-2] = Res.get(i).Startzeiten[j-1] - Res.get(i).Endzeiten[j-2];// Pause, Index: 2,4,5,6
                    }
                    Res.get(i).Ganntplan[2*j-1] = Res.get(i).Endzeiten[j-1] - Res.get(i).Startzeiten[j-1];  //Prozess: Endzeit, Index: 1,3,5,
                }
            }
        }
        return Res;
    }

    private ObservableList<XYChart.Series<String, Double>> getChartData() {

        double aValue = 17.56;
        double cValue = 17.06;
        ObservableList<XYChart.Series<String, Double>> answer = FXCollections.observableArrayList();
        Series<String, Double> aSeries = new Series<String, Double>();
        Series<String, Double> cSeries = new Series<String, Double>();
        aSeries.setName("a");
        cSeries.setName("C");
        
        for (int i = 2011; i < 2021; i++) {
            aSeries.getData().add(new XYChart.Data(Integer.toString(i), aValue));
            aValue = aValue + Math.random() - .5;
            cSeries.getData().add(new XYChart.Data(Integer.toString(i), cValue));
            cValue = cValue + Math.random() - .5;
        }
        answer.addAll(aSeries, cSeries);
        return answer;
    }
}