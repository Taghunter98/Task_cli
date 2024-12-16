import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a task with a unique identifier, description, status, and timestamps for creation and updates.
 * This class provides functionality to manage tasks, including updating descriptions, status, and the last
 * updated timestamp.
 */
public class Task {
    private final int id;
    private String description;
    private String status;
    private final String createdAt;
    private String updatedAt;

    /**
     * Constructs a new Task with the specified description.
     * The task is assigned a unique identifier, a "todo" status,
     * and timestamps for when it was created and last updated.
     * The updated timestamp is initially null.
     *
     * @param description The description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.id = createId();
        this.status = "todo";

        this.createdAt = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date());
        this.updatedAt = null;
    }

    /**
     * Generates a unique identifier by reading the last stored ID from a file,
     * incrementing it, and saving the updated ID back to the file.
     * If the file does not exist or is empty, the default starting ID is 1.
     *
     * @return The newly generated unique identifier as an integer.
     */
    public int createId() {
        int lastId = 0; // default value if the file doesn't exist or is empty
        Path filePath = Paths.get("id.txt");

        try {
            // check if the file exists and read the last ID
            if (Files.exists(filePath)) {
                String content = Files.readString(filePath).trim();
                if (!content.isEmpty()) {
                    lastId = Integer.parseInt(content);
                }
            }

            // Increment the ID
            lastId++;

            // Write the updated ID back to the file
            Files.writeString(filePath, String.valueOf(lastId));
        } catch (IOException e) {
            System.out.println("Error reading or writing file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("File content is not a valid number: " + e.getMessage());
        }

        return lastId; // Return the newly generated ID
    }

    /**
     * Getter methods for the Task class.
     *
     * @return
     * - The unique identifier of the task as an integer.
     * - The description of the task as a String.
     * - The status of the task as a String.
     * - The date the task was created at.
     * - The date the task was updated at.
     */
    public int getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public String getStatus() {
        return this.status;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public String getUpdatedAt() {
        return this.updatedAt;
    }


    /**
     * Updates the description of the task.
     *
     * @param newDescription The new description to set for the task.
     */
    public void updateDescription(String newDescription) {
        this.description = newDescription;
    }

    /**
     * Updates the status of the task.
     *
     * @param status The new status to set for the task.
     */
    public void updateStatus(String status) {
        this.status = status;
    }

    /**
     * Updates the last updated timestamp of the task.
     *
     * @param updatedAt The new timestamp to set for the 'updatedAt' field.
     */
    public void update(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}

