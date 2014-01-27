package com.github.katjahahn;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Reads the offset of the PE signature and the signature itself. Can be used to
 * verify that the file is indeed a PE file.
 * 
 * @author Katja Hahn
 * 
 */
public class PESignature extends PEModule {

	private static final int PE_OFFSET_LOCATION = 0x3c;
	private static final byte[] PE_SIG = "PE\0\0".getBytes();
	public static final int PE_SIG_LENGTH = PE_SIG.length;
	private int peOffset;
	private final File file;

	/**
	 * @constructor Creates a PESignature instance with the input file specified
	 * @param file
	 *            the PE file that should be checked for the signature
	 */
	public PESignature(File file) {
		this.file = file;
	}

	/**
	 * 
	 * 
	 * @throws FileFormatException
	 *             if file is not a PE file
	 * @throws IOException
	 *             if something went wrong while trying to read the file
	 */
	@Override
	public void read() throws FileFormatException, IOException {
		try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
			raf.seek(PE_OFFSET_LOCATION);
			byte[] offsetBytes = new byte[2];
			raf.readFully(offsetBytes);
			peOffset = PEModule.bytesToInt(offsetBytes);
			raf.seek(peOffset);
			byte[] peSigVal = new byte[4];
			raf.readFully(peSigVal);
			for (int i = 0; i < PE_SIG.length; i++) {
				if (peSigVal[i] != PE_SIG[i]) {
					System.out.println("");
					throw new FileFormatException("given file is no PE file");
				}
			}
		}
	}

	/**
	 * Returns the offset of the PE signature. Returns 0 if file hasn't been
	 * read yet.
	 * 
	 * @return
	 */
	public int getPEOffset() {
		return peOffset;
	}

	@Override
	public String getInfo() {
		return "-------------" + NL + "PE Signature" + NL + "-------------"
				+ NL + "pe offset: " + peOffset + NL;
	}

}
