package com.wcdk.marcutil;

import com.wcdk.marcutil.Bean.Record;
import com.wcdk.marcutil.Bibframe.Bibframe;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class test {
    public static void main(String[] args) {
        try {
//            String marcs = "02297cam a2200553 i 4500000000200000001001400002003000600016005001700022008004100039010001600080020001800096020003000114039012800144050002300272082001800295099001800313100002700331200000700358245011100365246003000476260007900506300004700585336002100632336002800653337002500681338002300706490002000729500003200749500002600781600007800807600004800885600004000933600007100973600006301044650005201107650004401159650003601203650002801239651005001267651004201317655003501359655002001394700002801414700006501442830002101507992000701528999001101535999019701546\u001E2\u001Evtls003484754\u001EHK-PL\u001E20210921171100.0\u001E171229s2014    mnua   j 6    000 1 eng c\u001E  \u001Fa2014-019433\u001E  \u001Fa9781496500168\u001E  \u001Fa9781496500359 (paperback)\u001E 9\u001Fa202109211711\u001FbAUTHORITY\u001Fc202009300900\u001FdAUTHORITY\u001Fc202009300854\u001FdAUTHORITY\u001Fc202009220935\u001Fdltpueepc1\u001Fy201712291548\u001Fzaltpueep5\u001E00\u001FaPZ7.7.P69\u001FbAr 2014\u001E04\u001Fa741.5/973\u001F223\u001E  \u001Fb741.5973\u001FcPOW\u001E1 \u001FaPowell, Martin,\u001Fd1959-\u001E  \u001Fa12\u001E10\u001FaArthur Conan Doyle's The hound of the Baskervilles :\u001Fba graphic novel /\u001Fcby Martin Powell & Daniel Ferran.\u001E3 \u001FaHound of the Baskervilles\u001E  \u001FaNorth Mankato, Minnesota :\u001FbStone Arch Books, a Capstone imprint,\u001Fc©2014.\u001E  \u001Fa69 pages :\u001Fbcolour illustrations ;\u001Fc23 cm.\u001E  \u001Fatext\u001F2rdacontent\u001E  \u001Fastill image\u001F2rdacontent\u001E  \u001Faunmediated\u001F2rdamedia\u001E  \u001Favolume\u001F2rdacarrier\u001E1 \u001FaGraphic revolve\u001E  \u001FaOriginally published: 2008.\u001E  \u001Fa\"GRL: U\"--Back cover.\u001E10\u001FaDoyle, Arthur Conan,\u001Fd1859-1930.\u001FtHound of the Baskervilles\u001FvAdaptations.\u001E10\u001FaHolmes, Sherlock\u001FvComic books, strips, etc.\u001E10\u001FaHolmes, Sherlock\u001FvJuvenile fiction.\u001E10\u001FaWatson, John H.\u001Fc(Fictitious character)\u001FvComic books, strips, etc.\u001E10\u001FaWatson, John H.\u001Fc(Fictitious character)\u001FvJuvenile fiction.\u001E 0\u001FaBlessing and cursing\u001FvComic books, strips, etc.\u001E 0\u001FaBlessing and cursing\u001FvJuvenile fiction.\u001E 0\u001FaDogs\u001FvComic books, strips, etc.\u001E 0\u001FaDogs\u001FvJuvenile fiction.\u001E 0\u001FaDartmoor (England)\u001FvComic books, strips, etc.\u001E 0\u001FaDartmoor (England)\u001FvJuvenile fiction.\u001E 0\u001FaDetective and mystery stories.\u001E 0\u001FaGraphic novels.\u001E1 \u001FaPérez, Daniel,\u001Fd1977-\u001E1 \u001FaDoyle, Arthur Conan,\u001Fd1859-1930.\u001FtHound of the Baskervilles.\u001E 0\u001FaGraphic revolve.\u001E  \u001FaRS\u001E  \u001FaVIRTUA\u001E  \u001FaVTLSSORT0080*0100*0200*0201*0500*0820*0990*1000*2450*2460*2600*3000*3360*3361*3370*3380*4900*5000*5001*6000*6001*6002*6505*6500*6501*6502*6503*6504*6003*6510*6511*6550*7000*7001*8300*9920*9992\u001E\u001D";
            String marcs = "02273cam a2201546 i 4500000000200000001001400002003000600016005001700022008004100039010001600080020001800096020003000114039012800144050002300272082001800295099001800313100002700331200000700358245011100365246003000476260007900506300004700585336002100632336002800653337002500681338002300706490002000729500003200749500002600781600007800807600004800885600004000933600007100973600006301044650005201107650004401159650003601203650002801239651005001267651004201317655003501359655002001394700002801414700006501442830002101507992000701528999001101535999019701546\u001E2\u001Evtls003484754\u001EHK-PL\u001E20210921171100.0\u001E171229s2014    mnua   j 6    000 1 eng c\u001E  \u001Fa2014-019433\u001E  \u001Fa9781496500168\u001E  \u001Fa9781496500359 (paperback)\u001E 9\u001Fa202109211711\u001FbAUTHORITY\u001Fc202009300900\u001FdAUTHORITY\u001Fc202009300854\u001FdAUTHORITY\u001Fc202009220935\u001Fdltpueepc1\u001Fy201712291548\u001Fzaltpueep5\u001E00\u001FaPZ7.7.P69\u001FbAr 2014\u001E04\u001Fa741.5/973\u001F223\u001E  \u001Fb741.5973\u001FcPOW\u001E1 \u001FaPowell, Martin,\u001Fd1959-\u001E  \u001Fa12\u001E10\u001FaArthur Conan Doyle's The hound of the Baskervilles :\u001Fba graphic novel /\u001Fcby Martin Powell & Daniel Ferran.\u001E3 \u001FaHound of the Baskervilles\u001E  \u001FaNorth Mankato, Minnesota :\u001FbStone Arch Books, a Capstone imprint,\u001Fc漏2014.\u001E  \u001Fa69 pages :\u001Fbcolour illustrations ;\u001Fc23 cm.\u001E  \u001Fatext\u001F2rdacontent\u001E  \u001Fastill image\u001F2rdacontent\u001E  \u001Faunmediated\u001F2rdamedia\u001E  \u001Favolume\u001F2rdacarrier\u001E1 \u001FaGraphic revolve\u001E  \u001FaOriginally published: 2008.\u001E  \u001Fa\"GRL: U\"--Back cover.\u001E10\u001FaDoyle, Arthur Conan,\u001Fd1859-1930.\u001FtHound of the Baskervilles\u001FvAdaptations.\u001E10\u001FaHolmes, Sherlock\u001FvComic books, strips, etc.\u001E10\u001FaHolmes, Sherlock\u001FvJuvenile fiction.\u001E10\u001FaWatson, John H.\u001Fc(Fictitious character)\u001FvComic books, strips, etc.\u001E10\u001FaWatson, John H.\u001Fc(Fictitious character)\u001FvJuvenile fiction.\u001E 0\u001FaBlessing and cursing\u001FvComic books, strips, etc.\u001E 0\u001FaBlessing and cursing\u001FvJuvenile fiction.\u001E 0\u001FaDogs\u001FvComic books, strips, etc.\u001E 0\u001FaDogs\u001FvJuvenile fiction.\u001E 0\u001FaDartmoor (England)\u001FvComic books, strips, etc.\u001E 0\u001FaDartmoor (England)\u001FvJuvenile fiction.\u001E 0\u001FaDetective and mystery stories.\u001E 0\u001FaGraphic novels.\u001E1 \u001FaPe虂rez, Daniel,\u001Fd1977-\u001E1 \u001FaDoyle, Arthur Conan,\u001Fd1859-1930.\u001FtHound of the Baskervilles.\u001E 0\u001FaGraphic revolve.\u001E  \u001FaRS\u001E  \u001FaVIRTUA\u001E  \u001FaVTLSSORT0080*0100*0200*0201*0500*0820*0990*1000*2450*2460*2600*3000*3360*3361*3370*3380*4900*5000*5001*6000*6001*6002*6505*6500*6501*6502*6503*6504*6003*6510*6511*6550*7000*7001*8300*9920*9992\u001D";
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
            System.out.println(record.toString());
            record.getControlfields().remove(0);

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
