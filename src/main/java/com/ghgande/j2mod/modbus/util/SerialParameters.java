/*
 * Copyright 2002-2016 jamod & j2mod development teams
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ghgande.j2mod.modbus.util;

import com.fazecast.jSerialComm.SerialPort;
import com.ghgande.j2mod.modbus.Modbus;

import java.util.Properties;

/**
 * Helper class wrapping all serial port communication parameters.
 * Very similar to the javax.comm demos, however, not the same.
 *
 * @author Dieter Wimberger
 * @author John Charlton
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 */
public class SerialParameters {

    private static final ModbusLogger logger = ModbusLogger.getLogger(SerialParameters.class);

    //instance attributes
    private String portName;
    private int baudRate;
    private int flowControlIn;
    private int flowControlOut;
    private int databits;
    private int stopbits;
    private int parity;
    private String encoding;
    private boolean echo;

    /**
     * Constructs a new <tt>SerialParameters</tt> instance with
     * default values.
     */
    public SerialParameters() {
        portName = "";
        baudRate = 9600;
        flowControlIn = SerialPort.FLOW_CONTROL_DISABLED;
        flowControlOut = SerialPort.FLOW_CONTROL_DISABLED;
        databits = 8;
        stopbits = SerialPort.ONE_STOP_BIT;
        parity = SerialPort.NO_PARITY;
        encoding = Modbus.DEFAULT_SERIAL_ENCODING;
        echo = false;
    }

    /**
     * Constructs a new <tt>SerialParameters<tt> instance with
     * given parameters.
     *
     * @param portName       The name of the port.
     * @param baudRate       The baud rate.
     * @param flowControlIn  Type of flow control for receiving.
     * @param flowControlOut Type of flow control for sending.
     * @param databits       The number of data bits.
     * @param stopbits       The number of stop bits.
     * @param parity         The type of parity.
     * @param echo           Flag for setting the RS485 echo mode.
     */
    public SerialParameters(String portName, int baudRate,
                            int flowControlIn,
                            int flowControlOut,
                            int databits,
                            int stopbits,
                            int parity,
                            boolean echo) {
        this.portName = portName;
        this.baudRate = baudRate;
        this.flowControlIn = flowControlIn;
        this.flowControlOut = flowControlOut;
        this.databits = databits;
        this.stopbits = stopbits;
        this.parity = parity;
        this.echo = echo;
    }

    /**
     * Constructs a new <tt>SerialParameters</tt> instance with
     * parameters obtained from a <tt>Properties</tt> instance.
     *
     * @param props  a <tt>Properties</tt> instance.
     * @param prefix a prefix for the properties keys if embedded into
     *               other properties.
     */
    public SerialParameters(Properties props, String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        setPortName(props.getProperty(prefix + "portName", ""));
        setBaudRate(props.getProperty(prefix + "baudRate", "" + 9600));
        setFlowControlIn(props.getProperty(prefix + "flowControlIn", "" + SerialPort.FLOW_CONTROL_DISABLED));
        setFlowControlOut(props.getProperty(prefix + "flowControlOut", "" + SerialPort.FLOW_CONTROL_DISABLED));
        setParity(props.getProperty(prefix + "parity", "" + SerialPort.NO_PARITY));
        setDatabits(props.getProperty(prefix + "databits", "8"));
        setStopbits(props.getProperty(prefix + "stopbits", "" + SerialPort.ONE_STOP_BIT));
        setEncoding(props.getProperty(prefix + "encoding", Modbus.DEFAULT_SERIAL_ENCODING));
        setEcho("true".equals(props.getProperty(prefix + "echo")));
    }

    /**
     * Returns the port name.
     *
     * @return the port name.
     */
    public String getPortName() {
        return portName;
    }

    /**
     * Sets the port name.
     *
     * @param name the new port name.
     */
    public void setPortName(String name) {
        portName = name;
    }

    /**
     * Sets the baud rate.
     *
     * @param rate the new baud rate.
     */
    public void setBaudRate(int rate) {
        baudRate = rate;
    }

    /**
     * Return the baud rate as <tt>int</tt>.
     *
     * @return the baud rate as <tt>int</tt>.
     */
    public int getBaudRate() {
        return baudRate;
    }

    /**
     * Sets the baud rate.
     *
     * @param rate the new baud rate.
     */
    public void setBaudRate(String rate) {
        baudRate = Integer.parseInt(rate);
    }

    /**
     * Returns the baud rate as a <tt>String</tt>.
     *
     * @return the baud rate as <tt>String</tt>.
     */
    public String getBaudRateString() {
        return Integer.toString(baudRate);
    }

