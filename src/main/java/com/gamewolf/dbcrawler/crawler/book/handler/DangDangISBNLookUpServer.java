package com.gamewolf.dbcrawler.crawler.book.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.gamewolf.dbcrawler.base.DBCrawler;
import com.gamewolf.dbcrawler.crawler.book.model.DDBookFSearch;
import com.gamewolf.dbcrawler.initializer.ParameterizedInitializer;
import com.gamewolf.util.book.ISBNCode;
import com.harmonywisdom.crawler.annotation.PageCrawlerDBSetting;

public class DangDangISBNLookUpServer extends ParameterizedInitializer {
	
	@PageCrawlerDBSetting(value = "ISBN_DD")
	public DBCrawler ddCrawler;
	
	public static void main(String[] args) {
		DangDangISBNLookUpServer main=new DangDangISBNLookUpServer();
		main.init(args);
		main.run();
	}
    
	public DBCrawler getDdCrawler() {
		return ddCrawler;
	}

	public void setDdCrawler(DBCrawler ddCrawler) {
		this.ddCrawler = ddCrawler;
	}

	private void run() {
		// TODO Auto-generated method stub
		int startPage=getPage();
		
		//String isbnFile="c:/isbn_dangdang_rest.txt";  //输入为isbn列表，每行一个isbn
		//String outFile="c:/isbn_dangdang.txt";
		
		String isbnFile=this.params.getStringValue("task_file");
		String outFile=this.params.getStringValue("output_file");
		System.out.println(isbnFile);
		System.out.println(outFile);
		try {
			BufferedReader br=new BufferedReader(new FileReader(isbnFile));
			BufferedWriter bw=new BufferedWriter(new FileWriter(outFile));
			String line=null;
			while((line=br.readLine())!=null) {
				//System.out.println("处理的书isbn  "+line);
				if(ISBNCode.IsbnValidation(line)) {
					//System.out.println(line);
					String isbn=line;
					
					String ddUrl="http://search.dangdang.com/?key=" + isbn;//"https://search.jd.com/Search?keyword=" + isbn;
					ddCrawler.getCrawler().addPage(ddUrl);
					List<Object> list=ddCrawler.getCrawler().crawl(DDBookFSearch.class);
					DDBookFSearch search=(DDBookFSearch) list.get(0);
					String bookName=search.getBookName();
					String bookCover=search.getBookCover();
					String lin=search.getLink();
					String author=search.getAuthor();
					String pressName=search.getPressName();
					String newLine=line+","+bookName+","+bookCover+","+lin+","+author+","+pressName;
					System.out.println(newLine);
					bw.write(newLine);
					bw.newLine();
					bw.flush();
					ddCrawler.getCrawler().clearPage();
					Thread.sleep(300);
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
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
