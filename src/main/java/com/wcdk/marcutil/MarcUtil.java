package com.wcdk.marcutil;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wcdk.marcutil.Bean.Controlfield;
import com.wcdk.marcutil.Bean.Datafield;
import com.wcdk.marcutil.Bean.Record;
import com.wcdk.marcutil.Bibframe.*;
import com.wcdk.marcutil.Bibframe.instance.*;
import com.wcdk.marcutil.Bibframe.work.*;
import lombok.SneakyThrows;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>marc 解析工具类 请勿更改</p>
 *
 * @Description:
 * @Author: WCDK
 **/
public class MarcUtil<T> {
    private String tempPath = "d:/temp";
    private String baseuri = "www.baidu.com";
    private String idfield = "001";
    private String idsource = "www.baidu.com";
    /** marc xml命名空间 必须真实有效**/
    private String ns = "http://www.loc.gov/MARC21/slim";
    private static final String[] FILES = {"conf/abbreviations.xml", "conf/codeMaps.xml", "conf/languageCrosswalk.xml", "conf/map880.xml", "conf/scriptCrosswalk.xml", "conf/subjectThesaurus.xml", "ConvSpec-1XX,7XX,8XX-names.xsl", "ConvSpec-001-007.xsl", "ConvSpec-3XX.xsl", "ConvSpec-5XX.xsl", "ConvSpec-006,008.xsl", "ConvSpec-010-048.xsl", "ConvSpec-050-088.xsl", "ConvSpec-200-247not240-Titles.xsl", "ConvSpec-240andX30-UnifTitle.xsl", "ConvSpec-250-270.xsl", "ConvSpec-490-510-Links.xsl", "ConvSpec-600-662.xsl", "ConvSpec-720+740to755.xsl", "ConvSpec-760-788-Links.xsl", "ConvSpec-841-887.xsl", "ConvSpec-880.xsl", "ConvSpec-ControlSubfields.xsl", "ConvSpec-LDR.xsl", "ConvSpec-Process6-Series.xsl", "marc2bibframe2.xsl", "naco-normalize.xsl", "utils.xsl", "variables.xsl"};
    private static String stylePath_def;
    private static final Logger log = LoggerFactory.getLogger(MarcUtil.class);

