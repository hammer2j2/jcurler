package com.slamgarden;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;

import org.apache.log4j.Logger;

import java.net.*;
import java.io.*;

/** Description of TestMod() 
* 
* A TestMod is used to make an entry point into one of the package modules without calling
*   it with all normal operating parameters or setup
* @throws           Description of myException
*/

public class TestMod
{
    public static void main( String[] args )
    {
        System.out.println( "TestMod!" );
    }
}
