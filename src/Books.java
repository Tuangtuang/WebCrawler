import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mysql.jdbc.Connection;

public class Books
{

	public static class BookInfo//图书信息
	{
		public String Name;
		public String Price;
		public String Introduction;
		public BookInfo(String a,String b,String c)
		{
			// TODO Auto-generated constructor stub
			Name=a;
			Price=b;
			Introduction=c;
		}
	};
	public static Connection conn = null;//数据库连接
	public static LinkedList<BookInfo> MyList=new LinkedList<BookInfo>();//暂时将数据存入链表	
	PreparedStatement ps=null;
	public static Document Visit(String URL) throws IOException// 访问URL
	{
		//模拟浏览器登陆
		
		
		Document document = Jsoup.connect(URL).userAgent(
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.89 Safari/537.36")
				.timeout(10000000).get();
					
		return document;
		
		
	}

	public static void ConnectDatabase() throws SQLException, ClassNotFoundException
	{

		Class.forName("com.mysql.jdbc.Driver");//加载驱动
		String url = "jdbc:mysql://localhost:3306/webcrawler?useUnicode=true&characterEncoding=utf-8&useSSL=false";//数据库地址
		String username = "root";
		String password = "16122253";
		try 
		{
			conn = (Connection) DriverManager.getConnection(url, username, password);//连接数据库
		} 
		catch (SQLException se) 
		{
			se.printStackTrace();
			System.out.println("Goodbye");
		}

	}
	public static void WriteToDataBase(BookInfo Canshu) throws ClassNotFoundException, SQLException
	{
		
		System.out.println("IN");
		String sql="insert into books(Name,Price,Introduction)values(?,?,?)" ;//？占位子          
		PreparedStatement ps=conn.prepareStatement(sql);
		
			ps.setString(1,Canshu.Name);
			ps.setString(2, Canshu.Price);
			ps.setString(3, Canshu.Introduction);//加入数据
			ps.execute();//提交	    	    		
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException
	{
		// TODO Auto-generated method stub
		int count=1;		
		ConnectDatabase();
		while(count<=100)
		{
			
			String temp="http://search.dangdang.com/?key=%B7%A8%C2%C9&act=input&page_index="+count;
			System.out.println(temp);
			Document doc=Visit(temp);
			doc.getElementById("p20481551");
			Elements link2s = doc.select("p.name").select("a[href]");
			ArrayList<String> UrlList2 = new ArrayList<String>();
			
			for (Element page : link2s)
			{
				String URL = page.attr("abs:href");
				UrlList2.add(URL);
			}
			
			for (int i = 0; i < UrlList2.size(); i++) 
			{
				try {
					Document doc2 = Visit(UrlList2.get(i));
					System.out.println(UrlList2.get(i));				
					Elements BookName = doc2.children().select("h1");
					Elements BookPrice = doc2.children().select(".price_d").select("#dd-price");
					Elements BookIntroduction = doc2.children().select(".head_title_name");	
					BookInfo Temp=new BookInfo(BookName.text(),BookPrice.text(),BookIntroduction.text());
					WriteToDataBase(Temp);//将数据写入数据库
					System.out.println(BookName.text()+"\n"+BookPrice.text()+"\n"+BookIntroduction.text());
					//MyList.add(Temp);
					//System.out.println("Ok");
					Elements LinkNext=doc2.select("div.cuxiao_info.choose_xilie.clearfix").select("div.right").select("div").select("ul").select("li").select("a");
				
					ArrayList<String> UrlNext = new ArrayList<String>();
					for (Element page2 : LinkNext) {
						String URLNext = page2.attr("href");
						if(URLNext.charAt(0)!='j')
						{
							UrlNext.add("http://product.dangdang.com" + URLNext);
						}
					
					}
					System.out.println(UrlNext.size());
					for (int j = 0; j < UrlNext.size(); j++) {
					
						System.out.println(UrlNext.get(j));	
						try {
							Document doc3 = Visit(UrlNext.get(j));
							Elements BookName2 = doc3.children().select("h1");
							Elements BookPrice2 = doc3.children().select(".price_d").select("#dd-price");
							Elements BookIntroduction2 = doc3.children().select(".head_title_name");
							BookInfo Temp2 = new BookInfo(BookName2.text(), BookPrice2.text(), BookIntroduction2.text());
							WriteToDataBase(Temp2);//将数据写入数据库
							System.out.println(BookName2.text() + "\n" + BookPrice2.text() + "\n" + BookIntroduction2.text());
						//MyList.add(Temp);
						} catch (Exception e) {
							// TODO: handle exception
							continue;
						}
						
					
					}
			}
			
				
				catch (Exception e) {
					// TODO: handle exception
					continue;
				}
				
				} 
							
			count++;
		}		
		
		conn.close();//关闭连接
	}

}
