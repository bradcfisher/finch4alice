/**
 * Finch 4 Alice is released under the BSD 2-Clause License
 *
 * Copyright (c) 2015, Brad Fisher
 * All rights reserved.
 */
package com.finch4alice;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A collection of musical notes, intended to be played in sequence.
 */
public class Melody {

	private String _name = "Unnamed";
	private int _beatsPerMinute = 63;
	private ArrayList<Note> _notes = new ArrayList<Note>();

	/**
	 * Constructs a new Melody instance with the specified name, beats per minute and array of notes.
	 *
	 * @param	name			The name of the Melody.
	 * @param	beatsPerMinute	Number of beats per minute to assign to the Melody.
	 * @param	notes			Array of Note instances that make up the Melody.
	 */
	public Melody(String name, int beatsPerMinute, Note[] notes) {
		setNotes(notes);
		setName(name);
		setBeatsPerMinute(beatsPerMinute);
	}

	/**
	 * Constructs a new Melody instance with the specified name, beats per minute and array of notes.
	 *
	 * @param	name			The name of the Melody.
	 * @param	beatsPerMinute	Number of beats per minute to assign to the Melody.
	 * @param	notes			List of Note instances that make up the Melody.
	 */
	public Melody(String name, int beatsPerMinute, List<Note> notes) {
		setNotes(notes);
		setName(name);
		setBeatsPerMinute(beatsPerMinute);
	}

	/**
	 * Retrieves a List of the Notes for this Melody.
	 * @return	A List of the Notes for this Melody.  This list is live, so any modifications to the
	 *			list or the Notes contained in it will affect the Melody itself.
	 */
	public List<Note> getNotes() {
		return _notes;
	}

	/**
	 * Sets the Notes for the Melody.
	 * @param	notes	Array of Note instances that make up the Melody.  The array itself is
	 *					copied, however the Notes themselves are added by reference.  Can be null,
	 *					in which case all Notes are removed from the Melody.
	 */
	public void setNotes(Note[] notes) {
		_notes = new ArrayList<Note>();
		if (notes != null) {
			for (Note n : notes)
				_notes.add(n);
		}
	}

	/**
	 * Sets the Notes for the Melody.
	 * @param	notes	List of Note instances that make up the Melody.  The List itself is
	 *					copied, however the Notes themselves are added by reference.  Can be null,
	 *					in which case all Notes are removed from the Melody.
	 */
	public void setNotes(List<Note> notes) {
		_notes = new ArrayList<Note>();
		if (notes != null)
			_notes.addAll(notes);
	}

	/**
	 * Retrieves the name of the Melody.
	 * @return	The name of the Melody.
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Sets the name of the Melody.
	 * @param	name	The new name for the Melody.  If null or empty string, a name of "Unnamed"
	 *					will be assigned.  The name cannot contain a colon (:) character.
	 */
	public void setName(String name) {
		if ((name == null) || (name.length() == 0))
			name = "Unnamed";
		else if (name.indexOf(":") != -1)
			throw new IllegalArgumentException("The name cannot contain a colon");
		_name = name;
	}

	/**
	 * Retrieves the number of beats per minute.
	 * @return	The number of beats per minute.
	 */
	public int getBeatsPerMinute() {
		return _beatsPerMinute;
	}

	/**
	 * Sets the number of beats per minute for the Melody.
	 * @param	beatsPerMinute	The number of beats per minute for the Melody.
	 */
	public void setBeatsPerMinute(int beatsPerMinute) {
		if ((beatsPerMinute <= 0) || (beatsPerMinute > 2000))
			throw new IllegalArgumentException("Beats per minute must be between 1 and 2000");
		_beatsPerMinute = beatsPerMinute;
	}

	/**
	 * Retrieves the number of milliseconds a whole note will play, based on the specified
	 * number of beats per minute.
	 * @return	The number of milliseconds a whole note will play.
	 */
	public double getMillisecondsPerWholeNote() {
		double secondsPerBeat = 60.0 / _beatsPerMinute;
		double msPerWholeNote = secondsPerBeat * 4 * 1000;		// 4 is just a guess...  Smaller values seem too short
		return msPerWholeNote;
	}

