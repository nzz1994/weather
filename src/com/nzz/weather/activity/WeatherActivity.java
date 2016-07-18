package com.nzz.weather.activity;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.nzz.weather.activity.WeatherActivity;
import com.nzz.weather.R;
import com.nzz.weather.service.AutoUpdateService;
import com.nzz.weather.util.HttpCallbackListener;
import com.nzz.weather.util.HttpUtil;
import com.nzz.weather.util.Utility;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class WeatherActivity extends BaseActivity implements OnClickListener{	
	private LinearLayout weatherInfoLayout;	
	private TextView cityNameText;	
	private TextView publishText;	
	private TextView weatherDespText;	
	private TextView temp1Text;	
	private TextView temp2Text;	
	private TextView currentDateText;
	private Button switchCity;
	private Button refreshWeather;
	private LocationClient mLocationClient;
	private boolean haveGetLocal = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		switchCity = (Button) findViewById(R.id.switch_city);
		switchCity.setOnClickListener(this);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		refreshWeather.setOnClickListener(this);
				
		showWeather();		//显示，不管是否已保存过		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			if (!prefs.getBoolean("city_selected", false)) {			//判断是否保存过数据
				localWeather();									//否，则启动定位并由定位信息请求天气信息
			}
		}
	
	
	//处理由选择城市页面返回的数据
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String selectedCity = data.getStringExtra("selectedCity");
		if(!TextUtils.isEmpty(selectedCity)){			
			if (selectedCity.equals("我的位置")) {
				localWeather();                //为我的位置则定位获得天气数据
			}else{
				queryWeatherInfo(selectedCity);
			}
		}
	}
	
	
	//定位方法
	private void localWeather(){				
		publishText.setText("同步中...");
		weatherInfoLayout.setVisibility(View.INVISIBLE);
		mLocationClient = new LocationClient(getApplicationContext());  
        mLocationClient.registerLocationListener( myListener );
        initLocation();	
        mLocationClient.start();
        haveGetLocal = true; 
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(haveGetLocal){							//判断是否使用过定位，以确定是否要注销监听器并停止定位
		mLocationClient.unRegisterLocationListener(myListener);
		mLocationClient.stop();
		}
	} 
	
	public BDLocationListener myListener = new BDLocationListener() {
		
		
		//获得返回的定位信息并处理
		@Override
		public void onReceiveLocation(BDLocation location) {
				//int r = location.getLocType();
				queryWeatherInfo(location.getLatitude()+":"+location.getLongitude());
			}
		}
	;
    
	
	//初始化定位设置
	private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=0;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死  
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }
			
	
	//点击事件
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.refresh_weather:
			publishText.setText("同步中...");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String cn = prefs.getString("weather_code", "");
			if(!TextUtils.isEmpty(cn)){
				queryWeatherInfo(cn);
			} else {
				localWeather();
			}
			break;
		case R.id.switch_city:
					Intent intent = new Intent(this, ChooseCityActivity.class);
					startActivityForResult(intent, 1);
			break;

		default:
			break;
		}
	}
	
	//查询天气代号所对应的天气
		private void queryWeatherInfo(String countyName) {
			// TODO Auto-generated method stub
			String address = "https://api.thinkpage.cn/v3/weather/daily.json?key=8grk8uebycktak6s&location="+
					java.net.URLEncoder.encode(countyName)+"&language=zh-Hans&unit=c&start=0&days=5" ;
			
			HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(final String response) {
				// TODO Auto-generated method stub
					Utility.handleWeatherResponse(WeatherActivity.this, response);					
					runOnUiThread(new Runnable() {
						public void run() {
							showWeather();							
						}
					});				
			}			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {					
					public void run() {
						// TODO Auto-generated method stub
						publishText.setText("同步失败");
					}
				});
			}
		});
	}

	private void showWeather() {
		// TODO Auto-generated method stub
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1", "")+"℃");
		temp2Text.setText(prefs.getString("temp2", "")+"℃");

		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText(prefs.getString("publish_time", "")+"发布");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		Intent intent = new Intent(this,AutoUpdateService.class);
		startService(intent);
	}	
	
	
}
