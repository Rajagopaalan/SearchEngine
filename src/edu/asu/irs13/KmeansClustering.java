package edu.asu.irs13;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class KmeansClustering {
	public void computeClusters(String str, ArrayList<Integer> docslist,HashMap<Integer,Float> Sorted_idf,int k,HashMap<Integer,HashMap<String,Double>> documentindex){

		HashMap<Integer,Integer> Centeroids = new HashMap<Integer,Integer>();	                         //Initialize the data structures
		HashMap<Integer,ArrayList<Integer>> Clusterindex = new HashMap<Integer,ArrayList<Integer>> ();	
		HashMap<Integer,ArrayList<Integer>> OldClusterindex = new HashMap<Integer,ArrayList<Integer>> ();	
		ArrayList<Integer> randomnos = new ArrayList<Integer>();	
		Random randomcenteroid = new Random();
		int N =50;

		for(int x=0;x<k;x++){randomnos.add(randomcenteroid.nextInt(N));}                             	//Randomly choosing the Centroid documents
		System.out.println("documents choosesn randonly");

		for(int x=0;x<randomnos.size();x++){
			Centeroids.put(x,docslist.get(randomnos.get(x)));		
		}	
		System.out.println("randomnos"+randomnos);
																								/* Adding Centroid to Cluster*/

		for(int x=0;x<Centeroids.size();x++){
			ArrayList<Integer> clustertemp = new ArrayList<Integer>();
			clustertemp.add(Centeroids.get(x)) ;
			Clusterindex.put(x, clustertemp);
		}	

		System.out.println(docslist);
		System.out.println(Centeroids);
		System.out.println("cluindrdrd"+Clusterindex);

		for(int x=0;x<docslist.size();x++){
			int maxcenteroidno =-1;
			double currentsimilarity =0;
			for(int y=0;y<Centeroids.size();y++){
				double currsim=computesimilarity(docslist.get(x),Centeroids.get(y),documentindex);
				if(currsim>=currentsimilarity){maxcenteroidno = y ;}
				//System.out.println(currentsimilarity + " " + currsim) ;
				currentsimilarity = currsim ;	

			}
			System.out.println(maxcenteroidno) ;
			Clusterindex.get(maxcenteroidno).add(docslist.get(x)) ;                //Allocating document to closest Centroid

			//	break;
		}
		System.out.println(Clusterindex);   										//End of iteration 1

		/*computer Avergae of Clusters*/


		HashMap<Integer,HashMap<String,Double>> Clusteraverage = new 	HashMap<Integer,HashMap<String,Double>>(); 
		HashMap<Integer,HashMap<String,Double>> Clusteraveragebackup = new 	HashMap<Integer,HashMap<String,Double>>(); 
		ArrayList<Integer> newcluster = new ArrayList<Integer>();                                                 
		int newclusterindex=0;
		for(int x=0;x<k;x++){
			newclusterindex=newclusterindex-1;
			newcluster.add(newclusterindex);
		}
		for(int x=0;x<Clusterindex.size();x++){

			HashMap<String,Double> avgcenteroid = getaverage(Clusterindex.get(x), documentindex);		
			Clusteraverage.put(newcluster.get(x), avgcenteroid) ;
		}

		System.out.println("newclusters" + Clusteraverage);
		Centeroids.clear();
		Set<Integer> newclusters = Clusteraverage.keySet() ;
		System.out.println("newclusters"+newclusters);

		Iterator<Integer> newclus = newclusters.iterator();
		int newclusterkey=0;
		while(newclus.hasNext()) {
			Centeroids.put(newclusterkey, newclus.next()) ;
			newclusterkey++;
		} 

		for(int y=0;y<newclusters.size();y++){
			documentindex.put(Centeroids.get(y), Clusteraverage.get(Centeroids.get(y))) ;
		}


		for(int x=0;x<Clusterindex.size();x++){
			Clusterindex.get(x).add(Centeroids.get(x)) ;
		}

		System.out.println("newCenteroids"+Centeroids);
		System.out.println("added centeroids"+Clusterindex);
		System.out.println("------------------------------------");

		System.out.println("Cluster after interation 2");

		Set<Integer> CurrentClusterkeys = Clusterindex.keySet() ;
		Iterator<Integer> itrcurrentclusterkeys = CurrentClusterkeys.iterator() ;
		while(itrcurrentclusterkeys.hasNext()){
			Integer keytemp = itrcurrentclusterkeys.next();
			OldClusterindex.put(keytemp, Clusterindex.get(keytemp)) ;
		}


		Clusterindex.clear();
		int itrstart = 0;
		while(true){
																						//Iteration of K means algorithms

			itrstart++;
			Clusterindex.clear();
			for(int x=0;x<Centeroids.size();x++){
				ArrayList<Integer> clustertemp = new ArrayList<Integer>();
				clustertemp.add(Centeroids.get(x)) ;
				Clusterindex.put(x, clustertemp);
			}
			
			for(int x=0;x<docslist.size();x++){
				int maxcenteroidno =-1;
				double currentsimilarity =0;
				for(int y=0;y<Centeroids.size();y++){
					double currsim=computesimilarity(docslist.get(x),Centeroids.get(y),documentindex);
					if(currsim>=currentsimilarity){maxcenteroidno = y ;}
					currentsimilarity = currsim ;		
				}
				Clusterindex.get(maxcenteroidno).add(docslist.get(x)) ;
			}

		
			Clusteraveragebackup.clear(); 
			Set<Integer> keyint = Clusteraverage.keySet() ;
			Iterator<Integer> keyitr = keyint.iterator() ;
			while(keyitr.hasNext()){
				Integer tempite = keyitr.next();
				Clusteraveragebackup.put(tempite, Clusteraverage.get(tempite)) ;
			}
		


			Clusteraverage.clear();
			System.out.println(Clusteraveragebackup);
			newcluster.clear();
			newclusterindex=0;//
			for(int x=0;x<k;x++){
				newclusterindex=newclusterindex-1;
				newcluster.add(newclusterindex);
			}
			for(int x=0;x<Clusterindex.size();x++){

				HashMap<String,Double> avgcenteroid =	getaverage(Clusterindex.get(x), documentindex);

				Clusteraverage.put(newcluster.get(x), avgcenteroid) ;
			}


			Centeroids.clear();
			newclusters = Clusteraverage.keySet() ; //
			newclus = newclusters.iterator();  //
			newclusterkey=0;  //
			while(newclus.hasNext()) {
				Centeroids.put(newclusterkey, newclus.next()) ;
				newclusterkey++;
			} 

			for(int y=0;y<newclusters.size();y++){
				//documentindex.put(Centeroids.get(y), Clusteraverage.get(Centeroids.get(y))) ;
				documentindex.remove(Centeroids.get(y)) ;
				documentindex.put(Centeroids.get(y), Clusteraverage.get(Centeroids.get(y))); 
			}



			Set<Integer> Currentiterationkeys = Clusterindex.keySet() ;
			Iterator<Integer> iterationclusterkeys = Currentiterationkeys.iterator() ;
			if(OldClusterindex.equals(Clusterindex)){break;}
			OldClusterindex.clear();                                                                                //Backup of current cluster

			while(iterationclusterkeys.hasNext()){
				Integer keytemp = iterationclusterkeys.next();
				OldClusterindex.put(keytemp, Clusterindex.get(keytemp)) ;
			}

			System.out.println("Current clusters"+Clusterindex);
			System.out.println("iterations"+itrstart);
			
		}
		System.out.println("done final Cluster"+Clusterindex);
		System.out.println("iterations"+itrstart);

		printSummary(Clusteraveragebackup);

	}

																										//Compute Cosine Similarity between vectors
	public double computesimilarity(Integer doc1,Integer doc2,HashMap<Integer, HashMap<String, Double>> documentindex){
		double distance=0 ;
		double cosine_similarity = 0;
		double dot_product = 0 ;
		double Norm1= 0 ;
		double Norm2 =0 ;

		HashMap<String, Double> doc1terms = documentindex.get(doc1) ;	
		HashMap<String, Double> doc2terms = documentindex.get(doc2) ;

		Set<String> doc1keys= doc1terms.keySet();	                                                        //Forming Rootset of all the keys in two documents
		Set<String> doc2keys=doc2terms.keySet() ;
		Set<String>  Union = new HashSet<String>();
		Union.addAll(doc1keys) ;
		Union.addAll(doc2keys) ;	
		Iterator<String> itr = Union.iterator();

		while(itr.hasNext()){
			String itrtemp = itr.next() ;
			double doc1count=0;
			double doc2count=0;
			if(doc1terms.get(itrtemp)==null){doc1count = 0;}	
			else{doc1count=doc1terms.get(itrtemp);}
			if(doc2terms.get(itrtemp)==null){doc2count = 0;}	
			else{doc2count=doc2terms.get(itrtemp);}	
			double sqrdist =  Math.pow((doc1count-doc2count), 2)  ;
			distance = distance + sqrdist ;		

			Norm1=  Norm1  + Math.pow(doc1count,2);
			Norm2= Norm2 +Math.pow(doc2count,2) ;

			dot_product =dot_product + doc1count*doc2count ;


		}
		distance =Math.sqrt(distance) ;
		Norm1 = Math.sqrt(Norm1) ;
		Norm2 = Math.sqrt(Norm2);

		cosine_similarity =  dot_product /(Norm1*Norm2) ;

		
		return cosine_similarity ;
	}

																													//Compute Average of Cluster

	public HashMap<String,Double>  getaverage(ArrayList<Integer> docslist, HashMap<Integer, HashMap<String, Double>> documentindex){

		HashMap<String,Double> avgdoc=new  HashMap<String,Double>();
		Set<String> dockeys = new 	HashSet<String>();
		Set<String>  Union = new HashSet<String>();	
		for(int x=0;x<docslist.size();x++){
			Union.addAll(documentindex.get(docslist.get(x)).keySet()) ;	
		}

		Iterator<String> itrn = Union.iterator() ;
		while(itrn.hasNext()){
			double total=0 ;
			String term =itrn.next(); 
			for(int x=0;x<docslist.size();x++){
				double termcount = 0;
				if(documentindex.get(docslist.get(x)).get(term)==null){
					termcount=0;
				}  
				else{termcount=documentindex.get(docslist.get(x)).get(term);}
				total = total + termcount ;
			}
			double avg  = total / docslist.size() ;
			avgdoc.put(term,avg) ;
		}

		return avgdoc;}


	public void printSummary(HashMap<Integer,HashMap<String,Double>> Centeroids){               //Generating summary for each clusters

		for (int x=-1; x>=(-Centeroids.size()); x--) {
			HashMap<String,Double> summary = (HashMap<String, Double>) sort(Centeroids.get(x));

			System.out.println("Summary Cluster-" + x);
			int count=0;
			for(Map.Entry<String, Double> entry:summary.entrySet())
			{
				if(count<50){
					String termsummary = entry.getKey();
					if(termsummary.length()>=2 || !termsummary.matches("-?\\d+(\\.\\d+)?") || !termsummary.contains(".")|| !termsummary.contains(",") || !termsummary.contains(";")){    


						System.out.println(entry.getKey());}

				}
				count++;
			}
		}

	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sort( Map<K, V> map )
	{
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>( map.entrySet() );
		Collections.sort( list, new Comparator<Map.Entry<K, V>>()
		{
			public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
			{
				return (o2.getValue()).compareTo( o1.getValue() );
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list)
		{
			result.put( entry.getKey(), entry.getValue() );
		}
		return result;
	}

}