    /**
     * Sets the type of flow control for the input
     * as given by the passed in <tt>int</tt>.
     *
     * @param flowcontrol the new flow control type.
     */
    public void setFlowControlIn(int flowcontrol) {
        flowControlIn = flowcontrol;
    }

    /**
     * Returns the input flow control type as <tt>int</tt>.
     *
     * @return the input flow control type as <tt>int</tt>.
     */
    public int getFlowControlIn() {
        return flowControlIn;
    }

    /**
     * Sets the type of flow control for the input
     * as given by the passed in <tt>String</tt>.
     *
     * @param flowcontrol the flow control for reading type.
     */
    public void setFlowControlIn(String flowcontrol) {
        flowControlIn = stringToFlow(flowcontrol);
    }

    /**
     * Returns the input flow control type as <tt>String</tt>.
     *
     * @return the input flow control type as <tt>String</tt>.
     */
    public String getFlowControlInString() {
        return flowToString(flowControlIn);
    }

    /**
     * Sets the output flow control type as given
     * by the passed in <tt>int</tt>.
     *
     * @param flowControlOut new output flow control type as <tt>int</tt>.
     */
    public void setFlowControlOut(int flowControlOut) {
        this.flowControlOut = flowControlOut;
    }

    /**
     * Returns the output flow control type as <tt>int</tt>.
     *
     * @return the output flow control type as <tt>int</tt>.
     */
    public int getFlowControlOut() {
        return flowControlOut;
    }

    /**
     * Sets the output flow control type as given
     * by the passed in <tt>String</tt>.
     *
     * @param flowControlOut the new output flow control type as <tt>String</tt>.
     */
    public void setFlowControlOut(String flowControlOut) {
        this.flowControlOut = stringToFlow(flowControlOut);
    }

    /**
     * Returns the output flow control type as <tt>String</tt>.
     *
     * @return the output flow control type as <tt>String</tt>.
     */
    public String getFlowControlOutString() {
        return flowToString(flowControlOut);
    }

    /**
     * Sets the number of data bits.
     *
     * @param databits the new number of data bits.
     */
    public void setDatabits(int databits) {
        this.databits = databits;
    }

    /**
     * Returns the number of data bits as <tt>int</tt>.
     *
     * @return the number of data bits as <tt>int</tt>.
     */
    public int getDatabits() {
        return databits;
    }

    /**
     * Sets the number of data bits from the given <tt>String</tt>.
     *
     * @param databits the new number of data bits as <tt>String</tt>.
     */
    public void setDatabits(String databits) {
        if (databits != null && !databits.isEmpty()) {
            this.databits = Integer.parseInt(databits);
        }
        else {
            this.databits = 8;
        }
    }

    /**
     * Returns the number of data bits as <tt>String</tt>.
     *
     * @return the number of data bits as <tt>String</tt>.
     */
    public String getDatabitsString() {
        return databits + "";
    }

    /**
     * Sets the number of stop bits.
     *
     * @param stopbits the new number of stop bits setting.
     */
    public void setStopbits(int stopbits) {
        this.stopbits = stopbits;
    }

    /**
     * Returns the number of stop bits as <tt>int</tt>.
     *
     * @return the number of stop bits as <tt>int</tt>.
     */
    public int getStopbits() {
        return stopbits;
    }

    /**
     * Sets the number of stop bits from the given <tt>String</tt>.
     *
     * @param stopbits the number of stop bits as <tt>String</tt>.
     */
    public void setStopbits(String stopbits) {
        if (stopbits.equals("1")) {
            this.stopbits = SerialPort.ONE_STOP_BIT;
        }
        if (stopbits.equals("1.5")) {
            this.stopbits = SerialPort.ONE_POINT_FIVE_STOP_BITS;
        }
        if (stopbits.equals("2")) {
            this.stopbits = SerialPort.TWO_STOP_BITS;
        }
    }

    /**
     * Returns the number of stop bits as <tt>String</tt>.
     *
     * @return the number of stop bits as <tt>String</tt>.
     */
    public String getStopbitsString() {
        switch (stopbits) {
            case SerialPort.ONE_STOP_BIT:
                return "1";
            case SerialPort.ONE_POINT_FIVE_STOP_BITS:
                return "1.5";
            case SerialPort.TWO_STOP_BITS:
                return "2";
            default:
                return "1";
        }
    }

    /**
     * Sets the parity schema.
     *
     * @param parity the new parity schema as <tt>int</tt>.
     */
    public void setParity(int parity) {
        this.parity = parity;
    }

