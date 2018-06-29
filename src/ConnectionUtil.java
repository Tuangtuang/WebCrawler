import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
public class ConnectionUtil {
	public static String Connect(String address){
        HttpURLConnection conn = null;
        
        URL url = null;
        InputStream in = null;
        BufferedReader reader = null;
        StringBuffer stringBuffer = null;
        try {
            url = new URL(address);
            conn = (HttpURLConnection) url.openConnection();//打开URL链接
            conn.setConnectTimeout(5000);//超时设置
            conn.setReadTimeout(5000);
            conn.setDoInput(true);//使用 URL连接进行输入
            conn.connect();
            in = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in,"utf-8"));
            stringBuffer = new StringBuffer();
            String line = null;
            while((line = reader.readLine()) != null)
            {
                stringBuffer.append(line);
                
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();//在命令行打印异常信息在程序中出错的位置及原因。
        } 
        finally{
            conn.disconnect();
            try {
                in.close();
                reader.close();
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }

        return stringBuffer.toString();
    }

	public static List<String> getInfo() throws IOException
	{
        //读取源文件
        BufferedReader bufferedReader= new BufferedReader(new FileReader("D:/Resource.txt"));
        //对读取的数据进行规则的匹配
        String regex_info= "title=\"(.*?)\">(.*?)</a>.*?";
        Pattern pattern= Pattern.compile(regex_info);//pattern 对象是一个正则表达式的编译表示。Pattern 类没有公共构造方法。要创建一个 Pattern 对象，你必须首先调用其公共静态编译方法，它返回一个 Pattern 对象。该方法接受一个正则表达式作为它的第一个参数。
        String line = null;
        List<String> list= new ArrayList<>();
        while ((line= bufferedReader.readLine())!=null)
        {
            Matcher matcher= pattern.matcher(line);
            while (matcher.find()) {
                list.add(matcher.group(1)+"\n");
                list.add(matcher.group(2));
        }
        }
        return list;
    }
	public static void main(String[] args)throws IOException
	{
		System.out.println("请输入一个URL");
		Scanner in=new Scanner (System.in);
		String a;
		PrintWriter pw;
		a=in.nextLine();
		pw = new PrintWriter(new FileWriter("D:/Resource.txt"), true);
		pw.println(Connect(a));//源码
		pw.close();
		System.out.println(getInfo());
		
				
	}
}


