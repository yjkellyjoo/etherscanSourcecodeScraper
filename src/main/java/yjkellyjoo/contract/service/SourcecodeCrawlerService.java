package yjkellyjoo.contract.service;

import com.opencsv.CSVReader;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
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

@Slf4j
@Service("yjkellyjoo.contract.service.SourcecodeCrawlerService")
public class SourcecodeCrawlerService {
	
	@Resource(name="yjkellyjoo.contract.dao.SourcecodeDao")
	private SourcecodeDao sourcecodeDao;
	
	@Resource(name="yjkellyjoo.runtime.util.FileUtil")
	private FileUtil fileUtil;
	
	private String CSV_FILE_PATH = "./export-verified-contractaddress-opensource-license.csv";
	private String URL_FRONT = "https://api.etherscan.io/api?module=contract&action=getsourcecode&address=";
	private String URL_BACK = "&apikey=W1R717YYPKPI2I7BVU7VS8Q4WWS5QYS4I1";
	
	
	public void perform() {
		ArrayList<String> urls = new ArrayList<String>();
		
		// get URLs
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
	
		// get Data from web
		List<SourcecodeVo> contractArray = new ArrayList<SourcecodeVo>();
		SourcecodeVo contract = null;
		JSONObject jsonText = null;
		int count = 0;
		
		for (String url : urls) {
			try {
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

			// write data into DB
			if (contract != null) {
				if (!contract.getSourceCode().isEmpty()) {
					contractArray.add(contract);								
					count++;
					log.debug("inserted : {}", contract.getAddress());					
				}
			}
			if (count % 1500 == 1499) {
				sourcecodeDao.insertAllData(contractArray);
				contractArray.clear();
			}
		}

		if (!contractArray.isEmpty()) {
			sourcecodeDao.insertAllData(contractArray);
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
		contract.setOptimizationUsed(Integer.parseInt(resultObject.get("OptimizationUsed").toString()));
		contract.setRuns(Integer.parseInt(resultObject.get("Runs").toString()));
		contract.setSourceCode(resultObject.get("SourceCode").toString());
		contract.setSwarmSource(resultObject.get("SwarmSource").toString());

		return contract;
	}
	
	
//	private String getURL() {
//		String url = null;
//		
//		
//		return url;
//	}
//	
//	private String getData() {
//		String result = null;
//		
//		return result;
//	}
//	
//	private void writeData() {
//		
//		return;
//	}

}
