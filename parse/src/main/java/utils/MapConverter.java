package utils;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class MapConverter
{
  public String convertMapToJson(Map<Object, Object> mapObject) {
    GsonBuilder gsonMapBuilder = new GsonBuilder();
    
    Gson gsonObject = gsonMapBuilder.create();
    
    return gsonObject.toJson(mapObject);
  }
}
