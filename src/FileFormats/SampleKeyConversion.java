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
    private Path keyfile;
    private HashMap<String, String> converter = new HashMap<>();
    
    public SampleKeyConversion(String keyfile){
        this.keyfile = Paths.get(keyfile);
    }
    
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
    
    public String GetValue(String key){
        if(converter.containsKey(key))
            return converter.get(key);
        else{
            System.err.println("[KEY CONVERSION] ERROR! Could not identify key: " + key + " in key conversion file!");
            return key;
        }
    }
}
