package io.openems;

import java.net.Inet4Address;

import com.fazecast.jSerialComm.SerialPort;
import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.ghgande.j2mod.modbus.util.SerialParameters;

public class J2modSendApp {

	private final static String PORT_NAME = "/dev/ttyUSB0";
	private final static int BAUDRATE = 19200;
	private final static int DATABITS = 8;
	private final static int STOPBITS = SerialPort.ONE_STOP_BIT;
	private final static int PARITY = SerialPort.NO_PARITY;
	private final static int ADDRESS = 1;
	private final static int UNIT_ID = 1;

	private static TCPMasterConnection tcpConnection;

	public static void main(String[] args) throws Exception {
		System.out.println("Started Modbus Master");
		try {
//		ModbusTransaction transaction = getSerialTransaction();
			ModbusTransaction transaction = getTcpTransaction();
//			WriteMultipleRegistersRequest request = new WriteMultipleRegistersRequest(ADDRESS,
//					new Register[] { new SimpleRegister(1) });
			FC40Request request = new FC40Request(ADDRESS, new Register[] { new SimpleRegister(1) });

			request.setUnitID(UNIT_ID);
			transaction.setRequest(request);
			transaction.execute();
			ModbusResponse response = transaction.getResponse();
			if (!(response instanceof FC40Response)) {
				throw new Exception("Unexpected Modbus response. Expected [WriteMultipleRegistersResponse], got ["
						+ response.getClass().getSimpleName() + "]");
			}
//			if (!(response instanceof WriteMultipleRegistersResponse)) {
//				throw new Exception("Unexpected Modbus response. Expected [WriteMultipleRegistersResponse], got ["
//						+ response.getClass().getSimpleName() + "]");
//			}
		} finally {
			if (tcpConnection != null) {
				tcpConnection.close();
			}
			System.out.println("Finished Modbus Master");
		}
	}

	private static ModbusTransaction getSerialTransaction() {
		SerialParameters params = new SerialParameters();
		params.setPortName(PORT_NAME);
		params.setBaudRate(BAUDRATE);
		params.setDatabits(DATABITS);
		params.setStopbits(STOPBITS);
		params.setParity(PARITY);
		params.setEncoding(Modbus.SERIAL_ENCODING_RTU);
		params.setEcho(false);
		SerialConnection connection = new SerialConnection(params);
		return new ModbusSerialTransaction(connection);
	}

	private static ModbusTransaction getTcpTransaction() throws Exception {
		tcpConnection = new TCPMasterConnection(Inet4Address.getLocalHost());
		tcpConnection.setPort(502);
		tcpConnection.connect();
		return new ModbusTCPTransaction(tcpConnection);
	}

}
