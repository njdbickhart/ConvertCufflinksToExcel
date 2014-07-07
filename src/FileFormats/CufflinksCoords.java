/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package FileFormats;

import file.BedAbstract;
import file.BedFileException;
import java.util.ArrayList;

/**
 *
 * @author bickhart
 */
public class CufflinksCoords <T> extends BedAbstract{
    public final String gene;
    public ArrayList<T> holder = null;
    
    public CufflinksCoords(String ucsc, String name, String gene){
        try{
            String[] coords = utils.UCSCToStringArray.UCSCToArray(ucsc);
            super.initialVals(coords[0], coords[1], coords[2]);
        }catch(BedFileException ex){
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        this.name = name;
        this.gene = gene;
    }
    
    public void AddToDataContainer(T data){
        if(holder == null)
            holder = new ArrayList<>();
        
        holder.add(data);
    }
    
    public ArrayList<T> GetDataContainer(){
        return this.holder;
    }
    
    public String[] ToStrArray(){
        String[] values = {name, gene, chr, String.valueOf(start), String.valueOf(end)};
        return values;
    }
    
    
    @Override
    public int compareTo(BedAbstract o) {
        return this.start - o.Start();
    }
    
}
