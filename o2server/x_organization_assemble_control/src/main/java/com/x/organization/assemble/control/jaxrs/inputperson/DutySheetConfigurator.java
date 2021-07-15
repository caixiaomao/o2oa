package com.x.organization.assemble.control.jaxrs.inputperson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.x.base.core.project.gson.GsonPropertyObject;

public class DutySheetConfigurator extends GsonPropertyObject {

	private static final Pattern attributePattern = Pattern.compile("^\\((.+?)\\)$");

	private Integer sheetIndex;
	private Integer memoColumn;
	private Integer firstRow;
	private Integer lastRow;

	private Integer nameColumn;
	private Integer iunitColumn;
	private Integer descriptionColumn;
	private Integer unitColumn;
	private Integer ipersonColumn;
	private Integer dutyUniqueColumn;

	private Map<String, Integer> attributes = new HashMap<>();

	public DutySheetConfigurator(XSSFWorkbook workbook, Sheet sheet) {
		this.sheetIndex = workbook.getSheetIndex(sheet);
		Row row = sheet.getRow(sheet.getFirstRowNum());
		this.firstRow = sheet.getFirstRowNum() + 1;
		this.lastRow = sheet.getLastRowNum();
		memoColumn = row.getLastCellNum() + 1;
		for (int i = row.getFirstCellNum(); i <= row.getLastCellNum(); i++) {
			Cell cell = row.getCell(i);
			if (null != cell) {
				String str = this.getCellStringValue(cell);
				//System.out.println("str="+str+"----i="+i);
				if (StringUtils.isNotEmpty(str)) {
					if (iunitItems.contains(str)) {
						this.iunitColumn = i;
					} else if (nameItems.contains(str)) {
						this.nameColumn = i;
					}else if (descriptionItems.contains(str)) {
						this.descriptionColumn = i;
					}else if(unitItems.contains(str)){
						this.unitColumn = i;
					}else if(ipersonItems.contains(str)){
						this.ipersonColumn = i;
					}else if (dutyUniqueItems.contains(str)) {
						this.dutyUniqueColumn = i;
					}else {
						Matcher matcher = attributePattern.matcher(str);
						if (matcher.matches()) {
							String attribute = matcher.group(1);
							this.attributes.put(attribute, new Integer(i));
						}
					}
				}
			}
		}
	}

	private static List<String> nameItems = Arrays.asList(new String[] { "职务名称 *", "name" });
	private static List<String> unitItems = Arrays.asList(new String[] { "职务所在组织唯一编码 *", "unit" });
	private static List<String> descriptionItems = Arrays.asList(new String[] { "描述","职务描述", "description" });
	private static List<String> ipersonItems = Arrays.asList(new String[] { "职务所含人员唯一编码", "iperson" });
	private static List<String> iunitItems = Arrays.asList(new String[] { "职务所含人员所在组织唯一编码",  "iunit" });
	private static List<String> dutyUniqueItems = Arrays.asList(new String[] { "职务编码",  "dutyUnique"});

	public String getCellStringValue(Cell cell) {
		if (null != cell) {
			switch (cell.getCellType()) {
			case BLANK:
				return "";
			case BOOLEAN:
				return BooleanUtils.toString(cell.getBooleanCellValue(), "true", "false", "false");
			case ERROR:
				return "";
			case FORMULA:
				return "";
			case NUMERIC:
				Double d = cell.getNumericCellValue();
				Long l = d.longValue();
				if (l.doubleValue() == d) {
					return l.toString();
				} else {
					return d.toString();
				}
			default:
				return cell.getStringCellValue();
			}
		}
		return "";
	}

	public Integer getMemoColumn() {
		return memoColumn;
	}

	public Integer getIunitColumn() {
		return iunitColumn;
	}

	public Integer getNameColumn() {
		return nameColumn;
	}

	public Integer getUnitColumn() {
		return unitColumn;
	}

	public Integer getIpersonColumn() {
		return ipersonColumn;
	}

	public Map<String, Integer> getAttributes() {
		return attributes;
	}
	
	public Integer getDescriptionColumn() {
		return descriptionColumn;
	}

	public Integer getDutyUniqueColumn() {
		return dutyUniqueColumn;
	}

	public Integer getFirstRow() {
		return firstRow;
	}

	public Integer getLastRow() {
		return lastRow;
	}

	public Integer getSheetIndex() {
		return sheetIndex;
	}

}