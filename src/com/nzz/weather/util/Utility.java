package com.nzz.weather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;



public class Utility {
	
	
	
	public static void handleWeatherResponse(Context context , String response){
		try {
			JSONObject jsonObject = new JSONObject(response);  			//response为返回的String型json数据
			JSONArray results = jsonObject.getJSONArray("results");		//得到键为results的JSONArray
			
			JSONObject location = results.getJSONObject(0).getJSONObject("location"); //得到results数组第一个数据中键为location的JSONObject
			
			JSONObject updateTime = results.getJSONObject(0);//得到得到results数组第一个数据
			
			JSONArray daily = results.getJSONObject(0).getJSONArray("daily");//得到results数组第一个数据中键为daily的JSONArray
			
			JSONObject today = daily.getJSONObject(0);//得到daily中今天的天气数据
			
			
			String cityName = location.getString("name");     //获得城市名
			String weatherCode = location.getString("id");    //获得城市id
			String temp1 = today.getString("low");            //获得最低温度
			String temp2 = today.getString("high");           //获得最高温度
			String weatherDesp = today.getString("text_day"); //获得天气描述
			String publishTime = updateTime.getString("last_update").substring(11, 16);//获得的更新时间
			saveWeatherInfo(context, cityName , weatherCode , temp1 , temp2 , weatherDesp , publishTime);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}	
}
