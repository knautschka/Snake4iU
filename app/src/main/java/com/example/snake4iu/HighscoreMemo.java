package com.example.snake4iu;

public class HighscoreMemo {

    private String username;
    private int scoredPoints;
    private long id;

    public HighscoreMemo(String username, int scoredPoints, long id) {
        this.username = username;
        this.scoredPoints = scoredPoints;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScoredPoints() {
        return scoredPoints;
    }

    public void setScoredPoints(int scoredPoints) {
        this.scoredPoints = scoredPoints;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        String output = username +": " + scoredPoints;

        return output;
    }
}
