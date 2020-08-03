

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class PreProcessing {
    protected static Movie [] movies = new Movie[Defines.numOfMovies + 1];
    private static User[] usersArr = new User[Defines.numOfUsers + 1];

    public static void main(String[] args) throws IOException {

        readFromInput("movies.dat");
        readFromInput("users.dat");
        readFromInput("ratings.dat");


        List<Movie> finalMovies = Arrays.stream(movies).filter(Objects::nonNull).filter(movie -> movie.getIgnored()==0).collect(Collectors.toList());
        movies = finalMovies.toArray(new Movie[0]);

        for (Movie movie : movies) {
            if (movie.getReviews() < 10) {
                movie.setIgnored(1);
                // System.out.printf("Movie <%d> ignored because it has only <%d> ratings\n",m.getId(),m.getReviews());
            }
        }
        try {
            File DataSet = new File("DataSet2.dat");
            if (DataSet.createNewFile()) {
                System.out.println("File created: " + DataSet.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        FileWriter myWriter = new FileWriter("DataSet2.dat");
        for (int i = 0 ; i < movies.length; i++){
            Movie m1 = movies[i];
            for(int j = i + 1; j < movies.length; j++){
                Movie m2 = movies[j];
                int correlation = calcCorrelation(m1,m2);
                WriteToFile(m1.getId(),m2.getId(),correlation,myWriter);
            }
        }
        myWriter.close();
        System.out.println("Preprocessing is finished");
    }

    private static void WriteToFile(int id1, int id2, int correlation, FileWriter myWriter) {
        try {
            char c = correlation == 1  ? '+' : '-';
            myWriter.write(id1 + " " + id2 + " " + c + "\n");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static int calcCorrelation(Movie m1, Movie m2){
        double p1 = m1.getProbability();
        double p2 = m2.getProbability();
        if (p1 == 0.0) {
            p1 = calcProb(m1);
            m1.setProbability(p1);
        }
        if (p2 == 0.0) {
            p2 = calcProb(m2);
            m2.setProbability(p2);
        }
        double p1_2 = calcProb(m1,m2);
        if (p1_2 >= p1*p2){
            return  1;
        }
        return 0;
    }

    private static double calcProb(Movie m_j) {
        int n_i;
        double sigma = 0;
        for (int i = 1; i < usersArr.length; i++) {
            User u = usersArr[i];
            int[] userMovies = u.getMovies();
            n_i = u.getRatedMovies();
            sigma += (2.0 / n_i) * userMovies[m_j.getId()];
        }
        int k = Defines.numOfMovies;
        return (1.0 / (Defines.numOfUsers + 1)) * ((2.0 / k) + sigma);
    }

    private static double calcProb(Movie m_j, Movie m_t) {
        int n_i;
        double sigma = 0;
        for (int i = 1; i < Defines.numOfUsers; i++) {
            User u = usersArr[i];
            int[] userMovies = u.getMovies();
            n_i = u.getRatedMovies();
            double factor = (2.0 / (n_i * (n_i - 1)));
            sigma +=  factor * userMovies[m_j.getId()] * userMovies[m_t.getId()];
        }
        int k = Defines.numOfMovies;
        sigma += (2.0 / (k * (k - 1)));
        return (1.0 / (Defines.numOfUsers + 1)) * sigma ;
    }

    private static void readFromInput(String fileName) throws IOException {
        BufferedReader bufferRead = new BufferedReader(new FileReader(fileName));
        String line = bufferRead.readLine();

        while (line != null) {
            String[] Data = line.split("::");
            switch (fileName) {
                case "movies.dat": {
                    Movie m = new Movie(Integer.parseInt(Data[0]), Data[1], Data[2]);
                    movies[m.getId()] = m;
                    line = bufferRead.readLine();
                    break;
                }

                case "ratings.dat": {
                    int userId = Integer.parseInt(Data[0]);
                    int movieId = Integer.parseInt(Data[1]);
                    Rating r = new Rating(userId, movieId, Data[2], Data[3]);
                    movies[movieId].incrementReviews();
                    // usersArr[userId].addReview(r);
                    usersArr[userId].updateMovies(movieId);
                    usersArr[userId].increaseRatedMovies();
                    line = bufferRead.readLine();
                    break;
                }

                case "users.dat": {
                    int userId = Integer.parseInt(Data[0]);
                    String gender = Data[1];
                    int age = Integer.parseInt(Data[2]);
                    User u = new User(userId, gender, age, Data[3], Data[4]);
                    usersArr[userId] = u;
                    line = bufferRead.readLine();
                    break;
                }
            }
        }
    }
}


