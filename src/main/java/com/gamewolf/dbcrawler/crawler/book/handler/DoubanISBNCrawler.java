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
import com.gamewolf.dbcrawler.model.base.Goods;
import com.gamewolf.util.book.ISBNCode;
import com.harmonywisdom.crawler.annotation.PageCrawlerDBSetting;

public class DoubanISBNCrawler extends ParameterizedInitializer{
	
	@PageCrawlerDBSetting(value = "ISBN_DB")
	public DBCrawler crawler;



	public DBCrawler getCrawler() {
		return crawler;
	}

	public void setCrawler(DBCrawler crawler) {
		this.crawler = crawler;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DoubanISBNCrawler main=new DoubanISBNCrawler();
		main.init(args);
		main.run();
		
	}
	
	private void run() {
		int startPage=getPage();
		
		String isbnFile="c:/isbn_db_rest.txt";
		String outFile="c:/isbn_db.txt";
		
		
		try {
			BufferedReader br=new BufferedReader(new FileReader(isbnFile));
			BufferedWriter bw=new BufferedWriter(new FileWriter(outFile));
			String line=null;
			while((line=br.readLine())!=null) {
				if(ISBNCode.IsbnValidation(line)) {
					//System.out.println(line);
					String isbn=line;
					String page="https://book.douban.com/subject_search?search_text=" + isbn; 
					crawler.getCrawler().context.login(page);
					crawler.getCrawler().addPage(page);
					List<Object> list=crawler.getCrawler().crawl(Goods.class);
					Goods search=(Goods) list.get(0);
					String newLine=line+","+search.getName()+","+search.getPrice()+","+search.getLink();
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

