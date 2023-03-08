package com.wcdk.marcutil.Bibframe.instance;

import com.wcdk.marcutil.Bibframe.BibNode;
import com.wcdk.marcutil.Bibframe.BibframeItem;
import lombok.Data;
import org.dom4j.Element;

@Data
public class ResponsibilityStatement extends BibframeItem {
    String name="responsibilityStatement";
    String value;
    @Override
    public BibNode toBibFrameNode() {
        BibNode bibNode = new BibNode("bf","responsibilityStatement",value);
        return bibNode;
    }

    @Override
    public BibframeItem buildBibToObj(Element element) {
        this.value = element.getStringValue();
        return this;
    }
}
