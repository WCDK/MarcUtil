package com.wcdk.marcutil;




import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * <p>可重复键数据集合 Key-Value(请勿更改)</p>
 * @Description:
 * @Author: wench
 **/
public class RepeatKeyMap<K,V>extends AbstractMap<K,V> implements Map<K,V>, Serializable{
    static final int DEFAULT_LENGTH = 1 << 3;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final long serialVersionUID = 2261595087579278012L;
    transient Node<K,V>[] nodes;
    transient int length;

    public RepeatKeyMap(){
        nodes = new Node[DEFAULT_LENGTH];
    }

    @Override
    public boolean isEmpty() {
        return length == 0;
    }
    private void resize(){
        Node<K,V>[] temp1;
        temp1 = nodes;
        if(length+1 == nodes.length * DEFAULT_LOAD_FACTOR){
            int newSize = nodes.length << 1;
            nodes = new Node[newSize];
            for(int i = 0; i < this.length; i++){
                nodes[i] = temp1[i];
            }
        }
    }

    public V removeLastValue(){

        Node<K,V>[] temp1 = nodes;
        nodes = new Node[temp1.length];
        int f2 = 0;
        int cur = length;
        V v = null;
        for(int i = 0;i < cur;i++){
            Node<K, V> node = temp1[i];
            if(i == cur -1){
                length--;
                v = node.value;
                continue;
            }
            nodes[f2] = node;
            f2++;
        }
        return v;
    }

