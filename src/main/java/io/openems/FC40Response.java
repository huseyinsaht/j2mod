package io.openems;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.ghgande.j2mod.modbus.msg.ModbusResponse;

/**
 * Class implementing FC 40
 */
public class FC40Response extends ModbusResponse {
	// instance attributes
	private int wordCount;
	private int reference;

	/**
	 * Constructs a new <tt>WriteMultipleRegistersResponse</tt> instance.
	 */
	public FC40Response() {
		super();

		setFunctionCode(FC40Request.FUNCTION_CODE);
		setDataLength(4);
	}

	/**
	 * Constructs a new <tt>WriteMultipleRegistersResponse</tt> instance.
	 *
	 * @param reference the offset to start writing from.
	 * @param wordCount the number of words (registers) to be written.
	 */
	public FC40Response(int reference, int wordCount) {
		super();

		setFunctionCode(FC40Request.FUNCTION_CODE);
		setDataLength(4);

		this.reference = reference;
		this.wordCount = wordCount;
	}

	/**
	 * Returns the reference of the register to start writing to with this
	 * <tt>WriteMultipleRegistersResponse</tt>.
	 * <p>
	 *
	 * @return the reference of the register to start writing to as <tt>int</tt> .
	 */
	public int getReference() {
		return reference;
	}

	/**
	 * Sets the reference of the register to start writing to with this
	 * <tt>WriteMultipleRegistersResponse</tt>.
	 * <p>
	 *
	 * @param ref the reference of the register to start writing to as <tt>int</tt>.
	 */
	public void setReference(int ref) {
		reference = ref;
	}

	/**
	 * Returns the number of bytes that have been written.
	 *
	 * @return the number of bytes that have been written as <tt>int</tt>.
	 */
	public int getByteCount() {
		return wordCount * 2;
	}

	/**
	 * Returns the number of words that have been written. The returned value should
	 * be half of the byte count of the response.
	 * <p>
	 *
	 * @return the number of words that have been written as <tt>int</tt>.
	 */
	public int getWordCount() {
		return wordCount;
	}

	/**
	 * Sets the number of words that have been returned.
	 *
	 * @param count the number of words as <tt>int</tt>.
	 */
	public void setWordCount(int count) {
		wordCount = count;
	}

	@Override
	public void writeData(DataOutput dout) throws IOException {
		dout.write(getMessage());
	}

	@Override
	public void readData(DataInput din) throws IOException {
		setReference(din.readUnsignedShort());
		setWordCount(din.readUnsignedShort());

		setDataLength(4);
	}

//	@Override
	public byte[] getMessage() {
		byte[] result = new byte[4];

		result[0] = (byte) ((reference >> 8) & 0xff);
		result[1] = (byte) (reference & 0xff);
		result[2] = (byte) ((wordCount >> 8) & 0xff);
		result[3] = (byte) (wordCount & 0xff);

		return result;
	}

	@Override
	protected void setFunctionCode(int code) {
		super.setFunctionCode(code);
	}
}