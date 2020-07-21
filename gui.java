package planningalgorithm;

import java.io.File;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
//import javafx.scene.layout.StackPane;
import javafx.stage.Stage;



public class gui extends Application {

    static List<Machine> Res;
    static int AnzMa;
    static int p;

    String filepath;
    Scene menu;
    Scene settings;


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
        //int p = 100;
        //AnzMa = 3;

        //Population P = new Population(p, AnzMa);
        //P.GenetischerAlgorithmus();
        //Res = P.Individuen.get(0).Machines;

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Genetic Algorithm for FJSSPs");

        //Scene1: Menü
        BorderPane MainLayout = new BorderPane();
        MainLayout.setPadding(new Insets(20,20,20,20));

        //TOP
        VBox VBoxTop = new VBox(20);

        Label TitleTop = new Label("File input");
        TitleTop.setFont(new Font("Arial", 20));
        HBox HBoxTop = new HBox(20);

            TextField ProcessInput = new TextField("C:/Users/Henrik/Documents/ExampleProcess.xls");
            ProcessInput.setMinWidth(500);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select process (.xls only)");
            Button ButtonChooseProcess = new Button("Search");

            ButtonChooseProcess.setOnAction(e ->{
                File file = fileChooser.showOpenDialog(primaryStage);
                filepath = file.getAbsolutePath();
                ProcessInput.clear();
                ProcessInput.setText(filepath);
            });
            HBoxTop.getChildren().addAll(ButtonChooseProcess,ProcessInput);
            HBoxTop.setHgrow(ProcessInput, Priority.ALWAYS);

        VBoxTop.getChildren().addAll(TitleTop, HBoxTop);
        MainLayout.setTop(VBoxTop);

        //CENTER
        VBox VBoxCenter = new VBox(20);
        Label TitleCenter = new Label("Input of the process variables");
        VBoxCenter.setPadding(new Insets(50, 0, 10, 0));

        HBox In1 = new HBox(20);
            Label TitleEingabe1 = new Label("Number of resources");
            TitleEingabe1.setMinWidth(200);
            TextField Eingabe1 = new TextField("3");
            Eingabe1.setMaxWidth(100);
            In1.getChildren().addAll(TitleEingabe1,Eingabe1);

        HBox In2 = new HBox(20);
            Label TitleEingabe2 = new Label("Populationsize");
            TitleEingabe2.setMinWidth(200);
            TextField Eingabe2 = new TextField("100");
            Eingabe2.setMaxWidth(100);
            In2.getChildren().addAll(TitleEingabe2,Eingabe2);
        
        HBox In3 = new HBox(20);
            Label TitleEingabe3 = new Label("Maximum generations");
            TitleEingabe3.setMinWidth(200);
            TextField Eingabe3 = new TextField("150");
            Eingabe3.setMaxWidth(100);
            In3.getChildren().addAll(TitleEingabe3,Eingabe3);

        HBox In4 = new HBox(20);
            Label TitleEingabe4 = new Label("Maximum calculation time [min]");
            TitleEingabe4.setMinWidth(200);
            TextField Eingabe4 = new TextField("15");
            Eingabe4.setMaxWidth(100);
            In4.getChildren().addAll(TitleEingabe4,Eingabe4);

        HBox In5 = new HBox(20);
            Label TitleEingabe5 = new Label("Input 5");
            TitleEingabe5.setMinWidth(200);
            TextField Eingabe5 = new TextField();
            Eingabe5.setMaxWidth(100);
            In5.getChildren().addAll(TitleEingabe5,Eingabe5);
        

        VBoxCenter.getChildren().addAll(TitleCenter,In1,In2,In3,In4,In5);
        MainLayout.setCenter(VBoxCenter);

        //BOTTOM
        HBox HBoxBottom = new HBox(20);
        HBoxBottom.setAlignment(Pos.TOP_RIGHT);

            Button StartGA = new Button("Start");
            StartGA.setOnAction(e->{
                String pSizeStr = Eingabe2.getText();
                p = Integer.parseInt(pSizeStr);
                String AnzMaStr = Eingabe1.getText();
                AnzMa = Integer.parseInt(AnzMaStr);
                
                Population P = new Population(p,AnzMa);
                P.GenetischerAlgorithmus();
            });

            Button ButtonSettings = new Button ("Detailed settings Genetic Algorithm");
            ButtonSettings.setOnAction(e->primaryStage.setScene(settings));

        HBoxBottom.getChildren().addAll(ButtonSettings,StartGA);
        MainLayout.setBottom(HBoxBottom);
        
        
        menu = new Scene(MainLayout,1000,600);

        //Settings
        BorderPane SettingsLayout = new BorderPane();
        SettingsLayout.setPadding(new Insets(20,20,20,20));
        
        //Top
        Label TitleSettings = new Label("Settings genetic algorithm");
        SettingsLayout.setTop(TitleSettings);
        
        //CENTER
        VBox VBoxCenterSet = new VBox(20);
        Label TitleCenterSet = new Label("Eingabe von Prozessgrößen");
        VBoxCenterSet.setPadding(new Insets(50, 0, 10, 0));

        HBox Set1 = new HBox(20);
            Label TitleSet1 = new Label("Mutation probability");
            TitleSet1.setMinWidth(200);
            TextField Setting1 = new TextField();
            Setting1.setMaxWidth(100);
            Set1.getChildren().addAll(TitleSet1,Setting1);

