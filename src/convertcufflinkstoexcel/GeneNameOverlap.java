/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package convertcufflinkstoexcel;

import file.BedMap;
import implement.BedSimple;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import utils.LineIntersect;

/**
 *
 * @author bickhart
 */
public class GeneNameOverlap extends LineIntersect<BedSimple>{
    private final Path path;
    private BedMap<BedSimple> data;
    
    public GeneNameOverlap(String file){
        path = Paths.get(file);
    }
    
    public void LoadOverlapper(){
        data = new BedMap<>();
        try(BufferedReader input = Files.newBufferedReader(path, Charset.defaultCharset())){
            String line = null;
            while((line = input.readLine()) != null){
                line = line.trim();
                String[] segs = line.split("\t");
                data.addBedData(new BedSimple(segs[0], Integer.parseInt(segs[1]), Integer.parseInt(segs[2]), segs[3]));
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    
    public String getGeneNameInt(String chr, int start, int end){
        ArrayList<BedSimple> holder = this.returnTypeIntersect(data, chr, start, end);
        if(holder.isEmpty())
            return "-";
        return JoinName(holder);
    }
    
    private String JoinName(ArrayList<BedSimple> a){
        String ret = a.get(0).Name();
        for(int i = 1; i < a.size(); i++){
            ret += ";" + a.get(i).Name();
        }
        return ret;
    }
}
