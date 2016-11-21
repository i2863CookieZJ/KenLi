package com.sobey.cloud.webtv.utils;

import java.util.HashMap;
import java.util.Map;

import com.sobey.cloud.webtv.kenli.R;

import android.annotation.SuppressLint;

@SuppressLint("UseSparseArrays")
public class FaceUtil {

	Integer[] faces = {R.drawable.face_b_01,R.drawable.face_b_02,R.drawable.face_b_03,
					   R.drawable.face_b_04,R.drawable.face_b_05,R.drawable.face_b_06,
					   R.drawable.face_b_07,R.drawable.face_b_08,R.drawable.face_b_09,
					   R.drawable.face_b_10,R.drawable.face_b_11,R.drawable.face_b_12,
					   R.drawable.face_b_13,R.drawable.face_b_14,R.drawable.face_b_15,
					   R.drawable.face_b_16,R.drawable.face_b_17,R.drawable.face_b_18,
					   R.drawable.face_b_19,
					   R.drawable.face_m_01,R.drawable.face_m_02,
					   R.drawable.face_m_03,R.drawable.face_m_04,R.drawable.face_m_05,
					   R.drawable.face_m_06,R.drawable.face_m_07,R.drawable.face_m_08,
					   R.drawable.face_m_09,R.drawable.face_m_10,R.drawable.face_m_11,
					   R.drawable.face_m_12,R.drawable.face_m_13,R.drawable.face_m_14,
					   R.drawable.face_m_15,R.drawable.face_m_16,
					   R.drawable.face_y_biggrin,
					   R.drawable.face_y_call,R.drawable.face_y_cry,R.drawable.face_y_curse,
					   R.drawable.face_y_dizzy,R.drawable.face_y_funk,R.drawable.face_y_handshake,
					   R.drawable.face_y_huffy,R.drawable.face_y_hug,R.drawable.face_y_kiss,
					   R.drawable.face_y_lol,R.drawable.face_y_loveliness,R.drawable.face_y_mad,
					   R.drawable.face_y_sad,R.drawable.face_y_shocked,R.drawable.face_y_shutup,
					   R.drawable.face_y_shy,R.drawable.face_y_sleepy,R.drawable.face_y_smile,
					   R.drawable.face_y_sweat,R.drawable.face_y_time,R.drawable.face_y_titter,
					   R.drawable.face_y_tongue,R.drawable.face_y_victory
					   };
	public static Map<String,Integer> defaultFaces = new HashMap<String, Integer>();
	public static Map<String,Integer> coolmonkeyFaces = new HashMap<String, Integer>();
	public static Map<String,Integer> grapemanFaces = new HashMap<String, Integer>();
	
	public static Map<Integer,String> defaultEditFaces = new HashMap<Integer, String>();
	public static Map<Integer,String> coolmonkeyEditFaces = new HashMap<Integer, String>();
	public static Map<Integer,String> grapemanEditFaces = new HashMap<Integer, String>();
	
	public static Map<Integer,String> defaultAppendFaces = new HashMap<Integer, String>();
	public static Map<Integer,String> coolmonkeyAppendFaces = new HashMap<Integer, String>();
	public static Map<Integer,String> grapemanAppendFaces = new HashMap<Integer, String>();
	
	
	
