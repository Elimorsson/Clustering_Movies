import java.util.*;

public class improveImplementation {

    private static Movie[] movies = new Movie[Defines.numOfMovies+1];
    private static double[][] correlMatrix;
    private static pivotImplementation pv = new pivotImplementation();
    public static void main(String[] args) throws Exception {

        pv.main(args);
        movies = pv.movies;
        correlMatrix = pv.correlMatrix;
        HashSet<ArrayList<Integer>> bigCluster = pv.bigCluster;

        runImproveAlgo(bigCluster);
        double sumImprove = pv.printCost(bigCluster);
//        System.out.println(sumImprove);
        System.out.printf(" the pivot is %f the best of the best is %f",pv.sumPivot,sumImprove);
    }

    private static void runImproveAlgo(HashSet<ArrayList<Integer>> bigCluster) {
        double prevBigClusterCost= Double.MAX_VALUE, bigClusterCost = 0.0;
        boolean firstIter = true;
        do {
            if (!firstIter) {
                prevBigClusterCost = bigClusterCost;
            }
            else {
                firstIter = false;
            }
            ArrayList<Integer> minMoviesIds = findTheLowestCorrel(bigCluster);
            bigClusterCost = insertBestPosition(minMoviesIds, bigCluster); //return the bigCluster Cost
        }
        while(prevBigClusterCost - bigClusterCost > 100);

    }

    private static double insertBestPosition(ArrayList<Integer> minMoviesIds, HashSet<ArrayList<Integer>> bigCluster) {
        double maxAccumulator = Double.MIN_VALUE;
        double bestClusterCost, isolatedClusterCost = 0,insertedBigClusterCost = 0, entireCostSubGruop, probMovieId,bestClusterNewCost;
        ArrayList<Integer> bestCluster = null;
        for (int movieId : minMoviesIds) {
            for (ArrayList<Integer> c : bigCluster) {
                double tempAcc = (double)calcCorrel(c, movieId)/c.size();
                if (tempAcc > maxAccumulator) {
                    maxAccumulator = tempAcc;
                    bestCluster = c;
                }
            }
            if(bestCluster == null){
                ArrayList<Integer> c = new ArrayList<>();
                c.add(movieId);
                bigCluster.add(c);
                continue;
            }
            probMovieId = movies[movieId].getProbability();
            entireCostSubGruop = calcEntireCost(bigCluster, bestCluster);
            bestClusterCost = pv.calcCost(bestCluster);
            isolatedClusterCost = entireCostSubGruop + bestClusterCost + Math.log(1.0 / probMovieId);
            bigCluster.remove(bestCluster);
            bestCluster.add(movieId);
            bestClusterNewCost = pv.calcCost(bestCluster);
            insertedBigClusterCost = entireCostSubGruop + bestClusterNewCost;
            if (isolatedClusterCost < insertedBigClusterCost) {
                bestCluster.remove(Integer.valueOf(movieId));
                ArrayList<Integer> newCluster = new ArrayList<>();
                newCluster.add(movieId);
                bigCluster.add(newCluster);
            }
            bigCluster.add(bestCluster);
        }

        return Math.min(isolatedClusterCost,insertedBigClusterCost);
    }


    protected static double calcEntireCost(HashSet<ArrayList<Integer>> bigCluster,ArrayList<Integer> ignoredCluster) {
        double sumPivot = 0;
        for(ArrayList<Integer> c: bigCluster){
            if (c == ignoredCluster) continue;
            double cost = pv.calcCost(c);
            sumPivot +=cost;
        }
        return sumPivot;
    }

    private static ArrayList<Integer> findTheLowestCorrel(HashSet<ArrayList<Integer>> bigCluster) {
        ArrayList<Integer> minMoviesIds = new ArrayList<>();
        HashSet<ArrayList<Integer>> toRemoveSet = new LinkedHashSet<>();
        HashMap<ArrayList<Integer>,Integer> elementToRemoveIncluster = new HashMap<>();
        for (ArrayList<Integer> c : bigCluster) {
            if (c.size() == 1){
                minMoviesIds.add(c.get(0));
                toRemoveSet.add(c);
                continue;
            }
            int minCorrel = Integer.MAX_VALUE;
            int minMovieId = -1;
            for (Integer movieId : c) {        //we have more that 1 elements and we pick one element to remove
                int tempCorrel = calcCorrel(c,movieId);
                if (tempCorrel < minCorrel) {
                    minCorrel = tempCorrel ;
                    minMovieId = movieId;
                }
            }
            minMoviesIds.add(minMovieId);
            elementToRemoveIncluster.put(c,minMovieId);
//            c.remove(Integer.valueOf(minMovieId));      //the min size is 1
        }
        for (ArrayList<Integer> clusterToRemove : toRemoveSet) {
            bigCluster.remove(clusterToRemove);
        }
        for(ArrayList<Integer> c : elementToRemoveIncluster.keySet()){
            Integer idToRemove = elementToRemoveIncluster.get(c);
            bigCluster.remove(c);
            c.remove(idToRemove);
            bigCluster.add(c);
        }
        return minMoviesIds;
    }

    private static int calcCorrel(ArrayList<Integer> cluster, Integer movieId) {
        int accumulator = 0;
        for (int otherMovieId : cluster) {
            int row = Math.min(movieId, otherMovieId);
            int col = Math.max(movieId, otherMovieId);
            accumulator += correlMatrix[row][col] > 0 ? 1 : 0;
        }
        return accumulator;
    }


}
