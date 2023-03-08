package com.wcdk.marcutil.Bibframe.instance;

import com.wcdk.marcutil.Bibframe.BibNode;
import com.wcdk.marcutil.Bibframe.BibframeItem;
import lombok.Data;
import org.dom4j.Element;

@Data
public class SeriesStatement extends BibframeItem {
    String name = "seriesStatement";
    String value;
    @Override
    public BibNode toBibFrameNode() {
        BibNode bibNode = new BibNode("bf","seriesStatement",value);
        return bibNode;
    }

    @Override
    public BibframeItem buildBibToObj(Element element) {
        this.value = element.getStringValue();
        return this;
    }
}
