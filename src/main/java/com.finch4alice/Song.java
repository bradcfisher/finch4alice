package com.finch4alice;

import java.util.ArrayList;
import java.util.HashMap;

public class Song {

	private String _name = "Unnamed";
	private int _beatsPerMinute = 63;
	private ArrayList<Note> _notes = new ArrayList<Note>();

	public Song(String name, int beatsPerMinute, Note[] notes) {
		setNotes(notes);
		setName(name);
		setBeatsPerMinute(beatsPerMinute);
	}

	public Song(String name, int beatsPerMinute, ArrayList<Note> notes) {
		setNotes(notes);
		setName(name);
		setBeatsPerMinute(beatsPerMinute);
	}

	public ArrayList<Note> getNotes() {
		return _notes;
	}

	public void setNotes(Note[] notes) {
		_notes = new ArrayList<Note>();
		if (notes != null) {
			for (Note n : notes)
				_notes.add(n);
		}
	}

	public void setNotes(ArrayList<Note> notes) {
		_notes = new ArrayList<Note>();
		if (notes != null) {
			for (Note n : notes)
				_notes.add(n);
		}
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		if ((name == null) || (name.length() == 0))
			name = "Unnamed";
		else if (name.indexOf(":") != -1)
			throw new IllegalArgumentException("The name cannot contain a colon");
		_name = name;
	}

	public int getBeatsPerMinute() {
		return _beatsPerMinute;
	}

	public void setBeatsPerMinute(int beatsPerMinute) {
		if ((beatsPerMinute <= 0) || (beatsPerMinute > 2000))
			throw new IllegalArgumentException("Beats per minute must be between 1 and 2000");
		_beatsPerMinute = beatsPerMinute;
	}

	public double getMillisecondsPerWholeNote() {
		double secondsPerBeat = 60.0 / _beatsPerMinute;
		double msPerWholeNote = secondsPerBeat * 4 * 1000;		// Just a guess...
		return msPerWholeNote;
	}

	// https://en.wikipedia.org/wiki/Ring_Tone_Transfer_Language
	// http://www.mobilefish.com/tutorials/rtttl/rtttl_quickguide_specification.html
	public static Song parseRTTTL(String rtttl) {
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

		return new Song(name, beatsPerMinute, notes);
	} // parseRTTTL

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
	 * Generates a RTTTL format string
	 */
	public String toString() {
		return toString(this.getMostCommonDuration(), this.getMostCommonOctave());
	}

	/**
	 * Generates a RTTTL format string
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

} // class Song
