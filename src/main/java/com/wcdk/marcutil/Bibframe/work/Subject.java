package com.wcdk.marcutil.Bibframe.work;

import com.wcdk.marcutil.Bibframe.BibNode;
import com.wcdk.marcutil.Bibframe.BibframeItem;
import com.wcdk.marcutil.Bibframe.Title;
import lombok.Data;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.tree.DefaultText;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class Subject extends BibframeItem {
    String name = "Subject";
    String agent_about;
    String type_resource1;
    String type_resource2;

    boolean topic = false;

    String authoritativeLabel;
    String nameMatchKey;
    String nameMarcKey;
    String label;

    String marcKey = "600";
    String index1 = " ";
    String index2 = " ";

    Title title;


    @Override
    public BibNode toBibFrameNode() {
        BibNode subject = new BibNode("bf", "subject");
        if (title == null) {
            String flg = marcKey.substring(1);

            BibNode agent = new BibNode("bf", "Agent").appendAttribute("rdf:about", this.agent_about);
            BibNode type1 = new BibNode("rdf", "type").appendAttribute("rdf:about", this.type_resource1);
            BibNode type2 = new BibNode("rdf", "type").appendAttribute("rdf:about", this.type_resource2);

            BibNode authoritativeLabel = new BibNode("madsrdf", "authoritativeLabel", this.authoritativeLabel);
            BibNode nameMatchKey = new BibNode("bflc", "name" + flg + "MatchKey", this.nameMatchKey);
            BibNode nameMarcKey = new BibNode("bflc", "name" + flg + "MarcKey", this.nameMarcKey);
            BibNode label = new BibNode("rdfs", "label", this.label);

            subject.appendChild(agent);
            agent.appendChild(type1, type2, authoritativeLabel, nameMatchKey, nameMarcKey, label);
        }else if(topic){
            BibNode top = new BibNode("bf", "Topic").appendAttribute("rdf:about", this.agent_about);
            BibNode type1 = new BibNode("rdf", "type").appendAttribute("rdf:about", this.type_resource1);
            BibNode label = new BibNode("madsrdf", "authoritativeLabel", this.authoritativeLabel);
            top.appendChild(type1, label, title.toBibFrameNode());
            subject.appendChild(top);
        } else {
            BibNode hub = new BibNode("bf", "Hub").appendAttribute("rdf:about", this.agent_about);
            BibNode type1 = new BibNode("rdf", "type").appendAttribute("rdf:about", this.type_resource1);
            BibNode label = new BibNode("madsrdf", "authoritativeLabel", this.authoritativeLabel);
            hub.appendChild(type1, label, title.toBibFrameNode());
            subject.appendChild(hub);
        }
        return subject;
    }

    @Override
    public BibframeItem buildBibToObj(Element element) {
        Node node = element.selectSingleNode("bf:Agent");
        if (node != null) {
            this.agent_about = ((Element) node).attribute(0).getStringValue();
            List<Node> nodes = node.selectNodes("rdf:type");

            Element el = (Element) nodes.get(0);
            type_resource1 = el.attribute(0).getStringValue();
            el = (Element) nodes.get(1);
            type_resource2 = el.attribute(0).getStringValue();

            this.authoritativeLabel = node.selectSingleNode("madsrdf:authoritativeLabel").getStringValue();
            this.label = node.selectSingleNode("rdfs:label").getStringValue();

            Element agen =(Element) node;
            List<Node> content = agen.content();
            content = content.stream().filter(e->!(e instanceof Namespace)).filter(e->!(e instanceof DefaultText)).collect(Collectors.toList());
            Node node1 = content.stream().filter(e -> e.getName().endsWith("MarcKey")).findFirst().orElse(null);

            if(node1 != null){
                this.nameMarcKey = node1.getStringValue();
                this.marcKey = nameMarcKey.substring(0,3);
                this.index1 = nameMarcKey.substring(3,4);
                this.index2 = nameMarcKey.substring(4,5);
                String flg = this.marcKey.substring(1,3);

                node1 = agen.selectSingleNode("bflc:name"+flg+"MatchKey");
                this.nameMatchKey = node1.getStringValue();
            }


        }else if(element.selectSingleNode("bf:Topic") != null){
            topic = true;
            node = element.selectSingleNode("bf:Topic");
            this.agent_about = ((Element) node).attribute(0).getStringValue();
            this.type_resource1 = ((Element)node.selectSingleNode("rdf:type")).attribute(0).getStringValue();
            this.type_resource1 = ((Element)node.selectSingleNode("rdf:type")).attribute(0).getStringValue();
            this.authoritativeLabel = node.selectSingleNode("madsrdf:authoritativeLabel").getStringValue();

        } else {
            node = element.selectSingleNode("bf:Hub");
            this.agent_about = ((Element) node).attribute(0).getStringValue();
            this.type_resource1 = ((Element)node.selectSingleNode("rdf:type")).attribute(0).getStringValue();
            this.authoritativeLabel = node.selectSingleNode("madsrdf:authoritativeLabel").getStringValue();
            Node tit = node.selectSingleNode("bf:title");
            this.title = new Title();
            this.title.buildBibToObj((Element) tit);
        }

        return this;
    }
}
