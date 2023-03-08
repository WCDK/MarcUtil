package com.wcdk.marcutil.Bibframe.work;

import com.wcdk.marcutil.Bibframe.BibNode;
import com.wcdk.marcutil.Bibframe.BibframeItem;
import lombok.Data;
import org.dom4j.Element;
import org.dom4j.Node;

@Data
public class Language extends BibframeItem {
    String name = "Language";
    /** bf **/
    String languagePart;
    /** rdf rdf **/
    String languageValue_resource;
    String languageValue;
    @Override
    public BibNode toBibFrameNode() {
        if(languageValue != null){
            BibNode language = new BibNode("bf","language");
            BibNode languagec = new BibNode("bf","Language");
            if(languagePart != null){
                BibNode part = new BibNode("bf","part",languagePart);
                languagec.appendChild(part);
            }
            BibNode value = new BibNode("bf","value").appendAttribute("rdf:resource",this.languageValue_resource+languageValue);
            languagec.appendChild(value);
            language.appendChild(languagec);
            return language;
        }


        return null;
    }

    @Override
    public BibframeItem buildBibToObj(Element element) {
        Node node = element.selectSingleNode("bf:Language");
        Element el = (Element) node;
        Node node1 = el.selectSingleNode("bf:part");
        if(node1 != null){
            this.languagePart = node1.getStringValue();
        }
        Node node2 = el.selectSingleNode("rdf:value");
        Element e2 = (Element) node2;
        String lan = e2.attribute(0).getStringValue();
        int i = lan.lastIndexOf("/");
        this.languageValue_resource = lan.substring(0,i);
        this.languageValue = lan.substring(i+1);
        return this;
    }
}
