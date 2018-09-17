import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.io.File;

public class SequentialAdd {
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
   private static String[] tempArr, trees;
   private static float[] totals;
   
   //methods to deal with the time measuring
   static long startTime = 0;
   private static void tick(){
		startTime = System.currentTimeMillis();
	}
   private static float tock(){
		return (System.currentTimeMillis() - startTime) / 1000.0f; 
	}
   
   public static void main(String[] args) throws FileNotFoundException {
   
            //check if command line argument is empty
            if(args.length!=0) {
               inputFileName = args[0];
               outputFileName = args[1];
            }
            else {
               inputFileName = "sample_input.txt";
               outputFileName = "sequential_output.txt";
            }
            
            //open file to read contents
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

            
            //calculate average sunlight for each tree
            counter = 1;
            treeTotal = 0;
            grandTotal = 0;
            
            tick();
            while(counter<=numTrees) {
               //remember each array entry is a string in the form "Yindex Xindex Extension"
               tempArr = trees[counter-1].split(" ");
               
               //each tempArr represents each tree's coordinates. extract x and y as below
               treeYindex = Integer.parseInt(tempArr[0]);
               treeXindex = Integer.parseInt(tempArr[1]);
               extension = Integer.parseInt(tempArr[2]);
               treeXend = treeXindex+extension;
               treeYend = treeYindex+extension;  
                            
               //deal with the different cases of trees extending beyond the terrain size
               if (treeXend > xTerrainSize){
                  treeXend = xTerrainSize;
               }
               if (treeYend > yTerrainSize){
                  treeYend = yTerrainSize;
               }
               treeTotal = 0;
               
                  for(int i=treeYindex; i < treeYend; i++){
                    for(int j=treeXindex; j < treeXend; j++){ 
                       treeTotal += terrain[i][j];
                       grandTotal += (double)terrain[i][j];
                    }
                  }
               
               //add the total to totals[] and increment counter
               totals[counter-1] = treeTotal;
               counter++;
            } //end of while loop
            
            //record elapsed time
            float time = tock();
            System.out.println("Sequential program: Time taken while adding is "+time+" seconds.");
            
            // //write things to file
            try {
               writeToFile(totals);
            }
            catch (IOException e){}
                        
            
      } //end of main method
      
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