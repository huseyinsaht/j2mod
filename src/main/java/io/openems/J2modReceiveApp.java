package io.openems;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.procimg.DigitalIn;
import com.ghgande.j2mod.modbus.procimg.DigitalOut;
import com.ghgande.j2mod.modbus.procimg.FIFO;
import com.ghgande.j2mod.modbus.procimg.File;
import com.ghgande.j2mod.modbus.procimg.IllegalAddressException;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.slave.ModbusSlave;
import com.ghgande.j2mod.modbus.slave.ModbusSlaveFactory;

public class J2modReceiveApp {

	private final static int UNIT_ID = 1;

	public static void main(String[] args) throws InterruptedException, ModbusException {
		ModbusSlave slave = null;
		try {
			slave = ModbusSlaveFactory.createTCPSlave(502, 5);
			slave.addProcessImage(UNIT_ID, new MyProcessImage());
			slave.open();
			
			System.out.println("Started Modbus/TCP Slave");
			Thread.sleep(Long.MAX_VALUE);
		} finally {
			slave.close();
		}

	}

	private static class MyRegister implements Register {

		private int value;

		public MyRegister(int value) {
			this.value = value;
		}

		public int getValue() {
			System.out.println("getValue");
			return this.value;
		}

		public int toUnsignedShort() {
			System.out.println("toUnsignedShort");
			return this.value;
		}

		public short toShort() {
			System.out.println("toShort");
			return (short) this.value;
		}

		public byte[] toBytes() {
			System.out.println("toBytes");
			return new byte[2];
		}

		public void setValue(int v) {
			System.out.println("setValue int " + v);
			this.value = v;
		}

		public void setValue(short s) {
			System.out.println("setValue short " + s);
			this.value = s;
		}

		public void setValue(byte[] bytes) {
			System.out.println("setValue bytes " + bytes);
			this.value = 0;
		}

	}

	private static class MyProcessImage implements ProcessImage {

		public DigitalOut[] getDigitalOutRange(int offset, int count) throws IllegalAddressException {
			System.out.println("getDigitalOutRange");
			return null;
		}

		public DigitalOut getDigitalOut(int ref) throws IllegalAddressException {
			System.out.println("getDigitalOut");
			return null;
		}

		public int getDigitalOutCount() {
			System.out.println("getDigitalOutCount");
			return 0;
		}

		public DigitalIn[] getDigitalInRange(int offset, int count) throws IllegalAddressException {
			System.out.println("getDigitalInRange");
			return null;
		}

		public DigitalIn getDigitalIn(int ref) throws IllegalAddressException {
			System.out.println("getDigitalIn");
			return null;
		}

		public int getDigitalInCount() {
			System.out.println("getDigitalInCount");
			return 0;
		}

		public InputRegister[] getInputRegisterRange(int offset, int count) throws IllegalAddressException {
			System.out.println("getInputRegisterRange");
			return null;
		}

		public InputRegister getInputRegister(int ref) throws IllegalAddressException {
			System.out.println("getInputRegister");
			return null;
		}

		public int getInputRegisterCount() {
			System.out.println("getInputRegisterCount");
			return 0;
		}

		public Register[] getRegisterRange(int offset, int count) throws IllegalAddressException {
			Register[] result = new Register[count];
			for (int i = 0; i < count; i++) {
				result[i] = new MyRegister(0);
			}
			return result;
		}

		public Register getRegister(int ref) throws IllegalAddressException {
			System.out.println("getRegister");
			return null;
		}

		public int getRegisterCount() {
			System.out.println("getRegisterCount");
			return 0;
		}

		public File getFile(int ref) throws IllegalAddressException {
			System.out.println("ref");
			return null;
		}

		public File getFileByNumber(int ref) throws IllegalAddressException {
			System.out.println("getFileByNumber");
			return null;
		}

		public int getFileCount() {
			System.out.println("getFileCount");
			return 0;
		}

		public FIFO getFIFO(int ref) throws IllegalAddressException {
			System.out.println("getFIFO");
			return null;
		}

		public FIFO getFIFOByAddress(int ref) throws IllegalAddressException {
			System.out.println("getFIFOByAddress");
			return null;
		}

		public int getFIFOCount() {
			System.out.println("getFIFOCount");
			return 0;
		}

	}

}
