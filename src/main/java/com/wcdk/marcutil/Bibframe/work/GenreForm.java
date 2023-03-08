package com.wcdk.marcutil.Bibframe.work;

import com.wcdk.marcutil.Bibframe.BibNode;
import com.wcdk.marcutil.Bibframe.BibframeItem;
import lombok.Data;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.List;

@Data
public class GenreForm extends BibframeItem {

    String name = "GenreForm";
    /**bf rdf **/
    String genreForm_about;
    /** rdfs **/
    String genreFormlabel;

    @Override
    public BibNode toBibFrameNode() {
        return null;
    }

    @Override
    public BibframeItem buildBibToObj(Element element) {
        List<Node> nodes = element.selectNodes("bf:generationProcess");

        return null;
    }
}
