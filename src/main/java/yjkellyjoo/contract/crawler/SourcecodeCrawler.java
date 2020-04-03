package yjkellyjoo.contract.crawler;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import yjkellyjoo.contract.service.SourcecodeCrawlerService;

/**
 * 
 * @author yjkellyjoo
 * @since 2020. 3. 17.
 */
@Component("yjkellyjoo.contract.crawler.SourcecodeCrawler")
public class SourcecodeCrawler {

	@Resource(name="yjkellyjoo.contract.service.SourcecodeCrawlerService")
	private SourcecodeCrawlerService sourcecodeCrawlerService;
	
	public void startCrawl() {
		sourcecodeCrawlerService.perform();
	}
	
}
