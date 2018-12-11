package com.gamewolf.dbcrawler.crawler.book.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.gamewolf.dbcrawler.base.DBCrawler;
import com.gamewolf.dbcrawler.crawler.book.model.MiruanSearch;
import com.gamewolf.dbcrawler.initializer.ParameterizedInitializer;
import com.gamewolf.dbcrawler.model.base.Goods;
import com.gamewolf.util.book.ISBNCode;
import com.harmonywisdom.crawler.annotation.PageCrawlerDBSetting;

public class MiruanISBNCrawler  extends ParameterizedInitializer{
	
	@PageCrawlerDBSetting(value = "ISBN_MIRUAN")
	public DBCrawler crawler;
	
	
	public DBCrawler getCrawler() {
		return crawler;
	}

	public void setCrawler(DBCrawler crawler) {
		this.crawler = crawler;
	}
	
	public static void main(String[] args) {
		MiruanISBNCrawler main=new MiruanISBNCrawler();
		main.init(args);
		main.run();
		
	}

	private void run() {
		
		String isbnFile="c:/isbn_miruan_rest.txt";
		String outFile="c:/isbn_miruan.txt";
		
		try {
			BufferedReader br=new BufferedReader(new FileReader(isbnFile));
			BufferedWriter bw=new BufferedWriter(new FileWriter(outFile));
			String line=null;
			while((line=br.readLine())!=null) {
				if(ISBNCode.IsbnValidation(line)) {
					//System.out.println(line);
					String isbn=line;
					String page="http://isbn.szmesoft.com/isbn/query?isbn=" + isbn; 
					//crawler.getCrawler().context.login(page);
					crawler.getCrawler().addPage(page);
					List<Object> list=crawler.getCrawler().crawl(MiruanSearch.class);
					MiruanSearch search=(MiruanSearch) list.get(0);
					String newLine=search.getIsbn()+","+search.getName().replace(",", "，")+","+search.getAuthor().replace(",", "，")+","+search.getPrice()+","+search.getExpressName().replace(",", "，")+","+search.getBrand().replace(",", "，")+","+search.getWeight().replace(",", "，")+","+search.getSize().replace(",", "，")+","+search.getPages().replace(",", "，")+","+search.getPhotoUrl();
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
