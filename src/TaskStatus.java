public enum TaskStatus {

    NEW("NEW"),
    IN_PROGRESS("IN_PROGRESS"),
    DONE("DONE");

    private String title;

    TaskStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}