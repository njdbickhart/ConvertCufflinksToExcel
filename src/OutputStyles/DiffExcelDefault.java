/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OutputStyles;

import FileFormats.DiffArray;

/**
 *
 * @author bickhart
 */
public class DiffExcelDefault {    
    private final String[] coordheaders = {"LocName", "GeneID", "Chr", "Start", "End"};
    private final String[] diffheaders = {"Status", "Log2 FoldChange", "TestStatistic", "pValue", "Corrected pValue", "Significance?"};
    private final DiffArray data;
    
    public DiffExcelDefault(DiffArray data){
        this.data = data;
    }
    
    
    
}
