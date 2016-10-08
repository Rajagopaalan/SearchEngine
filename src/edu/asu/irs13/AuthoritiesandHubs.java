package edu.asu.irs13;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AuthoritiesandHubs {



	public  <T> void authorityhub(String query,ArrayList<Integer> topdocstfidf){

		ArrayList<Integer> rootset = new ArrayList<Integer> ();                                      //Initalizing the arrays for storing hubs and auths values
		ArrayList<Integer> baseset = new ArrayList<Integer> ();

		ArrayList<Integer> TopHubs = new ArrayList<Integer>();
		ArrayList<Integer> TopAuths = new ArrayList<Integer>();

		ArrayList<Integer> authvector = new ArrayList<Integer>();
		ArrayList<Integer> hubvector= new ArrayList<Integer>();

		ArrayList<HashMap<Integer,ArrayList<Integer>>> adjmatrix=new  ArrayList<HashMap<Integer,ArrayList<Integer>>>();
		ArrayList<ArrayList<Integer>> adjmatrixtemp=new  ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> adjmatrixtranspose = new ArrayList<ArrayList<Integer>>();

		ArrayList<Integer> docid= new ArrayList<Integer>();


		rootset=topdocstfidf;
		LinkAnalysis linkA = new LinkAnalysis();
		long rootsetcreationtime=System.currentTimeMillis();

		for(int x=0;x<rootset.size();x++){                                                 //   Creating baseset with TF-IDF top N docs,inlinks and outlinks
			if(!baseset.contains(rootset.get(x))){
				baseset.add(rootset.get(x));
				int[] inlinks = linkA.getLinks(rootset.get(x)) ;
				int[] outlinks = linkA.getCitations(rootset.get(x)) ;

				for(int s=0;s<outlinks.length;s++){
					Integer ol=outlinks[s];					
					if (!baseset.contains(ol)) {
						baseset.add(ol);		}}
				for(int s=0;s<inlinks.length;s++){
					Integer il=inlinks[s];					
					if (!baseset.contains(il)) {
						baseset.add(il);		}}


			}}
		long rootsetendtime=System.currentTimeMillis();

		long adjmatstart=System.currentTimeMillis();

		for(int x=0;x<baseset.size();x++){                                    //creating adjacency matrix
			Integer basetemp=baseset.get(x);
			int oul[]=linkA.getCitations(basetemp);

			HashMap<Integer,ArrayList<Integer>> row = new HashMap<Integer,ArrayList<Integer>>();
			ArrayList<Integer> rowvector=new ArrayList<Integer>();
			Arrays.sort(oul);
			for(int i=0;i<baseset.size();i++){
				if( Arrays.binarySearch(oul, baseset.get(i))>-1){rowvector.add(1);}
				else{rowvector.add(0);}				 
			}
			adjmatrixtemp.add(rowvector);
			row.put(basetemp, rowvector);
			adjmatrix.add(row);
			docid.add(basetemp);
		}


		long adjmatend=System.currentTimeMillis();	
		int sizecolumn=adjmatrix.size(); 
		int sizerow=adjmatrix.size();

		for(int x=0;x<sizecolumn;x++){
			ArrayList<Integer> adjtransposetemp =new ArrayList<Integer>();
			for(int y=0;y<sizerow;y++){	

				adjtransposetemp.add(adjmatrixtemp.get(y).get(x));
			}

			adjmatrixtranspose.add(adjtransposetemp);                                     //Transpose of adjacency matrix
		}

		ArrayList<Integer> authlinkvector= new ArrayList<Integer>();                   //calculating ao and ho
		ArrayList<Integer> hublinkvector= new ArrayList<Integer>();
		for(int y=0;y<sizerow;y++){authvector.add(1);
		hubvector.add(1); }
		for(int x=0;x<sizerow;x++){
			Integer sumtemp=0;
			for(int y=0;y<sizecolumn;y++){

				sumtemp=sumtemp + adjmatrixtemp.get(x).get(y)*authvector.get(y);
			}
			authlinkvector.add(sumtemp);
		}

		for(int x=0;x<sizerow;x++){
			Integer sumtemp=0;
			for(int y=0;y<sizecolumn;y++){

				sumtemp=sumtemp + adjmatrixtemp.get(x).get(y)*authlinkvector.get(y);
			}
			hublinkvector.add(sumtemp);
		}

		double auth_norm=0;
		double hub_norm=0;
		for(int x=0;x<authlinkvector.size();x++){

			auth_norm = auth_norm + Math.pow(authlinkvector.get(x),2);
		}
		auth_norm=Math.sqrt(auth_norm);

		ArrayList<Double> normalized_auth =  new ArrayList<Double>();
		ArrayList<Double> normalized_hub =  new ArrayList<Double>();
		for(int x=0;x<authlinkvector.size();x++){
			normalized_auth.add(authlinkvector.get(x)/auth_norm);                       
		}
		System.out.println( " ");

		for(int x=0;x<hublinkvector.size();x++){

			hub_norm = hub_norm + Math.pow(hublinkvector.get(x),2);
		}
		hub_norm=Math.sqrt(hub_norm);


		for(int x=0;x<hublinkvector.size();x++){
			normalized_hub.add(hublinkvector.get(x)/hub_norm);                                  //Normalizing hub values in every power iteration
		}


		ArrayList<ArrayList<Integer>> product = new ArrayList<ArrayList<Integer>>() ;

		for(int x=0;x<sizerow;x++){		                                                          //calculating the product of A*transpose(A)
			ArrayList<Integer> arrtemp= new ArrayList<Integer>();

			for(int y=0;y<sizecolumn;y++){
				int sumtemp=0;
				int sumtemp1=0;
				for(int k=0;k<sizerow;k++){
					sumtemp=sumtemp+adjmatrixtemp.get(x).get(k)*adjmatrixtemp.get(y).get(k);

				}
				arrtemp.add(sumtemp);

			}
			product.add(arrtemp);                                                  


		}


		ArrayList<Double> authlinkvectorcurrent= new ArrayList<Double>();		                         //initialing arrays to store current and previous values of iterations for comparison inorder to check if it has acheived the stopping criteria
		ArrayList<Double> authlinkvectorprevious= new ArrayList<Double>();
		ArrayList<Double> hublinkvectorcurrent= new ArrayList<Double>();
		ArrayList<Double> hublinkvectorprevious= new ArrayList<Double>();
		ArrayList<Double> hublinkvectorcurrfinal= new ArrayList<Double>();
		ArrayList<Double> authlinkvectorcurrfinal= new ArrayList<Double>();	
		authlinkvectorcurrent=normalized_auth;
		hublinkvectorcurrent=normalized_hub;
		for(int x=0;x< hublinkvectorcurrent.size();x++){hublinkvectorprevious.add(hublinkvectorcurrent.get(x));}
		int t=0;
		ArrayList<Double> normalized_auth_current =  new ArrayList<Double>();
		ArrayList<Double> normalized_hub_current =  new ArrayList<Double>();
		boolean check=true;

		long convergestart=System.currentTimeMillis();
		long startauth=0;
		long starthub=0;
		long endauth = 0;
		long endhub=0 ;

		while(check){

			startauth = System.currentTimeMillis();
			for(int x=0;x<sizerow;x++){
				Double sumtemp=0.0;
				for(int y=0;y<sizecolumn;y++){


					sumtemp=sumtemp + (adjmatrixtranspose.get(x).get(y))*hublinkvectorprevious.get(y);        //calculating vector A
				}
				authlinkvectorcurrfinal.add(sumtemp);
			}
			double auth_norm_current=0;

			for(int x=0;x<authlinkvectorcurrent.size();x++){
				auth_norm_current = auth_norm_current + Math.pow(authlinkvectorcurrfinal.get(x),2);
			}
			auth_norm_current=Math.sqrt(auth_norm_current);

			endauth = System.currentTimeMillis();
			starthub  = System.currentTimeMillis();

			for(int x=0;x<sizerow;x++){
				Double sumtemp=0.0;
				for(int y=0;y<sizecolumn;y++){
					sumtemp=sumtemp + (adjmatrixtemp.get(x).get(y))*authlinkvectorcurrfinal.get(y) ;
				}
				hublinkvectorcurrfinal.add(sumtemp);                                                   //calculating vector h
			}


			for(int x=0;x<authlinkvectorcurrfinal.size();x++){
				normalized_auth_current.add(authlinkvectorcurrfinal.get(x)/auth_norm_current);  //Normalizing authorities values in every power iteration
			}

			double hub_norm_current=0;

			for(int x=0;x<hublinkvectorcurrent.size();x++){

				hub_norm_current = hub_norm_current + Math.pow(hublinkvectorcurrfinal.get(x),2);
			}
			hub_norm_current=Math.sqrt(hub_norm_current);


			for(int x=0;x<hublinkvectorcurrent.size();x++){
				normalized_hub_current.add(hublinkvectorcurrfinal.get(x)/hub_norm_current);  //Normalizing hub values in every power iteration


			}
			endhub = System.currentTimeMillis();
			if(authlinkvectorprevious.equals(normalized_auth_current) && hublinkvectorprevious.equals(normalized_hub_current)){check=false;break;}
			//stopping criteria

			authlinkvectorcurrent.clear();
			authlinkvectorprevious.clear();
			hublinkvectorcurrent.clear();
			hublinkvectorprevious.clear();
			for(int x=0;x<normalized_auth_current.size();x++){
				authlinkvectorcurrent.add(normalized_auth_current.get(x));
				authlinkvectorprevious.add(normalized_auth_current.get(x)) ;
			}

			for(int x=0;x<normalized_hub_current.size();x++){
				hublinkvectorcurrent.add(normalized_hub_current.get(x));
				hublinkvectorprevious.add(normalized_hub_current.get(x)) ;
			}


			normalized_auth_current.clear();
			normalized_hub_current.clear();
			authlinkvectorcurrfinal.clear();
			hublinkvectorcurrfinal.clear();

			t++;
		}

		long convergeend=System.currentTimeMillis();

		HashMap<Integer,Double> topauth = new HashMap<Integer,Double>();
		HashMap<Integer,Double> tophub = new HashMap<Integer,Double>();

		for(int x=0;x<authlinkvectorcurrent.size();x++){
			topauth.put(docid.get(x), authlinkvectorcurrent.get(x));
			tophub.put(docid.get(x), hublinkvectorcurrent.get(x)) ;
		}
		HashMap<Integer ,Double> sorted_topauth = (HashMap<Integer,Double>) sort(topauth);                       //Sorting authorities and hub values
		HashMap<Integer ,Double> sorted_tophub = (HashMap<Integer,Double>) sort(tophub);
		System.out.println("No of iterations"+t);

		long diffauth=startauth-endauth ;
		long diffhub=starthub-endhub ;
		long diffconverge = convergestart-convergeend;
		long  rootsettime = rootsetcreationtime -rootsetendtime ;
		long adjmatrixtime = adjmatstart-adjmatend ;

		System.out.println("TOp 10 auths");

		int counthub=0;
		int countauth=0;

		for(Map.Entry<Integer, Double> entry:sorted_topauth.entrySet())                                    //Calculating Top 10 authorities
		{
			if(countauth<10){

				System.out.println(entry.getKey()+ " - "+entry.getValue());

			}
			countauth++;
		}
		System.out.println("Top 10 hubs");                                                           //Calculating Top 10 hubs
		for(Map.Entry<Integer, Double> entry1:sorted_tophub.entrySet())
		{
			if(counthub<10){

				System.out.println(entry1.getKey()+ " - " +entry1.getValue());

			}
			counthub++;
		}

		System.out.println("----------");                                   //calculating time taken during every phase of the algorithm
		System.out.println("Time taken to calculate authorities" + diffauth);
		System.out.println("Time taken to calculate hubs" +diffhub );
		System.out.println("Time taken to calculate adjacency matrix" + adjmatrixtime);
		System.out.println("Time taken to frame rootset" + rootsettime);
		System.out.println("Time taken for convergence " + diffconverge);
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


	public static void main(String args[]){


	}

}
