package com.gamewolf.dbcrawler.crawler.book.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.gamewolf.dbcrawler.base.DBCrawler;
import com.gamewolf.dbcrawler.crawler.book.model.AmazonSearch;
import com.gamewolf.dbcrawler.initializer.ParameterizedInitializer;
import com.gamewolf.util.book.ISBNCode;
import com.harmonywisdom.crawler.annotation.PageCrawlerDBSetting;

/**
 * 亚马逊爬虫 失败有反爬取机制
 * @author Administrator
 *
 */
public class AmazonISBNCrawler extends ParameterizedInitializer{
	
	@PageCrawlerDBSetting(value = "ISBN_AMAZON")
	public DBCrawler crawler;

	public DBCrawler getCrawler() {
		return crawler;
	}

	public void setCrawler(DBCrawler crawler) {
		this.crawler = crawler;
	}
	
	
	public static void main(String[] args) {
		AmazonISBNCrawler main=new AmazonISBNCrawler();
		main.init(args);
		main.run();
	}

	private void run() {
		int startPage=getPage();
		System.out.println("==========="+startPage);
		String isbnFile="c:/isbn_amazon_rest.txt";
		String outFile="c:/isbn_amazon.txt";
		
		try {
			BufferedReader br=new BufferedReader(new FileReader(isbnFile));
			BufferedWriter bw=new BufferedWriter(new FileWriter(outFile));
			String line=null;
			while((line=br.readLine())!=null) {
				if(ISBNCode.IsbnValidation(line)) {
					//System.out.println(line);
					String isbn=line;
					String page="https://www.amazon.cn/s/field-keywords=" + isbn; 
					crawler.getCrawler().context.login(page);
					crawler.getCrawler().addPage(page);
					List<Object> list=crawler.getCrawler().crawl(AmazonSearch.class);
					AmazonSearch search=(AmazonSearch) list.get(0);
					String newLine=line+","+search.getBookName()+","+search.getLink();
					System.out.println(newLine);
					bw.append(newLine);
					bw.newLine();
					bw.flush();
					crawler.getCrawler().clearPage();
				}
			}
			bw.close();
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}

}
