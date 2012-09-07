import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

/**
 * Creates an index called 'index' in a temporary directory.
 * The number of documents to add to this index, the mergeFactor and
 * the maxMergeDocs must be specified on the command line
 * in that order - this class expects to be called correctly.
 * Additionally, if the fourth command line argument is '-r' this
 * class will first index all documents in RAMDirectory before
 * flushing them to the disk in the end.  To make this class use the
 * regular FSDirectory use '-f' as the fourth command line argument.
 * 
 * Note: before running this for the first time, manually create the
 * directory called 'index' in your temporary directory.
 */
public class MemoryVsDisk
{
    public static void main(String[] args) throws Exception
    {
        int docsInIndex  = Integer.parseInt(args[0]);

        // create an index called 'index' in a temporary directory
        String indexDir =
            System.getProperty("java.io.tmpdir", "tmp") +
            System.getProperty("file.separator") + "index";

        Analyzer analyzer  = new StopAnalyzer(Version.LUCENE_36);
        long     startTime = System.currentTimeMillis();

        if ("-r".equalsIgnoreCase(args[2]))
        {
            // if -r argument was specified, use RAMDirectory
            RAMDirectory ramDir    = new RAMDirectory();
            IndexWriterConfig myConf1= new IndexWriterConfig(Version.LUCENE_36, analyzer);
            IndexWriter  ramWriter = new IndexWriter(ramDir, myConf1);
            long heapsize = Runtime.getRuntime().totalMemory();
            System.out.println("the heap size is ="+(heapsize/1024/1024)+" MB");
            addDocs(ramWriter, docsInIndex);
            long size = ramDir.sizeInBytes();
            System.out.println("size of ram directory is = "+(size/1024/1024)+" mega bytes");
            heapsize=Runtime.getRuntime().totalMemory();
            System.out.println("the heap size is ="+(heapsize/1024/1024)+" MB");
            ramWriter.close();
        }
        else
        {
            // create an index using FSDirectory
        	File myFile = new File(indexDir);
        	SimpleFSDirectory fsDir = new SimpleFSDirectory(myFile);
        	IndexWriterConfig myConf2= new IndexWriterConfig(Version.LUCENE_36, analyzer);
            myConf2.setMaxBufferedDocs(Integer.parseInt(args[1]));
        	IndexWriter fsWriter  = new IndexWriter(fsDir,myConf2);
            
            
            //fsWriter.mergeFactor  = Integer.parseInt(args[1]);
            //fsWriter.maxMergeDocs = Integer.parseInt(args[2]);
            addDocs(fsWriter, docsInIndex);
            fsWriter.close();
        }

        long stopTime = System.currentTimeMillis();
        System.out.println("Total time: " + (stopTime - startTime) + " ms");
    }

    private static void addDocs(IndexWriter writer, int docsInIndex)
        throws IOException
    {
        for (int i = 0; i < docsInIndex; i++)
        {
            Document doc = new Document();
            doc.add(new Field("fieldname", "Bibamus, moriendum est suppose this is long string.",Field.Store.YES,Field.Index.ANALYZED));
            doc.add(new Field("last_name","this is the normal string",Field.Store.YES,Field.Index.ANALYZED));
            writer.addDocument(doc);
        }
    }
}