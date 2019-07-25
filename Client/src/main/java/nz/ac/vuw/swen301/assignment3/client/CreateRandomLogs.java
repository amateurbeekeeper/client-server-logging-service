package nz.ac.vuw.swen301.assignment3.client;

import org.apache.log4j.Logger;
import java.util.Random;

/**
 * Executable class ​that creates random logs (using multiple appenders, random 
 * message and levels) in an infinite loop at a rate of ca 1 LogEvent per second. 
 * Logging is set up to use the ​Resthome4LogsAppender appender​.​ [3 marks]
 *
 */
public class CreateRandomLogs {
    public static void main(String args[]) throws InterruptedException {
        Logger l = Logger.getLogger("CreateRandomLogs");
        Resthome4LogsAppender a = new Resthome4LogsAppender();
        l.addAppender(a);
        Random rand = new Random();
        
        long c = 0;
        int r = 0;

        while (true) {
            // NUMBER
            r = rand.nextInt(6);
            r += 1;
            c++;

            // MESSAGE
            String m = "random log #"+c;

            // LEVELS
            if (r == 1) l.trace(m);
            else if (r == 2) l.debug(m);
            else if (r == 3) l.info(m);
            else if (r == 4) l.warn(m);
            else if (r == 5) l.error(m);
            else if (r == 6) l.fatal(m);

            if(args.length > 0) break;
            
            Thread.sleep(1000);
        }
    }
}
