import com.sun.jdi.connect.Connector;

import java.util.ArrayList;

public class User {
    private int userID;
    private String gender;
    private int age;
    private int occupation;
    private int zipCode;
   //private ArrayList<Rating> reviews;
    private int[] movies;


    public User(int userID, String gender, int age, String occupation, String zipCode) {
        this.userID = userID;
        this.gender = gender;
        this.age = age;
        this.occupation = Integer.parseInt(occupation);
        this.zipCode = Integer.parseInt(zipCode);
       // reviews = new ArrayList<>();
        movies = new int[Defines.numOfMovies + 1];    //initialized with zeros for all values
    }

/*
    public void addReview(Rating r){
        reviews.add(r);
    }
*/
    public void updateMovies(int movieId){
        movies[movieId] = 1;
    }
    public int[] getMovies() {
        return movies;
    }

    public void setMovies(int[] movies) {
        this.movies = movies;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getOccupation() {
        return occupation;
    }

    public void setOccupation(int occupation) {
        this.occupation = occupation;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }


}
