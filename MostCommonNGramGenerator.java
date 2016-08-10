import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

public class MostCommonNGramGenerator extends Utils{
	//TODO serialize hash map after running
	private String outdir;
	private HashMap<String,Integer> nGramsMap;
	private int minNGram = 2;
	private int maxNGram = 4;
	public MostCommonNGramGenerator(){
		nGramsMap = new HashMap<String,Integer>();
	}
	public static void main(String[] args){
		String root = args[0];
		outdir = args[1];
		
		FilenameFilter filter = new FilenameFilter(){
			@Override
			public boolean accept(File arg0, String arg1) {
				if (arg0.getName().endsWith(".txt")){
					return true;
				}
				else{
					return false;
				}
			}
		};	
		File[] sourceFiles = new File(root).listFiles();
		String[] sourceFilepaths = new String[sourceFiles.length];
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
	void parseTokens(String[] tokens){
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
		//TODO
	}
	void swap(ArrayList<Pair<String,Integer>> array, int index1, int index2){
		Pair<String,Integer> temp = array.get(index1);
		array.set(index1, array.get(index2));
		array.set(index2, temp);
	}
	
}