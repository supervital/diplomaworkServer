package com.serverside.smartpic.main;

import java.util.Map;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import com.serverside.smartpic.server.NanoServer;
import com.serverside.smartpic.server.NanoServer.Response.Status;
import com.serverside.smartpic.server.ServerRunner;

public class PICResponse extends NanoServer {

	/* Server settings */
	public static final String HOST_NAME = "127.0.0.1";
	public static final int PORT = 8080;

	/* Command for COM port */
	public static final int WRITE = 241;
	public static final int READ = 242;
	public static final int RESET = 240;

	/* Secret headers */
	public static final String CLIENT_ID = "client_id"; // Key
	public static final String CLIENT_SECRET = "android_app"; // Value

	/* Command headers */
	public static final String DEVICE_COMMAND = "device_command"; // Key
	public static final String READ_FORM_COM_PORT = "read_from"; // Value
	public static final String WRITE_TO_COM_PORT = "write_to"; // Value

	/* State headers */
	public static final String DEVICE_STATE = "device_state"; // Key

	public PICResponse() {
		super(PORT);
	}

	public static void main(String[] args) {
		ServerRunner.run(PICResponse.class);

	}

	@Override
	public Response serve(String uri, Method method,
			Map<String, String> header, Map<String, String> parms,
			Map<String, String> files) {

		if (header.get(CLIENT_ID) == null
				|| !header.get(CLIENT_ID).equals(CLIENT_SECRET)) {
			// Redirect on 403 page
			return new Response(Status.FORBIDDEN, NanoServer.MIME_HTML, "");
		}
		if (parms.get(DEVICE_COMMAND) != null
				&& parms.get(DEVICE_STATE) != null) {
			// TODO: send WRITE/READ signals to COM - Port
			if (parms.get(DEVICE_COMMAND).equals(WRITE_TO_COM_PORT)) {
				// write to COM - Port
				System.out.println(parms.get(DEVICE_STATE));
				synchronized (parms.get(DEVICE_STATE)) {
					int value = Integer.parseInt(parms.get(DEVICE_STATE));
					if (writeToPort(getPortName(), value) == 404)
						return new Response(Status.BAD_REQUEST,
								NanoServer.MIME_HTML, "");
				}
			} else if (parms.get(DEVICE_COMMAND).equals(READ_FORM_COM_PORT)) {
				// read from COM - Port
			}
		} else {
			// Redirect on 404 page
			return new Response(Status.BAD_REQUEST, NanoServer.MIME_HTML, "");
		}

		return new Response("!");
	}

	private String getPortName() {
		String portName = "";
		String[] portNames = SerialPortList.getPortNames();
		for (int i = 0; i < portNames.length; i++) {
			portName = portNames[i];
		}
		return portName;
	}

	private int writeToPort(String portName, int number) {
		SerialPort ssport = new SerialPort(portName);
		try {
			ssport.openPort();
			ssport.setParams(SerialPort.BAUDRATE_19200, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			int[] comand = { WRITE, number, 0 };
			ssport.writeIntArray(comand);
			ssport.closePort();
			return number;
		} catch (SerialPortException e) {
			e.printStackTrace();
			return 404;
		}
	}

	private int readFromPort() {
		SerialPort ssport = new SerialPort("");
		int portState = 0;
		try {
			ssport.openPort();
			ssport.setParams(SerialPort.BAUDRATE_19200, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			int[] intArray = null;
			int[] comand = { READ, 0, 0 };
			ssport.writeIntArray(comand);
			intArray = ssport.readIntArray();
			ssport.closePort();
			return intArray[0];
		} catch (SerialPortException e) {
			e.printStackTrace();
			return 404;
		}

	}

}
