package planningalgorithm;

import java.util.List;
import java.util.ArrayList;
//import java.util.Arrays;

import java.io.File;
import java.io.FileInputStream;
//import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

public class ProcessList {

    // Klassenattribute
    int AnzOp;
    double[][] Präzedenzmatrix;
    double[][] Maschinenmatrix;
    List<Operationen> OperationenListe; 


    // Konstruktor
    ProcessList(){
        AnzOp = 0;
    }


    
    // Methoden
    
    // find in Zeile
    public static int findinRow(int zeilennummer, String zelleninhalt, HSSFSheet tabelle){
        Row Zeile = tabelle.getRow(zeilennummer);
        int LetzteSpalte = Zeile.getLastCellNum();
        int gesuchtespalte = 0;
        for (int i=0;i<LetzteSpalte+1;i++){
            Cell Zelle = Zeile.getCell(i);
            String cellstring = Zelle.toString();
            if (cellstring.equals(zelleninhalt)){
                gesuchtespalte = i;
                break;
            }
        }
        return gesuchtespalte;
    }


    // Liste der Operationen erstellen mit Daten aus Excel gefüttert
    void ReadoutExcel(int AnzMa) throws IOException {

        FileInputStream inputStream = new FileInputStream(new File("C:/Users/Henrik/OneDrive/Java Projekte/Prozess1.xls"));
        HSSFWorkbook excelmappe = new HSSFWorkbook(inputStream);
        HSSFSheet tabelle = excelmappe.getSheetAt(0);


        // Spalten mit Operationsattributen suchen
        int Erstezeile = tabelle.getFirstRowNum();
        int ZeileNr = findinRow(Erstezeile,"Nr.",tabelle);
        int ZeileName = findinRow(Erstezeile,"Beschreibung",tabelle);
        int ZeileVor = findinRow(Erstezeile,"Vorgänger",tabelle);
        int ZeileM1 = findinRow(Erstezeile,"Maschine 1",tabelle);
        //int ZeileNach = findinRow(Erstezeile,"Nachfolger",tabelle);


        // Anzahl der gesamten Operationen suchen
        //int AnzOp=0;
        Iterator<Row> rowIterator = tabelle.iterator();

        while (rowIterator.hasNext()) {
            Row Zeile = rowIterator.next();
            Cell CellNr = Zeile.getCell(0);
            CellType cellType = CellNr.getCellTypeEnum();
            if (cellType == CellType.NUMERIC){
                AnzOp = AnzOp + 1;
            }
        }
        

        // Arrayliste entsprechend Anzahl der Operationen erstellen
        OperationenListe = new ArrayList<Operationen>(AnzOp);
        //List<Operationen> OperationenListe = new ArrayList<Operationen>(AnzOp);
        for (int i=0;i<AnzOp;i++) {
            Operationen Op = new Operationen();
            OperationenListe .add(Op);
        }

        
        Iterator<Row> rowIterator2 = tabelle.iterator();
        Iterator<Operationen>  OpListIterator =  OperationenListe.iterator();

        
        while (rowIterator2.hasNext()) {
            Row Zeile = rowIterator2.next();    // Zeile mit RowIterator hochzählen

            
            // Nummer
            
            Cell CellNr = Zeile.getCell(ZeileNr);
            CellType TypeCellNr = CellNr.getCellTypeEnum();
            // dat geht alles nur wenn dat auch ne Operation ist mit ner Nummer
            if (TypeCellNr == CellType.NUMERIC){
                Operationen CurrentOp = OpListIterator.next();  // Operationen in OperationenListe hochzählen 
                CurrentOp.Nummer = (int)CellNr.getNumericCellValue();

                // Name oder Beschreibung
                Cell CellName = Zeile.getCell(ZeileName);
                CurrentOp.Operationsname = CellName.toString();

                // Vorgänger
                Cell CellVor = Zeile.getCell(ZeileVor);
                String OpVor = CellVor.toString();
                String[] StringVor = OpVor.split(";");
                int[] VorgängerArray = new int[StringVor.length];
                for (int i=0;i<StringVor.length;i++){
                    double VorDouble = Double.parseDouble(StringVor[i]);
                    VorgängerArray[i] = (int)VorDouble;
                }
                CurrentOp.Vorgänger = VorgängerArray;


                // Maschinen und Bearbeitungszeit auslesen  
                CurrentOp.Bearbeitungszeit = new int[AnzMa];
                CurrentOp.Maschinen = new int[AnzMa];

                int AnzMaschinenOp = 0;
                for (int MaIterator = 0;MaIterator<AnzMa;MaIterator++){
                    Cell CellMaschine = Zeile.getCell(ZeileM1+MaIterator);
                    int ZeitMaschine = (int)CellMaschine.getNumericCellValue();
                    //int [] BearbeitungsMaschinen;
                    //int [] Bearbeitungs
                    if (ZeitMaschine == 0){
                        CurrentOp.Bearbeitungszeit[MaIterator] = ZeitMaschine;
                    }
                    else{
                        CurrentOp.Bearbeitungszeit[MaIterator] = ZeitMaschine;
                        CurrentOp.Maschinen[AnzMaschinenOp] = MaIterator;
                        AnzMaschinenOp++;
                    }

                }
                
            }
 
            //Operationeniterator++;
        }
        excelmappe.close();
        System.out.println(OperationenListe.get(6).Operationsname);




        // Präzedenzmatrix aus Operationenliste erstellen
        Präzedenzmatrix = new double[AnzOp][AnzOp];
        for (int i=0;i<AnzOp;i++){
            for (int j=0;j<OperationenListe.get(i).Vorgänger.length;j++){
            int VorProzess = OperationenListe.get(i).Vorgänger[j];
                if (VorProzess != 0){
                Präzedenzmatrix[i][VorProzess-1] = 1;
                }
            } 
        }


        // Maschinenmatrix aus OperationenListe 
        Maschinenmatrix = new double[AnzOp][AnzOp];
        for (int i=0;i<AnzOp;i++){
            for (int j=0;j<AnzMa;j++){
                Maschinenmatrix[i][j] = OperationenListe.get(i).Bearbeitungszeit[j];
            }
        }


    }

 
}