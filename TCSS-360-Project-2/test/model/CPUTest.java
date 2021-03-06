package model;

import control.ModelListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
 * @version 10/29/2020
 */
class CPUTest implements ModelListener {

    private Memory mem;

    private CPU cpu;

    private ArrayList<String> names;

    private ArrayList<Object> cpuValues;

    private String output;

    private String input;

    @BeforeEach
    void initialize() {
        mem = new Memory();
        cpu = new CPU(mem);
        cpu.addListener(this);
        names = new ArrayList<>();
        cpuValues = new ArrayList<>();
        output = "";
        input = "";
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
        mem.setByte((short) 3, (byte) 0x00);
        cpu.fetchExecute(true);
        cpu.fetchExecute(false);
        short[] numberValues = new short[] {(short) 0xA2CF, (short) 0, (short) 0xFBCF, (short) 3, (short) 0x00C0,
                (short) 0xA2CF, (short) 0xA2CF};
        for (int i = 0; i < 7; i++) {
            assertEquals(numberValues[i], (short) cpuValues.get(i));
        }
        assertTrue((boolean) cpuValues.get(7));
        assertFalse((boolean) cpuValues.get(8));
        assertFalse((boolean) cpuValues.get(9));
        assertFalse((boolean) cpuValues.get(10));
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
        assertEquals((short) 0x0F01, (short) cpuValues.get(0));
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
        assertEquals((short) 0xA2CF, (short) cpuValues.get(1));
    }

