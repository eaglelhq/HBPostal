package com.yitao.db;

public class WhereBuidler {
	private String[] argsKeys;
	private String[] argsValue;
	public WhereBuidler(String[] keys,String[] values){
		this.argsKeys = keys;
		this.argsValue = values;
	}
	public String[] getArgsKeys() {
		return argsKeys;
	}

	public void setArgsKeys(String[] argsKeys) {
		this.argsKeys = argsKeys;
	}

	public String[] getArgsValue() {
		return argsValue;
	}

	public void setArgsValue(String[] argsValue) {
		this.argsValue = argsValue;
	}
	public String ModelToString(){
		String whereArgs = "";
		if(argsKeys!=null && argsKeys.length !=0){
			for(int i = 0;i<argsKeys.length;i++){
				whereArgs += " and "+argsKeys[i]+"='"+argsValue[i]+"'";
			}
		}
		return whereArgs;
	}
}
