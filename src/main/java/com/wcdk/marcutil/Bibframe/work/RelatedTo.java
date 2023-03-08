package com.wcdk.marcutil.Bibframe.work;

import com.wcdk.marcutil.Bibframe.BibNode;
import com.wcdk.marcutil.Bibframe.BibframeItem;
import com.wcdk.marcutil.Bibframe.Title;
import lombok.Data;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.tree.DefaultText;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class RelatedTo extends BibframeItem {
    String name = "RelatedTo";
    String about;
    List<BibframeItem> bibframeItems;
    @Override
    public BibNode toBibFrameNode() {
        BibNode relatedTo = new BibNode("bf","relatedTo");
        BibNode hub = new BibNode("bf","Hub").appendAttribute("rdf:about",about);
        bibframeItems.forEach(e->{
            BibNode contri = e.toBibFrameNode();
            hub.appendChild(contri);
        });
        relatedTo.appendChild(hub);

        return relatedTo;

    }

    @Override
    public BibframeItem buildBibToObj(Element element) {
        bibframeItems = new ArrayList<>();
        Node node = element.selectSingleNode("bf:Hub");
        Element el = (Element) node;
        this.about = el.attribute(0).getStringValue();
        List<Node> content = el.content();
        content = content.stream().filter(e->!(e instanceof Namespace)).filter(e->!(e instanceof DefaultText)).collect(Collectors.toList());
        for(Node node1 : content){
            String name = node1.getName();
            if("contribution".equals(name)){
                Contribution contribution = new Contribution();
                contribution.buildBibToObj((Element) node1);
                this.bibframeItems.add(contribution);
            }else if("title".equals(name)){
                Title title = new Title();
                title.buildBibToObj((Element) node1);
                this.bibframeItems.add(title);
            }
        }
        return this;
    }
}
