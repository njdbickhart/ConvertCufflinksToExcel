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
 * @param <T>
 */
public class CufflinksCoords <T> extends BedAbstract{

    /**
     * An extra String attribute to store the gene name
     */
    public final String gene;

    /**
     * This is the generic arraylist that holds the data value input into the
     * class.
     */
    public ArrayList<T> holder = null;
    
    /**
     * This Constructor creates the object using the UCSC coordinate convention string,
     * a location name and the name of the gene
     * @param ucsc The UCSC coordinates of the gene (ie. chrX:1-1000)
     * @param name The Location name 
     * @param gene The name of the gene
     */
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
    
    /**
     * Adds A data class object (ie. DiffData) to the "holder" array of this object
     * @param data A class of type "data" (ie. DiffData)
     */
    public void AddToDataContainer(T data){
        if(holder == null)
            holder = new ArrayList<>();
        
        holder.add(data);
    }
    
    /**
     * Returns the Data array
     * @return All data values stored in this coordinate location
     */
    public ArrayList<T> GetDataContainer(){
        return this.holder;
    }
    
    /**
     * Automatically formats attributes of this class for easy printing
     * @return A string[] array containing the following elements: {name, gene
     * chr, start, end}
     */
    public String[] ToStrArray(){
        String[] values = {name, gene, chr, String.valueOf(start), String.valueOf(end)};
        return values;
    }
    
    
    @Override
    public int compareTo(BedAbstract o) {
        return this.start - o.Start();
    }
    
}
