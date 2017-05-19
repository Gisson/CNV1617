import BIT.highBIT.*;
import java.io.*;
import java.util.*;
import pt.tecnico.cnv.server.WebServer;

import java.util.Map;
import java.util.HashMap;

public class Instrument {
    private static PrintStream out = null;
	private static String logFile="server.log";

    static void instrument_class(String input_class, String output_class) {
        System.out.println("Instrument class: " + input_class + " to "  + output_class);
        // create class info object
        ClassInfo ci = new ClassInfo(input_class);
        int count = ci.getRoutineCount();

        for (Enumeration e = ci.getRoutines().elements(); e.hasMoreElements(); ) {
            Routine routine = (Routine) e.nextElement();
            routine.addBefore("Instrument", "dynMethodCount", new Integer(1));
            if(routine.getMethodName().equals("draw") && routine.getClassName().equals("raytracer/RayTracer")) {
                System.out.println("Found draw method in " + input_class);
                routine.addAfter("Instrument", "printStats", "null");
            }

            // iterate instructions
            InstructionArray instructions = routine.getInstructionArray();
            for (Enumeration instrs = instructions.elements(); instrs.hasMoreElements(); ) {
                Instruction instr = (Instruction) instrs.nextElement();
                int opcode=instr.getOpcode();
                if ((opcode==InstructionTable.NEW) ||
                        (opcode==InstructionTable.newarray) ||
                        (opcode==InstructionTable.anewarray) ||
                        (opcode==InstructionTable.multianewarray)) {
                        instr.addBefore("Instrument", "allocCount", new Integer(opcode));
                }
            }
        }

        // unmodified class, for now
        ci.write(output_class);
        System.out.println(input_class + " has " + count + " routines.");
    }

    
    public static synchronized void printHello(String foo) {
        System.out.println("hello: " + foo);
    }
    
    private static int dyn_method_count = 0;
    public static synchronized void dynMethodCount(int incr) {
        dyn_method_count++;
    }

    private static int alloc_count = 0;
    public static synchronized void allocCount(int type) {
        alloc_count++;
    }

	public static synchronized void printStats(String s) {
		Map<String, String> request = WebServer.getRequest(Thread.currentThread().getId());

		Map<String, Integer> parameters = new HashMap<String, Integer>();
		String filename = request.get("f");
		int scols = Integer.parseInt(request.get("sc"));
		parameters.put("sc", scols);
		int srows = Integer.parseInt(request.get("sr"));
		parameters.put("sr", srows);
		int wcols = Integer.parseInt(request.get("wc"));
		parameters.put("wc", wcols);
		int wrows = Integer.parseInt(request.get("wr"));
		parameters.put("wr", wrows);
		int coff = Integer.parseInt(request.get("coff"));
		parameters.put("coff", coff);
		int roff = -Integer.parseInt(request.get("roff"));
		parameters.put("roff", roff);

        try {
		    MSS mss = MSS.getInstance();
            mss.sendToMSS(filename, parameters, dyn_method_count, alloc_count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        

		Instrument.logToFile("thread_id = " + Thread.currentThread().getId());
		Instrument.logToFile("dyn_method_count = " + dyn_method_count);
		Instrument.logToFile("alloc_count = " + alloc_count);
	}

	public static synchronized void logToFile(String logString) {
		try{
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(Instrument.logFile,true)));
			writer.println(logString);
			writer.close();
		} catch( IOException e){
			e.printStackTrace();
		}
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
