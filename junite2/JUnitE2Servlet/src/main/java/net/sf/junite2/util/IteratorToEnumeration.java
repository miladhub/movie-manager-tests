package net.sf.junite2.util;

import java.util.Enumeration;
import java.util.Iterator;

public class IteratorToEnumeration<E> implements Enumeration<E>{
	
	protected Iterator<E> iterator;
	
	public IteratorToEnumeration(Iterator<E> iterator){
		this.iterator = iterator;
	}

	public boolean hasMoreElements(){
		return iterator.hasNext();
	}

	public E nextElement(){
		return iterator.next();
	}

}
