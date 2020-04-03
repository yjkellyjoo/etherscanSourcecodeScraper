package yjkellyjoo;

import javax.annotation.Resource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import yjkellyjoo.contract.crawler.SourcecodeCrawler;

@SpringBootApplication
//@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@EnableAutoConfiguration
public class EtherscanSourcecodeScraperApplication implements CommandLineRunner {

	@Resource(name="yjkellyjoo.contract.crawler.SourcecodeCrawler")
	private SourcecodeCrawler sourcecodeCrawler;
	
	
	public static void main(String[] args) {
		SpringApplication.run(EtherscanSourcecodeScraperApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		sourcecodeCrawler.startCrawl();
	}
}
