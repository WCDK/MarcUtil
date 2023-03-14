package com.wcdk.marcutil.Bean;

import com.wcdk.marcutil.RepeatKeyMap;
import lombok.Data;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Data
public class Record {
    private String charSet="utf8";
    private String xmlns="http://www.loc.gov/MARC21/slim";
    private String leader = "";
    private List<Controlfield> controlfields = new LinkedList<>();
    private List<Datafield> datafields = new LinkedList<>();

    public Record addControlfield(Controlfield controlfield){
        controlfields.add(controlfield);
        return this;
    }
    public Record addDatafield(Datafield datafield){
        datafields.add(datafield);
        return this;
    }

    @Override
    public String toString(){
        StringBuffer value = new StringBuffer();
        StringBuffer conent = new StringBuffer();
        // "\u001E" 字段开始标识
        // 中间 索引标识
        //"\u001F" 子字段标识 只一位
        // "\u001D" 单条marc结束标识
        //conent.append(record.getLeader());
        String leader = this.getLeader();
        // 没有子字段的属性字段
        List<Controlfield> controlfields = this.getControlfields();
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

        List<Datafield> datafields = this.getDatafields();
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
                    byte[] bytes = new byte[0];
                    try {
                        bytes = v.getBytes(charSet);
                    } catch (UnsupportedEncodingException ex) {
                        ex.printStackTrace();
                    }
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
                byte[] bytes = new byte[0];
                try {
                    bytes = datafield.getBytes(charSet);
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
                int length = bytes.length;
                conent.append(completionZero_prefix(String.valueOf(length), 4));

                conent.append(completionZero_prefix(String.valueOf(index.get()), 5));
                index.set(index.get() + length);
                value.append(datafield);
            });
        }

        String dleanth = conent.substring(conent.length() - 5);
        String substring =completionZero_prefix(String.valueOf(conent.length()+value.toString().getBytes(StandardCharsets.UTF_8).length), 5);
        leader  = substring+leader.substring(5);
        leader = leader.substring(0,12)+dleanth+leader.substring(17);
        return leader+conent.append(value+"\u001D").toString();
    }

    private String completionZero_prefix(String value, int length) {
        while (value.length() < length) {
            value = "0" + value;
        }
        return value;
    }
}
