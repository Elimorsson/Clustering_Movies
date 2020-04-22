public class Rating {
    private int userID;
    private int movieID;
    private int rating;
    private int timestamp;

    public Rating(int userID, int movieID, String rating, String timestamp) {
        this.userID = userID;
        this.movieID = movieID;
        this.rating = Integer.parseInt(rating);
        this.timestamp = Integer.parseInt(timestamp);
    }

    @Override
    public String toString() {
        return "Rating{" +
                "userID=" + userID +
                ", movieID=" + movieID +
                ", rating=" + rating +
                ", timestamp=" + timestamp +
                "}\n";
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getMovieID() {
        return movieID;
    }

    public void setMovieID(int movieID) {
        this.movieID = movieID;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        timestamp = timestamp;
    }
}