    /**
     * Returns the parity schema as <tt>int</tt>.
     *
     * @return the parity schema as <tt>int</tt>.
     */
    public int getParity() {
        return parity;
    }

    /**
     * Sets the parity schema from the given
     * <tt>String</tt>.
     *
     * @param parity the new parity schema as <tt>String</tt>.
     */
    public void setParity(String parity) {
        parity = parity.toLowerCase();
        if (parity.equals("none")) {
            this.parity = SerialPort.NO_PARITY;
        }
        if (parity.equals("even")) {
            this.parity = SerialPort.EVEN_PARITY;
        }
        if (parity.equals("odd")) {
            this.parity = SerialPort.ODD_PARITY;
        }
        if (parity.equals("mark")) {
            this.parity = SerialPort.MARK_PARITY;
        }
        if (parity.equals("space")) {
            this.parity = SerialPort.SPACE_PARITY;
        }
    }

    /**
     * Returns the parity schema as <tt>String</tt>.
     *
     * @return the parity schema as <tt>String</tt>.
     */
    public String getParityString() {
        switch (parity) {
            case SerialPort.NO_PARITY:
                return "none";
            case SerialPort.EVEN_PARITY:
                return "even";
            case SerialPort.ODD_PARITY:
                return "odd";
            case SerialPort.MARK_PARITY:
                return "mark";
            case SerialPort.SPACE_PARITY:
                return "space";
            default:
                return "none";
        }
    }

    /**
     * Returns the encoding to be used.
     *
     * @return the encoding as string.
     *
     * @see Modbus#SERIAL_ENCODING_ASCII
     * @see Modbus#SERIAL_ENCODING_RTU
     * @see Modbus#SERIAL_ENCODING_BIN
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the encoding to be used.
     *
     * @param enc the encoding as string.
     *
     * @see Modbus#SERIAL_ENCODING_ASCII
     * @see Modbus#SERIAL_ENCODING_RTU
     */
    public void setEncoding(String enc) {
        enc = enc.toLowerCase();
        if (enc.equals(Modbus.SERIAL_ENCODING_ASCII) ||
                enc.equals(Modbus.SERIAL_ENCODING_RTU)
                ) {
            encoding = enc;
        }
        else {
            encoding = Modbus.DEFAULT_SERIAL_ENCODING;
        }
    }

    /**
     * Get the Echo value.
     *
     * @return the Echo value.
     */
    public boolean isEcho() {
        return echo;
    }

    /**
     * Set the Echo value.
     *
     * @param newEcho The new Echo value.
     */
    public void setEcho(boolean newEcho) {
        echo = newEcho;
    }

    /**
     * Converts a <tt>String</tt> describing a flow control type to the
     * <tt>int</tt> which is defined in SerialPort.
     *
     * @param flowcontrol the <tt>String</tt> describing the flow control type.
     *
     * @return the <tt>int</tt> describing the flow control type.
     */
    private int stringToFlow(String flowcontrol) {
        flowcontrol = flowcontrol.toLowerCase();
        if (flowcontrol.equals("none")) {
            return SerialPort.FLOW_CONTROL_DISABLED;
        }
        if (flowcontrol.equals("xon/xoff out")) {
            return SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED;
        }
        if (flowcontrol.equals("xon/xoff in")) {
            return SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED;
        }
        if (flowcontrol.equals("rts/cts")) {
            return SerialPort.FLOW_CONTROL_CTS_ENABLED | SerialPort.FLOW_CONTROL_RTS_ENABLED;
        }
        if (flowcontrol.equals("dsr/dtr")) {
            return SerialPort.FLOW_CONTROL_DSR_ENABLED | SerialPort.FLOW_CONTROL_DTR_ENABLED;
        }
        return SerialPort.FLOW_CONTROL_DISABLED;
    }

    /**
     * Converts an <tt>int</tt> describing a flow control type to a
     * String describing a flow control type.
     *
     * @param flowcontrol the <tt>int</tt> describing the
     *                    flow control type.
     *
     * @return the <tt>String</tt> describing the flow control type.
     */
    private String flowToString(int flowcontrol) {
        switch (flowcontrol) {
            case SerialPort.FLOW_CONTROL_DISABLED:
                return "none";
            case SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED:
                return "xon/xoff out";
            case SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED:
                return "xon/xoff in";
            case SerialPort.FLOW_CONTROL_CTS_ENABLED:
                return "rts/cts";
            case SerialPort.FLOW_CONTROL_DTR_ENABLED:
                return "dsr/dtr";
            default:
                return "none";
        }
    }

}