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
    int nOp;
    int[][] Präzedenzmatrix;
    int[][] Maschinenmatrix;
    List<Operationen> processInformation; // Umbennen in generellProcess, processInformation, 


    // Konstruktor
    ProcessList(){
        nOp = 0;
    }


    
    // Methoden
    
    // find in Zeile
    public static int findinRow(int rowNo, String contant, HSSFSheet table){
        Row row = table.getRow(rowNo);
        int lastColumn = row.getLastCellNum();
        int wantedColumn = 0;
        for (int i=0;i<lastColumn+1;i++){
            Cell cell = row.getCell(i);
            String cellstring = cell.toString();
            if (cellstring.equals(contant)){
                wantedColumn = i;
                break;
            }
        }
        return wantedColumn;
    }


    // Get all necessory informations from excel sheet
    void ReadoutExcel(int nMa) throws IOException {

        FileInputStream inputStream = new FileInputStream(new File("C:/Users/User/Documents/ExampleProject/Prozess1.xls"));
        HSSFWorkbook excelmappe = new HSSFWorkbook(inputStream);
        HSSFSheet table = excelmappe.getSheetAt(0);


        // Seach for Columns with Information
        int fistRow = table.getFirstRowNum();
        int rowNumber = findinRow(fistRow,"No.",table);
        int rowName = findinRow(fistRow,"Description",table);
        int rowPre = findinRow(fistRow,"Predecessor",table);
        int rowM1 = findinRow(fistRow,"Machine 1",table);
        //int ZeileNach = findinRow(Erstezeile,"Nachfolger",tabelle);


        // Anzahl der gesamten Operationen suchen
        //int AnzOp=0;
        Iterator<Row> rowIterator = table.iterator();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Cell CellNo = row.getCell(0);
            CellType cellType = CellNo.getCellTypeEnum();
            if (cellType == CellType.NUMERIC){
                nOp = nOp + 1;
            }
        }
        

        // Create List for information
        processInformation = new ArrayList<>(nOp);
        for (int i=0;i<nOp;i++) {
            Operationen Op = new Operationen(nOp,nMa);
            processInformation .add(Op);
        }

        
        Iterator<Row> rowIterator2 = table.iterator();
        Iterator<Operationen>  OpListIterator =  processInformation.iterator();

        
        while (rowIterator2.hasNext()) {
            Row row = rowIterator2.next();   

            
            // Nummer
            Cell CellNr = row.getCell(rowNumber);
            CellType TypeCellNr = CellNr.getCellTypeEnum();
            if (TypeCellNr == CellType.NUMERIC){
                Operationen CurrentOp = OpListIterator.next();
                CurrentOp.Nummer = (int)CellNr.getNumericCellValue();

                // Name
                Cell CellName = row.getCell(rowName);
                CurrentOp.opName = CellName.toString();

                // Predecessor
                Cell CellVor = row.getCell(rowPre);
                String OpVor = CellVor.toString();
                String[] StringVor = OpVor.split(";");
                int[] VorgängerArray = new int[StringVor.length];
                for (int i=0;i<StringVor.length;i++){
                    double VorDouble = Double.parseDouble(StringVor[i]);
                    VorgängerArray[i] = (int)VorDouble;
                }
                CurrentOp.Predecessor = VorgängerArray;


                // Productiontime 
                CurrentOp.timesProductionOnMachines = new int[nMa];
                CurrentOp.availableMachines = new int[nMa];

                for (int MaIterator = 0;MaIterator<nMa;MaIterator++){
                    Cell CellMaschine = row.getCell(rowM1+MaIterator);
                    int ZeitMaschine = (int)CellMaschine.getNumericCellValue();
                    if (ZeitMaschine == 0){
                        CurrentOp.timesProductionOnMachines[MaIterator] = ZeitMaschine;
                    }
                    else{
                        CurrentOp.timesProductionOnMachines[MaIterator] = ZeitMaschine;
                        CurrentOp.availableMachines[MaIterator] = 1;
                    }

                }
                
            }

        }
        excelmappe.close();




        // Make Präzedenzmatrix
        Präzedenzmatrix = new int[nOp][nOp];
        for (int i=0;i<nOp;i++){
            for (int j=0;j<processInformation.get(i).Predecessor.length;j++){
            int VorProzess = processInformation.get(i).Predecessor[j];
                if (VorProzess != 0){
                Präzedenzmatrix[i][VorProzess-1] = 1;
                }
            } 
        }


        // Maschinenmatrix aus OperationenListe 
        Maschinenmatrix = new int[nOp][nOp];
        for (int i=0;i<nOp;i++){
            for (int j=0;j<nMa;j++){
                Maschinenmatrix[i][j] = processInformation.get(i).timesProductionOnMachines[j];
            }
        }
    }
}