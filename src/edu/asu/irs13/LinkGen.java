package edu.asu.irs13;
/**
Program: LinkGen.java
Description: Generate the link matrix for use in Page Rank
Date: 02/19/2001
Developer: Ullas Nambiar

 **/

//import com.lucene.index.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import java.net.*;
import java.nio.*;
import java.nio.charset.*;
import java.nio.channels.*;
/** Generate the Link Matrix from the files crawled.
  The class considers the link mapping for only the files/URL present in the repository.
  Any URL not crawled and stored is considered not present. LinkGen first maps all the documents to a hastable.
 Then it recursively goes through each document and extracts URLs that this document points to.
 Each extracted URL is compared to the list of URLs in hastable and discards those that are not present. Then if
 document A has a link to B, a entry in link table saying A->B is made. This is done for all the documents. The link matrix
 so generated is stored in a file.
 */

public class LinkGen {
  Hashtable hashids = new Hashtable(30000);
  File opendirectory;
  String filelist[] = {"a", "b"};
  FileWriter fpw;

  /** Constructor that accepts directory name where crawled webpages are stored */
  public LinkGen(String repository) {
    // settig the directory to read
    opendirectory = new File(repository);
    try {
      fpw = new FileWriter("Hashedlinks");
    }
    catch (Exception e) {
      System.out.println(e);
    }
  }

