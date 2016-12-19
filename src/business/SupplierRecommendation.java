/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business;
import java.io.File;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.DecimalFormat;
import java.util.Scanner;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



import dataaccess.SourceDataReader;
import javax.swing.JOptionPane;

/**
 *
 * @author FUAN
 */
public class SupplierRecommendation {
    
    private Map<String,ArrayList<String>> purchasingSupplierInformationSheet;
    private Map<String,Map<String,ArrayList<String>>> classifiedPSI;
    private Map<String,Double> procurementPlan;
    private Map<String,ArrayList<String>> result;
    
    public SupplierRecommendation(File file){
        XSSFWorkbook wb = SourceDataReader.readSRMData(file);
        this.purchasingSupplierInformationSheet = SourceDataReader.readPurchasingSupplierInformationSheet(wb, 3);
        this.classifiedPSI = classifyPurchasingSupplierInfo(this.purchasingSupplierInformationSheet);
        this.procurementPlan = SourceDataReader.readBasicProcurementPlan(wb, 1);
        this.result = compute(this.classifiedPSI,this.procurementPlan);
    }
    
    private Map<String,Map<String,ArrayList<String>>> classifyPurchasingSupplierInfo(Map<String,ArrayList<String>> purchasingSupplierInfo){
        Map<String,Map<String,ArrayList<String>>> classifiedPSI = new HashMap();
        Iterator<Map.Entry<String,ArrayList<String>>> iterator = purchasingSupplierInfo.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,ArrayList<String>> entry = iterator.next();
            String recordID = entry.getKey();
            ArrayList<String> info = entry.getValue();
            String companyName = info.get(0);
            String companyID = info.get(1);
            String supplierPackageNumber = info.get(2);
            String supplierPackageName = info.get(3);
            String PHID = info.get(4);
            String receivedTime = info.get(5);
            
            if(classifiedPSI.get(companyName + "|" + companyID) == null){
                Map<String,ArrayList<String>> temp = new HashMap<String,ArrayList<String>>();
                ArrayList<String> infoTemp = new ArrayList<String>();
                infoTemp.add(supplierPackageNumber);
                infoTemp.add(supplierPackageName);
                infoTemp.add(receivedTime);
                temp.put(PHID, infoTemp);
                String name = companyName + "|" + companyID;
                classifiedPSI.put(name, temp);
            }else{
                Map<String,ArrayList<String>> temp = classifiedPSI.get(companyName + "|" + companyID);
                if(temp.get(PHID) == null){
                    ArrayList<String> infoTemp = new ArrayList<String>();
                    infoTemp.add(supplierPackageNumber);
                    infoTemp.add(supplierPackageName);
                    infoTemp.add(receivedTime);
                    temp.put(PHID, infoTemp);
                    classifiedPSI.put(companyName + "|" + companyID, temp);
                }else{
                    JOptionPane.showMessageDialog(null, "Same PHID! " + companyName + "|" + companyID, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        return classifiedPSI;
    }
   
    //<name + "|" + companyID,<PHID,<supplierPackageNumber,supplierPackageName,receivedTime>>>
    private Map compute(Map<String,Map<String,ArrayList<String>>> classifiedPSI, Map<String,Double> procurementPlan){
        try{
            
            Map<String,ArrayList<String>> result = new HashMap<String, ArrayList<String>>();

            Iterator<Map.Entry<String,Map<String,ArrayList<String>>>> iterator = classifiedPSI.entrySet().iterator();

            DecimalFormat decimalFormat = new DecimalFormat("##.00");

            while(iterator.hasNext()){
                Map.Entry<String,Map<String,ArrayList<String>>> entry = iterator.next();

                String companyNameAndID = entry.getKey();
                //<PHID,<supplierPackageNumber,supplierPackageName,receivedTime>>
                Map<String,ArrayList<String>> recordSet = entry.getValue();
                Iterator<Map.Entry<String,ArrayList<String>>> recordSetIterator = recordSet.entrySet().iterator();

                double biggestPrice = 0;
                double smallestPrice = 0;
                double totalAmount = 0;
                double averagePrice = 0;
                Date lastDate = null;
                Date secondLastDate = null;
                long timeSpan = 0;
                int recordNum = 0;

                while(recordSetIterator.hasNext()){
                    Map.Entry<String,ArrayList<String>> recordSetEntry = recordSetIterator.next();
                    //******************************************************************
                    //计算最大价格，最小价格、总价格及记录数目
                    String PHID = recordSetEntry.getKey();
                    ArrayList<String> record = recordSetEntry.getValue();
                    Double price = procurementPlan.get(PHID);

                    if(recordNum == 0){
                        biggestPrice = price;
                        smallestPrice = price;
                    }else{
                        if(price > biggestPrice){
                            biggestPrice = price;
                        }
                        if(price < smallestPrice){
                            smallestPrice = price;
                        }
                    }

                    totalAmount = totalAmount + price;
                    //******************************************************************

                    //计算最后一次日期和倒数第二次日期

                    if(recordNum == 0){
                        lastDate = transformStringToDate(record.get(2));
                        secondLastDate = transformStringToDate(record.get(2)); 
                    }else{
                        Date dateOfCurrRecord = transformStringToDate(record.get(2));

                        if(dateOfCurrRecord.after(lastDate)){
                            secondLastDate = lastDate;
                            lastDate = dateOfCurrRecord;
                        }else if(dateOfCurrRecord.after(secondLastDate)&&(!dateOfCurrRecord.after(lastDate))){
                            secondLastDate = dateOfCurrRecord;
                        }else if(lastDate.equals(secondLastDate)&&(dateOfCurrRecord.before(secondLastDate))){
                            secondLastDate = dateOfCurrRecord;
                        }else{
                            //do nothing
                        }
                    }

                    recordNum = recordNum + 1;
                    //System.out.println(recordNum);

                }

                timeSpan = (lastDate.getTime() - secondLastDate.getTime())/1000;
                long day = timeSpan/(24*60*60);
                long hour = timeSpan/(60*60) - day*24;
                long min = timeSpan/60 - day*24*60 - hour*60;
                long sec  = timeSpan - day*24*60*60 - hour * 60 * 60 - min * 60;
                String t = String.valueOf(day) + "天" + String.valueOf(hour) + "时" + String.valueOf(min) + "分" + String.valueOf(sec) + "秒";

                averagePrice = totalAmount/recordNum;

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                ArrayList<String> currInfo = new ArrayList();

                String[] temp = companyNameAndID.split("\\|");
                String name = temp[0];
                String ID = temp[1];

                currInfo.add(ID);
                currInfo.add(String.valueOf(decimalFormat.format(biggestPrice)));
                currInfo.add(String.valueOf(decimalFormat.format(smallestPrice)));
                currInfo.add(String.valueOf(decimalFormat.format(totalAmount)));
                currInfo.add(String.valueOf(decimalFormat.format(averagePrice)));
                currInfo.add(String.valueOf(recordNum));
                currInfo.add(formatter.format(lastDate));
                currInfo.add(formatter.format(secondLastDate));
                currInfo.add(String.valueOf(timeSpan));
                currInfo.add(t);

                result.put(name, currInfo);
            }


            return result;

        }catch(ParseException e){
            e.printStackTrace();
            return null;
        }
    }
    
    private Date transformStringToDate(String stringDate) throws ParseException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = formatter.parse(stringDate);
        return date;
    }
    
    public Map getResult(){
        return this.result;
    }
    
}
