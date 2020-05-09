package planningalgorithm;

//import java.util.Arrays;
//import java.util.ArrayList;

//import java.util.Arrays;
import java.util.List;
//import java.util.Map;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
//import javafx.scene.chart.XYChart;
//import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SchedulingDiagramm extends Application {

    List<Machine> Ressourcen;
    String name;

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
    
    public void PrepSchedule(int AnzMaschinen, List<Machine> Res) {

        Ressourcen = Res;

        //Get the maximum amount of operations of all machines
        int MaxOp = 0;
        for (int i=0;i<AnzMaschinen;i++){
            int temp = Ressourcen.get(i).PlannedOperations.length;
            if (temp>MaxOp){
                MaxOp = temp;
            }
        }

        //Ablaufppläne der anderen Maschinen mit Nullen auffüllen
        for (int i=0;i<AnzMaschinen;i++){
            int AnzOps = Ressourcen.get(i).PlannedOperations.length;
            if (AnzOps<MaxOp){
                int DiffOps = MaxOp - AnzOps;
                Ressourcen.get(i).Startzeiten = AddToArray(Ressourcen.get(i).Startzeiten, 0, DiffOps);
                Ressourcen.get(i).Endzeiten = AddToArray(Ressourcen.get(i).Endzeiten, 0, DiffOps);
            }
        }

        //Filling Array Ganttplan which is needed to create the XYChart.Series
        for (int i=0;i<AnzMaschinen;i++){
            Ressourcen.get(i).Ganntplan = new int[2*MaxOp+1];
            Ressourcen.get(i).Ganntplan[0] = Ressourcen.get(i).Startzeiten[0]; //Startzeit der erste Op = Belegungszeit, Index 0, Comment: Nicht ungebingt
            for (int j=1;j<MaxOp;j++){
                Ressourcen.get(i).Ganntplan[2*j-1] = Ressourcen.get(i).Endzeiten[j-1] - Ressourcen.get(i).Startzeiten[j-1];  //Prozess: Endzeit, Index: 1,3,5,
                if (Ressourcen.get(i).Endzeiten[j-1]<Ressourcen.get(i).Startzeiten[j]){
                    Ressourcen.get(i).Ganntplan[2*j] = Ressourcen.get(i).Startzeiten[j] - Ressourcen.get(i).Endzeiten[j-1];// Pause, Index: 2,4,5,6
                }
            }
        }
        

    }

    

    @Override
    public void start(Stage stage) {


        int AnzMaschinen = 3;
        //int MaxOp = 5;
        String[] ResourcenNamen = new String[AnzMaschinen];

        for (int i=0;i<AnzMaschinen;i++){
            String Num = String.valueOf(i);
            String ResName = "Ressource "+ Num;
            ResourcenNamen[i] = ResName;
        }

        ObservableList<String> Names = FXCollections.observableArrayList(ResourcenNamen); 


        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(Names);
        xAxis.setLabel("Ressource");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Zeit [s]");

        StackedBarChart <Number,String> schedule = new StackedBarChart<>(yAxis, xAxis);
        schedule.setTitle("Zeitplan");

        //for (int i=0;i<MaxOp;i++){
        //    XYChart.Series<Number,String> SeriesX = new XYChart.Series<>();
        //    for (int j=0;j<AnzMaschinen;j++){
        //       SeriesX.getData().add(new XYChart.Data<>(Ressourcen.get(j).Ganntplan[i],ResourcenNamen[j]));
        //    }
        //   schedule.getData().add(SeriesX);
        //}
          
        Scene szene = new Scene(schedule);

        stage.setTitle("Zeitplan");
        stage.setScene(szene);
        stage.show();
    }
}
