/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package FormatStatics;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author bickhart
 */
public class HighlightStyle {

    /**
     * A Highlight style that uses a custom color short value; short values
     * are derived from the Apache POI color definitions
     * @param wb The current workbook object
     * @param color a short value indicating the color desired; from the Apache
     * POI short list of colors
     * @return The desired cellstyle format
     */
    public static CellStyle CustomBoldHighlight(Workbook wb, Short color){
        CellStyle style;
        Font highlightFont = wb.createFont();
        highlightFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = BorderedStyle.createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setFillForegroundColor(color);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFont(highlightFont);
        return style;
    }
    
    /**
     * A simple highlight style that returns a bright yellow highlight cell style
     * @param wb The current workbook object
     * @return The desired yellow cell style
     */
    public static CellStyle YellowBoldHighlight(Workbook wb){
        CellStyle style;
        Font highlightFont = wb.createFont();
        highlightFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = BorderedStyle.createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFont(highlightFont);
        return style;
    }
}
