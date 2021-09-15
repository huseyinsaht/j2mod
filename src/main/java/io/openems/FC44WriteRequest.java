package io.openems;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.io.NonWordDataHandler;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import com.ghgande.j2mod.modbus.procimg.IllegalAddressException;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;

/**
 * Class implementing FC 40
 */
public class FC44WriteRequest extends ModbusRequest {

	public final static int FUNCTION_CODE = 0x44;

	private int reference;
	private Register[] registers;
	private NonWordDataHandler nonWordDataHandler = null;

	/**
	 * Constructs a new <tt>WriteMultipleRegistersRequest</tt> instance with a given
	 * starting reference and values to be written.
	 * <p>
	 *
	 * @param first     -- the address of the first register to write to.
	 * @param registers -- the registers to be written.
	 */
	public FC44WriteRequest(int first, Register[] registers) {
		setFunctionCode(FUNCTION_CODE);

		setReference(first);
		setRegisters(registers);
		setDataLength(5 + registers.length * 2);
	}

	/**
	 * Constructs a new <tt>WriteMultipleRegistersRequest</tt> instance.
	 */
	public FC44WriteRequest() {
		setFunctionCode(FUNCTION_CODE);
//		setDataLength(5);
	}

	@Override
	public ModbusResponse getResponse() {
		return updateResponseWithHeader(new FC40WriteResponse());
	}

	/**
	 * createResponse - Returns the <tt>WriteMultipleRegistersResponse</tt> that
	 * represents the answer to this <tt>WriteMultipleRegistersRequest</tt>.
	 *
	 * The implementation should take care about assembling the reply to this
	 * <tt>WriteMultipleRegistersRequest</tt>.
	 *
	 * This method is used to create responses from the process image associated
	 * with the listener. It is commonly used to implement Modbus slave instances.
	 *
	 * @return the corresponding ModbusResponse.
	 *         <p>
	 *
	 *         createResponse() must be able to handle the case where the word data
	 *         that is in the response is actually non-word data. That is, where the
	 *         slave device has data which are not actually <tt>short</tt> values in
	 *         the range of registers being processed.
	 */
	@Override
	public ModbusResponse createResponse(AbstractModbusListener listener) {
		FC44WriteResponse response;

		if (nonWordDataHandler == null) {
			Register[] regs;
			// 1. get process image
			ProcessImage procimg = listener.getProcessImage(getUnitID());
			// 2. get registers
			try {
				regs = procimg.getRegisterRange(getReference(), getWordCount());
				// 3. set Register values
				for (int i = 0; i < regs.length; i++) {
					regs[i].setValue(this.getRegister(i).getValue());
				}
			} catch (IllegalAddressException iaex) {
				return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
			}
			response = (FC44WriteResponse) getResponse();
			response.setReference(getReference());
			response.setWordCount(getWordCount());
		} else {
			int result = nonWordDataHandler.commitUpdate();
			if (result > 0) {
				return createExceptionResponse(result);
			}

			response = (FC44WriteResponse) getResponse();
			response.setReference(getReference());
			response.setWordCount(nonWordDataHandler.getWordCount());
		}

		return response;
	}

	/**
	 * setReference - Returns the reference of the register to start writing to with
	 * this <tt>WriteMultipleRegistersRequest</tt>.
	 * <p>
	 *
	 * @return the reference of the register to start writing to as <tt>int</tt> .
	 */
	public int getReference() {
		return reference;
	}

	/**
	 * setReference - Sets the reference of the register to write to with this
	 * <tt>WriteMultipleRegistersRequest</tt>.
	 * <p>
	 *
	 * @param ref the reference of the register to start writing to as an
	 *            <tt>int</tt>.
	 */
	public void setReference(int ref) {
		reference = ref;
	}

	/**
	 * getRegisters - Returns the registers to be written with this
	 * <tt>WriteMultipleRegistersRequest</tt>.
	 * <p>
	 *
	 * @return the registers to be written as <tt>Register[]</tt>.
	 */
	public synchronized Register[] getRegisters() {
		Register[] dest = new Register[registers.length];
		System.arraycopy(registers, 0, dest, 0, dest.length);
		return dest;
	}

	/**
	 * setRegisters - Sets the registers to be written with this
	 * <tt>WriteMultipleRegistersRequest</tt>.
	 * <p>
	 *
	 * @param registers the registers to be written as <tt>Register[]</tt>.
	 */
	public synchronized void setRegisters(Register[] registers) {
		if (registers == null) {
			this.registers = null;
			setDataLength(5);
		} else {
			this.registers = Arrays.copyOf(registers, registers.length);
			setDataLength(5 + registers.length * 2);
		}
	}

