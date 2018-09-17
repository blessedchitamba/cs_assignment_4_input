import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.io.File;
import java.util.Arrays;

import java.util.concurrent.ForkJoinPool;

public class ParallelAdd {
   private static float[][] terrain;
   private static String inputFileName;
   private static String outputFileName;
   private static int xTerrainSize;
   private static int yTerrainSize;
   private static int counter;
   private static int numTrees;
   private static int treeXindex, treeYindex, extension, treeXend, treeYend;
   private static float treeTotal;
   private static double grandTotal;
   private static float[] totals;
   private static String[] trees;
   
   //methods to deal with the time measuring
   static long startTime = 0;
   private static void tick(){
		startTime = System.currentTimeMillis();
	}
   private static float tock(){
		return (System.currentTimeMillis() - startTime) / 1000.0f; 
	}

   static final ForkJoinPool fjPool = new ForkJoinPool();
   
   //method that makes use of ForkJoin Pool
   public static double totalSunlight(String[] t) {
      return fjPool.invoke(new TreeTotals(t, terrain, totals, xTerrainSize, yTerrainSize, 0, t.length));
   }
   
   //main method
   public static void main(String[] args) throws FileNotFoundException {
      
      //check if command line argument is empty
      if(args.length!=0) {
         inputFileName = args[0];
         outputFileName = args[1];
      }
      else {
         inputFileName = "sample_input.txt";
         outputFileName = "parallel_output.txt";
      }
      
      //read contents from file
      File input = new File(inputFileName);
      Scanner scanner = new Scanner(input);
           
      String[] firstLine = scanner.nextLine().split(" ");
      xTerrainSize = Integer.parseInt(firstLine[0]); yTerrainSize = Integer.parseInt(firstLine[1]);
      
      //load the terrain matrix
      terrain = new float[xTerrainSize][yTerrainSize];
      for (int i=0; i<yTerrainSize; i++){
         for (int j=0; j<xTerrainSize; j++) {
            terrain[i][j] = scanner.nextFloat();
         }
      }   
            
      //deal with the trees now. take number of trees and initialize totals[]
      numTrees = scanner.nextInt();
      scanner.nextLine();
      totals = new float[numTrees];
      
      //load trees into trees array from file
      trees = new String[numTrees];
      counter=0;
      while(counter<1000000) {
         trees[counter] = scanner.nextLine().trim();
         counter++;
      }
      
      //call upon the method do start the multithreading and assign the returned value to the grandTotal var
      tick();
      grandTotal = totalSunlight(trees);
      float time = tock();
      System.out.println("Parallel program: The addition took "+time+"seconds with SQC=500.");

      //write things to file
      try {
         writeToFile(totals);
      }
      catch (IOException e){}
                  
            
   } //end of main method      
      
   //method to write results to output file.
   public static void writeToFile(float[] array) throws IOException {
      FileWriter writer = new FileWriter(outputFileName, true);
      BufferedWriter bf = new BufferedWriter(writer);
      
      bf.write(Double.toString(grandTotal/numTrees));
      bf.newLine();
      bf.write(Integer.toString(numTrees));
      bf.newLine();
      
      //write tree totals
      for(float t: array){
         bf.write(Float.toString(t));
         bf.newLine();
      }
      bf.close();
   }
      
}
