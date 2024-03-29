package yjkellyjoo.contract.service;


import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import yjkellyjoo.contract.dao.SourcecodeDao;
import yjkellyjoo.contract.model.SourcecodeVo;
import yjkellyjoo.runtime.util.FileUtil;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;


/**
 * 
 * @author 	yjkellyjoo
 * @since	2020. 04. 03.
 */
@SuppressWarnings({"TryWithIdenticalCatches", "StringBufferReplaceableByString", "StringBufferMayBeStringBuilder", "SpellCheckingInspection"})
@Slf4j
@Service("yjkellyjoo.contract.service.SourcecodeCrawlerService")
public class SourcecodeCrawlerService {

	@Resource(name="yjkellyjoo.contract.dao.SourcecodeDao")
	private SourcecodeDao sourcecodeDao;
	
	@Resource(name="yjkellyjoo.runtime.util.FileUtil")
	private FileUtil fileUtil;
	
	private static final String URL_FRONT = "https://api.etherscan.io/api?module=contract&action=getsourcecode&address=";
	private static final String URL_BACK = "&apikey=W1R717YYPKPI2I7BVU7VS8Q4WWS5QYS4I1";

	
	public void manageContracts(String filePath) {
		// get balance Map
		Map<String, String> balances = getBalanceMap(filePath);

		// get URLs
		Map<String, String> urls = this.getJsonURL(filePath);

		// remove already existing URLs
		urls = this.postProcessURL(urls);

		// get Data from web
		Map<String, SourcecodeVo> contractArray = this.getData(balances, urls);
		log.debug("{}", contractArray.size());

		// insert data
		this.insertAllData(contractArray);
	}

