package com.github.pr0lebenimholz.discordbot;

import com.github.fivekwbassmachine.jutils.CommandLineArguments;
import com.github.pr0lebenimholz.discordbot.exceptions.TooMuchIterationException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class DiscordBot {

    private static String name;
    private static int logLevel;
    private static int logLevelFile;
    private static String configFilePath;
    private static String logDirPath;
    private static int maxRestarts = Constants.VAL_MAX_RESTARTS;

    public static void main(String[] unparsedArgs) throws TooMuchIterationException {
        // Get program name
        name = DiscordBot.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        name = name.substring(name.lastIndexOf('/') + 1);
        // Patch: use class name when the program is executed by IntelliJ
        if (name.isEmpty()) name = DiscordBot.class.getSimpleName();

        // Parse arguments
        CommandLineArguments args = CommandLineArguments.from(unparsedArgs);

        // Show help and ignore each other parameter when help argument is passed
        if (args.getType("h").isKey() || args.getType("help").isKey()) {
            printUsage();
            System.exit(0);
        }

        // Process arguments
        logLevel = getArgument(args, "loglevel", Constants.VAL_DEFAULT_LOGLEVEL);
        logLevelFile = getArgument(args, "loglevel-file", Constants.VAL_DEFAULT_LOGLEVEL_FILE);
        configFilePath = args.getOrDefault("config", Constants.PATH_CONFIG_FILE);
        // Assign null when logLevel is -1 (=> file-logging disabled)
        logDirPath = (logLevelFile == -1 ? null : args.getOrDefault("logpath", Constants.PATH_LOG_DIR));

        // Inform about program start
        if (logLevel <= 1) {
            System.out.println("Starting " + name + " with parameters:");
            System.out.println("  logLevel (console): " + logLevel);
            System.out.println("           (file):    " + logLevelFile);
            System.out.println("  logfile:            " + (logDirPath == null ? "[file-logging disabled]" : logDirPath));
            System.out.println("  config:             " + configFilePath);
        }

        // Initialize DiscordBot (and keep restarting when it crashes)
        for (int i = 0; i < maxRestarts; i++) {
            try {
                run();
                // Prevent endless loop after program execution
                System.exit(0);
            } catch (RuntimeException e) {
                // Continue restarting the bot
                System.err.println("Restarting " + name + " the " + i + ". time because a RuntimeException occurred:");
                e.printStackTrace();
            } catch (Exception e) {
                // Don't restart the bot
                System.err.println("Can't restart " + name + " for the " + i + ". time because an  Exception occurred:");
                e.printStackTrace();
                System.exit(1);
            }
        }

        // The program can only get here when a RuntimeException occurs too often i.e. the loop iterates too much.
        throw new TooMuchIterationException(name + "has been restarted " + maxRestarts + " times. Limit Reached.");
    }

    private static void printUsage() {
        System.out.println("Usage");
        System.out.println("  Arguments:");
        System.out.println("        --config         Relative (to jar) or absolute path to config file");
        System.out.println("                           (Default: '" + Constants.PATH_CONFIG_FILE + "')");
        System.out.println("    -h  --help           Shows this text");
        System.out.println("        --loglevel       Specify minimum log level (Default: " + Constants.VAL_DEFAULT_LOGLEVEL + ")");
        System.out.println("                           -1: No logging");
        System.out.println("                           0: >= debug");
        System.out.println("                           1: >= info");
        System.out.println("                           2: >= log");
        System.out.println("                           3: >= warning");
        System.out.println("                           4: >= error");
        System.out.println("        --loglevel-file  Specify minimum log level for logfile (Default: " + Constants.VAL_DEFAULT_LOGLEVEL_FILE + ")");
        System.out.println("                           For values see --loglevel.");
        System.out.println("                           -1: Makes --logpath redundant");
        System.out.println("        --logpath        Relative (to jar) or absolute path to logging directory");
        System.out.println("                           (Default: '" + Constants.PATH_LOG_DIR + "')");
    }

    private static int getArgument(CommandLineArguments args, String arg, int defaultValue) {
        String unparsedArg = args.get(arg);
        int parsedArg = defaultValue;
        if (unparsedArg != null) {
            try {
                parsedArg = Integer.parseInt(unparsedArg);
            } catch (NumberFormatException e) {
                System.out.println("Illegal Argument value: '" + arg + "': '" + unparsedArg + "'");
                printUsage();
                System.exit(1);
            }
        }
        return parsedArg;
    }

    private static void run() throws Exception {
        /* TODO: 26.10.20
         * - config handling
         *   - does config file exist? => create from default template if not
         * - connection checks
         *   - connected to internet
         *   - required servers reachable
         * - start discord bot
         */
        throw new NotImplementedException();
    }

    // Don't provide a public constructor
    private DiscordBot() {}
}
