package trajectory.statistic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MultiMap<K, V>{
	public Map<K, Collection<V>> map = new HashMap<>();
	
	public void putNoDistinct(K key, V value){
		if(map.get(key) == null){
			map.put(key, new ArrayList<V>());
		}
		map.get(key).add(value);
	}
	
	public void putDistinct(K key, V value){
		if(map.get(key) == null){
			map.put(key, new ArrayList<V>());
			map.get(key).add(value);
		}
		else if(!map.get(key).contains(value)){
			map.get(key).add(value);
		}
	}
	
	public Collection<V> get(Object key){
		return map.get(key);
	}
	
	public Set<K> keySet(){
		return map.keySet();
	}
	
	public Collection<Collection<V>> values(){
		return map.values();
	}
	
	public boolean containsKey(Object key){
		return map.containsKey(key);
	}
	
	public Collection<V> remove(Object key){
		return map.remove(key);
	}
	
	public int size(){
		int count = 0;
		for(Collection<V> value: map.values()){
			count = count + value.size();
		}
		return count;
	}
	
	public boolean isEmpty(){
		return map.isEmpty();
	}
	
	public void clear(){
		map.clear();
	}
	
	public boolean remove(K key, V value){
		if(map.get(key) != null){
			return map.get(key).remove(value);
		}
		
		return false;
	}
	
	public boolean replace(K key, V previousVal, V newVal){
		if(map.get(key) != null){
			if(map.get(key).remove(previousVal))
				return map.get(key).add(newVal);					
		}
		return false;
	}
}
