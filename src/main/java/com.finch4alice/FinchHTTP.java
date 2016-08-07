/**
 * Finch 4 Alice is released under the BSD 2-Clause License
 *
 * Copyright (c) 2015, Brad Fisher
 * All rights reserved.
 */
package com.finch4alice;

import java.awt.Color;

import java.text.DecimalFormat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.net.ConnectException;
import java.net.MalformedURLException;

import java.util.Arrays;

/**
 * Simple wrapper class to communicate with the Bird Brain Robot Server.
 *
 * <p>The API of this class is intended to mimic that of the edu.cmu.ri.createlab.terk.robot.finch.Finch
 * class as closely as possible.</p>
 *
 * <p>Due to limitations of the Bird Brain Robot Server, some functionality of the Finch is currently
 * unsupported.  This currently includes the {@code playTone}, {@code playClip}, {@code isShaken},
 * and {@code isTapped} methods.</p>
 *
 * @see <a href="https://github.com/BirdBrainTechnologies/BirdBrainRobotServer/">The BirdBrain Robot Server</a>
 */
public class FinchHTTP
	implements FinchSensorState
{

	/**
	 * Private storage for the serverBaseURL property.
	 */
	private String _serverBaseURL;

	/**
	 * The last known time that the Finch was shaken.
	 */
	private long lastShakenTime = Long.MAX_VALUE;

	/**
	 * The last known time that the Finch was tapped.
	 */
	private long lastTappedTime = Long.MAX_VALUE;
	

	/**
	 * Constructs a new FinchHTTP instance using the default server URL of "http://localhost:22179/".
	 */
	public FinchHTTP() {
		this("http://localhost:22179/");
	} // FinchHTTP

	/**
	 * Constructs a new FinchHTTP instance with a custom server URL.
	 * @param	serverBaseURL	The base URL of the Bird Brain Robot Server to communicate with.
	 */
	public FinchHTTP(String serverBaseURL) {
		setServerBaseURL(serverBaseURL);
	} // FinchHTTP

	/**
	 * Sets the base URL of the Bird Brain Robot Server to communicate with.
	 * @param	baseURL	The base URL of the Bird Brain Robot Server to communicate with.
	 * @throws	IllegalArgumentException if the baseURL is null.
	 */
	public void setServerBaseURL(String baseURL) {
		if (baseURL == null)
			throw new IllegalArgumentException("baseURL cannot be null");
		_serverBaseURL = baseURL;
	} // setServerBaseURL

	/**
	 * Retrieves the current Bird Brain Robot Server base URL.
	 * @return	The current Bird Brain Robot Server base URL.
	 */
	public String getServerBaseURL() {
		return _serverBaseURL;
	} // getServerBaseURL

	/**
	 * Formats an integer as a string.
	 * Used when constructing service URLs for querying the Bird Brain Robot Server.
	 * @param	value	The integer value to convert to a string.
	 * @return	The value formatted as a string.
	 */
	private String formatInt(int value) {
		return Integer.toString(value);
	} // formatInt

	/**
	 * Formats a double as a string.
	 * Used when constructing service URLs for querying the Bird Brain Robot Server.
	 * @param	value	The double value to convert to a string.
	 * @return	The value formatted as a string.
	 */
	private String formatDouble(double value) {
		return new DecimalFormat("#.##").format(value);
	} // formatDouble

	/**
	 * Executes an HTTP GET request to the Bird Brain Robot Server.
	 * @param	path	Relative path from the server root URL of the service to request
	 * @param	args	Array of arguments for the request.  Each argument in this array is appended
	 * 					to the request URL separated by slashes.
	 * @return	The response returned by the server on success.  Returns the string {@code "null"} if
	 *			a connection could not be established to the specified server.
	 */
	private String httpGET(String path, String... args) {
		//System.out.println("httpGET: path="+ path +" args="+ (args == null ? "null" : Arrays.toString(args)));

		String utf8 = java.nio.charset.StandardCharsets.UTF_8.name();
		String baseURL = getServerBaseURL();
		String fullURL = baseURL + (baseURL.endsWith("/") ? "" : "/") + path;
		for (int i = 0; i < args.length; ++i) {
			String arg = "?";
			try {
				arg = URLEncoder.encode(args[i], utf8);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("Unexpected Exception", e);
			}

			fullURL = fullURL + (fullURL.endsWith("/") ? "" : "/") + arg;
		}

		URL url;
		try {
			url = new URL(fullURL);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Unexpected Exception", e);
		}

		String response = null;
		try {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Accept-Charset", utf8);

			int httpStatus = connection.getResponseCode();
			if (httpStatus == 200) {
				BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), utf8));
				// Return response stream contents
				response = responseReader.readLine();
			} else {
				System.out.println("Unexpected HTTP status received: "+ httpStatus +": "+ connection.getResponseMessage());
				BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), utf8));
				if (errorReader != null) {
					// Echo error stream contents...
					String err;
					while ((err = errorReader.readLine()) != null) {
						System.out.println(err);
					}
				}
			}
		} catch (ConnectException e) {
			System.out.println("WARNING: Unable to connect to Bird Brain Robot Server at "+ baseURL);
			response = "null";
		} catch (IOException e) {
			throw new RuntimeException("Unexpected Exception", e);
		}

		return response;
	} // httpGET

	/**
	 * Validates the response received from the Bird Brain Robot Server as a result of an "out" query.
	 * <p>Currently, this will simply output a warning message to the console if an unexpected reponse
	 * is received.</p>
	 * @param	result	The response received from the server.
	 */
	private void validateOutResult(String result) {
		if (!"Output set".equals(result) && !"null".equals(result))
			System.out.println("Unexpected result: '"+ result +"'");
	} // validateOutResult

	/**
	 * Tests whether the Bird Brain Robot Server is running and a Finch is currently connected.
	 * @return	Returns {@code true} if the Bird Brain Robot Server can be contacted and responds with
	 *			status data for an attached Finch, otherwise returns {@code false}.
	 */
	public boolean isConnected() {
		// The server will return 'null' for the sensor inputs if finch is not connected
		return !("null".equals(httpGET("finch/in/lights")));
	} // isConnected

	/**
	 * Plays a tone at the specified frequency for the specified duration on the Finch's
	 * internal buzzer.
	 *
	 * <p>Middle C is about 262Hz. Visit <a href="http://www.phy.mtu.edu/~suits/notefreqs.html">http://www.phy.mtu.edu/~suits/notefreqs.html</a> for frequencies
	 * of musical notes.</p>
	 *
	 * <p>Note that buzz is non-blocking - so if you call two buzz methods in a row without an
	 * intervening sleep, you will only hear the second buzz (it will over-write the first buzz).</p>
	 *
	 * @param	frequency	Frequency in Hertz of the tone to be played
	 * @param	duration	Duration in milliseconds of the tone
	 */
	public void buzz(int frequency, int duration) {
		validateOutResult(httpGET("finch/out/buzzer", formatInt(frequency), formatInt(duration)));
	} // buzz

	/**
	 * Plays a tone at the specified frequency for the specified duration on the Finch's
	 * internal buzzer, and blocks until complete.
	 *
	 * <p>Middle C is about 262Hz. Visit <a href="http://www.phy.mtu.edu/~suits/notefreqs.html">http://www.phy.mtu.edu/~suits/notefreqs.html</a> for frequencies
	 * of musical notes.</p>
	 *
	 * @param	frequency	Frequency in Hertz of the tone to be played
	 * @param	duration	Duration in milliseconds of the tone
	 */
	public void buzzAndWait(int frequency, int duration) {
		buzz(frequency, duration);
		sleep(duration);
	} // buzzAndWait

	/**
	 * Takes the text of 'sayThis' and synthesizes it into a sound file and plays the sound file
	 * over computer speakers.
	 *
	 * <p>{@code sayThis} can be arbitrarily long and can include variable arguments.</p>
	 *
	 * <p>Example:</p>
	 * {@code myFinch.saySomething("My light sensor has a value of "+ lightSensor + " and temperature is " + tempInCelcius);}
	 *
	 * Note that this version of saySomething is non-blocking - so if you call two saySomething
	 * methods in a row without an intervening sleep, you will only hear the second call (it will
	 * over-write the first call). 
	 *
	 * @param	sayThis	The string of text that will be spoken by the computer
	 */
	public void saySomething(String sayThis) {
		validateOutResult(httpGET("finch/out/speak", sayThis));
	} // saySomething

	/**
	 * Takes the text of 'sayThis' and synthesizes it into a sound file and plays the sound file
	 * over computer speakers.
	 *
	 * <p>{@code sayThis} can be arbitrarily long and can include variable arguments. The duration
	 * argument allows you to delay program execution for a number of milliseconds.</p>
	 *
	 * <p>Example:</p>
	 * {@code myFinch.saySomething("My light sensor has a value of "+ lightSensor + " and temperature is " + tempInCelcius);}
	 *
	 * @param	sayThis		The string of text that will be spoken by the computer
	 * @param	duration	The time to block while playing the sound in milliseconds
	 */
	public void saySomething(String sayThis, int duration) {
		saySomething(sayThis);
		sleep(duration);
	} // saySomething

	/**
	 * Plays a tone over the computer speakers or headphones at a given frequency (in Hertz) for
	 * a specified duration in milliseconds.
	 *
	 * <p>Middle C is about 262Hz. Visit <a href="http://www.phy.mtu.edu/~suits/notefreqs.html">http://www.phy.mtu.edu/~suits/notefreqs.html</a> for frequencies
	 * of musical notes.</p>
	 *
	 * <p>Note, playTone will block program execution for the specified number of milliseconds while
	 * the tone is played.</p>
	 *
	 * @param	frequency	The frequency of the tone in Hertz
	 * @param	duration	The time to play the tone in milliseconds
	 * 
	 * @throws UnsupportedOperationException	Since the Bird Brain Robot Server doesn't currently
	 *											provide support for this functionality.
	 */
	public void playTone(int frequency, int duration) {
// TODO: Verify the volume should be 10
		playTone(frequency, 10, duration);
	} // playTone

	/**
	 * Plays a tone over the computer speakers or headphones at a given frequency (in Hertz) for
	 * a specified duration in milliseconds at a specified volume.
	 *
	 * <p>Middle C is about 262Hz. Visit <a href="http://www.phy.mtu.edu/~suits/notefreqs.html">http://www.phy.mtu.edu/~suits/notefreqs.html</a> for frequencies
	 * of musical notes.</p>
	 *
	 * Note, playTone will block program execution for the specified number of milliseconds while
	 * the tone is played.
	 *
	 * @param	frequency	The frequency of the tone in Hertz
	 * @param	volume		The volume of the tone on a 1 to 10 scale
	 * @param	duration	The time to play the tone in milliseconds
	 * 
	 * @throws UnsupportedOperationException	Since the Bird Brain Robot Server doesn't currently
	 *											provide support for this functionality.
	 */
	public void playTone(int frequency, int volume, int duration) {
		// TODO: NOT IMPLEMENTED: The Bird Brain Robot Server doesn't currently provide support for
		//		this functionality.
		throw new UnsupportedOperationException("playTone is not implemented");
	} // playTone

	/**
	 * Plays a wav file over computer speakers at the specificied fileLocation path.
	 *
	 * <p>If you place the audio file in the same path as your source, you can just specify the
	 * name of the file.</p>
	 *
	 * @param	fileLocation	The path to the file to play.  The file path specified here is
	 *							local to the current JVM.  The sound file will be sent to the
	 *							server to be played remotely.
	 *
	 * @throws UnsupportedOperationException	Since the Bird Brain Robot Server doesn't currently
	 *											provide support for this functionality.
	 */
	public void playClip(String fileLocation) {
		/*
		TODO: NOT IMPLEMENTED: The Bird Brain Robot Server doesn't currently provide support for
				this functionality.  If this is implemented, the clip should be read locally and
				sent to the server to be played remotely.
		*/
		throw new UnsupportedOperationException("playClip is not implemented");
	} // playClip

	/**
	 * {@inheritDoc}
	 */
	public int[] getLightSensors() {
		String[] lights = httpGET("finch/in/lights").split(" ");

		if ("null".equals(lights[0]))
			return null;

		try {
			return new int[] {
					(int)(Double.parseDouble(lights[0]) * 2.55),
					(int)(Double.parseDouble(lights[1]) * 2.55)
				};
		} catch(NumberFormatException e) {
			throw new RuntimeException("Server returned invalid response (NumberFormatException)");
		}
	} // getLightSensors

	/**
	 * {@inheritDoc}
	 */
	public boolean[] getObstacleSensors() {
		String[] obstacles = httpGET("finch/in/obstacles").split(" ");
		if ("null".equals(obstacles[0]))
			return null;
		else
			return new boolean[] { "true".equals(obstacles[0]), "true".equals(obstacles[1]) };
	} // getObstacleSensors

	/**
	 * This method uses {@link java.lang.Thread#sleep(long)} to cause the currently running program to
	 * sleep for the specified number of milliseconds.
	 *
	 * <p>Note, this method may return before the specified number of milliseconds have elapsed, if
	 * the thread has been interrupted (eg. signalled to terminate).</p>
	 *
	 * @param	ms	The number of milliseconds to sleep for. Valid values are all positive integers.
	 */
	public void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// Interrupt thread again to ensure any following sleep is interrupted (eg. signal the thread to shutdown gracefully)
			Thread.currentThread().interrupt();
		}
	} // sleep

	/**
	 * This method simultaneously sets the velocities of both wheels.
	 *
	 * <p>Current valid values range from -255 to 255; negative values cause a wheel to move backwards.</p>
	 *
	 * <p>If {@code timeToHold} is positive, this method blocks further program execution for the amount of
	 * time specified by {@code timeToHold}, and then stops the wheels once time has elapsed.</p>
	 *
	 * @param	leftVelocity	The velocity at which to move the left wheel
	 * @param	rightVelocity	The velocity at which to move the right wheel
	 * @param	timeToHold		The amount of time in milliseconds to hold the velocity for; if 0
	 *							or negative, program execution is not blocked and the wheels are
	 *							not stopped.
	 */
	public void setWheelVelocities(int leftVelocity, int rightVelocity, int timeToHold) {
		setWheelVelocities(leftVelocity, rightVelocity);
		sleep(timeToHold);
		stopWheels();
	} // setWheelVelocities

	/**
	 * This method simultaneously sets the velocities of both wheels.
	 *
	 * <p>Current valid values range from -255 to 255; negative values cause a wheel to move backwards.</p>
	 *
	 * @param	leftVelocity	The velocity at which to move the left wheel
	 * @param	rightVelocity	The velocity at which to move the right wheel
	 */
	public void setWheelVelocities(int leftVelocity, int rightVelocity) {
		validateOutResult(
			httpGET(
				"finch/out/motor",
				formatDouble(leftVelocity / 2.55),
				formatDouble(rightVelocity / 2.55)
			)
		);
	} // setWheelVelocities

	/**
	 * Stops both wheels.
	 */
	public void stopWheels() {
		setWheelVelocities(0, 0);
	} // stopWheels

	/**
	 * Sets the color of the LED in the Finch's beak using a {@link java.awt.Color} object.
	 * @param	color	is a {@link java.awt.Color} object that determines the beaks color
	 */
	public void setLED(Color color) {
		setLED(color.getRed(), color.getGreen(), color.getBlue());
	} // setLED

	/**
	 * Sets the color of the LED in the Finch's beak using a {@link java.awt.Color} object for the
	 * length of time specified by duration.
	 *
	 * @param	color		is a {@link java.awt.Color} object that determines the beaks color
	 * @param	duration	is the length of time the color will display on the beak
	 */
	public void setLED(Color color, int duration) {
		setLED(color.getRed(), color.getGreen(), color.getBlue(), duration);
	} // setLED

	/**
	 * Sets the color of the LED in the Finch's beak.
	 *
	 * <p>The LED can be any color that can be created by mixing red, green, and blue; turning on
	 * all three colors in equal amounts results in white light.</p>
	 *
	 * <p>Valid ranges for the red, green, and blue elements are 0 to 255.</p>
	 *
	 * @param	red		sets the intensity of the red element of the LED
	 * @param	green	sets the intensity of the green element of the LED
	 * @param	blue	sets the intensity of the blue element of the LED
	 */
	public void setLED(int red, int green, int blue) {
		validateOutResult(
			httpGET(
				"finch/out/led",
				formatDouble(red / 2.55),
				formatDouble(green / 2.55),
				formatDouble(blue / 2.55)
			)
		);
	} // setLED

	/**
	 * Sets the color of the LED in the Finch's beak for the length of time specified by duration.
	 *
	 * <p>The LED can be any color that can be created by mixing red, green, and blue; turning on
	 * all three colors in equal amounts results in white light.</p>
	 *
	 * <p>Valid ranges for the red, green, and blue elements are 0 to 255.</p>
	 *
	 * @param	red		sets the intensity of the red element of the LED
	 * @param	green	sets the intensity of the green element of the LED
	 * @param	blue	sets the intensity of the blue element of the LED
	 * @param	duration	is the length of time the color will display on the beak
	 */
	public void setLED(int red, int green, int blue, int duration) {
		setLED(red, green, blue);
		sleep(duration);
		setLED(0, 0, 0);
	} // setLED

	/**
	 * {@inheritDoc}
	 */
	public double[] getAccelerations() {
		String[] accelerations = httpGET("finch/in/accelerations").split(" ");
		if ("null".equals(accelerations[0]))
			return null;

		try {
			return new double[] {
					Double.parseDouble(accelerations[0]),
					Double.parseDouble(accelerations[1]),
					Double.parseDouble(accelerations[2])
				};
		} catch(NumberFormatException e) {
			throw new RuntimeException("Server returned invalid response (NumberFormatException)");
		}
	} // getAccelerations

	/**
	 * {@inheritDoc}
	 */
	public String getOrientation() {
		String result = httpGET("finch/in/orientation");
		return ("null".equals(result)
				? null
				: result);
	} // getOrientation

	/**
	 * Returns {@code true} if the Finch has been shaken since the last call to the method.
	 * @return	{@code true} if the Finch is connected and was recently shaken
	 */
	public boolean isShaken() {
		String timeStr = httpGET("finch/in/lastShakenTime");
		long time;
		try {
			time = Long.parseLong(timeStr);
		} catch (NumberFormatException e) {
			// Unparseable result, likely "null" or an error
			throw new RuntimeException("Server returned invalid response (NumberFormatException)");
		}

		boolean shaken = (time > lastShakenTime);

		lastShakenTime = time;

		return shaken;
	} // isShaken

	/**
	 * Returns {@code true} if the Finch has been tapped since the last call to the method.
	 * @return	{@code true} if the Finch is connected and was recently tapped
	 */
	public boolean isTapped() {
		String timeStr = httpGET("finch/in/lastTappedTime");
		long time;
		try {
			time = Long.parseLong(timeStr);
		} catch (NumberFormatException e) {
			// Unparseable result, likely "null" or an error
			throw new RuntimeException("Server returned invalid response (NumberFormatException)");
		}

		boolean tapped = (time > lastTappedTime);

		lastTappedTime = time;

		return tapped;
	} // isTapped

	/**
	 * {@inheritDoc}
	 */
	public double getTemperature() {
		try {
			String response = httpGET("finch/in/temperature");
			if (!"null".equals(response))
				return Double.parseDouble(response);
			else
				return 0.0;	// Not connected
		} catch(NumberFormatException e) {
			throw new RuntimeException("Server returned invalid response (NumberFormatException)");
		}
	} // getTemperature

	private static class FinchState
		implements FinchSensorState
	{
		private double temperature;
		private int[] lightSensors;
		private boolean[] obstacles;
		private double[] accelerations;
		private String orientation;

		public double getTemperature() { return temperature; }
		public int[] getLightSensors() { return lightSensors; }
		public boolean[] getObstacleSensors() { return obstacles; }
		public double[] getAccelerations() { return accelerations; }
		public String getOrientation() { return orientation; }

		public FinchState(
			double temperature, int[] lightSensors, boolean[] obstacles,
			String orientation, double[] accelerations
		) {
			this.temperature = temperature;
			this.lightSensors = lightSensors;
			this.obstacles = obstacles;
			this.orientation = orientation;
			this.accelerations = accelerations;
		} // FinchState

	} // class FinchState

	/**
	 * Polls all of the Finch sensor data in one request to the server.
	 * @return	A FinchSensorState instance containing the sensor readings, or null if not connected.
	 */
	public FinchSensorState poll() {
		String result = httpGET("poll");

		if (result == null || "null".equals(result))
			return null;

		if (result.startsWith("_problem ")) {
			// Some sort of problem occurred
			String message = result.substring("_problem ".length());

			// TODO: Should we throw an exception or provide a way to read the problem message?

			return null;
		}

		double temperature = 0;
		int[] lightSensors = new int[2];
		boolean[] obstacles = new boolean[2];
		String orientation = "";
		double[] accelerations = new double[3];

		String[] lines = result.split("(?:\r\n|\n|\r)+");
		for (String line : lines) {
			String[] pieces = line.split("\\s+");
			switch (pieces[0]) {
			case "temperature":
				temperature = Double.parseDouble(pieces[1]);
				break;
			case "leftLight":
				lightSensors[0] = (int)(Double.parseDouble(pieces[1]) * 2.55);
				break;
			case "rightLight":
				lightSensors[1] = (int)(Double.parseDouble(pieces[1]) * 2.55);
				break;
			case "leftObstacle":
				obstacles[0] = "true".equals(pieces[1]);
				break;
			case "rightObstacle":
				obstacles[1] = "true".equals(pieces[1]);
				break;
			case "orientation":
				orientation = pieces[1].replace('_', ' ');
				break;
			case "XAcceleration":
				accelerations[0] = Double.parseDouble(pieces[1]);
				break;
			case "YAcceleration":
				accelerations[1] = Double.parseDouble(pieces[1]);
				break;
			case "ZAcceleration":
				accelerations[2] = Double.parseDouble(pieces[1]);
				break;
			}
		} // for

		return new FinchState(temperature, lightSensors, obstacles, orientation, accelerations);
	} // poll

	/**
	 * Turns off all motors, servos, and LEDs on the connected Finch.
	 */
	public void reset() {
		httpGET("reset_all");
	} // reset()

	/**
	 * Retrieves a brief status report string containing the current status of the Finch.
	 * @return	A brief status report string containing the current status of the Finch.
	 */
	public String getStatusReport() {
		boolean connected = isConnected();

		if (!connected)
			return "Finch NOT CONNECTED at "+ getServerBaseURL();

		return "Finch Connected at "+ getServerBaseURL() +":\n"+ FinchSensorState.super.getStatusReport();
	} // getStatusReport

} // FinchHTTP
