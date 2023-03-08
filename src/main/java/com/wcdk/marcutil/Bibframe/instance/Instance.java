package com.wcdk.marcutil.Bibframe.instance;

import com.wcdk.marcutil.Bibframe.BibframeItem;
import com.wcdk.marcutil.Bibframe.BibNode;
import lombok.Data;

import java.util.List;


@Data
public class Instance {
    String about;
    List<BibframeItem> bibframeItems;

    public BibNode toBibFrameNode(){
        BibNode instance = new BibNode("bf","Instance").appendAttribute("rdf:about",about);
        if(bibframeItems != null && bibframeItems.size() > 0){
            bibframeItems.forEach(e->{
                instance.appendChild(e.toBibFrameNode());
            });
        }
        return instance;
    }
}
