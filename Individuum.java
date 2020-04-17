package planningalgorithm;

import java.util.Random;

public class Individuum {
    int Nummer;
    int[] Zuordnung;
    int[] Sequenz;
    int[] StartzeitenOp;
    int[] EndzeitenOp;
    int[] ProzesszeitenOp;
    int[][] VorgängerZeiten;
    int[][] StartzeitenMatrix;
    int Geburtsgeneration;

    //Konstruktor
    Individuum(int Num, int startgen, int AnzOp, int AnzMa){
        Nummer=Num;
        Geburtsgeneration=startgen;
        StartzeitenOp = new int[AnzOp];
        EndzeitenOp = new int[AnzOp];
        ProzesszeitenOp = new int[AnzOp];
        VorgängerZeiten = new int[AnzOp][AnzOp];
        StartzeitenMatrix = new int[AnzOp][AnzOp];
    }


    // Zufallszahl 
    private static double Zufallszahl(){
        double RanNum;
        Random zufallszahl = new Random();
        RanNum = zufallszahl.nextDouble();
        return RanNum;
    }

    private double round(double value, int decimalPoints) {
        double d = Math.pow(10, decimalPoints);
        return Math.round(value * d) / d;
     }

    private static int max(int[] FooArr) {
        int max = 0;
        for (int i=0;i<FooArr.length;i++) {
            if (FooArr[i] > max) {
                max = FooArr[i];
            }
        }
        return max;
    }

    public static int[] OneValue(int[] BarArr, int Value){
        int[] OneValueArr = new int[BarArr.length];
        for (int i=0;i<BarArr.length;i++){
            OneValueArr[i] = Value;
        }
        return OneValueArr;
    }
 
    public static int[] ChangeValue (int[] ArrArr, int OldValue, int NewValue){
        int[] NewArrArr = new int[ArrArr.length];
        for (int i=0;i<ArrArr.length;i++){
            if (ArrArr[i] == OldValue){
                ArrArr[i] = NewValue;
            }
        }
        return NewArrArr;
    }

    public static int Count (int[] CountArr, int Value){
        int CountDoku = 0;
        for (int i=0;i<CountArr.length;i++){
            if (CountArr[i]==Value){
                CountDoku++;
            }
        }
        return CountDoku;
    }




    // Methoden

    void decodierung(int AnzOp,int AnzMa, int[][] Vorrangmatrix, int[][] Maschinenzeiten){
        
        // VORBEREITUNG
        //Maschinenmatrix bestimmen aus Zuordnung und Maschinenzeiten und Maschinenzeit in Matrix der Vorgänger-Prozess-Zeiten eintragen
        for (int z=0;z<AnzOp;z++){
            int WorkingMachine = Zuordnung[z]-1;
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





        // StartzeitenMatrix bestimmen
        int[] AnfangsOp = new int[AnzOp];
        int[][] VorgängerOp = Vorrangmatrix;

        for (int z=0;z<AnzOp;z++){
            if (max(VorgängerZeiten[z])<0.1){
                AnfangsOp[z]=1;
            }
        }

        int OperationsDone = 0;
        while (OperationsDone < AnzOp){

            //Startzeiten einer Operation: t = Startzeit des Vorgängers und Prozesszeit des Vorgängers (steht in VorgängerZeiten schon drin)
            for (int z=0;z<AnzOp;z++){
                if (AnfangsOp[z] == 1){
                    int FoundOp = z;
                    for (int s=0;s<AnzOp;s++){
                        if (VorgängerZeiten[FoundOp][s] !=0){
                            StartzeitenMatrix[FoundOp][s] = max(VorgängerZeiten[FoundOp]) + VorgängerZeiten[FoundOp][s];
                            VorgängerOp[FoundOp][s] = 0; // FoundOp gilt als ausgeführt und ist daher nicht länger Vorgänger der Operation in Spalte s
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

    }







    // 1-Bit-Mutation
    void einbitmutation(int N){
        
        for (int i = 0; i<N; i++) {
            double random = Zufallszahl();
            System.out.println(random);
            if (random<0.5) {
                if (Zuordnung[i]==1){
                    Zuordnung[i]=0;
                } 
                else {
                    Zuordnung[i]=1;
                }
            }
        }
    }



    void swapmutation(int N){

        for (int i = 0; i<N; i++) {
            double random = Zufallszahl();
            System.out.println(random);
            if (random<0.2) {
                double random2 = round(Zufallszahl()*(N-1),0);
                int randomposition = (int)random2;
                System.out.println(randomposition);
                int saveNumber = Sequenz[randomposition];
                Sequenz[randomposition] = Sequenz[i];
                Sequenz[i] = saveNumber;
            }
        }
    }


}