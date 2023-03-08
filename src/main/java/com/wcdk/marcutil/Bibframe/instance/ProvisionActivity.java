package com.wcdk.marcutil.Bibframe.instance;

import com.wcdk.marcutil.Bibframe.BibNode;
import com.wcdk.marcutil.Bibframe.BibframeItem;
import lombok.Data;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.List;

@Data
public class ProvisionActivity extends BibframeItem {
    String name = "ProvisionActivity";
    String type_resourece;
    String place_about;
    String placeValue;
    @Override
    public BibNode toBibFrameNode() {
        BibNode provisionActivity = new BibNode("bf","provisionActivity");
        BibNode provisionActivityc = new BibNode("bf","ProvisionActivity");

        BibNode type = new BibNode("rdf","type").appendAttribute("rdf:resource",this.type_resourece);

        BibNode place = new BibNode("bf","place");
        BibNode placec = new BibNode("bf","Place");
        if(this.place_about != null){
            placec.appendAttribute("rdf:about",this.place_about);
        }
        if(this.placeValue != null){
            BibNode placeLable = new BibNode("rdfs","label",this.placeValue);
            placec.appendChild(placeLable);
        }
        place.appendChild(placec);
        provisionActivityc.appendChild(type,place);
        provisionActivity.appendChild(provisionActivityc);
        return provisionActivity;
    }

    @Override
    public BibframeItem buildBibToObj(Element element) {
        Node node = element.selectSingleNode("bf:ProvisionActivity");
        this.type_resourece = ((Element)node.selectSingleNode("rdf:type")).attribute(0).getStringValue();
        List<Attribute> attributes = ((Element) node.selectSingleNode("bf:place").selectSingleNode("bf:Place")).attributes();
        if(attributes != null && attributes.size() > 0){
            this.place_about =attributes.get(0).getStringValue();
        }
        Node laveValue = node.selectSingleNode("bf:place").selectSingleNode("bf:Place").selectSingleNode("rdfs:label");
        if(laveValue != null){
            this.placeValue = laveValue.getStringValue();
        }

        return this;
    }
}
