package yjkellyjoo.contract.crawler;

import java.io.File;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import yjkellyjoo.contract.service.SourcecodeCrawlerService;

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
	
	private static final String CONTRACTDIR = "contracts/";
	
	public void startCrawl() {
		//TODO: sh 실행하기 
		
		
		String filePath = pathRelUploadBackup + CONTRACTDIR;
		Integer fileNumber = new File(filePath).listFiles().length;
		log.info(fileNumber.toString());
		
		sourcecodeCrawlerService.manageContracts(filePath, fileNumber.intValue());
	}
	
}
