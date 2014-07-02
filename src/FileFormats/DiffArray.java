/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package FileFormats;

import file.BedAbstract;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 *
 * @author bickhart
 */
public class DiffArray {
    private SampleKeyConversion KeyConvert;
    private Path DiffFile;
    private HashMap<String, CufflinksCoords<DiffData>> genomiccoords = new HashMap<>();
    private Set<String> comparisons = new HashSet<>();
    private final String[] coordheaders = {"LocName", "GeneID", "Chr", "Start", "End"};
    private final String[] diffheaders = {"Status"};
    
    /*
    TODO: change this so that the expression values are listed in columns prior to the diffdata array
    */
    
    public DiffArray(String file){
        DiffFile = Paths.get(file);
    }
    
    /*
    Diff files look like this:
    test_id gene_id gene    locus   sample_1        sample_2        status  value_1 value_2 log2(fold_change)       test_stat       p_value q_value significant
    XLOC_000001     XLOC_000001     Gm16088 1:3054232-3054733       q1      q2      NOTEST  0       0       0       0       1       1       no
    */
    
    private class DiffData{
        private String comparison;
        private String status;
        private Double value1;
        private Double value2;
        private Double foldchange;
        private Double teststat;
        private Double pvalue;
        private Double qvalue;
        private String significant;
        
        public DiffData(String[] segs, SampleKeyConversion convert){
            // Ignoring the first 4 values of segs, as those are in the genomic coords entry
            segs[4] = convert.GetValue(segs[4]);
            segs[5] = convert.GetValue(segs[5]);
            
            comparison = segs[4] + "-" + segs[5];
            status = segs[6];
            value1 = this.checkDouble(segs[7]);
            value2 = this.checkDouble(segs[8]);
            foldchange = this.checkDouble(segs[9]);
            teststat = this.checkDouble(segs[10]);
            pvalue = this.checkDouble(segs[11]);
            qvalue = this.checkDouble(segs[12]);
            significant = segs[13];
        }
        
        private Double checkDouble(String value){
            if(Pattern.matches("[+-]?(nan|inf)", value)){
                return Double.valueOf("NaN");
            }else
                return Double.valueOf(value);            
        }
    }
}
