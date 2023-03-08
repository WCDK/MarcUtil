package com.wcdk.marcutil.Bibframe.work;

import com.wcdk.marcutil.Bibframe.BibNode;
import com.wcdk.marcutil.Bibframe.BibframeItem;
import lombok.Data;

import java.util.List;

@Data
public class Work {
    String name = "Work";
    String about;
    List<BibframeItem> nodes;

    public BibNode toBibFrameNode(){
        BibNode work = new BibNode("bf","Work").appendAttribute("rdf:about",about);
        if(nodes != null && nodes.size() > 0){
            nodes.forEach(e->{
                work.appendChild(e.toBibFrameNode());
            });
        }
        return work;
    }
}