  /** Method to generate the link matrix from the files stored in repository.
   */
  public void linker() {

    //hashids store mappings of hashvalue to URL/File Value
    //needed to map between results of search and link matrix

    HashSet extractedlinks = new HashSet();
    Hashtable<String, String> urlConversion = new Hashtable();
    
    if (opendirectory.isDirectory()) {
      filelist = opendirectory.list();
      Arrays.sort(filelist);
        System.out.println("size of file list:" + filelist.length);
    }

      System.out.println("in first loop");
    //first hashing all the existing files
    for (int fi = 0; fi < filelist.length; fi++) {
     // String sturl = new String(filelist[fi]);
      //sturl = "file://" + sturl;
      try {
        //URL turl = new URL(sturl);
        hashids.put("file://" + filelist[fi], filelist[fi]);
      }
      catch (Exception e) {
        System.out.println("Malformed URL " + e);
      }
    }

    System.out.println("in second loop");

    for (int fi = 0; fi < filelist.length; fi++) {
    	String strURL = filelist[fi];

    	// Store file names with index.html or index.htm at end
    	if(strURL.endsWith("%%index.html")){
    		String tempStr = strURL.replace("%%index.html", "");
    		tempStr = tempStr.replace("%%", "/");
    		strURL = strURL.replace("%%", "/");
    		urlConversion.put(tempStr, strURL);

    		tempStr = tempStr + "/";
    		urlConversion.put(tempStr, strURL);
    	}
    	else if(strURL.endsWith("%%index.htm")){
    		String tempStr = strURL.replace("%%index.htm", "");
    		tempStr = tempStr.replace("%%", "/");
    		strURL = strURL.replace("%%", "/");
    		urlConversion.put(tempStr, strURL);

    		tempStr = tempStr + "/";
    		urlConversion.put(tempStr, strURL);
    	}
    }
System.out.println(urlConversion.size());
    for (int fi = 0; fi < filelist.length; fi++) {
        System.out.print("#");
      String strURL = filelist[fi];
      String secondStrURL = strURL;
      String directory = opendirectory.toString() + "/";

      //File here and not URL .... all processing in terms of Files
      try {

        String inLine = "";
        String htmlpage = "";
        String htmlpageLC = "";

        htmlpage = fromFile(directory+strURL).toString().replaceAll("&nbsp;", " ");
        htmlpageLC = htmlpage.toLowerCase();
        int index = 0;

        int indexFRAME, indexA;
        while ( ( (htmlpageLC.indexOf("<a", index) != -1) ||
                 (htmlpageLC.indexOf("<frame", index) != -1))) {
          indexA = htmlpageLC.indexOf("<a", index);
          indexFRAME = htmlpageLC.indexOf("<frame", index);
          if (indexFRAME == -1 || (indexFRAME > indexA && indexA != -1)) {
            if ( (index = htmlpageLC.indexOf("href", indexA)) == -1)
              break;
            if ( (index = htmlpageLC.indexOf("=", index)) == -1)
              break;
          }
          else {
            if ( (index = htmlpageLC.indexOf("src", indexFRAME)) == -1)
              break;
            if ( (index = htmlpageLC.indexOf("=", index)) == -1)
              break;
          }

          index++;
          String remaining = htmlpage.substring(index);

          StringTokenizer st = new StringTokenizer(remaining, "\t\n\r\">#");
          String strLink = st.nextToken();
          String strLinkReplaceHTTP = "";

          URL urlLink;

          try {
            urlLink = new URL(new URL("http://" + strURL.replaceAll("%%", "/")), strLink);
            strLink = urlLink.toString();

            System.out.println(strLink);
            strLinkReplaceHTTP = strLink.replace("http://", "");
            if(urlConversion.containsKey(strLinkReplaceHTTP) == true){
            	strLink = (String)urlConversion.get(strLinkReplaceHTTP);
            }

            if ( (urlLink.toString().indexOf("asu.edu") == -1)
                || urlLink.getFile().endsWith(".jpg")
                || urlLink.getFile().endsWith(".gif")
                || urlLink.getFile().endsWith(".png")
                || urlLink.getFile().endsWith(".mpg")
                || urlLink.getFile().endsWith(".mp3")
                || urlLink.getFile().endsWith(".mov")
                || urlLink.getFile().endsWith(".rm")
                || urlLink.getFile().endsWith(".wav")
                || urlLink.getFile().endsWith(".eps")
                || urlLink.getFile().endsWith(".pdf")
                || urlLink.getFile().endsWith(".pdf")
                || urlLink.getFile().endsWith(".exe")
                || urlLink.getFile().endsWith(".ppt")
                || urlLink.getFile().endsWith(".pps")
                || urlLink.getFile().endsWith(".doc")
                || urlLink.getFile().endsWith(".ps")
                || urlLink.getFile().endsWith(".dvi")
                || urlLink.getFile().endsWith(".jar")
                || urlLink.getFile().endsWith(".java")
                || urlLink.getFile().endsWith(".wpd")
                || urlLink.getFile().endsWith(".vcs")
                || urlLink.getFile().endsWith(".swf")
                || urlLink.getFile().endsWith(".rtf")
                || urlLink.getFile().endsWith(".gzip")
                || urlLink.getFile().endsWith(".gz")
                || urlLink.getFile().endsWith(".zip")
                || urlLink.getFile().endsWith(".tar")
                || urlLink.getFile().endsWith(".class")
                || urlLink.getFile().endsWith(".xls")) {
              System.out.print(",");
              continue;
            }

            extractedlinks.add(strLink);
          }
          catch (MalformedURLException e) {
              System.out.print(".");
            continue;
          }

        }


        String tempURL = "file://" + strURL;
          System.out.print("|");
        Vector storematched = new Vector();

        for (Iterator it = extractedlinks.iterator(); it.hasNext();) {
          tempURL = (String) it.next();

          //adjusting the URLs to suit the storage style on the harddisks
          if (! (tempURL.startsWith("file"))) {
            if (tempURL.length() > 7)
              tempURL = tempURL.substring(7);

            tempURL = tempURL.replaceAll("/", "%%");
            tempURL = "file://" + tempURL;
          }

          try {
            if (hashids.containsKey(tempURL))
              storematched.add(hashids.get(tempURL));
          }
          catch (Exception e) {}
        }
        try {
          URL rootURL = new URL("file://" + strURL);

          fpw.write(rootURL.toString());
          fpw.write("-->");
          fpw.write(storematched.toString());
          fpw.write("\n");
          fpw.flush();
        }
        catch (MalformedURLException e) {
          continue;
        }
        storematched = new Vector();
        extractedlinks = new HashSet();
      }
      catch (FileNotFoundException e) { System.out.println("Exception " + e + " occured."); }
      catch (IOException io) { System.out.println("Exception " + io + " occured."); }
    }

    return;
  }

  private static CharSequence fromFile(String filename) throws IOException {
  		FileInputStream fis = new FileInputStream(filename);
  		FileChannel fc = fis.getChannel();

  		// Create a read-only CharBuffer on the file
  		ByteBuffer bbuf = fc.map(FileChannel.MapMode.READ_ONLY, 0, (int) fc.size());
  		CharBuffer cbuf = Charset.forName("8859_1").newDecoder().decode(bbuf);
  		return cbuf;
  }

  /** Should be called as:  java LinkGen <i>Crawled_Files_Directory</i> (without the ending '/').
      Calls the methods to generate and store a link matrix. */
  public static void main(String[] args) {
	  String crawl = "result3";
	  if (args.length >= 1) {
		  crawl = args[0];
	  }
	  LinkGen lgen = new LinkGen(crawl);
	  lgen.linker();
	  return;
  }

}
