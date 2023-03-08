package com.wcdk.marcutil.Bibframe.work;

import com.wcdk.marcutil.Bibframe.BibNode;
import com.wcdk.marcutil.Bibframe.BibframeItem;
import lombok.Data;
import org.dom4j.Element;
import org.dom4j.Node;

@Data
public class Content extends BibframeItem {
    String name = "Content";
    /**bf **/
    /** rdf **/
    String content_about;
    /**rdfs **/
    String contentLabel;

    @Override
    public BibNode toBibFrameNode() {

        if(contentLabel != null){
            BibNode content = new BibNode("bf","content");
            BibNode contentc = new BibNode("bf","Content").appendAttribute("rdf:about",this.content_about);
            BibNode label = new BibNode("rdfs","label",this.contentLabel);
            contentc.appendChild(label);
            content.appendChild(contentc);
            return content;
        }

        return null;
    }

    @Override
    public BibframeItem buildBibToObj(Element element) {
        Node node = element.selectSingleNode("bf:Content");
        Element el = (Element) node;
        this.content_about = el.attribute(0).getStringValue();
        this.contentLabel = el.selectSingleNode("rdfs:label").getStringValue();
        return this;
    }
}
