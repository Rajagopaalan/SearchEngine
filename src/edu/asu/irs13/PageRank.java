package edu.asu.irs13;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

class docMatrix{
	int docid;
	double rankvalue;
}
public class PageRank {
	
	
	double c=0.60;
	int total_docs;
	LinkAnalysis LA = new LinkAnalysis();


	public void getresult(int maxDoc,String query,HashMap<Integer ,Float> idf,double w) {
		// TODO Auto-generated method stub
		total_docs=maxDoc ;

		System.out.println("start"+Runtime.getRuntime().freeMemory()) ;            				 //computing memory consumption 
		ArrayList<docMatrix> prank = pagerankprecompute();                    

		System.out.println("end"+Runtime.getRuntime().freeMemory()) ;
	
Collections.sort(prank,new ArrayCompare());                                                 	  //Ordering the page rank values                            

		ArrayList<docMatrix> res = new ArrayList<docMatrix>();
				for (int p=0; p<total_docs; p++) {
			
                   docMatrix dmatx= new docMatrix();
            
                   dmatx.docid = p ;
                   dmatx.rankvalue =0; 
                   res.add(dmatx) ;
              
				}
				
			
			for (int x : idf.keySet()) {
			
					res.get(x).rankvalue = w * prank.get(x).rankvalue + (1-w)*idf.get(x) ;                  //page rank with TF-IDF to fetch top N results
				}
				
	
			Collections.sort(res, new ArrayCompare());                                                //Sorting TOP N results for page rank
			
			System.out.println("Pagerank Top N results ");
		  	for (int x=0; x<10; x++) {
		  		System.out.println(res.get(x).docid + " - " + res.get(x).rankvalue );
		  	}
	}

	public ArrayList<docMatrix> pagerankprecompute(){      
		                                                                                         //computation of page rank algorithm
		 long start=System.currentTimeMillis();
		double k=(1-c)/total_docs ;
	
ArrayList<Double> pagerankprevious = new ArrayList<Double>();
	
ArrayList<docMatrix> pagerank =  new  ArrayList<docMatrix> ();
		
		for (int x=0; x<total_docs; x++) {

	
			docMatrix dmat = new docMatrix();
			 dmat.docid = x;
			 dmat.rankvalue = (double)1/total_docs ;
			pagerank.add(dmat) ;
			 pagerankprevious.add((double)1/total_docs);		}                           //Initializing all the page rank values to 1/N
		
int maxpagerankdoc=-1;
double maxPgrank = 0;
		int t=0;
		int m=100;
		while (true) {
			

			for (int x=0; x<total_docs; x++) {
				if (LA.getLinks(x).length == 0)                                                    //Handling of Sink nodes
				{			
					pagerank.get(x).rankvalue= 	pagerank.get(x).rankvalue + (double)pagerankprevious.get(x)/total_docs ;
				}
				else 
				{
					int[] ol = LA.getLinks(x);
					for (int y=0; y<ol.length; y++) {
						pagerank.get(y).rankvalue =	pagerank.get(x).rankvalue +  (double)pagerank.get(x).rankvalue/ol.length; 
					}}}
		
	

		for (int x=0; x<total_docs; x++) {                                                             //computing c*pgrank +(1-k)/N
					pagerank.get(x).rankvalue = c*pagerank.get(x).rankvalue + k ;
					if(maxPgrank < 	pagerank.get(x).rankvalue ){maxPgrank= pagerank.get(x).rankvalue ;System.out.println("kpo"+pagerank.get(x).docid) ;
					}
		}
	                       
		
		for (int x=0; x<total_docs; x++) { 
			
			pagerank.get(x).rankvalue= (double)pagerank.get(x).rankvalue/maxPgrank ;                   //Normalizing pagerank values
		}
		
		double diff = 0;
		for (int y=0; y< pagerankprevious.size(); y++)
		{
			diff =diff+ Math.pow(pagerank.get(y).rankvalue - pagerankprevious.get(y), 2);
		}
		if ( t==m  ||  Math.sqrt(diff) < 0.0010) {break;}                              //Stopping criteria
		else{
			for (int y=0; y< pagerankprevious.size(); y++) {
			
			pagerankprevious.set(y, pagerank.get(y).rankvalue); 	 
	
					}
			
		}
		t++;				

	}
	System.out.println("No of iteration"+t);
	 long end =System.currentTimeMillis();
	 long diff=start-end;
	 System.out.println("time" +diff) ;                                         //computing time of execution of pagerank computation algorithm
	 
		return pagerank;}
	
	
	
	public
	
	class ArrayCompare implements Comparator<docMatrix>                                   //comparator method to sort the array values
	{
	
		
	

		@Override
		public int compare(docMatrix x, docMatrix y) {
			// TODO Auto-generated method stub
			if(x.rankvalue == y.rankvalue) 
				return 0;
			 else if(x.rankvalue < y.rankvalue) 
				return 1;
			 else 
				return -1;
			
		}
	}


}
	