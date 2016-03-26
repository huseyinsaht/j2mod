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
package com.ghgande.j2mod.modbus.cmd;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.ModbusSlaveException;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransport;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTransport;
import com.ghgande.j2mod.modbus.msg.*;
import com.ghgande.j2mod.modbus.msg.ReadFileRecordRequest.RecordRequest;
import com.ghgande.j2mod.modbus.msg.ReadFileRecordResponse.RecordResponse;
import com.ghgande.j2mod.modbus.net.ModbusMasterFactory;
import com.ghgande.j2mod.modbus.util.ModbusLogger;

import java.util.Arrays;

/**
 * ReadFileRecordText -- Exercise the "READ FILE RECORD" Modbus
 * message.
 *
 * @author Julie
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 */
public class ReadFileRecordTest {

    private static final ModbusLogger logger = ModbusLogger.getLogger(ReadFileRecordTest.class);

    /**
     * usage -- Print command line arguments and exit.
     */
    private static void usage() {
        logger.system("Usage: ReadFileRecord connection unit file record registers [repeat]");

        System.exit(1);
    }

    public static void main(String[] args) {
        ModbusTransport transport = null;
        ReadFileRecordRequest request;
        ReadFileRecordResponse response;
        ModbusTransaction trans;
        int unit = 0;
        int file = 0;
        int record = 0;
        int registers = 0;
        int requestCount = 1;

		/*
         * Get the command line parameters.
		 */
        if (args.length < 5 || args.length > 6) {
            usage();
        }

        try {
            transport = ModbusMasterFactory.createModbusMaster(args[0]);
            if (transport instanceof ModbusSerialTransport) {
                ((ModbusSerialTransport)transport).setReceiveTimeout(500);
                if (System.getProperty("com.ghgande.j2mod.modbus.baud") != null) {
                    ((ModbusSerialTransport)transport).setBaudRate(Integer.parseInt(System.getProperty("com.ghgande.j2mod.modbus.baud")));
                }
                else {
                    ((ModbusSerialTransport)transport).setBaudRate(19200);
                }

                Thread.sleep(2000);
            }
            unit = Integer.parseInt(args[1]);
            file = Integer.parseInt(args[2]);
            record = Integer.parseInt(args[3]);
            registers = Integer.parseInt(args[4]);

            if (args.length > 5) {
                requestCount = Integer.parseInt(args[5]);
            }
        }
        catch (NumberFormatException x) {
            logger.system("Invalid parameter");
            usage();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            usage();
            System.exit(1);
        }

        try {
            for (int i = 0; i < requestCount; i++) {
                /*
                 * Setup the READ FILE RECORD request.  The record number
				 * will be incremented for each loop.
				 */
                request = new ReadFileRecordRequest();
                request.setUnitID(unit);

                RecordRequest recordRequest = new ReadFileRecordRequest.RecordRequest(file, record + i, registers);
                request.addRequest(recordRequest);

                logger.system("Request: %s", request.getHexMessage());

				/*
                 * Setup the transaction.
				 */
                trans = transport.createTransaction();
                trans.setRequest(request);

				/*
				 * Execute the transaction.
				 */
                try {
                    trans.execute();
                }
                catch (ModbusSlaveException x) {
                    logger.error("Slave Exception: %s", x.getLocalizedMessage());
                    continue;
                }
                catch (ModbusIOException x) {
                    logger.error("I/O Exception: %s", x.getLocalizedMessage());
                    continue;
                }
                catch (ModbusException x) {
                    logger.error("Modbus Exception: %s", x.getLocalizedMessage());
                    continue;
                }

                ModbusResponse dummy = trans.getResponse();
                if (dummy == null) {
                    logger.system("No response for transaction %d", i);
                    continue;
                }
                if (dummy instanceof ExceptionResponse) {
                    ExceptionResponse exception = (ExceptionResponse)dummy;
                    logger.system(exception.toString());
                    continue;
                }
                else if (dummy instanceof ReadFileRecordResponse) {
                    response = (ReadFileRecordResponse)dummy;

                    logger.system("Response: %s", response.getHexMessage());

                    int count = response.getRecordCount();
                    for (int j = 0; j < count; j++) {
                        RecordResponse data = response.getRecord(j);
                        short values[] = new short[data.getWordCount()];
                        for (int k = 0; k < data.getWordCount(); k++) {
                            values[k] = data.getRegister(k).toShort();
                        }
                        logger.system("data[%d][%d] = %s", i, Arrays.toString(values));
                    }
                    continue;
                }

				/*
				 * Unknown message.
				 */
                logger.system("Unknown Response: %s", dummy.getHexMessage());
            }
			
			/*
			 * Now read the number of events sent by the device.  Maybe it will
			 * tell us something useful.
			 */
            ReadCommEventCounterRequest eventRequest = new ReadCommEventCounterRequest();
            eventRequest.setUnitID(unit);
			
			/*
			 * Setup the transaction.
			 */
            if (transport != null) {
                trans = transport.createTransaction();
                trans.setRequest(eventRequest);

			/*
			 * Execute the transaction.
			 */
                try {
                    trans.execute();
                    ModbusResponse dummy = trans.getResponse();

                    if (dummy instanceof ReadCommEventCounterResponse) {
                        ReadCommEventCounterResponse eventResponse = (ReadCommEventCounterResponse)dummy;
                        logger.system("  Events: %s", eventResponse.getEventCount());
                    }
                }
                catch (ModbusException x) {
                    // Do nothing -- this isn't required.
                }

			/*
			 * Teardown the connection.
			 */
                transport.close();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }
}
