package planningalgorithm;

public class Machine {
    int Nummer;
    String Name;
    int Belegungszeit;
    int BelegungszeitLastRun;
    int[] PlannedOperations;
    int[] Startzeiten;
    int[] Endzeiten;
    int[] Ganntplan;


    Machine(int Num, int BelegteZeit){
        Nummer = Num;
        Belegungszeit = BelegteZeit;
    }


}
