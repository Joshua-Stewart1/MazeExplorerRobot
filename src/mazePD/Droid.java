package mazePD;

import mazePD.Maze.Content;
import mazePD.Maze.Direction;

public class Droid implements DroidInterface
{
	public enum Status {NEW,VISITED,PATH,BLOCK};
	
	private String name;
	private Cell[][][] map;
	private LinkedStack<Coordinates> path;
	
	private class Cell
	{
		private Status state;
		
		protected Cell(Status cellState)
		{
			setCellState(cellState);
		}

		protected Status getCellState()
		{
			return state;
		}
		
		protected void setCellState(Status cellState)
		{
			state = cellState;
		}

		public String toString()
		{
			switch(state)
			{
			case NEW:
				return "[ ]";
			case VISITED:
				return "[.]";
			case PATH:
				return "[X]";
			case BLOCK:
				return "[*]";
			default:
				return "[?]";
			}
		}
	}
	
	public Droid(String n)
	{
		name = n;
		path = new LinkedStack<Coordinates>();
	}
	
	public String getName()
	{
		return name;
	}
	
	public void runMaze(Maze maze)
	{
		Droid droid = this;
		//Start droid in maze
		maze.enterMaze(droid);
		//Create droid record of maze
		createMap(maze);
		//Update map of maze
		Coordinates currentPos = maze.getCurrentCoordinates(droid);
		updateMap(currentPos, Status.PATH);
		//Update stack
		path.push(currentPos);
		//Output current maze path
		printMap();
		
		int steps = 0;
		
		while(maze.scanCurLoc(droid) != Content.END)
		{
			//Move droid
			decideMove(maze, droid);
			//Output current maze path
			printMap();
			steps++;
		}
		System.out.println("Maze Complete -- Steps Taken: " + steps);
	}
	
	//Handles the movement of the droid
	private void decideMove(Maze maze, Droid droid)
	{
		Coordinates currentPos = maze.getCurrentCoordinates(droid);
		Content[] adj = maze.scanAdjLoc(droid);
		
		//Add nearby walls to the map
		findBlocks(maze, droid, adj, currentPos);
		
		//Go down a level if possible
		if(maze.scanCurLoc(droid) == Content.PORTAL_DN)
		{
			currentPos = maze.usePortal(droid, Direction.DN);
			updateMap(currentPos, Status.PATH);
		}
		//Else, move to the next available space
		else
		{
			Direction nextLoc = findMove(maze, droid, adj, currentPos);
			
			//If an adjacent space is unvisited, move there
			if(nextLoc != null)
			{
				currentPos = maze.move(droid, nextLoc);
				path.push(currentPos);
				updateMap(currentPos, Status.PATH);
			}
			//Else, move back to previous spot
			else
			{
				//Remove the current position from the path
				updateMap(currentPos, Status.VISITED);
				//Find direction of previous position
				path.pop();
				Coordinates prev = path.top();
				Direction prevLoc = findPreviousDir(maze,droid,prev);
				//move droid to previous position
				currentPos = maze.move(droid, prevLoc);
			}
			
		}
	}
	
	//Handles the scanning of adjacent cells to move to
	private Direction findMove(Maze maze, Droid droid, Content[] adj, Coordinates currentPos)
	{
		Direction dir = null;
		
		for(int i = 0; i < adj.length && dir == null; i++)
		{
			if(adj[i] != Content.BLOCK && adj[i] != Content.NA)
			{
				if(i == 0)
				{
					if(map[currentPos.getX()][currentPos.getY() - 1][currentPos.getZ()].getCellState() == Status.NEW)
					{
						dir = Direction.D00;
					}
				}
				else if (i == 1)
				{
					if(map[currentPos.getX() + 1][currentPos.getY()][currentPos.getZ()].getCellState() == Status.NEW)
					{
						dir = Direction.D90;
					}
				}
				else if (i == 2)
				{
					if(map[currentPos.getX()][currentPos.getY() + 1][currentPos.getZ()].getCellState() == Status.NEW)
					{
						dir = Direction.D180;
					}
				}
				else if (i == 3)
				{
					if(map[currentPos.getX() - 1][currentPos.getY()][currentPos.getZ()].getCellState() == Status.NEW)
					{
						dir = Direction.D270;
					}
				}
			}
		}
		
		return dir;
	}
	
	//Handles the scanning of adjacent cells for blocks
	private void findBlocks(Maze maze, Droid droid, Content[] adj, Coordinates currentPos)
	{
		for(int j = 0; j < adj.length; j++)
		{
			if(adj[j] == Content.BLOCK)
			{		
				Coordinates dirPos = new Coordinates(currentPos.getX(),currentPos.getY(),currentPos.getZ());
			
				if(j == 0)
				{
					dirPos.setY(currentPos.getY() - 1);
				}
				else if (j == 1)
				{
					dirPos.setX(currentPos.getX() + 1);
				}
				else if (j == 2)
				{
					dirPos.setY(currentPos.getY() + 1);
				}
				else
				{
					dirPos.setX(currentPos.getX() - 1);
				}
				updateMap(dirPos,Status.BLOCK);
			}
		}
	}
	
	private Direction findPreviousDir(Maze maze, Droid droid, Coordinates prev)
	{
		Direction prevDir;
		Coordinates currentPos = maze.getCurrentCoordinates(droid);
		
		if(currentPos.getX() == prev.getX())
		{
			if((currentPos.getY() - 1) == prev.getY())
			{
				prevDir = Direction.D00;
			}
			else
			{
				prevDir = Direction.D180;
			}
		}
		else
		{
			if((currentPos.getX() + 1) == prev.getX())
			{
				prevDir = Direction.D90;
			}
			else
			{
				prevDir = Direction.D270;
			}
		}
		return prevDir;
	}
	
	//Initializes the droid's map of the maze
	private void createMap(Maze maze)
	{
		int len = maze.getMazeDim();
		int dep = maze.getMazeDepth();
		map = new Cell[len][len][dep];
		
		for(int l = 0; l < len; l++)
		{
			for(int w = 0; w < len; w++)
			{
				for(int d = 0; d < dep; d++)
				{
					map[l][w][d] = new Cell(Status.NEW);
				}
			}
		}
	}
	
	//Updates the droid's map
	private void updateMap(Coordinates pos, Status state)
	{
		map[pos.getX()][pos.getY()][pos.getZ()].setCellState(state);
	}
	
	//Output the droid's map
	private void printMap()
	{
		for(int d = 0; d < map[0][0].length; d++)
		{
			System.out.println("Level: " + d);
			for(int l = 0; l < map.length; l++)
			{
				for(int w = 0; w < map[0].length; w++)
				{
					System.out.print(map[w][l][d]);
				}
				System.out.println();
			}
		}
		System.out.println();
	}
}