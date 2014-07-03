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
