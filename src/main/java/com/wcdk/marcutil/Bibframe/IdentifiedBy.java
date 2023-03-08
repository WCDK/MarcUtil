package com.wcdk.marcutil.Bibframe;

import lombok.Data;
import org.dom4j.Element;
import org.dom4j.Node;

@Data
public class IdentifiedBy extends BibframeItem {
    String name = "IdentifiedBy";
    String issnValue;
    String lccnValue;

    String isbnValue;
    String status_about;
    String statusLable;
    String qualifier;
    String publisherNumberValue;

    @Override
    public BibNode toBibFrameNode() {
        BibNode identifiedBy = new BibNode("bf", "identifiedBy");
        if (issnValue != null) {
            BibNode isnn = new BibNode("bf", "Issn");
            BibNode value = new BibNode("rdf", "value", issnValue);
            isnn.appendChild(value);
            identifiedBy.appendChild(isnn);
            return identifiedBy;
        }
        if (lccnValue != null) {
            BibNode lccnValue = new BibNode("bf", "Lccn");
            BibNode value = new BibNode("rdf", "value", this.lccnValue);
            lccnValue.appendChild(value);
            identifiedBy.appendChild(lccnValue);
            return identifiedBy;
        }
        if (isbnValue != null) {
            BibNode isbnValue = new BibNode("bf", "Isbn");
            BibNode value = new BibNode("rdf", "value", this.isbnValue);
            isbnValue.appendChild(value);
            if(this.statusLable != null){
                BibNode status = new BibNode("bf", "status");
                BibNode statusc = new BibNode("bf", "Status").appendAttribute("rdf:about", this.status_about);
                BibNode label = new BibNode("rdfs", "label", this.statusLable);
                statusc.appendChild(label);
                status.appendChild(statusc);
                isbnValue.appendChild(status);
            }
            if(this.qualifier != null){
                BibNode qualifier = new BibNode("bf", "qualifier", this.qualifier);
                isbnValue.appendChild(qualifier);
            }

            identifiedBy.appendChild(isbnValue);
            return identifiedBy;
        }
        if(publisherNumberValue != null){
            BibNode PublisherNumber = new BibNode("bf", "PublisherNumber");
            BibNode value = new BibNode("rdf", "value", publisherNumberValue);
            PublisherNumber.appendChild(value);
            identifiedBy.appendChild(PublisherNumber);
            return identifiedBy;
        }
        return null;
    }

    @Override
    public BibframeItem buildBibToObj(Element element) {
        Node node = element.selectSingleNode("bf:Issn");
        if (node != null) {
            this.issnValue = node.selectSingleNode("rdf:value").getStringValue();
        } else if (element.selectSingleNode("bf:Lccn") != null) {
            node = element.selectSingleNode("bf:Lccn");
            this.lccnValue = node.selectSingleNode("rdf:value").getStringValue();
        } else if (element.selectSingleNode("bf:Isbn") != null) {
            node = element.selectSingleNode("bf:Isbn");
            this.isbnValue = node.selectSingleNode("rdf:value").getStringValue();
            if (node.selectSingleNode("bf:status") != null) {
                Node node2 = node.selectSingleNode("bf:status");
                this.status_about = ((Element) node2.selectSingleNode("bf:Status")).attribute(0).getStringValue();
                this.statusLable = node2.selectSingleNode("bf:Status").selectSingleNode("rdfs:label").getStringValue();
            }
            if (node.selectSingleNode("bf:qualifier") != null) {
                this.qualifier = node.selectSingleNode("bf:qualifier").getStringValue();
            }
        } else if (element.selectSingleNode("bf:PublisherNumber") != null) {
            node = element.selectSingleNode("bf:PublisherNumber");
            this.publisherNumberValue = node.selectSingleNode("rdf:value").getStringValue();
        }

        return this;
    }
}