    static {
        if (stylePath_def == null || stylePath_def.trim() == "") {
            try {
                moveFiles();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void moveFiles() throws Exception {
        boolean windows = System.getProperty("os.name").toLowerCase().contains("windows");
        if (windows) {
            stylePath_def = "C:\\BibFrame\\";
        } else {
            stylePath_def = "/data/BibFrame/";
        }
        File file = new File(stylePath_def + File.separator + "conf");
        if (!file.exists()) {
            file.mkdirs();
        }
        for (String str : FILES) {
            URL resource = MarcUtil.class.getClassLoader().getResource("style/" + str);
//            ClassPathResource resource = new ClassPathResource("style/" + str);
            InputStream stream = resource.openStream();
            file = new File(stylePath_def + str);
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                int i = 0;
                byte[] b = new byte[1024];
                while ((i = stream.read(b)) != -1) {
                    fileOutputStream.write(b, 0, i);
                }
            }


        }
    }

    private static void copyDir(File srcDir, String newDir) {
        File destDir = new File(newDir);
        if (!destDir.exists()) {
            if (destDir.mkdirs()) {
                File[] files = srcDir.listFiles();
                for (File f : files) {
                    if (f.isFile()) {
                        copyFile(f, new File(newDir, f.getName()));
                    } else if (f.isDirectory()) {
                        copyDir(f, newDir + File.separator + f.getName());
                    }
                }
            }
        }
    }

    private static void copyFile(File oldDir, File newDir) {
        BufferedInputStream bufferedInputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        byte[] b = new byte[1024];
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(oldDir));
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(newDir));
            int len;
            while ((len = bufferedInputStream.read(b)) > -1) {
                bufferedOutputStream.write(b, 0, len);
            }
            bufferedOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * <p>获取头标区</p>
     *
     * @Description:
     * @Author: WCDK
     **/
    public List<String> getContents(String contentsString) throws RuntimeException {
        if (contentsString.length() % 12 != 0) {
            System.out.println("====================marc 目次区格式错误=================");
            throw new RuntimeException("marc格式不正确或者已损坏");
        }
        List<String> contents = new ArrayList<>();
        while (contentsString.length() / 12 >= 1) {
            String s = contentsString.substring(0, 12);
            contentsString = contentsString.substring(12);
            contents.add(s);
        }

        return contents;
    }

    public Record buildMarcDataSingle(String macData) {
        macData = macData.trim();
        String heading = macData.substring(0, 24);
        macData = macData.substring(24);
        int index = macData.indexOf("\u001E");
        String contentsString = macData.substring(0, index);
        macData = macData.substring(index);

        List<String> contents = getContents(contentsString);
        byte[] marcStr = marcCharSet(macData,contents);
        String charSet = getmarcCharSet(macData,contents);


        Record record = new Record();
        contents.forEach(conten -> {
            String key = conten.substring(0, 3);
            int leng = Integer.parseInt(conten.substring(3, 7));
            int start = Integer.parseInt(conten.substring(7));
            String data = getbyte(marcStr, start, start + leng,charSet);
            if (key.startsWith("00")) {
                Controlfield controlfield = new Controlfield();
                controlfield.setTag(key);
                controlfield.setValue(data.substring(1));
                controlfield.setValueSource(data);
                record.getControlfields().add(controlfield);
            } else {
                Datafield datafield = cleanDatafiled(data);
                datafield.setTag(key);
                record.getDatafields().add(datafield);
            }
        });
        record.setCharSet(charSet);
        record.setLeader(heading);
        return record;
    }

    public List<Record> buildMarcData(String macData) throws Exception {
        //
        macData = macData.trim();
        String[] split = macData.split("\u001D");
        List<Record> records = new ArrayList<>();
        for (String str : split) {
            str.length();
            str += "\u001D";
            Record record = new Record();
            String heading = str.substring(0, 24);
            str = str.substring(24);
            int index = str.indexOf("\u001E");
            String contentsString = str.substring(0, index);
            str = str.substring(index);
            List<String> contents = getContents(contentsString);

            byte[] marcStr = marcCharSet(str, contents);
            String charSet = getmarcCharSet(str, contents);

            contents.forEach(conten -> {
                String key = conten.substring(0, 3);
                int leng = Integer.parseInt(conten.substring(3, 7));
                int start = Integer.parseInt(conten.substring(7));
                String data = getbyte(marcStr, start, start + leng,charSet);
                if (key.startsWith("00")) {
                    Controlfield controlfield = new Controlfield();
                    controlfield.setTag(key);
                    controlfield.setValueSource(data);
                    controlfield.setValue(data.substring(1));
                    record.getControlfields().add(controlfield);
                } else {
                    Datafield datafield = cleanDatafiled(data);
                    datafield.setTag(key);
                    record.getDatafields().add(datafield);
                }
            });
            record.setCharSet(charSet);
            record.setLeader(heading);
            records.add(record);
        }
        return records;
    }


    private byte[] marcCharSet(String str,List<String> contents){
        try {
            byte[] marcStr = str.getBytes("utf8");
            int flg = str.endsWith("\u001D")?1:0;
            String st = contents.get(contents.size() - 1);
            int l = Integer.parseInt(st.substring(3, 7));
            int s = Integer.parseInt(st.substring(7));
            int lc = l+s;
            int f = marcStr.length-flg;
            if(lc != f){
                marcStr = str.getBytes("gbk");
            }
            return marcStr;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
    private String getmarcCharSet(String str,List<String> contents){
        try {
            byte[] marcStr = str.getBytes("utf8");
            int flg = str.endsWith("\u001D")?1:0;
            String st = contents.get(contents.size() - 1);
            int l = Integer.parseInt(st.substring(3, 7));
            int s = Integer.parseInt(st.substring(7));
            int lc = l+s;
            int f = marcStr.length-flg;
            if(lc == f){
                return "utf8";
            }
            return "gbk";
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


    /**
     * <p>獲取指定的第一個值的 子字段</p>
     *
     * @Description:
     * @Author: WCDK
     **/

    public String getFiled(List<RepeatKeyMap<String, Object>> getdata, String subFiled) {
        if (getdata != null && getdata.size() > 0) {
            RepeatKeyMap<String, Object> stringObjectRepeatKeyMap = getdata.get(0);
            RepeatKeyMap<String, String> child = (RepeatKeyMap<String, String>) stringObjectRepeatKeyMap.get("child");
            String filed = child.get(subFiled);
            if (filed != null) {
                return filed;
            }
        }
        return null;
    }

    /**
     * <p>拼接所有字字段</p>
     *
     * @Description:
     * @Author: WCDK
     **/
    public String getFiledAll(List<RepeatKeyMap<String, Object>> getdata, String... subFiled) {
        StringBuilder stringBuilder = new StringBuilder();
        if (getdata != null && getdata.size() > 0) {
            RepeatKeyMap<String, Object> stringObjectRepeatKeyMap = getdata.get(0);
            RepeatKeyMap<String, String> child = (RepeatKeyMap<String, String>) stringObjectRepeatKeyMap.get("child");
            if (subFiled == null || subFiled.length < 1) {
                child.forEach((k, v) -> {
                    stringBuilder.append(v.trim() + ",");
                });
                return stringBuilder.substring(0, stringBuilder.length() - 1).toString();
            } else {
                for (String key : subFiled) {
                    String filed = child.get(key.trim());
                    if (filed != null && filed.trim() != "") {
                        stringBuilder.append(filed.trim());
                    }
                }
                return stringBuilder.toString();
            }


        }
        return null;
    }


    /**
     * <p>权威记录格式</p>
     *
     * @Description:
     * @Author: WCDK
     **/

    public RepeatKeyMap<String, String> getSubFiled2Authority(List<RepeatKeyMap<String, Object>> getdata) {
        StringBuilder heading = new StringBuilder();
        RepeatKeyMap<String, String> result = new RepeatKeyMap<>();
        if (getdata != null && getdata.size() > 0) {
            RepeatKeyMap<String, Object> stringObjectRepeatKeyMap = getdata.get(0);
            RepeatKeyMap<String, String> child = (RepeatKeyMap<String, String>) stringObjectRepeatKeyMap.get("child");
            if (child == null) {
                result.put("rowData", (String) stringObjectRepeatKeyMap.get("value"));
                result.put("heading", (String) stringObjectRepeatKeyMap.get("value"));
                return result;
            }
            result.put("rowData", (String) stringObjectRepeatKeyMap.get("value"));
            child.forEach((k, v) -> {
                if (v != null && !v.equals("")) {
                    heading.append(v.trim() + ",");
                }
            });
            result.put("heading", heading.substring(0, heading.length() - 1).toString());

        }
        return result;
    }


    /**
     * <p>获取指定字段</p>
     *
     * @Description:
     * @Author: WCDK
     * @Date:
     **/
    public List<RepeatKeyMap<String, Object>> getdata(Record record, String key1) {
        List<RepeatKeyMap<String, Object>> array = new ArrayList<>();
        List<Controlfield> controlfields = record.getControlfields();
        List<Datafield> datafields = record.getDatafields();
        controlfields.forEach(cf -> {
            if (cf.getTag().equals(key1)) {
                RepeatKeyMap<String, Object> map = new RepeatKeyMap<>();
                map.put("key", cf.getTag());
                map.put("value", cf.getValue());
                map.put("valueSource", cf.getValueSource());
                array.add(map);
            }
        });

        datafields.forEach(df -> {
            if (df.getTag().equals(key1)) {
                RepeatKeyMap<String, Object> map = new RepeatKeyMap<>();
                map.put("key", df.getTag());
                map.put("ind1", df.getIndex1());
                map.put("ind2", df.getIndex2());
                map.put("value", df.getValue());
                map.put("child", df.getSubfields());
                array.add(map);
            }
        });


        return array;
    }

    /**
     * <p>获取指定字段</p>
     *
     * @Description:
     * @Author: WCDK
     * @Date:
     **/
    public <T> List<T> getdata(Record record, String key1, Class<T> t) {
        List array = new ArrayList();
        if (t.equals(Controlfield.class)) {
            array = new ArrayList<Controlfield>();
        } else if (t.equals(Datafield.class)) {
            array = new ArrayList<Datafield>();
        }
        List<Controlfield> controlfields = record.getControlfields();
        List<Datafield> datafields = record.getDatafields();
        List finalArray = array;
        controlfields.forEach(cf -> {
            if (cf.getTag().equals(key1)) {

                finalArray.add(cf);
            }
        });

        datafields.forEach(df -> {
            if (df.getTag().equals(key1)) {
                finalArray.add(df);
            }
        });

        return array;
    }

    public List<Datafield> getDataField(Record record, String... key1) {
        List<Datafield> array = new ArrayList<>();
        List<Datafield> datafields = record.getDatafields();
        List<String> list = Arrays.asList(key1);
        datafields.forEach(df -> {
            if (list.contains(df.getTag())) {
                array.add(df);
            }
        });
        return array;
    }

    public List<String> getFieldOriginalSource(Record record, String key1) {
        List<String> array = new ArrayList<>();
        List<Controlfield> controlfields = record.getControlfields();
        List<Datafield> datafields = record.getDatafields();
        controlfields.forEach(cf -> {
            if (cf.getTag().equals(key1)) {
                array.add(cf.getValue());
            }
        });

        datafields.forEach(df -> {
            if (df.getTag().equals(key1)) {
                RepeatKeyMap<String, Object> map = new RepeatKeyMap<>();
                map.put("key", df.getTag());
                map.put("ind1", df.getIndex1());
                map.put("ind2", df.getIndex2());
                map.put("value", df.getValue());
                map.put("child", df.getSubfields());
                array.add(df.getValue());
            }
        });


        return array;
    }


    private String getbyte(byte[] bytes, int index,String charSet) {
        byte[] b = new byte[1];
        b[0] = bytes[index];
        try {
            return new String(b,charSet);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getbyte(byte[] bytes, int start, int end,String charSet){
        byte[] b = new byte[end-start];
        int length = bytes.length;
        int index = 0;
        for(int i = 0;i < length;i++){
            if(i >= start && i< end){
                b[index] = bytes[i];
                index++;
            }
        }
        try {
            return new String(b,charSet);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * <p>数据清理</p>
     *
     * @Description:
     * @Author: WCDK
     * @Date:
     **/
    public Datafield cleanDatafiled(String data) {
        Datafield datafield = new Datafield();
        if (data == null || data.trim().equals("")) {
            return datafield;
        }
        datafield.setValue(data);
        log.info("字符串处理【data:"+data+",1:"+data.indexOf("\u001E")+1+",2:"+data.indexOf("\u001F"));String substring = data.substring(data.indexOf("\u001E") +1, data.indexOf("\u001F"));
        if (!"".equals(substring.trim())) {
            String substring1 = substring.substring(0, 1);
            if (!"".equals(substring1.trim())) {
                datafield.setIndex1(substring1.trim());
            }
            substring1 = substring.substring(1);
            if (!"".equals(substring1.trim())) {
                datafield.setIndex2(substring1.trim());
            }
        }
        data = data.substring(data.indexOf("\u001F"));
        boolean b = data.startsWith("\u001F");
        String[] split = data.split("\u001F");
        RepeatKeyMap subfield;
        if (split.length == 1) {
            subfield = new RepeatKeyMap();
            String code = "";
            if (b) {
                code = split[0].substring(0, 1);
                subfield.put(code, split[0].substring(1).replace("\u001D", ""));
            } else {
                subfield.put("", split[0]);
            }
            datafield.getSubfields().putAll(subfield);
        } else {
            for (int i = 0; i < split.length; i++) {
                subfield = new RepeatKeyMap();
                String code = "";
                String s = split[i];
                if (s.trim().length() == 0) {

                    continue;
                }
                try {
                    code = s.substring(0, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    subfield.put(code, s.substring(1).replaceAll("\u001D", "").replaceAll("\u0000", ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                datafield.getSubfields().putAll(subfield);
            }
        }

        return datafield;
    }

    /**
     * <p>生成xml 请勿修改节点名称</p>
     *
     * @Description:
     * @Author: WCDK
     * @Date:
     **/

    public String createXML(Record record) throws Exception {

        // \u001E \u001F  \u001D 

        Document document = DocumentHelper.createDocument();
        Element recordNode = document.addElement("record", ns);

        Element leader = recordNode.addElement("leader", ns);
        leader.setText(record.getLeader());


        List<Controlfield> controlfields = record.getControlfields();

        controlfields.forEach(cf -> {
            recordNode.addElement("controlfield").addAttribute("tag", cf.getTag()).setText(cf.getValue());
        });
        List<Datafield> datafields = record.getDatafields();
        datafields.forEach(df -> {
            Element datafieldNode = recordNode.addElement("datafield");
            datafieldNode.addAttribute("tag", df.getTag()).addAttribute("ind1", df.getIndex1()).addAttribute("ind2", df.getIndex2());
            RepeatKeyMap<String, String> subfields = df.getSubfields();

            subfields.forEach((k, v) -> {
                datafieldNode.addElement("subfield").addAttribute("code", k).setText(v);
            });
        });
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        StringWriter stringWriter = new StringWriter(1024);
        XMLWriter xmlWriter = new XMLWriter(stringWriter, format);
        xmlWriter.write(document);
        xmlWriter.setEscapeText(false);
        xmlWriter.flush();
        xmlWriter.close();
        return stringWriter.toString();

    }

    public String toBibmrame(Record record) throws Exception {
        System.out.println("========================================bibframe========================================");
        // \u001E \u001F  \u001D 
        String ns = "http://www.loc.gov/MARC21/slim";

        Document document = DocumentHelper.createDocument();
        Element recordNode = document.addElement("record", ns);

        Element leader = recordNode.addElement("leader", ns);
        leader.setText(record.getLeader());


        List<Controlfield> controlfields = record.getControlfields();

        controlfields.forEach(cf -> {
            recordNode.addElement("controlfield").addAttribute("tag", cf.getTag()).setText(cf.getValue().replaceAll("\\pP|\\pS", "").replace("&", "&amp;"));
        });
        List<Datafield> datafields = record.getDatafields();
        datafields.forEach(df -> {
            Element datafieldNode = recordNode.addElement("datafield");
            datafieldNode.addAttribute("tag", df.getTag()).addAttribute("ind1", df.getIndex1()).addAttribute("ind2", df.getIndex2());
//                    .setText(df.getValue());
            RepeatKeyMap<String, String> subfields = df.getSubfields();

            subfields.forEach((k, v) -> {
                datafieldNode.addElement("subfield").addAttribute("code", k).setText(v.replaceAll("\\pP|\\pS", "").replace("&", "&amp;"));
            });
        });
        File file = new File(tempPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        String ps = System.getProperty("file.separator");

        file = new File(tempPath + ps + Long.parseLong(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MddHHmmssSSS"))) + ".xml");
        FileWriter fileWriter = new FileWriter(file);
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        XMLWriter xmlWriter = new XMLWriter(fileWriter, format);
        xmlWriter.setEscapeText(false);
        xmlWriter.write(document);
        xmlWriter.flush();
        xmlWriter.close();
        String s = toBibmrame(file.getPath());
        file.deleteOnExit();
        return s;
    }

    private String toBibmrame(String macXmlPath) throws Exception {
        String ps = System.getProperty("file.separator");
        String commod = "xsltproc ";
        if (baseuri != null) {
            commod += " --stringparam baseuri " + baseuri;
        }
        if (idfield != null) {
            commod += " --stringparam idfield " + idfield;
        } else {
            commod += " --stringparam idfield 001";
        }
        if (idsource != null) {
            commod += " --stringparam idsource " + idsource;
        }
        String stylePath = stylePath_def + ps + "marc2bibframe2.xsl";
        log.info(stylePath);
        commod += " " + stylePath + " " + macXmlPath;
        boolean windows = System.getProperty("os.name").toLowerCase().contains("windows");
        String[] cmds = null;
        if (windows) {
            cmds = new String[]{"cmd", "/c chcp 65001"};
        } else {
            cmds = new String[]{"/bin/sh", "-c"};
        }
        log.info(commod);
        Process exec = Runtime.getRuntime().exec(commod, cmds);

        Thread t1 = new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                InputStream errorStream = exec.getErrorStream();
                int i = 0;
                while ((i = errorStream.read()) > -1) {

                }
                errorStream.close();
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                OutputStream outputStream = exec.getOutputStream();
                outputStream.flush();
                outputStream.close();
            }
        });
        t1.start();
        t2.start();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream(), "utf-8"));
        String line;
        StringBuilder b = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            b.append(line).append("\n");
        }
        bufferedReader.close();
        exec.waitFor();
        log.info(b.toString());


        return b.toString();
    }

    /**
     * <p>添加属性</p>
     *
     * @Description:
     * @Author: WCDK
     **/
    public void addfiled(Record record, Object... obj) {
        if (obj != null && obj.length > 0) {
            for (Object ob : obj) {
                if (ob instanceof Controlfield) {
                    record.getControlfields().add((Controlfield) ob);
                } else if (ob instanceof Datafield) {
                    record.getDatafields().add((Datafield) ob);
                } else {
                    throw new RuntimeException("obj class is wrong");
                }
            }
        }
    }

    /**
     * <p>marc字段替换  只替换不会补充缺省内容</p>
     *
     * @Description:
     * @Author: WCDK
     **/

    public void replace(Record record, Object... filed) {
        if (filed != null && filed.length > 0) {
            for (Object ob : filed) {
                if (ob instanceof Controlfield) {
                    Controlfield c = (Controlfield) ob;
                    record.getControlfields().stream().filter(e -> {
                        if (c.getTag().equals(e.getTag())) {
                            e.setValue(c.getValue());
                        }
                        return true;
                    }).collect(Collectors.toList());
                } else if (ob instanceof Datafield) {
                    record.getDatafields().forEach(e -> {
                        if (((Datafield) ob).getTag().equals(e.getTag())) {
                            Datafield d = (Datafield) ob;
                            e.setIndex1(d.getIndex1());
                            e.setIndex2(d.getIndex2());
                            e.setSubfields(d.getSubfields());
                        }
                    });
                } else {
                    throw new RuntimeException("obj class is wrong");
                }

            }
        }
    }

    /**
     * <p>替换指定字段</p>
     *
     * @param record Record对象
     * @param filed  字段对象
     * @param index  目标下标
     *               index 为替换第几个属性 默认1
     * @Description:
     * @Author: WCDK
     **/

    public void replace(Record record, Object filed, int index) {
        if (filed != null) {
            index = index <= 0 ? 1 : index;
            if (filed instanceof Controlfield) {
                Controlfield c = (Controlfield) filed;
                int finalIndex = index;
                AtomicInteger atomicInteger = new AtomicInteger(1);
                record.getControlfields().forEach(e -> {
                    if (c.getTag().equals(e.getTag()) && finalIndex == atomicInteger.getAndAdd(1)) {
                        e.setValue(c.getValue());
                    }
                });

            } else if (filed instanceof Datafield) {
                int finalIndex = index;
                Datafield c = (Datafield) filed;
//                List<Datafield> datafields = record.getDatafields();
                AtomicInteger atomicInteger = new AtomicInteger(1);
                record.getDatafields().forEach(e -> {
                    if (c.getTag().equals(e.getTag()) && finalIndex == atomicInteger.getAndAdd(1)) {
                        e.setIndex1(c.getIndex1());
                        e.setIndex2(c.getIndex2());
                        e.setSubfields(c.getSubfields());
                    }
                });
            } else {
                throw new RuntimeException("obj class is wrong");
            }
        }
    }


    /**
     * <p>将marc record对象转换为 marcString</p>
     *
     * @Description:
     * @Author: WCDK
     **/
    public String toMarcString(Record record) {
        StringBuffer value = new StringBuffer();
        StringBuffer conent = new StringBuffer();
        // "\u001E" 字段开始标识
        // 中间 索引标识
        //"\u001F" 子字段标识 只一位
        // "\u001D" 单条marc结束标识
        //conent.append(record.getLeader());
        String leader = record.getLeader();
        // 没有子字段的属性字段
        List<Controlfield> controlfields = record.getControlfields();
        controlfields = controlfields.stream().sorted((v1, v2) -> v1.getTag().compareTo(v2.getTag())).collect(Collectors.toList());
        AtomicInteger index = new AtomicInteger();
        if (controlfields != null && controlfields.size() > 0) {
            controlfields.forEach(e -> {
                String tag = e.getTag();
                conent.append(tag);
                String v = "\u001E" + e.getValue();
                byte[] bytes = v.getBytes(StandardCharsets.UTF_8);
                int length = bytes.length;
                conent.append(completionZero_prefix(String.valueOf(length), 4));
                conent.append(completionZero_prefix(String.valueOf(index.get()), 5));
                index.set(index.get() + length);
                value.append(v);
            });
        }

        List<Datafield> datafields = record.getDatafields();
        datafields = datafields.stream().sorted((v1, v2) -> v1.getTag().compareTo(v2.getTag())).collect(Collectors.toList());
        if (datafields != null && datafields.size() > 0) {
            datafields.forEach(e -> {
                String index1 = e.getIndex1();
                String index2 = e.getIndex2();
                String tag = e.getTag();
                conent.append(tag);

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("\u001E" + index1 + index2);
                RepeatKeyMap<String, String> subfields = e.getSubfields();
                subfields.forEach((k, v) -> {
                    stringBuilder.append("\u001F" + k);
                    byte[] bytes = v.getBytes(StandardCharsets.UTF_8);
                    if(bytes != null && bytes.length > 0){
                        boolean b = bytes[bytes.length - 1] == 0;
                        if (b) {
                            byte[] newb = new byte[bytes.length - 1];
                            for (int i = 0; i < newb.length; i++) {
                                newb[i] = bytes[i];
                            }
                            v = new String(newb);
                        }
                    }
                    stringBuilder.append(v);
                });
//                stringBuilder.append(e.getValue());
                String datafield = stringBuilder.toString();
                byte[] bytes = datafield.getBytes(StandardCharsets.UTF_8);
                int length = bytes.length;
                conent.append(completionZero_prefix(String.valueOf(length), 4));

                conent.append(completionZero_prefix(String.valueOf(index.get()), 5));
                index.set(index.get() + length);
                value.append(datafield);
            });
        }

        String dleanth = conent.substring(conent.length() - 5);
        String substring = completionZero_prefix(String.valueOf(conent.length() + value.toString().getBytes(StandardCharsets.UTF_8).length), 5);
        leader = substring + leader.substring(5);
        leader = leader.substring(0, 12) + dleanth + leader.substring(17);
        return leader + conent.append(value + "\u001D").toString();
    }

    private String completionZero_prefix(String value, int length) {
        while (value.length() < length) {
            value = "0" + value;
        }
        return value;
    }

    private String completionZero_suffix(String value, int length) {
        while (value.length() < length) {
            value += "0";
        }
        return value;
    }

    public <T> List<T> analysis007(Record record, Class<T> clazz) {
        List<RepeatKeyMap<String, Object>> getdata = getdata(record, "007");
        JSONArray array007 = new JSONArray();
        if (getdata != null && getdata.size() > 0) {
            JSONObject json007 = new JSONObject();
            for (RepeatKeyMap<String, Object> map : getdata) {
                byte[] values = map.get("value").toString().getBytes(StandardCharsets.UTF_8);
                if (values.length < 39) {
                    return null;
                }
                String material_type = getbyte(values, 0,record.getCharSet());
                json007.put("materialType", material_type);
                String material_designation = getbyte(values, 1,record.getCharSet());
                json007.put("materialDesignation", material_designation);
                array007.add(json007);
            }
            String s = JSONArray.toJSONString(array007);
            return JSONArray.parseArray(s, clazz);
        }
        return null;
    }

    public <T> List<T> analysis008(Record record, Class<T> clazz) {
        List<RepeatKeyMap<String, Object>> getdata = getdata(record, "008");
        JSONArray array008 = new JSONArray();
        if (getdata != null && getdata.size() > 0) {
            JSONObject json008 = new JSONObject();
            for (RepeatKeyMap<String, Object> map : getdata) {
                byte[] values = map.get("value").toString().getBytes(StandardCharsets.UTF_8);
                if (values.length < 40) {
                    return null;
                }
                String regDate = getbyte(values, 0, 5,record.getCharSet());
                String pubdateType = getbyte(values, 6,record.getCharSet());
                String date1 = getbyte(values, 07, 10,record.getCharSet());
                String date2 = getbyte(values, 11, 14,record.getCharSet());
                String pubPlace = getbyte(values, 15, 17,record.getCharSet());
                String illustrations = getbyte(values, 18, 21,record.getCharSet());
                String targetAudience = getbyte(values, 22,record.getCharSet());
                String formOfItem = getbyte(values, 23,record.getCharSet());
                String natureOfContents = getbyte(values, 24, 27,record.getCharSet());
                String language = getbyte(values, 35, 37,record.getCharSet());
                String modifiedRecord = getbyte(values, 38,record.getCharSet());
                String catalogingSource = getbyte(values, 39,record.getCharSet());
                json008.put("regDate", regDate);
                json008.put("pubdateType", pubdateType);
                json008.put("date1", date1);
                json008.put("date2", date2);
                json008.put("pubPlace", pubPlace);
                json008.put("illustrations", illustrations);
                json008.put("targetAudience", targetAudience);
                json008.put("formOfItem", formOfItem);
                json008.put("natureOfContents", natureOfContents);
                json008.put("language", language);
                json008.put("modifiedRecord", modifiedRecord);
                json008.put("catalogingSource", catalogingSource);

                array008.add(json008);
            }
            String s = JSONArray.toJSONString(array008);
            return JSONArray.parseArray(s, clazz);
        }
        return null;
    }

    public <T> T analysisLeader(Record record, Class<T> clazz) {

        String leader = record.getLeader();
        byte[] values = leader.getBytes(StandardCharsets.UTF_8);
        JSONObject leaderJson = new JSONObject();

        String bibLen = getbyte(values, 0, 4,record.getCharSet());
        String bibStatus = getbyte(values, 5,record.getCharSet());
        String bibType = getbyte(values, 06,record.getCharSet());
        String bibLevel = getbyte(values, 7,record.getCharSet());
        String bibTypeControl = getbyte(values, 8,record.getCharSet());
        String bibCharcode = getbyte(values, 9,record.getCharSet());
        String bibIndicatorCount = getbyte(values, 10,record.getCharSet());
        String bibSubfiedcodeCount = getbyte(values, 11,record.getCharSet());
        String bibDataAddress = getbyte(values, 12, 16,record.getCharSet());
        String bibEncodingLevel = getbyte(values, 17,record.getCharSet());
        String bib18 = getbyte(values, 18,record.getCharSet());
        String bib19 = getbyte(values, 19,record.getCharSet());
        String bib20 = getbyte(values, 20,record.getCharSet());
        String bib21 = getbyte(values, 21,record.getCharSet());
        String bib22 = getbyte(values, 22,record.getCharSet());
        String bib23 = getbyte(values, 23,record.getCharSet());
        leaderJson.put("bibLen", bibLen);
        leaderJson.put("bibStatus", bibStatus);
        leaderJson.put("bibType", bibType);
        leaderJson.put("bibLevel", bibLevel);
        leaderJson.put("bibTypeControl", bibTypeControl);
        leaderJson.put("bibCharcode", bibCharcode);
        leaderJson.put("bibIndicatorCount", bibIndicatorCount);
        leaderJson.put("bibSubfiedcodeCount", bibSubfiedcodeCount);
        leaderJson.put("bibDataAddress", bibDataAddress);
        leaderJson.put("bibEncodingLevel", bibEncodingLevel);
        leaderJson.put("bib18", bib18);
        leaderJson.put("bib19", bib19);
        leaderJson.put("bib20", bib20);
        leaderJson.put("bib21", bib21);
        leaderJson.put("bib22", bib22);
        leaderJson.put("bib23", bib23);
        return JSONObject.parseObject(leaderJson.toJSONString(), clazz);
    }


    /**
     * <p>构建039字段</p>
     *
     * @Description:
     * @Author: WCDK
     * @return createby createdate
     **/
    public Map<String,String> build039(Record record, String username) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYYMMddHHmm");
        String yyyymMdd = simpleDateFormat.format(new Date());
        List<RepeatKeyMap<String, Object>> getdata = getdata(record, "039");
        String createby = username;
        String createDate = yyyymMdd;
        String updateBy = username;
        String updateDate = yyyymMdd;

        if (getdata != null && getdata.size() > 0) {
            Datafield datafield = new Datafield();
            datafield.setTag("039");
            RepeatKeyMap<String, Object> stringObjectMap = getdata.get(0);
            datafield.setIndex1(stringObjectMap.get("ind1").toString());
            datafield.setIndex2(stringObjectMap.get("ind2").toString());
            RepeatKeyMap<String, String> child = (RepeatKeyMap<String, String>) stringObjectMap.get("child");
            RepeatKeyMap<String, String> mewchild = new RepeatKeyMap<>();
            if (child != null) {
                int size = (child.size() - 2) / 2;
                mewchild.put("a", yyyymMdd);
                mewchild.put("b", username);
                if (size == 1) {
                    mewchild.put("c", child.get("a"));
                    mewchild.put("d", child.get("b"));
                } else if (size == 2) {

                    mewchild.put("c", child.get("a"));
                    mewchild.put("d", child.get("b"));

                    if (child.get("c") != null) {
                        mewchild.put("c", child.get("c"));
                        mewchild.put("d", child.get("d"));
                    }
                } else if (size == 3) {
                    mewchild.put("c", child.get("a"));
                    mewchild.put("d", child.get("b"));

                    mewchild.put("c", child.get("c", 1));
                    mewchild.put("d", child.get("d", 1));

                    mewchild.put("c", child.get("c", 2));
                    mewchild.put("d", child.get("d", 2));

                } else {
                    if (size != 0) {
                        mewchild.put("c", child.get("a"));
                        mewchild.put("d", child.get("b"));

                        mewchild.put("c", child.get("c", 1));
                        mewchild.put("d", child.get("d", 1));

                        mewchild.put("c", child.get("c", 2));
                        mewchild.put("d", child.get("d", 2));

                        mewchild.put("c", child.get("c", 3));
                        mewchild.put("d", child.get("d", 3));
                    }

                }
                createDate = child.get("y") == null ? yyyymMdd : child.get("y");
                createby = child.get("z") == null ? username : child.get("z");
                mewchild.put("y",createDate );
                mewchild.put("z",createby);

                datafield.setSubfields(mewchild);
            }
            replace(record, datafield);
        } else {
            Datafield datafield = new Datafield();
            RepeatKeyMap<String, String> child = new RepeatKeyMap<>();
            child.put("y", yyyymMdd);
            child.put("z", username);
            datafield.setSubfields(child);
            datafield.setTag("039");
            addfiled(record, datafield);
        }
        Map<String,String> map = new HashMap<>();
        map.put("createby",createby);
        map.put("createtime",createDate);
        map.put("updateby",updateBy);
        map.put("updatetime",updateDate);

        return map;
    }

    /**
     * <p>刷新008字段</p>
     *
     * @Description:
     * @Author: WCDK
     * @return createby createdate
     **/
    public void flush008(Record record){
        List<Controlfield> getdata = getdata(record, "008", Controlfield.class);
        if(getdata != null && getdata.size() > 0){
            Controlfield controlfield = getdata.get(0);
            String substring = controlfield.getValue().substring(0, 6);
            String valueSource = controlfield.getValueSource();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYMMDD");
            String yymmdd = simpleDateFormat.format(new Date());
            controlfield.setValue(yymmdd+controlfield.getValue().substring(6));
            valueSource.replace(substring,yymmdd);
            controlfield.setValueSource(valueSource);
            replace(record,controlfield);
        }
    }

    /**
     * <p>构建039字段</p>
     *
     * @Description: dateString YYYYMMddHHmm
     * @Author: WCDK
     **/
    public void build039(Record record, String username, String dateString) {
        String yyyymMdd = dateString;
        List<RepeatKeyMap<String, Object>> getdata = getdata(record, "039");
        if (getdata != null && getdata.size() > 0) {
            Datafield datafield = new Datafield();
            datafield.setTag("039");
            RepeatKeyMap<String, Object> stringObjectMap = getdata.get(0);

            datafield.setIndex1(stringObjectMap.get("ind1").toString());
            datafield.setIndex2(stringObjectMap.get("ind2").toString());
            RepeatKeyMap<String, String> child = (RepeatKeyMap<String, String>) stringObjectMap.get("child");
            RepeatKeyMap<String, String> mewchild = new RepeatKeyMap<>();
            if (child != null) {
                int size = (child.size() - 2) / 2;
                mewchild.put("a", yyyymMdd);
                mewchild.put("b", username);
                if (size == 1) {
                    mewchild.put("c", child.get("a"));
                    mewchild.put("d", child.get("b"));
                } else if (size == 2) {

                    mewchild.put("c", child.get("a"));
                    mewchild.put("d", child.get("b"));

                    if (child.get("c") != null) {
                        mewchild.put("c", child.get("c"));
                        mewchild.put("d", child.get("d"));
                    }
                } else if (size == 3) {
                    mewchild.put("c", child.get("a"));
                    mewchild.put("d", child.get("b"));

                    mewchild.put("c", child.get("c", 1));
                    mewchild.put("d", child.get("d", 1));

                    mewchild.put("c", child.get("c", 2));
                    mewchild.put("d", child.get("d", 2));

                } else {
                    if (size != 0) {
                        mewchild.put("c", child.get("a"));
                        mewchild.put("d", child.get("b"));

                        mewchild.put("c", child.get("c", 1));
                        mewchild.put("d", child.get("d", 1));

                        mewchild.put("c", child.get("c", 2));
                        mewchild.put("d", child.get("d", 2));

                        mewchild.put("c", child.get("c", 3));
                        mewchild.put("d", child.get("d", 3));
                    }

                }
                mewchild.put("y", child.get("y") == null ? yyyymMdd : child.get("y"));
                mewchild.put("z", child.get("z") == null ? username : child.get("z"));
                datafield.setSubfields(mewchild);
            }
            replace(record, datafield);
        } else {
            Datafield datafield = new Datafield();
            RepeatKeyMap<String, String> child = new RepeatKeyMap<>();
            child.put("y", yyyymMdd);
            child.put("z", username);
            datafield.setSubfields(child);
            datafield.setTag("039");
            addfiled(record, datafield);
        }
    }

    public void deleteSubfiled(Record record, String subFiled, String... moresubFiled) {
        List<String> list = Arrays.asList(moresubFiled);
        List<Controlfield> controlfields = record.getControlfields();
        List<Datafield> datafields = record.getDatafields();
        for (int i = 0; i < controlfields.size(); i++) {
            Controlfield controlfield = controlfields.get(i);
            String tag = controlfield.getTag();
            if (tag.equals(subFiled) || list.contains(tag)) {
                controlfields.remove(i);
                i--;
            }
        }
        for (int i = 0; i < datafields.size(); i++) {
            Datafield datafield = datafields.get(i);
            String tag = datafield.getTag();
            if (tag.equals(subFiled) || list.contains(tag)) {
                datafields.remove(i);
                i--;
            }
        }
    }


    public Bibframe parseBibframeXML(String bibframeXml) {
        Bibframe bibframe = new Bibframe();
        try {
            Document document = DocumentHelper.parseText(bibframeXml);
            Element element = document.getRootElement();
            List<Namespace> namespaces = element.declaredNamespaces();
            for (Namespace namespace : namespaces) {
                bibframe.appendNameSpace(namespace.getPrefix(), namespace.getURI());
            }
            Node workNode = element.selectSingleNode("bf:Work");
            Node instanceNode = element.selectSingleNode("bf:Instance");
            List<BibframeItem> workItems = new ArrayList<>();
            List<BibframeItem> instanceItems = new ArrayList<>();
            if(workNode != null){
                Element nodeDocument = (Element)workNode;
                List<Node> content = nodeDocument.content();
                content = content.stream().filter(e->!(e instanceof Namespace)).filter(e->!(e instanceof DefaultText)).collect(Collectors.toList());
                for (Node node :content){
                    buildBibFrameObj((Element)node,workItems);
                }
                Work work = new Work();
                work.setAbout(nodeDocument.attribute(0).getStringValue());
                work.setNodes(workItems);
                bibframe.setWork(work);
            }
            if(instanceNode != null){
                Element nodeDocument = (Element) instanceNode;
                List<Node> content = nodeDocument.content();
                content = content.stream().filter(e->!(e instanceof Namespace)).filter(e->!(e instanceof DefaultText)).collect(Collectors.toList());
                for (Node node :content){
                    buildBibFrameObj((Element)node,instanceItems);
                }
                Instance instance = new Instance();
                instance.setAbout(nodeDocument.attribute(0).getStringValue());
                instance.setBibframeItems(instanceItems);
                bibframe.setInstance(instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bibframe;
    }

    private void buildBibFrameObj(Element element,List<BibframeItem> bibframeItems){
        String name = element.getName();
        BibframeItem bibframeItem = null;
        switch (name){
            case "adminMetadata":
                bibframeItem = new AdminMetadata();
                bibframeItem.buildBibToObj(element);
                break;
            case "type":
                bibframeItem = new Type();
                bibframeItem.buildBibToObj(element);
                break;
            case "content":
                bibframeItem = new Content();
                bibframeItem.buildBibToObj(element);
                break;
            case "identifiedBy":
                bibframeItem = new IdentifiedBy();
                bibframeItem.buildBibToObj(element);
                break;
            case "language":
                bibframeItem = new Language();
                bibframeItem.buildBibToObj(element);
                break;
            case "contribution":
                bibframeItem = new Contribution();
                bibframeItem.buildBibToObj(element);
                break;
            case "title":
                bibframeItem = new Title();
                bibframeItem.buildBibToObj(element);
                break;
            case "hasSeries":
                bibframeItem = new Series();
                bibframeItem.buildBibToObj(element);
                break;
            case "subject":
                bibframeItem = new Subject();
                bibframeItem.buildBibToObj(element);
                break;
            case "expressionOf":
                bibframeItem = new ExpressionOf();
                bibframeItem.buildBibToObj(element);
                break;
            case "relatedTo":
                bibframeItem = new RelatedTo();
                bibframeItem.buildBibToObj(element);
                break;
            case "issuance":
                bibframeItem = new Issuance();
                bibframeItem.buildBibToObj(element);
                break;
            case "provisionActivity":
                bibframeItem = new ProvisionActivity();
                bibframeItem.buildBibToObj(element);
                break;
            case "responsibilityStatement":
                bibframeItem = new ResponsibilityStatement();
                bibframeItem.buildBibToObj(element);
                break;
            case "dimensions":
                bibframeItem = new Dimensions();
                bibframeItem.buildBibToObj(element);
                break;
            case "seriesStatement":
                bibframeItem = new SeriesStatement();
                bibframeItem.buildBibToObj(element);
                break;
            default:
                break;
        }

        if(bibframeItem != null){
            bibframeItems.add(bibframeItem);
        }

    }


    /**
     * <p>bibframe对象转换字符串</p>
     *
     * @Description:
     * @Author: WCDK
     **/

    public String propertiesToStrem(Bibframe bibframe) throws Exception {
        Document document = DocumentHelper.createDocument();

        String rootName = "rdf:RDF";

        Element recordNode = document.addElement(rootName);
        Map<String, String> nameSpace = bibframe.getNameSpace();
        nameSpace.forEach((k, v) -> {
            recordNode.addNamespace(k, v);
        });
        BibNode workNode = bibframe.getWork().toBibFrameNode();
        Instance instance = bibframe.getInstance();
        if(instance != null){
            BibNode hasInstance = new BibNode("bf","hasInstance").appendAttribute("rdf:resource",instance.getAbout());
            workNode.appendChild(hasInstance);
        }
        stream(workNode, recordNode);
        if(instance != null){
            stream(instance.toBibFrameNode(), recordNode);
        }

        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        StringWriter stringWriter = new StringWriter(1024);
        XMLWriter xmlWriter = new XMLWriter(stringWriter, format);

        xmlWriter.setEscapeText(false);
        xmlWriter.write(document);
        xmlWriter.flush();
        xmlWriter.close();
        return stringWriter.toString();
    }

    private void stream(BibNode properties, Element element) {
        String name = properties.getName();
        String prefix = properties.getPrefix();
        String value = properties.getValue();

        Element element1 = element.addElement(name);
        if (value != null) {
            element1.setText(value);
        }
        if (prefix != null) {
            element1.setName(prefix + ":" + name);
        }

        Map<String, String> nameSpace = properties.getNameSpace();
        nameSpace.forEach((k, v) -> {
            element1.addNamespace(k, v);
        });
        Map<String, String> attribute = properties.getAttributes();
        attribute.forEach((k, v) -> {
            element1.addAttribute(k, v);
        });
        List<BibNode> child = properties.getChild();
        for (int i = 0; i < child.size(); i++) {
            BibNode coreProperties = child.get(i);
            stream(coreProperties, element1);
        }
    }
}
