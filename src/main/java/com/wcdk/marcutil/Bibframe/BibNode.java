package com.wcdk.marcutil.Bibframe;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.*;

@Data
@ToString
@NoArgsConstructor
public class BibNode {
    String prefix;
    String name;
    String value;
    Map<String,String> nameSpace = new HashMap<>();
    Map<String,String> attributes = new HashMap<>();
    List<BibNode> child = new ArrayList<>();

    public BibNode(String prefix,String name,String value){
        this.prefix = prefix;
        this.name = name;
        this.value = value;
    }

    public BibNode(String prefix,String name){
        this.prefix = prefix;
        this.name = name;
    }

    public BibNode appendChild(BibNode node,BibNode...nodes){
        child.add(node);
        if(nodes.length > 0){
            child.addAll(Arrays.asList(nodes));
        }
        return this;
    }
    public BibNode appendChild(List<BibNode> nodes){
        child.addAll(nodes);
        return this;
    }
    public BibNode appendAttribute(String key,String value){
        attributes.put(key,value);
        return this;
    }
    public BibNode appendAttribute(Map<String,String> attributes){
        this.attributes.putAll(attributes);
        return this;
    }

    public Map<String,String> appendNameSpace(String prefix,String uri){
        this.nameSpace.put(prefix,uri);
        return this.nameSpace;
    }
    public Map<String,String> appendNameSpace(Map<String,String> nameSpace){
        this.nameSpace.putAll(nameSpace);
        return this.nameSpace;
    }
}
