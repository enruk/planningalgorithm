package planningalgorithm;

import javafx.concurrent.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class taskGA extends Task<List<Integer>> {

    int p;
    int nMa;
    int maxGen;
    int currentGen;
    SettingsGA detailedSettings;

    taskGA(int Populationsize, int nMachines, int maxGenerations, SettingsGA Settings){  
        p = Populationsize;             //Source: GUI
        nMa = nMachines;                //Source: GUI
        maxGen = maxGenerations;        //Source: GUI
        detailedSettings = Settings;    //Source: GUI
    }
    
    @Override 
    protected List<Integer> call() throws Exception{
        List<Integer> Generations = new ArrayList<>();
        
        // Genetic Algorithm
        Population P = new Population(p,nMa,maxGen,detailedSettings);

        // Read Data
        P.readData();


        // Create Population
        currentGen = 1;
        Generations.add(Integer.valueOf(currentGen));
        this.updateProgress(currentGen, maxGen);
        P.Individuen = new ArrayList<>(100);
        for (int i = 0; i < p; i++) {
            Individuum indi = new Individuum(i, currentGen, P.nOp, nMa);
            P.Individuen.add(indi);
        }


        // Initizise First Generation
        P.initializeFirstGen();


        // Decoding First Generation
        for (int i=0;i<p;i++){
            P.Individuen.get(i).correctingAllocation( P.ProzessListe);
            P.Individuen.get(i).decodierung(P.Vorrangmatrix,P.MaschinenZeiten);
        }

        // Put Individuals in Temp
        P.Temp = new ArrayList<>(P.Individuen.size());
        for (int i=0;i<P.Individuen.size();i++){
            P.Temp.add(P.Individuen.get(i));
        }

        // Fitness
        P.calculateFitness();


        // Output fist Generation
        P.createScheduleGraph();


        while (currentGen < maxGen){

            // Elternselektion
            P.Parents = new ArrayList<Individuum>(p*2);

            
            // Selecting Parents with SUS
            P.stochasticUniversalSampling();


            // Make Pairs from Parents
            P.createPairs();


            //Recombination
            currentGen++; // New Generation, so count up
            Generations.add(Integer.valueOf(currentGen));
            this.updateProgress(currentGen, maxGen); //tell GUI

            P.Children = new ArrayList<Individuum>(p);
            for (int i = 0; i < p; i++) {
                Individuum indi = new Individuum(i, currentGen, P.nOp, nMa);
                P.Children.add(indi);
            }

            //Unifrom Recombination
            //missing so far


            //N-Point-Recombination
            P.nPointRecombination();

            // PMX / Kantenrekombination
            // Missing so far


            //Order Recombination
            P.orderRecombination();


            //Childmutation
            for (int i=0;i<p;i++){

                //Allocation
                //One-Bit-Mutation
                if (Boolean.TRUE.equals(detailedSettings.DoAlloBit)){
                    P.Children.get(i).einbitmutation(P.nOp,detailedSettings.MutAlloProbability);
                }

                //Swap-Mutation - currently missing



                //Sequence
                //Mixed-Muation
                if (Boolean.TRUE.equals(detailedSettings.DoSeqMix)){
                    P.Children.get(i).mixedmutation(P.nOp, detailedSettings.MutAlloProbability, 1);
                }

                //Swap-Mutation - currently missing
            }


            // Decoding Children
            for (int i=0;i<p;i++){
                P.Children.get(i).correctingAllocation(P.ProzessListe);
                P.Children.get(i).decodierung(P.Vorrangmatrix,P.MaschinenZeiten);
            }

            // Ersetzungsstrategie
            // First Attempt: Take only the 50 best old Individuals
            Collections.sort(P.Individuen, new FitnessComparator());


            //Bring Parents and Children together
            P.Temp.clear();
            P.Temp = new ArrayList<>(p/2+p);
            for (int i=0;i<p/2;i++){
                P.Temp.add(P.Individuen.get(i));
            }

            for (int i=0;i<p;i++){
                P.Temp.add(P.Children.get(i));
            }

            // Fitness, but from Temp, need to be fixed
            P.calculateFitness();


            // Tournament Selection
            P.tournamentSelection();


            // Sorting Temp by Wins in Tournamentselection
            Collections.sort(P.Temp, new TournamentWinsComparator());
            P.Individuen.clear();


            // Add the best p Individuals to Individuen
            for (int i=0;i<p;i++){
                P.Individuen.add(P.Temp.get(i));
            }


            // Clean up
            P.Parents.clear();
            P.Children.clear();
            P.Temp.clear();
        }

        Collections.sort(P.Individuen, new FitnessComparator());
        P.createScheduleGraph();

        return Generations;
    }

}






class FitnessComparator implements Comparator<Individuum> {
    @Override
    public int compare(Individuum a, Individuum b) {
        return a.timeFitness < b.timeFitness ? 1 : a.timeFitness == b.timeFitness ? 0 : -1;
    }
}

class TournamentWinsComparator implements Comparator<Individuum> {
    @Override
    public int compare(Individuum a, Individuum b) {
        return a.tournamentWins < b.tournamentWins ? 1 : a.tournamentWins == b.tournamentWins ? 0 : -1;
    }
}
