package com.dylan.uiparts.sortlistview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dylan.uiparts.edittext.ClearEditText;
import com.dylan.uiparts.sortlistview.SideBar.OnTouchingLetterChangedListener;
import com.third.library.R;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SortListView extends LinearLayout {

	private Context mContext = null;
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private ClearEditText mClearEditText;
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;
	private PinyinComparator pinyinComparator;
	
	public SortListView(Context context) {
		this(context, null);
	}
	public SortListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}
	
	void initView() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.sortlistview, null);
		addView(view);
		
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			@Override
			public void onTouchingLetterChanged(String s) {
				int position = adapter.getPositionForSection(s.charAt(0));
				if(position != -1){
					sortListView.setSelection(position);
				}
			}
		});
		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		sortListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(mContext, ((SortModel)adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
			}
		});
		
		SourceDateList = new ArrayList<SortModel>();
		Collections.sort(SourceDateList, pinyinComparator);
		adapter = new SortAdapter(mContext, SourceDateList);
		sortListView.setAdapter(adapter);
		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
		mClearEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				filterData(s.toString());
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}
	
	public List<SortModel> filledData(List<String> items){
		List<SortModel> mSortList = new ArrayList<SortModel>();
		for(int i = 0; i < items.size(); i++){
			SortModel sortModel = new SortModel();
			sortModel.setName(items.get(i));
			String pinyin = characterParser.getSelling(items.get(i));
			String sortString = pinyin.substring(0, 1).toUpperCase();
			if (sortString.matches("[A-Z]")){
				sortModel.setSortLetters(sortString.toUpperCase());
			} else{
				sortModel.setSortLetters("#");
			}
			mSortList.add(sortModel);
		}
		return mSortList;
	}
	public List<SortModel> filledData(String[] items){
		List<SortModel> mSortList = new ArrayList<SortModel>();
		for(int i = 0; i < items.length; i++){
			SortModel sortModel = new SortModel();
			sortModel.setName(items[i]);
			String pinyin = characterParser.getSelling(items[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();
			if (sortString.matches("[A-Z]")){
				sortModel.setSortLetters(sortString.toUpperCase());
			} else{
				sortModel.setSortLetters("#");
			}
			mSortList.add(sortModel);
		}
		return mSortList;
	}
	
	private void filterData(String filterStr){
		List<SortModel> filterDateList = new ArrayList<SortModel>();
		if (TextUtils.isEmpty(filterStr)){
			filterDateList = SourceDateList;
		} else{
			filterDateList.clear();
			for(SortModel sortModel : SourceDateList){
				String name = sortModel.getName();
				if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
					filterDateList.add(sortModel);
				}
			}
		}
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}
}
