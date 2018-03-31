package com.elasticcloudservice.predict;

import com.elasticcloudservice.flavor.Flavor;
import com.filetool.util.LogUtil;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Predict {

    public final static String[] FLAVOR_TYPES = {
            "flavor1",
            "flavor2",
            "flavor3", "flavor4", "flavor5", "flavor6",
            "flavor7", "flavor8", "flavor9", "flavor10", "flavor11", "flavor12", "flavor13", "flavor14", "flavor15"};

    public static String[] predictVm(String[] ecsContent, String[] inputContent) {

        /** =========do your work here========== **/
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String[] results = new String[ecsContent.length];

        List<String> history = new ArrayList<String>();

        List<Flavor> flavors = new ArrayList<>();

        for (int i = 1; i < ecsContent.length; i++) {

            if (ecsContent[i].contains(" ")
                    && ecsContent[i].split(" ").length == 3) {
                String[] array = ecsContent[i].split(" ");
                String uuid = array[0];
                String flavorName = array[1];
                String createTime = array[2];
                history.add(uuid + " " + flavorName + " " + createTime);
            } else if (ecsContent[i].contains("\t")) {
                String[] array = ecsContent[i].split("\t");
                String uuid = array[0];
                String flavorName = array[1];
                String createTime = array[2];
                try {
                    String[] dayAndTime = createTime.split(" ");
                    flavors.add(new Flavor((uuid), flavorName, format.parse(createTime), dayAndTime[0], dayAndTime[1]));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        Map<String, Map<String, List<Flavor>>> groups = group(flavors);
        int[][] trainData = transfer(groups);

//        for (int i = 0; i < trainData.length; i++) {
//            System.out.print("Line:" +(i+1)+ " :    ");
//            for (int j = 0; j < trainData[i].length; j++) {
//                System.out.print(trainData[i][j] + " ");
//            }
//            System.out.println();
//        }

//        LinearRegression regression = new LinearRegression(trainData,15,10,0.001,1000);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String time =  df.format(new Date());
        String filepath =  "C:\\Users\\17262\\Desktop\\软件精英挑战赛\\log\\log"+time+".txt";
        try {
            System.setOut(new PrintStream(new BufferedOutputStream(
                    new FileOutputStream(filepath)),true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("输入矩阵为：");
        for(int i = 0;i<trainData.length;i++){
            for(int j=0;j<trainData[i].length;j++){
                System.out.print(trainData[i][j]+" ");
            }
            System.out.println();
        }

        System.out.println("\r\n以下为训练部分");
        int flavorNum = 15; // 初赛15个flavor
        int dayForPredict = 20; //参与线性回归的参数 建议值：20
        double alpha = 0.001;   //更新的步长 建议值：0.001
        int iteration = 150000;     //迭代的次数 建议值：100000
        LinearRegression_1 regression = new LinearRegression_1(trainData,flavorNum,dayForPredict,alpha,iteration);
        regression.trainTheta();
        double [][] predicResults =  regression.predict(7);
        double[] finalPredict = new  double[flavorNum];
        for(int i = 0;i<predicResults.length;i++){
            for(int j = 0;j<predicResults[i].length;j++){
                if(i==0){
                    finalPredict[j] = predicResults[i][j];
                }else{
                    finalPredict[j]+=predicResults[i][j];
                }
            }
        }
        System.out.println("预测结果如下");
        for(int i = 0;i<flavorNum;i++){
            System.out.println("flavor"+(i+1)+": "+finalPredict[i]);
        }



//        for (int i = 0; i < predicResults.length; i++) {
//            System.out.print("Line:" +(i+1)+ " :    ");
//            for (int j = 0; j < predicResults[i].length; j++) {
//                System.out.print(predicResults[i][j] + " ");
//            }
//            System.out.println();
//        }
        for (int i = 1; i < inputContent.length; i++) {
        }

        for (int i = 0; i < history.size(); i++) {
            results[i] = history.get(i);

        }
        return results;
    }

    private static int[][] transfer(Map<String, Map<String, List<Flavor>>> groups) {
        int[][] trainData = new int[groups.keySet().size()][FLAVOR_TYPES.length];
        AtomicInteger line = new AtomicInteger(0);
        groups.forEach((key, value) -> {
            for(int i=0;i<FLAVOR_TYPES.length;i++){
                List<Flavor> flavorList = value.get(FLAVOR_TYPES[i]);
                if (flavorList == null) {
                    trainData[line.get()][i] = 0;
                }
                else{
                    trainData[line.get()][i] = flavorList.size();
                }
            }
            line.set(line.get() + 1);
        });
        return trainData;
    }

    private static Map<String, Map<String, List<Flavor>>> group(List<Flavor> flavors) {
        Map<String, Map<String, List<Flavor>>> groups = new HashMap<>();
        Map<String, List<Flavor>> finalMap = flavors.stream().collect(Collectors.groupingBy(Flavor::getDay));
        Map<String, Map<String, List<Flavor>>> finalGroups = new LinkedHashMap<>();
        finalMap.forEach((date, flavors1) -> {
            //按节点类型分类
            Map<String, List<Flavor>> type = flavors1.stream().collect(Collectors.groupingBy(Flavor::getFlavorName));
            groups.put(date, type);
        });
        //按日期分类
        groups.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(e -> finalGroups.put(e.getKey(), e.getValue()));
        return finalGroups;
    }
}