    public V getLastValue(){
        return nodes[length-1].value;
    }
    public K getLastKey(){
        return nodes[length-1].key;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public V put(K k,V v){
        resize();
        nodes[length] = new Node<>(k,v);
        addLength();
        return v;
    }
    /**
     * <p>插入到 第n个同名key之后</p>
     * @Description:
     * @Author: wench
     **/

    public V put(K key,V value,int index){
        resize();
        Node<K,V>[] temp1 = nodes;
        nodes = new Node[temp1.length];

        AtomicInteger atomicInteger = new AtomicInteger(0);
        for(int i = 0;i < length;i++){
            Node<K, V> node = temp1[i];
            nodes[i] = node;
            if(node.key.equals(key) && index == atomicInteger.getAndAdd(1)){
                nodes[i+1] = new Node<>(key, value);
                for(int j = i+1;j<length+1;j++){
                    nodes[j+1] = temp1[j];
                }
                addLength();
                return value;
            }
        }

        return null;
    }

    public V putBefor(K key,V value,int index){
        resize();
        Node<K,V>[] temp1 = nodes;
        nodes = new Node[temp1.length];

        AtomicInteger atomicInteger = new AtomicInteger(0);
        for(int i = 0;i < length;i++){
            Node<K, V> node = temp1[i];
            nodes[i] = node;
            if(node.key.equals(key) && index == atomicInteger.getAndAdd(1)){
                nodes[i] = new Node<>(key, value);
                for(int j = i;j<length+1;j++){
                    nodes[j+1] = temp1[j];
                }
                addLength();
                return value;
            }
        }

        return null;
    }

    /**
     * <p>插入到指定位置</p>
     * @Description:
     * @Author: wench
     **/

    public V putToIndex(K key,V value,int index){
        resize();
        Node<K,V>[] temp1 = nodes;
        nodes = new Node[temp1.length];

        AtomicInteger atomicInteger = new AtomicInteger(0);
        for(int i = 0;i < length;i++){
            Node<K, V> node = temp1[i];
            nodes[i] = node;
            if(index == atomicInteger.getAndAdd(1)){
                nodes[i] = new Node<>(key, value);
                for(int j = i+1;j<length+1;j++){
                    nodes[j] = temp1[j-1];
                }
                addLength();
                return value;
            }
        }

        return null;
    }

    public void putAll(RepeatKeyMap<? extends K, ? extends V> m) {
        m.forEach((k,v)->{
            put(k,v);
        });
    }

    @Override
    public V remove(Object key){
        return remove(key,0);
    }

    public V remove(int index){
        index = index<=0?0:index;
        Node<K,V>[] temp1 = nodes;
        nodes = new Node[temp1.length];
        V v = null;
        int n = 0;
        for(int i =0;i < temp1.length;i++){
            if(i == index){
                v = temp1[i].value;
                length--;
                continue;
            }
            nodes[n] = temp1[i];
            n++;
        }
        return v;
    }

    public V remove(Object key,int index){

        Node<K,V>[] temp1 = nodes;
        nodes = new Node[temp1.length];
        int f1 = 1;
        int f2 = 0;
        int cur = length;
        V v = null;
        for(int i = 0;i < cur;i++){
            Node<K, V> node = temp1[i];
            if(node.key.equals(key) ){
                if(f1 == index){
                    f1++;
                    length--;
                    v = node.value;
                    continue;
                }
                f1++;
            }
            nodes[f2] = node;
            f2++;
        }
        return v;
    }

    public Collection<K> keys(){
        LinkedList list = new LinkedList();
        for(int i = 0; i < length;i++){
            list.add(nodes[i].key);
        }
        return  list;
    }

    @Override
    public Collection<V> values(){
        LinkedList list = new LinkedList();
        for(int i = 0; i < length;i++){
            list.add(nodes[i].value);
        }
        return  list;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set set = new HashSet();
        for (int i = 0;i < length;i++) {
            set.add(nodes[i]);
        }

        return set;
    }

    @Override
    public int size(){
        return this.length;
    }

    @Override
    public V get(Object key){
        return get(key,0);
    }

    public V get(Object key,int index){

        AtomicInteger atomicInteger = new AtomicInteger(0);
        for(int i = 0;i < length;i++){
            Node<K, V> node = nodes[i];
            if(node.key.equals(key) && index == atomicInteger.getAndAdd(1)) {
                return node.value;
            }
        }
        return null;
    }

    public V replace(Object key,V value,int index){

        AtomicInteger atomicInteger = new AtomicInteger(0);
        for(int i = 0;i < length;i++){
            Node<K, V> node = nodes[i];
            if(node.key.equals(key) && index == atomicInteger.getAndAdd(1)) {
                node.value = value;
            }
        }
        return value;
    }

    public List<V> getAll(Object key){
        List<V> list = new ArrayList<>();
        for(int i = 0;i < length;i++){
            Node<K, V> node = nodes[i];
            if(node.key.equals(key)) {
                list.add(node.value);
            }
        }
        return list;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        for (Node<K,V> node : nodes) {
            K k;
            V v;
            if (node == null) {
                continue;
            }
            try {
                k = node.key;
                v = node.value;
            } catch(IllegalStateException ise) {
                // this usually means the entry is no longer in the map.
                throw new ConcurrentModificationException(ise);
            }
            action.accept(k, v);
        }
    }

    private void addLength(){
        length++;
    }

    @Override
    public String toString() {
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i <length;i++){
            Node<K, V> node = nodes[i];
            stringBuffer.append(node.key+"="+node.value+", ");

        }
        stringBuffer.replace(stringBuffer.length()-2,stringBuffer.length(),"}");
        return "{" +stringBuffer.toString();
    }

    static class Node <K,V> implements Map.Entry<K,V> {
        K key;
        V value;
        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
        @Override
        public final K getKey()        { return key; }
        @Override
        public final V getValue()      { return value; }

        @Override
        public V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        @Override
        public final String toString() { return key + "=" + value; }

        public final V set(Node<K,V> node){
            this.key = node.getKey();
            this.value = node.getValue();
            return this.value;
        }
    }
    /**
     * 浅拷贝 慎用
     * **/
    public RepeatKeyMap<K,V> copy(){
        RepeatKeyMap<K,V> repeatKeyMap = new RepeatKeyMap<>();
        if(length <= 0){
            return repeatKeyMap;
        }
        List<K> ks = this.keys().stream().collect(Collectors.toList());
        List<V> vs = this.values().stream().collect(Collectors.toList());
        for(int i = 0;i < length;i++){
            repeatKeyMap.put(ks.get(i),vs.get(i));
        }
        return repeatKeyMap;
    }
}
