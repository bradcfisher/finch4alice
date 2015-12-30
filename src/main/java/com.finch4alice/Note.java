/**
 * Finch 4 Alice is released under the BSD 2-Clause License
 *
 * Copyright (c) 2015, Brad Fisher
 * All rights reserved.
 */
package com.finch4alice;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Representation of a musical note.
 */
public class Note {

	// "P" is used to insert a pause/rest (no tone)
	private static String[] validNotes =
		new String[] { "P", "C", "C#", "D", "D#" /*"Eb"*/, "E", "F", "F#", "G", "G#", "A", "A#" /*"Bb"*/, "B" };

	/*
	Source: http://www.seventhstring.com/resources/notefrequencies.html

		C	C#	D	Eb	E	F	F#	G	G#	A	Bb	B
	0	16.35	17.32	18.35	19.45	20.60	21.83	23.12	24.50	25.96	27.50	29.14	30.87
	1	32.70	34.65	36.71	38.89	41.20	43.65	46.25	49.00	51.91	55.00	58.27	61.74
	2	65.41	69.30	73.42	77.78	82.41	87.31	92.50	98.00	103.8	110.0	116.5	123.5
	3	130.8	138.6	146.8	155.6	164.8	174.6	185.0	196.0	207.7	220.0	233.1	246.9
	4	261.6	277.2	293.7	311.1	329.6	349.2	370.0	392.0	415.3	440.0	466.2	493.9
	5	523.3	554.4	587.3	622.3	659.3	698.5	740.0	784.0	830.6	880.0	932.3	987.8
	6	1047	1109	1175	1245	1319	1397	1480	1568	1661	1760	1865	1976
	7	2093	2217	2349	2489	2637	2794	2960	3136	3322	3520	3729	3951
	8	4186	4435	4699	4978	5274	5588	5920	6272	6645	7040	7459	7902
	*/
	private static double[] frequencies =
		new double[] { 0, 16.35, 17.32, 18.35, 19.45, 20.60, 21.83, 23.12, 24.50, 25.96, 27.50, 29.14, 30.87 };

	// Regular expression for parsing/validating a note specification
	private static Pattern notePattern =
		Pattern.compile(
			"^\\s*([0-9]{0,2})(a|a#|b|h|c|c#|d|d#|e|f|f#|g|g#|p)(\\.?)([0-9]?)(\\.?)\\s*$",
			Pattern.CASE_INSENSITIVE
		);

	// The index of the note in the validNotes array (0 to 12)
	private int _noteIndex;

	// The octave of the note (0 to 8)
	private int _octave;

	// The duration of the note
	// 1 = whole, 2 = half, 4 = quarter, 8 = eighth, 16 = sixteenth, 32 = thirty-second
	private int _duration;

	// Whether the note is "dotted" or not.
	// If true, the duration is multiplied by 1 1/2
	private boolean _dotted;

	/**
	 * Constructs a new Note instance with the specified note, octave, duration and dotted values.
	 *
	 * @param	note		The note.  One of: "P" (pause), "C", "C#", "D", "D#", "E", "F", "F#",
	 *						"G", "G#", "A", "A#" or "B"
	 * @param	octave		The octave of the note.  Valid values are 0 to 8
	 * @param	duration	The duration of the note.  Valid values are: 1 = whole, 2 = half,
	 *						4 = quarter, 8 = eighth, 16 = sixteenth, 32 = thirty-second
	 * @param	dotted		Whether the note is "dotted" or not.  If true, the duration is multiplied by 1.5.
	 */
	public Note(String note, int octave, int duration, boolean dotted) {
		setNote(note);
		setOctave(octave);
		setDuration(duration);
		setDotted(dotted);
	}

	/**
	 * Retrieves the 0-based index of the specified note.
	 *
	 * @param	note		The note.  One of: "P" (pause), "C", "C#", "D", "D#", "E", "F", "F#",
	 *						"G", "G#", "A", "A#" or "B"
	 *
	 * @return	The index of the specified note, in the range 0 to 12.
	 */
	public static int getNoteIndex(String note) {
		note = note.toUpperCase();
		for (int index = validNotes.length - 1; index >= 0; --index) {
			if (validNotes[index].equals(note))
				return index;
		}
		throw new IllegalArgumentException("Invalid note " + note);
	}

	/**
	 * Sets the note.
	 *
	 * @param	note		The note.  One of: "P" (pause), "C", "C#", "D", "D#", "E", "F", "F#",
	 *						"G", "G#", "A", "A#" or "B"
	 */
	public void setNote(String note) {
		setNoteIndex(getNoteIndex(note));
	}

	/**
	 * Retrieves the note value.
	 * @return	The note.  One of: "P" (pause), "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#",
	 *			"A", "A#" or "B"
	 */
	public String getNote() {
		return validNotes[_noteIndex];
	}

	/**
	 * Sets the note by index.
	 *
	 * @param	index		The index of the note.  One of: 0 = "P" (pause), 1 = "C", 2 = "C#",
	 *						3 = "D", 4 = "D#", 5 = "E", 6 = "F", 7 = "F#", 8 = "G", 9 = "G#",
	 *						10 = "A", 11 = "A#" or 12 = "B"
	 */
	public void setNoteIndex(int index) {
		if ((index < 0) || (index >= validNotes.length))
			throw new IllegalArgumentException("Invalid note index "+ index);

		_noteIndex = index;
	}

