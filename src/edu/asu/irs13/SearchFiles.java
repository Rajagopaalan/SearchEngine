package edu.asu.irs13;

/*import org.apache.lucene.index.*;



import org.apache.lucene.store.*;
import org.apache.lucene.document.*;*/
import org.apache.lucene.index.*;


import org.apache.lucene.store.*;
import org.apache.lucene.document.* ;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.Iterator;

public class SearchFiles {
	private static final HashMap<Integer, Integer> NULL = null;
	static ArrayList<Integer> final_idf = new ArrayList<Integer>() ;
	public static <T> void main(String[] args) throws Exception
	//	public 	HashMap<Integer,Float> getTopresults(String query)  throws Exception
	{
		// the IndexReader object is the main handle that will give you 
		// all the documents, terms and inverted index
		IndexReader r = IndexReader.open(FSDirectory.open(new File("index")));

		// You can figure out the number of documents using the maxDoc() function
		System.out.println("The number of documents in this index is: " + r.maxDoc());
		System.out.println("wait for a moment..TOP 10 documents are being selected using TF and TF-IDF retrevial techniques ") ;

		int i = 0;
		// You can find out all the terms that have been indexed using the terms() function
		TermEnum t = r.terms();
		/*	while(t.next())
		{
			// Since there are so many terms, let us try printing only term #100000-#100010
			if (i > 100000) System.out.println("["+i+"] " + t.term().text());
			if (++i > 100010) break;
		}
		 */
		// You can create your own query terms by calling the Term constructor, with the field 'contents'
		// In the following example, the query term is 'brute'
		Term te = new Term("contents", "grades");

		// You can also quickly find out the number of documents that have term t
		//System.out.println("Number of documents with the word 'brute' is: " + r.docFreq(te));

		// You can use the inverted index to find out all the documents that contain the term 'brute'
		//  by using the termDocs function
		TermDocs td = r.termDocs(te);

		ArrayList<Term> termi = new ArrayList<Term>();
		int s=0;
		TermEnum tt = r.terms();
		TermEnum ttt = r.terms();
		int total_no_docs = r.docFreq(te);
		HashMap<Term,Float> idf = new HashMap<Term,Float>();
		HashMap<String,Float> idfval = new HashMap<String,Float>();

		HashMap<String,HashMap<Integer,Float>> tf = new HashMap<String,HashMap<Integer,Float>>();
		HashMap<String,HashMap<Integer,Float>> tidf = new HashMap<String,HashMap<Integer,Float>>();
		HashMap<Integer,Float> docspecific = new HashMap<Integer,Float>() ;
		HashMap<Integer ,Float> dist_mod = new HashMap<Integer,Float>();
		HashMap<Integer ,Float> dist_value = new HashMap<Integer,Float>();
		HashMap<Integer ,Float> dist_value_str = new HashMap<Integer,Float>();

		HashMap<Integer ,Float> dist_value_str_idf = new HashMap<Integer,Float>();

		HashMap<Integer ,Float> dist_mod_idf = new HashMap<Integer,Float>();


		//	HashMap<Integer,HashMap<String,Integer>> doc_term_count = new HashMap<Integer,	HashMap<String,Integer>>();

		HashMap<Integer,HashMap<String,Double>> doc_term_count = new HashMap<Integer,	HashMap<String,Double>>();


		while(tt.next()){
			int start=0;
			int countdoc=0;
			float temp=0;
			float idftemp=0;
			//Term terms[s] = new Term("contents", t.term().text());
			termi.add(new Term("contents", tt.term().text()));

			TermDocs tds = r.termDocs(new Term("contents", tt.term().text()));
			if(r.docFreq(new Term("contents", tt.term().text())) !=0){
				float idfvalue= (float) Math.log(r.maxDoc()/r.docFreq(new Term("contents", tt.term().text())));
				//	System.out.println("idfvalue"+idfvalue);
				idfval.put(tt.term().text(), idfvalue);
				//idf.put(, )
			}
			while(tds.next())
			{
				countdoc+=1;

				if(dist_mod.get(tds.doc())==null){

					dist_mod_idf.put(tds.doc(), (float) Math.pow(tds.freq()*idfval.get(tt.term().text()),2)) ;
					dist_mod.put(tds.doc(),(float) Math.pow(tds.freq(),2));
					//	tf.put(tds.doc(), (float) tds.freq());

				}
				else{

					idftemp= dist_mod_idf.get(tds.doc()) +  (float) Math.pow(tds.freq()*idfval.get(tt.term().text()),2) ;
					temp=dist_mod.get(tds.doc())+(float) Math.pow(tds.freq(),2) ; 
					dist_mod.put(tds.doc(), temp);
					dist_mod_idf.put(tds.doc(), idftemp);


				}

				HashMap<String, Double>  termcount =new HashMap<String,Double>() ;

				termcount.put(tt.term().text(),(double) tds.freq()) ;

				//System.out.println(tds.doc()+"-"+tt.term().text()+"-"+tds.freq());
				if(doc_term_count.get(tds.doc())==null){
					doc_term_count.put(tds.doc(), termcount) ;}
				else{doc_term_count.get(tds.doc()).put(tt.term().text(),(double) tds.freq());}


				start++;
			}	


			if(total_no_docs!=0 && countdoc!=0){
				idf.put(new Term("contents", tt.term().text()), (float) Math.log(total_no_docs/countdoc)) ;}

		}

		for(Map.Entry<Integer, Float> entry : dist_mod.entrySet())
		{
			float temp = (float) Math.sqrt(entry.getValue());
			dist_mod.put(entry.getKey(), temp);

			float tempidf= (float) Math.sqrt(dist_mod_idf.get(entry.getKey()));
			dist_mod_idf.put(entry.getKey(), tempidf);
			//	System.out.println("Doc : "+entry.getKey()+" , Value : "+ entry.getValue());
		}
		//System.out.println(doc_term_count);
		// You can find the URL of the a specific document number using the document() function
		// For example, the URL for document number 14191 is:
		Document d = r.document(14191);
		String url = d.getFieldable("path").stringValue(); // the 'path' field of the Document object holds the URL
		//	System.out.println(url.replace("%%", "/"));


		// -------- Now let us use all of the functions above to make something useful --------
		// The following bit of code is a worked out example of how to get a bunch of documents
		// in response to a query and show them (without ranking them according to TF/IDF)
		while(ttt.next()){
			Term ter = new Term("contents", ttt.term().text());

			HashMap<String, HashMap<Integer,Integer>> hmap = new HashMap<String, HashMap<Integer,Integer>> ();

			TermDocs tdoc = r.termDocs(ter);
			HashMap<Integer,Integer> hdoc= new HashMap<Integer,Integer>();
			while(tdoc.next())
			{
				hdoc.put(tdoc.doc(), tdoc.freq());				 
			}
			hmap.put(ter.toString(), hdoc);
			try{
				Set<Integer> keys = hdoc.keySet();  //get all keys
				Set<String> keysstr = hmap.keySet(); 
			}
			catch(Exception e){e.printStackTrace();}
		}


		Scanner sc = new Scanner(System.in);
		String str = "";
		String querystr="";
		System.out.print("query> ");

		long start = System.currentTimeMillis();

		HashMap<String,Integer> hmp= new HashMap<String,Integer>();
		ArrayList<String> newarr= new ArrayList<String>();

		ArrayList<Integer> documentlist= new ArrayList<Integer>();

		HashMap<String,HashMap<Integer,Float>> doccount = new HashMap<String,HashMap<Integer,Float>>() ;
		String[] terms ;
		while(!(str = sc.nextLine()).equals("quit"))
		{				

			terms = str.split("\\s+");
			for(String word : terms)
			{			

				if(hmp.isEmpty() || hmp.get(word)==null){
					hmp.put(word, 1);
				}
				else{
					hmp.put(word, hmp.get(word)+1);
				}
			}
			float Nrsum=0;
			float idfNrsum=0;
			int nrsqr=0;	
			for(String word1 : terms){

				nrsqr=nrsqr+hmp.get(word1) *hmp.get(word1) ;	

			}
			for(String word : terms){

				HashMap<Integer,Float> countword = new HashMap<Integer,Float>();


				Term term = new Term("contents", word);
				TermDocs tdocs = r.termDocs(term);
				TermDocs tdocssort = r.termDocs(term);

				while(tdocs.next())
				{

					countword.put(tdocs.doc(), (float) tdocs.freq());
					documentlist.add(tdocs.doc());


				}
				doccount.put(word,countword );
			}
			for(int x=0;x<documentlist.size();x++){
				Nrsum=0;
				idfNrsum=0;
				for(String word : terms){
					if(doccount.get(word).get(documentlist.get(x)) !=null){

						idfNrsum+=hmp.get(word)*doccount.get(word).get(documentlist.get(x))*idfval.get(word) ;
					}
					else{Nrsum+=0;
					idfNrsum+=0;
					}

				}

				float distance_doc,distance_doc_idf;

				distance_doc_idf= (float) (idfNrsum/((Math.sqrt(nrsqr))*(dist_mod_idf.get((documentlist.get(x)))))) ;


				dist_value_str_idf.put(documentlist.get(x), distance_doc_idf);
			}


			HashMap<Integer ,Float> sorted_idf = (HashMap<Integer, Float>) sort(dist_value_str_idf);

			//	final_idf =sorted_idf ;

			int count=0;

			System.out.println("Top 10 docs tf-idf");

			int countidf=0;
			for(Map.Entry<Integer, Float> entry:sorted_idf.entrySet())
			{
				if(countidf<50){

					System.out.println(entry.getKey());
					final_idf.add(entry.getKey());
				}
				countidf++;
			}
			System.out.println("Time");
			System.out.println((((System.currentTimeMillis()-start))/1000F) );

			//		AuthoritiesandHubs ahub = new AuthoritiesandHubs();
			//	ahub.authorityhub(str,final_idf);

			//	PageRank p = new PageRank();


			KmeansClustering kc = new KmeansClustering();
			long startkc= System.currentTimeMillis();
			kc.computeClusters(str,final_idf,sorted_idf,3,doc_term_count);
			long endkc= System.currentTimeMillis();
			System.out.println("Time of execution"+(endkc-startkc));
			double weight= 0.85;
			//	p.getresult(r.maxDoc(),str,sorted_idf,weight);
			//	p.getresult(r.maxDoc(), str, sorted_idf , weight);
			//	System.out.println(query);
			System.out.println("query> ");
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
