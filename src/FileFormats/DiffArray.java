/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package FileFormats;

import convertcufflinkstoexcel.GeneNameOverlap;
import file.BedFileException;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    
    
    /**
     * The constructor of the array. 
     * @param file The String of the path to the input cuffdiff file
     */
    public DiffArray(String file){
        DiffFile = Paths.get(file);
    }
    
    /**
     * This returns the sampleRPKM value if given a location name and sample name
     * @param loc The area location name (ie. CufflinksCoords.name attribute)
     * @param samp The name of the sample condition
     * @return The RPKM value for that sample at the locus
     */
    public Double GetSampleRPKM(String loc, String samp){
        if(rpkmvalues.containsKey(loc))
            if(rpkmvalues.get(loc).containsKey(samp))
                return rpkmvalues.get(loc).get(samp);
        
        return null;
    }
    
    /**
     * Returns the CufflinksCoords entry for the location
     * @param loc The genomic location string ID
     * @return The CufflinksCoords entry for that location
     */
    public CufflinksCoords<DiffData> GetCoordFromLoc(String loc){
        if(genomiccoords.containsKey(loc))
            return genomiccoords.get(loc);
        else
            return null;
    }
    
    /**
     * Returns a list of all of the stored coordinate locations for this sample
     * @return A set of coordinate locations (ie. "loc" strings for the rest of
     * this class's methods)
     */
    public Set<String> GetCoordLocs(){
        return this.genomiccoords.keySet();
    }
    
    /**
     * Returns a list of all of the samples that have RPKM values for this experiment
     * @return A set of sample names (ie. "samp" strings for the rest of this
     * class's methods
     */
    public Set<String> GetRpkmSamples(){
        return this.samples;
    }
    
    /**
     * Returns all of the pairwise comparisons performed in this experiment
     * @return A set of all pairwise comparisons (denoted as "sample1 - sample2" format)
     */
    public Set<String> GetComp(){
        return this.comparisons;
    }
    
    
    
    /*
    Diff files look like this:
    test_id gene_id gene    locus   sample_1        sample_2        status  value_1 value_2 log2(fold_change)       test_stat       p_value q_value significant
    XLOC_000001     XLOC_000001     Gm16088 1:3054232-3054733       q1      q2      NOTEST  0       0       0       0       1       1       no
    */

    /**
     * Imports a keyconversion file for changing sample names
     * @param file The string representation of the path to the keyconversion file
     */    
    public void LoadKeyConversion(String file){
        KeyConvert = new SampleKeyConversion(file);
        KeyConvert.InitializeConverter();
    }
    
    /**
     * Imports the gene bed file for gene coordinate intersections
     * @param file The string representation of the path to the gene bed file
     */
    public void LoadGeneOverlapper(String file){
        GeneUtility = new GeneNameOverlap(file);
        GeneUtility.LoadOverlapper();
    }
    
    /**
     * The main workhorse method. Run this after the constructor in order to 
     * load the Cuffdiff file and process its contents.
     */
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
        
    /**
     * This is a utility method designed to check a string value and convert it
     * to a type-safe java double value
     * @param value The string that should be a double 
     * @return A type-safe double value that accounts for c++ inf and nan values
     */
    protected Double checkDouble(String value){
        if(Pattern.matches("[+-]nan", value)){
            return Double.NaN;
        }else if (Pattern.matches("[+-]*inf", value)){
            return Double.POSITIVE_INFINITY;
        }else
            return Double.valueOf(value);            
    }
}
