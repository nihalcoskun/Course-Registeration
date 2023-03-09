package tr.edu.marmara;

public class Main {
    public static void help() {
        System.out.println("crs:    Course Registration System");
        System.out.println("desc:   CRS is a course registration system. It is capable of creating students,");
        System.out.println("        behaving like students, behaving like advisors, modeling course prerequisite");
        System.out.println("        tree, creating lectures, converting Letter Grades and many countless features");
        System.out.println("        that are required by a course registration system. Moreover, you can manage all");
        System.out.println("        the mentioned rich features by changing the settings properties of the file that");
        System.out.println("        you passed to CRS.");
        System.out.println("usage:  crs <command> [file]\n");
        System.out.println("These are possible CRS commands for various situations:");
        System.out.println("    simulate            Runs the simulation by [file]");
        System.out.println("    generate <number>   Replaces <number> of students for each semester to [file]");
        System.out.println("    gpa                 Prints the GPA of each student, placed in [file]");
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            help();
            return;
        }

        ActionReporter actionReporter = new ActionReporter();

        try {
            App a = new App(actionReporter);

            switch (args[0]) {
                case "simulate":
                    a.simulate(args);
                    break;
                case "generate":
                    a.generate(args);
                    break;
                case "gpa":
                    a.gpa(args);
                    break;
            }
        } catch (Exception e) {
            actionReporter.print(args[0] + " is not a command.");
        }
    }
}