	/**
	 * Constructs and populates a new Melody object by parsing an RTTTL (Ring Tone Text Transfer
	 * Language) string.
	 *
	 * @param	rtttl				The RTTTL-encoded string to parse.
	 *
	 * @return	Returns a new Melody instance initialized from the specified RTTTL string.
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/Ring_Tone_Transfer_Language">Wikipedia entry on RTTTL</a>
	 * @see <a href="http://www.mobilefish.com/tutorials/rtttl/rtttl_quickguide_specification.html">Mobilefish RTTTL 
Tutorial</a>
	 */
	public static Melody parseRTTTL(String rtttl) {
		int defaultDuration = 4;
		int defaultOctave = 6;
		int beatsPerMinute = 63;

		int p1, p2;

		// Name
		//==============================
		p1 = rtttl.indexOf(":");
		if (p1 == -1)
			throw new IllegalArgumentException("Invalid RTTTL input: Missing settings");
		String name = ((p1 == 0)
						? null
						: rtttl.substring(0, p1));

		// Settings
		//==============================
		p2 = rtttl.indexOf(":", p1 + 1);
		if (p2 == -1)
			throw new IllegalArgumentException("Invalid RTTTL input: Missing data");

		String[] settings = ((p2 > p1 + 1)
								? rtttl.substring(p1 + 1, p2)
								: "").split("\\s*,\\s*");

		for (String setting : settings) {
//System.out.println("setting: " + setting);
			String[] tmp = setting.split("\\s*=\\s*");
			if (tmp.length != 2)
				throw new IllegalArgumentException("Invalid RTTTL input: Unable to parse setting '"+ setting +"'");

			int val = Integer.parseInt(tmp[1]);

			String opt = tmp[0].replaceFirst("^\\s*(.*?)\\s*$", "$1");
			if ("d".equals(opt)) {
				// duration
				defaultDuration = val;
			} else if ("o".equals(opt)) {
				// octave
				defaultOctave = val;
			} else if ("b".equals(opt)) {
				// beats/min
				beatsPerMinute = val;
			}
		} // for

/*
System.out.println("name=" + name);
System.out.println("defaultDuration=" + defaultDuration);
System.out.println("defaultOctave=" + defaultOctave);
System.out.println("beatsPerMinute=" + beatsPerMinute);
//*/

		// Notes
		//==============================
		ArrayList<Note> notes = new ArrayList<Note>();
		String[] noteStr = rtttl.substring(p2 + 1).split("\\s*,\\s*");

		for (String n : noteStr) {
//System.out.println("note: '" + n + "'");
			notes.add(Note.parseRTTTL(n, defaultDuration, defaultOctave));
		} // for

		return new Melody(name, beatsPerMinute, notes);
	} // parseRTTTL

	/**
	 * Determine the most common octave used by the Notes in the Melody.
	 * @return	The most common octave used by the Notes in the Melody.
	 */
	public int getMostCommonOctave() {
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		int currentBest = 4;
		int currentCount = 0;

		for (Note n : this.getNotes()) {
			int o = n.getOctave();
			int c = 1;
			if (map.containsKey(o))
				c += map.get(o);
			map.put(o, c);

			if (c > currentCount) {
				currentBest = o;
				currentCount = c;
			}
		}

		return currentBest;
	}

	/**
	 * Determine the most common duration used by the Notes in the Melody.
	 * @return	The most common duration used by the Notes in the Melody.
	 */
	public int getMostCommonDuration() {
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

		int currentBest = 4;
		int currentCount = 0;

		for (Note n : this.getNotes()) {
			int d = n.getDuration();
			int c = (d > 9 ? 2 : 1);	// Number of characters added
			if (map.containsKey(d))
				c += map.get(d);
			map.put(d, c);

			if (c > currentCount) {
				currentBest = d;
				currentCount = c;
			}
		}

		return currentBest;
	}

	/**
	 * Generates an RTTTL formatted string.
	 * @return	An RTTTL formatted string, using the most common duration and octave values.
	 */
	public String toString() {
		return toString(this.getMostCommonDuration(), this.getMostCommonOctave());
	}

	/**
	 * Generates an RTTTL formatted string using the specified default duration and octave values.
	 *
	 * @param	defaultDuration	The default duration value to use.  This duration will be output in
	 *							the 'd=' settings field, and any notes matching the specified
	 *							duration will not have a duration output in the generated string.
	 *							Valid values are 1, 2, 4, 8, 16, and 32.
	 * @param	defaultOctave	The default octave value to use.  This octave 
	 *
	 * @return	An RTTTL formatted string using the specified default duration and octave values.
	 */
	public String toString(int defaultDuration, int defaultOctave) {
		StringBuilder rv = new StringBuilder();

		rv.append(this.getName());
		rv.append(":d="+ defaultDuration);
		rv.append(",o="+ defaultOctave);
		rv.append(",b="+ _beatsPerMinute +":");

		boolean first = true;
		for (Note n : _notes) {
			if (first)
				first = false;
			else
				rv.append(",");
			rv.append(n.toString(defaultDuration, defaultOctave));
		}

		return rv.toString();
	} // toString()

} // class Melody
