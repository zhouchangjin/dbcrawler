package com.gamewolf.dbcrawler.crawler.book.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gamewolf.util.book.ISBNCode;

public class ISBNList {

	List<String> pressList;

	public ISBNList() {
		pressList = new ArrayList<String>();
	}

	private void run() {
		String head = "9787";
		String pressListFile = "c:/presslist.txt";
		String isbnAllFile="c:/isbnall.txt";
		try {
			BufferedReader br = new BufferedReader(new FileReader(pressListFile));
			BufferedWriter bw=new BufferedWriter(new FileWriter(isbnAllFile));
			String line = null;
			while ((line = br.readLine()) != null) {
				pressList.add(line);
			}
			br.close();
			int counter=0;
			for (String press : pressList) {
				
				String isbn = head + "" + press;
				int lth = 13 - isbn.length() - 1;
				int max = (int) Math.pow(10, lth);
				//System.out.println(isbn + "   " + isbn.length() + "  " + (13 - isbn.length()) + "  " + max);

				for (int i = 0; i < max; i++) {
					String curIsbn=isbn + addZeroForNum("" + i, lth);
					curIsbn=curIsbn+""+ISBNCode.check(curIsbn);
					counter++;
					//System.out.println(counter+"=="+curIsbn);
					bw.append(curIsbn);
					bw.newLine();
					bw.flush();
				}

			}
			br.close();
			bw.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String addZeroForNum(String str, int strLength) {
		int strLen = str.length();
		if (strLen < strLength) {
			while (strLen < strLength) {
				StringBuffer sb = new StringBuffer();
				sb.append("0").append(str);
				str = sb.toString();
				strLen = str.length();
			}
		}
		return str;
	}

	public static void main(String[] args) {
		ISBNList list = new ISBNList();
		list.run();

	}

}
