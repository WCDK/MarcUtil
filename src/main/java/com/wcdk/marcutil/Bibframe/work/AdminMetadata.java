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
public class AdminMetadata extends BibframeItem {
    String name = "AdminMetadata";
    /** rdfs **/
    String generationProcessLabel;
    /** bf **/
    /**rdf **/
    String generationDate_datatype;
    String generationDateValue;

    /** bf **/
    /** rdf **/
    String status_about;
    /** rdfs **/
    String statusLabel;
    /** bflc **/
    /** rdf **/
    String encodingLevel_about;
    String encodingLevelLabel;
    /** bf **/
    /** rdf **/
    String descriptionConventions_about;
    String descriptionConventionsCode;
    /** bf <Local></>**/
    String identifiedByValue;
    String identifiedByCode;
    /** bf rdf**/
    String changeDate_datatype;
    String changeDateValue;
    String creationDate_datatype;
    String creationDateValue;

    @Override
    public BibNode toBibFrameNode() {
        BibNode adminMetadata = new BibNode("bf","adminMetadata");
        BibNode adminMetadatac = new BibNode("bf","AdminMetadata");
        adminMetadata.appendChild(adminMetadatac);

        if(this.generationProcessLabel != null){
            BibNode generationProcess = new BibNode("bf","generationProcess");
            BibNode generationProcessc = new BibNode("bf","GenerationProcess");
            BibNode label = new BibNode("rdfs","label",this.generationProcessLabel);
            BibNode generationDate = new BibNode("bf","generationDate",this.generationDateValue);
            generationDate.appendAttribute("rdf:datatype",this.generationDate_datatype);

            generationProcessc.appendChild(label,generationDate);
            generationProcess.appendChild(generationProcessc);
            adminMetadatac.appendChild(generationProcess);
        }
        if(this.statusLabel != null){
            BibNode status = new BibNode("bf","status");
            BibNode statusc = new BibNode("bf","Status");
            statusc.appendAttribute("rdf:about",this.status_about);
            BibNode label = new BibNode("rdfs","label",this.statusLabel);

            statusc.appendChild(label);
            status.appendChild(statusc);
            adminMetadatac.appendChild(status);
        }
        if(this.encodingLevelLabel != null){
            BibNode encodingLevel = new BibNode("bflc","encodingLevel");
            BibNode encodingLevelc = new BibNode("bflc","EncodingLevel");
            encodingLevelc.appendAttribute("rdf:about",this.encodingLevel_about);
            BibNode label = new BibNode("rdfs","label",this.encodingLevelLabel);

            encodingLevelc.appendChild(label);
            encodingLevel.appendChild(encodingLevelc);
            adminMetadatac.appendChild(encodingLevel);
        }
        if(this.encodingLevelLabel != null){
            BibNode descriptionConventions = new BibNode("bf","descriptionConventions");
            BibNode descriptionConventionsc = new BibNode("bf","DescriptionConventions");
            descriptionConventionsc.appendAttribute("rdf:about",this.descriptionConventions_about);
            BibNode code = new BibNode("bf","code",this.descriptionConventionsCode);

            descriptionConventionsc.appendChild(code);
            descriptionConventions.appendChild(descriptionConventionsc);
            adminMetadatac.appendChild(descriptionConventions);
        }
        if(this.identifiedByValue != null){
            BibNode identifiedBy = new BibNode("bf","identifiedBy");
            BibNode local = new BibNode("bf","Local");
            BibNode value = new BibNode("rdf","value",this.identifiedByValue);

            BibNode assigner = new BibNode("bf","assigner");
            BibNode agent = new BibNode("bf","Agent");
            BibNode code = new BibNode("bf","code",this.identifiedByCode);

            agent.appendChild(code);
            assigner.appendChild(agent);

            local.appendChild(value,assigner);
            identifiedBy.appendChild(local);
            adminMetadatac.appendChild(identifiedBy);
        }
        if(this.changeDateValue != null){
            BibNode changeDate = new BibNode("bf","changeDate",this.changeDateValue);
            changeDate.appendAttribute("rdf:datatype",this.changeDate_datatype);
            adminMetadatac.appendChild(changeDate);
        }
        if(this.creationDateValue != null){
            BibNode creationDate = new BibNode("bf","creationDate",this.creationDateValue);
            creationDate.appendAttribute("rdf:datatype",this.creationDate_datatype);
            adminMetadatac.appendChild(creationDate);
        }

        return adminMetadata;
    }

    @Override
    public BibframeItem buildBibToObj(Element element) {
        Node node = element.selectSingleNode("bf:AdminMetadata");
        List<Node> content = ((Element) node).content();
        content = content.stream().filter(e->!(e instanceof Namespace)).filter(e->!(e instanceof DefaultText)).collect(Collectors.toList());
        for (Node node1: content){
            Element e = (Element)node1;
            Node node2 = e.selectSingleNode("bf:Status");
            if (node2 != null){
                Element e2 = (Element) node2;
                this.status_about = e2.attribute(0).getStringValue();
                this.statusLabel = e2.selectSingleNode("rdfs:label").getStringValue();
                continue;
            }
            node2 = e.selectSingleNode("bflc:EncodingLevel");
            if (node2 != null){
                Element e2 = (Element) node2;
                this.encodingLevel_about = e2.attribute(0).getStringValue();
                this.encodingLevelLabel = e2.selectSingleNode("rdfs:label").getStringValue();
                continue;
            }
            node2 = e.selectSingleNode("bf:DescriptionConventions");
            if (node2 != null){
                Element e2 = (Element) node2;
                this.descriptionConventions_about = e2.attribute(0).getStringValue();
                this.descriptionConventionsCode = e2.selectSingleNode("bf:code").getStringValue();
                continue;
            }
            node2 = e.selectSingleNode("bf:Local");
            if (node2 != null){
                Element e2 = (Element) node2;
                this.identifiedByValue = e2.selectSingleNode("rdf:value").getStringValue();
                this.identifiedByCode = e2.selectSingleNode("bf:assigner").selectSingleNode("bf:Agent").selectSingleNode("bf:code").getStringValue();
                continue;
            }
            if(e.getName().equals("changeDate")){
                this.changeDate_datatype = e.attribute(0).getStringValue();
                this.changeDateValue = e.getStringValue();
            }else if(e.getName().equals("creationDate")){
                this.creationDate_datatype = e.attribute(0).getStringValue();
                this.creationDateValue = e.getStringValue();
            }
        }

        return this;
    }
}
