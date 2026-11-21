public class CommandParser {

    Command parse(String input) {
        Command command = new Command();
        String trimmed = input.trim();

        if (trimmed.isEmpty()) {
            command.type = Command.Type.STEP;
            return command;
        }

        if (trimmed.equals("exit")) {
            command.type = Command.Type.EXIT;
            return command;
        }

        String values;
        if (trimmed.matches("\\[\\s*\\d+\\s*,\\s*\\d+\\s*\\]")) {
            values = trimmed.substring(1, trimmed.length() - 1).trim();
        } else if (trimmed.matches("\\d+\\s*,\\s*\\d+")) {
            values = trimmed;
        } else {
            command.type = Command.Type.INVALID;
            command.error = "Request must be in the form [floor, destination].";
            return command;
        }

        String[] parts = values.split("\\s*,\\s*", -1);
        if (parts.length != 2) {
            command.type = Command.Type.INVALID;
            command.error = "Request must be in the form [floor, destination].";
            return command;
        }

        try {
            command.floor = Integer.parseInt(parts[0].trim());
            command.destination = Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException e) {
            command.type = Command.Type.INVALID;
            command.error = "Floor numbers are too large.";
            return command;
        }

        command.type = Command.Type.REQUEST;
        return command;
    }
}

