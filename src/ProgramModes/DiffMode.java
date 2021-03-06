/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ProgramModes;

import FileFormats.DiffArray;
import GetCmdOpt.SimpleModeCmdLineParser;
import OutputStyles.DiffExcelDefault;

/**
 *
 * @author bickhart
 */
public class DiffMode {
    private final SimpleModeCmdLineParser cmd;
    
    /**
     * The constructor for the class
     * @param cmd The input command line parser object, properly filled out
     */
    public DiffMode(SimpleModeCmdLineParser cmd){
        this.cmd = cmd;
    }
    
    /**
     * The main workhorse method. Could be changed to implement "Runnable" in
     * the future.
     */
    public void run(){
        DiffArray diff = new DiffArray(cmd.GetValue("input"));
        
        // Load conversion files if needed
        if(cmd.GetValue("keysfile") != null)
            diff.LoadKeyConversion(cmd.GetValue("keysfile"));
        if(cmd.GetValue("genes") != null)
            diff.LoadGeneOverlapper(cmd.GetValue("genes"));
        
        // Process the diff file
        diff.ProcessDiffFile();
        
        DiffExcelDefault output = new DiffExcelDefault(diff);
        output.OutputDiffToExcel(cmd.GetValue("output"), cmd.GetValue("n"));
    }
}
