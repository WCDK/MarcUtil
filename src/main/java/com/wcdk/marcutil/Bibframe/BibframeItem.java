package com.wcdk.marcutil.Bibframe;

import org.dom4j.Element;

public abstract class BibframeItem {
   public abstract BibNode toBibFrameNode();
   public abstract BibframeItem buildBibToObj(Element element);
}
