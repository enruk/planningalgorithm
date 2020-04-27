package planningalgorithm;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

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

    public static void main(String[] args) throws IOException {







    //Prozess AUSLESEN

    int gen = 1;
    int p = 1;
    int AnzMaschinen = 3;
    

    ProcessList ProzessRead = new ProcessList();
    ProzessRead.ReadoutExcel(AnzMaschinen);

    List<Operationen> Prozess = ProzessRead.OperationenListe; 
    int nOp = Prozess.size();

    int[][] Vorrangmatrix = ProzessRead.Präzedenzmatrix; 
    int[][] MaschinenZeiten = ProzessRead.Maschinenmatrix;

    

    System.out.println(Prozess.get(1).Operationsname);

    for (int k=0;k<nOp;k++){
        System.out.print("\n");
        for (int l=0;l<nOp;l++)
        System.out.print(Vorrangmatrix[k][l] + " ");
    }
    
    for (int k=0;k<nOp;k++){
        System.out.print("\n");
        for (int l=0;l<AnzMaschinen;l++)
        System.out.print(MaschinenZeiten[k][l] + " ");
    }








    //INITIALISIERUNG
    // Mehrere Objekte vom Typ Individuum erzeugen und unter einer Liste speichern


    // Population erzeugen
    List<Individuum> Population = new ArrayList<Individuum>(100);
    for (int i=0;i<100;i++) {
        Individuum indi = new Individuum(i,gen,nOp,AnzMaschinen);
        Population .add(indi);
     }


     //Zufällig Zuordnung in alle Individuen befüllen
     for (int i=0;i<p;i++){
         int[] RandomAllocation = new int [nOp];
        double RandomMachine;
        for (int j=0;j<nOp;j++){
            RandomMachine = round(Zufallszahl() * (AnzMaschinen-1),0);
            int Machine = (int) RandomMachine; 
            RandomAllocation[j] = Machine;
        }
         Population.get(i).Zuordnung = RandomAllocation;
     }


     //Zufällig Sequenz in alle Individuen befüllen
     int[] PermuSortiert = new int[nOp];
     for (int j=0;j<nOp;j++){
        PermuSortiert[j]=j+1;
     }

     for (int i=0;i<100;i++){
        int[] RandomSequenz = PermuSortiert;
        for (int j=0;j<nOp;j++){
            int randomposition = (int)round(Zufallszahl()*(nOp-1),0);
            int SaveNum = RandomSequenz[j];
            RandomSequenz[j] = RandomSequenz[randomposition];
            RandomSequenz[randomposition] = SaveNum;
        }
        Population.get(i).Sequenz = RandomSequenz;
     }


     // Decodierung der Startpopulation
     for (int i=0;i<p;i++){
        Population.get(i).decodierung(nOp,AnzMaschinen,Vorrangmatrix,MaschinenZeiten);
     }

     for (int i=0;i<nOp;i++){
         System.out.println(Population.get(0).StartzeitenOp[i]);
     }





    
    // 1-Bit-Mutation ausführen
    //Population.get(66).einbitmutation(nOp);
    //for (int i = 0; i<nOp; i++){
    //    System.out.println(Population.get(0).Zuordnung[i]);
    //}

    // Sawp-Mutation
    //opulation.get(34).swapmutation(nOp);
    //for (int i = 0; i<nOp; i++){
    //    System.out.println(Population.get(0).Sequenz[i]);
    //}


    
    }   
}



//ALT

// Neues Individuum mit Zuordnung und Sequenz
//Individuum indi1 = new Individuum(1,gen);
//ndi1.Zuordnung = new int[] {0,1,0,1,1};
//indi1.Sequenz = new int[] {2,1,4,3,5};