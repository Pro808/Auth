import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.GZIPInputStream;

/**
 * Created by Pro808 on 14.11.2017.


 */
public class SaveMusic {


    public SaveMusic(HTTPS https) throws IOException {

        String MusicContent = "";
        String tempContent = "";

        File MusicFile = new File(https.Files.get(https.Files.size() - 1));


        BufferedReader MusicFileIn = new BufferedReader(new InputStreamReader(new FileInputStream(MusicFile)));

        while ((tempContent = MusicFileIn.readLine()) != null) {
            MusicContent += tempContent + "\n";
        }

        MusicFileIn.close();


        String MusicHref = "<html><body>";

        HttpsURLConnection conToMusic;

        if (MusicContent.split("<input type=\"hidden\" value=\"").length > 1) {

            String HASH_MUSIC = MusicContent.split("<input type=\"hidden\" value=\"")[9].split("\"")[0];

            for(int g =45;g < MusicContent.split("<input type=\"hidden\" value=\"").length;g++)
            {
                MusicHref += MusicContent.split("<input type=\"hidden\" value=\"")[g].split("\"")[0] + "<br \\>\n";
                //СКАЧИВАНИЕ МУЗЫКИ
                String hrefForDownload = MusicContent.split("<input type=\"hidden\" value=\"")[g].split("\"")[0];
                String COOKIEDOWNLOAD_CUU = MusicContent.split("id=\"ai_menu_")[g].split("_audios")[0];
                String COOKIEDOWNLOAD_AUDIO = MusicContent.split("id=\"ai_menu_")[g].split("\"")[0];

                conToMusic = (HttpsURLConnection) new URL(hrefForDownload).openConnection();
                conToMusic.setUseCaches(false);
                conToMusic.setInstanceFollowRedirects(false);
                conToMusic.setDoInput(true);
                conToMusic.setDoOutput(true);

                conToMusic.setRequestMethod("GET");

                // Добавляем Response
                for (int i = 0; i < https.headersSite.size(); i++) {
                    conToMusic.setRequestProperty(https.headersSite.get(i).nameHeader, https.headersSite.get(i).valueHeader);
                }
                // Добавляем Response

                conToMusic.setRequestProperty("Host", hrefForDownload.split("https:..")[1].split("/")[0]);
                conToMusic.setRequestProperty("Referer", "https://vk.com/audios325692379");
                conToMusic.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36");

                conToMusic.setRequestProperty("DNT", "1");
                conToMusic.setRequestProperty("Connection", "keep-alive");
                conToMusic.setRequestProperty("Upgrade-Insecure-Requests","1");

                // Добавляем КУКИ
                BufferedReader setCookie = new BufferedReader(new InputStreamReader(new FileInputStream(https.CookiePath)));
                String tempCookie = "";
                String ALLCookie = "";
                int tempCo = 0;
                while ((tempCookie = setCookie.readLine()) != null) {
                    if (!(tempCo == 1)) {
                        ALLCookie += tempCookie.split(";")[0] + ";";
                    }
                    tempCo++;
                }
                ALLCookie += "remixflash=27.0.0; remixscreen_depth=24; remixdt=3600;" +
                        "remixmaudio="+COOKIEDOWNLOAD_AUDIO + ";" +
                        "remixcurr_audio=" + COOKIEDOWNLOAD_CUU + ";";
                conToMusic.setRequestProperty("Cookie", ALLCookie);
                // Добавляем КУКИ

                InputStream getMusic = conToMusic.getInputStream();

                for (int i = 0; i < https.headersSite.size(); i++) {
                    if (https.headersSite.get(i).valueHeader.equals("gzip")) {
                        getMusic = new GZIPInputStream(conToMusic.getInputStream());
                        break;
                    } else {
                        getMusic = conToMusic.getInputStream();
                    }
                }

                File downloadedMusic = new File(https.dirs.get("MusicDir") + (conToMusic.getContent()) + ".mp3");

                if (!downloadedMusic.exists()) {
                    downloadedMusic.createNewFile();
                }

                ReadableByteChannel rbc = Channels.newChannel(getMusic);

                new FileOutputStream(downloadedMusic).getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

                getMusic.close();

                //СКАЧИВАНИЕ МУЗЫКИ
            }
        }
        MusicHref += "</html></body>";

        File FileListMusic = new File(Main.dirs.get("MusicDir") + "music.html");

        if (!FileListMusic.exists()) {
            FileListMusic.createNewFile();
        }

        OutputStream os;
        try {
            os = new FileOutputStream(FileListMusic);
            os.write(MusicHref.getBytes(), 0, MusicHref.length());

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Получили список песен


    }

}
