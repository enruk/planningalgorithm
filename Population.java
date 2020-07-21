package planningalgorithm;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jfree.ui.RefineryUtilities;

import javafx.application.Application;
//import javafx.stage.Stage;

//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.Iterator;

//import org.apache.poi.hssf.usermodel.HSSFSheet;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.CellType;
//import org.apache.poi.ss.usermodel.Row;

public class Population {
    int p;
    int AnzMaschinen;
    int[][] Vorrangmatrix;
    int[][] MaschinenZeiten;
    List<Operationen> ProzessListe;
    List<Individuum> Individuen; //Current Population
    List<Individuum> Parents; //Choosen Parents from Current Population
    List<Individuum> Children; //New made Individuals


    Population(int Populationsize, int AnzMa){
        p = Populationsize;
        AnzMaschinen = AnzMa;
    }


    public static double Zufallszahl() {
        double RanNum;
        Random zufallszahl = new Random();
        RanNum = zufallszahl.nextDouble();
        return RanNum;
    }

    public static double round(double value, int decimalPoints) {
        double d = Math.pow(10, decimalPoints);
        return Math.round(value * d) / d;
    }

    public int[][] CopyArray (int[][] Arr) {
        int[][] Copy = new int[Arr.length][Arr[0].length];
        for (int i = 0; i < Arr.length; i++) {
            for (int j = 0; j < Arr[0].length; j++) {
                Copy[i][j] = Arr[i][j];
            }
        }
        return Copy;
    }

    private int max(int[] FooArr) {
        int maximum = -100;
        for (int i=0;i<FooArr.length;i++) {
            if (FooArr[i] > maximum) {
                maximum = FooArr[i];
            }
        }
        return maximum;
    }

    private int min(int[] FooArr){
        int minimum = 10000;
        for (int i=0;i<FooArr.length;i++) {
            if (FooArr[i] < minimum) {
                minimum = FooArr[i];
            }
        }
        return minimum;
    }


    public void GenetischerAlgorithmus (){

        // EXCELDATEI AUSLESEN
        int gen = 1;

        ProcessList ProzessRead = new ProcessList();
        try {
            ProzessRead.ReadoutExcel(AnzMaschinen);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        ProzessListe = ProzessRead.OperationenListe;
        int nOp = ProzessListe.size();

        Vorrangmatrix = CopyArray(ProzessRead.Präzedenzmatrix);
        MaschinenZeiten = CopyArray(ProzessRead.Maschinenmatrix);

        System.out.println(ProzessListe.get(1).Operationsname);

        for (int k = 0; k < nOp; k++) {
            System.out.print("\n");
            for (int l = 0; l < nOp; l++)
                System.out.print(Vorrangmatrix[k][l] + " ");
        }

        for (int k = 0; k < nOp; k++) {
            System.out.print("\n");
            for (int l = 0; l < AnzMaschinen; l++)
                System.out.print(MaschinenZeiten[k][l] + " ");
        }



        // INITIALISIERUNG

        // Population erzeugen
        Individuen = new ArrayList<Individuum>(100);
        for (int i = 0; i < p; i++) {
            Individuum indi = new Individuum(i, gen, nOp, AnzMaschinen);
            Individuen.add(indi);
        }

        // Zufällig Zuordnung in alle Individuen befüllen
        for (int i = 0; i < p; i++) {
            int[] RandomAllocation = new int[nOp];
            double RandomMachine;
            for (int j = 0; j < nOp; j++) {
                RandomMachine = round(Zufallszahl() * (AnzMaschinen - 1), 0);
                int Machine = (int) RandomMachine;
                RandomAllocation[j] = Machine;
            }
            Individuen.get(i).Zuordnung = RandomAllocation;
        }

        // Zufällig Sequenz in alle Individuen befüllen
        int[] PermuSortiert = new int[nOp];
        for (int j = 0; j < nOp; j++) {
            PermuSortiert[j] = j + 1;
        }

        for (int i = 0; i < p; i++) {
            int[] RandomSequenz = PermuSortiert;
            for (int j = 0; j < nOp; j++) {
                int randomposition = (int) round(Zufallszahl() * (nOp - 1), 0);
                int SaveNum = RandomSequenz[j];
                RandomSequenz[j] = RandomSequenz[randomposition];
                RandomSequenz[randomposition] = SaveNum;
            }
            Individuen.get(i).Sequenz = RandomSequenz;
        }

        //Decodierung der Startpopulation
        for (int i=0;i<p;i++){
            Individuen.get(i).decodierung(nOp,AnzMaschinen,Vorrangmatrix,MaschinenZeiten);
        }

        // Erste Bewertung
        
        // Rangbasierte Fitness
        int nRank = 5; //In GUI rein
        float HeighestRankFitness = 1.8f; //In GUI rein

        float[] RankedFitness = new float[nRank];

        for (int r=1;r<nRank+1;r++){
            RankedFitness[r-1] = (2-HeighestRankFitness) + (HeighestRankFitness - (2-HeighestRankFitness))*(r-1)/(nRank-1);
        }

        int[] FinishingTimes = new int[p];
        for (int i=0;i<p;i++){
            FinishingTimes[i] = max(Individuen.get(i).EndzeitenOp);
        }
        int MaxFinishingTimes = max(FinishingTimes);
        int MinFinishingTimes = min(FinishingTimes);
        
        int Range = MaxFinishingTimes - MinFinishingTimes;
        int RangeEachRank = Range / nRank;

        for (int r=1;r<nRank+1;r++){
            for (int i=0;i<p;i++){
                if (FinishingTimes[i] <= MaxFinishingTimes - (r-1)*RangeEachRank){
                    Individuen.get(i).SUSRank = r; //eigentlich jetzt unnötig
                    Individuen.get(i).TimeFitness = RankedFitness[r-1];
                }
            }
        }

        // GENERATIONENSCHLEIFE
        // Elternselektion
        List<Individuum> Parents = new ArrayList<Individuum>(p*2);
            
        //SUS Verfahren
        float SumFitness=0;
        for (int i=0;i<p;i++){
            SumFitness =+ Individuen.get(i).TimeFitness;
        }

        float PointerRange = SumFitness / (2*p);
        float startPointer = PointerRange * (float)Zufallszahl();

        float[] PointerArray = new float[2*p];
        for (int i=0;i<2*p;i++){
            PointerArray[i] = startPointer + (i-1)*PointerRange;
        }

        float[] Wheel = new float[p+1];
        Wheel[0] = 0;

        for (int i=1;i<=p;i++){
            Wheel[i] = Individuen.get(i-1).TimeFitness + Wheel[i-1];
        }

        //Pointer hochzählen
        for (int i=0;i<2*p;i++){
            //Wheel Abschnitte hochzählen
            for (int j=1;j<=p;j++){
                if (PointerArray[i] <= Wheel[j] && PointerArray[i] > Wheel[j-1]){
                    Parents.set(i,Individuen.get(j-1));
                }
            }
        }

        // Rekombination
        // Mutation
        // Decodierung
        // Ersetzungsstrategie
        // Umweltselektion
        // Abbsuchbedingung
        
        // Ausgabe
        Schedule Zeitplan = new Schedule("Test",AnzMaschinen,Individuen.get(0).Machines);
        Zeitplan.pack();
        RefineryUtilities.centerFrameOnScreen(Zeitplan);
        Zeitplan.setVisible(true);

        //Schedule.main();

    }   
}