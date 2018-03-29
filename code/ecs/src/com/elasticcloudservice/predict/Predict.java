package com.elasticcloudservice.predict;

import com.elasticcloudservice.flavor.Flavor;
import com.filetool.util.LogUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Predict {

    public final static String[] FLAVOR_TYPES = {"flavor0",
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
        Integer[][] trainData = new Integer[groups.keySet().size()][FLAVOR_TYPES.length];
        AtomicInteger line = new AtomicInteger(0);
        groups.forEach((key, value) -> {
            for(int i=1;i<FLAVOR_TYPES.length;i++){
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
        for (int i = 0; i < trainData.length; i++) {
            System.out.print("Line:" +(i+1)+ " :    ");
            for (int j = 1; j < trainData[i].length; j++) {
                System.out.print(trainData[i][j] + " ");
            }
            System.out.println();
        }
        for (int i = 1; i < inputContent.length; i++) {
        }

        for (int i = 0; i < history.size(); i++) {
            results[i] = history.get(i);

        }
        return results;
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