	/**
	 * Retrieves the index of the note.
	 * @return	The index of the note.  One of: 0 = "P" (pause), 1 = "C", 2 = "C#", 3 = "D",
	 *			4 = "D#", 5 = "E", 6 = "F", 7 = "F#", 8 = "G", 9 = "G#", 10 = "A", 11 = "A#"
	 *			or 12 = "B"
	 */
	public int getNoteIndex() {
		return _noteIndex;
	}

	/**
	 * Sets the octave of the Note.
	 * @param	octave	The new octave for the note.  Must be between 0 and 8.
	 */
	public void setOctave(int octave) {
		if ((octave < 0) || (octave > 8))
			throw new IllegalArgumentException("Invalid octave "+ octave);
		_octave = octave;
	}

	/**
	 * Retrieves the octave of the Note.
	 * @return	The octave of the Note in the range 0 to 8.
	 */
	public int getOctave() {
		return _octave;
	}

	/**
	 * Sets the duration of the Note.
	 * @param	duration	The new duration for the note.  Valid values are: 1 = whole, 2 = half,
	 *						4 = quarter, 8 = eighth, 16 = sixteenth, 32 = thirty-second
	 */
	public void setDuration(int duration) {
		switch (duration) {
			case 1:
			case 2:
			case 4:
			case 8:
			case 16:
			case 32:
				break;
			default:
				throw new IllegalArgumentException("Illegal note duration.  Must be one of 1, 2, 4, 8, 16, 32");
		}
		_duration = duration;
	}

	/**
	 * Retrievs the duration of the Note.
	 * @return	The duration for the note.  Will be one of 1 = whole, 2 = half, 4 = quarter,
	 *			8 = eighth, 16 = sixteenth, or 32 = thirty-second
	 */
	public int getDuration() {
		return _duration;
	}

	/**
	 * Sets whether the Note is "dotted" or not.
	 * @param	dotted	Whether the note is "dotted" or not.  If true, the duration is multiplied
	 *					by 1.5.
	 */
	public void setDotted(boolean dotted) {
		_dotted = dotted;
	}

	/**
	 * Retrieves whether the Note is "dotted" or not.
	 * @return	Whether the note is "dotted" or not.  If true, the duration is multiplied by 1.5.
	 */
	public boolean isDotted() {
		return _dotted;
	}

	/**
	 * Retrieves the frequency of the Note (in Hz).
	 * @return	The frequency of the Note (in Hz).
	 */
	public double getFrequency() {
		return frequencies[_noteIndex] * Math.pow(2, _octave);
	}

	/**
	 * Retrieves the duration as a fractional value.
	 *
	 * <p>For example, a quarter note has a duration value of 4, and this function will return the
	 * reciprocal of that, 1/4 or 0.25.</p>
	 *
	 * @return	 The duration of the Note as a fractional value.
	 */
	public double getDurationFraction() {
		double rv = 1.0 / _duration;
		if (_dotted)
			rv = rv * 1.5;

		return rv;
	}

	/**
	 * Returns a string representation of the Note in RTTTL format.
	 * @return	A string representation of the Note in RTTTL format.
	 */
	public String toString() {
		return toString(-1, -1);
	} // toString()

	/**
	 * Returns a string representation of the Note in RTTTL format, using the specified default
	 * duration and default octave.
	 *
	 * @param	defaultDuration	The default duration value.  If the Note's duration matches the
	 *							specified default value, no duration is output in the string.
	 * @param	defaultOctave	The default octave value.  If the Note's octave matches the
	 *							specified default value, no octave is output in the string.
	 *
	 * @return	A string representation of the Note in RTTTL format.
	 */
	public String toString(int defaultDuration, int defaultOctave) {
		StringBuilder rv = new StringBuilder();

		if (_duration != defaultDuration)
			rv.append(_duration);

		rv.append(getNote());

		if (_dotted)
			rv.append(".");

		if ((_octave != defaultOctave) && (frequencies[_noteIndex] > 0))
			rv.append(_octave);

		return rv.toString();
	} // toString()

	/**
	 * Instantiates a new Note by parsing an RTTTL representation.
	 *
	 * @param	rtttlNote		The RTTTL note to parse.
	 * @param	defaultDuration	The default duration value to use.  Must be one of 1 = whole,
	 *							2 = half, 4 = quarter, 8 = eighth, 16 = sixteenth, or
	 *							32 = thirty-second
	 * @param	defaultOctave	The default octave value to use, in the range 0 to 8.
	 *
	 * @return	A new Note instance initialized from the specified RTTTL note string.
	 */
	public static Note parseRTTTL( String rtttlNote, int defaultDuration, int defaultOctave ) {
		Matcher noteMatcher = notePattern.matcher(rtttlNote);
		if (!noteMatcher.matches())
			throw new IllegalArgumentException("Invalid RTTTL input: unable to parse note '"+ rtttlNote +"'");

		String d = noteMatcher.group(1);
		String note = noteMatcher.group(2).toUpperCase();
		String o = noteMatcher.group(4);
		boolean dotted = (noteMatcher.group(3).length() > 0) || (noteMatcher.group(5).length() > 0);

		if (note.equals("H"))
			note = "B";

		int duration = 
				((d.length() == 0)
					? defaultDuration
					: Integer.parseInt(d));

		int octave =
				((o.length() == 0)
					? defaultOctave
					: Integer.parseInt(o));
 
//System.out.println("  note=" + note + "  octave=" + octave + "  duration=" + duration +"  dotted="+ dotted);

		return new Note(note, octave, duration, dotted);
	} // parseRTTTL

} // class Note
