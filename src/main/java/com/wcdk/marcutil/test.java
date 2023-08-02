package com.wcdk.marcutil;

import com.wcdk.marcutil.Bean.Record;
import com.wcdk.marcutil.Bibframe.Bibframe;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class test {
    public static void main(String[] args) {
        try {
            String marcs = "01135nam a2200766 a 4500001001400000003000600014005001700020008004100037010002400078020002600102022001900128028001900147039016300166041002300329100002200352110002200374111002200396130002100418210002100439240002100460245004300481246002100524300000900545400002200554440002100576600002100597610002100618611002100639630002100660700004300681710002100724711002100745730002100766\u001Evtls003618920\u001EHK-PL\u001E20230303154542.0\u001E230362 2019    ch                  chi  \u001E  \u001Fa 控制号010的值\u001E  \u001Fa 123 \u001Fz ISBN(Z)的值\u001E  \u001Fa ISBN022的值\u001E  \u001Fa ISBN028的值\u001E  \u001Fa202303031545\u001Fbwwwwwwww\u001Fc 202303031538\u001Fd wwwwwwww\u001Fc 202303031142\u001Fd wwwwwwww\u001Fc 202303031135\u001Fd xiexinwang\u001Fc 202303031134\u001Fd xiexinwang\u001Fy 202302171402\u001Fz xiexinwang\u001E  \u001Fa eng \u001Fb chi \u001Fh sch\u001E  \u001Fa 著者100的值 \u001E  \u001Fa 著者110的值 \u001E  \u001Fa 著者111的值 \u001E  \u001Fa 题名130的值\u001E  \u001Fa 题名210的值\u001E  \u001Fa 题名240的值\u001E  \u001Fa 著者245的值 \u001Fc 著者245(c)的值\u001E  \u001Fa 题名246的值\u001E  \u001Fc cm.\u001E  \u001Fa 著者400的值 \u001E  \u001Fa 题名440的值\u001E  \u001Fa 主题600的值\u001E  \u001Fa 主题610的值\u001E  \u001Fa 主题611的值\u001E  \u001Fa 主题630的值\u001E  \u001Fa 著者700的值 \u001Ft 题名700(t)的值\u001E  \u001Fa 著者710的值\u001E  \u001Fa 著者711的值\u001E  \u001Fa 题名730的值\u001D";
            MarcUtil marc = new MarcUtil();
// 
//            File file = new File("d://DRMCatalogueTemplate_Audio-visual.mrc");
//            File file = new File("d://marc.txt");
//            File file = new File("d:\\marc111.marc");
//            File file = new File("C:\\Users\\hp\\Desktop\\DRMCatalogueTemplate_Audio-visual.mrc");
//            File file = new File("d:\\exportGGG2.txt");
//            String str = marc.fileChart2Utf8(file);
//            FileInputStream ins = new FileInputStream(file);
//            byte[] bytes = new byte[ins.available()];
//            ins.read(bytes);
//            String str = new String(bytes);
            List<Record> records = marc.buildMarcData(marcs);
            Record record = records.get(0);

            String xml = marc.createXML(record);
            System.out.println(xml);
//
//            System.out.println(marc.toMarcString(record));
            String s = marc.toBibmrame(record);
            System.out.println(s);
            Bibframe bibframe = marc.parseBibframeXML(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
