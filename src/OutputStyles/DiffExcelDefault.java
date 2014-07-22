/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OutputStyles;

import FileFormats.CufflinksCoords;
import FileFormats.DiffArray;
import FileFormats.DiffData;
import FormatStatics.HeaderFormats;
import FormatStatics.HighlightStyle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 *
 * @author bickhart
 */
public class DiffExcelDefault {    
    private final String[] coordheaders = {"LocName", "GeneID", "Chr", "Start", "End"};
    private final String[] diffheaders = {"Status", "Log2 FoldChange", "TestStatistic", "pValue", "Corrected pValue", "Significance?"};
    private DiffArray data;
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
    
    /**
     * The constructor for the class
     * @param data A DiffArray class that has been previously generated
     */
    public DiffExcelDefault(DiffArray data){
        this.data = data;
        headerStyles = new HashMap<>(); 
        highlightStyles = new HashMap<>();
    }
    
    /**
     * The output generating class. Creates an excel file at the path indicated
     * by the String, file
     * @param file The String representation of the output path location
     * @param skip A string "true or false" value derived from my command line
     * option interpreter class.
     */
    public void OutputDiffToExcel(String file, String skip){
        // Set boolean skip flag if zero values should be avoided
        if(skip.equals("true"))
            exclude = true;
        
        Workbook wb = new SXSSFWorkbook(1000);
        
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
        
        // I think that to minimize the memory overhead, I'm going to have to create 
        // a tab delimited text file and read that to generate the excel workbook
        String[] base = file.split("\\.");
        String outTab = base[0] + ".tab";
        try(BufferedWriter out = Files.newBufferedWriter(Paths.get(outTab), Charset.defaultCharset())){
            CreateTabFileFromData(out, sampleSet, comparisonSet, locSet);
            // Dereferencing for garbage collection
            this.data = null;
        }catch(IOException ex){
            ex.printStackTrace();
        }
        
        try(BufferedReader in = Files.newBufferedReader(Paths.get(outTab), Charset.defaultCharset())){
            String line = null;
            int row = 2;
            while((line = in.readLine()) != null){
                CreateRowFromTab(line, sampleSet, sheet, row);
                row++;
                if(row % 1000 == 0){
                    System.err.print("[DIFF EXCEL] Finished with row: " + row + "\r");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DiffExcelDefault.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.err.print(System.lineSeparator());
        System.err.println("[DIFF EXCEL] Printing to output file!");
        /*int row = 2;
        for(String l : locSet){
        CreateRowFromData(sheet, l, sampleSet, comparisonSet, row);
        row++;
        }*/
        
        // Freeze the top two panes
        sheet.createFreezePane(0, 2);
        
        try(FileOutputStream out = new FileOutputStream(file)){
            wb.write(out);
            out.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
        SXSSFWorkbook b = (SXSSFWorkbook) wb;
        b.dispose();
    }
    
    private void CreateTabFileFromData(BufferedWriter out, TreeSet<String> sampleSet, TreeSet<String> comparisonSet, TreeSet<String> locSet) throws IOException{
        String nl = System.lineSeparator();
        for(String loc : locSet){
            // Check if we need to exclude this row        
            CufflinksCoords<DiffData> working = this.data.GetCoordFromLoc(loc);
            Map<String, DiffData> diff = new HashMap<>();
            //working.GetDataContainer().stream().map((e)->diff.put(e.GetComp(),e));
            for(DiffData e : working.GetDataContainer()){
                diff.put(e.GetComp(), e);
            }
            if(exclude){
                int x = sampleSet.stream().mapToInt((p) -> this.data.GetSampleRPKM(loc, p) == 0 ? 1 : 0).sum();
                if(x == sampleSet.size())
                    continue;
            }
            
            Iterator<String> comps = comparisonSet.descendingIterator();
            StringBuilder str = new StringBuilder();
            
            String[] coord = working.ToStrArray();

            for (String c : coord) {
                str.append(c);
                str.append("\t");
            }

            // RPKM values
            int col = coord.length;
            Iterator<String> samp = sampleSet.descendingIterator();
            for(int i = col; i < col + sampleSet.size(); i++){
                str.append(data.GetSampleRPKM(loc, samp.next()));
                str.append("\t");
            }

            col += sampleSet.size();

            // Now for the comparison data
            for(int i = col; i < col + (comparisonSet.size() * 6); i +=6){
                String c = comps.next();
                DiffData d = diff.get(c);
                String[] values = d.GetStrArray();
                for(String v : values){
                    str.append(v);
                    if(i < col + (comparisonSet.size() * 6) - 1)
                        str.append("\t");
                }
            }
            out.write(str.toString());
            out.write(nl);
        }
    }
    
    private void CreateRowFromTab(String line, TreeSet<String> sampleSet, Sheet sheet, int row) throws IOException{
        String[] segs = line.split("\t");
        Row temp = sheet.createRow(row);
        
        for(int i = 0; i < 5 + sampleSet.size(); i++){
            Cell c = temp.createCell(i);
            c.setCellValue(segs[i]);
        }
        
        // Now for the complex data
        for(int i = 5 + sampleSet.size(); i < segs.length; i += 6){
            int sig = i + 5;
            for(int x = i; x < i + 6; x++){
                Cell c = temp.createCell(x);
                c.setCellValue(segs[x]);
                if(segs[sig].equals("yes")){
                    c.setCellStyle(this.highlightStyles.get("yellow"));
                }
            }
        }
        
    }
    
    @Deprecated
    private void CreateRowFromData(Sheet sheet, String loc, TreeSet<String> sampleSet, TreeSet<String> comparisonSet, int row){
        // Check if we need to exclude this row        
        CufflinksCoords<DiffData> working = this.data.GetCoordFromLoc(loc);
        Map<String, DiffData> diff = new HashMap<>();
        //working.GetDataContainer().stream().map(e->diff.put(e.GetComp(),e));
        for(DiffData e : working.GetDataContainer()){
            diff.put(e.GetComp(), e);
        }
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
            DiffData d = diff.get(c);
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
        CellRangeAddress second = new CellRangeAddress(0,0,5,sampleSet.size() + 4);
        
        sheet.addMergedRegion(first);
        sheet.addMergedRegion(second);
        
        //int col = 5 + sampleSet.size();
        Iterator<String> compItr = comparisonSet.descendingIterator();
        for(int i = 5 + sampleSet.size(); compItr.hasNext(); i += 6){
            Cell temp = FHeaderRow.createCell(i);
            String s = compItr.next();
            temp.setCellValue(s);
            temp.setCellStyle(this.headerStyles.get(s));
            sheet.addMergedRegion(new CellRangeAddress(0,0,i,i + 5));
            //col += 6;
        }        
        //CellRangeAddress third = new CellRangeAddress(0,0, 5 + sampleSet.size(), col - 6);
        
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
        
        
        System.err.println("[DIFF OUT] Created Header Row for output");
    }
}
