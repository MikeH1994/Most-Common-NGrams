import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.commons.io.FileUtils;

public class Utils {
   private PDFParser parser;
   private PDFTextStripper pdfStripper;
   private PDDocument pdDoc ;
   private COSDocument cosDoc ;
   private boolean sanitise = true;
   private String regexString = "[^a-zA-Z ]";
   
   public Utils() {
   
   }
   public void writeStringToFile(String outputPath,String string){
	   try{
		   PrintWriter writer = new PrintWriter(outputPath,"UTF-8");
		   writer.write(string);
		   writer.close();
	   }
	   catch (IOException e){
		   System.out.println(e.toString());
	   }
   }
   public void writeArrayListToFile(String outputPath,ArrayList<Pair<String,Integer>> list){
	   System.out.println("Writing");
	   try{
		   PrintWriter writer = new PrintWriter(outputPath,"UTF-8");
		   for (int i = 0; i<list.size(); i++){
			   writer.write(list.get(i).getLeft() + "\t" + list.get(i).getRight()+"\n");
		   }
		   writer.close();
	   }
	   catch (IOException e){
		   System.out.println(e.toString());
	   }
   }
   public void serializeObject(Object obj,String filepath){
	   try{
		   FileOutputStream fileOutputStream = new FileOutputStream(filepath);
		   ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		   objectOutputStream.writeObject(obj);
		   objectOutputStream.close();
		   fileOutputStream.close();
		   
	   }catch (IOException e){
		   System.out.println(e.toString());
	   }
   }
   public String getStringFromFilepath(String filepath){
	   if (filepath.endsWith(".pdf")){
		   return getStringFromPDF(filepath);
	   }
	   else{
		   try {
			   String returnedStr =  FileUtils.readFileToString(new File(filepath), "utf-8");
	    	   returnedStr = StringUtils.lowerCase(returnedStr);  
	    	   returnedStr = returnedStr.replace(regexString," ");
	    	   returnedStr = returnedStr.replace("[1234567890.,())+]", " ");
	    	   returnedStr = returnedStr.replace("\\p{P}", " ");
		       
			   return returnedStr;
			} catch (IOException e) {
				System.out.println(e.toString());
				return null;
			}
	   }
   }
   public String getStringFromPDF(String filePath) {
	   try{
		   return getStringFromPDF(new File(filePath));
	   }
	   catch(Exception e){
		   System.out.println(e.toString());
		   return null;
	   }
   }
   public String getStringFromPDF(File file) throws Exception {
       this.pdfStripper = null;
       this.pdDoc = null;
       this.cosDoc = null;
       parser = new PDFParser(new FileInputStream(file));
       parser.parse();
       cosDoc = parser.getDocument();
       pdfStripper = new PDFTextStripper();
       pdDoc = new PDDocument(cosDoc);
       pdDoc.getNumberOfPages();
       pdfStripper.setStartPage(0);
       pdfStripper.setEndPage(pdDoc.getNumberOfPages());
       String text = pdfStripper.getText(pdDoc);
       if (sanitise){
    	   text = StringUtils.lowerCase(text);  
    	   text = text.replace(regexString," ");
    	   text = text.replace("[1234567890]", " ");
       }    
       pdDoc.close();
       cosDoc.close();
       return text;
   }
   public void convertFolderToTxt(String rootFolder){	
	   File rootFolderFile = new File(rootFolder);
	   for (File file:rootFolderFile.listFiles()){
		   try{
			   if (file.getName().endsWith(".pdf")){
				   String text = getStringFromPDF(file);
			       String path = file.getAbsolutePath().split(".pdf")[0] + ".txt";
			       System.out.println("Saving pdf to " + path);
			       writeStringToFile(path,text);	   
			   }	   			   
		   }catch(Exception e){
			  System.out.println(e.toString());
		   }
	   }
	   for (File file:rootFolderFile.listFiles()){
		   if (file.isDirectory()){
			   convertFolderToTxt(file.getAbsolutePath());
		   }
	   }
   }
}