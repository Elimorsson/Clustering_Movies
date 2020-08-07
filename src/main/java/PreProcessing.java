
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class PreProcessing {
    protected static Movie [] movies = new Movie[Defines.numOfMovies + 1];
    private static User[] usersArr = new User[Defines.numOfUsers + 1];
    private static List<Movie> ignoredMovies = new LinkedList<>();

    public static void main(String[] args) throws IOException {

        readFromInput("movies.dat");
        readFromInput("users.dat");
        readFromInput("ratings.dat");


        List<Movie> finalMovies = Arrays.stream(movies).filter(Objects::nonNull).collect(Collectors.toList());
        movies = finalMovies.toArray(new Movie[0]);

        for (Movie movie : movies) {
            if (movie.getReviews() < 10) {
                movie.setIgnored(1);
                ignoredMovies.add(movie);
            }
        }

        finalMovies = Arrays.stream(movies).filter(movie -> movie.getIgnored()==0).collect(Collectors.toList());
        movies = finalMovies.toArray(new Movie[0]);


        FileWriter myWriter = createFile("correlationData.txt");
        for (int i = 0; i < movies.length; i++) {
            Movie m1 = movies[i];
            for (int j = i + 1; j < movies.length; j++) {
                Movie m2 = movies[j];
                double correlation = calcCorrelation(m1, m2);
                WriteToFile(m1, m2, correlation, myWriter);
            }
        }
        myWriter.close();
        myWriter = createFile("ignoredMovies.txt");
        for (Movie movie : ignoredMovies) {
            myWriter.write( movie.getId() + " " + movie.getReviews()+ "\n");
        }
        myWriter.close();
    }

    private static FileWriter createFile(String fileName) throws IOException {
        try {
            File file = new File(fileName);
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        FileWriter myWriter = new FileWriter(fileName);
        return myWriter;
    }
    private static void WriteToFile(Movie m1, Movie m2, double correlation, FileWriter myWriter) {
        try {
            myWriter.write(m1.getId()+ "::" + m1.getName() + "::" + m1.getProbability() + "::" +
                    m2.getId()+ "::" + m2.getName() + "::"  + m2.getProbability() + "::" +
                   correlation +"\n");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static double calcCorrelation(Movie m1, Movie m2){
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
            return  p1_2;
        }
        return -p1_2;
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

    public static void readFromInput(String fileName) throws IOException {
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


