import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class pivotImplementation {

    private static Movie[] movies = new Movie[Defines.numOfMovies+1];
    private static double[][] correlMatrix;
    public static void main(String[] args) throws Exception {
        if (args.length < 2)
            throw new Exception("should be at least 2 arguments");
        String subSSetPath = args[0];
        String subsetPath = args[1];
        HashSet<Integer> subsetMovies = parseSubSet(subsetPath);
        String dataSetPath = subSSetPath+ "\\correlationData.txt";
        String ignoredPath = subSSetPath+ "\\ignoredMovies.txt";
        filterIgnoredMovies(subsetMovies , ignoredPath);
        correlMatrix = parseDataSet(dataSetPath,subsetMovies);
        List<Integer> subArrayMovies = new ArrayList<>(subsetMovies);
        HashSet<HashSet<Integer>> bigCluster = new HashSet<>();
        pivotAlgo(bigCluster,subArrayMovies);

        
        for(HashSet<Integer> c: bigCluster){
            for(Integer i: c) {
                System.out.printf("<%d> <%s>, ", movies[i].getId(), movies[i].getName());
            }
            System.out.println();
            System.out.println(calcCost(c));
        }
    }

    private static double calcCost(HashSet<Integer> cluster) {
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

    private static void filterIgnoredMovies(HashSet<Integer> subsetMovies , String path) throws IOException {
        BufferedReader bufferRead = new BufferedReader(new FileReader(path));
        String line = bufferRead.readLine();
        while (line != null) {
            String[] movie = line.split(" ");
            Integer movieId = Integer.parseInt(movie[0]);
            Integer movieRate = Integer.parseInt(movie[1]);
            if(subsetMovies.contains(movieId)){
                System.err.printf("Movies <%d> ignored because it has only <%d> ratings",movieId,movieRate);
                subsetMovies.remove(movieId);
            }
            line = bufferRead.readLine();
        }
    }

    public static HashSet<Integer> parseSubSet(String pathName) throws Exception{
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


    public static double[][] parseDataSet(String pathName, HashSet<Integer> subset) throws Exception {
        BufferedReader bufferRead = new BufferedReader(new FileReader(pathName));
        String line = bufferRead.readLine();
        double [][] correlMatrix = new double[Defines.numOfMovies][Defines.numOfMovies];
        while (line != null) {
            String[] Data = line.split("::"); //{m1.id,m1.name,m1.probability,m2.id,m2.name,m2.probability,p1p2}
            int m1Id = Integer.parseInt(Data[0]);
            int m2Id = Integer.parseInt(Data[3]);
            Movie m1 = movies[m1Id] == null ? new Movie(m1Id,Data[1],Double.parseDouble(Data[2])) : movies[m1Id];
            Movie m2 =  movies[m2Id] == null ? new Movie(m2Id,Data[4],Double.parseDouble(Data[5])) : movies[m2Id];
            double probability_p1p2 = Double.parseDouble(Data[6]);
            if(subset.contains(m1.getId()) &&
                    subset.contains(m2.getId())){
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


    public static boolean isNumeric(String strNum) {
        Integer d;
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
    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static void pivotAlgo(HashSet<HashSet<Integer>> bigCluster, List<Integer> movies){
        if(movies.isEmpty()){
            return;
        }
        int index = getRandomNumber(0,movies.size());
        Integer randPivot = movies.get(index);
        HashSet<Integer> Cluster = new HashSet<>();
        List<Integer> vTag = new ArrayList<>();
        Cluster.add(randPivot);
        for(int j = 0; j < movies.size(); j++){
            if (j == index) continue;
            Integer otherMovie = movies.get(j);
            int row = randPivot < otherMovie ? randPivot : otherMovie;
            int column = randPivot > otherMovie ? randPivot : otherMovie;
            if(correlMatrix[row][column] > 0 ){
                Cluster.add(otherMovie);
            }
            else{
                vTag.add(otherMovie);
            }
        }
        bigCluster.add(Cluster);
        pivotAlgo(bigCluster,vTag);
    }
}
