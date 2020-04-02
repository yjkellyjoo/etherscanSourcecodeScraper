package yjkellyjoo;

import javax.annotation.Resource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import yjkellyjoo.contract.service.SourcecodeCrawlerService;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
public class EtherscanSourcecodeScraperApplication implements CommandLineRunner {

	@Resource(name="yjkellyjoo.contract.service.SourcecodeCrawlerService")
	private SourcecodeCrawlerService sourcecodeCrawlerService;
	
	public static void main(String[] args) {
		SpringApplication.run(EtherscanSourcecodeScraperApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		sourcecodeCrawlerService.perform();
	}
}
