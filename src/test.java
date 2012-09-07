import java.io.File;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;


public class test {
	public static void main(String args[]) throws Exception	{
		Directory dir=new SimpleFSDirectory(new File("E:\\lucene_indexes\\index"));
		IndexReader read=IndexReader.open(dir);
		System.out.println("No. of documents in index0 are :"+read.numDocs());
		System.out.println(IndexReader.listCommits(dir));
		System.out.println(Runtime.getRuntime().availableProcessors());
	}
}
