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
    List<Individuum> Individuen;

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


        Schedule Zeitplan = new Schedule("Test",AnzMaschinen,Individuen.get(0).Machines);
        Zeitplan.pack();
        RefineryUtilities.centerFrameOnScreen(Zeitplan);
        Zeitplan.setVisible(true);

        //Schedule.main();

    }   
}