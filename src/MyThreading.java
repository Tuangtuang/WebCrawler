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

public class MyThreading {
  private static String p_id = null;
  private static Url urls = null;
  public static Connection connection;
  public MyThreading(String p_id){
    this.p_id = p_id ;   //商品
    urls = new Url(p_id); 
  }
 
  public List<String> getUriList(){
    ExecutorService executor = Executors.newCachedThreadPool();
    for (int i = 0 ; i < 600 ; i ++){
      executor.execute(new AddUrl(i));    // 添加任务到线程池
    }
    executor.shutdown();
    while (!executor.isTerminated()){}
    return urls.getList();
  }
 
  public static class AddUrl implements Runnable{
    int page;
    public AddUrl(int page){
      this.page = page;
    }
    public void run(){
      urls.addList(page);   // 启动多线程任务

    }
 
  public static class Url {
 
    private static Lock lock = new ReentrantLock();     // 开启显式家锁
    private static List<String> urlList = new ArrayList();     
    private String p_id;
 
    public Url(String p_id ){
      this.p_id = p_id ;
    }
    public List<String> getList(){
      return urlList;
    }
    public void addList(int page){
      lock.lock();
      try{
        String url = "http://club.jd.com/productpage/p-" + p_id + "-s-0-t-0-p-" + String.valueOf(page) + ".html";
//       Thread.sleep(5);
        urlList.add(url);    ////添加url到url列表
      }catch(Exception ex ){
      }
      finally {
        lock.unlock();     // 解锁
      }
 
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
  public static void main(String[] args) throws ClassNotFoundException, SQLException {
    String p_id = "16580907535";
    MyThreading myThreading = new MyThreading(p_id);
    List <String> urlList = myThreading.getUriList();
    
    ConnectDatabase(); 
    String sql="insert into urls(url)values(?)" ;          
    PreparedStatement preparedStatement=connection.prepareStatement(sql);
    for(String content : urlList)
    {
    	
    	preparedStatement.setString(1,content);
    	preparedStatement.execute();
    }
    for(String url : urlList){
      System.out.println(url);
    }
    System.out.println(urlList.size());
    connection.close();
  }
}
