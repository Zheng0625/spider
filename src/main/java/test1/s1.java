package test1;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;


public class s1 {
    public static void main(String[] args) throws Exception {
//        test(1);
        testOne();

    }
    static void testOne()  throws Exception  {
        Class.forName("com.mysql.jdbc.Driver");// 创建连接对象
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/one?useUnicode=true&characterEncoding=UTF-8",
                "root", "1234");
        Statement statement = connection.createStatement();
//        statement.executeUpdate(
//                "create table OneData( id bigint primary key not null auto_increment,maintext varchar(100), titletext varchar(50), imgtitletext varchar(50), imgurl varchar(10)");

        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setCssEnabled(false);// 关闭css
        webClient.getOptions().setJavaScriptEnabled(false);// 关闭js
        HtmlPage page;
        String sql = null;
        for (int z = 2493; z <= 2546; z++) {
            try {
                page = webClient.getPage("http://wufazhuce.com/one/"+z);
                HtmlDivision element = (HtmlDivision) page.getHtmlElementById("main-container");
                List<HtmlElement> imgUrl = page.getByXPath("//div[@class='one-imagen']/img");
                String url = imgUrl.get(0).getAttribute("src").toString();
                List<HtmlElement> title = page.getByXPath("//div[@class='one-titulo']");
                String titleText = title.get(0).getFirstChild().asText().toString();

                List<HtmlElement> imgTitle = page.getByXPath("//div[@class='one-imagen-leyenda']");
                String imgTitleText = imgTitle.get(0).getFirstChild().asText().toString();

                List<HtmlElement> MainText = page.getByXPath("//div[@class='one-cita']");
                String Text = MainText.get(0).getFirstChild().asText().toString();
                sql = "insert into OneData(maintext,titletext,imgurl,imgtitletext,z) values('" + Text
                        + "','" + titleText + "','" + url + "','" + imgTitleText +"','"+ z +"')";
                statement.executeUpdate(sql);
//                Thread.sleep(1000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    //爬取图片
    static void test(int z) {
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setCssEnabled(false);// 关闭css
        webClient.getOptions().setJavaScriptEnabled(false);// 关闭js
        final HtmlPage page;
        try {
            page = webClient.getPage("https://www.doutula.com/search?type=photo&more=1&keyword=%E5%B0%8F%E9%BB%84%E9%B8%A1&page="+z);
            List<HtmlElement> tables = page.getByXPath("//a[@class='col-xs-6 col-md-2']/img");
            for (int i = 0; i < tables.size(); i++) {
                System.out.println(i+1+"、"+tables.get(i).getAttribute("data-original"));
                if (tables.get(i).getAttribute("data-original").equals("")) {
                    continue;
                }
                String str_url[] = tables.get(i).getAttribute("data-original").split("\\.");
                if (str_url.length < 4 || str_url[1].equals("doutula")) {
                    continue;
                }
                URL url= new URL(tables.get(i).getAttribute("data-original"));
                URLConnection conn = url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(10000);
                conn.connect();
                DataInputStream dataInputStream = new DataInputStream(conn.getInputStream());
                File file=new File("d:/BQB/"+(Math.random()*+Math.random()+Math.random())+"."+str_url[3]);
                if(!file.exists()) {
                    file.createNewFile();
                }
                //通过流复制图片
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = dataInputStream.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
                fileOutputStream.write(output.toByteArray());
                dataInputStream.close();
                fileOutputStream.close();
                webClient.close();
                if (i + 1 == tables.size()) {
                    z++;
                    test(z);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

        }

    }
}
