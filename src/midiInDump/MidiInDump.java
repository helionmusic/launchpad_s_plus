package midiInDump;

/*
 * Note: This class has been slightly modified in order to become compatible
 * with our "Launchpad S+" project
 */

/*
 * MidiInDump.java
 * 
 * This file is part of jsresources.org
 */

/*
 * Copyright (c) 1999 - 2001 by Matthias Pfisterer Copyright (c) 2003 by Florian
 * Bomers All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

import javax.sound.midi.Transmitter;
import javax.sound.midi.Receiver;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

/**
 * Dumps input into console and notifies listeners
 */
public class MidiInDump extends Thread {
	/**
	 * Flag for debugging messages. If true, some messages are dumped to the
	 * console during operation.
	 */
	private static boolean DEBUG = true;
	static String strDeviceName = "Launchpad";
	static boolean bUseDefaultSynthesizer = false;
	// static MidiDevice inputDevice =
	// launchpad_s_plus.Launchpad.getInputDevice();
	public static Receiver r = new DumpReceiver(System.out);

	public MidiInDump() {
		this.start();
	}

	public void run() {
		try {
			openInput();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setup() {
		try {
			Transmitter t = launchpad_s_plus.Launchpad.getInputDevice()
					.getTransmitter();
			t.setReceiver(r);
		} catch (MidiUnavailableException e) {
			out("wasn't able to connect the device's Transmitter to the Receiver:");
			out(e);
			System.exit(1);
		}
	}

	public static void openInput() throws Exception {
		setup();

		if (bUseDefaultSynthesizer) {
			Synthesizer synth = MidiSystem.getSynthesizer();
			synth.open();
			r = synth.getReceiver();
			try {
				Transmitter t = launchpad_s_plus.Launchpad.getInputDevice()
						.getTransmitter();
				t.setReceiver(r);
			} catch (MidiUnavailableException e) {
				out("wasn't able to connect the device's Transmitter to the default Synthesizer:");
				out(e);
				launchpad_s_plus.Launchpad.getInputDevice().close();
				System.exit(1);
			}
		}

		out("\nNow taking input.");

	}

	public static void displayAndCloseInput() {
		if (launchpad_s_plus.SetupProcess.lpdetected) {
			try {
				launchpad_s_plus.Launchpad.getInputDevice().close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		out("\tReceived " + DumpReceiver.seCount
				+ " sysex midi messages with a total of "
				+ DumpReceiver.seByteCount + " bytes");
		out("\tReceived " + DumpReceiver.smCount
				+ " short midi messages with a total of "
				+ DumpReceiver.smByteCount + " bytes");
		out("\tReceived a total of " + DumpReceiver.smCount / 2
				+ " key presses");
		float total = (DumpReceiver.smByteCount + DumpReceiver.seByteCount);
		if (total < 1000)
			out("\tReceived a total of " + total + " bytes");
		else if (total < 1000000)
			out("\tReceived a total of " + total / 1000 + " kilobytes");
		else
			out("\tReceived a total of " + total / 1000000 + " megabytes");
	}

	private static void out(String strMessage) {
		System.out.println(strMessage);
	}

	private static void out(Throwable t) {
		if (DEBUG) {
			t.printStackTrace();
		} else {
			out(t.toString());
		}
	}
}

/*** MidiInDump.java ***/
