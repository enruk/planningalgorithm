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

    static List<Machine> Res;
    static int AnzMa;

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
        AnzMa = 3;

        Population P = new Population(p, AnzMa);
        P.GenetischerAlgorithmus();
        Res = P.Individuen.get(0).Machines;

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        //CategoryAxis yAxis = new CategoryAxis();
        //yAxis.setCategories(Names);
        //yAxis.setLabel("Ressource");
        //NumberAxis xAxis = new NumberAxis();
        //xAxis.setLabel("Zeit [s]");

        //StackedBarChart <Number,String> schedule = new StackedBarChart<>(xAxis, yAxis);
        //schedule.setTitle("Zeitplan");

        CategoryAxis yAxis = new CategoryAxis();
        yAxis.setLabel("Ressource");
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Zeit [s]");
        StackedBarChart schedule = new StackedBarChart<>(xAxis, yAxis);
        schedule.setData(getChartData());
        schedule.setTitle("Bester gefundener Prozess");
        primaryStage.setTitle("Zeitplan");
        schedule.setCategoryGap(40);

        StackPane root = new StackPane();
        root.getChildren().add(schedule);
        primaryStage.setScene(new Scene(root, 400, 250));
        primaryStage.show();
    }

    

    public static ObservableList<XYChart.Series<Number, String>> getChartData() {
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
        

        // Formatierung des Graphen
        String[] ResourcenNamen = new String[AnzMa];
        for (int i=0;i<AnzMa;i++){
            String Num = String.valueOf(i);
            String ResName = "Ressource "+ Num;
            ResourcenNamen[i] = ResName;
        }
        
        ObservableList<String> Names = FXCollections.observableArrayList(ResourcenNamen); 

        ObservableList<XYChart.Series<Number, String>> answer = FXCollections.observableArrayList();
        for (int i=0;i<MaxOp;i++){
            Series<Number, String> SeriesX = new Series<Number, String>();
            for (int j=0;j<AnzMa;j++){
                SeriesX.getData().add(new XYChart.Data(Res.get(j).Ganntplan[i],ResourcenNamen[j]));
            }
            answer.add(SeriesX);
        }
        return answer;
    }
}