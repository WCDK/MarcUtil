package com.wcdk.marcutil.Bibframe.instance;

import com.wcdk.marcutil.Bibframe.BibNode;
import com.wcdk.marcutil.Bibframe.BibframeItem;
import lombok.Data;
import org.dom4j.Element;
import org.dom4j.Node;

@Data
public class Issuance extends BibframeItem {
    String name = "Issuance";
    String about;

    @Override
    public BibNode toBibFrameNode() {
        BibNode issuance = new BibNode("bf","issuance");
        BibNode issuancec = new BibNode("bf","Issuance").appendAttribute("rdf:about",about);
        issuance.appendChild(issuancec);
        return issuance;
    }

    @Override
    public BibframeItem buildBibToObj(Element element) {
        Node node = element.selectSingleNode("bf:Issuance");
        this.about = ((Element)node).attribute(0).getStringValue();

        return this;
    }
}
