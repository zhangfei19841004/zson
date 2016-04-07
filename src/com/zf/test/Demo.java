package com.zf.test;

import com.zf.zson.Zson;
import com.zf.zson.ZsonResult;

public class Demo {

	public static void main(String[] args) {
		String s = "[{ \"firstName\": \"Eric\", \"lastName\": \"Clapton\", \"instrument\": \"guitar\" },{ \"firstName\": \"Sergei\", \"lastName\": \"Rachmaninoff\", \"instrument\": \"piano\" }] ";
		String s1 = "[0,1,2,\"[]3\",\"\"]";
		String s2 = "[\"a\",\"b\",\"c\",\"d\",\"\"]";
		Zson z = new Zson();
		//String j = "{\"a\":[\"a\"],\"c\\\"b\":{\"a\":1},\"d\":[\"a\",{\"a\":[1,2]},{\"a\":2},\"\"],\"e\":\"b\"}";
		String j = "{\"status\":true,\"errorCode\":\"0\",\"errorMsg\":\"\",\"content\":{\"requestTimeStamp\":\"10#39#4#874\",\"microphoneOpen\":\"\",\"commandType\":\"NLPCommand\",\"ttsText\":\"正在为您加载\",\"cmdData\":{\"commandType\":\"CommandMusic\",\"data\":{\"musicInfo\":[{\"mediaSessionID\":\"\",\"songID\":\"17033\",\"songSourceType\":\"JuShangNet\",\"songSourceAddr\":\"http://cdnmusic.hezi.360iii.net/hezimusic/17033.mp3\",\"songName\":\"白色风车\",\"artist\":\"周杰伦\",\"format\":\"mp3\",\"duration\":\"272\",\"album_name\":\"依然范特西\",\"streamSize\":\"4253\",\"bitRateMode\":\"\",\"bitRate\":\"128000\",\"channels\":\"Stereo\",\"samplingRate\":\"44100\",\"compressionMode\":\"common\"},{\"mediaSessionID\":\"\",\"songID\":\"17035\",\"songSourceType\":\"JuShangNet\",\"songSourceAddr\":\"http://cdnmusic.hezi.360iii.net/hezimusic/17035.mp3\",\"songName\":\"菊花台\",\"artist\":\"周杰伦\",\"format\":\"mp3\",\"duration\":\"297\",\"album_name\":\"依然范特西\",\"streamSize\":\"4643\",\"bitRateMode\":\"\",\"bitRate\":\"128000\",\"channels\":\"Stereo\",\"samplingRate\":\"44100\",\"compressionMode\":\"common\"},{\"mediaSessionID\":\"\",\"songID\":\"17028\",\"songSourceType\":\"JuShangNet\",\"songSourceAddr\":\"http://cdnmusic.hezi.360iii.net/hezimusic/17028.mp3\",\"songName\":\"千里之外\",\"artist\":\"周杰伦\",\"format\":\"mp3\",\"duration\":\"255\",\"album_name\":\"依然范特西\",\"streamSize\":\"3987\",\"bitRateMode\":\"\",\"bitRate\":\"128000\",\"channels\":\"Stereo\",\"samplingRate\":\"44100\",\"compressionMode\":\"common\"},{\"mediaSessionID\":\"\",\"songID\":\"17031\",\"songSourceType\":\"JuShangNet\",\"songSourceAddr\":\"http://cdnmusic.hezi.360iii.net/hezimusic/17031.mp3\",\"songName\":\"心雨\",\"artist\":\"周杰伦\",\"format\":\"mp3\",\"duration\":\"271\",\"album_name\":\"依然范特西\",\"streamSize\":\"4238\",\"bitRateMode\":\"\",\"bitRate\":\"128000\",\"channels\":\"Stereo\",\"samplingRate\":\"44100\",\"compressionMode\":\"common\"},{\"mediaSessionID\":\"\",\"songID\":\"17037\",\"songSourceType\":\"JuShangNet\",\"songSourceAddr\":\"http://cdnmusic.hezi.360iii.net/hezimusic/17037.mp3\",\"songName\":\"退后\",\"artist\":\"周杰伦\",\"format\":\"mp3\",\"duration\":\"262\",\"album_name\":\"依然范特西\",\"streamSize\":\"4097\",\"bitRateMode\":\"\",\"bitRate\":\"128000\",\"channels\":\"Stereo\",\"samplingRate\":\"44100\",\"compressionMode\":\"common\"}],\"operationName\":\"sequence\"}},\"receiveName\":\"talkservice\",\"requestArriveTimeStamp\":\"10#39#4#879\",\"nlpDetailTimeStamp\":73,\"cmdType\":\"media\",\"responseTimeStamp\":\"10#39#4#957\",\"sequenceId\":\"1\"},\"boxVersion\":\"1.0.0.40\"}";

		String j1 = "[\"\\\"\"]";
		ZsonResult zr1 = z.parseJson(j);
		System.out.println(zr1.getMap("/content/cmdData/data/musicInfo/*[0]"));
		
		ZsonResult zr2 = z.parseJson(s);
		System.out.println(zr2.getValue("/*[1]/firstName"));
		
		ZsonResult zr3 = z.parseJson(s1);
		System.out.println(zr3.getValue("/*[1]"));
		
		ZsonResult zr4 = z.parseJson(s2);
		System.out.println(zr4.getValue("/*[1]"));
		
		ZsonResult zr6 = z.parseJson(j1);
		System.out.println(zr6.getValue("/*[0]"));
		
	}

}
