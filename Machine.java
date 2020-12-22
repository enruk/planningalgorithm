package planningalgorithm;

public class Machine {
    int Nummer;
    String Name;
    int timeOccupation;
    int BelegungszeitLastRun;
    int[] PlannedOperations;
    int[] Startzeiten;
    int[] Endzeiten;
    int[] Ganntplan;


    Machine(int Num, int BelegteZeit){
        Nummer = Num;
        timeOccupation = BelegteZeit;
    }


}
