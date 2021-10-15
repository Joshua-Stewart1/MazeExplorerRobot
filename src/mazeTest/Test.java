package mazeTest;

import mazePD.Droid;
import mazePD.Maze;
import mazePD.Maze.MazeMode;

public class Test
{
	public static void main(String args[])
	{
		Maze maze = new Maze(7,3,MazeMode.NORMAL);
		Droid droid = new Droid("R2D2");
		printMaze(maze);
		
		droid.runMaze(maze);
		
		System.out.println("\nFull Map:");
		printMaze(maze);
	}
	
	public static void printMaze(Maze maze)
	{
		System.out.println(maze.toString());
		
		for(int i = 0; i < maze.getMazeDepth(); i++)
		{
			System.out.println("Level: " + i);
			
			String[] level = maze.toStringLevel(i);
			
			for(int j = 0; j < maze.getMazeDim(); j++)
			{
				System.out.println(level[j].toString());
			}
		}
		System.out.println();
	}
}
