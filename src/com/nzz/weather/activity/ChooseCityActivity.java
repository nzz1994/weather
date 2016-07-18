package com.nzz.weather.activity;

import com.nzz.weather.R;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ChooseCityActivity extends BaseActivity implements OnClickListener{
	
	private Button send;
	private EditText inputCity;
	private ListView cityListView;
	private String[] data = {"我的位置","北京","上海","深圳","广州","昌平"};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_city);
		
		send = (Button) findViewById(R.id.send);
		inputCity = (EditText) findViewById(R.id.input_city);
		cityListView = (ListView) findViewById(R.id.city_list);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChooseCityActivity.this, 
				android.R.layout.simple_expandable_list_item_1, data);
		cityListView.setAdapter(adapter);
		send.setOnClickListener(this);
		cityListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				 String selectedCity =data[position];
				inputCity.setText(selectedCity);
			}
		});
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.send && !TextUtils.isEmpty(inputCity.getText().toString())){
			Intent intent = new Intent();
			intent.putExtra("selectedCity", inputCity.getText().toString());
			setResult(RESULT_OK, intent);
			finish();
		}
	}
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.putExtra("selectedCity", "");
		setResult(RESULT_OK, intent);
		finish();
	}

}
