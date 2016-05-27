package com.zf.test;

import com.zf.zson.Zson;
import com.zf.zson.ZsonResult;

public class Demo {

	public static void main(String[] args) {
//		String s = "[{ \"firstName\": \"Eric\", \"lastName\": \"Clapton\", \"instrument\": \"guitar\" },{ \"firstName\": \"Sergei\", \"lastName\": \"Rachmaninoff\", \"instrument\": \"piano\" }] ";
//		String s1 = "[0,1,2,\"[]3\",\"\"]";
//		String s2 = "[\"a\",\"b\",\"c\",\"d\",\"\"]";
		Zson z = new Zson();
//		String j = "{\"a\":[\"a1\"],\"c\\\"b\":{\"a\":1},\"d\":[\"a\",{\"a\":[1,20]},{\"a\":2},\"\"],\"e\":\"b\"}";
		//String j = "{\"status\":true,\"errorCode\":\"0\",\"errorMsg\":\"\",\"content\":{\"requestTimeStamp\":\"10#39#4#874\",\"microphoneOpen\":\"\",\"commandType\":\"NLPCommand\",\"ttsText\":\"正在为您加载\",\"cmdData\":{\"commandType\":\"CommandMusic\",\"data\":{\"musicInfo\":[{\"mediaSessionID\":\"\",\"songID\":\"17033\",\"songSourceType\":\"JuShangNet\",\"songSourceAddr\":\"http://cdnmusic.hezi.360iii.net/hezimusic/17033.mp3\",\"songName\":\"白色风车\",\"artist\":\"周杰伦\",\"format\":\"mp3\",\"duration\":\"272\",\"album_name\":\"依然范特西\",\"streamSize\":\"4253\",\"bitRateMode\":\"\",\"bitRate\":\"128000\",\"channels\":\"Stereo\",\"samplingRate\":\"44100\",\"compressionMode\":\"common\"},{\"mediaSessionID\":\"\",\"songID\":\"17035\",\"songSourceType\":\"JuShangNet\",\"songSourceAddr\":\"http://cdnmusic.hezi.360iii.net/hezimusic/17035.mp3\",\"songName\":\"菊花台\",\"artist\":\"周杰伦\",\"format\":\"mp3\",\"duration\":\"297\",\"album_name\":\"依然范特西\",\"streamSize\":\"4643\",\"bitRateMode\":\"\",\"bitRate\":\"128000\",\"channels\":\"Stereo\",\"samplingRate\":\"44100\",\"compressionMode\":\"common\"},{\"mediaSessionID\":\"\",\"songID\":\"17028\",\"songSourceType\":\"JuShangNet\",\"songSourceAddr\":\"http://cdnmusic.hezi.360iii.net/hezimusic/17028.mp3\",\"songName\":\"千里之外\",\"artist\":\"周杰伦\",\"format\":\"mp3\",\"duration\":\"255\",\"album_name\":\"依然范特西\",\"streamSize\":\"3987\",\"bitRateMode\":\"\",\"bitRate\":\"128000\",\"channels\":\"Stereo\",\"samplingRate\":\"44100\",\"compressionMode\":\"common\"},{\"mediaSessionID\":\"\",\"songID\":\"17031\",\"songSourceType\":\"JuShangNet\",\"songSourceAddr\":\"http://cdnmusic.hezi.360iii.net/hezimusic/17031.mp3\",\"songName\":\"心雨\",\"artist\":\"周杰伦\",\"format\":\"mp3\",\"duration\":\"271\",\"album_name\":\"依然范特西\",\"streamSize\":\"4238\",\"bitRateMode\":\"\",\"bitRate\":\"128000\",\"channels\":\"Stereo\",\"samplingRate\":\"44100\",\"compressionMode\":\"common\"},{\"mediaSessionID\":\"\",\"songID\":\"17037\",\"songSourceType\":\"JuShangNet\",\"songSourceAddr\":\"http://cdnmusic.hezi.360iii.net/hezimusic/17037.mp3\",\"songName\":\"退后\",\"artist\":\"周杰伦\",\"format\":\"mp3\",\"duration\":\"262\",\"album_name\":\"依然范特西\",\"streamSize\":\"4097\",\"bitRateMode\":\"\",\"bitRate\":\"128000\",\"channels\":\"Stereo\",\"samplingRate\":\"44100\",\"compressionMode\":\"common\"}],\"operationName\":\"sequence\"}},\"receiveName\":\"talkservice\",\"requestArriveTimeStamp\":\"10#39#4#879\",\"nlpDetailTimeStamp\":73,\"cmdType\":\"media\",\"responseTimeStamp\":\"10#39#4#957\",\"sequenceId\":\"1\"},\"boxVersion\":\"1.0.0.40\"}";

		//String j1 = "[\"\\\"\"]";
		
//		String j1 = "[{\"data\":[],\"retVal\":-1}]";
//		ZsonResult zr1 = z.parseJson(j);
//		//System.out.println(zr1.getMap("/content/cmdData/data/musicInfo/*[0]"));
//		System.out.println(zr1.getValues("//*[0]"));
//		System.out.println(zr1.getValues("//*[1]"));
//		
//		ZsonResult zr2 = z.parseJson(s);
//		System.out.println(zr2.getValue("/*[1]/firstName"));
//		
//		ZsonResult zr3 = z.parseJson(s1);
//		System.out.println(zr3.getValue("/*[1]"));
//		
//		ZsonResult zr4 = z.parseJson(s2);
//		System.out.println(zr4.getValue("/*[1]"));
		
//		String j1= "[{\"data\":[{\"list\":[{\"carId\":128294,\"carNo\":\"rrt12\",\"carStatus\":0,\"dir\":0,\"lg\":0,\"lt\":0,\"orgId\":8204,\"speed\":0},{\"carId\":128292,\"carNo\":\"tt123\",\"carStatus\":2,\"dir\":0,\"lg\":112.533725,\"lt\":31.19291,\"orgId\":8204,\"speed\":0},{\"carId\":128293,\"carNo\":\"tt2123\",\"carStatus\":0,\"dir\":0,\"lg\":0,\"lt\":0,\"orgId\":8204,\"speed\":0},{\"carId\":128424,\"carNo\":\"vn888888888888289\",\"carStatus\":0,\"dir\":0,\"lg\":0,\"lt\":0,\"orgId\":8203,\"speed\":0},{\"carId\":128423,\"carNo\":\"vn888888888888288\",\"carStatus\":0,\"dir\":0,\"lg\":0,\"lt\":0,\"orgId\":8203,\"speed\":0},{\"carId\":128422,\"carNo\":\"test20160405\",\"carStatus\":0,\"dir\":0,\"lg\":0,\"lt\":0,\"orgId\":8203,\"speed\":0},{\"carId\":128244,\"carNo\":\"vn124578895624567\",\"carStatus\":2,\"dir\":0,\"lg\":120.059358,\"lt\":30.311108,\"orgId\":8203,\"speed\":0},{\"carId\":128245,\"carNo\":\"vn111111111111211\",\"carStatus\":2,\"dir\":60,\"lg\":112.27008,\"lt\":31.283426,\"orgId\":8204,\"speed\":31},{\"carId\":128602,\"carNo\":\"测试222\",\"carStatus\":2,\"dir\":0,\"lg\":120.058906,\"lt\":30.311231,\"orgId\":8203,\"speed\":0},{\"carId\":128246,\"carNo\":\"devtest02\",\"carStatus\":0,\"dir\":0,\"lg\":0,\"lt\":0,\"orgId\":8207,\"speed\":0},{\"carId\":128296,\"carNo\":\"vb123451234512345\",\"carStatus\":0,\"dir\":0,\"lg\":0,\"lt\":0,\"orgId\":8203,\"speed\":0},{\"carId\":128523,\"carNo\":\"tttt123\",\"carStatus\":0,\"dir\":0,\"lg\":0,\"lt\":0,\"orgId\":8203,\"speed\":0},{\"carId\":128589,\"carNo\":\"11111111111111111\",\"carStatus\":0,\"dir\":0,\"lg\":0,\"lt\":0,\"orgId\":8203,\"speed\":0},{\"carId\":128586,\"carNo\":\"4\",\"carStatus\":0,\"dir\":0,\"lg\":0,\"lt\":0,\"orgId\":8203,\"speed\":0},{\"carId\":128587,\"carNo\":\"5\",\"carStatus\":0,\"dir\":0,\"lg\":0,\"lt\":0,\"orgId\":8203,\"speed\":0},{\"carId\":128584,\"carNo\":\"vh003\",\"carStatus\":0,\"dir\":0,\"lg\":0,\"lt\":0,\"orgId\":8207,\"speed\":0},{\"carId\":128585,\"carNo\":\"3\",\"carStatus\":0,\"dir\":0,\"lg\":0,\"lt\":0,\"orgId\":8203,\"speed\":0},{\"carId\":128380,\"carNo\":\"姚大帅\",\"carStatus\":2,\"dir\":0,\"lg\":112.902303,\"lt\":31.65315,\"orgId\":8203,\"speed\":0},{\"carId\":128562,\"carNo\":\"th001\",\"carStatus\":0,\"dir\":0,\"lg\":0,\"lt\":0,\"orgId\":8203,\"speed\":0},{\"carId\":128385,\"carNo\":\"55F0677\",\"carStatus\":2,\"dir\":0,\"lg\":5.4E-5,\"lt\":4.210585,\"orgId\":8205,\"speed\":34},{\"carId\":128384,\"carNo\":\"55F0676\",\"carStatus\":2,\"dir\":0,\"lg\":112.533828,\"lt\":31.192925,\"orgId\":8205,\"speed\":0},{\"carId\":128387,\"carNo\":\"55F0675\",\"carStatus\":2,\"dir\":0,\"lg\":112.53383,\"lt\":31.1929,\"orgId\":8205,\"speed\":0},{\"carId\":128386,\"carNo\":\"55F0679\",\"carStatus\":2,\"dir\":56,\"lg\":112.613071,\"lt\":31.273485,\"orgId\":8205,\"speed\":40},{\"carId\":128343,\"carNo\":\"test\",\"carStatus\":2,\"dir\":3,\"lg\":120.581475,\"lt\":29.993231,\"orgId\":8203,\"speed\":82},{\"carId\":128402,\"carNo\":\"vn123123123121221\",\"carStatus\":2,\"dir\":14,\"lg\":112.800698,\"lt\":31.533956,\"orgId\":8203,\"speed\":14}],\"total\":25}],\"retVal\":1}]";
		
		String j1 = "[-6.00,5.4E-5,6.000,-1,10]";
		
		ZsonResult zr6 = z.parseJson(j1);
		System.out.println(zr6.getValue("/*[0]"));
		
	}

}
