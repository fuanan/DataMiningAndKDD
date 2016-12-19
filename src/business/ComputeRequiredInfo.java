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
import dataaccess.SourceDataReader;

/**
 *
 * @author FUAN
 */
public class ComputeRequiredInfo {
    
    private File sourceDataFile;
    private Map<String,ArrayList<String>> rawData;
    private Map<String,Map<String,ArrayList<String>>> data;
    private Map<String,ArrayList<String>> requiredInfo;
    
    public ComputeRequiredInfo(File file){
        this.sourceDataFile = file;
        this.rawData = SourceDataReader.readRawData(this.sourceDataFile);
        this.data = classifyRecords(this.rawData);
        this.requiredInfo = compute(this.data);
    }
    
    private Map classifyRecords(Map<String,ArrayList<String>> rawData){
        Iterator<Map.Entry<String,ArrayList<String>>> iterator = rawData.entrySet().iterator();
        //第一个String 为 供应商名称 第二个Map为一个供应商中标记录的集合，其中 第一项String没实际意义，用serialNum填充，后面的
        //ArrayList保存中标记录的具体信息
        Map<String,Map<String,ArrayList<String>>> classifiedRecord = new HashMap<String,Map<String,ArrayList<String>>>();
        
        while(iterator.hasNext()){
            Map.Entry<String,ArrayList<String>> entry = iterator.next();
            String serialNum = entry.getKey();
            //info.get(0)  ->   supplierName
            //info.get(1)  ->   price
            //info.get(2)  ->   time
            ArrayList<String> info = entry.getValue();
            
            //此公司的记录首次添加进入classifiedRecord
            if(classifiedRecord.get(info.get(0)) == null){
                Map<String,ArrayList<String>> temp = new HashMap<String,ArrayList<String>>();
                ArrayList<String> infoTemp = new ArrayList<String>();
                infoTemp.add(info.get(1));
                infoTemp.add(info.get(2));
                temp.put(serialNum,infoTemp);
                classifiedRecord.put(info.get(0), temp);
            }else{//此公司的记录不是首次添加进入classifiedRecord
                Map<String, ArrayList<String>> temp = classifiedRecord.get(info.get(0));
                ArrayList<String> infoTemp = new ArrayList<String>();
                infoTemp.add(info.get(1));
                infoTemp.add(info.get(2));
                temp.put(serialNum, infoTemp);
                classifiedRecord.put(info.get(0), temp);
            }
        }
        return classifiedRecord;
    }
    
    private Map compute(Map<String,Map<String,ArrayList<String>>> supplierData){
        try{ 
            Map<String,ArrayList<String>> result = new HashMap<String,ArrayList<String>>();
            
            Iterator<Map.Entry<String,Map<String,ArrayList<String>>>> iterator = supplierData.entrySet().iterator();
            
            //double型的大数用下面的formatter转换为非科学计数法表示
            DecimalFormat decimalFormat = new DecimalFormat("##.00");
            
            while(iterator.hasNext()){
                Map.Entry<String,Map<String,ArrayList<String>>> entry = iterator.next();

                String supplierName = entry.getKey();
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
                    //计算最大价格，最小价格、总价格及中标记录数目

                    ArrayList<String> record = recordSetEntry.getValue();
                    //第一次计算当前记录组对应的信息
                    if(recordNum == 0){
                        biggestPrice = Double.valueOf(record.get(0));
                        smallestPrice = Double.valueOf(record.get(0));

                    }else{//非第一次计算当前记录组对应的信息
                        if(Double.valueOf(record.get(0)) > biggestPrice){
                            biggestPrice = Double.valueOf(record.get(0));
                        }
                        if(Double.valueOf(record.get(0)) < smallestPrice){
                            smallestPrice = Double.valueOf(record.get(0));
                        }
                    }
                    //
                    totalAmount = totalAmount + Double.valueOf(record.get(0));
                    //******************************************************************

                    //------------------------------------------------------------------
                    //计算最后一次日期和倒数第二次日期
                    //第一次计算日期
                    if(recordNum == 0){
                        lastDate = transformStringToDate(record.get(1));
                        secondLastDate = transformStringToDate(record.get(1));
                    }
                    else{//非第一次计算日期
                        Date dateOfCurrRecord = transformStringToDate(record.get(1));
                        //当前日期在lastDate之后，则将lastDate置为当前日期，secondLastDate置为lastDate
                        if(dateOfCurrRecord.after(lastDate)){
                            secondLastDate = lastDate;
                            lastDate = dateOfCurrRecord;
                        }//当前日期不在lastDate之后，而在secondLastDate之后，则将secondLastDate置为当前日期
                        else if((dateOfCurrRecord.after(secondLastDate))&&(!dateOfCurrRecord.after(lastDate))){
                            secondLastDate = dateOfCurrRecord;
                        }else if(lastDate.equals(secondLastDate)&&(dateOfCurrRecord.before(secondLastDate))){
                            secondLastDate = dateOfCurrRecord;
                        }else{
                            //do nothing
                        }
                    }
                    //------------------------------------------------------------------
                    
                    recordNum = recordNum + 1;
                }
                //变换为秒
                timeSpan = (lastDate.getTime() - secondLastDate.getTime())/1000;
                long day = timeSpan/(24*60*60);
                long hour = timeSpan/(60*60) - day*24;
                long min = timeSpan/60 - day*24*60 - hour*60;
                long sec  = timeSpan - day*24*60*60 - hour * 60 * 60 - min * 60;
                String t = String.valueOf(day) + "天" + String.valueOf(hour) + "时" + String.valueOf(min) + "分" + String.valueOf(sec) + "秒";
                
                averagePrice = totalAmount/recordNum;
                
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                
                
                ArrayList<String> currInfo = new ArrayList();
                currInfo.add(String.valueOf(decimalFormat.format(biggestPrice)));
                currInfo.add(String.valueOf(decimalFormat.format(smallestPrice)));
                currInfo.add(String.valueOf(decimalFormat.format(totalAmount)));
                currInfo.add(String.valueOf(decimalFormat.format(averagePrice)));
                currInfo.add(String.valueOf(recordNum));
                currInfo.add(formatter.format(lastDate));
                currInfo.add(formatter.format(secondLastDate));
                currInfo.add(String.valueOf(timeSpan));
                currInfo.add(t);
                
                result.put(supplierName, currInfo);
            }
            return result;
        }catch(ParseException e){
            e.printStackTrace();
        }
        return null;
    }
    
    private Date transformStringToDate(String stringDate) throws ParseException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = formatter.parse(stringDate);
        return date;
    }
    
    public Map getResult(){
        return this.requiredInfo;
    }
}
