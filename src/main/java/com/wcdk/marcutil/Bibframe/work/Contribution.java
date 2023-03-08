package com.wcdk.marcutil.Bibframe.work;

import com.wcdk.marcutil.Bibframe.BibNode;
import com.wcdk.marcutil.Bibframe.BibframeItem;
import lombok.Data;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.tree.DefaultText;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class Contribution extends BibframeItem  {

    String name = "Contribution";

    String contribution_resource;
    String type_resource;
    String agent_about;
    String agent_type_resource;

    String nameMatchKey;
    String nameMarcKey;
    String primary;
    String agentLabel;
    String role_about;
    String marcKey = "700";
    String index1 = " ";
    String index2 = " ";

    @Override
    public BibNode toBibFrameNode() {
        String flg = marcKey.substring(1);
        BibNode contribution = new BibNode("bf", "contribution");
        BibNode contributionc = new BibNode("bf", "Contribution");

        if(type_resource!= null){
            BibNode type = new BibNode("rdf", "type").appendAttribute("rdf:resource", this.type_resource);
            contributionc.appendChild(type);
        }
        BibNode agent = new BibNode("bf", "agent");
        BibNode agentc = new BibNode("bf", "Agent").appendAttribute("rdf:about", this.agent_about);
        BibNode agentType = new BibNode("rdf", "type").appendAttribute("rdf:resource", this.agent_type_resource);
        agent.appendChild(agentc);
        agentc.appendChild(agentType);
        contributionc.appendChild(agent);

        contribution.appendChild(contributionc);

        BibNode nameMatchKey = new BibNode("bflc", "name" + flg + "MatchKey",this.nameMatchKey);
        BibNode nameMarcKey = new BibNode("bflc", "name" + flg + "MarcKey",this.nameMarcKey);
        if(primary != null){
            BibNode primaryContributorNameMatchKey = new BibNode("bflc", "primaryContributorName" + flg + "MatchKey",this.nameMatchKey);
            agentc.appendChild(primaryContributorNameMatchKey);
        }
        BibNode label = new BibNode("rdfs", "label",this.agentLabel);
        agentc.appendChild(nameMatchKey,nameMarcKey,label);
        if(role_about != null){
            BibNode role = new BibNode("bf", "role");
            BibNode rolec = new BibNode("bf", "Role").appendAttribute("rdf:about",this.role_about);
            role.appendChild(rolec);
            contributionc.appendChild(role);
        }
        return contribution;
    }

    @Override
    public BibframeItem buildBibToObj(Element element) {
        Node node = element.selectSingleNode("bf:Contribution");
        Element re = (Element) node;
        if(re.selectSingleNode("rdf:type") != null){
            this.type_resource = ((Element) re.selectSingleNode("rdf:type")).attribute(0).getStringValue();
        }
        Element agen =(Element) re.selectSingleNode("bf:agent").selectSingleNode("bf:Agent");
        this.agent_about = agen.attribute(0).getStringValue();
        this.agent_type_resource = ((Element)agen.selectSingleNode("rdf:type")).attribute(0).getStringValue();
        this.agentLabel = agen.selectSingleNode("rdfs:label").getStringValue();
        List<Node> content = agen.content();
        content = content.stream().filter(e->!(e instanceof Namespace)).filter(e->!(e instanceof DefaultText)).collect(Collectors.toList());

        Node node1 = content.stream().filter(e -> e.getName().endsWith("MarcKey")).findFirst().orElse(null);
        this.nameMarcKey = node1.getStringValue();
        this.marcKey = nameMarcKey.substring(0,3);
        this.index1 = nameMarcKey.substring(3,4);
        this.index2 = nameMarcKey.substring(4,5);
        String flg = this.marcKey.substring(1,3);

        node1 = agen.selectSingleNode("bflc:name"+flg+"MatchKey");
        this.nameMatchKey = node1.getStringValue();
        node1 =agen.selectSingleNode("bflc:primaryContributorName"+flg+"MatchKey");
        if(node1 != null){
            this.primary = node1.getStringValue();
        }
        Node node2 = re.selectSingleNode("bf:role").selectSingleNode("bf:Role");
        this.role_about = ((Element)node2).attribute(0).getStringValue();
        return this;
    }
}
