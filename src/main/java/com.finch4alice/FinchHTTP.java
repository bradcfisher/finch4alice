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
 * https://github.com/BirdBrainTechnologies/BirdBrainRobotServer/
 *
 * The API of this class is intended to mimic that of the edu.cmu.ri.createlab.terk.robot.finch.Finch
 * class as closely as possible.
 *
 * Due to limitations of the Bird Brain Robot Server
 */
public class FinchHTTP {

	protected String _serverBaseURL;

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
	protected String formatInt(int value) {
		return Integer.toString(value);
	} // formatInt

	/**
	 * Formats a double as a string.
	 * Used when constructing service URLs for querying the Bird Brain Robot Server.
	 * @param	value	The double value to convert to a string.
	 * @return	The value formatted as a string.
	 */
	protected String formatDouble(double value) {
		return new DecimalFormat("#.##").format(value);
	} // formatDouble

	/**
	 * Executes an HTTP GET request to the Bird Brain Robot Server.
	 * @param	path	
	 * @param	args	Array of arguments for the request.  Each argument in this array is appended
	 * 					to the request URL separated by slashes.
	 * @return	The response returned by the server on success.  Returns "null" if a connection
	 *			could not be established to the specified server.
	 */
	protected String httpGET(String path, String... args) {
		//System.out.println("httpGET: path="+ path +" args="+ (args == null ? "null" : Arrays.toString(args)));

		String utf8 = java.nio.charset.StandardCharsets.UTF_8.name();
		String fullURL = _serverBaseURL + (_serverBaseURL.endsWith("/") ? "" : "/") + path;
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
			System.out.println("WARNING: Unable to connect to Bird Brain Robot Server at "+ _serverBaseURL);
			response = "null";
		} catch (IOException e) {
			throw new RuntimeException("Unexpected Exception", e);
		}

