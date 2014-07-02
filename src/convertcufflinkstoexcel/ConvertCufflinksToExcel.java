/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package convertcufflinkstoexcel;

import GetCmdOpt.SimpleModeCmdLineParser;

/**
 *
 * @author bickhart
 */
public class ConvertCufflinksToExcel {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SimpleModeCmdLineParser cmd = CreateCmdLineObject();
        
        cmd.GetAndCheckMode(args);
        
        switch(cmd.CurrentMode){
            case "diff":
                System.err.println("[Converter] Entering diff conversion mode.");
            default:
                System.err.println("[Converter] Error! Did not specify mode for program!");
                System.exit(-1);
        }
    }
    
    /*
    A separate subroutine to generate the command line data
    */
    private static SimpleModeCmdLineParser CreateCmdLineObject(){
        String nl = System.lineSeparator();
        String defaultusage = "ConvertCufflinkstoExcel: a program designed to process cufflinks files to human-readable text" + nl +
                    "\tUsage: java -jar ConvertCufflinksToExcel.jar [mode] [arguments ...]" + nl +
                    "\tModes:" + nl +
                    "\t\tdiff\tProcesses multi-sample \"diff\" files and turns them into an xlsx spreasheet with highlighting" + nl;
        SimpleModeCmdLineParser cmd = new SimpleModeCmdLineParser(defaultusage, "diff");
        
        cmd.AddMode("diff",
                "ConvertCufflinksToExcel diff mode" + nl +
                        "\tUsage: java -jar ConvertCufflinksToExcel.jar diff [-i input -o output] [other options]" + nl +
                        "\t\t-i\tThe input \".diff\" file" + nl +
                        "\t\t-o\tThe output file. MUST have .xlsx in the name!" + nl +
                        "\t\t-k\tA keys file. Converts the \"q1\" sample designations to different names if selected" + nl, 
                "i:o:k:", 
                "io", 
                "iok", 
                "input", "output", "keysfile");
        
        return cmd;
    }
}