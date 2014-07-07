/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package FileFormats;

import java.util.regex.Pattern;

/**
 *
 * @author bickhart
 */
public class DiffData{
    private final String comparison;
    private final String status;
    //private Double value1;
    //private Double value2;
    private final Double foldchange;
    private final Double teststat;
    private final Double pvalue;
    private final Double qvalue;
    private final String significant;

    public DiffData(String[] segs){
        // Ignoring the first 4 values of segs, as those are in the genomic coords entry
        // Sample Key conversion must be done in SuperClass before generating the subclass

        comparison = segs[4] + " - " + segs[5];
        status = segs[6];
        //value1 = this.checkDouble(segs[7]);
        //value2 = this.checkDouble(segs[8]);
        foldchange = checkDouble(segs[9]);
        teststat = checkDouble(segs[10]);
        pvalue = checkDouble(segs[11]);
        qvalue = checkDouble(segs[12]);
        significant = segs[13];
    }

    public String GetComp(){
        return this.comparison;
    }

    public String[] GetStrArray(){
        String[] temp = {status, FDouble(foldchange), FDouble(teststat), 
            FDouble(pvalue), FDouble(qvalue), significant};
        return temp;
    }

    private String FDouble(Double e){
        return String.format("%.4f", e);
    }
    
    private Double checkDouble(String value){
        if(Pattern.matches("[+-]nan", value)){
            return Double.NaN;
        }else if (Pattern.matches("[+-]*inf", value)){
            return Double.POSITIVE_INFINITY;
        }else
            return Double.valueOf(value);            
    }
}
