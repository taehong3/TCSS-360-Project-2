package model;

import control.ModelListener;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/*
TCSS 360 Project #2
Group 8
RJ Alabado, Walter Kagel, Taehong Kim
 */

/**
 * Test class for the CPU. Implements ModelListener so it can get the updates generated by the CPU.
 * @author Group 8, Lead: Walter Kagel
 * @version 10/28/2020
 */
class CPUTest implements ModelListener {

    private Memory mem;

    private CPU cpu;

    private ArrayList<String> names;

    private ArrayList<Object> values;

    private String output = "";

    private String error = "";

    private String input = "";

    @BeforeEach
    void initialize() {
        mem = new Memory();
        cpu = new CPU(mem);
        cpu.addListener(this);
        names = new ArrayList<String>();
        values = new ArrayList<Object>();
    }

    /**
     * This tests that loading a short into the accumulator works. Also tests that load
     * calls listener for all of the expected registers and flags.
     */
    @Test
    void loadShortImmediateAccumulator() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0xA2);
        mem.setByte((short) 2, (byte) 0xCF);
        cpu.fetchExecute(true);
        String[] expectedNames = new String[] {"programCounter", "instructionSpecifier", "operandSpecifier",
                "operand", "accumulator", "index", "negativeFlag", "zeroFlag"};
        short[] numberValues = new short[] {(short) 3, (short) 0x00C0, (short) 0xA2CF, (short) 0xA2CF,
                (short) 0xA2CF, (short) 0};
        for (int i = 0; i < 6; i++) {
            assertEquals(expectedNames[i], names.get(i));
            assertEquals(numberValues[i], (short) values.get(i));
        }
        assertEquals(expectedNames[6], names.get(6));
        assertEquals(expectedNames[7], names.get(7));
        assertTrue((boolean) values.get(6));
        assertFalse((boolean) values.get(7));
    }

    /**
     * This test is being used to ensure that the direct addressing mode works properly.
     */
    @Test
    void loadShortDirectAccumulator() {
        mem.setByte((short) 0, (byte) 0xC1);
        mem.setByte((short) 1, (byte) 0x00);
        mem.setByte((short) 2, (byte) 0x04);
        mem.setShort((short) 4, (short) 0x0F01);
        cpu.fetchExecute(true);
        assertEquals((short) 0x0F01, (short) values.get(4));
    }

    /**
     * Tests that when the register bit is 1 load puts the operand into the index.
     */
    @Test
    void loadShortImmediateIndex() {
        mem.setByte((short) 0, (byte) 0xC8);
        mem.setByte((short) 1, (byte) 0xA2);
        mem.setByte((short) 2, (byte) 0xCF);
        cpu.fetchExecute(true);
        assertEquals((short) 0xA2CF, (short) values.get(5));
    }

    /**
     * Used to pass input to the CPU for testing.
     * @return
     */
    @Override
    public String getInput() {
        return input;
    }

    /**
     * Grabs the register updates generated by the CPU and adds them to an ArrayList.
     * @param name name of register
     * @param value value of register
     */
    @Override
    public void registerUpdate(String name, Short value) {
        names.add(name);
        values.add(value);
    }

    /**
     * Grabs the flag updates generated by the CPU and adds them to an ArrayList.
     * @param name of the flag to be updated.
     * @param value if the flag is set true, false otherwise.
     */
    @Override
    public void flagUpdate(String name, boolean value) {
        names.add(name);
        values.add(value);
    }

    /**
     * Grabs the memory updates generated by the CPU and adds them with the name of the starting address
     * and adds the value of the byte added.
     * @param startingAddress the starting memory address of the given values. Should be read as an unsigned short.
     * @param values a consecutive set of values in memory with the first value being the value at the
     */
    @Override
    public void memoryUpdate(short startingAddress, byte... values) {
        for (int i = 0; i < values.length; i++) {
            names.add(Integer.toString(startingAddress + i));
            this.values.add(values[i]);
        }
    }

    /**
     * Grabs any generated output text.
     * @param outText generated output
     */
    @Override
    public void output(String outText) {
        output = outText;
    }

    /**
     * Grabs any generated error messages. Note that in general error messages will be produced by the machine.
     * The CPU simply throws the error.
     * @param message the error message.
     */
    @Override
    public void errorMessage(String message) {
        error = message;
    }
}