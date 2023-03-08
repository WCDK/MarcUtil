package com.wcdk.marcutil.Bibframe.work;

import com.wcdk.marcutil.Bibframe.BibNode;
import com.wcdk.marcutil.Bibframe.BibframeItem;
import com.wcdk.marcutil.Bibframe.Title;
import lombok.Data;
import org.dom4j.Element;
import org.dom4j.Node;

@Data
public class Series extends BibframeItem {
    String name = "hasSeries";
    Contribution contribution;
    Title title;
    String about;

    @Override
    public BibNode toBibFrameNode() {
        BibNode Series = new BibNode("bf","hasSeries");
        BibNode hub = new BibNode("bf","Hub").appendAttribute("rdf:about",about);
        Series.appendChild(hub);
        if(contribution != null ){
            BibNode contri = contribution.toBibFrameNode();
            hub.appendChild(contri);
        }else if(title != null){
            title.setSeries(true);
            BibNode ti = title.toBibFrameNode();
            hub.appendChild(ti);
        }

        return Series;
    }

    @Override
    public BibframeItem buildBibToObj(Element element) {
        Node node = element.selectSingleNode("bf:Hub");
        this.about = ((Element)node).attribute(0).getStringValue();

        Node con = node.selectSingleNode("bf:contribution");
        Node tit = node.selectSingleNode("bf:title");
        if(con != null){
            this.contribution = new Contribution();
            this.contribution.buildBibToObj((Element) con);
        }
        if(tit != null){
            this.title = new Title();
            this.title.buildBibToObj((Element) tit);
        }
        return this;
    }
}
