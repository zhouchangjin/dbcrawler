package com.gamewolf.dbcrawler.crawler.book.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.gamewolf.dbcrawler.base.DBCrawler;
import com.gamewolf.dbcrawler.initializer.ParameterizedInitializer;
import com.gamewolf.dbcrawler.model.base.Search;
import com.gamewolf.util.book.ISBNCode;
import com.harmonywisdom.crawler.annotation.PageCrawlerDBSetting;

/**
 * 
 * @author Administrator
 *
 */
public class TianmaoISBNCrawler extends ParameterizedInitializer{
	
	@PageCrawlerDBSetting(value = "ISBN_TIANMAO")
	public DBCrawler crawler;

	public DBCrawler getCrawler() {
		return crawler;
	}

	public void setCrawler(DBCrawler crawler) {
		this.crawler = crawler;
	}
	
	
	public static void main(String[] args) {
		TianmaoISBNCrawler main=new TianmaoISBNCrawler();
		main.init(args);
		main.run();
	}

	private void run() {
		int startPage=getPage();
		
		String isbnFile="c:/isbn_tianmao_rest.txt";
		String outFile="c:/isbn_tianmao.txt";
		
		
		try {
			BufferedReader br=new BufferedReader(new FileReader(isbnFile));
			BufferedWriter bw=new BufferedWriter(new FileWriter(outFile));
			String line=null;
			while((line=br.readLine())!=null) {
				if(ISBNCode.IsbnValidation(line)) {
					//System.out.println(line);
					String isbn=line;
					String page="https://list.tmall.com/search_product.htm?q=" + isbn; 
					crawler.getCrawler().context.login(page);
					crawler.getCrawler().addPage(page);
					List<Object> list=crawler.getCrawler().crawl(Search.class);
					Search search=(Search) list.get(0);
					String newLine=line+","+search.getName()+","+search.getLink();
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
