import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;


public class SharedWriter implements Runnable {
	private static IndexWriter writer;
	private static BufferedReader inputReader;
	public void run()	{
		String input;
		String array[];
		Field.Store store=Field.Store.YES;
		Field.Index index=Field.Index.ANALYZED;
		try	{
			while((input=SharedWriter.inputReader.readLine())!=null)	{
				array=input.split("\\|");
				Document d = null;
				for(int i=0;i<array.length;i++)	{
					if((i%3)==0)	{
						d=new Document();
						d.add(new Field("id",array[i].trim(),store,index));
					}
					if((i%3)==1)	{
						d.add(new Field("name",array[i].trim(),store,index));
					}
					if((i%3)==2)	{
						d.add(new Field("address",array[i].trim(),store,index));
						SharedWriter.writer.addDocument(d);
					}
				}
			}
		}
		catch(Exception e)	{
			e.printStackTrace();
		}
	}
	
	public static void main(String args[])	{
		final int totalThreads=Runtime.getRuntime().availableProcessors();
		System.out.println("Total Threads :"+totalThreads);
		try	{
			Directory dir=new SimpleFSDirectory(new File("E:\\lucene_indexes\\index"));
			Analyzer analyzer=new SimpleAnalyzer(Version.LUCENE_36);
			IndexWriterConfig conf=new IndexWriterConfig(Version.LUCENE_36,analyzer);
			//conf.setRAMBufferSizeMB(64.0);
			SharedWriter.writer=new IndexWriter(dir, conf);
			SharedWriter.writer.setInfoStream(new PrintStream(new File("E:\\lucene_debug.txt")));
			Thread th[]=new Thread[totalThreads];
			long start,end;
			start=System.currentTimeMillis();
			for(int fileNum=0;fileNum<1;fileNum++)	{
				File input=new File("E:\\lucene_input\\big_input.txt");
				FileInputStream fis=new FileInputStream(input);
				InputStreamReader read=new InputStreamReader(fis);
				SharedWriter.inputReader=new BufferedReader(read);
				for(int num=0;num<totalThreads;num++)	{
					th[num]=new Thread(new SharedWriter());
					th[num].start();
				}
				for(int num=0;num<totalThreads;num++)	{
					th[num].join();
				}
				end=System.currentTimeMillis();
				System.out.println("File"+fileNum+" processed after :"+(float)(end-start)/1000+" seconds");
				SharedWriter.inputReader.close();
			}
			SharedWriter.writer.close();
			end=System.currentTimeMillis();
			System.out.println("The time required is :"+(float)(end-start)/1000+" seconds");
		}
		catch(Exception e)	{
			e.printStackTrace();
		}
		
	}
}
