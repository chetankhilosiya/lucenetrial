import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;


public class SearchSeq	{
	public static void main(String args[])	{
		System.out.println("Hello User !!!");
		System.out.println("Usage : <field name>=<text to search>");
		BufferedReader inputReader=null;
		IndexReader indexReader=null;
		IndexSearcher search=null;
		try	{
			inputReader=new BufferedReader(new InputStreamReader(System.in));
			Directory dir=new SimpleFSDirectory(new File("E:\\lucene_indexes\\index"));
			indexReader=IndexReader.open(dir);
			Analyzer analyzer=new SimpleAnalyzer(Version.LUCENE_36);
			search=new IndexSearcher(indexReader);
			
			String str=null;
			while(true)	{
				System.out.print("Search :");
				str=inputReader.readLine();
				//String array[]=str.split("=");
				/*if(array.length!=2)	{
					if(array[0].compareTo("exit")==0)	{
						System.out.println("Bye Bye....");
						System.exit(0);
					}
					System.out.println("Usage : <field name>=<text to search>");
					continue;
				}*/
				if(str.compareTo("exit")==0)	{
					System.out.println("Bye Bye...");
					System.exit(0);
				}
				Query q=new QueryParser(Version.LUCENE_36,"name", analyzer).parse(str.trim());
				TopScoreDocCollector collector=TopScoreDocCollector.create(1000, true);
				search.search(q, collector);
				ScoreDoc docs[]=null;
				docs=collector.topDocs().scoreDocs;
				int number=1;
				for(ScoreDoc result:docs)	{
					int docid=result.doc;
					Document d=search.doc(docid);
					System.out.println("number-"+number+"	"+d.toString());
					number++;
				}
				/*if(docs.length==0)	{
					System.out.println("not found any result");
					continue;
				}
				ScoreDoc result=docs[0];
				System.out.println(search.doc(result.doc).toString());*/
			}
			//System.out.println("Program is finished");
		}
		catch(Exception ex)	{
			ex.printStackTrace();
		}
		finally	{
			try	{
				inputReader.close();
				indexReader.close();
				search.close();
			}
			catch(Exception expt)	{
				
			}
		}
	}
}
