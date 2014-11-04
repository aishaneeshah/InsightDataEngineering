import java.util.*;
import java.util.Map.Entry;
import java.io.*;
/*This class is used by PlaySudoky class, do not run this. The main method is empty for this class*/
public class sudokuSolver{
	static String rows = "123456789";
	static String cols = "ABCDEFGHI";
	Map<String, String> sudokuGrid = new HashMap<String, String>();
	Map<String, String> finalSolution = new HashMap<String, String>();
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
	public void printSudoku(Map<String,String> printSudokuGrid){
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
				writer.append(this.finalSolution.get(""+rows.charAt(i)+cols.charAt(j)));
				writer.append(",");
			}
			writer.append(this.finalSolution.get(""+rows.charAt(i)+cols.charAt(j)));
			writer.append("\n");
		}
		writer.flush();
		writer.close();
	}
	public Map<String, String> solveSudoku(Map<String, String> presentSudoku, Map<String, String> presentSolution) throws IOException{
		for(Entry<String, String> s : presentSolution.entrySet()){
			if( presentSudoku.get(s.getKey()).length()==1 && !presentSudoku.get(s.getKey()).equals("0")){
				presentSolution = removeFromPeers(presentSolution,s.getKey(),presentSudoku.get(s.getKey()));
			}
		}
		presentSolution = rule2Implementation(presentSolution);
		return (presentSolution);
	}
	public Map<String, String> rule2Implementation(Map<String, String> grid){
		for(Entry<String,String> s: grid.entrySet()){
			int len;
			String key;
			if((len = (key = grid.get(s.getKey())).length())!=1){
				for(int i = 0 ; i< len ;i++){
					if(this.isUniqueInPeers(grid,s.getKey(),key.charAt(i))){
						grid = this.assign(grid,s.getKey(),key.charAt(i));
					}
				}
			}
		}
		return grid;
	}
	public void solutionFinder(Map<String, String> sudokuGrid) throws IOException{
		Map<String, String> initSolution = new HashMap<String, String>();
		for (Entry<String, String> s : this.sudokuGrid.entrySet()){
			initSolution.put(s.getKey(), rows);
		}
		Map<String, String> basic = solveSudoku(sudokuGrid, initSolution);
		Map<String, String> newBasic = null;
		while(!basic.equals(newBasic)){
			newBasic = this.getCopy(basic);
			basic = solveSudoku(basic, basic);
		}
		if(solveUsingSearch(basic)){
			return;
		}
	}
	public boolean solveUsingSearch(Map<String, String> grid) throws IOException{
		if(sudokuSolved(grid)){
			this.finalSolution = grid;
			return true;
		}
		int minLen = 9;
		int len;
		Entry<String, String> searchWith = null;
		for(Entry<String, String> s: grid.entrySet()){
			if(( len = grid.get(s.getKey()).length()) != 1 && len < minLen){
				minLen = len;
				searchWith = s;
			}
			if(minLen == 2)
				break;
		}
		int i = 0;
		while(i<minLen){
			Map<String, String> newGrid = this.getCopy(grid);
			newGrid = assign(newGrid, searchWith.getKey(), searchWith.getValue().charAt(i));
			newGrid = solveSudoku(newGrid, newGrid);
			Map<String, String> newBasic = null;
			while(!newGrid.equals(newBasic)){
				newBasic = this.getCopy(newGrid);
				newGrid = solveSudoku(newGrid, newGrid);
			}
			int flag= 0;
			for(Entry<String, String> s : newGrid.entrySet()){
				if(newGrid.get(s.getKey()).length() == 0)
					flag = 1;
			}
			if(flag == 0){
				if(!solveUsingSearch(newGrid))
					i++;
				else
					return true;
			}
			else{
				i++;
			}
		}
		return false;
	}
	public Map<String, String> getCopy(Map<String, String> grid){
		Map<String, String> Newgrid = new HashMap<String, String>();
		for(Entry<String, String> s : grid.entrySet()){
			Newgrid.put(s.getKey(), s.getValue());
		}
		return Newgrid;
	}
	public Map<String,String> assign(Map<String, String> grid, String key, char value){
		grid.put(key, ""+value);
		grid = this.removeFromPeers(grid,key,""+value);
		return rule2Implementation(grid);
	}
	public static boolean sudokuSolved(Map<String, String> sudokuCandidate){
		for(Entry<String, String> s : sudokuCandidate.entrySet()){
			if(s.getValue().length()!=1)
				return false;
		}
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
	}
}
