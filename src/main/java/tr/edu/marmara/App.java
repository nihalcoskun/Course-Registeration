package tr.edu.marmara;

import java.io.FileReader;
import java.io.FileWriter;

public class App {
    private ActionReporter ar;

    public App(ActionReporter ar) {
        this.ar = ar;
    }

    public void simulate(String[] args) {
        try {
            if (args.length < 2) {
                throw new Exception("The file parameter is mandatory");
            }
            CourseRegistrationSystem crs = new CourseRegistrationSystem();

            crs.run(new FileReader(args[1]));
            crs.write(new FileWriter(args[1]));
            ar.print("The end of simulation.");
        } catch (Exception e) {
            ar.err(e);
        }
    }

    public void generate(String[] args) {
        try {
            if (args.length < 3) {
                throw new Exception("The number and the file parameters are mandatory");
            }
            CourseRegistrationSystem crs = new CourseRegistrationSystem();
            crs.gather(new FileReader(args[2]));
            crs.generate(Integer.parseInt(args[1]));
            crs.write(new FileWriter(args[2]));
            ar.print(args[1] + " students created for each semester.");
        } catch (Exception e) {
            ar.err(e);
        }
    }

    public void gpa(String[] args) {
        try {
            if (args.length < 2) {
                throw new Exception("The file parameter is mandatory");
            }
            CourseRegistrationSystem crs = new CourseRegistrationSystem();
            crs.gather(new FileReader(args[1]));
            crs.printGPAs();
        } catch (Exception e) {
            ar.err(e);
        }
    }
}
