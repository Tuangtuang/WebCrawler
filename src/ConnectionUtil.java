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
            conn = (HttpURLConnection) url.openConnection();//��URL����
            conn.setConnectTimeout(5000);//��ʱ����
            conn.setReadTimeout(5000);
            conn.setDoInput(true);//ʹ�� URL���ӽ�������
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
            e.printStackTrace();//�������д�ӡ�쳣��Ϣ�ڳ����г����λ�ü�ԭ��
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
        //��ȡԴ�ļ�
        BufferedReader bufferedReader= new BufferedReader(new FileReader("D:/Resource.txt"));
        //�Զ�ȡ�����ݽ��й����ƥ��
        String regex_info= "title=\"(.*?)\">(.*?)</a>.*?";
        Pattern pattern= Pattern.compile(regex_info);//pattern ������һ��������ʽ�ı����ʾ��Pattern ��û�й������췽����Ҫ����һ�� Pattern ������������ȵ����乫����̬���뷽����������һ�� Pattern ���󡣸÷�������һ��������ʽ��Ϊ���ĵ�һ��������
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
		System.out.println("������һ��URL");
		Scanner in=new Scanner (System.in);
		String a;
		PrintWriter pw;
		a=in.nextLine();
		pw = new PrintWriter(new FileWriter("D:/Resource.txt"), true);
		pw.println(Connect(a));//Դ��
		pw.close();
		System.out.println(getInfo());
		
				
	}
}


