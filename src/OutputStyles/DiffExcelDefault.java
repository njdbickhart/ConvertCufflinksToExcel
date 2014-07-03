/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OutputStyles;

import FileFormats.CufflinksCoords;
import FileFormats.DiffArray;
import FormatStatics.HeaderFormats;
import FormatStatics.HighlightStyle;
import SetUtils.SortSetToList;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 *
 * @author bickhart
 */
public class DiffExcelDefault {    
    private final String[] coordheaders = {"LocName", "GeneID", "Chr", "Start", "End"};
    private final String[] diffheaders = {"Status", "Log2 FoldChange", "TestStatistic", "pValue", "Corrected pValue", "Significance?"};
    private final DiffArray data;
    private final HashMap<String, CellStyle> headerStyles;
    private final HashMap<String, CellStyle> highlightStyles;
    private final short[] headcolors = {
        IndexedColors.AQUA.getIndex(), 
        IndexedColors.BLUE.getIndex(), 
        IndexedColors.GREEN.getIndex(),
        IndexedColors.RED.getIndex(),
        IndexedColors.TAN.getIndex(),
        IndexedColors.YELLOW.getIndex(),
        IndexedColors.VIOLET.getIndex(),
        IndexedColors.BLUE_GREY.getIndex()};
    private boolean exclude = false;
    
    public DiffExcelDefault(DiffArray data){
        this.data = data;
        headerStyles = new HashMap<>(); 
        highlightStyles = new HashMap<>();
    }
    
