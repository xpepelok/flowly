package dev.xpepelok.flowly.data.excel;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class ExcelStyles {
    CellStyle title, header, key, money, dateTime, dateOnly, link;

    public ExcelStyles(Workbook workbook) {
        CreationHelper helper = workbook.getCreationHelper();

        this.title = createTitleStyle(workbook);
        this.header = createHeaderStyle(workbook);
        this.key = createKeyStyle(workbook);
        this.money = createMoneyStyle(workbook, helper);
        this.dateTime = createDateTimeStyle(workbook, helper);
        this.dateOnly = createDateOnlyStyle(workbook, helper);
        this.link = createLinkStyle(workbook);
    }

    private CellStyle createTitleStyle(Workbook wb) {
        var style = wb.createCellStyle();
        var font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        return style;
    }

    private CellStyle createHeaderStyle(Workbook wb) {
        var style = wb.createCellStyle();
        var font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createKeyStyle(Workbook wb) {
        var style = wb.createCellStyle();
        var font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle createMoneyStyle(Workbook wb, CreationHelper helper) {
        var style = wb.createCellStyle();
        style.setDataFormat(helper.createDataFormat().getFormat("#,##0.00"));
        return style;
    }

    private CellStyle createDateTimeStyle(Workbook wb, CreationHelper helper) {
        var style = wb.createCellStyle();
        style.setDataFormat(helper.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));
        return style;
    }

    private CellStyle createDateOnlyStyle(Workbook wb, CreationHelper helper) {
        var style = wb.createCellStyle();
        style.setDataFormat(helper.createDataFormat().getFormat("yyyy-mm-dd"));
        return style;
    }

    private CellStyle createLinkStyle(Workbook wb) {
        var style = wb.createCellStyle();
        var font = wb.createFont();
        font.setUnderline(Font.U_SINGLE);
        style.setFont(font);
        return style;
    }
}
