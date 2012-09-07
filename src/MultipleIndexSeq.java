import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;


public class MultipleIndexSeq {
	private File index1;
	private File index2;
	private IndexWriter writer1;
	private IndexWriter writer2;
	public static void main(String args[]) throws Exception	{
		MultipleIndexSeq mi=new MultipleIndexSeq();
		try	{
			mi.index1=new File("E:\\lucene_indexes\\index0");
			mi.index2=new File("E:\\lucene_indexes\\index1");
			SimpleFSDirectory directory1=new SimpleFSDirectory(mi.index1);
			SimpleFSDirectory directory2=new SimpleFSDirectory(mi.index2);
			StandardAnalyzer analyzer=new StandardAnalyzer(Version.LUCENE_36);
			IndexWriterConfig config1=new IndexWriterConfig(Version.LUCENE_36,analyzer);
			IndexWriterConfig config2=new IndexWriterConfig(Version.LUCENE_36,analyzer);
			mi.writer1=new IndexWriter(directory1,config1);
			mi.writer2=new IndexWriter(directory2,config2);
			IndexWriter writer;
			long start,end;
			start=System.currentTimeMillis();
			for(int fileNum=1;fileNum<=1;fileNum++)	{
				String inputFile=new String("E:\\lucene_input\\input1.txt");
				FileInputStream fis=new FileInputStream(inputFile);
				InputStreamReader read=new InputStreamReader(fis);
				BufferedReader breader=new BufferedReader(read);
				String input;
				String array[];
				Field.Store store=Field.Store.YES;
				Field.Index index=Field.Index.ANALYZED;
				writer=mi.writer2;
				while((input=breader.readLine()) != null)	{
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
							if(writer.equals(mi.writer1))	{
								writer=mi.writer2;
							}
							else	{
								writer=mi.writer1;
							}
							writer.addDocument(d);
						}
					}
				}
				fis.close();
				System.out.println("File :"+inputFile+" is completed for adding in index.");
			}
			end=System.currentTimeMillis();
			System.out.println("Time required for indexing :"+(float)(end-start)/1000+" seconds");
		}
		catch(Exception e)	{
			e.printStackTrace();
		}
		finally	{
			mi.writer1.close();
			mi.writer2.close();
		}	
	}
}

