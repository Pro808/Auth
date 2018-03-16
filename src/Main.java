import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by Pro808 on 30.10.2017.
 */
public class Main {

    public static HashMap<String, String> dirs = new HashMap<>();

    public static ArrayList<HeadersSite> requestPror = new ArrayList<>();

    public static String domenSite = "";

    public String SteamId = "";
    public String SteamEMAIL = "login";
    public String SteamPASS = "pass";
    public HashMap<String,String> parseKeysParam = new HashMap<>();
    public HTTPS conHTTPSSteam;


    public static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                File f = new File(dir, children[i]);
                deleteDirectory(f);
            }
            dir.delete();
        } else dir.delete();
    }


    public void createDirs(String nameSite) {
        for (int i = 0; i < dirs.size(); i++) {
            File folder = new File(dirs.values().toArray()[i].toString());
            if (!folder.exists()) {
                folder.mkdirs();
            } else {
                deleteDirectory(folder);
                folder.mkdirs();
            }
        }

    }

    public void AuthVK(String nameSite)
    {

        HTTPS conHTTPS = new HTTPS();
        try {
            createDirs(nameSite);

            //FirstConnection
            conHTTPS.FirstConnectToSite(nameSite);
            //FirstConnection


            //LOGINING
            String ORIGIN = "https%3A%2F%2Fvk.com";
            String EMAIL = "number";
            String PASS = "pass";
            String IG_H = conHTTPS.someValuesToAuth.get("lg_h");
            String IP_H = conHTTPS.someValuesToAuth.get("ip_h");
            String queryLogin = String.format(
                    "captcha_key=&" +
                            "captcha_sid=&" +
                            "recaptcha=&" +
                            "expire=&" +
                            "act=login&" +
                            "role=al_frame&" +
                            "_origin=%s&" +
                            "email=%s&" +
                            "pass=%s&" +
                            "lg_h=%s&" +
                            "ip_h=%s",
                    ORIGIN,
                    EMAIL,
                    PASS,
                    IG_H,
                    IP_H);

            requestPror.clear();
            requestPror.add(new HeadersSite("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
            requestPror.add(new HeadersSite("Content-Type", "application/x-www-form-urlencoded; charset = utf-8"));
            requestPror.add(new HeadersSite("Connection", "keep-alive"));
            requestPror.add(new HeadersSite("DNT", "1"));
            requestPror.add(new HeadersSite("Host", "login.vk.com"));
            requestPror.add(new HeadersSite("Origin", "https://vk.com/"));
            requestPror.add(new HeadersSite("Referer", "https://vk.com/"));
            requestPror.add(new HeadersSite("Content-Length", "" + queryLogin.length()));
            requestPror.add(new HeadersSite("Upgrade-Insecure-Requests", "1"));

            conHTTPS.connectMethod("https://login.vk.com/?act=login",queryLogin,"POST",requestPror);

            String locationAtfterLogin = "";

            for(int i =0;i < conHTTPS.headersSite.size();i++)
            {
                if(conHTTPS.headersSite.get(i).nameHeader.equals("Location"))
                {
                    locationAtfterLogin = conHTTPS.headersSite.get(i).valueHeader;
                }
            }

            conHTTPS.connectMethod(locationAtfterLogin,"","GET",requestPror);

            //LOGINING


            String locationMusic = "https://m.vk.com/audio";

            conHTTPS.connectMethod(locationMusic,"","GET",requestPror);

            new SaveMusic(conHTTPS);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public HashMap<String,String> getKeysJson(File MusicFile) throws IOException {
        String KeysContent = "";
        String tempContent = "";
        BufferedReader MusicFileIn = new BufferedReader(new InputStreamReader(new FileInputStream(MusicFile)));

        while ((tempContent = MusicFileIn.readLine()) != null) {
            KeysContent += tempContent + "\n";
        }

        // Получение страницы JSON

        HashMap<String,String> parseKeysParam = new HashMap<>();

        ArrayList<String> nameVarsArray = new ArrayList<>();
        ArrayList<String> valueVarsArray = new ArrayList<>();

        char[] chars = KeysContent.toCharArray();
        for(int key = 0; key <chars.length;key++)
        {
            String tempNameVar = "";
            if(chars[key] == '"')
            {
                key++;
                tempNameVar = "";
                while(chars[key] != '"'&& key < chars.length-1)
                {
                    tempNameVar += chars[key];
                    key++;
                }
                nameVarsArray.add(tempNameVar);
            }
            if(chars[key] == ':')
            {
                key++;
                tempNameVar = "";
                while((chars[key] != ',' && chars[key] != '}') && key < chars.length-1)
                {
                    if(chars[key] != '"') {
                        tempNameVar += chars[key];
                    }
                    key++;
                }
                valueVarsArray.add(tempNameVar);
            }
        }
        for(int i =0;i<nameVarsArray.size();i++) {
            parseKeysParam.put(nameVarsArray.get(i), valueVarsArray.get(i));
        }

        return parseKeysParam;
    }

    public String getEncryptPassSteam() throws IOException {

        String Data = Long.toString(System.currentTimeMillis());

        String reqestToGetRSAKEY = "username="+SteamEMAIL+"&donotcache="+Data;

        requestPror.clear();
        requestPror.add(new HeadersSite("User-Agent","Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0"));
        requestPror.add(new HeadersSite("DNT","1"));
        requestPror.add(new HeadersSite("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
        requestPror.add(new HeadersSite("Content-Type", "application/x-www-form-urlencoded; charset = utf-8"));
        requestPror.add(new HeadersSite("Connection", "keep-alive"));
        requestPror.add(new HeadersSite("Host", "steamcommunity.com"));
        requestPror.add(new HeadersSite("Referer", "https://steamcommunity.com/openid/login?openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0&openid.mode=checkid_setup&openid.return_to=http%3A%2F%2Fyhdovh7o.com%2Fauthorization.php%3Flogin&openid.realm=http%3A%2F%2Fyhdovh7o.com&openid.ns.sreg=http%3A%2F%2Fopenid.net%2Fextensions%2Fsreg%2F1.1&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select"));
        requestPror.add(new HeadersSite("Content-Length",""+reqestToGetRSAKEY.length()));
        // Получение Ключей
        conHTTPSSteam.connectMethod("https://steamcommunity.com/login/getrsakey/", reqestToGetRSAKEY,"POST",requestPror);
        // Получение Ключей

        //Парсер Ключей



        File MusicFile = new File(conHTTPSSteam.Files.get(conHTTPSSteam.Files.size() - 1));

        parseKeysParam.clear();

        parseKeysParam = getKeysJson(MusicFile);


        //Парсер Ключей

        // Генерация пароля
        String EncryptPassword = "";
        ScriptEngineManager evalJsCode = new ScriptEngineManager();
        ScriptEngine Engine = evalJsCode.getEngineByName("javascript");
        try {
            Engine.eval("" +
                    "function getEncryptPasswordJs(){" +
                    "\n" +
                    "// Copyright (c) 2005  Tom Wu\n" +
                    "// All Rights Reserved.\n" +
                    "// See \"LICENSE\" for details.\n" +
                    "\n" +
                    "/*\n" +
                    " * Copyright (c) 2003-2005  Tom Wu\n" +
                    " * All Rights Reserved.\n" +
                    " *\n" +
                    " * Permission is hereby granted, free of charge, to any person obtaining\n" +
                    " * a copy of this software and associated documentation files (the\n" +
                    " * \"Software\"), to deal in the Software without restriction, including\n" +
                    " * without limitation the rights to use, copy, modify, merge, publish,\n" +
                    " * distribute, sublicense, and/or sell copies of the Software, and to\n" +
                    " * permit persons to whom the Software is furnished to do so, subject to\n" +
                    " * the following conditions:\n" +
                    " *\n" +
                    " * The above copyright notice and this permission notice shall be\n" +
                    " * included in all copies or substantial portions of the Software.\n" +
                    " *\n" +
                    " * THE SOFTWARE IS PROVIDED \"AS-IS\" AND WITHOUT WARRANTY OF ANY KIND, \n" +
                    " * EXPRESS, IMPLIED OR OTHERWISE, INCLUDING WITHOUT LIMITATION, ANY \n" +
                    " * WARRANTY OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  \n" +
                    " *\n" +
                    " * IN NO EVENT SHALL TOM WU BE LIABLE FOR ANY SPECIAL, INCIDENTAL,\n" +
                    " * INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY KIND, OR ANY DAMAGES WHATSOEVER\n" +
                    " * RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER OR NOT ADVISED OF\n" +
                    " * THE POSSIBILITY OF DAMAGE, AND ON ANY THEORY OF LIABILITY, ARISING OUT\n" +
                    " * OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.\n" +
                    " *\n" +
                    " * In addition, the following condition applies:\n" +
                    " *\n" +
                    " * All redistributions must retain an intact copy of this copyright notice\n" +
                    " * and disclaimer.\n" +
                    " */\n" +
                    "\n" +
                    "// Basic JavaScript BN library - subset useful for RSA encryption.\n" +
                    "\n" +
                    "// Bits per digit\n" +
                    "var dbits;\n" +
                    "\n" +
                    "// JavaScript engine analysis\n" +
                    "var canary = 0xdeadbeefcafe;\n" +
                    "var j_lm = ((canary&0xffffff)==0xefcafe);\n" +
                    "\n" +
                    "// (public) Constructor\n" +
                    "function BigInteger(a,b,c) {\n" +
                    "\tif(a != null)\n" +
                    "\t\tif(\"number\" == typeof a) this.fromNumber(a,b,c);\n" +
                    "\t\telse if(b == null && \"string\" != typeof a) this.fromString(a,256);\n" +
                    "\t\telse this.fromString(a,b);\n" +
                    "}\n" +
                    "\n" +
                    "// return new, unset BigInteger\n" +
                    "function nbi() { return new BigInteger(null); }\n" +
                    "\n" +
                    "// am: Compute w_j += (x*this_i), propagate carries,\n" +
                    "// c is initial carry, returns final carry.\n" +
                    "// c < 3*dvalue, x < 2*dvalue, this_i < dvalue\n" +
                    "// We need to select the fastest one that works in this environment.\n" +
                    "\n" +
                    "// am1: use a single mult and divide to get the high bits,\n" +
                    "// max digit bits should be 26 because\n" +
                    "// max internal value = 2*dvalue^2-2*dvalue (< 2^53)\n" +
                    "function am1(i,x,w,j,c,n) {\n" +
                    "\twhile(--n >= 0) {\n" +
                    "\t\tvar v = x*this[i++]+w[j]+c;\n" +
                    "\t\tc = Math.floor(v/0x4000000);\n" +
                    "\t\tw[j++] = v&0x3ffffff;\n" +
                    "\t}\n" +
                    "\treturn c;\n" +
                    "}\n" +
                    "// am2 avoids a big mult-and-extract completely.\n" +
                    "// Max digit bits should be <= 30 because we do bitwise ops\n" +
                    "// on values up to 2*hdvalue^2-hdvalue-1 (< 2^31)\n" +
                    "function am2(i,x,w,j,c,n) {\n" +
                    "\tvar xl = x&0x7fff, xh = x>>15;\n" +
                    "\twhile(--n >= 0) {\n" +
                    "\t\tvar l = this[i]&0x7fff;\n" +
                    "\t\tvar h = this[i++]>>15;\n" +
                    "\t\tvar m = xh*l+h*xl;\n" +
                    "\t\tl = xl*l+((m&0x7fff)<<15)+w[j]+(c&0x3fffffff);\n" +
                    "\t\tc = (l>>>30)+(m>>>15)+xh*h+(c>>>30);\n" +
                    "\t\tw[j++] = l&0x3fffffff;\n" +
                    "\t}\n" +
                    "\treturn c;\n" +
                    "}\n" +
                    "// Alternately, set max digit bits to 28 since some\n" +
                    "// browsers slow down when dealing with 32-bit numbers.\n" +
                    "function am3(i,x,w,j,c,n) {\n" +
                    "\tvar xl = x&0x3fff, xh = x>>14;\n" +
                    "\twhile(--n >= 0) {\n" +
                    "\t\tvar l = this[i]&0x3fff;\n" +
                    "\t\tvar h = this[i++]>>14;\n" +
                    "\t\tvar m = xh*l+h*xl;\n" +
                    "\t\tl = xl*l+((m&0x3fff)<<14)+w[j]+c;\n" +
                    "\t\tc = (l>>28)+(m>>14)+xh*h;\n" +
                    "\t\tw[j++] = l&0xfffffff;\n" +
                    "\t}\n" +
                    "\treturn c;\n" +
                    "}\n" +
                    "\tBigInteger.prototype.am = am2;\n" +
                    "\tdbits = 30;\n" +

                    "\n" +
                    "BigInteger.prototype.DB = dbits;\n" +
                    "BigInteger.prototype.DM = ((1<<dbits)-1);\n" +
                    "BigInteger.prototype.DV = (1<<dbits);\n" +
                    "\n" +
                    "var BI_FP = 52;\n" +
                    "BigInteger.prototype.FV = Math.pow(2,BI_FP);\n" +
                    "BigInteger.prototype.F1 = BI_FP-dbits;\n" +
                    "BigInteger.prototype.F2 = 2*dbits-BI_FP;\n" +
                    "\n" +
                    "// Digit conversions\n" +
                    "var BI_RM = \"0123456789abcdefghijklmnopqrstuvwxyz\";\n" +
                    "var BI_RC = new Array();\n" +
                    "var rr,vv;\n" +
                    "rr = \"0\".charCodeAt(0);\n" +
                    "for(vv = 0; vv <= 9; ++vv) BI_RC[rr++] = vv;\n" +
                    "rr = \"a\".charCodeAt(0);\n" +
                    "for(vv = 10; vv < 36; ++vv) BI_RC[rr++] = vv;\n" +
                    "rr = \"A\".charCodeAt(0);\n" +
                    "for(vv = 10; vv < 36; ++vv) BI_RC[rr++] = vv;\n" +
                    "\n" +
                    "function int2char(n) { return BI_RM.charAt(n); }\n" +
                    "function intAt(s,i) {\n" +
                    "\tvar c = BI_RC[s.charCodeAt(i)];\n" +
                    "\treturn (c==null)?-1:c;\n" +
                    "}\n" +
                    "\n" +
                    "// (protected) copy this to r\n" +
                    "function bnpCopyTo(r) {\n" +
                    "\tfor(var i = this.t-1; i >= 0; --i) r[i] = this[i];\n" +
                    "\tr.t = this.t;\n" +
                    "\tr.s = this.s;\n" +
                    "}\n" +
                    "\n" +
                    "// (protected) set from integer value x, -DV <= x < DV\n" +
                    "function bnpFromInt(x) {\n" +
                    "\tthis.t = 1;\n" +
                    "\tthis.s = (x<0)?-1:0;\n" +
                    "\tif(x > 0) this[0] = x;\n" +
                    "\telse if(x < -1) this[0] = x+DV;\n" +
                    "\telse this.t = 0;\n" +
                    "}\n" +
                    "\n" +
                    "// return bigint initialized to value\n" +
                    "function nbv(i) { var r = nbi(); r.fromInt(i); return r; }\n" +
                    "\n" +
                    "// (protected) set from string and radix\n" +
                    "function bnpFromString(s,b) {\n" +
                    "\tvar k;\n" +
                    "\tif(b == 16) k = 4;\n" +
                    "\telse if(b == 8) k = 3;\n" +
                    "\telse if(b == 256) k = 8; // byte array\n" +
                    "\telse if(b == 2) k = 1;\n" +
                    "\telse if(b == 32) k = 5;\n" +
                    "\telse if(b == 4) k = 2;\n" +
                    "\telse { this.fromRadix(s,b); return; }\n" +
                    "\tthis.t = 0;\n" +
                    "\tthis.s = 0;\n" +
                    "\tvar i = s.length, mi = false, sh = 0;\n" +
                    "\twhile(--i >= 0) {\n" +
                    "\t\tvar x = (k==8)?s[i]&0xff:intAt(s,i);\n" +
                    "\t\tif(x < 0) {\n" +
                    "\t\t\tif(s.charAt(i) == \"-\") mi = true;\n" +
                    "\t\t\tcontinue;\n" +
                    "\t\t}\n" +
                    "\t\tmi = false;\n" +
                    "\t\tif(sh == 0)\n" +
                    "\t\t\tthis[this.t++] = x;\n" +
                    "\t\telse if(sh+k > this.DB) {\n" +
                    "\t\t\tthis[this.t-1] |= (x&((1<<(this.DB-sh))-1))<<sh;\n" +
                    "\t\t\tthis[this.t++] = (x>>(this.DB-sh));\n" +
                    "\t\t}\n" +
                    "\t\telse\n" +
                    "\t\t\tthis[this.t-1] |= x<<sh;\n" +
                    "\t\tsh += k;\n" +
                    "\t\tif(sh >= this.DB) sh -= this.DB;\n" +
                    "\t}\n" +
                    "\tif(k == 8 && (s[0]&0x80) != 0) {\n" +
                    "\t\tthis.s = -1;\n" +
                    "\t\tif(sh > 0) this[this.t-1] |= ((1<<(this.DB-sh))-1)<<sh;\n" +
                    "\t}\n" +
                    "\tthis.clamp();\n" +
                    "\tif(mi) BigInteger.ZERO.subTo(this,this);\n" +
                    "}\n" +
                    "\n" +
                    "// (protected) clamp off excess high words\n" +
                    "function bnpClamp() {\n" +
                    "\tvar c = this.s&this.DM;\n" +
                    "\twhile(this.t > 0 && this[this.t-1] == c) --this.t;\n" +
                    "}\n" +
                    "\n" +
                    "// (public) return string representation in given radix\n" +
                    "function bnToString(b) {\n" +
                    "\tif(this.s < 0) return \"-\"+this.negate().toString(b);\n" +
                    "\tvar k;\n" +
                    "\tif(b == 16) k = 4;\n" +
                    "\telse if(b == 8) k = 3;\n" +
                    "\telse if(b == 2) k = 1;\n" +
                    "\telse if(b == 32) k = 5;\n" +
                    "\telse if(b == 4) k = 2;\n" +
                    "\telse return this.toRadix(b);\n" +
                    "\tvar km = (1<<k)-1, d, m = false, r = \"\", i = this.t;\n" +
                    "\tvar p = this.DB-(i*this.DB)%k;\n" +
                    "\tif(i-- > 0) {\n" +
                    "\t\tif(p < this.DB && (d = this[i]>>p) > 0) { m = true; r = int2char(d); }\n" +
                    "\t\twhile(i >= 0) {\n" +
                    "\t\t\tif(p < k) {\n" +
                    "\t\t\t\td = (this[i]&((1<<p)-1))<<(k-p);\n" +
                    "\t\t\t\td |= this[--i]>>(p+=this.DB-k);\n" +
                    "\t\t\t}\n" +
                    "\t\t\telse {\n" +
                    "\t\t\t\td = (this[i]>>(p-=k))&km;\n" +
                    "\t\t\t\tif(p <= 0) { p += this.DB; --i; }\n" +
                    "\t\t\t}\n" +
                    "\t\t\tif(d > 0) m = true;\n" +
                    "\t\t\tif(m) r += int2char(d);\n" +
                    "\t\t}\n" +
                    "\t}\n" +
                    "\treturn m?r:\"0\";\n" +
                    "}\n" +
                    "\n" +
                    "// (public) -this\n" +
                    "function bnNegate() { var r = nbi(); BigInteger.ZERO.subTo(this,r); return r; }\n" +
                    "\n" +
                    "// (public) |this|\n" +
                    "function bnAbs() { return (this.s<0)?this.negate():this; }\n" +
                    "\n" +
                    "// (public) return + if this > a, - if this < a, 0 if equal\n" +
                    "function bnCompareTo(a) {\n" +
                    "\tvar r = this.s-a.s;\n" +
                    "\tif(r != 0) return r;\n" +
                    "\tvar i = this.t;\n" +
                    "\tr = i-a.t;\n" +
                    "\tif(r != 0) return r;\n" +
                    "\twhile(--i >= 0) if((r=this[i]-a[i]) != 0) return r;\n" +
                    "\treturn 0;\n" +
                    "}\n" +
                    "\n" +
                    "// returns bit length of the integer x\n" +
                    "function nbits(x) {\n" +
                    "\tvar r = 1, t;\n" +
                    "\tif((t=x>>>16) != 0) { x = t; r += 16; }\n" +
                    "\tif((t=x>>8) != 0) { x = t; r += 8; }\n" +
                    "\tif((t=x>>4) != 0) { x = t; r += 4; }\n" +
                    "\tif((t=x>>2) != 0) { x = t; r += 2; }\n" +
                    "\tif((t=x>>1) != 0) { x = t; r += 1; }\n" +
                    "\treturn r;\n" +
                    "}\n" +
                    "\n" +
                    "// (public) return the number of bits in \"this\"\n" +
                    "function bnBitLength() {\n" +
                    "\tif(this.t <= 0) return 0;\n" +
                    "\treturn this.DB*(this.t-1)+nbits(this[this.t-1]^(this.s&this.DM));\n" +
                    "}\n" +
                    "\n" +
                    "// (protected) r = this << n*DB\n" +
                    "function bnpDLShiftTo(n,r) {\n" +
                    "\tvar i;\n" +
                    "\tfor(i = this.t-1; i >= 0; --i) r[i+n] = this[i];\n" +
                    "\tfor(i = n-1; i >= 0; --i) r[i] = 0;\n" +
                    "\tr.t = this.t+n;\n" +
                    "\tr.s = this.s;\n" +
                    "}\n" +
                    "\n" +
                    "// (protected) r = this >> n*DB\n" +
                    "function bnpDRShiftTo(n,r) {\n" +
                    "\tfor(var i = n; i < this.t; ++i) r[i-n] = this[i];\n" +
                    "\tr.t = Math.max(this.t-n,0);\n" +
                    "\tr.s = this.s;\n" +
                    "}\n" +
                    "\n" +
                    "// (protected) r = this << n\n" +
                    "function bnpLShiftTo(n,r) {\n" +
                    "\tvar bs = n%this.DB;\n" +
                    "\tvar cbs = this.DB-bs;\n" +
                    "\tvar bm = (1<<cbs)-1;\n" +
                    "\tvar ds = Math.floor(n/this.DB), c = (this.s<<bs)&this.DM, i;\n" +
                    "\tfor(i = this.t-1; i >= 0; --i) {\n" +
                    "\t\tr[i+ds+1] = (this[i]>>cbs)|c;\n" +
                    "\t\tc = (this[i]&bm)<<bs;\n" +
                    "\t}\n" +
                    "\tfor(i = ds-1; i >= 0; --i) r[i] = 0;\n" +
                    "\tr[ds] = c;\n" +
                    "\tr.t = this.t+ds+1;\n" +
                    "\tr.s = this.s;\n" +
                    "\tr.clamp();\n" +
                    "}\n" +
                    "\n" +
                    "// (protected) r = this >> n\n" +
                    "function bnpRShiftTo(n,r) {\n" +
                    "\tr.s = this.s;\n" +
                    "\tvar ds = Math.floor(n/this.DB);\n" +
                    "\tif(ds >= this.t) { r.t = 0; return; }\n" +
                    "\tvar bs = n%this.DB;\n" +
                    "\tvar cbs = this.DB-bs;\n" +
                    "\tvar bm = (1<<bs)-1;\n" +
                    "\tr[0] = this[ds]>>bs;\n" +
                    "\tfor(var i = ds+1; i < this.t; ++i) {\n" +
                    "\t\tr[i-ds-1] |= (this[i]&bm)<<cbs;\n" +
                    "\t\tr[i-ds] = this[i]>>bs;\n" +
                    "\t}\n" +
                    "\tif(bs > 0) r[this.t-ds-1] |= (this.s&bm)<<cbs;\n" +
                    "\tr.t = this.t-ds;\n" +
                    "\tr.clamp();\n" +
                    "}\n" +
                    "\n" +
                    "// (protected) r = this - a\n" +
                    "function bnpSubTo(a,r) {\n" +
                    "\tvar i = 0, c = 0, m = Math.min(a.t,this.t);\n" +
                    "\twhile(i < m) {\n" +
                    "\t\tc += this[i]-a[i];\n" +
                    "\t\tr[i++] = c&this.DM;\n" +
                    "\t\tc >>= this.DB;\n" +
                    "\t}\n" +
                    "\tif(a.t < this.t) {\n" +
                    "\t\tc -= a.s;\n" +
                    "\t\twhile(i < this.t) {\n" +
                    "\t\t\tc += this[i];\n" +
                    "\t\t\tr[i++] = c&this.DM;\n" +
                    "\t\t\tc >>= this.DB;\n" +
                    "\t\t}\n" +
                    "\t\tc += this.s;\n" +
                    "\t}\n" +
                    "\telse {\n" +
                    "\t\tc += this.s;\n" +
                    "\t\twhile(i < a.t) {\n" +
                    "\t\t\tc -= a[i];\n" +
                    "\t\t\tr[i++] = c&this.DM;\n" +
                    "\t\t\tc >>= this.DB;\n" +
                    "\t\t}\n" +
                    "\t\tc -= a.s;\n" +
                    "\t}\n" +
                    "\tr.s = (c<0)?-1:0;\n" +
                    "\tif(c < -1) r[i++] = this.DV+c;\n" +
                    "\telse if(c > 0) r[i++] = c;\n" +
                    "\tr.t = i;\n" +
                    "\tr.clamp();\n" +
                    "}\n" +
                    "\n" +
                    "// (protected) r = this * a, r != this,a (HAC 14.12)\n" +
                    "// \"this\" should be the larger one if appropriate.\n" +
                    "function bnpMultiplyTo(a,r) {\n" +
                    "\tvar x = this.abs(), y = a.abs();\n" +
                    "\tvar i = x.t;\n" +
                    "\tr.t = i+y.t;\n" +
                    "\twhile(--i >= 0) r[i] = 0;\n" +
                    "\tfor(i = 0; i < y.t; ++i) r[i+x.t] = x.am(0,y[i],r,i,0,x.t);\n" +
                    "\tr.s = 0;\n" +
                    "\tr.clamp();\n" +
                    "\tif(this.s != a.s) BigInteger.ZERO.subTo(r,r);\n" +
                    "}\n" +
                    "\n" +
                    "// (protected) r = this^2, r != this (HAC 14.16)\n" +
                    "function bnpSquareTo(r) {\n" +
                    "\tvar x = this.abs();\n" +
                    "\tvar i = r.t = 2*x.t;\n" +
                    "\twhile(--i >= 0) r[i] = 0;\n" +
                    "\tfor(i = 0; i < x.t-1; ++i) {\n" +
                    "\t\tvar c = x.am(i,x[i],r,2*i,0,1);\n" +
                    "\t\tif((r[i+x.t]+=x.am(i+1,2*x[i],r,2*i+1,c,x.t-i-1)) >= x.DV) {\n" +
                    "\t\t\tr[i+x.t] -= x.DV;\n" +
                    "\t\t\tr[i+x.t+1] = 1;\n" +
                    "\t\t}\n" +
                    "\t}\n" +
                    "\tif(r.t > 0) r[r.t-1] += x.am(i,x[i],r,2*i,0,1);\n" +
                    "\tr.s = 0;\n" +
                    "\tr.clamp();\n" +
                    "}\n" +
                    "\n" +
                    "// (protected) divide this by m, quotient and remainder to q, r (HAC 14.20)\n" +
                    "// r != q, this != m.  q or r may be null.\n" +
                    "function bnpDivRemTo(m,q,r) {\n" +
                    "\tvar pm = m.abs();\n" +
                    "\tif(pm.t <= 0) return;\n" +
                    "\tvar pt = this.abs();\n" +
                    "\tif(pt.t < pm.t) {\n" +
                    "\t\tif(q != null) q.fromInt(0);\n" +
                    "\t\tif(r != null) this.copyTo(r);\n" +
                    "\t\treturn;\n" +
                    "\t}\n" +
                    "\tif(r == null) r = nbi();\n" +
                    "\tvar y = nbi(), ts = this.s, ms = m.s;\n" +
                    "\tvar nsh = this.DB-nbits(pm[pm.t-1]);    // normalize modulus\n" +
                    "\tif(nsh > 0) { pm.lShiftTo(nsh,y); pt.lShiftTo(nsh,r); }\n" +
                    "\telse { pm.copyTo(y); pt.copyTo(r); }\n" +
                    "\tvar ys = y.t;\n" +
                    "\tvar y0 = y[ys-1];\n" +
                    "\tif(y0 == 0) return;\n" +
                    "\tvar yt = y0*(1<<this.F1)+((ys>1)?y[ys-2]>>this.F2:0);\n" +
                    "\tvar d1 = this.FV/yt, d2 = (1<<this.F1)/yt, e = 1<<this.F2;\n" +
                    "\tvar i = r.t, j = i-ys, t = (q==null)?nbi():q;\n" +
                    "\ty.dlShiftTo(j,t);\n" +
                    "\tif(r.compareTo(t) >= 0) {\n" +
                    "\t\tr[r.t++] = 1;\n" +
                    "\t\tr.subTo(t,r);\n" +
                    "\t}\n" +
                    "\tBigInteger.ONE.dlShiftTo(ys,t);\n" +
                    "\tt.subTo(y,y);    // \"negative\" y so we can replace sub with am later\n" +
                    "\twhile(y.t < ys) y[y.t++] = 0;\n" +
                    "\twhile(--j >= 0) {\n" +
                    "\t\t// Estimate quotient digit\n" +
                    "\t\tvar qd = (r[--i]==y0)?this.DM:Math.floor(r[i]*d1+(r[i-1]+e)*d2);\n" +
                    "\t\tif((r[i]+=y.am(0,qd,r,j,0,ys)) < qd) {    // Try it out\n" +
                    "\t\t\ty.dlShiftTo(j,t);\n" +
                    "\t\t\tr.subTo(t,r);\n" +
                    "\t\t\twhile(r[i] < --qd) r.subTo(t,r);\n" +
                    "\t\t}\n" +
                    "\t}\n" +
                    "\tif(q != null) {\n" +
                    "\t\tr.drShiftTo(ys,q);\n" +
                    "\t\tif(ts != ms) BigInteger.ZERO.subTo(q,q);\n" +
                    "\t}\n" +
                    "\tr.t = ys;\n" +
                    "\tr.clamp();\n" +
                    "\tif(nsh > 0) r.rShiftTo(nsh,r);    // Denormalize remainder\n" +
                    "\tif(ts < 0) BigInteger.ZERO.subTo(r,r);\n" +
                    "}\n" +
                    "\n" +
                    "// (public) this mod a\n" +
                    "function bnMod(a) {\n" +
                    "\tvar r = nbi();\n" +
                    "\tthis.abs().divRemTo(a,null,r);\n" +
                    "\tif(this.s < 0 && r.compareTo(BigInteger.ZERO) > 0) a.subTo(r,r);\n" +
                    "\treturn r;\n" +
                    "}\n" +
                    "\n" +
                    "// Modular reduction using \"classic\" algorithm\n" +
                    "function Classic(m) { this.m = m; }\n" +
                    "function cConvert(x) {\n" +
                    "\tif(x.s < 0 || x.compareTo(this.m) >= 0) return x.mod(this.m);\n" +
                    "\telse return x;\n" +
                    "}\n" +
                    "function cRevert(x) { return x; }\n" +
                    "function cReduce(x) { x.divRemTo(this.m,null,x); }\n" +
                    "function cMulTo(x,y,r) { x.multiplyTo(y,r); this.reduce(r); }\n" +
                    "function cSqrTo(x,r) { x.squareTo(r); this.reduce(r); }\n" +
                    "\n" +
                    "Classic.prototype.convert = cConvert;\n" +
                    "Classic.prototype.revert = cRevert;\n" +
                    "Classic.prototype.reduce = cReduce;\n" +
                    "Classic.prototype.mulTo = cMulTo;\n" +
                    "Classic.prototype.sqrTo = cSqrTo;\n" +
                    "\n" +
                    "// (protected) return \"-1/this % 2^DB\"; useful for Mont. reduction\n" +
                    "// justification:\n" +
                    "//         xy == 1 (mod m)\n" +
                    "//         xy =  1+km\n" +
                    "//   xy(2-xy) = (1+km)(1-km)\n" +
                    "// x[y(2-xy)] = 1-k^2m^2\n" +
                    "// x[y(2-xy)] == 1 (mod m^2)\n" +
                    "// if y is 1/x mod m, then y(2-xy) is 1/x mod m^2\n" +
                    "// should reduce x and y(2-xy) by m^2 at each step to keep size bounded.\n" +
                    "// JS multiply \"overflows\" differently from C/C++, so care is needed here.\n" +
                    "function bnpInvDigit() {\n" +
                    "\tif(this.t < 1) return 0;\n" +
                    "\tvar x = this[0];\n" +
                    "\tif((x&1) == 0) return 0;\n" +
                    "\tvar y = x&3;        // y == 1/x mod 2^2\n" +
                    "\ty = (y*(2-(x&0xf)*y))&0xf;    // y == 1/x mod 2^4\n" +
                    "\ty = (y*(2-(x&0xff)*y))&0xff;    // y == 1/x mod 2^8\n" +
                    "\ty = (y*(2-(((x&0xffff)*y)&0xffff)))&0xffff;    // y == 1/x mod 2^16\n" +
                    "\t// last step - calculate inverse mod DV directly;\n" +
                    "\t// assumes 16 < DB <= 32 and assumes ability to handle 48-bit ints\n" +
                    "\ty = (y*(2-x*y%this.DV))%this.DV;        // y == 1/x mod 2^dbits\n" +
                    "\t// we really want the negative inverse, and -DV < y < DV\n" +
                    "\treturn (y>0)?this.DV-y:-y;\n" +
                    "}\n" +
                    "\n" +
                    "// Montgomery reduction\n" +
                    "function Montgomery(m) {\n" +
                    "\tthis.m = m;\n" +
                    "\tthis.mp = m.invDigit();\n" +
                    "\tthis.mpl = this.mp&0x7fff;\n" +
                    "\tthis.mph = this.mp>>15;\n" +
                    "\tthis.um = (1<<(m.DB-15))-1;\n" +
                    "\tthis.mt2 = 2*m.t;\n" +
                    "}\n" +
                    "\n" +
                    "// xR mod m\n" +
                    "function montConvert(x) {\n" +
                    "\tvar r = nbi();\n" +
                    "\tx.abs().dlShiftTo(this.m.t,r);\n" +
                    "\tr.divRemTo(this.m,null,r);\n" +
                    "\tif(x.s < 0 && r.compareTo(BigInteger.ZERO) > 0) this.m.subTo(r,r);\n" +
                    "\treturn r;\n" +
                    "}\n" +
                    "\n" +
                    "// x/R mod m\n" +
                    "function montRevert(x) {\n" +
                    "\tvar r = nbi();\n" +
                    "\tx.copyTo(r);\n" +
                    "\tthis.reduce(r);\n" +
                    "\treturn r;\n" +
                    "}\n" +
                    "\n" +
                    "// x = x/R mod m (HAC 14.32)\n" +
                    "function montReduce(x) {\n" +
                    "\twhile(x.t <= this.mt2)    // pad x so am has enough room later\n" +
                    "\t\tx[x.t++] = 0;\n" +
                    "\tfor(var i = 0; i < this.m.t; ++i) {\n" +
                    "\t\t// faster way of calculating u0 = x[i]*mp mod DV\n" +
                    "\t\tvar j = x[i]&0x7fff;\n" +
                    "\t\tvar u0 = (j*this.mpl+(((j*this.mph+(x[i]>>15)*this.mpl)&this.um)<<15))&x.DM;\n" +
                    "\t\t// use am to combine the multiply-shift-add into one call\n" +
                    "\t\tj = i+this.m.t;\n" +
                    "\t\tx[j] += this.m.am(0,u0,x,i,0,this.m.t);\n" +
                    "\t\t// propagate carry\n" +
                    "\t\twhile(x[j] >= x.DV) { x[j] -= x.DV; x[++j]++; }\n" +
                    "\t}\n" +
                    "\tx.clamp();\n" +
                    "\tx.drShiftTo(this.m.t,x);\n" +
                    "\tif(x.compareTo(this.m) >= 0) x.subTo(this.m,x);\n" +
                    "}\n" +
                    "\n" +
                    "// r = \"x^2/R mod m\"; x != r\n" +
                    "function montSqrTo(x,r) { x.squareTo(r); this.reduce(r); }\n" +
                    "\n" +
                    "// r = \"xy/R mod m\"; x,y != r\n" +
                    "function montMulTo(x,y,r) { x.multiplyTo(y,r); this.reduce(r); }\n" +
                    "\n" +
                    "Montgomery.prototype.convert = montConvert;\n" +
                    "Montgomery.prototype.revert = montRevert;\n" +
                    "Montgomery.prototype.reduce = montReduce;\n" +
                    "Montgomery.prototype.mulTo = montMulTo;\n" +
                    "Montgomery.prototype.sqrTo = montSqrTo;\n" +
                    "\n" +
                    "// (protected) true iff this is even\n" +
                    "function bnpIsEven() { return ((this.t>0)?(this[0]&1):this.s) == 0; }\n" +
                    "\n" +
                    "// (protected) this^e, e < 2^32, doing sqr and mul with \"r\" (HAC 14.79)\n" +
                    "function bnpExp(e,z) {\n" +
                    "\tif(e > 0xffffffff || e < 1) return BigInteger.ONE;\n" +
                    "\tvar r = nbi(), r2 = nbi(), g = z.convert(this), i = nbits(e)-1;\n" +
                    "\tg.copyTo(r);\n" +
                    "\twhile(--i >= 0) {\n" +
                    "\t\tz.sqrTo(r,r2);\n" +
                    "\t\tif((e&(1<<i)) > 0) z.mulTo(r2,g,r);\n" +
                    "\t\telse { var t = r; r = r2; r2 = t; }\n" +
                    "\t}\n" +
                    "\treturn z.revert(r);\n" +
                    "}\n" +
                    "\n" +
                    "// (public) this^e % m, 0 <= e < 2^32\n" +
                    "function bnModPowInt(e,m) {\n" +
                    "\tvar z;\n" +
                    "\tif(e < 256 || m.isEven()) z = new Classic(m); else z = new Montgomery(m);\n" +
                    "\treturn this.exp(e,z);\n" +
                    "}\n" +
                    "\n" +
                    "// protected\n" +
                    "BigInteger.prototype.copyTo = bnpCopyTo;\n" +
                    "BigInteger.prototype.fromInt = bnpFromInt;\n" +
                    "BigInteger.prototype.fromString = bnpFromString;\n" +
                    "BigInteger.prototype.clamp = bnpClamp;\n" +
                    "BigInteger.prototype.dlShiftTo = bnpDLShiftTo;\n" +
                    "BigInteger.prototype.drShiftTo = bnpDRShiftTo;\n" +
                    "BigInteger.prototype.lShiftTo = bnpLShiftTo;\n" +
                    "BigInteger.prototype.rShiftTo = bnpRShiftTo;\n" +
                    "BigInteger.prototype.subTo = bnpSubTo;\n" +
                    "BigInteger.prototype.multiplyTo = bnpMultiplyTo;\n" +
                    "BigInteger.prototype.squareTo = bnpSquareTo;\n" +
                    "BigInteger.prototype.divRemTo = bnpDivRemTo;\n" +
                    "BigInteger.prototype.invDigit = bnpInvDigit;\n" +
                    "BigInteger.prototype.isEven = bnpIsEven;\n" +
                    "BigInteger.prototype.exp = bnpExp;\n" +
                    "\n" +
                    "// public\n" +
                    "BigInteger.prototype.toString = bnToString;\n" +
                    "BigInteger.prototype.negate = bnNegate;\n" +
                    "BigInteger.prototype.abs = bnAbs;\n" +
                    "BigInteger.prototype.compareTo = bnCompareTo;\n" +
                    "BigInteger.prototype.bitLength = bnBitLength;\n" +
                    "BigInteger.prototype.mod = bnMod;\n" +
                    "BigInteger.prototype.modPowInt = bnModPowInt;\n" +
                    "\n" +
                    "// \"constants\"\n" +
                    "BigInteger.ZERO = nbv(0);\n" +
                    "BigInteger.ONE = nbv(1);\n" +
                    "\n" +
                    "\n" +
                    "// Copyright (c) 2005  Tom Wu\n" +
                    "// All Rights Reserved.\n" +
                    "// See \"LICENSE\" for details.\n" +
                    "\n" +
                    "// Extended JavaScript BN functions, required for RSA private ops.\n" +
                    "\n" +
                    "// (public)\n" +
                    "function bnClone() { var r = nbi(); this.copyTo(r); return r; }\n" +
                    "\n" +
                    "// (public) return value as integer\n" +
                    "function bnIntValue() {\n" +
                    "\tif(this.s < 0) {\n" +
                    "\t\tif(this.t == 1) return this[0]-this.DV;\n" +
                    "\t\telse if(this.t == 0) return -1;\n" +
                    "\t}\n" +
                    "\telse if(this.t == 1) return this[0];\n" +
                    "\telse if(this.t == 0) return 0;\n" +
                    "\t// assumes 16 < DB < 32\n" +
                    "\treturn ((this[1]&((1<<(32-this.DB))-1))<<this.DB)|this[0];\n" +
                    "}\n" +
                    "\n" +
                    "// (public) return value as byte\n" +
                    "function bnByteValue() { return (this.t==0)?this.s:(this[0]<<24)>>24; }\n" +
                    "\n" +
                    "// (public) return value as short (assumes DB>=16)\n" +
                    "function bnShortValue() { return (this.t==0)?this.s:(this[0]<<16)>>16; }\n" +
                    "\n" +
                    "// (protected) return x s.t. r^x < DV\n" +
                    "function bnpChunkSize(r) { return Math.floor(Math.LN2*this.DB/Math.log(r)); }\n" +
                    "\n" +
                    "// (public) 0 if this == 0, 1 if this > 0\n" +
                    "function bnSigNum() {\n" +
                    "\tif(this.s < 0) return -1;\n" +
                    "\telse if(this.t <= 0 || (this.t == 1 && this[0] <= 0)) return 0;\n" +
                    "\telse return 1;\n" +
                    "}\n" +
                    "\n" +
                    "// (protected) convert to radix string\n" +
                    "function bnpToRadix(b) {\n" +
                    "\tif(b == null) b = 10;\n" +
                    "\tif(this.signum() == 0 || b < 2 || b > 36) return \"0\";\n" +
                    "\tvar cs = this.chunkSize(b);\n" +
                    "\tvar a = Math.pow(b,cs);\n" +
                    "\tvar d = nbv(a), y = nbi(), z = nbi(), r = \"\";\n" +
                    "\tthis.divRemTo(d,y,z);\n" +
                    "\twhile(y.signum() > 0) {\n" +
                    "\t\tr = (a+z.intValue()).toString(b).substr(1) + r;\n" +
                    "\t\ty.divRemTo(d,y,z);\n" +
                    "\t}\n" +
                    "\treturn z.intValue().toString(b) + r;\n" +
                    "}\n" +
                    "\n" +
                    "// (protected) convert from radix string\n" +
                    "function bnpFromRadix(s,b) {\n" +
                    "\tthis.fromInt(0);\n" +
                    "\tif(b == null) b = 10;\n" +
                    "\tvar cs = this.chunkSize(b);\n" +
                    "\tvar d = Math.pow(b,cs), mi = false, j = 0, w = 0;\n" +
                    "\tfor(var i = 0; i < s.length; ++i) {\n" +
                    "\t\tvar x = intAt(s,i);\n" +
                    "\t\tif(x < 0) {\n" +
                    "\t\t\tif(s.charAt(i) == \"-\" && this.signum() == 0) mi = true;\n" +
                    "\t\t\tcontinue;\n" +
                    "\t\t}\n" +
                    "\t\tw = b*w+x;\n" +
                    "\t\tif(++j >= cs) {\n" +
                    "\t\t\tthis.dMultiply(d);\n" +
                    "\t\t\tthis.dAddOffset(w,0);\n" +
                    "\t\t\tj = 0;\n" +
                    "\t\t\tw = 0;\n" +
                    "\t\t}\n" +
                    "\t}\n" +
                    "\tif(j > 0) {\n" +
                    "\t\tthis.dMultiply(Math.pow(b,j));\n" +
                    "\t\tthis.dAddOffset(w,0);\n" +
                    "\t}\n" +
                    "\tif(mi) BigInteger.ZERO.subTo(this,this);\n" +
                    "}\n" +
                    "\n" +
                    "// (protected) alternate constructor\n" +
                    "function bnpFromNumber(a,b,c) {\n" +
                    "\tif(\"number\" == typeof b) {\n" +
                    "\t\t// new BigInteger(int,int,RNG)\n" +
                    "\t\tif(a < 2) this.fromInt(1);\n" +
                    "\t\telse {\n" +
                    "\t\t\tthis.fromNumber(a,c);\n" +
                    "\t\t\tif(!this.testBit(a-1))    // force MSB set\n" +
                    "\t\t\t\tthis.bitwiseTo(BigInteger.ONE.shiftLeft(a-1),op_or,this);\n" +
                    "\t\t\tif(this.isEven()) this.dAddOffset(1,0); // force odd\n" +
                    "\t\t\twhile(!this.isProbablePrime(b)) {\n" +
                    "\t\t\t\tthis.dAddOffset(2,0);\n" +
                    "\t\t\t\tif(this.bitLength() > a) this.subTo(BigInteger.ONE.shiftLeft(a-1),this);\n" +
                    "\t\t\t}\n" +
                    "\t\t}\n" +
                    "\t}\n" +
                    "\telse {\n" +
                    "\t\t// new BigInteger(int,RNG)\n" +
                    "\t\tvar x = new Array(), t = a&7;\n" +
                    "\t\tx.length = (a>>3)+1;\n" +
                    "\t\tb.nextBytes(x);\n" +
                    "\t\tif(t > 0) x[0] &= ((1<<t)-1); else x[0] = 0;\n" +
                    "\t\tthis.fromString(x,256);\n" +
                    "\t}\n" +
                    "}\n" +
                    "\n" +
                    "// (public) convert to bigendian byte array\n" +
                    "function bnToByteArray() {\n" +
                    "\tvar i = this.t, r = new Array();\n" +
                    "\tr[0] = this.s;\n" +
                    "\tvar p = this.DB-(i*this.DB)%8, d, k = 0;\n" +
                    "\tif(i-- > 0) {\n" +
                    "\t\tif(p < this.DB && (d = this[i]>>p) != (this.s&this.DM)>>p)\n" +
                    "\t\t\tr[k++] = d|(this.s<<(this.DB-p));\n" +
                    "\t\twhile(i >= 0) {\n" +
                    "\t\t\tif(p < 8) {\n" +
                    "\t\t\t\td = (this[i]&((1<<p)-1))<<(8-p);\n" +
                    "\t\t\t\td |= this[--i]>>(p+=this.DB-8);\n" +
                    "\t\t\t}\n" +
                    "\t\t\telse {\n" +
                    "\t\t\t\td = (this[i]>>(p-=8))&0xff;\n" +
                    "\t\t\t\tif(p <= 0) { p += this.DB; --i; }\n" +
                    "\t\t\t}\n" +
                    "\t\t\tif((d&0x80) != 0) d |= -256;\n" +
                    "\t\t\tif(k == 0 && (this.s&0x80) != (d&0x80)) ++k;\n" +
                    "\t\t\tif(k > 0 || d != this.s) r[k++] = d;\n" +
                    "\t\t}\n" +
                    "\t}\n" +
                    "\treturn r;\n" +
                    "}\n" +
                    "\n" +
                    "function bnEquals(a) { return(this.compareTo(a)==0); }\n" +
                    "function bnMin(a) { return(this.compareTo(a)<0)?this:a; }\n" +
                    "function bnMax(a) { return(this.compareTo(a)>0)?this:a; }\n" +
                    "\n" +
                    "// (protected) r = this op a (bitwise)\n" +
                    "function bnpBitwiseTo(a,op,r) {\n" +
                    "\tvar i, f, m = Math.min(a.t,this.t);\n" +
                    "\tfor(i = 0; i < m; ++i) r[i] = op(this[i],a[i]);\n" +
                    "\tif(a.t < this.t) {\n" +
                    "\t\tf = a.s&this.DM;\n" +
                    "\t\tfor(i = m; i < this.t; ++i) r[i] = op(this[i],f);\n" +
                    "\t\tr.t = this.t;\n" +
                    "\t}\n" +
                    "\telse {\n" +
                    "\t\tf = this.s&this.DM;\n" +
                    "\t\tfor(i = m; i < a.t; ++i) r[i] = op(f,a[i]);\n" +
                    "\t\tr.t = a.t;\n" +
                    "\t}\n" +
                    "\tr.s = op(this.s,a.s);\n" +
                    "\tr.clamp();\n" +
                    "}\n" +
                    "\n" +
                    "// (public) this & a\n" +
                    "function op_and(x,y) { return x&y; }\n" +
                    "function bnAnd(a) { var r = nbi(); this.bitwiseTo(a,op_and,r); return r; }\n" +
                    "\n" +
                    "// (public) this | a\n" +
                    "function op_or(x,y) { return x|y; }\n" +
                    "function bnOr(a) { var r = nbi(); this.bitwiseTo(a,op_or,r); return r; }\n" +
                    "\n" +
                    "// (public) this ^ a\n" +
                    "function op_xor(x,y) { return x^y; }\n" +
                    "function bnXor(a) { var r = nbi(); this.bitwiseTo(a,op_xor,r); return r; }\n" +
                    "\n" +
                    "// (public) this & ~a\n" +
                    "function op_andnot(x,y) { return x&~y; }\n" +
                    "function bnAndNot(a) { var r = nbi(); this.bitwiseTo(a,op_andnot,r); return r; }\n" +
                    "\n" +
                    "// (public) ~this\n" +
                    "function bnNot() {\n" +
                    "\tvar r = nbi();\n" +
                    "\tfor(var i = 0; i < this.t; ++i) r[i] = this.DM&~this[i];\n" +
                    "\tr.t = this.t;\n" +
                    "\tr.s = ~this.s;\n" +
                    "\treturn r;\n" +
                    "}\n" +
                    "\n" +
                    "// (public) this << n\n" +
                    "function bnShiftLeft(n) {\n" +
                    "\tvar r = nbi();\n" +
                    "\tif(n < 0) this.rShiftTo(-n,r); else this.lShiftTo(n,r);\n" +
                    "\treturn r;\n" +
                    "}\n" +
                    "\n" +
                    "// (public) this >> n\n" +
                    "function bnShiftRight(n) {\n" +
                    "\tvar r = nbi();\n" +
                    "\tif(n < 0) this.lShiftTo(-n,r); else this.rShiftTo(n,r);\n" +
                    "\treturn r;\n" +
                    "}\n" +
                    "\n" +
                    "// return index of lowest 1-bit in x, x < 2^31\n" +
                    "function lbit(x) {\n" +
                    "\tif(x == 0) return -1;\n" +
                    "\tvar r = 0;\n" +
                    "\tif((x&0xffff) == 0) { x >>= 16; r += 16; }\n" +
                    "\tif((x&0xff) == 0) { x >>= 8; r += 8; }\n" +
                    "\tif((x&0xf) == 0) { x >>= 4; r += 4; }\n" +
                    "\tif((x&3) == 0) { x >>= 2; r += 2; }\n" +
                    "\tif((x&1) == 0) ++r;\n" +
                    "\treturn r;\n" +
                    "}\n" +
                    "\n" +
                    "// (public) returns index of lowest 1-bit (or -1 if none)\n" +
                    "function bnGetLowestSetBit() {\n" +
                    "\tfor(var i = 0; i < this.t; ++i)\n" +
                    "\t\tif(this[i] != 0) return i*this.DB+lbit(this[i]);\n" +
                    "\tif(this.s < 0) return this.t*this.DB;\n" +
                    "\treturn -1;\n" +
                    "}\n" +
                    "\n" +
                    "// return number of 1 bits in x\n" +
                    "function cbit(x) {\n" +
                    "\tvar r = 0;\n" +
                    "\twhile(x != 0) { x &= x-1; ++r; }\n" +
                    "\treturn r;\n" +
                    "}\n" +
                    "\n" +
                    "// (public) return number of set bits\n" +
                    "function bnBitCount() {\n" +
                    "\tvar r = 0, x = this.s&this.DM;\n" +
                    "\tfor(var i = 0; i < this.t; ++i) r += cbit(this[i]^x);\n" +
                    "\treturn r;\n" +
                    "}\n" +
                    "\n" +
                    "// (public) true iff nth bit is set\n" +
                    "function bnTestBit(n) {\n" +
                    "\tvar j = Math.floor(n/this.DB);\n" +
                    "\tif(j >= this.t) return(this.s!=0);\n" +
                    "\treturn((this[j]&(1<<(n%this.DB)))!=0);\n" +
                    "}\n" +
                    "\n" +
                    "// (protected) this op (1<<n)\n" +
                    "function bnpChangeBit(n,op) {\n" +
                    "\tvar r = BigInteger.ONE.shiftLeft(n);\n" +
                    "\tthis.bitwiseTo(r,op,r);\n" +
                    "\treturn r;\n" +
                    "}\n" +
                    "\n" +
                    "// (public) this | (1<<n)\n" +
                    "function bnSetBit(n) { return this.changeBit(n,op_or); }\n" +
                    "\n" +
                    "// (public) this & ~(1<<n)\n" +
                    "function bnClearBit(n) { return this.changeBit(n,op_andnot); }\n" +
                    "\n" +
                    "// (public) this ^ (1<<n)\n" +
                    "function bnFlipBit(n) { return this.changeBit(n,op_xor); }\n" +
                    "\n" +
                    "// (protected) r = this + a\n" +
                    "function bnpAddTo(a,r) {\n" +
                    "\tvar i = 0, c = 0, m = Math.min(a.t,this.t);\n" +
                    "\twhile(i < m) {\n" +
                    "\t\tc += this[i]+a[i];\n" +
                    "\t\tr[i++] = c&this.DM;\n" +
                    "\t\tc >>= this.DB;\n" +
                    "\t}\n" +
                    "\tif(a.t < this.t) {\n" +
                    "\t\tc += a.s;\n" +
                    "\t\twhile(i < this.t) {\n" +
                    "\t\t\tc += this[i];\n" +
                    "\t\t\tr[i++] = c&this.DM;\n" +
                    "\t\t\tc >>= this.DB;\n" +
                    "\t\t}\n" +
                    "\t\tc += this.s;\n" +
                    "\t}\n" +
                    "\telse {\n" +
                    "\t\tc += this.s;\n" +
                    "\t\twhile(i < a.t) {\n" +
                    "\t\t\tc += a[i];\n" +
                    "\t\t\tr[i++] = c&this.DM;\n" +
                    "\t\t\tc >>= this.DB;\n" +
                    "\t\t}\n" +
                    "\t\tc += a.s;\n" +
                    "\t}\n" +
                    "\tr.s = (c<0)?-1:0;\n" +
                    "\tif(c > 0) r[i++] = c;\n" +
                    "\telse if(c < -1) r[i++] = this.DV+c;\n" +
                    "\tr.t = i;\n" +
                    "\tr.clamp();\n" +
                    "}\n" +
                    "\n" +
                    "// (public) this + a\n" +
                    "function bnAdd(a) { var r = nbi(); this.addTo(a,r); return r; }\n" +
                    "\n" +
                    "// (public) this - a\n" +
                    "function bnSubtract(a) { var r = nbi(); this.subTo(a,r); return r; }\n" +
                    "\n" +
                    "// (public) this * a\n" +
                    "function bnMultiply(a) { var r = nbi(); this.multiplyTo(a,r); return r; }\n" +
                    "\n" +
                    "// (public) this / a\n" +
                    "function bnDivide(a) { var r = nbi(); this.divRemTo(a,r,null); return r; }\n" +
                    "\n" +
                    "// (public) this % a\n" +
                    "function bnRemainder(a) { var r = nbi(); this.divRemTo(a,null,r); return r; }\n" +
                    "\n" +
                    "// (public) [this/a,this%a]\n" +
                    "function bnDivideAndRemainder(a) {\n" +
                    "\tvar q = nbi(), r = nbi();\n" +
                    "\tthis.divRemTo(a,q,r);\n" +
                    "\treturn new Array(q,r);\n" +
                    "}\n" +
                    "\n" +
                    "// (protected) this *= n, this >= 0, 1 < n < DV\n" +
                    "function bnpDMultiply(n) {\n" +
                    "\tthis[this.t] = this.am(0,n-1,this,0,0,this.t);\n" +
                    "\t++this.t;\n" +
                    "\tthis.clamp();\n" +
                    "}\n" +
                    "\n" +
                    "// (protected) this += n << w words, this >= 0\n" +
                    "function bnpDAddOffset(n,w) {\n" +
                    "\twhile(this.t <= w) this[this.t++] = 0;\n" +
                    "\tthis[w] += n;\n" +
                    "\twhile(this[w] >= this.DV) {\n" +
                    "\t\tthis[w] -= this.DV;\n" +
                    "\t\tif(++w >= this.t) this[this.t++] = 0;\n" +
                    "\t\t++this[w];\n" +
                    "\t}\n" +
                    "}\n" +
                    "\n" +
                    "// A \"null\" reducer\n" +
                    "function NullExp() {}\n" +
                    "function nNop(x) { return x; }\n" +
                    "function nMulTo(x,y,r) { x.multiplyTo(y,r); }\n" +
                    "function nSqrTo(x,r) { x.squareTo(r); }\n" +
                    "\n" +
                    "NullExp.prototype.convert = nNop;\n" +
                    "NullExp.prototype.revert = nNop;\n" +
                    "NullExp.prototype.mulTo = nMulTo;\n" +
                    "NullExp.prototype.sqrTo = nSqrTo;\n" +
                    "\n" +
                    "// (public) this^e\n" +
                    "function bnPow(e) { return this.exp(e,new NullExp()); }\n" +
                    "\n" +
                    "// (protected) r = lower n words of \"this * a\", a.t <= n\n" +
                    "// \"this\" should be the larger one if appropriate.\n" +
                    "function bnpMultiplyLowerTo(a,n,r) {\n" +
                    "\tvar i = Math.min(this.t+a.t,n);\n" +
                    "\tr.s = 0; // assumes a,this >= 0\n" +
                    "\tr.t = i;\n" +
                    "\twhile(i > 0) r[--i] = 0;\n" +
                    "\tvar j;\n" +
                    "\tfor(j = r.t-this.t; i < j; ++i) r[i+this.t] = this.am(0,a[i],r,i,0,this.t);\n" +
                    "\tfor(j = Math.min(a.t,n); i < j; ++i) this.am(0,a[i],r,i,0,n-i);\n" +
                    "\tr.clamp();\n" +
                    "}\n" +
                    "\n" +
                    "// (protected) r = \"this * a\" without lower n words, n > 0\n" +
                    "// \"this\" should be the larger one if appropriate.\n" +
                    "function bnpMultiplyUpperTo(a,n,r) {\n" +
                    "\t--n;\n" +
                    "\tvar i = r.t = this.t+a.t-n;\n" +
                    "\tr.s = 0; // assumes a,this >= 0\n" +
                    "\twhile(--i >= 0) r[i] = 0;\n" +
                    "\tfor(i = Math.max(n-this.t,0); i < a.t; ++i)\n" +
                    "\t\tr[this.t+i-n] = this.am(n-i,a[i],r,0,0,this.t+i-n);\n" +
                    "\tr.clamp();\n" +
                    "\tr.drShiftTo(1,r);\n" +
                    "}\n" +
                    "\n" +
                    "// Barrett modular reduction\n" +
                    "function Barrett(m) {\n" +
                    "\t// setup Barrett\n" +
                    "\tthis.r2 = nbi();\n" +
                    "\tthis.q3 = nbi();\n" +
                    "\tBigInteger.ONE.dlShiftTo(2*m.t,this.r2);\n" +
                    "\tthis.mu = this.r2.divide(m);\n" +
                    "\tthis.m = m;\n" +
                    "}\n" +
                    "\n" +
                    "function barrettConvert(x) {\n" +
                    "\tif(x.s < 0 || x.t > 2*this.m.t) return x.mod(this.m);\n" +
                    "\telse if(x.compareTo(this.m) < 0) return x;\n" +
                    "\telse { var r = nbi(); x.copyTo(r); this.reduce(r); return r; }\n" +
                    "}\n" +
                    "\n" +
                    "function barrettRevert(x) { return x; }\n" +
                    "\n" +
                    "// x = x mod m (HAC 14.42)\n" +
                    "function barrettReduce(x) {\n" +
                    "\tx.drShiftTo(this.m.t-1,this.r2);\n" +
                    "\tif(x.t > this.m.t+1) { x.t = this.m.t+1; x.clamp(); }\n" +
                    "\tthis.mu.multiplyUpperTo(this.r2,this.m.t+1,this.q3);\n" +
                    "\tthis.m.multiplyLowerTo(this.q3,this.m.t+1,this.r2);\n" +
                    "\twhile(x.compareTo(this.r2) < 0) x.dAddOffset(1,this.m.t+1);\n" +
                    "\tx.subTo(this.r2,x);\n" +
                    "\twhile(x.compareTo(this.m) >= 0) x.subTo(this.m,x);\n" +
                    "}\n" +
                    "\n" +
                    "// r = x^2 mod m; x != r\n" +
                    "function barrettSqrTo(x,r) { x.squareTo(r); this.reduce(r); }\n" +
                    "\n" +
                    "// r = x*y mod m; x,y != r\n" +
                    "function barrettMulTo(x,y,r) { x.multiplyTo(y,r); this.reduce(r); }\n" +
                    "\n" +
                    "Barrett.prototype.convert = barrettConvert;\n" +
                    "Barrett.prototype.revert = barrettRevert;\n" +
                    "Barrett.prototype.reduce = barrettReduce;\n" +
                    "Barrett.prototype.mulTo = barrettMulTo;\n" +
                    "Barrett.prototype.sqrTo = barrettSqrTo;\n" +
                    "\n" +
                    "// (public) this^e % m (HAC 14.85)\n" +
                    "function bnModPow(e,m) {\n" +
                    "\tvar i = e.bitLength(), k, r = nbv(1), z;\n" +
                    "\tif(i <= 0) return r;\n" +
                    "\telse if(i < 18) k = 1;\n" +
                    "\telse if(i < 48) k = 3;\n" +
                    "\telse if(i < 144) k = 4;\n" +
                    "\telse if(i < 768) k = 5;\n" +
                    "\telse k = 6;\n" +
                    "\tif(i < 8)\n" +
                    "\t\tz = new Classic(m);\n" +
                    "\telse if(m.isEven())\n" +
                    "\t\tz = new Barrett(m);\n" +
                    "\telse\n" +
                    "\t\tz = new Montgomery(m);\n" +
                    "\n" +
                    "\t// precomputation\n" +
                    "\tvar g = new Array(), n = 3, k1 = k-1, km = (1<<k)-1;\n" +
                    "\tg[1] = z.convert(this);\n" +
                    "\tif(k > 1) {\n" +
                    "\t\tvar g2 = nbi();\n" +
                    "\t\tz.sqrTo(g[1],g2);\n" +
                    "\t\twhile(n <= km) {\n" +
                    "\t\t\tg[n] = nbi();\n" +
                    "\t\t\tz.mulTo(g2,g[n-2],g[n]);\n" +
                    "\t\t\tn += 2;\n" +
                    "\t\t}\n" +
                    "\t}\n" +
                    "\n" +
                    "\tvar j = e.t-1, w, is1 = true, r2 = nbi(), t;\n" +
                    "\ti = nbits(e[j])-1;\n" +
                    "\twhile(j >= 0) {\n" +
                    "\t\tif(i >= k1) w = (e[j]>>(i-k1))&km;\n" +
                    "\t\telse {\n" +
                    "\t\t\tw = (e[j]&((1<<(i+1))-1))<<(k1-i);\n" +
                    "\t\t\tif(j > 0) w |= e[j-1]>>(this.DB+i-k1);\n" +
                    "\t\t}\n" +
                    "\n" +
                    "\t\tn = k;\n" +
                    "\t\twhile((w&1) == 0) { w >>= 1; --n; }\n" +
                    "\t\tif((i -= n) < 0) { i += this.DB; --j; }\n" +
                    "\t\tif(is1) {    // ret == 1, don't bother squaring or multiplying it\n" +
                    "\t\t\tg[w].copyTo(r);\n" +
                    "\t\t\tis1 = false;\n" +
                    "\t\t}\n" +
                    "\t\telse {\n" +
                    "\t\t\twhile(n > 1) { z.sqrTo(r,r2); z.sqrTo(r2,r); n -= 2; }\n" +
                    "\t\t\tif(n > 0) z.sqrTo(r,r2); else { t = r; r = r2; r2 = t; }\n" +
                    "\t\t\tz.mulTo(r2,g[w],r);\n" +
                    "\t\t}\n" +
                    "\n" +
                    "\t\twhile(j >= 0 && (e[j]&(1<<i)) == 0) {\n" +
                    "\t\t\tz.sqrTo(r,r2); t = r; r = r2; r2 = t;\n" +
                    "\t\t\tif(--i < 0) { i = this.DB-1; --j; }\n" +
                    "\t\t}\n" +
                    "\t}\n" +
                    "\treturn z.revert(r);\n" +
                    "}\n" +
                    "\n" +
                    "// (public) gcd(this,a) (HAC 14.54)\n" +
                    "function bnGCD(a) {\n" +
                    "\tvar x = (this.s<0)?this.negate():this.clone();\n" +
                    "\tvar y = (a.s<0)?a.negate():a.clone();\n" +
                    "\tif(x.compareTo(y) < 0) { var t = x; x = y; y = t; }\n" +
                    "\tvar i = x.getLowestSetBit(), g = y.getLowestSetBit();\n" +
                    "\tif(g < 0) return x;\n" +
                    "\tif(i < g) g = i;\n" +
                    "\tif(g > 0) {\n" +
                    "\t\tx.rShiftTo(g,x);\n" +
                    "\t\ty.rShiftTo(g,y);\n" +
                    "\t}\n" +
                    "\twhile(x.signum() > 0) {\n" +
                    "\t\tif((i = x.getLowestSetBit()) > 0) x.rShiftTo(i,x);\n" +
                    "\t\tif((i = y.getLowestSetBit()) > 0) y.rShiftTo(i,y);\n" +
                    "\t\tif(x.compareTo(y) >= 0) {\n" +
                    "\t\t\tx.subTo(y,x);\n" +
                    "\t\t\tx.rShiftTo(1,x);\n" +
                    "\t\t}\n" +
                    "\t\telse {\n" +
                    "\t\t\ty.subTo(x,y);\n" +
                    "\t\t\ty.rShiftTo(1,y);\n" +
                    "\t\t}\n" +
                    "\t}\n" +
                    "\tif(g > 0) y.lShiftTo(g,y);\n" +
                    "\treturn y;\n" +
                    "}\n" +
                    "\n" +
                    "// (protected) this % n, n < 2^26\n" +
                    "function bnpModInt(n) {\n" +
                    "\tif(n <= 0) return 0;\n" +
                    "\tvar d = this.DV%n, r = (this.s<0)?n-1:0;\n" +
                    "\tif(this.t > 0)\n" +
                    "\t\tif(d == 0) r = this[0]%n;\n" +
                    "\t\telse for(var i = this.t-1; i >= 0; --i) r = (d*r+this[i])%n;\n" +
                    "\treturn r;\n" +
                    "}\n" +
                    "\n" +
                    "// (public) 1/this % m (HAC 14.61)\n" +
                    "function bnModInverse(m) {\n" +
                    "\tvar ac = m.isEven();\n" +
                    "\tif((this.isEven() && ac) || m.signum() == 0) return BigInteger.ZERO;\n" +
                    "\tvar u = m.clone(), v = this.clone();\n" +
                    "\tvar a = nbv(1), b = nbv(0), c = nbv(0), d = nbv(1);\n" +
                    "\twhile(u.signum() != 0) {\n" +
                    "\t\twhile(u.isEven()) {\n" +
                    "\t\t\tu.rShiftTo(1,u);\n" +
                    "\t\t\tif(ac) {\n" +
                    "\t\t\t\tif(!a.isEven() || !b.isEven()) { a.addTo(this,a); b.subTo(m,b); }\n" +
                    "\t\t\t\ta.rShiftTo(1,a);\n" +
                    "\t\t\t}\n" +
                    "\t\t\telse if(!b.isEven()) b.subTo(m,b);\n" +
                    "\t\t\tb.rShiftTo(1,b);\n" +
                    "\t\t}\n" +
                    "\t\twhile(v.isEven()) {\n" +
                    "\t\t\tv.rShiftTo(1,v);\n" +
                    "\t\t\tif(ac) {\n" +
                    "\t\t\t\tif(!c.isEven() || !d.isEven()) { c.addTo(this,c); d.subTo(m,d); }\n" +
                    "\t\t\t\tc.rShiftTo(1,c);\n" +
                    "\t\t\t}\n" +
                    "\t\t\telse if(!d.isEven()) d.subTo(m,d);\n" +
                    "\t\t\td.rShiftTo(1,d);\n" +
                    "\t\t}\n" +
                    "\t\tif(u.compareTo(v) >= 0) {\n" +
                    "\t\t\tu.subTo(v,u);\n" +
                    "\t\t\tif(ac) a.subTo(c,a);\n" +
                    "\t\t\tb.subTo(d,b);\n" +
                    "\t\t}\n" +
                    "\t\telse {\n" +
                    "\t\t\tv.subTo(u,v);\n" +
                    "\t\t\tif(ac) c.subTo(a,c);\n" +
                    "\t\t\td.subTo(b,d);\n" +
                    "\t\t}\n" +
                    "\t}\n" +
                    "\tif(v.compareTo(BigInteger.ONE) != 0) return BigInteger.ZERO;\n" +
                    "\tif(d.compareTo(m) >= 0) return d.subtract(m);\n" +
                    "\tif(d.signum() < 0) d.addTo(m,d); else return d;\n" +
                    "\tif(d.signum() < 0) return d.add(m); else return d;\n" +
                    "}\n" +
                    "\n" +
                    "var lowprimes = [2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97,101,103,107,109,113,127,131,137,139,149,151,157,163,167,173,179,181,191,193,197,199,211,223,227,229,233,239,241,251,257,263,269,271,277,281,283,293,307,311,313,317,331,337,347,349,353,359,367,373,379,383,389,397,401,409,419,421,431,433,439,443,449,457,461,463,467,479,487,491,499,503,509];\n" +
                    "var lplim = (1<<26)/lowprimes[lowprimes.length-1];\n" +
                    "\n" +
                    "// (public) test primality with certainty >= 1-.5^t\n" +
                    "function bnIsProbablePrime(t) {\n" +
                    "\tvar i, x = this.abs();\n" +
                    "\tif(x.t == 1 && x[0] <= lowprimes[lowprimes.length-1]) {\n" +
                    "\t\tfor(i = 0; i < lowprimes.length; ++i)\n" +
                    "\t\t\tif(x[0] == lowprimes[i]) return true;\n" +
                    "\t\treturn false;\n" +
                    "\t}\n" +
                    "\tif(x.isEven()) return false;\n" +
                    "\ti = 1;\n" +
                    "\twhile(i < lowprimes.length) {\n" +
                    "\t\tvar m = lowprimes[i], j = i+1;\n" +
                    "\t\twhile(j < lowprimes.length && m < lplim) m *= lowprimes[j++];\n" +
                    "\t\tm = x.modInt(m);\n" +
                    "\t\twhile(i < j) if(m%lowprimes[i++] == 0) return false;\n" +
                    "\t}\n" +
                    "\treturn x.millerRabin(t);\n" +
                    "}\n" +
                    "\n" +
                    "// (protected) true if probably prime (HAC 4.24, Miller-Rabin)\n" +
                    "function bnpMillerRabin(t) {\n" +
                    "\tvar n1 = this.subtract(BigInteger.ONE);\n" +
                    "\tvar k = n1.getLowestSetBit();\n" +
                    "\tif(k <= 0) return false;\n" +
                    "\tvar r = n1.shiftRight(k);\n" +
                    "\tt = (t+1)>>1;\n" +
                    "\tif(t > lowprimes.length) t = lowprimes.length;\n" +
                    "\tvar a = nbi();\n" +
                    "\tfor(var i = 0; i < t; ++i) {\n" +
                    "\t\ta.fromInt(lowprimes[i]);\n" +
                    "\t\tvar y = a.modPow(r,this);\n" +
                    "\t\tif(y.compareTo(BigInteger.ONE) != 0 && y.compareTo(n1) != 0) {\n" +
                    "\t\t\tvar j = 1;\n" +
                    "\t\t\twhile(j++ < k && y.compareTo(n1) != 0) {\n" +
                    "\t\t\t\ty = y.modPowInt(2,this);\n" +
                    "\t\t\t\tif(y.compareTo(BigInteger.ONE) == 0) return false;\n" +
                    "\t\t\t}\n" +
                    "\t\t\tif(y.compareTo(n1) != 0) return false;\n" +
                    "\t\t}\n" +
                    "\t}\n" +
                    "\treturn true;\n" +
                    "}\n" +
                    "\n" +
                    "// protected\n" +
                    "BigInteger.prototype.chunkSize = bnpChunkSize;\n" +
                    "BigInteger.prototype.toRadix = bnpToRadix;\n" +
                    "BigInteger.prototype.fromRadix = bnpFromRadix;\n" +
                    "BigInteger.prototype.fromNumber = bnpFromNumber;\n" +
                    "BigInteger.prototype.bitwiseTo = bnpBitwiseTo;\n" +
                    "BigInteger.prototype.changeBit = bnpChangeBit;\n" +
                    "BigInteger.prototype.addTo = bnpAddTo;\n" +
                    "BigInteger.prototype.dMultiply = bnpDMultiply;\n" +
                    "BigInteger.prototype.dAddOffset = bnpDAddOffset;\n" +
                    "BigInteger.prototype.multiplyLowerTo = bnpMultiplyLowerTo;\n" +
                    "BigInteger.prototype.multiplyUpperTo = bnpMultiplyUpperTo;\n" +
                    "BigInteger.prototype.modInt = bnpModInt;\n" +
                    "BigInteger.prototype.millerRabin = bnpMillerRabin;\n" +
                    "\n" +
                    "// public\n" +
                    "BigInteger.prototype.clone = bnClone;\n" +
                    "BigInteger.prototype.intValue = bnIntValue;\n" +
                    "BigInteger.prototype.byteValue = bnByteValue;\n" +
                    "BigInteger.prototype.shortValue = bnShortValue;\n" +
                    "BigInteger.prototype.signum = bnSigNum;\n" +
                    "BigInteger.prototype.toByteArray = bnToByteArray;\n" +
                    "BigInteger.prototype.equals = bnEquals;\n" +
                    "BigInteger.prototype.min = bnMin;\n" +
                    "BigInteger.prototype.max = bnMax;\n" +
                    "BigInteger.prototype.and = bnAnd;\n" +
                    "BigInteger.prototype.or = bnOr;\n" +
                    "BigInteger.prototype.xor = bnXor;\n" +
                    "BigInteger.prototype.andNot = bnAndNot;\n" +
                    "BigInteger.prototype.not = bnNot;\n" +
                    "BigInteger.prototype.shiftLeft = bnShiftLeft;\n" +
                    "BigInteger.prototype.shiftRight = bnShiftRight;\n" +
                    "BigInteger.prototype.getLowestSetBit = bnGetLowestSetBit;\n" +
                    "BigInteger.prototype.bitCount = bnBitCount;\n" +
                    "BigInteger.prototype.testBit = bnTestBit;\n" +
                    "BigInteger.prototype.setBit = bnSetBit;\n" +
                    "BigInteger.prototype.clearBit = bnClearBit;\n" +
                    "BigInteger.prototype.flipBit = bnFlipBit;\n" +
                    "BigInteger.prototype.add = bnAdd;\n" +
                    "BigInteger.prototype.subtract = bnSubtract;\n" +
                    "BigInteger.prototype.multiply = bnMultiply;\n" +
                    "BigInteger.prototype.divide = bnDivide;\n" +
                    "BigInteger.prototype.remainder = bnRemainder;\n" +
                    "BigInteger.prototype.divideAndRemainder = bnDivideAndRemainder;\n" +
                    "BigInteger.prototype.modPow = bnModPow;\n" +
                    "BigInteger.prototype.modInverse = bnModInverse;\n" +
                    "BigInteger.prototype.pow = bnPow;\n" +
                    "BigInteger.prototype.gcd = bnGCD;\n" +
                    "BigInteger.prototype.isProbablePrime = bnIsProbablePrime;\n" +
                    "\n" +
                    "// BigInteger interfaces not implemented in jsbn:\n" +
                    "\n" +
                    "// BigInteger(int signum, byte[] magnitude)\n" +
                    "// double doubleValue()\n" +
                    "// float floatValue()\n" +
                    "// int hashCode()\n" +
                    "// long longValue()\n" +
                    "// static BigInteger valueOf(long val)\n" +
                    "var RSAPublicKey = function($modulus_hex, $encryptionExponent_hex) {\n" +
                    "\tthis.modulus = new BigInteger( $modulus_hex, 16);\n" +
                    "\tthis.encryptionExponent = new BigInteger( $encryptionExponent_hex, 16);\n" +
                    "};\n" +
                    "\n" +
                    "var Base64 = {\n" +
                    "\tbase64: \"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=\",\n" +
                    "\tencode: function($input) {\n" +
                    "\t\tif (!$input) {\n" +
                    "\t\t\treturn false;\n" +
                    "\t\t}\n" +
                    "\t\tvar $output = \"\";\n" +
                    "\t\tvar $chr1, $chr2, $chr3;\n" +
                    "\t\tvar $enc1, $enc2, $enc3, $enc4;\n" +
                    "\t\tvar $i = 0;\n" +
                    "\t\tdo {\n" +
                    "\t\t\t$chr1 = $input.charCodeAt($i++);\n" +
                    "\t\t\t$chr2 = $input.charCodeAt($i++);\n" +
                    "\t\t\t$chr3 = $input.charCodeAt($i++);\n" +
                    "\t\t\t$enc1 = $chr1 >> 2;\n" +
                    "\t\t\t$enc2 = (($chr1 & 3) << 4) | ($chr2 >> 4);\n" +
                    "\t\t\t$enc3 = (($chr2 & 15) << 2) | ($chr3 >> 6);\n" +
                    "\t\t\t$enc4 = $chr3 & 63;\n" +
                    "\t\t\tif (isNaN($chr2)) $enc3 = $enc4 = 64;\n" +
                    "\t\t\telse if (isNaN($chr3)) $enc4 = 64;\n" +
                    "\t\t\t$output += this.base64.charAt($enc1) + this.base64.charAt($enc2) + this.base64.charAt($enc3) + this.base64.charAt($enc4);\n" +
                    "\t\t} while ($i < $input.length);\n" +
                    "\t\treturn $output;\n" +
                    "\t},\n" +
                    "\tdecode: function($input) {\n" +
                    "\t\tif(!$input) return false;\n" +
                    "\t\t$input = $input.replace(/[^A-Za-z0-9\\+\\/\\=]/g, \"\");\n" +
                    "\t\tvar $output = \"\";\n" +
                    "\t\tvar $enc1, $enc2, $enc3, $enc4;\n" +
                    "\t\tvar $i = 0;\n" +
                    "\t\tdo {\n" +
                    "\t\t\t$enc1 = this.base64.indexOf($input.charAt($i++));\n" +
                    "\t\t\t$enc2 = this.base64.indexOf($input.charAt($i++));\n" +
                    "\t\t\t$enc3 = this.base64.indexOf($input.charAt($i++));\n" +
                    "\t\t\t$enc4 = this.base64.indexOf($input.charAt($i++));\n" +
                    "\t\t\t$output += String.fromCharCode(($enc1 << 2) | ($enc2 >> 4));\n" +
                    "\t\t\tif ($enc3 != 64) $output += String.fromCharCode((($enc2 & 15) << 4) | ($enc3 >> 2));\n" +
                    "\t\t\tif ($enc4 != 64) $output += String.fromCharCode((($enc3 & 3) << 6) | $enc4);\n" +
                    "\t\t} while ($i < $input.length);\n" +
                    "\t\treturn $output;\n" +
                    "\t}\n" +
                    "};\n" +
                    "\n" +
                    "var Hex = {\n" +
                    "\thex: \"0123456789abcdef\",\n" +
                    "\tencode: function($input) {\n" +
                    "\t\tif(!$input) return false;\n" +
                    "\t\tvar $output = \"\";\n" +
                    "\t\tvar $k;\n" +
                    "\t\tvar $i = 0;\n" +
                    "\t\tdo {\n" +
                    "\t\t\t$k = $input.charCodeAt($i++);\n" +
                    "\t\t\t$output += this.hex.charAt(($k >> 4) &0xf) + this.hex.charAt($k & 0xf);\n" +
                    "\t\t} while ($i < $input.length);\n" +
                    "\t\treturn $output;\n" +
                    "\t},\n" +
                    "\tdecode: function($input) {\n" +
                    "\t\tif(!$input) return false;\n" +
                    "\t\t$input = $input.replace(/[^0-9abcdef]/g, \"\");\n" +
                    "\t\tvar $output = \"\";\n" +
                    "\t\tvar $i = 0;\n" +
                    "\t\tdo {\n" +
                    "\t\t\t$output += String.fromCharCode(((this.hex.indexOf($input.charAt($i++)) << 4) & 0xf0) | (this.hex.indexOf($input.charAt($i++)) & 0xf));\n" +
                    "\t\t} while ($i < $input.length);\n" +
                    "\t\treturn $output;\n" +
                    "\t}\n" +
                    "};\n" +
                    "\n" +
                    "var RSA = {\n" +
                    "\n" +
                    "\tgetPublicKey: function( $modulus_hex, $exponent_hex ) {\n" +
                    "\t\treturn new RSAPublicKey( $modulus_hex, $exponent_hex );\n" +
                    "\t},\n" +
                    "\n" +
                    "\tencrypt: function($data, $pubkey) {\n" +
                    "\t\tif (!$pubkey) return false;\n" +
                    "\t\t$data = this.pkcs1pad2($data,($pubkey.modulus.bitLength()+7)>>3);\n" +
                    "\t\tif(!$data) return false;\n" +
                    "\t\t$data = $data.modPowInt($pubkey.encryptionExponent, $pubkey.modulus);\n" +
                    "\t\tif(!$data) return false;\n" +
                    "\t\t$data = $data.toString(16);\n" +
                    "\t\tif(($data.length & 1) == 1)\n" +
                    "\t\t\t$data = \"0\" + $data;\n" +
                    "\t\treturn Base64.encode(Hex.decode($data));\n" +
                    "\t},\n" +
                    "\n" +
                    "\tpkcs1pad2: function($data, $keysize) {\n" +
                    "\t\tif($keysize < $data.length + 11)\n" +
                    "\t\t\treturn null;\n" +
                    "\t\tvar $buffer = [];\n" +
                    "\t\tvar $i = $data.length - 1;\n" +
                    "\t\twhile($i >= 0 && $keysize > 0)\n" +
                    "\t\t\t$buffer[--$keysize] = $data.charCodeAt($i--);\n" +
                    "\t\t$buffer[--$keysize] = 0;\n" +
                    "\t\twhile($keysize > 2)\n" +
                    "\t\t\t$buffer[--$keysize] = Math.floor(Math.random()*254) + 1;\n" +
                    "\t\t$buffer[--$keysize] = 2;\n" +
                    "\t\t$buffer[--$keysize] = 0;\n" +
                    "\t\treturn new BigInteger($buffer);\n" +
                    "\t}\n" +
                    "};" +
                    "\tvar pubKey = RSA.getPublicKey(\""+parseKeysParam.get("publickey_mod")+"\",\""+parseKeysParam.get("publickey_exp")+"\");\n" +
                    "\tvar password =\""+SteamPASS+"\"\n" +
                    "\tvar encryptedPassword = RSA.encrypt(password, pubKey);" +
                    "" +
                    "return encryptedPassword;" +
                    "}");

            Invocable inv = (Invocable) Engine;
            EncryptPassword = (String) inv.invokeFunction("getEncryptPasswordJs");
        } catch (final ScriptException se) { se.printStackTrace(); } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        // Генерация пароля
        return EncryptPassword;
    }

    public void authSteam(String nameSite)
    {

        try {
            createDirs(nameSite);

            conHTTPSSteam = new HTTPS();

            //FirstConnection
            conHTTPSSteam.FirstConnectToSite(nameSite);
            //FirstConnection

           //LOGINING


            String Data = Long.toString(System.currentTimeMillis());
            String encryptPass = getEncryptPassSteam();
            String queryLogin = String.format(
                            "emailauth=&" +
                            "loginfriendlyname=&" +
                            "captchagid=-1&" +
                            "captcha_text=&" +
                            "emailsteamid=%s&" +
                            "rsatimestamp=%s&" +
                            "username=%s&" +
                            "password=%s&" +
                            "donotcache=%s&" +
                            "twofactorcode=&" +
                            "remember_login=false&"+
                            "oauth_client_id=&"+
                   "sessionid="+conHTTPSSteam.someValuesToAuth.get("sessionid"),
                    SteamId,
                    parseKeysParam.get("timestamp"),
                    SteamEMAIL,
                    encryptPass,
                    Data);


            // Логирование в Стим

            requestPror.clear();
            requestPror.add(new HeadersSite("User-Agent","Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0"));
            requestPror.add(new HeadersSite("DNT","1"));
            requestPror.add(new HeadersSite("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
            requestPror.add(new HeadersSite("Content-Type", "application/x-www-form-urlencoded; charset = utf-8"));
            requestPror.add(new HeadersSite("Connection", "keep-alive"));
            requestPror.add(new HeadersSite("Host", "steamcommunity.com"));
            requestPror.add(new HeadersSite("Referer", "https://steamcommunity.com/openid/login?openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0&openid.mode=checkid_setup&openid.return_to=http%3A%2F%2Fyhdovh7o.com%2Fauthorization.php%3Flogin&openid.realm=http%3A%2F%2Fyhdovh7o.com&openid.ns.sreg=http%3A%2F%2Fopenid.net%2Fextensions%2Fsreg%2F1.1&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select"));
            requestPror.add(new HeadersSite("Content-Length",""+queryLogin.length()));


            //Получение кода аунтификации

            conHTTPSSteam.connectMethod("https://steamcommunity.com/openid/login", "" +
                    "action=steam_openid_login&" +
                    "openid.mode=checkid_setup&" +
                    "openidparams="+conHTTPSSteam.someValuesToAuth.get("openidparams")+"&"+
                    "nonce"+conHTTPSSteam.someValuesToAuth.get("nonce"),"POST",requestPror);

            //Получение кода аунтификации*/


            conHTTPSSteam.connectMethod("https://steamcommunity.com/login/dologin/",queryLogin,"POST",requestPror);
            // Логирование в Стим
            System.out.println(queryLogin);
            // Проверка капчи

            File MusicFile = new File(conHTTPSSteam.Files.get(conHTTPSSteam.Files.size() - 1));

            HashMap<String,String> ParseNumberCaptcha = new HashMap<>();
            ParseNumberCaptcha = getKeysJson(MusicFile);

              while(ParseNumberCaptcha.get("captcha_needed").equals("true"))
              {
                  String FileContent = "";
                  String tempContent = "";

                  File CookieFile = new File(conHTTPSSteam.Files.get(conHTTPSSteam.Files.size() - 2));

                  BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(CookieFile)));

                  while ((tempContent = in.readLine()) != null) {
                      FileContent += tempContent + "\n";
                  }

                  String captcha = "";

                  if(FileContent.split("<img id=\"captchaImg\" src=\"").length > 1) {
                      System.out.println(FileContent.split("<img id=\"captchaImg\" src=\"")[1].split("\"")[0]);
                      Scanner inputCaptcha = new Scanner(System.in);
                       captcha = inputCaptcha.nextLine();
                  }


                  System.out.println(ParseNumberCaptcha.get("captcha_gid"));

                  encryptPass = getEncryptPassSteam();
                  Data = Long.toString(System.currentTimeMillis());
                   queryLogin = String.format(
                          "emailauth=&" +
                                  "loginfriendlyname=&" +
                                  "captchagid=%s&" +
                                  "captcha_text=%s&" +
                                  "emailsteamid=%s&" +
                                  "rsatimestamp=%s&" +
                                  "username=%s&" +
                                  "password=%s&" +
                                  "donotcache=%s&" +
                                  "twofactorcode=&" +
                                  "remember_login=false&"+
                                  "oauth_client_id=&"+
                                  "sessionid="+conHTTPSSteam.someValuesToAuth.get("sessionid"),
                           ParseNumberCaptcha.get("captcha_gid"),
                           captcha,
                          SteamId,
                          parseKeysParam.get("timestamp"),
                          SteamEMAIL,
                           encryptPass,
                          Data);

                  System.out.println(queryLogin);

                  //Получение кода аунтификации

                  conHTTPSSteam.connectMethod("https://steamcommunity.com/openid/login", "" +
                          "action=steam_openid_login&" +
                          "openid.mode=checkid_setup&" +
                          "openidparams="+conHTTPSSteam.someValuesToAuth.get("openidparams")+"&"+
                          "nonce"+conHTTPSSteam.someValuesToAuth.get("nonce"),"POST",requestPror);

                  //Получение кода аунтификации*/

                  // Логирование в Стим

                  requestPror.clear();
                  requestPror.add(new HeadersSite("User-Agent","Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0"));
                  requestPror.add(new HeadersSite("DNT","1"));
                  requestPror.add(new HeadersSite("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
                  requestPror.add(new HeadersSite("Content-Type", "application/x-www-form-urlencoded; charset = utf-8"));
                  requestPror.add(new HeadersSite("Connection", "keep-alive"));
                  requestPror.add(new HeadersSite("Host", "steamcommunity.com"));
                  requestPror.add(new HeadersSite("Referer", "https://steamcommunity.com/openid/login?openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0&openid.mode=checkid_setup&openid.return_to=http%3A%2F%2Fyhdovh7o.com%2Fauthorization.php%3Flogin&openid.realm=http%3A%2F%2Fyhdovh7o.com&openid.ns.sreg=http%3A%2F%2Fopenid.net%2Fextensions%2Fsreg%2F1.1&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select"));
                  requestPror.add(new HeadersSite("Content-Length",""+queryLogin.length()));

                  conHTTPSSteam.connectMethod("https://steamcommunity.com/login/dologin/",queryLogin,"POST",requestPror);
                  MusicFile = new File(conHTTPSSteam.Files.get(conHTTPSSteam.Files.size() - 1));
                  ParseNumberCaptcha = getKeysJson(MusicFile);
              }

            // Проверка капчи


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Main() {

        deleteDirectory(new File("./User/"));
        String nameSite;

        nameSite = "https://vk.com/";
        domenSite = nameSite.split(":..")[1].split("[.]")[0];

        dirs.clear();
        dirs.put("HtmlDocumentDir", "./User/" + domenSite + "/Content/");
        dirs.put("CookiesDir", "./User/" + domenSite + "/Cookie/");
        dirs.put("MusicDir", "./User/" + domenSite + "/Music/");

        AuthVK(nameSite);



        nameSite = "https://steamcommunity.com/openid/login?openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0&openid.mode=checkid_setup&openid.return_to=http%3A%2F%2Fyhdovh7o.com%2Fauthorization.php%3Flogin&openid.realm=http%3A%2F%2Fyhdovh7o.com&openid.ns.sreg=http%3A%2F%2Fopenid.net%2Fextensions%2Fsreg%2F1.1&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select";
        domenSite = nameSite.split(":..")[1].split("[.]")[0];

        dirs.clear();
        dirs.put("HtmlDocumentDir", "./User/" + domenSite + "/Content/");
        dirs.put("CookiesDir", "./User/" + domenSite + "/Cookie/");

        authSteam(nameSite);
    }

    public static void main(String[] args) {

        new Main();


    }

}
