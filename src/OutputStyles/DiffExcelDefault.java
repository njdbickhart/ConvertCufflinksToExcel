/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OutputStyles;

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
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

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
        CreateHeaderStyles(sampleSet, wb);
        highlightStyles.put("yellow", HighlightStyle.YellowBoldHighlight(wb));
        
        
        // Create spreadsheet header
        
        locSet.stream().map(null);
    }
    
    private Sheet GenerateSheetFromWb(Workbook wb){
        Sheet sheet = wb.createSheet("diffdata");
        return sheet;
    } 
    
    private void CreateHeaderStyles(TreeSet<String> sampleSet, Workbook wb){
        int num;
        if(sampleSet.size() >= 8)
            num = 8;
        else
            num = sampleSet.size();
        
        ArrayList<CellStyle> styles = new ArrayList<>();
        for(int i = 0; i < num; i++){
            CellStyle cell = HeaderFormats.CustomBoldCenterHeader(wb, this.headcolors[i]);
            styles.add(cell);
        }
        
        int op = 0;
        for(String s : sampleSet){
            this.headerStyles.put(s, styles.get(op));
            op++;
            if(op >= 8)
                op = 0;
        }
        
        this.headerStyles.put("grey", HeaderFormats.GreyBoldCenterHeader(wb));
    }
    
    private void SetHeaderRow(Sheet sheet, TreeSet<String> sampleSet, TreeSet<String> comparisonSet){
        
    }
}
