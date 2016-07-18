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
				
		showWeather();		//��ʾ�������Ƿ��ѱ����		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			if (!prefs.getBoolean("city_selected", false)) {			//�ж��Ƿ񱣴������
				localWeather();									//����������λ���ɶ�λ��Ϣ����������Ϣ
			}
		}
	
	
	//������ѡ�����ҳ�淵�ص�����
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String selectedCity = data.getStringExtra("selectedCity");
		if(!TextUtils.isEmpty(selectedCity)){			
			if (selectedCity.equals("�ҵ�λ��")) {
				localWeather();                //Ϊ�ҵ�λ����λ�����������
			}else{
				queryWeatherInfo(selectedCity);
			}
		}
	}
	
	
	//��λ����
	private void localWeather(){				
		publishText.setText("ͬ����...");
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
		if(haveGetLocal){							//�ж��Ƿ�ʹ�ù���λ����ȷ���Ƿ�Ҫע����������ֹͣ��λ
		mLocationClient.unRegisterLocationListener(myListener);
		mLocationClient.stop();
		}
	} 
	
	public BDLocationListener myListener = new BDLocationListener() {
		
		
		//��÷��صĶ�λ��Ϣ������
		@Override
		public void onReceiveLocation(BDLocation location) {
				//int r = location.getLocType();
				queryWeatherInfo(location.getLatitude()+":"+location.getLongitude());
			}
		}
	;
    
	
	//��ʼ����λ����
	private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);//��ѡ��Ĭ�ϸ߾��ȣ����ö�λģʽ���߾��ȣ��͹��ģ����豸
        option.setCoorType("bd09ll");//��ѡ��Ĭ��gcj02�����÷��صĶ�λ�������ϵ
        int span=0;
        option.setScanSpan(span);//��ѡ��Ĭ��0��������λһ�Σ����÷���λ����ļ����Ҫ���ڵ���1000ms������Ч��
        option.setIsNeedAddress(true);//��ѡ�������Ƿ���Ҫ��ַ��Ϣ��Ĭ�ϲ���Ҫ
        option.setOpenGps(true);//��ѡ��Ĭ��false,�����Ƿ�ʹ��gps
        option.setLocationNotify(true);//��ѡ��Ĭ��false�������Ƿ�gps��Чʱ����1S1��Ƶ�����GPS���
        option.setIsNeedLocationDescribe(true);//��ѡ��Ĭ��false�������Ƿ���Ҫλ�����廯�����������BDLocation.getLocationDescribe��õ�����������ڡ��ڱ����찲�Ÿ�����
        option.setIsNeedLocationPoiList(true);//��ѡ��Ĭ��false�������Ƿ���ҪPOI�����������BDLocation.getPoiList��õ�
        option.setIgnoreKillProcess(false);//��ѡ��Ĭ��true����λSDK�ڲ���һ��SERVICE�����ŵ��˶������̣������Ƿ���stop��ʱ��ɱ��������̣�Ĭ�ϲ�ɱ��  
        option.SetIgnoreCacheException(false);//��ѡ��Ĭ��false�������Ƿ��ռ�CRASH��Ϣ��Ĭ���ռ�
        option.setEnableSimulateGps(false);//��ѡ��Ĭ��false�������Ƿ���Ҫ����gps��������Ĭ����Ҫ
        mLocationClient.setLocOption(option);
    }
			
	
	//����¼�
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.refresh_weather:
			publishText.setText("ͬ����...");
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
	
	//��ѯ������������Ӧ������
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
						publishText.setText("ͬ��ʧ��");
					}
				});
			}
		});
	}

	private void showWeather() {
		// TODO Auto-generated method stub
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1", "")+"��");
		temp2Text.setText(prefs.getString("temp2", "")+"��");

		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText(prefs.getString("publish_time", "")+"����");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		Intent intent = new Intent(this,AutoUpdateService.class);
		startService(intent);
	}	
	
	
}
