package com.wcdk.marcutil.Bibframe;

import lombok.Data;
import org.dom4j.Element;

@Data
public class Type extends BibframeItem {
    String name = "Type";
    /** rdf rdf**/
    String type_resource;

    @Override
    public BibNode toBibFrameNode() {
        return  new BibNode("rdf","type").appendAttribute("rdf:resource",this.type_resource);
    }

    @Override
    public BibframeItem buildBibToObj(Element element) {
        String stringValue = element.attribute(0).getStringValue();
        this.type_resource = stringValue;
        return this;
    }
}
