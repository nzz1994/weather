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
			JSONObject jsonObject = new JSONObject(response);  			//responseΪ���ص�String��json����
			JSONArray results = jsonObject.getJSONArray("results");		//�õ���Ϊresults��JSONArray
			
			JSONObject location = results.getJSONObject(0).getJSONObject("location"); //�õ�results�����һ�������м�Ϊlocation��JSONObject
			
			JSONObject updateTime = results.getJSONObject(0);//�õ��õ�results�����һ������
			
			JSONArray daily = results.getJSONObject(0).getJSONArray("daily");//�õ�results�����һ�������м�Ϊdaily��JSONArray
			
			JSONObject today = daily.getJSONObject(0);//�õ�daily�н������������
			
			
			String cityName = location.getString("name");     //��ó�����
			String weatherCode = location.getString("id");    //��ó���id
			String temp1 = today.getString("low");            //�������¶�
			String temp2 = today.getString("high");           //�������¶�
			String weatherDesp = today.getString("text_day"); //�����������
			String publishTime = updateTime.getString("last_update").substring(11, 16);//��õĸ���ʱ��
			saveWeatherInfo(context, cityName , weatherCode , temp1 , temp2 , weatherDesp , publishTime);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��",Locale.CHINA);
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
