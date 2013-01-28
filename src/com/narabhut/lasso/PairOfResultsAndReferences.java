package com.narabhut.lasso;

import java.util.ArrayList;

import android.util.Log;

public class PairOfResultsAndReferences {
	
	private ArrayList<String> resultList;
	private ArrayList<String> referenceList;
	
	public PairOfResultsAndReferences(ArrayList<String> resultList, ArrayList<String> referenceList){
		this.resultList = resultList;
		this.referenceList = referenceList;
	}
	
	public PairOfResultsAndReferences(){
		resultList = null;
		referenceList = null;
	}
	
	public ArrayList<String> getResultList(){
		return resultList;
	}
	
	public void createNewLists(int length){
		resultList = new ArrayList<String>(length);
		referenceList = new ArrayList<String>(length);
	}
	
	public void setLists(String result, String reference){
		resultList.add(result);
		referenceList.add(reference);
	}
	
	public String getReference(String str){
		int position = resultList.indexOf(str);
		if (position==-1){
			return null;
		}
		Log.e("position number is ", ""+1);
		return referenceList.get(position);
		
	}
}
