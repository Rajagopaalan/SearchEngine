package edu.asu.irs13;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

class documentmatrix{
	int docid;
	double rankvalue;
	
}

public class pageRankalgo {	
	int total_docs;
	double c=0.4;
	LinkAnalysis LA = new LinkAnalysis();	
	public void getresult(int maxDoc,String query,HashMap<Integer ,Float> idf,double w) {		
		total_docs=maxDoc ;
		ArrayList<documentmatrix> prdoc = pagerankcalculate() ;		
  Collections.sort(prdoc, new ArrayCompare());
	   for(int x=0;x<10;x++){System.out.println(prdoc.get(x).docid +" -"+ prdoc.get(x).rankvalue);}
	}
	
	
	public  ArrayList<documentmatrix> pagerankcalculate(){
				ArrayList<documentmatrix> pagerank = new ArrayList<documentmatrix>();
			for(int x=0;x<total_docs;x++){
			documentmatrix docmat= new documentmatrix();
			docmat.docid = x;
			docmat.rankvalue=(double)1/total_docs ;
			pagerank.add(docmat) ;}		
		ArrayList<documentmatrix> sinknodes= new ArrayList<documentmatrix>();
		ArrayList<documentmatrix> newpageranknodes= new ArrayList<documentmatrix>();
		ArrayList<documentmatrix> oldpageranknodes= new ArrayList<documentmatrix>();
		
		
		for(int x=0;x<total_docs;x++){
			documentmatrix docmat= new documentmatrix();
			docmat.docid = x;
			docmat.rankvalue=(double)1/total_docs ;
			newpageranknodes.add(docmat) ;	}
			
		
		
		
		for(int y=0;y<total_docs;y++){
			int ol[]=LA.getLinks(y);
			if(ol.length==0){
		
				sinknodes.add(pagerank.get(y));}
			}		
		int itr=0;
			
			while(true){
				itr++;
			
				//System.out.println("govind");			
			int totalsinkPR=0;			
			for(int x=0;x<sinknodes.size();x++){
				totalsinkPR+=sinknodes.get(x).rankvalue;
			}			
			for(int i=0;i<total_docs;i++){
				double newpr=(double)(1-c)/total_docs;
				newpr+=c*totalsinkPR/total_docs ;
				int il[] = LA.getCitations(i);
				  for(int k=0;k<il.length;k++){
					  newpr+=c*newpageranknodes.get(k).rankvalue/il.length ;
				  }
				  newpageranknodes.get(i).rankvalue = newpr ;
			}
			if(oldpageranknodes.equals(newpageranknodes) ){
				System.out.println("breaking");
				break;}
			  oldpageranknodes.clear();
			 for(int x=0;x<newpageranknodes.size();x++){
		    	  oldpageranknodes.add(newpageranknodes.get(x));};	
		    		  
			}
		
		
					return newpageranknodes  ;}
		
	
	
}

class ArrayCompare implements Comparator<documentmatrix>
{

	


	@Override
	public int compare(documentmatrix x, documentmatrix y) {
		// TODO Auto-generated method stub
		if(x.rankvalue == y.rankvalue) 
			return 0;
		 else if(x.rankvalue < y.rankvalue) 
			return 1;
		 else 
			return -1;
		
	}
}
