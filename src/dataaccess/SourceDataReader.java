/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataaccess;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author FUAN
 */
public class SourceDataReader {
    
    public static Map readRawData(File f){
        Map<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
        
        try{
            FileInputStream is = new FileInputStream(f);
            XSSFWorkbook wb = new XSSFWorkbook(is);
            XSSFSheet dataSheet = wb.getSheetAt(0);
            
            int rowID = 0;
            XSSFRow currRow = dataSheet.getRow(rowID);
            while(currRow != null){
                if(rowID == 0){
                    //第一行表头略过
                    rowID = rowID + 1;
                    currRow = dataSheet.getRow(rowID);
                }else{
                    //第二个格子是系统编码
                    String serialNumber =  currRow.getCell(1).toString();
                    //第三个格子是供应商名称
                    String supplierName = currRow.getCell(2).toString();
                    //第五个格子是总价
                    String price = currRow.getCell(4).toString();
                    //第七个格子是时间
                    String time = currRow.getCell(6).toString();
                    ArrayList<String> info = new ArrayList<String>();
                    info.add(supplierName);
                    info.add(price);
                    info.add(time);
                    map.put(serialNumber, info);
                    rowID = rowID + 1;
                    currRow = dataSheet.getRow(rowID);
                }
            }
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return map;
    }
    
    
    //读SRM数据xlsx表
    public static XSSFWorkbook readSRMData(File f){
        try{
            FileInputStream fis = new FileInputStream(f);
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            return wb;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    //读采购方案基本数据表
    //Key 是 PHID，Value是 price(TOTAL_VALUE)和 time(RECEICE_TIME)
    public static Map readPurchasingSupplierInformationSheet(XSSFWorkbook wb,int sheetNum){
        Map<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
        
        //采购供应商信息表
        XSSFSheet dataSheet = wb.getSheetAt(sheetNum);

        int rowID = 0;
        XSSFRow currRow = dataSheet.getRow(rowID);
        while(currRow != null){
            if(rowID == 0){
                //第一行表头略过
                rowID = rowID + 1;
                currRow = dataSheet.getRow(rowID);
            }else{
                
                //第一个格子是记录ID
                String recordID;
                if(currRow.getCell(0)!=null){
                    recordID = currRow.getCell(0).toString();
                }else{
                    recordID = "null";
                }
                    
                //第二个格子是公司名称
                String companyName;
                if(currRow.getCell(1)!=null){
                    companyName =  currRow.getCell(1).toString();
                }else{
                    companyName = null;
                }
                
                //第三个格子是公司标识
                String companyID;
                if(currRow.getCell(2)!=null){
                    companyID = currRow.getCell(2).toString();
                }else{
                    companyID = "null";
                }
                //第四个格子是供应商包号                
                String supplierPackageNumber;
                if(currRow.getCell(3)!=null){
                    supplierPackageNumber = currRow.getCell(3).toString();
                }else{
                    supplierPackageNumber = "null";
                }
                //第五个格子是供应商包名称
                String supplierPackageName;
                if(currRow.getCell(4)!=null){
                    supplierPackageName = currRow.getCell(4).toString();
                }else{
                    supplierPackageName = "null";
                }
                //第七个格子是PurchaseHeadID
                String PHID;
                if(currRow.getCell(6)!=null){
                    PHID = currRow.getCell(6).toString();
                }else{
                    PHID = "null";
                }
                
                
                //第八个格子是接收时间
                String receivedTime;
                if(currRow.getCell(7)!= null){
                    receivedTime = currRow.getCell(7).toString();
                }else{
                    receivedTime = "null";
                }

                ArrayList<String> info = new ArrayList<String>();
                info.add(companyName);
                info.add(companyID);
                info.add(supplierPackageNumber);
                info.add(supplierPackageName);
                info.add(PHID);
                info.add(receivedTime);
                map.put(recordID, info);
                rowID = rowID + 1;
                currRow = dataSheet.getRow(rowID);
            }
        }
        return map;
    }
    
    public static Map readBasicProcurementPlan(XSSFWorkbook wb,int sheetNum){
        Map<String,Double> procurementPlan = new HashMap<String,Double>();
        XSSFSheet sheet = wb.getSheetAt(sheetNum);
        int rowID = 0;
        XSSFRow currRow = sheet.getRow(rowID);
        while(currRow != null){
            if((rowID == 0)||(rowID == 1)){
                //第一、二行表头略过
                rowID = rowID + 1;
                currRow = sheet.getRow(rowID);
            }else{
                
                //第二个格子是PHID
                String PHID =  currRow.getCell(1).toString();
                //第七个格子是价格
                Double price = Double.valueOf(currRow.getCell(7).toString());
                procurementPlan.put(PHID, price);
                rowID = rowID + 1;
                currRow = sheet.getRow(rowID);
            }
            
        }
        
        
        return procurementPlan;
    }
    
}
