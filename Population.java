package planningalgorithm;

import java.util.List;
import java.awt.EventQueue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import org.jfree.ui.RefineryUtilities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Population {
    int p;
    int AnzMaschinen;
    int gen;
    StringProperty valueGen = new SimpleStringProperty();
    int[][] Vorrangmatrix;
    int[][] MaschinenZeiten;
    List<Operationen> ProzessListe;
    List<Individuum> Individuen; //Current Population
    List<Individuum> Parents; //Choosen Parents from Current Population
    List<Individuum> Children; //New made Individuals
    List<Individuum> Temp;


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

    public int[] CopyArr (int[] Arr){
        int[] Copy = new int[Arr.length];
        for (int i=0;i<Arr.length;i++){
            Copy[i] = Arr[i];
        }
        return Copy;
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


    public void GenetischerAlgorithmus (int p, int AnzMaschinen, int maxGen, SettingsGA DetailedSettings){

        // EXCELDATEI AUSLESEN
        gen = 1;
        System.out.println(gen + "");

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
            Individuen.get(i).Zuordnung = CopyArr(RandomAllocation);
        }

        // Zufällig Sequenz in alle Individuen befüllen
        int[] PermuSortiert = new int[nOp];
        for (int j = 0; j < nOp; j++) {
            PermuSortiert[j] = j + 1;
        }

        for (int i = 0; i < p; i++) {
            int[] RandomSequenz = CopyArr(PermuSortiert);
            for (int j = 0; j < nOp; j++) {
                int randomposition = (int) round(Zufallszahl() * (nOp - 1), 0);
                int SaveNum = RandomSequenz[j];
                RandomSequenz[j] = RandomSequenz[randomposition];
                RandomSequenz[randomposition] = SaveNum;
            }
            Individuen.get(i).Sequenz = CopyArr(RandomSequenz);
        }

        //Decodierung der Startpopulation

        for (int i=0;i<p;i++){
            Individuen.get(i).correctingAllocation(AnzMaschinen, nOp, ProzessListe);
            Individuen.get(i).decodierung(nOp,AnzMaschinen,Vorrangmatrix,MaschinenZeiten);
        }

        // Erste Bewertung
        
        // Rangbasierte Fitness
        int nRank = DetailedSettings.nRanks;
        float HeighestRankFitness = DetailedSettings.RankedFitness;

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
        
        EventQueue.invokeLater(new Runnable(){

            @Override 
            public void run(){
                Schedule iniSchedule = new Schedule(AnzMaschinen,Individuen.get(0).Machines); 
            }
        });

        // GENERATIONENSCHLEIFE
        while (gen < maxGen){

        
            // Elternselektion
            List<Individuum> Parents = new ArrayList<Individuum>(p*2);
                
            //SUS Verfahren
            float SumFitness=0;
            for (int i=0;i<p;i++){
                SumFitness = SumFitness + Individuen.get(i).TimeFitness;
            }

            float PointerRange = SumFitness / (2*p);
            float startPointer = PointerRange * (float)Zufallszahl();

            float[] PointerArray = new float[2*p];
            for (int i=0;i<2*p;i++){
                PointerArray[i] = startPointer + (i)*PointerRange;
            }

            float[] Wheel = new float[p+1];
            Wheel[0] = 0;

            for (int i=1;i<=p;i++){
                Wheel[i] = Individuen.get(i-1).TimeFitness + Wheel[i-1];
            }

            float TopBorder;
            float BottomBorder;
            int DeletedPointers = 0;
            int foundPointer = 0;

            for (int w=1;w<=p;w++){
                // Count Wheelsections
                TopBorder = Wheel[w];
                BottomBorder = Wheel[w-1];
                DeletedPointers = DeletedPointers + foundPointer;
                foundPointer = 0;
                for (int i=DeletedPointers;i<p*2;i++){
                    // Count Pointer
                    // Check if Pointer in Wheelsection
                    if(PointerArray[i] <= TopBorder && PointerArray[i] > BottomBorder){
                        foundPointer = foundPointer + 1;
                        Parents.add(Individuen.get(w-1));
                    }
                    if(PointerArray[i] > TopBorder){
                        break;
                    }
                }
                continue;
            }


            // Pairing
            int[] PairingSorted = new int[p*2];
            for (int j = 0; j < p*2; j++) {
                PairingSorted[j] = j;
            }

            int[] PairingRandom = new int[p*2];
            for (int i = 0; i < p*2; i++) {
                PairingRandom = CopyArr(PairingSorted);
                for (int j = 0; j < p*2; j++) {
                    int randomposition = (int) round(Zufallszahl() * (p*2 - 1), 0);
                    int SaveNum = PairingRandom[j];
                    PairingRandom[j] = PairingRandom[randomposition];
                    PairingRandom[randomposition] = SaveNum;
                }
            }

            
            // Rekombination
            gen++; // New Generation, so count up
            String genStr = String.valueOf(gen);
            valueGen.set(genStr);
            

            Children = new ArrayList<Individuum>(p);
            for (int i = 0; i < p; i++) {
                Individuum indi = new Individuum(i, gen, nOp, AnzMaschinen);
                Children.add(indi);
            }


            // Uniform Rekombination
            // Fehlt noch 

            // N-Punkt-Rekombination
            if (Boolean.TRUE.equals(DetailedSettings.DoRecNPoint)){

                int RecN = 2; //Nicht aus GUI, besser berechnen aus nOp

                for (int i=0;i<p;i++){

                    // Creating new Arrays
                    int[] Allocation1 = new int[nOp];
                    int[] Allocation2 = new int[nOp];
                    System.arraycopy(Parents.get(PairingRandom[i * 2]).Zuordnung, 0, Allocation1, 0, nOp);
                    System.arraycopy(Parents.get(PairingRandom[i * 2+1]).Zuordnung, 0, Allocation2, 0, nOp);

                    int[] Child = new int[nOp];

                    
                    // Making the Cuts
                    int[] Cuts = new int[RecN+2];
                    Cuts[0] = 0;

                    for (int N=1;N<RecN+1;N++){
                        Cuts[N] = (int) round(Zufallszahl()*nOp,0);
                        if (N>1){
                            for (int j=0;j<N;j++){
                                while(Cuts[N] == Cuts[j] || Cuts[N]-Cuts[j]==1 || Cuts[N]-Cuts[j]==-1){
                                    Cuts[N] = (int) round(Zufallszahl()*nOp,0);
                                }
                            }
                        }
                    }
                    Cuts[RecN+1] = nOp-1;
                    Arrays.sort(Cuts);

                    // Filling the Child
                    for (int N=0;N<RecN+1;N++){
                        if((N%2)==0){
                            System.arraycopy(Allocation1, Cuts[N], Child, Cuts[N], Cuts[N+1]-Cuts[N]);
                            if(N==RecN){
                                Child[nOp-1] = Allocation1[nOp-1];
                            }
                        }
                        else{
                            System.arraycopy(Allocation1, Cuts[N], Child, Cuts[N], Cuts[N+1]-Cuts[N]);
                            if(N==RecN){
                                Child[nOp-1] = Allocation2[nOp-1];
                            }
                        }
                    }
                    System.arraycopy(Child,0,Children.get(i).Zuordnung,0,nOp);
                }
            }


            // PMX / Kantenrekombination
            // Fehlt noch

            // Ordnungsrekombination
            if (Boolean.TRUE.equals(DetailedSettings.DoRecOrder)){

                for (int i=0;i<p;i++){
                    int[] Sequence1 = new int[nOp];
                    int[] Sequence2 = new int[nOp];
    
                    System.arraycopy(Parents.get(PairingRandom[i * 2]).Sequenz, 0, Sequence1, 0, nOp);
                    System.arraycopy(Parents.get(PairingRandom[i * 2+1]).Sequenz, 0, Sequence2, 0, nOp);
    
    
                    // Get one section
                    int sectionStart;
                    int sectionEnd;
    
                    sectionStart = (int) round(Zufallszahl()*nOp,0);
                    sectionEnd = (int) round(Zufallszahl()*nOp,0);
                    while (sectionEnd == sectionStart){
                        sectionEnd = (int) round(Zufallszahl()*nOp,0);
                    }
                    if (sectionEnd < sectionStart){
                        int temp = sectionStart;
                        sectionStart = sectionEnd;
                        sectionEnd = temp;
                    }
    
                    int[] Child = new int[nOp];
    
                    System.arraycopy(Sequence1, sectionStart, Child, sectionStart, sectionEnd-sectionStart);
    
                    for (int k=0;k<nOp;k++){
                        int value = Sequence2[k];
                        boolean result = IntStream.of(Child).anyMatch(x -> x == value);
                        if (!result){
                            Child[k] = value;
                        }
                    }
                    System.arraycopy(Child,0,Children.get(i).Sequenz,0,nOp);
                }

            }

            

            // Mutation

            // Childmutation

            for (int i=0;i<p;i++){

                //Allocation
                //One-Bit-Mutation
                if (Boolean.TRUE.equals(DetailedSettings.DoAlloBit)){
                    Children.get(i).einbitmutation(nOp,DetailedSettings.MutAlloProbability);
                }

                //Swap-Mutation - currently missing



                //Sequence

                //Mixed-Muation
                if (Boolean.TRUE.equals(DetailedSettings.DoSeqMix)){
                    Children.get(i).mixedmutation(nOp, DetailedSettings.MutAlloProbability, 1);
                }
                //Swap-Mutation - currently missing


            }


            // Decodierung
            for (int i=0;i<p;i++){
                Children.get(i).correctingAllocation(AnzMaschinen, nOp, ProzessListe);
                Children.get(i).decodierung(nOp,AnzMaschinen,Vorrangmatrix,MaschinenZeiten);
            }

            // Ersetzungsstrategie
            // First Attempt: Take only the 50 best old Individuals
            Collections.sort(Individuen, new FitnessComparator());

            // Bewertung

            //Bring Parents and Children together

            Temp = new ArrayList<>(p/2+p);
            for (int i=0;i<p/2;i++){
                Temp.add(Individuen.get(i));
            }

            for (int i=0;i<p;i++){
                Temp.add(Children.get(i));
            }

            // Rangbasierte Fitness
            // Frage: Bewertung nur von Kindern und von Kindern und Eltern? 
            RankedFitness = new float[nRank];

            for (int r=1;r<nRank+1;r++){
                RankedFitness[r-1] = (2-HeighestRankFitness) + (HeighestRankFitness - (2-HeighestRankFitness))*(r-1)/(nRank-1);
            }

            FinishingTimes = new int[Temp.size()];
            for (int i=0;i<Temp.size();i++){
                FinishingTimes[i] = max(Temp.get(i).EndzeitenOp);
            }
            MaxFinishingTimes = max(FinishingTimes);
            MinFinishingTimes = min(FinishingTimes);
            
            Range = MaxFinishingTimes - MinFinishingTimes;
            RangeEachRank = Range / nRank;

            for (int r=1;r<nRank+1;r++){
                for (int i=0;i<Temp.size();i++){
                    if (FinishingTimes[i] <= MaxFinishingTimes - (r-1)*RangeEachRank){
                        Temp.get(i).SUSRank = r; //eigentlich jetzt unnötig
                        Temp.get(i).TimeFitness = RankedFitness[r-1];
                    }
                }
            }



            
            // Umweltselektion

            for (int q=0;q<DetailedSettings.QTournaments;q++){
                Collections.shuffle(Temp);
                for (int m=0;m<Temp.size()/2;m++){
                    if (Temp.get(m*2).TimeFitness>=Temp.get(m*2+1).TimeFitness){
                        Temp.get(m*2).TournamentWins++;
                    }
                    else{
                        Temp.get(m*2+1).TournamentWins++;
                    }
                }
            }

            // Sorting Temp by Wins in Tournamentselection
            Collections.sort(Temp, new TournamentWinsComparator());
            Individuen.clear();

            // Add the best p Individuals to Individuen
            for (int i=0;i<p;i++){
                Individuen.add(Temp.get(i));
            }


            // Clean up
            Parents.clear();
            Children.clear();
            Temp.clear();

            //Output Generation
            System.out.println(gen+"");
        

        // Abbbruchbedingung
        }
        
        // Ausgabe des Champions
        //Schedule ZeitplanChamp = new Schedule("Test",AnzMaschinen,Individuen.get(0).Machines);
        //ZeitplanChamp.pack();
        //RefineryUtilities.centerFrameOnScreen(ZeitplanChamp);
        //ZeitplanChamp.setVisible(true);

        EventQueue.invokeLater(new Runnable(){

            @Override 
            public void run(){
                Schedule champSchedule = new Schedule(AnzMaschinen,Individuen.get(0).Machines); 
            }
        });
    }

}

class FitnessComparator implements Comparator<Individuum> {
    @Override
    public int compare(Individuum a, Individuum b) {
        return a.TimeFitness < b.TimeFitness ? 1 : a.TimeFitness == b.TimeFitness ? 0 : -1;
    }
}

class TournamentWinsComparator implements Comparator<Individuum> {
    @Override
    public int compare(Individuum a, Individuum b) {
        return a.TournamentWins < b.TournamentWins ? 1 : a.TournamentWins == b.TournamentWins ? 0 : -1;
    }
}