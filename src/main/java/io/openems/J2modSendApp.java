package io.openems;

import java.net.Inet4Address;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;

public class J2modSendApp {

//	private final static String PORT_NAME = "/dev/ttyUSB0";
//	private final static int BAUDRATE = 19200;
//	private final static int DATABITS = 8;
//	private final static int STOPBITS = SerialPort.ONE_STOP_BIT;
//	private final static int PARITY = SerialPort.NO_PARITY;
	private final static int ADDRESS = 1;
	private final static int UNIT_ID = 1;
	private final static StringBuilder hex = new StringBuilder();

	private static TCPMasterConnection tcpConnection;

	public static void main(String[] args) throws Exception {
		System.out.println("Started Modbus Master");

		String path = "C:\\Users\\hueseyin.sahutoglu\\Desktop\\ReceivedTofile-COM17-2021_7_8_16-09-52.DAT";
		byte[] allBytes = Files.readAllBytes(Paths.get(path));

		ArrayList<ModbusRequest> arrlist = new ArrayList<ModbusRequest>(5);
		try {
			ModbusTransaction transaction = getTcpTransaction();
//			WriteMultipleRegistersRequest request = new WriteMultipleRegistersRequest(ADDRESS,
//					new Register[] { new SimpleRegister(1) });
//			FC40WriteRequest fc40Request = new FC40WriteRequest();
//			fc40Request.setRegisters(new Register[] { new SimpleRegister(0x04), new SimpleRegister(0x00),
//					new SimpleRegister(0x00), new SimpleRegister(0xA8), new SimpleRegister(0x28) });
//			fc40Request.setReference(ADDRESS);
//			arrlist.add(fc40Request);

			FC41WriteRequest fc41Request = new FC41WriteRequest();
			fc41Request.setReference(ADDRESS);
			
			FC42WriteRequest fc42Request = new FC42WriteRequest();
			fc42Request.setReference(ADDRESS);
//			fc41Request.setRegisters(
//					new Register[] { new SimpleRegister(0x00), new SimpleRegister(0x10), new SimpleRegister(0x50) });
			byte[] bytess;
			Register[] reg = new Register[20];
//			for (byte bytes : allBytes) {
			for (int i = 0; i < 20; i++) {
				String register = hex.append(String.format("%02X", allBytes[i])).toString();
//				bytess = register.getBytes(StandardCharsets.US_ASCII);
				reg[i] = new SimpleRegister(allBytes[i]);
				hex.setLength(0);
			}
			
			fc42Request.setRegisters(reg);
//			for (Register arr : reg) {
//				System.out.println(arr);
//			}
			arrlist.add(fc42Request);

			for (ModbusRequest req : arrlist) {
				req.setUnitID(UNIT_ID);
				req.setDataLength(req.getDataLength());
				transaction.setRequest(req);
				transaction.execute();
				ModbusResponse response = transaction.getResponse();
				if (!(response instanceof FC40WriteResponse) && !(response instanceof FC41WriteResponse)
						&& !(response instanceof FC42WriteResponse) && !(response instanceof FC43WriteResponse)
						&& !(response instanceof FC41WriteResponse)) {
					throw new Exception("Unexpected Modbus response. Expected [WriteMultipleRegistersResponse], got ["
							+ response.getClass().getSimpleName() + "]");
				}
			}
//			FC40ReadRequest readRequest = new FC40ReadRequest(1, 5);
//			readRequest.setUnitID(UNIT_ID);
//			transaction.setRequest(readRequest);
//			transaction.execute();
//			ModbusResponse response = transaction.getResponse();
		} finally {
			if (tcpConnection != null) {
				tcpConnection.close();
			}
			System.out.println("Finished Modbus Master");
		}
	}

	private static ModbusTransaction getTcpTransaction() throws Exception {
		tcpConnection = new TCPMasterConnection(Inet4Address.getLocalHost());
		tcpConnection.setPort(502);
		tcpConnection.connect();
		return new ModbusTCPTransaction(tcpConnection);
	}

	public static byte[] take(byte[] allBytes, int n) {
		if (allBytes.length == 0)
			return allBytes;
		if (n > allBytes.length)
			return allBytes;

		byte[] arr2 = new byte[n];
		for (int i = 0; i < n; i++) {
			arr2[i] = allBytes[i];
		}
		return arr2;
	}

//	private static ModbusTransaction getSerialTransaction() {
//		SerialParameters params = new SerialParameters();
//		params.setPortName(PORT_NAME);
//		params.setBaudRate(BAUDRATE);
//		params.setDatabits(DATABITS);
//		params.setStopbits(STOPBITS);
//		params.setParity(PARITY);
//		params.setEncoding(Modbus.SERIAL_ENCODING_RTU);
//		params.setEcho(false);
//		SerialConnection connection = new SerialConnection(params);
//		return new ModbusSerialTransaction(connection);
//	}

}
