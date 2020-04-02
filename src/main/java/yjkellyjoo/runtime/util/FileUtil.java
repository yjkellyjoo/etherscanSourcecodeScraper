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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 	hyeonggookim
 * @since 	2019. 1. 3.	
 */
@Slf4j
@Component("yjkellyjoo.runtime.util.FileUtil")
public class FileUtil {

	
	@Value("${path.rel.upload.backup}")
	private String pathRelUploadBackup;
	
	/**
	 * 특정 URL로부터 text string 다운로드
	 * @param url
	 * @return
	 */
	public String readStringFromURL(String requestURL) throws IOException {
	    try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
	            StandardCharsets.UTF_8.toString()))
	    {
	        scanner.useDelimiter("\\A");
	        return scanner.hasNext() ? scanner.next() : "";
	    }
	}
	
	/**
	 * 특정 URL로부터 파일 다운로드
	 * @param url
	 * @return
	 */
	public File downloadFileFromUrl(String url, String name) {
		
		log.debug("start download : {}", url);
		File base = new File(System.getProperty("user.dir")).getAbsoluteFile();
		File backupDir = new File(base, pathRelUploadBackup);
		if (backupDir.exists()) {
			backupDir.mkdirs();
		}
		
		log.debug("backupDir : {}", backupDir.getAbsolutePath());
		String fileName = name;
		//String fileName = StringUtil.getFileNameFromUrl(url);
		File destination = new File(base, pathRelUploadBackup+fileName);
		try {
			URL downloadSrc = new URL(url);			
			FileUtils.copyURLToFile(downloadSrc, destination);
		} catch (Exception e) {
			log.error("cannot download file : {}", url);
		}		
		log.info("End download : {}, {}", backupDir.getAbsolutePath(), fileName);
		return destination;
	}
	
	/**
	 * gzip 파일 압축 해제
	 * @param gzip
	 */
	public File extractGzip(File gzip) {
        
		if (!gzip.getPath().endsWith(".gz")) {
			log.error("{} is not gzip file", gzip.getPath());
			return null;
		}
		
		String originalPath = gzip.getPath().substring(0, gzip.getPath().lastIndexOf(".gz"));
		log.info("originalPath : {}", originalPath);
		
        final File newFile = new File(originalPath);
        try (FileInputStream fis = new FileInputStream(gzip);
            GZIPInputStream cin = new GZIPInputStream(fis);
            FileOutputStream out = new FileOutputStream(newFile)) {
            IOUtils.copy(cin, out);
        } catch(Exception e) {
        	log.error("error");
        	e.printStackTrace();
        	return null;
        } finally {
            if (gzip.isFile() && !FileUtils.deleteQuietly(gzip)) {
                log.error("Failed to delete temporary file when extracting 'gz' {}", gzip.toString());
                gzip.deleteOnExit();
            }
        }
        
        return newFile;
    }
	
	/**
	 * zip 파일 압축 해제
	 * @param zip, extension
	 * @return File
	 */
	public File extractZip(File zip, String ext) {
		if (!zip.getPath().endsWith(".zip")) {
			log.error("{} is not zip file", zip.getPath());
			return null;
		}

		String originalPath = zip.getPath().substring(0, zip.getPath().lastIndexOf(".zip")) + "." + ext;
		log.debug("originalPath : {}", originalPath);
		
        final File newFile = new File(originalPath);
        try (FileInputStream fis = new FileInputStream(zip);
        		ZipInputStream cin = new ZipInputStream(fis);
            FileOutputStream out = new FileOutputStream(newFile)) {
        	ZipFile zipFile = new ZipFile(zip.getPath());
    		ZipEntry zipEntry = cin.getNextEntry();
    		InputStream in = zipFile.getInputStream(zipEntry);
            IOUtils.copy(in, out);
            zipFile.close();
        } catch(Exception e) {
        	log.error("error");
        	e.printStackTrace();
        	return null;
        } finally {
            if (zip.isFile() && !FileUtils.deleteQuietly(zip)) {
                log.error("Failed to delete temporary file when extracting 'zip' {}", zip.toString());
                zip.deleteOnExit();
            }
        }
		log.info("End unzip : {}", originalPath);
        return newFile;
	}

	/**
	 * xml 파일 json 오브젝트화
	 * @param xml
	 * @return JSONObject
	 */
	public JSONObject xmlToJson(File xml) {

		if (!xml.getPath().endsWith(".xml")) {
			log.error("{} is not xml file", xml.getPath());
			return null;
		}
		
		JSONObject jsonObject = null;
		try
        {
            String data = FileUtils.readFileToString(xml, "UTF-8");
            String originalPath = xml.getPath();
    		String destDir = originalPath.substring(0, originalPath.lastIndexOf(File.separator));

    		log.debug("destDir is {}", destDir);
    		
            jsonObject = XML.toJSONObject(data);
        } catch (IOException e)
        {
        	log.error("xmlToJson convert error");
            e.printStackTrace();
        }
		
		log.info("End converting XML to JSON ");
		return jsonObject;
	}

}



