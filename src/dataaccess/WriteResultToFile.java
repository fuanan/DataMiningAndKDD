/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataaccess;

import java.io.*;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author DELL
 */
public class WriteResultToFile {
    
    public static boolean writeDataToXLSXFile(String path,Map<String,ArrayList<String>> result){
        
        try{
            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet spreadsheet = wb.createSheet("result");
            XSSFRow row0 = spreadsheet.createRow(0);
            row0.createCell(0).setCellValue("单位名称");
            row0.createCell(1).setCellValue("最大价格");
            row0.createCell(2).setCellValue("最小价格");
            row0.createCell(3).setCellValue("总价格");
            row0.createCell(4).setCellValue("平均价格");
            row0.createCell(5).setCellValue("中标次数");
            row0.createCell(6).setCellValue("最近一次中标时间");
            row0.createCell(7).setCellValue("倒数第二次中标时间");
            row0.createCell(8).setCellValue("时间间隔(秒)");
            row0.createCell(9).setCellValue("时间间隔(时:分:秒)");
            
            Iterator<Map.Entry<String,ArrayList<String>>> resultIterator = result.entrySet().iterator();
            
            XSSFRow row;
            int rowNum = 1;
            while(resultIterator.hasNext()){
                Map.Entry<String,ArrayList<String>> resultEntry = resultIterator.next();
                
                String supplierName = resultEntry.getKey();
                ArrayList<String> info = resultEntry.getValue();
                row = spreadsheet.createRow(rowNum);
                row.createCell(0).setCellValue(supplierName);
                for(int i = 0; i < info.size(); i ++){
                    row.createCell(i + 1).setCellValue(info.get(i));
                }
                rowNum = rowNum + 1;
            }
            for(int i = 0; i < 10; i ++){
                spreadsheet.autoSizeColumn(i);
            }
            OutputStream outputStream = new FileOutputStream(path + "\\" + "result.xlsx");
            wb.write(outputStream);
            outputStream.close();
            return true;
            
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    } 
    
    
    public static boolean writeRecommendationInfoToFile(String path,Map<String,ArrayList<String>> result){
        
        try{
            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet spreadsheet = wb.createSheet("result");
            XSSFRow row0 = spreadsheet.createRow(0);
            row0.createCell(0).setCellValue("公司名称");
            row0.createCell(1).setCellValue("公司标识(供应商编码)");
            row0.createCell(2).setCellValue("最大价格");
            row0.createCell(3).setCellValue("最小价格");
            row0.createCell(4).setCellValue("总价格");
            row0.createCell(5).setCellValue("平均价格");
            row0.createCell(6).setCellValue("中标次数");
            row0.createCell(7).setCellValue("最近一次中标时间");
            row0.createCell(8).setCellValue("倒数第二次中标时间");
            row0.createCell(9).setCellValue("时间间隔(秒)");
            row0.createCell(10).setCellValue("时间间隔(时:分:秒)");
            
            Iterator<Map.Entry<String,ArrayList<String>>> resultIterator = result.entrySet().iterator();
            
            XSSFRow row;
            int rowNum = 1;
            while(resultIterator.hasNext()){
                Map.Entry<String,ArrayList<String>> resultEntry = resultIterator.next();
                
                String supplierName = resultEntry.getKey();
                ArrayList<String> info = resultEntry.getValue();
                row = spreadsheet.createRow(rowNum);
                row.createCell(0).setCellValue(supplierName);
                for(int i = 0; i < info.size(); i ++){
                    row.createCell(i + 1).setCellValue(info.get(i));
                }
                rowNum = rowNum + 1;
            }
            for(int i = 0; i < 10; i ++){
                spreadsheet.autoSizeColumn(i);
            }
            OutputStream outputStream = new FileOutputStream(path + "\\" + "result.xlsx");
            wb.write(outputStream);
            outputStream.close();
            return true;
            
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
