

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
                //ignoredMovies.add(m.getId());
                // System.out.printf("Movie <%d> ignored because it has only <%d> ratings\n",m.getId(),m.getReviews());
            }
        }
        try {
            File DataSet = new File("DataSet.txt");
            if (DataSet.createNewFile()) {
                System.out.println("File created: " + DataSet.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        FileWriter myWriter = new FileWriter("DataSet.txt");
        for (int i = 0 ; i < movies.length; i++){
            Movie m1 = movies[i];
            for(int j = i + 1; j < movies.length; j++){
                Movie m2 = movies[j];
                int correl = calcCorrelation(m1,m2);
//                WriteToFile(m1.getId(),m2.getId(),correl,myWriter);
            }
        }
//        myWriter.close();
        System.out.println("Preproccesing is finished\n");
    }

    private static void WriteToFile(int id1, int id2, int correl, FileWriter myWriter) {
        try {

            char c = correl == 1  ? '+' : '-';
            myWriter.write(id1 + " " + id2 + " " + c + "\n");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    private static float calcProb(Movie m_j) {
        int n_i = 0;
        float sigma = 0;
        for (int i = 1; i < usersArr.length; i++) {
            User u = usersArr[i];
            int[] movies = u.getMovies();
            n_i = u.getRatedMovies();
            sigma = (2 /(float) n_i) * movies[m_j.getId()];
        }
        int k = Defines.numOfMovies;
        return (1 / (float)(Defines.numOfUsers + 1)) * ((2 / (float)k) + sigma);

    }

    private static float calcProb(Movie m_j, Movie m_t) {
        int n_i = 0;
        float sigma = 0;
        for (int i = 1; i < Defines.numOfUsers; i++) {
            User u = usersArr[i];
            int[] movies = u.getMovies();
            n_i = u.getRatedMovies();
            sigma = (2 / (float) (n_i * (n_i - 1))) * movies[m_j.getId()] * movies[m_t.getId()];
        }
        int k = Defines.numOfMovies;
        return  (1 / (float) (Defines.numOfUsers + 1)) * ((2 / (float) (k * (k + 1))) + sigma);

    }

    private static int calcCorrelation(Movie m1, Movie m2){
        float p1 = calcProb(m1);
        float p2 = calcProb(m2);
        float p1_2 = calcProb(m1,m2);
        if (p1_2 >= p1*p2){
            return  1;
        }
        return 0;
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


