/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package FileFormats;

import convertcufflinkstoexcel.GeneNameOverlap;
import file.BedAbstract;
import file.BedFileException;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author bickhart
 */
public class DiffArray {
    private SampleKeyConversion KeyConvert = null;
    private GeneNameOverlap GeneUtility = null;
    private final Path DiffFile;
    private final HashMap<String, CufflinksCoords<DiffData>> genomiccoords = new HashMap<>(); // Key is the "LocName"
    private final HashMap<String, HashMap<String, Double>> rpkmvalues = new HashMap<>(); // First key is the "LocName," Second key is the sample id
    private final HashSet<String> samples = new HashSet<>();
    private final Set<String> comparisons = new HashSet<>(); // Key is the sample name, "-", sample name
    
    
    public Double GetSampleRPKM(String loc, String samp){
        if(rpkmvalues.containsKey(loc))
            if(rpkmvalues.get(loc).containsKey(samp))
                return rpkmvalues.get(loc).get(samp);
        
        return null;
    }
    
    public CufflinksCoords<DiffData> GetCoordFromLoc(String loc){
        if(genomiccoords.containsKey(loc))
            return genomiccoords.get(loc);
        else
            return null;
    }
    
    public Set<String> GetCoordLocs(){
        return this.genomiccoords.keySet();
    }
    
    public Set<String> GetRpkmSamples(){
        return this.samples;
    }
    
    public Set<String> GetComp(){
        return this.comparisons;
    }
    
    public DiffArray(String file){
        DiffFile = Paths.get(file);
    }
    
    /*
    Diff files look like this:
    test_id gene_id gene    locus   sample_1        sample_2        status  value_1 value_2 log2(fold_change)       test_stat       p_value q_value significant
    XLOC_000001     XLOC_000001     Gm16088 1:3054232-3054733       q1      q2      NOTEST  0       0       0       0       1       1       no
    */
    
    public void LoadKeyConversion(String file){
        KeyConvert = new SampleKeyConversion(file);
        KeyConvert.InitializeConverter();
    }
    
    public void LoadGeneOverlapper(String file){
        GeneUtility = new GeneNameOverlap(file);
        GeneUtility.LoadOverlapper();
    }
    
    public void ProcessDiffFile(){
        try(BufferedReader input = Files.newBufferedReader(DiffFile, Charset.defaultCharset())){
            // Gets rid of the header
            String line = input.readLine();
            while((line = input.readLine()) != null){
                line = line.trim();
                String[] segs = line.split("\t");
                
                // If told to convert sample names, do that conversion now
                if(KeyConvert != null){
                    segs[4] = KeyConvert.GetValue(segs[4]);
                    segs[5] = KeyConvert.GetValue(segs[5]);
                }
                
                // Create CufflinksCoord if necessary
                if(!genomiccoords.containsKey(segs[1])){
                    if(segs[2].equals("-") && this.GeneUtility != null){
                        String[] values = null;
                        try {
                            values = utils.UCSCToStringArray.UCSCToArray(segs[3]);
                        } catch (BedFileException ex) {
                            Logger.getLogger(DiffArray.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        segs[2] = this.GeneUtility.getGeneNameInt(values[0], Integer.parseInt(values[1]), Integer.parseInt(values[2]));
                    }
                    genomiccoords.put(segs[1], new CufflinksCoords<>(segs[3], segs[1], segs[2]));
                }
                // Add to dataholder
                genomiccoords.get(segs[1]).AddToDataContainer(new DiffData(segs));
                
                // Put sample value into container if not already there
                String comp = segs[4] + " - " + segs[5];
                if(!comparisons.contains(comp))
                    comparisons.add(comp);
                
                if(!rpkmvalues.containsKey(segs[1]))
                    rpkmvalues.put(segs[1], new HashMap<>());
                
                if(!rpkmvalues.get(segs[1]).containsKey(segs[4]))
                    rpkmvalues.get(segs[1]).put(segs[4], checkDouble(segs[7]));
                
                if(!rpkmvalues.get(segs[1]).containsKey(segs[5]))
                    rpkmvalues.get(segs[1]).put(segs[5], checkDouble(segs[8]));
                
                if(!samples.contains(segs[4]))
                    samples.add(segs[4]);
                if(!samples.contains(segs[5]))
                    samples.add(segs[5]);
            }
        }catch(IOException ex){
            System.err.println("Could not open diff file!!");
        }
    }
        
       
    
    protected Double checkDouble(String value){
        if(Pattern.matches("[+-]nan", value)){
            return Double.NaN;
        }else if (Pattern.matches("[+-]*inf", value)){
            return Double.POSITIVE_INFINITY;
        }else
            return Double.valueOf(value);            
    }
}
