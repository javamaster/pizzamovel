/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bluecommunicator;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mauricio
 */
public class SerialReceiver implements SerialPortEventListener{

    SerialPort serialPort;
    /** The port we're normally going to use. */
    private static final String PORT_NAMES[] = { 
            "/dev/tty.usbserial-A9007UX1", // Mac OS X
            "/dev/ttyUSB0", // Linux
            "COM4", "COM7",// Windows   
            "/dev/rfcomm0" // Ubuntu Bluetooth
    };
    /**
    * A BufferedReader which will be fed by a InputStreamReader 
    * converting the bytes into characters 
    * making the displayed results codepage independent
    */
    private BufferedReader input;
    /** The output stream to the port */
    private OutputStream output;
    /** Milliseconds to block while waiting for port open */
    private static final int TIME_OUT = 2000;
    /** Default bits per second for COM port. */
    private static final int DATA_RATE = 9600;

    public void initialize() {
        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        String nome = "";
        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    nome = portName;
                    break;
                }
            }
        }
        if (portId == null) {
            System.out.println("Could not find COM port.");
            return;
        }

        try {
            
            //print port
            System.out.println("porta selecionada: "+nome);
            
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(),
                    TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // open the streams
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();

            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    public void write(int value){
        if(output != null){
        try {
            output.write(value);
            
        } catch (IOException ex) {
            Logger.getLogger(SerialReceiver.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        }else{
            System.out.println("Error: Command not send!!");
        }
    }
    /**
     * This should be called when you stop using the port.
     * This will prevent port locking on platforms like Linux.
     */
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    /**
     * Handle an event on the serial port. Read the data and print it.
     */
    @Override
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE ) {
            try {
    // The following line reads the same value every time the event is fired...
                String inputLine=input.readLine();               
//                String[] arrayValues=inputLine.split(",");
//                System.out.print(arrayValues[0]);
//                System.out.print(",");
//                System.out.println(arrayValues[1]);
                System.out.println(inputLine);
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
        // Ignore all the other eventTypes, but you should consider the other ones.
    }

    public static void main(String[] args) throws Exception {
        SerialReceiver main = new SerialReceiver();
        main.initialize();
        main.write(0);
        Thread t = new Thread() {
            @Override
            public void run() {
                //the following line will keep this app alive for 60 seconds,
                //waiting for events to occur and responding to them (printing incoming messages to console).
                try {Thread.sleep(10000);} catch (InterruptedException ie) {}
            }
        };
        System.out.println("Started");
        //t.start();
        //t.join();
//        main.close();
        System.out.println("Stopped");
    }
}
