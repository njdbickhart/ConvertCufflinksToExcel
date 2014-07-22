/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package FormatStatics;

import org.apache.poi.ss.usermodel.*;


/**
 *
 * @author bickhart
 */
public class HeaderFormats {
    
    /**
     * A method to create a header style that is Grey in color and bold
     * @param wb The current workbook object
     * @return The desired cellstyle format
     */
    public static CellStyle GreyBoldCenterHeader(Workbook wb){
        CellStyle style;
        Font headerFont = wb.createFont();
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = BorderedStyle.createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFont(headerFont);
        return style;
    }
    
    /**
     * A generic method for creating a cell style with a gray bold header
     * @param wb The current workbook object
     * @return The desired cellstyle format
     */
    public static CellStyle GreyBoldHeader(Workbook wb){
        CellStyle style;
        Font headerFont = wb.createFont();
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = BorderedStyle.createBorderedStyle(wb);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFont(headerFont);
        return style;
    }
    
    /**
     * A generic method for creating a cell style with a gray header
     * @param wb The current workbook object
     * @return The desired cellstyle format
     */
    public static CellStyle GreyHeader(Workbook wb){
        CellStyle style;
        Font headerFont = wb.createFont();
        style = BorderedStyle.createBorderedStyle(wb);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFont(headerFont);
        return style;
    }
    
    /**
     * A generic method for creating a cell style with a custom color bold header
     * @param wb The current workbook object
     * @param color a lowercase string indicating the color desired
     * @return The desired cellstyle format
     */
    public static CellStyle CustomBoldCenterHeader(Workbook wb, String color){
        CellStyle style;
        Font headerFont = wb.createFont();
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = BorderedStyle.createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFillForegroundColor(InterpretColor(color));
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFont(headerFont);
        return style;
    }
    
    /**
     * A generic method for creating a cell style with a custom bold header
     * @param wb The current workbook object
     * @param color a short value indicating the color desired; from the Apache
     * POI short list of colors
     * @return The desired cellstyle format
     */
    public static CellStyle CustomBoldCenterHeader(Workbook wb, short color){
        CellStyle style;
        Font headerFont = wb.createFont();
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = BorderedStyle.createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFillForegroundColor(color);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFont(headerFont);
        return style;
    }
    
    private static short InterpretColor(String color){
        switch(color){
            case "blue":
                return IndexedColors.BLUE.getIndex();
            case "red":
                return IndexedColors.RED.getIndex();
            case "green":
                return IndexedColors.GREEN.getIndex();
            case "teal":
                return IndexedColors.TEAL.getIndex();
            case "orange":
                return IndexedColors.ORANGE.getIndex();
            default:
                return IndexedColors.WHITE.getIndex();
        }
    }
}