    /**
     * Tests that loading a byte works and that it only replaces the least significant byte of the register.
     */
    @Test
    void loadByteImmediateAccumulator() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0x71);
        mem.setByte((short) 2, (byte) 0x71);
        mem.setByte((short) 3, (byte) 0xD0);
        mem.setByte((short) 4, (byte) 0x00);
        mem.setByte((short) 5, (byte) 0xF2);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x71F2, cpuValues.get(0));
    }

    /**
     * Tests that loading a byte works and that it only replaces the least significant byte of the register.
     */
    @Test
    void loadByteImmediateIndex() {
        mem.setByte((short) 0, (byte) 0xC8);
        mem.setByte((short) 1, (byte) 0x71);
        mem.setByte((short) 2, (byte) 0x71);
        mem.setByte((short) 3, (byte) 0xD8);
        mem.setByte((short) 4, (byte) 0x00);
        mem.setByte((short) 5, (byte) 0xF2);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x71F2, cpuValues.get(1));
    }

    /**
     * Tests that storing a short using direct addressing works properly.
     */
    @Test
    void storeShortDirectAccumulator() {
        //Load something into the accumulator and store it at the address specified.
        byte[] memInitial = new byte[] {(byte) 0xC0, (byte) 0x27, (byte) 0xFF, (byte) 0xE1, (byte) 0x29,
                (byte) 0x53};
        for (int i = 0; i < memInitial.length; i++) mem.setByte((short) i, memInitial[i]);
        //Fetch execute called twice to run both instructions.
        cpu.fetchExecute(false);
        cpu.fetchExecute(false);
        assertEquals((short) 0x27FF, mem.getShort((short) 0x2953));
    }

    /**
     * Tests that a direct addressing mode store works with short value.
     */
    @Test
    void storeShortDirectIndex() {
        //Load something into the index and store it at the address specified.
        byte[] memInitial = new byte[] {(byte) 0xC8, (byte) 0xD1, (byte) 0x80, (byte) 0xE9, (byte) 0xF7,
                (byte) 0x00};
        for (int i = 0; i < memInitial.length; i++) mem.setByte((short) i, memInitial[i]);
        //Fetch execute called twice to run both instructions.
        cpu.fetchExecute(false);
        cpu.fetchExecute(false);
        assertEquals((short) 0xD180, mem.getShort((short) 0xF700));
    }

    @Test
    void storeByteDirectAccumulator() {
        //Load something into the accumulator and store it at the address specified.
        byte[] memInitial = new byte[] {(byte) 0xC0, (byte) 0x27, (byte) 0xFF, (byte) 0xF1, (byte) 0x29,
                (byte) 0x53};
        for (int i = 0; i < memInitial.length; i++) mem.setByte((short) i, memInitial[i]);
        //Fetch execute called twice to run both instructions.
        cpu.fetchExecute(false);
        cpu.fetchExecute(false);
        assertEquals((byte) 0xFF, mem.getByte((short) 0x2953));
        assertEquals((byte) 0x00, mem.getByte((short) 0x2954));
    }

    @Test
    void storeByteDirectIndex() {
        //Load something into the index and store it at the address specified.
        byte[] memInitial = new byte[] {(byte) 0xC8, (byte) 0xD1, (byte) 0x80, (byte) 0xF9, (byte) 0xF7,
                (byte) 0x00};
        for (int i = 0; i < memInitial.length; i++) mem.setByte((short) i, memInitial[i]);
        //Fetch execute called twice to run both instructions.
        cpu.fetchExecute(false);
        cpu.fetchExecute(false);
        assertEquals((byte) 0x80, mem.getByte((short) 0xF700));
        assertEquals((byte) 0x00, mem.getByte((short) 0xF701));
    }

    /**
     * Test that attempting to use the immediate addressing mode for store throws an error.
     */
    @Test
    void storeIllegalAddressingMode() {
        //Load something into the index, then call store with the immediate addressing mode.
        byte[] memInitial = new byte[] {(byte) 0xC8, (byte) 0xD1, (byte) 0x80, (byte) 0xF8, (byte) 0xF7,
                (byte) 0x00};
        for (int i = 0; i < memInitial.length; i++) mem.setByte((short) i, memInitial[i]);
        //Fetch execute called twice to run both instructions.
        cpu.fetchExecute(false);
        assertThrows(IllegalArgumentException.class, () -> cpu.fetchExecute(false));
    }

    /**
     * Test inputting a single character with direct addressing mode.
     */
    @Test
    void charInputDirect() {
        mem.setByte((short) 0, (byte) 0x49);
        mem.setByte((short) 1, (byte) 0x00);
        mem.setByte((short) 2, (byte) 0x01);
        input = "y";
        cpu.fetchExecute(false);
        assertEquals('y', (char) mem.getByte((short) 0x0001));
    }

    /**
     * Test a single character output with immediate addressing mode.
     */
    @Test
    void charOutputImmediate() {
        mem.setByte((short) 0, (byte) 0x50);
        mem.setByte((short) 1, (byte) 0x00);
        mem.setByte((short) 2, (byte) 0x35);
        cpu.fetchExecute(false);
        assertEquals("5", output);
    }

    /**
     * Test single character output with direct addressing mode.
     */
    @Test
    void charOutputDirect() {
        mem.setByte((short) 0, (byte) 0x51);
        mem.setByte((short) 1, (byte) 0xA5);
        mem.setByte((short) 2, (byte) 0x11);
        mem.setByte((short) 0xA511, (byte) 0x0A);
        cpu.fetchExecute(false);
        assertEquals("\n", output);
    }

    /**
     * Test output of a simple string with direct addressing mode;
     */
    @Test
    void stringOutDirect() {
        mem.setByte((short) 0, (byte) 0x41);
        mem.setByte((short) 1, (byte) 0x22);
        mem.setByte((short) 2, (byte) 0x22);
        String testString = "I am the walrus\n I am the eggman\n These are the wrong lyrics! 556";
        byte[] testBytes = testString.getBytes();
        for (int i = 0; i < testBytes.length; i++) {
            mem.setByte((short) (0x2222 + i), testBytes[i]);
        }
        cpu.fetchExecute(false);
        assertEquals(testString, output);
    }

    /**
     * Test that decimal input works with values that are not too large.
     */
    @Test
    void decimalInput() {
        mem.setByte((short) 0, (byte) 0x31);
        mem.setByte((short) 1, (byte) 0x22);
        mem.setByte((short) 2, (byte) 0x22);
        input = "3456";
        cpu.fetchExecute(false);
        assertEquals((short) 3456, mem.getShort((short) 0x2222));
    }

    /**
     * Test that decimal output works.
     */
    @Test
    void decimalOutput() {
        mem.setByte((short) 0, (byte) 0x38);
        mem.setByte((short) 1, (byte) 0xFF);
        mem.setByte((short) 2, (byte) 0xFF);
        cpu.fetchExecute(false);
        assertEquals("-1", output);
    }

    /**
     * Tests that moving the stack pointer to the accumulator works.
     */
    @Test
    void moveSPToAccumulator() {
        mem.setByte((short) 0, (byte) 0x02);
        cpu.fetchExecute(true);
        assertEquals((short) 0xFBCF, cpuValues.get(0));
    }

    /**
     * Tests that the N flag is properly moved into the accumulator.
     */
    @Test
    void moveNFlagToAccumulator() {
        //First instruction sets N flag, then moves flags into accumulator
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0xA2);
        mem.setByte((short) 2, (byte) 0xCF);
        mem.setByte((short) 3, (byte) 0x03);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 8, cpuValues.get(0));
    }

    /**
     * Test that unconditional branch works and that an instruction at the new program counter location
     * will run. Further tests will simply check program counter.
     */
    @Test
    void branchUnconditionalImmediate() {
        mem.setByte((short) 0, (byte) 0x04);
        mem.setByte((short) 1, (byte) 0x09);
        mem.setByte((short) 2, (byte) 0x00);
        mem.setByte((short) 0x0900, (byte) 0xC0);
        mem.setByte((short) 0x0901, (byte) 0x67);
        mem.setByte((short) 0x0902, (byte) 0x89);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x6789, cpuValues.get(0));
    }

    /**
     * Tests the case that the branch for less than or equal should be taken works.
     */
    @Test
    void branchLessEqualTrue() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0x00);
        mem.setByte((short) 2, (byte) 0x00);
        mem.setByte((short) 3, (byte) 0x06);
        mem.setByte((short) 4, (byte) 0xFF);
        mem.setByte((short) 5, (byte) 0x22);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0xFF22, cpuValues.get(3));
    }

    /**
     * Tests the case that the branch for less than or equal should not be taken works.
     */
    @Test
    void branchLessEqualFalse() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0x10);
        mem.setByte((short) 2, (byte) 0x00);
        mem.setByte((short) 3, (byte) 0x06);
        mem.setByte((short) 4, (byte) 0xFF);
        mem.setByte((short) 5, (byte) 0x22);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x0006, cpuValues.get(3));
    }

    /**
     * Tests the case that the branch for less than should be taken works.
     */
    @Test
    void branchLessTrue() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0xA0);
        mem.setByte((short) 2, (byte) 0x00);
        mem.setByte((short) 3, (byte) 0x08);
        mem.setByte((short) 4, (byte) 0xFF);
        mem.setByte((short) 5, (byte) 0x22);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0xFF22, cpuValues.get(3));
    }

    /**
     * Tests the case that the branch for less than should not be taken works.
     */
    @Test
    void branchLessFalse() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0x10);
        mem.setByte((short) 2, (byte) 0x00);
        mem.setByte((short) 3, (byte) 0x08);
        mem.setByte((short) 4, (byte) 0xFF);
        mem.setByte((short) 5, (byte) 0x22);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x0006, cpuValues.get(3));
    }

    /**
     * Tests the case that the branch for equal should be taken works.
     */
    @Test
    void branchEqualTrue() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0x00);
        mem.setByte((short) 2, (byte) 0x00);
        mem.setByte((short) 3, (byte) 0x0A);
        mem.setByte((short) 4, (byte) 0xFF);
        mem.setByte((short) 5, (byte) 0x22);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0xFF22, cpuValues.get(3));
    }

    /**
     * Tests the case that the branch for equal should not be taken works.
     */
    @Test
    void branchEqualFalse() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0x10);
        mem.setByte((short) 2, (byte) 0x00);
        mem.setByte((short) 3, (byte) 0x0A);
        mem.setByte((short) 4, (byte) 0xFF);
        mem.setByte((short) 5, (byte) 0x22);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x0006, cpuValues.get(3));
    }

    /**
     * Tests the case that the branch for not equal should be taken works.
     */
    @Test
    void branchNotEqualTrue() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0x50);
        mem.setByte((short) 2, (byte) 0x00);
        mem.setByte((short) 3, (byte) 0x0C);
        mem.setByte((short) 4, (byte) 0xFF);
        mem.setByte((short) 5, (byte) 0x22);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0xFF22, cpuValues.get(3));
    }

    /**
     * Tests the case that the branch for not equal should not be taken works.
     */
    @Test
    void branchNotEqualFalse() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0x00);
        mem.setByte((short) 2, (byte) 0x00);
        mem.setByte((short) 3, (byte) 0x0C);
        mem.setByte((short) 4, (byte) 0xFF);
        mem.setByte((short) 5, (byte) 0x22);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x0006, cpuValues.get(3));
    }

    /**
     * Tests the case that the branch for greater than or equal should be taken works.
     */
    @Test
    void branchGreaterEqualTrue() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0x00);
        mem.setByte((short) 2, (byte) 0x00);
        mem.setByte((short) 3, (byte) 0x0E);
        mem.setByte((short) 4, (byte) 0xFF);
        mem.setByte((short) 5, (byte) 0x22);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0xFF22, cpuValues.get(3));
    }

    /**
     * Tests the case that the branch for greater than or equal should not be taken works.
     */
    @Test
    void branchGreaterEqualFalse() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0xF0);
        mem.setByte((short) 2, (byte) 0x05);
        mem.setByte((short) 3, (byte) 0x0E);
        mem.setByte((short) 4, (byte) 0xFF);
        mem.setByte((short) 5, (byte) 0x22);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x0006, cpuValues.get(3));
    }

    /**
     * Tests the case that the branch for greater than should be taken works.
     */
    @Test
    void branchGreaterTrue() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0x50);
        mem.setByte((short) 2, (byte) 0x00);
        mem.setByte((short) 3, (byte) 0x10);
        mem.setByte((short) 4, (byte) 0xFF);
        mem.setByte((short) 5, (byte) 0x22);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0xFF22, cpuValues.get(3));
    }

    /**
     * Tests the case that the branch for greater than should not be taken works.
     */
    @Test
    void branchGreaterFalse() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0x00);
        mem.setByte((short) 2, (byte) 0x00);
        mem.setByte((short) 3, (byte) 0x10);
        mem.setByte((short) 4, (byte) 0xFF);
        mem.setByte((short) 5, (byte) 0x22);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x0006, cpuValues.get(3));
    }

    /**
     * Tests the case that the branch for overflow flag should be taken works.
     */
    @Test
    void branchOverflowTrue() {
        mem.setByte((short) 0, (byte) 0xC8);
        mem.setByte((short) 1, (byte) 0x70);
        mem.setByte((short) 2, (byte) 0x00);
        mem.setByte((short) 3, (byte) 0x1D);
        mem.setByte((short) 4, (byte) 0x12);
        mem.setByte((short) 5, (byte) 0xFF);
        mem.setByte((short) 6, (byte) 0x22);
        cpu.fetchExecute(false);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0xFF22, cpuValues.get(3));
    }

    /**
     * Tests the case that the branch for overflow flag should not be taken works.
     */
    @Test
    void branchOverflowFalse() {
        mem.setByte((short) 0, (byte) 0xC8);
        mem.setByte((short) 1, (byte) 0x30);
        mem.setByte((short) 2, (byte) 0x00);
        mem.setByte((short) 3, (byte) 0x1D);
        mem.setByte((short) 4, (byte) 0x12);
        mem.setByte((short) 5, (byte) 0xFF);
        mem.setByte((short) 6, (byte) 0x22);
        cpu.fetchExecute(false);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x0007, cpuValues.get(3));
    }

    /**
     * Tests the case that the branch for carry flag should be taken works.
     */
    @Test
    void branchCarryTrue() {
        mem.setByte((short) 0, (byte) 0xC8);
        mem.setByte((short) 1, (byte) 0x80);
        mem.setByte((short) 2, (byte) 0x00);
        mem.setByte((short) 3, (byte) 0x1D);
        mem.setByte((short) 4, (byte) 0x14);
        mem.setByte((short) 5, (byte) 0xFF);
        mem.setByte((short) 6, (byte) 0x22);
        cpu.fetchExecute(false);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0xFF22, cpuValues.get(3));
    }

    /**
     * Tests the case that the branch for carry flag should not be taken works.
     */
    @Test
    void branchCarryFalse() {
        mem.setByte((short) 0, (byte) 0xC8);
        mem.setByte((short) 1, (byte) 0x30);
        mem.setByte((short) 2, (byte) 0x00);
        mem.setByte((short) 3, (byte) 0x1D);
        mem.setByte((short) 4, (byte) 0x14);
        mem.setByte((short) 5, (byte) 0xFF);
        mem.setByte((short) 6, (byte) 0x22);
        cpu.fetchExecute(false);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x0007, cpuValues.get(3));
    }

    /**
     * Attempts to call and return from a subroutine. Should cause the word "Hi" to be into
     * output.
     */
    @Test
    void callAndReturnSubroutine() {
        mem.setByte((short) 0, (byte) 0x16);
        mem.setByte((short) 1, (byte) 0x55);
        mem.setByte((short) 2, (byte) 0xFF);
        mem.setByte((short) 3, (byte) 0x50);
        mem.setByte((short) 4, (byte) 0x00);
        mem.setByte((short) 5, (byte) 0x69);
        mem.setByte((short) 0x55FF, (byte) 0x50);
        mem.setByte((short) 0x5600, (byte) 0x00);
        mem.setByte((short) 0x5601, (byte) 0x48);
        mem.setByte((short) 0x5602, (byte) 0x58);
        cpu.fetchExecute(false);
        cpu.fetchExecute(false);
        cpu.fetchExecute(false);
        cpu.fetchExecute(false);
        assertEquals("Hi", output);
    }

    /**
     * Tests that the NOT operation works with the accumulator register.
     */
    @Test
    void notAccumulator() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0xF0);
        mem.setByte((short) 2, (byte) 0xF0);
        mem.setByte((short) 3, (byte) 0x18);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x0F0F, cpuValues.get(0));
    }


    /**
     * Tests that the NOT operation works with the index register.
     */
    @Test
    void notIndex() {
        mem.setByte((short) 0, (byte) 0xC8);
        mem.setByte((short) 1, (byte) 0xF0);
        mem.setByte((short) 2, (byte) 0xF0);
        mem.setByte((short) 3, (byte) 0x19);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x0F0F, cpuValues.get(1));
    }


    /**
     * Tests that the NEG operation works with the accumulator register.
     */
    @Test
    void negAccumulator() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0x00);
        mem.setByte((short) 2, (byte) 0x01);
        mem.setByte((short) 3, (byte) 0x1A);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0xFFFF, cpuValues.get(0));
    }


    /**
     * Tests that the NEG operation works with the index register.
     */
    @Test
    void negIndex() {
        mem.setByte((short) 0, (byte) 0xC8);
        mem.setByte((short) 1, (byte) 0xFF);
        mem.setByte((short) 2, (byte) 0xFF);
        mem.setByte((short) 3, (byte) 0x1B);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x0001, cpuValues.get(1));
    }

    /**
     * Tests that the ASL operation works with the accumulator register.
     */
    @Test
    void aslAccumulator() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0x00);
        mem.setByte((short) 2, (byte) 0x01);
        mem.setByte((short) 3, (byte) 0x1C);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x0002, cpuValues.get(0));
    }


    /**
     * Tests that the ASL operation works with the index register.
     */
    @Test
    void aslIndex() {
        mem.setByte((short) 0, (byte) 0xC8);
        mem.setByte((short) 1, (byte) 0x08);
        mem.setByte((short) 2, (byte) 0x00);
        mem.setByte((short) 3, (byte) 0x1D);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x1000, cpuValues.get(1));
    }

    /**
     * Tests that the ASR operation works with the accumulator register.
     */
    @Test
    void asrAccumulator() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0x00);
        mem.setByte((short) 2, (byte) 0x08);
        mem.setByte((short) 3, (byte) 0x1E);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x0004, cpuValues.get(0));
    }


    /**
     * Tests that the ASR operation works with the index register.
     */
    @Test
    void asrIndex() {
        mem.setByte((short) 0, (byte) 0xC8);
        mem.setByte((short) 1, (byte) 0xF0);
        mem.setByte((short) 2, (byte) 0x00);
        mem.setByte((short) 3, (byte) 0x1F);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0xF800, cpuValues.get(1));
    }

    /**
     * Tests that the ROL operation works with the accumulator register.
     */
    @Test
    void rolAccumulator() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0x80);
        mem.setByte((short) 2, (byte) 0x00);
        mem.setByte((short) 3, (byte) 0x20);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x0001, cpuValues.get(0));
    }


    /**
     * Tests that the ROL operation works with the index register.
     */
    @Test
    void rolIndex() {
        mem.setByte((short) 0, (byte) 0xC8);
        mem.setByte((short) 1, (byte) 0x00);
        mem.setByte((short) 2, (byte) 0x01);
        mem.setByte((short) 3, (byte) 0x21);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x0002, cpuValues.get(1));
    }

    /**
     * Tests that the ROR operation works with the accumulator register.
     */
    @Test
    void rorAccumulator() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0x80);
        mem.setByte((short) 2, (byte) 0x00);
        mem.setByte((short) 3, (byte) 0x22);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x4000, cpuValues.get(0));
    }


    /**
     * Tests that the ROR operation works with the index register.
     */
    @Test
    void rorIndex() {
        mem.setByte((short) 0, (byte) 0xC8);
        mem.setByte((short) 1, (byte) 0x00);
        mem.setByte((short) 2, (byte) 0x01);
        mem.setByte((short) 3, (byte) 0x23);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x8000, cpuValues.get(1));
    }

    /**
     * Tests that the unary ALU operations can set the negative flag.
     */
    @Test
    void unaryALUNegativeFlag() {
        mem.setByte((short) 0, (byte) 0xC8);
        mem.setByte((short) 1, (byte) 0x70);
        mem.setByte((short) 2, (byte) 0x00);
        mem.setByte((short) 3, (byte) 0x1D);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertTrue((boolean) cpuValues.get(7));
    }

    /**
     * Tests that the unary ALU operations can set the overflow flag.
     */
    @Test
    void unaryALUOverflowFlag() {
        mem.setByte((short) 0, (byte) 0xC8);
        mem.setByte((short) 1, (byte) 0x70);
        mem.setByte((short) 2, (byte) 0x00);
        mem.setByte((short) 3, (byte) 0x1D);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertTrue((boolean) cpuValues.get(9));
    }

    /**
     * Tests that the unary ALU operations can set the zero flag.
     */
    @Test
    void unaryALUZeroFlag() {
        mem.setByte((short) 0, (byte) 0xC8);
        mem.setByte((short) 1, (byte) 0x80);
        mem.setByte((short) 2, (byte) 0x00);
        mem.setByte((short) 3, (byte) 0x1D);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertTrue((boolean) cpuValues.get(8));
    }

    /**
     * Tests that the unary ALU operations can set the carry flag.
     */
    @Test
    void unaryALUCarryFlag() {
        mem.setByte((short) 0, (byte) 0xC8);
        mem.setByte((short) 1, (byte) 0x80);
        mem.setByte((short) 2, (byte) 0x00);
        mem.setByte((short) 3, (byte) 0x1D);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertTrue((boolean) cpuValues.get(10));
    }

    /**
     * Tests that the add to stack pointer call works.
     */
    @Test
    void addSP() {
        mem.setByte((short) 0, (byte) 0x60);
        mem.setByte((short) 1, (byte) 0x00);
        mem.setByte((short) 2, (byte) 0x01);
        cpu.fetchExecute(true);
        assertEquals((short) 0xFBD0, cpuValues.get(2));
    }

    /**
     * Tests that the subtract from stack pointer call works
     */
    @Test
    void subSP() {
        mem.setByte((short) 0, (byte) 0x68);
        mem.setByte((short) 1, (byte) 0x00);
        mem.setByte((short) 2, (byte) 0x01);
        cpu.fetchExecute(true);
        assertEquals((short) 0xFBCE, cpuValues.get(2));
    }

    /**
     * Tests that add works with the accumulator register.
     */
    @Test
    void addAccumulator() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0x11);
        mem.setByte((short) 2, (byte) 0x11);
        mem.setByte((short) 3, (byte) 0x70);
        mem.setByte((short) 4, (byte) 0x77);
        mem.setByte((short) 5, (byte) 0x77);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x8888, cpuValues.get(0));
    }

    /**
     * Tests that add works with the index register.
     */
    @Test
    void addIndex() {
        mem.setByte((short) 0, (byte) 0xC8);
        mem.setByte((short) 1, (byte) 0x22);
        mem.setByte((short) 2, (byte) 0x22);
        mem.setByte((short) 3, (byte) 0x78);
        mem.setByte((short) 4, (byte) 0x11);
        mem.setByte((short) 5, (byte) 0x11);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x3333, cpuValues.get(1));
    }

    /**
     * Tests that subtract works with the accumulator register.
     */
    @Test
    void subAccumulator() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0x71);
        mem.setByte((short) 2, (byte) 0x71);
        mem.setByte((short) 3, (byte) 0x80);
        mem.setByte((short) 4, (byte) 0x20);
        mem.setByte((short) 5, (byte) 0x20);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x5151, cpuValues.get(0));
    }

    /**
     * Tests that subtract works with the index register.
     */
    @Test
    void subIndex() {
        mem.setByte((short) 0, (byte) 0xC8);
        mem.setByte((short) 1, (byte) 0xFF);
        mem.setByte((short) 2, (byte) 0xFF);
        mem.setByte((short) 3, (byte) 0x88);
        mem.setByte((short) 4, (byte) 0xFF);
        mem.setByte((short) 5, (byte) 0x0F);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x00F0, cpuValues.get(1));
    }

    /**
     * Tests that AND works with the accumulator register.
     */
    @Test
    void andAccumulator() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0x71);
        mem.setByte((short) 2, (byte) 0x71);
        mem.setByte((short) 3, (byte) 0x90);
        mem.setByte((short) 4, (byte) 0x50);
        mem.setByte((short) 5, (byte) 0x81);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x5001, cpuValues.get(0));
    }

    /**
     * Tests that AND works with the index register.
     */
    @Test
    void andIndex() {
        mem.setByte((short) 0, (byte) 0xC8);
        mem.setByte((short) 1, (byte) 0x8F);
        mem.setByte((short) 2, (byte) 0xFF);
        mem.setByte((short) 3, (byte) 0x98);
        mem.setByte((short) 4, (byte) 0xF8);
        mem.setByte((short) 5, (byte) 0x0F);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x880F, cpuValues.get(1));
    }

    /**
     * Tests that OR works with the accumulator register.
     */
    @Test
    void orAccumulator() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0x21);
        mem.setByte((short) 2, (byte) 0x21);
        mem.setByte((short) 3, (byte) 0xA0);
        mem.setByte((short) 4, (byte) 0x50);
        mem.setByte((short) 5, (byte) 0x81);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x71A1, cpuValues.get(0));
    }

    /**
     * Tests that OR works with the index register.
     */
    @Test
    void orIndex() {
        mem.setByte((short) 0, (byte) 0xC8);
        mem.setByte((short) 1, (byte) 0x0A);
        mem.setByte((short) 2, (byte) 0x0A);
        mem.setByte((short) 3, (byte) 0xA8);
        mem.setByte((short) 4, (byte) 0xF0);
        mem.setByte((short) 5, (byte) 0xFE);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0xFAFE, cpuValues.get(1));
    }

    /**
     * Tests that compare works with the accumulator register.
     */
    @Test
    void cmpAccumulator() {
        mem.setByte((short) 0, (byte) 0xC0);
        mem.setByte((short) 1, (byte) 0xAC);
        mem.setByte((short) 2, (byte) 0xAC);
        mem.setByte((short) 3, (byte) 0xB0);
        mem.setByte((short) 4, (byte) 0xAC);
        mem.setByte((short) 5, (byte) 0xAC);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0xACAC, cpuValues.get(0));
        //Need to check Z flag once implemented.
    }

    /**
     * Tests that compare works with the index register.
     */
    @Test
    void cmpIndex() {
        mem.setByte((short) 0, (byte) 0xC8);
        mem.setByte((short) 1, (byte) 0x00);
        mem.setByte((short) 2, (byte) 0x01);
        mem.setByte((short) 3, (byte) 0xB8);
        mem.setByte((short) 4, (byte) 0x11);
        mem.setByte((short) 5, (byte) 0x10);
        cpu.fetchExecute(false);
        cpu.fetchExecute(true);
        assertEquals((short) 0x0001, cpuValues.get(1));
        //check N flag once implemented.
    }

    //Everything past here is implementing the ModelListener, so the test class can get feedback from the CPU.

    /**
     * Used to pass input to the CPU for testing.
     * @return input string that should be set during individual test
     */
    @Override
    public String getInput() {
        return input;
    }

    /**
     * Grabs the register updates generated by the CPU and adds them to an ArrayList.
     * @param values value of registers
     */
    @Override
    public void registerUpdate(short[] values) {
        for (short value:values) cpuValues.add(value);
    }

    /**
     * Grabs the flag updates generated by the CPU and adds them to an ArrayList
     * @param values store the boolean value of the flags
     */
    @Override
    public void flagUpdate(boolean[] values) {
        for (boolean value:values) cpuValues.add(value);
    }

    /**
     * Not implemented as CPUTest has direct access to the memory used during the test.
     * @param values a full copy of memory.
     */
    @Override
    public void memoryUpdate(byte[] values) {

    }

    /**
     * Grabs any generated output text.
     * @param outText generated output
     */
    @Override
    public void output(String outText) {
        output = output + outText;
    }

    /**
     * Not implemented since the CPU throws errors that will be caught by the Machine and then passed
     * to the listener. Thus, no listener.errorMessage() methods will be called while testing CPU.
     * @param message the error message.
     */
    @Override
    public void errorMessage(String message) {

    }
}