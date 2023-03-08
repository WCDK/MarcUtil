package com.wcdk.marcutil.Bibframe;

import com.wcdk.marcutil.Bibframe.instance.Instance;
import com.wcdk.marcutil.Bibframe.work.Work;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Bibframe {
    Map<String,String> nameSpace = new HashMap<>();
    Work work;
    Instance instance;

    public BibNode toBibFrameNode(){
        BibNode bibf = new BibNode("rdf","RDF");
//        bibf.appendAttribute("xmlns:rdf",rdf);
//        bibf.appendAttribute("xmlns:rdfs",rdfs);
//        bibf.appendAttribute("xmlns:bf",bf);
//        bibf.appendAttribute("xmlns:bflc",bflc);
//        bibf.appendAttribute("xmlns:madsrdf",madsrdf);
        bibf.setNameSpace(nameSpace);
        if(work != null ){
            BibNode workNode = work.toBibFrameNode();
            bibf.appendChild(workNode);
            if(instance != null){
                bibf.appendChild(instance.toBibFrameNode());
                BibNode hasInstance = new BibNode("bf","hasInstance").appendAttribute("rdf:resource",instance.getAbout());
                workNode.appendChild(hasInstance);
            }
        }

        return bibf;
    }


    public Map<String,String> appendNameSpace(String prefix, String uri){
        this.nameSpace.put(prefix,uri);
        return this.nameSpace;
    }
}
