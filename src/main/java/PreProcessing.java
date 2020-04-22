import org.graalvm.compiler.lir.LIRInstruction;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

public class PreProcessing {
    protected static Movie[] moviesArr = new Movie[Defines.numOfMovies + 1];
    private static User[] usersArr = new User[Defines.numOfUsers + 1];
    private static HashSet<Integer> ignoredMovies = new HashSet<Integer>();

    public static void main(String[] args) throws IOException {

        readFromInput("movies.dat");
        readFromInput("ratings.dat");
        readFromInput("users.dat");

        for (Movie m : moviesArr) {
            if (m.reviews < 10) ignoredMovies.add(m.getId());
        }

    }

    private static float calcProb(Movie m_j) {
        int n_i = 0;
        float sigma = 0;
        for (int i = 0; i < Defines.numOfUsers + 1; i++) {
            User u = usersArr[i];
            int[] movies = u.getMovies();
            for (int j = 0; j < movies.length; i++) {
                n_i += movies[j];
            }
            sigma = (2 / n_i) * movies[m_j.getId()];
        }
        int k = Defines.numOfMovies;
        float prob = (1 / (Defines.numOfUsers + 1)) * ((2 / k) + sigma);
        return prob;
    }

    private static float calcProb(Movie m_j, Movie m_t) {
        int n_i = 0;
        float sigma = 0;
        for (int i = 1; i < Defines.numOfUsers; i++) {
            User u = usersArr[i];
            int[] movies = u.getMovies();
            for (int j = 1; j < movies.length; j++) {
                n_i += movies[j];
            }
            sigma = (2 / (n_i * (n_i - 1))) * movies[m_j.getId()] * movies[m_t.getId()];
        }
        int k = Defines.numOfMovies;
        float prob = (1 / (Defines.numOfUsers + 1)) * ((2 / (k * (k + 1))) + sigma);
        return prob;
    }

    private static void readFromInput(String fileName) throws IOException {
        BufferedReader bufferRead = new BufferedReader(new FileReader(fileName));
        String line = bufferRead.readLine();

        while (line != null) {
            String[] Data = line.split("::");
            switch (fileName) {
                case "movies.dat": {
                    Movie m = new Movie(Integer.parseInt(Data[0]), Data[1], Data[2]);
                    moviesArr[m.getId()] = m;
                    line = bufferRead.readLine();
                    break;
                }

                case "ratings.dat": {
                    int userId = Integer.parseInt(Data[0]);
                    int movieId = Integer.parseInt(Data[1]);
                    Rating r = new Rating(userId, movieId, Data[2], Data[3]);
                    moviesArr[movieId].incrementReviews();
                    // usersArr[userId].addReview(r);
                    usersArr[userId].updateMovies(movieId);

                    line = bufferRead.readLine();
                    break;
                }

                case "user.dat": {
                    int userId = Integer.parseInt(Data[0]);
                    String gender = Data[1];
                    int age = Integer.parseInt(Data[2]);
                    User u = new User(userId, gender, age, Data[3], Data[4]);
                    usersArr[userId] = u;
                    break;
                }
            }
        }
    }


}


