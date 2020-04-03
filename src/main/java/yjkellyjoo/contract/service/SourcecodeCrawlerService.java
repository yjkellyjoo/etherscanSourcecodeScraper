package yjkellyjoo.contract.service;

import com.opencsv.CSVReader;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import yjkellyjoo.contract.dao.SourcecodeDao;
import yjkellyjoo.contract.model.SourcecodeVo;
import yjkellyjoo.runtime.util.FileUtil;


/**
 * 
 * @author 	yjkellyjoo
 * @since	2020. 04. 03.
 */
@Slf4j
@Service("yjkellyjoo.contract.service.SourcecodeCrawlerService")
public class SourcecodeCrawlerService {
	
	@Resource(name="yjkellyjoo.contract.dao.SourcecodeDao")
	private SourcecodeDao sourcecodeDao;
	
	@Resource(name="yjkellyjoo.runtime.util.FileUtil")
	private FileUtil fileUtil;
	
	private static final String CSV_FILE_PATH = "./export-verified-contractaddress-opensource-license.csv";
	private static final String JSON_FILE_PATH = "./bq-results-20200401-171150-vp6f1zm8u2mt.json";
	private static final String URL_FRONT = "https://api.etherscan.io/api?module=contract&action=getsourcecode&address=";
	private static final String URL_BACK = "&apikey=W1R717YYPKPI2I7BVU7VS8Q4WWS5QYS4I1";
	
	
	public void perform() {
		
		// get URLs from csv file
//		ArrayList<String> urls = this.getCsvURL();
	
		// get URLs from json file, 1500 at a time	
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(JSON_FILE_PATH));
			String line = reader.readLine();
			while (line != null) {
				List<String> urls = this.getJsonURL(reader, line);
				
				// get Data from web
				List<SourcecodeVo> contractArray = this.getData(urls);
				// insert data
				if (!contractArray.isEmpty()) {
					try {
						int _index = 0;
						for (SourcecodeVo sourcecodeVo : contractArray) {
							System.out.println(_index++);
							log.info(sourcecodeVo.getAddress());
						}
						sourcecodeDao.insertAllData(contractArray);
					} catch(NullPointerException e) {
						e.printStackTrace();
					}
				}
				line = reader.readLine();
			}
			reader.close();
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}


	/**
	 * Set one Contract Data info into one SourcecodeVo object
	 * @param JSONObject containing Contract Data, String with the Contract Address
	 * @return SourcecodeVo object
	 */
	private SourcecodeVo setOneData(JSONObject resultObject, String address) {
		SourcecodeVo contract = new SourcecodeVo();
		
		contract.setAbi(resultObject.get("ABI").toString());
		contract.setAddress(address);
		contract.setCompilerVersion(resultObject.get("CompilerVersion").toString());
		contract.setConstructorArguments(resultObject.get("ConstructorArguments").toString());
		contract.setContractName(resultObject.get("ContractName").toString());
		contract.setLibrary(resultObject.get("Library").toString());
		contract.setLicenseType(resultObject.get("LicenseType").toString());
		if (!resultObject.get("OptimizationUsed").toString().isEmpty()) {
			contract.setOptimizationUsed(Integer.parseInt(resultObject.get("OptimizationUsed").toString()));			
		}
		if (!resultObject.get("Runs").toString().isEmpty()) {
			contract.setRuns(Integer.parseInt(resultObject.get("Runs").toString()));			
		}
		contract.setSourceCode(resultObject.get("SourceCode").toString());
		contract.setSwarmSource(resultObject.get("SwarmSource").toString());

		return contract;
	}
	
	
	/**
	 * make URLs from address information in CSV file
	 * @return ArrayList containing url strings
	 */
	private ArrayList<String> getCsvURL() {
		ArrayList<String> urls = new ArrayList<String>();
		
		try(Reader reader = Files.newBufferedReader(Paths.get(CSV_FILE_PATH));
				CSVReader csvReader = new CSVReader(reader);){
				
				List<String[]> records = csvReader.readAll();
				for (String[] record : records) {
					String address = record[1];
					StringBuffer url = new StringBuffer();
					url.append(URL_FRONT);
					url.append(address);
					url.append(URL_BACK);
					
					urls.add(url.toString());
				}
				
			} catch (IOException e) {
				log.error("csv file read error.");
				e.printStackTrace();
			}	
		
		return urls;
	}

	
	/**
	 * get 1500 URLs from a json file
	 * @param	BufferedReader of the file
	 * @param	first line from the Reader
	 * @return	ArrayList containing url strings
	 */
	private List<String> getJsonURL(BufferedReader reader, String line) {
		List<String> urls = new ArrayList<String>();
		int count = 1;
		
		while (line != null) {
			JSONObject obj = new JSONObject(line);
			String address = obj.get("address").toString();
		
			StringBuffer url = new StringBuffer();
			url.append(URL_FRONT);
			url.append(address);
			url.append(URL_BACK);
			
			urls.add(url.toString());
			if (count % 15 == 14) {
				break;
			}
			
			try {
				// read next line
				line = reader.readLine();
				count++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return urls;
	}
	
	/**
	 * get data from the urls and write the result into an array
	 * @param	List of urls
	 * @result	List of contract information in the form of SourcecodeVo
	 */
	private List<SourcecodeVo> getData(List<String> urls) {		
		List<SourcecodeVo> contractArray = new ArrayList<SourcecodeVo>();

		for (String url : urls) {
			SourcecodeVo contract = null;

			try {
				JSONObject jsonText = null;
				String rawText = fileUtil.readStringFromURL(url);
				jsonText = new JSONObject(rawText);
				
				if (jsonText.get("result").getClass() == JSONArray.class) {
					JSONObject resultObject = jsonText.getJSONArray("result").getJSONObject(0);
					if (!resultObject.isEmpty()) {
						int beginIndex = url.indexOf("address") + 8;
						int endIndex = url.indexOf("&apikey");
						String address = url.substring(beginIndex, endIndex);
						log.debug(address);
						
						contract = this.setOneData(resultObject, address);			
//					log.info(resultObject.toString());
					}
				}
				
				// write error info into another file
				else {
					String fileName = System.getProperty("user.dir");
				    String resultText = jsonText.getString("result");
				    
				    FileOutputStream fos = new FileOutputStream(fileName + "errorLog.txt");
				    DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(fos));
				    outStream.writeUTF(resultText);
				    outStream.close();
				}
				
			} catch (IOException e) {
				log.error("reading text from URL error");
				e.printStackTrace();
//				log.debug(contract.getAddress());
			} catch (JSONException e) {
				log.error("JSON conversion error");
				e.printStackTrace();
//				log.debug(contract.getAddress());
			}

			// write data into array
			if (contract != null) {
				if (!contract.getSourceCode().isEmpty()) {
					contractArray.add(contract);								
					log.info("inserted : {}", contract.getAddress());					
				}
			}

		}

		return contractArray;
	}
	
}
