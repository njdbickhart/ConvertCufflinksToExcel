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
public class BorderedStyle {

    /**
     * This method alters the workbook object to create a completely bordered
     * cell for use in tables.
     * @param wb The current workbook object that will use the bordered style
     * @return A bordered cell style.
     */
    public static CellStyle createBorderedStyle(Workbook wb){
        CellStyle style = wb.createCellStyle();
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        return style;
    }
}
