package com.gamewolf.dbcrawler.crawler.book.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.gamewolf.dbcrawler.base.DBCrawler;
import com.gamewolf.dbcrawler.crawler.book.model.JDCrawlerSearch;
import com.gamewolf.dbcrawler.initializer.ParameterizedInitializer;
import com.gamewolf.util.book.ISBNCode;
import com.harmonywisdom.crawler.annotation.PageCrawlerDBSetting;

public class JDISBNCrawler extends ParameterizedInitializer {
	
	@PageCrawlerDBSetting(value = "ISBN_JD")
	public DBCrawler jdCrawler;


	public void setJdCrawler(DBCrawler jdCrawler) {
		this.jdCrawler = jdCrawler;
	}

	public static void main(String[] args) {
		JDISBNCrawler main=new JDISBNCrawler();
		main.init(args);
		main.run();
	}

	private void run() {
		int startPage=getPage();
		String isbnFile="c:/isbn_jd_rest.txt";
		String outFile="c:/isbn_jd.txt";
		try {
			BufferedReader br=new BufferedReader(new FileReader(isbnFile));
			BufferedWriter bw=new BufferedWriter(new FileWriter(outFile));
			String line=null;
			while((line=br.readLine())!=null) {
				//System.out.println("处理的书isbn  "+line);
				if(ISBNCode.IsbnValidation(line)) {
					//System.out.println(line);
					String isbn=line;
					
					String jdISBNPage="https://search.jd.com/Search?keyword=" + isbn;
					jdCrawler.getCrawler().addPage(jdISBNPage);
					List<Object> list=jdCrawler.getCrawler().crawl(JDCrawlerSearch.class);
					JDCrawlerSearch search=(JDCrawlerSearch) list.get(0);
					String bookName=search.getBookName();
					String bookCover=search.getBookCover();
					String lin=search.getLink();
					String newLine=line+","+bookName+","+bookCover+","+lin;
					System.out.println(newLine);
					bw.append(newLine);
					bw.newLine();
					bw.flush();
					jdCrawler.getCrawler().clearPage();
				}
				
			}
			br.close();
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
