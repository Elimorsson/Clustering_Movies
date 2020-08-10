import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class pivotImplementation {
    protected Movie[] movies = new Movie[Defines.numOfMovies+1];
    protected double[][] correlMatrix;
    protected HashSet<ArrayList<Integer>> bigCluster = new HashSet<>();
    protected double sumPivot = 0;
    public void runAlgo(String[] args) throws Exception {
        if (args.length < 2)
            throw new Exception("should be at least 2 arguments");
        String subSSetPath = args[0];
        String subsetPath = args[1];
        int pivotOrImprove = Integer.parseInt(args[2]);
        HashSet<Integer> subsetMovies = parseSubSet(subsetPath);
        String dataSetPath = subSSetPath+ "/correlationData.txt";
        String ignoredPath = subSSetPath+ "/ignoredMovies.txt";
        filterIgnoredMovies(subsetMovies , ignoredPath);
        correlMatrix = parseDataSet(dataSetPath,subsetMovies);
        List<Integer> subArrayMovies = new ArrayList<>(subsetMovies);
        pivotAlgo(bigCluster,subArrayMovies);

        //if (pivotOrImprove == 1) {
        sumPivot = printCost(bigCluster);
        System.out.println();
        System.out.println(sumPivot);
        //}
    }
    public static void main(String[] args) throws Exception {
        pivotImplementation pv = new pivotImplementation();
        pv.runAlgo(args);

    }

    protected double printCost(HashSet<ArrayList<Integer>> bigCluster){
        double sumAlgo = 0;
        for(ArrayList<Integer> c: bigCluster){
            System.out.printf("<%d> <%s> ", movies[c.get(0)].getId(), movies[c.get(0)].getName());
            for(int i = 1 ; i<c.size(); i++) {
                System.out.printf(",<%d> <%s> ", movies[c.get(i)].getId(), movies[c.get(i)].getName());
            }
            System.out.println();
            double cost = calcCost(c);
            System.out.println(cost);
            sumAlgo +=cost;
        }
        return sumAlgo;

    }

    protected double calcCost(ArrayList<Integer> cluster) {
        double sigma = 0;
        for (Integer i: cluster) {
            double factor = 1.0 / (cluster.size() - 1);
            if (cluster.size() == 1) {
                return Math.log(1.0 / movies[i].getProbability());
            }
            for (Integer j : cluster) {
                if (i.equals(j)) continue;
                int row = i < j ? i : j;
                int coll = i > j ? i : j;

                sigma += factor * Math.log(1.0 / Math.abs(correlMatrix[row][coll]));
            }
        }
        return sigma;
    }

    private void filterIgnoredMovies(HashSet<Integer> subsetMovies , String path) throws IOException {
        BufferedReader bufferRead = new BufferedReader(new FileReader(path));
        String line = bufferRead.readLine();
        while (line != null) {
            String[] movie = line.split(" ");
            Integer movieId = Integer.parseInt(movie[0]);
            Integer movieRate = Integer.parseInt(movie[1]);
            if(subsetMovies.contains(movieId)){
                System.err.printf("Movies <%d> ignored because it has only <%d> ratings\n",movieId,movieRate);
                subsetMovies.remove(movieId);
            }
            line = bufferRead.readLine();
        }
    }

    private HashSet<Integer> parseSubSet(String pathName) throws Exception{
        BufferedReader bufferRead = new BufferedReader(new FileReader(pathName));
        String line = bufferRead.readLine();
        HashSet<Integer> movies = new HashSet<>();
        while (line != null) {
            String[] Data = line.split(" ");
            if (Data.length > 1 || !isNumeric(Data[0])) {
                throw new Exception("illegal input");
            }
            movies.add(Integer.parseInt(line));
            line = bufferRead.readLine();
        }
        return movies;
    }


    private double[][] parseDataSet(String pathName, HashSet<Integer> subset) throws Exception {
        BufferedReader bufferRead = new BufferedReader(new FileReader(pathName));
        String line = bufferRead.readLine();
        double [][] correlMatrix = new double[Defines.numOfMovies+1][Defines.numOfMovies+1];
        while (line != null) {
            String[] Data = line.split("::"); //{m1.id,m1.name,m1.probability,m2.id,m2.name,m2.probability,p1p2}
            int m1Id = Integer.parseInt(Data[0]);
            int m2Id = Integer.parseInt(Data[3]);
            Movie m1 = movies[m1Id] == null ? new Movie(m1Id,Data[1],Double.parseDouble(Data[2])) : movies[m1Id];
            Movie m2 =  movies[m2Id] == null ? new Movie(m2Id,Data[4],Double.parseDouble(Data[5])) : movies[m2Id];
            double probability_p1p2 = Double.parseDouble(Data[6]);
            if(subset.contains(m1.getId()) && subset.contains(m2.getId())){
                movies[m1.getId()] = m1;
                movies[m2.getId()] = m2;
                if(m1.getId()>m2.getId()){
                    correlMatrix[m2.getId()][m1.getId()] = probability_p1p2;
                }
                else correlMatrix[m1.getId()][m2.getId()] = probability_p1p2;
            }

            line = bufferRead.readLine();
        }
        return correlMatrix;
    }


    private static boolean isNumeric(String strNum) {
        int d;
        if (strNum == null) {
            return false;
        }
        try {
            d = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return d <= Defines.numOfMovies;
    }
    private static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private void pivotAlgo(HashSet<ArrayList<Integer>> bigCluster, List<Integer> cluster){
        if(cluster.isEmpty()){
            return;
        }
        int index = getRandomNumber(0,cluster.size());
        Integer randPivot = cluster.get(index);
        ArrayList<Integer> newCluster = new ArrayList<>();
        List<Integer> vTag = new ArrayList<>();
        newCluster.add(randPivot);
        for(int j = 0; j < cluster.size(); j++){
            if (j == index) continue;
            Integer otherMovie = cluster.get(j);
            int row = randPivot < otherMovie ? randPivot : otherMovie;
            int column = randPivot > otherMovie ? randPivot : otherMovie;
            if(correlMatrix[row][column] > 0 ){
                newCluster.add(otherMovie);
            }
            else{
                vTag.add(otherMovie);
            }
        }
        bigCluster.add(newCluster);
        pivotAlgo(bigCluster,vTag);
    }
}
