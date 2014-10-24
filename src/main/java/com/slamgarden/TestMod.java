package com.slamgarden;

import org.apache.commons.codec.binary.*;

import org.apache.log4j.Logger;

// import com.slamgarden.HttpLine;

/** Description of TestMod()
 *
 * A TestMod is used to make an entry point into one of the package modules without calling
 *   it with all normal operating parameters or setup
 * @throws           Description of myException
 */

public class TestMod
{

    private static Logger logger = Logger.getLogger(TestMod.class);

    public static void main(String args[]) {
        logger.info( "In method Main()");
        logger.debug("This is debug in TestMod()");

        HttpLine httpline;
  
        if (args.length > 0 ) {
          httpline = new HttpLine(args[0]);
        } else {
          httpline = new HttpLine();
        }
        int i = 0;
        while(httpline.next() ) {
          logger.debug("reading next line number " + i++ + ": " + httpline.getLineAsText());
        }

        System.out.println("End of TestMod");
    }

}
