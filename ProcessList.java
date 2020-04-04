package planningalgorithm;

import java.util.List;
import java.util.ArrayList;

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

    // bislang leere Klasse mit Hauptmethode ReadoutExcel

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


    public List<Operationen> ReadoutExcel() throws IOException {

        FileInputStream inputStream = new FileInputStream(new File("C:/Users/Henrik/OneDrive/Java Projekte/Prozess1.xls"));
        HSSFWorkbook excelmappe = new HSSFWorkbook(inputStream);
        HSSFSheet tabelle = excelmappe.getSheetAt(0);

        // Spalten mit Operationsattributen suchen
        int Erstezeile = tabelle.getFirstRowNum();
        int ZeileNr = findinRow(Erstezeile,"Nr.",tabelle);
        int ZeileName = findinRow(Erstezeile,"Beschreibung",tabelle);
        //int ZeileVor = findinRow(Erstezeile,"Vorgänger",tabelle);
        //int ZeileNach = findinRow(Erstezeile,"Nachfolger",tabelle);


        // Anzahl der gesamten Operationen suchen
        int AnzOp=0;
        Iterator<Row> rowIterator = tabelle.iterator();

        while (rowIterator.hasNext()) {
            Row Zeile = rowIterator.next();
            Cell CellNr = Zeile.getCell(0);
            CellType cellType = CellNr.getCellTypeEnum();
            if (cellType == CellType.NUMERIC){
                AnzOp = AnzOp + 1;
            }
        }


        // Leere Operationen erstellen
        List<Operationen> OperationenListe = new ArrayList<Operationen>(AnzOp);
        for (int i=0;i<AnzOp;i++) {
            Operationen Op = new Operationen();
            OperationenListe .add(Op);
        }

        
        Iterator<Row> rowIterator2 = tabelle.iterator();
        Iterator<Operationen>  OpListIterator =  OperationenListe.iterator();

        while (rowIterator2.hasNext()) {
            Row Zeile = rowIterator2.next();
            Cell CellNr = Zeile.getCell(ZeileNr);
            Cell CellName = Zeile.getCell(ZeileName);

            CellType cellType = CellNr.getCellTypeEnum();

            if (cellType == CellType.NUMERIC){
                int OpNummer = (int)CellNr.getNumericCellValue();
                String OpName = CellName.toString();

                
                Operationen CurrentOp = OpListIterator.next();
                CurrentOp.Nummer = OpNummer;
                CurrentOp.Operationsname = OpName;
            }
            //Operationeniterator++;
        }
        excelmappe.close();
        System.out.println(OperationenListe.get(6).Operationsname);
        return (OperationenListe);
    }
}