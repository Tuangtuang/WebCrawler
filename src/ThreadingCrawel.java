import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
 
public class ThreadingCrawel {
  private static Content content = null;
  private static List<String> urlList = null;
  public static Connection connection;
  public ThreadingCrawel(List<String> urlList){
    this.urlList = urlList;
    content = new Content();
  }
  public List<String> getContent(){
 
    ExecutorService executor = Executors.newCachedThreadPool();//�����̳߳�
    for (String url : urlList)
    {
      executor.execute(new AddContent(url));//�������
    }
    executor.shutdown();//�ر� ExecutorService
    while(!executor.isTerminated()){}//ֱ���������
    return content.getContent();
 
  }
  public static class AddContent implements Runnable{
    String url;
    public AddContent(String url){
      this.url = url;
    }
    public void run(){
      content.addContent(url);
    }
  } 
  public static class Content {
 
    private static Lock lock = new ReentrantLock();//�� ReentrantLock ���������
    private static List<String> contentList = new ArrayList();
 
    public void addContent(String url){
 
      String content = "";
      BufferedReader in = null;
      try{
        URL realUrl = new URL(url);
        URLConnection connection = realUrl.openConnection();//��ʼ����
        in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "gbk"));
        String line;
        while( (line = in.readLine()) != null){
          content += line +"\n";
        }
      }catch(Exception e){
        e.printStackTrace();
      }
      finally{
        try{
          if (in != null){
            in.close();//�ر�����
          }
        }catch(Exception e2){
          e2.printStackTrace();
        }
      }
 
      Pattern p = Pattern.compile("content\":\".*?\"");//����
      Matcher match = p.matcher(content);//ƥ��
      String tmp;
      lock.lock();
      while(match.find()){
        tmp = match.group();
        tmp = tmp.replaceAll("\"", "");
        tmp = tmp.replace("content:", "");
        tmp = tmp.replaceAll("<.*?>", "");
        contentList.add(tmp);        
      }
      lock.unlock();
 
    }
    public List getContent(){
      return contentList;
    }
  }
  
  static void ConnectDatabase() throws SQLException,ClassNotFoundException
  {
	  
	  Class.forName("com.mysql.jdbc.Driver");	
	  String url = "jdbc:mysql://localhost:3306/webcrawler?useUnicode=true&characterEncoding=utf-8&useSSL=false" ;    
		String username = "root" ;   
		String password = "16122253";		
		try
		{   
		 connection = DriverManager.getConnection(url,username,password);   
		}
		catch(SQLException se){   		           
		            se.printStackTrace() ;
		            System.out.println("Goodbye");
		      }   
  
  }  
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException{
    long start = System.currentTimeMillis();
   
    String p_id = "2441288";
    MyThreading myThreading = new MyThreading(p_id);
    List <String> urlList = myThreading.getUriList();
    ThreadingCrawel threadingCrawel = new ThreadingCrawel(urlList);
    List <String> contentList = threadingCrawel.getContent();
    PrintWriter fw;
    fw = new PrintWriter(new FileWriter("D:/SiteURL.txt"), true);
    for(String content : contentList){
      System.out.println(content);
    	fw.write(content+"\r\n");
    }
    fw.close();
    ConnectDatabase(); 
    String sql="insert into comments(id)values(?)" ;          
    PreparedStatement preparedStatement=connection.prepareStatement(sql);
    for(String content : contentList)
    {
        
    	preparedStatement.setString(1,content);
    	preparedStatement.execute();
    }
    connection.close();
    long end = System.currentTimeMillis();
    System.out.println(end - start);
  }
}
