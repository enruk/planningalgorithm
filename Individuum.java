package planningalgorithm;

import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Individuum {
    int Nummer;
    int Geburtsgeneration;

    int[] Zuordnung;
    int[] Sequenz;
    int[] StartzeitenOp; // Unnötig mit Liste Prozess, evtl nur für Funkton wie max einfacher
    int[] EndzeitenOp; // Unnötig mit Liste Prozess
    int[] ProzesszeitenOp; // Unnötig mit Liste Prozess
    int[][] VorgängerZeiten;
    int[][] StartzeitenMatrix;
    float TimeFitness;
    int SUSRank;
    int TournamentWins;

    List<Machine> Machines;
    List<Operationen> Prozess;


    //Konstruktor
    Individuum(int Num, int startgen, int AnzOp, int AnzMa){
        Nummer=Num;
        Geburtsgeneration=startgen;
        Zuordnung = new int[AnzOp];
        Sequenz = new int[AnzOp];
        StartzeitenOp = new int[AnzOp];
        EndzeitenOp = new int[AnzOp];
        ProzesszeitenOp = new int[AnzOp];
        VorgängerZeiten = new int[AnzOp][AnzOp];
        StartzeitenMatrix = new int[AnzOp][AnzOp+1];
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

    public int[][] CopyArray (int[][] Arr) {
        int[][] Copy = new int[Arr.length][Arr[0].length];
        for (int i = 0; i < Arr.length; i++) {
            for (int j = 0; j < Arr[0].length; j++) {
                Copy[i][j] = Arr[i][j];
            }
        }
        return Copy;
    }
    




    // Methoden
    // Decodierung
    void decodierung(int AnzOp,int AnzMa, int[][] Vorrangmatrix, int[][] Maschinenzeiten){

        // Liste der Maschinen erstellen
        Machines = new ArrayList<Machine>(AnzMa);
        for (int i=0;i<AnzMa;i++) {
            Machine MachineX = new Machine(i,0);
            Machines .add(MachineX);
        }

        // Liste der Prozesse erstellen
        Prozess = new ArrayList<Operationen>(AnzOp);
        for (int i=0;i<AnzOp;i++) {
            Operationen Ops = new Operationen();
            Prozess .add(Ops);
        }



        // VORBEREITUNG
        //Prozesszeitenmatrix bestimmen aus Zuordnung und Maschinenzeiten und Maschinenzeit in Matrix der Vorgänger-Prozess-Zeiten eintragen
        for (int z=0;z<AnzOp;z++){
            int WorkingMachine = Zuordnung[z];
            ProzesszeitenOp[z] = Maschinenzeiten[z][WorkingMachine]; 
        }
    

        for (int z=0;z<AnzOp;z++){
            for (int s=0;s<AnzOp;s++){
                if  (Vorrangmatrix[z][s]==1){
                    VorgängerZeiten[z][s] = ProzesszeitenOp[s];
                }
                else{
                    VorgängerZeiten[z][s] = 0;
                }
            }
        }



        // Liste der Maschinen erstellen
        Machines = new ArrayList<Machine>(AnzMa);
        for (int i=0;i<AnzMa;i++) {
            Machine MachineX = new Machine(i,0);
            Machines .add(MachineX);
        }

        Prozess = new ArrayList<Operationen>(AnzOp);
        for (int i=0;i<AnzOp;i++) {
            Operationen Ops = new Operationen();
            Prozess .add(Ops);
         }

        // Optional: Ist das hier wirklich notwendig?
        for (int i=0;i<AnzOp;i++) {
            Prozess.get(i).Prozesszeit = ProzesszeitenOp[i];
            Prozess.get(i).WorkingMachine = Zuordnung[i];
        }



        // StartzeitenMatrix bestimmen
        int[] AnfangsOp = new int[AnzOp];
        int[][] VorgängerOp = CopyArray(Vorrangmatrix);


        for (int z=0;z<AnzOp;z++){
            if (max(VorgängerZeiten[z])<0.1){
                AnfangsOp[z]=1;
            }
        }

        int OperationsDone = 0;
        while (OperationsDone < AnzOp){

            //Startzeiten einer Operation: t = Startzeit des Vorgängers und Prozesszeit des Vorgängers (steht in VorgängerZeiten schon drin)
            for (int z=0;z<AnzOp;z++){
                int FoundOp = 0;
                if (AnfangsOp[z] == 1){
                    FoundOp = z;
                    for (int z2=0;z2<AnzOp;z2++){
                        if (VorgängerZeiten[z2][FoundOp] !=0){
                            StartzeitenMatrix[z2][FoundOp] = max(StartzeitenMatrix[FoundOp]) + VorgängerZeiten[z2][FoundOp];
                        }
                    }

                for (int z3=0;z3<AnzOp;z3++){
                    if (VorgängerOp[z3][FoundOp] == 1){
                        VorgängerOp[z3][FoundOp] = 0; // FoundOp gilt als ausgeführt und ist daher nicht länger Vorgänger der Operation in Zeile s
                    }
                    
                }

                VorgängerOp[FoundOp] = OneValue(VorgängerOp[FoundOp],-1);
                AnfangsOp[FoundOp] = -1;

                }
            }

            //Anfangsoperationen neu bestimmen
            for (int z=0;z<AnzOp;z++){
                if (max(VorgängerOp[z])==0){
                    AnfangsOp[z] = 1;
                }
                if (max(VorgängerOp[z])==-1){
                    AnfangsOp[z] = -1;
                }
                if (max(VorgängerOp[z]) == 1){
                    AnfangsOp[z] = 0;
                }
            }


            //Abbruchbedingungen ermitteln
            OperationsDone = Count(AnfangsOp, -1);
        }


        //Fertigungszeiten berechnen
        int[] Fertigungszeiten = new int[AnzOp];
        for (int z=0;z<AnzOp;z++){
            Fertigungszeiten[z] = max(StartzeitenMatrix[z]) + ProzesszeitenOp[z];
        }


        int[] A = new int[AnzOp];
        int[][] V = CopyArray(Vorrangmatrix);
        int[] B = new int[AnzOp];
        int OperationStrich;
        int OperationStrichStrich;
        int OperationStern;


        for (int z=0;z<AnzOp;z++){
            if (max(Vorrangmatrix[z]) < 0.1)
                A[z] = 1;
            else{
                A[z] = 0; 
            }
        }
        

        // Beginn GT Algorithmus
        int GTAbbruchbedingung = 0;
        while (GTAbbruchbedingung < AnzOp){
            OperationStrich = 0;
            OperationStrichStrich = 0; 
            OperationStern = 0;

            // GT - Schritt 2.H1: Ermittlung von O'
            int FruhsteFertigungszeit = 1000;
            for (int z=0;z<AnzOp;z++){
                if (A[z] == 1 && Fertigungszeiten[z]< FruhsteFertigungszeit){
                     OperationStrich = z;
                     FruhsteFertigungszeit = Fertigungszeiten[z];
                }
            }

            // GT - Schritt 2.H2: Menge B bestimmen
            // Ermitteln der Resource / Maschine
            int CurrentMachine = Zuordnung[OperationStrich];

            for (int z=0;z<AnzOp;z++){
                if (A[z]==1 && Zuordnung[z]==CurrentMachine){
                    B[z]=1;
                }
                else{
                    B[z] = 0;
                }
            }
            
            // GT - Schritt 2.H3: Ermittlung von OperationStrichStrich
            int FruhsteStartzeit = 1000;
            for (int z=0;z<AnzOp;z++){
                if (B[z]==1 && max(StartzeitenMatrix[z])<FruhsteStartzeit){
                    OperationStrichStrich = z;
                    FruhsteStartzeit = max(StartzeitenMatrix[z]);
                }
            }

            // GT - Schritt 2.H4: Operationen außerhalb des Zeitfensters entfernen

            int Belegungszeit = Machines.get(CurrentMachine).Belegungszeit;
            int Startzeit;
            if (max(StartzeitenMatrix[OperationStrichStrich])<Belegungszeit){
                Startzeit = Belegungszeit;
            }
            else{
                Startzeit = max(StartzeitenMatrix[OperationStrichStrich]);
            } 
            
            double sigma = 0.5;

            for (int z=0;z<AnzOp;z++){
                if (B[z]==1){
                    if (max(StartzeitenMatrix[z]) > (Startzeit + sigma * (Fertigungszeiten[OperationStrich] - Startzeit))){
                        B[z] = 0;
                    }
                }
            }

            //GT - Schritt 2.H5: OperationStern ermitteln

            int Permutation = 1000;
            for (int z=0;z<AnzOp;z++){
                if (B[z]==1 && Sequenz[z]<Permutation){
                    Permutation = Sequenz[z];
                    OperationStern = z;
                }
            }

            //OperationStern aus A löschen
            A[OperationStern] = -1;
            for (int z=0;z<AnzOp;z++){
                if (A[z] != -1){
                    V[z][OperationStern] = 0;
                }
            }
            V[OperationStern] = OneValue(V[OperationStern], -1);

            if (max(StartzeitenMatrix[OperationStern]) < Belegungszeit){
                StartzeitenOp[OperationStern] = Belegungszeit;
            }
            if (max(StartzeitenMatrix[OperationStern]) >= Belegungszeit){
                StartzeitenOp[OperationStern] = max(StartzeitenMatrix[OperationStern]);
            }

            EndzeitenOp[OperationStern] = StartzeitenOp[OperationStern] + ProzesszeitenOp[OperationStern];

            Machines.get(CurrentMachine).Belegungszeit = EndzeitenOp[OperationStern];

            // GT - Schritt 4: Nachfolger von O* zu A hinzufügen

            for (int z=0;z<AnzOp;z++){

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

            // GT - Schritt 5: Belegungszeiten aktualisieren
            for (int z=0;z<AnzOp;z++){
                if (A[z] != -1 && Zuordnung[z] == CurrentMachine){
                    StartzeitenMatrix[z][AnzOp] = EndzeitenOp[OperationStern];
                }
            }


            // 
            // StartzeitenMatrix bestimmen
            AnfangsOp = OneValue(AnfangsOp, 0);
            VorgängerOp = CopyArray(Vorrangmatrix);

            for (int z=0;z<AnzOp;z++){
                if (max(VorgängerZeiten[z])<0.1){
                    AnfangsOp[z]=1;
                }
            }
        

            OperationsDone = 0;
            while (OperationsDone < AnzOp){

                //Startzeiten einer Operation: t = Startzeit des Vorgängers und Prozesszeit des Vorgängers (steht in VorgängerZeiten schon drin)
                for (int z=0;z<AnzOp;z++){
                    int FoundOp = 0;
                    if (AnfangsOp[z] == 1){
                        FoundOp = z;
                        for (int z2=0;z2<AnzOp;z2++){
                            if (VorgängerZeiten[z2][FoundOp] !=0){
                                StartzeitenMatrix[z2][FoundOp] = max(StartzeitenMatrix[FoundOp]) + VorgängerZeiten[z2][FoundOp];
                            }
                        }

                    for (int z3=0;z3<AnzOp;z3++){
                        if (VorgängerOp[z3][FoundOp] == 1){
                            VorgängerOp[z3][FoundOp] = 0; // FoundOp gilt als ausgeführt und ist daher nicht länger Vorgänger der Operation in Zeile s
                        }
                        
                    }

                    VorgängerOp[FoundOp] = OneValue(VorgängerOp[FoundOp],-1);
                    AnfangsOp[FoundOp] = -1;

                    }
                }

                //Anfangsoperationen neu bestimmen
                for (int z=0;z<AnzOp;z++){
                    if (max(VorgängerOp[z])==0){
                        AnfangsOp[z] = 1;
                    }
                    if (max(VorgängerOp[z])==-1){
                        AnfangsOp[z] = -1;
                    }
                    if (max(VorgängerOp[z]) == 1){
                        AnfangsOp[z] = 0;
                    }
                }


                //Abbruchbedingungen ermitteln
                OperationsDone = Count(AnfangsOp, -1);
            }


            //Fertigungszeiten berechnen
            for (int z=0;z<AnzOp;z++){
                Fertigungszeiten[z] = max(StartzeitenMatrix[z]) + ProzesszeitenOp[z];
            }

            //Give the working Machine some Information about the Operation
            Machines.get(CurrentMachine).PlannedOperations = AddOneToArray(Machines.get(CurrentMachine).PlannedOperations, OperationStern);
            Machines.get(CurrentMachine).Startzeiten = AddOneToArray(Machines.get(CurrentMachine).Startzeiten, StartzeitenOp[OperationStern]);
            Machines.get(CurrentMachine).Endzeiten = AddOneToArray(Machines.get(CurrentMachine).Endzeiten, EndzeitenOp[OperationStern]);

            // Abbruchbedingung bestimmen
            GTAbbruchbedingung = Count(A, -1);
        }

        for (int i=0;i<AnzOp;i++){
            Prozess.get(i).Startzeit = StartzeitenOp[i];
            Prozess.get(i).Endzeit = EndzeitenOp[i];
        }

    }


    // 1-Bit-Mutation
    void einbitmutation(int N, double MutProbability){
        
        for (int i = 0; i<N; i++) {
            double random = Zufallszahl();
            if (random<MutProbability){
                if (Zuordnung[i]==1){
                    Zuordnung[i]=0;
                } 
                else {
                    Zuordnung[i]=1;
                }
            }
        }
    }

    //Mixed Mutation
    void mixedmutation(int nOp, double MixMutProbability, int typeCoding){

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
                System.arraycopy(Zuordnung, sectionStart, tempArr, 0, sectionEnd-sectionStart);
                List<Integer> tempList = new ArrayList();
                for (int k=0;k<tempArr.length;k++){
                    tempList.add(tempArr[k]);
                }
                Collections.shuffle(tempList);
                int[] MixedArr = tempList.stream().mapToInt(i->i).toArray();
                System.arraycopy(MixedArr, 0, Zuordnung, sectionStart, sectionEnd-sectionStart);
            }
            else if (typeCoding == 1){
                System.arraycopy(Sequenz, sectionStart, tempArr, 0, sectionEnd-sectionStart);
                List<Integer> tempList = new ArrayList();
                for (int k=0;k<tempArr.length;k++){
                    tempList.add(tempArr[k]);
                }
                Collections.shuffle(tempList);
                int[] MixedArr = tempList.stream().mapToInt(i->i).toArray();
                System.arraycopy(MixedArr, 0, Sequenz, sectionStart, sectionEnd-sectionStart);
            }
            else{
                System.out.println("Wrong Input for Coding Type of Mixed Mutation");
            }
        }
    }


    // Swap-Mutation
    void swapmutation(int N, double MutProbability){

        for (int i = 0; i<N; i++) {
            double random = Zufallszahl();
            if (random<0.2) {
                double random2 = round(Zufallszahl()*(N-1),0);
                int randomposition = (int)random2;
                int saveNumber = Sequenz[randomposition];
                Sequenz[randomposition] = Sequenz[i];
                Sequenz[i] = saveNumber;
            }
        }
    }

}