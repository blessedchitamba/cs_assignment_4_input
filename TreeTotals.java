import java.util.concurrent.RecursiveTask;

public class TreeTotals extends RecursiveTask<Double> {
   //instance variables
   String[] trees;
   float[][] terrain;
   float[] totals;
   int low, high;
   double subTotalSunlight;  //total for all trees in the subArray. this is the value thats returned by each thread
   String[] tempArr;
   private int xTerrainSize;
   private int yTerrainSize;
   private int treeXindex, treeYindex, extension, treeXend, treeYend;
   static final int SEQUENTIAL_CUTOFF=500;
   
   //constructor: needs the String[] array of tree coordinates, terrain, float[] totals to populate,
      //terrain sizes, and low and high indexes to reference in the String[] array
   TreeTotals(String[] t, float[][] ter, float[] tot, int xTerrainSize, int yTerrainSize, int lo, int hi) {
      trees = t;
      terrain = ter;
      totals = tot;
      low = lo;
      high = hi;
      this.xTerrainSize = xTerrainSize;
      this.yTerrainSize = yTerrainSize;
   }
   
   //overriding compute()
   protected Double compute() {
      if((high-low) < SEQUENTIAL_CUTOFF) {
         double ans = computeSequentially(trees, high, low);
         return ans;
      }
      else {
         //recursively create two parallel threads
         TreeTotals left = new TreeTotals(trees, terrain, totals, xTerrainSize, yTerrainSize, low, (high+low)/2);
         TreeTotals right = new TreeTotals(trees, terrain, totals, xTerrainSize, yTerrainSize, (high+low)/2, high);
         
         left.fork();  //send left thread to do its work independently. fork() is equivalent to start()
			double rightAns = right.compute();   //let this thread do the work in the right path. compute()<=>run()
		   double leftAns  = left.join();
			return leftAns + rightAns;
      }
   } //end of method
   
   //method to sequentially run through all the trees in the sub array and add their totals
   private double computeSequentially(String[] treeArr, int high, int low) {
   
      for(int k=low; k<high; k++) {
      
         //remember each array entry is a string in the form "Yindex Xindex Extension"
         tempArr = treeArr[k].split(" ");
         
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
         float treeTotal = 0;
         
         //add the total sunlight for that tree
         for(int i=treeYindex; i < treeYend; i++){
           for(int j=treeXindex; j < treeXend; j++){ 
              treeTotal += terrain[i][j];
              subTotalSunlight += (double)terrain[i][j];
           }
         }
         
         //add the tree total sunlight to the array of totals
         totals[k] = treeTotal;
         
      } //end of for loop
      return subTotalSunlight;

   } //end of method


}