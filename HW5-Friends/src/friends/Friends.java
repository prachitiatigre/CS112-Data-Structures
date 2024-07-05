package friends;
import java.util.ArrayList;
import structures.Queue;
import structures.Stack;

public class Friends 
{
	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null or empty array list if there is no
	 *         path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) 
	{	
		if (g == null || p1 == null || p2 == null)
		{
			return null;
		}
		p1 = p1.toLowerCase();
		p2 = p2.toLowerCase();

		ArrayList<String> shortest_chain = new ArrayList<>();
		  
		if (p1.equals(p2))
		{
			shortest_chain.add(g.members[g.map.get(p1)].name);
			return shortest_chain;
		}
		if (g.map.get(p1) == null || g.map.get(p2) == null)
		{
			return null;
		}

		Queue<Integer> queue = new Queue<>();
		int[] numbersAfterDFS = new int[g.members.length];
		int[] prev = new int[g.members.length];
		boolean[] visited = new boolean[g.members.length]; 

		for (int i = 0; i < visited.length; i++)
		{
			numbersAfterDFS[i] = Integer.MAX_VALUE;
			prev[i] = -1;
			visited[i] = false;
		}
		int startIndex = g.map.get(p1);
		numbersAfterDFS[startIndex] = 0; 
		visited[startIndex] = true;
		queue.enqueue(startIndex);

		while (!queue.isEmpty())
		{
			int num = queue.dequeue(); 
			Person p = g.members[num];

			for (Friend ptr = p.first; ptr != null; ptr = ptr.next)
			{
				int fnum = ptr.fnum;
				if (!visited[fnum])
				{
					numbersAfterDFS[fnum] = numbersAfterDFS[num] + 1; 
					prev[fnum] = num;
					visited[fnum] = true;
					queue.enqueue(fnum); 
				}
			}
		}
		Stack<String> path = new Stack<>();
		int index = g.map.get(p2);
		
		return checkShortestChain(path, index, prev, visited, g);
	}
	private static ArrayList<String> checkShortestChain(Stack<String> path, int index, int prev[], boolean visited[], Graph g)
	{	
		ArrayList<String> shortest_chain = new ArrayList<>();

		if (visited[index] == false)
		{
			return null;
		}
		while (index != -1)
		{
		   path.push(g.members[index].name);
		   index = prev[index];
		}
		while (path.isEmpty() == false)
		{
			shortest_chain.add(path.pop());
		}
		return shortest_chain;
	}

	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null or empty array list if there is no student in the
	 *         given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) 
	{
		
		if (g == null || school == null) {
			return null;
		}
		
		ArrayList<ArrayList<String>> list_of_cliques = new ArrayList<ArrayList<String>>();
		
		boolean[] visited = new boolean[g.members.length];
		
		return BFS(g, g.members[0], list_of_cliques, visited, school);	
	}

	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null or empty array list if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) 
	{	
		if (g == null) 
		{
			return null;
		}
		
		ArrayList<String> allConnectors = new ArrayList<String>();
		
		boolean[] visited = new boolean[g.members.length];
		
		ArrayList<String> predecessor = new ArrayList<String>();
		
		int[] numbersOfDFS= new int[g.members.length];
		
		int[] beforeNums = new int[g.members.length];
		
		
		for (int i = 0; i < g.members.length; i++)
		{
			if (visited[i] == false) 
			{
				allConnectors = DFS(allConnectors, g, g.members[i], visited, new int[] {0,0}, numbersOfDFS, beforeNums, predecessor, true);
			}
		}
		return allConnectors;
	}
	private static ArrayList<ArrayList<String>> BFS(Graph g, Person start, ArrayList<ArrayList<String>> listOfCliques, boolean[] visited, String school)
	{
		ArrayList<String> search = new ArrayList<String>();
		
		Queue<Person> queue = new Queue<Person>();
		
		queue.enqueue(start);
		visited[g.map.get(start.name)] = true;
		
		Person mainone = new Person();
		Friend neighbor;
		
		if (start.school == null || start.school.equals(school) != true) 
		{
			queue.dequeue();
			
			for (int j = 0; j < visited.length; j++) 
			{
				if (visited[j] == false) 
				{
					return BFS(g, g.members[j], listOfCliques, visited, school);
				}
			}
		}
		
		while (queue.isEmpty() != true) 
		{	
			mainone = queue.dequeue();
			neighbor = mainone.first;
			
			search.add(mainone.name);
			
			while (neighbor != null) 
			{
				if (visited[neighbor.fnum] != true) 
				{
					if (g.members[neighbor.fnum].school != null) 
					{
						if (g.members[neighbor.fnum].school.equals(school)) 
						{
							queue.enqueue(g.members[neighbor.fnum]);
						}
					}
					visited[neighbor.fnum] = true;
				}
				neighbor = neighbor.next;
			}
		}
		
		if (listOfCliques.isEmpty() == true || !search.isEmpty()) 
		{
			listOfCliques.add(search);
		} 
		for (int i = 0; i < visited.length; i++) 
		{
			if (visited[i] != true) 
			{
				return BFS(g, g.members[i], listOfCliques, visited, school);
			}
		}
		return listOfCliques;
	}
	
	private static ArrayList<String> DFS(ArrayList<String> allConnectors, Graph g, Person start, boolean[] visited, 
			int[] count, int[] numbersOfDFS, int[] back, ArrayList<String> backward, boolean started)
	{
		Friend neighbor = start.first;

		visited[g.map.get(start.name)] = true;
		
		numbersOfDFS[g.map.get(start.name)] = count[0];
		back[g.map.get(start.name)] = count[1];
		
		while (neighbor != null) 
		{	
			if (visited[neighbor.fnum] != true) 
			{
				count[0]++;
				count[1]++;
				
				allConnectors = DFS(allConnectors, g, g.members[neighbor.fnum], visited, count, numbersOfDFS, back, backward, false);
				
				//This means that there can be an answer that is inserted here just have to ensure it is 
				if (numbersOfDFS[g.map.get(start.name)] <= back[neighbor.fnum]) 
				{
					if ((allConnectors.contains(start.name) == false && backward.contains(start.name)) || 
							(allConnectors.contains(start.name) == false && started == false)) 
					{
						allConnectors.add(start.name);
					}
				}
				else 
				{
					int first = back[g.map.get(start.name)];
					int second = back[neighbor.fnum];
					
					if (second > first) 
					{
						back[g.map.get(start.name)] = first;
					}
					else 
					{
						back[g.map.get(start.name)] = second;
					} 
				}		
			backward.add(start.name);
			}
			else 
			{
				int third = back[g.map.get(start.name)];
				int fourth = numbersOfDFS[neighbor.fnum];
				
				if (fourth > third) 
				{
					back[g.map.get(start.name)] = third;
				}
				else 
				{
					back[g.map.get(start.name)] = fourth;
				}
			}
			neighbor = neighbor.next;
		}
		return allConnectors;
	}
}