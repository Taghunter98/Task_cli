# Task CLI Application

A simple command-line interface (CLI) application for managing tasks. This application allows you to create, update, delete, and display tasks that are stored in a JSON file. The task manager also provides functionality to filter tasks by status and view details such as creation or update timestamps.

## Features
- Add new tasks to your task list.
- Delete tasks by their unique identifier.
- Update task descriptions or statuses.
- Display all tasks or filter by status.
- View task details (creation date, update date, status).
- JSON-based task storage for persistence.

## Commands
The application recognizes the following commands:

| Command                   | Arguments                | Description                                   |
|---------------------------|--------------------------|-----------------------------------------------|
| **add** `<description>`   | One argument             | Creates a new task with the given description. |
| **display**               | None or `<status>`       | Displays all tasks or filters by status.       |
| **delete** `<id>`         | One argument (task ID)   | Deletes the task specified by its ID.          |
| **update** `<id>` `<status>` | Two arguments (task ID, status) | Updates the status of the given task.     |
| **update_name** `<id>` `<description>` | Two arguments (task ID, new description) | Updates the task's description. |
| **status** `<id>`         | One argument (task ID)   | Displays the status of the task with the given ID. |
| **created** `<id>`        | One argument (task ID)   | Shows the creation date of the task.           |
| **updated** `<id>`        | One argument (task ID)   | Shows the last updated date of the task.       |
| **help**                  | None                    | Displays all available commands and their usage. |
| **quit**                  | None                    | Exits the application.                         |

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven (optional, if you wish to build the project)

### Installation
1. Clone this repository:
   ```bash
   git clone https://github.com/Taghunter98/Task-cli.git
   cd task-cli
   ```
2. Compile the application:
   ```bash
   javac -cp gson-2.10.jar Task_cli.java Task.java
   ```
3. Run the application:
   ```bash
   java -cp .:gson-2.10.jar Task_cli
   ```

### Example Usage
1. Add a task:
   ```bash
   java -cp .:gson-2.10.jar Task_cli add "Buy groceries"
   ```
   Output:
   ```
   Task created: Buy groceries, 1
   ```

2. Display all tasks:
   ```bash
   java -cp .:gson-2.10.jar Task_cli display
   ```

3. Update a task's status:
   ```bash
   java -cp .:gson-2.10.jar Task_cli update 1 2
   ```
   Status levels:
   - 1: `todo`
   - 2: `in-progress`
   - 3: `done`

4. View task creation date:
   ```bash
   java -cp .:gson-2.10.jar Task_cli created 1
   ```

5. Get help:
   ```bash
   java -cp .:gson-2.10.jar Task_cli help
   ```

### Data Storage
Tasks are stored in a JSON file named `Tasks.json`. Each task includes the following fields:
- `id`: Unique identifier for the task.
- `description`: Task description.
- `status`: Current status (`todo`, `in-progress`, `done`).
- `createdAt`: Date the task was created.
- `updatedAt`: Date the task was last updated (if applicable).

A separate file, `id.txt`, is used to generate unique task IDs.

## How It Works
1. **Task Creation**:
   Tasks are written to the `Tasks.json` file with a unique ID and a default status of `todo`.

2. **Task Management**:
   - You can perform CRUD operations on tasks using predefined commands.
   - Tasks are filtered, updated, and displayed by reading/writing to `Tasks.json`.

3. **Task Identification**:
   Each task is assigned a unique ID, which is maintained in the `id.txt` file to ensure no duplicate IDs are generated.

## Contributing
Contributions, issues, and feature requests are welcome! Feel free to fork the repository and create a pull request.

## Roadmap
- [ ] Add unit tests to ensure application robustness.
- [ ] Integrate better error handling for invalid input or missing files.
- [ ] Extend task filtering (e.g., sort by creation date or status).
- [ ] Refactor to support multiple data storage formats (e.g., SQLite).

## License
This project is licensed under the MIT License. See the `LICENSE` file for details.