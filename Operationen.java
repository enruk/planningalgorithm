package planningalgorithm;

import java.util.ArrayList;

public class Operationen {
    int Nummer;
    String opName;
    int[] Predecessor;
    //ArrayList<Integer> predecessor;
    int[] remainingPredecessors;
    int[] Machines;
    int[] Bearbeitungszeit;
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
        Machines = new int[nMa];
        Predecessor = new int[nOp];
    }

}
