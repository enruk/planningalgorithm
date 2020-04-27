package planningalgorithm;

import java.util.Arrays;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SchedulingDiagramm extends Application {
    
    public void start(Stage primaryStage) throws Exception {
    
    int AnzMa = 3;
    //Defining x axis
    CategoryAxis xAxis = new CategoryAxis();
    String[] Ressourcen = new String[AnzMa];
    for (int i=1;i==AnzMa;i++){
        Ressourcen[i] = String.valueOf(i);
    }
    xAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(Ressourcen)));
    xAxis.setLabel("Ressourcen");

    //Defining y axis
    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Zeit [s]");


    //Creating BarChart
    StackedBarChart<String,Number> schedule = new StackedBarChart<>(xAxis, yAxis);
    schedule.setTitle("Schedule");
     

    }

}