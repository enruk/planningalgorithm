package planningalgorithm;

import java.util.Random;

public class Individuum {
    int Nummer;
    int[] Zuordnung;
    int[] Sequenz;
    int[] Startzeiten;
    int[] Prozesszeiten;
    int[] Endzeiten;
    int Generation;

    //Konstruktor
    Individuum(int Num, int startgen){
        Nummer=Num;
        Generation=startgen;
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



    // Methoden

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



    void nextgen(){
        Generation++;
    }

}