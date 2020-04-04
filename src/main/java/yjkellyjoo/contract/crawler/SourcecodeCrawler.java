package yjkellyjoo.contract.crawler;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import yjkellyjoo.contract.service.SourcecodeCrawlerService;
import yjkellyjoo.runtime.util.FileUtil;

/**
 * 
 * @author yjkellyjoo
 * @since 2020. 3. 17.
 */
@Slf4j
@Component("yjkellyjoo.contract.crawler.SourcecodeCrawler")
public class SourcecodeCrawler {
	
	@Value("${path.rel.upload.backup}")
	private String pathRelUploadBackup;
	
	@Resource(name="yjkellyjoo.contract.service.SourcecodeCrawlerService")
	private SourcecodeCrawlerService sourcecodeCrawlerService;
	
	@Resource(name="yjkellyjoo.runtime.util.FileUtil")
	private FileUtil fileUtil;
	
	private static final String CONTRACTDIR = "contracts/";
	private static final String CONTRACTDONE = "contracts_done/";
	
	public void startCrawl() {
		
		String filePath = pathRelUploadBackup + CONTRACTDIR;
		String desPath =  pathRelUploadBackup + CONTRACTDONE;
		Integer fileNumber = new File(filePath).listFiles().length;
		log.info("  ---\t number of files... {}", fileNumber.toString());
		
		// get URLs from csv file
//		ArrayList<String> urls = this.getCsvURL();
	
		// process json files one by one
		for (int i = 1; i <= fileNumber.intValue(); i++) {
			sourcecodeCrawlerService.manageContracts(filePath + i + ".json");
			
			// move the finished file into a different folder
			try {
				fileUtil.moveFile(filePath + i + ".json", desPath + i + ".json");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
}
