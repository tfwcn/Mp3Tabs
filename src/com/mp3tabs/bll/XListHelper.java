package com.mp3tabs.bll;

import java.util.List;

import com.mp3tabs.model.XFileInfo;

public class XListHelper<T> {
	public static abstract class XCheck<T>{
		public T oldObject;
		public XCheck(){};
		public XCheck(T oldObject){
			this.oldObject=oldObject;
		};
		public abstract boolean check(T t);
	}
	public T Find(List<T> list,XCheck<T> xCheck) {
		for (T t : list) {
			if(xCheck.check(t)){
				return t;
			}
		}
		return null;
	}
	public List<T> FindAll(List<T> list,XCheck<T> xCheck) throws IllegalAccessException, InstantiationException {
		List<T> newList=list.getClass().newInstance();
		for (T t : list) {
			if(xCheck.check(t)){
				newList.add(t);
			}
		}
		return newList;
	}
	public boolean Existed(List<T> list,XCheck<T> xCheck) {
		for (T t : list) {
			if(xCheck.check(t)){
				return true;
			}
		}
		return false;
	}
}
