package sudokuTry;

import java.util.*;
import java.util.Map.Entry;
import java.io.*;

public class sudokuSolver{
	static String rows = "123456789";
	static String cols = "ABCDEFGHI";
	Map<String, String> sudokuGrid = new HashMap<String, String>();
	Map<String, String> initSolution = new HashMap<String, String>();
	Map<String, String[]> peers = new HashMap<String, String[]>();
	Map<String, String[]> rowUnit = new HashMap<String, String[]>();
	Map<String, String[]> colUnit = new HashMap<String, String[]>();
	Map<String, String[]> boxUnit = new HashMap<String, String[]>();
	public sudokuSolver(String name) throws IOException{
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader(name));
		String line = "";
		String splitBy = ",";
		int lineNo = 0;
		while ((line = br.readLine())!= null) {			
			String[] sudokuElem = line.split(splitBy);			
			for(int i = 0;i <9 ; i++){
				this.sudokuGrid.put(""+rows.charAt(lineNo)+cols.charAt(i), sudokuElem[i]);
			}
			lineNo ++;
		}
		for (Entry<String, String> s : this.sudokuGrid.entrySet()){
			this.initSolution.put(s.getKey(), rows);
			String blockRow = "";
			String blockCol = "";
			if("ABC".contains(""+s.getKey().charAt(1)))
				blockCol = "ABC";
			else if("DEF".contains(""+s.getKey().charAt(1)))
				blockCol = "DEF";
			else if("GHI".contains(""+s.getKey().charAt(1)))
				blockCol = "GHI";
			if("123".contains(""+s.getKey().charAt(0)))
				blockRow = "123";
			else if("456".contains(""+s.getKey().charAt(0)))
				blockRow = "456";
			else if("789".contains(""+s.getKey().charAt(0)))
				blockRow = "789";
			this.boxUnit.put(s.getKey(),cellNames(blockRow,blockCol));
			this.rowUnit.put(s.getKey(),cellNames(""+s.getKey().charAt(0),cols));
			this.colUnit.put(s.getKey(),cellNames(rows,""+s.getKey().charAt(1)));
			Set<String> mySet = new HashSet<String>(Arrays.asList(this.rowUnit.get(s.getKey())));
			mySet.addAll(Arrays.asList(this.colUnit.get(s.getKey())));
			mySet.addAll(Arrays.asList(this.colUnit.get(s.getKey())));
			mySet.addAll(Arrays.asList(this.boxUnit.get(s.getKey())));
			mySet.remove(s.getKey());
			this.peers.put(s.getKey(), (String[]) mySet.toArray(new String[mySet.size()]));
		}
	}
	public static String[] cellNames(String rowList,String colList){
		String[] retString = new String[rowList.length()*colList.length()];
		for(int i=0; i<rowList.length(); i++){
			for(int j=0;j<colList.length();j++){
				retString[(i*colList.length())+j] = ""+rowList.charAt(i)+colList.charAt(j);
			}
		}
		return retString;
	}
	public static void printSudoku(Map<String,String> printSudokuGrid){
		for(int i=0;i<9;i++){
			for(int j=0 ; j<9 ; j++){
				System.out.printf(String.format("%-10s",printSudokuGrid.get(""+rows.charAt(i)+cols.charAt(j))));
			}
			System.out.print("\n");
		}
	}
	public void exportCSV(String fileName) throws IOException{
		FileWriter writer = new FileWriter(fileName);
		for(int i = 0; i<9 ; i++){
			int j;
			for(j = 0; j<8 ; j++){
				writer.append(this.initSolution.get(""+rows.charAt(i)+cols.charAt(j)));
				writer.append(",");
			}
			writer.append(this.initSolution.get(""+rows.charAt(i)+cols.charAt(j)));
			writer.append("\n");
		}
		writer.flush();
		writer.close();
	}
	public Map<String, String> solveSudoku(Map<String, String> presentSudoku, Map<String, String> presentSolution) throws IOException{
		System.out.println("Entered solveSudoku");
		for(Entry<String, String> s : presentSolution.entrySet()){
			if( presentSudoku.get(s.getKey()).length()==1 && !presentSudoku.get(s.getKey()).equals("0")){
				presentSolution = removeFromPeers(presentSolution,s.getKey(),presentSudoku.get(s.getKey()));
			}
		}
		for(Entry<String,String> s: presentSolution.entrySet()){
			int len;
			String key;
			if((len = (key = presentSolution.get(s.getKey())).length())!=1){
				for(int i = 0 ; i< len ;i++){
					if(this.isUniqueInPeers(presentSolution,s.getKey(),key.charAt(i))){
						presentSolution.put(s.getKey(), ""+key.charAt(i));
					}
				}
			}
		}
		return presentSolution;
	}
	public void solutionFinder(Map<String, String> sudokuGrid) throws IOException{
		System.out.println("Entered solutionFinder");
		Map<String, String> initSolution = new HashMap<String, String>();
		for (Entry<String, String> s : this.sudokuGrid.entrySet()){
			initSolution.put(s.getKey(), rows);
		}
		Map<String, String> basic = solveSudoku(sudokuGrid, initSolution);
		while(!this.sudokuSolved(basic)){
			basic = searchSudoku(basic);
			printSudoku(basic);
		}
		printSudoku(basic);
	}
	public Map<String, String> searchSudoku(Map<String, String> presentSolution) throws IOException{
		System.out.println("Entered searchSudoku");
		while(!this.sudokuSolved(presentSolution)){
			int len1;
			int minLen = 9;
			Entry<String, String> searchWith = presentSolution.entrySet().iterator().next();
			for(Entry<String, String> s1 : presentSolution.entrySet()){
				if((len1  = presentSolution.get(s1.getKey()).length())>1 && len1 < minLen){
					minLen = len1;
					searchWith = s1;
				}
			}
			System.out.println(minLen + searchWith.getKey() + searchWith.getValue());
//			for(int i =0;i < minLen ; i++){
				presentSolution.put(presentSolution.get(searchWith.getKey()),""+presentSolution.get(searchWith.getValue()).charAt(0));
				presentSolution = this.solveSudoku(presentSolution, presentSolution);
				System.exit(1);
//			}
			
		}
		return presentSolution;
	}
	public boolean sudokuSolved(Map<String, String> sudokuCandidate){
		for(Entry<String, String> s : sudokuCandidate.entrySet()){
			if(sudokuCandidate.get(s.getKey()).length()!=1)
				return false;
		}
		System.exit(1);
		return true;
	}
	public boolean isUniqueInPeers(Map<String,String> gridVal, String location, char value){
		String[] x = this.rowUnit.get(location);
		for(int i = 0; i<x.length; i++){
			if(!location.equals(x[i])){
				String temp = gridVal.get(x[i]);
				if(temp.contains(""+value))
					return false;
			}
		}
		x = this.colUnit.get(location);
		for(int i=0;i<x.length;i++){
			if(!location.equals(x[i])){
				String temp = gridVal.get(x[i]);
				if(temp.contains(""+value))
					return false;
			}
		}
		x = this.boxUnit.get(location);
		for(int i=0;i<x.length;i++){
			if(!location.equals(x[i])){
				String temp = gridVal.get(x[i]);
				if(temp.contains(""+value))
						return false;
			}
		}
		return true;
	}
	public Map<String, String> removeFromPeers(Map<String, String> gridVal, String location, String value){
		gridVal.put(location, value);
		String[] x = this.peers.get(location);
		for(int i = 0; i<x.length ; i++){
			String temp = gridVal.get(x[i]);
			temp = temp.replace(value, "");
			gridVal.put(x[i],temp);
		}
		return gridVal;
	}
    
	
	public static void main(String[] args) throws IOException {
	   sudokuSolver a = new sudokuSolver("C:/Users/Aishanee/workspace/sudokuTry/src/sudokuTry/file2.csv");
	   System.out.println("The Sudoku Puzzle read from file : ");
	   System.out.println("The solution to the puzzle : ");
//	   Map<String, String> newstuff = new HashMap<String,String>();
	   a.solutionFinder(a.sudokuGrid);
	   System.out.println("Came out..");
//	   Map<String, String> newstuff = new HashMap<String, String>();
//	   newstuff = a.initSolution;
//	   a.exportCSV("C:/Users/Aishanee/workspace/sudokuTry/src/sudokuTry/file1solution.csv");
   }
}
//BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
//String input = br.readLine();
