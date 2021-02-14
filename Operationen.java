package planningalgorithm;

public class Operationen {
    int Nummer;
    String opName;
    int[] Predecessor;
    //ArrayList<Integer> predecessor;
    int[] remainingPredecessors;
    int[] availableMachines;
    int[] timesProductionOnMachines;
    int timeStart;
    int timeWorking;
    int timeEnd;
    int workingMachine;
    int statusTemp; // -1 = Done, 0 = Not ready, 1 = ready to start
    boolean operationDone;
    boolean operationNotReady;
    boolean operationReadyToStart; 
    boolean readyToStart;   //A

    Operationen(int nOp, int nMa){
        operationDone = false;
        operationNotReady = true;
        operationReadyToStart = false; 
        availableMachines = new int[nMa];
        Predecessor = new int[nOp];
        remainingPredecessors = new int[nOp];
    }
}
