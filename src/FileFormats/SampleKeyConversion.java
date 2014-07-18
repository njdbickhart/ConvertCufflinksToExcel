/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package FileFormats;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 *
 * @author bickhart
 */
public class SampleKeyConversion {
    private final Path keyfile;
    private final HashMap<String, String> converter = new HashMap<>();
    
    /**
     * This creates the attributes for the class
     * @param keyfile A string representation of the path to the key file
     */
    public SampleKeyConversion(String keyfile){
        this.keyfile = Paths.get(keyfile);
    }
    
    /**
     * This loads the information from the sample key file into the Map attribute
     * of this class.
     */
    public void InitializeConverter(){
        try(BufferedReader input = Files.newBufferedReader(keyfile, Charset.defaultCharset())){
            String line = null;
            while((line = input.readLine()) != null){
                line = line.trim();
                String[] segs = line.split("\t");
                if(segs.length == 2){
                    this.converter.put(segs[0], segs[1]);
                }
            }
        }catch(IOException ex){
            System.err.println("Could not open key file!!");
            System.err.println(ex.getMessage());
        }
    }
    
    /**
     * This is the main method of this utility as it allows the lookup of sample names
     * for conversion.
     * @param key The name of the sample to be converted
     * @return The converted name (or the name of the sample if it does not find it in 
     * the key file)
     */
    public String GetValue(String key){
        if(converter.containsKey(key))
            return converter.get(key);
        else{
            System.err.println("[KEY CONVERSION] ERROR! Could not identify key: " + key + " in key conversion file!");
            return key;
        }
    }
}
