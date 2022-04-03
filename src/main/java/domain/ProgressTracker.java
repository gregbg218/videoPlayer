package domain;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ProgressTracker implements Serializable{
private Map<String, Float> progressMap;

    public ProgressTracker()
    {
     progressMap=new HashMap<String, Float>();
    }

    public float getProgress(String fileName)
    {
        return progressMap.get(fileName);
    }

    public void addProgress(String fileName,Float position)
    {
        progressMap.remove(fileName);
        progressMap.put(fileName,position);
    }

    public boolean checkProgressMap(String fileName)
    {
       if(progressMap.containsKey(fileName))
           return true;
       return false;
    }


}