	/**
	 * getRegister - Returns the <tt>Register</tt> at the given position.
	 *
	 * @param index the relative index of the <tt>Register</tt>.
	 *
	 * @return the register as <tt>Register</tt>.
	 *
	 * @throws IndexOutOfBoundsException if the index is out of bounds.
	 */
	public Register getRegister(int index) throws IndexOutOfBoundsException {
		if (index < 0) {
			throw new IndexOutOfBoundsException(index + " < 0");
		}

		if (index >= getWordCount()) {
			throw new IndexOutOfBoundsException(index + " > " + getWordCount());
		}

		return registers[index];
	}

	/**
	 * getRegisterValue - Returns the value of the specified register.
	 * <p>
	 *
	 * @param index the index of the desired register.
	 *
	 * @return the value as an <tt>int</tt>.
	 *
	 * @throws IndexOutOfBoundsException if the index is out of bounds.
	 */
	public int getRegisterValue(int index) throws IndexOutOfBoundsException {
		return getRegister(index).toUnsignedShort();
	}

	/**
	 * getByteCount - Returns the number of bytes representing the values to be
	 * written.
	 * <p>
	 *
	 * @return the number of bytes to be written as <tt>int</tt>.
	 */
	public int getByteCount() {
		return getWordCount() * 2;
	}

	/**
	 * getWordCount - Returns the number of words to be written.
	 *
	 * @return the number of words to be written as <tt>int</tt>.
	 */
	public int getWordCount() {
		if (registers == null) {
			return 0;
		}

		return registers.length;
	}

	/**
	 * getNonWordDataHandler - Returns the actual non word data handler.
	 *
	 * @return the actual <tt>NonWordDataHandler</tt>.
	 */
	public NonWordDataHandler getNonWordDataHandler() {
		return nonWordDataHandler;
	}

	/**
	 * setNonWordHandler - Sets a non word data handler. A non-word data handler is
	 * responsible for converting words from a Modbus packet into the non-word
	 * values associated with the actual device's registers.
	 *
	 * @param dhandler a <tt>NonWordDataHandler</tt> instance.
	 */
	public void setNonWordDataHandler(NonWordDataHandler dhandler) {
		nonWordDataHandler = dhandler;
	}

	@Override
	public void writeData(DataOutput output) throws IOException {
		output.write(getMessage());
	}

	@Override
	public void readData(DataInput input) throws IOException {
		reference = input.readUnsignedShort();
		int registerCount = input.readUnsignedShort();
		int byteCount = input.readUnsignedByte();

		if (nonWordDataHandler == null) {
			byte[] buffer = new byte[byteCount];
			input.readFully(buffer, 0, byteCount);

			int offset = 0;
			registers = new Register[registerCount];

			for (int register = 0; register < registerCount; register++) {
				registers[register] = new SimpleRegister(buffer[offset], buffer[offset + 1]);
				offset += 2;
			}
		} else {
			nonWordDataHandler.readData(input, reference, registerCount);
		}
	}

//	@Override
	public byte[] getMessage() {
		int len = 5;

		if (registers != null) {
			len += registers.length * 2;
		}

		byte[] result = new byte[len];
		int registerCount = registers != null ? registers.length : 0;

		result[0] = (byte) ((reference >> 8) & 0xff);
		result[1] = (byte) (reference & 0xff);
		result[2] = (byte) ((registerCount >> 8) & 0xff);
		result[3] = (byte) (registerCount & 0xff);
		result[4] = (byte) (registerCount * 2);

		int offset = 5;

		if (nonWordDataHandler == null) {
			for (int i = 0; i < registerCount; i++) {
				byte[] bytes = registers[i].toBytes();
				result[offset++] = bytes[0];
				result[offset++] = bytes[1];
			}
		} else {
			nonWordDataHandler.prepareData(reference, registerCount);
			byte[] bytes = nonWordDataHandler.getData();
			if (bytes != null) {
				int nonWordBytes = bytes.length;
				if (nonWordBytes > registerCount * 2) {
					nonWordBytes = registerCount * 2;
				}

				System.arraycopy(bytes, 0, result, offset, nonWordBytes);
			}
		}
		return result;
	}

	/**
	 * Updates the response with the header information to match the request
	 *
	 * @param response           Response to update
	 * @param ignoreFunctionCode True if the function code should stay unmolested
	 * @return Updated response
	 */
	ModbusResponse updateResponseWithHeader(FC40WriteResponse response) {

		// transfer header data
		response.setHeadless(isHeadless());
		if (!isHeadless()) {
			response.setTransactionID(getTransactionID());
			response.setProtocolID(getProtocolID());
		} else {
			response.setHeadless();
		}
		response.setUnitID(getUnitID());
//		if (!ignoreFunctionCode) {
		response.setFunctionCode(getFunctionCode());
//		}
		return response;
	}

}
