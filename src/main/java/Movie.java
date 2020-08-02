public class Movie {
    private  int id;
    private String name;
    private String genre;
    private int reviews;
    private int ignored;

    public Movie(int id, String name, String genre) {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.reviews = 0;
        this.ignored = 0;
    }

    public int getIgnored() {
        return ignored;
    }

    public void setIgnored(int ignored) {
        this.ignored = ignored;
    }

    public void incrementReviews(){
        this.reviews += 1;
    }
    public int getId() {
        return id;
    }
    public int getReviews() {
        return reviews;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", genre='" + genre + '\'' +
                ", reviews=" + reviews +
                ", isIgnored = " + ignored +
                '}' + '\n';
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