        HBox Set2 = new HBox(20);
            Label TitleSet2 = new Label("Recombination probability");
            TitleSet2.setMinWidth(200);
            TextField Setting2 = new TextField();
            Setting2.setMaxWidth(100);
            Set2.getChildren().addAll(TitleSet2,Setting2);
        
        HBox Set3 = new HBox(20);
            Label TitleSet3 = new Label("");
            TitleSet3.setMinWidth(200);
            TextField Setting3 = new TextField();
            Setting3.setMaxWidth(100);
            Set3.getChildren().addAll(TitleSet3,Setting3);

        HBox Set4 = new HBox(20);
            Label TitleSet4 = new Label("Eingabe 4");
            TitleSet4.setMinWidth(200);
            TextField Setting4 = new TextField();
            Setting4.setMaxWidth(100);
            Set4.getChildren().addAll(TitleSet4,Setting4);

        HBox Set5 = new HBox(20);
            Label TitleSet5 = new Label("Eingabe 5");
            TitleSet5.setMinWidth(200);
            TextField Setting5 = new TextField();
            Setting5.setMaxWidth(100);
            Set5.getChildren().addAll(TitleSet5,Setting5);
        

        VBoxCenterSet.getChildren().addAll(TitleCenterSet,Set1,Set2,Set3,Set4,Set5);
        SettingsLayout.setCenter(VBoxCenterSet);
        
        //RIGHT
        VBox VBoxRightSet = new VBox(20);
        Label TitleMutation = new Label("Mutation");
        TitleMutation.setFont(new Font("Arial",20));
        VBoxRightSet.setPadding(new Insets(50, 0, 10, 0));
        

        HBox Set6 = new HBox(20);
        Label TitleSet6 = new Label("Mutation probability sequence");
        TitleSet6.setMinWidth(200);
        TextField Setting6 = new TextField();
        Setting6.setMaxWidth(100);
        Set6.getChildren().addAll(TitleSet6,Setting6);

        HBox Set7 = new HBox(20);
        Label TitleSet7 = new Label("Mutation probability allocation");
        TitleSet7.setMinWidth(200);
        TextField Setting7 = new TextField();
        Setting7.setMaxWidth(100);
        Set7.getChildren().addAll(TitleSet7,Setting7);

        Label EmptyRow = new Label("");

        Label TitleRecombination = new Label("Recombination");
        TitleRecombination.setFont(new Font("Arial",20));

        HBox Set8 = new HBox(20);
        Label TitleSet8 = new Label("Crossover probability sequence");
        TitleSet8.setMinWidth(200);
        TextField Setting8 = new TextField();
        Setting8.setMaxWidth(100);
        Set8.getChildren().addAll(TitleSet8,Setting8);


        HBox Set9 = new HBox(20);
        Label TitleSet9 = new Label("Mutation probability allocation");
        TitleSet9.setMinWidth(200);
        TextField Setting9 = new TextField();
        Setting9.setMaxWidth(100);
        Set9.getChildren().addAll(TitleSet9,Setting9);

        Label EmptyRow2 = new Label("");


        HBox Set10 = new HBox(20);
        Label TitleSet10 = new Label("Mutation probability");
        TitleSet10.setMinWidth(200);
        TextField Setting10 = new TextField();
        Setting10.setMaxWidth(100);
        Set10.getChildren().addAll(TitleSet10,Setting10);

        VBoxRightSet.getChildren().addAll(TitleMutation,Set6,Set7,EmptyRow,TitleRecombination,Set8,Set9,EmptyRow2,Set10);
        SettingsLayout.setRight(VBoxRightSet);


        //BOTTOM
        HBox HBoxBottom2 = new HBox(20);
        HBoxBottom2.setAlignment(Pos.TOP_RIGHT);

            Button Backbutton = new Button("Back");
            Backbutton.setOnAction(e->primaryStage.setScene(menu));
            HBoxBottom2.getChildren().add(Backbutton);

        SettingsLayout.setBottom(HBoxBottom2);


        settings = new Scene(SettingsLayout,1000,600);

        primaryStage.setScene(menu);
        primaryStage.show();


        //CategoryAxis yAxis = new CategoryAxis();
        //yAxis.setCategories(Names);
        //yAxis.setLabel("Ressource");
        //NumberAxis xAxis = new NumberAxis();
        //xAxis.setLabel("Zeit [s]");

        //StackedBarChart <Number,String> schedule = new StackedBarChart<>(xAxis, yAxis);
        //schedule.setTitle("Zeitplan");

        //CategoryAxis yAxis = new CategoryAxis();
        //yAxis.setLabel("Ressource");
        //NumberAxis xAxis = new NumberAxis();
        //xAxis.setLabel("Zeit [s]");
        //StackedBarChart schedule = new StackedBarChart<>(xAxis, yAxis);
        //schedule.setData(getChartData());
        //schedule.setTitle("Bester gefundener Prozess");
        //primaryStage.setTitle("Zeitplan");
        //schedule.setCategoryGap(40);

        //StackPane root = new StackPane();
        //root.getChildren().add(schedule);
        //primaryStage.setScene(new Scene(root, 400, 250));
        //primaryStage.show();
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