    public void OutputDiffToExcel(String file, String skip){
        // Set boolean skip flag if zero values should be avoided
        if(skip.equals("true"))
            exclude = true;
        
        Workbook wb = null;
        try {
            // Set up workbook and create sheet
            wb = WorkbookFactory.create(new File(file));
        } catch (IOException | InvalidFormatException ex) {
            Logger.getLogger(DiffExcelDefault.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Get the information that we need from the diff file before proceeding
        TreeSet<String> sampleSet = new TreeSet<>(data.GetRpkmSamples());
        TreeSet<String> comparisonSet = new TreeSet<>(data.GetComp());
        TreeSet<String> locSet = new TreeSet<>(data.GetCoordLocs());
        
        // Create important styles
        CreateHeaderStyles(comparisonSet, wb);
        highlightStyles.put("yellow", HighlightStyle.YellowBoldHighlight(wb));
        Sheet sheet = GenerateSheetFromWb(wb);
        
        // Create spreadsheet header
        SetHeaderRow(sheet, sampleSet, comparisonSet);
        
        int row = 2;
        for(String l : locSet){
            
        }
    }
    
    private void CreateRowFromData(Sheet sheet, String loc, TreeSet<String> sampleSet, TreeSet<String> comparisonSet, int row){
        // Check if we need to exclude this row        
        CufflinksCoords<DiffArray.DiffData> working = this.data.GetCoordFromLoc(loc);
        Map<String, DiffArray.DiffData> diff = new HashMap<>();
        working.GetDataContainer().stream().map(e->diff.put(e.GetComp(),e));
        if(exclude){
            int x = sampleSet.stream().mapToInt((p) -> this.data.GetSampleRPKM(loc, p) == 0 ? 1 : 0).sum();
            if(x == sampleSet.size())
                return;
        }
        Row temp = sheet.createRow(row);
        Iterator<String> comps = comparisonSet.descendingIterator();
        String[] coord = working.ToStrArray();
        
        // Coordinate values
        for(int i = 0; i < coord.length; i++){
            Cell tcell = temp.createCell(i);
            tcell.setCellValue(coord[i]);
        }
        
        // RPKM values
        int col = coord.length;
        Iterator<String> samp = sampleSet.descendingIterator();
        for(int i = col; i < col + sampleSet.size(); i++){
            Cell tcell = temp.createCell(i);
            tcell.setCellValue(data.GetSampleRPKM(loc, samp.next()));
        }
        
        col += sampleSet.size();
        
        // Now for the comparison data
        for(int i = col; i < col + (comparisonSet.size() * 6); i +=6){
            String c = comps.next();
            DiffArray.DiffData d = diff.get(c);
            String[] values = d.GetStrArray();            
            for(int x = 0; x < 6; x++){
                Cell tcell = temp.createCell(x + i);
                tcell.setCellValue(values[x]);
                if(values[5].equals("yes")){
                    tcell.setCellStyle(this.highlightStyles.get("yellow"));
                }
            }
        }
    }
    
    private Sheet GenerateSheetFromWb(Workbook wb){
        Sheet sheet = wb.createSheet("diffdata");
        return sheet;
    } 
    
    private void CreateHeaderStyles(TreeSet<String> comparisonSet, Workbook wb){
        int num;
        if(comparisonSet.size() >= 8)
            num = 8;
        else
            num = comparisonSet.size();
        
        ArrayList<CellStyle> styles = new ArrayList<>();
        for(int i = 0; i < num; i++){
            CellStyle cell = HeaderFormats.CustomBoldCenterHeader(wb, this.headcolors[i]);
            styles.add(cell);
        }
        
        int op = 0;
        for(String s : comparisonSet){
            this.headerStyles.put(s, styles.get(op));
            op++;
            if(op >= 8)
                op = 0;
        }
        
        this.headerStyles.put("grey", HeaderFormats.GreyBoldCenterHeader(wb));
    }
    
    private void SetHeaderRow(Sheet sheet, TreeSet<String> sampleSet, TreeSet<String> comparisonSet){
        // Merged upper row
        Row FHeaderRow = sheet.createRow(0);
                
        FHeaderRow.setHeightInPoints(20f);
        Cell locCell = FHeaderRow.createCell(0);
        locCell.setCellValue("Gene and Location Data");
        locCell.setCellStyle(this.headerStyles.get("grey"));
        CellRangeAddress first = new CellRangeAddress(0,0,0,4);
        
        Cell sampCell = FHeaderRow.createCell(5);
        sampCell.setCellValue("Sample RPKM values");
        sampCell.setCellStyle(this.headerStyles.get("grey"));
        CellRangeAddress second = new CellRangeAddress(0,0,5,sampleSet.size());
        
        int col = 5 + sampleSet.size();
        for(String s : comparisonSet){
            Cell temp = FHeaderRow.createCell(col);
            col += 6;
            temp.setCellValue(s);
            temp.setCellStyle(this.headerStyles.get(s));
        }        
        CellRangeAddress third = new CellRangeAddress(0,0, 5 + sampleSet.size(), col - 6);
        
        sheet.addMergedRegion(first);
        sheet.addMergedRegion(second);
        sheet.addMergedRegion(third);
        
        // Non-merged second row
        Row SHeaderRow = sheet.createRow(1);
        
        for(int i = 0; i < this.coordheaders.length; i++){
            Cell temp = SHeaderRow.createCell(i);
            temp.setCellValue(coordheaders[i]);
            temp.setCellStyle(headerStyles.get("grey"));
        }
        Iterator<String> samps = sampleSet.descendingIterator();
        for(int i = coordheaders.length; i < sampleSet.size() + coordheaders.length; i++){
            Cell temp = SHeaderRow.createCell(i);
            temp.setCellValue(samps.next());
            temp.setCellStyle(headerStyles.get("grey"));
        }
        
        int op = 0;
        for(int i = coordheaders.length + sampleSet.size(); 
                i < coordheaders.length + sampleSet.size() + (comparisonSet.size() * 6); 
                i++){
            Cell temp = SHeaderRow.createCell(i);
            temp.setCellValue(this.diffheaders[op]);
            temp.setCellStyle(headerStyles.get("grey"));
            op++;
            if(op >= 6)
                op = 0;
        }
        
        // Freeze the top two panes
        sheet.createFreezePane(0, 2);
    }
}
