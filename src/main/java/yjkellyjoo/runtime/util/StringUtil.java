/**
 * Copyright (c) 2018 IoTcube, Inc.
 * All right reserved.
 *
 * This software is the confidential and proprietary information of IoTcube, Inc.
 * You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with IoTcube, Inc.
*/

package yjkellyjoo.runtime.util;

import java.sql.Timestamp;
import java.util.Iterator;

/**
 * 문자열 처리
 * @author 	hyeonggookim
 * @since 	2019. 1. 3.
 */
public class StringUtil {

	/**
	 * url에서 파일명 추출
	 * @param url
	 * @return
	 */
	public static String getFileNameFromUrl(String url) {

		if (isNull(url) || url.indexOf("/") < 0 || url.endsWith("/")){
			return null;
		}

		return url.substring(url.lastIndexOf("/") + 1);
	}

	/**
	 * 문자열이 비었는지 확인
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str) {
		if (str == null || str.trim().length() < 1){
			return true;
		}
		return false;
	}

	public static boolean notContainNumber(String str) {

		return str.matches("^([^0-9]*)$");
	}

	/**
	 * object == null 일시 null 반환. 이외의 경우 String 문자열 반환
	 * @param object
	 * @return String
	 */
	public static String stringValueOf(Object object) {
    	return object == null ? null : String.valueOf(object);
    }

    /**
     * items 를 seperator로 구분하는 String 을 반환
     * ex) itmes = ["A","B"], seperator = ":" -> "A:B"
     * @param items
     * @param seperator
     * @return
     */
    public static String join(Iterable<?> items, String seperator) {
        Iterator<?> iter = items.iterator();
        if (!iter.hasNext()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        builder.append(iter.next());
        while (iter.hasNext()) {
            builder.append(seperator).append(iter.next());
        }

        return builder.toString();
    }

	/**
	 * groupId를 pom url 형태로 변경
	 * @param groupId
	 * @return
	 */
	public static String convertGroupIdToUrlPath(String groupId) {

		return groupId.replaceAll("\\.", "/");
	}

	/**
	 * productKey 생성
	 * @param groupId
	 * @param artifactId
	 * @return
	 */
	public static String getProductKey(String groupId, String artifactId) {

		return groupId.toLowerCase() + ":" + artifactId.toLowerCase();
	}

	public static String getOnlyAlphabet(String str) {

		return str.replaceAll("[^A-Za-z]+", "");
	}

	/**
	 * Method which converts dateString extracted from the nvd files into Timestamp data format.
	 * @param A String with Date information of format 'yyyy-MM-dd', 'yyyy-MM-ddThh:mmZ', 'yyyy-MM-ddThh:mm:ss.mmmZ', or 'yyyy-MM-ddThh:mm:ss.mmm-HH:MM'
	 * @return Timestamp of format 'yyyy-MM-dd hh:mm:ss.000000'
	 */
	public static Timestamp convertStringToTimestamp(String dateString) {
		Timestamp timestamp = null;
		int len = dateString.length();

		// for CWE, CAPEC of format 'yyyy-MM-dd'
		if (len <= 10) {
	    	timestamp = Timestamp.valueOf(dateString + " 00:00:00.000000");
		}
		// for CVE of format 'yyyy-MM-ddThh:mmZ'
		else if (len <= 17) {
			dateString = dateString.replace('T', ' ');
			dateString = dateString.substring(0,16).concat(":00.000000");
			timestamp = Timestamp.valueOf(dateString);
		}
		// for CPE of format 'yyyy-MM-ddThh:mm:ss.mmmZ', or 'yyyy-MM-ddThh:mm:ss.mmm-HH:MM'
		else {
			dateString = dateString.replace('T', ' ');
			dateString = dateString.substring(0,19).concat(".000000");
			timestamp = Timestamp.valueOf(dateString);
		}
		return timestamp;
	}
}
