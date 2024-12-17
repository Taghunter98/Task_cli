
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Task_cli is a command-line interface application for managing tasks. It provides functionality
 * to add, update, delete, and view tasks stored in a JSON file. Tasks can be categorized based
 * on statuses such as "todo", "in-progress", and "done". The application processes user commands
 * passed as arguments.
 */
public class Task_cli {

    // gson instance for handling JSON parsing
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Name of the JSON file
    private static final String FILE_NAME = "Tasks.json";

    // Messages for CLI
    private static final String INSUFFICIENT_ARGS = "Insufficient arguments: ";
    private static final String HELP_MESSAGE = "Use 'help' to display available commands.";

    /**
     * The entry point for the Task CLI application. This method parses the command-line arguments,
     * determines the command to execute, and invokes the appropriate handler method for each command.
     *
     * Recognized commands include:
     * - add: Adds a new task.
     * - display: Displays tasks based on provided filters.
     * - delete: Deletes a task by its ID.
     * - update: Updates the status of a task by its ID.
     * - update_name: Updates the description of a task by its ID.
     * - status: Displays the status of a specific task by its ID.
     * - created: Shows the creation date of a task.
     * - updated: Shows the last updated date of a task.
     * - help: Displays usage information.
     * - quit: Exits the application.
     *
     * Additional arguments are required for some commands, and error messages are displayed for invalid input.
     *
     * @param args The command-line arguments array. The first argument specifies the command to execute,
     *             and additional arguments (if any) provide input data for the command.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No command provided! " + HELP_MESSAGE);
            return;
        }

        String command = args[0].toLowerCase();
        try {
            switch (command) {
                case "add":
                    handleAddCommand(args);
                    break;
                case "display":
                    handleDisplayCommand(args);
                    break;
                case "delete":
                    handleDeleteCommand(args);
                    break;
                case "update":
                    handleUpdateCommand(args);
                    break;
                case "update_name":
                    handleUpdateNameCommand(args);
                    break;
                case "status":
                    handleStatusCommand(args);
                    break;
                case "created":
                    handleCreatedCommand(args);
                    break;
                case "updated":
                    handleUpdatedCommand(args);
                    break;
                case "help":
                    if (args.length > 1) {
                        System.out.println("Too many arguments for 'help': Provide no arguments.");
                    } else {
                        help();
                    }
                    break;
                case "quit":
                    System.exit(0);
                default:
                    System.out.println("Unknown command: " + command + ". " + HELP_MESSAGE);
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Handles the "add" command by creating a new task with the provided description.
     * This method validates the number of arguments, extracts the task description,
     * creates a new task, and outputs the task's description and ID if successfully added.
     *
     * @param args The array of command-line arguments. The second argument specifies the
     *             task description to be added. The method expects this array to have
     *             at least two elements: the command name and the task description.
     */
    private static void handleAddCommand(String[] args) {
        validateArgsLength(args, 2, INSUFFICIENT_ARGS + "'add': " + HELP_MESSAGE);
        String description = args[1];
        createTask(description);

        List<Task> tasks = readTasks();
        if (!tasks.isEmpty()) {
            int id = tasks.get(tasks.size() - 1).getId();
            System.out.println("Task created: " + description + "  ID:" + id);
        } else {
            System.out.println("Failed to retrieve task ID after creation.");
        }
    }

    /**
     * Handles the "display" command by displaying tasks based on provided arguments.
     * If only the "display" command is provided, all tasks are displayed.
     * If a specific status is provided as an additional argument, tasks with
     * the specified status are displayed.
     *
     * @param args The array of command-line arguments. The first argument is
     *             the command ("display"), and the second (optional) argument
     *             specifies the task status filter.
     */
    private static void handleDisplayCommand(String[] args) {
        if (args.length == 1) {
            displayAllTasks();
        } else if (args.length == 2) {
            displayTasksStatus(args[1]);
        } else {
            System.out.println("Too many arguments for 'display'. " + HELP_MESSAGE);
        }
    }