	public static Integer[] defaultFacesResids = {
		R.drawable.face_y_biggrin,R.drawable.face_y_call,R.drawable.face_y_cry,
		R.drawable.face_y_curse,R.drawable.face_y_dizzy,R.drawable.face_y_funk,
		R.drawable.face_y_handshake,R.drawable.face_y_hug,R.drawable.face_y_kiss,
		R.drawable.face_y_lol,R.drawable.face_y_mad,R.drawable.face_y_sad,
		R.drawable.face_y_shocked,R.drawable.face_y_shutup,R.drawable.face_y_shy,
		R.drawable.face_y_sleepy,R.drawable.face_y_smile,R.drawable.face_y_sweat,
		R.drawable.face_y_time,R.drawable.face_y_titter,R.drawable.face_y_tongue,
		R.drawable.face_y_victory,R.drawable.face_y_huffy,R.drawable.face_y_loveliness};
	public static Integer[] coolmonkeyFacesResids = {
		R.drawable.face_m_01,R.drawable.face_m_02,R.drawable.face_m_03,
		R.drawable.face_m_04,R.drawable.face_m_05,R.drawable.face_m_06,
		R.drawable.face_m_07,R.drawable.face_m_08,R.drawable.face_m_09,
		R.drawable.face_m_10,R.drawable.face_m_11,R.drawable.face_m_12,
		R.drawable.face_m_13,R.drawable.face_m_14,R.drawable.face_m_15,
		R.drawable.face_m_16};
	public static Integer[] grapemanFacesResids = {
		R.drawable.face_b_01,R.drawable.face_b_02,R.drawable.face_b_03,
		R.drawable.face_b_04,R.drawable.face_b_05,R.drawable.face_b_06,
		R.drawable.face_b_07,R.drawable.face_b_08,R.drawable.face_b_09,
		R.drawable.face_b_10,R.drawable.face_b_11,R.drawable.face_b_12,
		R.drawable.face_b_13,R.drawable.face_b_14,R.drawable.face_b_15,
		R.drawable.face_b_16,R.drawable.face_b_17,R.drawable.face_b_18,
		R.drawable.face_b_19,R.drawable.face_b_20,R.drawable.face_b_21,
		R.drawable.face_b_22,R.drawable.face_b_23,R.drawable.face_b_24};
	static{
		defaultFaces.put("biggrin", R.drawable.face_y_biggrin);
		defaultFaces.put("call", R.drawable.face_y_call);
		defaultFaces.put("cry", R.drawable.face_y_cry);
		defaultFaces.put("curse", R.drawable.face_y_curse);
		defaultFaces.put("dizzy", R.drawable.face_y_dizzy);
		defaultFaces.put("funk", R.drawable.face_y_funk);
		defaultFaces.put("handshake", R.drawable.face_y_handshake);
		defaultFaces.put("hug", R.drawable.face_y_hug);
		defaultFaces.put("kiss", R.drawable.face_y_kiss);
		defaultFaces.put("lol", R.drawable.face_y_lol);
		defaultFaces.put("mad", R.drawable.face_y_mad);
		defaultFaces.put("sad", R.drawable.face_y_sad);
		defaultFaces.put("shocked", R.drawable.face_y_shocked);
		defaultFaces.put("shutup", R.drawable.face_y_shutup);
		defaultFaces.put("shy", R.drawable.face_y_shy);
		defaultFaces.put("sleepy", R.drawable.face_y_sleepy);
		defaultFaces.put("smile", R.drawable.face_y_smile);
		defaultFaces.put("sweat", R.drawable.face_y_sweat);
		defaultFaces.put("time", R.drawable.face_y_time);
		defaultFaces.put("titter", R.drawable.face_y_titter);
		defaultFaces.put("tongue", R.drawable.face_y_tongue);
		defaultFaces.put("victory", R.drawable.face_y_victory);
		defaultFaces.put("huffy", R.drawable.face_y_huffy);
		defaultFaces.put("loveliness", R.drawable.face_y_loveliness);
		
		defaultAppendFaces.put(R.drawable.face_y_biggrin ,"biggrin");
		defaultAppendFaces.put(R.drawable.face_y_call ,"call");
		defaultAppendFaces.put(R.drawable.face_y_cry ,"cry");
		defaultAppendFaces.put(R.drawable.face_y_curse ,"curse");
		defaultAppendFaces.put(R.drawable.face_y_dizzy ,"dizzy");
		defaultAppendFaces.put(R.drawable.face_y_funk ,"funk");
		defaultAppendFaces.put(R.drawable.face_y_handshake,"handshake");
		defaultAppendFaces.put(R.drawable.face_y_hug,"hug");
		defaultAppendFaces.put(R.drawable.face_y_kiss,"kiss");
		defaultAppendFaces.put(R.drawable.face_y_lol,"lol");
		defaultAppendFaces.put(R.drawable.face_y_mad,"mad");
		defaultAppendFaces.put(R.drawable.face_y_sad,"sad");
		defaultAppendFaces.put(R.drawable.face_y_shocked,"shocked");
		defaultAppendFaces.put(R.drawable.face_y_shutup,"shutup");
		defaultAppendFaces.put(R.drawable.face_y_shy,"shy");
		defaultAppendFaces.put(R.drawable.face_y_sleepy,"sleepy");
		defaultAppendFaces.put(R.drawable.face_y_smile,"smile");
		defaultAppendFaces.put(R.drawable.face_y_sweat,"sweat");
		defaultAppendFaces.put(R.drawable.face_y_time,"time");
		defaultAppendFaces.put(R.drawable.face_y_titter,"titter");
		defaultAppendFaces.put(R.drawable.face_y_tongue,"tongue");
		defaultAppendFaces.put(R.drawable.face_y_victory,"victory");
		defaultAppendFaces.put(R.drawable.face_y_huffy,"huffy");
		defaultAppendFaces.put(R.drawable.face_y_loveliness,"loveliness");
		
		
		
		coolmonkeyFaces.put("01", R.drawable.face_m_01);
		coolmonkeyFaces.put("02", R.drawable.face_m_02);
		coolmonkeyFaces.put("03", R.drawable.face_m_03);
		coolmonkeyFaces.put("04", R.drawable.face_m_04);
		coolmonkeyFaces.put("05", R.drawable.face_m_05);
		coolmonkeyFaces.put("06", R.drawable.face_m_06);
		coolmonkeyFaces.put("07", R.drawable.face_m_07);
		coolmonkeyFaces.put("08", R.drawable.face_m_08);
		coolmonkeyFaces.put("09", R.drawable.face_m_09);
		coolmonkeyFaces.put("10", R.drawable.face_m_10);
		coolmonkeyFaces.put("11", R.drawable.face_m_11);
		coolmonkeyFaces.put("12", R.drawable.face_m_12);
		coolmonkeyFaces.put("13", R.drawable.face_m_13);
		coolmonkeyFaces.put("14", R.drawable.face_m_14);
		coolmonkeyFaces.put("15", R.drawable.face_m_15);
		coolmonkeyFaces.put("16", R.drawable.face_m_16);
		
		coolmonkeyAppendFaces.put(R.drawable.face_m_01,"01");
		coolmonkeyAppendFaces.put(R.drawable.face_m_02,"02");
		coolmonkeyAppendFaces.put(R.drawable.face_m_03,"03");
		coolmonkeyAppendFaces.put(R.drawable.face_m_04,"04");
		coolmonkeyAppendFaces.put(R.drawable.face_m_05,"05");
		coolmonkeyAppendFaces.put(R.drawable.face_m_06,"06");
		coolmonkeyAppendFaces.put(R.drawable.face_m_07,"07");
		coolmonkeyAppendFaces.put(R.drawable.face_m_08,"08");
		coolmonkeyAppendFaces.put(R.drawable.face_m_09,"09");
		coolmonkeyAppendFaces.put(R.drawable.face_m_10,"10");
		coolmonkeyAppendFaces.put(R.drawable.face_m_11,"11");
		coolmonkeyAppendFaces.put(R.drawable.face_m_12,"12");
		coolmonkeyAppendFaces.put(R.drawable.face_m_13,"13");
		coolmonkeyAppendFaces.put(R.drawable.face_m_14,"14");
		coolmonkeyAppendFaces.put(R.drawable.face_m_15,"15");
		coolmonkeyAppendFaces.put(R.drawable.face_m_16,"16");
		
		grapemanFaces.put("01", R.drawable.face_b_01);
		grapemanFaces.put("02", R.drawable.face_b_02);
		grapemanFaces.put("03", R.drawable.face_b_03);
		grapemanFaces.put("04", R.drawable.face_b_04);
		grapemanFaces.put("05", R.drawable.face_b_05);
		grapemanFaces.put("06", R.drawable.face_b_06);
		grapemanFaces.put("07", R.drawable.face_b_07);
		grapemanFaces.put("08", R.drawable.face_b_08);
		grapemanFaces.put("09", R.drawable.face_b_09);
		grapemanFaces.put("10", R.drawable.face_b_10);
		grapemanFaces.put("11", R.drawable.face_b_11);
		grapemanFaces.put("12", R.drawable.face_b_12);
		grapemanFaces.put("13", R.drawable.face_b_13);
		grapemanFaces.put("14", R.drawable.face_b_14);
		grapemanFaces.put("15", R.drawable.face_b_15);
		grapemanFaces.put("16", R.drawable.face_b_16);
		grapemanFaces.put("17", R.drawable.face_b_17);
		grapemanFaces.put("18", R.drawable.face_b_18);
		grapemanFaces.put("19", R.drawable.face_b_19);
		grapemanFaces.put("20", R.drawable.face_b_20);
		grapemanFaces.put("21", R.drawable.face_b_21);
		grapemanFaces.put("22", R.drawable.face_b_22);
		grapemanFaces.put("23", R.drawable.face_b_23);
		grapemanFaces.put("24", R.drawable.face_b_24);
		
		grapemanAppendFaces.put(R.drawable.face_b_01,"01");
		grapemanAppendFaces.put(R.drawable.face_b_02,"02");
		grapemanAppendFaces.put(R.drawable.face_b_03,"03");
		grapemanAppendFaces.put(R.drawable.face_b_04,"04");
		grapemanAppendFaces.put(R.drawable.face_b_05,"05");
		grapemanAppendFaces.put(R.drawable.face_b_06,"06");
		grapemanAppendFaces.put(R.drawable.face_b_07,"07");
		grapemanAppendFaces.put(R.drawable.face_b_08,"08");
		grapemanAppendFaces.put(R.drawable.face_b_09,"09");
		grapemanAppendFaces.put(R.drawable.face_b_10,"10");
		grapemanAppendFaces.put(R.drawable.face_b_11,"11");
		grapemanAppendFaces.put(R.drawable.face_b_12,"12");
		grapemanAppendFaces.put(R.drawable.face_b_13,"13");
		grapemanAppendFaces.put(R.drawable.face_b_14,"14");
		grapemanAppendFaces.put(R.drawable.face_b_15,"15");
		grapemanAppendFaces.put(R.drawable.face_b_16,"16");
		grapemanAppendFaces.put(R.drawable.face_b_17,"17");
		grapemanAppendFaces.put(R.drawable.face_b_18,"18");
		grapemanAppendFaces.put(R.drawable.face_b_19,"19");
		grapemanAppendFaces.put(R.drawable.face_b_20,"20");
		grapemanAppendFaces.put(R.drawable.face_b_21,"21");
		grapemanAppendFaces.put(R.drawable.face_b_22,"22");
		grapemanAppendFaces.put(R.drawable.face_b_23,"23");
		grapemanAppendFaces.put(R.drawable.face_b_24,"24");
		
		defaultEditFaces.put(R.drawable.face_y_smile, ":)");//1
		defaultEditFaces.put(R.drawable.face_y_sad, ":(");//2
		defaultEditFaces.put(R.drawable.face_y_biggrin, ":D");//3
		defaultEditFaces.put(R.drawable.face_y_cry, ":'(");//4
		defaultEditFaces.put(R.drawable.face_y_huffy, ":@");//5
		defaultEditFaces.put(R.drawable.face_y_shocked, ":o");//6
		defaultEditFaces.put(R.drawable.face_y_tongue, ":P");//7
		defaultEditFaces.put(R.drawable.face_y_shy, ":$");//8
		defaultEditFaces.put(R.drawable.face_y_titter, ";P");//9
		defaultEditFaces.put(R.drawable.face_y_sweat, ":L");//10
		defaultEditFaces.put(R.drawable.face_y_mad, ":Q");//11
		defaultEditFaces.put(R.drawable.face_y_lol, ":lol");//12
		defaultEditFaces.put(R.drawable.face_y_loveliness, ":loveliness:");//13
		defaultEditFaces.put(R.drawable.face_y_funk, ":funk:");//14
		defaultEditFaces.put(R.drawable.face_y_curse, ":curse:");//15
		defaultEditFaces.put(R.drawable.face_y_dizzy, ":dizzy:");//16
		defaultEditFaces.put(R.drawable.face_y_shutup, ":shutup:");//17
		defaultEditFaces.put(R.drawable.face_y_sleepy, ":sleepy:");//18
		defaultEditFaces.put(R.drawable.face_y_hug, ":hug:");//19
		defaultEditFaces.put(R.drawable.face_y_victory, ":victory:");//20
		defaultEditFaces.put(R.drawable.face_y_time, ":time:");//21
		defaultEditFaces.put(R.drawable.face_y_kiss, ":kiss:");//22
		defaultEditFaces.put(R.drawable.face_y_handshake, ":handshake");//23
		defaultEditFaces.put(R.drawable.face_y_call, ":call:");//24
		
		coolmonkeyEditFaces.put(R.drawable.face_m_01, "{:2_25:}");
		coolmonkeyEditFaces.put(R.drawable.face_m_02, "{:2_26:}");
		coolmonkeyEditFaces.put(R.drawable.face_m_03, "{:2_27:}");
		coolmonkeyEditFaces.put(R.drawable.face_m_04, "{:2_28:}");
		coolmonkeyEditFaces.put(R.drawable.face_m_05, "{:2_29:}");
		coolmonkeyEditFaces.put(R.drawable.face_m_06, "{:2_30:}");
		coolmonkeyEditFaces.put(R.drawable.face_m_07, "{:2_31:}");
		coolmonkeyEditFaces.put(R.drawable.face_m_08, "{:2_32:}");
		coolmonkeyEditFaces.put(R.drawable.face_m_09, "{:2_33:}");
		coolmonkeyEditFaces.put(R.drawable.face_m_10, "{:2_34:}");
		coolmonkeyEditFaces.put(R.drawable.face_m_11, "{:2_35:}");
		coolmonkeyEditFaces.put(R.drawable.face_m_12, "{:2_36:}");
		coolmonkeyEditFaces.put(R.drawable.face_m_13, "{:2_37:}");
		coolmonkeyEditFaces.put(R.drawable.face_m_14, "{:2_38:}");
		coolmonkeyEditFaces.put(R.drawable.face_m_15, "{:2_39:}");
		coolmonkeyEditFaces.put(R.drawable.face_m_16, "{:2_40:}");
		
		grapemanEditFaces.put(R.drawable.face_b_01, "{:3_41:}");
		grapemanEditFaces.put(R.drawable.face_b_02, "{:3_42:}");
		grapemanEditFaces.put(R.drawable.face_b_03, "{:3_43:}");
		grapemanEditFaces.put(R.drawable.face_b_04, "{:3_44:}");
		grapemanEditFaces.put(R.drawable.face_b_05, "{:3_45:}");
		grapemanEditFaces.put(R.drawable.face_b_06, "{:3_46:}");
		grapemanEditFaces.put(R.drawable.face_b_07, "{:3_47:}");
		grapemanEditFaces.put(R.drawable.face_b_08, "{:3_48:}");
		grapemanEditFaces.put(R.drawable.face_b_09, "{:3_49:}");
		grapemanEditFaces.put(R.drawable.face_b_10, "{:3_50:}");
		grapemanEditFaces.put(R.drawable.face_b_11, "{:3_51:}");
		grapemanEditFaces.put(R.drawable.face_b_12, "{:3_52:}");
		grapemanEditFaces.put(R.drawable.face_b_13, "{:3_53:}");
		grapemanEditFaces.put(R.drawable.face_b_14, "{:3_54:}");
		grapemanEditFaces.put(R.drawable.face_b_15, "{:3_55:}");
		grapemanEditFaces.put(R.drawable.face_b_16, "{:3_56:}");
		grapemanEditFaces.put(R.drawable.face_b_17, "{:3_57:}");
		grapemanEditFaces.put(R.drawable.face_b_18, "{:3_58:}");
		grapemanEditFaces.put(R.drawable.face_b_19, "{:3_59:}");
		grapemanEditFaces.put(R.drawable.face_b_20, "{:3_60:}");
		grapemanEditFaces.put(R.drawable.face_b_21, "{:3_61:}");
		grapemanEditFaces.put(R.drawable.face_b_22, "{:3_62:}");
		grapemanEditFaces.put(R.drawable.face_b_23, "{:3_63:}");
		grapemanEditFaces.put(R.drawable.face_b_24, "{:3_64:}");
	}
}
