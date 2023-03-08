package com.wcdk.marcutil.Bibframe;

import lombok.Data;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.tree.DefaultText;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class Title extends BibframeItem {
    String name = "Title";
    boolean series = false;
    boolean abbreviatedTitle = false;
    String mainTitle;
    String code;
    String variantTitle;
    boolean titleSortkey = false;

    String marcKey = "440";
    String index1 = " ";
    String index2 = " ";
    String titleMatchKey;
    String titleMarcKey;
    @Override
    public BibNode toBibFrameNode() {
        BibNode title = new BibNode();
        title.setPrefix("bf");
        title.setName("title");

        BibNode titleValue = new BibNode("bf","mainTitle");
        titleValue.setPrefix("bf");
        boolean isMain = false;
        if (mainTitle != null && !mainTitle.equals("")) {
            titleValue.setValue(this.mainTitle);
            isMain = true;
        } else if (variantTitle != null && !variantTitle.equals("")) {
            titleValue.setValue(this.variantTitle);
            isMain = false;
        } else {
            return null;
        }
        if (abbreviatedTitle) {
            BibNode abbreviatedTitle = new BibNode();
            abbreviatedTitle.setPrefix("bf");
            abbreviatedTitle.setName("AbbreviatedTitle");

            BibNode assigner = new BibNode();
            assigner.setPrefix("bf");
            assigner.setName("assigner");

            BibNode agent = new BibNode();
            agent.setPrefix("bf");
            agent.setName("Agent");

            BibNode code = new BibNode();
            code.setPrefix("bf");
            code.setName("code");
            code.setValue(this.code);

            agent.appendChild(code);
            assigner.appendChild(agent);

            abbreviatedTitle.appendChild(titleValue,assigner);
            title.appendChild(abbreviatedTitle);
        } else {
            if (isMain) {
                BibNode titlec = new BibNode();
                titlec.setPrefix("bf");
                titlec.setName("Title");
                if(titleSortkey){
                    BibNode titleSortKey = new BibNode();
                    titleSortKey.setPrefix("bflc");
                    titleSortKey.setName("titleSortKey");
                    titlec.appendChild(titleSortKey);
                }
                titlec.appendChild(titleValue);

                if(series || titleMatchKey != null){
                    String flg = marcKey.substring(1);
                    BibNode nameMatchKey = new BibNode("bflc","name"+flg+"MatchKey",titleValue.getValue());
                    BibNode nameMarcKey = new BibNode("bflc","name"+flg+"MarcKey",this.marcKey+index1+index2+titleValue.getValue());
                    titlec.appendChild(nameMatchKey,nameMarcKey);
                }
                title.appendChild(titlec);
            }else {
                BibNode variantTitle = new BibNode();
                variantTitle.setPrefix("bf");
                variantTitle.setName("VariantTitle");

                variantTitle.appendChild(titleValue);
                title.appendChild(variantTitle);
            }
        }


        return title;
    }

    @Override
    public BibframeItem buildBibToObj(Element element) {
        Node node = element.selectSingleNode("bf:AbbreviatedTitle");
        if(node != null){
            abbreviatedTitle = true;
            Element addt = (Element)node;
            this.mainTitle = addt.selectSingleNode("bf:mainTitle").getStringValue();
            code = addt.selectSingleNode("bf:assigner").selectSingleNode("bf:Agent").selectSingleNode("bf:code").getStringValue();
            return this;
        }
        node = element.selectSingleNode("bf:Title");
        if(node != null){
            this.mainTitle = node.selectSingleNode("bf:mainTitle").getStringValue();
            if(node.selectSingleNode("bflc:titleSortKey") != null){
                titleSortkey = true;
            }
            Element agen =(Element) node;
            List<Node> content = agen.content();
            content = content.stream().filter(e->!(e instanceof Namespace)).filter(e->!(e instanceof DefaultText)).collect(Collectors.toList());
            Node node1 = content.stream().filter(e -> e.getName().endsWith("MarcKey")).findFirst().orElse(null);

            if(node1 != null){
                this.titleMarcKey = node1.getStringValue();
                this.marcKey = titleMarcKey.substring(0,3);
                this.index1 = titleMarcKey.substring(3,4);
                this.index2 = titleMarcKey.substring(4,5);
                String flg = this.marcKey.substring(1,3);

                node1 = agen.selectSingleNode("bflc:title"+flg+"MatchKey");
                this.titleMatchKey = node1.getStringValue();
            }

        }else {
            node = element.selectSingleNode("bf:VariantTitle");
            this.variantTitle = node.selectSingleNode("bf:mainTitle").getStringValue();
        }
        return this;
    }
}