    /**
     * Handles the "delete" command by removing a task identified by its unique ID.
     * This method validates the number of arguments provided, parses the task ID
     * from the input, and invokes the deleteTask method to delete the specified task.
     *
     * @param args The array of command-line arguments. The first element specifies the
     *             command ("delete"), and the second element specifies the task ID
     *             to be deleted.
     */
    private static void handleDeleteCommand(String[] args) {
        validateArgsLength(args, 2, INSUFFICIENT_ARGS + "'delete': Provide a task ID.");
        int id = parseInt(args[1], "Invalid task ID.");
        deleteTask(id);
    }

    /**
     * Handles the "update" command by updating the status of a task identified by its unique ID.
     * This method validates the provided arguments, parses the task ID and the status from input,
     * and invokes the method to update the task's status.
     *
     * @param args The array of command-line arguments. The first element specifies the
     *             command ("update"), the second element specifies the task ID to be
     *             updated (as an integer), and the third element specifies the new status
     *             (as an integer).
     */
    private static void handleUpdateCommand(String[] args) {
        validateArgsLength(args, 3, INSUFFICIENT_ARGS + "'update': Provide a task ID and a status.");
        int id = parseInt(args[1], "Invalid task ID.");
        int status = parseInt(args[2], "Invalid status value.");
        updateTask(id, status);
    }

    /**
     * Handles the "update_name" command by updating the description of a task identified by its unique ID.
     * This method validates the arguments, parses the task ID and new description from input,
     * and invokes the method to update the task's description.
     *
     * @param args The array of command-line arguments. The first element specifies the
     *             command ("update_name"), the second element specifies the task ID to be
     *             updated (as an integer), and the third element specifies the new description.
     */
    private static void handleUpdateNameCommand(String[] args) {
        validateArgsLength(args, 3, INSUFFICIENT_ARGS + "'updateName': Provide a task ID and new description.");
        int id = parseInt(args[1], "Invalid task ID.");
        String newDescription = args[2];
        updateTaskDescription(newDescription, id);
    }

    /**
     * Handles the "status" command by retrieving and displaying the status of a specific task
     * based on the task ID provided in the arguments.
     *
     * @param args the array of command arguments where the second argument is expected
     *             to be the task ID to check the status of
     */
    private static void handleStatusCommand(String[] args) {
        validateArgsLength(args, 2, INSUFFICIENT_ARGS + "'status': Provide a task ID.");
        int id = parseInt(args[1], "Invalid task ID.");
        Task task = findTaskById(id);
        if (task != null) {
            System.out.println(task.getDescription() + " Status: " + task.getStatus().toUpperCase());
        }
    }

    /**
     * Handles the 'created' command by validating the arguments, retrieving the task information,
     * and printing the task's description along with its creation timestamp.
     *
     * @param args the array of command-line arguments, where the second element should be a task ID
     */
    private static void handleCreatedCommand(String[] args) {
        validateArgsLength(args, 2, INSUFFICIENT_ARGS + "'created': Provide a task ID.");
        int id = parseInt(args[1], "Invalid task ID.");
        Task task = findTaskById(id);
        if (task != null) {
            System.out.println(task.getDescription() + " Created: " + task.getCreatedAt());
        }
    }

    /**
     * Handles the "updated" command, validating the input arguments,
     * finding the task by ID, and printing its updated details.
     *
     * @param args the command-line arguments passed to the method,
     *             where the second argument is expected to be the task ID.
     */
    private static void handleUpdatedCommand(String[] args) {
        if (args.length == 2) {
            int id = parseInt(args[1], "Invalid task ID.");
            Task task = findTaskById(id);
            if (task != null) {
                System.out.println(task.getDescription() + " Updated: " + task.getUpdatedAt());
            }
        } else {
            System.out.println("Too many arguments for 'updated'. Provide no arguments.");
        }
    }

