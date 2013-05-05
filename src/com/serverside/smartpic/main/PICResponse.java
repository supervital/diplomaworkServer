package com.serverside.smartpic.main;

import java.util.Map;

import jssc.SerialPortList;

import com.serverside.smartpic.server.NanoServer;
import com.serverside.smartpic.server.ServerRunner;
import com.serverside.smartpic.server.NanoServer.Response.Status;

public class PICResponse extends NanoServer {

	public static final String HOST_NAME = "127.0.0.1";
	public static final String DEVICE_ID = "device_id";
	public static final String DEVICE_STATE = "device_state";
	public static final String CLIENT_ID = "client_id";
	public static final String CLIENT_SECRET = "android_app";
	public static final int PORT = 8080;

	public PICResponse() {
		super(HOST_NAME, PORT);
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
			return new Response(Status.FORBIDDEN, NanoServer.MIME_HTML, "");
		}
		System.out.println(header.get(CLIENT_ID));
		if (parms.get(DEVICE_ID) != null && parms.get(DEVICE_STATE) != null) {
			System.out.println(parms.get(DEVICE_ID));
			System.out.println(parms.get(DEVICE_STATE));
		} else {
			return new Response(Status.BAD_REQUEST, NanoServer.MIME_HTML, "");
		}

		return new Response("!");
	}

	private void getPortsNames() {
		String[] portNames = SerialPortList.getPortNames();
		for (int i = 0; i < portNames.length; i++) {
			System.out.println(portNames[i]);
		}
	}

}
