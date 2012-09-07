
									//process test data from csv

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class DataProcess {
	public static void main(String args[])	{
		DataProcess pro=new DataProcess();
		//pro.csvToTxt();
		pro.nameMerge();
	}
	
	public void csvToTxt()	{
		File file1=null,file2=null;
		InputStreamReader isr=null;
		BufferedReader reader=null;
		OutputStreamWriter osw=null;
		BufferedWriter writer=null;
		try	{
			file1=new File("E:\\350000.csv");
			isr=new InputStreamReader(new FileInputStream(file1));
			reader=new BufferedReader(isr);
			file2=new File("E:\\test.txt");
			osw=new OutputStreamWriter(new FileOutputStream(file2));
			writer=new BufferedWriter(osw);
			String input=null,output=null;
			input=reader.readLine();
			System.out.println(input);
			int idNum=1;
			String array[]=null;
			while((input=reader.readLine())!=null)	{
				array=input.split("\"");
				//System.out.println(input);
				int position=0;
				output=new String();
				for(String data:array)	{
					if(data.compareTo("")==0)	{
						//System.out.println("empty string...");
					}
					else if(data.compareTo(",")==0)	{
						//System.out.println("comma,,,,");
					}
					else	{
						if(position==0)	{
							//System.out.println("First Name = "+data.trim());
							output=Integer.toString(idNum);
							output=output+"|";
							output=output+data.trim();
						}
						if(position==1)	{
							//System.out.println("Last Name = "+data.trim());
							output=output+" ";
							output=output+data.trim();
						}
						if(position==4)	{
							//System.out.println("City = "+data.trim());
							output=output+"|";
							output=output+data.trim();
						}
						position++;
					}
				}
				writer.write(output);
				writer.newLine();
				idNum++;
			}
		}
		catch(Exception e)	{
			e.printStackTrace();
		}
		finally	{
			try	{
				reader.close();
				isr.close();
				writer.close();
				osw.close();
			}
			catch(Exception e){
				
			}
		}
	}

	public void nameMerge()	{
		BufferedReader reader1=null,reader2=null;
		BufferedWriter writer=null;
		try	{
			File input1=new File("E:\\fakename.txt");
			FileInputStream fis1=new FileInputStream(input1);
			reader1=new BufferedReader(new InputStreamReader(fis1));
			File input2=new File("E:\\big_input.txt");
			FileInputStream fis2=new FileInputStream(input2);
			reader2=new BufferedReader(new InputStreamReader(fis2));
			File output=new File("E:\\big_output.txt");
			FileOutputStream fos=new FileOutputStream(output);
			writer=new BufferedWriter(new OutputStreamWriter(fos));
			String str=null;
			int idNum=1;
			while((str=reader1.readLine())!=null)	{
				String array[]=str.split("\\|");
				String value=null;
				for(int i=0;i<array.length;i++)	{
					if(i%4==0)	{
						value=Integer.toString(idNum);
					}
					if(i%4==1)	{
						value=value+'|';
						value=value+array[i].trim();
					}
					if(i%4==2)	{
						value=value+' ';
						value=value+array[i].trim();
					}
					if(i%4==3)	{
						value=value+'|';
						value=value+array[i].trim();
					}
				}
				writer.write(value);
				writer.newLine();
				idNum++;
			}
			while((str=reader2.readLine())!=null)	{
				String array[]=str.split("\\|");
				String value=null;
				for(int i=0;i<array.length;i++)	{
					if(i%3==0)	{
						value=Integer.toString(idNum);
					}
					if(i%3==1)	{
						value=value+'|';
						value=value+array[i].trim();
					}
					if(i%3==2)	{
						value=value+'|';
						value=value+array[i].trim();
					}
				}
				writer.write(value);
				writer.newLine();
				idNum++;
				//break;
			}
			System.out.println(idNum);
		}
		catch(Exception ex)	{
			ex.printStackTrace();
		}
		finally	{
			try	{
				reader1.close();
				reader2.close();
				writer.close();
			}
			catch(Exception e)	{
				
			}
		}
		
	}
}
