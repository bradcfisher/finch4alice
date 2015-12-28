package com.finch4alice;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

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

	private static Pattern notePattern =
		Pattern.compile(
			"^\\s*([0-9]{0,2})(a|a#|b|h|c|c#|d|d#|e|f|f#|g|g#|p)(\\.?)([0-9]?)(\\.?)\\s*$",
			Pattern.CASE_INSENSITIVE
		);

	private int _noteIndex;		// 0 to 12
	private int _octave;		// 0 to 8
	private int _duration;		// 1 = whole, 2 = half, 4 = quarter, 8 = eighth, 16 = sixteenth, 32 = thirty-second
	private boolean _dotted;	// If true, multiply duration by 1 1/2

	public Note(String note, int octave, int duration, boolean dotted) {
		setNote(note);
		setOctave(octave);
		setDuration(duration);
		setDotted(dotted);
	}

	public static int getNoteIndex(String note) {
		note = note.toUpperCase();
		for (int index = validNotes.length - 1; index >= 0; --index) {
			if (validNotes[index].equals(note))
				return index;
		}
		throw new IllegalArgumentException("Invalid note " + note);
	}

	public void setNote(String note) {
		setNoteIndex(getNoteIndex(note));
	}

	public String getNote() {
		return validNotes[_noteIndex];
	}

	public void setNoteIndex(int index) {
		if ((index < 0) || (index >= validNotes.length))
			throw new IllegalArgumentException("Invalid note index "+ index);

		_noteIndex = index;
	}

	public int getNoteIndex() {
		return _noteIndex;
	}

	public void setOctave(int octave) {
		if ((octave < 0) || (octave > 8))
			throw new IllegalArgumentException("Invalid octave "+ octave);
		_octave = octave;
	}

	public int getOctave() {
		return _octave;
	}

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

	public int getDuration() {
		return _duration;
	}

	public void setDotted(boolean dotted) {
		_dotted = dotted;
	}

	public boolean isDotted() {
		return _dotted;
	}

	public double getFrequency() {
		return frequencies[_noteIndex] * Math.pow(2, _octave);
	}

	public double getDurationFraction() {
		double rv = 1.0 / _duration;
		if (_dotted)
			rv = rv * 1.5;

		return rv;
	}

	public String toString() {
		return toString(-1, -1);
	} // toString()

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
