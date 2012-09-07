import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;



public class MultiIndexParallel	implements Runnable		{
	private LinkedBlockingQueue<String> dataQue;
	private IndexWriter writer;
	
	public void run()	{
		try	{
			System.out.println(this.writer.getDirectory().toString());
			String input;
			String array[];
			Field.Store store=Field.Store.NO;
			Field.Index index=Field.Index.ANALYZED;
			while((input=this.dataQue.poll(2, TimeUnit.SECONDS))!= null)	{
				//input=this.dataQue.take()!=null		for waiting until document gets available
				array=null;
				array=input.split("\\|");
				Document d=null;
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
						this.writer.addDocument(d);
					}
				}
			}
			System.out.println("Done in run");
		}
		catch(Exception e)	{
			e.printStackTrace();
		}
		finally	{
			try	{
				this.writer.close();
			}
			catch(Exception e)	{
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String args[])	{
		FileInputStream fis = null;
		try	{
			final int totalThreads=3,maxQueSize=1000;
			Directory d[]=new SimpleFSDirectory[totalThreads];
			SimpleAnalyzer analyzer=new SimpleAnalyzer(Version.LUCENE_36);
			IndexWriterConfig config[]=new IndexWriterConfig[totalThreads];
			MultiIndexParallel multiindex[]=new MultiIndexParallel[totalThreads];
			Thread th[]=new Thread[totalThreads];
			for(int num=0;num<totalThreads;num++)	{
				multiindex[num]=new MultiIndexParallel();
				d[num]=new SimpleFSDirectory(new File("E:\\lucene_indexes\\index"+num));
				config[num]=new IndexWriterConfig(Version.LUCENE_36, analyzer);
				multiindex[num].writer=new IndexWriter(d[num], config[num]);
				multiindex[num].dataQue=new LinkedBlockingQueue<String>(maxQueSize);
				th[num]=new Thread(multiindex[num]);
				th[num].start();
				System.out.println("The thread "+num+"is created");
			}
			long start,end;
			start=System.currentTimeMillis();
			for(int fileNum=0;fileNum<3;fileNum++)	{				//one line goes into all threads dataQue.
				File inputFile=new File("E:\\lucene_input\\input1.txt");
				fis=new FileInputStream(inputFile);
				InputStreamReader read=new InputStreamReader(fis);
				BufferedReader breader=new BufferedReader(read);
				String input=null;
				while((input=breader.readLine())!=null)	{
					for(int num=0;num<totalThreads;num++)	{
						multiindex[num].dataQue.put(input);
					}
				}
				fis.close();
				System.out.println("File :"+inputFile+" is completed for adding in index.");
				end=System.currentTimeMillis();
				System.out.println("After :"+(float)(end-start)/1000+" seconds");
			}
			for(int num=0;num<totalThreads;num++)	{
				th[num].join();
			}
			analyzer.close();
			end=System.currentTimeMillis();
			System.out.println("Time required is :"+(float)(end-start)/1000+" seconds");
			System.out.println("index writing done in main");
		}
		catch(Exception e)	{
			e.printStackTrace();
		}
		finally	{
			try	{
				fis.close();
			}
			catch(Exception e)	{
				e.printStackTrace();
			}
		}
	}
}