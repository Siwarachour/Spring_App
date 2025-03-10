package models;

public enum ApplicationStatus {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected");

    private final String status;

    // Constructor to associate the string with the enum
    ApplicationStatus(String status) {
        this.status = status;
    }

    // Getter for the status
    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return this.status;
    }
}
