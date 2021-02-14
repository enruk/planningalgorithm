package planningalgorithm;

import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Individuum {
    int number;
    int birthGeneration;

    int nOp;
    int nMa;

    int[] Allocation;
    int[] Sequence;
    int[][] predecessorWorkingTimes;
    int[][] startingTimeMatrix;
    float timeFitness;
    int susRank;
    int tournamentWins;

    List<Machine> Machines; 
    List<Operationen> Process;  


    //Konstruktor
    Individuum(int Num, int startgen, int AnzOp, int AnzMa){
        number=Num;
        nOp = AnzOp;
        nMa = AnzMa;
        birthGeneration=startgen;
        Allocation = new int[AnzOp];
        Sequence = new int[AnzOp];
        predecessorWorkingTimes = new int[AnzOp][AnzOp];
        startingTimeMatrix = new int[AnzOp][AnzOp+1];

        // Liste der Maschinen erstellen
        Machines = new ArrayList<>(AnzMa);
        for (int i=0;i<AnzMa;i++) {
            Machine MachineX = new Machine(i,0);
            Machines .add(MachineX);
        }

        // Liste der Prozesse erstellen
        Process = new ArrayList<>(AnzOp);
        for (int i=0;i<AnzOp;i++) {
            Operationen Ops = new Operationen(nOp,nMa);
            Process.add(Ops);
        }
    }


    // Zufallszahl 
    private double Zufallszahl(){
        double RanNum;
        Random zufallszahl = new Random();
        RanNum = zufallszahl.nextDouble();
        return RanNum;
    }

    private double round(double value, int decimalPoints) {
        double d = Math.pow(10, decimalPoints);
        return Math.round(value * d) / d;
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

    public int[] copyArr (int[] Arr){
        int[] Copy = new int[Arr.length];
        for (int i=0;i<Arr.length;i++){
            Copy[i] = Arr[i];
        }
        return Copy;
    }


    public int[] AddOneToArray (int[] Arr, int what){
        // Case 1: Array is empty
        if (Arr == null){
            int[] NewArr = new int[1];
            NewArr[0] = what;
            return NewArr;
        }
        // Case 2: Array isnt empty
        else {
            int[] NewArr = new int[Arr.length + 1];
            for (int i=0;i<Arr.length;i++){
                NewArr[i] = Arr[i];
            }
            NewArr[Arr.length] = what;
            return NewArr;
        }
    }

    public int[] OneValue(int[] BarArr, int Value){
        int[] OneValueArr = new int[BarArr.length];
        for (int i=0;i<BarArr.length;i++){
            OneValueArr[i] = Value;
        }
        return OneValueArr;
    }
 
    public int[] ChangeValue (int[] ArrArr, int OldValue, int NewValue){
        int[] NewArrArr = new int[ArrArr.length];
        for (int i=0;i<ArrArr.length;i++){
            if (ArrArr[i] == OldValue){
                ArrArr[i] = NewValue;
            }
        }
        return NewArrArr;
    }

    public int Count (int[] CountArr, int Value){
        int CountDoku = 0;
        for (int i=0;i<CountArr.length;i++){
            if (CountArr[i]==Value){
                CountDoku++;
            }
        }
        return CountDoku;
    }

    public int[][] copyMatrix (int[][] Arr) {
        int[][] Copy = new int[Arr.length][Arr[0].length];
        for (int i = 0; i < Arr.length; i++) {
            for (int j = 0; j < Arr[0].length; j++) {
                Copy[i][j] = Arr[i][j];
            }
        }
        return Copy;
    }


    
    void correctingAllocation (List<Operationen> OperationsList){


        // Liste der Maschinen erstellen
        for (int i=0;i<nOp;i++){
            Process.get(i).availableMachines = new int[nMa];
            System.arraycopy(OperationsList.get(i).availableMachines, 0, Process.get(i).availableMachines, 0, nMa);
            // Unterschied OperationsList und Prozess
        }


        for (int i=0;i<nOp;i++){
            int RequestedMachine = Allocation[i];
            if (Process.get(i).availableMachines[RequestedMachine] == 0){
                // Requested Machine cant do the operation
                // Find available Machines
                List<Integer> numbersAvailableMachines = new ArrayList<>(); 
                for (int j=0;j<nMa;j++){
                    if (Process.get(j).availableMachines[j] == 1){
                        numbersAvailableMachines.add(j);
                    }
                }
                // Pick random Machine
                double randomMachine =  round((numbersAvailableMachines.size()-1) * Zufallszahl(),0);
                int newMachine = (int) randomMachine;

                //Put new Machine in Allocation
                Allocation[i] = numbersAvailableMachines.get(newMachine);
            }
        }


    }

    // Decodierung
    void decodierung(int[][] Vorrangmatrix, int[][] Maschinenzeiten){

        // VORBEREITUNG
        //Prozesszeitenmatrix bestimmen aus Zuordnung und Maschinenzeiten und Maschinenzeit in Matrix der Vorgänger-Prozess-Zeiten eintragen
        for (int z=0;z<nOp;z++){
            int WorkingMachine = Allocation[z]; 
            Process.get(z).timeWorking = Maschinenzeiten[z][WorkingMachine];
        }
    

        for (int z=0;z<nOp;z++){
            for (int s=0;s<nOp;s++){
                if  (Vorrangmatrix[z][s]==1){
                    predecessorWorkingTimes[z][s] = Process.get(s).timeWorking;
                }
                else{
                    predecessorWorkingTimes[z][s] = 0;
                }
            }
        }

        // Optional: Ist das hier wirklich notwendig?
        for (int i=0;i<nOp;i++) {
            Process.get(i).workingMachine = Allocation[i];
        }

        
        // Get the predecessors of every Operation and save them under Process.get(x).Predecessors
        for (int i=0;i<nOp;i++){
            Process.get(i).Predecessor = copyArr(Vorrangmatrix[i]);
            Process.get(i).remainingPredecessors = copyArr(Vorrangmatrix[i]);
        }

        // Get the starting Operations
        for (int z=0;z<nOp;z++){
            if (max(predecessorWorkingTimes[z])==0){
                Process.get(z).operationReadyToStart = true;
                Process.get(z).operationNotReady = false;
            }
        }

        // Calculate Matrix of starting times: t_Op = t_PredecessorStart (in Matrix starting times) + t_PredecessorProcess(from predecessor times);
        int OperationsDone = 0;
        while (OperationsDone < nOp){

            
            for (int z=0;z<nOp;z++){
                int FoundOp = 0;

                // Search for starting Operation
                if (Process.get(z).operationReadyToStart == true){
                    FoundOp = z;
                    for (int z2=0;z2<nOp;z2++){
                        if (predecessorWorkingTimes[z2][FoundOp] !=0){
                            startingTimeMatrix[z2][FoundOp] = max(startingTimeMatrix[FoundOp]) + predecessorWorkingTimes[z2][FoundOp]; //t_Op = t_PredecessorStart(from startingTimeMatrix) + t_PredecessorProcess(from predecessorTimes)
                        }
                    }

                // Mark the found operation as done
                for (int z3=0;z3<nOp;z3++){
                    if (Process.get(z3).remainingPredecessors[FoundOp] == 1){
                        Process.get(z3).remainingPredecessors[FoundOp] = 0; // FoundOp is done and is now no longer a predecessor on which other operations have to wait for
                    }
                }

                Process.get(FoundOp).operationDone = true;
                }
            }

            //Calculate new  Starting Operations
            for (int z=0;z<nOp;z++){

                // Operation has no remaining Predecessor and wasnt done yet: Ready to Start
                if (max(Process.get(z).remainingPredecessors)==0 && Process.get(z).operationDone == false){
                    Process.get(z).operationReadyToStart = true;
                    Process.get(z).operationNotReady = false;
                }

                // Operation has no remaining Predecessor, but i already done
                if (max(Process.get(z).remainingPredecessors)==0 && Process.get(z).operationDone == true){
                    Process.get(z).operationReadyToStart = false;
                    Process.get(z).operationNotReady = false;
                }

                // Operation still has remaining Predecessors
                if (max(Process.get(z).remainingPredecessors)!=0){
                    Process.get(z).operationNotReady = true;
                }
            }


            //Abbruchbedingungen ermitteln
            OperationsDone = 0;
            for (int z=0;z<nOp;z++){
                if(Process.get(z).operationDone == true){
                    OperationsDone++;
                }
            }
        }


        //Fertigungszeiten berechnen
        int[] timesProduction = new int[nOp];
        for (int z=0;z<nOp;z++){
            timesProduction[z] = max(startingTimeMatrix[z]) + Process.get(z).timeWorking;
        }


        int[] A = new int[nOp];
        int[][] V = copyMatrix(Vorrangmatrix);
        int[] B = new int[nOp];
        int operationDash;
        int operationDashDash;
        int operationStar;


        for (int z=0;z<nOp;z++){
            if (max(Vorrangmatrix[z]) < 0.1)
                A[z] = 1;
            else{
                A[z] = 0; 
            }
        }
        

        // Beginn Giffler-Thompson Algorithmus
        int GTAbbruchbedingung = 0;
        while (GTAbbruchbedingung < nOp){
            operationDash = 0;
            operationDashDash = 0; 
            operationStar = 0;

            // GT - Step 2.H1: Get O' (Operation with earliest Productiontime, finsihed first)
            int earliestProductiontime = 1000;
            for (int z=0;z<nOp;z++){
                if (A[z] == 1 && timesProduction[z]< earliestProductiontime){
                     operationDash = z;
                     earliestProductiontime = timesProduction[z];
                }
            }

            // GT - Step 2.H2: Get B, all Operations of A on the same Machine as O'
            int CurrentMachine = Allocation[operationDash];

            for (int z=0;z<nOp;z++){
                if (A[z]==1 && Allocation[z]==CurrentMachine){
                    B[z]=1;
                }
                else{
                    B[z] = 0;
                }
            }
            
            // GT - Step 2.H3: Get O'' (Operation of B with ealiest starting Time)
            int earliestStartingtime = 1000;
            for (int z=0;z<nOp;z++){
                if (B[z]==1 && max(startingTimeMatrix[z])<earliestStartingtime){
                    operationDashDash = z;
                    earliestStartingtime = max(startingTimeMatrix[z]);
                }
            }


            // GT - Step 2.H4: Delete operations outside of the sigma window
            
            //Check if earliest starttime is smaller then occupation
            int starttime;
            if (max(startingTimeMatrix[operationDashDash]) < Machines.get(CurrentMachine).timeOccupation){
                starttime = Machines.get(CurrentMachine).timeOccupation;
            }
            else{
                starttime = max(startingTimeMatrix[operationDashDash]);
            } 
            
            double sigma = 0.5;

            // Delete Operations outside
            for (int z=0;z<nOp;z++){
                if (B[z]==1 && max(startingTimeMatrix[z]) > (starttime + sigma * (timesProduction[operationDash] - starttime))){
                    B[z] = 0;
                }
            }


            //GT - Step 2.H5: Get O*
            int Permutation = 1000;
            for (int z=0;z<nOp;z++){
                if (B[z]==1 && Sequence[z]<Permutation){
                    Permutation = Sequence[z];
                    operationStar = z;
                }
            }


            // Mark OperationStern as done
            A[operationStar] = -1; // OperationStar is done, mark with -1 in A
            for (int z=0;z<nOp;z++){
                if (A[z] != -1){
                    V[z][operationStar] = 0; // OperationStar is no longer predecessor of other operations
                }
            }
            V[operationStar] = OneValue(V[operationStar], -1); // OperationStar is done, mark with -1 in V

            // Set the starting time of operationStar
            if (max(startingTimeMatrix[operationStar]) < Machines.get(CurrentMachine).timeOccupation){
                Process.get(operationStar).timeStart = Machines.get(CurrentMachine).timeOccupation;
            }
            if (max(startingTimeMatrix[operationStar]) >= Machines.get(CurrentMachine).timeOccupation){
                Process.get(operationStar).timeStart = max(startingTimeMatrix[operationStar]);
            }

            // Set the ending time of operationStar
            Process.get(operationStar).timeEnd = Process.get(operationStar).timeStart + Process.get(operationStar).timeWorking;

            // Set new occupation time of the current machine
            Machines.get(CurrentMachine).timeOccupation = Process.get(operationStar).timeEnd;


            // GT - Schritt 4: Add successors of O* to A
            for (int z=0;z<nOp;z++){

                if (max(V[z]) == 0){
                    A[z] = 1;
                }
                if (max(V[z]) == -1){
                    A[z] = -1;
                }
                if (max(V[z]) == 1){
                    A[z] = 0;
                }
            }

            //GT - Step 5: Update occupation time of the current machine to every operation which is also done on that machine
            for (int z=0;z<nOp;z++){
                if (A[z] != -1 && Allocation[z] == CurrentMachine){
                    startingTimeMatrix[z][nOp] = Process.get(operationStar).timeEnd;
                }
            }



            //Update startingTimeMatrix, bc of new occupation time

            //Reset startingTimeMatrix except the last column nOp which includes the occupation times
            for (int r=0;r<nOp;r++){
                for (int c=0;c<nOp;c++){
                    startingTimeMatrix[r][c] = 0;
                }
            }

            //Update startingTimeMatrix, bc of new occupation time

            // Reset the status in every operation
            for (int i=0;i<nOp;i++){
                Process.get(i).operationDone = false;
                Process.get(i).operationNotReady = true;
                Process.get(i).operationReadyToStart = false;
            }

            // Get the predecessors of every Operation  and save them under Process.get(x).Predecessors
            for (int i=0;i<nOp;i++){
                Process.get(i).remainingPredecessors = copyArr(Process.get(i).Predecessor);
                //System.arraycopy(Process.get(i).Predecessor, Process.get(i).Predecessor[0],Process.get(i).remainingPredecessors,Process.get(i).remainingPredecessors[0],Process.get(i).Predecessor.length);
            }

            // Get the new starting Operations
            for (int z=0;z<nOp;z++){
                if (max(predecessorWorkingTimes[z])==0){
                    Process.get(z).operationReadyToStart = true;
                    Process.get(z).operationNotReady = false;
                }
            }

            // Calculate startingTimeMatrix: t_Op = t_PredecessorStart(from startingTimeMatrix) + t_PredecessorProcess(from predecessorTimes);
            OperationsDone = 0;
            while (OperationsDone < nOp){

                
                for (int z=0;z<nOp;z++){
                    int FoundOp = 0;

                    // Search for starting Operation
                    if (Process.get(z).operationReadyToStart == true){
                        FoundOp = z;
                        for (int z2=0;z2<nOp;z2++){
                            if (predecessorWorkingTimes[z2][FoundOp] !=0){
                                startingTimeMatrix[z2][FoundOp] = max(startingTimeMatrix[FoundOp]) + predecessorWorkingTimes[z2][FoundOp]; //t_Op = t_PredecessorStart(from startingTimeMatrix) + t_PredecessorProcess(from predecessorTimes)
                            }
                        }

                    // Mark the found operation as done
                    for (int z3=0;z3<nOp;z3++){
                        if (Process.get(z3).Predecessor[FoundOp] == 1){
                            Process.get(z3).Predecessor[FoundOp] = 0; // FoundOp is done and is now no longer a predecessor on which other operations have to wait for
                        }
                    }

                    Process.get(FoundOp).operationDone = true;
                    }
                }

                //Calculate new  Starting Operations
                for (int z=0;z<nOp;z++){

                    // Operation has no remaining Predecessor and wasnt done yet: Ready to Start
                    if (max(Process.get(z).Predecessor)==0 && Process.get(z).operationDone == false){
                        Process.get(z).operationReadyToStart = true;
                        Process.get(z).operationNotReady = false;
                    }

                    // Operation has no remaining Predecessor, but i already done
                    if (max(Process.get(z).Predecessor)==0 && Process.get(z).operationDone == true){
                        Process.get(z).operationReadyToStart = false;
                        Process.get(z).operationNotReady = false;
                    }

                    // Operation still has remaining Predecessors
                    if (max(Process.get(z).Predecessor)!=0){
                        Process.get(z).operationNotReady = true;
                    }
                }


                // Get termination condition
                OperationsDone = 0;
                for (int z=0;z<nOp;z++){
                    if(Process.get(z).operationDone == true){
                        OperationsDone++;
                    }
                }
            }


            //Calculate production times
            for (int z=0;z<nOp;z++){
                timesProduction[z] = max(startingTimeMatrix[z]) + Process.get(z).timeWorking;
            }

            //Give the working Machine some Information about the Operation: Needed for the BarChart
            Machines.get(CurrentMachine).plannedOperations = AddOneToArray(Machines.get(CurrentMachine).plannedOperations, operationStar);
            Machines.get(CurrentMachine).startingTimesOps = AddOneToArray(Machines.get(CurrentMachine).startingTimesOps, Process.get(operationStar).timeStart);
            Machines.get(CurrentMachine).endingTimesOps = AddOneToArray(Machines.get(CurrentMachine).endingTimesOps, Process.get(operationStar).timeEnd);

            // Abbruchbedingung bestimmen
            GTAbbruchbedingung = Count(A, -1);
        }
    }


    // 1-Bit-Mutation
    void einbitmutation(int N, double MutProbability){
        
        for (int i = 0; i<N; i++) {
            double random = Zufallszahl();
            if (random<MutProbability){
                if (Allocation[i]==1){
                    Allocation[i]=0;
                } 
                else {
                    Allocation[i]=1;
                }
            }
        }
    }

    //Mixed Mutation
    void mixedmutation(int nOp, float MixMutProbability, int typeCoding){

        double random = Zufallszahl();
        if(random > MixMutProbability){
            int sectionStart = (int) round(Zufallszahl()*nOp,0);
            int sectionEnd = (int) round(Zufallszahl()*nOp,0);
            while (sectionEnd == sectionStart){
                sectionEnd = (int) round(Zufallszahl()*nOp,0);
            }
            if (sectionEnd < sectionStart){
                int temp = sectionStart;
                sectionStart = sectionEnd;
                sectionEnd = temp;
            }

            int[] tempArr = new int[sectionEnd-sectionStart];
            if (typeCoding == 0){
                System.arraycopy(Allocation, sectionStart, tempArr, 0, sectionEnd-sectionStart);
                List<Integer> tempList = new ArrayList();
                for (int k=0;k<tempArr.length;k++){
                    tempList.add(tempArr[k]);
                }
                Collections.shuffle(tempList);
                int[] MixedArr = tempList.stream().mapToInt(i->i).toArray();
                System.arraycopy(MixedArr, 0, Allocation, sectionStart, sectionEnd-sectionStart);
            }
            else if (typeCoding == 1){
                System.arraycopy(Sequence, sectionStart, tempArr, 0, sectionEnd-sectionStart);
                List<Integer> tempList = new ArrayList();
                for (int k=0;k<tempArr.length;k++){
                    tempList.add(tempArr[k]);
                }
                Collections.shuffle(tempList);
                int[] MixedArr = tempList.stream().mapToInt(i->i).toArray();
                System.arraycopy(MixedArr, 0, Sequence, sectionStart, sectionEnd-sectionStart);
            }
            else{
                System.out.println("Wrong Input for Coding Type of Mixed Mutation");
            }
        }
    }


    // Swap-Mutation
    void swapmutation(int N, float MutProbability){

        for (int i = 0; i<N; i++) {
            double random = Zufallszahl();
            if (random<MutProbability) {
                double random2 = round(Zufallszahl()*(N-1),0);
                int randomposition = (int)random2;
                int saveNumber = Sequence[randomposition];
                Sequence[randomposition] = Sequence[i];
                Sequence[i] = saveNumber;
            }
        }
    }

}