import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

/**
 * Created by Pro808 on 13.11.2017.
 */
public class HTTPS {

    public HttpsURLConnection con;

    public ArrayList<HeadersSite> headersSite = new ArrayList<>();

    public HashMap<String,String> someValuesToAuth = new HashMap<>();

    public HashMap<String,String> dirs = Main.dirs;

    public ArrayList<String> Files = new ArrayList<>();

    public String CookiePath = dirs.get("CookiesDir") + "Cookie.txt";





    public void FirstConnectToSite(String nameSite) throws IOException {
        URL url = new URL(nameSite);
        con = (HttpsURLConnection) url.openConnection();

        con.setRequestMethod("GET");

        con.setRequestProperty("Accept-Encoding", "gzip");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.8,es;q=0.6");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset = utf-8");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
        saveResponseHeaders();
        saveCookie();
        savePageContent(Files.size()+ "_" +nameSite.split("http....")[1].replaceAll("[/.?=]","_"));
        getSomeValuesToAuth();
    }

    public void saveResponseHeaders() {
        System.out.println("RESPONSE |||||||||||||||||||||||||||||||||||||||||| RESPONSE");
        headersSite.clear();
        int numHeader = 1;

        while (con.getHeaderField(numHeader) != null) {
            headersSite.add(new HeadersSite(con.getHeaderFieldKey(numHeader), con.getHeaderField(numHeader)));
            numHeader++;
        }
        for (int i = 0; i < headersSite.size(); i++) {
            System.out.println(headersSite.get(i).nameHeader + " = " + headersSite.get(i).valueHeader);
        }
        System.out.println("RESPONSE |||||||||||||||||||||||||||||||||||||||||| RESPONSE");
    }

    public void savePageContent(String nameDocument) throws IOException {
        InputStream in = con.getInputStream();

        for (int i = 0; i < headersSite.size(); i++) {
            if (headersSite.get(i).valueHeader.equals("gzip")) {
                in = new GZIPInputStream(con.getInputStream());
                break;
            } else {
                in = con.getInputStream();
            }
        }

        String pathHtmlDocument = dirs.get("HtmlDocumentDir") + nameDocument + ".html";
        File contentFile = new File(pathHtmlDocument);

        Files.add(pathHtmlDocument);

        if (!contentFile.exists()) {
            contentFile.createNewFile();
        }

        ReadableByteChannel rbc = Channels.newChannel(in);

        new FileOutputStream(contentFile).getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

        in.close();
    }

    public void saveCookie() throws IOException, NullPointerException {

        String Cookies = "";
        String lastCookie = "";
        String tempCookie = "";

        File CookieFile = new File(CookiePath);

        if (!CookieFile.exists()) {
            CookieFile.createNewFile();
        }


        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(CookieFile)));

        while ((tempCookie = in.readLine()) != null) {
            lastCookie += tempCookie + "\n";
        }

        Cookies += lastCookie;

        for (int i = 0; i < headersSite.size(); i++) {
            if (headersSite.get(i).nameHeader.equals("Set-Cookie")) {
                Cookies += headersSite.get(i).valueHeader + "\n";
                if(headersSite.get(i).valueHeader.split("sessionid=").length > 1) {
                    someValuesToAuth.put("sessionid", headersSite.get(i).valueHeader.split("sessionid=")[1].split(";")[0]);
                }
            }
        }

        OutputStream os;
        try {
            os = new FileOutputStream(CookieFile);
            os.write(Cookies.getBytes(), 0, Cookies.length());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getSomeValuesToAuth() throws IOException {

        String FileContent = "";
        String tempContent = "";

        File CookieFile = new File(Files.get(Files.size() - 1));

        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(CookieFile)));

        while ((tempContent = in.readLine()) != null) {
            FileContent += tempContent + "\n";
        }

        if(FileContent.split("name=\"ip_h\" value=\"").length > 1) {
            someValuesToAuth.put("ip_h", FileContent.split("name=\"ip_h\" value=\"")[1].split("\"")[0]);
        }
        if(FileContent.split("name=\"lg_h\" value=\"").length > 1) {
            someValuesToAuth.put("lg_h", FileContent.split("name=\"lg_h\" value=\"")[1].split("\"")[0]);
        }
        if(FileContent.split("lang_id: 0, hash: '").length > 1) {
            someValuesToAuth.put("hash", FileContent.split("lang_id: 0, hash: '")[1].split("'")[0]);
        }
        if(FileContent.split("\"uid\":\"").length > 1) {
            someValuesToAuth.put("id", FileContent.split("\"uid\":\"")[1].split("\"")[0]);
        }
        if(FileContent.split("openidparams\" value=\"").length > 1) {
            someValuesToAuth.put("openidparams", FileContent.split("openidparams\" value=\"")[1].split("\"")[0]);
        }
        if(FileContent.split("name=\"nonce\" value=\"").length > 1) {
            someValuesToAuth.put("nonce", FileContent.split("name=\"nonce\" value=\"")[1].split("\"")[0]);
        }
    }

    public void connectMethod(String nameSite,String postParam, String Method, ArrayList<HeadersSite> requestProp  ) throws IOException {
        con.disconnect();

        URL url = new URL(nameSite);
        con = (HttpsURLConnection) url.openConnection();

        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        con.setInstanceFollowRedirects(false);
        con.setRequestMethod(Method);


        // Добавляем Response
        for (int i = 0; i < headersSite.size(); i++) {
            con.setRequestProperty(headersSite.get(i).nameHeader, headersSite.get(i).valueHeader);
        }
        // Добавляем Response


        //Добавляем reqestProp

        for(int i =0; i < requestProp.size();i++)
        {
            con.setRequestProperty(requestProp.get(i).nameHeader,requestProp.get(i).valueHeader);
        }

        //Добавляем reqestProp


        // Добавляем КУКИ
        BufferedReader setCookie = new BufferedReader(new InputStreamReader(new FileInputStream(CookiePath)));
        String tempCookie = "";
        String ALLCookie = "";
        int tempCo = 0;
        while ((tempCookie = setCookie.readLine()) != null) {
            if (!(tempCo == 1)) {
                ALLCookie += tempCookie.split(";")[0] + ";";
            }
            tempCo++;
        }
        ALLCookie += "remixflash=27.0.0; remixscreen_depth=24; remixdt=3600;timezoneOffset=14400,0";
        con.setRequestProperty("Cookie", ALLCookie);
        // Добавляем КУКИ

        if(Method.equals("POST")) {
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParam);
            wr.flush();
            wr.close();
        }

        saveResponseHeaders();
        saveCookie();
        savePageContent(Files.size()+ "_" +nameSite.split("http....")[1].replaceAll("[/.?=]","_"));

    }


}