	/*
	 * convert JSON into Map
	 * @param filePath
	 * @return balanceMap
	 */
	private Map<String, String> getBalanceMap(String filePath){
		Map<String, String> balances = new HashMap<>();
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			String line = reader.readLine();
			while (line != null) {
				JSONObject obj = new JSONObject(line);
				String address = obj.get("address").toString();
				String balance = obj.get("eth_balance").toString();
				balances.put(address, balance);

				try {
					// read next line
					line = reader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			reader.close();
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (JSONException e3) {
			e3.printStackTrace();
		}
		
		return balances;
	}


	/*
	 * insert all data into Database
	 * @param list of contracts information in Sourcecode VO
	 */
	private void insertAllData(Map<String, SourcecodeVo> contractArray) {
		List<SourcecodeVo> insertArray = new ArrayList<>();
		List<SourcecodeVo> updateArray = new ArrayList<>();

		if (!contractArray.isEmpty()) {
			for (String address: contractArray.keySet()){
				SourcecodeVo contract;
				contract = sourcecodeDao.selectData(address);
				// if address not in DB, insert
				if (Objects.isNull(contract)){
					contract = contractArray.get(address);					
					insertArray.add(contract);
					log.debug("address not in DB: {}", address);
				}

				// if address in DB, update balance
				else {
					contract = contractArray.get(address);
					updateArray.add(contract);
					log.debug("update contract info {}, {} ", contract.getAddress(), contract.getBalance());
				}
			}

			if (!insertArray.isEmpty()) {
				try {
					sourcecodeDao.insertAllData(insertArray);
					log.info("  ---\tInserted Data\t  ---");
				} catch (DataIntegrityViolationException e) {
					log.error("ERROR while insertAllData");
				}				
			}
			
			if (!updateArray.isEmpty()) {
				try {
					sourcecodeDao.updateDataList(updateArray);
					log.info("  ---\tUpdated Data\t  ---");
				} catch (DataIntegrityViolationException e) {
					log.error("ERROR while updateDataList");
				}
			}

		}
	}
	

	/*
	 * Set one Contract Data info into one SourcecodeVo object
	 * @param JSONObject containing Contract Data, String with the Contract Address
	 * @return SourcecodeVo object
	 */
	private SourcecodeVo setOneData(JSONObject resultObject, String balance, String address) {
		SourcecodeVo contract = new SourcecodeVo();
		try {
			contract.setAbi(resultObject.get("ABI").toString());
			contract.setAddress(address);
			contract.setCompilerVersion(resultObject.get("CompilerVersion").toString());
			contract.setConstructorArguments(resultObject.get("ConstructorArguments").toString());
			contract.setContractName(resultObject.get("ContractName").toString());
//			contract.setLibrary(resultObject.get("Library").toString());
			contract.setLicenseType(resultObject.get("LicenseType").toString());
//			if (!resultObject.get("OptimizationUsed").toString().isEmpty()) {
//				contract.setOptimizationUsed(Integer.parseInt(resultObject.get("OptimizationUsed").toString()));
//			}
//			if (!resultObject.get("Runs").toString().isEmpty()) {
//				contract.setRuns(Integer.parseInt(resultObject.get("Runs").toString()));
//			}
			contract.setSourceCode(resultObject.get("SourceCode").toString());
			contract.setSwarmSource(resultObject.get("SwarmSource").toString());
			contract.setBalance(balance);

			return contract;
		} catch (JSONException e1) {
			e1.printStackTrace();
			return null;
		}
	}
	
	
	/*
	 * make URLs from address information in CSV file
	 * @return ArrayList containing url strings
	 */
//	private ArrayList<String> getCsvURL() {
//		ArrayList<String> urls = new ArrayList<String>();
//		
//		try(Reader reader = Files.newBufferedReader(Paths.get(CSV_FILE_PATH));
//				CSVReader csvReader = new CSVReader(reader);){
//				
//				List<String[]> records = csvReader.readAll();
//				for (String[] record : records) {
//					String address = record[1];
//					StringBuffer url = new StringBuffer();
//					url.append(URL_FRONT);
//					url.append(address);
//					url.append(URL_BACK);
//					
//					urls.add(url.toString());
//				}
//				
//			} catch (IOException e) {
//				log.error("csv file read error.");
//				e.printStackTrace();
//			}	
//		
//		return urls;
//	}

	
	/*
	 * get URLs from a json file
	 * @param filePath
	 * @return
	 */
	private Map<String, String> getJsonURL(String filePath) {
		Map<String, String> urls = new HashMap<>();

		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			String line = reader.readLine();
			while (line != null) {
				JSONObject obj = new JSONObject(line);
				String address = obj.get("address").toString();
			
				StringBuffer url = new StringBuffer();
				url.append(URL_FRONT);
				url.append(address);
				url.append(URL_BACK);

				urls.put(address, url.toString());

				try {
					// read next line
					line = reader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			reader.close();
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		return urls;
	}


	/*
	 * remove urls of addresses that we already have the source code of.
	 * @param	List of urls
	 * @result	List of processed urls
	 */
	private Map<String, String> postProcessURL(Map<String, String> urls) {
		Map<String, String> postUrls = new HashMap<>(urls);

		for (String address : urls.keySet()) {
			SourcecodeVo sourcecodeVo = sourcecodeDao.selectData(address);
			if (sourcecodeVo != null) {
				postUrls.remove(address);
			}
		}

		return postUrls;
	}


	/*
	 * get data from the urls and write the result into an array
	 * @param	List of urls
	 * @result	List of contract information in the form of SourcecodeVo
	 */
	private Map<String, SourcecodeVo> getData(Map<String, String> balanceMap, Map<String, String> urls) {
		Map<String, SourcecodeVo> contractArray = new HashMap<>();
		Map<String, String> urls_copy = new HashMap<>(urls);

		while (!urls_copy.isEmpty()) {
		int count = 1;
			for (String address : urls.keySet()) {
				SourcecodeVo contract = null;

				try {
					JSONObject jsonText;
					String rawText = fileUtil.readStringFromURL(urls.get(address));
					jsonText = new JSONObject(rawText);

					if (jsonText.get("result").getClass() == JSONArray.class) {
						JSONObject resultObject = jsonText.getJSONArray("result").getJSONObject(0);
						if (Objects.nonNull(resultObject)) {
							log.debug(address);

							contract = this.setOneData(resultObject, balanceMap.get(address), address);
							log.info("{} processed ", count);
							urls_copy.remove(address);
							count++;
//							log.info(resultObject.toString());
						}
					}

					// in case etherscan.io API time limit is reached, reinquire.
					else if(jsonText.get("result").toString().contains("Max rate limit reached")) {
						continue;
					}

					// log error info into error.log
					else {
						String fileName = System.getProperty("user.dir");
						String resultText = jsonText.get("result").toString();

						FileOutputStream fos = new FileOutputStream(fileName + "/error.log", true);
						DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(fos));
						outStream.writeUTF(resultText + '\t' + address + '\n');
						outStream.close();

						log.error("{} error detected", address);
						urls_copy.remove(address);
					}

				} catch (IOException e) {
					log.error("reading text from URL error");
					e.printStackTrace();
//					log.debug(contract.getAddress());
				} catch (JSONException e) {
					log.error("JSON conversion error");
					e.printStackTrace();
//					log.debug(contract.getAddress());
				}

				// write data with source code into array
				if (contract != null) {
					if (!contract.getSourceCode().isEmpty()) {
						contractArray.put(address, contract);
						log.info("added : {}", contract.getAddress());
					}
				}
			}

		urls = new HashMap<>(urls_copy);
		}
		return contractArray;
	}
	
}
