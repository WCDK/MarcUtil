package com.wcdk.marcutil.Bibframe.instance;

import com.wcdk.marcutil.Bibframe.BibNode;
import com.wcdk.marcutil.Bibframe.BibframeItem;
import lombok.Data;
import org.dom4j.Element;

@Data
public class InstanceOf extends BibframeItem {
    String instanceOf;
    @Override
    public BibNode toBibFrameNode() {
        return  new BibNode("bf","instanceOf").appendAttribute("rdf:resource",instanceOf);
    }

    @Override
    public BibframeItem buildBibToObj(Element element) {
        this.instanceOf = element.attribute(0).getStringValue();
        return this;
    }
}
