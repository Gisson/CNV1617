import BIT.highBIT.*;
import java.io.*;
import java.util.*;


public class Instrument {
    private static PrintStream out = null;

    static void instrument_class(String input_class, String output_class) {
        System.out.println("Instrument class: " + input_class + " to "  + output_class);
        // create class info object
        ClassInfo ci = new ClassInfo(input_class);
        int count = ci.getRoutineCount();
        ci.addAfter("Instrument", "printHello", ci.getClassName());
        // unmodified class, for now
        ci.write(output_class);
        System.out.println(input_class + " has " + count + " routines.");
    }

    public static synchronized void printHello(String foo) {
        System.out.println("hello: " + foo);
    }

    static void instrument_dir(String path, String dir_name, String output_dir) {
        final String separator = System.getProperty("file.separator");
        System.out.println("Instrument dir: " + path  + separator + dir_name);
        
        File o_dir = new File( output_dir + separator + dir_name);
        o_dir.mkdir();

        File dir = new File(path + separator + dir_name);
        String files[] = dir.list();
        if(files != null) {
            for( int i = 0; i < files.length; i++) {
                String filename = files[i];
                if (filename.endsWith(".class")) {
                    instrument_class(path + separator + dir_name + separator+ filename,
                                     output_dir + separator + dir_name + separator + filename);

                } else if (!filename.endsWith(".java")) {
                    // assume it's a folder
                    instrument_dir(path + separator + dir_name, filename,
                                   output_dir + separator + dir_name);
                }
            }
        } else {
            System.out.println("no files in " + path  + separator + dir_name);
        }
    }
    // instrument all files in argv[0] to argv[1]
    public static void main(String argv[]) {
        System.out.println(" argv[0]: "+argv[0]);
        instrument_dir(argv[0], ".", argv[1]);
    }

}
// vim: expandtab:ts=4:sw=4