    // Utility Methods
    private static void validateArgsLength(String[] args, int expectedLength, String errorMessage) {
        if (args.length < expectedLength) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private static int parseInt(String value, String errorMessage) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private static Task findTaskById(int id) {
        return readTasks().stream().filter(task -> task.getId() == id).findFirst().orElse(null);
    }

    /**
     * Creates a new task with the specified description and writes it to the storage.
     *
     * @param description The description of the task to be created.
     */
    public static void createTask(String description) {
        Task task = new Task(description);
        writeTask(task);
    }

    /**
     * Updates the description of a task identified by its unique ID.
     * If the specified task is found, its description is updated, and the task list is persisted back to storage.
     * If the task is not found, an appropriate message is displayed.
     *
     * @param description The new description to set for the task.
     * @param id The unique identifier of the task to update.
     */
    public static void updateTaskDescription(String description, int id) {
        // Read all tasks
        List<Task> tasks = readTasks();
        boolean taskFound = false;

        // Iterate through tasks
        for (Task task : tasks) {
            if (task.getId() == id) {
                task.updateDescription(description);
                taskFound = true;
            }
        }

        // If task was found, overwrite the updated list of tasks
        if (taskFound) {
            for (Task task : tasks) {
                deleteTask(task.getId());
                writeTask(task);
            }
        } else {
            System.out.println("Task not found: " + id);
        }
    }

    /**
     * Updates the status of a task identified by its ID. If the task is found,
     * it updates its status and last updated timestamp based on the provided status code.
     * Updated tasks are then persisted back to storage.
     *
     * @param id     The unique identifier of the task to update.
     * @param status The new status of the task, represented as an integer:
     *               1 for "todo", 2 for "in-progress", and 3 for "done".
     */
    public static void updateTask(int id, int status) {
        // Read all tasks
        List<Task> tasks = readTasks();
        boolean taskFound = false;

        // Iterate through tasks
        for (Task task : tasks) {
            if (task.getId() == id) {
                taskFound = true; // Mark that we found the task
                Date now = new Date();
                task.update(now.toString());
                // Update task status based on input
                if (status == 1) {
                    task.updateStatus("todo");
                } else if (status == 2) {
                    task.updateStatus("in-progress");
                } else if (status == 3) {
                    task.updateStatus("done");
                }

                break; // Task updated, stop searching
            }
        }

        // If task was found, overwrite the updated list of tasks
        if (taskFound) {
            for (Task task : tasks) {
                deleteTask(task.getId());
                writeTask(task);
            }
        } else {
            System.out.println("Task not found: " + id);
        }
    }

    /**
     * Writes a new task to the storage by reading the existing tasks, adding the new task,
     * and serializing the updated task list back to the storage file.
     *
     * @param task The new task object to be added to the storage.
     */
    public static void writeTask(Task task) {
        List<Task> tasks = readTasks(); // Fetch existing tasks
        tasks.add(task); // Add the new task

        try (Writer writer = Files.newBufferedWriter(Paths.get(FILE_NAME))) {
            gson.toJson(tasks, writer); // Serialize and write tasks list to file
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    /**
     * Deletes a task identified by its unique ID. If the task is found and successfully deleted, the updated
     * task list is written back to the storage file. If the task is not found, a message is displayed.
     *
     * @param id The unique identifier of the task to be deleted.
     */
    public static void deleteTask(int id) {
        // read the list of people from the JSON file
        List<Task> tasks = readTasks();

        // remove the person with the given name
        boolean removed = tasks.removeIf(p -> p.getId() == id);

        if (removed) {
            System.out.println("Task deleted: " + id);

            // write the updated list back to json file
            try (Writer writer = Files.newBufferedWriter(Paths.get(FILE_NAME))) {
                gson.toJson(tasks, writer);
            } catch (IOException e) {
                System.out.println("Error writing to file: " + e.getMessage());
            }
        } else {
            System.out.println("Task not found: " + id);
        }
    }

    /**
     * Reads tasks from the storage file. If the file does not exist, it is created with an empty list of tasks.
     * If the file is empty or contains invalid data, an empty list is returned.
     * Errors during file operations or JSON deserialization are caught, and an empty list is returned in such cases.
     *
     * @return A list of Task objects read from the storage file. If no tasks exist or an error occurs, an empty list is returned.
     */
    public static List<Task> readTasks() {
        try {
            File file = new File(FILE_NAME);

            final Path path = Paths.get(FILE_NAME);
            if (!file.exists()) {
                if (file.createNewFile()) { // Create new file if not existing
                    try (Writer writer = Files.newBufferedWriter(path)) {
                        gson.toJson(new ArrayList<>(), writer);
                    }
                }
                return new ArrayList<>();
            }

            try (Reader reader = Files.newBufferedReader(path)) {
                Task[] tasks = gson.fromJson(reader, Task[].class);
                if (tasks == null) return new ArrayList<>(); // Handle null case
                return new ArrayList<>(List.of(tasks));
            }
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
            return new ArrayList<>();
        } catch (Exception e) {
            System.out.println("JSON deserialization issue: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Displays all tasks by reading them from the storage file and printing their details to the standard output.
     * Each task is printed with its ID, description, and status in uppercase. If no tasks are found, an appropriate message is displayed.
     *
     * The method retrieves the list of tasks by invoking the `readTasks()` method. It iterates through the list of tasks,
     * extracting and displaying the properties of each task.
     *
     * Key functionality:
     * - Reads the existing tasks from storage.
     * - Prints each task's ID, description, and status.
     * - Prints a message indicating no tasks are present if the task list is empty.
     */
    public static void displayAllTasks() {
        List<Task> tasks = readTasks();
        System.out.println("Tasks:\n");
        for (Task task : tasks) {
            System.out.println("ID:" + task.getId() + " " + task.getDescription() + " " + task.getStatus().toUpperCase() + "\n");
        }

        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
        }
    }

    /**
     * Displays tasks with the specified status by reading them from the storage file and printing their details.
     * Only tasks whose status matches the provided status are displayed. Each matching task is printed
     * with its ID, description, and status in uppercase. If no tasks are found with the specified status,
     * no output related to tasks will be displayed.
     *
     * @param status The status of the tasks to filter and display (e.g., "todo", "in-progress", "done").
     */
    public static void displayTasksStatus(String status) {
        List<Task> tasks = readTasks();
        System.out.println("Tasks:\n");
        for (Task task : tasks) {
            if (task.getStatus().equals(status)) {
                System.out.println("ID:" + task.getId() + " " + task.getDescription() + " " + task.getStatus().toUpperCase() + "\n");
            }
        }
    }

    /**
     * Displays a help message outlining the available commands and their usage in the Task CLI application.
     * The help message contains details about commands such as adding, displaying, updating, and deleting tasks,
     * along with their respective syntax and descriptions.
     *
     * Key details:
     * - Lists all recognized commands.
     * - Provides a brief description of each command's functionality.
     * - Outlines the correct syntax for using each command.
     *
     * Commands included in the help message:
     * - add: Creates a new task with a given description.
     * - display: Shows all tasks or filters them based on status.
     * - delete: Removes a task using its ID.
     * - update: Modifies the status of a specified task by ID.
     * - status: Displays the current status of a task.
     * - created: Shows the date a task was created.
     * - updated: Displays the last modified date of a task.
     * - help: Outputs this help message.
     * - quit: Exits the CLI application.
     */
    private static void help() {
        System.out.println("\nAvailable commands:".toUpperCase());
        System.out.println();
        System.out.println("add <description>\nCreates a new task with the given description\n");
        System.out.println("display\nDisplays all tasks\n");
        System.out.println("display <status>\nDisplays the task with the given status\n");
        System.out.println("delete <id>\nDeletes the task with the given ID\n");
        System.out.println("update <id> <status>\nUpdates the status of the task with the given ID\n");
        System.out.println("status <id>\nDisplays the status of the task with the given ID\n");
        System.out.println("created <id>\nDisplays the date the task was created on.\n");
        System.out.println("updated <id>\nDisplay's the date the task was last updated.\n");
        System.out.println("help\nDisplays this help message\n");
        System.out.println("quit\nQuits the application\n");
    }

}