		return response;
	} // httpGET

	/**
	 * Validates the response received from the Bird Brain Robot Server as a result of an "out" query.
	 * Currently, this will simply output a warning message to the console if an unexpected reponse
	 * is received.
	 * @param	result	The response received from the server.
	 */
	protected void validateOutResult(String result) {
		if (!"Output set".equals(result) && !"null".equals(result))
			System.out.println("Unexpected result: '"+ result +"'");
	} // validateOutResult

	/**
	 * Tests whether the Bird Brain Robot Server is running and a Finch is currently connected.
	 * @return	Returns true if the Bird Brain Robot Server can be contacted and responds with
	 *			status data for an attached Finch, otherwise returns false.
	 */
	public boolean isConnected() {
		// The server will return 'null' for the sensor inputs if finch is not connected
		return !("null".equals(httpGET("finch/in/lights")));
	} // isConnected

	/**
	 * Plays a tone at the specified frequency for the specified duration on the Finch's
	 * internal buzzer.
	 *
	 * Middle C is about 262Hz. Visit http://www.phy.mtu.edu/~suits/notefreqs.html for frequencies
	 * of musical notes.
	 *
	 * Note that buzz is non-blocking - so if you call two buzz methods in a row without an
	 * intervening sleep, you will only hear the second buzz (it will over-write the first buzz). 
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
	 * Middle C is about 262Hz. Visit http://www.phy.mtu.edu/~suits/notefreqs.html for frequencies
	 * of musical notes.
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
	 * sayThis can be arbitrarily long and can include variable arguments.
	 *
	 * Example:
	 *   myFinch.saySomething("My light sensor has a value of "+ lightSensor + " and temperature is " + tempInCelcius);
	 *
	 * Note that this version of saySomething is non-blocking - so if you call two saySomethign
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
	 * sayThis can be arbitrarily long and can include variable arguments. The duration argument
	 * allows you to delay program execution for a number of milliseconds.
	 *
	 * Example:
	 *   myFinch.saySomething("My light sensor has a value of "+ lightSensor + " and temperature is " + tempInCelcius);
	 *
	 * @param	sayThis		The string of text that will be spoken by the computer
	 * @param	duration	
	 */
	public void saySomething(String sayThis, int duration) {
		saySomething(sayThis);
		sleep(duration);
	} // saySomething

	/**
	 * Plays a tone over the computer speakers or headphones at a given frequency (in Hertz) for
	 * a specified duration in milliseconds.
	 *
	 * Middle C is about 262Hz. Visit http://www.phy.mtu.edu/~suits/notefreqs.html for frequencies
	 * of musical notes.
	 *
	 * Note, playTone will block program execution for the specified number of milliseconds while
	 * the tone is played.
	 *
	 * @param	frequency	The frequency of the tone in Hertz
	 * @param	duration	The time to play the tone in milliseconds
	 * 
	 * @todo	NOT IMPLEMENTED: The Bird Brain Robot Server doesn't currently provide support for
	 *			this functionality.
	 */
	public void playTone(int frequency, int duration) {
// TODO: Verify the volume should be 10
		playTone(frequency, 10, duration);
	} // playTone

	/**
	 * Plays a tone over the computer speakers or headphones at a given frequency (in Hertz) for
	 * a specified duration in milliseconds at a specified volume.
	 *
	 * Middle C is about 262Hz. Visit http://www.phy.mtu.edu/~suits/notefreqs.html for frequencies
	 * of musical notes.
	 *
	 * Note, playTone will block program execution for the specified number of milliseconds while
	 * the tone is played.
	 *
	 * @param	frequency	The frequency of the tone in Hertz
	 * @param	volume		The volume of the tone on a 1 to 10 scale
	 * @param	duration	The time to play the tone in milliseconds
	 * 
	 * @todo	NOT IMPLEMENTED: The Bird Brain Robot Server doesn't currently provide support for
	 *			this functionality.
	 */
	public void playTone(int frequency, int volume, int duration) {
		throw new UnsupportedOperationException("playTone is not implemented");
	} // playTone

	/**
	 * Plays a wav file over computer speakers at the specificied fileLocation path.
	 *
	 * If you place the audio file in the same path as your source, you can just specify the
	 * name of the file.
	 *
	 * @todo	NOT IMPLEMENTED: The Bird Brain Robot Server doesn't currently provide support for
	 *			this functionality.  If this is implemented, the clip should be read locally and
	 *			sent to the server to be played remotely.
	 */
	public void playClip(String fileLocation) {
		throw new UnsupportedOperationException("playClip is not implemented");
	} // playClip

	/**
	 * Returns the value of the left light sensor. Valid values range from 0 to 255, with higher
	 * values indicating more light is being detected by the sensor.
	 *
	 * @return	The current light level at the left light sensor
	 */
	public int getLeftLightSensor() {
		int[] lights = getLightSensors();
		return ((lights != null) ? lights[0] : 0);
	} // getLeftLightSensor

	/**
	 * Returns the value of the right light sensor. Valid values range from 0 to 255, with higher
	 * values indicating more light is being detected by the sensor.
	 *
	 * @return	The current light level at the right light sensor
	 */
	public int getRightLightSensor() {
		int[] lights = getLightSensors();
		return ((lights != null) ? lights[1] : 0);
	} // getRightLightSensor

	/**
	 * Returns a 2 integer array containing the current values of both light sensors.
	 * The left sensor is the 0th array element, and the right sensor is the 1st element.
	 *
	 * @return	A 2 int array containing both light sensor readings.
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
	 * Returns true if the left light sensor is less than the value specified by limit, false otherwise.
	 *
	 * @param	limit	The value the light sensor needs to exceed
	 * @return	whether the light sensor exceeds the value specified by limit
	 *
	 * @todo	The javadoc on finchrobot.com is contradictory about what this function returns.
	 *			Need to review source to determine actual behavior (eg. return true if > limit or
	 *			if < limit?)
	 */
	public boolean isLeftLightSensor(double limit) {
		return (getLeftLightSensor() > limit);
	} // isLeftLightSensor

	/**
	 * Returns true if the right light sensor is less than the value specified by limit, false otherwise.
	 *
	 * @param	limit	The value the light sensor needs to exceed
	 * @return	whether the light sensor exceeds the value specified by limit
	 *
	 * @todo	The javadoc on finchrobot.com is contradictory about what this function returns.
	 *			Need to review source to determine actual behavior (eg. return true if > limit or
	 *			if < limit?)
	 */
	public boolean isRightLightSensor(double limit) {
		return (getRightLightSensor() > limit);
	} // isRightLightSensor

	/**
	 * Returns true if there is an obstruction in front of the left side of the robot.
	 * @return	Whether an obstacle exists in front of the left side of the robot.
	 */
	public boolean isObstacleLeftSide() {
		boolean[] obstacles = getObstacleSensors();
		return (obstacles != null) && obstacles[0];
	} // isObstacleLeftSide

	/**
	 * Returns true if there is an obstruction in front of the right side of the robot.
	 * @return	Whether an obstacle exists in front of the right side of the robot.
	 */
	public boolean isObstacleRightSide() {
		boolean[] obstacles = getObstacleSensors();
		return (obstacles != null) && obstacles[1];
	} // isObstacleRightSide

	/**
	 * Returns true if either left or right obstacle sensor detect an obstacle.
	 * @return	Whether either obstacle sensor sees an obstacle.
	 */
	public boolean isObstacle() {
		boolean[] obstacles = getObstacleSensors();
		return (obstacles != null) && (obstacles[0] || obstacles[1]);
	} // isObstacle

	/**
	 * Returns the value of both obstacle sensors as 2 element boolean array. The left sensor is
	 * the 0th element, and the right sensor is the 1st element.
	 * @return	The values of left and right obstacle sensors in a 2 element array
	 */
	public boolean[] getObstacleSensors() {
		String[] obstacles = httpGET("finch/in/obstacles").split(" ");
		if ("null".equals(obstacles[0]))
			return null;
		else
			return new boolean[] { "true".equals(obstacles[0]), "true".equals(obstacles[1]) };
	} // getObstacleSensors

	/**
	 * This method uses Thread.sleep to cause the currently running program to sleep for the
	 * specified number of seconds.
	 *
	 * Note, this method may return before the specified number of milliseconds have elapsed, if
	 * the thread has been interrupted (eg. signalled to terminate).
	 *
	 * @param	ms	the number of milliseconds to sleep for. Valid values are all positive integers.
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
	 * Current valid values range from -255 to 255; negative values cause a wheel to move backwards.
	 *
	 * If timeToHold is positive, this method blocks further program execution for the amount of
	 * time specified by timeToHold, and then stops the wheels once time has elapsed. 
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
	 * Current valid values range from -255 to 255; negative values cause a wheel to move backwards.
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
	 * Sets the color of the LED in the Finch's beak using a Color object.
	 * @param	color	is a Color object that determines the beaks color
	 */
	public void setLED(Color color) {
		setLED(color.getRed(), color.getGreen(), color.getBlue());
	} // setLED

	/**
	 * Sets the color of the LED in the Finch's beak using a Color object for the length of time
	 * specified by duration.
	 *
	 * @param	color	is a Color object that determines the beaks color
	 * @param	duration	is the length of time the color will display on the beak
	 */
	public void setLED(Color color, int duration) {
		setLED(color.getRed(), color.getGreen(), color.getBlue(), duration);
	} // setLED

	/**
	 * Sets the color of the LED in the Finch's beak.
	 *
	 * The LED can be any color that can be created by mixing red, green, and blue; turning on
	 * all three colors in equal amounts results in white light.
	 *
	 * Valid ranges for the red, green, and blue elements are 0 to 255.
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
	 * The LED can be any color that can be created by mixing red, green, and blue; turning on
	 * all three colors in equal amounts results in white light.
	 *
	 * Valid ranges for the red, green, and blue elements are 0 to 255.
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
	 * This method returns the current X-axis acceleration value experienced by the robot.
	 * Values for acceleration range from -1.5 to +1.5g. The X-axis is the beak-tail axis.
	 *
	 * @return	The X-axis acceleration value
	 */
	public double getXAcceleration() {
		double[] accelerations = getAccelerations();
		return ((accelerations != null) ? accelerations[0] : Double.NaN);
	} // getXAcceleration

	/**
	 * This method returns the current Y-axis acceleration value experienced by the robot.
	 * Values for acceleration range from -1.5 to +1.5g. The Y-axis is the wheel-to-wheel axis.
	 *
	 * @return	The Y-axis acceleration value
	 */
	public double getYAcceleration() {
		double[] accelerations = getAccelerations();
		return ((accelerations != null) ? accelerations[1] : Double.NaN);
	} // getYAcceleration

	/**
	 * This method returns the current Z-axis acceleration value experienced by the robot.
	 * Values for acceleration range from -1.5 to +1.5g. The Z-axis runs perpendicular to the
	 * Finch's circuit board.
	 *
	 * @return	The Z-axis acceleration value
	 */
	public double getZAcceleration() {
		double[] accelerations = getAccelerations();
		return ((accelerations != null) ? accelerations[2] : Double.NaN);
	} // getZAcceleration

	/**
	 * Use this method to simultaneously return the current X, Y, and Z accelerations experienced
	 * by the robot.
	 *
	 * Values for acceleration can be in the range of -1.5g to +1.5g. When the robot is on a flat
	 * surface, X and Y should be close to 0g, and Z should be near +1.0g.
	 *
	 * @return	an array of 3 doubles containing the X, Y, and Z acceleration values
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
	 * Returns the current orientation of the Finch.
	 * @return	When connected to a Finch, returns one of: "Level", "Upside Down", "Beak Up",
	 *			"Beak Down", "Left Wing Down", "Right Wing Down", "In Between".  If unable to
	 *			connect, returns null.
	 */
	public String getOrientation() {
		String result = httpGET("finch/in/orientation");
		return ("null".equals(result)
				? null
				: result);
	} // getOrientation

	/**
	 * This method returns true if the beak is up (Finch sitting on its tail), false otherwise
	 * @return	true if beak is pointed at ceiling
	 */
	public boolean isBeakUp() {
		return "Beak Up".equals(getOrientation());
	} // isBeakUp

	/**
	 * This method returns true if the beak is pointed at the floor, false otherwise
	 * @return	true if beak is pointed at the floor
	 */
	public boolean isBeakDown() {
		return "Beak Down".equals(getOrientation());
	} // isBeakDown

	/**
	 * This method returns true if the Finch is on a flat surface
	 * @return	true if the Finch is level
	 */
	public boolean isFinchLevel() {
		return "Level".equals(getOrientation());
	} // isFinchLevel

	/**
	 * This method returns true if the Finch is upside down, false otherwise
	 * @return	true if Finch is upside down
	 */
	public boolean isFinchUpsideDown() {
		return "Upside Down".equals(getOrientation());
	} // isFinchUpsideDown

	/**
	 * This method returns true if the Finch's left wing is pointed at the ground
	 * @return	true if Finch's left wing is down
	 */
	public boolean isLeftWingDown() {
		return "Left Wing Down".equals(getOrientation());
	} // isLeftWingDown

	/**
	 * This method returns true if the Finch's right wing is pointed at the ground
	 * @return	true if Finch's right wing is down
	 */
	public boolean isRightWingDown() {
		return "Right Wing Down".equals(getOrientation());
	} // isRightWingDown

	/**
	 * Returns true if the Finch has been shaken since the last call to the method.
	 * @return	true if the Finch was recently shaken
	 * @todo	NOT IMPLEMENTED: The Bird Brain Robot Server doesn't currently support this property.
	 */
	public boolean isShaken() {
		// TODO: Determine if there is a route for this
		throw new UnsupportedOperationException("isShaken is not implemented");
	} // isShaken

	/**
	 * Returns true if the Finch has been tapped since the last call to the method.
	 * @return	true if the Finch was recently tapped
	 * @todo	NOT IMPLEMENTED: The Bird Brain Robot Server doesn't currently support this property.
	 */
	public boolean isTapped() {
		// TODO: Determine if there is a route for this
		throw new UnsupportedOperationException("isTapped is not implemented");
	} // isTapped

	/**
	 * Returns true if the temperature is less than the value specified by limit, false otherwise.
	 *
	 * @param	limit	The value the temperature needs to exceed
	 * @return	true if the temperature exceeds the value specified by limit
	 *
	 * @todo	The javadoc on finchrobot.com is contradictory about what this function returns.
	 *			Need to review source to determine actual behavior (eg. return true if > limit or
	 *			if < limit?)
	 */
	public boolean isTemperature(double limit) {
		// TODO: javadoc on finchrobot.com is contradictory about what this function returns.  Need to review source to determine actual behavior (eg. return true if > limit or if < limit?)
		return (getTemperature() > limit);
	} // isTemperature

	/**
	 * The current temperature reading at the temperature probe.
	 * The value returned is in Celsius.
	 * To get Fahrenheit from Celsius, multiply the number by 1.8 and then add 32.
	 *
	 * @return	The current temperature in degrees Celsius
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

	/**
	 * The current temperature reading at the temperature probe, in Fahrenheit.
	 * @return	The current temperature in degrees Fahrenheit
	 */
	public double getTemperatureFahrenheit() {
		return getTemperature() * 1.8 + 32;
	} // getTemperatureFahrenheit

	/**
	 * Retrieves a brief status report string containing the current status of the Finch.
	 * @return	A brief status report string containing the current status of the Finch.
	 */
	public String getStatusReport() {
		boolean connected = isConnected();

		if (!connected)
			return "Finch NOT CONNECTED at "+ getServerBaseURL();

		int[] lights = getLightSensors();
		boolean[] obstacles = getObstacleSensors();
		double[] accelerations = getAccelerations();
		double temperature = getTemperature();

		return "Finch Connected at "+ getServerBaseURL() +":"+
				(!connected ? "" :
					"\nSensors:\n"+
					"  Light:        left="+ lights[0] +" right="+ lights[1] +"\n"+
					"  Obstacle:     left="+ obstacles[0] +" right="+ obstacles[1] +"\n"+
					"  Acceleration: X="+ accelerations[0] +" Y="+ accelerations[1] +" Z="+ accelerations[2] +"\n"+
					"  Temperature:  "+ temperature +" C ("+ (temperature * 1.8 + 32) +" F)"
				);
	} // getStatusReport

} // FinchHTTP
