import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class MostCommonNGramGenerator extends Utils{
	private String outdir = "C:\\Users\\Michael\\Desktop\\NGram Output\\";//"C:\\Users\\Michael\\Desktop\\Mevitae\\Old\\Corpus\\Physics\\";//TODO
	private String lemmaPath = "C:\\Users\\Michael\\workspace\\MostCommonNGrams\\src\\lemmatization-en.txt";
	private HashMap<String,Integer> nGramsMap;
	private HashMap<String,String> lemmaMap;
	private int minNGram = 1;
	private int maxNGram = 3;
	public MostCommonNGramGenerator(){
		nGramsMap = new HashMap<String,Integer>();
		lemmaMap = new HashMap<String,String>();
		loadLemmaMap();
	}
	public static void main(String[] args){
		String root = "C:\\Users\\Michael\\Desktop\\Deleted\\Theoretical\\";
		FilenameFilter filter = new FilenameFilter(){
			@Override
			public boolean accept(File arg0, String arg1) {
				if (arg0.getName().endsWith(".txt ")){
					return true;
				}
				else{
					return false;
				}
			}
		};	
		File[] sourceFiles = new File(root).listFiles();
		String[] sourceFilepaths = new String[sourceFiles.length];//sourceFiles.length
		for (int i = 0; i<sourceFilepaths.length; i++){
			sourceFilepaths[i] = sourceFiles[i].getAbsolutePath();
			
		}
		new MostCommonNGramGenerator().run(sourceFilepaths);
	}
	void run(String... sourceFilepaths){
		String text;
		String[] tokens;
		for (int i = 0; i<sourceFilepaths.length; i++){
			text = getStringFromFilepath(sourceFilepaths[i]);
			if (text!=null){
				System.out.println("Parsing " + sourceFilepaths[i]);				
				tokens = text.split("\\s+");
				parseTokens(tokens);
			}
		}
		ArrayList<Pair<String,Integer>> array = getSortedList(5);
		writeArrayListToFile(outdir + "Output.txt",array);
		System.out.println(sourceFilepaths.length + " files done");
		
	}
	void loadLemmaMap(){
		try{
			BufferedReader br = new BufferedReader(new FileReader(lemmaPath));
			String line = br.readLine();
			String l,r;
			Scanner s;
			while (line!=null){
				s = new Scanner(line).useDelimiter("\\t");
				l = s.next();
				r = s.next();
				lemmaMap.put(l,r);
				s.close();
				line = br.readLine();
			}
			br.close();
		}catch(IOException e){
			System.out.println(e.toString());
		}
	}
	String lemmatise(String str){
		if (lemmaMap.containsKey(str)){
			return lemmaMap.get(str);
		}
		else{
			return str;
		}
	}
	void parseTokens(String[] tokens){
		for(int i =0; i<tokens.length; i++){
			tokens[i] = lemmatise(tokens[i]);
		}
		String substring;
		for (int nGramLength = minNGram; nGramLength<=maxNGram; nGramLength++){
			for (int i = 0; i<tokens.length - nGramLength; i++){
				substring = "";
				for (int j = 0; j<nGramLength; j++){
					substring+=tokens[i+j] + " ";
				}
				if (nGramsMap.containsKey(substring)){
					nGramsMap.put(substring,nGramsMap.get(substring)+1);
				}
				else{
					nGramsMap.put(substring, 1);
				}
			}
		}
	}
	ArrayList<Pair<String,Integer>> getSortedList(int cutoff){
		System.out.println("Getting sorted list");
		ArrayList<Pair<String,Integer>> array = new ArrayList<Pair<String,Integer>>();
		int n;
		for (String key: nGramsMap.keySet()){
			n = nGramsMap.get(key);
			if (n>=cutoff){
				array.add(new Pair<String,Integer>(key,n));
			}
		}
		bubbleSort(array);
		return array;
	}
	void bubbleSort(ArrayList<Pair<String,Integer>> array){
		int nChanges = 1;
		while(nChanges!=0){
			nChanges = 0;
			for (int i = 0; i<array.size()-1; i++){
				if (array.get(i).getRight()>array.get(i+1).getRight()){
					swap(array,i,i+1);
					nChanges++;
				}
			}
		}
	}
	void quickSort(ArrayList<Pair<String,Integer>> array){
		
	}
	void swap(ArrayList<Pair<String,Integer>> array, int index1, int index2){
		Pair<String,Integer> temp = array.get(index1);
		array.set(index1, array.get(index2));
		array.set(index2, temp);
	}
	
}